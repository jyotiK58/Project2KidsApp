package com.learningapp;



public class QuizAnswer {
    private String answerText;
    private boolean isCorrect;

    public QuizAnswer(String answerText, boolean isCorrect) {
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
