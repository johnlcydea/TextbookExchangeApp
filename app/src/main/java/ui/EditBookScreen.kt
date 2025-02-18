package com.example.textbookexchangeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.textbookexchangeapp.data.local.Book
import com.example.textbookexchangeapp.data.local.BookViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    navController: NavController,
    viewModel: BookViewModel,
    bookId: Int
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var errorState by remember {
        mutableStateOf<EditBookErrorState>(EditBookErrorState.None)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Use observeAsState with LiveData
    val book by viewModel.getBookById(bookId).observeAsState()

    // Populate form when book is loaded
    LaunchedEffect(book) {
        book?.let { currentBook ->
            title = currentBook.title
            author = currentBook.author ?: ""
            price = currentBook.price.toString()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Book") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Cancel")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title TextField
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    errorState = EditBookErrorState.None
                },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorState is EditBookErrorState.TitleError,
                supportingText = {
                    if (errorState is EditBookErrorState.TitleError) {
                        Text(
                            text = "Title cannot be empty",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )

            // Author TextField
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Author (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )

            // Price TextField
            OutlinedTextField(
                value = price,
                onValueChange = {
                    price = it
                    errorState = EditBookErrorState.None
                },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorState is EditBookErrorState.PriceError,
                supportingText = {
                    if (errorState is EditBookErrorState.PriceError) {
                        Text(
                            text = "Invalid price. Must be a positive number.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cancel Button
                OutlinedButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Cancel")
                }

                // Save Button
                Button(
                    onClick = {
                        // Validate inputs
                        when {
                            title.isBlank() -> {
                                errorState = EditBookErrorState.TitleError
                                return@Button
                            }
                            price.toDoubleOrNull()?.let { it <= 0 } ?: true -> {
                                errorState = EditBookErrorState.PriceError
                                return@Button
                            }
                            else -> {
                                book?.let { currentBook ->
                                    val updatedBook = currentBook.copy(
                                        title = title.trim(),
                                        author = author.trim().takeIf { it.isNotEmpty() },
                                        price = price.toDouble()
                                    )

                                    viewModel.updateBook(updatedBook)

                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Book updated successfully")
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

// Sealed class for managing different error states
sealed class EditBookErrorState {
    object None : EditBookErrorState()
    object TitleError : EditBookErrorState()
    object PriceError : EditBookErrorState()
}