package com.photos.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.photos.PhotosApp;
import com.photos.R;
import com.photos.model.Album;
import com.photos.model.User;
import com.photos.ui.adapters.AlbumAdapter;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private AlbumAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user     = PhotosApp.getInstance().getCurrentUser();
        listView = findViewById(R.id.albumListView);

        adapter = new AlbumAdapter(this, user.getAlbums());
        listView.setAdapter(adapter);

        // Open album on tap
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Album album = user.getAlbums().get(position);
            Intent intent = new Intent(this, AlbumActivity.class);
            intent.putExtra("albumName", album.getName());
            startActivity(intent);
        });

        // Long-press for rename / delete
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Album album = user.getAlbums().get(position);
            showAlbumOptions(album);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create_album) {
            showCreateAlbumDialog();
            return true;
        } else if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCreateAlbumDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Album name");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create Album")
                .setView(input)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (user.getAlbumByName(name) != null) {
                Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            user.addAlbum(new Album(name));
            PhotosApp.getInstance().save();
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
    }

    private void showAlbumOptions(Album album) {
        String[] options = {"Rename", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(album.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) showRenameDialog(album);
                    else            showDeleteConfirm(album);
                })
                .show();
    }

    private void showRenameDialog(Album album) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(album.getName());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setView(input)
                .setPositiveButton("Rename", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!name.equalsIgnoreCase(album.getName()) &&
                    user.getAlbumByName(name) != null) {
                Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            album.setName(name);
            PhotosApp.getInstance().save();
            adapter.notifyDataSetChanged();
        });
    }

    private void showDeleteConfirm(Album album) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Album")
                .setMessage("Delete \"" + album.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    user.removeAlbum(album);
                    PhotosApp.getInstance().save();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
