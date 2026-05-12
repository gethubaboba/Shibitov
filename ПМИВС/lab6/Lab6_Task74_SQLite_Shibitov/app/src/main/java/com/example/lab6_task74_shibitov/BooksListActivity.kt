package com.example.lab6_task74_shibitov

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BooksListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_list)

        val dbHelper = BookDbHelper(this)
        val books = dbHelper.getAllBooks()

        val sb = StringBuilder()
        books.forEach { b ->
            sb.appendLine("─────────────────────────")
            sb.appendLine("ID: ${b.id}")
            sb.appendLine("Автор: ${b.author}")
            sb.appendLine("Название: ${b.title}")
            sb.appendLine("Год издания: ${b.year}")
        }
        if (books.isEmpty()) sb.appendLine("Список пуст")

        findViewById<TextView>(R.id.tvBooks).text = sb.toString()
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}
