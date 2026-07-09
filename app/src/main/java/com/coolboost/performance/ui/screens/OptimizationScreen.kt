package com.coolboost.performance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.presentation.viewmodel.OptimizationViewModel
import com.coolboost.performance.presentation.viewmodel.ViewModelFactory
import com.coolboost.performance.ui.components.GlassCard
import com.coolboost.performance.ui.components.PrimaryActionButton
import com.coolboost.performance.ui.theme.AlertRed
import com.coolboost.performance.ui.theme.CoolCyanDim
import com.coolboost.performance.utils.FormatUtils

@Composable
fun OptimizationScreen(container: AppContainer, onRequestUsageAccess: () -> Unit) {
    val viewModel: OptimizationViewModel = viewModel(factory = ViewModelFactory(container))
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Background App Manager", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        if (!uiState.hasUsageAccess) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Permission needed", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Text(
                    "CoolBoost needs Usage Access to see which apps are running in the background. This permission never leaves your device — it's used only for on-device analysis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                PrimaryActionButton(text = "Grant Access", onClick = onRequestUsageAccess, modifier = Modifier.fillMaxWidth())
            }
            return@Column
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryActionButton(
                text = "Optimize Now",
                onClick = { viewModel.runFullOptimization() },
                isLoading = uiState.isOptimizing,
                modifier = Modifier.weight(1f),
                containerColor = CoolCyanDim
            )
            OutlinedButton(
                onClick = { viewModel.clearAllCache() },
                modifier = Modifier.weight(1f).height(52.dp)
            ) {
                Icon(Icons.Filled.CleaningServices, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Clear Cache")
            }
        }

        if (uiState.lastCacheFreedBytes > 0) {
            Spacer(Modifier.height(10.dp))
            Text(
                "Freed ${FormatUtils.formatBytes(uiState.lastCacheFreedBytes)} of cache",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.selectedPackages.isNotEmpty()) {
            PrimaryActionButton(
                text = "Close ${uiState.selectedPackages.size} selected",
                onClick = { viewModel.closeSelected() },
                modifier = Modifier.fillMaxWidth(),
                containerColor = AlertRed
            )
            Spacer(Modifier.height(16.dp))
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.apps.isEmpty()) {
            Text("No background apps detected right now.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(uiState.apps, key = { it.packageName }) { app ->
                    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = app.packageName in uiState.selectedPackages,
                                    onCheckedChange = { viewModel.toggleSelection(app.packageName) }
                                )
                                Column {
                                    Text(app.appName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                    Text(
                                        FormatUtils.formatBytes(app.memoryBytes),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.toggleWhitelist(app) }) {
                                Icon(
                                    if (app.isWhitelisted) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Whitelist",
                                    tint = if (app.isWhitelisted) CoolCyanDim else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
