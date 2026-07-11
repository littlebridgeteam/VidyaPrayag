package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.presentation.InstitutionalProfileState
import com.littlebridge.enrollplus.feature.admin.presentation.InstitutionalProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VProgressRing
import com.littlebridge.enrollplus.ui.v2.components.VThemePicker
import com.littlebridge.enrollplus.ui.v2.components.VLanguagePicker
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * SchoolSettingsScreenV2 — `Admin.tsx → SettingsScreen`, wired to the real
 * [InstitutionalProfileViewModel] (`UserProfileApi` → `GET /api/v1/user/profile`).
 *
 * The institutional-profile health card (completion %, storage usage, public/private
 * visibility) is rendered from live VM state. The remaining static admin settings rows
 * (academic year, fee structure, notifications, data export) have no dedicated backend
 * endpoint of their own yet, so they keep their descriptive copy and are clearly marked
 * "Coming Soon" rather than fabricating data (LAW 6). No MockV2 in production; the three
 * UI states come from [VStateHost].
 */
@Composable
fun SchoolSettingsScreenV2(
    onLogout: () -> Unit = {},
    onOpenTeachers: () -> Unit = {},
    // RA-47 — open the editable institutional-profile (schools row) screen.
    onOpenProfile: () -> Unit = {},
    // VP-CAL — open the real Academic Year management screen.
    onOpenAcademicYear: () -> Unit = {},
    // Transport Management — routes, vehicles, student assignments.
    onOpenTransport: () -> Unit = {},
    // Scholarship Management — schemes, applications & renewals.
    onOpenScholarships: () -> Unit = {},
    // School Branding Kit — colors, logo, subdomain.
    onOpenBranding: () -> Unit = {},
    // ID Card Generation — templates, card generation, PDF export.
    onOpenIdCards: () -> Unit = {},
    // Library Management — catalog, issues, returns, fines.
    onOpenLibrary: () -> Unit = {},
    // Classes & Subjects — consolidated management (classes, subjects, bell schedule, timetable).
    onOpenClassesSubjects: () -> Unit = {},
    // Gamification Management — feature flags, badges, rewards, leaderboard, redemptions, boosts.
    onOpenGamification: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: InstitutionalProfileViewModel = koinViewModel(),
    preferenceRepository: PreferenceRepository = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val themeMode by preferenceRepository.getThemeMode().collectAsState(initial = "system")
    val customThemeId by preferenceRepository.getCustomThemeId().collectAsState(initial = null)
    val localeManager = koinInject<LocaleManager>()
    val currentLocale by localeManager.currentLocale.collectAsState()
    val scope = rememberCoroutineScope()
    SchoolSettingsContent(
        state = state,
        themeMode = themeMode,
        customThemeId = customThemeId,
        currentLocale = currentLocale,
        onLanguageSelect = { lang -> localeManager.setLocale(lang) },
        onThemeSelect = { mode, customId ->
            scope.launch {
                preferenceRepository.setThemeMode(mode)
                preferenceRepository.setCustomThemeId(customId)
            }
        },
        onLogout = onLogout,
        onOpenTeachers = onOpenTeachers,
        onOpenProfile = onOpenProfile,
        onOpenAcademicYear = onOpenAcademicYear,
        onOpenTransport = onOpenTransport,
        onOpenScholarships = onOpenScholarships,
        onOpenBranding = onOpenBranding,
        onOpenIdCards = onOpenIdCards,
        onOpenLibrary = onOpenLibrary,
        onOpenClassesSubjects = onOpenClassesSubjects,
        onOpenGamification = onOpenGamification,
        onRetry = viewModel::load,
        modifier = modifier.statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    )
}

