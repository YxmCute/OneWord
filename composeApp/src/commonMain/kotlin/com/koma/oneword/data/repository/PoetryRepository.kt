package com.koma.oneword.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.koma.oneword.data.api.ApiException
import com.koma.oneword.data.api.PoetryApi
import com.koma.oneword.data.api.SentenceDto
import com.koma.oneword.database.OneWordDatabase
import com.koma.oneword.model.HomeData
import com.koma.oneword.model.Poem
import com.koma.oneword.model.RefreshResult
import com.koma.oneword.util.PlatformTime
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PoetryRepository(
    private val api: PoetryApi,
    private val settingsRepository: SettingsRepository,
    private val database: OneWordDatabase,
    private val json: Json,
) {
    fun observeHome(): Flow<HomeData?> = database.currentPoemQueries
        .selectHomePoem()
        .asFlow()
        .mapToOneOrNull(Dispatchers.Default)
        .map { row ->
            row?.let {
                HomeData(
                    poem = Poem(
                        id = it.poem_id,
                        content = it.content,
                        title = it.title,
                        author = it.author,
                        dynasty = it.dynasty,
                        popularity = it.popularity,
                        fullText = json.decodeFromString(it.full_text_json),
                        translation = it.translation_json?.let { value -> json.decodeFromString(value) } ?: emptyList(),
                    ),
                    fetchedAtEpochMs = it.fetched_at_epoch_ms,
                )
            }
        }

    suspend fun refresh(force: Boolean): RefreshResult = withContext(Dispatchers.Default) {
        val cached = database.currentPoemQueries.selectHomePoem().executeAsOneOrNull()
        return@withContext try {
            var token = settingsRepository.currentToken()
            if (token.isNullOrBlank()) {
                token = api.fetchToken()
                settingsRepository.saveToken(token)
            }

            val sentence = try {
                api.fetchSentence(token)
            } catch (error: Throwable) {
                if (shouldRefreshToken(error)) {
                    val refreshedToken = api.fetchToken()
                    settingsRepository.saveToken(refreshedToken)
                    api.fetchSentence(refreshedToken)
                } else {
                    throw error
                }
            }

            persistSentence(sentence)
            RefreshResult.Success
        } catch (error: Throwable) {
            if (cached != null && !force) {
                RefreshResult.Offline("已展示本地缓存")
            } else if (cached != null) {
                RefreshResult.Offline("网络不可用，已回退到本地缓存")
            } else {
                RefreshResult.Failure(error.message ?: "加载失败")
            }
        }
    }

    private suspend fun persistSentence(sentence: SentenceDto) {
        val timestamp = PlatformTime.currentTimeMillis()
        withContext(Dispatchers.Default) {
            database.transaction {
                database.currentPoemQueries.replaceHomePoem(
                    poem_id = sentence.id,
                    content = sentence.content,
                    title = sentence.origin.title,
                    author = sentence.origin.author,
                    dynasty = sentence.origin.dynasty,
                    popularity = sentence.popularity,
                    full_text_json = json.encodeToString(sentence.origin.content),
                    translation_json = sentence.origin.translation?.let { json.encodeToString(it) },
                    fetched_at_epoch_ms = timestamp,
                )
                database.appSettingsQueries.updateLastSync(timestamp)
            }
        }
    }

    private fun shouldRefreshToken(error: Throwable): Boolean = when (error) {
        is ClientRequestException -> error.response.status.value == 401 || error.response.status.value == 403
        is ApiException -> error.message?.contains("token", ignoreCase = true) == true
        else -> false
    }
}
