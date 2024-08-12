package com.learningapp;

import java.util.List;

public class QuizQuestion {
    private String questionText;
    private List<QuizAnswer> answers;

    public QuizQuestion(String questionText, List<QuizAnswer> answers) {
        this.questionText = questionText;
        this.answers = answers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<QuizAnswer> getAnswers() {
        return answers;
    }
}
