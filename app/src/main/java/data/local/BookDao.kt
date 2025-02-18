package com.example.textbookexchangeapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: Int): Flow<Book?>  // ✅ Still Flow, ViewModel will convert it to LiveData

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>  // ✅ Still Flow

    @Query("SELECT * FROM books WHERE categoryId = :categoryId")
    fun getBooksByCategory(categoryId: Int): Flow<List<Book>>  // ✅ Still Flow
}
