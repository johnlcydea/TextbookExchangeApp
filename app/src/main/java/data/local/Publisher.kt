package com.example.textbookexchangeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Publisher(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
)