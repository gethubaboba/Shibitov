package com.example.lab6_task74_shibitov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: BookDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = BookDbHelper(this)

        // При запуске: создать БД, удалить все записи, добавить 5
        dbHelper.initData()
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = getString(R.string.db_init_done)

        // Кнопка 1: просмотр всех записей
        findViewById<Button>(R.id.btnView).setOnClickListener {
            startActivity(Intent(this, BooksListActivity::class.java))
        }

        // Кнопка 2: добавить новую запись
        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }

        // Кнопка 3: изменить первую запись
        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            val first = dbHelper.getFirstBook()
            if (first != null) {
                startActivity(
                    Intent(this, EditBookActivity::class.java)
                        .putExtra("book_id", first.id)
                        .putExtra("book_author", first.author)
                        .putExtra("book_title", first.title)
                        .putExtra("book_year", first.year)
                )
            } else {
                Toast.makeText(this, "Нет записей для редактирования", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
