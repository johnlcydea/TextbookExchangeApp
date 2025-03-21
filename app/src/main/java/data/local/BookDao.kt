package com.example.textbookexchangeapp.data.local

        import androidx.room.*
        import kotlinx.coroutines.flow.Flow

   @Dao
   interface BookDao {
       // Add this method to get books by sync status
       @Query("SELECT * FROM books WHERE syncStatus = :status")
       suspend fun getBooksByStatus(status: Int): List<Book>

       // Your existing methods...
       @Query("SELECT * FROM books ORDER BY id ASC")
       fun getAllBooks(): Flow<List<Book>>

       @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun insertBook(book: Book): Long

       @Update
       suspend fun updateBook(book: Book)

       @Delete
       suspend fun deleteBook(book: Book)

       @Query("SELECT * FROM books WHERE id = :id")
       fun getBookById(id: Int): Flow<Book?>

       @Query("SELECT * FROM books WHERE category = :category")
       fun getBooksByCategory(category: String): Flow<List<Book>>
   }