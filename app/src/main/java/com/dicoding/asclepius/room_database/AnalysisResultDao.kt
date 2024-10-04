package com.dicoding.asclepius.room_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnalysisResultDao {
    @Query("SELECT * FROM analysis_results")
    fun getAllResults(): LiveData<List<AnalysisResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: AnalysisResult)
}
