package com.coolboost.performance.ai

import com.coolboost.performance.domain.model.RunningAppInfo

/**
 * Flags apps as "resource heavy" using a composite heuristic score derived
 * from estimated memory footprint and recency-based CPU impact, since
 * exact per-app CPU/battery attribution requires root or OEM-only APIs.
 */
class HeavyAppDetector {

    fun detectHeavyApps(apps: List<RunningAppInfo>, thresholdScore: Int = 60): List<RunningAppInfo> {
        return apps.filter { score(it) >= thresholdScore }
            .sortedByDescending { score(it) }
    }

    private fun score(app: RunningAppInfo): Int {
        val memMb = app.memoryBytes / (1024 * 1024)
        val memScore = (memMb / 4).coerceAtMost(60L).toInt()
        val cpuScore = (app.estimatedCpuImpact * 0.4).toInt()
        return (memScore + cpuScore).coerceIn(0, 100)
    }
}
