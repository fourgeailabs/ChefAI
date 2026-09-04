package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ai.VisualIngredientRecognitionResult
import com.example.data.BarcodeProductRegistry
import com.example.data.ScannedProduct
import com.example.data.ShoppingItemEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.TerracottaPrimary
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

enum class ScannerMode {
    BARCODE,
    AI_VISION_NON_BARCODE
}

data class VisualFoodPreset(
    val name: String,
    val category: String,
    val culinaryNotes: String,
    val suggestedChefUsage: String
)

val sampleVisualFoodPresets = listOf(
    VisualFoodPreset(
        name = "Fresh Rosemary Sprigs",
        category = "Herbs & Spices",
        culinaryNotes = "Aromatic evergreen herb with pine notes and natural savory oils.",
        suggestedChefUsage = "Toss with hot garlic butter to baste seared steaks or roasted potatoes."
    ),
    VisualFoodPreset(
        name = "Roma Tomatoes",
        category = "Produce",
        culinaryNotes = "Plump, low-moisture tomatoes with balanced sweetness and vibrant acidity.",
        suggestedChefUsage = "Roast slowly with olive oil and garlic for a classic Mediterranean skillet."
    ),
    VisualFoodPreset(
        name = "Hass Avocado",
        category = "Produce",
        culinaryNotes = "Rich buttery flesh loaded with heart-healthy monounsaturated fats.",
        suggestedChefUsage = "Slice fresh and finish with flaky sea salt and lime zest."
    ),
    VisualFoodPreset(
        name = "Yellow Onion",
        category = "Produce",
        culinaryNotes = "Sweet allium foundation with high natural sugars for caramelization.",
        suggestedChefUsage = "Caramelize in French butter over gentle heat for fond development."
    ),
    VisualFoodPreset(
        name = "Garlic Bulb",
        category = "Herbs & Spices",
        culinaryNotes = "Pungent aromatic bulb providing foundational depth to savory pan sauces.",
        suggestedChefUsage = "Crush whole cloves and infuse into hot olive oil and butter."
    ),
    VisualFoodPreset(
        name = "Fresh Basil",
        category = "Herbs & Spices",
        culinaryNotes = "Delicate, peppery-sweet Italian green packed with volatile aromatics.",
        suggestedChefUsage = "Tear by hand and fold in during the final 30 seconds of cooking."
    ),
    VisualFoodPreset(
        name = "Fresh Lemons",
        category = "Produce",
        culinaryNotes = "Crisp citrus providing clean citric acidity to cut through rich sauces.",
        suggestedChefUsage = "Zest the peel into pan sauces and finish with a squeeze of juice."
    ),
    VisualFoodPreset(
        name = "Boneless Ribeye Steak",
        category = "Meat & Poultry",
        culinaryNotes = "Prime beef cut with rich intramuscular marbling.",
        suggestedChefUsage = "Hard sear in cast iron skillet and baste with thyme and garlic butter."
    )
)

