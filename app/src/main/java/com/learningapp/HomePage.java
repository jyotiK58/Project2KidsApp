package com.learningapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class HomePage extends AppCompatActivity {

    // Declare the bottom navigation buttons
    LinearLayout home, progress, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_layout);
        // Initialize the bottom navigation buttons
        home = findViewById(R.id.home);
        progress = findViewById(R.id.progress);
        profile = findViewById(R.id.profile);

        // Load the default fragment (HomeFragment) when activity starts
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new HomeFragment()) // Replace container with HomeFragment
                .commit();
    }

    // Method to handle fragment transactions based on clicked tab
    public void tabClickListener(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (view.getId() == R.id.home) {
            transaction.replace(R.id.container, new HomeFragment());
        } else if (view.getId() == R.id.progress) {
            transaction.replace(R.id.container, new ProgressFragment());
        } else if (view.getId() == R.id.profile) {
            transaction.replace(R.id.container, new ProfileFragment());
        }

        transaction.commit();
    }
}