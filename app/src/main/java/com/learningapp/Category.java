package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;

public class Category extends AppCompatActivity {

    private static final String TAG = "CategoryImagesActivity";
    private GridLayout gridcategory;
    private ImageView ic_back;
    private EditText searchInput;

    private List<CategoryItem> categoryList = new ArrayList<>(); // Store all categories for filtering
    private List<CategoryItem> filteredList = new ArrayList<>(); // Store filtered categories

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_page);

        gridcategory = findViewById(R.id.gridcategory);
        ic_back = findViewById(R.id.ic_back);
        searchInput = findViewById(R.id.search_input); // Assuming you added this in your layout

        if (gridcategory == null) {
            Log.e(TAG, "GridLayout is null!");
        } else {
            fetchCategories();
        }

        ic_back.setOnClickListener(v -> {
            Intent intent = new Intent(Category.this, HomePage.class);
            startActivity(intent);
        });


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchCategories() {
        String url = "http://10.0.2.2/PhpForKidsLearninApp/fetching_categories.php";
        Log.d(TAG, "Fetching categories from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Response received: " + response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject categoryObj = jsonArray.getJSONObject(i);
                            String categoryName = categoryObj.getString("type");
                            String imageUrl = categoryObj.getString("image_url");


                            CategoryItem categoryItem = new CategoryItem(categoryName, imageUrl);
                            categoryList.add(categoryItem);
                        }

                        showCategories(categoryList);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(Category.this, "Error parsing response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching categories", error);
                    Toast.makeText(Category.this, "Error fetching categories. Please try again later.", Toast.LENGTH_LONG).show();
                });

        queue.add(stringRequest);
    }

    private void showCategories(List<CategoryItem> categories) {
        gridcategory.removeAllViews();
        for (CategoryItem category : categories) {
            addCategoryCard(category.getName(), category.getImageUrl());
        }
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

    private void filterCategories(String query) {
        filteredList.clear();

        for (CategoryItem category : categoryList) {
            if (category.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(category);
            }
        }

        showCategories(filteredList);
    }


    private static class CategoryItem {
        private final String name;
        private final String imageUrl;

        public CategoryItem(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
