package com.coolboost.performance.monitoring

import android.app.ActivityManager
import android.content.Context
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.MemoryState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Reads system-wide RAM usage via ActivityManager.getMemoryInfo — public API, no permission needed. */
class RamMonitor(private val context: Context) {

    private val activityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    fun readCurrent(): MemoryState {
        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)
        val used = info.totalMem - info.availMem
        val usedPercent = if (info.totalMem > 0) ((used * 100) / info.totalMem).toInt() else 0
        return MemoryState(
            totalBytes = info.totalMem,
            availableBytes = info.availMem,
            usedBytes = used,
            usedPercent = usedPercent,
            isLowMemory = info.lowMemory,
            threshold = info.threshold
        )
    }

    fun observe(intervalMs: Long = Constants.NORMAL_POLL_INTERVAL_MS): Flow<MemoryState> = flow {
        while (true) {
            emit(readCurrent())
            delay(intervalMs)
        }
    }
}
