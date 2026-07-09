package com.coolboost.performance.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.coolboost.performance.MainActivity
import com.coolboost.performance.R
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.ThermalState

object NotificationHelper {

    fun buildMonitoringNotification(context: Context, thermal: ThermalState, ramPercent: Int): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_MONITORING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_monitoring_title))
            .setContentText(
                context.getString(
                    R.string.notification_monitoring_text,
                    thermal.batteryTempCelsius.toInt(),
                    ramPercent
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .build()
    }

    fun buildCoolingNotification(context: Context, message: String): Notification {
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_MONITORING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_cooling_title))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    fun buildAlertNotification(context: Context, title: String, message: String): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }
}
