package com.learningapp;

import static com.learningapp.LoginActivity.KEY_USER_ID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryImages extends AppCompatActivity {

    private static final String TAG = "CategoryImagesActivity";
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<String> imageUrls = new ArrayList<>();
    private String category;
    private ImageView ic_back;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_images);

        // Fetch the category sent from the previous activity
        category = getIntent().getStringExtra("CATEGORY");
        Log.d(TAG, "Category received: " + category);

        if (category == null) {
            Log.e(TAG, "No category received");
            Toast.makeText(this, "Invalid category", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        ic_back = findViewById(R.id.ic_back);

        // Set up a grid layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Initialize the adapter with image URLs and set it to the RecyclerView
        adapter = new ImageAdapter(imageUrls, category);
        recyclerView.setAdapter(adapter);

        // Fetch images from the server based on the category
        fetchImages(category);

        // Handle back button click
        ic_back.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryImages.this, Category.class);
            startActivity(intent);
        });

        // Track the start time when the activity is created
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Track the end time when the user leaves the activity
        long endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;

        // Send the time spent to the server
        sendTimeToServer(timeSpent);
    }

    private void sendTimeToServer(long timeSpent) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/track_time.php";

        // Fetch user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE); // Match with the key used in LoginActivity
        String userId = sharedPreferences.getString(KEY_USER_ID, null); // Updated to use the correct key

        if (userId == null) {
            Log.e(TAG, "User ID is missing");
            Toast.makeText(this, "User ID is missing. Please log in again.", Toast.LENGTH_SHORT).show(); // Provide feedback
            return; // Exit if user ID is not available
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Log.d(TAG, "Time tracked successfully: " + response),
                error -> {
                    Log.e(TAG, "Error tracking time", error);
                    Toast.makeText(CategoryImages.this, "Error tracking time", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category", category);  // Send the category
                params.put("time_spent", String.valueOf(timeSpent));  // Send time spent in milliseconds
                params.put("user_id", userId);  // Send the user ID from SharedPreferences
                Log.d(TAG, "Parameters: " + params.toString()); // Log parameters for debugging
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }



    private void fetchImages(String category) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetch_images.php?category=" + category;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            imageUrls.add(jsonArray.getString(i));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CategoryImages.this, "Error parsing images", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(CategoryImages.this, "Error fetching images", Toast.LENGTH_LONG).show());

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private List<String> urls;
        private String category;

        ImageAdapter(List<String> urls, String category) {
            this.urls = urls;
            this.category = category;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String url = urls.get(position);
            Log.d(TAG, "Loading image from URL: " + url);
            Picasso.get().load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_home)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image loaded successfully: " + url);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Error loading image: " + url, e);
                        }
                    });

            // Handle image click to open a full-screen image activity
            holder.imageView.setOnClickListener(v -> {
                Log.d(TAG, "Image clicked: " + position);
                Intent intent = new Intent(holder.imageView.getContext(), FullImageActivity.class);
                intent.putExtra("IMAGE_URLS", imageUrls.toArray(new String[0]));
                intent.putExtra("CURRENT_INDEX", position);
                intent.putExtra("CATEGORY", category);
                holder.imageView.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
}
