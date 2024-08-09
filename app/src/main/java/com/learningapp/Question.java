package com.learningapp;

public class Question {
    private int[] imageResourceIds;
    private int[] counts;
    private int correctAnswer;

    public Question(int[] imageResourceIds, int[] counts, int correctAnswer) {
        this.imageResourceIds = imageResourceIds;
        this.counts = counts;
        this.correctAnswer = correctAnswer;
    }

    public int[] getImageResourceIds() {
        return imageResourceIds;
    }

    public int[] getCounts() {
        return counts;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
