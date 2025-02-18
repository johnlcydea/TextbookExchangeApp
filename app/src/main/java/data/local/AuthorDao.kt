package com.example.textbookexchangeapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: Author): Long

    @Update
    suspend fun updateAuthor(author: Author): Int

    @Delete
    suspend fun deleteAuthor(author: Author): Int

    @Query("SELECT * FROM authors WHERE authorId = :authorId")
    fun getAuthorById(authorId: Int): Flow<Author>

    @Query("SELECT * FROM authors WHERE bookId = :bookId")
    fun getAuthorsForBook(bookId: Int): Flow<List<Author>>
}