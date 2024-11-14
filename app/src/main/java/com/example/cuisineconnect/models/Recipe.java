package com.example.cuisineconnect.models;

public class Recipe {
    private String recipeId;
    public String title;
    public String description;
    public String imageUrl;
    public String country;

    // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    public Recipe() {
    }

    public Recipe(String recipeId, String title, String description, String imageUrl, String country) {
        this.recipeId = recipeId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.country = country;
    }

    // Getters and Setters
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
