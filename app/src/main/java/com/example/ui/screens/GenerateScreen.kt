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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CelebrityChef
import com.example.data.CelebrityChefRegistry
import com.example.data.CookbookMealImageProvider
import com.example.data.RecipeEntity
import com.example.ui.ChefViewModel
import com.example.ui.components.BarcodeScannerDialog
import com.example.ui.components.BuildRecipeDialog
import com.example.ui.components.ChefBiographyDialog
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
    var selectedPortions by remember { mutableIntStateOf(4) }
    var showBuildDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showBioChef by remember { mutableStateOf<CelebrityChef?>(null) }

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
            },
            onIdentifyVisualIngredient = { bitmap, onResult ->
                viewModel.identifyVisualIngredient(bitmap, onResult)
            },
            onConfirmVisualIngredient = { ingredientName, category, autoPantry, autoShopping, onConfirmed ->
                viewModel.confirmVisualIngredient(
                    ingredientName = ingredientName,
                    category = category,
                    autoAddToPantry = autoPantry,
                    autoCheckShoppingList = autoShopping
                ) { matchedShopping ->
                    if (!ingredientsInput.contains(ingredientName, ignoreCase = true)) {
                        ingredientsInput = if (ingredientsInput.isBlank()) ingredientName else "$ingredientsInput, $ingredientName"
                    }
                    onConfirmed(matchedShopping)
                }
            }
        )
    }

    if (showBioChef != null) {
        ChefBiographyDialog(
            chef = showBioChef!!,
            onDismiss = { showBioChef = null }
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
            // Top Item: Gemini API Recipe Search Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 8.dp, shapeRadius = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Search recipes or ask Gemini AI (e.g. 'Guy Fieri smash burger')...",
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = TerracottaPrimary
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            if (searchQuery.isNotBlank()) {
                                                viewModel.searchRecipe(
                                                    query = searchQuery.trim(),
                                                    preferredChef = selectedChef.name,
                                                    servings = selectedPortions
                                                ) { newId ->
                                                    onRecipeClick(newId)
                                                }
                                            }
                                        }
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = TerracottaPrimary,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.AutoAwesome,
                                                    contentDescription = "Search with Gemini AI",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TerracottaPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchQuery.isNotBlank()) {
                                        viewModel.searchRecipe(
                                            query = searchQuery.trim(),
                                            preferredChef = selectedChef.name,
                                            servings = selectedPortions
                                        ) { newId ->
                                            onRecipeClick(newId)
                                        }
                                    }
                                }
                            )
                        )

                        // Action button when user enters query
                        AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.searchRecipe(
                                            query = searchQuery.trim(),
                                            preferredChef = selectedChef.name,
                                            servings = selectedPortions
                                        ) { newId ->
                                            onRecipeClick(newId)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ask Gemini AI for \"$searchQuery\"", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                // Quick filter matching local cookbook recipes
                                val localMatches = recipes.filter {
                                    it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.ingredients.contains(searchQuery, ignoreCase = true) ||
                                    it.chefInspiration.contains(searchQuery, ignoreCase = true)
                                }
                                if (localMatches.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Saved in Cookbook:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    localMatches.take(3).forEach { matched ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onRecipeClick(matched.id) }
                                                .padding(vertical = 4.dp, horizontal = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = matched.title,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Quick Search Suggestions Pills
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val suggestions = listOf(
                                "🔥 Guy Fieri Smash Burger",
                                "🐟 Gordon Ramsay Crispy Salmon",
                                "🍷 Julia Child Boeuf Bourguignon",
                                "🌮 Bobby Flay Chipotle Tacos",
                                "🥩 Anthony Bourdain Steak Frites",
                                "🥔 Martha Stewart Gratin",
                                "🍝 Rachael Ray 30-Min Pasta"
                            )
                            items(suggestions) { pill ->
                                val cleanQuery = pill.substringAfter(" ")
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        searchQuery = cleanQuery
                                        viewModel.searchRecipe(
                                            query = cleanQuery,
                                            preferredChef = selectedChef.name,
                                            servings = selectedPortions
                                        ) { newId ->
                                            onRecipeClick(newId)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = pill,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
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


            // RECOMMENDED FOR YOU (Based on completed recipes)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Recommended for You",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Based on your history of completed recipes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        val recommendations = listOf(
                            "Smoky Cedar Plank Salmon",
                            "Tuscan Garlic Butter Steak",
                            "Lemon Herb Roast Chicken",
                            "Rich Mushroom Risotto"
                        )
                        items(recommendations) { rec ->
                            Card(
                                modifier = Modifier.width(160.dp).height(100.dp).clickable {
                                    searchQuery = rec
                                    viewModel.searchRecipe(rec, selectedChef.name, selectedPortions) { onRecipeClick(it) }
                                }.gourmetDepth(elevation = 4.dp, shapeRadius = 12.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text(rec, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                }
                            }
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

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedChef.name,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                IconButton(onClick = { showBioChef = selectedChef }) {
                                    Icon(Icons.Default.Info, contentDescription = "Chef Biography", tint = selectedChef.accentColor)
                                }
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            CelebrityChefRegistry.allChefs.forEach { chef ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Surface(shape = CircleShape, color = chef.accentColor, modifier = Modifier.size(32.dp)) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(text = chef.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                            Text(text = chef.name, fontWeight = FontWeight.SemiBold)
                                        }
                                    },
                                    onClick = {
                                        selectedChef = chef
                                        expanded = false
                                    }
                                )
                            }
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

                        // Target Portion / Servings Yield
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target Yield / Portion Size:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { if (selectedPortions > 1) selectedPortions -= 1 }, modifier = Modifier.size(36.dp)) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease Portions", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        Text(
                                            text = "$selectedPortions ${if (selectedPortions == 1) "portion" else "portions"}",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(onClick = { if (selectedPortions < 50) selectedPortions += 1 }, modifier = Modifier.size(36.dp)) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase Portions", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }

                            // Quick Portion Presets
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val portionPresets = listOf(1 to "1 (Solo)", 2 to "2 (Couple)", 4 to "4 (Family)", 6 to "6 (Party)", 8 to "8 (Feast)", 12 to "12 (Crowd)")
                                items(portionPresets) { (count, label) ->
                                    FilterChip(
                                        selected = selectedPortions == count,
                                        onClick = { selectedPortions = count },
                                        label = { Text(label, fontSize = 11.sp) },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // GENERATE BUTTON
                        val activeCraving = customCraving.ifBlank { selectedCraving }
                        Button(
                            onClick = {
                                viewModel.generateRecipe(
                                    chef = selectedChef.name,
                                    craving = activeCraving,
                                    ingredients = ingredientsInput.ifBlank { "Eggs, butter, garlic, olive oil, cracked pepper, pantry staples" },
                                    dietary = selectedDietary,
                                    cuisine = selectedCuisine,
                                    servings = selectedPortions
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
    onClick: () -> Unit,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(250.dp)
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
                // Mini Chef Image - Click opens biography & website dialog
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(2.dp, chef.accentColor, CircleShape)
                        .clickable(onClick = onImageClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (chef.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = chef.avatarUrl,
                            contentDescription = "${chef.name} mini image - Tap for Biography",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = chef.accentColor,
                            modifier = Modifier.fillMaxSize()
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
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tap for Biography button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable(onClick = onImageClick)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = chef.accentColor
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Bio ↗",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val mealImageUrl = CookbookMealImageProvider.resolveMealImage(
        title = recipe.title,
        chefInspiration = recipe.chefInspiration,
        craving = recipe.craving,
        customImageUrl = recipe.imageUrl
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Completed Meal Cookbook Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = mealImageUrl,
                    contentDescription = "Completed ${recipe.title} from Cookbook",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cookbook Appearance",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
            }

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

                if (recipe.platePresentation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = recipe.platePresentation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
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
}
