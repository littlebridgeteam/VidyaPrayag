package com.littlebridge.enrollplus.core

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.FeatureFlagsTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * GAP-019 — General-purpose feature flag service.
 *
 * Hot-reloadable flags sourced from [FeatureFlagsTable]. A background coroutine
 * polls the DB every [POLL_INTERVAL_MS] ms and refreshes the in-memory cache so
 * toggles take effect without a restart.
 *
 * Usage:
 *   FeatureFlagService.isEnabled("pews", "enabled")  // → true/false
 *   FeatureFlagService.getValue("messaging", "max_retries")  // → "5"
 *
 * The "global" / "enabled" flag is the master switch — when false, all
 * [isEnabled] calls return false regardless of individual flag state.
 */
object FeatureFlagService {
    private val log = LoggerFactory.getLogger("FeatureFlagService")

    private const val POLL_INTERVAL_MS = 60_000L
    private const val GLOBAL_SCOPE = "global"
    private const val ENABLED_KEY = "enabled"

    private data class FlagEntry(
        val isEnabled: Boolean,
        val value: String?,
    )

    private data class FlagState(
        val flags: Map<String, FlagEntry> = emptyMap(),
        val globalEnabled: Boolean = true,
        val loaded: Boolean = false,
    )

    private val state = AtomicReference(FlagState())

    private fun cacheKey(scope: String, key: String) = "$scope:$key"

    /**
     * Start the hot-reload polling loop. Called once at boot.
     */
    fun startPolling(scope: CoroutineScope) {
        scope.launch {
            log.info("FeatureFlagService polling started (interval={}ms)", POLL_INTERVAL_MS)
            while (isActive) {
                runCatching { reload() }
                    .onFailure { log.warn("FeatureFlagService reload failed: {}", it.message) }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    /**
     * Force an immediate reload from the DB.
     */
    suspend fun reload() {
        val rows = dbQuery {
            FeatureFlagsTable.selectAll().toList()
        }
        val newFlags = ConcurrentHashMap<String, FlagEntry>()
        for (row in rows) {
            val key = cacheKey(row[FeatureFlagsTable.scope], row[FeatureFlagsTable.key])
            newFlags[key] = FlagEntry(
                isEnabled = row[FeatureFlagsTable.isEnabled],
                value = row[FeatureFlagsTable.value],
            )
        }
        val globalEnabled = newFlags[cacheKey(GLOBAL_SCOPE, ENABLED_KEY)]?.isEnabled ?: true
        state.set(FlagState(flags = newFlags, globalEnabled = globalEnabled, loaded = true))
        log.debug("FeatureFlagService reloaded: {} flags, globalEnabled={}", newFlags.size, globalEnabled)
    }

    /**
     * True if the flag (scope, key) is enabled. Returns false if the global
     * switch is off. Returns true if flags haven't loaded yet (fail-open at boot).
     */
    fun isEnabled(scope: String, key: String = ENABLED_KEY): Boolean {
        val s = state.get()
        if (!s.loaded) return true
        if (!s.globalEnabled && scope != GLOBAL_SCOPE) return false
        return s.flags[cacheKey(scope, key)]?.isEnabled ?: true
    }

    /**
     * Get the string value of a flag, or null if not set.
     */
    fun getValue(scope: String, key: String): String? {
        return state.get().flags[cacheKey(scope, key)]?.value
    }

    /**
     * Get the string value of a flag, or a default if not set.
     */
    fun getValueOrDefault(scope: String, key: String, default: String): String {
        return getValue(scope, key) ?: default
    }

    /**
     * Get an integer flag value, or a default.
     */
    fun getIntValue(scope: String, key: String, default: Int): Int {
        return getValue(scope, key)?.toIntOrNull() ?: default
    }

    /**
     * Update a flag's enabled state in the DB. The next polling cycle picks
     * up the change, or call [reload] for immediate effect.
     */
    suspend fun setEnabled(scope: String, key: String, enabled: Boolean) {
        val rows = dbQuery {
            FeatureFlagsTable.update(
                { (FeatureFlagsTable.scope eq scope) and (FeatureFlagsTable.key eq key) }
            ) {
                it[FeatureFlagsTable.isEnabled] = enabled
                it[FeatureFlagsTable.updatedAt] = Instant.now()
            }
        }
        if (rows > 0) reload()
    }

    /**
     * Update a flag's string value in the DB.
     */
    suspend fun setValue(scope: String, key: String, value: String) {
        val rows = dbQuery {
            FeatureFlagsTable.update(
                { (FeatureFlagsTable.scope eq scope) and (FeatureFlagsTable.key eq key) }
            ) {
                it[FeatureFlagsTable.value] = value
                it[FeatureFlagsTable.updatedAt] = Instant.now()
            }
        }
        if (rows > 0) reload()
    }
}
