package com.example.lab6_task71_shibitov

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID

class LoginActivity : AppCompatActivity() {

    private lateinit var appSettings: SharedPreferences
    private lateinit var userProfile: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Два файла настроек
        appSettings = getSharedPreferences("app_settings", MODE_PRIVATE)
        userProfile = getSharedPreferences("user_profile", MODE_PRIVATE)

        // Инициализация app_settings при первом запуске
        if (!appSettings.contains("app_id")) {
            appSettings.edit()
                .putString("app_id", UUID.randomUUID().toString())
                .putInt("launch_count", 0)
                .putInt("best_score", Int.MAX_VALUE)
                .putInt("total_attempts", 0)
                .apply()
        }

        // Увеличиваем счётчик запусков
        val launches = appSettings.getInt("launch_count", 0) + 1
        appSettings.edit().putInt("launch_count", launches).apply()

        val tvLaunches = findViewById<TextView>(R.id.tvLaunches)
        tvLaunches.text = getString(R.string.label_launches) + launches

        // Если пользователь уже авторизован — переходим в игру
        if (userProfile.contains("email")) {
            startGame()
            return
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

            // Сохраняем данные авторизации в user_profile
            userProfile.edit()
                .putString("email", email)
                .putString("password", password)
                .apply()

            startGame()
        }
    }

    private fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }
}
