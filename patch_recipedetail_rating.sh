head -n -14 app/src/main/java/com/example/ui/screens/RecipeDetailScreen.kt > temp_rd.kt
cat << 'INNER' >> temp_rd.kt
                }

                if (recipeId != -1L) {
                    var rating by remember { mutableFloatStateOf(recipe.rating) }
                    Card(
                        modifier = Modifier.fillMaxWidth().gourmetDepth(elevation = 6.dp, shapeRadius = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rate this recipe for better recommendations", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.ThumbDown, contentDescription = "Dislike", tint = if (rating < 0.5f) TerracottaPrimary else MaterialTheme.colorScheme.outline)
                                Slider(
                                    value = rating,
                                    onValueChange = { rating = it },
                                    onValueChangeFinished = { viewModel.updateRecipe(recipe.copy(rating = rating)) },
                                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                )
                                Icon(Icons.Default.ThumbUp, contentDescription = "Like", tint = if (rating > 0.5f) OliveGreen else MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoPill(icon: ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        }
    }
}
INNER
mv temp_rd.kt app/src/main/java/com/example/ui/screens/RecipeDetailScreen.kt
