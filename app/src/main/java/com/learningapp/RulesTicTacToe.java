package com.learningapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RulesTicTacToe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_tictactoe);

        // Button to close rules and go back to TicTacToeActivity
        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish()); // Close the activity
    }
}