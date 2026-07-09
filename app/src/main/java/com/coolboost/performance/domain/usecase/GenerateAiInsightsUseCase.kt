package com.coolboost.performance.domain.usecase

import com.coolboost.performance.ai.AiOptimizationEngine
import com.coolboost.performance.domain.model.AiInsight
import com.coolboost.performance.domain.model.DashboardSnapshot
import com.coolboost.performance.domain.model.RunningAppInfo

class GenerateAiInsightsUseCase(
    private val aiEngine: AiOptimizationEngine
) {
    suspend operator fun invoke(
        snapshot: DashboardSnapshot,
        runningApps: List<RunningAppInfo>
    ): List<AiInsight> = aiEngine.analyze(snapshot, runningApps)
}
