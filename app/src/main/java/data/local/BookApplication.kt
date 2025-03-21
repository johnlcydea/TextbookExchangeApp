package com.example.textbookexchangeapp

import android.app.Application
import com.example.textbookexchangeapp.data.local.AppDatabase
import com.example.textbookexchangeapp.data.local.BookRepository
import com.example.textbookexchangeapp.data.remote.ApiService
import com.example.textbookexchangeapp.data.remote.ApiClient

class BookApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val bookDao by lazy { database.bookDao() }

    // Use ApiClient instead of RetrofitClient
    val repository by lazy { BookRepository(bookDao) }
}