package com.photos.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.photos.PhotosApp;
import com.photos.R;
import com.photos.model.Album;
import com.photos.model.Photo;
import com.photos.model.User;
import com.photos.ui.adapters.PhotoGridAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    public static final int REQUEST_PICK_PHOTO = 1;

    private GridView gridView;
    private PhotoGridAdapter adapter;
    private Album album;
    private User  user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        user = PhotosApp.getInstance().getCurrentUser();
        String albumName = getIntent().getStringExtra("albumName");
        album = user.getAlbumByName(albumName);

        if (album == null) { finish(); return; }

        setTitle(album.getName());

        gridView = findViewById(R.id.photoGridView);
        adapter  = new PhotoGridAdapter(this, album.getPhotos());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> openPhoto(position));

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            showPhotoOptions(album.getPhotos().get(position));
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void openPhoto(int position) {
        Intent intent = new Intent(this, PhotoDisplayActivity.class);
        intent.putExtra("albumName",     album.getName());
        intent.putExtra("photoPosition", position);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_photo) {
            pickPhoto();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // -------------------------------------------------------------------------
    // Multi-photo picker
    // -------------------------------------------------------------------------

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PICK_PHOTO || resultCode != RESULT_OK || data == null) return;

        int added = 0, skipped = 0;

        if (data.getClipData() != null) {
            // Multiple selections
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                if (addPhotoUri(uri)) added++; else skipped++;
            }
        } else if (data.getData() != null) {
            // Single selection
            if (addPhotoUri(data.getData())) added++; else skipped++;
        }

        if (added > 0) {
            PhotosApp.getInstance().save();
            adapter.notifyDataSetChanged();
        }

        if (skipped > 0) {
            Toast.makeText(this, added + " added, " + skipped + " already in album",
                    Toast.LENGTH_SHORT).show();
        } else if (added > 0) {
            Toast.makeText(this,
                    added + " photo" + (added == 1 ? "" : "s") + " added",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Persists read permission and adds the photo. Returns true if added, false if duplicate. */
    private boolean addPhotoUri(Uri uri) {
        try {
            getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        Photo photo = new Photo(uri.toString(), "", Calendar.getInstance());
        if (album.getPhotos().contains(photo)) return false;
        album.addPhoto(photo);
        return true;
    }

    // -------------------------------------------------------------------------
    // Photo options (long-press)
    // -------------------------------------------------------------------------

    private void showPhotoOptions(Photo photo) {
        new AlertDialog.Builder(this)
                .setTitle("Photo options")
                .setItems(new String[]{"Remove", "Move to another album"}, (dialog, which) -> {
                    if (which == 0) confirmRemove(photo);
                    else            showMoveDialog(photo);
                })
                .show();
    }

    private void confirmRemove(Photo photo) {
        new AlertDialog.Builder(this)
                .setTitle("Remove photo")
                .setMessage("Remove this photo from the album?")
                .setPositiveButton("Remove", (d, w) -> {
                    album.removePhoto(photo);
                    PhotosApp.getInstance().save();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMoveDialog(Photo photo) {
        List<Album> targets = new ArrayList<>();
        for (Album a : user.getAlbums()) {
            if (!a.getName().equalsIgnoreCase(album.getName())) targets.add(a);
        }
        if (targets.isEmpty()) {
            Toast.makeText(this, "No other albums exist", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[targets.size()];
        for (int i = 0; i < targets.size(); i++) names[i] = targets.get(i).getName();

        new AlertDialog.Builder(this)
                .setTitle("Move to album")
                .setItems(names, (d, which) -> {
                    Album dest = targets.get(which);
                    if (dest.getPhotos().contains(photo)) {
                        Toast.makeText(this, "Photo already in that album", Toast.LENGTH_SHORT).show();
                    } else {
                        dest.addPhoto(photo);
                        album.removePhoto(photo);
                        PhotosApp.getInstance().save();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Moved to " + dest.getName(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
