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
import com.example.lab6_task77_shibitov.db.FilmDatabase
import com.example.lab6_task77_shibitov.db.Session
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SessionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_sessions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = FilmDatabase.getInstance(requireContext())
        val dao = db.sessionDao()
        val tvSessions = view.findViewById<TextView>(R.id.tvSessions)

        val etFilmId = view.findViewById<TextInputEditText>(R.id.etFilmId)
        val etDate = view.findViewById<TextInputEditText>(R.id.etDate)
        val etViewers = view.findViewById<TextInputEditText>(R.id.etViewers)
        val etPrice = view.findViewById<TextInputEditText>(R.id.etPrice)

        loadSessions(dao, tvSessions)

        view.findViewById<Button>(R.id.btnAddSession).setOnClickListener {
            val filmId = etFilmId.text.toString().toLongOrNull()
            val date = etDate.text.toString().trim()
            val viewers = etViewers.text.toString().toIntOrNull()
            val price = etPrice.text.toString().toDoubleOrNull()

            if (filmId == null || date.isEmpty() || viewers == null || price == null) {
                Toast.makeText(requireContext(), getString(R.string.msg_fill_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                dao.insert(Session(filmId = filmId, date = date, viewers = viewers, ticketPrice = price))
                etFilmId.text?.clear(); etDate.text?.clear()
                etViewers.text?.clear(); etPrice.text?.clear()
                loadSessions(dao, tvSessions)
                Toast.makeText(requireContext(), getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSessions(dao: com.example.lab6_task77_shibitov.db.SessionDao, tv: TextView) {
        lifecycleScope.launch {
            val sessions = dao.getAll()
            val sb = StringBuilder()
            sessions.forEach { s ->
                sb.appendLine("─────────────────────")
                sb.appendLine("ID: ${s.id}  Фильм ID: ${s.filmId}")
                sb.appendLine("Дата: ${s.date}")
                sb.appendLine("Зрители: ${s.viewers}  Цена: ${s.ticketPrice} руб.")
            }
            if (sessions.isEmpty()) sb.appendLine("Список пуст")
            tv.text = sb.toString()
        }
    }
}
