package com.learningapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ProgressFragment extends Fragment {

    private ProgressBar performanceProgressBar;
    private TextView progressPercentageTextView;
    private int userId;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USER_ID = "user_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_result, container, false);


        performanceProgressBar = view.findViewById(R.id.performanceProgressBar);
        progressPercentageTextView = view.findViewById(R.id.progressPercentageTextView);
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        String userIdString = preferences.getString(KEY_USER_ID, "-1");
        userId = Integer.parseInt(userIdString);


//        Log.d("ProgressFragment", "Retrieved User ID: " + userId);

        if (userId == -1) {

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


        String url = "http://10.0.2.2/PhpForKidsLearninApp/performance_quiz.php?user_id=" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int totalCorrect = 0;
                    int totalWrong = 0;

                    // Loop through each category result to sum correct and wrong counts
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        totalCorrect += jsonObject.getInt("correct_answers_count");
                        totalWrong += jsonObject.getInt("wrong_answers_count");
                    }

                    // Calculate the percentage of correct answers
                    int totalQuestions = totalCorrect + totalWrong;
                    int percentageCorrect = (totalQuestions > 0) ? (int) ((totalCorrect / (float) totalQuestions) * 100) : 0;

                    updateProgressBar(percentageCorrect);
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

    private void updateProgressBar(int percentageCorrect) {
        if (performanceProgressBar != null && progressPercentageTextView != null) {

            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(performanceProgressBar, "progress", 0, percentageCorrect);
            progressAnimator.setDuration(1000);
            progressAnimator.start();


            progressPercentageTextView.setText(percentageCorrect + "%");
        }
    }
}
