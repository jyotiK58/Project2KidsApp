package com.learningapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProgressFragment extends Fragment {

    private TextView imageIdTextView;
    private TextView timeSpentTextView;
    private TextView clusterTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.performance_result, container, false); // Ensure this matches your XML layout filename

        // Find the TextViews by ID
        imageIdTextView = view.findViewById(R.id.imageIdTextView);
        timeSpentTextView = view.findViewById(R.id.timeSpentTextView);
        clusterTextView = view.findViewById(R.id.clusterTextView);

        // Example data, you would typically fetch this from your data source
        String imageId = "Image123"; // Replace with actual data
        String timeSpent = "5 minutes"; // Replace with actual data
        String cluster = "Cluster A"; // Replace with actual data

        // Set data to the TextViews
        imageIdTextView.setText("Image: " + imageId);
        timeSpentTextView.setText("Time spent: " + timeSpent);
        clusterTextView.setText("Cluster: " + cluster);

        return view;
    }
}