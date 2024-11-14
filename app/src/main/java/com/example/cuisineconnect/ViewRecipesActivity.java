package com.example.cuisineconnect;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cuisineconnect.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewRecipesActivity extends AppCompatActivity {

    private ListView recipeListView;
    private ArrayList<String> recipeList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private FirebaseDatabase database;
    private DatabaseReference recipeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipes);

        recipeListView = findViewById(R.id.recipeListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        recipeListView.setAdapter(adapter);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        recipeRef = database.getReference("recipes");

        // Retrieve recipes from Firebase Realtime Database
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipeList.add(recipe.getTitle() + "\n" + recipe.getDescription());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
