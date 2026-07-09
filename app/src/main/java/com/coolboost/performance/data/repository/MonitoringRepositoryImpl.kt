package com.coolboost.performance.data.repository

import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.*
import com.coolboost.performance.domain.repository.MonitoringRepository
import com.coolboost.performance.monitoring.*
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

class MonitoringRepositoryImpl(
    private val thermalMonitor: ThermalMonitor,
    private val ramMonitor: RamMonitor,
    private val storageMonitor: StorageMonitor,
    private val cpuMonitor: CpuMonitor,
    private val fpsMonitor: FpsMonitor,
    private val batteryMonitor: BatteryMonitor,
    private val runningAppsMonitor: RunningAppsMonitor
) : MonitoringRepository {

    override fun observeThermalState(): Flow<ThermalState> = thermalMonitor.observe()
    override fun observeMemoryState(): Flow<MemoryState> = ramMonitor.observe()
    override fun observeStorageState(): Flow<StorageState> = storageMonitor.observe()
    override fun observeCpuState(): Flow<CpuState> = cpuMonitor.observe()
    override fun observeFpsState(): Flow<FpsState> = fpsMonitor.observe()
    override fun observeBatteryState(): Flow<BatteryState> = batteryMonitor.observe()

    override suspend fun getRunningApps(): List<RunningAppInfo> =
        runningAppsMonitor.getRecentlyActiveApps()

    override suspend fun getDashboardSnapshot(): DashboardSnapshot {
        val thermal = thermalMonitor.readCurrent()
        val memory = ramMonitor.readCurrent()
        val storage = storageMonitor.readCurrent()
        val cpu = cpuMonitor.readCurrent()
        val battery = batteryMonitor.readCurrent()
        // FPS snapshot defaults to a healthy reading outside of active Choreographer observation
        val fps = FpsState(
            currentFps = Constants.TARGET_FPS,
            averageFps = Constants.TARGET_FPS,
            jankCount = 0,
            isSmooth = true
        )

        val score = computePerformanceScore(thermal, memory, cpu, fps)

        return DashboardSnapshot(
            thermal = thermal,
            memory = memory,
            storage = storage,
            cpu = cpu,
            fps = fps,
            battery = battery,
            performanceScore = score
        )
    }

    private fun computePerformanceScore(
        thermal: ThermalState,
        memory: MemoryState,
        cpu: CpuState,
        fps: FpsState
    ): Int {
        val thermalScore = when (thermal.level) {
            ThermalLevel.NORMAL -> 100
            ThermalLevel.WARM -> 75
            ThermalLevel.HOT -> 45
            ThermalLevel.CRITICAL -> 15
        }
        val ramScore = (100 - memory.usedPercent).coerceIn(0, 100)
        val cpuScore = (100 - cpu.usagePercent).coerceIn(0, 100)
        val fpsScore = ((fps.averageFps / Constants.TARGET_FPS) * 100).roundToInt().coerceIn(0, 100)

        // Weighted composite: thermal health weighted highest since it's the app's core purpose
        return ((thermalScore * 0.4) + (ramScore * 0.25) + (cpuScore * 0.2) + (fpsScore * 0.15))
            .roundToInt()
            .coerceIn(0, 100)
    }
}
