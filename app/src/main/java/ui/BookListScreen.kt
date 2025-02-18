package com.example.textbookexchangeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState  // Add this import
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.textbookexchangeapp.R
import com.example.textbookexchangeapp.data.local.Book
import com.example.textbookexchangeapp.data.local.BookViewModel
import com.example.textbookexchangeapp.navigation.Screen
import androidx.compose.material.icons.filled.Add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(navController: NavController, viewModel: BookViewModel) {
    val bookList by viewModel.allBooks.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.book_list_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Dashboard.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Dashboard")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddBook.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { paddingValues ->
        BookListContent(
            bookList = bookList,
            onDeleteBook = { book -> viewModel.deleteBook(book) },
            onEditBook = { bookId -> navController.navigate("editBook/$bookId") },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun BookListContent(
    bookList: List<Book>,
    onDeleteBook: (Book) -> Unit,
    onEditBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (bookList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_books_available),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
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
                // Edit Button
                IconButton(onClick = { onEditBook(book.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Book")
                }
                // Delete Button
                IconButton(onClick = { onDeleteBook(book) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Book", tint = Color.Red)
                }
            }
        }
    }
}