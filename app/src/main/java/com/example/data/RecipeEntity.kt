package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val ingredients: String, // comma or newline separated
    val instructions: String, // numbered steps or newline separated
    val prepTime: String = "15 mins",
    val cookTime: String = "20 mins",
    val calories: String = "450 kcal",
    val protein: String = "28g",
    val carbs: String = "38g",
    val fat: String = "14g",
    val servings: Int = 4,
    val difficulty: String = "Medium", // Easy, Medium, Master
    val chefTip: String = "Sear ingredients on high heat first to lock in rich, caramelized juices.",
    val cuisine: String = "Fusion",
    val dietary: String = "None",
    val chefInspiration: String = "Gordon Ramsay", // Gordon Ramsay, Julia Child, Anthony Bourdain, Martha Stewart, Rachael Ray, Bobby Flay
    val craving: String = "Crispy & Savory", // e.g. "Warm & Comforting", "Smoky & Spicy"
    val chefQuote: String = "Cook with passion! Wake up the pan, taste as you go, and season at every layer.",
    val imageUrl: String = "",
    val platePresentation: String = "",
    val isFavorite: Boolean = false,
    val rating: Float = 0.5f,
    val timestamp: Long = System.currentTimeMillis()
)
