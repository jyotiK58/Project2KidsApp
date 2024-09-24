package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Category extends AppCompatActivity {

    private static final String TAG = "CategoryImagesActivity";
    private GridLayout gridcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_page);

        gridcategory = findViewById(R.id.gridcategory);

        if (gridcategory == null) {
            Log.e(TAG, "GridLayout is null!");
        } else {
            fetchCategories();
        }
    }

    private void fetchCategories() {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetching_categories.php";
        Log.d(TAG, "Fetching categories from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response received: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject categoryObj = jsonArray.getJSONObject(i);
                                String categoryName = categoryObj.getString("type");
                                String imageUrl = categoryObj.getString("image_url");

                                addCategoryCard(categoryName, imageUrl);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            Toast.makeText(Category.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching categories", error);
                        Toast.makeText(Category.this, "Error fetching categories. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(stringRequest);
    }

    private void addCategoryCard(String categoryName, String imageUrl) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_category_card, gridcategory, false);

        ImageView imageView = cardView.findViewById(R.id.categoryImage);
        TextView textView = cardView.findViewById(R.id.categoryTitle);

        textView.setText(categoryName);
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_home)
                .into(imageView);

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(Category.this, CategoryImages.class);
            intent.putExtra("CATEGORY", categoryName);
            startActivity(intent);
        });

        gridcategory.addView(cardView);
    }
}
