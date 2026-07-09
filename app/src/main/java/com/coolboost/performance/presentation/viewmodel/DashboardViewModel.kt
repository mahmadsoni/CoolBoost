package com.coolboost.performance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.domain.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardUiState(
    val snapshot: DashboardSnapshot? = null,
    val insights: List<AiInsight> = emptyList(),
    val isCooling: Boolean = false,
    val lastCoolingResult: CoolingResult? = null,
    val isLoading: Boolean = true
)

class DashboardViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        startObserving()
    }

    private fun startObserving() {
        pollingJob = viewModelScope.launch {
            combine(
                container.thermalMonitor.observe(),
                container.ramMonitor.observe(),
                container.cpuMonitor.observe(),
                container.batteryMonitor.observe()
            ) { thermal, memory, cpu, battery ->
                Quad(thermal, memory, cpu, battery)
            }.collect { (thermal, memory, cpu, battery) ->
                val storage = container.storageMonitor.readCurrent()
                val fps = FpsState(currentFps = 60.0, averageFps = 60.0, jankCount = 0, isSmooth = true)
                val score = computeScore(thermal, memory, cpu, fps)

                val snapshot = DashboardSnapshot(
                    thermal = thermal,
                    memory = memory,
                    storage = storage,
                    cpu = cpu,
                    fps = fps,
                    battery = battery,
                    performanceScore = score,
                    activeCoolingMode = container.coolingManager.getActiveMode()
                )

                val apps = container.runningAppsMonitor.getRecentlyActiveApps()
                val insights = container.generateAiInsightsUseCase(snapshot, apps)

                _uiState.value = _uiState.value.copy(
                    snapshot = snapshot,
                    insights = insights,
                    isLoading = false
                )
            }
        }
    }

    private fun computeScore(thermal: ThermalState, memory: MemoryState, cpu: CpuState, fps: FpsState): Int {
        val thermalScore = when (thermal.level) {
            ThermalLevel.NORMAL -> 100
            ThermalLevel.WARM -> 75
            ThermalLevel.HOT -> 45
            ThermalLevel.CRITICAL -> 15
        }
        val ramScore = (100 - memory.usedPercent).coerceIn(0, 100)
        val cpuScore = (100 - cpu.usagePercent).coerceIn(0, 100)
        return ((thermalScore * 0.45) + (ramScore * 0.3) + (cpuScore * 0.25)).toInt().coerceIn(0, 100)
    }

    fun runSmartCooling() {
        if (_uiState.value.isCooling) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCooling = true)
            val result = container.runSmartCoolingUseCase()
            container.usageTracker.recordCooling(result)
            _uiState.value = _uiState.value.copy(isCooling = false, lastCoolingResult = result)
        }
    }

    fun runExtremeCooling() {
        if (_uiState.value.isCooling) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCooling = true)
            try {
                val result = container.runExtremeCoolingUseCase()
                container.usageTracker.recordCooling(result)
                _uiState.value = _uiState.value.copy(isCooling = false, lastCoolingResult = result)
            } catch (e: IllegalStateException) {
                _uiState.value = _uiState.value.copy(isCooling = false)
            }
        }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        super.onCleared()
    }

    private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
}
