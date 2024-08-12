package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class QuizCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_category);

        findViewById(R.id.cardAlphabets).setOnClickListener(v -> startQuizActivity(1));
        findViewById(R.id.cardNumberQuiz).setOnClickListener(v -> startQuizActivity(2));
        findViewById(R.id.cardAnimals).setOnClickListener(v -> startQuizActivity(3));
        findViewById(R.id.cardColor).setOnClickListener(v -> startQuizActivity(4));
        findViewById(R.id.cardStories).setOnClickListener(v -> startQuizActivity(5));
        findViewById(R.id.cardBirds).setOnClickListener(v -> startQuizActivity(6));
        findViewById(R.id.cardFlowers).setOnClickListener(v -> startQuizActivity(7));
        findViewById(R.id.cardFruits).setOnClickListener(v -> startQuizActivity(8));
        findViewById(R.id.cardEmotions).setOnClickListener(v -> startQuizActivity(9));
        findViewById(R.id.cardMonths).setOnClickListener(v -> startQuizActivity(10));
        findViewById(R.id.cardWeeks).setOnClickListener(v -> startQuizActivity(11));
        findViewById(R.id.cardWeather).setOnClickListener(v -> startQuizActivity(12));
    }

    private void startQuizActivity(int categoryId) {
        Intent intent = new Intent(QuizCategory.this, QuizActivity.class);
        intent.putExtra("CATEGORY_ID", categoryId);
        startActivity(intent);
    }
}
