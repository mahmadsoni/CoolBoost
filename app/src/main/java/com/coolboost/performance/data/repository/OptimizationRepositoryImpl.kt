package com.coolboost.performance.data.repository

import com.coolboost.performance.data.local.dao.WhitelistDao
import com.coolboost.performance.data.local.entity.WhitelistEntity
import com.coolboost.performance.domain.model.RunningAppInfo
import com.coolboost.performance.domain.repository.OptimizationRepository
import com.coolboost.performance.monitoring.RunningAppsMonitor
import com.coolboost.performance.optimization.AppOptimizer
import com.coolboost.performance.optimization.BackgroundAppManager
import com.coolboost.performance.optimization.CacheCleaner

class OptimizationRepositoryImpl(
    private val runningAppsMonitor: RunningAppsMonitor,
    private val backgroundAppManager: BackgroundAppManager,
    private val cacheCleaner: CacheCleaner,
    private val appOptimizer: AppOptimizer,
    private val whitelistDao: WhitelistDao
) : OptimizationRepository {

    override suspend fun getBackgroundApps(): List<RunningAppInfo> {
        val whitelist = whitelistDao.getAll().toSet()
        return runningAppsMonitor.getRecentlyActiveApps().map {
            it.copy(isWhitelisted = it.packageName in whitelist)
        }
    }

    override suspend fun closeApp(packageName: String): Boolean =
        backgroundAppManager.closeApp(packageName)

    override suspend fun closeApps(packageNames: List<String>): Int =
        backgroundAppManager.closeApps(packageNames)

    override suspend fun clearAppCache(packageName: String): Long =
        cacheCleaner.clearOrphanCache()

    override suspend fun clearAllCache(): Long = cacheCleaner.clearAllAppCache()

    override suspend fun addToWhitelist(packageName: String) {
        whitelistDao.insert(WhitelistEntity(packageName))
    }

    override suspend fun removeFromWhitelist(packageName: String) {
        whitelistDao.delete(packageName)
    }

    override suspend fun getWhitelist(): List<String> = whitelistDao.getAll()

    override suspend fun runFullOptimization(): Long {
        val idleApps = getBackgroundApps().filterNot { it.isWhitelisted }.map { it.packageName }
        return appOptimizer.optimize(idleApps)
    }
}
