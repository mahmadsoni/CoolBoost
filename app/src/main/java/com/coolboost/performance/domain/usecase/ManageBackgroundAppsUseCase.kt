package com.coolboost.performance.domain.usecase

import com.coolboost.performance.domain.model.RunningAppInfo
import com.coolboost.performance.domain.repository.OptimizationRepository

class ManageBackgroundAppsUseCase(
    private val repository: OptimizationRepository
) {
    suspend fun listApps(): List<RunningAppInfo> = repository.getBackgroundApps()
    suspend fun closeSelected(packages: List<String>): Int = repository.closeApps(packages)
    suspend fun closeOne(packageName: String): Boolean = repository.closeApp(packageName)
    suspend fun whitelist(packageName: String) = repository.addToWhitelist(packageName)
    suspend fun unwhitelist(packageName: String) = repository.removeFromWhitelist(packageName)
}
