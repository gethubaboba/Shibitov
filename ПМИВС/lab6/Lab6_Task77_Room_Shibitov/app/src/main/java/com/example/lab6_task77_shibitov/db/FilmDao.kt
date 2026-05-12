package com.example.lab6_task77_shibitov.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FilmDao {

    @Insert
    suspend fun insert(film: Film): Long

    // Все фильмы
    @Query("SELECT * FROM film ORDER BY title ASC")
    suspend fun getAllSortedByTitle(): List<Film>

    @Query("SELECT * FROM film ORDER BY year ASC")
    suspend fun getAllSortedByYear(): List<Film>

    // Фильтрация: год >= yearFrom
    @Query("SELECT * FROM film WHERE year >= :yearFrom ORDER BY year ASC")
    suspend fun getFilmsFromYear(yearFrom: Int): List<Film>

    // Группировка по жанру (COUNT)
    @Query("SELECT genre, COUNT(*) as cnt FROM film GROUP BY genre ORDER BY genre ASC")
    suspend fun getGroupedByGenre(): List<GenreCount>

    // Агрегация по сеансам для каждого фильма
    @Query("""
        SELECT f.id as filmId, f.title as title,
               COUNT(s.id) as sessionCount,
               SUM(s.viewers) as totalViewers,
               AVG(s.viewers) as avgViewers,
               MAX(s.viewers) as maxViewers,
               MIN(s.viewers) as minViewers
        FROM film f
        LEFT JOIN session s ON s.filmId = f.id
        GROUP BY f.id
        ORDER BY f.title ASC
    """)
    suspend fun getFilmStats(): List<FilmStats>
}

data class GenreCount(val genre: String, val cnt: Int)
