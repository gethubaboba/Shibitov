package com.example.lab6_task72_shibitov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var appRepo: AppSettingsRepository
    private lateinit var userRepo: UserProfileRepository

    private var secretNumber = 0
    private var attemptsLeft = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        appRepo = AppSettingsRepository(this)
        userRepo = UserProfileRepository(this)

        lifecycleScope.launch {
            val email = userRepo.emailFlow.first() ?: "Гость"
            val appId = appRepo.appIdFlow.first()
            val best = appRepo.bestScoreFlow.first()

            findViewById<TextView>(R.id.tvWelcome).text =
                String.format(getString(R.string.welcome), email)
            findViewById<TextView>(R.id.tvAppId).text = "ID приложения: $appId"
            updateStats(best)
        }

        startNewGame()

        findViewById<Button>(R.id.btnCheck).setOnClickListener { checkGuess() }
        findViewById<Button>(R.id.btnNewGame).setOnClickListener { startNewGame() }
        findViewById<Button>(R.id.btnLogout).setOnClickListener { logout() }
    }

    private fun startNewGame() {
        secretNumber = Random.nextInt(1, 101)
        attemptsLeft = 10
        findViewById<TextView>(R.id.tvResult).text = "Загадал число от 1 до 100"
        refreshStats()
    }

    private fun checkGuess() {
        val etGuess = findViewById<TextInputEditText>(R.id.etGuess)
        val input = etGuess.text.toString().toIntOrNull()
        if (input == null) {
            Toast.makeText(this, getString(R.string.msg_invalid), Toast.LENGTH_SHORT).show()
            return
        }

        attemptsLeft--
        lifecycleScope.launch { appRepo.incrementTotalAttempts() }

        val tvResult = findViewById<TextView>(R.id.tvResult)
        when {
            input == secretNumber -> {
                val usedAttempts = 10 - attemptsLeft
                lifecycleScope.launch {
                    appRepo.updateBestScore(usedAttempts)
                    val best = appRepo.bestScoreFlow.first()
                    updateStats(best)
                }
                tvResult.text = getString(R.string.msg_win) + " за $usedAttempts попыток!"
            }
            attemptsLeft <= 0 -> tvResult.text = "Проигрыш! Число: $secretNumber"
            input > secretNumber -> tvResult.text = getString(R.string.msg_too_high)
            else -> tvResult.text = getString(R.string.msg_too_low)
        }
        refreshStats()
        etGuess.text?.clear()
    }

    private fun refreshStats() {
        lifecycleScope.launch {
            val best = appRepo.bestScoreFlow.first()
            updateStats(best)
        }
    }

    private fun updateStats(best: Int) {
        val bestStr = if (best == Int.MAX_VALUE) "—" else "$best"
        findViewById<TextView>(R.id.tvStats).text =
            "${getString(R.string.label_attempts)}$attemptsLeft  |  " +
                    "${getString(R.string.label_best)}$bestStr"
    }

    private fun logout() {
        lifecycleScope.launch {
            userRepo.clearCredentials()
            startActivity(Intent(this@GameActivity, LoginActivity::class.java))
            finish()
        }
    }
}
