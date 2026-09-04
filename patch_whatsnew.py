with open("app/src/main/java/com/example/ui/screens/WhatsNewScreen.kt", "r") as f:
    content = f.read()

new_release = """        ReleaseNote(
            version = "1.10.00",
            date = "September 04, 2026",
            title = "Privacy GPS, Shopping Optimization & Settings Expansion",
            isLatest = true,
            highlights = listOf(
                "Manual Saving & Feedback System: Recipes are no longer auto-saved. Explicitly save them to your cookbook and use the thumbs up/down feedback slider.",
                "Settings Menus Expanded: Toggle Exact vs Approximate GPS privacy, Ingredient Brand preferences (Name vs Store), and edit household allergies & history.",
                "Shopping List Intelligence: Top banner computes the overall cheapest grocery store for your complete shopping list across a 15-mile radius.",
                "Recommended Discover Section: Highlights curated AI recipe suggestions based explicitly on your completed cooking history.",
                "UI Polish: Streamlined portion steppers, Master Chef dropdown menus, and enhanced formatting for all data fields."
            )
        ),
"""

content = content.replace('isLatest = true,', 'isLatest = false,')
content = content.replace('val releases = listOf(', 'val releases = listOf(\n' + new_release)

with open("app/src/main/java/com/example/ui/screens/WhatsNewScreen.kt", "w") as f:
    f.write(content)

