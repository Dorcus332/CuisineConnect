package com.example.cuisineconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuisineconnect.adapters.RecipeAdapter;
import com.example.cuisineconnect.models.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private SearchView searchView;
    private FloatingActionButton fabAddRecipe;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        fabAddRecipe = findViewById(R.id.fabAddRecipe);

        // Initialize Firebase Database
        databaseRef = FirebaseDatabase.getInstance("https://cuisineconnect-ccbd8-default-rtdb.europe-west1.firebasedatabase.app").getReference("recipes");

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this); // Pass this as the click listener
        recyclerView.setAdapter(recipeAdapter);

        // Load recipes from Firebase Realtime Database
        loadRecipes();

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recipeAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recipeAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Add new recipe functionality
        fabAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PostRecipeActivity.class);
            startActivity(intent);
        });
    }

    private void loadRecipes() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Recipe> fetchedRecipes = new ArrayList<>();  // Use a temporary list to avoid clearing recipeList prematurely

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        // Get the recipeId (key) from the snapshot
                        String recipeId = snapshot.getKey();
                        recipe.setRecipeId(recipeId);
                        fetchedRecipes.add(recipe);
                    } else {
                    }
                }

                // Update the adapter only after the recipes are fetched
                if (!fetchedRecipes.isEmpty()) {
                    recipeAdapter.updateRecipes(fetchedRecipes);  // Call the method in RecipeAdapter to update both recipeList and recipeListFull
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {

        Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getRecipeId());
        intent.putExtra("recipeTitle", recipe.getTitle());
        intent.putExtra("recipeDescription", recipe.getDescription());
        intent.putExtra("recipeImageUrl", recipe.getImageUrl());

        startActivity(intent);
    }
}