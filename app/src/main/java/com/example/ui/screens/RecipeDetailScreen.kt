package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CelebrityChefRegistry
import com.example.data.CookbookMealImageProvider
import com.example.data.RecipeEntity
import com.example.ui.ChefViewModel
import com.example.ui.components.BuildRecipeDialog
import com.example.ui.components.MacroItem
import com.example.ui.components.StepCookModeDialog
import com.example.ui.components.gourmetButtonShadow
import com.example.ui.components.gourmetDepth
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.theme.TerracottaSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    viewModel: ChefViewModel,
    onBack: () -> Unit
) {
    val recipes by viewModel.allRecipes.collectAsState()
    val recipe = recipes.find { it.id == recipeId }
    val context = LocalContext.current

    var servingMultiplier by remember { mutableIntStateOf(recipe?.servings ?: 4) }
    var addedToCartMessage by remember { mutableStateOf(false) }
    var showCookMode by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(recipe?.servings) {
        if (recipe != null) {
            servingMultiplier = recipe.servings
        }
    }

    if (showCookMode && recipe != null) {
        StepCookModeDialog(recipe = recipe, onDismiss = { showCookMode = false })
    }

    if (showEditDialog && recipe != null) {
        BuildRecipeDialog(
            initialRecipe = recipe,
            onDismiss = { showEditDialog = false },
            onSave = { updatedRecipe ->
                viewModel.updateRecipe(updatedRecipe)
                showEditDialog = false
            }
        )
    }

    if (showDeleteConfirm && recipe != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Recipe?") },
            text = { Text("Are you sure you want to remove '${recipe.title}' from your cookbook?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecipe(recipe)
                        showDeleteConfirm = false
                        onBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = recipe?.title ?: "Recipe Details",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (recipe != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Details")
                        }
                        IconButton(onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🍳 ${recipe.title} (Inspired by ${recipe.chefInspiration})\n\nCraving: ${recipe.craving}\nPrep: ${recipe.prepTime} | Cook: ${recipe.cookTime} | ${recipe.calories}\n\nIngredients:\n${recipe.ingredients}\n\nInstructions:\n${recipe.instructions}\n\nCrafted with ChefAI Studio by FourgeAI LABS"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Recipe"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { viewModel.toggleFavorite(recipe) }) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (recipe.isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (recipe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Recipe not found or removed.")
            }
        } else {
            val chefObj = CelebrityChefRegistry.getChefByName(recipe.chefInspiration)

            val mealImageUrl = CookbookMealImageProvider.resolveMealImage(
                title = recipe.title,
                chefInspiration = recipe.chefInspiration,
                craving = recipe.craving,
                customImageUrl = recipe.imageUrl
            )
            val plateDescription = if (recipe.platePresentation.isNotBlank()) {
                recipe.platePresentation
            } else {
                CookbookMealImageProvider.getCookbookAppearanceGuide(
                    title = recipe.title,
                    chefInspiration = recipe.chefInspiration,
                    craving = recipe.craving
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Completed Meal Appearance from Chef's Cookbook
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 12.dp, shapeRadius = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            AsyncImage(
                                model = mealImageUrl,
                                contentDescription = "Completed ${recipe.title} plated from ${recipe.chefInspiration}'s Cookbook",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            )
                            // Subtle gradient overlay for text readability
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.35f),
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.8f)
                                            )
                                        )
                                    )
                            )

                            // Top Pill Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TerracottaPrimary,
                                modifier = Modifier
                                    .padding(14.dp)
                                    .align(Alignment.TopStart)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CHEF'S COOKBOOK MEAL",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // Bottom image caption
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Completed Meal Appearance",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "What this dish is supposed to look like from ${recipe.chefInspiration}'s cookbook",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // Cookbook Plating & Presentation Guide
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = TerracottaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Plating & Visual Presentation Guide",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = plateDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }

                // Hero Banner Card with Depth
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 10.dp, shapeRadius = 22.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = recipe.cuisine.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                if (recipe.dietary != "None") {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = recipe.dietary,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Badges Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoPill(icon = Icons.Default.Schedule, text = "Prep: ${recipe.prepTime}")
                                InfoPill(icon = Icons.Default.Timer, text = "Cook: ${recipe.cookTime}")
                                InfoPill(icon = Icons.Default.Whatshot, text = recipe.calories)
                                InfoPill(icon = Icons.Default.Stars, text = recipe.difficulty)
                            }
                        }
                    }
                }

                // Celebrity Chef Inspiration Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 8.dp, shapeRadius = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = chefObj.accentColor,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = chefObj.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Crafted in the Style of ${recipe.chefInspiration}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = chefObj.moniker,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (recipe.craving.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = recipe.craving,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        if (recipe.chefQuote.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "\"${recipe.chefQuote}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Interactive Cook Mode Launcher Button
                Button(
                    onClick = { showCookMode = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .gourmetButtonShadow(elevation = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Start Step-by-Step Cooking Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                // Servings Adjuster
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Serving Size & Portion Scaling",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Dynamic ingredient scaling ($servingMultiplier ${if (servingMultiplier == 1) "portion" else "portions"})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { if (servingMultiplier > 1) servingMultiplier -= 1 },
                                    enabled = servingMultiplier > 1
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "$servingMultiplier portions",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                IconButton(
                                    onClick = { if (servingMultiplier < 100) servingMultiplier += 1 },
                                    enabled = servingMultiplier < 100
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }

                        // Quick Scale Preset Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val presets = listOf(1 to "1 (Solo)", 2 to "2 (Couple)", 4 to "4 (Family)", 6 to "6 (Party)", 8 to "8 (Feast)", 12 to "12 (Crowd)", 20 to "20 (Catering)")
                            items(presets) { (count, label) ->
                                FilterChip(
                                    selected = servingMultiplier == count,
                                    onClick = { servingMultiplier = count },
                                    label = { Text(label, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }

                // Macronutrient Breakdown
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Nutritional Profile",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val proteinG = recipe.protein.filter { it.isDigit() }.toFloatOrNull() ?: 28f
                            val carbsG = recipe.carbs.filter { it.isDigit() }.toFloatOrNull() ?: 40f
                            val fatG = recipe.fat.filter { it.isDigit() }.toFloatOrNull() ?: 15f

                            Box(modifier = Modifier.weight(1f)) {
                                MacroItem(
                                    label = "Protein",
                                    value = recipe.protein,
                                    percent = proteinG / 60f,
                                    barColor = TerracottaPrimary,
                                    icon = Icons.Default.FitnessCenter
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MacroItem(
                                    label = "Carbs",
                                    value = recipe.carbs,
                                    percent = carbsG / 80f,
                                    barColor = AmberAccent,
                                    icon = Icons.Default.Grain
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                MacroItem(
                                    label = "Fat",
                                    value = recipe.fat,
                                    percent = fatG / 40f,
                                    barColor = OliveGreen,
                                    icon = Icons.Default.Spa
                                )
                            }
                        }
                    }
                }

                // Chef's Pro Tip Card
                if (recipe.chefTip.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AmberAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${recipe.chefInspiration}'s Pro Technique",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = recipe.chefTip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Ingredients Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 8.dp, shapeRadius = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = {
                                    viewModel.addIngredientsToShoppingList(recipe.ingredients, recipe.title)
                                    addedToCartMessage = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.LocalGroceryStore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add All to Cart", fontSize = 12.sp)
                            }
                        }

                        if (addedToCartMessage) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ingredients successfully added to your shopping list!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        // Split ingredients
                        val ingredientList = recipe.ingredients.split(",", "\n").map { it.trim() }.filter { it.isNotBlank() }
                        ingredientList.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.addShoppingItem(item, recipe.title)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Step-by-Step Instructions Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 8.dp, shapeRadius = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FormatListNumbered,
                                    contentDescription = null,
                                    tint = TerracottaPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Detailed Step-by-Step Instructions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Cookbook Edition",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        val instructionsList = recipe.instructions.lines().filter { it.isNotBlank() }
                        instructionsList.forEachIndexed { index, step ->
                            val cleanStep = step.removePrefix("${index + 1}.").removePrefix("${index + 1}:").trim()
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = TerracottaPrimary,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "STEP ${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TerracottaPrimary,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = cleanStep,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoPill(icon: ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        }
    }
}
