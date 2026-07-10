@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = com.coolboost.performance.ui.theme.CoolCyan,
                    modifier = Modifier.height(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Дар бораи барнома", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(10.dp))
            Text(DeviceUtils.deviceLabel(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(DeviceUtils.androidVersionLabel(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            Text(
                "CoolBoost комилан дар дохили телефони шумо кор мекунад: root лозим нест, " +
                    "ягон маълумот аз телефон берун намеравад, реклама вуҷуд надорад. " +
                    "Ҳарорат, RAM, CPU ва батарея аз API-ҳои расмии Android хонда мешаванд, " +
                    "на аз тахмин.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            Spacer(Modifier.height(14.dp))

            Text("Технологияҳо", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Kotlin", "Jetpack Compose", "MVVM", "Coroutines & Flow",
                    "Room", "Material 3"
                ).forEach { TechBadge(it) }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            Spacer(Modifier.height(16.dp))

            Text(
                "Тарроҳӣ ва барномасозӣ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Маҳмадсони",
                style = MaterialTheme.typography.titleLarge,
                color = com.coolboost.performance.ui.theme.CoolCyan
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Сохта шудааст бо диққат ба меъморӣ, амният ва суръат — на танҳо намуди зоҳирӣ.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TechBadge(label: String) {
    androidx.compose.material3.Surface(
        color = com.coolboost.performance.ui.theme.CoolCyan.copy(alpha = 0.12f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        modifier = Modifier
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = com.coolboost.performance.ui.theme.CoolCyan,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
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
