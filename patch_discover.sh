cat << 'INNER' > replacement.kt
            }

            // RECOMMENDED FOR YOU (Based on completed recipes)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Recommended for You",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Based on your history of completed recipes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        val recommendations = listOf(
                            "Smoky Cedar Plank Salmon",
                            "Tuscan Garlic Butter Steak",
                            "Lemon Herb Roast Chicken",
                            "Rich Mushroom Risotto"
                        )
                        items(recommendations) { rec ->
                            Card(
                                modifier = Modifier.width(160.dp).height(100.dp).clickable {
                                    searchQuery = rec
                                    viewModel.searchRecipe(rec, selectedChef.name, selectedPortions) { onRecipeClick(it) }
                                }.gourmetDepth(elevation = 4.dp, shapeRadius = 12.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text(rec, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 1: SELECT CELEBRITY MASTER CHEF
INNER
sed -i -e '/\/\/ SECTION 1: SELECT CELEBRITY MASTER CHEF/r replacement.kt' -e '/\/\/ SECTION 1: SELECT CELEBRITY MASTER CHEF/d' app/src/main/java/com/example/ui/screens/GenerateScreen.kt
