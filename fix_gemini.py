import re

with open("app/src/main/java/com/example/ai/GeminiRecipeService.kt", "r") as f:
    content = f.read()

gordon_instr = """1. PREPARE THE CANVAS: Bring all core ingredients to room temperature. Pat dry with paper towels to ensure maximum sear.
2. HEAT THE PAN: Place a heavy-bottomed pan over medium-high heat. Wait until the olive oil shimmers and just begins to smoke.
3. THE INITIAL SEAR: Lay the ingredients down away from you. Do not touch or move them for at least 3-4 minutes to develop a deep, caramelized crust.
4. FLIP AND BASTE: Flip the ingredients carefully. Introduce a generous knob of cold butter, crushed whole garlic cloves, and sprigs of fresh thyme.
5. ARROSER TECHNIQUE: Tilt the pan slightly and use a spoon to repeatedly baste the hot, foaming aromatic butter over the ingredients.
6. DEGLAZE: Remove the ingredients to rest. Pour a splash of dry white wine or chicken stock into the hot pan, scraping up all the golden fond (browned bits).
7. MOUNT THE SAUCE: Turn off the heat. Swirl in one last cube of cold butter to thicken and emulsify the pan sauce.
8. RESTING: Allow the cooked ingredients to rest on a warm cutting board for at least 5 minutes to allow juices to redistribute.
9. SLICING: Slice against the grain using a sharp knife to ensure maximum tenderness.
10. PLATING: Fan the slices on a warm plate, spoon the glossy pan reduction generously over the top, and finish with a pinch of flaky sea salt."""

bourdain_instr = """1. PREP THE STATION: Sharpen your knife. Slice the shallots thin and gather your mise en place.
2. SEASON AGGRESSIVELY: Liberally coat your main ingredients with coarse sea salt and freshly cracked black pepper. Don't be shy.
3. GET IT SMOKING: Heat a cast-iron skillet until it is punishingly hot. Add a glug of neutral oil.
4. HARD SEAR: Drop the ingredients in. Listen to the sizzle. Leave them alone to build a righteous crust (about 4 mins).
5. THE FLIP: Turn the ingredients over. Marvel at the golden-brown Maillard reaction.
6. SWEETEN THE POT: Toss the sliced shallots into the rendered fat around the edges of the pan.
7. DEGLAZE WITH ATTITUDE: Pour in the rich stock and a heavy spoonful of Dijon mustard. Scrape the bottom of the pan vigorously with a wooden spoon.
8. REDUCE & MOUNT: Let the liquid bubble and reduce by half. Kill the heat. Vigorously swirl in cold butter to mount the sauce into a glossy glaze.
9. REST THE MEAT: Let the main ingredient rest on a board for 5-7 minutes. If you cut it now, you ruin it.
10. SERVE RUSTIC: Plate the dish unapologetically. Drown it in the mustard-shallot jus and shower with chopped flat-leaf parsley."""

# We will just replace the hardcoded strings. The regex should match the block.
# Gordon
content = re.sub(r'"1\. Sear protein.*?"', '"""' + gordon_instr + '"""', content, flags=re.DOTALL)
# Bourdain
content = re.sub(r'"1\. Season your protein.*?parsley\."', '"""' + bourdain_instr + '"""', content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ai/GeminiRecipeService.kt", "w") as f:
    f.write(content)
