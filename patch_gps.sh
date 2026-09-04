cat << 'INNER' > replacement.kt
        var gpsAccuracy by remember { mutableStateOf("Approximate") }
        var zipCode by remember { mutableStateOf("") }
        var useZipCode by remember { mutableStateOf(false) }

        // GPS LOCALE & REGIONAL GROCERY PRICING CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (localePricing.isGpsActive) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (localePricing.isGpsActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Location & Pricing Index",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${localePricing.locationName} • ${localePricing.priceIndexMultiplier}x Price Index",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.refreshLocalePricing() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recalibrate GPS",
                            tint = TerracottaPrimary
                        )
                    }
                }
                
                // Privacy toggles
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !useZipCode && gpsAccuracy == "Approximate", onClick = { useZipCode = false; gpsAccuracy = "Approximate" }, label = { Text("Approximate GPS") })
                    FilterChip(selected = !useZipCode && gpsAccuracy == "Exact", onClick = { useZipCode = false; gpsAccuracy = "Exact" }, label = { Text("Exact GPS") })
                    FilterChip(selected = useZipCode, onClick = { useZipCode = true }, label = { Text("Use Zip Code") })
                }
                if (useZipCode) {
                    OutlinedTextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        label = { Text("Enter Zip Code") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Status: ${localePricing.statusMessage} • Live pricing data is tailored to your nearest supermarkets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
INNER
sed -i -e '/\/\/ GPS LOCALE & REGIONAL GROCERY PRICING CARD/,/What.s New Item/!b' -e '/\/\/ GPS LOCALE & REGIONAL GROCERY PRICING CARD/!b' -e '/\/\/ GPS LOCALE & REGIONAL GROCERY PRICING CARD/r replacement.kt' -e '/\/\/ GPS LOCALE & REGIONAL GROCERY PRICING CARD/,/\/\/ What.s New Item/d' app/src/main/java/com/example/ui/screens/SettingsScreen.kt
