package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherSelfLeaveDto
import com.littlebridge.enrollplus.feature.teacher.presentation.ActionResult
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherProfile
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherProfileActionsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherProfileViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VThemePicker
import com.littlebridge.enrollplus.ui.v2.components.VLanguagePicker
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.util.AnalyticsTracker
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherProfileScreenV2(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    teacherName: String = "",
    unreadCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    onOpenSalary: () -> Unit = {},
    onOpenFeeEscalation: () -> Unit = {},
    profileViewModel: TeacherProfileViewModel = koinViewModel(),
    actionsViewModel: TeacherProfileActionsViewModel = koinViewModel(),
) {
    val profileState by profileViewModel.state.collectAsStateV2()
    val leave by actionsViewModel.leave.collectAsStateV2()
    val applyResult by actionsViewModel.apply.collectAsStateV2()
    val passwordResult by actionsViewModel.password.collectAsStateV2()
    val themeMode by actionsViewModel.themeMode.collectAsStateV2()
    val customThemeId by actionsViewModel.customThemeId.collectAsStateV2()
    val localeManager = koinInject<LocaleManager>()
    val currentLocale by localeManager.currentLocale.collectAsStateV2()

    val headerName = teacherName.ifBlank { profileState.profile?.name ?: "" }

    var showLeaveComposer by remember { mutableStateOf(false) }
    var showPasswordForm by remember { mutableStateOf(false) }
    var confirmLogout by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(profileState.isLoading) {
        if (!profileState.isLoading) isRefreshing = false
    }

    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true; profileViewModel.load() },
        modifier = modifier.fillMaxSize().background(VColors.cream),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = TeacherDockClearance + 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                TeacherPremiumHeader(
                    teacherName = headerName,
                    lead = appString(StringKeys.TC_YOUR),
                    accent = appString(StringKeys.TC_ACCOUNT_ACCENT),
                    unreadCount = unreadCount,
                    onOpenNotifications = onOpenNotifications,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }

            // ── IDENTITY HERO
            item {
                when {
                    profileState.isLoading && profileState.profile == null ->
                        TCard { Box(Modifier.fillMaxWidth().height(96.dp), contentAlignment = Alignment.Center) { TeacherSpinner() } }
                    profileState.profile != null -> {
                        val profile = profileState.profile ?: return@item
                        ModernIdentityHero(profile)
                    }
                    else -> TCard {
                        Column {
                            Text(appString(StringKeys.TC_COULDNT_LOAD_PROFILE), style = VtT.bodyStrong.coloredV(VColors.ink))
                            profileState.error?.let { Spacer(Modifier.height(4.dp)); Text(it, style = VtT.caption.coloredV(VColors.ink3)) }
                            Spacer(Modifier.height(12.dp))
                            VButton(appString(StringKeys.COMMON_BUTTON_TRY_AGAIN), onClick = { profileViewModel.load() }, size = VButtonSize.Sm, tone = VButtonTone.Lavender)
                        }
                    }
                }
            }

            // ── DETAILS
            profileState.profile?.let { p ->
                if (p.email.isNotBlank() || p.phone.isNotBlank() || p.subjects.isNotEmpty() || p.classes.isNotEmpty()) {
                    item { ModernDetailsCard(p) }
                }
            }

            // ── QUICK ACTIONS
            item {
                QuickActionsRow(
                    onOpenSalary = onOpenSalary,
                    onOpenFeeEscalation = onOpenFeeEscalation,
                )
            }

            // ── TIME OFF (leave)
            item {
                ModernSectionHeader(
                    title = appString(StringKeys.TC_SEC_TIME_OFF),
                    icon = VIcons.Calendar,
                    tint = VColors.gold,
                )
                Spacer(Modifier.height(4.dp))
                LeaveCard(
                    leave = leave,
                    applyResult = applyResult,
                    showComposer = showLeaveComposer,
                    onToggleComposer = {
                        showLeaveComposer = !showLeaveComposer
                        if (!showLeaveComposer) actionsViewModel.clearApplyResult()
                    },
                    onApply = { from, to, reason -> actionsViewModel.applyLeave(from, to, reason) },
                    onApplied = {
                        showLeaveComposer = false
                        actionsViewModel.clearApplyResult()
                    },
                    onRetry = { actionsViewModel.loadLeave() },
                )
            }

            // ── SECURITY (password)
            item {
                ModernSectionHeader(
                    title = appString(StringKeys.TC_SEC_SECURITY),
                    icon = VIcons.Lock,
                    tint = VColors.violet,
                )
                Spacer(Modifier.height(4.dp))
                PasswordCard(
                    result = passwordResult,
                    expanded = showPasswordForm,
                    onToggle = {
                        showPasswordForm = !showPasswordForm
                        if (!showPasswordForm) actionsViewModel.clearPasswordResult()
                    },
                    onSubmit = { old, new, confirm -> actionsViewModel.changePassword(old, new, confirm) },
                    onDone = {
                        showPasswordForm = false
                        actionsViewModel.clearPasswordResult()
                    },
                )
            }

            // ── PREFERENCES
            item {
                ModernSectionHeader(
                    title = appString(StringKeys.TC_SEC_PREFERENCES),
                    icon = VIcons.Settings,
                    tint = VColors.sky,
                )
                Spacer(Modifier.height(4.dp))
                TCard {
                    Column {
                        Text(appString(StringKeys.TC_APPEARANCE), style = VtT.bodyStrong.coloredV(VColors.ink))
                        Spacer(Modifier.height(10.dp))
                        VThemePicker(
                            currentMode = themeMode,
                            currentCustomId = customThemeId,
                            onSelect = { mode, customId ->
                                AnalyticsTracker.event("vp_teacher_theme_change", mapOf("theme" to mode))
                                actionsViewModel.setThemeMode(mode)
                                actionsViewModel.setCustomThemeId(customId)
                            },
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
                        Spacer(Modifier.height(16.dp))
                        Text(appString(StringKeys.TC_LANGUAGE), style = VtT.bodyStrong.coloredV(VColors.ink))
                        Spacer(Modifier.height(10.dp))
                        VLanguagePicker(
                            currentLang = currentLocale,
                            onSelect = { lang -> localeManager.setLocale(lang) },
                        )
                    }
                }
            }

            // ── FOOTER
            item {
                Column {
                    VButton(
                        text = appString(StringKeys.TC_LOG_OUT),
                        onClick = { confirmLogout = true },
                        variant = VButtonVariant.Destructive,
                        full = true,
                        leading = { Icon(VIcons.ArrowLeft, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    )
                    Spacer(Modifier.height(10.dp))
                    profileState.profile?.let {
                        Text(
                            appString(StringKeys.TC_SIGNED_IN_AS, "username" to it.username),
                            style = VtT.caption.coloredV(VColors.ink3),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }
        }
    }

    if (confirmLogout) {
        TeacherConfirmSheet(
            title = appString(StringKeys.TC_LOG_OUT_Q),
            body = appString(StringKeys.TC_LOG_OUT_DESC),
            confirmLabel = appString(StringKeys.TC_LOG_OUT),
            destructive = true,
            onConfirm = { confirmLogout = false; onLogout() },
            onDismiss = { confirmLogout = false },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MODERN SECTION HEADER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ModernSectionHeader(title: String, icon: ImageVector, tint: Color) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Text(
            title.uppercase(),
            style = VtT.label.coloredV(VColors.ink3).copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, fontSize = 11.sp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MODERN IDENTITY HERO
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ModernIdentityHero(p: TeacherProfile) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(VColors.violet, VColors.violetHover, VColors.violet.copy(alpha = 0.85f)),
                ),
            )
            .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar with animated border
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(3.dp, VColors.white.copy(alpha = 0.3f), CircleShape)
                        .background(VColors.white.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        p.name.take(1).uppercase().ifBlank { "?" },
                        style = VtT.h2.coloredV(VColors.white).copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
                    )
                }
                Spacer(Modifier.width(18.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        p.name.ifBlank { "\u2014" },
                        style = VtT.h3.coloredV(VColors.white).copy(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
                    )
                    Spacer(Modifier.height(3.dp))
                    Text("@${p.username}", style = VtT.body.coloredV(VColors.white.copy(alpha = 0.8f)))
                    if (p.schoolName.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Icon(VIcons.School, contentDescription = null, tint = VColors.white.copy(alpha = 0.65f), modifier = Modifier.size(13.dp))
                            Text(p.schoolName, style = VtT.caption.coloredV(VColors.white.copy(alpha = 0.8f)))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Stats strip
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VColors.white.copy(alpha = 0.1f))
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HeroStat(p.subjects.size.toString(), appString(StringKeys.TC_STAT_SUBJECTS), Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(28.dp).background(VColors.white.copy(alpha = 0.2f)))
                HeroStat(p.classes.size.toString(), appString(StringKeys.TC_STAT_CLASSES), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = VtT.h2.coloredV(VColors.white).copy(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp))
        Spacer(Modifier.height(2.dp))
        Text(
            label.uppercase(),
            style = VtT.label.coloredV(VColors.white.copy(alpha = 0.7f)).copy(fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp, fontSize = 10.sp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MODERN DETAILS CARD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ModernDetailsCard(p: TeacherProfile) {
    TCard {
        Column {
            if (p.email.isNotBlank() || p.phone.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (p.email.isNotBlank()) ModernContactLine(VIcons.Mail, p.email, VColors.sky)
                    if (p.phone.isNotBlank()) ModernContactLine(VIcons.Phone, p.phone, VColors.mint)
                }
            }
            if (p.subjects.isNotEmpty()) {
                if (p.email.isNotBlank() || p.phone.isNotBlank()) Spacer(Modifier.height(16.dp))
                TEyebrow(appString(StringKeys.TC_SUBJECTS))
                Spacer(Modifier.height(8.dp))
                ModernChipFlow(p.subjects) { s -> vtSubjectColor(s) }
            }
            if (p.classes.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                TEyebrow(appString(StringKeys.TC_CLASSES))
                Spacer(Modifier.height(8.dp))
                ModernChipFlow(p.classes) { VColors.violet }
            }
        }
    }
}

@Composable
private fun ModernContactLine(icon: ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Text(text, style = VtT.body.coloredV(VColors.ink2))
    }
}

@Composable
private fun ModernChipFlow(items: List<String>, tint: (String) -> Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { label ->
                    val t = tint(label)
                    TPill(label, t.copy(alpha = 0.1f), t)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QUICK ACTIONS ROW
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsRow(onOpenSalary: () -> Unit, onOpenFeeEscalation: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickActionCard(
            title = "Salary",
            subtitle = "View history",
            icon = VIcons.Wallet,
            tint = VColors.violet,
            onClick = onOpenSalary,
            modifier = Modifier.weight(1f),
        )
        QuickActionCard(
            title = "Fee Escalation",
            subtitle = "Remind parents",
            icon = VIcons.ClipboardList,
            tint = VColors.coral,
            onClick = onOpenFeeEscalation,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(),
        label = "scale",
    )

    TCard(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        ),
    ) {
        Column {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(tint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, style = VtT.bodyStrong.coloredV(VColors.ink))
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = VtT.caption.coloredV(VColors.ink3))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LEAVE CARD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LeaveCard(
    leave: com.littlebridge.enrollplus.feature.teacher.presentation.TeacherLeaveUiState,
    applyResult: ActionResult,
    showComposer: Boolean,
    onToggleComposer: () -> Unit,
    onApply: (from: String, to: String, reason: String) -> Unit,
    onApplied: () -> Unit,
    onRetry: () -> Unit,
) {
    LaunchedEffect(applyResult) {
        if (applyResult is ActionResult.Success) onApplied()
    }
    TCard {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(appString(StringKeys.TC_MY_LEAVE), style = VtT.bodyStrong.coloredV(VColors.ink))
                    if (leave.pendingCount > 0) {
                        Spacer(Modifier.height(2.dp))
                        Text(appString(StringKeys.TC_PENDING_COUNT, "count" to leave.pendingCount.toString()), style = VtT.caption.coloredV(VColors.gold))
                    }
                }
                VButton(
                    text = if (showComposer) appString(StringKeys.COMMON_BUTTON_CLOSE) else appString(StringKeys.TC_APPLY),
                    onClick = onToggleComposer,
                    size = VButtonSize.Sm,
                    variant = if (showComposer) VButtonVariant.Ghost else VButtonVariant.Secondary,
                    tone = VButtonTone.Lavender,
                    leading = if (showComposer) null else {
                        { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(15.dp)) }
                    },
                )
            }

            AnimatedVisibility(
                visible = showComposer,
                enter = expandVertically(tween(300, easing = FastOutSlowInEasing)) + fadeIn(tween(200)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(150)),
            ) {
                LeaveComposer(applyResult = applyResult, onApply = onApply)
            }

            Spacer(Modifier.height(12.dp))
            when {
                leave.isLoading && leave.requests.isEmpty() -> Box(Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.Center) { TeacherSpinner(24.dp) }
                leave.error != null && leave.requests.isEmpty() -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(leave.error ?: "", style = VtT.caption.coloredV(VColors.ink3), modifier = Modifier.weight(1f))
                    VButton(appString(StringKeys.COMMON_BUTTON_RETRY), onClick = onRetry, size = VButtonSize.Sm, variant = VButtonVariant.Ghost)
                }
                leave.requests.isEmpty() -> Text(appString(StringKeys.TC_NO_LEAVE_REQUESTS), style = VtT.body.coloredV(VColors.ink3))
                else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    leave.requests.forEach { ModernLeaveRow(it) }
                }
            }
        }
    }
}

@Composable
private fun LeaveComposer(
    applyResult: ActionResult,
    onApply: (from: String, to: String, reason: String) -> Unit,
) {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    val inFlight = applyResult is ActionResult.InFlight

    Column(Modifier.fillMaxWidth().wrapContentHeight().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VDatePicker(value = from, onValueChange = { from = it }, label = appString(StringKeys.TC_FROM), enabled = !inFlight)
        VDatePicker(value = to, onValueChange = { to = it }, label = appString(StringKeys.TC_TO), enabled = !inFlight)
        VInput(
            value = reason, onValueChange = { reason = it }, label = appString(StringKeys.TC_REASON),
            placeholder = appString(StringKeys.TC_WHY_APPLYING), singleLine = false, enabled = !inFlight,
            modifier = Modifier.heightIn(min = 120.dp),
        )
        if (applyResult is ActionResult.Failure) {
            Text(applyResult.message, style = VtT.caption.coloredV(VColors.error))
        }
        VButton(
            text = appString(StringKeys.TC_SUBMIT_REQUEST), onClick = { onApply(from, to, reason) }, full = true,
            tone = VButtonTone.Lavender, stateful = true, loading = inFlight,
            enabled = from.isNotBlank() && to.isNotBlank() && reason.isNotBlank() && !inFlight,
        )
    }
}

@Composable
private fun ModernLeaveRow(leave: TeacherSelfLeaveDto) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(VColors.surfaceTint).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                if (leave.dateFrom == leave.dateTo) prettyDateShort(leave.dateFrom)
                else "${prettyDateShort(leave.dateFrom)} \u2013 ${prettyDateShort(leave.dateTo)}",
                style = VtT.bodyStrong.coloredV(VColors.ink),
            )
            if (leave.reason.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(leave.reason, style = VtT.caption.coloredV(VColors.ink3))
            }
        }
        LeaveStatusPill(leave.status)
    }
}

