package com.example.data

import androidx.compose.ui.graphics.Color

data class CelebrityChef(
    val id: String,
    val name: String,
    val moniker: String,
    val tagline: String,
    val quote: String,
    val signatureTechnique: String,
    val specialtyExamples: List<String>,
    val accentColor: Color
)

object CelebrityChefRegistry {
    val GORDON_RAMSAY = CelebrityChef(
        id = "gordon_ramsay",
        name = "Gordon Ramsay",
        moniker = "The Michelin Precision Master",
        tagline = "Crisp searing, elevated classics, and obsessive seasoning",
        quote = "Cook with passion! Wake up the pan, taste as you go, and season at every layer.",
        signatureTechnique = "High-heat searing, butter basting with garlic & thyme, pan-sauce deglazing.",
        specialtyExamples = listOf("Crispy Skin Salmon", "Pan-Roasted Herb Steak", "Scrambled Eggs with Crème Fraîche"),
        accentColor = Color(0xFFE65100)
    )

    val JULIA_CHILD = CelebrityChef(
        id = "julia_child",
        name = "Julia Child",
        moniker = "The French Gastronomy Legend",
        tagline = "Classic French sauces, rich butter reductions, and slow braises",
        quote = "Remember, no one's watching: bring on the butter and cook with joyful abandon!",
        signatureTechnique = "Wine reductions, slow braising, roux bases, and velvety herb butter sauces.",
        specialtyExamples = listOf("Boeuf Bourguignon", "Coq au Vin", "Classic French Herb Omelet"),
        accentColor = Color(0xFFC2185B)
    )

    val ANTHONY_BOURDAIN = CelebrityChef(
        id = "anthony_bourdain",
        name = "Anthony Bourdain",
        moniker = "The Bistro & Street Food Icon",
        tagline = "Unapologetic comfort, rustic bistro soul, and deep globe-trotting flavors",
        quote = "Good food is very often, even most of the time, simple, authentic food with soul.",
        signatureTechnique = "Cast iron pan frying, rich bone broths, rustic braises, and assertive aromatics.",
        specialtyExamples = listOf("Steak Frites with Herb Butter", "Spicy Street Noodle Bowl", "Bistro Roast Chicken"),
        accentColor = Color(0xFF455A64)
    )

    val MARTHA_STEWART = CelebrityChef(
        id = "martha_stewart",
        name = "Martha Stewart",
        moniker = "The Scratch Entertaining Authority",
        tagline = "Refined scratch cooking, farm-fresh herbs, and golden baked perfection",
        quote = "Do what you love, and do it with flawless, foolproof scratch technique.",
        signatureTechnique = "Golden baking, layered gratins, farm-fresh herb infusions, and immaculate balance.",
        specialtyExamples = listOf("Golden Potato Gratin", "Farmhouse Herb Roast", "Classic Scratch Pasta"),
        accentColor = Color(0xFF2E7D32)
    )

    val RACHAEL_RAY = CelebrityChef(
        id = "rachael_ray",
        name = "Rachael Ray",
        moniker = "The 30-Minute Flavor Dynamo",
        tagline = "Fast 30-minute meals, EVOO flavor bases, and cozy family comfort",
        quote = "Yum-O! Cook smart, cook fast, and make every 30-minute dish a flavor bomb!",
        signatureTechnique = "EVOO pan tossing, 30-minute reductions, hearty stoups, and smart pantry shortcuts.",
        specialtyExamples = listOf("30-Min Hearty Stoup", "Garlic Butter Pasta Toss", "Crispy Cutlet Parmigiana"),
        accentColor = Color(0xFFF57C00)
    )

    val BOBBY_FLAY = CelebrityChef(
        id = "bobby_flay",
        name = "Bobby Flay",
        moniker = "The Southwestern & Grill Maestro",
        tagline = "Smoky chiles, chipotle honey glazes, and vibrant citrus marinades",
        quote = "Layer the bold flavors! Char the chiles, hit it with citrus, and master the heat.",
        signatureTechnique = "Smoky chipotle glazes, char-grilling, vibrant citrus salsas, and crunchy toppings.",
        specialtyExamples = listOf("Chipotle Honey Skirt Steak", "Charred Citrus Tacos", "Smoky Black Bean Bowl"),
        accentColor = Color(0xFFD84315)
    )

    val allChefs = listOf(
        GORDON_RAMSAY,
        JULIA_CHILD,
        ANTHONY_BOURDAIN,
        MARTHA_STEWART,
        RACHAEL_RAY,
        BOBBY_FLAY
    )

    fun getChefByName(name: String): CelebrityChef {
        return allChefs.find { it.name.equals(name, ignoreCase = true) || it.id.equals(name, ignoreCase = true) }
            ?: GORDON_RAMSAY
    }
}
