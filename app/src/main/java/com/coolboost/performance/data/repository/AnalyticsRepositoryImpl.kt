package com.coolboost.performance.data.repository

import com.coolboost.performance.core.Constants
import com.coolboost.performance.data.local.dao.CoolingHistoryDao
import com.coolboost.performance.data.local.dao.PerformanceHistoryDao
import com.coolboost.performance.data.local.entity.CoolingHistoryEntity
import com.coolboost.performance.data.local.entity.PerformanceHistoryEntity
import com.coolboost.performance.domain.model.CoolingMode
import com.coolboost.performance.domain.model.CoolingResult
import com.coolboost.performance.domain.repository.AnalyticsRepository
import com.coolboost.performance.domain.repository.PerformanceHistoryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class AnalyticsRepositoryImpl(
    private val performanceHistoryDao: PerformanceHistoryDao,
    private val coolingHistoryDao: CoolingHistoryDao
) : AnalyticsRepository {

    override suspend fun recordSnapshot(point: PerformanceHistoryPoint) {
        performanceHistoryDao.insert(
            PerformanceHistoryEntity(
                timestamp = point.timestamp,
                cpuPercent = point.cpuPercent,
                ramPercent = point.ramPercent,
                tempCelsius = point.tempCelsius,
                fps = point.fps,
                performanceScore = point.performanceScore
            )
        )
    }

    override fun observeHistory(sinceMillis: Long): Flow<List<PerformanceHistoryPoint>> =
        performanceHistoryDao.observeSince(sinceMillis).map { list ->
            list.map {
                PerformanceHistoryPoint(
                    timestamp = it.timestamp,
                    cpuPercent = it.cpuPercent,
                    ramPercent = it.ramPercent,
                    tempCelsius = it.tempCelsius,
                    fps = it.fps,
                    performanceScore = it.performanceScore
                )
            }
        }

    override suspend fun recordCoolingResult(result: CoolingResult) {
        coolingHistoryDao.insert(
            CoolingHistoryEntity(
                mode = result.mode.name,
                appsClosed = result.appsClosed,
                ramFreedBytes = result.ramFreedBytes,
                cacheClearedBytes = result.cacheClearedBytes,
                tempBeforeCelsius = result.tempBeforeCelsius,
                tempAfterCelsius = result.tempAfterCelsius,
                durationMs = result.durationMs,
                timestamp = result.timestamp
            )
        )
    }

    override fun observeCoolingHistory(): Flow<List<CoolingResult>> =
        coolingHistoryDao.observeAll().map { list ->
            list.map {
                CoolingResult(
                    mode = runCatching { CoolingMode.valueOf(it.mode) }.getOrDefault(CoolingMode.NONE),
                    appsClosed = it.appsClosed,
                    ramFreedBytes = it.ramFreedBytes,
                    cacheClearedBytes = it.cacheClearedBytes,
                    tempBeforeCelsius = it.tempBeforeCelsius,
                    tempAfterCelsius = it.tempAfterCelsius,
                    durationMs = it.durationMs,
                    timestamp = it.timestamp
                )
            }
        }

    override suspend fun purgeOldRecords() {
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(Constants.HISTORY_RETENTION_DAYS.toLong())
        performanceHistoryDao.deleteOlderThan(cutoff)
        coolingHistoryDao.deleteOlderThan(cutoff)
    }

    override suspend fun getAverageScore(sinceMillis: Long): Int =
        performanceHistoryDao.getAverageScore(sinceMillis) ?: 0
}
