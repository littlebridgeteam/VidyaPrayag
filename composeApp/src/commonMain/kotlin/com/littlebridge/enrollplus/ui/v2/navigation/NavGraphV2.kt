package com.littlebridge.enrollplus.ui.v2.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.littlebridge.enrollplus.ui.v2.components.VBackHandler
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.presentation.OnboardingGate
import com.littlebridge.enrollplus.feature.admin.presentation.OnboardingGateViewModel
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.AdminAuthScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.CommonLandingScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.LanguageSelectionScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.LegalInfoScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.ParentAuthScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.ParentLinkChildScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.SchoolOnboardingScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.auth.TeacherFirstLoginScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.parent.ParentDiscoveryScreen
import com.littlebridge.enrollplus.ui.v2.screens.premium.parent.ParentPortalShell
import com.littlebridge.enrollplus.ui.v2.screens.premium.school.SchoolPortalPremium
import com.littlebridge.enrollplus.ui.v2.screens.premium.teacher.TeacherPortalShell
import com.littlebridge.enrollplus.feature.branding.presentation.BrandingThemeManager
import com.littlebridge.enrollplus.ui.v2.theme.BrandingColorMapper
import com.littlebridge.enrollplus.ui.v2.theme.VMotion
import com.littlebridge.enrollplus.ui.v2.theme.VStatusBarAdapter
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.VThemeDef
import com.littlebridge.enrollplus.ui.v2.theme.VThemeRegistry
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * NavGraphV2 — the single source of nav truth for the `ui/v2` entry experience (PHASE 7).
 *
 * This is the Compose translation of the target entry flow. Navigation is **state-driven** (no flat
 * NavHost): the persisted session decides auth-vs-portal, and small enum state machines own the
 * unauth funnel and the post-login gate. Every transition is explicit and every post-auth jump pops
 * its predecessor so back-press can never return to splash, landing, or an auth screen (LAW 4).
 *
 *   Splash (in App.kt)
 *     ├─ valid session → [AuthedFlow] → role gate → correct portal
 *     └─ no session    → [UnauthFlow] → CommonLanding → Parent/Admin auth
 *
 * Role is the persisted JWT role; [EntryRole] normalizes it (handles ADMIN / SCHOOL_ADMIN / TEACHER
 * / PARENT) so no decision site hardcodes a raw string.
 */
@Composable
fun NavGraphV2(
    role: String?,
    isAuthenticated: Boolean,
    onLogout: () -> Unit,
    deepLink: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val entryRole = EntryRole.from(role)

    // ── Theme resolution from user preference ────────────────────────────────
    // The theme is applied at the NavGraphV2 level so ALL portals (parent,
    // teacher, admin) honour the user's preference — not just the teacher portal.
    val preferenceRepository = koinInject<PreferenceRepository>()
    val brandingThemeManager = koinInject<BrandingThemeManager>()
    val themeMode by preferenceRepository.getThemeMode().collectAsState(initial = "system")
    val customThemeId by preferenceRepository.getCustomThemeId().collectAsState(initial = null)
    val schoolBranding by brandingThemeManager.branding.collectAsState()
    val fontScale by preferenceRepository.getFontScale().collectAsState(initial = 1f)

    val baseDef = resolveThemeDef(themeMode, customThemeId, entryRole, isAuthenticated)
    val resolvedDef = remember(baseDef, schoolBranding) {
        val brandedColors = BrandingColorMapper.apply(baseDef.colors, schoolBranding)
        if (brandedColors !== baseDef.colors) baseDef.copy(colors = brandedColors) else baseDef
    }

    // Fetch school branding when authenticated; clear on logout
    // PRF-004: Guard against auth state flutter — only load if branding is empty.
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            if (schoolBranding == null) brandingThemeManager.loadBranding()
        } else brandingThemeManager.clear()
    }

    // Parse the deep link once when it arrives — but only if we know the user's role.
    // If the user is not yet authenticated (role is Unknown), defer parsing until the
    // role is known. This prevents parseDeepLink from producing Generic targets for
    // paths that should map to role-specific screens.
    var pendingNavigation by remember { mutableStateOf<DeepLinkTarget?>(null) }
    var rawDeepLink by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            rawDeepLink = deepLink
            onDeepLinkConsumed()
        }
    }
    LaunchedEffect(rawDeepLink, entryRole) {
        val link = rawDeepLink
        if (link != null && entryRole != EntryRole.Unknown) {
            pendingNavigation = try {
                val target = parseDeepLink(link, entryRole)
                val targetRole = target.role
                val roleMatches = when {
                    entryRole == targetRole -> true
                    entryRole == EntryRole.SuperAdmin && targetRole == EntryRole.SchoolAdmin -> true
                    else -> false
                }
                if (!roleMatches) {
                    com.littlebridge.enrollplus.util.AppLogger.e("NavGraphV2", "Deep link role mismatch: user=$entryRole target=$targetRole for path '$link' — ignoring")
                    null
                } else {
                    target
                }
            } catch (e: Exception) {
                com.littlebridge.enrollplus.util.AppLogger.e("NavGraphV2", "Failed to parse deep link '$link': ${e.message}", e)
                null
            }
            rawDeepLink = null
        }
    }

    // Smooth crossfade on theme/branding switch (300ms) — avoids a jarring flash.
    AnimatedContent(
        targetState = resolvedDef,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
        label = "theme-switch",
    ) { def ->
        VTheme(themeDef = def, fontScale = fontScale) {
            // Phase 7: adapt system bars (status bar / nav bar) to match the
            // active theme — light icons on dark themes, dark icons on light.
            VStatusBarAdapter(def.colors.isNight)

            if (isAuthenticated) {
                AuthedFlow(
                    role = entryRole,
                    onLogout = onLogout,
                    deepLinkTarget = pendingNavigation,
                    onDeepLinkNavigated = { pendingNavigation = null },
                    modifier = modifier,
                )
            } else {
                UnauthFlow(modifier = modifier)
            }
        }
    }
}

