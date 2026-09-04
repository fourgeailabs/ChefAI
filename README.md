# ChefAI Studio

**ChefAI Studio** is a premier, AI-powered gourmet culinary discovery, recipe builder, and cooking assistant application built for Android using Kotlin and Jetpack Compose. Developed by **FourgeAI LABS**.

## Features & Highlights

- **Smart Barcode Scanner & Pantry Procure Engine**: Camera-based barcode scanner powered by CameraX and ML Kit with live viewfinder reticle, animated laser sweep, torch toggle, and haptic feedback to scan ingredient barcodes, automatically stock your "In The House" pantry, and check off items on your shopping list in real-time.
- **Offline & Cloud Hybrid Barcode Database**: Pre-indexed offline database of 30+ everyday grocery staples with seamless OpenFoodFacts cloud API fallback.
- **Celebrity Master Chef AI Generation**: Channel the authentic culinary philosophy, signature searing, French reductions, rustic street soul, farm scratch baking, 30-minute speed, and smoky Southwestern grill styles of **Gordon Ramsay**, **Julia Child**, **Anthony Bourdain**, **Martha Stewart**, **Rachael Ray**, and **Bobby Flay**.
- **Cravings Engine**: Tailor recipe creations to specific cravings ("Crispy & Savory", "Warm & Comforting", "Smoky & Spicy", "Rich & Decadent", "Fresh & Zesty", "Sweet & Tangy", "Fast 30-Min Feast") or freeform custom cravings.
- **"In The House Right Now" Smart Pantry Synthesis**: Real-time recipe formulation taking what the user has in their kitchen (with 1-tap common pantry staples and scanned barcode items) and synthesizing iconic meals.
- **Material 3 Progress Indicators**: Visual progress indicators (Circular & Linear) with live percentage counters, culinary stage progression, and chef tips while awaiting Gemini API responses.
- **Rich Recipe Details & Chef Badges**: Comprehensive recipe information including chef quotes, technique breakdowns, cook/prep timings, macronutrient breakdown (protein, carbs, fat), and dynamic serving scale adjusters.
- **Interactive Step-by-Step Cook Mode**: Guided cooking flow with step progress indicators, large readable typography, and an interactive built-in Kitchen Timer (Start, Pause, Reset, +1 Min).
- **Custom Recipe Builder**: Create, build, and edit recipes directly with customizable fields and nutritional facts.
- **Deep Multi-Level Depth & Shadows**: Bespoke visual design with warm terracotta & amber culinary palette, multi-tiered drop shadows, ambient glow borders, and layered surfaces.
- **Saved Cookbook with Chef Filtering**: Offline local SQLite persistence powered by Android Jetpack Room Database with filter-by-chef capability.
- **Smart Pantry & Shopping List**: Dual-tab view for Active Shopping List and Scanned Pantry Inventory, one-tap ingredient import from any recipe, interactive check-off states, and barcode scanner shortcuts.
- **Settings & What's New**: Expandable historical release changelogs with accordion dropdowns, automated update checks, and direct links to FourgeAI LABS.

## Release History

### Current Version: `1.03.00` (Build 4) — September 03, 2026
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
- **Auto-Update Scanner**: Added check-for-updates system with "Remind for later" and "Skip this version" dialog options.

### Version: `1.00.00` (Build 1) — August 29, 2026
- Initial release of ChefAI Studio with AI generator, Room Database persistence, and shopping list.

## CI/CD & Build Workflow
- **GitHub Actions (`build.yml`)**: Automated CI/CD workflow builds debug and release APKs upon push, pull request, manual dispatch, and releases.
- **Keystore Persistence**: Restores `debug.keystore` from `debug.keystore.base64` to preserve signing credentials and guarantee continuous APK upgrade compatibility across builds.
- **Release Automation**: Automatically publishes APK assets to GitHub Releases.

## Creator & Repository
- **App Creator**: [FourgeAI LABS](https://github.com/fourgeailabs)
- **GitHub Repository**: [https://github.com/fourgeailabs/chefai](https://github.com/fourgeailabs/chefai)
- **Application ID**: `com.fourgeailabs.chefai`

## License
MIT License - Developed by FourgeAI LABS.
