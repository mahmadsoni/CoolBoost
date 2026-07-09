package com.coolboost.performance.domain.usecase

import com.coolboost.performance.domain.repository.OptimizationRepository

class AutoCleanCacheUseCase(
    private val repository: OptimizationRepository
) {
    suspend operator fun invoke(): Long = repository.clearAllCache()
}
