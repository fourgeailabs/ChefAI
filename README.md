# ChefAI Studio

**ChefAI Studio** is a premier, AI-powered gourmet culinary discovery, recipe builder, visual ingredient scanner, and cooking assistant application built for Android using Kotlin and Jetpack Compose. Developed by **FourgeAI LABS**.

## Features & Highlights

- **Gemini AI Recipe Search Bar (NEW)**: Search recipes or ask Gemini AI to create any custom dish directly from the top search bar, with instant suggestions for famous chef recipes.
- **Guy Fieri - The Mayor of Flavortown (NEW)**: Added Guy Fieri to the celebrity chef lineup with bold recipes, culinary style, and signature techniques.
- **Mini Chef Portraits & Interactive Biographies (NEW)**: Mini avatar portraits on every chef card. Tapping any portrait opens an interactive modal with the chef's full biography, signature dishes, philosophy, and a link to their official website.
- **Cookbook Plated Meal Presentation (NEW)**: Experience high-definition completed meal images showcasing what each dish is supposed to look like from the chef's cookbook, with detailed plating descriptions and presentation guides.
- **Detailed Step-by-Step Instructions (NEW)**: High-contrast numbered instruction cards with sensory timing cues and cookbook-grade step-by-step clarity.
- **Animated Gourmet Splash & Loading Screen**: Immersive startup screen with a pulsing glowing chef emblem, live progress counter, and status tracking as it connects to internet services and calibrates GPS locale data.
- **GPS Locale & Regional Supermarket Pricing Engine (NEW)**: Real-time location integration that collects GPS coordinates to determine the user's city/locale, calculates regional produce price indexes, and recommends nearby supermarket deals (ALDI, Trader Joe's, Local Farmer's Co-Op, Whole Foods).
- **AI Camera Vision for Non-Barcode Ingredients**: Use the camera to snap and discover loose produce, fresh garden herbs, cuts of meat, seafood, cheeses, spices, and bulk grocery items without barcodes using Gemini AI multimodal vision.
- **Mandatory Interactive Verification Step**: Every visual scan presents an interactive verification card where users can verify or edit the detected ingredient name, review culinary notes and chef pairing advice, see automatic shopping list matches, and confirm pantry addition.
- **Strict Zero-Storage Privacy Architecture**: In-memory camera frame analysis only (`previewView.bitmap`). Images are immediately processed in volatile RAM and dereferenced/recycled. Images are **never** saved to device storage or persistent cache.
- **Custom Light & Dark Appearance Modes**: Seamless toggle between System Default, crisp Light Mode (warm culinary canvas), and rich Dark Mode (charcoal & terracotta aesthetic) in the Settings menu.
- **Smart Barcode Scanner & Pantry Procure Engine**: Camera-based barcode scanner powered by CameraX and ML Kit with live viewfinder reticle, animated laser sweep, torch toggle, and haptic feedback to scan ingredient barcodes, automatically stock your "In The House" pantry, and check off items on your shopping list in real-time.
- **Celebrity Master Chef AI Generation**: Channel the authentic culinary philosophy, signature searing, French reductions, rustic street soul, farm scratch baking, 30-minute speed, and smoky Southwestern grill styles of **Gordon Ramsay**, **Julia Child**, **Anthony Bourdain**, **Martha Stewart**, **Rachael Ray**, and **Bobby Flay**.
- **Cravings Engine**: Tailor recipe creations to specific cravings ("Crispy & Savory", "Warm & Comforting", "Smoky & Spicy", "Rich & Decadent", "Fresh & Zesty", "Sweet & Tangy", "Fast 30-Min Feast") or freeform custom cravings.
- **"In The House Right Now" Smart Pantry Synthesis**: Real-time recipe formulation taking what the user has in their kitchen (with 1-tap common pantry staples, visual produce scans, and scanned barcode items) and synthesizing iconic meals.
- **Material 3 Progress Indicators**: Visual progress indicators (Circular & Linear) with live percentage counters, culinary stage progression, and chef tips while awaiting Gemini API responses.
- **Rich Recipe Details & Chef Badges**: Comprehensive recipe information including chef quotes, technique breakdowns, cook/prep timings, macronutrient breakdown (protein, carbs, fat), and dynamic serving scale adjusters.
- **Interactive Step-by-Step Cook Mode**: Guided cooking flow with step progress indicators, large readable typography, and an interactive built-in Kitchen Timer (Start, Pause, Reset, +1 Min).
- **Custom Recipe Builder**: Create, build, and edit recipes directly with customizable fields and nutritional facts.
- **Deep Multi-Level Depth & Shadows**: Bespoke visual design with warm terracotta & amber culinary palette, multi-tiered drop shadows, ambient glow borders, and layered surfaces.
- **Saved Cookbook with Chef Filtering**: Offline local SQLite persistence powered by Android Jetpack Room Database with filter-by-chef capability.
- **Smart Pantry & Shopping List**: Dual-tab view for Active Shopping List and Scanned Pantry Inventory, one-tap ingredient import from any recipe, interactive check-off states, and barcode / camera vision shortcuts.
- **Settings & What's New**: Expandable historical release changelogs with accordion dropdowns, theme appearance picker, and direct links to FourgeAI LABS.

