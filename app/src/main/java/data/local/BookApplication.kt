package com.example.textbookexchangeapp

import android.app.Application
import com.example.textbookexchangeapp.data.local.AppDatabase
import com.example.textbookexchangeapp.data.local.BookRepository

class BookApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BookRepository(database.bookDao()) }
}