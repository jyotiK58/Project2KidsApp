package com.learningapp;

import java.util.concurrent.atomic.AtomicInteger;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private List<QuizQuestion> quizQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private QuizAdapter quizAdapter;
    private TextView questionTextView;
    private Button nextButton;
    private StringBuilder results = new StringBuilder();
    private String category;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String USER_ID_KEY = "user_id"; // Key for user ID
    private AtomicInteger correctAnswers = new AtomicInteger(0); // Count of correct answers
    private AtomicInteger wrongAnswers = new AtomicInteger(0); // Count of wrong answers

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q_each_question);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        category = getIntent().getStringExtra("CATEGORY");
        Log.d(TAG, "Category received: " + category);

        questionTextView = findViewById(R.id.questionTextView);
        nextButton = findViewById(R.id.nextButton);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(new ArrayList<>(), this::handleAnswerSelection);
        recyclerView.setAdapter(quizAdapter);

        fetchQuizQuestions(category);
    }

    private void fetchQuizQuestions(String category) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetch_quiz.php?category=" + category;
        Log.d(TAG, "Fetching quiz questions from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                this::onResponse,
                error -> {
                    Log.e(TAG, "Error fetching quiz questions", error);
                    showToast("Error fetching quiz questions. Please try again later.");
                });

        queue.add(stringRequest);
    }

    private void onResponse(String response) {
        String trimmedResponse = response.trim();
        if (trimmedResponse.startsWith("{") || trimmedResponse.startsWith("[")) {
            parseQuestions(trimmedResponse);
        } else {
            Log.e(TAG, "Unexpected response format: " + response);
            showToast("Unexpected response from server.");
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
                showToast("No questions found for this category.");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            showToast("Error parsing quiz questions.");
        }
    }

    private void handleAnswerSelection(QuizAnswer selectedAnswer) {
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        currentQuestion.setUserSelectedAnswer(selectedAnswer);

        // Record the result for this question
        results.append("Question: ").append(currentQuestion.getQuestionText()).append("\n");
        results.append("Selected Answer: ").append(selectedAnswer.getAnswerText()).append("\n");
        results.append("Correct Answer: ").append(currentQuestion.getCorrectAnswer().getAnswerText()).append("\n\n");

        // Increment correct or wrong answer count
        if (selectedAnswer.isCorrect()) {
            correctAnswers.incrementAndGet();
        } else {
            wrongAnswers.incrementAndGet();
        }

        // Move to the next question
        currentQuestionIndex++;
        if (currentQuestionIndex < quizQuestions.size()) {
            showQuestion(currentQuestionIndex);
        } else {
            sendQuizResultsToServer();
        }
    }

    private void sendQuizResultsToServer() {
        String userId = sharedPreferences.getString(USER_ID_KEY, "");
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is missing. Cannot send results to server.");
            showToast("User ID is missing. Please log in again.");
            return;
        }

        String url = "http://10.0.2.2/PhpForKidsLearninApp/quiz_result.php"; // Using the same URL for all results
        RequestQueue queue = Volley.newRequestQueue(this);
        int totalQuestions = quizQuestions.size();
        AtomicInteger completedRequests = new AtomicInteger(0);

        // Iterate through each question and send the results
        for (QuizQuestion question : quizQuestions) {
            int questionId = question.getId();
            int categoryId = getCategoryId(category);
            int isCorrect = (question.getUserSelectedAnswer() != null && question.getUserSelectedAnswer().isCorrect()) ? 1 : 0;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d(TAG, "Response from server: " + response);
                        checkAllRequestsCompleted(completedRequests.incrementAndGet(), totalQuestions);
                    },
                    error -> {
                        Log.e(TAG, "Error sending results to server", error);
                        checkAllRequestsCompleted(completedRequests.incrementAndGet(), totalQuestions);
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", userId);
                    params.put("quiz_question_id", String.valueOf(questionId));
                    params.put("category_id", String.valueOf(categoryId));
                    params.put("correct", String.valueOf(isCorrect));
                    params.put("wrong", String.valueOf(isCorrect == 0 ? 1 : 0)); // Send wrong count
                    return params;
                }
            };

            queue.add(stringRequest);
        }
    }

    private void checkAllRequestsCompleted(int completedRequests, int totalQuestions) {
        if (completedRequests == totalQuestions) {
            // All results have been sent successfully
            sendOverallResultsToServer(correctAnswers.get(), wrongAnswers.get());
        } else {
            showToast("Some results could not be submitted. Please check your connection.");
        }
        navigateToResults();
    }

    private void sendOverallResultsToServer(int correctCount, int wrongCount) {
        String userId = sharedPreferences.getString(USER_ID_KEY, "");
        String url = "http://10.0.2.2/PhpForKidsLearninApp/quiz_result.php"; // Using the same URL for overall results
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Log.d(TAG, "Overall results updated: " + response),
                error -> Log.e(TAG, "Error updating overall results", error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("correct_count", String.valueOf(correctCount));
                params.put("wrong_count", String.valueOf(wrongCount));
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void navigateToResults() {
        Intent intent = new Intent(this, QuizResult.class);
        intent.putExtra("RESULTS", results.toString());
        startActivity(intent);
        finish();
    }

    private void showQuestion(int index) {
        QuizQuestion question = quizQuestions.get(index);
        questionTextView.setText(question.getQuestionText());
        quizAdapter.updateAnswers(question.getAnswers());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private int getCategoryId(String category) {
        // Map category names to category IDs
        if ("Alphabets".equals(category)) {
            return 1;
        } else if ("Numbers".equals(category)) {
            return 2;
        } else if ("Animals".equals(category)) {
            return 3;
        }
        return 0; // Default category ID
    }
}
