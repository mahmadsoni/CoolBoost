package com.coolboost.performance.ai

import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.*
import java.util.UUID

/**
 * The central "AI Optimization Engine". Combines:
 *  - RamUsageAnalyzer: RAM pressure + memory-hog ranking
 *  - HeavyAppDetector: composite resource-impact scoring per app
 *  - ThermalPredictor: on-device trend model predicting overheating risk
 * into a single ranked list of actionable insights shown on the Dashboard
 * and Analytics screens. Fully offline — no network calls, no data leaves
 * the device (Privacy First / Offline First by design).
 */
class AiOptimizationEngine(
    private val ramUsageAnalyzer: RamUsageAnalyzer,
    private val heavyAppDetector: HeavyAppDetector,
    private val thermalPredictor: ThermalPredictor
) {

    fun analyze(
        snapshot: DashboardSnapshot,
        runningApps: List<RunningAppInfo>
    ): List<AiInsight> {
        val insights = mutableListOf<AiInsight>()

        thermalPredictor.addSample(snapshot.thermal.timestamp, snapshot.thermal.batteryTempCelsius)
        val prediction = thermalPredictor.predict(Constants.TEMP_DANGER)

        if (prediction.willOverheat) {
            insights += AiInsight(
                id = UUID.randomUUID().toString(),
                title = "Overheating predicted",
                description = "Temperature is trending upward. Based on the current rate, " +
                    "your device may reach a risky temperature soon. Consider running Smart Cooling now.",
                severity = InsightSeverity.WARNING,
                actionable = true,
                predictedOverheatEtaSeconds = prediction.etaSeconds
            )
        }

        if (ramUsageAnalyzer.isUnderPressure(snapshot.memory)) {
            val hogs = ramUsageAnalyzer.rankMemoryHogs(runningApps, 3)
            insights += AiInsight(
                id = UUID.randomUUID().toString(),
                title = "High RAM usage detected",
                description = "Memory usage is at ${snapshot.memory.usedPercent}%. " +
                    (hogs.firstOrNull()?.let { "\"${it.appName}\" is using the most memory. " } ?: "") +
                    "Freeing background apps can improve responsiveness.",
                severity = if (snapshot.memory.usedPercent >= Constants.RAM_CRITICAL_PERCENT)
                    InsightSeverity.CRITICAL else InsightSeverity.SUGGESTION,
                actionable = true,
                relatedPackage = hogs.firstOrNull()?.packageName
            )
        }

        val heavyApps = heavyAppDetector.detectHeavyApps(runningApps)
        heavyApps.take(2).forEach { app ->
            insights += AiInsight(
                id = UUID.randomUUID().toString(),
                title = "${app.appName} is resource-intensive",
                description = "This app appears to be consuming significant memory in the " +
                    "background. Closing it can reduce heat and improve battery life.",
                severity = InsightSeverity.SUGGESTION,
                actionable = true,
                relatedPackage = app.packageName
            )
        }

        if (snapshot.cpu.usagePercent >= Constants.CPU_WARNING_PERCENT) {
            insights += AiInsight(
                id = UUID.randomUUID().toString(),
                title = "Elevated processing load",
                description = "The device is under higher-than-normal processing load. " +
                    "This can contribute to heat build-up over time.",
                severity = InsightSeverity.INFO,
                actionable = false
            )
        }

        if (snapshot.storage.usedPercent >= Constants.STORAGE_WARNING_PERCENT) {
            insights += AiInsight(
                id = UUID.randomUUID().toString(),
                title = "Storage nearly full",
                description = "Storage is at ${snapshot.storage.usedPercent}% capacity. " +
                    "Low free storage can slow down the system and increase background I/O load.",
                severity = InsightSeverity.WARNING,
                actionable = true
            )
        }

        if (insights.isEmpty()) {
            insights += AiInsight(
                id = UUID.randomUUID().toString(),
                title = "Everything looks healthy",
                description = "Temperature, memory, and CPU load are all within normal ranges.",
                severity = InsightSeverity.INFO,
                actionable = false
            )
        }

        return insights.sortedByDescending { it.severity.ordinal }
    }

    fun predictThermal(): ThermalPrediction = thermalPredictor.predict(Constants.TEMP_DANGER)
}
