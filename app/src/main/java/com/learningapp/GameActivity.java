package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private TextView questionText;
    private LinearLayout imageContainer;
    private ImageView frogAnswer;
    private ImageView monkeyAnswer;

    private List<Questions> questions;
    private List<Boolean> userAnswers; // Track user answers
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        questionText = findViewById(R.id.questionText);
        imageContainer = findViewById(R.id.imageContainer);
        frogAnswer = findViewById(R.id.frogAnswer);
        monkeyAnswer = findViewById(R.id.monkeyAnswer);

        questions = generateQuestions();
        userAnswers = new ArrayList<>(); // Initialize user answers list
        loadQuestion();

        frogAnswer.setOnClickListener(v -> handleAnswer(true));
        monkeyAnswer.setOnClickListener(v -> handleAnswer(false));
    }

    private List<Questions> generateQuestions() {
        List<Questions> questionList = new ArrayList<>();
        // Add multiple questions
        questionList.add(new Questions(R.mipmap.frog, R.mipmap.monkey, 3, 2, true)); // Question 1
        questionList.add(new Questions(R.mipmap.frog, R.mipmap.monkey, 1, 4, false)); // Question 2
        questionList.add(new Questions(R.mipmap.frog, R.mipmap.monkey, 5, 1, true)); // Question 3
        questionList.add(new Questions(R.mipmap.frog, R.mipmap.monkey, 2, 2, false)); // Question 4
        questionList.add(new Questions(R.mipmap.frog, R.mipmap.monkey, 3, 3, true)); // Question 5
        // Add more questions as needed
        return questionList;
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Questions currentQuestion = questions.get(currentQuestionIndex);
            displayQuestion(currentQuestion);
        } else {
            // End of game logic
            showFinalResults();
        }
    }

    private void displayQuestion(Questions question) {
        imageContainer.removeAllViews();

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < question.getCount1(); i++) {
            addImageToContainer(question.getImage1(), i < 2 ? topRow : bottomRow);
        }

        for (int i = 0; i < question.getCount2(); i++) {
            addImageToContainer(question.getImage2(), bottomRow);
        }

        imageContainer.addView(topRow);
        imageContainer.addView(bottomRow);
    }

    private void addImageToContainer(int resourceId, LinearLayout container) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resourceId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(60), dpToPx(60)
        );
        params.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        imageView.setLayoutParams(params);
        container.addView(imageView);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void handleAnswer(boolean isFrog) {
        Questions currentQuestion = questions.get(currentQuestionIndex);
        boolean isCorrect = (isFrog && currentQuestion.isAnswerFrog()) ||
                (!isFrog && !currentQuestion.isAnswerFrog());

        // Store the user's answer
        userAnswers.add(isFrog);

        // Immediately move to the next question
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            loadQuestion(); // Load the next question
        } else {
            // End of game logic
            showFinalResults();
        }
    }

    private void showFinalResults() {
        // Calculate the number of correct answers
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            Questions q = questions.get(i);
            boolean userAnswer = userAnswers.get(i);
            boolean isCorrect = (userAnswer && q.isAnswerFrog()) || (!userAnswer && !q.isAnswerFrog());
            if (isCorrect) {
                correctCount++;
            }
        }

        // Pass results to ResultActivity
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra("questions", new ArrayList<>(questions));
        intent.putExtra("userAnswers", new ArrayList<>(userAnswers));
        startActivity(intent);
        finish();
    }

    public static class Questions implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int image1;
        private final int image2;
        private final int count1;
        private final int count2;
        private final boolean answerIsFrog;

        public Questions(int image1, int image2, int count1, int count2, boolean answerIsFrog) {
            this.image1 = image1;
            this.image2 = image2;
            this.count1 = count1;
            this.count2 = count2;
            this.answerIsFrog = answerIsFrog;
        }

        public int getImage1() { return image1; }
        public int getImage2() { return image2; }
        public int getCount1() { return count1; }
        public int getCount2() { return count2; }
        public boolean isAnswerFrog() { return answerIsFrog; }
    }
}
