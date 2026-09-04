sed -i 's/fun generateAndSaveRecipe/fun generateRecipe/' app/src/main/java/com/example/ui/ChefViewModel.kt
sed -i 's/val id = dao.insertRecipe(recipeEntity)/_unsavedRecipe.value = recipeEntity\n            val id = -1L/' app/src/main/java/com/example/ui/ChefViewModel.kt
