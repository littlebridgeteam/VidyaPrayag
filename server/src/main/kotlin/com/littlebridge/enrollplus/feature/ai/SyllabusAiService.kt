/*
 * File: SyllabusAiService.kt
 * Module: feature.ai
 *
 * The AI service layer for the Agentic Syllabus Management & AI Assignment
 * System. All LLM calls go through AiService.complete() / completeWithVision()
 * — this service NEVER talks to LlmClient or a provider directly.
 *
 * Six AI functions:
 *   1. parseSyllabusImage()  — vision: image → structured hierarchy
 *   2. parseSyllabusText()   — text: raw text → structured hierarchy
 *   3. estimatePacePlan()    — batch: topics + classes → pace estimate
 *   4. generateDailySummary()— fast:  topic titles → parent-friendly summary
 *   5. reconfirmAlert()      — reason: alert data → confirmed/rejected + reasoning
 *   6. generateQuiz()        — reason: topics + config → quiz questions JSON
 *
 * Every function degrades gracefully: AI unavailable → returns a sentinel
 * result that the caller can handle (manual fallback / skip alert / error msg).
 */
package com.littlebridge.enrollplus.feature.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.util.UUID

object SyllabusAiService {
    private val log = LoggerFactory.getLogger("SyllabusAiService")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // ── Result types ──────────────────────────────────────────────────────

    @Serializable
    data class ParsedHierarchy(
        val chapters: List<ParsedChapter>,
        val providerUsed: String,
    )

    @Serializable
    data class ParsedChapter(
        val title: String,
        val topics: List<ParsedTopic>,
    )

    @Serializable
    data class ParsedTopic(
        val title: String,
        val subtopics: List<ParsedSubtopic> = emptyList(),
    )

    @Serializable
    data class ParsedSubtopic(
        val title: String,
    )

    @Serializable
    data class PacePlanEstimate(
        @SerialName("per_class_pct") val perClassPct: Double,
        @SerialName("estimated_completion_week") val estimatedCompletionWeek: Int,
        val reasoning: String,
    )

    @Serializable
    data class AlertReconfirmation(
        val confirmed: Boolean,
        val reasoning: String,
    )

    @Serializable
    data class GeneratedQuestion(
        @SerialName("question_type") val questionType: String,
        @SerialName("question_text") val questionText: String,
        val options: List<String> = emptyList(),
        @SerialName("correct_answer") val correctAnswer: String,
        val explanation: String = "",
    )

    // ── 1. parseSyllabusImage ─────────────────────────────────────────────

    suspend fun parseSyllabusImage(
        imageBase64: String,
        imageMimeType: String,
        classLevel: String,
        subject: String,
        schoolId: UUID? = null,
    ): ParsedHierarchy? {
        val systemPrompt = """
            You are a syllabus parser for Indian school education (CBSE/NCERT).
            Given an image of a syllabus, extract the complete hierarchy:
            chapters → topics → subtopics.
            Return ONLY valid JSON: {"chapters":[{"title":"...","topics":[{"title":"...","subtopics":[{"title":"..."}]}]}]}
            Reference sources for structure quality:
            - NCERT: https://www.ncert.nic.in/syllabus.php
            - CBSE: https://www.cbse.gov.in/curriculum.html
            Subject: $subject, Class: $classLevel
            If the image is unclear or not a syllabus, return {"chapters":[]}.
        """.trimIndent()

        val result = AiService.completeWithVision(
            feature = "syllabus_parse",
            systemPrompt = systemPrompt,
            userText = "Parse this syllabus image and return the JSON hierarchy.",
            imageBase64 = imageBase64,
            imageMimeType = imageMimeType,
            schoolId = schoolId,
            temperature = 0.2,
            maxTokens = 4096,
        )
        if (!result.ok || result.content.isNullOrBlank()) {
            log.warn("parseSyllabusImage AI unavailable: {}", result.errorMessage)
            return null
        }
        return parseHierarchyJson(result.content, result.providerUsed)
    }

    // ── 2. parseSyllabusText ──────────────────────────────────────────────

    suspend fun parseSyllabusText(
        rawText: String,
        classLevel: String,
        subject: String,
        schoolId: UUID? = null,
    ): ParsedHierarchy? {
        val systemPrompt = """
            You are a syllabus parser for Indian school education (CBSE/NCERT).
            Given the raw text of a syllabus, extract the complete hierarchy:
            chapters → topics → subtopics.
            Return ONLY valid JSON: {"chapters":[{"title":"...","topics":[{"title":"...","subtopics":[{"title":"..."}]}]}]}
            Reference sources for structure quality:
            - NCERT: https://www.ncert.nic.in/syllabus.php
            - CBSE: https://www.cbse.gov.in/curriculum.html
            Subject: $subject, Class: $classLevel
            If the text is unclear or not a syllabus, return {"chapters":[]}.
        """.trimIndent()

        val messages = listOf(
            LlmMessage(role = "system", content = systemPrompt),
            LlmMessage(role = "user", content = "Syllabus text:\n\n$rawText"),
        )
        val result = AiService.complete(
            feature = "syllabus_parse",
            lane = AiLane.REASON,
            messages = messages,
            containsPii = false,
            schoolId = schoolId,
            temperature = 0.2,
            maxTokens = 4096,
        )
        if (!result.ok || result.content.isNullOrBlank()) {
            log.warn("parseSyllabusText AI unavailable: {}", result.errorMessage)
            return null
        }
        return parseHierarchyJson(result.content, result.providerUsed)
    }

