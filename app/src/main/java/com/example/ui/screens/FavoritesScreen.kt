package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CelebrityChefRegistry
import com.example.ui.ChefViewModel
import com.example.ui.components.gourmetDepth
import com.example.ui.theme.TerracottaPrimary

@Composable
fun FavoritesScreen(
    viewModel: ChefViewModel,
    onRecipeClick: (Long) -> Unit
) {
    val favorites by viewModel.favoriteRecipes.collectAsState()
    var selectedChefFilter by remember { mutableStateOf("All Chefs") }

    val chefFilters = listOf("All Chefs") + CelebrityChefRegistry.allChefs.map { it.name }
    val filteredRecipes = if (selectedChefFilter == "All Chefs") {
        favorites
    } else {
        favorites.filter { it.chefInspiration.equals(selectedChefFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Saved Cookbook",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${filteredRecipes.size} curated master chef creations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chef Filter Tabs
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chefFilters) { chefName ->
                FilterChip(
                    selected = selectedChefFilter == chefName,
                    onClick = { selectedChefFilter = chefName },
                    label = { Text(chefName, fontSize = 12.sp) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredRecipes.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .gourmetDepth(elevation = 8.dp, shapeRadius = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedChefFilter == "All Chefs") "No Saved Recipes Yet" else "No Saved Recipes from $selectedChefFilter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Star recipes generated by master chefs to curate your personal gourmet cookbook offline.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredRecipes, key = { it.id }) { recipe ->
                    RecipeFeedCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(recipe) }
                    )
                }
            }
        }
    }
}
