package com.coolboost.performance.domain.usecase

import com.coolboost.performance.domain.model.CoolingResult
import com.coolboost.performance.domain.repository.AnalyticsRepository
import com.coolboost.performance.domain.repository.CoolingRepository

class RunSmartCoolingUseCase(
    private val coolingRepository: CoolingRepository,
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(): CoolingResult {
        val result = coolingRepository.runSmartCooling()
        analyticsRepository.recordCoolingResult(result)
        return result
    }
}
