package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoLearning extends AppCompatActivity {

    private static final String TAG = "VideoLearning";
    private ListView videoListView;  // Now we have a reference to the ListView
    private List<Video> videoList;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_learning);

        // Bind the ListView
        videoListView = findViewById(R.id.video_list_view);
        videoList = new ArrayList<>();

        // Fetch video categories
        fetchVideoCategories();

        // Set click listener for list items
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video selectedVideo = videoList.get(position);
                String videoUrl = selectedVideo.getVideoUrl();
                Log.d(TAG, "Selected video URL: " + videoUrl);

                // Redirect to VideoPlayer activity with the selected video URL
                Intent intent = new Intent(VideoLearning.this, VideoPlayer.class);
                intent.putExtra("VIDEO_URL", videoUrl);
                startActivity(intent);
            }
        });
    }

    private void fetchVideoCategories() {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetch_videos.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        videoList.clear();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject videoObject = response.getJSONObject(i);
                                String id = videoObject.getString("id");
                                String title = videoObject.getString("title");
                                String videoUrl = videoObject.getString("video_url");
                                String imageUrl = videoObject.getString("image_url");

                                // Create Video object
                                Video video = new Video(id, title, videoUrl, imageUrl);
                                videoList.add(video);
                            }

                            // Set up the custom adapter for the list view
                            videoAdapter = new VideoAdapter(VideoLearning.this, videoList);
                            videoListView.setAdapter(videoAdapter);

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON data", e);
                            Toast.makeText(VideoLearning.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data", error);
                        Toast.makeText(VideoLearning.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }
}
