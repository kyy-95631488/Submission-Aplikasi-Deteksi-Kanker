package com.dicoding.asclepius.room_database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [AnalysisResult::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun analysisResultDao(): AnalysisResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "analysis_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
