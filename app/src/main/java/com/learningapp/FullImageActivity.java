package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import android.util.Log;

public class FullImageActivity extends AppCompatActivity {

    private static final String TAG = "FullImageActivity";
    private ImageView fullImageView;
    private ImageView backArrow, ic_back;
    private ImageView forwardArrow;
    private Button startQuizButton;

    private String[] imageUrls;
    private int currentIndex;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity);

        fullImageView = findViewById(R.id.fullImageView);
        backArrow = findViewById(R.id.backArrow);
        forwardArrow = findViewById(R.id.forwardArrow);
        ic_back = findViewById(R.id.ic_back);
        startQuizButton = findViewById(R.id.start_quiz_button);


        startQuizButton.setVisibility(View.GONE);

        Intent intent = getIntent();
        imageUrls = intent.getStringArrayExtra("IMAGE_URLS");
        currentIndex = intent.getIntExtra("CURRENT_INDEX", 0);
        category = intent.getStringExtra("CATEGORY");

        if (imageUrls != null && imageUrls.length > 0) {
            loadImage(currentIndex);
        } else {
            Log.e(TAG, "Image URLs are null or empty");
            finish();
            return;
        }

        backArrow.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                loadImage(currentIndex);
            }
        });

        forwardArrow.setOnClickListener(v -> {
            if (currentIndex < imageUrls.length - 1) {
                currentIndex++;
                loadImage(currentIndex);
            } else {
                // If it's the last image, show the button
                startQuizButton.setVisibility(View.VISIBLE);
            }
        });

        ic_back.setOnClickListener(v -> {
            Intent in = new Intent(FullImageActivity.this, Category.class);
            startActivity(in);
        });

        startQuizButton.setOnClickListener(v -> {
            Intent i = new Intent(FullImageActivity.this, QuizActivity.class);
            // Use the retrieved category here
            i.putExtra("CATEGORY", category);
            startActivity(i);
        });
    }

    private void loadImage(int index) {
        String imageUrl = imageUrls[index];
        Log.d(TAG, "Loading image: " + imageUrl);
        Picasso.get().load(imageUrl).into(fullImageView);

        // Hide the startQuizButton initially
        startQuizButton.setVisibility(View.GONE);

        // Check if the current image is the last one
        if (index == imageUrls.length - 1) {
            // If it's the last image, hide forwardArrow and show the startQuizButton
            forwardArrow.setVisibility(View.GONE);
            startQuizButton.setVisibility(View.VISIBLE);
        } else {
            // If it's not the last image, ensure forwardArrow is visible
            forwardArrow.setVisibility(View.VISIBLE);
        }

        // Hide the back arrow if it's the first image
        if (index == 0) {
            backArrow.setVisibility(View.GONE);
        } else {
            backArrow.setVisibility(View.VISIBLE);
        }
    }

}
