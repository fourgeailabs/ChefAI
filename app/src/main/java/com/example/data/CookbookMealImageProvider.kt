package com.example.data

object CookbookMealImageProvider {

    // Curated high-resolution cookbook presentation photography
    private val MEAL_IMAGES = mapOf(
        "burger" to "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=900&q=85",
        "smash" to "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=900&q=85",
        "salmon" to "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=900&q=85",
        "fish" to "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=900&q=85",
        "steak" to "https://images.unsplash.com/photo-1544025162-d76694265947?w=900&q=85",
        "beef" to "https://images.unsplash.com/photo-1558030006-450675393462?w=900&q=85",
        "chicken" to "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=900&q=85",
        "poultry" to "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=900&q=85",
        "pasta" to "https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=900&q=85",
        "spaghetti" to "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=900&q=85",
        "taco" to "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=900&q=85",
        "chipotle" to "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?w=900&q=85",
        "nachos" to "https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?w=900&q=85",
        "bbq" to "https://images.unsplash.com/photo-1544025162-d76694265947?w=900&q=85",
        "ribs" to "https://images.unsplash.com/photo-1544025162-d76694265947?w=900&q=85",
        "chili" to "https://images.unsplash.com/photo-1541832676-9b763b0239ab?w=900&q=85",
        "braise" to "https://images.unsplash.com/photo-1547496502-affa22d38842?w=900&q=85",
        "wine" to "https://images.unsplash.com/photo-1547496502-affa22d38842?w=900&q=85",
        "stoup" to "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=900&q=85",
        "soup" to "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=900&q=85",
        "gratin" to "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=900&q=85",
        "potato" to "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=900&q=85",
        "salad" to "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=900&q=85",
        "omelet" to "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=900&q=85",
        "eggs" to "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=900&q=85",
        "pizza" to "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=900&q=85",
        "shrimp" to "https://images.unsplash.com/photo-1559742811-822873691df8?w=900&q=85",
        "seafood" to "https://images.unsplash.com/photo-1559742811-822873691df8?w=900&q=85",
        "curry" to "https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=900&q=85",
        "noodle" to "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=900&q=85"
    )

    private val CHEF_DEFAULT_IMAGES = mapOf(
        "Gordon Ramsay" to "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=900&q=85", // Crispy Salmon
        "Guy Fieri" to "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=900&q=85", // Out of bounds Burger
        "Julia Child" to "https://images.unsplash.com/photo-1547496502-affa22d38842?w=900&q=85", // French Wine Braise
        "Anthony Bourdain" to "https://images.unsplash.com/photo-1544025162-d76694265947?w=900&q=85", // Steak Frites
        "Martha Stewart" to "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=900&q=85", // Farmhouse Gratin
        "Rachael Ray" to "https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=900&q=85", // 30-min pasta
        "Bobby Flay" to "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=900&q=85" // Charred Chipotle Tacos
    )

    fun resolveMealImage(
        title: String,
        chefInspiration: String = "",
        craving: String = "",
        customImageUrl: String = ""
    ): String {
        if (customImageUrl.isNotBlank()) return customImageUrl

        val textToSearch = "$title $craving".lowercase()
        for ((keyword, url) in MEAL_IMAGES) {
            if (textToSearch.contains(keyword)) {
                return url
            }
        }

        return CHEF_DEFAULT_IMAGES[chefInspiration]
            ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=900&q=85"
    }

    fun getCookbookAppearanceGuide(
        title: String,
        chefInspiration: String = "",
        craving: String = ""
    ): String {
        val chef = CelebrityChefRegistry.getChefByName(chefInspiration)
        val text = "$title $craving".lowercase()

        return when {
            text.contains("burger") || text.contains("smash") ->
                "Glistening toasted brioche bun crowned with a lacy, mahogany-seared patty edge, melted aged cheese dripping down the sides, crisp bacon crumbles, and a generous pool of vibrant donkey sauce."

            text.contains("salmon") || text.contains("fish") ->
                "Shatteringly crisp, golden-amber scored fish skin with a translucent moist center, glistening in foaming thyme-garlic butter over vibrant blistered cherry tomatoes and tender wilted greens."

            text.contains("steak") || text.contains("beef") || text.contains("frites") ->
                "Deep caramelized mahogany sear with diamond grill marks, sliced thick across the grain to reveal a rosy pink center, basted with bubbling herb-garlic pan drippings and coarse flaky sea salt."

            text.contains("braise") || text.contains("bourguignon") || text.contains("stew") ->
                "Fork-tender chunks coated in a glossy, dark burgundy wine reduction sauce that coats the back of a spoon, interspersed with browned pearl onions and thyme sprigs."

            text.contains("pasta") || text.contains("spaghetti") || text.contains("stoup") ->
                "Glossy al dente ribbons coated in an emulsified olive oil and garlic sauce, dusted with finely grated Parmigiano-Reggiano, and speckled with torn sweet basil."

            text.contains("taco") || text.contains("chipotle") || text.contains("chili") ->
                "Smoky blistered edges with charred chile highlights, brightened by vibrant green cilantro leaves, pickled red onions, and a glistening drizzle of chipotle-honey glaze."

            else ->
                "Masterfully plated with rich color contrasts: deeply seared golden edges, a glistening chef pan-sauce glaze, and freshly showered garden microgreens as featured on the cover of ${chef.name}'s master cookbook."
        }
    }
}
