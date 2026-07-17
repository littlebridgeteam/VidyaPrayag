package com.littlebridge.enrollplus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.presentation.MainViewModel
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.ui.navigation.AuthNavGraph
import com.littlebridge.enrollplus.ui.screens.shared.SplashScreen
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.locale.LocalLocale
import com.littlebridge.enrollplus.ui.v2.navigation.NavGraphV2
import com.littlebridge.enrollplus.util.Config
import io.ktor.client.*
import kotlinx.coroutines.delay
import okio.FileSystem
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Application entrypoint — drives the **`ui/v2`** design-system UI exclusively.
 *
 * The legacy `ui/` (theme/components/auth/screens) and the old 35-destination
 * `navigation/NavGraph.kt` have been removed; navigation is now role-driven through
 * [NavGraphV2], which selects the correct portal (`SchoolPortalPremium` / `TeacherPortalShell` /
 * `ParentPortalShell`) and applies the matching theme via [VThemeRegistry].
 *
 * Flow:
 *  - `KoinContext` → [MainViewModel] (auth state).
 *  - Install the Coil image loader (Ktor fetcher + Supabase token-stripping cache mapper).
 *  - Splash always plays for a minimum 1600ms brand reveal, regardless of auth state.
 *  - Once both the splash minimum has elapsed AND auth is loaded → transition.
 *  - Authenticated → [NavGraphV2] (portals only).
 *  - Unauthenticated → [AuthNavGraph] (starts at Landing — splash is already handled here).
 */
