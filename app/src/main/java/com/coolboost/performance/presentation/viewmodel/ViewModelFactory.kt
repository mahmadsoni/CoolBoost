package com.coolboost.performance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coolboost.performance.core.AppContainer

/** Simple factory wiring every ViewModel to the shared AppContainer (manual DI, no Hilt). */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(container) as T
            modelClass.isAssignableFrom(CoolingViewModel::class.java) ->
                CoolingViewModel(container) as T
            modelClass.isAssignableFrom(OptimizationViewModel::class.java) ->
                OptimizationViewModel(container) as T
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) ->
                AnalyticsViewModel(container) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(container) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
