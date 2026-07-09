package com.coolboost.performance

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.WorkManager
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.workers.WorkScheduler

class CoolBoostApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer.getInstance(this)
        createNotificationChannels()
        WorkScheduler.scheduleDefaultWork(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)

        val monitoringChannel = NotificationChannel(
            com.coolboost.performance.core.Constants.NOTIFICATION_CHANNEL_MONITORING,
            getString(R.string.notification_channel_monitoring),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = getString(R.string.notification_channel_monitoring_desc)
            setShowBadge(false)
        }

        val alertsChannel = NotificationChannel(
            com.coolboost.performance.core.Constants.NOTIFICATION_CHANNEL_ALERTS,
            getString(R.string.notification_channel_alerts),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.notification_channel_alerts_desc)
        }

        manager.createNotificationChannel(monitoringChannel)
        manager.createNotificationChannel(alertsChannel)
    }
}
