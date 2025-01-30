package com.example.textbookexchangeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.textbookexchangeapp.ui.theme.TextbookExchangeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TextbookExchangeAppTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * App Navigation - Controls Login & Dashboard Flow
 */
@Composable
fun AppNavigation() {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        DashboardScreen(onLogout = { isLoggedIn = false })
    } else {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    }
}

/**
 * Login Screen - Checks Credentials, Navigates to Dashboard on Success
 */
@OptIn(ExperimentalMaterial3Api::class) // ✅ FIX: Opt-in to Experimental API
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
            SmallTopAppBar(title = { Text("Textbook Exchange App") })
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
            Text(text = "Welcome to the Textbook Exchange App!", style = MaterialTheme.typography.headlineMedium)

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

            Spacer(modifier = Modifier.height(10.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (email == validEmail && password == validPassword) {
                        onLoginSuccess()
                    } else {
                        errorMessage = "Invalid email or password"
                    }
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Login")
            }
        }
    }
}

/**
 * Dashboard Screen - Main Hub After Login
 */
@OptIn(ExperimentalMaterial3Api::class) // ✅ FIX: Opt-in to Experimental API
@Composable
fun DashboardScreen(onLogout: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SmallTopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    Text(
                        text = "Logout",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onLogout() },
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
            Text(text = "Welcome to the Dashboard!", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { /* TODO: Implement Add Book Feature */ },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Add New Book")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { /* TODO: Implement View Books Feature */ },
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
    }
}

/**
 * Preview UI
 */
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
    TextbookExchangeAppTheme {
        DashboardScreen(onLogout = {})
    }
}
