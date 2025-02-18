package com.example.textbookexchangeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.textbookexchangeapp.R
import com.example.textbookexchangeapp.data.local.BookViewModel
import com.example.textbookexchangeapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: BookViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.dashboard_title)) },
                actions = {
                    Text(
                        text = stringResource(id = R.string.logout),
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { navController.navigate(Screen.Login.route) },
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
                text = stringResource(id = R.string.welcome_dashboard),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate(Screen.AddBook.route) }) {
                Text(stringResource(id = R.string.add_new_book))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { navController.navigate(Screen.BookList.route) }) {
                Text(stringResource(id = R.string.view_books))
            }
        }
    }
}