@OptIn(KoinExperimentalAPI::class, coil3.annotation.ExperimentalCoilApi::class)
@Composable
@Preview
fun App(
    // FEATURE 1: fired once Compose has drawn its first frame so the Android host
    // can dismiss the native SplashScreen with zero white flash. No-op default keeps
    // iOS / desktop / @Preview callers unchanged (RULE-5: commonMain-safe).
    onContentRendered: () -> Unit = {},
    // Deep-link path from a notification tap (Android only). When non-null,
    // NavGraphV2 parses it and routes to the correct portal/screen.
    deepLink: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    // Push notification ref info for auto-mark-read (Android only).
    pushRefType: String? = null,
    pushRefId: String? = null,
    onPushRefConsumed: () -> Unit = {},
) {
    KoinContext {
        // Signal the platform host after the first composition lands. SideEffect runs
        // after every successful recomposition; the host only reads the first flip.
        SideEffect { onContentRendered() }

        val viewModel: MainViewModel = koinViewModel()
        val authState by viewModel.authState.collectAsState()

        // Proactive token refresh on app foreground — when the app returns to
        // the foreground (ON_RESUME), check if the access token is about to
        // expire and refresh it silently before the user interacts with any
        // screen. This is the professional pattern: refresh BEFORE expiry,
        // not after a 401.
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                    viewModel.refreshOnForeground()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        // Multi-Language: provide the current locale to all composables via LocalLocale.
        val localeManager = koinInject<LocaleManager>()
        val currentLocale by localeManager.currentLocale.collectAsState()

        val httpClient = koinInject<HttpClient>()
        val platform = koinInject<Platform>()

        // Restore cached school branding so splash/login screens can show the
        // school's brand immediately (before authentication completes).
        val brandingThemeManager = koinInject<com.littlebridge.enrollplus.feature.branding.presentation.BrandingThemeManager>()
        LaunchedEffect(Unit) { brandingThemeManager.loadCached() }

        setSingletonImageLoaderFactory { context: PlatformContext ->
            ImageLoader.Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory(httpClient))
                    // Mapper to strip Supabase tokens from cache keys to avoid re-downloads
                    add(coil3.map.Mapper<io.ktor.http.Url, String> { data, _ ->
                        if (data.host.contains("supabase.co")) {
                            data.toString().substringBefore("?token=")
                        } else {
                            null
                        }
                    })
                }
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, 0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(platform.cacheDir / "image_cache")
                        .maxSizeBytes(512L * 1024 * 1024) // 512MB
                        .build()
                }
                .logger(coil3.util.DebugLogger())
                .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
                .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
                .crossfade(true)
                .build()
        }

        val authViewModel: AuthViewModel = koinViewModel()

        // ── SESSION-BLEED FIX (root cause) ───────────────────────────────────────
        // This app has NO NavHost / NavController: NavGraphV2 is a hand-rolled
        // AnimatedContent state machine, so without intervention there would be
        // exactly ONE ViewModelStoreOwner for the whole app (the platform root).
        // Every `koinViewModel()` in every portal/screen (SchoolDashboardViewModel,
        // MessagesViewModel, ParentHomeViewModel, …) is cached in that store keyed
        // by class, so a `factory{}` Koin registration does NOT yield a fresh
        // instance across a logout → re-login: AndroidX's ViewModelStore hands back
        // the SAME cached instance, still holding the previous session's school_id /
        // data / screen state. That is the real reason an Admin logout → Parent
        // login renders the Admin dashboard with the Parent's name stuck in skeleton
        // (the surviving SchoolDashboardViewModel re-fetches admin endpoints with a
        // parent JWT and silently stalls).
        //
        // The fix: give the authenticated graph its own SESSION-SCOPED
        // ViewModelStoreOwner, keyed by user id. MainViewModel (the app-lifetime
        // auth/session VM) stays on the ROOT owner and is resolved ABOVE this scope,
        // so it is never torn down and keeps driving authState reactively. When the
        // session identity changes (logout clears the user id → new login sets a new
        // one), the keyed owner is recreated and its predecessor's store is cleared
        // in onDispose — calling onCleared() on every portal/screen VM from the old
        // session. The next session therefore builds every VM from scratch: no state
        // can bleed across a role switch.
        val isAuthenticated = !authState.token.isNullOrBlank()

        // Splash minimum duration — the brand reveal always plays in full,
        // even if the session check resolves instantly. This gives a
        // consistent premium feel for both logged-in and logged-out users.
        var splashMinElapsed by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(1600)
            splashMinElapsed = true
        }
        val showSplash = !authState.isLoaded || !splashMinElapsed

        // Auto-mark notification as read when a push notification is tapped.
        // Uses refType+refId to identify the notification on the server.
        if (pushRefType != null && pushRefId != null && isAuthenticated) {
            val notificationsVm: com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel = koinViewModel()
            LaunchedEffect(pushRefType, pushRefId) {
                notificationsVm.markByRef(pushRefType, pushRefId)
                onPushRefConsumed()
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(VColors.cream)) {
         // Multi-Language: provide the current locale to all composables below.
         CompositionLocalProvider(LocalLocale provides currentLocale) {
            // Splash plays for a minimum 1600ms brand reveal regardless of auth
            // state. Only transitions when BOTH the min duration has elapsed AND
            // the session check (JWT + role) has resolved. This ensures a
            // consistent premium entry for logged-in and logged-out users alike.
            AnimatedContent(
                targetState = showSplash,
                transitionSpec = { fadeIn(tween(400, easing = VMotion.ease)) togetherWith fadeOut(tween(400, easing = VMotion.ease)) },
                label = "splash-to-app",
                modifier = Modifier.fillMaxSize(),
            ) { isSplash ->
                if (isSplash) {
                    SplashScreen(onTimeout = { })
                } else if (isAuthenticated) {
                    // The session key is the live JWT (unique per login) while
                    // authenticated, or a constant sentinel while logged out. When it
                    // changes — logout, or a logout→login role switch — SessionScope
                    // tears down the previous session's ViewModelStore (onCleared on
                    // every cached portal/screen VM) and hands the new graph a clean
                    // store. MainViewModel above is untouched and keeps authState live.
                    val sessionKey = authState.token?.takeIf { isAuthenticated } ?: "unauthenticated"
                    SessionScope(sessionKey = sessionKey) {
                        NavGraphV2(
                            role = authState.role,
                            isAuthenticated = isAuthenticated,
                            onLogout = { viewModel.logout() },
                            deepLink = deepLink,
                            onDeepLinkConsumed = onDeepLinkConsumed,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                } else {
                    AuthNavGraph(
                        authViewModel = authViewModel,
                        onAuthSuccess = { },
                    )
                }
            }

            // Debug-only backend banner (SCHOOL_SIDE_STATUS_REPORT §8.4).
            // Shows the exact base URL the dev app is using so a phone
            // screenshot proves laptop-vs-Render at a glance. Red = still
            // pointing at Render (devBaseUrl not set), green = laptop/LAN.
            if (Config.isDev) {
                val isRender = Config.schoolBaseUrl.contains("onrender.com")
                Text(
                    text = "DEV → ${Config.schoolBaseUrl}" +
                        if (isRender) "  ⚠ set devBaseUrl" else "",
                    color = Color.White,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .background(if (isRender) Color(0xFFD32F2F) else Color(0xFF2E7D32))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
         } // end CompositionLocalProvider
        }
    }
}

/**
 * SessionScope — a per-login [ViewModelStoreOwner] for everything below it.
 *
 * WHY THIS EXISTS (session-bleed root cause): NavGraphV2 is a hand-rolled
 * `AnimatedContent` state machine with no `NavHost`/`NavController`, so every
 * `koinViewModel()` in every portal and screen would otherwise resolve against the
 * single app-root ViewModelStore and be cached there for the whole process. A
 * `factory{}` Koin registration does not help, because `koinViewModel()` layers
 * AndroidX's ViewModelStore (keyed by class) on top — the same store returns the
 * same cached instance after a logout → re-login, leaking the previous user's
 * `school_id` / data / screen state into the next session (Admin dashboard shown
 * to a freshly-logged-in Parent, name stuck in skeleton).
 *
 * By providing a fresh [ViewModelStore] keyed on [sessionKey] (the live JWT, which
 * is unique per login and becomes a sentinel on logout), the authenticated graph
 * gets its own store. The instant the key changes, Compose disposes the old scope
 * and [DisposableEffect.onDispose] calls `store.clear()` — running `onCleared()` on
 * every session-scoped VM and evicting them — before the new session builds its VMs
 * from scratch. The app-lifetime [MainViewModel] is resolved ABOVE this scope on the
 * root owner, so it is never torn down and keeps `authState` reactive across logins.
 */
@Composable
private fun SessionScope(
    sessionKey: String,
    content: @Composable () -> Unit,
) {
    // A new store per distinct session key. `remember(sessionKey)` rebuilds it the
    // moment the key changes (logout or role switch), and onDispose clears the
    // outgoing store so its ViewModels are destroyed deterministically.
    val store = remember(sessionKey) { ViewModelStore() }
    val owner = remember(sessionKey) {
        object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = store
        }
    }
    DisposableEffect(sessionKey) {
        onDispose { store.clear() }
    }
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        content()
    }
}
