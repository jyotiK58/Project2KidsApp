package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class HomePage extends AppCompatActivity {

    private TextView welcomeText;
    private SharedPreferences sharedPreferences;
    private static final String USER_PREFS = "LoginPrefs";
    private static final String USER_ID_KEY = "user_id";
    private static final String TAG = "HomePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        welcomeText = findViewById(R.id.welcome_text);

        // Fetch the user first name from the server and update the welcome message
        fetchUserName();

        // Initialize category items
        initializeCategoryItems();

        // Start activity for learning card button
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Category.class);
                startActivity(intent);
            }
        });
    }

    private void initializeCategoryItems() {
        CardView categoryItemStudy = findViewById(R.id.category_item_study);
        CardView categoryItemQuiz = findViewById(R.id.category_item_quiz);
        CardView categoryItemLullaby = findViewById(R.id.category_item_lullaby);
        CardView categoryItemVideo = findViewById(R.id.category_item_video);

        ImageView manageSetting = findViewById(R.id.manage_setting);
        manageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ManageSetting.class);
                startActivity(intent);
            }
        });

        categoryItemStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Category.class);
                startActivity(intent);
            }
        });

        categoryItemQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, QuizActivity.class);
                startActivity(intent);
            }
        });

        categoryItemLullaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, LullabyActivity.class);
                startActivity(intent);
            }
        });

        categoryItemVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, VideoLearning.class);
                startActivity(intent);
            }
        });
    }

    private void fetchUserName() {
        sharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);

        if (userId != null) {
            String url = "http://10.0.2.2/PhpForKidsLearninApp/get_user.php?user_id=" + userId;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Get user data from the response
                                if (response.has("data")) {
                                    JSONObject userData = response.getJSONObject("data");
                                    String firstName = userData.getString("firstname");
                                    updateGreeting(firstName);
                                } else {
                                    Log.e(TAG, "User data not found in response");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Error parsing user data", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error fetching user data", error);
                        }
                    });

            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } else {
            Log.e(TAG, "User ID is not available in SharedPreferences");
        }
    }

    private void updateGreeting(String firstName) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hourOfDay >= 0 && hourOfDay < 12) {
            greeting = "Good Morning, " + firstName;
        } else if (hourOfDay >= 12 && hourOfDay < 17) {
            greeting = "Good Afternoon, " + firstName;
        } else {
            greeting = "Good Evening, " + firstName;
        }

        welcomeText.setText(greeting);
    }
}