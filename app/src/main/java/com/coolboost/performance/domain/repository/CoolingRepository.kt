package com.coolboost.performance.domain.repository

import com.coolboost.performance.domain.model.CoolingMode
import com.coolboost.performance.domain.model.CoolingResult

interface CoolingRepository {
    suspend fun runSmartCooling(): CoolingResult
    suspend fun runExtremeCooling(): CoolingResult
    suspend fun enableGameMode(packageName: String?): Boolean
    suspend fun disableGameMode(): Boolean
    suspend fun getActiveCoolingMode(): CoolingMode
    suspend fun getLastCoolingResult(): CoolingResult?
}
