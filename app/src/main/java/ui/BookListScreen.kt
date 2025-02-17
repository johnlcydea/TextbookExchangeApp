package com.example.textbookexchangeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.textbookexchangeapp.data.local.Book
import com.example.textbookexchangeapp.data.local.BookViewModel
import com.example.textbookexchangeapp.data.local.BookRepository
import com.example.textbookexchangeapp.data.local.BookDao
import com.example.textbookexchangeapp.ui.theme.TextbookExchangeAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(viewModel: BookViewModel) {
    val books by viewModel.allBooks.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Book List") }
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
                text = "Your Books",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (books.isEmpty()) {
                Text(
                    text = "No books found. Add some books!",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(books) { book ->
                        BookItem(book)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookItem(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Author: ${book.author}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Price: $${book.price}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// For Preview
private class FakeBookDao : BookDao {
    override suspend fun insertBook(book: Book) = 0L
    override suspend fun updateBook(book: Book) = 0
    override suspend fun deleteBook(book: Book) = 0
    override fun getBookById(bookId: Int): Flow<Book> = flow {
        emit(Book(0, "", "", 0.0))
    }
    override fun getAllBooks(): Flow<List<Book>> = flow {
        emit(emptyList<Book>())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookListScreen() {
    val mockRepository = BookRepository(FakeBookDao())
    val viewModel = BookViewModel(mockRepository)

    TextbookExchangeAppTheme {
        BookListScreen(viewModel = viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookItem() {
    val fakeBook = Book(
        id = 1,
        title = "Sample Book",
        author = "John Doe",
        price = 29.99
    )

    TextbookExchangeAppTheme {
        BookItem(book = fakeBook)
    }
}