## Release History
### Current Version: `1.10.00` (Build 11) — September 04, 2026
- **Manual Saving & Recipe Feedback**: Refactored generation flow to require explicit saving to your personal cookbook. Added interactive thumbs up/down rating sliders to teach the AI your palate.
- **Privacy-First GPS Toggles**: Set your location accuracy explicitly between Exact GPS, Approximate GPS, or manual Zip Code overrides.
- **Smart Shopping List Optimization**: A dedicated intelligence banner evaluates your entire shopping list and finds the single cheapest supermarket in a 15-mile radius.
- **Advanced Preference Controls**: Detailed settings for "Name Brand" vs "Store Brand" ingredients, customizable household allergies, and complete Like/Dislike history logging.
- **Discover "Recommended for You"**: Curated recipe suggestions explicitly based on your completed cooking history instead of just saved recipes.


### Version: `1.09.00` (Build 10) — September 04, 2026
- **Gemini API Recipe Search Bar**: Added an intuitive search bar at the top of the main screen allowing users to query existing recipes or ask Gemini AI to formulate any custom gourmet recipe in real time. Features quick-suggestion pills for famous chef dishes.
- **Guy Fieri Added to Master Chefs**: Integrated Guy Fieri as a featured celebrity chef with bold Flavortown recipes, techniques, and culinary profile.
- **Mini Chef Portraits & Interactive Biographies**: Added mini image portraits to every chef card in the carousel. Tapping a portrait opens an interactive modal presenting their biography, culinary background, and a direct link to their official website.
- **Cookbook Plated Meal Presentation**: Enhanced recipe details with high-definition completed meal photos displaying what the finished dish looks like from the chef's cookbook, along with detailed plating and visual presentation guides.
- **Detailed Step-by-Step Instructions**: Overhauled instructions layout with numbered badges, sensory timing cues, and cookbook-grade step clarity.

### Version: `1.08.00` (Build 9) — September 03, 2026
- **Camera Viewport Scrollability**: Added smooth vertical scrolling (`verticalScroll(rememberScrollState())`) and visual drag handles to all camera bottom sheets and overlay cards (AI vision produce verification, barcode lookup result, non-barcode capture sheet, and preset drawers). Users can now comfortably scroll up the bottom card on any device or orientation to access all buttons, verification details, and secondary options without clipping.
- **Dynamic Portion Size Scaling**: Full bidirectional scaling for portion sizes across the entire app. Steppers allow increasing or decreasing portions from 1 solo serving up to 100 catering portions, complemented by one-tap quick preset chips (`1 (Solo)`, `2 (Couple)`, `4 (Family)`, `6 (Party)`, `8 (Feast)`, `12 (Crowd)`, `20 (Catering)`).
- **End-to-End AI Recipe Portion Generator**: Gemini AI recipe prompt now explicitly calculates ingredients and nutritional macros scaled to target portion yields, and the recipe detail view dynamically scales measurements when adjusted.
- **Category & Ingredient Polishing**: Improved category fallback logic ensuring neat, formatted tags for all discovered and scanned grocery goods.

### Version: `1.07.00` (Build 8) — September 03, 2026
- **CI/CD Pipeline Build Fix & Portions Engine**: Upgraded GitHub Actions setup-gradle action to v4 with Gradle 9.3.1 for reliable automated cloud compilation.
- **Portion Size Calculation Core**: Integrated AI prompt instructions for scaling ingredient weights, volumes, and nutritional macros according to custom servings.

### Version: `1.06.00` (Build 7) — September 03, 2026
- **Gradle Wrapper & CI/CD Pipeline Build Fix**: Added root `gradlew` script and updated GitHub Actions with `gradle/actions/setup-gradle@v4` (Gradle 9.3.1) to eliminate `./gradlew: No such file or directory` errors during cloud APK builds.
- **Universal Local & Remote Gradle Execution**: Enabled seamless fallback execution for `./gradlew assembleDebug` and `gradle assembleDebug`.
- **Zero-Failure Release Pipeline**: Verified flawless APK artifact packaging and GitHub Releases upload flow.

