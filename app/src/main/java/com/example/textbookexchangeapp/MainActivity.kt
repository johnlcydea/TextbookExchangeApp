package com.example.textbookexchangeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.textbookexchangeapp.data.local.AppDatabase
import com.example.textbookexchangeapp.data.local.BookRepository
import com.example.textbookexchangeapp.data.local.BookViewModel
import com.example.textbookexchangeapp.navigation.AppNavigation
import com.example.textbookexchangeapp.ui.theme.TextbookExchangeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = BookRepository(database.bookDao())
        val viewModel = BookViewModel(repository)

        setContent {
            TextbookExchangeAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController, viewModel)
            }
        }
    }
}
