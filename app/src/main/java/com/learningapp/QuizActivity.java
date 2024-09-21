package com.learningapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private List<QuizQuestion> quizQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private QuizAdapter quizAdapter;
    private TextView questionTextView;
    private Button nextButton;
    private StringBuilder results = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q_each_question);

        questionTextView = findViewById(R.id.questionTextView);
        nextButton = findViewById(R.id.nextButton);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(new ArrayList<>(), new QuizAdapter.QuizAdapterListener() {
            @Override
            public void onAnswerSelected(QuizAnswer selectedAnswer) {
                handleAnswerSelection(selectedAnswer);
            }
        });
        recyclerView.setAdapter(quizAdapter);

        new FetchQuestionsTask().execute("http://10.0.2.2/PhpForKidsLearninApp/fetch_quiz.php");
    }

    private void handleAnswerSelection(QuizAnswer selectedAnswer) {
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        currentQuestion.setUserSelectedAnswer(selectedAnswer);

        // Record the result for this question
        results.append("Question: ").append(currentQuestion.getQuestionText()).append("\n");
        results.append("Selected Answer: ").append(selectedAnswer.getAnswerText()).append("\n");
        results.append("Correct Answer: ").append(currentQuestion.getCorrectAnswer().getAnswerText()).append("\n\n");

        // Move to next question
        currentQuestionIndex++;
        if (currentQuestionIndex < quizQuestions.size()) {
            showQuestion(currentQuestionIndex);
        } else {
            // Pass results to QuizResult activity
            Intent intent = new Intent(QuizActivity.this, QuizResult.class);
            intent.putExtra("RESULTS", results.toString());
            startActivity(intent);
            finish(); // Close the current activity
        }
    }

    private void showQuestion(int index) {
        QuizQuestion question = quizQuestions.get(index);
        questionTextView.setText(question.getQuestionText());
        quizAdapter.setAnswers(question.getAnswers());
        quizAdapter.notifyDataSetChanged();
    }

    private class FetchQuestionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String jsonString = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                jsonString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.e("FetchQuestionsTask", "Error fetching questions");
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(result);
                quizQuestions.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject questionObject = jsonArray.getJSONObject(i);
                    int questionId = questionObject.getInt("id");
                    String questionText = questionObject.getString("question_text");

                    QuizQuestion quizQuestion = new QuizQuestion(questionId, questionText);

                    JSONArray answersArray = questionObject.getJSONArray("answers");
                    for (int j = 0; j < answersArray.length(); j++) {
                        JSONObject answerObject = answersArray.getJSONObject(j);
                        int answerId = answerObject.getInt("id");
                        String answerText = answerObject.getString("answer_text");
                        boolean isCorrect = answerObject.getBoolean("is_correct");

                        QuizAnswer quizAnswer = new QuizAnswer(answerId, questionId, answerText, isCorrect);
                        quizQuestion.addAnswer(quizAnswer);

                        if (isCorrect) {
                            quizQuestion.setCorrectAnswer(quizAnswer);
                        }
                    }

                    quizQuestions.add(quizQuestion);
                }
                quizAdapter.notifyDataSetChanged();
                if (!quizQuestions.isEmpty()) {
                    showQuestion(0);
                }
            } catch (JSONException e) {
                Log.e("FetchQuestionsTask", "Error parsing JSON", e);
            }
        }
    }
}
