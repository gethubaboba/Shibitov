package com.example.lab6_task77_shibitov.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lab6_task77_shibitov.R
import com.example.lab6_task77_shibitov.db.Film
import com.example.lab6_task77_shibitov.db.FilmDatabase
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class FilmsFragment : Fragment() {

    private lateinit var tvFilms: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_films, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = FilmDatabase.getInstance(requireContext())
        val dao = db.filmDao()
        tvFilms = view.findViewById(R.id.tvFilms)

        val etTitle = view.findViewById<TextInputEditText>(R.id.etTitle)
        val etDirector = view.findViewById<TextInputEditText>(R.id.etDirector)
        val etYear = view.findViewById<TextInputEditText>(R.id.etYear)
        val etGenre = view.findViewById<TextInputEditText>(R.id.etGenre)
        val etFilterYear = view.findViewById<TextInputEditText>(R.id.etFilterYear)

        // Загрузка начального списка
        loadFilms { dao.getAllSortedByTitle() }

        view.findViewById<Button>(R.id.btnAddFilm).setOnClickListener {
            val title = etTitle.text.toString().trim()
            val director = etDirector.text.toString().trim()
            val year = etYear.text.toString().toIntOrNull()
            val genre = etGenre.text.toString().trim()
            if (title.isEmpty() || director.isEmpty() || year == null || genre.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.msg_fill_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                dao.insert(Film(title = title, director = director, year = year, genre = genre))
                etTitle.text?.clear(); etDirector.text?.clear()
                etYear.text?.clear(); etGenre.text?.clear()
                loadFilms { dao.getAllSortedByTitle() }
                Toast.makeText(requireContext(), getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnSortTitle).setOnClickListener {
            loadFilms { dao.getAllSortedByTitle() }
        }

        view.findViewById<Button>(R.id.btnSortYear).setOnClickListener {
            loadFilms { dao.getAllSortedByYear() }
        }

        view.findViewById<Button>(R.id.btnFilter).setOnClickListener {
            val fromYear = etFilterYear.text.toString().toIntOrNull() ?: 0
            loadFilms { dao.getFilmsFromYear(fromYear) }
        }

        view.findViewById<Button>(R.id.btnGroupGenre).setOnClickListener {
            lifecycleScope.launch {
                val groups = dao.getGroupedByGenre()
                val sb = StringBuilder("=== Группировка по жанру ===\n")
                groups.forEach { sb.appendLine("${it.genre}: ${it.cnt} фильм(ов)") }
                tvFilms.text = sb.toString()
            }
        }
    }

    private fun loadFilms(query: suspend () -> List<Film>) {
        lifecycleScope.launch {
            val films = query()
            val sb = StringBuilder()
            films.forEach { f ->
                sb.appendLine("─────────────────────")
                sb.appendLine("ID: ${f.id}  Год: ${f.year}")
                sb.appendLine("${f.title} [${f.genre}]")
                sb.appendLine("Реж.: ${f.director}")
            }
            if (films.isEmpty()) sb.appendLine("Список пуст")
            tvFilms.text = sb.toString()
        }
    }
}
