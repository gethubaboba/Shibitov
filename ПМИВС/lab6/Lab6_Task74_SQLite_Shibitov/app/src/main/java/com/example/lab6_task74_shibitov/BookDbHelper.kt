package com.example.lab6_task74_shibitov

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Book(
    val id: Long = 0,
    val author: String,
    val title: String,
    val year: Int
)

class BookDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "books.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "book"
        const val COL_ID = "_id"
        const val COL_AUTHOR = "author"
        const val COL_TITLE = "title"
        const val COL_YEAR = "year"

        private val INITIAL_DATA = listOf(
            Book(author = "Толстой Лев Николаевич", title = "Война и мир", year = 1869),
            Book(author = "Достоевский Фёдор Михайлович", title = "Преступление и наказание", year = 1866),
            Book(author = "Пушкин Александр Сергеевич", title = "Евгений Онегин", year = 1833),
            Book(author = "Чехов Антон Павлович", title = "Вишнёвый сад", year = 1904),
            Book(author = "Булгаков Михаил Афанасьевич", title = "Мастер и Маргарита", year = 1967)
        )
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_AUTHOR TEXT NOT NULL,
                $COL_TITLE TEXT NOT NULL,
                $COL_YEAR INTEGER NOT NULL
            )"""
        )
        resetData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun resetData(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM $TABLE_NAME")
        INITIAL_DATA.forEach { book ->
            db.insert(TABLE_NAME, null, bookToValues(book))
        }
    }

    fun initData() {
        resetData(writableDatabase)
    }

    private fun bookToValues(b: Book) = ContentValues().apply {
        put(COL_AUTHOR, b.author)
        put(COL_TITLE, b.title)
        put(COL_YEAR, b.year)
    }

    fun getAllBooks(): List<Book> {
        val list = mutableListOf<Book>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_ID", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Book(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        author = cursor.getString(cursor.getColumnIndexOrThrow(COL_AUTHOR)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                        year = cursor.getInt(cursor.getColumnIndexOrThrow(COL_YEAR))
                    )
                )
            }
        }
        return list
    }

    fun getFirstBook(): Book? = getAllBooks().firstOrNull()

    fun insertBook(book: Book): Long =
        writableDatabase.insert(TABLE_NAME, null, bookToValues(book))

    fun updateBook(id: Long, book: Book): Int =
        writableDatabase.update(
            TABLE_NAME, bookToValues(book),
            "$COL_ID = ?", arrayOf(id.toString())
        )
}
