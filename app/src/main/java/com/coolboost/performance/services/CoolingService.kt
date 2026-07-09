package com.coolboost.performance.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.coolboost.performance.CoolBoostApp
import com.coolboost.performance.core.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Short-lived foreground service that performs a Smart or Extreme cooling
 * pass. Runs as a foreground service (rather than a plain coroutine) so the
 * OS does not deprioritize the work mid-cleanup if the user backgrounds the app.
 */
class CoolingService : Service() {

    private var scope: CoroutineScope? = null

    companion object {
        const val EXTRA_MODE = "mode"
        const val MODE_SMART = "smart"
        const val MODE_EXTREME = "extreme"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(Dispatchers.Default + Job())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mode = intent?.getStringExtra(EXTRA_MODE) ?: MODE_SMART
        val container = (application as CoolBoostApp).container

        startForegroundCompat(
            NotificationHelper.buildCoolingNotification(
                this,
                getString(
                    if (mode == MODE_EXTREME) com.coolboost.performance.R.string.cooling_running_extreme
                    else com.coolboost.performance.R.string.cooling_running_smart
                )
            )
        )

        scope?.launch {
            try {
                if (mode == MODE_EXTREME) {
                    container.runExtremeCoolingUseCase()
                } else {
                    container.runSmartCoolingUseCase()
                }
            } finally {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun startForegroundCompat(notification: android.app.Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                Constants.NOTIFICATION_ID_COOLING,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(Constants.NOTIFICATION_ID_COOLING, notification)
        }
    }

    override fun onDestroy() {
        scope?.cancel()
        super.onDestroy()
    }
}
