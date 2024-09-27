package com.learningapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

public class DrawingView extends View {

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private float lastX, lastY;
    private Stack<Bitmap> history; // Stack to keep history for redo functionality

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);  // Default color
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        history = new Stack<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {  // Ensure the dimensions are valid
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            // Fill the canvas with white color
            canvas.drawColor(Color.WHITE);
            history.push(bitmap.copy(Bitmap.Config.ARGB_8888, true)); // Save initial state
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Start drawing
                lastX = x;
                lastY = y;
                drawOnCanvas(lastX, lastY, lastX, lastY);  // Draw a point
                break;
            case MotionEvent.ACTION_MOVE:
                // Continue drawing
                drawOnCanvas(lastX, lastY, x, y);
                lastX = x;
                lastY = y;
                break;
        }
        invalidate();  // Redraw the view
        return true;  // Indicate that the event has been handled
    }

    private void drawOnCanvas(float startX, float startY, float endX, float endY) {
        if (canvas != null) {
            canvas.drawLine(startX, startY, endX, endY, paint);
            // Save the current bitmap state after drawing
            history.push(bitmap.copy(Bitmap.Config.ARGB_8888, true));
        }
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void clearCanvas() {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            history.clear(); // Clear history when clearing the canvas
            history.push(bitmap.copy(Bitmap.Config.ARGB_8888, true)); // Save the empty state
            invalidate();  // Refresh the view
        }
    }

    public void redo() {
        if (history.size() > 1) {
            history.pop(); // Remove the last bitmap (current state)
            bitmap = history.peek(); // Get the previous state
            invalidate(); // Refresh the view
        }
    }

    public Bitmap getDrawingBitmap() {
        return bitmap;
    }
}
