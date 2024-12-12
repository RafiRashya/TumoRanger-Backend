package com.dicoding.tumoranger.ui.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.tumoranger.data.UserPreference

class SettingsViewModelFactory(
    private val application: Application,
    private val userPreference: UserPreference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(application, userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
