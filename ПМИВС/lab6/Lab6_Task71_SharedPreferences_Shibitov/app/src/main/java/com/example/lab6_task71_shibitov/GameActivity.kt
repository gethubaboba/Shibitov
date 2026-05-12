package com.example.lab6_task71_shibitov

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var appSettings: SharedPreferences
    private lateinit var userProfile: SharedPreferences

    private var secretNumber = 0
    private var attemptsLeft = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        appSettings = getSharedPreferences("app_settings", MODE_PRIVATE)
        userProfile = getSharedPreferences("user_profile", MODE_PRIVATE)

        val email = userProfile.getString("email", "Гость") ?: "Гость"
        val appId = appSettings.getString("app_id", "—") ?: "—"
        val bestScore = appSettings.getInt("best_score", Int.MAX_VALUE)

        findViewById<TextView>(R.id.tvWelcome).text =
            String.format(getString(R.string.welcome), email)
        findViewById<TextView>(R.id.tvAppId).text = "ID приложения: $appId"

        updateStats(bestScore)
        startNewGame()

        findViewById<Button>(R.id.btnCheck).setOnClickListener { checkGuess() }
        findViewById<Button>(R.id.btnNewGame).setOnClickListener { startNewGame() }
        findViewById<Button>(R.id.btnLogout).setOnClickListener { logout() }
    }

    private fun startNewGame() {
        secretNumber = Random.nextInt(1, 101)
        attemptsLeft = 10
        findViewById<TextView>(R.id.tvResult).text = "Загадал число от 1 до 100"
        updateAttemptsView()
    }

    private fun checkGuess() {
        val etGuess = findViewById<TextInputEditText>(R.id.etGuess)
        val input = etGuess.text.toString().toIntOrNull()
        if (input == null) {
            Toast.makeText(this, getString(R.string.msg_invalid), Toast.LENGTH_SHORT).show()
            return
        }

        attemptsLeft--
        val totalAttempts = appSettings.getInt("total_attempts", 0) + 1
        appSettings.edit().putInt("total_attempts", totalAttempts).apply()

        val tvResult = findViewById<TextView>(R.id.tvResult)
        when {
            input == secretNumber -> {
                val usedAttempts = 10 - attemptsLeft
                val best = appSettings.getInt("best_score", Int.MAX_VALUE)
                if (usedAttempts < best) {
                    appSettings.edit().putInt("best_score", usedAttempts).apply()
                    updateStats(usedAttempts)
                }
                tvResult.text = getString(R.string.msg_win) + " за $usedAttempts попыток!"
                etGuess.text?.clear()
            }
            attemptsLeft <= 0 -> {
                tvResult.text = "Проигрыш! Загаданное число: $secretNumber"
            }
            input > secretNumber -> tvResult.text = getString(R.string.msg_too_high)
            else -> tvResult.text = getString(R.string.msg_too_low)
        }
        updateAttemptsView()
        etGuess.text?.clear()
    }

    private fun updateAttemptsView() {
        val tvStats = findViewById<TextView>(R.id.tvStats)
        val best = appSettings.getInt("best_score", Int.MAX_VALUE)
        val bestStr = if (best == Int.MAX_VALUE) "—" else "$best"
        tvStats.text = "${getString(R.string.label_attempts)}$attemptsLeft  |  " +
                "${getString(R.string.label_best)}$bestStr"
    }

    private fun updateStats(bestScore: Int) {
        val bestStr = if (bestScore == Int.MAX_VALUE) "—" else "$bestScore"
        findViewById<TextView>(R.id.tvStats).text =
            "${getString(R.string.label_attempts)}$attemptsLeft  |  " +
                    "${getString(R.string.label_best)}$bestStr"
    }

    private fun logout() {
        userProfile.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
