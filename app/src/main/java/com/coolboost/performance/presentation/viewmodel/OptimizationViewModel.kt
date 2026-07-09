package com.coolboost.performance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.domain.model.RunningAppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OptimizationUiState(
    val apps: List<RunningAppInfo> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isOptimizing: Boolean = false,
    val hasUsageAccess: Boolean = true,
    val lastCacheFreedBytes: Long = 0L
)

class OptimizationViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(OptimizationUiState())
    val uiState: StateFlow<OptimizationUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val hasAccess = container.runningAppsMonitor.hasUsageAccess()
            val apps = if (hasAccess) container.manageBackgroundAppsUseCase.listApps() else emptyList()
            _uiState.value = _uiState.value.copy(
                apps = apps,
                isLoading = false,
                hasUsageAccess = hasAccess
            )
        }
    }

    fun toggleSelection(packageName: String) {
        val current = _uiState.value.selectedPackages
        _uiState.value = _uiState.value.copy(
            selectedPackages = if (packageName in current) current - packageName else current + packageName
        )
    }

    fun closeSelected() {
        viewModelScope.launch {
            val selected = _uiState.value.selectedPackages.toList()
            if (selected.isEmpty()) return@launch
            _uiState.value = _uiState.value.copy(isOptimizing = true)
            container.manageBackgroundAppsUseCase.closeSelected(selected)
            _uiState.value = _uiState.value.copy(selectedPackages = emptySet(), isOptimizing = false)
            loadApps()
        }
    }

    fun toggleWhitelist(app: RunningAppInfo) {
        viewModelScope.launch {
            if (app.isWhitelisted) {
                container.manageBackgroundAppsUseCase.unwhitelist(app.packageName)
            } else {
                container.manageBackgroundAppsUseCase.whitelist(app.packageName)
            }
            loadApps()
        }
    }

    fun runFullOptimization() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOptimizing = true)
            val freed = container.optimizationRepository.runFullOptimization()
            _uiState.value = _uiState.value.copy(isOptimizing = false, lastCacheFreedBytes = freed)
            loadApps()
        }
    }

    fun clearAllCache() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOptimizing = true)
            val freed = container.autoCleanCacheUseCase()
            _uiState.value = _uiState.value.copy(isOptimizing = false, lastCacheFreedBytes = freed)
        }
    }
}
