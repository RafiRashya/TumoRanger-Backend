package com.dicoding.tumoranger.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tumoranger.api.RetrofitClient
import com.dicoding.tumoranger.api.response.ProfileResponse
import com.dicoding.tumoranger.data.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _profile = MutableLiveData<ProfileResponse?>()
    val profile: LiveData<ProfileResponse?> get() = _profile

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchUserProfile() {
        // Cek apakah data profil sudah ada, jika ada, tidak perlu fetch ulang
        if (_profile.value != null) {
            return
        }

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
                        _profile.value = response.body()
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
