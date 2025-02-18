package com.example.textbookexchangeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    var name: String,
    var description: String? = null
)