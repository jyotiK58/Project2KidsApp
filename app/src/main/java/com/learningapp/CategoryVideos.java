package com.learningapp;



import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CategoryVideos extends AppCompatActivity {

    private static final String TAG = "CategoryVideos";
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<String> videoUrls = new ArrayList<>();
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_videos);

        // Retrieve category from the intent
        category = getIntent().getStringExtra("CATEGORY");
        Log.d(TAG, "Category received: " + category);

        if (category == null) {
            Log.e(TAG, "No category received");
            Toast.makeText(this, "Invalid category", Toast.LENGTH_LONG).show();
            finish(); // Close the activity
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new VideoAdapter(videoUrls);
        recyclerView.setAdapter(adapter);

        // Fetch videos for the selected category
        fetchVideos(category);
    }

    private void fetchVideos(String category) {
        String url = "http://10.0.2.2/KidsApp/fetch_videos.php?category=" + category;
        Log.d(TAG, "Fetching videos from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response received: " + response);
                        try {
                            // Parse the response as a JSONArray
                            JSONArray jsonArray = new JSONArray(response);
                            List<String> urls = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                urls.add(jsonArray.getString(i));
                            }
                            Log.d(TAG, "Parsed URLs: " + urls);
                            videoUrls.clear();
                            videoUrls.addAll(urls);
                            adapter.notifyDataSetChanged();

                            if (videoUrls.isEmpty()) {
                                Log.d(TAG, "No videos found for category: " + category);
                                Toast.makeText(CategoryVideos.this, "No videos found for this category", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            Toast.makeText(CategoryVideos.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching videos", error);
                        Toast.makeText(CategoryVideos.this, "Error fetching videos. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(stringRequest);
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
        private List<String> urls;

        VideoAdapter(List<String> urls) {
            this.urls = urls;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String url = urls.get(position);
            Log.d(TAG, "Preparing to play video from URL: " + url);

            // Prepare ExoPlayer to play video
            ExoPlayer player = new ExoPlayer.Builder(CategoryVideos.this).build();
            holder.playerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(url);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            PlayerView playerView;

            ViewHolder(View itemView) {
                super(itemView);
                playerView = itemView.findViewById(R.id.playerView);
            }
        }
    }
}
