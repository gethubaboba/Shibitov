package com.example.lab6_task77_shibitov.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Film::class, Session::class], version = 1, exportSchema = false)
abstract class FilmDatabase : RoomDatabase() {

    abstract fun filmDao(): FilmDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: FilmDatabase? = null

        fun getInstance(context: Context): FilmDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, FilmDatabase::class.java, "filmoteka.db")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Предзаполнение при первом создании
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val filmDao = database.filmDao()
                                    val sessionDao = database.sessionDao()

                                    val id1 = filmDao.insert(Film(title = "Интерстеллар", director = "Кристофер Нолан", year = 2014, genre = "Sci-Fi"))
                                    val id2 = filmDao.insert(Film(title = "Начало", director = "Кристофер Нолан", year = 2010, genre = "Sci-Fi"))
                                    val id3 = filmDao.insert(Film(title = "Зелёная миля", director = "Фрэнк Дарабонт", year = 1999, genre = "Драма"))
                                    val id4 = filmDao.insert(Film(title = "Форрест Гамп", director = "Роберт Земекис", year = 1994, genre = "Драма"))
                                    val id5 = filmDao.insert(Film(title = "Джокер", director = "Тодд Филлипс", year = 2019, genre = "Триллер"))

                                    sessionDao.insert(Session(filmId = id1, date = "01.03.2025", viewers = 120, ticketPrice = 15.0))
                                    sessionDao.insert(Session(filmId = id1, date = "05.03.2025", viewers = 95, ticketPrice = 15.0))
                                    sessionDao.insert(Session(filmId = id1, date = "10.03.2025", viewers = 140, ticketPrice = 12.0))
                                    sessionDao.insert(Session(filmId = id2, date = "02.03.2025", viewers = 80, ticketPrice = 15.0))
                                    sessionDao.insert(Session(filmId = id2, date = "08.03.2025", viewers = 110, ticketPrice = 15.0))
                                    sessionDao.insert(Session(filmId = id3, date = "03.03.2025", viewers = 60, ticketPrice = 10.0))
                                    sessionDao.insert(Session(filmId = id4, date = "04.03.2025", viewers = 75, ticketPrice = 10.0))
                                    sessionDao.insert(Session(filmId = id5, date = "06.03.2025", viewers = 200, ticketPrice = 18.0))
                                    sessionDao.insert(Session(filmId = id5, date = "07.03.2025", viewers = 185, ticketPrice = 18.0))
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
