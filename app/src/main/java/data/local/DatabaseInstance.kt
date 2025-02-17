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
                "textbook_exchange_db" // ✅ Use a consistent database name
            )
                .fallbackToDestructiveMigration() // ✅ Prevent crashes if schema changes
                .build()
            instance = newInstance
            newInstance
        }
    }
}
