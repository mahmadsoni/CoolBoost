package com.coolboost.performance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.presentation.viewmodel.SettingsViewModel
import com.coolboost.performance.presentation.viewmodel.ViewModelFactory
import com.coolboost.performance.ui.components.GlassCard
import com.coolboost.performance.utils.DeviceUtils

@Composable
fun SettingsScreen(container: AppContainer) {
    val viewModel: SettingsViewModel = viewModel(factory = ViewModelFactory(container))
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            SettingSwitchRow("Auto Clean Cache", "Periodically clear leftover cache in the background", settings.autoCleanEnabled, viewModel::setAutoClean)
            SettingDivider()
            SettingSwitchRow("Auto Optimize", "Periodically close idle background apps", settings.autoOptimizeEnabled, viewModel::setAutoOptimize)
            SettingDivider()
            SettingSwitchRow("Background Monitoring", "Keep thermal/RAM monitoring active when app is closed", settings.backgroundMonitoringEnabled, viewModel::setBackgroundMonitoring)
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            SettingSwitchRow("Dark Mode", "Use dark theme across the app", settings.darkModeEnabled, viewModel::setDarkMode)
            SettingDivider()
            SettingSwitchRow("Dynamic Color", "Match system wallpaper colors (Android 12+)", settings.dynamicColorEnabled, viewModel::setDynamicColor)
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            SettingSwitchRow("Notifications", "Show overheating and cooling alerts", settings.notificationsEnabled, viewModel::setNotifications)
            SettingDivider()
            SettingSwitchRow("Confirm Extreme Cooling", "Ask before running Extreme Cooling", settings.extremeCoolingConfirmation, viewModel::setExtremeConfirmation)
            SettingDivider()
            SettingSwitchRow("Auto-detect Game Mode", "Automatically enable Game Mode when a game is launched", settings.gameModeAutoDetect, viewModel::setGameModeAutoDetect)
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("About", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text(DeviceUtils.deviceLabel(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(DeviceUtils.androidVersionLabel(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(
                "CoolBoost works entirely on-device: no root required, no data leaves your phone, no ads.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingDivider() {
    Spacer(Modifier.height(12.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
    Spacer(Modifier.height(12.dp))
}
