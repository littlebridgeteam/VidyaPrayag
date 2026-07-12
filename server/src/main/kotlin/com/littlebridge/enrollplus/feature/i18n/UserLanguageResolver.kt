/*
 * File: UserLanguageResolver.kt
 * Module: feature.i18n
 *
 * Resolves a user's language preference for server-side notification translation.
 * Reads from AppUsersTable.languagePref with an in-memory cache (10-minute TTL)
 * to avoid a DB hit on every notification send.
 *
 * Cache: ConcurrentHashMap with timestamp-based expiry (Caffeine not available
 * in the server module). Evicted on PATCH /api/v1/user/language-pref.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §9.3 Mechanism A
 */
package com.littlebridge.enrollplus.feature.i18n

import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

object UserLanguageResolver {

    private val supportedLanguages = setOf("en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa")

    private data class CachedLang(
        val lang: String,
        val markedAt: TimeSource.Monotonic.ValueTimeMark,
    )

    private val cache = ConcurrentHashMap<UUID, CachedLang>()
    private val ttl = 10.minutes
    private val timeSource = TimeSource.Monotonic

    suspend fun resolve(userId: UUID): String {
        val cached = cache[userId]
        if (cached != null && (timeSource.markNow() - cached.markedAt) < ttl) {
            return cached.lang
        }

        val lang = dbQuery {
            AppUsersTable.selectAll()
                .where { AppUsersTable.id eq userId }
                .singleOrNull()
                ?.get(AppUsersTable.languagePref)
        }

        val resolved = lang?.takeIf { it.isNotBlank() && it in supportedLanguages } ?: "en"
        cache[userId] = CachedLang(resolved, timeSource.markNow())
        return resolved
    }

    fun evict(userId: UUID) {
        cache.remove(userId)
    }
}
