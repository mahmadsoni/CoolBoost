package com.coolboost.performance.analytics

import com.coolboost.performance.domain.model.DashboardSnapshot
import com.coolboost.performance.domain.repository.AnalyticsRepository
import com.coolboost.performance.domain.repository.PerformanceHistoryPoint

/** Bridges live monitoring snapshots into persisted analytics history. */
class PerformanceAnalytics(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend fun recordSnapshot(snapshot: DashboardSnapshot) {
        analyticsRepository.recordSnapshot(
            PerformanceHistoryPoint(
                timestamp = System.currentTimeMillis(),
                cpuPercent = snapshot.cpu.usagePercent,
                ramPercent = snapshot.memory.usedPercent,
                tempCelsius = snapshot.thermal.batteryTempCelsius,
                fps = snapshot.fps.averageFps,
                performanceScore = snapshot.performanceScore
            )
        )
    }

    suspend fun cleanupOldRecords() = analyticsRepository.purgeOldRecords()
}
