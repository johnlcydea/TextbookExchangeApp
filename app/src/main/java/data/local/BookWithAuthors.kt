package com.example.textbookexchangeapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithAuthors(
    @Embedded
    var book: Book,

    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    var authors: List<Author>
)