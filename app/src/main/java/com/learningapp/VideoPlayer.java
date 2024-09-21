package com.learningapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayer extends AppCompatActivity {

    private static final String TAG = "VideoPlayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);

        // Get the video URL from the intent and play it
        Intent intent = getIntent();
        String videoUrl = intent.getStringExtra("VIDEO_URL");

        if (videoUrl != null) {
            openVideo(videoUrl);
        } else {
            Log.e(TAG, "No video URL passed to VideoPlayer");
        }
    }

    private void openVideo(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        startActivity(intent);
    }
}
