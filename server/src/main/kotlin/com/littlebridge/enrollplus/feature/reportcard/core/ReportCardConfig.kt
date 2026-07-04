// FILE: server/src/main/kotlin/com/littlebridge/enrollplus/feature/reportcard/core/ReportCardConfig.kt
package com.littlebridge.enrollplus.feature.reportcard.core

import com.littlebridge.enrollplus.core.EnvConfig
import java.util.concurrent.atomic.AtomicReference

/**
 * Centralized configuration for the AI Report Card 2.0 feature.
 *
 * All values are sourced from environment variables (via [EnvConfig]) with
 * sensible defaults. No hardcoded values anywhere in the feature — every
 * service reads from this object.
 *
 * Supported env vars:
 *   REPORTCARD_ENABLED           — master toggle ("true"/"false", default "true")
 *   AI_BATCH_CONCURRENCY         — max parallel students in batch job (default 5)
 *   NARRATOR_MAX_STEPS           — max agent tool-call steps (default 6)
 *   NARRATOR_TEMPERATURE         — LLM sampling temperature (default 0.3)
 *   NARRATOR_MAX_TOKENS          — max output tokens per student (default 2048)
 *   TRIAGE_CLASSIFY_MODEL        — optional model override for Tier-1 classify
 *   CACHE_TTL_MINUTES            — response cache TTL in minutes (default 1440)
 *   REPORTCARD_FALLBACK_ON_AI_FAIL — use deterministic fallback when AI fails (default "true")
 *
 * SOLID: S (single responsibility: configuration only).
 */
object ReportCardConfig {

    private data class ConfigSnapshot(
        val currentTermOverride: String? = null,
        val termWindowDaysOverride: Int? = null,
        val enabledOverride: Boolean? = null,
        val fallbackOverride: Boolean? = null,
    )

    private val snapshot = AtomicReference(ConfigSnapshot())

    fun updateConfig(
        currentTerm: String? = null,
        termWindowDays: Int? = null,
        enabled: Boolean? = null,
        fallbackOnAiFail: Boolean? = null,
    ) {
        snapshot.getAndUpdate { current ->
            ConfigSnapshot(
                currentTermOverride = currentTerm ?: current.currentTermOverride,
                termWindowDaysOverride = termWindowDays ?: current.termWindowDaysOverride,
                enabledOverride = enabled ?: current.enabledOverride,
                fallbackOverride = fallbackOnAiFail ?: current.fallbackOverride,
            )
        }
    }

    val enabled: Boolean
        get() = snapshot.get().enabledOverride ?: EnvConfig.get("REPORTCARD_ENABLED", "true").equals("true", ignoreCase = true)

    val batchConcurrency: Int
        get() = (EnvConfig.get("AI_BATCH_CONCURRENCY")?.toIntOrNull() ?: 5).coerceIn(1, 20)

    val narratorMaxSteps: Int
        get() = (EnvConfig.get("NARRATOR_MAX_STEPS")?.toIntOrNull() ?: 6).coerceIn(1, 20)

    val narratorTemperature: Double
        get() = (EnvConfig.get("NARRATOR_TEMPERATURE")?.toDoubleOrNull() ?: 0.3).coerceIn(0.0, 2.0)

    val narratorMaxTokens: Int
        get() = (EnvConfig.get("NARRATOR_MAX_TOKENS")?.toIntOrNull() ?: 2048).coerceIn(256, 8192)

    val triageClassifyModel: String?
        get() = EnvConfig.get("TRIAGE_CLASSIFY_MODEL")

    val cacheTtlMinutes: Long
        get() = (EnvConfig.get("CACHE_TTL_MINUTES")?.toLongOrNull() ?: 1440L).coerceIn(1L, 10080L)

    val fallbackOnAiFail: Boolean
        get() = snapshot.get().fallbackOverride ?: EnvConfig.get("REPORTCARD_FALLBACK_ON_AI_FAIL", "true").equals("true", ignoreCase = true)

    /** Current term label for scheduled auto-generation (e.g. "Term 1"). Null disables auto-trigger. */
    val currentTerm: String?
        get() = snapshot.get().currentTermOverride ?: EnvConfig.get("REPORTCARD_CURRENT_TERM")

    /** Days before term end to start auto-generation (default 7). */
    val termWindowDays: Int
        get() = (snapshot.get().termWindowDaysOverride ?: (EnvConfig.get("REPORTCARD_TERM_WINDOW_DAYS")?.toIntOrNull() ?: 7)).coerceIn(1, 90)

    /** Retry backoff delays in milliseconds for AI provider failures. */
    val retryBackoffMs: List<Long>
        get() = listOf(1000L, 2000L, 4000L)
}
