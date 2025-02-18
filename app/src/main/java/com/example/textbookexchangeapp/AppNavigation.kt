package com.example.textbookexchangeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.textbookexchangeapp.ui.screens.*
import com.example.textbookexchangeapp.data.local.BookViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object AddBook : Screen("addBook")
    object BookList : Screen("bookList")
    object EditBook : Screen("editBook/{bookId}") {
        fun createRoute(bookId: Int) = "editBook/$bookId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: BookViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, viewModel)
        }

        composable(Screen.AddBook.route) {
            AddBookScreen(navController, viewModel)
        }

        composable(Screen.BookList.route) {
            BookListScreen(navController, viewModel)
        }

        // Add EditBook screen with bookId parameter
        composable(
            route = Screen.EditBook.route,
            arguments = listOf(
                navArgument("bookId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: return@composable
            EditBookScreen(
                navController = navController,
                viewModel = viewModel,
                bookId = bookId
            )
        }
    }
}