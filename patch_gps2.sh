cat << 'INNER' > replacement.kt
        var gpsAccuracy by remember { mutableStateOf("Approximate") }
        var zipCode by remember { mutableStateOf("") }
        var useZipCode by remember { mutableStateOf(false) }

        // GPS LOCALE & REGIONAL GROCERY PRICING CARD
        Card(
            modifier = Modifier.fillMaxWidth().gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(shape = RoundedCornerShape(12.dp), color = if (localePricing.isGpsActive) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(44.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = if (localePricing.isGpsActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(24.dp)) }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = "Location & Pricing Index", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "${localePricing.locationName} • ${localePricing.priceIndexMultiplier}x Price Index", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { viewModel.refreshLocalePricing() }) { Icon(imageVector = Icons.Default.Refresh, contentDescription = "Recalibrate GPS", tint = TerracottaPrimary) }
                }
                
                // Privacy toggles
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !useZipCode && gpsAccuracy == "Approximate", onClick = { useZipCode = false; gpsAccuracy = "Approximate" }, label = { Text("Approximate GPS") })
                    FilterChip(selected = !useZipCode && gpsAccuracy == "Exact", onClick = { useZipCode = false; gpsAccuracy = "Exact" }, label = { Text("Exact GPS") })
                    FilterChip(selected = useZipCode, onClick = { useZipCode = true }, label = { Text("Use Zip Code") })
                }
                if (useZipCode) {
                    OutlinedTextField(value = zipCode, onValueChange = { zipCode = it }, label = { Text("Enter Zip Code") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true)
                }
                Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) { Text(text = "Status: ${localePricing.statusMessage} • Live pricing data is tailored to your nearest supermarkets.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) }
            }
        }

        // GROCERY BRAND PREFERENCES
        var brandPref by remember { mutableStateOf("Name Brand") }
        Card(modifier = Modifier.fillMaxWidth().gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ingredient Brand Preferences", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = brandPref == "Name Brand", onClick = { brandPref = "Name Brand" }, label = { Text("Name Brand") })
                    FilterChip(selected = brandPref == "Store Brand", onClick = { brandPref = "Store Brand" }, label = { Text("Store Brand") })
                    FilterChip(selected = brandPref == "Cheapest", onClick = { brandPref = "Cheapest" }, label = { Text("Cheapest Overall") })
                }
            }
        }

        // HOUSEHOLD ALLERGIES
        var allergies by remember { mutableStateOf("None") }
        Card(modifier = Modifier.fillMaxWidth().gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Household Allergies", fontWeight = FontWeight.Bold)
                OutlinedTextField(value = allergies, onValueChange = { allergies = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            }
        }

        // LIKE & DISLIKE HISTORY
        var likes by remember { mutableStateOf("Garlic, Butter, Steak, Pasta") }
        var dislikes by remember { mutableStateOf("Olives, Anchovies") }
        Card(modifier = Modifier.fillMaxWidth().gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Like & Dislike History (AI Recommendations)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = likes, onValueChange = { likes = it }, label = { Text("Likes") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = dislikes, onValueChange = { dislikes = it }, label = { Text("Dislikes") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            }
        }

        // What's New Item
        SettingsItem(
            icon = Icons.Default.NewReleases,
            title = "What's New",
            subtitle = "Release notes for v1.08.00 & historical changelog",
            onClick = onNavigateWhatsNew
        )

        // About Item
        SettingsItem(
            icon = Icons.Default.Info,
            title = "About ChefAI Studio",
            subtitle = "Created by FourgeAI LABS • GitHub repository",
            onClick = onNavigateAbout
        )

        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "ChefAI Studio v1.08.00 • Master Chef Culinary Engine",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
INNER
sed -i -e '/\/\/ Theme Selector/,/    \}/!b' -e '/\/\/ Theme Selector/!b' -e '/\/\/ Theme Selector/r replacement.kt' -e '/\/\/ Theme Selector/,/    \}/d' app/src/main/java/com/example/ui/screens/SettingsScreen.kt
