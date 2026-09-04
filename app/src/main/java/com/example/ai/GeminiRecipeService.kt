package com.example.ai

import com.example.data.CelebrityChefRegistry
import com.example.data.CookbookMealImageProvider
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GeneratedRecipe(
    val title: String,
    val prepTime: String,
    val cookTime: String,
    val calories: String,
    val protein: String,
    val carbs: String,
    val fat: String,
    val servings: Int,
    val difficulty: String,
    val chefTip: String,
    val ingredients: String,
    val instructions: String,
    val cuisine: String,
    val dietary: String,
    val chefInspiration: String,
    val craving: String,
    val chefQuote: String,
    val imageUrl: String = "",
    val platePresentation: String = ""
)

class GeminiRecipeService {
    suspend fun generateRecipe(
        chef: String,
        craving: String,
        ingredientsInput: String,
        dietary: String,
        cuisine: String,
        servings: Int = 4
    ): GeneratedRecipe = withContext(Dispatchers.IO) {
        val chefObj = CelebrityChefRegistry.getChefByName(chef)
        try {
            val model = Firebase.ai.generativeModel("gemini-2.5-flash")
            val prompt = """
                You are channeling the culinary genius and philosophy of celebrity master chef ${chefObj.name} (${chefObj.moniker}).
                Chef's Signature Technique: ${chefObj.signatureTechnique}
                Chef's Philosophy/Quote: "${chefObj.quote}"
                
                The home cook is craving: "$craving".
                Dietary Restriction / Preference: "$dietary".
                Preferred Cuisine Profile: "$cuisine".
                Ingredients the user has in the house right now: "$ingredientsInput".
                Target Yield / Portion Size: $servings portions (calculate ingredient amounts and nutritional facts specifically for $servings portions).
                
                Task: Create a mouthwatering, restaurant-worthy recipe that embodies ${chefObj.name}'s signature cooking style and technique, perfectly satisfies the craving for "$craving", complies strictly with the dietary preference "$dietary", scales ingredient portions appropriately for $servings people, and creatively utilizes the ingredients the user already has in the house (with basic pantry staples like salt, pepper, butter, oil, or spices as needed).
                
                Provide deep, highly detailed, step-by-step cooking instructions explaining exact pan temperatures, visual cues, searing times, resting advice, and seasoning checkpoints.
                Also provide a vivid cookbook plate presentation description of what the completed meal looks like plated from the chef's cookbook.
                
                Respond in this exact structured format:
                TITLE: [Appetizing Recipe Title in ${chefObj.name}'s style]
                PREP: [e.g. 15 mins]
                COOK: [e.g. 20 mins]
                CALORIES: [e.g. 480 kcal]
                PROTEIN: [e.g. 32g]
                CARBS: [e.g. 45g]
                FAT: [e.g. 16g]
                SERVINGS: $servings
                DIFFICULTY: [Easy / Medium / Master Chef]
                CHEF_TIP: [${chefObj.name}'s signature pro-tip for executing this dish flawlessly]
                CHEF_QUOTE: [An authentic, inspiring culinary quote in ${chefObj.name}'s voice]
                PLATE_PRESENTATION: [Vivid 1-2 sentence description of what the completed dish looks like plated from the chef's cookbook, including colors, sear crust, garnishes, and sauce gloss]
                INGREDIENTS: [Comma-separated ingredient list with measurements scaled for $servings portions based on what the user has in the house]
                INSTRUCTIONS:
                1. [Detailed Step 1 emphasizing ${chefObj.name}'s technique, heat level, and pan preparation]
                2. [Detailed Step 2 with sensory cues and cooking duration]
                3. [Detailed Step 3 with sauce development or basting]
                4. [Detailed Step 4 with doneness checking]
                5. [Detailed Step 5 with plating, resting, and garnish notes]
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: return@withContext getFallbackRecipe(chef, craving, ingredientsInput, dietary, cuisine, servings)

            parseResponse(text, chef, craving, cuisine, dietary, defaultServings = servings)
        } catch (e: Exception) {
            getFallbackRecipe(chef, craving, ingredientsInput, dietary, cuisine, servings)
        }
    }

    suspend fun searchRecipeWithGemini(
        query: String,
        preferredChef: String? = null,
        servings: Int = 4
    ): GeneratedRecipe = withContext(Dispatchers.IO) {
        // Detect if query mentions a specific chef
        val detectedChef = CelebrityChefRegistry.allChefs.find {
            query.contains(it.name, ignoreCase = true) ||
            query.contains(it.id, ignoreCase = true) ||
            (it.id == "guy_fieri" && (query.contains("guy", ignoreCase = true) || query.contains("flavortown", ignoreCase = true))) ||
            (it.id == "gordon_ramsay" && query.contains("ramsay", ignoreCase = true)) ||
            (it.id == "julia_child" && query.contains("julia", ignoreCase = true)) ||
            (it.id == "anthony_bourdain" && query.contains("bourdain", ignoreCase = true)) ||
            (it.id == "bobby_flay" && query.contains("flay", ignoreCase = true))
        }?.name ?: preferredChef ?: run {
            // Pick fitting chef based on food keywords
            val lower = query.lowercase()
            when {
                lower.contains("burger") || lower.contains("bbq") || lower.contains("nacho") || lower.contains("chili") || lower.contains("rib") || lower.contains("wings") -> "Guy Fieri"
                lower.contains("salmon") || lower.contains("scallop") || lower.contains("beef wellington") || lower.contains("wellington") || lower.contains("risotto") -> "Gordon Ramsay"
                lower.contains("bourguignon") || lower.contains("french") || lower.contains("coq au vin") || lower.contains("souffle") -> "Julia Child"
                lower.contains("taco") || lower.contains("fajita") || lower.contains("chipotle") || lower.contains("grill") || lower.contains("southwest") -> "Bobby Flay"
                lower.contains("steak") || lower.contains("bistro") || lower.contains("frites") || lower.contains("street food") -> "Anthony Bourdain"
                lower.contains("pasta") || lower.contains("30 min") || lower.contains("quick") || lower.contains("stoup") -> "Rachael Ray"
                lower.contains("gratin") || lower.contains("pie") || lower.contains("bake") || lower.contains("roast") -> "Martha Stewart"
                else -> "Guy Fieri"
            }
        }

        val chefObj = CelebrityChefRegistry.getChefByName(detectedChef)

        try {
            val model = Firebase.ai.generativeModel("gemini-2.5-flash")
            val prompt = """
                You are a world-class celebrity chef search engine and master recipe developer.
                The home cook searched for: "$query".
                You are channeling celebrity master chef ${chefObj.name} (${chefObj.moniker}).
                Chef's Signature Technique: ${chefObj.signatureTechnique}
                Chef's Philosophy/Quote: "${chefObj.quote}"
                
                Portion size: $servings servings.
                
                Task: Formulate an extraordinary, restaurant-caliber recipe for "$query" in ${chefObj.name}'s unmistakable style.
                Ensure step-by-step instructions are comprehensive and clear with cooking temperatures, visual indicators, basting cues, and plating tips.
                Describe what the completed meal looks like plated from the chef's cookbook.
                
                Respond in this exact structured format:
                TITLE: [Appetizing Recipe Title for "$query" in ${chefObj.name}'s style]
                PREP: [e.g. 15 mins]
                COOK: [e.g. 20 mins]
                CALORIES: [e.g. 520 kcal]
                PROTEIN: [e.g. 35g]
                CARBS: [e.g. 40g]
                FAT: [e.g. 18g]
                SERVINGS: $servings
                DIFFICULTY: [Easy / Medium / Master Chef]
                CHEF_TIP: [${chefObj.name}'s signature pro-tip for this dish]
                CHEF_QUOTE: [Inspiring quote in ${chefObj.name}'s voice]
                PLATE_PRESENTATION: [Vivid 1-2 sentence description of what the completed dish looks like plated from the chef's cookbook, including colors, sear crust, garnishes, and sauce gloss]
                INGREDIENTS: [Comma-separated ingredient list with measurements scaled for $servings portions]
                INSTRUCTIONS:
                1. [Detailed Step 1: Prep and pan heat setup]
                2. [Detailed Step 2: Searing / cooking technique with timing and heat level]
                3. [Detailed Step 3: Sauce / reduction / basting]
                4. [Detailed Step 4: Doneness check and resting]
                5. [Detailed Step 5: Master plating presentation and garnishing]
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: return@withContext getFallbackSearchResult(query, chefObj.name, servings)

            parseResponse(text, chefObj.name, craving = query, cuisine = "Chef Signature", dietary = "None", defaultServings = servings)
        } catch (e: Exception) {
            getFallbackSearchResult(query, chefObj.name, servings)
        }
    }

    private fun parseResponse(
        text: String,
        chef: String,
        craving: String,
        cuisine: String,
        dietary: String,
        defaultServings: Int = 4
    ): GeneratedRecipe {
        val chefObj = CelebrityChefRegistry.getChefByName(chef)
        var title = "${chefObj.name}'s Signature $cuisine Dish"
        var prepTime = "15 mins"
        var cookTime = "25 mins"
        var calories = "520 kcal"
        var protein = "34g"
        var carbs = "42g"
        var fat = "18g"
        var servings = defaultServings
        var difficulty = "Medium"
        var chefTip = chefObj.signatureTechnique
        var chefQuote = chefObj.quote
        var platePresentation = CookbookMealImageProvider.getCookbookAppearanceGuide(title, chefObj.name, craving)
        var ingredients = "Fresh pantry ingredients, olive oil, garlic, sea salt, cracked black pepper, fresh garden herbs"
        var instructions = "1. Prep and measure all fresh ingredients.\n2. Heat skillet over medium-high heat with extra virgin olive oil.\n3. Sauté aromatics until fragrant and golden.\n4. Simmer gently until flavors marry perfectly.\n5. Garnish with fresh herbs and serve piping hot."

        try {
            val lines = text.lines()
            val instIndex = lines.indexOfFirst { it.startsWith("INSTRUCTIONS", ignoreCase = true) }
            val headerLimit = if (instIndex >= 0) instIndex else lines.size

            for (i in 0 until headerLimit) {
                val line = lines[i].trim()
                when {
                    line.startsWith("TITLE:", ignoreCase = true) -> title = line.substringAfter(":").trim()
                    line.startsWith("PREP:", ignoreCase = true) -> prepTime = line.substringAfter(":").trim()
                    line.startsWith("COOK:", ignoreCase = true) -> cookTime = line.substringAfter(":").trim()
                    line.startsWith("CALORIES:", ignoreCase = true) -> calories = line.substringAfter(":").trim()
                    line.startsWith("PROTEIN:", ignoreCase = true) -> protein = line.substringAfter(":").trim()
                    line.startsWith("CARBS:", ignoreCase = true) -> carbs = line.substringAfter(":").trim()
                    line.startsWith("FAT:", ignoreCase = true) -> fat = line.substringAfter(":").trim()
                    line.startsWith("SERVINGS:", ignoreCase = true) -> {
                        val numStr = line.substringAfter(":").filter { it.isDigit() }
                        servings = numStr.toIntOrNull() ?: defaultServings
                    }
                    line.startsWith("DIFFICULTY:", ignoreCase = true) -> difficulty = line.substringAfter(":").trim()
                    line.startsWith("CHEF_TIP:", ignoreCase = true) -> chefTip = line.substringAfter(":").trim()
                    line.startsWith("CHEF_QUOTE:", ignoreCase = true) -> chefQuote = line.substringAfter(":").trim()
                    line.startsWith("PLATE_PRESENTATION:", ignoreCase = true) -> platePresentation = line.substringAfter(":").trim()
                    line.startsWith("INGREDIENTS:", ignoreCase = true) -> ingredients = line.substringAfter(":").trim()
                }
            }

            if (instIndex >= 0 && instIndex + 1 < lines.size) {
                val stepLines = lines.subList(instIndex + 1, lines.size).filter { it.isNotBlank() }
                if (stepLines.isNotEmpty()) {
                    instructions = stepLines.joinToString("\n").trim()
                }
            }
        } catch (_: Exception) {
        }

        if (platePresentation.isBlank()) {
            platePresentation = CookbookMealImageProvider.getCookbookAppearanceGuide(title, chefObj.name, craving)
        }

        val imageUrl = CookbookMealImageProvider.resolveMealImage(title, chefObj.name, craving)

        return GeneratedRecipe(
            title = title,
            prepTime = prepTime,
            cookTime = cookTime,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            servings = servings,
            difficulty = difficulty,
            chefTip = chefTip,
            ingredients = ingredients,
            instructions = instructions,
            cuisine = cuisine,
            dietary = dietary,
            chefInspiration = chefObj.name,
            craving = craving,
            chefQuote = chefQuote,
            imageUrl = imageUrl,
            platePresentation = platePresentation
        )
    }

    private fun getFallbackSearchResult(
        query: String,
        chefName: String,
        servings: Int
    ): GeneratedRecipe {
        val lower = query.lowercase()
        return when {
            lower.contains("guy") || lower.contains("fieri") || lower.contains("burger") || lower.contains("nacho") || lower.contains("bbq") || lower.contains("smash") -> {
                getFallbackRecipe("Guy Fieri", query, "Ground chuck, bacon, brioche buns, cheddar cheese", "None", "American Comfort", servings)
            }
            lower.contains("ramsay") || lower.contains("salmon") || lower.contains("wellington") || lower.contains("steak") -> {
                getFallbackRecipe("Gordon Ramsay", query, "Fresh salmon fillets, butter, garlic, rosemary", "None", "British Master", servings)
            }
            lower.contains("french") || lower.contains("julia") || lower.contains("bourguignon") || lower.contains("wine") -> {
                getFallbackRecipe("Julia Child", query, "Beef or chicken, red wine, butter, mushrooms, shallots", "None", "French Gastronomy", servings)
            }
            lower.contains("taco") || lower.contains("chipotle") || lower.contains("flay") || lower.contains("grill") -> {
                getFallbackRecipe("Bobby Flay", query, "Skirt steak or chicken, chipotle in adobo, limes, honey", "None", "Southwestern", servings)
            }
            lower.contains("bourdain") || lower.contains("bistro") || lower.contains("frites") -> {
                getFallbackRecipe("Anthony Bourdain", query, "Ribeye steak, potatoes, shallots, Dijon mustard, butter", "None", "Rustic Bistro", servings)
            }
            lower.contains("ray") || lower.contains("pasta") || lower.contains("stoup") -> {
                getFallbackRecipe("Rachael Ray", query, "Pasta, EVOO, crushed tomatoes, garlic, pecorino", "None", "Italian Comfort", servings)
            }
            else -> {
                getFallbackRecipe(chefName, query, "Pantry essentials, fresh protein, garlic, herbs", "None", "Chef Specialty", servings)
            }
        }
    }

    private fun getFallbackRecipe(
        chef: String,
        craving: String,
        ingredients: String,
        dietary: String,
        cuisine: String,
        servings: Int = 4
    ): GeneratedRecipe {
        val chefObj = CelebrityChefRegistry.getChefByName(chef)
        val cleanIng = if (ingredients.isNotBlank()) ingredients else "Chicken breast, garlic, olive oil, baby spinach, parmesan"

        return when (chefObj.id) {
            "guy_fieri" -> GeneratedRecipe(
                title = "Guy Fieri's Out-Of-Bounds Flavortown BBQ Bacon Smash Burger & Donkey Sauce",
                prepTime = "15 mins",
                cookTime = "12 mins",
                calories = "680 kcal",
                protein = "44g",
                carbs = "42g",
                fat = "38g",
                servings = servings,
                difficulty = "Medium",
                chefTip = "Smash those patties paper-thin onto a blistering smoking griddle to get those crispy, lacy caramel edges! And don't skimp on the real-deal Donkey Sauce on both toasted buns.",
                ingredients = "$cleanIng, 1.5 lbs Ground Chuck (80/20), 8 strips Crispy Applewood Smoked Bacon, 4 Brioche Buns (toasted), 4 slices Sharp Cheddar, 1/2 cup Mayonnaise, 1 tbsp Roasted Garlic Puree, 1 tsp Yellow Mustard, 1 tsp Worcestershire, 1/4 cup Smoky BBQ Glaze",
                instructions = "1. Whisk mayonnaise, roasted garlic puree, yellow mustard, and Worcestershire sauce in a bowl to build Guy's legendary Flavortown Donkey Sauce.\n" +
                        "2. Divide chilled ground beef into loose 3-ounce meatballs. Season aggressively with kosher salt and coarse black pepper.\n" +
                        "3. Heat a heavy flat-top cast iron griddle over maximum smoking heat. Butter and toast the brioche buns until golden amber; spread Donkey Sauce generously on top and bottom buns.\n" +
                        "4. Drop meatballs onto the blistering griddle. Using a heavy spatula, smash flat with firm downward pressure until thin with lacy outer edges.\n" +
                        "5. Sear undisturbed for 2 minutes until deeply crusted and charred. Scrape up the crust, flip, and immediately top with sharp cheddar and crispy bacon strips.\n" +
                        "6. Drizzle with smoky BBQ glaze, stack double patties onto prepared toasted buns, and serve hot with crinkle-cut fries!",
                cuisine = "American Comfort",
                dietary = dietary,
                chefInspiration = "Guy Fieri",
                craving = craving,
                chefQuote = "This is out of bounds! We're taking this righteous dish straight to Flavortown!",
                imageUrl = CookbookMealImageProvider.resolveMealImage("smash burger", "Guy Fieri", craving),
                platePresentation = "Glistening golden-toasted brioche crown with lacy, caramelized burger patty edges spilling out, molten sharp cheddar cheese pooling over smoky bacon strips, and an out-of-bounds drizzle of savory donkey sauce."
            )
            "gordon_ramsay" -> GeneratedRecipe(
                title = "Gordon Ramsay's Pan-Seared Crispy Garlic Herb Medallions",
                prepTime = "10 mins",
                cookTime = "15 mins",
                calories = "520 kcal",
                protein = "42g",
                carbs = "12g",
                fat = "26g",
                servings = servings,
                difficulty = "Master Chef",
                chefTip = "Get the skillet smoking hot. Once the protein hits the pan, do not move it for 3 minutes to guarantee that deep golden crust. Baste continuously with foaming butter, garlic, and rosemary.",
                ingredients = "$cleanIng, 3 tbsp Salted Butter, 4 cloves Crushed Garlic, 2 sprigs Fresh Rosemary, 1 tsp Flaky Sea Salt, Fresh Coarse Black Pepper",
                instructions = "1. Pat ingredients bone dry with paper towels; season both sides aggressively with coarse sea salt and cracked pepper.\n" +
                        "2. Heat a heavy cast iron skillet over high heat until shimmering. Add a splash of oil and lay the main protein in away from you.\n" +
                        "3. Sear undisturbed for 3–4 minutes until a deep golden mahogany crust develops.\n" +
                        "4. Flip over and immediately drop in whole butter, crushed garlic cloves, and rosemary sprigs.\n" +
                        "5. Tilt the skillet slightly and continuously spoon the foaming aromatic butter over the top for 2 minutes.\n" +
                        "6. Transfer to a warm board to rest for 5 minutes before slicing against the grain.",
                cuisine = cuisine,
                dietary = dietary,
                chefInspiration = "Gordon Ramsay",
                craving = craving,
                chefQuote = "Cook with passion! Wake up the pan, taste as you go, and season at every layer.",
                imageUrl = CookbookMealImageProvider.resolveMealImage("salmon steak", "Gordon Ramsay", craving),
                platePresentation = "Shatteringly crisp golden-seared crust with deep mahogany caramelization, glistening in frothing garlic-rosemary herb butter and scattered with flaky sea salt crystals."
            )
            "julia_child" -> GeneratedRecipe(
                title = "Julia Child's Classical French Red Wine & Herb Sauté",
                prepTime = "15 mins",
                cookTime = "30 mins",
                calories = "560 kcal",
                protein = "36g",
                carbs = "22g",
                fat = "32g",
                servings = servings,
                difficulty = "Medium",
                chefTip = "Never crowd the mushrooms or vegetables in the pan, or they will steam rather than brown. And remember: butter is not the enemy, it is the soul of French sauce-making.",
                ingredients = "$cleanIng, 4 tbsp French Butter, 1/2 cup Dry Red Wine (or broth), 1 tbsp Tomato Paste, 2 cloves Minced Garlic, Fresh Thyme, Pinch of Nutmeg",
                instructions = "1. Dry your ingredients thoroughly so they brown gloriously when meeting hot French butter.\n" +
                        "2. Melt 2 tablespoons of butter in a wide sauté pan over medium-high heat until the foaming subsides.\n" +
                        "3. Brown the ingredients in batches until beautifully caramelized; remove and set aside on a warm plate.\n" +
                        "4. Add minced garlic and tomato paste to the pan, stirring for 1 minute until fragrant.\n" +
                        "5. Pour in the red wine, scraping up every delicious browned bit from the bottom of the pan; reduce by half.\n" +
                        "6. Whisk in the remaining cold butter to create a silky, glossy reduction sauce. Return ingredients to pan, toss gently, and serve with fresh thyme.",
                cuisine = "French",
                dietary = dietary,
                chefInspiration = "Julia Child",
                craving = craving,
                chefQuote = "Remember, no one's watching: bring on the butter and cook with joyful abandon!",
                imageUrl = CookbookMealImageProvider.resolveMealImage("braise wine", "Julia Child", craving),
                platePresentation = "Velvety, dark mahogany French wine glaze coating fork-tender caramelized cuts, garnished with fresh thyme sprigs and glistening with cold butter emulsion."
            )
            "anthony_bourdain" -> GeneratedRecipe(
                title = "Anthony Bourdain's Rustic Bistro Pan-Roast & Shallot Jus",
                prepTime = "12 mins",
                cookTime = "22 mins",
                calories = "580 kcal",
                protein = "40g",
                carbs = "28g",
                fat = "30g",
                servings = servings,
                difficulty = "Medium",
                chefTip = "Don't overcomplicate good food. Respect the ingredients, embrace the pan drippings, and finish with good Dijon mustard and cold butter.",
                ingredients = "$cleanIng, 2 Shallots (sliced), 1 tbsp Dijon Mustard, 1/3 cup Rich Stock, 2 tbsp Butter, Coarse Sea Salt, Chopped Flat-Leaf Parsley",
                instructions = "1. Season your protein heavily with coarse salt and black pepper—don't be shy.\n" +
                        "2. Get your pan genuinely hot with olive oil and sear hard until deeply browned and caramelized on all sides.\n" +
                        "3. Toss in sliced shallots around the perimeter and let them soften and sweeten in the rendered drippings.\n" +
                        "4. Deglaze the hot pan with stock and a dollop of sharp Dijon mustard, swirling vigorously to create a soul-satisfying bistro jus.\n" +
                        "5. Pull off heat, swirl in a knob of cold butter, scatter flat-leaf parsley, and serve with rustic bread or pan potatoes.",
                cuisine = cuisine,
                dietary = dietary,
                chefInspiration = "Anthony Bourdain",
                craving = craving,
                chefQuote = "Good food is very often, even most of the time, simple, authentic food with soul.",
                imageUrl = CookbookMealImageProvider.resolveMealImage("steak frites", "Anthony Bourdain", craving),
                platePresentation = "Deep dark cast iron sear with sweet golden caramelized shallots pooling in rich Dijon pan juices, showered with fresh chopped flat-leaf parsley."
            )
            "martha_stewart" -> GeneratedRecipe(
                title = "Martha Stewart's Elegant Farmhouse Herb Roasted Skillet",
                prepTime = "15 mins",
                cookTime = "25 mins",
                calories = "470 kcal",
                protein = "34g",
                carbs = "32g",
                fat = "18g",
                servings = servings,
                difficulty = "Medium",
                chefTip = "Uniform slicing ensures every piece cooks evenly. Finish with a bright squeeze of Meyer lemon and fresh garden herbs to elevate the natural sweetness.",
                ingredients = "$cleanIng, 2 tbsp Extra Virgin Olive Oil, 1 Lemon (zested and juiced), 1 tbsp Fresh Tarragon & Parsley, 1 tsp Flaky Sea Salt, 1 tbsp Honey",
                instructions = "1. Preheat oven to 400°F (200°C). Uniformly slice ingredients to ensure pristine, even roasting.\n" +
                        "2. In a ceramic roasting pan, toss ingredients with cold-pressed olive oil, sea salt, fresh cracked pepper, and lemon zest.\n" +
                        "3. Roast for 18–20 minutes until edges turn golden amber and tender.\n" +
                        "4. In a small bowl, whisk lemon juice with a touch of honey and chopped tarragon.\n" +
                        "5. Drizzle the warm herb-citrus vinaigrette over the roasted skillet and garnish with microgreens or flat parsley.",
                cuisine = cuisine,
                dietary = dietary,
                chefInspiration = "Martha Stewart",
                craving = craving,
                chefQuote = "Do what you love, and do it with flawless, foolproof scratch technique.",
                imageUrl = CookbookMealImageProvider.resolveMealImage("roast gratin", "Martha Stewart", craving),
                platePresentation = "Immaculately roasted golden amber edges with vibrant lemon-herb glaze, served in white farmhouse porcelain with tender garden microgreens."
            )
            "rachael_ray" -> GeneratedRecipe(
                title = "Rachael Ray's 30-Minute Cozy Pan Stoup & Crispy Dippers",
                prepTime = "8 mins",
                cookTime = "20 mins",
                calories = "490 kcal",
                protein = "30g",
                carbs = "44g",
                fat = "16g",
                servings = servings,
                difficulty = "Easy",
                chefTip = "Two turns of the pan with EVOO (Extra Virgin Olive Oil) creates your flavorful sauté base. Grate the garlic right over the hot pan so it melts without burning!",
                ingredients = "$cleanIng, 2 tbsp EVOO, 3 cloves Grated Garlic, 1 can Crushed Tomatoes or Broth, 1/2 tsp Crushed Red Pepper, Fresh Basil, Grated Pecorino Romano",
                instructions = "1. Heat a large skillet over medium-high heat with two turns of the pan with EVOO.\n" +
                        "2. Add your chopped pantry ingredients and grated garlic; season with salt, pepper, and a pinch of red pepper flakes.\n" +
                        "3. Sauté for 6–8 minutes until aromatics are tender and fragrant.\n" +
                        "4. Pour in crushed tomatoes or broth, bring to a rapid bubble, then reduce to medium-low to simmer into a luscious 'stoup' consistency.\n" +
                        "5. Tear in plenty of fresh basil and top generously with freshly grated Pecorino Romano cheese.\n" +
                        "6. Serve hot in wide bowls with crusty garlic bread for dipping.",
                cuisine = cuisine,
                dietary = dietary,
                chefInspiration = "Rachael Ray",
                craving = craving,
                chefQuote = "Yum-O! Cook smart, cook fast, and make every 30-minute dish a flavor bomb!",
                imageUrl = CookbookMealImageProvider.resolveMealImage("pasta soup", "Rachael Ray", craving),
                platePresentation = "Vibrant crimson bubbling stoup served in deep rustic bowls, crowned with fluffy mounds of freshly grated Pecorino Romano and torn emerald basil."
            )
            else -> GeneratedRecipe(
                title = "Bobby Flay's Smoky Chipotle & Charred Citrus Sizzle",
                prepTime = "12 mins",
                cookTime = "18 mins",
                calories = "530 kcal",
                protein = "38g",
                carbs = "34g",
                fat = "22g",
                servings = servings,
                difficulty = "Medium",
                chefTip = "Don't be afraid of the char! High heat caramelizes natural sugars and adds signature smokiness. Always balance chile heat with vibrant fresh lime juice and a touch of honey.",
                ingredients = "$cleanIng, 1 tbsp Chipotle in Adobo (minced), 2 Limes (juiced), 2 tbsp Honey, 2 tbsp Olive Oil, 1 tsp Ground Cumin, Fresh Cilantro, Sea Salt",
                instructions = "1. In a bowl, whisk minced chipotle, honey, lime juice, ground cumin, and olive oil into a vibrant smoky marinade.\n" +
                        "2. Toss your core ingredients in the marinade to coat thoroughly.\n" +
                        "3. Fire up a cast iron grill pan or heavy skillet over high heat until smoking.\n" +
                        "4. Sear the ingredients vigorously for 4–5 minutes per side until deeply charred and caramelized.\n" +
                        "5. Remove to a warm platter, squeeze fresh lime juice over the top, and shower with chopped fresh cilantro and sea salt.\n" +
                        "6. Serve with warm tortillas, charred corn salsa, or rice.",
                cuisine = "Southwestern",
                dietary = dietary,
                chefInspiration = "Bobby Flay",
                craving = craving,
                chefQuote = "Layer the bold flavors! Char the chiles, hit it with citrus, and master the heat.",
                imageUrl = CookbookMealImageProvider.resolveMealImage("tacos chipotle", "Bobby Flay", craving),
                platePresentation = "Deep smoky char-grilled edges drizzled with glistening amber chipotle-honey glaze, brightened by vibrant fresh cilantro and charred lime wedges."
            )
        }
    }
}
