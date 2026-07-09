package com.coolboost.performance.core

/**
 * Central, tunable constants for monitoring cadence and thermal thresholds.
 * All thresholds are heuristics based on typical Android battery/CPU behaviour,
 * since no public API exposes raw SoC die temperature without root.
 */
object Constants {

    // Monitoring intervals
    const val FAST_POLL_INTERVAL_MS = 1_000L      // FPS / CPU usage
    const val NORMAL_POLL_INTERVAL_MS = 2_000L     // RAM / Battery temp
    const val SLOW_POLL_INTERVAL_MS = 10_000L      // Storage / Analytics snapshot
    const val AI_ANALYSIS_INTERVAL_MS = 5_000L

    // Battery temperature thresholds (°C) — battery temp is the only officially
    // exposed thermal signal on all Android versions via BatteryManager.
    const val TEMP_NORMAL_MAX = 35.0f
    const val TEMP_WARM_MAX = 40.0f
    const val TEMP_HOT_MAX = 45.0f
    const val TEMP_DANGER = 45.0f

    // RAM thresholds (percent used)
    const val RAM_WARNING_PERCENT = 75
    const val RAM_CRITICAL_PERCENT = 90

    // CPU thresholds (percent)
    const val CPU_WARNING_PERCENT = 70
    const val CPU_CRITICAL_PERCENT = 90

    // Storage thresholds (percent used)
    const val STORAGE_WARNING_PERCENT = 85

    // FPS target
    const val TARGET_FPS = 60.0
    const val FPS_JANK_THRESHOLD = 45.0

    // Cooling
    const val SMART_COOLING_RAM_TARGET_FREE_PERCENT = 30
    const val EXTREME_COOLING_MIN_INTERVAL_MS = 60_000L

    // Notification
    const val NOTIFICATION_CHANNEL_MONITORING = "coolboost_monitoring"
    const val NOTIFICATION_CHANNEL_ALERTS = "coolboost_alerts"
    const val NOTIFICATION_ID_MONITORING = 1001
    const val NOTIFICATION_ID_COOLING = 1002
    const val NOTIFICATION_ID_ALERT = 1003

    // WorkManager
    const val WORK_AUTO_CLEAN = "coolboost_auto_clean_work"
    const val WORK_AUTO_OPTIMIZE = "coolboost_auto_optimize_work"

    // DataStore
    const val DATASTORE_NAME = "coolboost_settings"

    // Database
    const val DATABASE_NAME = "coolboost_database.db"
    const val HISTORY_RETENTION_DAYS = 14
}
