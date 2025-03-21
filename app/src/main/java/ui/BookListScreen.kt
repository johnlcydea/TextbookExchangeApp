package com.example.textbookexchangeapp.ui.screens

            import android.util.Log
            import androidx.compose.foundation.layout.*
            import androidx.compose.foundation.lazy.LazyColumn
            import androidx.compose.foundation.lazy.items
            import androidx.compose.material.icons.Icons
            import androidx.compose.material.icons.automirrored.filled.ArrowBack
            import androidx.compose.material.icons.filled.Delete
            import androidx.compose.material.icons.filled.Edit
            import androidx.compose.material3.*
            import androidx.compose.runtime.*
            import androidx.compose.runtime.livedata.observeAsState
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.unit.dp
            import androidx.navigation.NavController
            import com.example.textbookexchangeapp.data.local.Book
            import com.example.textbookexchangeapp.data.local.BookViewModel
            import com.example.textbookexchangeapp.navigation.Screen

            private const val TAG = "BookListScreen"

            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun BookListScreen(navController: NavController, viewModel: BookViewModel) {
                // Default to LOCAL data source for safety
                var selectedDataSource by remember { mutableStateOf(DataSource.LOCAL) }

                // Safely observe local books
                val localBooks by viewModel.allBooks.observeAsState(emptyList())

                // Safely observe Firestore books with fallback
                val firestoreBooks by viewModel.firestoreBooks.observeAsState(emptyList())

                // Determine which books to display
                val booksToDisplay = when (selectedDataSource) {
                    DataSource.LOCAL -> localBooks
                    DataSource.FIRESTORE -> firestoreBooks
                }

                // Fetch data on screen load
                LaunchedEffect(Unit) {
                    try {
                        viewModel.fetchBooksFromApi()
                        Log.d(TAG, "Successfully requested API fetch")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to fetch books from API: ${e.message}")
                    }
                }

                Scaffold(
                    topBar = {
                        Column {
                            CenterAlignedTopAppBar(
                                title = { Text("Book List") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        try {
                                            navController.navigate(Screen.Dashboard.route)
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Navigation error: ${e.message}")
                                        }
                                    }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Dashboard")
                                    }
                                }
                            )

                            // Data source selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                FilterChip(
                                    selected = selectedDataSource == DataSource.LOCAL,
                                    onClick = { selectedDataSource = DataSource.LOCAL },
                                    label = { Text("Local Database") }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                FilterChip(
                                    selected = selectedDataSource == DataSource.FIRESTORE,
                                    onClick = { selectedDataSource = DataSource.FIRESTORE },
                                    label = { Text("Firestore") }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    BookListContent(
                        bookList = booksToDisplay,
                        onDeleteBook = { book ->
                            try {
                                when (selectedDataSource) {
                                    DataSource.LOCAL -> viewModel.deleteBook(book)
                                    DataSource.FIRESTORE -> viewModel.deleteBookFromFirestore(book) // Fixed: passing book object instead of book.id
                                }
                                Log.d(TAG, "Delete requested for book ID: ${book.id}")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error deleting book: ${e.message}")
                            }
                        },
                        onEditBook = { bookId ->
                            try {
                                navController.navigate("editBook/$bookId")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error navigating to edit screen: ${e.message}")
                            }
                        },
                        modifier = Modifier.padding(paddingValues),
                        dataSource = selectedDataSource
                    )
                }
            }

            @Composable
            private fun BookListContent(
                bookList: List<Book>,
                onDeleteBook: (Book) -> Unit,
                onEditBook: (Int) -> Unit,
                modifier: Modifier = Modifier,
                dataSource: DataSource
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Showing books from ${dataSource.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (bookList.isEmpty()) {
                        Text(
                            text = "No books available",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(bookList) { book ->
                                BookItem(book, onDeleteBook, onEditBook)
                            }
                        }
                    }
                }
            }

            @Composable
            private fun BookItem(
                book: Book,
                onDeleteBook: (Book) -> Unit,
                onEditBook: (Int) -> Unit
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "📖 ${book.title}", style = MaterialTheme.typography.titleMedium)
                        Text(text = "✍️ Author: ${book.author ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "💰 Price: $${book.price}", style = MaterialTheme.typography.bodyMedium)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { onEditBook(book.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Book")
                            }
                            IconButton(onClick = { onDeleteBook(book) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Book", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            enum class DataSource(val displayName: String) {
                LOCAL("Local Database"),
                FIRESTORE("Firestore")
            }