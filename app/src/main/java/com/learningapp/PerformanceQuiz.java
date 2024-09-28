package com.learningapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceQuiz extends AppCompatActivity {
    private ProgressBar performanceProgressBar;
    private TextView progressPercentageTextView;
    private TextView clusterTextView;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.performance_quiz);

        // Initialize UI components
        performanceProgressBar = findViewById(R.id.performanceProgressBar);
        progressPercentageTextView = findViewById(R.id.progressPercentageTextView);
        clusterTextView = findViewById(R.id.clusterTextView);

        // Fetch user ID from shared preferences
        userId = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", -1);
        if (userId == -1) {
            // Handle the case where user ID is not found
            return;
        }

        fetchUserData();
    }

    private void fetchUserData() {
        new FetchUserDataTask().execute("http://10.0.2.2/PhpForKidsLearninApp/performance_quiz.php");
    }

    private class FetchUserDataTask extends AsyncTask<String, Void, List<UserData>> {
        @Override
        protected List<UserData> doInBackground(String... urls) {
            List<UserData> userDataList = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int userId = jsonObject.getInt("user_id");
                        int correctCount = jsonObject.getInt("correct_answers_count");
                        int wrongCount = jsonObject.getInt("wrong_answers_count");
                        userDataList.add(new UserData(userId, correctCount, wrongCount));
                    }
                } else {
                    Log.e("PerformanceQuiz", "GET request not worked");
                }
            } catch (Exception e) {
                Log.e("PerformanceQuiz", "Exception: " + e.getMessage(), e);
            }
            return userDataList;
        }

        @Override
        protected void onPostExecute(List<UserData> userDataList) {
            applyKMeansClustering(userDataList);
        }
    }

    private void applyKMeansClustering(List<UserData> userDataList) {
        // K-means algorithm implementation (same as before)
        List<float[]> data = new ArrayList<>();
        for (UserData userData : userDataList) {
            data.add(new float[]{userData.correctCount, userData.wrongCount});
        }

        KMeans kmeans = new KMeans(3, 100);
        kmeans.fit(data);

        Map<Integer, Integer> userClusterMap = new HashMap<>();
        for (UserData userData : userDataList) {
            int cluster = kmeans.predict(new float[]{userData.correctCount, userData.wrongCount});
            userClusterMap.put(userData.userId, cluster);
        }

        updateUI(userDataList, userClusterMap);
    }

    private void updateUI(List<UserData> userDataList, Map<Integer, Integer> userClusterMap) {
        if (userClusterMap.containsKey(userId)) {
            int cluster = userClusterMap.get(userId);
            clusterTextView.setText("Cluster: " + cluster);

            // Find the user's data
            for (UserData userData : userDataList) {
                if (userData.userId == userId) {
                    // Calculate the percentage of correct answers
                    int totalQuestions = userData.correctCount + userData.wrongCount;
                    if (totalQuestions > 0) {
                        int percentageCorrect = (int) ((userData.correctCount / (float) totalQuestions) * 100);
                        updateProgressBar(percentageCorrect);
                    } else {
                        updateProgressBar(0);
                    }
                    break;
                }
            }
        }
    }

    private void updateProgressBar(int percentageCorrect) {
        if (performanceProgressBar != null && progressPercentageTextView != null) {
            performanceProgressBar.setProgress(percentageCorrect);
            progressPercentageTextView.setText(percentageCorrect + "%");
        }
    }
}
