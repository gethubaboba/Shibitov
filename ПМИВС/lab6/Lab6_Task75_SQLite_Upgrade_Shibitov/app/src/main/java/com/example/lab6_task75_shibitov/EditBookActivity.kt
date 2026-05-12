package com.example.lab6_task75_shibitov

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class EditBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        val bookId = intent.getLongExtra("book_id", -1L)
        val dbHelper = BookDbHelper(this)

        val etAuthor = findViewById<TextInputEditText>(R.id.etAuthor)
        val etTitle = findViewById<TextInputEditText>(R.id.etTitle)
        val etYear = findViewById<TextInputEditText>(R.id.etYear)
        val etPublisher = findViewById<TextInputEditText>(R.id.etPublisher)
        val etPages = findViewById<TextInputEditText>(R.id.etPages)
        val etPrice = findViewById<TextInputEditText>(R.id.etPrice)

        findViewById<TextView>(R.id.tvEditInfo).text = "Редактирование записи ID=$bookId"

        etAuthor.setText(intent.getStringExtra("book_author") ?: "")
        etTitle.setText(intent.getStringExtra("book_title") ?: "")
        etYear.setText(intent.getIntExtra("book_year", 2020).toString())
        etPublisher.setText(intent.getStringExtra("book_publisher") ?: "")
        etPages.setText(intent.getIntExtra("book_pages", 0).toString())
        etPrice.setText(intent.getIntExtra("book_price", 0).toString())

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val author = etAuthor.text.toString().trim()
            val title = etTitle.text.toString().trim()
            val yearStr = etYear.text.toString().trim()
            if (author.isEmpty() || title.isEmpty() || yearStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_fill_all), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rows = dbHelper.updateBook(bookId, Book(
                id = bookId,
                author = author,
                title = title,
                year = yearStr.toIntOrNull() ?: 2020,
                publisher = etPublisher.text.toString().trim(),
                pages = etPages.text.toString().toIntOrNull() ?: 0,
                price = etPrice.text.toString().toIntOrNull() ?: 0
            ))
            if (rows > 0) {
                Toast.makeText(this, getString(R.string.msg_updated), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
