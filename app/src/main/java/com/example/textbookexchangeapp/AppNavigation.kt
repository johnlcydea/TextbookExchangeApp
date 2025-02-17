package com.example.textbookexchangeapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.textbookexchangeapp.Screen
import com.example.textbookexchangeapp.data.local.BookViewModel
import com.example.textbookexchangeapp.LoginScreen
import com.example.textbookexchangeapp.DashboardScreen
import com.example.textbookexchangeapp.ui.AddBookScreen
import com.example.textbookexchangeapp.ui.BookListScreen

@Composable
fun AppNavigation(navController: NavHostController, viewModel: BookViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddBook.route) {
            AddBookScreen(
                viewModel = viewModel,
                onBookAdded = { navController.navigateUp() }
            )
        }

        composable(Screen.BookList.route) {
            BookListScreen(viewModel = viewModel)
        }
    }
}