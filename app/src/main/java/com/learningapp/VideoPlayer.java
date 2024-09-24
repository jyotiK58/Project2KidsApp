package com.learningapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayer extends AppCompatActivity {

    private static final String TAG = "VideoPlayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);

        Intent intent = getIntent();
        String videoUrl = intent.getStringExtra("VIDEO_URL");

        if (videoUrl != null && !videoUrl.isEmpty()) {
            openVideo(videoUrl);
        } else {
            Log.e(TAG, "No video URL passed to VideoPlayer");
            Toast.makeText(this, "Error: No video URL provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void openVideo(String videoUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        intent.setDataAndType(Uri.parse(videoUrl), "video/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "No app available to play video");
            Toast.makeText(this, "No app available to play video", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
