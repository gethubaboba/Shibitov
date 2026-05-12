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

class LoginActivity : AppCompatActivity() {

    private lateinit var appRepo: AppSettingsRepository
    private lateinit var userRepo: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        appRepo = AppSettingsRepository(this)
        userRepo = UserProfileRepository(this)

        lifecycleScope.launch {
            appRepo.initIfNeeded()
            appRepo.incrementLaunchCount()

            val launches = appRepo.launchCountFlow.first()
            findViewById<TextView>(R.id.tvLaunches).text =
                getString(R.string.label_launches) + launches

            // Если уже авторизован — сразу в игру
            val email = userRepo.emailFlow.first()
            if (!email.isNullOrEmpty()) {
                startGame()
            }
        }

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_email_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_pass_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                userRepo.saveCredentials(email, password)
                startGame()
            }
        }
    }

    private fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }
}
