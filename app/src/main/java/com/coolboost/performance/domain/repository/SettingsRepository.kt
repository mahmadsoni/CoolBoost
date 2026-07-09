package com.coolboost.performance.domain.repository

import kotlinx.coroutines.flow.Flow

data class AppSettings(
    val autoCleanEnabled: Boolean = true,
    val autoOptimizeEnabled: Boolean = true,
    val backgroundMonitoringEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val dynamicColorEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val extremeCoolingConfirmation: Boolean = true,
    val gameModeAutoDetect: Boolean = true
)

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun updateSettings(transform: (AppSettings) -> AppSettings)
}
