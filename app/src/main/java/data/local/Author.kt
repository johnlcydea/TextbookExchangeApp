package com.example.textbookexchangeapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authors",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId")] // Add this index for better performance
)
data class Author(
    @PrimaryKey(autoGenerate = true)
    val authorId: Int = 0,
    val bookId: Int,
    val name: String,
    val email: String?
)