package com.coolboost.performance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.domain.model.BatteryHealthStatus
import com.coolboost.performance.presentation.viewmodel.AnalyticsViewModel
import com.coolboost.performance.presentation.viewmodel.ViewModelFactory
import com.coolboost.performance.ui.components.CircularGauge
import com.coolboost.performance.ui.components.GlassCard
import com.coolboost.performance.ui.theme.AlertRed
import com.coolboost.performance.ui.theme.SuccessGreen
import com.coolboost.performance.ui.theme.WarningAmber

@Composable
fun BatteryHealthScreen(container: AppContainer) {
    val viewModel: AnalyticsViewModel = viewModel(factory = ViewModelFactory(container))
    val uiState by viewModel.uiState.collectAsState()
    val battery = uiState.battery

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Battery Health", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        if (battery == null) {
            Text("Reading battery data…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            return@Column
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CircularGauge(
                    progress = battery.levelPercent / 100f,
                    label = "Charge",
                    valueText = "${battery.levelPercent}%",
                    color = SuccessGreen,
                    gaugeSize = 140.dp
                )
                CircularGauge(
                    progress = battery.capacityHealthPercent / 100f,
                    label = "Est. Health",
                    valueText = "${battery.capacityHealthPercent}%",
                    color = healthColor(battery.health),
                    gaugeSize = 140.dp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            InfoRow("Status", if (battery.isCharging) "Charging" else "Discharging")
            InfoRow("Temperature", "${battery.temperatureCelsius}°C")
            InfoRow("Voltage", "${battery.voltageMilliVolts} mV")
            InfoRow("Health Flag", battery.health.name.lowercase().replaceFirstChar { it.uppercase() })
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Estimated health is derived from the battery's reported health flag and thermal stress " +
                "history, since Android does not expose precise wear-cycle data without OEM-specific APIs.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun healthColor(status: BatteryHealthStatus): androidx.compose.ui.graphics.Color = when (status) {
    BatteryHealthStatus.GOOD -> SuccessGreen
    BatteryHealthStatus.OVERHEAT, BatteryHealthStatus.OVER_VOLTAGE -> AlertRed
    BatteryHealthStatus.COLD, BatteryHealthStatus.UNKNOWN -> WarningAmber
    BatteryHealthStatus.DEAD -> AlertRed
}
