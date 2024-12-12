package com.dicoding.tumoranger.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val TOKEN_KEY = stringPreferencesKey("token")
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            val token = preferences[TOKEN_KEY] ?: ""
            User(token)
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    }

    suspend fun getLanguage(): String {
        var language = "en" // Nilai default
        dataStore.data.collect { preferences ->
            language = preferences[PreferencesKeys.LANGUAGE] ?: "en"
        }
        return language
    }

    suspend fun saveLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
        Log.d("UserPreference", "Saved language: $language") // Log untuk memastikan bahasa disimpan
    }

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language_key")
        val THEME = stringPreferencesKey("theme_key")
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}

data class User(val token: String)