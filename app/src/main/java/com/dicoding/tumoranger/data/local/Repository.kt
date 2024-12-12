package com.dicoding.tumoranger.data.local
/*
import androidx.lifecycle.LiveData
import com.dicoding.tumoranger.api.ApiService
import com.dicoding.tumoranger.data.local.entity.DiagnosisHistory
import com.dicoding.tumoranger.data.local.entity.UserProfile

class Repository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {

    val userProfile: LiveData<UserProfile> = database.userProfileDao().getUserProfile()
    val diagnosisHistory: LiveData<List<DiagnosisHistory>> = database.diagnosisHistoryDao().getAllHistory()

    suspend fun fetchUserProfile() {
        val response = apiService.getUserProfile()
        if (response.isSuccessful) {
            response.body()?.let { userProfile ->
                database.userProfileDao().insertUserProfile(userProfile)
            }
        }
    }

    suspend fun fetchDiagnosisHistory() {
        val response = apiService.getDiagnosisHistory()
        if (response.isSuccessful) {
            response.body()?.let { historyList ->
                historyList.forEach { history ->
                    database.diagnosisHistoryDao().insertHistory(history)
                }
            }
        }
    }

    suspend fun syncData() {
        fetchUserProfile()
        fetchDiagnosisHistory()
    }
}*/