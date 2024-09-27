package com.learningapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class TicTacToeActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean playerXTurn = true;
    private int turnCount = 0;

    // Using color resources
    private int colorX;
    private int colorO;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tic_tac_toe);
        backButton = findViewById(R.id.backButton);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicTacToeActivity.this, HomePage.class);
                startActivity(intent);
            }
        });

        // Initialize colors from resources
        colorX = ContextCompat.getColor(this, R.color.colorX);
        colorO = ContextCompat.getColor(this, R.color.colorO);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + (i * 3 + j + 1);
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(new CellClickListener(i, j));
            }
        }

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> resetGame());

        Button rulesButton = findViewById(R.id.rulesButton);
        rulesButton.setOnClickListener(v -> {
            Intent intent = new Intent(TicTacToeActivity.this, RulesTicTacToe.class);
            startActivity(intent); // Start the RulesActivity
        });
    }

    private class CellClickListener implements View.OnClickListener {
        private final int row;
        private final int col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            if (!((Button) v).getText().toString().equals("")) return;

            if (playerXTurn) {
                ((Button) v).setText("X");
                v.setBackgroundColor(colorX); // Set color for X
            } else {
                ((Button) v).setText("O");
                v.setBackgroundColor(colorO); // Set color for O
            }

            turnCount++;

            if (checkForWin()) {
                showResultDialog(playerXTurn ? "X Wins!" : "O Wins!");
            } else if (turnCount == 9) {
                showResultDialog("It's a Draw!");
            } else {
                playerXTurn = !playerXTurn; // Change turns
            }
        }
    }

    private boolean checkForWin() {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().toString().equals(buttons[i][1].getText().toString()) &&
                    buttons[i][0].getText().toString().equals(buttons[i][2].getText().toString()) &&
                    !buttons[i][0].getText().toString().equals("")) {
                return true;
            }
            if (buttons[0][i].getText().toString().equals(buttons[1][i].getText().toString()) &&
                    buttons[0][i].getText().toString().equals(buttons[2][i].getText().toString()) &&
                    !buttons[0][i].getText().toString().equals("")) {
                return true;
            }
        }
        if (buttons[0][0].getText().toString().equals(buttons[1][1].getText().toString()) &&
                buttons[0][0].getText().toString().equals(buttons[2][2].getText().toString()) &&
                !buttons[0][0].getText().toString().equals("")) {
            return true;
        }
        if (buttons[0][2].getText().toString().equals(buttons[1][1].getText().toString()) &&
                buttons[0][2].getText().toString().equals(buttons[2][0].getText().toString()) &&
                !buttons[0][2].getText().toString().equals("")) {
            return true;
        }
        return false;
    }

    private void showResultDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> resetGame())
                .show();
    }

    private void resetGame() {
        turnCount = 0;
        playerXTurn = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(""); // Clear X and O
                buttons[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.background)); // Reset background color to the defined background color
            }
        }
    }
}