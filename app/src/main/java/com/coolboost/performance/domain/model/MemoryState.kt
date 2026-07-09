package com.coolboost.performance.domain.model

data class MemoryState(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long = totalBytes - availableBytes,
    val usedPercent: Int,
    val isLowMemory: Boolean,
    val threshold: Long,
    val timestamp: Long = System.currentTimeMillis()
)
