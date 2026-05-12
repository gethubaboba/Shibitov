package com.example.lab6_task75_shibitov

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class AddBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        val dbHelper = BookDbHelper(this)
        val etAuthor = findViewById<TextInputEditText>(R.id.etAuthor)
        val etTitle = findViewById<TextInputEditText>(R.id.etTitle)
        val etYear = findViewById<TextInputEditText>(R.id.etYear)
        val etPublisher = findViewById<TextInputEditText>(R.id.etPublisher)
        val etPages = findViewById<TextInputEditText>(R.id.etPages)
        val etPrice = findViewById<TextInputEditText>(R.id.etPrice)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val author = etAuthor.text.toString().trim()
            val title = etTitle.text.toString().trim()
            val yearStr = etYear.text.toString().trim()
            val publisher = etPublisher.text.toString().trim()
            val pagesStr = etPages.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()

            if (author.isEmpty() || title.isEmpty() || yearStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_fill_all), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = dbHelper.insertBook(Book(
                author = author,
                title = title,
                year = yearStr.toIntOrNull() ?: 2020,
                publisher = publisher,
                pages = pagesStr.toIntOrNull() ?: 0,
                price = priceStr.toIntOrNull() ?: 0
            ))
            if (id > 0) {
                Toast.makeText(this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
