cat << 'INNER' > replacement.kt
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedChef.name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            CelebrityChefRegistry.allChefs.forEach { chef ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Surface(shape = CircleShape, color = chef.accentColor, modifier = Modifier.size(32.dp)) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(text = chef.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                            }
                                            Text(text = chef.name, fontWeight = FontWeight.SemiBold)
                                        }
                                    },
                                    onClick = {
                                        selectedChef = chef
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
INNER
sed -i -e '/LazyRow(/,/} \/\/ End LazyRow/!b' -e '/LazyRow(/!b' -e '/LazyRow(/r replacement.kt' -e '/LazyRow(/,/}[[:space:]]*$/d' app/src/main/java/com/example/ui/screens/GenerateScreen.kt
# Actually, standard sed multi-line replace is tricky. Let's use awk or perl.
