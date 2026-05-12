package com.example.lab6_task77_shibitov.db

// Результат агрегирующего запроса по фильму
data class FilmStats(
    val filmId: Long,
    val title: String,
    val sessionCount: Int,
    val totalViewers: Long,
    val avgViewers: Double,
    val maxViewers: Int,
    val minViewers: Int
)
