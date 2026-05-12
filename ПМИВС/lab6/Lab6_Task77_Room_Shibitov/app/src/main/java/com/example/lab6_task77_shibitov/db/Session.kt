package com.example.lab6_task77_shibitov.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "session",
    foreignKeys = [ForeignKey(
        entity = Film::class,
        parentColumns = ["id"],
        childColumns = ["filmId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("filmId")]
)
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filmId: Long,
    val date: String,
    val viewers: Int,
    val ticketPrice: Double
)
