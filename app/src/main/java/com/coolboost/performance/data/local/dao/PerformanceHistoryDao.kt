package com.coolboost.performance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.coolboost.performance.data.local.entity.PerformanceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformanceHistoryDao {
    @Insert
    suspend fun insert(entity: PerformanceHistoryEntity)

    @Query("SELECT * FROM performance_history WHERE timestamp >= :sinceMillis ORDER BY timestamp ASC")
    fun observeSince(sinceMillis: Long): Flow<List<PerformanceHistoryEntity>>

    @Query("SELECT AVG(performanceScore) FROM performance_history WHERE timestamp >= :sinceMillis")
    suspend fun getAverageScore(sinceMillis: Long): Int?

    @Query("DELETE FROM performance_history WHERE timestamp < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)
}
