package com.coolboost.performance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cooling_history")
data class CoolingHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val appsClosed: Int,
    val ramFreedBytes: Long,
    val cacheClearedBytes: Long,
    val tempBeforeCelsius: Float,
    val tempAfterCelsius: Float,
    val durationMs: Long,
    val timestamp: Long
)
