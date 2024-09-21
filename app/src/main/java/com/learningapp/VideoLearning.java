package com.learningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class VideoLearning extends AppCompatActivity {

    private static final String TAG = "VideoLearning";
    private GridView videoGridView;  // Change ListView to GridView
    private List<GetterSetterVideo> videoList;
    private VideoAdapter videoAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_learning);

        // Bind the GridView
        videoGridView = findViewById(R.id.video_grid_view);
        videoList = new ArrayList<>();

        // Fetch video categories
        fetchVideoCategories();

        // Set click listener for grid items
        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetterSetterVideo selectedVideo = videoList.get(position);
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

        // Use JsonObjectRequest instead of JsonArrayRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        videoList.clear();

                        try {
                            // Get the array of videos from the JSONObject
                            JSONArray videosArray = response.getJSONArray("videos");

                            for (int i = 0; i < videosArray.length(); i++) {
                                JSONObject videoObject = videosArray.getJSONObject(i);
                                String id = videoObject.getString("id");
                                String title = videoObject.getString("title");
                                String videoUrl = videoObject.getString("video_url");
                                String imageUrl = videoObject.optString("image_url", null);  // Handle if image_url is missing

                                // Create Video object and add to the list
                                GetterSetterVideo video = new GetterSetterVideo(id, title, videoUrl, imageUrl);
                                videoList.add(video);
                            }

                            // Set up the custom adapter for the GridView
                            videoAdapter = new VideoAdapter(VideoLearning.this, videoList);
                            videoGridView.setAdapter(videoAdapter);

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

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
