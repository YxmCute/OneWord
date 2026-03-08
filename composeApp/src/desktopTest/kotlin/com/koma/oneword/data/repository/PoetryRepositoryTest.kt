package com.koma.oneword.data.repository

/**
 * Repository tests covering token acquisition, caching, and offline fallback behavior.
 */

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.koma.oneword.data.api.PoetryApi
import com.koma.oneword.database.OneWordDatabase
import com.koma.oneword.model.RefreshResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PoetryRepositoryTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `refresh stores token and poem on first launch`() = runTest {
        val engine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/token" -> respond(
                    content = """{"status":"success","data":"abc-token"}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
                "/sentence" -> respond(
                    content = """
                        {"status":"success","data":{"id":"poem-1","content":"海上生明月","popularity":100,
                        "origin":{"title":"望月怀远","dynasty":"唐代","author":"张九龄",
                        "content":["海上生明月","天涯共此时"],"translate":["大海上升起一轮明月"]}}}
                    """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }
        val database = inMemoryDatabase()
        database.appSettingsQueries.ensureDefaultSettings()
        val settingsRepository = SettingsRepository(database)
        val repository = PoetryRepository(
            api = PoetryApi(mockClient(engine)),
            settingsRepository = settingsRepository,
            database = database,
            json = json,
        )

        val result = repository.refresh(force = false)

        assertIs<RefreshResult.Success>(result)
        assertEquals("abc-token", settingsRepository.currentToken())
        assertEquals("海上生明月", repository.observeHome().first()?.poem?.content)
    }

    @Test
    fun `refresh falls back to cache when network fails`() = runTest {
        val successEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/token" -> respond(
                    content = """{"status":"success","data":"abc-token"}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
                "/sentence" -> respond(
                    content = """
                        {"status":"success","data":{"id":"poem-1","content":"海上生明月","popularity":100,
                        "origin":{"title":"望月怀远","dynasty":"唐代","author":"张九龄",
                        "content":["海上生明月","天涯共此时"],"translate":["大海上升起一轮明月"]}}}
                    """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }
        val database = inMemoryDatabase()
        database.appSettingsQueries.ensureDefaultSettings()
        val settingsRepository = SettingsRepository(database)
        val repository = PoetryRepository(
            api = PoetryApi(mockClient(successEngine)),
            settingsRepository = settingsRepository,
            database = database,
            json = json,
        )
        repository.refresh(force = false)

        val failingRepository = PoetryRepository(
            api = PoetryApi(
                mockClient(
                    MockEngine {
                        error("network down")
                    },
                ),
            ),
            settingsRepository = settingsRepository,
            database = database,
            json = json,
        )

        val result = failingRepository.refresh(force = true)

        assertIs<RefreshResult.Offline>(result)
        assertEquals("海上生明月", failingRepository.observeHome().first()?.poem?.content)
    }

    private fun mockClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    private fun inMemoryDatabase(): OneWordDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        OneWordDatabase.Schema.create(driver)
        return OneWordDatabase(driver)
    }
}
