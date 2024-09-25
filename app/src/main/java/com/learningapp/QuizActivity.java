package com.learningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private List<QuizQuestion> quizQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private QuizAdapter quizAdapter;
    private TextView questionTextView;
    private Button nextButton;
    private StringBuilder results = new StringBuilder();
    private String category;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q_each_question);

        category = getIntent().getStringExtra("CATEGORY"); // Retrieve the category
        Log.d(TAG, "Category received: " + category);

        questionTextView = findViewById(R.id.questionTextView);
        nextButton = findViewById(R.id.nextButton);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(new ArrayList<>(), selectedAnswer -> handleAnswerSelection(selectedAnswer));
        recyclerView.setAdapter(quizAdapter);

        fetchQuizQuestions(category); // Fetch questions based on the category
    }

    private void fetchQuizQuestions(String category) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetch_quiz.php?category=" + category;
        Log.d(TAG, "Fetching quiz questions from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Response received: " + response);
                    onResponse(response);
                },
                error -> {
                    Log.e(TAG, "Error fetching quiz questions", error);
                    Toast.makeText(QuizActivity.this, "Error fetching quiz questions. Please try again later.", Toast.LENGTH_LONG).show();
                });

        queue.add(stringRequest);
    }

    private void onResponse(String response) {
        // Check if the response is a valid JSON before parsing
        // Trim whitespace and check for valid JSON format
        String trimmedResponse = response.trim();
        if (trimmedResponse.startsWith("{") || trimmedResponse.startsWith("[")) {
            parseQuestions(trimmedResponse); // Proceed with parsing
        } else {
            Log.e(TAG, "Unexpected response format: " + response);
            Toast.makeText(this, "Unexpected response from server.", Toast.LENGTH_LONG).show();
        }
    }

    private void parseQuestions(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
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
            } else {
                Toast.makeText(this, "No questions found for this category.", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            Toast.makeText(this, "Error parsing quiz questions", Toast.LENGTH_LONG).show();
        }
    }

    private void handleAnswerSelection(QuizAnswer selectedAnswer) {
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        currentQuestion.setUserSelectedAnswer(selectedAnswer);

        // Record the result for this question
        results.append("Question: ").append(currentQuestion.getQuestionText()).append("\n");
        results.append("Selected Answer: ").append(selectedAnswer.getAnswerText()).append("\n");
        results.append("Correct Answer: ").append(currentQuestion.getCorrectAnswer().getAnswerText()).append("\n\n");

        // Move to the next question
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
}