/**
 * Resolves the user's theme preference into a [VThemeDef].
 *
 * - "system" → follows OS dark/light via [isSystemInDarkTheme]
 * - "light" / "dark" → forces that theme
 * - "custom" → uses the stored custom theme id
 *
 * Before the user has chosen (mode == "system" and no prior preference), the
 * role-based default is used as a fallback so admins/teachers still get their
 * canonical warm look on first launch.
 */
@Composable
private fun resolveThemeDef(
    mode: String,
    customId: String?,
    entryRole: EntryRole,
    isAuthenticated: Boolean,
): VThemeDef {
    return when (mode) {
        "light" -> VThemeRegistry.resolve("light")
        "dark" -> VThemeRegistry.resolve("dark")
        "high_contrast" -> VThemeRegistry.resolve("high_contrast")
        "custom" -> VThemeRegistry.resolveInclusive(customId ?: "warm")
        else -> {
            // "system" — follow OS, but use role-based default on first launch
            // (before the user has explicitly chosen a mode).
            val systemDark = isSystemInDarkTheme()
            VThemeRegistry.resolveSystem(systemDark)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Deep-link routing — parse notification deep-link paths into typed targets.
// ─────────────────────────────────────────────────────────────────────────────

sealed class DeepLinkTarget {
    abstract val role: EntryRole

    data class ParentTab(override val role: EntryRole, val tab: String, val overlay: String? = null, val params: Map<String, String> = emptyMap()) : DeepLinkTarget()
    data class TeacherScreen(override val role: EntryRole, val screen: String, val params: Map<String, String> = emptyMap()) : DeepLinkTarget()
    data class SchoolScreen(override val role: EntryRole, val screen: String, val params: Map<String, String> = emptyMap()) : DeepLinkTarget()
    data class AlumniScreen(override val role: EntryRole, val screen: String, val alumniId: String? = null) : DeepLinkTarget()
    data class Messages(override val role: EntryRole, val threadId: String? = null) : DeepLinkTarget()
    data class Generic(override val role: EntryRole, val path: String) : DeepLinkTarget()
}

fun parseDeepLink(path: String, currentRole: EntryRole): DeepLinkTarget {
    // Strip query string before segment splitting — deep links from
    // notifications carry className/section/term params (e.g.
    // "/teacher/report-review?className=8&section=A&term=Term 1").
    val pathOnly = path.substringBefore("?").removeSuffix("/")
    val queryStr = path.substringAfter("?", "")
    val normalized = pathOnly.trim().removePrefix("/")
    val segments = normalized.split("/").filter { it.isNotBlank() }
    if (segments.isEmpty()) return DeepLinkTarget.Generic(currentRole, path)

    return when (segments.first()) {
        "parent" -> {
            val secondSeg = segments.getOrNull(1) ?: "home"
            val thirdSeg = segments.getOrNull(2)
            // Messages deep link with thread ID: /parent/messages/<threadId>
            if (secondSeg == "messages" && thirdSeg != null) {
                if (!isValidUuid(thirdSeg)) return DeepLinkTarget.Generic(currentRole, path)
                return DeepLinkTarget.Messages(EntryRole.Parent, threadId = thirdSeg)
            }
            // Valid bottom-nav tabs in ParentPortalShell.
            val validTabs = setOf("home", "academics", "fees", "conversations", "profile")
            if (secondSeg in validTabs) {
                // Second segment is a tab name; third segment (if any) is an overlay.
                val overlay = when (thirdSeg) {
                    "leave" -> "leave"
                    "messages" -> "messages"
                    "notifications" -> "notifications"
                    "calendar" -> "calendar"
                    "events" -> "events"
                    "transport" -> "transport"
                    "library" -> "library"
                    "scholarships" -> "scholarships"
                    "health" -> "health"
                    "pulse" -> "pulse"
                    "id-card", "digital-id" -> "id-card"
                    "report-card" -> "report-card"
                    "tutor" -> "tutor"
                    "tutor-progress" -> "tutor-progress"
                    "timetable" -> "timetable"
                    "account-settings" -> "account-settings"
                    "fees" -> "fees"
                    "announcements" -> "announcements"
                    "marks" -> "marks"
                    "attendance" -> "attendance"
                    "homework" -> "homework"
                    "quizzes" -> "quizzes"
                    "syllabus" -> "syllabus"
                    "discovery" -> "discovery"
                    "school-detail" -> "school-detail"
                    else -> null
                }
                val reportDraftId = if (thirdSeg == "report-card") segments.getOrNull(3) else null
                val feeId = if (secondSeg == "fees" && thirdSeg != null && thirdSeg != "fees") thirdSeg else null
                val params = when {
                    reportDraftId != null -> mapOf("draftId" to reportDraftId)
                    feeId != null -> mapOf("feeId" to feeId)
                    else -> emptyMap()
                }
                DeepLinkTarget.ParentTab(EntryRole.Parent, secondSeg, overlay, params)
            } else {
                // Second segment is an overlay/screen name, not a bottom-nav tab.
                // Map it to the correct tab + overlay so the LaunchedEffect can navigate.
                val (mappedTab, mappedOverlay) = when (secondSeg) {
                    "announcements" -> "conversations" to "announcements"
                    "transport" -> "home" to "transport"
                    "leave" -> "home" to "leave"
                    "messages" -> "home" to "messages"
                    "notifications" -> "home" to "notifications"
                    "calendar" -> "home" to "calendar"
                    "events" -> "home" to "events"
                    "library" -> "home" to "library"
                    "scholarships" -> "home" to "scholarships"
                    "health" -> "home" to "health"
                    "pulse" -> "home" to "pulse"
                    "id-card", "digital-id" -> "home" to "id-card"
                    "report-card" -> "academics" to "report-card"
                    "tutor" -> "academics" to "tutor"
                    "tutor-progress" -> "academics" to "tutor-progress"
                    "timetable" -> "academics" to "timetable"
                    "link-child" -> "profile" to "link-child"
                    "account-settings" -> "profile" to "account-settings"
                    "discovery" -> "home" to "discovery"
                    "school-detail" -> "home" to "school-detail"
                    else -> "home" to null
                }
                val reportDraftId = segments.getOrNull(2)
                DeepLinkTarget.ParentTab(EntryRole.Parent, mappedTab, mappedOverlay, if (secondSeg == "report-card" && reportDraftId != null) mapOf("draftId" to reportDraftId) else emptyMap())
            }
        }
        "teacher" -> {
            val screen = segments.getOrNull(1) ?: "home"
            // Messages deep link with thread ID: /teacher/messages/<threadId>
            if (screen == "messages" && segments.size > 2) {
                val tid = segments.getOrNull(2)
                if (tid != null && !isValidUuid(tid)) return DeepLinkTarget.Generic(currentRole, path)
                DeepLinkTarget.Messages(EntryRole.Teacher, threadId = tid)
            } else {
                // Parse query params for report-review deep links (className, section, term)
                val params = parseQueryParams(queryStr)
                DeepLinkTarget.TeacherScreen(EntryRole.Teacher, screen, params)
            }
        }
        "school", "admin" -> {
            val screen = segments.getOrNull(1) ?: "home"
            // Messages deep link with thread ID: /school/messages/<threadId>
            if (screen == "messages" && segments.size > 2) {
                val tid = segments.getOrNull(2)
                if (tid != null && !isValidUuid(tid)) return DeepLinkTarget.Generic(currentRole, path)
                DeepLinkTarget.Messages(EntryRole.SchoolAdmin, threadId = tid)
            } else {
                var params = parseQueryParams(queryStr)
                // Capture extra path segments as params for specific screens.
                // /school/pews/student/<code> → params["studentCode"] = <code>
                if (screen == "pews" && segments.size > 3) {
                    params = params + ("studentCode" to segments[3])
                }
                DeepLinkTarget.SchoolScreen(EntryRole.SchoolAdmin, screen, params)
            }
        }
        "alumni" -> {
            val screen = segments.getOrNull(1) ?: "directory"
            val alumniId = segments.getOrNull(2)
            DeepLinkTarget.AlumniScreen(EntryRole.SchoolAdmin, screen, alumniId)
        }
        "announcements" -> {
            val annId = segments.getOrNull(1)
            when (currentRole) {
                EntryRole.Parent -> DeepLinkTarget.ParentTab(EntryRole.Parent, "conversations", "announcements")
                EntryRole.Teacher -> DeepLinkTarget.TeacherScreen(EntryRole.Teacher, "announcements", if (annId != null) mapOf("id" to annId) else emptyMap())
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "announcements", if (annId != null) mapOf("id" to annId) else emptyMap())
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "calendar" -> {
            when (currentRole) {
                EntryRole.Parent -> DeepLinkTarget.ParentTab(EntryRole.Parent, "home", "calendar")
                EntryRole.Teacher -> DeepLinkTarget.TeacherScreen(EntryRole.Teacher, "calendar")
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "calendar")
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "messages" -> {
            val threadId = segments.getOrNull(1)
            if (threadId != null && !isValidUuid(threadId)) return DeepLinkTarget.Generic(currentRole, path)
            when (currentRole) {
                EntryRole.Parent -> DeepLinkTarget.Messages(EntryRole.Parent, threadId)
                EntryRole.Teacher -> DeepLinkTarget.Messages(EntryRole.Teacher, threadId)
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin -> DeepLinkTarget.Messages(currentRole, threadId)
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "fees" -> {
            val feeId = segments.getOrNull(1)
            when (currentRole) {
                EntryRole.Parent -> DeepLinkTarget.ParentTab(EntryRole.Parent, "fees", null, if (feeId != null) mapOf("feeId" to feeId) else emptyMap())
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "fees", if (feeId != null) mapOf("id" to feeId) else emptyMap())
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "leave" -> {
            when (currentRole) {
                EntryRole.Parent -> DeepLinkTarget.ParentTab(EntryRole.Parent, "home", "leave")
                EntryRole.Teacher -> DeepLinkTarget.TeacherScreen(EntryRole.Teacher, "leave-requests")
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "leave-requests")
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "scholarships" -> {
            when (currentRole) {
                EntryRole.Parent -> DeepLinkTarget.ParentTab(EntryRole.Parent, "home", "scholarships")
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "scholarships")
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "link-requests" -> {
            when (currentRole) {
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "link-requests")
                EntryRole.Parent -> DeepLinkTarget.ParentTab(EntryRole.Parent, "profile", "link-child")
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "timetable" -> {
            when (currentRole) {
                EntryRole.Teacher -> DeepLinkTarget.TeacherScreen(EntryRole.Teacher, "timetable")
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "timetable")
                EntryRole.Parent ->
                    DeepLinkTarget.ParentTab(EntryRole.Parent, "home", "timetable")
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "transport" -> {
            when (currentRole) {
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "transport")
                EntryRole.Teacher ->
                    DeepLinkTarget.TeacherScreen(currentRole, "transport")
                else ->
                    DeepLinkTarget.ParentTab(EntryRole.Parent, "home", "transport")
            }
        }
        "report-card" -> {
            when (currentRole) {
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "report-card")
                EntryRole.Teacher ->
                    DeepLinkTarget.TeacherScreen(currentRole, "report-card")
                else -> {
                    val draftId = segments.getOrNull(1)
                    DeepLinkTarget.ParentTab(EntryRole.Parent, "academics", "report-card", if (draftId != null) mapOf("draftId" to draftId) else emptyMap())
                }
            }
        }
        "tutor" -> {
            when (currentRole) {
                EntryRole.Teacher ->
                    DeepLinkTarget.TeacherScreen(currentRole, "tutor")
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "tutor")
                else ->
                    DeepLinkTarget.ParentTab(EntryRole.Parent, "academics", "tutor")
            }
        }
        "library" -> {
            when (currentRole) {
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "library")
                EntryRole.Teacher ->
                    DeepLinkTarget.TeacherScreen(currentRole, "library")
                else ->
                    DeepLinkTarget.ParentTab(EntryRole.Parent, "home", "library")
            }
        }
        "events" -> {
            when (currentRole) {
                EntryRole.Parent ->
                    DeepLinkTarget.ParentTab(EntryRole.Parent, "home", "events")
                EntryRole.Teacher ->
                    DeepLinkTarget.TeacherScreen(currentRole, "events")
                EntryRole.SchoolAdmin, EntryRole.SuperAdmin ->
                    DeepLinkTarget.SchoolScreen(currentRole, "events")
                else ->
                    DeepLinkTarget.Generic(currentRole, path)
            }
        }
        "student" -> {
            // Students access the app through the parent portal.
            // /student/library → parent library overlay
            val screen = segments.getOrNull(1) ?: "library"
            when (currentRole) {
                EntryRole.Parent -> DeepLinkTarget.ParentTab(EntryRole.Parent, "home", screen)
                else -> DeepLinkTarget.Generic(currentRole, path)
            }
        }
        else -> DeepLinkTarget.Generic(currentRole, path)
    }
}

/** Parse a URL query string into a Map. Handles URL-encoded values. */
private fun parseQueryParams(queryStr: String): Map<String, String> {
    if (queryStr.isBlank()) return emptyMap()
    return queryStr.split("&").mapNotNull { pair ->
        val idx = pair.indexOf("=")
        if (idx > 0) {
            val key = pair.substring(0, idx)
            val rawValue = urlDecode(pair.substring(idx + 1))
            val sanitizedValue = rawValue.filter { it.isLetterOrDigit() || it.isWhitespace() || it in ".,-_/" }.take(200)
            key to sanitizedValue
        } else null
    }.toMap()
}

/** Validates that a string is a well-formed UUID (hyphenated or non-hyphenated). DFL-011. */
private fun isValidUuid(s: String): Boolean {
    val uuidRegex = Regex("^[0-9a-fA-F]{8}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{12}$")
    return uuidRegex.matches(s)
}

/** Percent-decodes a URL-encoded string (%XX → byte, + → space). */
private fun urlDecode(s: String): String {
    val sb = StringBuilder(s.length)
    var i = 0
    while (i < s.length) {
        val c = s[i]
        when {
            c == '+' -> { sb.append(' '); i++ }
            c == '%' && i + 2 < s.length -> {
                val hex = s.substring(i + 1, i + 3)
                val code = hex.toIntOrNull(16)
                if (code != null) { sb.append(code.toChar()); i += 3 }
                else { sb.append(c); i++ }
            }
            else -> { sb.append(c); i++ }
        }
    }
    return sb.toString()
}

// ─────────────────────────────────────────────────────────────────────────────
// Role model — one place that turns the persisted string into a typed role.
// ─────────────────────────────────────────────────────────────────────────────

/** Typed app role, parsed once from the persisted JWT role string (LAW: no scattered role literals). */
enum class EntryRole {
    Parent, SchoolAdmin, SuperAdmin, Teacher, Alumni, Unknown;

    companion object {
        fun from(raw: String?): EntryRole = when (raw?.trim()?.uppercase()) {
            "PARENT" -> Parent
            "ADMIN", "SCHOOL_ADMIN", "SCHOOLADMIN" -> SchoolAdmin
            // Audit §3.5: super_admin was previously unmapped → Unknown → Parent
            // portal. It is an operator/admin role, so it lands on the admin surface.
            "SUPER_ADMIN", "SUPERADMIN" -> SuperAdmin
            "TEACHER" -> Teacher
            "ALUMNI" -> Alumni
            else -> Unknown
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Unauthenticated funnel:  CommonLanding → Parent/Admin auth (+ discovery/link/onboard branches)
// ─────────────────────────────────────────────────────────────────────────────

private enum class UnauthRoute { LanguageSelection, Landing, ParentAuth, AdminAuth, Discovery, ParentLinkChild, SchoolOnboarding, Legal }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UnauthFlow(modifier: Modifier = Modifier) {
    val preferenceRepository = koinInject<PreferenceRepository>()
    val hasLanguagePref by preferenceRepository.getLanguagePref()
        .collectAsState(initial = "")

    var route by remember { mutableStateOf(UnauthRoute.Landing) }
    // Which legal/info document the Legal route opens on (Privacy / Terms / Help Desk).
    var legalDoc by remember { mutableStateOf("Privacy") }

    // First-launch gate: if no language preference is set, show the language
    // selection screen before the landing page.
    LaunchedEffect(hasLanguagePref) {
        if (hasLanguagePref.isBlank() && route == UnauthRoute.Landing) {
            route = UnauthRoute.LanguageSelection
        } else if (hasLanguagePref.isNotBlank() && route == UnauthRoute.LanguageSelection) {
            route = UnauthRoute.Landing
        }
    }

    // System back: collapse the funnel toward the landing screen (never exit from a leaf).
    VBackHandler(enabled = route != UnauthRoute.Landing) {
        route = when (route) {
            UnauthRoute.ParentAuth -> UnauthRoute.Landing
            UnauthRoute.AdminAuth -> UnauthRoute.Landing
            UnauthRoute.Discovery -> UnauthRoute.ParentAuth
            UnauthRoute.ParentLinkChild -> UnauthRoute.Discovery
            UnauthRoute.SchoolOnboarding -> UnauthRoute.AdminAuth
            // Legal/Support is a leaf reachable from the landing footer — back returns there.
            UnauthRoute.Legal -> UnauthRoute.Landing
            UnauthRoute.LanguageSelection -> UnauthRoute.Landing
            UnauthRoute.Landing -> UnauthRoute.Landing
        }
    }

    AnimatedContent(
        targetState = route,
        // Funnel screens advance "deeper" → subtle forward horizontal momentum + fade.
        transitionSpec = { VMotion.forwardSlide() },
        label = "unauth-flow",
        modifier = modifier,
    ) { current ->
        when (current) {
            UnauthRoute.LanguageSelection -> LanguageSelectionScreen(
                onLanguageSelected = { route = UnauthRoute.Landing },
                modifier = modifier,
            )
            // The single landing surface for BOTH roles (PHASE 7). Its two role-entry cards are the
            // only auth CTAs: "I'm a Parent" → [onParent] → OTP funnel; "School / Administration" →
            // [onAdmin] → credential funnel (teachers sign in via the Admin path). A tap on any
            // Featured-Institution card or Portal-access row also funnels into the matching auth
            // screen (a school tap leads families into the parent OTP sign-in). Content (hero copy,
            // featured schools, offerings, portals) is CMS-driven inside the screen itself via
            // LandingViewModel + MainViewModel — both fetch in `init`, so no extra wiring is needed
            // here; this site only supplies the navigation callbacks.
            UnauthRoute.Landing -> CommonLandingScreen(
                onParent = { route = UnauthRoute.ParentAuth },
                onAdmin = { route = UnauthRoute.AdminAuth },
                onLegal = { doc ->
                    legalDoc = doc
                    route = UnauthRoute.Legal
                },
            )
            UnauthRoute.ParentAuth -> ParentAuthScreen(
                onAuthSuccess = {},
                onBack = { route = UnauthRoute.Landing },
            )
            UnauthRoute.AdminAuth -> AdminAuthScreen(
                onAuthSuccess = {},
                onBack = { route = UnauthRoute.Landing },
            )
            UnauthRoute.Discovery -> ParentDiscoveryScreen(
                onExit = { route = UnauthRoute.ParentAuth },
                onOpenSchool = { _ -> route = UnauthRoute.ParentLinkChild },
            )
            UnauthRoute.ParentLinkChild -> ParentLinkChildScreen(
                onDone = { route = UnauthRoute.ParentAuth },
                onBack = { route = UnauthRoute.Discovery },
            )
            UnauthRoute.SchoolOnboarding -> SchoolOnboardingScreen(
                onComplete = { route = UnauthRoute.AdminAuth },
                onBack = { route = UnauthRoute.AdminAuth },
            )
            UnauthRoute.Legal -> LegalInfoScreen(
                onBack = { route = UnauthRoute.Landing },
                initial = legalDoc,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Authenticated gate:  role → (child-link | onboarding | first-login) → portal
// ─────────────────────────────────────────────────────────────────────────────

private enum class AuthedRoute { Resolving, ParentLinkChild, SchoolOnboarding, TeacherFirstLogin, Portal }

/**
 * AuthedFlow — runs the one-time post-login gate before handing control to the role portal.
 *
 * Decision inputs:
 *  • PARENT       — lands straight on the portal (child-link is opt-in, not forced).
 *  • SCHOOL_ADMIN — decided by SERVER TRUTH via `GET /api/v1/onboarding/status`
 *                   (see [OnboardingGateViewModel]). NOT the local `profile_completed`
 *                   flag. This fixes the class of bug where a manually-inserted /
 *                   hand-edited admin row reported onboarding as complete while its
 *                   `schools` row / classes did not actually exist — the admin would
 *                   land on an empty dashboard with onboarding wrongly skipped. The
 *                   status endpoint derives completion from real persisted data, and
 *                   also yields the first incomplete step so a partially-onboarded
 *                   admin RESUMES at the right place.
 *  • TEACHER      — `profileCompleted == false` → first-login change-password gate, else portal.
 *
 * The change-password gate (RA-54) is backed by a real `POST /auth/change-password` +
 * `must_change_password` flag.
 */
@Composable
private fun AuthedFlow(
    role: EntryRole,
    onLogout: () -> Unit,
    deepLinkTarget: DeepLinkTarget? = null,
    onDeepLinkNavigated: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val authRepository = koinInject<AuthRepository>()

    var route by remember(role) { mutableStateOf(AuthedRoute.Resolving) }
    // The step the school-onboarding wizard should open on, resolved from the
    // server status (first incomplete step) for a returning/partial admin.
    var onboardingResumeStep by remember(role) { mutableStateOf(com.littlebridge.enrollplus.feature.admin.domain.model.ObStepType.BASIC) }

    // For school roles, the decision is made by the server-truth gate VM below
    // (it sets `route`). The gate is AUTHORITATIVE: OnboardingGateViewModel reads
    // the server /onboarding/status (derived from real persisted school data, not
    // the local profile_completed flag) and yields both the Dashboard/Onboarding
    // decision AND the first incomplete step so a partial/manually-seeded admin
    // RESUMES at the right place instead of being wrongly dropped on an empty
    // dashboard ("shows onboarding completed" bug). For the other roles we resolve
    // locally as before.
    val isSchoolRole = role == EntryRole.SchoolAdmin || role == EntryRole.SuperAdmin

    if (isSchoolRole) {
        val gateVm: OnboardingGateViewModel = org.koin.compose.viewmodel.koinViewModel()
        val gate by gateVm.gate.collectAsStateV2()
        LaunchedEffect(gate) {
            when (val g = gate) {
                is OnboardingGate.Resolving -> route = AuthedRoute.Resolving
                is OnboardingGate.Onboarding -> {
                    onboardingResumeStep = g.resumeStep
                    route = AuthedRoute.SchoolOnboarding
                }
                is OnboardingGate.Dashboard -> route = AuthedRoute.Portal
            }
        }
    } else {
        // Resolve the gate exactly once per authenticated session.
        LaunchedEffect(role) {
            // Local cached flag (set at login from the server's profile_completed).
            // We do NOT default a missing flag to `true` — a missing/false flag
            // means "not completed".
            val localProfileCompleted = runCatching { authRepository.getSession()?.profileCompleted }
                .getOrNull() ?: false
            route = when (role) {
                // RA-S04: a parent is NEVER pushed into the child-link flow after signup/login.
                EntryRole.Parent -> AuthedRoute.Portal
                EntryRole.Teacher -> if (localProfileCompleted) AuthedRoute.Portal else AuthedRoute.TeacherFirstLogin
                // Alumni self-service UI is Phase 2 (deferred). For now alumni
                // land on the parent portal surface — their deep links route to
                // school admin alumni screens when accessed by admins.
                EntryRole.Alumni -> AuthedRoute.Portal
                EntryRole.Unknown -> AuthedRoute.Portal
                else -> AuthedRoute.Portal
            }
        }
    }

    // Inside a portal, back-press is owned by the portal's own tab logic; the gate screens never
    // allow a back path to auth/splash (the session is already established).
    AnimatedContent(
        targetState = route,
        // Gate steps (link-child / onboarding / first-login) read as modal sheets →
        // vertical rise + fade. The brief Resolving frame uses a quiet cross-fade so
        // the common (already-completed) path never shows a directional slide.
        transitionSpec = {
            if (initialState == AuthedRoute.Resolving || targetState == AuthedRoute.Resolving) {
                VMotion.quietFade()
            } else {
                VMotion.modalRise()
            }
        },
        label = "authed-flow",
        modifier = modifier,
    ) { current ->
        when (current) {
            // Brief resolving frame — themed background only, no spinner flash for the common
            // (already-completed) case which resolves on the first composition.
            AuthedRoute.Resolving -> Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            AuthedRoute.ParentLinkChild -> ParentLinkChildScreen(
                onDone = { route = AuthedRoute.Portal },
                onBack = { route = AuthedRoute.Portal },
            )
            AuthedRoute.SchoolOnboarding -> SchoolOnboardingScreen(
                resumeStep = onboardingResumeStep,
                onComplete = { route = AuthedRoute.Portal },
                onBack = { route = AuthedRoute.Portal },
            )
            AuthedRoute.TeacherFirstLogin -> TeacherFirstLoginScreen(
                onDone = { route = AuthedRoute.Portal },
            )
            AuthedRoute.Portal -> RolePortal(
                role = role,
                onLogout = onLogout,
                deepLinkTarget = deepLinkTarget,
                onDeepLinkNavigated = onDeepLinkNavigated,
                modifier = modifier,
            )
        }
    }
}

/** Maps the typed role to its self-contained, tabbed portal (the role's "dashboard"). */
@Composable
private fun RolePortal(
    role: EntryRole,
    onLogout: () -> Unit,
    deepLinkTarget: DeepLinkTarget? = null,
    onDeepLinkNavigated: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when (role) {
        // super_admin currently shares the school-admin operator surface (no
        // dedicated super-admin portal exists yet) — but it is no longer
        // silently dropped into the parent UI (audit §3.5).
        EntryRole.SchoolAdmin, EntryRole.SuperAdmin -> SchoolPortalPremium(
            onLogout = onLogout,
            modifier = modifier,
            deepLinkTarget = deepLinkTarget,
            isDark = isSystemInDarkTheme(),
        )
        EntryRole.Teacher -> TeacherPortalShell(
            onLogout = onLogout,
            modifier = modifier,
            deepLinkTarget = deepLinkTarget,
        )
        EntryRole.Parent -> ParentPortalShell(
            onLogout = onLogout,
            modifier = modifier,
            deepLinkTarget = deepLinkTarget,
        )
        // Authenticated but role unknown → safest default is the parent surface.
        // Alumni also use the parent portal surface until Phase 2 self-service UI ships.
        EntryRole.Unknown -> {
            com.littlebridge.enrollplus.util.AppLogger.e("NavGraphV2", "Unknown role detected — forcing logout")
            LaunchedEffect(Unit) { onLogout() }
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    androidx.compose.material3.Text(
                        "Unrecognised account role. Please log in again.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        EntryRole.Alumni -> {
            LaunchedEffect(Unit) { onLogout() }
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    androidx.compose.material3.Text(
                        "Alumni portal is not yet available. Please log in again.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }

    // Consume the deep link once the portal is composed. The yield ensures
    // the portal's own LaunchedEffect(deepLinkTarget, localDeepLink) runs
    // first — clearing pendingNavigation immediately could null out
    // deepLinkTarget before the portal processes it (CON-003 race fix).
    LaunchedEffect(deepLinkTarget) {
        if (deepLinkTarget != null) {
            kotlinx.coroutines.yield()
            onDeepLinkNavigated()
        }
    }
}
