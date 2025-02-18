package com.example.textbookexchangeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.textbookexchangeapp.R
import com.example.textbookexchangeapp.data.local.Book
import com.example.textbookexchangeapp.data.local.BookViewModel
import com.example.textbookexchangeapp.navigation.Screen
import kotlinx.coroutines.launch

data class AddBookState(
    val title: String = "",
    val author: String = "",
    val price: String = "",
    val errorMessage: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    navController: NavController,
    viewModel: BookViewModel
) {
    var state by remember { mutableStateOf(AddBookState()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // ✅ Move string resources OUTSIDE onClick
    val titleRequiredMessage = stringResource(R.string.title_required)
    val invalidPriceMessage = stringResource(R.string.invalid_price)
    val bookAddedMessage = stringResource(R.string.book_added_successfully)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.add_book_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Dashboard.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Dashboard")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BookInputFields(
                state = state,
                onStateChange = { state = it }
            )

            if (state.errorMessage.isNotEmpty()) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    val error = when {
                        state.title.isBlank() -> titleRequiredMessage
                        state.price.toDoubleOrNull()?.let { it <= 0 } ?: true -> invalidPriceMessage
                        else -> {
                            viewModel.insertBook(
                                Book(
                                    title = state.title,
                                    author = state.author.takeIf { it.isNotBlank() },
                                    price = state.price.toDoubleOrNull() ?: 0.0,
                                    categoryId = 1
                                )
                            )

                            // ✅ Reset input fields
                            state = AddBookState()

                            // ✅ Show Snackbar inside coroutine
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(bookAddedMessage)
                            }
                            return@Button
                        }
                    }
                    state = state.copy(errorMessage = error)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_book_button))
            }
        }
    }
}

@Composable
private fun BookInputFields(
    state: AddBookState,
    onStateChange: (AddBookState) -> Unit
) {
    OutlinedTextField(
        value = state.title,
        onValueChange = { onStateChange(state.copy(title = it)) },
        label = { Text(stringResource(R.string.title_label)) },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = state.author,
        onValueChange = { onStateChange(state.copy(author = it)) },
        label = { Text(stringResource(R.string.author_label)) },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = state.price,
        onValueChange = { onStateChange(state.copy(price = it)) },
        label = { Text(stringResource(R.string.price_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}
