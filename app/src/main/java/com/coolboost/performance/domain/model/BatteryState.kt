package com.coolboost.performance.domain.model

enum class BatteryHealthStatus {
    GOOD, OVERHEAT, DEAD, OVER_VOLTAGE, COLD, UNKNOWN
}

data class BatteryState(
    val levelPercent: Int,
    val isCharging: Boolean,
    val temperatureCelsius: Float,
    val voltageMilliVolts: Int,
    val health: BatteryHealthStatus,
    val chargeCycleEstimate: Int = 0,
    val capacityHealthPercent: Int = 100,
    val timestamp: Long = System.currentTimeMillis()
)
