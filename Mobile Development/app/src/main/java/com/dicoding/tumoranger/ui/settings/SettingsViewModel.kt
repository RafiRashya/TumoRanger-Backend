package com.dicoding.tumoranger.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.tumoranger.api.RetrofitClient
import com.dicoding.tumoranger.api.response.ProfileResponse
import com.dicoding.tumoranger.data.UserPreference
import com.dicoding.tumoranger.data.local.AppDatabase
import com.dicoding.tumoranger.data.local.entity.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsViewModel(application: Application, private val userPreference: UserPreference) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val userProfileDao = database.userProfileDao()

    val userProfile: LiveData<UserProfile> = userProfileDao.getUserProfile()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchUserProfile() {
        viewModelScope.launch {
            val token = userPreference.getUser().first().token
            if (token.isEmpty()) {
                _errorMessage.value = "Authentication token not found"
                return@launch
            }

            val apiService = RetrofitClient.getApiService(token)
            apiService.getUserProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { profileData ->
                            val userProfile = UserProfile(
                                id = 0,
                                username = profileData.name,
                                email = profileData.email
                            )
                            viewModelScope.launch {
                                userProfileDao.insertUserProfile(userProfile)
                            }
                        }
                    } else {
                        _errorMessage.value = "Failed to retrieve profile: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    _errorMessage.value = "Failed to retrieve profile: ${t.message}"
                }
            })
        }
    }
}