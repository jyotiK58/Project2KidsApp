package com.learningapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private CustomVideoView videoView;
    private Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_video);  // Ensure this matches the layout file name

        videoView = findViewById(R.id.videoView);
        skipButton = findViewById(R.id.skipButton);

        // Set the path to the video
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.videointro;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // Start the video once prepared
        videoView.setOnPreparedListener(mp -> videoView.start());

        // Handle skip button click
        skipButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            // Optionally, navigate to another activity or perform other UI updates here
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
