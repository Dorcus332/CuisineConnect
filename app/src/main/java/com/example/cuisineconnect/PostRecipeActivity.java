package com.example.cuisineconnect;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuisineconnect.models.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostRecipeActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etImageUrl;
    private Button btnPostRecipe;
    private Spinner spinnerCountry;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_recipe);

        // Initialize views
        etTitle = findViewById(R.id.recipeTitle);
        etDescription = findViewById(R.id.recipeDescription);
        etImageUrl = findViewById(R.id.recipeImageUrl);
        btnPostRecipe = findViewById(R.id.btnPostRecipe);
        spinnerCountry = findViewById(R.id.spinnerCountry);

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance("https://cuisineconnect-ccbd8-default-rtdb.europe-west1.firebasedatabase.app").getReference("recipes");

        // Populate the country spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);

        // Post recipe button click
        btnPostRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRecipe();
            }
        });
    }

    private void postRecipe() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String country = spinnerCountry.getSelectedItem().toString();  // Get selected country

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(PostRecipeActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique key for each recipe
        String recipeId = databaseRef.push().getKey();

        // Check if recipeId is generated
        if (recipeId == null) {
            Toast.makeText(PostRecipeActivity.this, "Failed to generate recipe ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Recipe object with the recipeId
        Recipe recipe = new Recipe(recipeId, title, description, imageUrl, country);

        // Add the recipe to the database under the generated ID
        databaseRef.child(recipeId).setValue(recipe)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostRecipeActivity.this, "Recipe posted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to main activity
                    } else {
                        Toast.makeText(PostRecipeActivity.this, "Failed to post recipe", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
