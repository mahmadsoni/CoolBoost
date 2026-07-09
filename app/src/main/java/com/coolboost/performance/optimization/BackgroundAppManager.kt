package com.coolboost.performance.optimization

import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Stops background app processes using ActivityManager.killBackgroundProcesses,
 * the standard public API for "clear from recents"-style cleanup. This only
 * affects cached/idle background processes the OS is already willing to kill —
 * it cannot force-stop a foreground app, which matches Android's security model
 * and requires no root.
 */
class BackgroundAppManager(private val context: Context) {

    private val activityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    suspend fun closeApp(packageName: String): Boolean = withContext(Dispatchers.Default) {
        try {
            activityManager.killBackgroundProcesses(packageName)
            true
        } catch (e: SecurityException) {
            false
        }
    }

    suspend fun closeApps(packageNames: List<String>): Int = withContext(Dispatchers.Default) {
        var count = 0
        for (pkg in packageNames) {
            try {
                activityManager.killBackgroundProcesses(pkg)
                count++
            } catch (e: SecurityException) {
                // Skip protected/system packages
            }
        }
        count
    }
}
