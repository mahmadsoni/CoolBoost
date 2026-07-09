package com.coolboost.performance.domain.model

data class CpuState(
    val usagePercent: Int,
    val coreCount: Int,
    val perCoreUsage: List<Int> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
