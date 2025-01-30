package com.example.textbookexchangeapp.data.local

import android.content.Context
import androidx.room.Room

object DatabaseInstance {
    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            instance = newInstance
            newInstance
        }
    }
}
