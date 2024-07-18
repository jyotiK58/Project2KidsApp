package com.learningapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FruitsActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ImageView apple,watermelon,cherry,strawberry;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fruits_layout);
        apple = findViewById(R.id.apple);
        watermelon = findViewById(R.id.watermelon);
        cherry = findViewById(R.id.cherry);
        strawberry = findViewById(R.id.strawberry);

        apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(R.raw.apple);
            }
        });

        watermelon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(R.raw.watermelon);
            }
        });
    }
        private void playSound(int soundResourceId) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, soundResourceId);
            mediaPlayer.start();
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

    }

