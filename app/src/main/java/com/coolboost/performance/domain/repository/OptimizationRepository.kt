package com.coolboost.performance.domain.repository

import com.coolboost.performance.domain.model.RunningAppInfo

interface OptimizationRepository {
    suspend fun getBackgroundApps(): List<RunningAppInfo>
    suspend fun closeApp(packageName: String): Boolean
    suspend fun closeApps(packageNames: List<String>): Int
    suspend fun clearAppCache(packageName: String): Long
    suspend fun clearAllCache(): Long
    suspend fun addToWhitelist(packageName: String)
    suspend fun removeFromWhitelist(packageName: String)
    suspend fun getWhitelist(): List<String>
    suspend fun runFullOptimization(): Long
}
