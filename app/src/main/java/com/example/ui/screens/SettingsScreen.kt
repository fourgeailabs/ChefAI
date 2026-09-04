package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.ChefViewModel
import com.example.ui.components.gourmetDepth

@Composable
fun SettingsScreen(
    viewModel: ChefViewModel,
    onNavigateWhatsNew: () -> Unit,
    onNavigateAbout: () -> Unit
) {
    val updateState by viewModel.updateState.collectAsState()

    if (updateState.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUpdateDialog() },
            title = { Text("App Update Status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Installed Version: v${updateState.currentVersion}")
                    Text("Latest Release: v${updateState.latestVersion}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You are on the latest official build with the Celebrity Master Chef AI Engine (Gordon Ramsay, Julia Child, Anthony Bourdain, Martha Stewart, Rachael Ray, Bobby Flay), Cravings Selector, and In-the-House Pantry Intelligence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissUpdateDialog() }) {
                    Text("Got it")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { viewModel.dismissUpdateDialog() }) {
                        Text("Remind Later")
                    }
                    TextButton(onClick = { viewModel.dismissUpdateDialog() }) {
                        Text("Skip Version")
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Check for updates
        SettingsItem(
            icon = Icons.Default.SystemUpdate,
            title = "Check for Updates",
            subtitle = if (updateState.isChecking) "Checking GitHub Releases..." else "Current Version v1.03.00",
            onClick = { viewModel.checkForUpdates(isManual = true) }
        )

        SettingsItem(
            icon = Icons.Default.NewReleases,
            title = "What's New",
            subtitle = "Release notes for v1.03.00 & historical changelog",
            onClick = onNavigateWhatsNew
        )

        SettingsItem(
            icon = Icons.Default.Info,
            title = "About ChefAI Studio",
            subtitle = "Created by FourgeAI LABS • GitHub repository",
            onClick = onNavigateAbout
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "ChefAI Studio v1.03.00 • Master Chef Culinary Engine",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .gourmetDepth(elevation = 6.dp, shapeRadius = 18.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
