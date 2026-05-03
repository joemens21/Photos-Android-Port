package com.photos.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.photos.PhotosApp;
import com.photos.R;
import com.photos.model.Album;
import com.photos.model.Photo;
import com.photos.model.Tag;
import com.photos.model.User;

import java.util.List;

public class PhotoDisplayActivity extends AppCompatActivity {

    private ImageView   imageView;
    private TextView    fileNameText;
    private TextView    tagsText;
    private Button      btnPrev, btnNext, btnAddTag, btnDeleteTag;

    private Album album;
    private List<Photo> photos;
    private int   currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        User user = PhotosApp.getInstance().getCurrentUser();
        String albumName = getIntent().getStringExtra("albumName");
        album  = user.getAlbumByName(albumName);
        photos = album.getPhotos();
        currentIndex = getIntent().getIntExtra("photoPosition", 0);

        imageView    = findViewById(R.id.fullImageView);
        fileNameText = findViewById(R.id.photoFileName);
        tagsText     = findViewById(R.id.photoTags);
        btnPrev      = findViewById(R.id.btnPrev);
        btnNext      = findViewById(R.id.btnNext);
        btnAddTag    = findViewById(R.id.btnAddTag);
        btnDeleteTag = findViewById(R.id.btnDeleteTag);

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) { currentIndex--; displayCurrent(); }
        });
        btnNext.setOnClickListener(v -> {
            if (currentIndex < photos.size() - 1) { currentIndex++; displayCurrent(); }
        });
        btnAddTag.setOnClickListener(v -> showAddTagDialog());
        btnDeleteTag.setOnClickListener(v -> showDeleteTagDialog());

        displayCurrent();
    }

    private void displayCurrent() {
        if (photos.isEmpty()) { finish(); return; }

        Photo photo = photos.get(currentIndex);
        setTitle((currentIndex + 1) + " / " + photos.size());

        Uri uri = Uri.parse(photo.getFilePath());
        imageView.setImageURI(uri);

        fileNameText.setText(photo.getFileName());

        // Build tag display
        List<Tag> tags = photo.getTags();
        if (tags.isEmpty()) {
            tagsText.setText("No tags");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Tag t : tags) sb.append(t.toString()).append("  ");
            tagsText.setText(sb.toString().trim());
        }

        btnPrev.setEnabled(currentIndex > 0);
        btnNext.setEnabled(currentIndex < photos.size() - 1);
        btnDeleteTag.setEnabled(!tags.isEmpty());
    }

    private void showAddTagDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_tag, null);
        Spinner  spinner   = dialogView.findViewById(R.id.tagTypeSpinner);
        EditText valueEdit = dialogView.findViewById(R.id.tagValueEdit);

        String[] types = {Tag.TYPE_PERSON, Tag.TYPE_LOCATION};
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(this)
                .setTitle("Add Tag")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String type  = (String) spinner.getSelectedItem();
                    String value = valueEdit.getText().toString().trim();
                    if (value.isEmpty()) {
                        Toast.makeText(this, "Value cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Tag tag = new Tag(type, value);
                    Photo photo = photos.get(currentIndex);
                    if (photo.getTags().contains(tag)) {
                        Toast.makeText(this, "Tag already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        photo.addTag(tag);
                        PhotosApp.getInstance().save();
                        displayCurrent();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteTagDialog() {
        Photo photo = photos.get(currentIndex);
        List<Tag> tags = photo.getTags();
        if (tags.isEmpty()) return;

        String[] tagStrings = new String[tags.size()];
        for (int i = 0; i < tags.size(); i++) tagStrings[i] = tags.get(i).toString();

        new AlertDialog.Builder(this)
                .setTitle("Delete Tag")
                .setItems(tagStrings, (dialog, which) -> {
                    photo.removeTag(tags.get(which));
                    PhotosApp.getInstance().save();
                    displayCurrent();
                })
                .show();
    }
}
