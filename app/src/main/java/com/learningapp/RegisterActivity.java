package com.learningapp;

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
            passwordEditText, confirmPasswordEditText;

    private Button registerButton;

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String REGISTER_URL = "http://10.0.2.2/PhpForKidsLearninApp/register.php";

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

        registerButton = findViewById(R.id.btnregister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String firstname = firstNameEditText.getText().toString().trim();
        final String lastname = lastNameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String username = usernameEditText.getText().toString().trim();
        final String phoneNumber = phoneNumberEditText.getText().toString().trim();
        final String address = addressEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input fields
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

        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Try to parse response as JSON
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            // Handle case where response is a plain string
                            if (response.equals("Connected")) {
                                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, " " + response, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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
