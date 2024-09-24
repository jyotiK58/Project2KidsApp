package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    private ImageView fullImageView;  // Initialize the full image view
    private ImageView backArrow;
    private ImageView forwardArrow;

    private String[] imageUrls; // Array of image URLs
    private int currentIndex; // Index of the currently displayed image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity);

        fullImageView = findViewById(R.id.fullImageView); // Find the full image view
        backArrow = findViewById(R.id.backArrow);
        forwardArrow = findViewById(R.id.forwardArrow);

        // Get the image URLs and current index from the intent
        Intent intent = getIntent();
        imageUrls = intent.getStringArrayExtra("IMAGE_URLS");
        currentIndex = intent.getIntExtra("CURRENT_INDEX", 0);

        // Check if imageUrls is null to avoid NullPointerException
        if (imageUrls != null && imageUrls.length > 0) {
            loadImage(currentIndex);
        } else {
            finish(); // Or you could show a Toast message
            return;
        }

        // Set click listener for back arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    loadImage(currentIndex);
                }
            }
        });

        // Set click listener for forward arrow
        forwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < imageUrls.length - 1) {
                    currentIndex++;
                    loadImage(currentIndex);
                }
            }
        });
    }

    private void loadImage(int index) {
        String imageUrl = imageUrls[index];
        Picasso.get().load(imageUrl).into(fullImageView); // Load the image into the full image view
    }
}
