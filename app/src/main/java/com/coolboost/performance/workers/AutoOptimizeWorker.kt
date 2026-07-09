package com.coolboost.performance.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.coolboost.performance.CoolBoostApp
import kotlinx.coroutines.flow.first

/** Periodic background full-optimization pass (idle app cleanup + cache), respecting user settings. */
class AutoOptimizeWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val container = (applicationContext as CoolBoostApp).container
            val settings = container.settingsRepository.observeSettings().first()
            if (!settings.autoOptimizeEnabled) return Result.success()

            container.optimizationRepository.runFullOptimization()
            container.analyticsRepository.purgeOldRecords()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
