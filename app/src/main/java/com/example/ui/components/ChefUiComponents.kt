package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.GenerationProgressState
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.theme.TerracottaSecondary

// Custom Multi-Level Shadow Modifiers for rich tactile depth
fun Modifier.gourmetDepth(
    elevation: Dp = 8.dp,
    shapeRadius: Dp = 20.dp,
    hasBorderGlow: Boolean = true
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = RoundedCornerShape(shapeRadius),
        spotColor = Color(0xFFFF5722).copy(alpha = 0.35f),
        ambientColor = Color(0xFF000000).copy(alpha = 0.25f)
    )
    .then(
        if (hasBorderGlow) {
            Modifier.border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF7043).copy(alpha = 0.35f),
                            Color(0x10000000)
                        )
                    )
                ),
                shape = RoundedCornerShape(shapeRadius)
            )
        } else Modifier
    )

fun Modifier.gourmetButtonShadow(
    elevation: Dp = 10.dp,
    shapeRadius: Dp = 16.dp
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = RoundedCornerShape(shapeRadius),
        spotColor = Color(0xFFFF5722).copy(alpha = 0.45f),
        ambientColor = Color(0xFF000000).copy(alpha = 0.35f)
    )

@Composable
fun GourmetGradientCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 10.dp,
    shapeRadius: Dp = 22.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.gourmetDepth(elevation = elevation, shapeRadius = shapeRadius),
        shape = RoundedCornerShape(shapeRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(18.dp),
            content = content
        )
    }
}

// Full Material 3 Progress Indicator Overlay Dialog with Animated Percentage & Culinary Stage Tracker
@Composable
fun GeminiProgressModal(
    progressState: GenerationProgressState,
    onDismiss: () -> Unit = {}
) {
    if (progressState.isGenerating) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .gourmetDepth(elevation = 16.dp, shapeRadius = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Material 3 Circular Progress Indicator
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 6.dp,
                            color = TerracottaPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (progressState.chefName.isNotBlank()) "${progressState.chefName} AI Studio" else "ChefAI Culinary Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (progressState.chefName.isNotBlank()) {
                            Text(
                                text = "Channeling Culinary Icon",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Animated stage message
                    Text(
                        text = progressState.stageMessage.ifEmpty { "Brewing culinary alchemy..." },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Material 3 Linear Progress Indicator
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Formulating Recipe",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(progressState.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = TerracottaPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    if (progressState.chefTipPreview.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = progressState.chefTipPreview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Nutrition Macro Item with Visual Meter
@Composable
fun MacroItem(
    label: String,
    value: String,
    percent: Float,
    barColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .gourmetDepth(elevation = 4.dp, shapeRadius = 14.dp, hasBorderGlow = false),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = barColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                }
                Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { percent.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}