### Version: `1.05.00` (Build 6) — September 03, 2026
- **Animated Gourmet Splash & Loading Screen**: Dynamic startup experience featuring a glowing chef insignia and progress tracking while connecting to cloud services and GPS.
- **GPS Locale & Local Supermarket Pricing Engine**: Collects GPS coordinates to determine city and regional produce index multiplier, ensuring accurate budget calculations and local deals.
- **Local Supermarket Deals & Value Hubs**: Direct access to nearest grocery deals (ALDI, Trader Joe's, Local Co-op, Whole Foods) with live distances and price tier indicators.
- **Pantry & Shopping Screen Locale Integration**: Real-time market pricing banner with manual GPS recalibration button.
- **GitHub Actions Signing Pipeline Fix**: Fully resilient keystore restoration from base64 with automated keytool generation fallback, ensuring zero `validateSigningDebug` failures in CI.

### Version: `1.04.00` (Build 5) — September 03, 2026
- **AI Camera Vision for Non-Barcode Produce**: Use live camera framing to discover loose produce, fresh herbs, cuts of meat, seafood, and spices with Gemini AI multimodal vision.
- **Mandatory Verification Step**: Review, edit/rename, and confirm discovered ingredients before adding to pantry or checking off shopping list items.
- **Strict Zero-Storage Privacy**: Camera frames are processed strictly in volatile RAM and immediately recycled; images are never written or saved to disk.
- **Light & Dark Theme Setting**: Added selectable theme mode switcher (System Default, Light Mode, Dark Mode) to Settings.
- **Settings Screen Streamline**: Removed legacy update checking button for a focused, distraction-free preferences experience.

### Version: `1.03.00` (Build 4) — September 03, 2026
- **Smart Barcode Scanner for Ingredients**: Live CameraX + ML Kit barcode reader with animated viewfinder laser, torch switch, test presets, and manual UPC entry.
- **Instant Pantry Inventory Stocking**: Scanned grocery products are automatically added to the SQLite pantry database and live "In The House Right Now" recipe synthesizer.
- **Automated Shopping List Check-Off**: Scanning ingredients automatically recognizes matching items on the user's shopping list and marks them as procured with visual confirmation.
- **Scanned Pantry Inventory View**: Added dedicated Pantry Inventory tab in the Grocery section with item management and one-tap transfer to shopping list.

### Version: `1.02.00` (Build 3) — September 03, 2026
- **Celebrity Master Chef AI Personas**: Integrated iconic chef channels for Gordon Ramsay, Julia Child, Anthony Bourdain, Martha Stewart, Rachael Ray, and Bobby Flay.
- **Cravings Engine**: Added quick-select and custom craving inputs to synthesize meals matching your exact craving profile.
- **"In The House Right Now" Pantry Intelligence**: Smart pantry synthesizer with 1-tap household staples to formulate dishes from existing kitchen inventory.
- **Chef Philosophy & Technique Badges**: Recipe details screen displays master chef quotes, signature techniques, and craving tags.
- **Chef Filtered Cookbook**: Added filter tabs to view and organize saved recipes by celebrity chef.

### Version: `1.01.00` (Build 2) — September 03, 2026
- **Material 3 Progress Indicators**: Integrated Circular & Linear progress indicators with animated percentage and culinary stage indicators during Gemini API generation.
- **Comprehensive Recipe Details**: Added Cook Mode with Kitchen Timer, Serving Size Scaler (1 to 8 servings), and Macronutrient breakdown meters.
- **Custom Recipe Builder**: Added dialog to build and edit recipes from scratch.
- **Multi-Level Tactile Depth**: Enhanced theme with custom elevated drop shadows, ambient glow borders, and gourmet color system.

### Version: `1.00.00` (Build 1) — August 29, 2026
- Initial release of ChefAI Studio with AI generator, Room Database persistence, and shopping list.

## CI/CD & Build Workflow
- **GitHub Actions (`build.yml`)**: Automated CI/CD workflow builds debug and release APKs upon push, pull request, manual dispatch, and releases.
- **Keystore Persistence**: Restores `debug.keystore` from `debug.keystore.base64` or creates a keytool fallback to preserve signing credentials and guarantee continuous APK upgrade compatibility across builds.
- **Release Automation**: Automatically publishes APK assets to GitHub Releases.

## Creator & Repository
- **Creator**: [FourgeAI LABS](https://github.com/fourgeailabs)
- **Application ID**: `com.fourgeailabs.chefai`
- **Repository URL**: `https://github.com/fourgeailabs/chefai`
