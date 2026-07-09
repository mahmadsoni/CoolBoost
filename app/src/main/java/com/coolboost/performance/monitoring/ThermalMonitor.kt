package com.coolboost.performance.monitoring

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.ThermalLevel
import com.coolboost.performance.domain.model.ThermalState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Reads real thermal signals available without root:
 *  - BatteryManager EXTRA_TEMPERATURE (tenths of a degree Celsius) — always available.
 *  - PowerManager.getCurrentThermalStatus() on API 29+ — reflects the OS-level
 *    thermal throttling decision made by the vendor's thermal HAL.
 *
 * There is no public, unprivileged API that exposes raw SoC die temperature,
 * so battery temperature is used as the primary, well-correlated proxy.
 */
class ThermalMonitor(private val context: Context) {

    private val powerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    fun readCurrent(): ThermalState {
        val batteryIntent = context.registerReceiver(
            null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val tempCelsius = tempTenths / 10f

        val systemStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.currentThermalStatus
        } else 0

        return ThermalState(
            batteryTempCelsius = tempCelsius,
            level = classify(tempCelsius, systemStatus),
            systemThermalStatus = systemStatus
        )
    }

    private fun classify(temp: Float, systemStatus: Int): ThermalLevel {
        // Prefer the OS thermal status when available since it accounts for
        // more than just battery temperature (CPU/GPU/skin sensors internally).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && systemStatus > 0) {
            return when {
                systemStatus >= PowerManager.THERMAL_STATUS_SEVERE -> ThermalLevel.CRITICAL
                systemStatus >= PowerManager.THERMAL_STATUS_MODERATE -> ThermalLevel.HOT
                systemStatus >= PowerManager.THERMAL_STATUS_LIGHT -> ThermalLevel.WARM
                else -> ThermalLevel.NORMAL
            }
        }
        return when {
            temp >= Constants.TEMP_HOT_MAX -> ThermalLevel.CRITICAL
            temp >= Constants.TEMP_WARM_MAX -> ThermalLevel.HOT
            temp >= Constants.TEMP_NORMAL_MAX -> ThermalLevel.WARM
            else -> ThermalLevel.NORMAL
        }
    }

    fun observe(intervalMs: Long = Constants.NORMAL_POLL_INTERVAL_MS): Flow<ThermalState> = flow {
        while (true) {
            emit(readCurrent())
            delay(intervalMs)
        }
    }
}