@Composable
private fun LeaveStatusPill(status: String) {
    val (bg, fg) = when (status.lowercase()) {
        "approved" -> VColors.success.copy(alpha = 0.14f) to VColors.success
        "rejected" -> VColors.error.copy(alpha = 0.12f) to VColors.error
        else -> VColors.gold.copy(alpha = 0.14f) to VColors.gold
    }
    TPill(status.uppercase(), bg, fg)
}

// ─────────────────────────────────────────────────────────────────────────────
// PASSWORD CARD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PasswordCard(
    result: ActionResult,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSubmit: (old: String, new: String, confirm: String) -> Unit,
    onDone: () -> Unit,
) {
    LaunchedEffect(result) {
        if (result is ActionResult.Success) onDone()
    }
    TCard {
        Column {
            Row(
                Modifier.fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggle,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TIconDisc(VIcons.Lock, VColors.ink, VColors.ink.copy(alpha = 0.08f))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f).fillMaxWidth()) {
                    Text(appString(StringKeys.TC_PASSWORD), style = VtT.bodyStrong.coloredV(VColors.ink), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    Text(appString(StringKeys.TC_CHANGE_PASSWORD), style = VtT.caption.coloredV(VColors.ink3), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 90f else 0f,
                    animationSpec = spring(stiffness = 300f),
                    label = "chevron",
                )
                Icon(
                    VIcons.ChevronRight,
                    contentDescription = null, tint = VColors.ink3,
                    modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = rotation },
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(300, easing = FastOutSlowInEasing)) + fadeIn(tween(200)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(150)),
            ) {
                PasswordForm(result = result, onSubmit = onSubmit)
            }
        }
    }
}

