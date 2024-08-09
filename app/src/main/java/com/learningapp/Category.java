
package com.learningapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Category extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_page);

        // Get references to each card view
        CardView cardAlphabets = findViewById(R.id.cardAlphabets);
        CardView cardAnimals = findViewById(R.id.cardAnimals);
        CardView cardStories = findViewById(R.id.cardStories);
        CardView cardColoring = findViewById(R.id.cardColor);
        CardView cardNumbers = findViewById(R.id.cardNumbers);


        // Set click listeners for each card view
        cardAlphabets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new activity for Alphabets
                Intent intent = new Intent(Category.this, AlphabetsActivity.class);
                startActivity(intent);
            }
        });

        cardAnimals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new activity for Animals
                Intent intent = new Intent(Category.this, AnimalsActivity.class);
                startActivity(intent);
            }
        });



        cardStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new activity for Stories
                Intent intent = new Intent(Category.this, Shapes.class);
                startActivity(intent);
            }
        });

        cardColoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new activity for Coloring
                Intent intent = new Intent(Category.this, Vegetables.class);
                startActivity(intent);
            }
        });

        cardNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new activity for Numbers
                Intent intent = new Intent(Category.this, NumbersActivity.class);
                startActivity(intent);
            }
        });
    }
//     fruits.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // Start a new activity for Numbers
//            Intent intent = new Intent(Category.this, FruitsActivity    .class);
//            startActivity(intent);
//        }
//    });
}


