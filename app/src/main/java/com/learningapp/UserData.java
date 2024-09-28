package com.learningapp;

public class UserData {
    public int userId;
    public int correctCount;
    public int wrongCount;

    public UserData(int userId, int correctCount, int wrongCount) {
        this.userId = userId;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
    }
}
