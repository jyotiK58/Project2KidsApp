package com.learningapp;

import android.content.Intent;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_images);

        category = getIntent().getStringExtra("CATEGORY");
        Log.d(TAG, "Category received: " + category);

        if (category == null) {
            Log.e(TAG, "No category received");
            Toast.makeText(this, "Invalid category", Toast.LENGTH_LONG).show();
            finish(); // Close the activity
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        ic_back = findViewById(R.id.ic_back);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(adapter);



        fetchImages(category);


        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryImages.this, Category.class);
                startActivity(intent);
            }
        });
    }





    private void fetchImages(String category) {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetch_images.php/?category=" + category;
        Log.d(TAG, "Fetching images from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response received: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<String> urls = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                urls.add(jsonArray.getString(i));
                            }
                            Log.d(TAG, "Parsed URLs: " + urls);
                            imageUrls.clear();
                            imageUrls.addAll(urls);
                            adapter.notifyDataSetChanged();

                            if (imageUrls.isEmpty()) {
                                Log.d(TAG, "No images found for category: " + category);
                                Toast.makeText(CategoryImages.this, "No images found for this category", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            Toast.makeText(CategoryImages.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching images", error);
                        Toast.makeText(CategoryImages.this, "Error fetching images. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(stringRequest);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private List<String> urls;

        ImageAdapter(List<String> urls) {
            this.urls = urls;
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

            // Set up the click listener to open FullImageActivity
            holder.imageView.setOnClickListener(v -> {
                Log.d(TAG, "Image clicked: " + position);
                Intent intent = new Intent(holder.imageView.getContext(), FullImageActivity.class);
                intent.putExtra("IMAGE_URLS", imageUrls.toArray(new String[0]));
                intent.putExtra("CURRENT_INDEX", position);
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
