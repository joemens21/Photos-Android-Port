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
import com.photos.model.Tag;

import java.util.List;

public class SearchResultAdapter extends BaseAdapter {

    private final Context     context;
    private final List<Photo> photos;

    public SearchResultAdapter(Context context, List<Photo> photos) {
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
                    .inflate(R.layout.item_search_result, parent, false);
        }
        Photo photo = photos.get(position);

        ImageView thumb    = convertView.findViewById(R.id.resultThumb);
        TextView  nameText = convertView.findViewById(R.id.resultName);
        TextView  tagsText = convertView.findViewById(R.id.resultTags);

        thumb.setImageURI(Uri.parse(photo.getFilePath()));
        nameText.setText(photo.getFileName());

        StringBuilder sb = new StringBuilder();
        for (Tag t : photo.getTags()) sb.append(t).append("  ");
        tagsText.setText(sb.toString().trim());

        return convertView;
    }
}
