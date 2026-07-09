package com.coolboost.performance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "whitelist")
data class WhitelistEntity(
    @PrimaryKey val packageName: String,
    val addedAt: Long = System.currentTimeMillis()
)
