package com.example.cuisineconnect;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    // Views
    private ImageView recipeImageView;
    private TextView recipeTitleTextView, recipeCountryTextView, recipeDescriptionTextView;
    private RatingBar ratingBar;
    private Button translateButton, commentSubmitButton;
    private EditText commentInput;

    // RecyclerView for comments
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    // Firebase Database Reference
    private DatabaseReference databaseRef;

    // Recipe Data
    private String recipeId, recipeTitle, recipeCountry, recipeDescription, recipeImageUrl;

    private String fromLanguage="sw";
    private String toLanguage="en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize Views
        recipeImageView = findViewById(R.id.recipeImageView);
        recipeTitleTextView = findViewById(R.id.recipeTitleTextView);
        recipeCountryTextView = findViewById(R.id.recipeCountryTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
        ratingBar = findViewById(R.id.ratingBar);
        translateButton = findViewById(R.id.translateButton);
        commentInput = findViewById(R.id.commentInput);
        commentSubmitButton = findViewById(R.id.commentSubmitButton);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);

        // Set up RecyclerView for comments
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // Get recipe data from Intent
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            recipeTitle = intent.getStringExtra("recipeTitle");
            recipeDescription = intent.getStringExtra("recipeDescription");
            recipeImageUrl = intent.getStringExtra("recipeImageUrl");
            recipeId = intent.getStringExtra("recipeId");

            // Verify data
            if (recipeTitle == null || recipeDescription == null || recipeImageUrl == null || recipeId == null) {
                Toast.makeText(this, "Recipe data is missing", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Set recipe details to views
            recipeTitleTextView.setText(recipeTitle);
            recipeDescriptionTextView.setText(recipeDescription);
            Picasso.get().load(recipeImageUrl).into(recipeImageView);

            // Load country from Firebase
            loadCountry(recipeId);

            // Load comments from Firebase
            loadComments(recipeId);

            // Load and display rating from Firebase
            loadRating(recipeId);
        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Rating Bar functionality
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                postRating(recipeId, rating);
                Toast.makeText(RecipeDetailActivity.this, "Rating: " + rating, Toast.LENGTH_SHORT).show();
            }
            
        });

        // Translate Button functionality
        translateButton.setOnClickListener(v ->
                translateDescription(recipeDescription, fromLanguage, toLanguage)
        );

        // Submit Comment Button functionality
        commentSubmitButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (!TextUtils.isEmpty(commentText)) {
                postComment(recipeId, commentText);  // Post comment to Firebase
            } else {
                Toast.makeText(RecipeDetailActivity.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to load country from Firebase
    private void loadCountry(String recipeId) {
        // Reference Firebase for country field
        DatabaseReference countryRef = FirebaseDatabase.getInstance("https://cuisineconnect-ccbd8-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("recipes")
                .child(recipeId)
                .child("country");

        countryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    recipeCountry = dataSnapshot.getValue(String.class);  // Retrieve the country
                    recipeCountryTextView.setText("Country: " + recipeCountry); // Set it in the TextView
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecipeDetailActivity.this, "Failed to load country", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments(String recipeId) {
        // Reference Firebase for comments under the recipe
        databaseRef = FirebaseDatabase.getInstance("https://cuisineconnect-ccbd8-default-rtdb.europe-west1.firebasedatabase.app").getReference("recipes").child(recipeId).child("comments");

        // Listen for comment changes in Firebase
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged(); // Update RecyclerView when new comments are fetched
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecipeDetailActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment(String recipeId, String commentText) {
        // Create a new comment reference in Firebase
        DatabaseReference commentRef = FirebaseDatabase.getInstance("https://cuisineconnect-ccbd8-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("recipes")
                .child(recipeId)
                .child("comments");

        String commentId = commentRef.push().getKey();  // Generate a unique ID for the comment
        Comment newComment = new Comment("Anonymous", commentText);  // Replace with the actual user's name

        if (commentId != null) {
            commentRef.child(commentId).setValue(newComment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RecipeDetailActivity.this, "Comment posted", Toast.LENGTH_SHORT).show();
                        commentInput.setText("");  // Clear input after posting
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RecipeDetailActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(RecipeDetailActivity.this, "Error: commentId is null", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to post rating to Firebase
    private void postRating(String recipeId, float rating) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance("https://cuisineconnect-ccbd8-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("recipes")
                .child(recipeId)
                .child("ratings");

        // Generate a unique user ID using timestamp or UUID
        String userId = String.valueOf(System.currentTimeMillis()); // or UUID.randomUUID().toString();

        // Create a map to hold the rating
        HashMap<String, Object> ratingData = new HashMap<>();
        ratingData.put(userId, rating); // Store the rating under the generated user ID

        // Push the rating data to the database
        ratingsRef.updateChildren(ratingData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RecipeDetailActivity.this, "Rating posted", Toast.LENGTH_SHORT).show();
                    // Reload the rating after posting to update the UI
                    loadRating(recipeId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RecipeDetailActivity.this, "Failed to post rating", Toast.LENGTH_SHORT).show();
                });
    }

    // Function to load and display rating from Firebase
    private void loadRating(String recipeId) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance("https://cuisineconnect-ccbd8-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("recipes")
                .child(recipeId)
                .child("ratings");

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float totalRating = 0;
                    int ratingCount = 0;

                    // Iterate over each rating entry
                    for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                        Float rating = ratingSnapshot.getValue(Float.class);
                        if (rating != null) {
                            totalRating += rating;
                            ratingCount++;
                        }
                    }

                    if (ratingCount > 0) {
                        float averageRating = totalRating / ratingCount;
                        ratingBar.setRating(averageRating); // Set the rating bar to the average rating
                        Toast.makeText(RecipeDetailActivity.this, "Average Rating: " + averageRating, Toast.LENGTH_SHORT).show();
                    } else {
                        ratingBar.setRating(0); // No ratings
                    }
                } else {
                    ratingBar.setRating(0); // No ratings in the database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecipeDetailActivity.this, "Failed to load ratings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  void translateDescription(String description, String fromLanguage, String toLanguage) {
        final MicrosoftTranslatorClient translatorClient = new MicrosoftTranslatorClient();
                new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return translatorClient.translate(description, fromLanguage, toLanguage);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String translatedText) {
                if (translatedText != null) {
                    // Update your UI with the translated text
                    recipeDescriptionTextView.setText(translatedText);
                } else {
                    // Handle translation error
                    Toast.makeText(RecipeDetailActivity.this, "Translation failed", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}