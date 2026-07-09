package com.coolboost.performance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coolboost.performance.data.local.entity.WhitelistEntity

@Dao
interface WhitelistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WhitelistEntity)

    @Query("DELETE FROM whitelist WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("SELECT packageName FROM whitelist")
    suspend fun getAll(): List<String>
}
