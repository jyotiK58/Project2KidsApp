package com.learningapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
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

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String QUESTIONS_URL = "http://10.0.2.2/KidsApp/fetch_quiz.php?category_id=";
    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private List<QuizQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q_display_question);

        recyclerView = findViewById(R.id.answers_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionList = new ArrayList<>();
        quizAdapter = new QuizAdapter(questionList);
        recyclerView.setAdapter(quizAdapter);

        int categoryId = 1; // Replace with your desired category ID
        new FetchQuestionsTask().execute(categoryId);
    }

    private class FetchQuestionsTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
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
                return result.toString();

            } catch (Exception e) {
                Log.e(TAG, "Error fetching questions", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            if (jsonResponse == null) {
                Toast.makeText(QuizActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONArray questionsArray = new JSONArray(jsonResponse);
                questionList.clear(); // Clear existing questions if any

                for (int i = 0; i < questionsArray.length(); i++) {
                    JSONObject questionObj = questionsArray.getJSONObject(i);
                    String questionText = questionObj.getString("question_text");

                    // Fetch answers for this question
                    JSONArray answersArray = questionObj.getJSONArray("answers");
                    List<QuizAnswer> answers = new ArrayList<>();
                    for (int j = 0; j < answersArray.length(); j++) {
                        JSONObject answerObj = answersArray.getJSONObject(j);
                        String answerText = answerObj.getString("answer_text");
                        boolean isCorrect = answerObj.getBoolean("is_correct");
                        answers.add(new QuizAnswer(answerText, isCorrect));
                    }

                    questionList.add(new QuizQuestion(questionText, answers));
                }

                quizAdapter.notifyDataSetChanged(); // Notify adapter about data changes

            } catch (Exception e) {
                Log.e(TAG, "Error parsing JSON", e);
                Toast.makeText(QuizActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
