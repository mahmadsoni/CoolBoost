package com.coolboost.performance.domain.model

data class RunningAppInfo(
    val packageName: String,
    val appName: String,
    val memoryBytes: Long,
    val isSystemApp: Boolean,
    val lastUsedTimestamp: Long = 0L,
    val estimatedCpuImpact: Int = 0, // 0-100 heuristic score
    val isWhitelisted: Boolean = false
)