@Composable
private fun SchoolSettingsContent(
    state: InstitutionalProfileState,
    themeMode: String,
    customThemeId: String?,
    currentLocale: String,
    onLanguageSelect: (String) -> Unit,
    onThemeSelect: (String, String?) -> Unit,
    onLogout: () -> Unit,
    onOpenTeachers: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAcademicYear: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenBranding: () -> Unit,
    onOpenIdCards: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenClassesSubjects: () -> Unit,
    onOpenGamification: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Stagger entrance
    val headerAlpha = remember { Animatable(0f) }
    val headerOffset = remember { Animatable(20f) }
    LaunchedEffect(Unit) {
        headerAlpha.snapTo(0f); headerOffset.snapTo(20f)
        launch {
            delay(100)
            headerAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
            headerOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
        }
    }

    VConfirmDialog(
        visible = showLogoutConfirm,
        title = appString(StringKeys.AUTH_LOGOUT),
        message = "You'll be signed out of the admin console and need to sign in again.",
        confirmLabel = appString(StringKeys.AUTH_LOGOUT),
        onConfirm = {
            showLogoutConfirm = false
            onLogout()
        },
        onDismiss = { showLogoutConfirm = false },
        icon = VIcons.AlertTriangle,
    )

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Premium header
        Column(
            modifier = Modifier
                .graphicsLayer(translationY = headerOffset.value)
                .alpha(headerAlpha.value),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(VColors.violet))
                Text(appString(StringKeys.SETTINGS_TITLE), style = VTypography.accentLabel, color = VColors.violet)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VColors.ink)) {
                        append("Settings")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                        append(" & Setup")
                    }
                },
                style = VTypography.h2,
            )
        }

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = false,
            onRetry = onRetry,
            skeleton = { SkeletonList(rows = 6) },
        ) {
            InstitutionalProfileHealthCard(state = state, onClick = onOpenProfile)

            val rows = listOf(
                SettingRow(VIcons.Calendar, "Academic year", "Manage term dates & holidays", false, onClick = onOpenAcademicYear),
                SettingRow(VIcons.BookOpen, "Classes & subjects", "Classes, subjects, bell schedule & timetable", false, onClick = onOpenClassesSubjects),
                SettingRow(VIcons.UsersGroup, "Teacher management", "Add, view & remove teachers", false, onClick = onOpenTeachers),
                SettingRow(VIcons.MapPin, "Transport Management", "Routes, vehicles & student assignments", false, onClick = onOpenTransport),
                SettingRow(VIcons.Sparkles, "Scholarship Management", "Schemes, applications & renewals", false, onClick = onOpenScholarships),
                SettingRow(VIcons.School, "Branding Kit", "Logo, colors & custom subdomain", false, onClick = onOpenBranding),
                SettingRow(VIcons.IdCard, "ID Cards", "Templates, generation & PDF export", false, onClick = onOpenIdCards),
                SettingRow(VIcons.BookOpen, "Library Management", "Catalog, issues, returns & fines", false, onClick = onOpenLibrary),
                SettingRow(VIcons.Sparkles, "Gamification", "Feature flags, badges, rewards, boosts & analytics", false, onClick = onOpenGamification),
                SettingRow(VIcons.Wallet, "Fee structure", "Edit heads & amounts for next cycle", true),
                SettingRow(VIcons.Bell, "Notifications", "Channels & quiet hours", true),
                SettingRow(VIcons.Download, "Data export", "CSV / PDF / UDISE", true),
                SettingRow(
                    VIcons.Chat,
                    "Help & support",
                    "Email support@vidyaprayag.in",
                    false,
                    onClick = {
                        runCatching {
                            uriHandler.openUri(
                                "mailto:support@vidyaprayag.in" +
                                    "?subject=VidyaSetu%20Support",
                            )
                        }
                    },
                ),
                SettingRow(VIcons.Settings, appString(StringKeys.AUTH_LOGOUT), "Sign out of the admin console", false, onClick = { showLogoutConfirm = true }),
            )
            rows.forEachIndexed { idx, row ->
                SettingsCreamCard(
                    onClick = if (row.isComingSoon) null else row.onClick,
                    modifier = Modifier.staggeredItemEntrance(idx, true),
                ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(VColors.violetSoft),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(row.icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(row.title, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                Text(row.sub, style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
                            }
                            if (row.isComingSoon) {
                                Text(
                                    text = "Coming soon",
                                    style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                    color = VColors.ink3,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(VColors.creamDeep)
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                )
                            } else {
                                Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
            }

            // Language picker
            SettingsCreamCard {
                Column {
                    Text(appString(StringKeys.SETTINGS_LANGUAGE), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    Spacer(Modifier.height(10.dp))
                    VLanguagePicker(currentLang = currentLocale, onSelect = onLanguageSelect)
                }
            }

            // Theme picker
            SettingsCreamCard {
                VThemePicker(currentMode = themeMode, currentCustomId = customThemeId, onSelect = onThemeSelect)
            }
        }
    }
}

@Composable
private fun InstitutionalProfileHealthCard(
    state: InstitutionalProfileState,
    onClick: () -> Unit,
) {
    val completionTone = if (state.profileCompletion < 60) VBadgeTone.Warning else VBadgeTone.Success
    val storagePercent = (state.storageUsage * 100f).coerceIn(0f, 100f)
    val visibilityTone = if (state.isPublic) VBadgeTone.Success else VBadgeTone.Neutral
    val visibilityLabel = if (state.isPublic) "Public profile" else "Private profile"
    val profileTitle = state.schoolName.ifBlank { "Institutional profile" }
    val nextStep = when {
        state.profileCompletion >= 90 -> "Profile is ready for families to discover."
        state.profileCompletion >= 60 -> "Add media and details to make it stand out."
        else -> "Complete the essentials to improve trust and discovery."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
    ) {
        // Header section with violetSoft tint
        Column(
            Modifier
                .fillMaxWidth()
                .background(VColors.violetSoft.copy(alpha = 0.4f))
                .padding(16.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(VColors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.School, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(22.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(profileTitle, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    Text(nextStep, style = VTypography.caption, color = VColors.ink3)
                }
                Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VBadge(
                    text = visibilityLabel,
                    tone = visibilityTone,
                    leadingIcon = if (state.isPublic) VIcons.Check else VIcons.Lock,
                )
                if (state.activeTourName.isNotBlank()) {
                    VBadge(text = "Tour live", tone = VBadgeTone.Arctic, leadingIcon = VIcons.Eye)
                }
            }
        }

        // Body section
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VProgressRing(
                    value = state.profileCompletion.toFloat(),
                    size = 72.dp,
                    strokeWidth = 8.dp,
                    tone = completionTone,
                    label = "${state.profileCompletion}%",
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Profile completion", style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                        Text("${state.profileCompletion}%", style = VTypography.caption, color = VColors.ink2)
                    }
                    VProgressBar(value = state.profileCompletion.toFloat(), tone = completionTone, height = 8.dp)
                    Text("School details, visibility, gallery and tour media.", style = VTypography.caption, color = VColors.ink3)
                }
            }

            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(VColors.creamDeep).padding(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(VIcons.Upload, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
                            Text("Media storage", style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                        }
                        Text("${state.storageUsedHuman} / ${state.totalStorageHuman}", style = VTypography.caption, color = VColors.ink2)
                    }
                    VProgressBar(value = storagePercent, tone = VBadgeTone.Arctic, height = 7.dp)
                }
            }

            if (state.learningModel.isNotBlank() || state.primaryLanguage.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (state.learningModel.isNotBlank()) {
                        VBadge(text = state.learningModel, tone = VBadgeTone.Neutral, leadingIcon = VIcons.BookOpen)
                    }
                    if (state.primaryLanguage.isNotBlank()) {
                        VBadge(text = state.primaryLanguage, tone = VBadgeTone.Neutral, leadingIcon = VIcons.Chat)
                    }
                }
            }
        }
    }
}

private data class SettingRow(
    val icon: ImageVector,
    val title: String,
    val sub: String,
    val isComingSoon: Boolean,
    val onClick: (() -> Unit)? = null,
)

// ── Premium shared primitives ─────────────────────────────────────────────────

@Composable
private fun SettingsCreamCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onClick() } else Modifier
            )
            .padding(16.dp),
    ) { content() }
}

@Composable
private fun SettingsStaggeredItem(index: Int, content: @Composable () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(24f) }
    LaunchedEffect(Unit) {
        delay(220 + index * 60L)
        launch { alpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease)) }
        launch { offsetY.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease)) }
    }
    Box(
        modifier = Modifier
            .graphicsLayer(translationY = offsetY.value)
            .alpha(alpha.value),
    ) { content() }
}
