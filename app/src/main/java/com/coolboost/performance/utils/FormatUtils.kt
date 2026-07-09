package com.coolboost.performance.utils

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

object FormatUtils {

    private val oneDecimal = DecimalFormat("#.#")

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var value = bytes.toDouble()
        var unitIndex = 0
        while (value >= 1024 && unitIndex < units.lastIndex) {
            value /= 1024
            unitIndex++
        }
        return "${oneDecimal.format(value)} ${units[unitIndex]}"
    }

    fun formatDuration(millis: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
            else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
        }
    }

    fun formatEta(seconds: Long?): String {
        if (seconds == null || seconds <= 0) return "--"
        return when {
            seconds < 60 -> "${seconds}s"
            else -> "${seconds / 60}m ${seconds % 60}s"
        }
    }

    fun formatTemp(celsius: Float): String = "${oneDecimal.format(celsius)}°C"
}
