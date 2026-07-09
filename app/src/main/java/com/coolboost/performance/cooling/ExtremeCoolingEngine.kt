package com.coolboost.performance.cooling

import com.coolboost.performance.domain.model.CoolingMode
import com.coolboost.performance.domain.model.CoolingResult
import com.coolboost.performance.monitoring.RunningAppsMonitor
import com.coolboost.performance.monitoring.ThermalMonitor
import com.coolboost.performance.optimization.BackgroundAppManager
import com.coolboost.performance.optimization.CacheCleaner

/**
 * Extreme Cooling: aggressively stops ALL non-whitelisted background apps
 * (regardless of idle time) and performs a full cache sweep. Intended for
 * manual, occasional use when the device is genuinely overheating, since it
 * will interrupt apps that were doing legitimate background work.
 */
class ExtremeCoolingEngine(
    private val runningAppsMonitor: RunningAppsMonitor,
    private val backgroundAppManager: BackgroundAppManager,
    private val cacheCleaner: CacheCleaner,
    private val thermalMonitor: ThermalMonitor
) {
    private var lastRunTimestamp = 0L

    suspend fun run(): CoolingResult {
        val start = System.currentTimeMillis()
        val tempBefore = thermalMonitor.readCurrent().batteryTempCelsius

        val candidates = runningAppsMonitor.getRecentlyActiveApps()
            .filterNot { it.isWhitelisted }

        val closed = backgroundAppManager.closeApps(candidates.map { it.packageName })
        val cacheFreed = cacheCleaner.clearAllAppCache()

        val tempAfter = thermalMonitor.readCurrent().batteryTempCelsius
        val ramFreed = candidates.sumOf { it.memoryBytes }
        lastRunTimestamp = System.currentTimeMillis()

        return CoolingResult(
            mode = CoolingMode.EXTREME,
            appsClosed = closed,
            ramFreedBytes = ramFreed,
            cacheClearedBytes = cacheFreed,
            tempBeforeCelsius = tempBefore,
            tempAfterCelsius = tempAfter,
            durationMs = System.currentTimeMillis() - start
        )
    }

    fun canRunAgain(minIntervalMs: Long): Boolean =
        System.currentTimeMillis() - lastRunTimestamp >= minIntervalMs
}
