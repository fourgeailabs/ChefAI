package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val itemName: String,
    val isChecked: Boolean = false,
    val recipeTitle: String = "Custom"
)
