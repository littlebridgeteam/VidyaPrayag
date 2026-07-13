package com.littlebridge.enrollplus.feature.tutor.rag

import com.littlebridge.enrollplus.db.DatabaseFactory
import com.littlebridge.enrollplus.db.TutorKnowledgeChunksTable
import com.littlebridge.enrollplus.feature.ai.AiProvider
import com.littlebridge.enrollplus.feature.ai.EmbeddingClient
import com.littlebridge.enrollplus.feature.ai.KeyVault
import com.littlebridge.enrollplus.feature.tutor.core.TutorConstants
import com.littlebridge.enrollplus.feature.tutor.core.TutorKillSwitch
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.slf4j.LoggerFactory
import java.util.UUID

class RagService {
    private val log = LoggerFactory.getLogger("RagService")

    data class KnowledgeChunk(
        val id: UUID,
        val source: String,
        val board: String,
        val classLabel: String,
        val subject: String,
        val topicId: UUID?,
        val chunkText: String,
    )

    data class RetrievalResult(
        val chunks: List<KnowledgeChunk>,
        val query: String,
        val providerUsed: String,
        val note: String? = null,
    )

    private var embeddingClientCache: EmbeddingClient? = null
    private var embeddingClientInit = false

    private suspend fun getEmbeddingClient(): EmbeddingClient? {
        if (embeddingClientInit) return embeddingClientCache
        embeddingClientInit = true
        embeddingClientCache = try {
            val provider = AiProvider.entries.firstOrNull {
                it.noTraining && KeyVault.isConfigured(it)
            } ?: return null
            val apiKey = KeyVault.keyFor(provider) ?: return null
            val baseUrl = KeyVault.baseUrlFor(provider)
            val model = System.getenv("AI_EMBEDDING_MODEL")
                ?: "text-embedding-3-small"
            EmbeddingClient(baseUrl = baseUrl, apiKey = apiKey, model = model)
        } catch (e: Exception) {
            log.warn("RagService: embedding client init failed: {}", e.message)
            null
        }
        return embeddingClientCache
    }

    suspend fun retrieve(
        schoolId: UUID?,
        query: String,
        topicId: UUID? = null,
        limit: Int = 5,
    ): RetrievalResult {
        TutorKillSwitch.require(TutorConstants.MODULE_RAG)

        val client = getEmbeddingClient()
        if (client != null && DatabaseFactory.isPostgres) {
            val embResult = client.embed(query)
            if (embResult.ok && embResult.embedding != null) {
                val vectorLiteral = embResult.embedding.joinToString(",", "[", "]")
                return try {
                    retrieveByVector(schoolId, vectorLiteral, topicId, limit.coerceIn(1, 50), query)
                } catch (e: Exception) {
                    log.warn("RagService: vector search failed ({}), falling back to text search", e.message)
                    retrieveByText(schoolId, query, topicId, limit.coerceIn(1, 50))
                }
            }
            log.warn("RagService: embedding failed ({}), falling back to text search", embResult.errorMessage)
        }

        return retrieveByText(schoolId, query, topicId, limit.coerceIn(1, 50))
    }

    private suspend fun retrieveByVector(
        schoolId: UUID?,
        vectorLiteral: String,
        topicId: UUID?,
        limit: Int,
        query: String,
    ): RetrievalResult {
        val safeLimit = limit.coerceIn(1, 50)
        val safeVector = vectorLiteral.filter { it.isDigit() || it in ".,-[]eE+" }
        val sql = StringBuilder(
            "SELECT id, source, board, class_label, subject, topic_id, chunk_text " +
                "FROM tutor_knowledge_chunks " +
                "WHERE embedding IS NOT NULL "
        )
        if (schoolId != null) {
            sql.append("AND (school_id = '$schoolId' OR school_id IS NULL) ")
        }
        if (topicId != null) {
            sql.append("AND topic_id = '$topicId' ")
        }
        sql.append("ORDER BY embedding <=> '$safeVector'::vector LIMIT $safeLimit")

        val chunks = DatabaseFactory.dbQuery {
            TransactionManager.current().exec(sql.toString()) { rs ->
                val results = mutableListOf<KnowledgeChunk>()
                while (rs.next()) {
                    results.add(
                        KnowledgeChunk(
                            id = rs.getObject("id") as UUID,
                            source = rs.getString("source"),
                            board = rs.getString("board"),
                            classLabel = rs.getString("class_label"),
                            subject = rs.getString("subject"),
                            topicId = rs.getObject("topic_id") as? UUID,
                            chunkText = rs.getString("chunk_text"),
                        )
                    )
                }
                results
            } ?: emptyList()
        }

        log.info("RagService: retrieved {} chunks via pgvector for query='{}'", chunks.size, query.take(50))

        return RetrievalResult(
            chunks = chunks,
            query = query,
            providerUsed = "pgvector_cosine",
            note = if (chunks.isEmpty()) "No knowledge chunks found via vector search." else null,
        )
    }

    private suspend fun retrieveByText(
        schoolId: UUID?,
        query: String,
        topicId: UUID?,
        limit: Int,
    ): RetrievalResult {
        val chunks = DatabaseFactory.dbQuery {
            val rows = TutorKnowledgeChunksTable.selectAll().where {
                val conditions = mutableListOf<org.jetbrains.exposed.sql.Op<Boolean>>()
                if (schoolId != null) {
                    conditions.add(
                        (TutorKnowledgeChunksTable.schoolId eq schoolId) or
                            (TutorKnowledgeChunksTable.schoolId.isNull())
                    )
                }
                if (topicId != null) {
                    conditions.add(TutorKnowledgeChunksTable.topicId eq topicId)
                }
                if (conditions.isEmpty()) {
                    org.jetbrains.exposed.sql.Op.TRUE
                } else {
                    conditions.reduce { acc, op -> acc and op }
                }
            }.toList()

            val lowerQuery = query.lowercase()
            rows.filter { it[TutorKnowledgeChunksTable.chunkText].lowercase().contains(lowerQuery) }
                .take(limit)
                .map { row ->
                    KnowledgeChunk(
                        id = row[TutorKnowledgeChunksTable.id].value,
                        source = row[TutorKnowledgeChunksTable.chunkSource],
                        board = row[TutorKnowledgeChunksTable.board],
                        classLabel = row[TutorKnowledgeChunksTable.classLabel],
                        subject = row[TutorKnowledgeChunksTable.subject],
                        topicId = row[TutorKnowledgeChunksTable.topicId],
                        chunkText = row[TutorKnowledgeChunksTable.chunkText],
                    )
                }
        }

        log.info("RagService: retrieved {} chunks for query='{}' (text search fallback)", chunks.size, query.take(50))

        return RetrievalResult(
            chunks = chunks,
            query = query,
            providerUsed = "text_search_fallback",
            note = if (chunks.isEmpty()) "No knowledge chunks found." else null,
        )
    }

    suspend fun isRagActive(schoolId: UUID?): Boolean = DatabaseFactory.dbQuery {
        val query = TutorKnowledgeChunksTable.selectAll()
        val filtered = if (schoolId != null) {
            query.where {
                (TutorKnowledgeChunksTable.schoolId eq schoolId) or
                    (TutorKnowledgeChunksTable.schoolId.isNull())
            }
        } else query

        filtered.any { it[TutorKnowledgeChunksTable.embedding] != null }
    }
}
