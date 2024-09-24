package com.learningapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Performance extends AppCompatActivity {

    private TextView performanceTextView;
    private static final String TAG = "Performance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.performance);

        performanceTextView = findViewById(R.id.performanceTextView);

        // Assuming the user ID is available
        int userId = 22; // Replace with actual logged-in user ID

        fetchPerformanceData(userId);
    }

    private void fetchPerformanceData(int userId) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/performance.php?user_id=" + userId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() == 0) {
                            // Handle the empty response
                            performanceTextView.setText("No performance data available.");
                            return;
                        }

                        StringBuilder performanceData = new StringBuilder();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject performance = response.getJSONObject(i);
                                performanceData.append("User ID: ")
                                        .append(performance.getInt("user_id"))
                                        .append(", Average Score: ")
                                        .append(performance.getDouble("avg_score"))
                                        .append(", Total Time: ")
                                        .append(performance.getInt("total_time"))
                                        .append(", Average Level: ")
                                        .append(performance.getDouble("avg_level"))
                                        .append(", Average Progress: ")
                                        .append(performance.getDouble("avg_progress"))
                                        .append("\n");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        performanceTextView.setText(performanceData.toString());
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

}
