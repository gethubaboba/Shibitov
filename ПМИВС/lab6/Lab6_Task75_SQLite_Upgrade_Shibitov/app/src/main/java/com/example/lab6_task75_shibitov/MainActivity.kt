package com.example.lab6_task75_shibitov

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
        dbHelper.initData()

        findViewById<TextView>(R.id.tvStatus).text = getString(R.string.db_init_done)

        findViewById<Button>(R.id.btnView).setOnClickListener {
            startActivity(Intent(this, BooksListActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            val first = dbHelper.getFirstBook()
            if (first != null) {
                startActivity(Intent(this, EditBookActivity::class.java).apply {
                    putExtra("book_id", first.id)
                    putExtra("book_author", first.author)
                    putExtra("book_title", first.title)
                    putExtra("book_year", first.year)
                    putExtra("book_publisher", first.publisher)
                    putExtra("book_pages", first.pages)
                    putExtra("book_price", first.price)
                })
            } else {
                Toast.makeText(this, "Нет записей для редактирования", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
