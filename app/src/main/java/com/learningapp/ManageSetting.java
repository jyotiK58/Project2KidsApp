package com.learningapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ManageSetting extends AppCompatActivity {

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_setting); // Ensure this matches your XML layout filename
        String userId = getIntent().getStringExtra("user_id"); // Get user_id from intent
        Log.d(TAG, "User ID: " + userId); // Add this line

        // Initialize the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Find the views by ID
        ImageView backButton = findViewById(R.id.ic_back);
        LinearLayout logoutOption = findViewById(R.id.logout_option);
        LinearLayout updateAccountOption = findViewById(R.id.account_option); // Updated to use the correct ID from XML

        // Set click listener for the back button
        backButton.setOnClickListener(v -> finish()); // Close the current activity

        // Set click listener for the logout option
        logoutOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout functionality
                logoutUser();
            }
        });

        // Set click listener for the update account option
        updateAccountOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the current user's ID from SharedPreferences or your data source
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1); // Default to -1 if not found

                // Navigate to RegisterActivity for updating account
                Intent intent = new Intent(ManageSetting.this, RegisterActivity.class);
                intent.putExtra("isUpdate", true); // Pass true to indicate update mode
                intent.putExtra("user_id", userId); // Pass the user ID
                startActivity(intent);
            }
        });
    }

    private void logoutUser() {
        String logoutUrl = "http://10.0.2.2/PhpForKidsLearninApp/logout.php"; // Replace with your actual server URL

        // Clear SharedPreferences (remove saved username and password)
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Make a JSON Object Request to log out from the server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                logoutUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                Toast.makeText(ManageSetting.this, "Logout successful", Toast.LENGTH_SHORT).show();

                                // Redirect to login page after successful logout
                                Intent intent = new Intent(ManageSetting.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ManageSetting.this, "Logout failed: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ManageSetting.this, "Logout failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ManageSetting.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the Volley request queue
        requestQueue.add(jsonObjectRequest);
    }
}
