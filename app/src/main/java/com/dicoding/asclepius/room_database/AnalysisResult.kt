package com.dicoding.asclepius.room_database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_results")
data class AnalysisResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val result: String,
    val imageUri: String,
    val timestamp: Long = System.currentTimeMillis() // Save current time in milliseconds
)

