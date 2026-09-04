cat << 'INNER' > replacement.kt
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
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // LIKE & DISLIKE HISTORY
        var likes by remember { mutableStateOf("Garlic, Butter, Steak, Pasta") }
        var dislikes by remember { mutableStateOf("Olives, Anchovies") }
        Card(modifier = Modifier.fillMaxWidth().gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Like & Dislike History (AI Recommendations)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = likes,
                    onValueChange = { likes = it },
                    label = { Text("Likes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dislikes,
                    onValueChange = { dislikes = it },
                    label = { Text("Dislikes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // What's New Item
INNER
sed -i -e '/\/\/ What.s New Item/r replacement.kt' -e '/\/\/ What.s New Item/d' app/src/main/java/com/example/ui/screens/SettingsScreen.kt
