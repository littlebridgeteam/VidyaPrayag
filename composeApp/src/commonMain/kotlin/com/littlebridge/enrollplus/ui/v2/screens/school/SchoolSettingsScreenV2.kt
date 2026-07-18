package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.presentation.BrandingPhotosState
import com.littlebridge.enrollplus.feature.admin.presentation.BrandingPhotosViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.InstitutionalProfileState
import com.littlebridge.enrollplus.feature.admin.presentation.InstitutionalProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VProgressRing
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VThemePicker
import com.littlebridge.enrollplus.ui.v2.components.VLanguagePicker
import com.littlebridge.enrollplus.feature.i18n.domain.model.SUPPORTED_LANGUAGES
import com.littlebridge.enrollplus.ui.v2.theme.VThemeRegistry
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.util.AnalyticsTracker
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolSettingsScreenV2(
    onLogout: () -> Unit = {},
    onOpenTeachers: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenAcademicYear: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenScholarships: () -> Unit = {},
    onOpenBranding: () -> Unit = {},
    onOpenIdCards: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenClassesSubjects: () -> Unit = {},
    onOpenGamification: () -> Unit = {},
    onOpenFeeSalary: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: InstitutionalProfileViewModel = koinViewModel(),
    brandingViewModel: BrandingPhotosViewModel = koinViewModel(),
    preferenceRepository: PreferenceRepository = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val brandingState by brandingViewModel.state.collectAsStateV2()
    val themeMode by preferenceRepository.getThemeMode().collectAsState(initial = "system")
    val customThemeId by preferenceRepository.getCustomThemeId().collectAsState(initial = null)
    val localeManager = koinInject<LocaleManager>()
    val currentLocale by localeManager.currentLocale.collectAsStateV2()
    val scope = rememberCoroutineScope()
    SchoolSettingsContent(
        state = state,
        themeMode = themeMode,
        customThemeId = customThemeId,
        currentLocale = currentLocale,
        brandingState = brandingState,
        onLanguageSelect = { lang -> localeManager.setLocale(lang) },
        onThemeSelect = { mode, customId ->
            AnalyticsTracker.event("vp_admin_theme_change", mapOf("theme" to mode))
            scope.launch {
                preferenceRepository.setThemeMode(mode)
                preferenceRepository.setCustomThemeId(customId)
            }
        },
        onLogout = onLogout,
        onOpenProfile = onOpenProfile,
        onOpenAcademicYear = onOpenAcademicYear,
        onOpenTransport = onOpenTransport,
        onOpenScholarships = onOpenScholarships,
        onOpenBranding = onOpenBranding,
        onOpenIdCards = onOpenIdCards,
        onOpenLibrary = onOpenLibrary,
        onOpenClassesSubjects = onOpenClassesSubjects,
        onOpenGamification = onOpenGamification,
        onOpenFeeSalary = onOpenFeeSalary,
        onRetry = viewModel::load,
        modifier = modifier.statusBarsPadding().imePadding(),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Telegram-style settings content — collapsing header + sticky toolbar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SchoolSettingsContent(
    state: InstitutionalProfileState,
    brandingState: BrandingPhotosState,
    themeMode: String,
    customThemeId: String?,
    currentLocale: String,
    brandingSummaryText: String = run {
        val items = listOfNotNull(
            "Logo".takeIf { brandingState.schoolLogoUrl.isNotBlank() },
            "Cover".takeIf { brandingState.coverImageUrl.isNotBlank() },
            "Photo".takeIf { brandingState.adminProfilePicUrl.isNotBlank() },
            "Gallery (${brandingState.galleryPhotos.size})".takeIf { brandingState.galleryPhotos.isNotEmpty() },
        )
        items.joinToString(" \u00B7 ").ifBlank { "Logo, cover, gallery & profile picture" }
    },
    onLanguageSelect: (String) -> Unit,
    onThemeSelect: (String, String?) -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAcademicYear: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenBranding: () -> Unit,
    onOpenIdCards: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenClassesSubjects: () -> Unit,
    onOpenGamification: () -> Unit,
    onOpenFeeSalary: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showAppearanceSheet by remember { mutableStateOf(false) }

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

    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) isRefreshing = false
    }

    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true; onRetry() },
        modifier = modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()

        val collapsedFraction by remember {
            derivedStateOf {
                val firstItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
                if (firstItem != null && firstItem.index == 0) {
                    val scrolled = (-firstItem.offset).toFloat() / firstItem.size.toFloat()
                    scrolled.coerceIn(0f, 1f)
                } else {
                    1f
                }
            }
        }
        val showToolbar by remember {
            derivedStateOf {
                collapsedFraction > 0.85f
            }
        }
        val toolbarAlpha by animateFloatAsState(
            targetValue = if (showToolbar) 1f else 0f,
            animationSpec = tween(150),
        )

        Box(Modifier.fillMaxSize()) {
            VStateHost(
                loading = state.isLoading,
                error = state.errorMessage,
                isEmpty = false,
                onRetry = onRetry,
                skeleton = { SkeletonList(rows = 6) },
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // ── Profile header (collapses on scroll) ──
                    item(key = "profile") {
                        ProfileHeader(
                            state = state,
                            brandingState = brandingState,
                            collapseFraction = collapsedFraction,
                            onClick = onOpenProfile,
                        )
                    }

                    // ── ACCOUNT ──
                    item(key = "sec_account") { SettingsSectionHeader("ACCOUNT") }
                    item(key = "row_edit_profile") {
                        TelegramRow(VIcons.User, "Edit Profile", "School details, contact & address", onClick = onOpenProfile)
                    }
                    item(key = "row_branding") {
                        TelegramRow(VIcons.School, "Branding & Photos", brandingSummaryText, onClick = onOpenBranding)
                    }

                    // ── SCHOOL MANAGEMENT ──
                    item(key = "sec_school") { SettingsSectionHeader("SCHOOL MANAGEMENT") }
                    item(key = "row_academic") {
                        TelegramRow(VIcons.Calendar, "Academic Year", "Manage term dates & holidays", onClick = onOpenAcademicYear)
                    }
                    item(key = "row_classes") {
                        TelegramRow(VIcons.BookOpen, "Classes & Subjects", "Classes, subjects, bell schedule & timetable", onClick = onOpenClassesSubjects)
                    }
                    item(key = "row_transport") {
                        TelegramRow(VIcons.MapPin, "Transport Management", "Routes, vehicles & student assignments", onClick = onOpenTransport)
                    }
                    item(key = "row_scholarships") {
                        TelegramRow(VIcons.Sparkles, "Scholarship Management", "Schemes, applications & renewals", onClick = onOpenScholarships)
                    }

                    // ── TOOLS ──
                    item(key = "sec_tools") { SettingsSectionHeader("TOOLS") }
                    item(key = "row_idcards") {
                        TelegramRow(VIcons.IdCard, "ID Cards", "Templates, generation & PDF export", onClick = onOpenIdCards)
                    }
                    item(key = "row_library") {
                        TelegramRow(VIcons.BookOpen, "Library Management", "Catalog, issues, returns & fines", onClick = onOpenLibrary)
                    }
                    item(key = "row_gamification") {
                        TelegramRow(VIcons.Sparkles, "Gamification", "Badges, rewards, boosts & analytics", onClick = onOpenGamification)
                    }
                    item(key = "row_fee") {
                        TelegramRow(VIcons.Wallet, "Fee & Salary", "Fee structures, payment tracking & salary", onClick = onOpenFeeSalary)
                    }

                    // ── PREFERENCES ──
                    item(key = "sec_prefs") { SettingsSectionHeader("PREFERENCES") }
                    item(key = "row_language") {
                        val selected = SUPPORTED_LANGUAGES.find { it.code == currentLocale } ?: SUPPORTED_LANGUAGES.first()
                        TelegramRow(
                            VIcons.Chat,
                            appString(StringKeys.SETTINGS_LANGUAGE),
                            "${selected.nativeName} \u00B7 ${selected.englishName}",
                            onClick = { showLanguageSheet = true },
                        )
                    }
                    item(key = "row_appearance") {
                        val label = when (themeMode) {
                            "system" -> "System"
                            "light" -> "Light"
                            "dark" -> "Dark"
                            "custom" -> VThemeRegistry.allThemes.find { it.id == customThemeId }?.displayName ?: "Custom"
                            else -> "System"
                        }
                        TelegramRow(
                            VIcons.Settings,
                            appString(StringKeys.SETTINGS_THEME),
                            label,
                            onClick = { showAppearanceSheet = true },
                        )
                    }
                    item(key = "row_notifications") {
                        TelegramRow(VIcons.Bell, "Notifications", "Channels & quiet hours", isComingSoon = true)
                    }
                    item(key = "row_export") {
                        TelegramRow(VIcons.Download, "Data Export", "CSV / PDF / UDISE", isComingSoon = true)
                    }

                    // ── SUPPORT ──
                    item(key = "sec_support") { SettingsSectionHeader("SUPPORT") }
                    item(key = "row_help") {
                        TelegramRow(
                            VIcons.Chat,
                            "Help & Support",
                            "Email support@vidyaprayag.in",
                            onClick = {
                                runCatching {
                                    uriHandler.openUri("mailto:support@vidyaprayag.in?subject=VidyaSetu%20Support")
                                }
                            },
                        )
                    }

                    // ── Logout ──
                    item(key = "spacer_logout") { Spacer(Modifier.height(8.dp)) }
                    item(key = "row_logout") {
                        LogoutRow(onClick = { showLogoutConfirm = true })
                    }
                }
            }

            // ── Sticky toolbar (fades in when header is collapsed) ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(toolbarAlpha)
                    .background(VColors.surfaceCard.copy(alpha = toolbarAlpha.coerceAtLeast(0.9f)))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .align(Alignment.TopCenter),
            ) {
                Text(
                    text = appString(StringKeys.SETTINGS_TITLE),
                    style = VTypography.h3.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
            }
        }
    }

    // ── Bottom sheets ──
    if (showLanguageSheet) {
        VBottomSheet(visible = showLanguageSheet, onDismiss = { showLanguageSheet = false }) {
            VBottomSheetHeader(title = appString(StringKeys.SETTINGS_LANGUAGE), onClose = { showLanguageSheet = false })
            Spacer(Modifier.height(16.dp))
            VLanguagePicker(
                currentLang = currentLocale,
                onSelect = { lang ->
                    onLanguageSelect(lang)
                    showLanguageSheet = false
                },
            )
        }
    }

    if (showAppearanceSheet) {
        VBottomSheet(visible = showAppearanceSheet, onDismiss = { showAppearanceSheet = false }) {
            VBottomSheetHeader(title = appString(StringKeys.SETTINGS_THEME), onClose = { showAppearanceSheet = false })
            Spacer(Modifier.height(16.dp))
            VThemePicker(
                currentMode = themeMode,
                currentCustomId = customThemeId,
                onSelect = { mode, customId ->
                    onThemeSelect(mode, customId)
                    showAppearanceSheet = false
                },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile Header — large avatar, school name, completion badges
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeader(
    state: InstitutionalProfileState,
    brandingState: BrandingPhotosState,
    collapseFraction: Float,
    onClick: () -> Unit,
) {
    val completionTone = if (state.profileCompletion < 60) VBadgeTone.Warning else VBadgeTone.Success
    val visibilityTone = if (state.isPublic) VBadgeTone.Success else VBadgeTone.Neutral
    val visibilityLabel = if (state.isPublic) "Public" else "Private"

    val avatarSize = lerp(96f, 40f, collapseFraction).dp
    val coverHeight = lerp(180f, 0f, collapseFraction).dp
    val titleSize = lerp(22f, 16f, collapseFraction).sp
    val badgesVisible = collapseFraction < 0.6f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Cover image (full width) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverHeight)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(VColors.violetSoft),
        ) {
            if (brandingState.coverImageUrl.isNotBlank()) {
                AsyncImage(
                    model = brandingState.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // ── Overlapping avatar + info ──
        Box(modifier = Modifier.fillMaxWidth()) {
            // Circular avatar overlapping the cover image
            Box(
                modifier = Modifier
                    .offset(y = -(avatarSize / 2))
                    .size(avatarSize)
                    .align(Alignment.TopCenter)
                    .clip(CircleShape)
                    .background(VColors.surfaceCard)
                    .border(3.dp, VColors.surfaceCard, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (brandingState.schoolLogoUrl.isNotBlank()) {
                    AsyncImage(
                        model = brandingState.schoolLogoUrl,
                        contentDescription = state.schoolName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                    )
                } else {
                    Icon(VIcons.School, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(avatarSize * 0.45f))
                }
            }

            // Content below the avatar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = avatarSize / 2)
                    .padding(top = lerp(12f, 4f, collapseFraction).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // School name
                Text(
                    text = state.schoolName.ifBlank { "Your School" },
                    style = VTypography.h3.copy(fontWeight = FontWeight.Bold, fontSize = titleSize),
                    color = VColors.ink,
                )

                if (collapseFraction < 0.3f) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Admin account",
                        style = VTypography.body,
                        color = VColors.ink3,
                    )
                }

                if (badgesVisible) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VBadge(text = "${state.profileCompletion}% complete", tone = completionTone)
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
            }
        }

        Spacer(Modifier.height(lerp(0f, 8f, collapseFraction).dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section header — small uppercase label
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
        color = VColors.ink3,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Telegram-style setting row — icon circle, title, subtitle, chevron
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TelegramRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    isComingSoon: Boolean = false,
) {
    val clickable = onClick != null && !isComingSoon
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .then(
                if (clickable) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick!!,
                ) else Modifier
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
                maxLines = 1,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = VTypography.caption,
                    color = VColors.ink3,
                    maxLines = 1,
                )
            }
        }

        if (isComingSoon) {
            Text(
                text = "Coming soon",
                style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                color = VColors.ink3,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(VColors.creamDeep)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        } else if (clickable) {
            Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
        }
    }
    TelegramDivider()
}

@Composable
private fun TelegramDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 68.dp)
            .height(0.5.dp)
            .background(VColors.line),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Logout row — danger/red styled
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LogoutRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VColors.coralSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Settings, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(18.dp))
        }
        Text(
            text = appString(StringKeys.AUTH_LOGOUT),
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.coral,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)
