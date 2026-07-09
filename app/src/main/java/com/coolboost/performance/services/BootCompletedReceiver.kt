package com.coolboost.performance.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/** Restarts background monitoring after device reboot, if the user has it enabled. */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, MonitoringService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
