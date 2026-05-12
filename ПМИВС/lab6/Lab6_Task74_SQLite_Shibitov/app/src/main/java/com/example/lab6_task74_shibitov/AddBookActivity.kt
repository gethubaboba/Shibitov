package com.example.lab6_task74_shibitov

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

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val author = etAuthor.text.toString().trim()
            val title = etTitle.text.toString().trim()
            val yearStr = etYear.text.toString().trim()

            if (author.isEmpty() || title.isEmpty() || yearStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_fill_all), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val year = yearStr.toIntOrNull() ?: 2020
            val id = dbHelper.insertBook(Book(author = author, title = title, year = year))
            if (id > 0) {
                Toast.makeText(this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
