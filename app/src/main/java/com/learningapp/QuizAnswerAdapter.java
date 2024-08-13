package com.learningapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizAnswerAdapter extends RecyclerView.Adapter<QuizAnswerAdapter.AnswerViewHolder> {

    private List<QuizAnswer> answerList;

    public QuizAnswerAdapter(List<QuizAnswer> answers) {
        this.answerList = answers;
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.q_answers, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnswerViewHolder holder, int position) {
        QuizAnswer answer = answerList.get(position);
        holder.answerTextView.setText(answer.getAnswerText());
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView answerTextView;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            answerTextView = itemView.findViewById(R.id.answerTextView);
        }
    }
}
