package com.learningapp;

import android.os.CountDownTimer;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Question> questions;
    private int currentQuestionIndex;
    private int score;
    private CountDownTimer timer;
    private ProgressBar timerProgressBar;
    private GameListener gameListener;

    public GameManager(ProgressBar timerProgressBar, GameListener listener) {
        this.timerProgressBar = timerProgressBar;
        this.gameListener = listener;
        this.questions = new ArrayList<>();
        initializeTimer();
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void startGame() {
        currentQuestionIndex = 0;
        score = 0;
        loadNextQuestion();
        timer.start();
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            gameListener.onQuestionLoaded(currentQuestion);
        } else {
            endGame();
        }
    }

    public boolean checkAnswer(String answer) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        boolean isCorrect = answer.equals(currentQuestion.getCorrectAnswer());
        if (isCorrect) {
            score++;
        }
        currentQuestionIndex++;
        loadNextQuestion();
        return isCorrect;
    }

    private void initializeTimer() {
        timer = new CountDownTimer(60000, 1000) { // 60 seconds timer, update every 1 second
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                timerProgressBar.setProgress((int) secondsRemaining);
            }

            @Override
            public void onFinish() {
                timerProgressBar.setProgress(0);
                endGame();
            }
        };
    }

    private void endGame() {
        if (timer != null) {
            timer.cancel();
        }
        gameListener.onGameFinished(score);
    }

    public interface GameListener {
        void onQuestionLoaded(Question question);
        void onGameFinished(int score);
    }
}
