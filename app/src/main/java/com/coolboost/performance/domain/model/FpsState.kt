package com.coolboost.performance.domain.model

data class FpsState(
    val currentFps: Double,
    val averageFps: Double,
    val jankCount: Int,
    val isSmooth: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
