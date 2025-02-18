package com.example.textbookexchangeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
public final data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var price: Double,
    var categoryId: Int,
    var author: String? = null
)