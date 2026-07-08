// FILE: src/main/kotlin/com/littlebridge/enrollplus/feature/pews/core/KillSwitchConfig.kt
package com.littlebridge.enrollplus.feature.pews.core

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PewsFeatureFlagsTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Hot-reloadable kill-switch configuration sourced from the [PewsFeatureFlagsTable].
 *
 * Two levels:
 *   - Global kill (module_name = "global"): disables the entire PEWS system.
 *   - Granular kill (module_name = "<feature>"): disables a single PEWS module.
 *
 * A background coroutine polls the DB every [POLL_INTERVAL_MS] ms and refreshes
 * the in-memory map so toggles take effect without a restart.
 */
object KillSwitchConfig {
    private val log = LoggerFactory.getLogger("KillSwitchConfig")

    const val GLOBAL_MODULE = "global"
    private const val POLL_INTERVAL_MS = 60_000L // 60 seconds

    private val flags = ConcurrentHashMap<String, Boolean>()

    private data class KillSwitchState(
        val globalKilled: Boolean = false,
        val loaded: Boolean = false,
    )

    private val state = AtomicReference(KillSwitchState())

    /**
     * Start the hot-reload polling loop. Called once at boot from Application.kt.
     */
    fun startPolling(scope: CoroutineScope) {
        scope.launch {
            log.info("KillSwitchConfig polling started (interval={}ms)", POLL_INTERVAL_MS)
            while (isActive) {
                runCatching { reload() }
                    .onFailure { log.warn("KillSwitchConfig reload failed: {}", it.message) }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    /**
     * Force an immediate reload from the DB. Called at boot and by the polling loop.
     */
    suspend fun reload() {
        val rows = dbQuery {
            PewsFeatureFlagsTable.selectAll().toList()
        }
        val newFlags = ConcurrentHashMap<String, Boolean>()
        for (row in rows) {
            val name = row[PewsFeatureFlagsTable.moduleName]
            val killed = row[PewsFeatureFlagsTable.isKilled]
            newFlags[name] = killed
        }
        flags.clear()
        flags.putAll(newFlags)
        val newGlobalKilled = newFlags[GLOBAL_MODULE] ?: false
        state.set(KillSwitchState(globalKilled = newGlobalKilled, loaded = true))
        log.debug("KillSwitchConfig reloaded: {} flags, globalKilled={}", newFlags.size, newGlobalKilled)
    }

    /**
     * True if the global kill switch is active (entire PEWS disabled).
     */
    fun isGlobalKilled(): Boolean = state.get().globalKilled

    /**
     * True if [moduleName] is killed, either by its own flag or by the global kill.
     * Returns false if flags haven't been loaded yet (fail-open at boot before first reload).
     */
    fun isKilled(moduleName: String): Boolean {
        val s = state.get()
        if (!s.loaded) return false
        return s.globalKilled || (flags[moduleName] ?: false)
    }

    /**
     * Toggle a module's kill switch (for admin endpoints or manual DB updates).
     * The next polling cycle picks up the change.
     */
    fun isModuleKilled(moduleName: String): Boolean = isKilled(moduleName)
}
