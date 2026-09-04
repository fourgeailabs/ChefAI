                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { if (selectedPortions > 1) selectedPortions -= 1 }, modifier = Modifier.size(36.dp)) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease Portions", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        Text(
                                            text = "$selectedPortions ${if (selectedPortions == 1) "portion" else "portions"}",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(onClick = { if (selectedPortions < 50) selectedPortions += 1 }, modifier = Modifier.size(36.dp)) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase Portions", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
