package com.example.lab6_task72_shibitov

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

// Расширение для app_settings DataStore
val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

// Расширение для user_profile DataStore
val Context.userProfileDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")

object AppKeys {
    val APP_ID = stringPreferencesKey("app_id")
    val LAUNCH_COUNT = intPreferencesKey("launch_count")
    val BEST_SCORE = intPreferencesKey("best_score")
    val TOTAL_ATTEMPTS = intPreferencesKey("total_attempts")
}

object UserKeys {
    val EMAIL = stringPreferencesKey("email")
    val PASSWORD = stringPreferencesKey("password")
}

class AppSettingsRepository(private val context: Context) {

    val appIdFlow: Flow<String> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppKeys.APP_ID] ?: ""
    }

    val launchCountFlow: Flow<Int> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppKeys.LAUNCH_COUNT] ?: 0
    }

    val bestScoreFlow: Flow<Int> = context.appSettingsDataStore.data.map { prefs ->
        prefs[AppKeys.BEST_SCORE] ?: Int.MAX_VALUE
    }

    suspend fun initIfNeeded() {
        context.appSettingsDataStore.edit { prefs ->
            if (!prefs.contains(AppKeys.APP_ID)) {
                prefs[AppKeys.APP_ID] = UUID.randomUUID().toString()
                prefs[AppKeys.LAUNCH_COUNT] = 0
                prefs[AppKeys.BEST_SCORE] = Int.MAX_VALUE
                prefs[AppKeys.TOTAL_ATTEMPTS] = 0
            }
        }
    }

    suspend fun incrementLaunchCount() {
        context.appSettingsDataStore.edit { prefs ->
            prefs[AppKeys.LAUNCH_COUNT] = (prefs[AppKeys.LAUNCH_COUNT] ?: 0) + 1
        }
    }

    suspend fun updateBestScore(score: Int) {
        context.appSettingsDataStore.edit { prefs ->
            val current = prefs[AppKeys.BEST_SCORE] ?: Int.MAX_VALUE
            if (score < current) prefs[AppKeys.BEST_SCORE] = score
        }
    }

    suspend fun incrementTotalAttempts() {
        context.appSettingsDataStore.edit { prefs ->
            prefs[AppKeys.TOTAL_ATTEMPTS] = (prefs[AppKeys.TOTAL_ATTEMPTS] ?: 0) + 1
        }
    }
}

class UserProfileRepository(private val context: Context) {

    val emailFlow: Flow<String?> = context.userProfileDataStore.data.map { prefs ->
        prefs[UserKeys.EMAIL]
    }

    suspend fun saveCredentials(email: String, password: String) {
        context.userProfileDataStore.edit { prefs ->
            prefs[UserKeys.EMAIL] = email
            prefs[UserKeys.PASSWORD] = password
        }
    }

    suspend fun clearCredentials() {
        context.userProfileDataStore.edit { it.clear() }
    }
}
