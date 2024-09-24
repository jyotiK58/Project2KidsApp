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

    private static final String TAG = "VideoAdapter"; // For logging
    private Context context; // Context of the calling Activity
    private List<GetterSetterVideo> videos; // List of video objects to display

    // Constructor
    public VideoAdapter(@NonNull Context context, List<GetterSetterVideo> videos) {
        super(context, R.layout.video_item, videos); // Use video_item layout for each item
        this.context = context; // Initialize context
        this.videos = videos; // Initialize the list of videos
    }

    // Called when each list item is created or recycled
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder; // Holds references to the views

        if (convertView == null) {
            // If the view doesn't exist, inflate a new view using video_item layout
            convertView = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
            holder = new ViewHolder();
            holder.videoImage = convertView.findViewById(R.id.video_image); // Image view for video thumbnail
            holder.videoTitle = convertView.findViewById(R.id.video_title); // Text view for video title
            convertView.setTag(holder); // Save the view holder for later
        } else {
            // Reuse the view holder if the view exists
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current video item from the list
        GetterSetterVideo video = videos.get(position);

        // Set the video title in the TextView
        holder.videoTitle.setText(video.getTitle());

        // Check if the image URL is not null or empty before loading it
        if (video.getImageUrl() != null && !video.getImageUrl().isEmpty()) {
            Log.d(TAG, "Loading image for " + video.getTitle() + ": " + video.getImageUrl());

            // Load the image using Picasso with a placeholder and error image
            Picasso.get()
                    .load(video.getImageUrl()) // URL of the image
                    .placeholder(R.drawable.ic_home) // Placeholder image while loading
                    .error(R.drawable.account) // Image to show on error
                    .into(holder.videoImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Log successful image load
                            Log.d(TAG, "Image loaded successfully for " + video.getTitle());
                        }

                        @Override
                        public void onError(Exception e) {
                            // Log error if image fails to load
                            Log.e(TAG, "Error loading image for " + video.getTitle() + ": " + e.getMessage());
                        }
                    });
        } else {
            // If no image URL is available, set a default image
            Log.w(TAG, "No image URL for " + video.getTitle() + ", using default image");
            holder.videoImage.setImageResource(R.mipmap.flowers); // Default image
        }

        return convertView; // Return the completed view
    }

    // ViewHolder pattern to improve list performance by avoiding repeated calls to findViewById
    private static class ViewHolder {
        ImageView videoImage; // ImageView for the video thumbnail
        TextView videoTitle; // TextView for the video title
    }
}