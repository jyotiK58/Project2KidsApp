package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QuizResult extends AppCompatActivity {

    private TextView resultsTextView;
    Button retry, backHome;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_result);

        resultsTextView = findViewById(R.id.results_text_view);
        retry = findViewById(R.id.retry_button);
        backHome = findViewById(R.id.home_button);

        // Get the data from the Intent
        Intent intent = getIntent();
        String results = intent.getStringExtra("RESULTS");
        selectedCategory = intent.getStringExtra("CATEGORY");  // Get the category for retry functionality

        // Display the results
        resultsTextView.setText(results);

        // Retry button should reload the quiz for the same category
        retry.setOnClickListener(v -> {
            Intent retryIntent = new Intent(QuizResult.this, QuizFetchAndView.class);
            retryIntent.putExtra("CATEGORY", selectedCategory);  // Pass the selected category back to QuizFetchAndView
            startActivity(retryIntent);
        });

        // Back to home button
        backHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(QuizResult.this, HomePage.class);
            startActivity(homeIntent);
        });
    }
}
