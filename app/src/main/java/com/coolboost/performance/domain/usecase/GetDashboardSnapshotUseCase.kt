package com.coolboost.performance.domain.usecase

import com.coolboost.performance.domain.model.DashboardSnapshot
import com.coolboost.performance.domain.repository.MonitoringRepository

class GetDashboardSnapshotUseCase(
    private val repository: MonitoringRepository
) {
    suspend operator fun invoke(): DashboardSnapshot = repository.getDashboardSnapshot()
}
