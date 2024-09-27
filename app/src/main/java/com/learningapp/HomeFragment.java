package com.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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

public class HomeFragment extends Fragment {

    private EditText searchInput;
    private ListView searchResults;
    private TextView welcomeText;
    private List<String> categoriesList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private static final String USER_PREFS = "LoginPrefs";
    private static final String USER_ID_KEY = "user_id";
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage_fragment, container, false);

        // Initialize views
        searchInput = view.findViewById(R.id.search_input);
        searchResults = view.findViewById(R.id.search_results);
        welcomeText = view.findViewById(R.id.welcome_text);

        // Initialize list and adapter for search results
        categoriesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, categoriesList);
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
        initializeCategoryItems(view);

        return view;
    }

    // Function to handle category item clicks
    private void initializeCategoryItems(View view) {
        CardView categoryItemStudy = view.findViewById(R.id.category_item_study);
        CardView categoryItemQuiz = view.findViewById(R.id.category_item_quiz);
        CardView categoryItemLullaby = view.findViewById(R.id.category_item_lullaby);
        CardView categoryItemVideo = view.findViewById(R.id.category_item_video);


        categoryItemStudy.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Category.class);
            startActivity(intent);
        });

        categoryItemQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            startActivity(intent);
        });

        categoryItemLullaby.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TicTacToeActivity.class);
            startActivity(intent);
        });

        categoryItemVideo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VideoLearning.class);
            startActivity(intent);
        });
    }

    // Fetch user name from server and set greeting based on time of day
    private void fetchUserName() {
        sharedPreferences = getActivity().getSharedPreferences(USER_PREFS, getContext().MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);

        if (userId != null) {
            String url = "http://10.0.2.2/PhpForKidsLearninApp/get_user.php?user_id=" + userId;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
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
                    },
                    error -> Log.e(TAG, "Error fetching user data", error));

            Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
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
                response -> {
                    categoriesList.clear();
                    try {
                        // Check if "message" exists in the response
                        if (response.has("message")) {
                            // No categories found
                            Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Log.e("HomeFragment", "Error parsing JSON response", e);
                    }
                },
                error -> Log.e("HomeFragment", "Error fetching search results", error));

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }
}