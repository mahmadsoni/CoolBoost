package com.coolboost.performance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.domain.model.BatteryState
import com.coolboost.performance.domain.repository.PerformanceHistoryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class AnalyticsUiState(
    val history: List<PerformanceHistoryPoint> = emptyList(),
    val averageScore: Int = 0,
    val battery: BatteryState? = null,
    val coolingRunsToday: Int = 0,
    val totalRamFreed: Long = 0L,
    val totalCacheFreed: Long = 0L,
    val isLoading: Boolean = true
)

class AnalyticsViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
        loadBattery()
    }

    private fun observeHistory() {
        val since = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
        viewModelScope.launch {
            container.getPerformanceHistoryUseCase(since).collect { points ->
                val avg = container.analyticsRepository.getAverageScore(since)
                _uiState.value = _uiState.value.copy(
                    history = points,
                    averageScore = avg,
                    isLoading = false,
                    coolingRunsToday = container.usageTracker.getCoolingRunsToday(),
                    totalRamFreed = container.usageTracker.getTotalRamFreedBytes(),
                    totalCacheFreed = container.usageTracker.getTotalCacheFreedBytes()
                )
            }
        }
    }

    private fun loadBattery() {
        viewModelScope.launch {
            container.batteryMonitor.observe().collect { battery ->
                _uiState.value = _uiState.value.copy(battery = battery)
            }
        }
    }
}
