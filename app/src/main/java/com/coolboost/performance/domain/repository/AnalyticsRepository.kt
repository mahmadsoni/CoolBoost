package com.coolboost.performance.domain.repository

import com.coolboost.performance.domain.model.CoolingResult
import kotlinx.coroutines.flow.Flow

data class PerformanceHistoryPoint(
    val timestamp: Long,
    val cpuPercent: Int,
    val ramPercent: Int,
    val tempCelsius: Float,
    val fps: Double,
    val performanceScore: Int
)

interface AnalyticsRepository {
    suspend fun recordSnapshot(point: PerformanceHistoryPoint)
    fun observeHistory(sinceMillis: Long): Flow<List<PerformanceHistoryPoint>>
    suspend fun recordCoolingResult(result: CoolingResult)
    fun observeCoolingHistory(): Flow<List<CoolingResult>>
    suspend fun purgeOldRecords()
    suspend fun getAverageScore(sinceMillis: Long): Int
}
