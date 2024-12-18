package com.dicoding.tumoranger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.tumoranger.data.UserPreference
import com.dicoding.tumoranger.data.dataStore
import com.dicoding.tumoranger.databinding.ActivityMainBinding
import com.dicoding.tumoranger.ui.login.LoginActivity
import com.dicoding.tumoranger.ui.settings.SettingsViewModel
import com.dicoding.tumoranger.ui.settings.SettingsViewModelFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(application, UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()

        super.onCreate(savedInstanceState)

        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySavedLanguage()

        val navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scan, R.id.navigation_history, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        settingsViewModel.fetchUserProfile()
        settingsViewModel.userProfile.observe(this) { profile ->
            if (profile != null) {
                Log.d("SettingsFragment", "Profile data: $profile")
                // Handle the profile data
            }
        }
        settingsViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
            Log.d("SettingsFragment", errorMessage)
            }
        }
    }

    private fun applySavedLanguage() {
        val sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selected_language", Locale.getDefault().language)
        val currentLanguage = Locale.getDefault().language

        if (savedLanguage != currentLanguage) {
            applyLanguage(savedLanguage ?: Locale.getDefault().language)
        }
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        supportActionBar?.apply {
            title = getString(R.string.app_name)
        }

        recreate()
    }

    private fun applySavedTheme() {
        val sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val savedTheme = sharedPreferences.getString("selected_theme", "system")
        when (savedTheme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
}