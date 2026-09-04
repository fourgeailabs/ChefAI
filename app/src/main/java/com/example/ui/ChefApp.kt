package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*

@Composable
fun ChefApp(viewModel: ChefViewModel = viewModel()) {
    val navController = rememberNavController()

    val showSplash by viewModel.showSplashScreen.collectAsStateWithLifecycle()
    val localeData by viewModel.localePricingData.collectAsStateWithLifecycle()
    val initStatus by viewModel.initializationStatus.collectAsStateWithLifecycle()
    val initProgress by viewModel.initializationProgress.collectAsStateWithLifecycle()
    val isReady by viewModel.isAppInitialized.collectAsStateWithLifecycle()

    if (showSplash) {
        SplashScreen(
            localeData = localeData,
            initializationStatus = initStatus,
            initializationProgress = initProgress,
            isReady = isReady,
            onFinishLoading = { viewModel.dismissSplashScreen() }
        )
    } else {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar = currentRoute in listOf("generate", "favorites", "shopping", "settings")

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        modifier = Modifier
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                                spotColor = Color(0xFFFF5722).copy(alpha = 0.25f)
                            )
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentRoute == "generate",
                            onClick = { navController.navigate("generate") { popUpTo("generate") } },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Discover") },
                            label = { Text("Discover", fontWeight = if (currentRoute == "generate") FontWeight.Bold else FontWeight.Normal) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "favorites",
                            onClick = { navController.navigate("favorites") { popUpTo("generate") } },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = "Saved") },
                            label = { Text("Cookbook", fontWeight = if (currentRoute == "favorites") FontWeight.Bold else FontWeight.Normal) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "shopping",
                            onClick = { navController.navigate("shopping") { popUpTo("generate") } },
                            icon = { Icon(Icons.Default.LocalGroceryStore, contentDescription = "Pantry") },
                            label = { Text("Pantry List", fontWeight = if (currentRoute == "shopping") FontWeight.Bold else FontWeight.Normal) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "settings",
                            onClick = { navController.navigate("settings") { popUpTo("generate") } },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text("Settings", fontWeight = if (currentRoute == "settings") FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "generate",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("generate") {
                    GenerateScreen(
                        viewModel = viewModel,
                        onRecipeClick = { recipeId ->
                            navController.navigate("recipe_detail/$recipeId")
                        }
                    )
                }
                composable("favorites") {
                    FavoritesScreen(
                        viewModel = viewModel,
                        onRecipeClick = { recipeId ->
                            navController.navigate("recipe_detail/$recipeId")
                        }
                    )
                }
                composable("shopping") {
                    ShoppingListScreen(viewModel = viewModel)
                }
                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateWhatsNew = { navController.navigate("whats_new") },
                        onNavigateAbout = { navController.navigate("about") }
                    )
                }
                composable("whats_new") {
                    WhatsNewScreen(onBack = { navController.popBackStack() })
                }
                composable("about") {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = "recipe_detail/{recipeId}",
                    arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
                    RecipeDetailScreen(
                        recipeId = recipeId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