    // ── 3. estimatePacePlan ───────────────────────────────────────────────

    suspend fun estimatePacePlan(
        totalTopics: Int,
        weeklyPeriods: Int,
        academicYearWeeks: Int,
        classLevel: String,
        schoolId: UUID? = null,
    ): PacePlanEstimate? {
        val totalClasses = weeklyPeriods * academicYearWeeks
        val prompt = """
            Estimate a pace plan for $totalTopics topics over $totalClasses classes
            (approximately $academicYearWeeks weeks at $weeklyPeriods periods/week).
            Class level: $classLevel
            Return ONLY valid JSON: {"per_class_pct":2.5,"estimated_completion_week":15,"reasoning":"..."}
            per_class_pct = percentage of syllabus to cover per class session.
        """.trimIndent()

        val result = AiService.complete(
            feature = "syllabus_pace",
            lane = AiLane.BATCH,
            messages = listOf(LlmMessage(role = "user", content = prompt)),
            containsPii = false,
            schoolId = schoolId,
            temperature = 0.3,
            maxTokens = 512,
        )
        if (!result.ok || result.content.isNullOrBlank()) {
            log.warn("estimatePacePlan AI unavailable: {}", result.errorMessage)
            return null
        }
        return try {
            val cleaned = result.content.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = json.parseToJsonElement(cleaned).jsonObject
            PacePlanEstimate(
                perClassPct = obj["per_class_pct"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: 0.0,
                estimatedCompletionWeek = obj["estimated_completion_week"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                reasoning = obj["reasoning"]?.jsonPrimitive?.contentOrNull ?: "",
            )
        } catch (e: Exception) {
            log.warn("estimatePacePlan parse failed: {}", e.message)
            null
        }
    }

    // ── 4. generateDailySummary ───────────────────────────────────────────

    suspend fun generateDailySummary(
        topicTitles: List<String>,
        classLevel: String,
        subject: String,
        schoolId: UUID? = null,
    ): String? {
        val topicsStr = topicTitles.joinToString(", ")
        val prompt = """
            Write a 2-3 sentence parent-friendly summary of what was taught in a $classLevel $subject class today.
            Topics covered: $topicsStr
            The summary should be simple, jargon-free, and help a parent understand what their child learned.
            Return ONLY the summary text, no JSON, no markdown.
        """.trimIndent()

        val result = AiService.complete(
            feature = "syllabus_summary",
            lane = AiLane.FAST_CHAT,
            messages = listOf(LlmMessage(role = "user", content = prompt)),
            containsPii = false,
            schoolId = schoolId,
            temperature = 0.5,
            maxTokens = 256,
        )
        if (!result.ok || result.content.isNullOrBlank()) {
            log.warn("generateDailySummary AI unavailable: {}", result.errorMessage)
            return null
        }
        return result.content.trim()
    }

    // ── 5. reconfirmAlert ─────────────────────────────────────────────────

    suspend fun reconfirmAlert(
        alertLevel: String,
        expectedPct: Int,
        actualPct: Int,
        subject: String,
        className: String,
        recentLogs: List<String>,
        schoolId: UUID? = null,
    ): AlertReconfirmation? {
        val logsStr = recentLogs.takeLast(5).joinToString("; ")
        val prompt = """
            A syllabus pace monitoring system detected a potential alert:
            - Alert level: $alertLevel
            - Expected coverage: $expectedPct%
            - Actual coverage: $actualPct%
            - Subject: $subject, Class: $className
            - Recent daily logs: $logsStr

            Is this a real concern that warrants alerting the teacher and admin,
            or is it likely a data artifact (e.g., teacher hasn't logged recently,
            holidays, exam week)?
            Return ONLY valid JSON: {"confirmed":true,"reasoning":"..."}
        """.trimIndent()

        val result = AiService.complete(
            feature = "syllabus_pace_reconfirm",
            lane = AiLane.REASON,
            messages = listOf(LlmMessage(role = "user", content = prompt)),
            containsPii = false,
            schoolId = schoolId,
            temperature = 0.3,
            maxTokens = 512,
        )
        if (!result.ok || result.content.isNullOrBlank()) {
            log.warn("reconfirmAlert AI unavailable: {}", result.errorMessage)
            return null
        }
        return try {
            val cleaned = result.content.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = json.parseToJsonElement(cleaned).jsonObject
            AlertReconfirmation(
                confirmed = obj["confirmed"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: false,
                reasoning = obj["reasoning"]?.jsonPrimitive?.contentOrNull ?: "",
            )
        } catch (e: Exception) {
            log.warn("reconfirmAlert parse failed: {}", e.message)
            null
        }
    }

    // ── 6. generateQuiz ───────────────────────────────────────────────────

    suspend fun generateQuiz(
        topicTitles: List<String>,
        classLevel: String,
        subject: String,
        questionTypes: List<String>,
        questionCount: Int,
        difficultyOffset: Int,
        schoolId: UUID? = null,
    ): List<GeneratedQuestion>? {
        val typesStr = questionTypes.joinToString(", ")
        val topicsStr = topicTitles.joinToString(", ")
        val difficultyDesc = when {
            difficultyOffset < -3 -> "slightly easier than standard $classLevel level"
            difficultyOffset > 3 -> "slightly harder than standard $classLevel level"
            else -> "standard $classLevel level"
        }
        val prompt = """
            Generate $questionCount quiz questions for a $classLevel $subject class.
            Topics: $topicsStr
            Question types: $typesStr (MCQ, FILL_BLANK, TRUE_FALSE)
            Difficulty: $difficultyDesc
            Reference sources for quality:
            - NCERT: https://www.ncert.nic.in/syllabus.php
            - CBSE: https://www.cbse.gov.in/curriculum.html

            CRITICAL CONSTRAINTS:
            - Every question MUST be strictly within the topics listed above.
            - Do NOT include questions from topics outside this list.
            - Do NOT include questions from other chapters or unrelated concepts.
            - Each question should test understanding of one of the listed topics.

            Return ONLY a JSON array:
            [{"question_type":"MCQ","question_text":"...","options":["A) ...","B) ...","C) ...","D) ..."],"correct_answer":"A","explanation":"..."}]

            For FILL_BLANK: options=[], correct_answer="the answer text"
            For TRUE_FALSE: options=[], correct_answer="true" or "false"
            Every question MUST have a correct_answer and explanation.
        """.trimIndent()

        val result = AiService.complete(
            feature = "syllabus_quiz",
            lane = AiLane.REASON,
            messages = listOf(LlmMessage(role = "user", content = prompt)),
            containsPii = false,
            schoolId = schoolId,
            temperature = 0.5,
            maxTokens = 4096,
        )
        if (!result.ok || result.content.isNullOrBlank()) {
            log.warn("generateQuiz AI unavailable: {}", result.errorMessage)
            return null
        }
        return parseQuizJson(result.content)
    }

    // ── JSON parsing helpers ──────────────────────────────────────────────

    private fun parseHierarchyJson(content: String, provider: String?): ParsedHierarchy {
        return try {
            val cleaned = content.trim()
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```")
                .trim()
            val obj = json.parseToJsonElement(cleaned).jsonObject
            val chapters = obj["chapters"]?.jsonArray?.map { ch ->
                val chObj = ch.jsonObject
                ParsedChapter(
                    title = chObj["title"]?.jsonPrimitive?.contentOrNull ?: "",
                    topics = chObj["topics"]?.jsonArray?.map { tp ->
                        val tpObj = tp.jsonObject
                        ParsedTopic(
                            title = tpObj["title"]?.jsonPrimitive?.contentOrNull ?: "",
                            subtopics = tpObj["subtopics"]?.jsonArray?.map { st ->
                                ParsedSubtopic(
                                    title = st.jsonObject["title"]?.jsonPrimitive?.contentOrNull ?: "",
                                )
                            } ?: emptyList(),
                        )
                    } ?: emptyList(),
                )
            } ?: emptyList()
            ParsedHierarchy(chapters = chapters, providerUsed = provider ?: "unknown")
        } catch (e: Exception) {
            log.warn("parseHierarchyJson failed: {}", e.message)
            ParsedHierarchy(chapters = emptyList(), providerUsed = provider ?: "unknown")
        }
    }

    private fun parseQuizJson(content: String): List<GeneratedQuestion> {
        return try {
            // Strip markdown code fences if present (```json ... ```)
            val cleaned = content.trim()
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```")
                .trim()
            val arr = json.parseToJsonElement(cleaned).jsonArray
            arr.map { el ->
                val obj = el.jsonObject
                GeneratedQuestion(
                    questionType = obj["question_type"]?.jsonPrimitive?.contentOrNull ?: "MCQ",
                    questionText = obj["question_text"]?.jsonPrimitive?.contentOrNull ?: "",
                    options = obj["options"]?.jsonArray?.map { it.jsonPrimitive.contentOrNull ?: "" } ?: emptyList(),
                    correctAnswer = obj["correct_answer"]?.jsonPrimitive?.contentOrNull ?: "",
                    explanation = obj["explanation"]?.jsonPrimitive?.contentOrNull ?: "",
                )
            }.filter { it.questionText.isNotBlank() && it.correctAnswer.isNotBlank() }
        } catch (e: Exception) {
            log.warn("parseQuizJson failed: {}", e.message)
            emptyList()
        }
    }
}
