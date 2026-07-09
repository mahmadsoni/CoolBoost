package com.coolboost.performance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.domain.repository.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val container: AppContainer) : ViewModel() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            container.settingsRepository.observeSettings().collect {
                _settings.value = it
            }
        }
    }

    fun setAutoClean(enabled: Boolean) = update { it.copy(autoCleanEnabled = enabled) }
    fun setAutoOptimize(enabled: Boolean) = update { it.copy(autoOptimizeEnabled = enabled) }
    fun setBackgroundMonitoring(enabled: Boolean) = update { it.copy(backgroundMonitoringEnabled = enabled) }
    fun setDarkMode(enabled: Boolean) = update { it.copy(darkModeEnabled = enabled) }
    fun setDynamicColor(enabled: Boolean) = update { it.copy(dynamicColorEnabled = enabled) }
    fun setNotifications(enabled: Boolean) = update { it.copy(notificationsEnabled = enabled) }
    fun setExtremeConfirmation(enabled: Boolean) = update { it.copy(extremeCoolingConfirmation = enabled) }
    fun setGameModeAutoDetect(enabled: Boolean) = update { it.copy(gameModeAutoDetect = enabled) }

    private fun update(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            container.settingsRepository.updateSettings(transform)
        }
    }
}
