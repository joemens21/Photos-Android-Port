package com.photos.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.photos.R;
import com.photos.model.Album;

import java.util.List;

public class AlbumAdapter extends BaseAdapter {

    private final Context    context;
    private final List<Album> albums;

    public AlbumAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums  = albums;
    }

    @Override public int     getCount()             { return albums.size(); }
    @Override public Object  getItem(int pos)        { return albums.get(pos); }
    @Override public long    getItemId(int pos)      { return pos; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_album, parent, false);
        }
        Album album = albums.get(position);

        TextView nameText  = convertView.findViewById(R.id.albumName);
        TextView countText = convertView.findViewById(R.id.albumPhotoCount);

        nameText.setText(album.getName());
        countText.setText(album.getPhotoCount() + " photo" +
                (album.getPhotoCount() == 1 ? "" : "s"));

        return convertView;
    }
}
