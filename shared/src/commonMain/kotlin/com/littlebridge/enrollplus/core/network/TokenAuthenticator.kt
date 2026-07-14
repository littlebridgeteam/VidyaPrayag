package com.littlebridge.enrollplus.core.network

import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.util.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val lenientJson = Json { ignoreUnknownKeys = true }

/**
 * Serializes concurrent refresh-token calls. Without this, when multiple API
 * requests get 401 at the same time, each one independently calls
 * `refreshTokens()`. The first call rotates the refresh token on the server
 * (revoking the old one). The second call still uses the old refresh token →
 * the server's reuse-detection revokes the ENTIRE session family → random
 * logout. The mutex ensures only one refresh happens at a time; concurrent
 * 401s wait, then reuse the freshly-persisted tokens without a second call.
 */
private val refreshMutex = Mutex()

/**
 * Ktor [Auth] / `bearer` configuration — automatic token refresh on 401 with a
 * clean logout on refresh failure.
 *
 * Flow (transparent to every API call made through the singleton [HttpClient]):
 * ```
 * request → 401 (access token expired)
 *   → refreshTokens() calls POST /api/v1/auth/refresh with the stored refresh token
 *       → success → persist NEW access AND refresh token → original request retried
 *       → failure → clear the session + evict cached bearer tokens (logout)
 * ```
 *
 * The refresh exchange goes through a *separate* plain [HttpClient] (no [Auth]
 * plugin) so a 401 on the refresh endpoint itself can never recurse back into
 * this block.
 *
 * RA-35 (server refresh-token ROTATION + reuse-detection): the server mints a
 * brand-new `refresh_token` on every `/refresh` call and single-uses the old
 * one. The client therefore MUST persist the new refresh token returned here —
 * not just the new access token — or the next refresh would replay a revoked
 * token and trip reuse-detection, killing the whole session family.
 */
internal fun HttpClientConfig<*>.installTokenAuth(
    prefs: PreferenceRepository,
    refreshClient: HttpClient,
    // Called when the refresh token itself is invalid/expired/revoked. Reuses the
    // existing logout path (clear DataStore session + evict the in-memory bearer
    // cache). Clearing the token makes the reactive authState (App.kt) navigate
    // back to landing — no Composable, ViewModel or repository involvement.
    onRefreshFailed: suspend () -> Unit,
) {
    install(Auth) {
        bearer {
            loadTokens {
                val access = prefs.getUserToken().first()
                val refresh = prefs.getRefreshToken().first()
                if (access != null) BearerTokens(access, refresh ?: "") else null
            }
            refreshTokens {
                // Capture the access token that triggered the 401 so we can
                // detect if a concurrent refresh already replaced it.
                val expiredAccess = prefs.getUserToken().first()

                refreshMutex.withLock {
                    // After acquiring the lock, check if another concurrent
                    // refresh already replaced the access token. If so, return
                    // the new tokens WITHOUT making another refresh call —
                    // replaying the old (now-rotated) refresh token would trip
                    // the server's reuse-detection and kill the session family.
                    val currentAccess = prefs.getUserToken().first()
                    if (currentAccess != null && currentAccess != expiredAccess) {
                        val currentRefresh = prefs.getRefreshToken().first() ?: ""
                        return@refreshTokens BearerTokens(currentAccess, currentRefresh)
                    }

                    val refresh = prefs.getRefreshToken().first()
                        ?: run {
                            onRefreshFailed()
                            return@refreshTokens null
                        }
                    // Retry the refresh call up to 3 times with backoff.
                    // Render free-tier spin-down can take 30+ seconds to wake;
                    // a single transient failure must NOT kill the session.
                    var resp: io.ktor.client.statement.HttpResponse? = null
                    var lastStatus: HttpStatusCode? = null
                    for (attempt in 1..3) {
                        resp = runCatching {
                            refreshClient.post(
                                AppConfig.authBaseUrl.trimEnd('/') + "/api/v1/auth/refresh"
                            ) {
                                contentType(ContentType.Application.Json)
                                setBody(mapOf("refresh_token" to refresh))
                            }
                        }.getOrNull()
                        if (resp != null) {
                            lastStatus = resp.status
                            if (resp.status.isSuccess()) break
                            // 401/403 from refresh endpoint = token truly invalid, don't retry
                            if (resp.status == HttpStatusCode.Unauthorized ||
                                resp.status == HttpStatusCode.Forbidden) break
                        }
                        // Network error or 5xx → wait and retry (Render spin-up)
                        if (attempt < 3) kotlinx.coroutines.delay(2000L * attempt)
                    }
                    if (resp == null || !resp.status.isSuccess()) {
                        // Only log out if the refresh endpoint explicitly rejected
                        // the token (401/403). Transient network errors (null resp
                        // or 5xx) should NOT clear the session — the user keeps
                        // their tokens and the next API call will retry refresh.
                        if (lastStatus == HttpStatusCode.Unauthorized ||
                            lastStatus == HttpStatusCode.Forbidden) {
                            onRefreshFailed()
                        } else {
                            // Transient failure — return null to abort this refresh
                            // cycle WITHOUT clearing session. The next request will
                            // re-trigger loadTokens() with the existing (expired)
                            // access token, get another 401, and retry refresh.
                            // If the server comes back, refresh succeeds. If the
                            // user backgrounds the app, they stay logged in.
                        }
                        return@refreshTokens null
                    }
                    // Parse { success, message, data: { token, refresh_token, ... } }
                    val bodyText = runCatching { resp.bodyAsText() }.getOrNull()
                    val data = bodyText?.let {
                        runCatching {
                            lenientJson
                                .parseToJsonElement(it)
                                .jsonObject["data"]?.jsonObject
                        }.getOrNull()
                    }
                    val newAccess = data?.get("token")?.jsonPrimitive?.contentOrNull
                    if (newAccess == null) {
                        onRefreshFailed()
                        return@refreshTokens null
                    }
                    // RA-35: persist the ROTATED refresh token, falling back to the
                    // current one only if the server omitted it.
                    val newRefresh = data["refresh_token"]?.jsonPrimitive?.contentOrNull ?: refresh
                    prefs.setUserToken(newAccess)
                    prefs.setRefreshToken(newRefresh)
                    BearerTokens(newAccess, newRefresh)
                }
            }
        }
    }
}

/**
 * Builds the plain refresh [HttpClient] (no [Auth] plugin) used solely to perform
 * the refresh-token exchange so the bearer refresh path never recurses.
 */
internal fun buildRefreshClient(engine: io.ktor.client.engine.HttpClientEngine): HttpClient =
    HttpClient(engine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
    }

/**
 * Clears the Ktor [Auth] plugin's in-memory bearer-token cache on the given
 * [client] (see [SessionManager] for the full rationale). Used by the
 * refresh-failure logout path so the next request re-resolves from the (now
 * cleared) store instead of replaying a dead token.
 */
internal fun HttpClient.clearBearerCache() {
    runCatching {
        authProviders
            .filterIsInstance<BearerAuthProvider>()
            .forEach { it.clearToken() }
    }
}
