package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Category extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_page);

        CardView cardAlphabets = findViewById(R.id.cardAlphabets);
        CardView cardAnimals = findViewById(R.id.cardAnimals);
        CardView cardStories = findViewById(R.id.cardStories);
        CardView cardColoring = findViewById(R.id.cardColor);
        CardView cardNumbers = findViewById(R.id.cardNumbers);

        cardAlphabets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("alphabets");
            }
        });

        cardAnimals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("animals");
            }
        });

        cardStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("stories");
            }
        });

        cardColoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("coloring");
            }
        });

        cardNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory("numbers");
            }
        });
    }

    private void openCategory(String category) {
        Intent intent = new Intent(Category.this, CategoryImages.class);
        intent.putExtra("CATEGORY", category);
        startActivity(intent);
    }
}
