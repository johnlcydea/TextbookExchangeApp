package com.example.textbookexchangeapp.data.local

import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {

    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun insert(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun update(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun delete(book: Book) {
        bookDao.deleteBook(book)
    }

    fun getBookById(id: Int): Flow<Book> {
        return bookDao.getBookById(id)
    }
}