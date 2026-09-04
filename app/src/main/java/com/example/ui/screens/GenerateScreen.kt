package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CelebrityChef
import com.example.data.CelebrityChefRegistry
import com.example.data.RecipeEntity
import com.example.ui.ChefViewModel
import com.example.ui.components.BarcodeScannerDialog
import com.example.ui.components.BuildRecipeDialog
import com.example.ui.components.GeminiProgressModal
import com.example.ui.components.gourmetButtonShadow
import com.example.ui.components.gourmetDepth
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.theme.TerracottaSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GenerateScreen(
    viewModel: ChefViewModel,
    onRecipeClick: (Long) -> Unit
) {
    var selectedChef by remember { mutableStateOf(CelebrityChefRegistry.GORDON_RAMSAY) }
    var selectedCraving by remember { mutableStateOf("Crispy & Savory") }
    var customCraving by remember { mutableStateOf("") }
    var ingredientsInput by remember { mutableStateOf("") }
    var selectedDietary by remember { mutableStateOf("None") }
    var selectedCuisine by remember { mutableStateOf("Chef's Specialty") }
    var showBuildDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }

    val cravingOptions = listOf(
        "Crispy & Savory",
        "Warm & Comforting",
        "Smoky & Spicy",
        "Rich & Decadent",
        "Fresh & Zesty",
        "Sweet & Tangy",
        "Fast 30-Min Feast"
    )

    val commonHouseStaples = listOf(
        "Chicken Breast", "Eggs", "Pasta", "Garlic", "Butter",
        "Cheddar / Parm", "Rice", "Canned Tomatoes", "Potatoes",
        "Spinach", "Lemon", "Ground Beef", "Mushrooms"
    )

    val cuisines = listOf("Chef's Specialty", "French", "Italian", "Southwestern", "American Bistro", "Mediterranean", "Asian Fusion")
    val dietaryOptions = listOf("None", "High Protein", "Keto", "Vegetarian", "Vegan", "Gluten-Free", "Low Carb")

    val recipes by viewModel.allRecipes.collectAsState()
    val pantryItems by viewModel.pantryItems.collectAsState()
    val progressState by viewModel.progressState.collectAsState()

    // Gemini API Loading Modal with Progress & Stage Tracker
    GeminiProgressModal(progressState = progressState)

    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = { showBarcodeScanner = false },
            onBarcodeScanned = { barcode, onProcessed ->
                viewModel.processBarcode(
                    barcode = barcode,
                    autoAddToPantry = true,
                    autoCheckShoppingList = true
                ) { product, matchedList ->
                    // Auto append scanned ingredient to the In-The-House prompt
                    if (!ingredientsInput.contains(product.name, ignoreCase = true)) {
                        ingredientsInput = if (ingredientsInput.isBlank()) product.name else "$ingredientsInput, ${product.name}"
                    }
                    onProcessed(product, matchedList)
                }
            },
            onManualAddShoppingItem = { name ->
                viewModel.addShoppingItem(name)
            }
        )
    }

    if (showBuildDialog) {
        BuildRecipeDialog(
            onDismiss = { showBuildDialog = false },
            onSave = { newRecipe ->
                viewModel.buildAndSaveCustomRecipe(newRecipe) { newId ->
                    showBuildDialog = false
                    onRecipeClick(newId)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 12.dp, shapeRadius = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(TerracottaPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "ChefAI Studio",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Master Chef Culinary Engine",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                FilledTonalButton(
                                    onClick = { showBuildDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Build Recipe", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Pull iconic meals from master chefs tailored to your specific craving, diet, and what you have in the house right now.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // SECTION 1: SELECT CELEBRITY MASTER CHEF
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. Choose Master Chef Inspiration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${CelebrityChefRegistry.allChefs.size} Chefs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(CelebrityChefRegistry.allChefs) { chef ->
                            val isSelected = selectedChef.id == chef.id
                            CelebrityChefCard(
                                chef = chef,
                                isSelected = isSelected,
                                onClick = { selectedChef = chef }
                            )
                        }
                    }
                }
            }

            // SECTION 2: WHAT ARE YOU CRAVING?
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. What are you craving?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Craving Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            cravingOptions.forEach { craving ->
                                val isSelected = selectedCraving == craving && customCraving.isBlank()
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedCraving = craving
                                        customCraving = ""
                                    },
                                    label = { Text(craving, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        // Or Custom Craving
                        OutlinedTextField(
                            value = customCraving,
                            onValueChange = {
                                customCraving = it
                                if (it.isNotBlank()) selectedCraving = it
                            },
                            placeholder = { Text("Or describe your custom craving (e.g. late night spicy pasta...)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // SECTION 3: WHAT'S IN THE HOUSE? (PANTRY INVENTORY & BARCODE SCANNER)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Kitchen,
                                    contentDescription = null,
                                    tint = TerracottaPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "3. In The House Right Now",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // 1-Tap Barcode Scanner Button
                            FilledTonalButton(
                                onClick = { showBarcodeScanner = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Barcode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = ingredientsInput,
                            onValueChange = { ingredientsInput = it },
                            placeholder = { Text("e.g. Chicken breast, garlic, butter, canned tomatoes, rice...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2,
                            trailingIcon = {
                                if (ingredientsInput.isNotBlank()) {
                                    IconButton(onClick = { ingredientsInput = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            }
                        )

                        // Scanned Pantry Items if available
                        if (pantryItems.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Scanned Pantry Inventory (${pantryItems.size}):",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TerracottaPrimary
                                    )
                                    TextButton(
                                        onClick = {
                                            val pantryNames = pantryItems.map { it.name }
                                            val newItems = pantryNames.filter { !ingredientsInput.contains(it, ignoreCase = true) }
                                            if (newItems.isNotEmpty()) {
                                                ingredientsInput = if (ingredientsInput.isBlank()) {
                                                    newItems.joinToString(", ")
                                                } else {
                                                    "$ingredientsInput, ${newItems.joinToString(", ")}"
                                                }
                                            }
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("+ Add All to Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    pantryItems.forEach { pantryItem ->
                                        val isAdded = ingredientsInput.contains(pantryItem.name, ignoreCase = true)
                                        FilterChip(
                                            selected = isAdded,
                                            onClick = {
                                                if (isAdded) {
                                                    // Remove from input
                                                    ingredientsInput = ingredientsInput
                                                        .split(",")
                                                        .map { it.trim() }
                                                        .filter { !it.equals(pantryItem.name, ignoreCase = true) }
                                                        .joinToString(", ")
                                                } else {
                                                    ingredientsInput = if (ingredientsInput.isBlank()) pantryItem.name else "$ingredientsInput, ${pantryItem.name}"
                                                }
                                            },
                                            label = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(pantryItem.name, fontSize = 11.sp)
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Quick-tap common household staples
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Or tap common household staples:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                commonHouseStaples.forEach { staple ->
                                    val isAdded = ingredientsInput.contains(staple, ignoreCase = true)
                                    SuggestionChip(
                                        onClick = {
                                            if (!isAdded) {
                                                ingredientsInput = if (ingredientsInput.isBlank()) staple else "$ingredientsInput, $staple"
                                            }
                                        },
                                        label = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (isAdded) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = TerracottaPrimary)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                }
                                                Text(staple, fontSize = 11.sp)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 4: DIET & CUISINE PREFERENCES
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "4. Diet & Cuisine Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Dietary
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Dietary Restriction:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(dietaryOptions) { diet ->
                                    FilterChip(
                                        selected = selectedDietary == diet,
                                        onClick = { selectedDietary = diet },
                                        label = { Text(diet, fontSize = 12.sp) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }

                        // Cuisine
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Cuisine Style:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(cuisines) { cuisine ->
                                    FilterChip(
                                        selected = selectedCuisine == cuisine,
                                        onClick = { selectedCuisine = cuisine },
                                        label = { Text(cuisine, fontSize = 12.sp) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // GENERATE BUTTON
                        val activeCraving = customCraving.ifBlank { selectedCraving }
                        Button(
                            onClick = {
                                viewModel.generateAndSaveRecipe(
                                    chef = selectedChef.name,
                                    craving = activeCraving,
                                    ingredients = ingredientsInput.ifBlank { "Eggs, butter, garlic, olive oil, cracked pepper, pantry staples" },
                                    dietary = selectedDietary,
                                    cuisine = selectedCuisine
                                ) { newId ->
                                    onRecipeClick(newId)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .gourmetButtonShadow(elevation = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !progressState.isGenerating,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TerracottaPrimary
                            )
                        ) {
                            if (progressState.isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Synthesizing ${selectedChef.name} Recipe...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pull ${selectedChef.name} Meal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 5: SAVED & RECENT RECIPES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chef Recipe Cookbook",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${recipes.size} Recipes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(recipes, key = { it.id }) { recipe ->
                RecipeFeedCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe.id) },
                    onFavoriteClick = { viewModel.toggleFavorite(recipe) }
                )
            }
        }
    }
}

@Composable
fun CelebrityChefCard(
    chef: CelebrityChef,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .gourmetDepth(
                elevation = if (isSelected) 10.dp else 4.dp,
                shapeRadius = 18.dp,
                hasBorderGlow = isSelected
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        border = if (isSelected) BorderStroke(2.dp, TerracottaPrimary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = chef.accentColor,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = chef.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }

                if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TerracottaPrimary
                    ) {
                        Text(
                            text = "SELECTED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = chef.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = chef.moniker,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"${chef.quote}\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Signature technique pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = chef.tagline,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun RecipeFeedCard(
    recipe: RecipeEntity,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Chef & Craving tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = recipe.chefInspiration,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (recipe.craving.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = recipe.craving,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (recipe.isFavorite) TerracottaPrimary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.prepTime} prep • ${recipe.cookTime} cook",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp), tint = TerracottaSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = recipe.calories,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
