package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manage_setting, container, false); // Ensure this matches your XML layout filename

        // Initialize the Volley request queue
        requestQueue = Volley.newRequestQueue(requireActivity());

        // Find the views by ID
        ImageView backButton = view.findViewById(R.id.ic_back);

        LinearLayout logoutOption = view.findViewById(R.id.logout_option);
//        LinearLayout progressOption = view.findViewById(R.id.progress_option);
        LinearLayout updateAccountOption = view.findViewById(R.id.account_option); // Updated to use the correct ID from XML

        // Set click listener for the back button
        backButton.setOnClickListener(v -> getActivity().onBackPressed()); // Close the fragment or go back

        // Set click listener for the progress option
//        progressOption.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), PerformanceActivity.class);
//            startActivity(intent);
//        });

        // Set click listener for the logout option
        logoutOption.setOnClickListener(v -> logoutUser());

        // Set click listener for the update account option
        updateAccountOption.setOnClickListener(v -> {
            // Retrieve the current user's ID from SharedPreferences as a string
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE);
            String userId = sharedPreferences.getString("user_id", null); // Default to null if not found

            // Check if userId is valid
            if (userId != null) {
                // Navigate to RegisterActivity for updating account
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                intent.putExtra("isUpdate", true); // Pass true to indicate update mode
                intent.putExtra("user_id", userId); // Pass the user ID
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "User not found, please login again.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void logoutUser() {
        String logoutUrl = "http://10.0.2.2/PhpForKidsLearninApp/logout.php"; // Replace with your actual server URL

        // Clear SharedPreferences (remove saved username, password, and user_id)
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // This clears all the saved session data
        editor.apply();

        // Make a JSON Object Request to log out from the server (optional, depending on server-side session management)
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
                                Toast.makeText(getActivity(), "Logout successful", Toast.LENGTH_SHORT).show();
                                // Redirect to login page after successful logout
                                redirectToLogin();
                            } else {
                                Toast.makeText(getActivity(), "Logout failed: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Logout failed", Toast.LENGTH_SHORT).show();
                            redirectToLogin(); // Proceed with redirection even if server response failed
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        redirectToLogin();  // Proceed with redirection in case of network error
                    }
                }
        );

        // Add the request to the Volley request queue
        requestQueue.add(jsonObjectRequest);
    }

    private void redirectToLogin() {
        // Redirect to LoginActivity after clearing session
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();  // Finish current activity to prevent user from returning to it
    }
}