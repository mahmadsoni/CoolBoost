package com.coolboost.performance.cooling

import com.coolboost.performance.domain.model.CoolingMode
import com.coolboost.performance.domain.model.CoolingResult
import com.coolboost.performance.domain.model.RunningAppInfo
import com.coolboost.performance.monitoring.RunningAppsMonitor
import com.coolboost.performance.monitoring.ThermalMonitor
import com.coolboost.performance.optimization.BackgroundAppManager
import com.coolboost.performance.optimization.CacheCleaner

/**
 * Smart Cooling: a balanced pass that only stops idle background apps that
 * are NOT whitelisted and haven't been used recently, then clears leftover
 * app cache. Designed to run frequently without disrupting active workflows.
 */
class SmartCoolingEngine(
    private val runningAppsMonitor: RunningAppsMonitor,
    private val backgroundAppManager: BackgroundAppManager,
    private val cacheCleaner: CacheCleaner,
    private val thermalMonitor: ThermalMonitor
) {
    suspend fun run(): CoolingResult {
        val start = System.currentTimeMillis()
        val tempBefore = thermalMonitor.readCurrent().batteryTempCelsius

        val candidates = runningAppsMonitor.getRecentlyActiveApps()
            .filterNot { it.isWhitelisted }
            .filter { isIdleCandidate(it) }
            .sortedByDescending { it.memoryBytes }
            .take(15)

        val closed = backgroundAppManager.closeApps(candidates.map { it.packageName })
        val cacheFreed = cacheCleaner.clearOrphanCache()

        val tempAfter = thermalMonitor.readCurrent().batteryTempCelsius
        val ramFreed = candidates.sumOf { it.memoryBytes }

        return CoolingResult(
            mode = CoolingMode.SMART,
            appsClosed = closed,
            ramFreedBytes = ramFreed,
            cacheClearedBytes = cacheFreed,
            tempBeforeCelsius = tempBefore,
            tempAfterCelsius = tempAfter,
            durationMs = System.currentTimeMillis() - start
        )
    }

    private fun isIdleCandidate(app: RunningAppInfo): Boolean {
        val idleForMs = System.currentTimeMillis() - app.lastUsedTimestamp
        return idleForMs > 3 * 60 * 1000 // idle more than 3 minutes
    }
}
