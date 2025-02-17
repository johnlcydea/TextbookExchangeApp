package com.example.textbookexchangeapp.data.local

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    val allBooks: LiveData<List<Book>> = repository.allBooks.asLiveData()

    fun addBook(book: Book) = viewModelScope.launch {
        repository.insert(book)
    }

    fun update(book: Book) = viewModelScope.launch {
        repository.update(book)
    }

    fun delete(book: Book) = viewModelScope.launch {
        repository.delete(book)
    }

    fun getBookById(bookId: Int): LiveData<Book> {
        return repository.getBookById(bookId).asLiveData()
    }

    // Add ViewModelFactory
    class BookViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BookViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}