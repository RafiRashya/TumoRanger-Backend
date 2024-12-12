package com.dicoding.tumoranger.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.tumoranger.data.local.entity.DiagnosisHistory
import com.dicoding.tumoranger.data.local.entity.UserProfile

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 0")
    fun getUserProfile(): LiveData<UserProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)
}

@Dao
interface DiagnosisHistoryDao {
    @Query("SELECT * FROM diagnosis_history ORDER BY date DESC")
    fun getAllHistory(): LiveData<List<DiagnosisHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: DiagnosisHistory)
}