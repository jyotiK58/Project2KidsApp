package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView register;
    private CheckBox rememberMeCheckbox;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    public static final String KEY_USER_ID = "user_id"; // Change to public
 // New key for user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        register = findViewById(R.id.register);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadSavedCredentials();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadSavedCredentials() {
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            usernameInput.setText(savedUsername);
            passwordInput.setText(savedPassword);
            rememberMeCheckbox.setChecked(true);
        }else{

        }
    }

    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Replace with your server URL
        String url = "http://10.0.2.2/PhpForKidsLearninApp/login.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                String userId = jsonResponse.getString("user_id");
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_USER_ID, userId); // Save user ID

                                if (rememberMeCheckbox.isChecked()) {
                                    editor.putString(KEY_USERNAME, username);
                                    editor.putString(KEY_PASSWORD, password);
                                    editor.putBoolean(KEY_REMEMBER_ME, true);
                                } else {
                                    editor.remove(KEY_USERNAME);
                                    editor.remove(KEY_PASSWORD);
                                    editor.putBoolean(KEY_REMEMBER_ME, false);
                                }
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, HomePage.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


}