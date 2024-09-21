package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // Initialize category items
        CardView categoryItemStudy = findViewById(R.id.category_item_study);
        CardView categoryItemQuiz = findViewById(R.id.category_item_quiz);
        CardView categoryItemLullaby = findViewById(R.id.category_item_lullaby);
        CardView categoryItemVideo = findViewById(R.id.category_item_video);
       ImageView managesetting = findViewById(R.id.manage_setting);


        managesetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ManageSetting.class);
                startActivity(intent);
            }
        });

        categoryItemStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Category.class);
                startActivity(intent);
            }
        });

        categoryItemQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, QuizActivity.class);
                startActivity(intent);
            }
        });

        categoryItemLullaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, LullabyActivity.class);
                startActivity(intent);
            }
        });

        categoryItemVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, VideoLearning.class);
                startActivity(intent);
            }
        });

        // Start activity for learning card button
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Category.class);
                startActivity(intent);
            }
        });
    }
}
