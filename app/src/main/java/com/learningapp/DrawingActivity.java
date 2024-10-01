package com.learningapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_PERMISSION = 100;
    private DrawingView drawingView;
    private Button clearButton;
    private ImageView colorPickerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_activity);

        drawingView = findViewById(R.id.drawing_view);
        clearButton = findViewById(R.id.clear_button);
        colorPickerIcon = findViewById(R.id.color_picker_icon);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clearCanvas();
            }
        });

        colorPickerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker();
            }
        });
    }

    private void showColorPicker() {

        final int[] colors = {0xFF000000, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a color")
                .setItems(new String[]{"Black", "Red", "Green", "Blue", "Yellow", "Magenta", "Cyan"}, (dialog, which) -> {
                    // Set the selected color on the drawing view
                    drawingView.setColor(colors[which]);
                })
                .show();
    }
}
