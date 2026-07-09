package com.coolboost.performance.analytics

import com.coolboost.performance.domain.model.CoolingResult

/** In-memory session counters surfaced on the Analytics screen (e.g. "cooled 4 times today"). */
class UsageTracker {
    private var coolingRunsToday = 0
    private var totalRamFreedBytes = 0L
    private var totalCacheFreedBytes = 0L

    fun recordCooling(result: CoolingResult) {
        coolingRunsToday++
        totalRamFreedBytes += result.ramFreedBytes
        totalCacheFreedBytes += result.cacheClearedBytes
    }

    fun getCoolingRunsToday(): Int = coolingRunsToday
    fun getTotalRamFreedBytes(): Long = totalRamFreedBytes
    fun getTotalCacheFreedBytes(): Long = totalCacheFreedBytes

    fun reset() {
        coolingRunsToday = 0
        totalRamFreedBytes = 0L
        totalCacheFreedBytes = 0L
    }
}
