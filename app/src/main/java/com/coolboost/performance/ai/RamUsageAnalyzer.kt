package com.coolboost.performance.ai

import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.MemoryState
import com.coolboost.performance.domain.model.RunningAppInfo

/** Analyzes RAM pressure and identifies which running apps contribute most. */
class RamUsageAnalyzer {

    fun isUnderPressure(memoryState: MemoryState): Boolean =
        memoryState.usedPercent >= Constants.RAM_WARNING_PERCENT || memoryState.isLowMemory

    fun rankMemoryHogs(apps: List<RunningAppInfo>, limit: Int = 5): List<RunningAppInfo> =
        apps.sortedByDescending { it.memoryBytes }.take(limit)
}
