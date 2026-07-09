package com.coolboost.performance.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.coolboost.performance.CoolBoostApp
import kotlinx.coroutines.flow.first

/** Periodic background cache cleanup, respecting the user's Auto Clean Cache setting. */
class AutoCleanWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val container = (applicationContext as CoolBoostApp).container
            val settings = container.settingsRepository.observeSettings().first()
            if (!settings.autoCleanEnabled) return Result.success()

            container.autoCleanCacheUseCase()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
