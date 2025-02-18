package com.example.textbookexchangeapp.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    // ✅ Using LiveData instead of Flow
    val allBooks: LiveData<List<Book>> = repository.allBooks

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

    fun getBookById(id: Int): LiveData<Book?> {
        return repository.getBookById(id)  // ✅ Now returns LiveData
    }

    fun getBooksByCategory(categoryId: Int): LiveData<List<Book>> {
        return repository.getBooksByCategory(categoryId)  // ✅ Now returns LiveData
    }
}
