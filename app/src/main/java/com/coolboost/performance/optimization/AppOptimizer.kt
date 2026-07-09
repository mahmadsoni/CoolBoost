package com.coolboost.performance.optimization

/**
 * Orchestrates a "one tap optimize" pass: cache cleanup + idle background app
 * cleanup, combined into a single result used by the AutoOptimize worker and
 * the Optimization screen's manual trigger.
 */
class AppOptimizer(
    private val backgroundAppManager: BackgroundAppManager,
    private val cacheCleaner: CacheCleaner
) {
    suspend fun optimize(idleAppPackages: List<String>): Long {
        val cacheFreed = cacheCleaner.clearOrphanCache()
        backgroundAppManager.closeApps(idleAppPackages)
        return cacheFreed
    }
}
