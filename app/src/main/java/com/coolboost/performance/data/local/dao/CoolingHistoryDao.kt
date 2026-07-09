package com.coolboost.performance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.coolboost.performance.data.local.entity.CoolingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoolingHistoryDao {
    @Insert
    suspend fun insert(entity: CoolingHistoryEntity)

    @Query("SELECT * FROM cooling_history ORDER BY timestamp DESC LIMIT 100")
    fun observeAll(): Flow<List<CoolingHistoryEntity>>

    @Query("SELECT * FROM cooling_history ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLast(): CoolingHistoryEntity?

    @Query("DELETE FROM cooling_history WHERE timestamp < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)
}
