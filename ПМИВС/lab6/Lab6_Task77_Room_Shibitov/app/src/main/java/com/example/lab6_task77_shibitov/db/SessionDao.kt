package com.example.lab6_task77_shibitov.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(session: Session): Long

    @Query("SELECT * FROM session ORDER BY date ASC")
    suspend fun getAll(): List<Session>

    @Query("SELECT * FROM session WHERE filmId = :filmId ORDER BY date ASC")
    suspend fun getByFilm(filmId: Long): List<Session>
}
