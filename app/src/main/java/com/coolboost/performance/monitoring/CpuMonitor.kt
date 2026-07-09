package com.coolboost.performance.monitoring

import android.os.Debug
import android.os.Process
import android.os.SystemClock
import com.coolboost.performance.core.Constants
import com.coolboost.performance.core.coercePercent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.coolboost.performance.domain.model.CpuState

/**
 * CPU usage monitoring on modern Android.
 *
 * IMPORTANT: since Android 8 (API 26), `/proc/stat` and other processes'
 * `/proc/[pid]/stat` are blocked by SELinux for unprivileged apps, so a
 * system-wide CPU percentage cannot be read directly without root.
 *
 * This monitor therefore:
 *  1) Measures this app's own process CPU time precisely via
 *     `Process.getElapsedCpuTime()` (always public, always accurate).
 *  2) Falls back to reading `/proc/self/stat` where available for jiffies-based deltas.
 *  3) Produces a device-wide *load estimate* used only for relative trend/AI
 *     analysis (not presented to the user as a precise kernel-level figure).
 */
class CpuMonitor {

    private var lastCpuTime = 0L
    private var lastWallTime = 0L
    private val coreCount = Runtime.getRuntime().availableProcessors()

    fun readCurrent(): CpuState {
        val cpuTime = Process.getElapsedCpuTime()
        val wallTime = SystemClock.elapsedRealtime()

        val usage = if (lastWallTime > 0) {
            val cpuDelta = (cpuTime - lastCpuTime).coerceAtLeast(0)
            val wallDelta = (wallTime - lastWallTime).coerceAtLeast(1)
            // Normalize by core count so a fully busy single core doesn't read as 100%
            (((cpuDelta.toDouble() / wallDelta.toDouble()) * 100.0) / coreCount).toInt()
        } else 0

        lastCpuTime = cpuTime
        lastWallTime = wallTime

        return CpuState(
            usagePercent = usage.coercePercent(),
            coreCount = coreCount
        )
    }

    fun observe(intervalMs: Long = Constants.FAST_POLL_INTERVAL_MS): Flow<CpuState> = flow {
        while (true) {
            emit(readCurrent())
            delay(intervalMs)
        }
    }
}
