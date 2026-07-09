package com.coolboost.performance.workers

import android.content.Context
import androidx.work.*
import com.coolboost.performance.core.Constants
import java.util.concurrent.TimeUnit

/** Schedules recurring WorkManager jobs for Auto Clean and Auto Optimize. */
object WorkScheduler {

    fun scheduleDefaultWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val autoCleanRequest = PeriodicWorkRequestBuilder<AutoCleanWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        val autoOptimizeRequest = PeriodicWorkRequestBuilder<AutoOptimizeWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_AUTO_CLEAN,
            ExistingPeriodicWorkPolicy.KEEP,
            autoCleanRequest
        )
        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_AUTO_OPTIMIZE,
            ExistingPeriodicWorkPolicy.KEEP,
            autoOptimizeRequest
        )
    }

    fun cancelAll(context: Context) {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork(Constants.WORK_AUTO_CLEAN)
            cancelUniqueWork(Constants.WORK_AUTO_OPTIMIZE)
        }
    }
}
