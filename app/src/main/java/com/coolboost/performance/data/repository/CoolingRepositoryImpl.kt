package com.coolboost.performance.data.repository

import com.coolboost.performance.cooling.CoolingManager
import com.coolboost.performance.domain.model.CoolingMode
import com.coolboost.performance.domain.model.CoolingResult
import com.coolboost.performance.domain.repository.CoolingRepository

class CoolingRepositoryImpl(
    private val coolingManager: CoolingManager
) : CoolingRepository {

    override suspend fun runSmartCooling(): CoolingResult = coolingManager.runSmart()

    override suspend fun runExtremeCooling(): CoolingResult = coolingManager.runExtreme()

    override suspend fun enableGameMode(packageName: String?): Boolean {
        coolingManager.setGameMode(true)
        return true
    }

    override suspend fun disableGameMode(): Boolean {
        coolingManager.setGameMode(false)
        return true
    }

    override suspend fun getActiveCoolingMode(): CoolingMode = coolingManager.getActiveMode()

    override suspend fun getLastCoolingResult(): CoolingResult? = coolingManager.getLastResult()
}
