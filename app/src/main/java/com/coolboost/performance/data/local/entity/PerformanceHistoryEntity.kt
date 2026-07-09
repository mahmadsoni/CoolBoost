package com.coolboost.performance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "performance_history")
data class PerformanceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val cpuPercent: Int,
    val ramPercent: Int,
    val tempCelsius: Float,
    val fps: Double,
    val performanceScore: Int
)
