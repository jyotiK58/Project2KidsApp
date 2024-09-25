package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private EditText searchInput;
    private ListView searchResults;
    private TextView welcomeText;
    private List<String> categoriesList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private static final String USER_PREFS = "LoginPrefs";
    private static final String USER_ID_KEY = "user_id";
    private static final String TAG = "HomePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // Initialize views
        searchInput = findViewById(R.id.search_input);
        searchResults = findViewById(R.id.search_results);
        welcomeText = findViewById(R.id.welcome_text);

        // Initialize list and adapter for search results
        categoriesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriesList);
        searchResults.setAdapter(adapter);

        // Add listener to the search input for real-time search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence query, int i, int i1, int i2) {
                if (query.length() > 0) {
                    fetchSearchResults(query.toString());
                } else {
                    categoriesList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Fetch user name and set greeting
        fetchUserName();

        // Initialize the category items (buttons, cards, etc.)
        initializeCategoryItems();
    }

    // Function to handle category item clicks
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

    // Fetch user name from server and set greeting based on time of day
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

    // Update greeting based on the time of day
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

    // Fetch search results based on the query
    private void fetchSearchResults(String query) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/search_category.php?query=" + query;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        categoriesList.clear();
                        try {
                            // Check if "message" exists in the response
                            if (response.has("message")) {
                                // No categories found
                                Toast.makeText(HomePage.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                searchResults.setVisibility(View.GONE);
                            } else {
                                // Parse the categories array
                                JSONArray categoriesArray = response.getJSONArray("categories");
                                searchResults.setVisibility(View.VISIBLE);
                                for (int i = 0; i < categoriesArray.length(); i++) {
                                    JSONObject category = categoriesArray.getJSONObject(i);
                                    String type = category.getString("type");
                                    categoriesList.add(type);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("HomePage", "Error parsing JSON response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HomePage", "Error fetching search results", error);
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
