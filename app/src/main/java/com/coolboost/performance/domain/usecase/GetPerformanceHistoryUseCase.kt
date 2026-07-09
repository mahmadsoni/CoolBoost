package com.coolboost.performance.domain.usecase

import com.coolboost.performance.domain.repository.AnalyticsRepository
import com.coolboost.performance.domain.repository.PerformanceHistoryPoint
import kotlinx.coroutines.flow.Flow

class GetPerformanceHistoryUseCase(
    private val repository: AnalyticsRepository
) {
    operator fun invoke(sinceMillis: Long): Flow<List<PerformanceHistoryPoint>> =
        repository.observeHistory(sinceMillis)
}
