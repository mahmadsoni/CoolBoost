package com.coolboost.performance.monitoring

import android.os.Environment
import android.os.StatFs
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.StorageState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Reads internal storage usage via StatFs on the data directory — no permission needed. */
class StorageMonitor {

    fun readCurrent(): StorageState {
        val stat = StatFs(Environment.getDataDirectory().path)
        val total = stat.blockCountLong * stat.blockSizeLong
        val free = stat.availableBlocksLong * stat.blockSizeLong
        val used = total - free
        val usedPercent = if (total > 0) ((used * 100) / total).toInt() else 0
        return StorageState(
            totalBytes = total,
            freeBytes = free,
            usedBytes = used,
            usedPercent = usedPercent
        )
    }

    fun observe(intervalMs: Long = Constants.SLOW_POLL_INTERVAL_MS): Flow<StorageState> = flow {
        while (true) {
            emit(readCurrent())
            delay(intervalMs)
        }
    }
}
