package com.example.textbookexchangeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao  // ✅ This is required
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: Book)

    @Query("SELECT * FROM books")
    fun getAllBooks(): List<Book>
}
