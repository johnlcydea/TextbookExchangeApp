package com.example.textbookexchangeapp.data.local

import androidx.room.Entity

@Entity(primaryKeys = ["bookId", "authorId"])
data class BookAuthorCrossRef(
    val bookId: Int,
    val authorId: Int
)