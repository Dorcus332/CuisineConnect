package com.example.cuisineconnect;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuisineconnect.adapters.CommentAdapter;
import com.example.cuisineconnect.models.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewComments;
    private EditText editTextComment;
    private Button buttonPostComment;

    private DatabaseReference commentsRef;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private String recipeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Initialize views
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonPostComment = findViewById(R.id.buttonPostComment);

        // Get recipe title from intent
        recipeTitle = getIntent().getStringExtra("recipeTitle");

        // Initialize Firebase Database reference
        commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(recipeTitle);

        // Initialize RecyclerView
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        recyclerViewComments.setAdapter(commentAdapter);

        // Load existing comments from Firebase
        loadComments();

        // Post a new comment
        buttonPostComment.setOnClickListener(v -> postComment());
    }

    private void loadComments() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CommentActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment() {
        String commentText = editTextComment.getText().toString().trim();

        if (TextUtils.isEmpty(commentText)) {
            editTextComment.setError("Enter a comment");
            return;
        }

        // Assuming the user is logged in and has a userName
        String userName = "Anonymous"; // Replace with actual username if available

        // Create a new comment object
        Comment comment = new Comment(userName, commentText);

        // Push the comment to Firebase
        commentsRef.push().setValue(comment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CommentActivity.this, "Comment posted", Toast.LENGTH_SHORT).show();
                    editTextComment.setText(""); // Clear the input field
                })
                .addOnFailureListener(e -> Toast.makeText(CommentActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show());
    }
}
