package com.koma.oneword.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class PoetryApi(
    private val client: HttpClient,
) {
    suspend fun fetchToken(): String {
        val response = client.get("https://v2.jinrishici.com/token").body<TokenEnvelope>()
        return response.data ?: throw ApiException(response.message ?: "今日诗词 token 获取失败")
    }

    suspend fun fetchSentence(token: String): SentenceDto {
        val response = client.get("https://v2.jinrishici.com/sentence") {
            header("X-User-Token", token)
        }.body<SentenceEnvelope>()
        return response.data ?: throw ApiException(response.message ?: "今日诗词内容获取失败")
    }
}

class ApiException(message: String) : IllegalStateException(message)

@Serializable
private data class TokenEnvelope(
    val status: String,
    val data: String? = null,
    val message: String? = null,
)

@Serializable
private data class SentenceEnvelope(
    val status: String,
    val data: SentenceDto? = null,
    val message: String? = null,
)

@Serializable
data class SentenceDto(
    val id: String,
    val content: String,
    val popularity: Long? = null,
    val origin: OriginDto,
)

@Serializable
data class OriginDto(
    val title: String,
    val dynasty: String,
    val author: String,
    val content: List<String> = emptyList(),
    @SerialName("translate")
    val translation: List<String>? = null,
)
