package com.coolboost.performance.monitoring

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.BatteryHealthStatus
import com.coolboost.performance.domain.model.BatteryState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Reads battery state via the sticky ACTION_BATTERY_CHANGED broadcast — public, no permission required. */
class BatteryMonitor(private val context: Context) {

    fun readCurrent(): BatteryState {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else 0

        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        val tempTenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val temperature = tempTenths / 10f

        val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0

        val healthExtra = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val health = when (healthExtra) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealthStatus.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealthStatus.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealthStatus.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealthStatus.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealthStatus.COLD
            else -> BatteryHealthStatus.UNKNOWN
        }

        // Capacity health cannot be read precisely without OEM-specific APIs; we
        // derive a conservative estimate from the reported health flag + temperature stress.
        val capacityHealth = when (health) {
            BatteryHealthStatus.GOOD -> if (temperature > Constants.TEMP_HOT_MAX) 92 else 100
            BatteryHealthStatus.OVERHEAT -> 80
            BatteryHealthStatus.OVER_VOLTAGE -> 75
            BatteryHealthStatus.DEAD -> 40
            BatteryHealthStatus.COLD -> 90
            BatteryHealthStatus.UNKNOWN -> 95
        }

        return BatteryState(
            levelPercent = percent,
            isCharging = isCharging,
            temperatureCelsius = temperature,
            voltageMilliVolts = voltage,
            health = health,
            capacityHealthPercent = capacityHealth
        )
    }

    fun observe(intervalMs: Long = Constants.NORMAL_POLL_INTERVAL_MS): Flow<BatteryState> = flow {
        while (true) {
            emit(readCurrent())
            delay(intervalMs)
        }
    }
}
