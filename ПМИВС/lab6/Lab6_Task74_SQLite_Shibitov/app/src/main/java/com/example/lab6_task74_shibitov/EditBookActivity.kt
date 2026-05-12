package com.example.lab6_task74_shibitov

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
        val author = intent.getStringExtra("book_author") ?: ""
        val title = intent.getStringExtra("book_title") ?: ""
        val year = intent.getIntExtra("book_year", 2020)

        val dbHelper = BookDbHelper(this)

        val tvInfo = findViewById<TextView>(R.id.tvEditInfo)
        tvInfo.text = "Редактирование записи ID=$bookId"

        val etAuthor = findViewById<TextInputEditText>(R.id.etAuthor)
        val etTitle = findViewById<TextInputEditText>(R.id.etTitle)
        val etYear = findViewById<TextInputEditText>(R.id.etYear)

        etAuthor.setText(author)
        etTitle.setText(title)
        etYear.setText(year.toString())

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val newAuthor = etAuthor.text.toString().trim()
            val newTitle = etTitle.text.toString().trim()
            val newYearStr = etYear.text.toString().trim()

            if (newAuthor.isEmpty() || newTitle.isEmpty() || newYearStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_fill_all), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newYear = newYearStr.toIntOrNull() ?: 2020
            val rows = dbHelper.updateBook(
                bookId,
                Book(id = bookId, author = newAuthor, title = newTitle, year = newYear)
            )
            if (rows > 0) {
                Toast.makeText(this, getString(R.string.msg_updated), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
