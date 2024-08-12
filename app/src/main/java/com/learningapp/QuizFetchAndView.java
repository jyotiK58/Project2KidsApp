package com.learningapp;



import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuizFetchAndView extends AppCompatActivity {

    private RecyclerView questionsRecyclerView;
    private QuizAdapter quizAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q_display_question);

        questionsRecyclerView = findViewById(R.id.answers_recycler_view);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch questions
        new FetchQuestionsTask().execute("http://10.0.2.2/KidsApp/fetch_quiz.php");
    }

    private class FetchQuestionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            StringBuilder result = new StringBuilder();
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                List<QuizQuestion> questions = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String questionText = jsonObject.getString("questionText");

                    List<QuizAnswer> answers = new ArrayList<>();
                    JSONArray answersArray = jsonObject.getJSONArray("answers");
                    for (int j = 0; j < answersArray.length(); j++) {
                        JSONObject answerObject = answersArray.getJSONObject(j);
                        String answerText = answerObject.getString("answerText");
                        boolean isCorrect = answerObject.getBoolean("isCorrect");
                        answers.add(new QuizAnswer(answerText, isCorrect));
                    }
                    questions.add(new QuizQuestion(questionText, answers));
                }

                quizAdapter = new QuizAdapter(questions);
                questionsRecyclerView.setAdapter(quizAdapter);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("QuizActivity", "Error parsing JSON", e);
            }
        }
    }
}
