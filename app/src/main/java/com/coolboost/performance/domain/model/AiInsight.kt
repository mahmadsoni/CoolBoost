package com.coolboost.performance.domain.model

enum class InsightSeverity { INFO, SUGGESTION, WARNING, CRITICAL }

data class AiInsight(
    val id: String,
    val title: String,
    val description: String,
    val severity: InsightSeverity,
    val actionable: Boolean,
    val relatedPackage: String? = null,
    val predictedOverheatEtaSeconds: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ThermalPrediction(
    val willOverheat: Boolean,
    val confidencePercent: Int,
    val etaSeconds: Long?,
    val trendSlopePerMinute: Float
)
