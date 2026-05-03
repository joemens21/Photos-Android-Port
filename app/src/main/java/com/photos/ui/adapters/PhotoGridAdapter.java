package com.photos.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.photos.R;
import com.photos.model.Photo;

import java.util.List;

public class PhotoGridAdapter extends BaseAdapter {

    private final Context     context;
    private final List<Photo> photos;

    public PhotoGridAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos  = photos;
    }

    @Override public int    getCount()        { return photos.size(); }
    @Override public Object getItem(int pos)  { return photos.get(pos); }
    @Override public long   getItemId(int pos){ return pos; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_photo_grid, parent, false);
        }
        Photo photo = photos.get(position);

        ImageView imageView = convertView.findViewById(R.id.thumbnailImage);
        TextView  nameText  = convertView.findViewById(R.id.thumbnailName);

        Uri uri = Uri.parse(photo.getFilePath());
        imageView.setImageURI(uri);
        nameText.setText(photo.getFileName());

        return convertView;
    }
}
