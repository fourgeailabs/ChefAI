sed -i 's/fun searchAndSaveRecipe/fun searchRecipe/' app/src/main/java/com/example/ui/ChefViewModel.kt
sed -i 's/dao.insertRecipe(recipeEntity)/_unsavedRecipe.value = recipeEntity\n            val id = -1L/' app/src/main/java/com/example/ui/ChefViewModel.kt
