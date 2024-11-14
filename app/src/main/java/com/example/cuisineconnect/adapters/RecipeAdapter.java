package com.example.cuisineconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuisineconnect.R;
import com.example.cuisineconnect.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> implements Filterable {

    private List<Recipe> recipeList;
    private List<Recipe> recipeListFull; // A full copy of the original recipe list
    private OnRecipeClickListener onRecipeClickListener;

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.recipeListFull = new ArrayList<>(recipeList); // Copy of the original list for filtering
        this.onRecipeClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe currentRecipe = recipeList.get(position);

        holder.titleTextView.setText(currentRecipe.getTitle());

        Picasso.get().load(currentRecipe.getImageUrl()).into(holder.recipeImage);

        holder.itemView.setOnClickListener(v -> onRecipeClickListener.onRecipeClick(currentRecipe));
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    // Method to update the recipe list and the full list for filtering
    public void updateRecipes(List<Recipe> newRecipes) {
        recipeList.clear();
        recipeList.addAll(newRecipes);  // Update the main list

        recipeListFull.clear();
        recipeListFull.addAll(newRecipes);  // Ensure the full list is in sync for filtering

        notifyDataSetChanged();  // Notify the adapter that the data set has changed
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Recipe> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(recipeListFull); // No filter applied
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Recipe recipe : recipeListFull) {
                        if (recipe.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(recipe); // Add matching recipe to the filtered list
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                recipeList.clear();
                recipeList.addAll((List) results.values); // Update the displayed list
                notifyDataSetChanged(); // Notify the adapter of change
            }
        };
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView recipeImage; // Declare the ImageView for the recipe image

        public RecipeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.recipeTitle); // TextView for the recipe title
            recipeImage = itemView.findViewById(R.id.recipeImage); // Initialize the ImageView
        }
    }
}
