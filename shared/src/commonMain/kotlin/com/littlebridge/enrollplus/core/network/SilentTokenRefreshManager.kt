package com.littlebridge.enrollplus.core.network

import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.core.currentTimeMillis
import com.littlebridge.enrollplus.feature.auth.data.remote.AuthApi
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Interface for proactive (silent) token refresh.
 */
interface TokenRefreshManager {
    /**
     * Checks if the current access token is about to expire and refreshes it
     * silently if so. Safe to call on app start, app foreground, or before any
     * critical API call.
     *
     * Returns true if the token is valid (either was still fresh or was
     * successfully refreshed), false if the session is invalid and the user
     * should be logged out.
     */
    suspend fun refreshIfNeeded(): Boolean
}

/**
 * Proactive (silent) token refresh — refreshes the access token BEFORE it
 * expires so the user never sees a 401 during normal usage.
 *
 * Professional apps don't wait for a 401 to refresh; they check token expiry
 * on app start and app foreground, and refresh silently if the token will
 * expire within a safety window. This eliminates the race condition where a
 * 401 triggers refresh while the server (e.g. Render free tier) is spun down.
 *
 * The refresh uses the same [AuthApi.refresh] endpoint and the same refresh-
 * token rotation + reuse detection (RA-35) as the reactive [TokenAuthenticator].
 * It is safe because:
 *  - The server rotates the refresh token on every call (single-use).
 *  - Reuse detection revokes the session family if a rotated token is replayed.
 *  - The mutex prevents concurrent refresh calls (same pattern as
 *    TokenAuthenticator's refreshMutex).
 *
 * If the refresh fails transiently (network error, 5xx, Render spin-down), the
 * session is NOT cleared — the existing (still-valid) access token remains
 * usable until it actually expires, at which point the TokenAuthenticator
 * handles the 401 as before.
 *
 * If the refresh fails with 401/403 (refresh token revoked/expired), the
 * session IS cleared via [onSessionInvalid] — same as TokenAuthenticator's
 * onRefreshFailed.
 */
class SilentTokenRefreshManager(
    private val prefs: PreferenceRepository,
    private val authApi: AuthApi,
    private val localeManager: LocaleManager,
    // Called when the refresh token is truly invalid (401/403 from /refresh).
    // Clears the session so the reactive authState navigates to login.
    private val onSessionInvalid: suspend () -> Unit,
) : TokenRefreshManager {
    private val mutex = Mutex()

    companion object {
        // Refresh if the access token will expire within this many seconds.
        // 5 minutes gives a comfortable buffer — even if the server takes 30s
        // to wake up on Render, the token is still valid when the refresh
        // response arrives.
        private const val SAFETY_WINDOW_SECONDS = 300L

        // JWT exp claim is at index 1 in a 3-part JWT (header.payload.signature).
        // The payload is base64url-encoded JSON. We decode just the "exp" field
        // to avoid pulling in a full JWT library on the client side.
    }

    /**
     * Checks if the current access token is about to expire and refreshes it
     * silently if so. Safe to call on app start, app foreground, or before any
     * critical API call.
     *
     * Returns true if the token is valid (either was still fresh or was
     * successfully refreshed), false if the session is invalid and the user
     * should be logged out.
     */
    override suspend fun refreshIfNeeded(): Boolean {
        val token = prefs.getUserToken().first() ?: return false
        val refreshToken = prefs.getRefreshToken().first() ?: return false

        val expSeconds = decodeJwtExpiry(token)
        if (expSeconds == null) {
            AppLogger.w("SilentRefresh", "Could not decode token expiry — skipping proactive refresh")
            return true // Let TokenAuthenticator handle it reactively
        }

        val nowSeconds = currentTimeMillis() / 1000
        val secondsUntilExpiry = expSeconds - nowSeconds

        if (secondsUntilExpiry > SAFETY_WINDOW_SECONDS) {
            // Token is still fresh — no refresh needed
            return true
        }

        AppLogger.i("SilentRefresh", "Token expires in ${secondsUntilExpiry}s (within ${SAFETY_WINDOW_SECONDS}s safety window) — refreshing proactively")

        return mutex.withLock {
            // Double-check after acquiring the lock — another caller may have
            // already refreshed the token while we were waiting.
            val currentToken = prefs.getUserToken().first()
            if (currentToken != null && currentToken != token) {
                AppLogger.i("SilentRefresh", "Concurrent refresh already completed; reusing new token")
                return@withLock true
            }

            doRefresh(refreshToken)
        }
    }

    private suspend fun doRefresh(refreshToken: String): Boolean {
        // Retry up to 3 times with backoff — Render spin-down can take 30+ seconds.
        for (attempt in 1..3) {
            val result = authApi.refresh(refreshToken)

            when (result) {
                is com.littlebridge.enrollplus.core.network.NetworkResult.Success -> {
                    val data = result.data.data
                    if (data == null) {
                        AppLogger.w("SilentRefresh", "Refresh response missing data field — aborting")
                        return true // Keep existing session; TokenAuthenticator will handle later
                    }
                    // Persist the new tokens (same as AuthRepositoryImpl.saveSession)
                    prefs.setUserToken(data.token)
                    prefs.setRefreshToken(data.refreshToken)
                    prefs.setUserId(data.userId)
                    prefs.setUserRole(data.role)
                    prefs.setUserName(data.name)
                    prefs.setProfileCompleted(data.profileCompleted)
                    localeManager.setLocaleFromServer(data.languagePref)
                    AppLogger.i("SilentRefresh", "Proactive refresh succeeded on attempt $attempt")
                    return true
                }
                is com.littlebridge.enrollplus.core.network.NetworkResult.Error -> {
                    AppLogger.w("SilentRefresh", "Refresh attempt $attempt got HTTP ${result.code}: ${result.message}")
                    if (result.code == 401 || result.code == 403) {
                        // Refresh token is truly invalid/expired/revoked — log out
                        AppLogger.i("SilentRefresh", "Refresh token rejected (${result.code}) — session invalid")
                        onSessionInvalid()
                        return false
                    }
                    // 5xx or other error — retry with backoff
                    if (attempt < 3) {
                        kotlinx.coroutines.delay(2000L * attempt)
                    }
                }
                is com.littlebridge.enrollplus.core.network.NetworkResult.ConnectionError -> {
                    AppLogger.w("SilentRefresh", "Refresh attempt $attempt — connection error (Render spin-down?); will retry")
                    if (attempt < 3) {
                        kotlinx.coroutines.delay(2000L * attempt)
                    }
                }
            }
        }

        // All retries failed with transient errors — keep the session alive.
        // The existing access token is still valid for a few more minutes.
        // TokenAuthenticator will handle the 401 when it eventually expires.
        AppLogger.w("SilentRefresh", "All refresh attempts failed (transient) — keeping session, TokenAuthenticator will handle later")
        return true
    }

    /**
     * Decodes the `exp` claim from a JWT without a full JWT library.
     * Returns the expiry as Unix epoch seconds, or null if parsing fails.
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeJwtExpiry(jwt: String): Long? {
        return runCatching {
            val parts = jwt.split(".")
            if (parts.size < 2) return null

            // Base64url decode the payload (part 1)
            val payload = parts[1]
            val bytes = Base64.UrlSafe.decode(payload)
            val json = bytes.decodeToString()

            // Extract "exp" value from the JSON — simple regex to avoid a JSON parser
            val expRegex = """"exp"\s*:\s*(\d+)""".toRegex()
            val match = expRegex.find(json) ?: return null
            match.groupValues[1].toLongOrNull()
        }.getOrNull()
    }
}
