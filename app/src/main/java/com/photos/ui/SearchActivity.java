package com.photos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.photos.PhotosApp;
import com.photos.R;
import com.photos.model.Album;
import com.photos.model.Photo;
import com.photos.model.Tag;
import com.photos.model.User;
import com.photos.ui.adapters.SearchResultAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    // Tag 1
    private Spinner  spinner1;
    private EditText value1Edit;
    private ListView autocomplete1;

    // Conjunction selector
    private RadioGroup conjunctionGroup;

    // Tag 2 (optional)
    private Spinner  spinner2;
    private EditText value2Edit;
    private ListView autocomplete2;

    private Button   btnSearch;
    private ListView resultsView;

    private User user;
    private SearchResultAdapter resultAdapter;
    private List<Photo>  results = new ArrayList<>();

    // All known values per type (for autocomplete)
    private List<String> allPersonValues   = new ArrayList<>();
    private List<String> allLocationValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search Photos");

        user = PhotosApp.getInstance().getCurrentUser();
        collectTagValues();

        spinner1       = findViewById(R.id.tagTypeSpinner1);
        value1Edit     = findViewById(R.id.tagValue1Edit);
        autocomplete1  = findViewById(R.id.autocomplete1);
        conjunctionGroup = findViewById(R.id.conjunctionGroup);
        spinner2       = findViewById(R.id.tagTypeSpinner2);
        value2Edit     = findViewById(R.id.tagValue2Edit);
        autocomplete2  = findViewById(R.id.autocomplete2);
        btnSearch      = findViewById(R.id.btnSearch);
        resultsView    = findViewById(R.id.searchResultsList);

        String[] types = {Tag.TYPE_PERSON, Tag.TYPE_LOCATION};
        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinner1.setAdapter(typeAdapter);
        spinner2.setAdapter(typeAdapter);

        setupAutocomplete(value1Edit, autocomplete1, spinner1);
        setupAutocomplete(value2Edit, autocomplete2, spinner2);

        resultAdapter = new SearchResultAdapter(this, results);
        resultsView.setAdapter(resultAdapter);

        resultsView.setOnItemClickListener((parent, view, position, id) -> {
            // Find which album this photo belongs to and open it
            Photo photo = results.get(position);
            openPhotoInAlbum(photo);
        });

        btnSearch.setOnClickListener(v -> performSearch());
    }

    /** Gather all tag values from all albums for autocomplete. */
    private void collectTagValues() {
        allPersonValues.clear();
        allLocationValues.clear();
        for (Album a : user.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                for (Tag t : p.getTags()) {
                    String val = t.getValue().toLowerCase();
                    if (t.getType().equalsIgnoreCase(Tag.TYPE_PERSON)) {
                        if (!allPersonValues.contains(val)) allPersonValues.add(val);
                    } else {
                        if (!allLocationValues.contains(val)) allLocationValues.add(val);
                    }
                }
            }
        }
    }

    private void setupAutocomplete(EditText editText, ListView listView, Spinner typeSpinner) {
        final ArrayAdapter<String> acAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(acAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String prefix = s.toString().toLowerCase();
                String type = (String) typeSpinner.getSelectedItem();
                List<String> pool = type.equals(Tag.TYPE_PERSON) ? allPersonValues : allLocationValues;

                acAdapter.clear();
                if (!prefix.isEmpty()) {
                    for (String v : pool) {
                        if (v.startsWith(prefix)) acAdapter.add(v);
                    }
                }
                acAdapter.notifyDataSetChanged();
                listView.setVisibility(acAdapter.getCount() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String chosen = (String) acAdapter.getItem(position);
            editText.setText(chosen);
            editText.setSelection(chosen.length());
            listView.setVisibility(View.GONE);
        });
    }

    private void performSearch() {
        String v1 = value1Edit.getText().toString().trim();
        if (v1.isEmpty()) {
            Toast.makeText(this, "Enter at least one tag value", Toast.LENGTH_SHORT).show();
            return;
        }
        String type1 = (String) spinner1.getSelectedItem();
        String v2    = value2Edit.getText().toString().trim();
        String type2 = (String) spinner2.getSelectedItem();

        int selectedId = conjunctionGroup.getCheckedRadioButtonId();
        boolean useAnd = (selectedId == R.id.radioAnd);

        results.clear();
        for (Album a : user.getAlbums()) {
            for (Photo p : a.getPhotos()) {
                boolean match1 = hasTag(p, type1, v1);
                boolean match;
                if (v2.isEmpty()) {
                    match = match1;
                } else {
                    boolean match2 = hasTag(p, type2, v2);
                    match = useAnd ? (match1 && match2) : (match1 || match2);
                }
                if (match && !results.contains(p)) results.add(p);
            }
        }

        resultAdapter.notifyDataSetChanged();
        if (results.isEmpty()) {
            Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
        }
    }

    /** Case-insensitive prefix match on tag value. */
    private boolean hasTag(Photo photo, String type, String valuePrefix) {
        String prefix = valuePrefix.toLowerCase();
        for (Tag t : photo.getTags()) {
            if (t.getType().equalsIgnoreCase(type) &&
                    t.getValue().toLowerCase().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void openPhotoInAlbum(Photo photo) {
        for (Album a : user.getAlbums()) {
            int idx = a.getPhotos().indexOf(photo);
            if (idx >= 0) {
                Intent intent = new Intent(this, PhotoDisplayActivity.class);
                intent.putExtra("albumName",     a.getName());
                intent.putExtra("photoPosition", idx);
                startActivity(intent);
                return;
            }
        }
    }
}
