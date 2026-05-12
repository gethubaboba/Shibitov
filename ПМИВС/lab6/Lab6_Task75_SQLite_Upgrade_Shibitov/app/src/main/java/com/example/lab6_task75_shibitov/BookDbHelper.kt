package com.example.lab6_task75_shibitov

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Book(
    val id: Long = 0,
    val author: String,
    val title: String,
    val year: Int,
    // Новые поля версии 2
    val publisher: String = "",
    val pages: Int = 0,
    val price: Int = 0
)

class BookDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "books_v2.db"
        // Версия 1 — базовая схема (ID, Автор ФИО, Название книги, Год издания)
        // Версия 2 — добавлены поля Издательство (char), Количество страниц (integer), Стоимость (integer)
        const val DATABASE_VERSION = 2

        const val TABLE_NAME = "book"
        const val COL_ID = "_id"
        const val COL_AUTHOR = "author"
        const val COL_TITLE = "title"
        const val COL_YEAR = "year"
        // Добавлены в версии 2
        const val COL_PUBLISHER = "publisher"   // char
        const val COL_PAGES = "pages"           // integer
        const val COL_PRICE = "price"           // integer

        private val INITIAL_DATA = listOf(
            Book(author = "Толстой Лев Николаевич", title = "Война и мир", year = 1869, publisher = "АСТ", pages = 1274, price = 850),
            Book(author = "Достоевский Фёдор Михайлович", title = "Преступление и наказание", year = 1866, publisher = "Эксмо", pages = 608, price = 450),
            Book(author = "Пушкин Александр Сергеевич", title = "Евгений Онегин", year = 1833, publisher = "Азбука", pages = 320, price = 350),
            Book(author = "Чехов Антон Павлович", title = "Вишнёвый сад", year = 1904, publisher = "Дрофа", pages = 128, price = 280),
            Book(author = "Булгаков Михаил Афанасьевич", title = "Мастер и Маргарита", year = 1967, publisher = "Эксмо", pages = 480, price = 520)
        )
    }

    // Создание таблицы версии 1 (базовая схема)
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_AUTHOR TEXT NOT NULL,
                $COL_TITLE TEXT NOT NULL,
                $COL_YEAR INTEGER NOT NULL
            )"""
        )
        // Сразу применяем upgrade до версии 2
        onUpgrade(db, 1, DATABASE_VERSION)
    }

    // Обновление схемы: добавляем новые столбцы без потери данных
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Добавляем поля char и integer через ALTER TABLE
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_PUBLISHER TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_PAGES INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_PRICE INTEGER NOT NULL DEFAULT 0")
        }
        // Заполняем тестовыми данными
        db.execSQL("DELETE FROM $TABLE_NAME")
        INITIAL_DATA.forEach { db.insert(TABLE_NAME, null, bookToValues(it)) }
    }

    fun initData() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        INITIAL_DATA.forEach { db.insert(TABLE_NAME, null, bookToValues(it)) }
    }

    private fun bookToValues(b: Book) = ContentValues().apply {
        put(COL_AUTHOR, b.author)
        put(COL_TITLE, b.title)
        put(COL_YEAR, b.year)
        put(COL_PUBLISHER, b.publisher)
        put(COL_PAGES, b.pages)
        put(COL_PRICE, b.price)
    }

    fun getAllBooks(): List<Book> {
        val list = mutableListOf<Book>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_ID", null).use { c ->
            while (c.moveToNext()) {
                list.add(Book(
                    id = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                    author = c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                    title = c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                    year = c.getInt(c.getColumnIndexOrThrow(COL_YEAR)),
                    publisher = c.getString(c.getColumnIndexOrThrow(COL_PUBLISHER)),
                    pages = c.getInt(c.getColumnIndexOrThrow(COL_PAGES)),
                    price = c.getInt(c.getColumnIndexOrThrow(COL_PRICE))
                ))
            }
        }
        return list
    }

    fun getFirstBook(): Book? = getAllBooks().firstOrNull()

    fun insertBook(b: Book): Long =
        writableDatabase.insert(TABLE_NAME, null, bookToValues(b))

    fun updateBook(id: Long, b: Book): Int =
        writableDatabase.update(TABLE_NAME, bookToValues(b), "$COL_ID = ?", arrayOf(id.toString()))
}
