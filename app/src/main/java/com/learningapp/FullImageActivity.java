package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import android.util.Log;

public class FullImageActivity extends AppCompatActivity {

    private static final String TAG = "FullImageActivity";
    private ImageView fullImageView;
    private ImageView backArrow , ic_back;
    private ImageView forwardArrow;

    private String[] imageUrls;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity);

        fullImageView = findViewById(R.id.fullImageView);
        backArrow = findViewById(R.id.backArrow);
        forwardArrow = findViewById(R.id.forwardArrow);
        ic_back = findViewById(R.id.ic_back);

        Intent intent = getIntent();
        imageUrls = intent.getStringArrayExtra("IMAGE_URLS");
        currentIndex = intent.getIntExtra("CURRENT_INDEX", 0);

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
            }
        });

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullImageActivity.this, Category.class);
                startActivity(intent);
            }
        });
    }

    private void loadImage(int index) {
        String imageUrl = imageUrls[index];
        Log.d(TAG, "Loading image: " + imageUrl);
        Picasso.get().load(imageUrl).into(fullImageView);
    }
}
