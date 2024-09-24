package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText,
            usernameEditText, phoneNumberEditText, addressEditText,
            passwordEditText, confirmPasswordEditText, userIdEditText;

    private Button registerButton;
    private boolean isUpdate = false;

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String REGISTER_URL = "http://10.0.2.2/PhpForKidsLearninApp/register.php";
    private static final String UPDATE_URL = "http://10.0.2.2/PhpForKidsLearninApp/update.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // Initialize EditText fields and Button
        firstNameEditText = findViewById(R.id.firstname);
        lastNameEditText = findViewById(R.id.lastname);
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        phoneNumberEditText = findViewById(R.id.phonenumber);
        addressEditText = findViewById(R.id.address);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        userIdEditText = findViewById(R.id.userid); // Initialize userIdEditText

        registerButton = findViewById(R.id.btnregister);

        // Check if in update mode
        isUpdate = getIntent().getBooleanExtra("isUpdate", false);
        if (isUpdate) {
            registerButton.setText("Update");
            String userId = getIntent().getStringExtra("user_id"); // Get user_id from intent
            userIdEditText.setText(userId); // Pre-fill user_id field
            loadUserData(userId); // Load user data to pre-fill the fields
        } else {
            registerButton.setText("Register");
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void loadUserData(String userId) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/get_user.php?user_id=" + userId; // Your endpoint to fetch user data

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONObject userData = jsonResponse.getJSONObject("data");
                                firstNameEditText.setText(userData.getString("firstname"));
                                lastNameEditText.setText(userData.getString("lastname"));
                                emailEditText.setText(userData.getString("email"));
                                usernameEditText.setText(userData.getString("username"));
                                phoneNumberEditText.setText(userData.getString("phone_number"));
                                addressEditText.setText(userData.getString("address"));
                                // You might not want to pre-fill the password fields for security reasons
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void registerUser() {
        final String userId = userIdEditText.getText().toString().trim(); // Get user_id
        final String firstname = firstNameEditText.getText().toString().trim();
        final String lastname = lastNameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String username = usernameEditText.getText().toString().trim();
        final String phoneNumber = phoneNumberEditText.getText().toString().trim();
        final String address = addressEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input fields
        if (!isUpdate) { // Only validate for registration
            if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
                    username.isEmpty() || phoneNumber.isEmpty() || address.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Determine the URL (register or update)
        String url = isUpdate ? UPDATE_URL : REGISTER_URL;

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            // Handle response without JSON
                            if (response.equals("Connected")) {
                                Toast.makeText(RegisterActivity.this, isUpdate ? "Update successful" : "Registration successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                        }
                        // Navigate back to ManageSetting or the previous activity
                        Intent intent = new Intent(RegisterActivity.this, ManageSetting.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(RegisterActivity.this, isUpdate ? "Update failed!" : "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (isUpdate) {
                    params.put("user_id", userId); // Include user_id in the update request
                }
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("email", email);
                params.put("username", username);
                params.put("phonenumber", phoneNumber);
                params.put("address", address);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }
}
