package com.coolboost.performance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.presentation.viewmodel.CoolingViewModel
import com.coolboost.performance.presentation.viewmodel.ViewModelFactory
import com.coolboost.performance.ui.components.GlassCard
import com.coolboost.performance.ui.components.PrimaryActionButton
import com.coolboost.performance.ui.theme.AlertRed
import com.coolboost.performance.ui.theme.CoolCyan
import com.coolboost.performance.ui.theme.CoolCyanDim
import com.coolboost.performance.utils.FormatUtils

@Composable
fun CoolingScreen(container: AppContainer) {
    val viewModel: CoolingViewModel = viewModel(factory = ViewModelFactory(container))
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Cooling Center", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(20.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AcUnit, contentDescription = null, tint = CoolCyan)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Smart Cooling", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "Stops idle background apps and clears leftover cache without interrupting active work.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            PrimaryActionButton(
                text = "Run Smart Cooling",
                onClick = { viewModel.runSmartCooling() },
                isLoading = uiState.isRunning && uiState.mode == "smart",
                enabled = !uiState.isRunning,
                modifier = Modifier.fillMaxWidth(),
                containerColor = CoolCyanDim
            )
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Whatshot, contentDescription = null, tint = AlertRed)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Extreme Cooling", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "Aggressively stops all non-essential background apps. Use only when the device is genuinely overheating.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            PrimaryActionButton(
                text = "Run Extreme Cooling",
                onClick = { viewModel.runExtremeCooling() },
                isLoading = uiState.isRunning && uiState.mode == "extreme",
                enabled = !uiState.isRunning,
                modifier = Modifier.fillMaxWidth(),
                containerColor = AlertRed
            )
            uiState.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = AlertRed, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Game Mode", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "Silences background notifications and idle-app cleanup while you play.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(checked = uiState.gameModeActive, onCheckedChange = { viewModel.toggleGameMode() })
            }
        }

        uiState.lastResult?.let { result ->
            Spacer(Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Last Cooling Result", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Text("Apps closed: ${result.appsClosed}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("RAM freed: ${FormatUtils.formatBytes(result.ramFreedBytes)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Cache cleared: ${FormatUtils.formatBytes(result.cacheClearedBytes)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Temperature change: ${FormatUtils.formatTemp(result.tempDelta)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
