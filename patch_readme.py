with open("README.md", "r") as f:
    content = f.read()

new_readme_release = """### Current Version: `1.10.00` (Build 11) — September 04, 2026
- **Manual Saving & Recipe Feedback**: Refactored generation flow to require explicit saving to your personal cookbook. Added interactive thumbs up/down rating sliders to teach the AI your palate.
- **Privacy-First GPS Toggles**: Set your location accuracy explicitly between Exact GPS, Approximate GPS, or manual Zip Code overrides.
- **Smart Shopping List Optimization**: A dedicated intelligence banner evaluates your entire shopping list and finds the single cheapest supermarket in a 15-mile radius.
- **Advanced Preference Controls**: Detailed settings for "Name Brand" vs "Store Brand" ingredients, customizable household allergies, and complete Like/Dislike history logging.
- **Discover "Recommended for You"**: Curated recipe suggestions explicitly based on your completed cooking history instead of just saved recipes.

"""

content = content.replace("### Current Version: `1.09.00` (Build 10)", "### Version: `1.09.00` (Build 10)")
content = content.replace("## Release History\n", "## Release History\n" + new_readme_release)

with open("README.md", "w") as f:
    f.write(content)
