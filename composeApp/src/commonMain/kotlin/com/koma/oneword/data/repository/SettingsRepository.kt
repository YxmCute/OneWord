package com.koma.oneword.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.koma.oneword.database.OneWordDatabase
import com.koma.oneword.model.AppThemeSettings
import com.koma.oneword.model.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsRepository(
    private val database: OneWordDatabase,
) {
    fun observeTheme(): Flow<AppThemeSettings> = database.appSettingsQueries
        .selectSettings()
        .asFlow()
        .mapToOne(Dispatchers.Default)
        .map { row ->
            AppThemeSettings(
                mode = ThemeMode.fromStorage(row.theme_mode),
                seedHex = row.theme_seed_hex,
            )
        }

    fun currentTheme(): AppThemeSettings {
        val row = database.appSettingsQueries.selectSettings().executeAsOne()
        return AppThemeSettings(
            mode = ThemeMode.fromStorage(row.theme_mode),
            seedHex = row.theme_seed_hex,
        )
    }

    fun currentToken(): String? = database.appSettingsQueries.selectSettings().executeAsOne().user_token

    suspend fun saveToken(token: String) {
        withContext(Dispatchers.Default) {
            database.appSettingsQueries.updateToken(token)
        }
    }

    suspend fun saveThemeMode(mode: ThemeMode) {
        withContext(Dispatchers.Default) {
            database.appSettingsQueries.updateThemeMode(mode.name)
        }
    }

    suspend fun saveThemeSeed(hex: String) {
        withContext(Dispatchers.Default) {
            database.appSettingsQueries.updateThemeSeed(hex)
        }
    }

    suspend fun updateLastSync(epochMillis: Long) {
        withContext(Dispatchers.Default) {
            database.appSettingsQueries.updateLastSync(epochMillis)
        }
    }
}
