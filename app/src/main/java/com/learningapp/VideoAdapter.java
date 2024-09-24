package com.learningapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;

public class VideoAdapter extends ArrayAdapter<GetterSetterVideo> {

    private static final String TAG = "VideoAdapter";
    private Context context;
    private List<GetterSetterVideo> videos;

    public VideoAdapter(@NonNull Context context, List<GetterSetterVideo> videos) {
        super(context, R.layout.video_item, videos);
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
            holder = new ViewHolder();
            holder.videoImage = convertView.findViewById(R.id.video_image);
            holder.videoTitle = convertView.findViewById(R.id.video_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GetterSetterVideo video = videos.get(position);

        holder.videoTitle.setText(video.getTitle());

        if (video.getImageUrl() != null && !video.getImageUrl().isEmpty()) {
            Log.d(TAG, "Loading image for " + video.getTitle() + ": " + video.getImageUrl());
            Picasso.get()
                    .load(video.getImageUrl())
                    .placeholder(R.drawable.ic_home)
                    .error(R.drawable.account)
                    .into(holder.videoImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image loaded successfully for " + video.getTitle());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Error loading image for " + video.getTitle() + ": " + e.getMessage());
                        }
                    });
        } else {
            Log.w(TAG, "No image URL for " + video.getTitle() + ", using default image");
            holder.videoImage.setImageResource(R.mipmap.flowers);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView videoImage;
        TextView videoTitle;
    }
}