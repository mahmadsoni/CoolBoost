package com.coolboost.performance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.domain.model.CoolingResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CoolingUiState(
    val isRunning: Boolean = false,
    val mode: String? = null,
    val lastResult: CoolingResult? = null,
    val gameModeActive: Boolean = false,
    val error: String? = null
)

class CoolingViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(CoolingUiState())
    val uiState: StateFlow<CoolingUiState> = _uiState.asStateFlow()

    fun runSmartCooling() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunning = true, mode = "smart", error = null)
            val result = container.runSmartCoolingUseCase()
            container.usageTracker.recordCooling(result)
            _uiState.value = _uiState.value.copy(isRunning = false, lastResult = result)
        }
    }

    fun runExtremeCooling() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunning = true, mode = "extreme", error = null)
            try {
                val result = container.runExtremeCoolingUseCase()
                container.usageTracker.recordCooling(result)
                _uiState.value = _uiState.value.copy(isRunning = false, lastResult = result)
            } catch (e: IllegalStateException) {
                _uiState.value = _uiState.value.copy(isRunning = false, error = e.message)
            }
        }
    }

    fun toggleGameMode() {
        viewModelScope.launch {
            val newState = !_uiState.value.gameModeActive
            if (newState) {
                container.coolingRepository.enableGameMode(null)
            } else {
                container.coolingRepository.disableGameMode()
            }
            _uiState.value = _uiState.value.copy(gameModeActive = newState)
        }
    }
}
