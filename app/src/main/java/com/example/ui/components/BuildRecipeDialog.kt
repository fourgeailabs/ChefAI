package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CelebrityChefRegistry
import com.example.data.RecipeEntity
import com.example.ui.theme.TerracottaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildRecipeDialog(
    initialRecipe: RecipeEntity? = null,
    onDismiss: () -> Unit,
    onSave: (RecipeEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialRecipe?.title ?: "") }
    var chefInspiration by remember { mutableStateOf(initialRecipe?.chefInspiration ?: "Gordon Ramsay") }
    var craving by remember { mutableStateOf(initialRecipe?.craving ?: "Crispy & Savory") }
    var ingredients by remember { mutableStateOf(initialRecipe?.ingredients ?: "") }
    var instructions by remember { mutableStateOf(initialRecipe?.instructions ?: "") }
    var prepTime by remember { mutableStateOf(initialRecipe?.prepTime ?: "15 mins") }
    var cookTime by remember { mutableStateOf(initialRecipe?.cookTime ?: "25 mins") }
    var calories by remember { mutableStateOf(initialRecipe?.calories ?: "450 kcal") }
    var protein by remember { mutableStateOf(initialRecipe?.protein ?: "25g") }
    var carbs by remember { mutableStateOf(initialRecipe?.carbs ?: "40g") }
    var fat by remember { mutableStateOf(initialRecipe?.fat ?: "15g") }
    var servings by remember { mutableIntStateOf(initialRecipe?.servings ?: 4) }
    var difficulty by remember { mutableStateOf(initialRecipe?.difficulty ?: "Medium") }
    var chefTip by remember { mutableStateOf(initialRecipe?.chefTip ?: "Season every layer evenly during cooking.") }
    var cuisine by remember { mutableStateOf(initialRecipe?.cuisine ?: "Fusion") }
    var dietary by remember { mutableStateOf(initialRecipe?.dietary ?: "None") }

    val chefs = CelebrityChefRegistry.allChefs.map { it.name }
    val cravings = listOf("Crispy & Savory", "Warm & Comforting", "Smoky & Spicy", "Rich & Decadent", "Fresh & Zesty", "Sweet & Tangy")
    val difficulties = listOf("Easy", "Medium", "Master Chef")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .gourmetDepth(elevation = 16.dp, shapeRadius = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (initialRecipe == null) "Build Custom Recipe" else "Edit Recipe Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Recipe Title *") },
                        placeholder = { Text("e.g. Crispy Garlic Butter Ribeye") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Master Chef Inspiration Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Chef Inspiration:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(chefs) { chefName ->
                                FilterChip(
                                    selected = chefInspiration == chefName,
                                    onClick = { chefInspiration = chefName },
                                    label = { Text(chefName, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                    // Craving Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Craving Profile:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(cravings) { c ->
                                FilterChip(
                                    selected = craving == c,
                                    onClick = { craving = c },
                                    label = { Text(c, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ingredients,
                        onValueChange = { ingredients = it },
                        label = { Text("Ingredients * (Comma or newline separated)") },
                        placeholder = { Text("e.g. 2 Ribeye steaks, 4 cloves garlic, 3 tbsp butter, fresh rosemary...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Step-by-Step Instructions *") },
                        placeholder = { Text("1. Season meat...\n2. Sear in cast iron...\n3. Baste with butter...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = prepTime,
                            onValueChange = { prepTime = it },
                            label = { Text("Prep Time") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = cookTime,
                            onValueChange = { cookTime = it },
                            label = { Text("Cook Time") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = calories,
                            onValueChange = { calories = it },
                            label = { Text("Calories") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = protein,
                            onValueChange = { protein = it },
                            label = { Text("Protein") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = carbs,
                            onValueChange = { carbs = it },
                            label = { Text("Carbs") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = fat,
                            onValueChange = { fat = it },
                            label = { Text("Fat") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = chefTip,
                        onValueChange = { chefTip = it },
                        label = { Text("Chef's Pro Tip") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Difficulty:", style = MaterialTheme.typography.labelMedium)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(difficulties) { d ->
                                FilterChip(
                                    selected = difficulty == d,
                                    onClick = { difficulty = d },
                                    label = { Text(d) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && ingredients.isNotBlank() && instructions.isNotBlank()) {
                            val selectedChefObj = CelebrityChefRegistry.getChefByName(chefInspiration)
                            val recipeToSave = (initialRecipe ?: RecipeEntity(title = title, ingredients = ingredients, instructions = instructions)).copy(
                                title = title.trim(),
                                ingredients = ingredients.trim(),
                                instructions = instructions.trim(),
                                prepTime = prepTime.trim(),
                                cookTime = cookTime.trim(),
                                calories = calories.trim(),
                                protein = protein.trim(),
                                carbs = carbs.trim(),
                                fat = fat.trim(),
                                servings = servings,
                                difficulty = difficulty,
                                chefTip = chefTip.trim(),
                                cuisine = cuisine,
                                dietary = dietary,
                                chefInspiration = chefInspiration,
                                craving = craving,
                                chefQuote = selectedChefObj.quote
                            )
                            onSave(recipeToSave)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .gourmetButtonShadow(),
                    shape = RoundedCornerShape(14.dp),
                    enabled = title.isNotBlank() && ingredients.isNotBlank() && instructions.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Text(
                        text = if (initialRecipe == null) "Save Custom Recipe" else "Update Recipe",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
