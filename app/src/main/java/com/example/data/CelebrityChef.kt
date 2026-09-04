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
    val accentColor: Color,
    val avatarUrl: String = "",
    val bio: String = "",
    val websiteUrl: String = ""
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
        accentColor = Color(0xFFE65100),
        avatarUrl = "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400&q=80",
        bio = "Gordon Ramsay OBE is a British multi-Michelin-starred chef, restaurateur, and global television icon. Holding 17 Michelin stars across his illustrious restaurant group, Ramsay is renowned for uncompromising standards, classical French culinary rigor, and high-energy passion. Host of legendary shows like Hell's Kitchen, MasterChef, and Kitchen Nightmares, he champions razor-sharp precision, aggressive seasoning, and letting world-class ingredients shine.",
        websiteUrl = "https://www.gordonramsay.com"
    )

    val GUY_FIERI = CelebrityChef(
        id = "guy_fieri",
        name = "Guy Fieri",
        moniker = "The Mayor of Flavortown & Comfort King",
        tagline = "Righteous comfort classics, killer barbecue glazes, and out-of-bounds flavor explosions",
        quote = "This is out of bounds! We're taking this righteous dish straight to Flavortown!",
        signatureTechnique = "Triple-dunk batter frying, rich barbecue reductions, donkey sauce layering, and smoky char.",
        specialtyExamples = listOf("Trash Can Nachos", "Dragon's Breath Chili", "Righteous Pastrami Smash Burger", "Flavortown Glazed Ribs"),
        accentColor = Color(0xFFD32F2F),
        avatarUrl = "https://images.unsplash.com/photo-1577219491135-ce391730fb2c?w=400&q=80",
        bio = "Guy Fieri is an Emmy Award-winning television host, restaurateur, New York Times bestselling author, and the charismatic 'Mayor of Flavortown'. Known worldwide for hosting Food Network megahits like Diners, Drive-Ins and Dives, Guy's Grocery Games, and Tournament of Champions, Guy revolutionized bold, comfort-forward American cuisine. He champions righteous flavors, high-energy scratch cooking, and vibrant street food fusion.",
        websiteUrl = "https://guyfieri.com"
    )

    val JULIA_CHILD = CelebrityChef(
        id = "julia_child",
        name = "Julia Child",
        moniker = "The French Gastronomy Legend",
        tagline = "Classic French sauces, rich butter reductions, and slow braises",
        quote = "Remember, no one's watching: bring on the butter and cook with joyful abandon!",
        signatureTechnique = "Wine reductions, slow braising, roux bases, and velvety herb butter sauces.",
        specialtyExamples = listOf("Boeuf Bourguignon", "Coq au Vin", "Classic French Herb Omelet"),
        accentColor = Color(0xFFC2185B),
        avatarUrl = "https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=400&q=80",
        bio = "Julia Child was an American cooking pioneer, author, and television personality who brought French cuisine to the American public. Her seminal cookbook 'Mastering the Art of French Cooking' and groundbreaking PBS series 'The French Chef' transformed home dining forever with warmth, fearlessness with butter, and joyful mastery.",
        websiteUrl = "https://www.juliachildfoundation.org"
    )

    val ANTHONY_BOURDAIN = CelebrityChef(
        id = "anthony_bourdain",
        name = "Anthony Bourdain",
        moniker = "The Bistro & Street Food Icon",
        tagline = "Unapologetic comfort, rustic bistro soul, and deep globe-trotting flavors",
        quote = "Good food is very often, even most of the time, simple, authentic food with soul.",
        signatureTechnique = "Cast iron pan frying, rich bone broths, rustic braises, and assertive aromatics.",
        specialtyExamples = listOf("Steak Frites with Herb Butter", "Spicy Street Noodle Bowl", "Bistro Roast Chicken"),
        accentColor = Color(0xFF455A64),
        avatarUrl = "https://images.unsplash.com/photo-1574966740793-953ad375ded6?w=400&q=80",
        bio = "Anthony Bourdain was an influential chef, author, and cultural storyteller. Beginning at Brasserie Les Halles in New York, his bestselling memoir 'Kitchen Confidential' and award-winning documentary series 'No Reservations' and 'Parts Unknown' celebrated authentic street cuisine, culinary honesty, and human connection across the globe.",
        websiteUrl = "https://explorepartsunknown.com"
    )

    val MARTHA_STEWART = CelebrityChef(
        id = "martha_stewart",
        name = "Martha Stewart",
        moniker = "The Scratch Entertaining Authority",
        tagline = "Refined scratch cooking, farm-fresh herbs, and golden baked perfection",
        quote = "Do what you love, and do it with flawless, foolproof scratch technique.",
        signatureTechnique = "Golden baking, layered gratins, farm-fresh herb infusions, and immaculate balance.",
        specialtyExamples = listOf("Golden Potato Gratin", "Farmhouse Herb Roast", "Classic Scratch Pasta"),
        accentColor = Color(0xFF2E7D32),
        avatarUrl = "https://images.unsplash.com/photo-1581299894007-aaa50297cf16?w=400&q=80",
        bio = "Martha Stewart is America's foremost lifestyle and culinary authority. Founder of Martha Stewart Living Omnimedia and author of nearly 100 lifestyle and cookbooks, Martha is celebrated for immaculate entertaining, precision baking, scratch recipes, and garden-to-table farm perfection.",
        websiteUrl = "https://www.marthastewart.com"
    )

    val RACHAEL_RAY = CelebrityChef(
        id = "rachael_ray",
        name = "Rachael Ray",
        moniker = "The 30-Minute Flavor Dynamo",
        tagline = "Fast 30-minute meals, EVOO flavor bases, and cozy family comfort",
        quote = "Yum-O! Cook smart, cook fast, and make every 30-minute dish a flavor bomb!",
        signatureTechnique = "EVOO pan tossing, 30-minute reductions, hearty stoups, and smart pantry shortcuts.",
        specialtyExamples = listOf("30-Min Hearty Stoup", "Garlic Butter Pasta Toss", "Crispy Cutlet Parmigiana"),
        accentColor = Color(0xFFF57C00),
        avatarUrl = "https://images.unsplash.com/photo-1595273670150-bd0c3c392e46?w=400&q=80",
        bio = "Rachael Ray is an American celebrity cook, Emmy-winning talk show host, and author famous for making weeknight cooking fast, accessible, and delicious. Her signature '30 Minute Meals' franchise and iconic catchphrases like 'EVOO' and 'Yum-O' revolutionized home cooking for millions of busy families.",
        websiteUrl = "https://rachaelray.com"
    )

    val BOBBY_FLAY = CelebrityChef(
        id = "bobby_flay",
        name = "Bobby Flay",
        moniker = "The Southwestern & Grill Maestro",
        tagline = "Smoky chiles, chipotle honey glazes, and vibrant citrus marinades",
        quote = "Layer the bold flavors! Char the chiles, hit it with citrus, and master the heat.",
        signatureTechnique = "Smoky chipotle glazes, char-grilling, vibrant citrus salsas, and crunchy toppings.",
        specialtyExamples = listOf("Chipotle Honey Skirt Steak", "Charred Citrus Tacos", "Smoky Black Bean Bowl"),
        accentColor = Color(0xFFD84315),
        avatarUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=400&q=80",
        bio = "Bobby Flay is an acclaimed Iron Chef, restaurateur, and Master of the Grill. A pioneer of Southwestern and bold American barbecue cuisine, Flay has starred on Iron Chef America, Beat Bobby Flay, and Grill It! His cooking emphasizes complex chile heat, sweet-and-smoky glazes, and bright citrus finishes.",
        websiteUrl = "https://bobbyflay.com"
    )

    val allChefs = listOf(
        GORDON_RAMSAY,
        GUY_FIERI,
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
