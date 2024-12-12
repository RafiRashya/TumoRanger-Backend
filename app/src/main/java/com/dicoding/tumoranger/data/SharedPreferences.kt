package com.dicoding.tumoranger.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ThemePreference(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME = "selected_theme"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }

    fun saveTheme(theme: String) {
        sharedPreferences.edit().putString(KEY_THEME, theme).apply()
    }

    fun getTheme(): String {
        return sharedPreferences.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    fun applyTheme() {
        when (getTheme()) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
