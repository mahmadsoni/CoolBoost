package com.coolboost.performance.monitoring

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.coolboost.performance.domain.model.RunningAppInfo
import java.util.concurrent.TimeUnit

/**
 * Lists apps that have been recently active in the background using
 * UsageStatsManager (requires the user-granted PACKAGE_USAGE_STATS special
 * permission — no root needed). This is the standard, Play-Store-compliant
 * way to build a "background app manager" feature on modern Android, since
 * ActivityManager#getRunningAppProcesses() has been restricted to the
 * caller's own process since Android 5.
 */
class RunningAppsMonitor(private val context: Context) {

    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }
    private val activityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }
    private val packageManager: PackageManager get() = context.packageManager

    fun hasUsageAccess(): Boolean {
        return try {
            val end = System.currentTimeMillis()
            val start = end - TimeUnit.MINUTES.toMillis(1)
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, start, end
            )
            !stats.isNullOrEmpty()
        } catch (e: SecurityException) {
            false
        }
    }

    fun getRecentlyActiveApps(lookbackHours: Long = 12): List<RunningAppInfo> {
        if (!hasUsageAccess()) return emptyList()

        val end = System.currentTimeMillis()
        val start = end - TimeUnit.HOURS.toMillis(lookbackHours)
        val statsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST, start, end
        ) ?: emptyList()

        val selfPackage = context.packageName
        val memInfo = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
        val avgAppMemory = (memInfo.totalMem / 20).coerceAtLeast(50L * 1024 * 1024)

        return statsList
            .filter { it.totalTimeInForeground > 0 && it.packageName != selfPackage }
            .distinctBy { it.packageName }
            .sortedByDescending { it.lastTimeUsed }
            .take(50)
            .mapNotNull { usageStat ->
                try {
                    val appInfo = packageManager.getApplicationInfo(usageStat.packageName, 0)
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val label = packageManager.getApplicationLabel(appInfo).toString()

                    // Heuristic per-app memory: apps used more recently/longer are
                    // weighted heavier, since real per-process RSS for other apps
                    // is not readable without root on modern Android.
                    val recencyWeight = if (end - usageStat.lastTimeUsed < TimeUnit.MINUTES.toMillis(5)) 1.4 else 0.8
                    val estimatedMemory = (avgAppMemory * recencyWeight).toLong()

                    RunningAppInfo(
                        packageName = usageStat.packageName,
                        appName = label,
                        memoryBytes = estimatedMemory,
                        isSystemApp = isSystem,
                        lastUsedTimestamp = usageStat.lastTimeUsed,
                        estimatedCpuImpact = (recencyWeight * 40).toInt().coerceIn(0, 100)
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }
            .filter { !it.isSystemApp }
    }
}
