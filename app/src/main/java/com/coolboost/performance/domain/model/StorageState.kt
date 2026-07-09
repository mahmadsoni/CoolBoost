package com.coolboost.performance.domain.model

data class StorageState(
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long = totalBytes - freeBytes,
    val usedPercent: Int,
    val cacheBytes: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)
