package com.example.lab6_task77_shibitov.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lab6_task77_shibitov.R
import com.example.lab6_task77_shibitov.db.FilmDatabase
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_stats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dao = FilmDatabase.getInstance(requireContext()).filmDao()
        val tvStats = view.findViewById<TextView>(R.id.tvStats)

        loadStats(dao, tvStats)

        view.findViewById<Button>(R.id.btnRefreshStats).setOnClickListener {
            loadStats(dao, tvStats)
        }
    }

    private fun loadStats(dao: com.example.lab6_task77_shibitov.db.FilmDao, tv: TextView) {
        lifecycleScope.launch {
            val stats = dao.getFilmStats()
            val sb = StringBuilder()
            stats.forEach { s ->
                sb.appendLine("═════════════════════════")
                sb.appendLine("Фильм: ${s.title}")
                sb.appendLine("  Сеансов:       ${s.sessionCount}")
                sb.appendLine("  Зрителей всего: ${s.totalViewers}")
                sb.appendLine("  Среднее:        ${"%.1f".format(s.avgViewers)}")
                sb.appendLine("  Максимум:       ${s.maxViewers}")
                sb.appendLine("  Минимум:        ${s.minViewers}")
            }
            if (stats.isEmpty()) sb.appendLine("Нет данных")
            tv.text = sb.toString()
        }
    }
}
