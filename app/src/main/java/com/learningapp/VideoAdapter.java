package com.learningapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends ArrayAdapter<GetterSetterVideo> {

    private Context context;
    private List<GetterSetterVideo> videos;

    public VideoAdapter(@NonNull Context context, List<GetterSetterVideo> videos) {
        super(context, R.layout.video_player, videos);
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        }

        GetterSetterVideo video = videos.get(position);

        // Set video title
        TextView videoTitle = convertView.findViewById(R.id.video_title);
        videoTitle.setText(video.getTitle());

        // Set video image
        ImageView videoImage = convertView.findViewById(R.id.video_image);
        Picasso.get().load(video.getImageUrl()).into(videoImage);

        return convertView;
    }
}
