package com.example.textbookexchangeapp

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object AddBook : Screen("addBook")
    object BookList : Screen("bookList")
}
