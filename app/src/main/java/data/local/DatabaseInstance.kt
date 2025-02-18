package com.example.textbookexchangeapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseInstance {

    @Volatile
    private var instance: AppDatabase? = null

    // Migration from version 1 to 2: Add Category table and categoryId column to Books
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS categories (" +
                        "categoryId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "categoryName TEXT NOT NULL)"
            )
            database.execSQL("ALTER TABLE books ADD COLUMN categoryId INTEGER DEFAULT 0 NOT NULL")
        }
    }

    // Migration from version 2 to 3: Add Author table and Many-to-Many (Book-Author)
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS authors (" +
                        "authorId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "authorName TEXT NOT NULL)"
            )
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS book_author_cross_ref (" +
                        "bookId INTEGER NOT NULL, " +
                        "authorId INTEGER NOT NULL, " +
                        "PRIMARY KEY (bookId, authorId), " +
                        "FOREIGN KEY (bookId) REFERENCES books(bookId) ON DELETE CASCADE, " +
                        "FOREIGN KEY (authorId) REFERENCES authors(authorId) ON DELETE CASCADE)"
            )
        }
    }

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "textbook_exchange_db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // ✅ Apply migrations
                .fallbackToDestructiveMigration() // ✅ Prevent crashes on schema change
                .build()
            instance = newInstance
            newInstance
        }
    }
}