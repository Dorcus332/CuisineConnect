package com.example.cuisineconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuisineconnect.R;
import com.example.cuisineconnect.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.userNameTextView.setText(comment.getUserName());
        holder.commentTextView.setText(comment.getCommentText());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // Method to update comments list and refresh adapter
    public void updateComments(List<Comment> newComments) {
        commentList.clear();
        commentList.addAll(newComments);
        notifyDataSetChanged();
    }
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, commentTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
