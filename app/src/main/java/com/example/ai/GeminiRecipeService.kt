package com.example.ai

import com.example.data.CelebrityChefRegistry
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
    val chefQuote: String
)

class GeminiRecipeService {
    suspend fun generateRecipe(
        chef: String,
        craving: String,
        ingredientsInput: String,
        dietary: String,
        cuisine: String
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
                
                Task: Create a mouthwatering, restaurant-worthy recipe that embodies ${chefObj.name}'s signature cooking style and technique, perfectly satisfies the craving for "$craving", complies strictly with the dietary preference "$dietary", and creatively utilizes the ingredients the user already has in the house (with basic pantry staples like salt, pepper, butter, oil, or spices as needed).
                
                Respond in this exact structured format:
                TITLE: [Appetizing Recipe Title in ${chefObj.name}'s style]
                PREP: [e.g. 15 mins]
                COOK: [e.g. 20 mins]
                CALORIES: [e.g. 480 kcal]
                PROTEIN: [e.g. 32g]
                CARBS: [e.g. 45g]
                FAT: [e.g. 16g]
                SERVINGS: [e.g. 4]
                DIFFICULTY: [Easy / Medium / Master Chef]
                CHEF_TIP: [${chefObj.name}'s signature pro-tip for executing this dish flawlessly]
                CHEF_QUOTE: [An authentic, inspiring culinary quote in ${chefObj.name}'s voice]
                INGREDIENTS: [Comma-separated ingredient list with measurements based on what the user has in the house]
                INSTRUCTIONS:
                1. [Detailed Step 1 emphasizing ${chefObj.name}'s technique]
                2. [Detailed Step 2]
                3. [Detailed Step 3]
                4. [Detailed Step 4]
                5. [Detailed Step 5]
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: return@withContext getFallbackRecipe(chef, craving, ingredientsInput, dietary, cuisine)

            parseResponse(text, chef, craving, cuisine, dietary)
        } catch (e: Exception) {
            getFallbackRecipe(chef, craving, ingredientsInput, dietary, cuisine)
        }
    }

    private fun parseResponse(
        text: String,
        chef: String,
        craving: String,
        cuisine: String,
        dietary: String
    ): GeneratedRecipe {
        val chefObj = CelebrityChefRegistry.getChefByName(chef)
        var title = "${chefObj.name}'s Signature $cuisine Skillet"
        var prepTime = "15 mins"
        var cookTime = "25 mins"
        var calories = "520 kcal"
        var protein = "34g"
        var carbs = "42g"
        var fat = "18g"
        var servings = 4
        var difficulty = "Medium"
        var chefTip = chefObj.signatureTechnique
        var chefQuote = chefObj.quote
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
                        servings = numStr.toIntOrNull() ?: 4
                    }
                    line.startsWith("DIFFICULTY:", ignoreCase = true) -> difficulty = line.substringAfter(":").trim()
                    line.startsWith("CHEF_TIP:", ignoreCase = true) -> chefTip = line.substringAfter(":").trim()
                    line.startsWith("CHEF_QUOTE:", ignoreCase = true) -> chefQuote = line.substringAfter(":").trim()
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
            chefQuote = chefQuote
        )
    }

    private fun getFallbackRecipe(
        chef: String,
        craving: String,
        ingredients: String,
        dietary: String,
        cuisine: String
    ): GeneratedRecipe {
        val chefObj = CelebrityChefRegistry.getChefByName(chef)
        val cleanIng = if (ingredients.isNotBlank()) ingredients else "Chicken breast, garlic, olive oil, baby spinach, parmesan"

        return when (chefObj.id) {
            "gordon_ramsay" -> GeneratedRecipe(
                title = "Gordon Ramsay's Pan-Seared Crispy Garlic Herb Medallions",
                prepTime = "10 mins",
                cookTime = "15 mins",
                calories = "520 kcal",
                protein = "42g",
                carbs = "12g",
                fat = "26g",
                servings = 2,
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
                chefQuote = "Cook with passion! Wake up the pan, taste as you go, and season at every layer."
            )
            "julia_child" -> GeneratedRecipe(
                title = "Julia Child's Classical French Red Wine & Herb Sauté",
                prepTime = "15 mins",
                cookTime = "30 mins",
                calories = "560 kcal",
                protein = "36g",
                carbs = "22g",
                fat = "32g",
                servings = 4,
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
                chefQuote = "Remember, no one's watching: bring on the butter and cook with joyful abandon!"
            )
            "anthony_bourdain" -> GeneratedRecipe(
                title = "Anthony Bourdain's Rustic Bistro Pan-Roast & Shallot Jus",
                prepTime = "12 mins",
                cookTime = "22 mins",
                calories = "580 kcal",
                protein = "40g",
                carbs = "28g",
                fat = "30g",
                servings = 2,
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
                chefQuote = "Good food is very often, even most of the time, simple, authentic food with soul."
            )
            "martha_stewart" -> GeneratedRecipe(
                title = "Martha Stewart's Elegant Farmhouse Herb Roasted Skillet",
                prepTime = "15 mins",
                cookTime = "25 mins",
                calories = "470 kcal",
                protein = "34g",
                carbs = "32g",
                fat = "18g",
                servings = 4,
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
                chefQuote = "Do what you love, and do it with flawless, foolproof scratch technique."
            )
            "rachael_ray" -> GeneratedRecipe(
                title = "Rachael Ray's 30-Minute Cozy Pan Stoup & Crispy Dippers",
                prepTime = "8 mins",
                cookTime = "20 mins",
                calories = "490 kcal",
                protein = "30g",
                carbs = "44g",
                fat = "16g",
                servings = 4,
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
                chefQuote = "Yum-O! Cook smart, cook fast, and make every 30-minute dish a flavor bomb!"
            )
            else -> GeneratedRecipe(
                title = "Bobby Flay's Smoky Chipotle & Charred Citrus Sizzle",
                prepTime = "12 mins",
                cookTime = "18 mins",
                calories = "530 kcal",
                protein = "38g",
                carbs = "34g",
                fat = "22g",
                servings = 4,
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
                chefQuote = "Layer the bold flavors! Char the chiles, hit it with citrus, and master the heat."
            )
        }
    }
}
