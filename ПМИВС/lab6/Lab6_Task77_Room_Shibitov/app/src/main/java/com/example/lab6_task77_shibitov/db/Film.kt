package com.example.lab6_task77_shibitov.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "film")
data class Film(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val director: String,
    val year: Int,
    val genre: String
)
