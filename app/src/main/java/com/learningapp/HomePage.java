package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomePage extends AppCompatActivity {

    private CardView start_learning;
    private CardView quiz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_layout);
        start_learning = findViewById(R.id.start_learning);
        quiz = findViewById(R.id.quiz);
        start_learning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Category.class);
                startActivity(intent);
            }
        });


        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }
    }