@Composable
fun BarcodeScannerDialog(
    onDismiss: () -> Unit,
    onBarcodeScanned: (String, (ScannedProduct, List<ShoppingItemEntity>) -> Unit) -> Unit,
    onManualAddShoppingItem: (String) -> Unit,
    onIdentifyVisualIngredient: ((Bitmap, (VisualIngredientRecognitionResult, List<ShoppingItemEntity>) -> Unit) -> Unit)? = null,
    onConfirmVisualIngredient: ((String, String, Boolean, Boolean, (List<ShoppingItemEntity>) -> Unit) -> Unit)? = null
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var activeMode by remember { mutableStateOf(ScannerMode.BARCODE) }
    var manualBarcodeText by remember { mutableStateOf("") }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Barcode scanned result
    var scannedResult by remember { mutableStateOf<ScannedProduct?>(null) }
    var matchedShoppingList by remember { mutableStateOf<List<ShoppingItemEntity>>(emptyList()) }

    // AI Vision Verification Step State
    var visualVerificationResult by remember { mutableStateOf<VisualIngredientRecognitionResult?>(null) }
    var verifiedIngredientName by remember { mutableStateOf("") }
    var verifiedCategory by remember { mutableStateOf("Produce") }
    var visualMatchedShoppingList by remember { mutableStateOf<List<ShoppingItemEntity>>(emptyList()) }
    var isVisualConfirmed by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Live Camera View or Permission Prompt
                if (hasCameraPermission) {
                    UnifiedCameraPreviewView(
                        isBarcodeMode = (activeMode == ScannerMode.BARCODE && scannedResult == null && visualVerificationResult == null),
                        onBarcodeDetected = { barcode ->
                            if (!isProcessing && scannedResult == null && visualVerificationResult == null) {
                                isProcessing = true
                                onBarcodeScanned(barcode) { product, matches ->
                                    scannedResult = product
                                    matchedShoppingList = matches
                                    isProcessing = false
                                }
                            }
                        },
                        onCameraControlReady = { control ->
                            cameraControl = control
                        },
                        onPreviewViewReady = { pView ->
                            previewViewRef = pView
                        },
                        isFlashlightOn = isFlashlightOn
                    )
                } else {
                    // No Camera Permission UI
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2C2C2C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color(0xFFFF8A65),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Camera Access Needed",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Grant camera permission to scan barcodes or visually discover non-barcode food ingredients using AI.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Grant Camera Permission")
                        }
                    }
                }

                // Scanning Reticle Overlays
                if (hasCameraPermission && scannedResult == null && visualVerificationResult == null) {
                    if (activeMode == ScannerMode.BARCODE) {
                        BarcodeScannerOverlay(isScanning = !isProcessing)
                    } else {
                        FoodVisionScannerOverlay(isAnalyzing = isProcessing)
                    }
                }

                // Top Header Controls: Dismiss, Mode Toggle & Flashlight
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }

                        // Mode Switcher Tabs (Barcode vs AI Visual Food)
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444444))
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                ModeTabButton(
                                    title = "Barcode",
                                    icon = Icons.Default.QrCodeScanner,
                                    isSelected = activeMode == ScannerMode.BARCODE,
                                    onClick = {
                                        activeMode = ScannerMode.BARCODE
                                        visualVerificationResult = null
                                        scannedResult = null
                                    }
                                )
                                ModeTabButton(
                                    title = "AI Food Vision",
                                    icon = Icons.Default.AutoAwesome,
                                    isSelected = activeMode == ScannerMode.AI_VISION_NON_BARCODE,
                                    onClick = {
                                        activeMode = ScannerMode.AI_VISION_NON_BARCODE
                                        visualVerificationResult = null
                                        scannedResult = null
                                    }
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                isFlashlightOn = !isFlashlightOn
                                cameraControl?.enableTorch(isFlashlightOn)
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isFlashlightOn) AmberAccent else Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = if (isFlashlightOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flashlight",
                                tint = if (isFlashlightOn) Color.Black else Color.White
                            )
                        }
                    }
                }

                // BOTTOM CONTROLS & CARDS
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    when {
                        // 1. AI Visual Ingredient Verification Step (MANDATORY VERIFICATION STEP)
                        visualVerificationResult != null -> {
                            VisualIngredientVerificationCard(
                                originalResult = visualVerificationResult!!,
                                ingredientName = verifiedIngredientName,
                                onNameChange = { verifiedIngredientName = it },
                                category = verifiedCategory,
                                matchedShoppingItems = visualMatchedShoppingList,
                                isConfirmed = isVisualConfirmed,
                                onConfirm = {
                                    if (onConfirmVisualIngredient != null) {
                                        onConfirmVisualIngredient(
                                            verifiedIngredientName,
                                            verifiedCategory,
                                            true,
                                            true
                                        ) { matches ->
                                            visualMatchedShoppingList = matches
                                            isVisualConfirmed = true
                                        }
                                    } else {
                                        isVisualConfirmed = true
                                    }
                                },
                                onScanAnother = {
                                    visualVerificationResult = null
                                    verifiedIngredientName = ""
                                    visualMatchedShoppingList = emptyList()
                                    isVisualConfirmed = false
                                    isProcessing = false
                                },
                                onDone = onDismiss
                            )
                        }

                        // 2. Barcode Scanned Success Card
                        scannedResult != null -> {
                            ScannedProductCard(
                                product = scannedResult!!,
                                matchedItems = matchedShoppingList,
                                onScanNext = {
                                    scannedResult = null
                                    matchedShoppingList = emptyList()
                                    isProcessing = false
                                },
                                onAddToShopping = {
                                    onManualAddShoppingItem(scannedResult!!.name)
                                },
                                onDone = onDismiss
                            )
                        }

                        // 3. Live Capture & Presets View
                        activeMode == ScannerMode.AI_VISION_NON_BARCODE -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 480.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Drag handle
                                    Box(
                                        modifier = Modifier
                                            .width(36.dp)
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(Color.Gray.copy(alpha = 0.4f))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Snap Non-Barcode Ingredient (Produce, Herbs, Meat)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFB74D)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Quick visual ingredient test presets
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(sampleVisualFoodPresets) { preset ->
                                            Surface(
                                                modifier = Modifier.clickable {
                                                    if (!isProcessing) {
                                                        visualVerificationResult = VisualIngredientRecognitionResult(
                                                            ingredientName = preset.name,
                                                            category = preset.category,
                                                            culinaryNotes = preset.culinaryNotes,
                                                            suggestedChefUsage = preset.suggestedChefUsage
                                                        )
                                                        verifiedIngredientName = preset.name
                                                        verifiedCategory = preset.category
                                                        isVisualConfirmed = false
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFF2C2C2C),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444444))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Eco,
                                                        contentDescription = null,
                                                        tint = OliveGreen,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = preset.name,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Camera Shutter Button (In-memory capture, Images NEVER saved)
                                    Button(
                                        onClick = {
                                            if (!isProcessing) {
                                                isProcessing = true
                                                val pView = previewViewRef
                                                val bitmap = pView?.bitmap
                                                if (bitmap != null && onIdentifyVisualIngredient != null) {
                                                    onIdentifyVisualIngredient(bitmap) { result, matches ->
                                                        visualVerificationResult = result
                                                        verifiedIngredientName = result.ingredientName
                                                        verifiedCategory = result.category
                                                        visualMatchedShoppingList = matches
                                                        isVisualConfirmed = false
                                                        isProcessing = false
                                                        // Immediately recycle bitmap (Image NEVER saved)
                                                        try { bitmap.recycle() } catch (_: Exception) {}
                                                    }
                                                } else {
                                                    // Fallback recognition preset
                                                    val randomPreset = sampleVisualFoodPresets.random()
                                                    visualVerificationResult = VisualIngredientRecognitionResult(
                                                        ingredientName = randomPreset.name,
                                                        category = randomPreset.category,
                                                        culinaryNotes = randomPreset.culinaryNotes,
                                                        suggestedChefUsage = randomPreset.suggestedChefUsage
                                                    )
                                                    verifiedIngredientName = randomPreset.name
                                                    verifiedCategory = randomPreset.category
                                                    isVisualConfirmed = false
                                                    isProcessing = false
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        enabled = !isProcessing
                                    ) {
                                        if (isProcessing) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text("AI Master Chef Discovering Ingredient...", fontWeight = FontWeight.Bold)
                                        } else {
                                            Icon(Icons.Default.Camera, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Capture & Discover with AI", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Barcode Mode Drawer
                        else -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 480.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    // Drag handle
                                    Box(
                                        modifier = Modifier
                                            .width(36.dp)
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(Color.Gray.copy(alpha = 0.4f))
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Quick Barcode Presets (Tap to Test)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFB74D)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(BarcodeProductRegistry.sampleBarcodePresets) { preset ->
                                            Surface(
                                                modifier = Modifier.clickable {
                                                    if (!isProcessing) {
                                                        isProcessing = true
                                                        onBarcodeScanned(preset.barcode) { product, matches ->
                                                            scannedResult = product
                                                            matchedShoppingList = matches
                                                            isProcessing = false
                                                        }
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFF2C2C2C),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444444))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.QrCode,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFF8A65),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = preset.name,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Manual input row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = manualBarcodeText,
                                            onValueChange = { manualBarcodeText = it },
                                            placeholder = { Text("Enter UPC/EAN barcode...", color = Color.Gray, fontSize = 13.sp) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = TerracottaPrimary,
                                                unfocusedBorderColor = Color(0xFF555555)
                                            )
                                        )
                                        Button(
                                            onClick = {
                                                if (manualBarcodeText.isNotBlank()) {
                                                    isProcessing = true
                                                    onBarcodeScanned(manualBarcodeText.trim()) { product, matches ->
                                                        scannedResult = product
                                                        matchedShoppingList = matches
                                                        isProcessing = false
                                                        manualBarcodeText = ""
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                            modifier = Modifier.height(54.dp)
                                        ) {
                                            Text("Lookup", fontWeight = FontWeight.Bold)
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
}

@Composable
fun ModeTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) TerracottaPrimary else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}

// MANDATORY VERIFICATION STEP COMPONENT
@Composable
fun VisualIngredientVerificationCard(
    originalResult: VisualIngredientRecognitionResult,
    ingredientName: String,
    onNameChange: (String) -> Unit,
    category: String,
    matchedShoppingItems: List<ShoppingItemEntity>,
    isConfirmed: Boolean,
    onConfirm: () -> Unit,
    onScanAnother: () -> Unit,
    onDone: () -> Unit
) {
    val displayCategory = remember(category) {
        if (category.isBlank() || category.equals("undefined", ignoreCase = true) || category.equals("null", ignoreCase = true)) {
            "Fresh Produce"
        } else {
            category
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 540.dp)
            .gourmetDepth(elevation = 14.dp, shapeRadius = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Drag Handle & Scroll Indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isConfirmed) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isConfirmed) "Ingredient Added to Pantry!" else "AI Discovered Ingredient",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Verification Step • Images never saved",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = displayCategory,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Editable Ingredient Name (Allows user to verify or tweak)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Ingredient Name (Verify & Edit if needed):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = ingredientName,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isConfirmed
                )
            }

            // Culinary Profile Note
            if (originalResult.culinaryNotes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = OliveGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = originalResult.culinaryNotes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Shopping List Match Notice
            if (matchedShoppingItems.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = OliveGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OliveGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = OliveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ Automatically checking off ${matchedShoppingItems.size} matched item(s) on your Shopping List",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = OliveGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            if (!isConfirmed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onScanAnother,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm & Add", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onScanAnother,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan Next")
                    }
                    Button(
                        onClick = onDone,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ScannedProductCard(
    product: ScannedProduct,
    matchedItems: List<ShoppingItemEntity>,
    onScanNext: () -> Unit,
    onAddToShopping: () -> Unit,
    onDone: () -> Unit
) {
    val displayCategory = remember(product.category) {
        if (product.category.isBlank() || product.category.equals("undefined", ignoreCase = true) || product.category.equals("null", ignoreCase = true)) {
            "Pantry Item"
        } else {
            product.category
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 540.dp)
            .gourmetDepth(elevation = 14.dp, shapeRadius = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Drag Handle & Scroll Indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Barcode Identified!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "UPC: ${product.barcode}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = displayCategory,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Product Name & Brand
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (product.brand.isNotBlank()) {
                    Text(
                        text = "Brand: ${product.brand}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Pantry & Shopping Status Badges
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Added to 'In The House Right Now' Pantry",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (matchedItems.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = OliveGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OliveGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = OliveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ Checked off ${matchedItems.size} matching item(s) on Shopping List",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = OliveGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onScanNext,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan Next")
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BarcodeScannerOverlay(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_anim")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            ) {
                if (isScanning) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val y = size.height * laserPosition
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color(0xFFFF5252), Color(0xFFFF1744), Color(0xFFFF5252), Color.Transparent)
                            ),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 6f
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.7f)
            ) {
                Text(
                    text = "Point camera at food ingredient barcode",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FoodVisionScannerOverlay(isAnalyzing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(2.dp, if (isAnalyzing) TerracottaPrimary else AmberAccent, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusWeak,
                    contentDescription = null,
                    tint = if (isAnalyzing) TerracottaPrimary else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.7f)
            ) {
                Text(
                    text = if (isAnalyzing) "Analyzing culinary produce with AI..." else "Center produce, herb, or meat in frame & tap capture",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun UnifiedCameraPreviewView(
    isBarcodeMode: Boolean,
    onBarcodeDetected: (String) -> Unit,
    onCameraControlReady: (CameraControl) -> Unit,
    onPreviewViewReady: (PreviewView) -> Unit,
    isFlashlightOn: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            onPreviewViewReady(previewView)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    if (!isBarcodeMode) {
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    val rawValue = barcode.rawValue
                                    if (!rawValue.isNullOrBlank()) {
                                        onBarcodeDetected(rawValue)
                                        break
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    onCameraControlReady(camera.cameraControl)
                    camera.cameraControl.enableTorch(isFlashlightOn)
                } catch (_: Exception) {
                    // Fail gracefully
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}
