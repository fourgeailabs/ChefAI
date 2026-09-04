import re

with open("app/src/main/java/com/example/ai/GeminiRecipeService.kt", "r") as f:
    content = f.read()

missing_params = """,
                cuisine = cuisine,
                dietary = dietary,
                chefInspiration = "Anthony Bourdain",
                craving = craving,
                chefQuote = "Good food is very often, even most of the time, simple, authentic food with soul.",
                imageUrl = CookbookMealImageProvider.resolveMealImage("steak frites", "Anthony Bourdain", craving),
                platePresentation = "Deep dark cast iron sear with sweet golden caramelized shallots pooling in rich Dijon pan juices, showered with fresh chopped flat-leaf parsley."
"""

content = content.replace('parsley."""\n            )', 'parsley."""' + missing_params + '            )')

with open("app/src/main/java/com/example/ai/GeminiRecipeService.kt", "w") as f:
    f.write(content)
