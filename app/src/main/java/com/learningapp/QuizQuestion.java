package com.learningapp;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestion {

    private int id;
    private String questionText;
    private List<QuizAnswer> answers;
    private QuizAnswer correctAnswer;  // Add this field
    private QuizAnswer userSelectedAnswer;

    public QuizQuestion(int id, String questionText) {
        this.id = id;
        this.questionText = questionText;
        this.answers = new ArrayList<>();
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for questionText
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    // Getter and Setter for answers
    public List<QuizAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswer> answers) {
        this.answers = answers;
    }

    // Add method to set correct answer
    public void setCorrectAnswer(QuizAnswer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    // Getter for correctAnswer
    public QuizAnswer getCorrectAnswer() {
        return correctAnswer;
    }

    // Add method to set user-selected answer
    public void setUserSelectedAnswer(QuizAnswer userSelectedAnswer) {
        this.userSelectedAnswer = userSelectedAnswer;
    }

    // Getter for userSelectedAnswer
    public QuizAnswer getUserSelectedAnswer() {
        return userSelectedAnswer;
    }

    // Method to add an answer to the list
    public void addAnswer(QuizAnswer answer) {
        answers.add(answer);
    }
}
