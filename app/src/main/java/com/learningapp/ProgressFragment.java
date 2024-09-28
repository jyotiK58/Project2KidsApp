package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressFragment extends Fragment {

    private ProgressBar performanceProgressBar;
    private TextView progressPercentageTextView;
    private TextView clusterTextView;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_quiz, container, false);

        // Initialize UI components
        performanceProgressBar = view.findViewById(R.id.performanceProgressBar);
        progressPercentageTextView = view.findViewById(R.id.progressPercentageTextView);
        clusterTextView = view.findViewById(R.id.clusterTextView);

        // Fetch user ID from shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = Integer.parseInt(preferences.getString(LoginActivity.KEY_USER_ID, "-1")); // Retrieve as String and parse


        // Log the retrieved user ID
        Log.d("ProgressFragment", "Retrieved User ID: " + userId);

        if (userId == -1) {
            // User ID not found, redirect to login activity
            Toast.makeText(getContext(), "Please log in to view your progress", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        fetchUserData();
        return view;
    }

    private void fetchUserData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "http://10.0.2.2/PhpForKidsLearninApp/performance_quiz.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<UserData> userDataList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int userId = jsonObject.getInt("user_id");
                        int correctCount = jsonObject.getInt("correct_answers_count");
                        int wrongCount = jsonObject.getInt("wrong_answers_count");
                        userDataList.add(new UserData(userId, correctCount, wrongCount));
                    }
                    applyKMeansClustering(userDataList);
                } catch (Exception e) {
                    Log.e("ProgressFragment", "Parsing error: " + e.getMessage(), e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ProgressFragment", "Volley error: " + error.getMessage(), error);
            }
        });

        requestQueue.add(stringRequest);
    }

    private void applyKMeansClustering(List<UserData> userDataList) {
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
