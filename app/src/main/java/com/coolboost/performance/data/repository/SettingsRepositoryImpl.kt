package com.coolboost.performance.data.repository

import com.coolboost.performance.data.local.SettingsDataStore
import com.coolboost.performance.domain.repository.AppSettings
import com.coolboost.performance.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> = dataStore.settingsFlow

    override suspend fun updateSettings(transform: (AppSettings) -> AppSettings) {
        dataStore.update(transform)
    }
}
