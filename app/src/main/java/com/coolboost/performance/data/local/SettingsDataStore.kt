package com.coolboost.performance.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.repository.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

/** DataStore-backed persistence for app settings — fully offline, no cloud sync. */
class SettingsDataStore(private val context: Context) {

    private object Keys {
        val AUTO_CLEAN = booleanPreferencesKey("auto_clean_enabled")
        val AUTO_OPTIMIZE = booleanPreferencesKey("auto_optimize_enabled")
        val BACKGROUND_MONITORING = booleanPreferencesKey("background_monitoring_enabled")
        val DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color_enabled")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val EXTREME_CONFIRM = booleanPreferencesKey("extreme_cooling_confirmation")
        val GAME_MODE_AUTO = booleanPreferencesKey("game_mode_auto_detect")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            autoCleanEnabled = prefs[Keys.AUTO_CLEAN] ?: true,
            autoOptimizeEnabled = prefs[Keys.AUTO_OPTIMIZE] ?: true,
            backgroundMonitoringEnabled = prefs[Keys.BACKGROUND_MONITORING] ?: true,
            darkModeEnabled = prefs[Keys.DARK_MODE] ?: true,
            dynamicColorEnabled = prefs[Keys.DYNAMIC_COLOR] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            extremeCoolingConfirmation = prefs[Keys.EXTREME_CONFIRM] ?: true,
            gameModeAutoDetect = prefs[Keys.GAME_MODE_AUTO] ?: true
        )
    }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                autoCleanEnabled = prefs[Keys.AUTO_CLEAN] ?: true,
                autoOptimizeEnabled = prefs[Keys.AUTO_OPTIMIZE] ?: true,
                backgroundMonitoringEnabled = prefs[Keys.BACKGROUND_MONITORING] ?: true,
                darkModeEnabled = prefs[Keys.DARK_MODE] ?: true,
                dynamicColorEnabled = prefs[Keys.DYNAMIC_COLOR] ?: true,
                notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
                extremeCoolingConfirmation = prefs[Keys.EXTREME_CONFIRM] ?: true,
                gameModeAutoDetect = prefs[Keys.GAME_MODE_AUTO] ?: true
            )
            val updated = transform(current)
            prefs[Keys.AUTO_CLEAN] = updated.autoCleanEnabled
            prefs[Keys.AUTO_OPTIMIZE] = updated.autoOptimizeEnabled
            prefs[Keys.BACKGROUND_MONITORING] = updated.backgroundMonitoringEnabled
            prefs[Keys.DARK_MODE] = updated.darkModeEnabled
            prefs[Keys.DYNAMIC_COLOR] = updated.dynamicColorEnabled
            prefs[Keys.NOTIFICATIONS] = updated.notificationsEnabled
            prefs[Keys.EXTREME_CONFIRM] = updated.extremeCoolingConfirmation
            prefs[Keys.GAME_MODE_AUTO] = updated.gameModeAutoDetect
        }
    }
}
