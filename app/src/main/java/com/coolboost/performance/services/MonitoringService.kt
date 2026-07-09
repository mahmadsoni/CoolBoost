package com.coolboost.performance.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coolboost.performance.CoolBoostApp
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.ThermalLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Low-priority foreground service that keeps thermal/RAM monitoring alive
 * while the app is backgrounded, persists periodic snapshots for Analytics,
 * and raises a high-priority alert notification if temperature crosses the
 * danger threshold. Uses a MIN-importance silent notification per Android's
 * foreground service requirements — this is what makes "background
 * monitoring" possible without the OS killing the process.
 */
class MonitoringService : Service() {

    private var scope: CoroutineScope? = null
    private var lastAlertTimestamp = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(Dispatchers.Default + Job())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val container = (application as CoolBoostApp).container

        startForegroundCompat(
            NotificationHelper.buildMonitoringNotification(
                this,
                container.thermalMonitor.readCurrent(),
                container.ramMonitor.readCurrent().usedPercent
            )
        )

        scope?.launch {
            combine(
                container.thermalMonitor.observe(Constants.NORMAL_POLL_INTERVAL_MS),
                container.ramMonitor.observe(Constants.NORMAL_POLL_INTERVAL_MS)
            ) { thermal, memory -> thermal to memory }
                .collect { (thermal, memory) ->
                    updateNotification(thermal.batteryTempCelsius, memory.usedPercent)
                    maybeAlert(thermal.level, thermal.batteryTempCelsius)

                    val cpu = container.cpuMonitor.readCurrent()
                    container.performanceAnalytics.recordSnapshot(
                        com.coolboost.performance.domain.model.DashboardSnapshot(
                            thermal = thermal,
                            memory = memory,
                            storage = container.storageMonitor.readCurrent(),
                            cpu = cpu,
                            fps = com.coolboost.performance.domain.model.FpsState(60.0, 60.0, 0, true),
                            battery = container.batteryMonitor.readCurrent(),
                            performanceScore = 0
                        )
                    )
                }
        }

        return START_STICKY
    }

    private fun startForegroundCompat(notification: android.app.Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                Constants.NOTIFICATION_ID_MONITORING,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(Constants.NOTIFICATION_ID_MONITORING, notification)
        }
    }

    private fun updateNotification(temp: Float, ramPercent: Int) {
        val notification = NotificationHelper.buildMonitoringNotification(
            this,
            com.coolboost.performance.domain.model.ThermalState(temp, classify(temp)),
            ramPercent
        )
        NotificationManagerCompat.from(this).apply {
            if (ContextCompat.checkSelfPermission(
                    this@MonitoringService,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                notify(Constants.NOTIFICATION_ID_MONITORING, notification)
            }
        }
    }

    private fun classify(temp: Float): ThermalLevel = when {
        temp >= Constants.TEMP_HOT_MAX -> ThermalLevel.CRITICAL
        temp >= Constants.TEMP_WARM_MAX -> ThermalLevel.HOT
        temp >= Constants.TEMP_NORMAL_MAX -> ThermalLevel.WARM
        else -> ThermalLevel.NORMAL
    }

    private fun maybeAlert(level: ThermalLevel, temp: Float) {
        if (level != ThermalLevel.CRITICAL) return
        val now = System.currentTimeMillis()
        if (now - lastAlertTimestamp < 5 * 60 * 1000) return // throttle alerts to once per 5 min
        lastAlertTimestamp = now

        val notification = NotificationHelper.buildAlertNotification(
            this,
            getString(com.coolboost.performance.R.string.alert_overheat_title),
            getString(com.coolboost.performance.R.string.alert_overheat_text, temp.toInt())
        )
        NotificationManagerCompat.from(this).apply {
            if (ContextCompat.checkSelfPermission(
                    this@MonitoringService,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                notify(Constants.NOTIFICATION_ID_ALERT, notification)
            }
        }
    }

    override fun onDestroy() {
        scope?.cancel()
        super.onDestroy()
    }
}
