package com.learningapp;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private List<QuizAnswer> answers;
    private QuizAdapterListener listener;
    private int selectedPosition = -1;

    public QuizAdapter(List<QuizAnswer> answers, QuizAdapterListener listener) {
        this.answers = answers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.q_answers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizAnswer answer = answers.get(position);
        holder.answerTextView.setText(answer.getAnswerText());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnswerSelected(answer);
                selectAnswer(position);
            }
        });

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.selected_answer_color)); // Change background color
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.transparent)); // Reset background color
        }
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public void setAnswers(List<QuizAnswer> answers) {
        this.answers = answers;
        notifyDataSetChanged();
    }

    public void selectAnswer(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();

        // Flashing effect
        new Handler().postDelayed(() -> {
            selectedPosition = -1;
            notifyDataSetChanged();
        }, 500); // Change the delay for the flashing effect duration
    }

    public void updateAnswers(List<QuizAnswer> newAnswers) {
        this.answers = newAnswers;
        notifyDataSetChanged();
    }

    public interface QuizAdapterListener {
        void onAnswerSelected(QuizAnswer selectedAnswer);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView answerTextView;

        ViewHolder(View itemView) {
            super(itemView);
            answerTextView = itemView.findViewById(R.id.answerTextView);
        }
    }
}
