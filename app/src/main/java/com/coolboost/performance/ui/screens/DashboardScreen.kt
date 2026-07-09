package com.coolboost.performance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.ThermalLevel
import com.coolboost.performance.presentation.viewmodel.DashboardViewModel
import com.coolboost.performance.presentation.viewmodel.ViewModelFactory
import com.coolboost.performance.ui.components.*
import com.coolboost.performance.ui.theme.*
import com.coolboost.performance.utils.FormatUtils

@Composable
fun DashboardScreen(
    container: AppContainer,
    onNavigateToCooling: () -> Unit,
    onNavigateToOptimization: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val viewModel: DashboardViewModel = viewModel(factory = ViewModelFactory(container))
    val uiState by viewModel.uiState.collectAsState()
    val snapshot = uiState.snapshot

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "CoolBoost",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Phone Cooling & Performance Booster",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            if (uiState.isLoading || snapshot == null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CoolCyan)
                    }
                }
            } else {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CircularGauge(
                            progress = snapshot.performanceScore / 100f,
                            label = "Health Score",
                            valueText = "${snapshot.performanceScore}",
                            color = scoreColor(snapshot.performanceScore),
                            gaugeSize = 150.dp
                        )
                        CircularGauge(
                            progress = (snapshot.thermal.batteryTempCelsius / 55f).coerceIn(0f, 1f),
                            label = "Temperature",
                            valueText = FormatUtils.formatTemp(snapshot.thermal.batteryTempCelsius),
                            color = thermalColor(snapshot.thermal.level),
                            gaugeSize = 150.dp
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    ThermalStatusBadge(snapshot.thermal.level)
                }

                Spacer(Modifier.height(16.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Live Metrics", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(16.dp))
                    MetricRow(
                        icon = Icons.Filled.Memory,
                        label = "RAM",
                        valueText = "${snapshot.memory.usedPercent}%",
                        progress = snapshot.memory.usedPercent / 100f,
                        progressColor = CoolCyan
                    )
                    Spacer(Modifier.height(14.dp))
                    MetricRow(
                        icon = Icons.Filled.Speed,
                        label = "CPU",
                        valueText = "${snapshot.cpu.usagePercent}%",
                        progress = snapshot.cpu.usagePercent / 100f,
                        progressColor = BoosterOrange
                    )
                    Spacer(Modifier.height(14.dp))
                    MetricRow(
                        icon = Icons.Filled.Storage,
                        label = "Storage",
                        valueText = "${snapshot.storage.usedPercent}%",
                        progress = snapshot.storage.usedPercent / 100f,
                        progressColor = WarningAmber
                    )
                    Spacer(Modifier.height(14.dp))
                    MetricRow(
                        icon = Icons.Filled.BatteryChargingFull,
                        label = "Battery",
                        valueText = "${snapshot.battery.levelPercent}%",
                        progress = snapshot.battery.levelPercent / 100f,
                        progressColor = SuccessGreen
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryActionButton(
                        text = "Smart Cooling",
                        onClick = { viewModel.runSmartCooling() },
                        isLoading = uiState.isCooling,
                        modifier = Modifier.weight(1f),
                        containerColor = CoolCyanDim
                    )
                    PrimaryActionButton(
                        text = "Extreme Cooling",
                        onClick = { viewModel.runExtremeCooling() },
                        isLoading = uiState.isCooling,
                        modifier = Modifier.weight(1f),
                        containerColor = AlertRed
                    )
                }
            }
        }

        if (uiState.insights.isNotEmpty()) {
            item {
                Text("AI Insights", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            }
            items(uiState.insights) { insight ->
                InsightCard(insight)
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun ThermalStatusBadge(level: ThermalLevel) {
    val (text, color) = when (level) {
        ThermalLevel.NORMAL -> "Normal — running cool" to SuccessGreen
        ThermalLevel.WARM -> "Warm — keep an eye on it" to WarningAmber
        ThermalLevel.HOT -> "Hot — cooling recommended" to BoosterOrange
        ThermalLevel.CRITICAL -> "Critical — cool down now" to AlertRed
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

private fun scoreColor(score: Int): androidx.compose.ui.graphics.Color = when {
    score >= 80 -> SuccessGreen
    score >= 55 -> WarningAmber
    else -> AlertRed
}

private fun thermalColor(level: ThermalLevel): androidx.compose.ui.graphics.Color = when (level) {
    ThermalLevel.NORMAL -> CoolCyan
    ThermalLevel.WARM -> WarningAmber
    ThermalLevel.HOT -> BoosterOrange
    ThermalLevel.CRITICAL -> AlertRed
}
