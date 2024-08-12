package com.learningapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String QUESTIONS_URL = "http://10.0.2.2/KidsApp/fetch_quiz.php?category_id=";
    private TextView questionText;
    private LinearLayout answersContainer;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q_activity_quiz);

        questionText = findViewById(R.id.question_text);
        answersContainer = findViewById(R.id.answers_container);

        int categoryId = 1; // Replace with your desired category ID
        new FetchQuestionsTask().execute(categoryId);
    }

    private void displayQuestion(QuizQuestion question) {
        questionText.setText(question.getQuestionText());

        answersContainer.removeAllViews(); // Clear previous answers

        for (QuizAnswer answer : question.getAnswers()) {
            Button answerButton = new Button(this);
            answerButton.setText(answer.getAnswerText());
            answerButton.setOnClickListener(v -> handleAnswerClick(answer));
            answersContainer.addView(answerButton);
        }
    }

    private void handleAnswerClick(QuizAnswer answer) {
        // Handle the answer click
        Toast.makeText(this, "Selected: " + answer.getAnswerText(), Toast.LENGTH_SHORT).show();

        // Move to the next question
        if (++currentQuestionIndex < questions.size()) {
            displayQuestion(questions.get(currentQuestionIndex));
        } else {
            Toast.makeText(this, "Quiz complete!", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchQuestionsTask extends AsyncTask<Integer, Void, List<QuizQuestion>> {

        @Override
        protected List<QuizQuestion> doInBackground(Integer... params) {
            int categoryId = params[0];
            String urlString = QUESTIONS_URL + categoryId;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                return parseQuestions(result.toString());

            } catch (Exception e) {
                Log.e(TAG, "Error fetching questions", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<QuizQuestion> result) {
            if (result == null) {
                Toast.makeText(QuizActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                return;
            }

            questions = result;
            if (!questions.isEmpty()) {
                displayQuestion(questions.get(currentQuestionIndex));
            }
        }

        private List<QuizQuestion> parseQuestions(String jsonResponse) {
            List<QuizQuestion> questions = new ArrayList<>();
            try {
                JSONArray questionsArray = new JSONArray(jsonResponse);

                for (int i = 0; i < questionsArray.length(); i++) {
                    JSONObject questionObj = questionsArray.getJSONObject(i);
                    String questionText = questionObj.getString("question_text");

                    JSONArray answersArray = questionObj.getJSONArray("answers");
                    List<QuizAnswer> answers = new ArrayList<>();
                    for (int j = 0; j < answersArray.length(); j++) {
                        JSONObject answerObj = answersArray.getJSONObject(j);
                        String answerText = answerObj.getString("answer_text");
                        boolean isCorrect = answerObj.getBoolean("is_correct"); // Assuming your JSON includes this field
                        answers.add(new QuizAnswer(answerText, isCorrect));
                    }

                    questions.add(new QuizQuestion(questionText, answers));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error parsing JSON", e);
            }
            return questions;
        }

    }
}
