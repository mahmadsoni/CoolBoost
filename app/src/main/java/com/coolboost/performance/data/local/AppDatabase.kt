package com.coolboost.performance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.coolboost.performance.core.Constants
import com.coolboost.performance.data.local.dao.CoolingHistoryDao
import com.coolboost.performance.data.local.dao.PerformanceHistoryDao
import com.coolboost.performance.data.local.dao.WhitelistDao
import com.coolboost.performance.data.local.entity.CoolingHistoryEntity
import com.coolboost.performance.data.local.entity.PerformanceHistoryEntity
import com.coolboost.performance.data.local.entity.WhitelistEntity

@Database(
    entities = [
        PerformanceHistoryEntity::class,
        CoolingHistoryEntity::class,
        WhitelistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun performanceHistoryDao(): PerformanceHistoryDao
    abstract fun coolingHistoryDao(): CoolingHistoryDao
    abstract fun whitelistDao(): WhitelistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
