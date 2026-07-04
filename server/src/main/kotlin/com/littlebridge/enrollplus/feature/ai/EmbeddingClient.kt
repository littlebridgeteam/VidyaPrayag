package com.littlebridge.enrollplus.feature.ai

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

data class EmbeddingResult(
    val ok: Boolean,
    val embedding: List<Double>? = null,
    val modelUsed: String? = null,
    val errorMessage: String? = null,
)

class EmbeddingClient(
    private val baseUrl: String,
    private val apiKey: String,
    private val model: String,
) {
    private val log = LoggerFactory.getLogger("EmbeddingClient")

    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(HttpTimeout) { requestTimeoutMillis = 15_000 }
        }.also { com.littlebridge.enrollplus.core.HttpClientRegistry.register(it) }
    }

    @Serializable
    private data class EmbeddingRequest(
        val model: String,
        val input: String,
        val dimensions: Int = 768,
    )

    @Serializable
    private data class EmbeddingResponse(
        val data: List<EmbeddingData> = emptyList(),
        val model: String? = null,
    ) {
        @Serializable
        data class EmbeddingData(
            val embedding: List<Double> = emptyList(),
        )
    }

    suspend fun embed(text: String): EmbeddingResult {
        return try {
            val resp = client.post("$baseUrl/embeddings") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(EmbeddingRequest(model = model, input = text))
            }
            if (!resp.status.isSuccess()) {
                val body = resp.bodyAsText()
                log.warn("Embedding API returned {}: {}", resp.status, body.take(200))
                return EmbeddingResult(ok = false, errorMessage = "HTTP ${resp.status}")
            }
            val parsed: EmbeddingResponse = resp.body()
            if (parsed.data.isEmpty()) {
                EmbeddingResult(ok = false, errorMessage = "Empty embedding response")
            } else {
                EmbeddingResult(
                    ok = true,
                    embedding = parsed.data[0].embedding,
                    modelUsed = parsed.model ?: model,
                )
            }
        } catch (e: Exception) {
            log.warn("Embedding request failed: {}", e.message)
            EmbeddingResult(ok = false, errorMessage = e.message)
        }
    }

    fun close() = runCatching { client.close() }
}
