package com.coolboost.performance.cooling

import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.CoolingMode
import com.coolboost.performance.domain.model.CoolingResult

/**
 * Facade coordinating cooling engines and tracking the currently active mode,
 * used by the CoolingRepository implementation and the CoolingService.
 */
class CoolingManager(
    private val smartCoolingEngine: SmartCoolingEngine,
    private val extremeCoolingEngine: ExtremeCoolingEngine
) {
    @Volatile
    private var activeMode: CoolingMode = CoolingMode.NONE

    @Volatile
    private var lastResult: CoolingResult? = null

    suspend fun runSmart(): CoolingResult {
        activeMode = CoolingMode.SMART
        val result = smartCoolingEngine.run()
        lastResult = result
        activeMode = CoolingMode.NONE
        return result
    }

    suspend fun runExtreme(): CoolingResult {
        check(extremeCoolingEngine.canRunAgain(Constants.EXTREME_COOLING_MIN_INTERVAL_MS)) {
            "Extreme cooling was run too recently. Please wait before running again."
        }
        activeMode = CoolingMode.EXTREME
        val result = extremeCoolingEngine.run()
        lastResult = result
        activeMode = CoolingMode.NONE
        return result
    }

    fun setGameMode(active: Boolean) {
        activeMode = if (active) CoolingMode.GAME_MODE else CoolingMode.NONE
    }

    fun getActiveMode(): CoolingMode = activeMode
    fun getLastResult(): CoolingResult? = lastResult
}
