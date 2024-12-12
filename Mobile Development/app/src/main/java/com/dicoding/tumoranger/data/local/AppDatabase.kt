package com.dicoding.tumoranger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.tumoranger.data.local.entity.DiagnosisHistory
import com.dicoding.tumoranger.data.local.entity.UserProfile

@Database(entities = [UserProfile::class, DiagnosisHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun diagnosisHistoryDao(): DiagnosisHistoryDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app_database"
                ).build().also { instance = it }
            }
    }
}