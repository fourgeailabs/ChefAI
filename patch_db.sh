sed -i 's/val isFavorite: Boolean = false,/val isFavorite: Boolean = false,\n    val rating: Float = 0.5f,/' app/src/main/java/com/example/data/RecipeEntity.kt
sed -i 's/version = 5,/version = 6,/' app/src/main/java/com/example/data/ChefDatabase.kt
