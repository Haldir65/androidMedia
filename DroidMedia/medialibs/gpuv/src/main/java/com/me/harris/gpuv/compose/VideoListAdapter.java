package com.me.harris.gpuv.compose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.daasuu.gpuv.R;

import java.util.List;

public class VideoListAdapter extends ArrayAdapter<VideoItem> {

    private LayoutInflater layoutInflater;

    public VideoListAdapter(Context context, int resource, List<VideoItem> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoItem data = getItem(position);

        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.row_video_list, null);
        }

        ImageView imageView = convertView.findViewById(R.id.image);
        TextView textView = convertView.findViewById(R.id.txt_image_name);

        ImageLoader.loadVideoThumbnail(imageView,data.getPath(),getContext());
//        Glide.with(getContext().getApplicationContext())
//                .load(data.getPath())
//                .into(imageView);


        textView.setText(data.getPath());

        return convertView;
    }

}

