package com.dicoding.tumoranger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnosis_history")
data class DiagnosisHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diagnosis: String,
    val date: String
)