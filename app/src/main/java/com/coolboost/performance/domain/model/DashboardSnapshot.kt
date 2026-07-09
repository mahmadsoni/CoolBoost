package com.coolboost.performance.domain.model

/** Aggregated real-time state consumed directly by the Dashboard screen. */
data class DashboardSnapshot(
    val thermal: ThermalState,
    val memory: MemoryState,
    val storage: StorageState,
    val cpu: CpuState,
    val fps: FpsState,
    val battery: BatteryState,
    val performanceScore: Int, // 0-100 composite health score
    val activeCoolingMode: CoolingMode = CoolingMode.NONE
)
