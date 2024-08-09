package com.learningapp;



import java.util.List;

public class Quiz {
    private String type;
    private List<Question> questions;

    public Quiz(String type, List<Question> questions) {
        this.type = type;
        this.questions = questions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
