package com.example.recipebook

enum class RecipeStatus {
    WANT_TO_COOK,
    COOKING,
    COOKED
}

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val cookingTime: Int,
    val difficulty: String,
    val category: String,
    val status: RecipeStatus
)