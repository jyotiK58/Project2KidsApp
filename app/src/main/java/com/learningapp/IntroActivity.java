package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private CustomVideoView videoView;
    private Button skipButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPreferences";
    private static final String FIRST_LAUNCH_KEY = "isFirstLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if it's the first launch
        boolean isFirstLaunch = sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true);

        if (isFirstLaunch) {
            // First time launch, show intro video
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
                // Mark as not first launch
                markIntroAsWatched();
                // Optionally, navigate to another activity or perform other UI updates here
                navigateToLogin();
            });

            // Automatically mark video as watched when it finishes
            videoView.setOnCompletionListener(mp -> {
                // Mark as not first launch
                markIntroAsWatched();
                // Navigate to login activity
                navigateToLogin();
            });

        } else {
            // If not first launch, skip intro and go directly to login
            navigateToLogin();
        }
    }

    private void markIntroAsWatched() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LAUNCH_KEY, false);  // Set the value to false so the intro won't show again
        editor.apply();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Close the current activity to prevent returning to it
    }
}