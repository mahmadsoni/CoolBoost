package com.coolboost.performance.domain.repository

import com.coolboost.performance.domain.model.*
import kotlinx.coroutines.flow.Flow

/** Contract for observing live device telemetry. Implemented in data/repository. */
interface MonitoringRepository {
    fun observeThermalState(): Flow<ThermalState>
    fun observeMemoryState(): Flow<MemoryState>
    fun observeStorageState(): Flow<StorageState>
    fun observeCpuState(): Flow<CpuState>
    fun observeFpsState(): Flow<FpsState>
    fun observeBatteryState(): Flow<BatteryState>
    suspend fun getRunningApps(): List<RunningAppInfo>
    suspend fun getDashboardSnapshot(): DashboardSnapshot
}
