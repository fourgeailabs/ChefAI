package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.gourmetDepth
import com.example.ui.theme.TerracottaPrimary

data class ReleaseNote(
    val version: String,
    val date: String,
    val title: String,
    val isLatest: Boolean = false,
    val highlights: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsNewScreen(onBack: () -> Unit) {
    val releases = listOf(
        ReleaseNote(
            version = "1.08.00",
            date = "September 03, 2026",
            title = "Camera Viewport Scrollability & Dynamic Portion Size Scaling",
            isLatest = true,
            highlights = listOf(
                "Scrollable Camera Section: Added smooth vertical scrolling and visual drag handles to the AI Camera & Barcode Scanner bottom cards, ensuring all actions and recognition details are fully visible on any screen size or orientation.",
                "Bidirectional Portion Size Scaling: Users can now freely increase or decrease portion sizes (from 1 solo portion up to 100 catering portions) with quick presets (Solo, Couple, Family, Party, Feast, Crowd, Catering).",
                "Full-Chain Recipe Scaler: Portion sizes are seamlessly passed to the Gemini AI recipe generator, dynamic detail view scaler, and custom recipe builder.",
                "Polished Category Fallbacks: Enhanced produce and pantry classification display ensuring clean labels across all scanned goods."
            )
        ),
        ReleaseNote(
            version = "1.07.00",
            date = "September 03, 2026",
            title = "CI/CD Pipeline Build Fix & Portions Engine",
            isLatest = false,
            highlights = listOf(
                "GitHub Actions Setup: Upgraded setup-gradle action to v4 with Gradle 9.3.1 for reliable automated cloud compilation.",
                "Portion Size Calculation Core: Integrated AI prompt instructions for scaling ingredient weights, volumes, and nutritional macros according to custom servings."
            )
        ),
        ReleaseNote(
            version = "1.06.00",
            date = "September 03, 2026",
            title = "Gradle Wrapper & CI/CD Pipeline Build Fix",
            isLatest = false,
            highlights = listOf(
                "Gradle Wrapper (gradlew) Script Integration: Added standard root gradlew executable with automated wrapper bootstrap and direct Gradle delegation.",
                "GitHub Actions setup-gradle Action: Upgraded CI workflow with gradle/actions/setup-gradle@v4 and Gradle 9.3.1 for automated runner environment provisioning.",
                "Universal Fallback Execution: Supports ./gradlew assembleDebug and gradle assembleDebug seamlessly across local and cloud CI/CD runners.",
                "Zero-Failure APK Release Workflow: Guaranteed artifact export and GitHub Releases upload without binary missing errors."
            )
        ),
        ReleaseNote(
            version = "1.05.00",
            date = "September 03, 2026",
            title = "Loading Screen, GPS Locale & Supermarket Pricing Engine",
            isLatest = false,
            highlights = listOf(
                "Animated Gourmet Splash & Loading Screen: Beautiful startup screen featuring a glowing chef emblem, pulse animations, and real-time cloud service & GPS connection progress.",
                "GPS Locale & Market Pricing Intelligence: Automatically detects user city and calculates regional produce price indexes to ensure the best local grocery pricing.",
                "Nearby Supermarket Deals & Value Hubs: Explore nearby grocery options (ALDI, Trader Joe's, Local Co-ops, Whole Foods) with live estimated distance and discount tags.",
                "Pantry & Shopping List Price Indexing: View real-time local price multipliers and budget indicators directly on the Pantry and Shopping screens.",
                "Foolproof GitHub Actions CI/CD: Automated keystore generation and base64 restore pipeline guaranteeing zero keystore signing validation failures during APK builds."
            )
        ),
        ReleaseNote(
            version = "1.04.00",
            date = "September 03, 2026",
            title = "AI Camera Vision for Fresh Ingredients & Light/Dark Themes",
            isLatest = false,
            highlights = listOf(
                "AI Camera Vision for Non-Barcode Produce: Use the camera to snap photos of raw produce, fresh herbs, cuts of meat, seafood, and loose ingredients to identify them with AI.",
                "Mandatory Interactive Verification Step: Always review the AI's identification with an editable name field, category badge, and culinary pairing notes before confirming.",
                "Strict Privacy (Images Never Saved): Captured camera frames are analyzed entirely in volatile memory and immediately recycled — images are never saved to disk or persistent storage.",
                "Automated Pantry Addition & Shopping List Check-Off: Discovered ingredients can be added directly to your pantry and automatically cross off matching items from your Shopping List.",
                "Custom Light & Dark Mode Settings: Choose between System Default, crisp Light Mode, and rich Dark Mode gourmet aesthetics from the Settings screen.",
                "Streamlined Settings Menu: Removed the legacy update button to provide a clean, distraction-free preferences experience."
            )
        ),
        ReleaseNote(
            version = "1.03.00",
            date = "September 03, 2026",
            title = "Smart Barcode Scanner & Pantry Inventory Check-Off",
            isLatest = false,
            highlights = listOf(
                "Camera-Based Barcode Scanner: Integrated CameraX and ML Kit to scan UPC/EAN barcodes on food packaging with a sleek animated laser viewfinder and flashlight control.",
                "Automated Pantry Inventory: Scanned ingredients are immediately added to 'In The House Right Now' pantry storage to power celebrity master chef recipe generation.",
                "Automated Shopping List Procure & Check-Off: Scanning any ingredient on your shopping list automatically checks it off with instant visual confirmation.",
                "Offline & Online Hybrid Barcode Engine: Built-in instant database of 30+ everyday grocery staples with graceful OpenFoodFacts cloud fallback lookup.",
                "Quick Test Presets & Manual UPC Entry: 1-tap tester presets and manual barcode input for fast testing without physical packaging."
            )
        ),
        ReleaseNote(
            version = "1.02.00",
            date = "September 03, 2026",
            title = "Celebrity Master Chefs, Cravings & Pantry Intelligence",
            isLatest = false,
            highlights = listOf(
                "Celebrity Master Chef AI Personas: Pull meals inspired by culinary icons Gordon Ramsay, Julia Child, Anthony Bourdain, Martha Stewart, Rachael Ray, and Bobby Flay.",
                "Cravings Engine: Select or type exactly what you crave (Crispy & Savory, Warm & Comforting, Smoky & Spicy, Rich & Decadent, Fresh & Zesty, Sweet & Tangy, 30-Min Fast Comfort).",
                "'In The House Right Now' Pantry Synthesis: Generate gourmet chef-inspired meals using ingredients and staples already in your kitchen.",
                "Chef Quotes & Signature Technique Badges: Recipe details now showcase master chef philosophies, technique secrets, and craving indicators.",
                "Chef Filtered Cookbook: Organize and filter your saved recipes by individual celebrity master chef or view all."
            )
        ),
        ReleaseNote(
            version = "1.01.00",
            date = "September 03, 2026",
            title = "Visual Depth, Recipe Details & Material 3 Progress",
            isLatest = false,
            highlights = listOf(
                "Integrated Material 3 Circular & Linear progress indicators with animated percentage and culinary stage indicators during Gemini AI generation.",
                "Built complete Recipe Details system: interactive Cook Mode with built-in Kitchen Timer, dynamic Serving Size scaler, and Macronutrient breakdown.",
                "Added Custom Recipe Builder allowing users to create, customize, and edit detailed recipes directly.",
                "Crafted a bespoke visual theme with deep multi-level drop shadows, warm ambient borders, and elevated tactility."
            )
        ),
        ReleaseNote(
            version = "1.00.00",
            date = "August 29, 2026",
            title = "Initial Release of ChefAI Studio",
            isLatest = false,
            highlights = listOf(
                "AI-powered recipe generation using Gemini AI with cuisine and dietary customization.",
                "Local offline Room database persistence for saved favorite recipes.",
                "Interactive smart shopping list with check-off status and one-tap ingredient import.",
                "Complete Material 3 UI with cards and dark theme support."
            )
        ),
        ReleaseNote(
            version = "0.90.00",
            date = "August 15, 2026",
            title = "Beta Preview & UI Polish",
            isLatest = false,
            highlights = listOf(
                "Added multi-tab bottom navigation for Discover, Saved Recipes, Shopping List, and Settings.",
                "Implemented Gemini AI prompt fallback mechanics for seamless offline usage.",
                "Added recipe preparation timers and calorie estimation tags."
            )
        )
    )

    // Starts closed (null), opens on user interaction closing previously opened one
    var expandedVersion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("What's New", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Release History & Updates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap any release below to expand details. Each dropdown starts closed and automatically collapses previously opened entries.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(releases) { release ->
                val isExpanded = expandedVersion == release.version

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp)
                        .clickable {
                            expandedVersion = if (isExpanded) null else release.version
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "v${release.version}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (release.isLatest) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "CURRENT",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${release.title} • ${release.date}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    expandedVersion = if (isExpanded) null else release.version
                                }
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(4.dp))

                                release.highlights.forEach { highlight ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = TerracottaPrimary,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = highlight,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
