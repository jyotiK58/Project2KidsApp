package com.learningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AlphabetsActivity extends AppCompatActivity {

    private static final String TAG = "AlphabetsActivity";
    private static final String IMAGE_URL = "http://10.0.2.2/KidsApp/fetch_alphabets.php";

    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_alphabets);

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(adapter);

        fetchImages();
    }

    private void fetchImages() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Raw response: " + response);
                        List<String> urls = extractImageUrlsFromHtml(response);
                        Log.d(TAG, "Number of image URLs extracted: " + urls.size());
                        imageUrls.clear();
                        imageUrls.addAll(urls);
                        adapter.notifyDataSetChanged();

                        if (imageUrls.isEmpty()) {
                            Toast.makeText(AlphabetsActivity.this, "No images found", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: " + error.getMessage());
                        Toast.makeText(AlphabetsActivity.this, "Error fetching images", Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(stringRequest);
    }

    private List<String> extractImageUrlsFromHtml(String htmlString) {
        List<String> urls = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(htmlString);
            Elements imgElements = doc.select("img");
            for (Element imgElement : imgElements) {
                String src = imgElement.attr("src");
                if (!src.isEmpty()) {
                    if (!src.startsWith("http")) {
                        src = "http://10.0.2.2/KidsApp/" + src;
                    }
                    urls.add(src);
                    Log.d(TAG, "Found image URL: " + src);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing HTML: " + e.getMessage());
        }
        return urls;
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private List<String> urls;

        ImageAdapter(List<String> urls) {
            this.urls = urls;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String url = urls.get(position);
            Picasso.get().load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_home)
                    .into(holder.imageView);
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