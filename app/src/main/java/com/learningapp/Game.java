package com.learningapp;

public class Game {
    private String type;
    private int drawableResId;

    // Constructor
    public Game(String type, int drawableResId) {
        this.type = type;
        this.drawableResId = drawableResId;
    }

    // Getter for type
    public String getType() {
        return type;
    }

    // Setter for type
    public void setType(String type) {
        this.type = type;
    }

    // Getter for drawableResId
    public int getDrawableResId() {
        return drawableResId;
    }

    // Setter for drawableResId
    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }
}
