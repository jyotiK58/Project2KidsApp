package com.learningapp;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QuizResult extends AppCompatActivity {

    private TextView resultsTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_result);

        resultsTextView = findViewById(R.id.results_text_view);

        // Get score and total questions from intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        // Display results
        String resultsText = "You scored " + score + " out of " + totalQuestions + "!";
        resultsTextView.setText(resultsText);
    }
}
