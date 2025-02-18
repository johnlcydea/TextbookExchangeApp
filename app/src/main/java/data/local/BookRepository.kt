package com.example.textbookexchangeapp.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {

    // ✅ Convert Flow to LiveData using `asLiveData()`
    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks().asLiveData()

    suspend fun insertBook(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }

    fun getBookById(id: Int): LiveData<Book?> {
        return bookDao.getBookById(id).asLiveData()  // ✅ Convert to LiveData
    }

    fun getBooksByCategory(categoryId: Int): LiveData<List<Book>> {
        return bookDao.getBooksByCategory(categoryId).asLiveData()  // ✅ Convert to LiveData
    }
}
