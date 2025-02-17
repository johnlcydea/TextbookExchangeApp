package com.example.textbookexchangeapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.textbookexchangeapp.data.local.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: BookViewModel, onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf("dashboard") }

    when (currentScreen) {
        "dashboard" -> DashboardView(
            onAddBook = { currentScreen = "add_book" },
            onViewBooks = { currentScreen = "view_books" },
            onLogout = onLogout
        )
        "add_book" -> AddBookScreen(
            viewModel = viewModel,
            onBookAdded = { currentScreen = "dashboard" }
        )
        "view_books" -> BookListScreen(viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardView(
    onAddBook: () -> Unit,
    onViewBooks: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    Text(
                        text = "Logout",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { showLogoutDialog = true },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to the Dashboard!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAddBook,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Add New Book")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onViewBooks,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("View Books")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { /* TODO: Implement Profile Feature */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Profile")
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
