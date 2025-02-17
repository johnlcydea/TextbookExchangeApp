package com.example.textbookexchangeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.flow.flow
import com.example.textbookexchangeapp.ui.theme.TextbookExchangeAppTheme
import com.example.textbookexchangeapp.data.local.*
import com.example.textbookexchangeapp.ui.AddBookScreen
import com.example.textbookexchangeapp.ui.BookListScreen

sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Dashboard : AppScreen("dashboard")
    object AddBook : AppScreen("addBook")
    object BookList : AppScreen("bookList")
}

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: BookRepository
    private lateinit var viewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "book_database"
        ).build()

        // Initialize Repository and ViewModel
        repository = BookRepository(database.bookDao())
        viewModel = BookViewModel(repository)

        setContent {
            TextbookExchangeAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = AppScreen.Login.route
                ) {
                    composable(AppScreen.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(AppScreen.Dashboard.route) {
                                    popUpTo(AppScreen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(AppScreen.Dashboard.route) {
                        DashboardScreen(
                            viewModel = viewModel,
                            onLogout = {
                                navController.navigate(AppScreen.Login.route) {
                                    popUpTo(AppScreen.Dashboard.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(AppScreen.AddBook.route) {
                        AddBookScreen(
                            viewModel = viewModel,
                            onBookAdded = {
                                navController.navigateUp()
                            }
                        )
                    }

                    composable(AppScreen.BookList.route) {
                        BookListScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val validEmail = "user@example.com"
    val validPassword = "password123"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Textbook Exchange App") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to the Textbook Exchange App!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(0.8f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(0.8f),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        email.trim().isEmpty() || password.trim().isEmpty() -> {
                            errorMessage = "Fields cannot be empty"
                        }
                        email == validEmail && password == validPassword -> {
                            onLoginSuccess()
                        }
                        else -> {
                            errorMessage = "Invalid email or password"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Login")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: BookViewModel, onLogout: () -> Unit) {
    var currentScreen by rememberSaveable { mutableStateOf("dashboard") }

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

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    TextbookExchangeAppTheme {
        LoginScreen(onLoginSuccess = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboard() {
    val mockDao = object : BookDao {
        override suspend fun insertBook(book: Book) = 0L
        override suspend fun updateBook(book: Book) = 0
        override suspend fun deleteBook(book: Book) = 0
        override fun getBookById(bookId: Int) = flow { emit(Book(0, "", "", 0.0)) }
        override fun getAllBooks() = flow { emit(emptyList<Book>()) }
    }
    val mockRepository = BookRepository(mockDao)
    val mockViewModel = BookViewModel(mockRepository)

    TextbookExchangeAppTheme {
        DashboardScreen(viewModel = mockViewModel, onLogout = {})
    }
}