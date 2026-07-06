package com.littlebridge.enrollplus

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
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.littlebridge.enrollplus.core.notification.NotificationFeedRepository
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.presentation.MainViewModel
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.navigation.AuthNavGraph
import com.littlebridge.enrollplus.ui.navigation.DeepLinkTarget
import com.littlebridge.enrollplus.ui.navigation.TeacherDeepLinkTab
import com.littlebridge.enrollplus.ui.navigation.parseDeepLink
import com.littlebridge.enrollplus.ui.screens.admin.AdminPortalScreen
import com.littlebridge.enrollplus.ui.screens.parent.ParentDeepLinkParser
import com.littlebridge.enrollplus.ui.screens.parent.ParentPortalScreen
import com.littlebridge.enrollplus.ui.screens.teacher.TeacherPortalScreen
import com.littlebridge.enrollplus.ui.screens.teacher.TeacherTab
import com.littlebridge.enrollplus.util.Config
import io.ktor.client.*
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

/**
 * Application entrypoint — minimal shell after full UI nuke.
 * All screens, components, tokens, and ViewModels have been deleted.
 * Rebuild from scratch.
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

        val httpClient = koinInject<HttpClient>()
        val platform = koinInject<Platform>()

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

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBF8F4))) {
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
            val isAuthed = !authState.token.isNullOrBlank()
            val isTeacher = authState.role?.equals("teacher", ignoreCase = true) == true
            val isParent = authState.role?.equals("parent", ignoreCase = true) == true
            val isAdmin = authState.role?.let { it.equals("admin", ignoreCase = true) || it.equals("school_admin", ignoreCase = true) } == true

            // Parse deep link once when it arrives
            val deepLinkTarget = remember(deepLink) {
                parseDeepLink(deepLink, authState.role)
            }

            val parentDeepLink = remember(deepLink) {
                ParentDeepLinkParser.parse(deepLink)
            }

            val notificationRepo = koinInject<NotificationFeedRepository>()

            // Auto-mark-read via push ref (refType + refId from notification tap)
            LaunchedEffect(pushRefType, pushRefId) {
                if (!pushRefType.isNullOrBlank() && !pushRefId.isNullOrBlank()) {
                    val token = authState.token
                    if (!token.isNullOrBlank()) {
                        runCatching { notificationRepo.markNotificationByRef(token, pushRefType, pushRefId) }
                        onPushRefConsumed()
                    }
                }
            }

            // Consume deep link after routing
            LaunchedEffect(deepLinkTarget) {
                if (deepLinkTarget !is DeepLinkTarget.None) {
                    onDeepLinkConsumed()
                }
            }

            if (isAuthed && isAdmin) {
                val homeViewModel: com.littlebridge.enrollplus.presentation.admin.AdminHomeViewModel = koinViewModel()
                val peopleViewModel: com.littlebridge.enrollplus.presentation.admin.AdminPeopleViewModel = koinViewModel()
                val recordsViewModel: com.littlebridge.enrollplus.presentation.admin.AdminRecordsViewModel = koinViewModel()
                val commsViewModel: com.littlebridge.enrollplus.presentation.admin.AdminCommsViewModel = koinViewModel()
                val settingsViewModel: com.littlebridge.enrollplus.presentation.admin.AdminSettingsViewModel = koinViewModel()
                AdminPortalScreen(
                    homeViewModel = homeViewModel,
                    peopleViewModel = peopleViewModel,
                    recordsViewModel = recordsViewModel,
                    commsViewModel = commsViewModel,
                    settingsViewModel = settingsViewModel,
                    onLogout = { viewModel.logout() },
                )
            } else if (isAuthed && isTeacher) {
                val teacherViewModel: TeacherViewModel = koinViewModel()
                val initialTab = when (deepLinkTarget) {
                    is DeepLinkTarget.TeacherTab -> when (deepLinkTarget.tab) {
                        TeacherDeepLinkTab.Home -> TeacherTab.Home
                        TeacherDeepLinkTab.Update -> TeacherTab.Update
                        TeacherDeepLinkTab.Classes -> TeacherTab.Classes
                        TeacherDeepLinkTab.Timetable -> TeacherTab.Timetable
                        TeacherDeepLinkTab.Profile -> TeacherTab.Profile
                    }
                    else -> null
                }
                TeacherPortalScreen(
                    viewModel = teacherViewModel,
                    onLogout = { viewModel.logout() },
                    initialTab = initialTab,
                )
            } else if (isAuthed && isParent) {
                val parentViewModel: ParentViewModel = koinViewModel()
                LaunchedEffect(authState.token) {
                    authState.token?.let { parentViewModel.setToken(it) }
                }
                ParentPortalScreen(
                    viewModel = parentViewModel,
                    onLogout = { viewModel.logout() },
                    initialDeepLink = parentDeepLink,
                )
            } else {
                AuthNavGraph(
                    authViewModel = authViewModel,
                    onAuthSuccess = { },
                )
            }
        }
    }
}

