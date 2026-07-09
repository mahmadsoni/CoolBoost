package com.coolboost.performance.domain.model

/**
 * Represents the device's thermal condition derived from battery temperature
 * (BatteryManager) and, on API 29+, the system PowerManager thermal status.
 */
enum class ThermalLevel {
    NORMAL, WARM, HOT, CRITICAL
}

data class ThermalState(
    val batteryTempCelsius: Float,
    val level: ThermalLevel,
    val systemThermalStatus: Int = 0, // maps to PowerManager.THERMAL_STATUS_*
    val timestamp: Long = System.currentTimeMillis()
) {
    val isDangerous: Boolean get() = level == ThermalLevel.HOT || level == ThermalLevel.CRITICAL
}
