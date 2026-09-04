package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PantryItemEntity
import com.example.data.ShoppingItemEntity
import com.example.ui.ChefViewModel
import com.example.ui.components.BarcodeScannerDialog
import com.example.ui.components.gourmetButtonShadow
import com.example.ui.components.gourmetDepth
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.TerracottaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ChefViewModel) {
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val pantryItems by viewModel.pantryItems.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Shopping List, 1 = Scanned Pantry
    var newItemName by remember { mutableStateOf("") }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var lastScannedNotification by remember { mutableStateOf<String?>(null) }
    var showDealsExpanded by remember { mutableStateOf(false) }

    val localePricing by viewModel.localePricingData.collectAsState()

    val checkedCount = shoppingItems.count { it.isChecked }
    val totalCount = shoppingItems.size

    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = { showBarcodeScanner = false },
            onBarcodeScanned = { barcode, onProcessed ->
                viewModel.processBarcode(
                    barcode = barcode,
                    autoAddToPantry = true,
                    autoCheckShoppingList = true
                ) { product, matchedList ->
                    if (matchedList.isNotEmpty()) {
                        lastScannedNotification = "✓ Checked off ${matchedList.size} item(s) from Shopping List: ${product.name}"
                    } else {
                        lastScannedNotification = "✓ Added '${product.name}' to Pantry"
                    }
                    onProcessed(product, matchedList)
                }
            },
            onManualAddShoppingItem = { name ->
                viewModel.addShoppingItem(name)
            },
            onIdentifyVisualIngredient = { bitmap, onResult ->
                viewModel.identifyVisualIngredient(bitmap, onResult)
            },
            onConfirmVisualIngredient = { ingredientName, category, autoPantry, autoShopping, onConfirmed ->
                viewModel.confirmVisualIngredient(
                    ingredientName = ingredientName,
                    category = category,
                    autoAddToPantry = autoPantry,
                    autoCheckShoppingList = autoShopping
                ) { matchedShopping ->
                    if (matchedShopping.isNotEmpty()) {
                        lastScannedNotification = "✓ Discovered & Checked off ${matchedShopping.size} item(s): $ingredientName"
                    } else {
                        lastScannedNotification = "✓ Added '$ingredientName' to Pantry"
                    }
                    onConfirmed(matchedShopping)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pantry & Groceries",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (selectedTab == 0) "$checkedCount of $totalCount items procured" else "${pantryItems.size} items in household inventory",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Barcode Scanner Top Action Button
            Button(
                onClick = { showBarcodeScanner = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                modifier = Modifier.gourmetButtonShadow(elevation = 6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan Barcode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scanner Banner Toast/Alert if recently scanned
        AnimatedVisibility(
            visible = lastScannedNotification != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            if (lastScannedNotification != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = lastScannedNotification!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(
                            onClick = { lastScannedNotification = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // GPS Local Market Pricing Intelligence Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp, hasBorderGlow = false)
                .clickable { showDealsExpanded = !showDealsExpanded },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
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
                            shape = CircleShape,
                            color = if (localePricing.isGpsActive) Color(0xFF4CAF50).copy(alpha = 0.2f) else AmberAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (localePricing.isGpsActive) Color(0xFF2E7D32) else TerracottaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = localePricing.locationName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${localePricing.priceIndexMultiplier}x Index",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${localePricing.statusMessage} • Tap to view store deals",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.refreshLocalePricing() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh GPS",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showDealsExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Text(
                            text = "Nearby Supermarkets & Recommended Value Hubs",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        localePricing.nearbyDeals.forEach { deal ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = deal.storeName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = deal.dealTag,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "${deal.distanceMiles} mi • ${deal.priceTier}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Segmented Tabs: Shopping List vs Scanned Pantry
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = TerracottaPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp, hasBorderGlow = false)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Shopping List (${shoppingItems.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scanned Pantry (${pantryItems.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        if (selectedTab == 0) {
            // SHOPPING LIST TAB
            // Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        placeholder = { Text("Add custom grocery item...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (newItemName.isNotBlank()) {
                                viewModel.addShoppingItem(newItemName)
                                newItemName = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .gourmetButtonShadow(elevation = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            if (shoppingItems.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 8.dp, shapeRadius = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalGroceryStore,
                                contentDescription = null,
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your Shopping List is Empty",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add ingredients directly from master chef recipes or scan package barcodes to check off items automatically.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Items to Purchase",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { viewModel.clearShoppingList() }) {
                        Text("Clear All", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(shoppingItems, key = { it.id }) { item ->
                        ShoppingRowCard(
                            item = item,
                            onToggle = { viewModel.toggleShoppingItem(item) },
                            onDelete = { viewModel.deleteShoppingItem(item) }
                        )
                    }
                }
            }
        } else {
            // SCANNED PANTRY TAB
            if (pantryItems.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gourmetDepth(elevation = 8.dp, shapeRadius = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Scanned Pantry Items Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'Scan Barcode' to scan food packaging. Ingredients are instantly added here and automatically checked off your shopping list.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { showBarcodeScanner = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Launch Barcode Scanner")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "In-House Scanned Inventory",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { viewModel.clearPantry() }) {
                        Text("Clear Pantry", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pantryItems, key = { it.id }) { item ->
                        PantryRowCard(
                            item = item,
                            onDelete = { viewModel.deletePantryItem(item) },
                            onAddToShopping = { viewModel.addShoppingItem(item.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingRowCard(
    item: ShoppingItemEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp, hasBorderGlow = false)
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (item.isChecked) "Checked" else "Unchecked",
                    tint = if (item.isChecked) TerracottaPrimary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )
                    if (item.recipeTitle != "Custom") {
                        Text(
                            text = "From: ${item.recipeTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete item",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun PantryRowCard(
    item: PantryItemEntity,
    onDelete: () -> Unit,
    onAddToShopping: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp, hasBorderGlow = false),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Kitchen,
                            contentDescription = null,
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.brand.isNotBlank()) {
                            Text(
                                text = item.brand,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (item.barcode.isNotBlank()) {
                            Text(
                                text = "UPC: ${item.barcode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Row {
                IconButton(onClick = onAddToShopping) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Shopping List",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete item",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
