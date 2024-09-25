package com.learningapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
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
    private GridView videoGridView;
    private List<GetterSetterVideo> videoList;
    private List<GetterSetterVideo> filteredVideoList; // For holding filtered videos
    private VideoAdapter videoAdapter;
    private WebView videoWebView;
    private EditText searchBar; // For searching videos

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_video);

        videoGridView = findViewById(R.id.video_grid_view);
        videoWebView = findViewById(R.id.video_web_view);
        searchBar = findViewById(R.id.search_input); // Initialize the search bar
        videoList = new ArrayList<>();
        filteredVideoList = new ArrayList<>(); // Initialize filtered list

        // Start fetching video categories
        Log.d(TAG, "Fetching video categories...");
        fetchVideoCategories();

        // Set up the search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVideos(s.toString()); // Call filter method
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetterSetterVideo selectedVideo = filteredVideoList.get(position);
                String videoUrl = selectedVideo.getVideoUrl();

                // Log which video was selected
                Log.d(TAG, "Selected video: " + selectedVideo.getTitle() + ", URL: " + videoUrl);

                // Load the video URL in the WebView
                loadVideoInWebView(videoUrl);
            }
        });
    }

    // Fetch video categories from server
    private void fetchVideoCategories() {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetch_videos.php";

        // Log the request URL
        Log.d(TAG, "Requesting video data from: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        videoList.clear();

                        try {
                            JSONArray videosArray = response.getJSONArray("videos");

                            for (int i = 0; i < videosArray.length(); i++) {
                                JSONObject videoObject = videosArray.getJSONObject(i);
                                String id = videoObject.getString("id");
                                String title = videoObject.getString("title");
                                String videoUrl = videoObject.getString("video_url");
                                String imageUrl = videoObject.optString("image_url", "");

                                // Log each video that is being processed
                                Log.d(TAG, "Processing video: " + title + ", URL: " + videoUrl + ", Image URL: " + imageUrl);

                                GetterSetterVideo video = new GetterSetterVideo(id, title, videoUrl, imageUrl);
                                videoList.add(video);
                            }

                            // Set the adapter after data is loaded
                            filteredVideoList.addAll(videoList); // Copy all videos to filtered list
                            videoAdapter = new VideoAdapter(VideoLearning.this, filteredVideoList);
                            videoGridView.setAdapter(videoAdapter);

                            // Log success message
                            Log.d(TAG, "Video categories fetched successfully. Total videos: " + videoList.size());

                        } catch (JSONException e) {
                            // Log JSON parsing errors
                            Log.e(TAG, "Error parsing JSON data: " + e.getMessage(), e);
                            Toast.makeText(VideoLearning.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log network errors
                        Log.e(TAG, "Error fetching video data: " + error.toString(), error);
                        Toast.makeText(VideoLearning.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add request to the queue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    // Filter videos based on search query
    private void filterVideos(String query) {
        filteredVideoList.clear();

        if (query.isEmpty()) {
            filteredVideoList.addAll(videoList); // Show all videos if query is empty
        } else {
            for (GetterSetterVideo video : videoList) {
                if (video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredVideoList.add(video); // Add matching video to the filtered list
                }
            }
        }

        // Notify the adapter of the change
        videoAdapter.notifyDataSetChanged(); // Refresh the GridView
    }

    // Load the selected video into the WebView
    private void loadVideoInWebView(String videoUrl) {
        // Log that WebView loading has started
        Log.d(TAG, "Loading video in WebView. URL: " + videoUrl);

        // Make the WebView visible
        videoWebView.setVisibility(View.VISIBLE);

        // Enable JavaScript for better video support
        WebSettings webSettings = videoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Convert the YouTube URL to the embed format if necessary
        if (videoUrl.contains("youtu.be")) {
            // Extract the video ID from youtu.be URLs
            String videoId = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
            videoUrl = "https://www.youtube.com/embed/" + videoId;
            Log.d(TAG, "Converted short URL to embed format: " + videoUrl);
        } else if (videoUrl.contains("watch?v=")) {
            // Extract the video ID from standard YouTube URLs
            String videoId = videoUrl.substring(videoUrl.indexOf("watch?v=") + 8);
            videoUrl = "https://www.youtube.com/embed/" + videoId;
            Log.d(TAG, "Converted standard URL to embed format: " + videoUrl);
        }

        // Load the YouTube video in an iframe
        String iframeHtml = "<html><body><iframe width=\"100%\" height=\"100%\" src=\""
                + videoUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        // Ensure links open in the WebView instead of an external browser
        videoWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Log any errors in loading the WebView content
                Log.e(TAG, "Error loading WebView content: " + description + " (code: " + errorCode + ")");
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        videoWebView.loadData(iframeHtml, "text/html", "utf-8");

        // Log completion of WebView load initiation
        Log.d(TAG, "WebView content loaded with video URL.");
    }
}