@Composable
private fun PasswordForm(
    result: ActionResult,
    onSubmit: (old: String, new: String, confirm: String) -> Unit,
) {
    var old by remember { mutableStateOf("") }
    var new0 by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var reveal by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    val inFlight = result is ActionResult.InFlight

    val newError = if (submitted && new0.isBlank()) "New password is required"
    else if (submitted && new0.length < 8) "New password must be at least 8 characters"
    else null
    val confirmError = if (submitted && confirm.isBlank()) "Please confirm your new password"
    else if (submitted && new0.isNotBlank() && new0 != confirm) "Passwords don\u2019t match"
    else null

    Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VInput(
            value = old, onValueChange = { old = it }, label = appString(StringKeys.TC_CURRENT_PASSWORD),
            isPassword = true, passwordVisible = reveal, enabled = !inFlight,
            keyboardType = KeyboardType.Password, modifier = Modifier.heightIn(min = 56.dp),
        )
        VInput(
            value = new0, onValueChange = { new0 = it }, label = appString(StringKeys.TC_NEW_PASSWORD),
            hint = appString(StringKeys.TC_AT_LEAST_), isPassword = true, passwordVisible = reveal, enabled = !inFlight,
            keyboardType = KeyboardType.Password, isError = newError != null, errorText = newError,
            modifier = Modifier.heightIn(min = 56.dp),
        )
        VInput(
            value = confirm, onValueChange = { confirm = it }, label = appString(StringKeys.TC_CONFIRM_NEW_PASSWORD),
            isPassword = true, passwordVisible = reveal, enabled = !inFlight,
            keyboardType = KeyboardType.Password, isError = confirmError != null, errorText = confirmError,
            modifier = Modifier.heightIn(min = 56.dp),
            trailing = {
                val ix = remember { MutableInteractionSource() }
                Icon(
                    VIcons.Eye, contentDescription = appString(StringKeys.TC_TOGGLE_VISIBILITY), tint = VColors.ink3,
                    modifier = Modifier.size(18.dp).clickable(interactionSource = ix, indication = null) { reveal = !reveal },
                )
            },
        )
        if (result is ActionResult.Failure) {
            Text(result.message, style = VtT.caption.coloredV(VColors.error))
        }
        VButton(
            text = appString(StringKeys.TC_UPDATE_PASSWORD),
            onClick = { submitted = true; onSubmit(old, new0, confirm) },
            full = true, tone = VButtonTone.Navy, stateful = true, loading = inFlight, enabled = !inFlight,
        )
    }
}
