package com.example.textbookexchangeapp.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    // 🔹 LiveData Sources
    val allBooks: LiveData<List<Book>> = repository.allBooks
    val remoteBooks: LiveData<List<Book>> = repository.remoteBooks
    val firestoreBooks: LiveData<List<Book>> = repository.firestoreBooks

    // 🔹 API Functions
    fun fetchBooksFromApi() {
        viewModelScope.launch {
            repository.fetchBooksFromApi()
        }
    }

    // 🔹 CRUD Operations that sync with all data sources
    fun insertBook(book: Book) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    // 🔹 Direct Firestore Operations
    fun addBookToFirestore(book: Book) {
        viewModelScope.launch {
            repository.addBookToFirestore(book)
        }
    }

    fun updateBookInFirestore(book: Book) {
        viewModelScope.launch {
            repository.updateBookInFirestore(book)
        }
    }

    // Updated to use Book object instead of just the ID
    fun deleteBookFromFirestore(book: Book) {
        viewModelScope.launch {
            repository.deleteBookFromFirestore(book)
        }
    }

    // 🔹 Query Operations
    fun getBookById(id: Int): LiveData<Book?> {
        return repository.getBookById(id)
    }

    fun getBooksByCategory(category: String): LiveData<List<Book>> {
        return repository.getBooksByCategory(category)
    }
}