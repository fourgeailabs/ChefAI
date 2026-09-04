package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiRecipeService
import com.example.ai.GeminiVisionService
import com.example.ai.GeneratedRecipe
import com.example.ai.VisualIngredientRecognitionResult
import com.example.data.BarcodeProductRegistry
import com.example.data.CelebrityChefRegistry
import com.example.data.ChefDatabase
import com.example.data.LocalePricingData
import com.example.data.LocalePricingManager
import com.example.data.PantryItemEntity
import com.example.data.RecipeEntity
import com.example.data.ScannedProduct
import com.example.data.ShoppingItemEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppThemeMode(val title: String, val subtitle: String) {
    SYSTEM("System Default", "Follows device light/dark appearance"),
    LIGHT("Light Mode", "Warm culinary canvas with crisp high-contrast styling"),
    DARK("Dark Mode", "Rich charcoal & terracotta dark gourmet aesthetic")
}

data class GenerationProgressState(
    val isGenerating: Boolean = false,
    val progress: Float = 0f,
    val stageMessage: String = "",
    val chefTipPreview: String = "",
    val chefName: String = ""
)

class ChefViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("chefai_settings", Context.MODE_PRIVATE)
    private val dao = ChefDatabase.getDatabase(application).chefDao()
    private val geminiService = GeminiRecipeService()
    private val geminiVisionService = GeminiVisionService()
    private val localePricingManager = LocalePricingManager(application)

    private val _themeMode = MutableStateFlow(
        try {
            val saved = prefs.getString("theme_mode", AppThemeMode.SYSTEM.name)
            AppThemeMode.valueOf(saved ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    // Locale & GPS Local Pricing Intelligence State
    private val _localePricingData = MutableStateFlow(LocalePricingData())
    val localePricingData: StateFlow<LocalePricingData> = _localePricingData.asStateFlow()

    private val _initializationStatus = MutableStateFlow("Connecting to cloud services...")
    val initializationStatus: StateFlow<String> = _initializationStatus.asStateFlow()

    private val _initializationProgress = MutableStateFlow(0.15f)
    val initializationProgress: StateFlow<Float> = _initializationProgress.asStateFlow()

    private val _isAppInitialized = MutableStateFlow(false)
    val isAppInitialized: StateFlow<Boolean> = _isAppInitialized.asStateFlow()

    private val _showSplashScreen = MutableStateFlow(true)
    val showSplashScreen: StateFlow<Boolean> = _showSplashScreen.asStateFlow()

    fun dismissSplashScreen() {
        _showSplashScreen.value = false
    }

    fun refreshLocalePricing() {
        viewModelScope.launch {
            _initializationStatus.value = "Updating GPS & regional price index..."
            _initializationProgress.value = 0.4f
            val data = localePricingManager.resolveLocaleAndPricing { stage, prog ->
                _initializationStatus.value = stage
                _initializationProgress.value = prog
            }
            _localePricingData.value = data
            _initializationStatus.value = "Market pricing updated!"
            _initializationProgress.value = 1.0f
        }
    }

    val allRecipes: StateFlow<List<RecipeEntity>> = dao.getAllRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRecipes: StateFlow<List<RecipeEntity>> = dao.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItems: StateFlow<List<ShoppingItemEntity>> = dao.getAllShoppingItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pantryItems: StateFlow<List<PantryItemEntity>> = dao.getAllPantryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _progressState = MutableStateFlow(GenerationProgressState())
    val progressState: StateFlow<GenerationProgressState> = _progressState.asStateFlow()

    init {
        // Run background app startup: network check, GPS location & local market pricing index
        viewModelScope.launch {
            _initializationStatus.value = "Connecting to internet services..."
            _initializationProgress.value = 0.25f
            delay(300)

            val pricing = localePricingManager.resolveLocaleAndPricing { stage, progress ->
                _initializationStatus.value = stage
                _initializationProgress.value = progress
            }
            _localePricingData.value = pricing
            delay(200)

            _initializationStatus.value = "Culinary AI & Master Chef Personas Ready!"
            _initializationProgress.value = 1.0f
            _isAppInitialized.value = true
        }
        // Populate initial showcase recipes from iconic master chefs if database is clean
        viewModelScope.launch {
            val existing = dao.getRecipeById(1L)
            if (existing == null) {
                dao.insertRecipe(
                    RecipeEntity(
                        title = "Gordon Ramsay's Pan-Seared Crispy Skin Salmon",
                        ingredients = "2 Salmon Fillets (6oz each), 3 tbsp Salted Butter, 3 cloves Crushed Garlic, 2 sprigs Fresh Rosemary, 1 cup Cherry Tomatoes (halved), 2 cups Fresh Baby Spinach, 1 tbsp Extra Virgin Olive Oil, 1 tsp Flaky Sea Salt, 1/2 tsp Coarse Black Pepper",
                        instructions = "1. Pat salmon fillets dry and score skin lightly; season both sides with coarse sea salt and cracked pepper.\n2. Heat olive oil in a heavy cast iron skillet over high heat until shimmering. Lay salmon skin-side down away from you.\n3. Sear undisturbed for 4 minutes until the skin is shatteringly crisp and golden.\n4. Flip salmon over and drop in butter, crushed garlic, and rosemary sprigs. Tilt pan and continuously baste for 2 minutes.\n5. Toss cherry tomatoes and baby spinach into the aromatic pan butter until wilted.\n6. Plate salmon over wilted greens and spoon foaming pan butter over top.",
                        prepTime = "10 mins",
                        cookTime = "15 mins",
                        calories = "520 kcal",
                        protein = "42g",
                        carbs = "8g",
                        fat = "28g",
                        servings = 2,
                        difficulty = "Master Chef",
                        chefTip = "Keep the pan hot and don't move the fish for 4 minutes—that's how you unlock restaurant-crisp skin!",
                        cuisine = "Mediterranean",
                        dietary = "Keto",
                        chefInspiration = "Gordon Ramsay",
                        craving = "Crispy & Savory",
                        chefQuote = "Cook with passion! Wake up the pan, taste as you go, and season at every layer.",
                        isFavorite = true
                    )
                )
                dao.insertRecipe(
                    RecipeEntity(
                        title = "Julia Child's Classical French Red Wine Braised Skillet",
                        ingredients = "1.5 lbs Boneless Chicken Thighs (or Beef), 4 tbsp French Butter, 1/2 cup Dry Red Wine, 1 cup Sliced Cremini Mushrooms, 2 Shallots (minced), 2 cloves Garlic (minced), 1 tbsp Tomato Paste, Fresh Thyme Sprigs, Sea Salt, Cracked Pepper",
                        instructions = "1. Dry the protein thoroughly with paper towels so it browns richly.\n2. Melt 2 tablespoons butter in a wide skillet over medium-high heat until foaming ceases; sear protein on all sides until golden brown.\n3. Remove protein; add mushrooms and shallots to pan, cooking until caramelized.\n4. Stir in tomato paste and garlic for 1 minute until fragrant.\n5. Deglaze pan with dry red wine, scraping up all browned fond; simmer until reduced by half.\n6. Return protein to pan, reduce heat to low, cover, and gently simmer for 15 minutes. Swirl in remaining cold butter for a velvety finish.",
                        prepTime = "15 mins",
                        cookTime = "25 mins",
                        calories = "540 kcal",
                        protein = "38g",
                        carbs = "14g",
                        fat = "30g",
                        servings = 4,
                        difficulty = "Medium",
                        chefTip = "Never crowd the mushrooms in the pan, or they will steam instead of browning gloriously!",
                        cuisine = "French",
                        dietary = "None",
                        chefInspiration = "Julia Child",
                        craving = "Rich & Decadent",
                        chefQuote = "Remember, no one's watching: bring on the butter and cook with joyful abandon!",
                        isFavorite = true
                    )
                )
                dao.insertRecipe(
                    RecipeEntity(
                        title = "Bobby Flay's Charred Chipotle Honey Glazed Skillet",
                        ingredients = "1 lb Chicken Breast or Flank Steak (sliced), 1 tbsp Chipotle in Adobo (minced), 2 tbsp Honey, 2 Fresh Limes (juiced), 2 tbsp Olive Oil, 1 tsp Cumin, 1 Bell Pepper (sliced), 1 Red Onion (sliced), 1/4 cup Fresh Chopped Cilantro, Sea Salt",
                        instructions = "1. Whisk minced chipotle, honey, lime juice, olive oil, and cumin in a bowl to create a smoky glaze.\n2. Toss sliced protein with half the glaze to coat.\n3. Heat a cast iron skillet over high heat until smoking hot.\n4. Sear protein and sliced peppers/onions for 5–6 minutes until deeply charred and caramelized.\n5. Drizzle with remaining chipotle honey glaze in the last minute of cooking to caramelize.\n6. Finish with fresh lime squeeze and a shower of chopped cilantro.",
                        prepTime = "10 mins",
                        cookTime = "15 mins",
                        calories = "480 kcal",
                        protein = "36g",
                        carbs = "28g",
                        fat = "18g",
                        servings = 3,
                        difficulty = "Medium",
                        chefTip = "Don't fear the char! The smoky blackened edges paired with sweet honey and bright lime is where pure flavor lives.",
                        cuisine = "Southwestern",
                        dietary = "High Protein",
                        chefInspiration = "Bobby Flay",
                        craving = "Smoky & Spicy",
                        chefQuote = "Layer the bold flavors! Char the chiles, hit it with citrus, and master the heat.",
                        imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=900&q=85",
                        platePresentation = "Deep smoky char-grilled edges drizzled with glistening amber chipotle-honey glaze, brightened by vibrant fresh cilantro and charred lime wedges.",
                        isFavorite = true
                    )
                )
                dao.insertRecipe(
                    RecipeEntity(
                        title = "Guy Fieri's Out-Of-Bounds Flavortown BBQ Bacon Smash Burger & Donkey Sauce",
                        ingredients = "1.5 lbs Ground Chuck (80/20), 8 strips Crispy Applewood Smoked Bacon, 4 Brioche Buns (toasted), 4 slices Sharp Cheddar, 1/2 cup Mayonnaise, 1 tbsp Roasted Garlic Puree, 1 tsp Yellow Mustard, 1 tsp Worcestershire, 1/4 cup Smoky BBQ Glaze, Kosher Salt, Coarse Black Pepper",
                        instructions = "1. Whisk mayonnaise, roasted garlic puree, yellow mustard, and Worcestershire sauce in a bowl to build Guy's legendary Flavortown Donkey Sauce.\n2. Divide chilled ground beef into loose 3-ounce meatballs. Season aggressively with kosher salt and coarse black pepper.\n3. Heat a heavy flat-top cast iron griddle over maximum smoking heat. Butter and toast brioche buns until golden amber; spread Donkey Sauce generously on both buns.\n4. Drop meatballs onto the blistering griddle. Using a heavy spatula, smash flat with firm downward pressure until thin with lacy outer edges.\n5. Sear undisturbed for 2 minutes until deeply crusted and charred. Scrape up the crust, flip, and immediately top with sharp cheddar and crispy bacon strips.\n6. Drizzle with smoky BBQ glaze, stack double patties onto prepared toasted buns, and serve hot with crinkle-cut fries!",
                        prepTime = "15 mins",
                        cookTime = "12 mins",
                        calories = "680 kcal",
                        protein = "44g",
                        carbs = "42g",
                        fat = "38g",
                        servings = 4,
                        difficulty = "Medium",
                        chefTip = "Smash those patties paper-thin onto a blistering smoking griddle to get those crispy, lacy caramel edges! And don't skimp on the real-deal Donkey Sauce on both toasted buns.",
                        cuisine = "American Comfort",
                        dietary = "None",
                        chefInspiration = "Guy Fieri",
                        craving = "Crispy & Savory",
                        chefQuote = "This is out of bounds! We're taking this righteous dish straight to Flavortown!",
                        imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=900&q=85",
                        platePresentation = "Glistening golden-toasted brioche crown with lacy, caramelized burger patty edges spilling out, molten sharp cheddar cheese pooling over smoky bacon strips, and an out-of-bounds drizzle of savory donkey sauce.",
                        isFavorite = true
                    )
                )
            }
        }
    }

    fun searchAndSaveRecipe(
        query: String,
        preferredChef: String? = null,
        servings: Int = 4,
        onComplete: (Long) -> Unit
    ) {
        viewModelScope.launch {
            _progressState.value = GenerationProgressState(
                isGenerating = true,
                progress = 0.20f,
                stageMessage = "Searching culinary archives & master cookbooks for '$query'...",
                chefTipPreview = "Consulting Master Chefs...",
                chefName = preferredChef ?: "Master Chef"
            )

            delay(350)
            _progressState.value = _progressState.value.copy(
                progress = 0.55f,
                stageMessage = "Developing chef techniques, measurements & step-by-step instructions...",
                chefTipPreview = "Formulating perfect flavor balance..."
            )

            val generated = geminiService.searchRecipeWithGemini(
                query = query,
                preferredChef = preferredChef,
                servings = servings
            )

            _progressState.value = _progressState.value.copy(
                progress = 0.85f,
                stageMessage = "Finalizing ${generated.chefInspiration}'s presentation & cookbook guide...",
                chefTipPreview = generated.chefTip,
                chefName = generated.chefInspiration
            )

            delay(300)
            _progressState.value = _progressState.value.copy(
                progress = 1.0f,
                stageMessage = "Your master cookbook recipe is ready!"
            )

            val recipeEntity = RecipeEntity(
                title = generated.title,
                ingredients = generated.ingredients,
                instructions = generated.instructions,
                prepTime = generated.prepTime,
                cookTime = generated.cookTime,
                calories = generated.calories,
                protein = generated.protein,
                carbs = generated.carbs,
                fat = generated.fat,
                servings = generated.servings,
                difficulty = generated.difficulty,
                chefTip = generated.chefTip,
                cuisine = generated.cuisine,
                dietary = generated.dietary,
                chefInspiration = generated.chefInspiration,
                craving = generated.craving,
                chefQuote = generated.chefQuote,
                imageUrl = generated.imageUrl,
                platePresentation = generated.platePresentation,
                isFavorite = true
            )
            val id = dao.insertRecipe(recipeEntity)
            delay(200)
            _progressState.value = GenerationProgressState(isGenerating = false)
            onComplete(id)
        }
    }

    fun generateAndSaveRecipe(
        chef: String,
        craving: String,
        ingredients: String,
        dietary: String,
        cuisine: String,
        servings: Int = 4,
        onComplete: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val chefObj = CelebrityChefRegistry.getChefByName(chef)

            _progressState.value = GenerationProgressState(
                isGenerating = true,
                progress = 0.15f,
                stageMessage = "Channeling ${chefObj.name}'s culinary philosophy for $servings portion(s)...",
                chefTipPreview = chefObj.quote,
                chefName = chefObj.name
            )

            delay(400)
            _progressState.value = _progressState.value.copy(
                progress = 0.45f,
                stageMessage = "Synthesizing pantry ingredients for '$craving' craving ($servings portions)...",
                chefTipPreview = "Master Technique: ${chefObj.signatureTechnique}"
            )

            val generated = geminiService.generateRecipe(
                chef = chef,
                craving = craving,
                ingredientsInput = ingredients,
                dietary = dietary,
                cuisine = cuisine,
                servings = servings
            )

            _progressState.value = _progressState.value.copy(
                progress = 0.80f,
                stageMessage = "Balancing ${chefObj.name}'s flavor profile & step timings...",
                chefTipPreview = generated.chefTip
            )

            delay(350)
            _progressState.value = _progressState.value.copy(
                progress = 1.0f,
                stageMessage = "Plating your ${chefObj.name} signature creation!"
            )

            val recipeEntity = RecipeEntity(
                title = generated.title,
                ingredients = generated.ingredients,
                instructions = generated.instructions,
                prepTime = generated.prepTime,
                cookTime = generated.cookTime,
                calories = generated.calories,
                protein = generated.protein,
                carbs = generated.carbs,
                fat = generated.fat,
                servings = generated.servings,
                difficulty = generated.difficulty,
                chefTip = generated.chefTip,
                cuisine = generated.cuisine,
                dietary = generated.dietary,
                chefInspiration = generated.chefInspiration,
                craving = generated.craving,
                chefQuote = generated.chefQuote,
                imageUrl = generated.imageUrl,
                platePresentation = generated.platePresentation,
                isFavorite = true
            )
            val id = dao.insertRecipe(recipeEntity)
            delay(200)
            _progressState.value = GenerationProgressState(isGenerating = false)
            onComplete(id)
        }
    }

    fun buildAndSaveCustomRecipe(recipe: RecipeEntity, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = dao.insertRecipe(recipe)
            onComplete(id)
        }
    }

    fun updateRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            dao.updateRecipe(recipe)
        }
    }

    fun toggleFavorite(recipe: RecipeEntity) {
        viewModelScope.launch {
            dao.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            dao.deleteRecipe(recipe)
        }
    }

    fun addShoppingItem(itemName: String, recipeTitle: String = "Custom") {
        if (itemName.isBlank()) return
        viewModelScope.launch {
            dao.insertShoppingItem(ShoppingItemEntity(itemName = itemName.trim(), recipeTitle = recipeTitle))
        }
    }

    fun toggleShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            dao.updateShoppingItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            dao.deleteShoppingItem(item)
        }
    }

    fun addIngredientsToShoppingList(ingredients: String, recipeTitle: String) {
        viewModelScope.launch {
            val items = ingredients.split(",", "\n").map { it.trim() }.filter { it.isNotBlank() }
            for (item in items) {
                dao.insertShoppingItem(ShoppingItemEntity(itemName = item, recipeTitle = recipeTitle))
            }
        }
    }

    fun clearShoppingList() {
        viewModelScope.launch {
            dao.clearShoppingList()
        }
    }

    // Pantry Management
    fun addPantryItem(name: String, barcode: String = "", brand: String = "", category: String = "Pantry") {
        if (name.isBlank()) return
        viewModelScope.launch {
            dao.insertPantryItem(
                com.example.data.PantryItemEntity(
                    name = name.trim(),
                    barcode = barcode.trim(),
                    brand = brand.trim(),
                    category = category.trim()
                )
            )
        }
    }

    fun deletePantryItem(item: com.example.data.PantryItemEntity) {
        viewModelScope.launch {
            dao.deletePantryItem(item)
        }
    }

    fun clearPantry() {
        viewModelScope.launch {
            dao.clearPantry()
        }
    }

    // Barcode Scanner Engine: Lookup, Add to Pantry, and Auto-Check Shopping List
    fun processBarcode(
        barcode: String,
        autoAddToPantry: Boolean = true,
        autoCheckShoppingList: Boolean = true,
        onProcessed: (com.example.data.ScannedProduct, List<ShoppingItemEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val product = com.example.data.BarcodeProductRegistry.lookupProduct(barcode)
            
            // 1. Add to Pantry if requested
            if (autoAddToPantry) {
                dao.insertPantryItem(
                    com.example.data.PantryItemEntity(
                        name = product.name,
                        barcode = product.barcode,
                        brand = product.brand,
                        category = product.category
                    )
                )
            }

            // 2. Find and check off matching shopping items
            val currentShopping = shoppingItems.value
            val currentNames = currentShopping.map { it.itemName }
            val matchedNames = com.example.data.BarcodeProductRegistry.findMatchingShoppingItems(product.name, currentNames)
            
            val matchedEntities = currentShopping.filter { it.itemName in matchedNames }

            if (autoCheckShoppingList && matchedEntities.isNotEmpty()) {
                for (item in matchedEntities) {
                    if (!item.isChecked) {
                        dao.updateShoppingItem(item.copy(isChecked = true))
                    }
                }
            }

            val productWithMatches = product.copy(matchedShoppingItems = matchedNames)
            onProcessed(productWithMatches, matchedEntities)
        }
    }

    // Visual AI Ingredient Recognition Engine (Images NEVER saved to disk)
    fun identifyVisualIngredient(
        bitmap: Bitmap,
        onResult: (VisualIngredientRecognitionResult, List<ShoppingItemEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val result = geminiVisionService.identifyIngredient(bitmap)
            
            // Look up matching shopping list items for UI verification prompt
            val currentShopping = shoppingItems.value
            val currentNames = currentShopping.map { it.itemName }
            val matchedNames = BarcodeProductRegistry.findMatchingShoppingItems(result.ingredientName, currentNames)
            val matchedEntities = currentShopping.filter { it.itemName in matchedNames }

            onResult(result, matchedEntities)
        }
    }

    // Confirmation step for AI Discovered ingredient
    fun confirmVisualIngredient(
        ingredientName: String,
        category: String = "Produce",
        autoAddToPantry: Boolean = true,
        autoCheckShoppingList: Boolean = true,
        onConfirmed: (List<ShoppingItemEntity>) -> Unit
    ) {
        if (ingredientName.isBlank()) return
        viewModelScope.launch {
            // 1. Add to Pantry Inventory
            if (autoAddToPantry) {
                dao.insertPantryItem(
                    PantryItemEntity(
                        name = ingredientName.trim(),
                        barcode = "",
                        brand = "Fresh / Non-Barcode",
                        category = category.trim()
                    )
                )
            }

            // 2. Auto check off matching shopping list items
            val currentShopping = shoppingItems.value
            val currentNames = currentShopping.map { it.itemName }
            val matchedNames = BarcodeProductRegistry.findMatchingShoppingItems(ingredientName.trim(), currentNames)
            val matchedEntities = currentShopping.filter { it.itemName in matchedNames }

            if (autoCheckShoppingList && matchedEntities.isNotEmpty()) {
                for (item in matchedEntities) {
                    if (!item.isChecked) {
                        dao.updateShoppingItem(item.copy(isChecked = true))
                    }
                }
            }

            onConfirmed(matchedEntities)
        }
    }
}
