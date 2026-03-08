package com.koma.oneword.app

import app.cash.sqldelight.db.SqlDriver
import com.koma.oneword.data.api.PoetryApi
import com.koma.oneword.data.repository.PoetryRepository
import com.koma.oneword.data.repository.SettingsRepository
import com.koma.oneword.database.OneWordDatabase
import com.koma.oneword.util.PlatformLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.serialization.json.Json

class AppContainer private constructor(
    private val driver: SqlDriver,
    private val client: HttpClient,
    val database: OneWordDatabase,
    val settingsRepository: SettingsRepository,
    val poetryRepository: PoetryRepository,
    val scope: CoroutineScope,
) {
    fun close() {
        scope.cancel()
        client.close()
        driver.close()
    }

    companion object {
        fun create(driver: SqlDriver): AppContainer {
            val database = OneWordDatabase(driver)
            database.appSettingsQueries.ensureDefaultSettings()
            val client = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            PlatformLogger.info(tag = "OneWordHttp", message = message)
                        }
                    }
                    level = LogLevel.ALL
                    sanitizeHeader { header ->
                        header == "X-User-Token" || header == HttpHeaders.Authorization
                    }
                }
            }
            val settingsRepository = SettingsRepository(database)
            val poetryRepository = PoetryRepository(
                api = PoetryApi(client),
                settingsRepository = settingsRepository,
                database = database,
                json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
            )
            return AppContainer(
                driver = driver,
                client = client,
                database = database,
                settingsRepository = settingsRepository,
                poetryRepository = poetryRepository,
                scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
            )
        }
    }
}
