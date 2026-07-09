package com.coolboost.performance.domain.model

enum class CoolingMode {
    NONE, SMART, EXTREME, GAME_MODE
}

data class CoolingResult(
    val mode: CoolingMode,
    val appsClosed: Int,
    val ramFreedBytes: Long,
    val cacheClearedBytes: Long,
    val tempBeforeCelsius: Float,
    val tempAfterCelsius: Float,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    val tempDelta: Float get() = tempBeforeCelsius - tempAfterCelsius
}
