package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QuizResult extends AppCompatActivity {

    private TextView resultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_result);

        resultsTextView = findViewById(R.id.results_text_view);

        // Get the data from the Intent
        Intent intent = getIntent();
        String results = intent.getStringExtra("RESULTS");

        // Display the results
        resultsTextView.setText(results);
    }
}
