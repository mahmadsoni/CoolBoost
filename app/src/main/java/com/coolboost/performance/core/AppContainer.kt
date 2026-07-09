package com.coolboost.performance.core

import android.content.Context
import com.coolboost.performance.ai.AiOptimizationEngine
import com.coolboost.performance.ai.HeavyAppDetector
import com.coolboost.performance.ai.RamUsageAnalyzer
import com.coolboost.performance.ai.ThermalPredictor
import com.coolboost.performance.analytics.PerformanceAnalytics
import com.coolboost.performance.analytics.UsageTracker
import com.coolboost.performance.cooling.CoolingManager
import com.coolboost.performance.cooling.ExtremeCoolingEngine
import com.coolboost.performance.cooling.SmartCoolingEngine
import com.coolboost.performance.data.local.AppDatabase
import com.coolboost.performance.data.local.SettingsDataStore
import com.coolboost.performance.data.repository.AnalyticsRepositoryImpl
import com.coolboost.performance.data.repository.CoolingRepositoryImpl
import com.coolboost.performance.data.repository.MonitoringRepositoryImpl
import com.coolboost.performance.data.repository.OptimizationRepositoryImpl
import com.coolboost.performance.data.repository.SettingsRepositoryImpl
import com.coolboost.performance.domain.repository.AnalyticsRepository
import com.coolboost.performance.domain.repository.CoolingRepository
import com.coolboost.performance.domain.repository.MonitoringRepository
import com.coolboost.performance.domain.repository.OptimizationRepository
import com.coolboost.performance.domain.repository.SettingsRepository
import com.coolboost.performance.domain.usecase.*
import com.coolboost.performance.monitoring.*
import com.coolboost.performance.optimization.AppOptimizer
import com.coolboost.performance.optimization.BackgroundAppManager
import com.coolboost.performance.optimization.CacheCleaner

/**
 * Lightweight, hand-rolled dependency container (no Dagger/Hilt) so the
 * project compiles with zero annotation-processing setup beyond Room/KSP.
 * Every dependency is wired explicitly here in one place, following the
 * same "single composition root" principle Hilt would otherwise enforce.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    // ---- Monitoring ----
    val thermalMonitor = ThermalMonitor(appContext)
    val ramMonitor = RamMonitor(appContext)
    val storageMonitor = StorageMonitor()
    val cpuMonitor = CpuMonitor()
    val fpsMonitor = FpsMonitor()
    val batteryMonitor = BatteryMonitor(appContext)
    val runningAppsMonitor = RunningAppsMonitor(appContext)

    // ---- Optimization ----
    val backgroundAppManager = BackgroundAppManager(appContext)
    val cacheCleaner = CacheCleaner(appContext)
    val appOptimizer = AppOptimizer(backgroundAppManager, cacheCleaner)

    // ---- Cooling ----
    val smartCoolingEngine = SmartCoolingEngine(runningAppsMonitor, backgroundAppManager, cacheCleaner, thermalMonitor)
    val extremeCoolingEngine = ExtremeCoolingEngine(runningAppsMonitor, backgroundAppManager, cacheCleaner, thermalMonitor)
    val coolingManager = CoolingManager(smartCoolingEngine, extremeCoolingEngine)

    // ---- AI ----
    val ramUsageAnalyzer = RamUsageAnalyzer()
    val heavyAppDetector = HeavyAppDetector()
    val thermalPredictor = ThermalPredictor()
    val aiOptimizationEngine = AiOptimizationEngine(ramUsageAnalyzer, heavyAppDetector, thermalPredictor)

    // ---- Database ----
    private val database by lazy { AppDatabase.getInstance(appContext) }
    private val settingsDataStore = SettingsDataStore(appContext)

    // ---- Repositories ----
    val monitoringRepository: MonitoringRepository = MonitoringRepositoryImpl(
        thermalMonitor, ramMonitor, storageMonitor, cpuMonitor, fpsMonitor, batteryMonitor, runningAppsMonitor
    )
    val coolingRepository: CoolingRepository = CoolingRepositoryImpl(coolingManager)
    val optimizationRepository: OptimizationRepository by lazy {
        OptimizationRepositoryImpl(
            runningAppsMonitor, backgroundAppManager, cacheCleaner, appOptimizer, database.whitelistDao()
        )
    }
    val analyticsRepository: AnalyticsRepository by lazy {
        AnalyticsRepositoryImpl(database.performanceHistoryDao(), database.coolingHistoryDao())
    }
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(settingsDataStore)

    // ---- Analytics helpers ----
    val performanceAnalytics by lazy { PerformanceAnalytics(analyticsRepository) }
    val usageTracker = UsageTracker()

    // ---- Use cases ----
    val getDashboardSnapshotUseCase get() = GetDashboardSnapshotUseCase(monitoringRepository)
    val runSmartCoolingUseCase get() = RunSmartCoolingUseCase(coolingRepository, analyticsRepository)
    val runExtremeCoolingUseCase get() = RunExtremeCoolingUseCase(coolingRepository, analyticsRepository)
    val manageBackgroundAppsUseCase get() = ManageBackgroundAppsUseCase(optimizationRepository)
    val autoCleanCacheUseCase get() = AutoCleanCacheUseCase(optimizationRepository)
    val generateAiInsightsUseCase get() = GenerateAiInsightsUseCase(aiOptimizationEngine)
    val getPerformanceHistoryUseCase get() = GetPerformanceHistoryUseCase(analyticsRepository)

    companion object {
        @Volatile
        private var INSTANCE: AppContainer? = null

        fun getInstance(context: Context): AppContainer =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppContainer(context).also { INSTANCE = it }
            }
    }
}
