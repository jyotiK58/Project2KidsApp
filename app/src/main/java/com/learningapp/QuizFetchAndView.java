package com.learningapp;

import android.annotation.SuppressLint;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFetchAndView extends AppCompatActivity {

    private RecyclerView answersRecyclerView;
    private TextView questionTextView;
    private Button nextButton;

    private List<QuizQuestion> quizQuestions = new ArrayList<>();
    private QuizAdapter quizAdapter;
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_fetch_and_view);

        questionTextView = findViewById(R.id.questionTextView);
        answersRecyclerView = findViewById(R.id.answers_recycler_view);
        nextButton = findViewById(R.id.nextButton);

        answersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Inside QuizActivity
        quizAdapter = new QuizAdapter(new ArrayList<>(), new QuizAdapter.QuizAdapterListener() {
            @Override
            public void onAnswerSelected(QuizAnswer selectedAnswer) {
                handleAnswerSelection(selectedAnswer);
            }
        });


        answersRecyclerView.setAdapter(quizAdapter);

        new FetchQuestionsTask().execute("http://10.0.2.2/KidsApp/fetch_quiz.php");
    }

    private void handleAnswerSelection(QuizAnswer selectedAnswer) {
        QuizQuestion currentQuestion = quizQuestions.get(currentQuestionIndex);
        currentQuestion.setUserSelectedAnswer(selectedAnswer);

        currentQuestionIndex++;
        if (currentQuestionIndex < quizQuestions.size()) {
            showQuestion(currentQuestionIndex);
        } else {
            showResults();
        }
    }

    private void showQuestion(int index) {
        QuizQuestion question = quizQuestions.get(index);
        questionTextView.setText(question.getQuestionText());
        quizAdapter.setAnswers(question.getAnswers());
        quizAdapter.notifyDataSetChanged();
    }

    private void showResults() {
        // Logic to display results
        // For demonstration purposes, you can log the results
        StringBuilder results = new StringBuilder();
        for (QuizQuestion question : quizQuestions) {
            results.append("Question: ").append(question.getQuestionText()).append("\n");
            results.append("Selected Answer: ").append(question.getUserSelectedAnswer().getAnswerText()).append("\n");
            results.append("Correct Answer: ").append(question.getCorrectAnswer().getAnswerText()).append("\n\n");
        }
        Log.d("QuizResults", results.toString());
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
                Map<Integer, QuizQuestion> questionMap = new HashMap<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject questionObject = jsonArray.getJSONObject(i);
                    int questionId = questionObject.getInt("id");
                    String questionText = questionObject.getString("question_text");

                    QuizQuestion quizQuestion = questionMap.get(questionId);
                    if (quizQuestion == null) {
                        quizQuestion = new QuizQuestion(questionId, questionText);
                        questionMap.put(questionId, quizQuestion);
                    }

                    JSONArray answersArray = questionObject.getJSONArray("answers");
                    for (int j = 0; j < answersArray.length(); j++) {
                        JSONObject answerObject = answersArray.getJSONObject(j);
                        int answerId = answerObject.getInt("id");
                        String answerText = answerObject.getString("answer_text");
                        boolean isCorrect = answerObject.getBoolean("is_correct");

                        QuizAnswer quizAnswer = new QuizAnswer(answerId, questionId, answerText, isCorrect);
                        quizQuestion.addAnswer(quizAnswer);
                    }
                }
                quizQuestions.addAll(questionMap.values());
                if (!quizQuestions.isEmpty()) {
                    showQuestion(0);
                }
            } catch (JSONException e) {
                Log.e("FetchQuestionsTask", "Error parsing JSON", e);
            }
        }
    }

}
