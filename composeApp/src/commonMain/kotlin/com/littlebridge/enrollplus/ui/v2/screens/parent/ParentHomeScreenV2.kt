package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardAlertDto
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.presentation.AttendanceDayState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsParentActionDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsParentNudgeDto
import com.littlebridge.enrollplus.feature.pews.presentation.ParentNudgeViewModel
import com.littlebridge.enrollplus.presentation.PermissionViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.platform.rememberNotificationPermissionLauncher
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

/**
 * ParentHomeScreenV2 — the rebuilt parent Home tab.
 *
 * Premium, calm, and information-first: a warm cream canvas with white cards,
 * a clear child hero below the shared header, and quick access to everything a
 * parent cares about. No sparklines, no bouncing springs, no gamification rings,
 * no cheap effects. Real data from the dashboard, academics, and announcement VMs.
 */
@Composable
fun ParentHomeScreenV2(
    modifier: Modifier = Modifier,
    onDiscoverSchools: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenFees: () -> Unit = {},
    onOpenAcademics: () -> Unit = {},
    onOpenMessages: () -> Unit = {},
    onOpenPulse: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenTutor: () -> Unit = {},
    onOpenTutorProgress: () -> Unit = {},
    onOpenScholarships: () -> Unit = {},
    onOpenIdCard: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    viewModel: ParentDashboardViewModel = koinViewModel(),
    academicsViewModel: ParentAcademicsViewModel = koinViewModel(),
    announcementsViewModel: ParentAnnouncementViewModel = koinViewModel(),
    nudgeViewModel: ParentNudgeViewModel = koinViewModel(),
    permissionVm: PermissionViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val academics by academicsViewModel.state.collectAsStateV2()
    val announcements by announcementsViewModel.state.collectAsStateV2()
    val nudgeState by nudgeViewModel.state.collectAsStateV2()

    val activeChildId = state.selectedChild?.id

    LaunchedEffect(activeChildId) {
        activeChildId?.let {
            academicsViewModel.loadDailySummary(it)
            nudgeViewModel.load(it)
        }
    }

    // Live minute clock for the time-aware greeting and period status.
    LaunchedEffect(Unit) {
        permissionVm.checkNotificationPermission()
        while (true) {
            delay(60_000L)
            viewModel.refreshLiveClock()
        }
    }

    val showRationale by permissionVm.showNotificationRationale.collectAsStateV2()
    val launchPermission by permissionVm.launchPermissionRequest.collectAsStateV2()
    val permissionLauncher = rememberNotificationPermissionLauncher { granted ->
        permissionVm.onPermissionResult(granted)
    }
    LaunchedEffect(launchPermission) {
        if (launchPermission) {
            permissionVm.consumeLaunchPermissionRequest()
            permissionLauncher.launch()
        }
    }

    ParentHomeContent(
        state = state,
        academics = academics,
        announcements = announcements,
        nudge = nudgeState.nudge?.takeIf { nudgeState.visible },
        onRetry = viewModel::load,
        onDiscoverSchools = onDiscoverSchools,
        onOpenFees = onOpenFees,
        onOpenAcademics = onOpenAcademics,
        onOpenPulse = onOpenPulse,
        onOpenTransport = onOpenTransport,
        onOpenTutor = onOpenTutor,
        onOpenTutorProgress = onOpenTutorProgress,
        onOpenScholarships = onOpenScholarships,
        onOpenIdCard = onOpenIdCard,
        onOpenLibrary = onOpenLibrary,
        onOpenEvents = onOpenEvents,
        onOpenMessages = onOpenMessages,
        onNudgeAction = { action ->
            nudgeViewModel.acknowledgeNudge(activeChildId)
            val target = action.deepLink.lowercase()
            if (target.contains("message") || target.contains("teacher") || target.contains("chat")) {
                onOpenMessages()
            } else {
                onOpenAcademics()
            }
        },
        onNudgeDismiss = { nudgeViewModel.acknowledgeNudge(activeChildId) },
        modifier = modifier,
    )

    VConfirmDialog(
        visible = showRationale,
        title = appString(StringKeys.PH_STAY_INFORMED),
        message = appString(StringKeys.PH_STAY_INFORMED_MSG),
        confirmLabel = appString(StringKeys.PH_ENABLE),
        onConfirm = permissionVm::requestNotificationPermission,
        onDismiss = permissionVm::declineNotifications,
        cancelLabel = appString(StringKeys.PH_NOT_NOW),
        icon = VIcons.Bell,
    )
}

@Composable
private fun ParentHomeContent(
    state: ParentDashboardState,
    academics: ParentAcademicsState,
    announcements: ParentAnnouncementState,
    nudge: PewsParentNudgeDto?,
    onRetry: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenPulse: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenTutorProgress: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenMessages: () -> Unit,
    onNudgeAction: (PewsParentActionDto) -> Unit,
    onNudgeDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .padding(bottom = 140.dp),
    ) {
        when {
            state.isLoading && state.selectedChild == null -> HomeSkeleton()
            state.error != null && state.selectedChild == null -> HomeError(message = state.error ?: "", onRetry = onRetry)
            state.children.isEmpty() -> HomeEmpty(onDiscoverSchools = onDiscoverSchools, onLinkChild = onOpenIdCard)
            else -> HomeLoaded(
                state = state,
                academics = academics,
                announcements = announcements,
                nudge = nudge,
                onOpenFees = onOpenFees,
                onOpenAcademics = onOpenAcademics,
                onOpenPulse = onOpenPulse,
                onOpenTransport = onOpenTransport,
                onOpenTutor = onOpenTutor,
                onOpenTutorProgress = onOpenTutorProgress,
                onOpenScholarships = onOpenScholarships,
                onOpenIdCard = onOpenIdCard,
                onOpenLibrary = onOpenLibrary,
                onOpenEvents = onOpenEvents,
                onOpenMessages = onOpenMessages,
                onNudgeAction = onNudgeAction,
                onNudgeDismiss = onNudgeDismiss,
            )
        }
    }
}

@Composable
private fun HomeSkeleton() {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkeletonCard(height = 160.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonCard(modifier = Modifier.weight(1f), height = 90.dp)
            SkeletonCard(modifier = Modifier.weight(1f), height = 90.dp)
        }
        SkeletonCard(height = 120.dp)
        SkeletonCard(height = 180.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) { SkeletonCard(modifier = Modifier.weight(1f), height = 96.dp) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) { SkeletonCard(modifier = Modifier.weight(1f), height = 96.dp) }
        }
    }
}

@Composable
private fun SkeletonCard(height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(VShapes.lg)
            .background(VColors.lineSoft),
    )
}

@Composable
private fun HomeError(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(VColors.errorSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.AlertTriangle,
                contentDescription = null,
                tint = VColors.error,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = appString(StringKeys.COMMON_ERROR_GENERIC),
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = VTypography.body,
            color = VColors.ink2,
        )
        Spacer(Modifier.height(20.dp))
        VButton(
            text = appString(StringKeys.COMMON_BUTTON_RETRY),
            onClick = onRetry,
        )
    }
}

@Composable
private fun HomeEmpty(onDiscoverSchools: () -> Unit, onLinkChild: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.User,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = appString(StringKeys.PH_NO_CHILD_LINKED),
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = appString(StringKeys.PH_NO_CHILD_LINKED_DESC),
            style = VTypography.body,
            color = VColors.ink2,
        )
        Spacer(Modifier.height(24.dp))
        VButton(
            text = "Link a child",
            onClick = onLinkChild,
        )
        Spacer(Modifier.height(10.dp))
        VButton(
            text = "Discover schools",
            onClick = onDiscoverSchools,
            variant = com.littlebridge.enrollplus.ui.v2.components.VButtonVariant.Secondary,
        )
    }
}

@Composable
private fun HomeLoaded(
    state: ParentDashboardState,
    academics: ParentAcademicsState,
    announcements: ParentAnnouncementState,
    nudge: PewsParentNudgeDto?,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenPulse: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenTutorProgress: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenMessages: () -> Unit,
    onNudgeAction: (PewsParentActionDto) -> Unit,
    onNudgeDismiss: () -> Unit,
) {
    val child = state.selectedChild
    val className = state.timetable?.className.orEmpty()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeroCard(
            child = child,
            className = className,
            attendance = state.attendance,
            latestMark = state.latestMark,
            fees = state.fees,
            todayState = state.today.state,
            statusLabel = state.today.label,
        )

        if (state.alerts.isNotEmpty()) {
            AlertStrip(alerts = state.alerts)
        }

        if (nudge != null) {
            NudgeCard(nudge = nudge, onAction = onNudgeAction, onDismiss = onNudgeDismiss)
        }

        LearningSummaryCard(
            summary = academics.dailySummary,
            isLoading = academics.dailySummaryLoading,
        )

        InsightsRow(
            attendance = state.attendance,
            latestMark = state.latestMark,
            fees = state.fees,
            onOpenAcademics = onOpenAcademics,
            onOpenFees = onOpenFees,
        )

        DayTimeline(
            periods = state.todayPeriods,
            modifier = Modifier.fillMaxWidth(),
        )

        QuickAccessGrid(
            onOpenFees = onOpenFees,
            onOpenMessages = onOpenMessages,
            onOpenAcademics = onOpenAcademics,
            onOpenTransport = onOpenTransport,
            onOpenPulse = onOpenPulse,
            onOpenLibrary = onOpenLibrary,
            onOpenScholarships = onOpenScholarships,
            onOpenIdCard = onOpenIdCard,
            onOpenEvents = onOpenEvents,
            onOpenTutor = onOpenTutor,
        )

        AnnouncementsPreview(
            announcements = announcements.announcements,
            isLoading = announcements.isLoading,
        )

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun HeroCard(
    child: DashboardChildSummary?,
    className: String,
    attendance: ParentAttendanceData?,
    latestMark: ParentMarkDto?,
    fees: FeeData?,
    todayState: AttendanceDayState,
    statusLabel: String,
) {
    val name = child?.name?.ifBlank { "Your child" } ?: "Your child"
    val status = statusVisualFor(todayState)

    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                VAvatar(name = name, src = child?.profilePic, size = 64.dp)
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(VColors.surfaceCard)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(status.color),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = VTypography.h2,
                    color = VColors.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = className.ifBlank { "Class not set" },
                    style = VTypography.bodySmall,
                    color = VColors.ink2,
                )
                Spacer(Modifier.height(4.dp))
                StatusChip(text = status.label, color = status.color, softColor = status.softColor)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatPill(
                label = "Attendance",
                value = "${attendance?.attendanceRate ?: 0}%",
                color = VColors.mint,
                softColor = VColors.mintSoft,
                modifier = Modifier.weight(1f),
            )
            StatPill(
                label = "Marks",
                value = markDisplay(latestMark),
                color = VColors.sky,
                softColor = VColors.skySoft,
                modifier = Modifier.weight(1f),
            )
            StatPill(
                label = "Fees",
                value = fees?.outstandingFees?.ifBlank { "—" } ?: "—",
                color = if ((fees?.overdueCount ?: 0) > 0) VColors.coral else VColors.gold,
                softColor = if ((fees?.overdueCount ?: 0) > 0) VColors.coralSoft else VColors.goldSoft,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatusChip(text: String, color: Color, softColor: Color) {
    if (text.isBlank()) return
    Box(
        Modifier
            .clip(VShapes.full)
            .background(softColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = VTypography.label.copy(fontWeight = FontWeight.SemiBold),
            color = color,
        )
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color, softColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(VShapes.md)
            .background(softColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = value,
            style = VTypography.h3,
            color = color,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun AlertStrip(alerts: List<DashboardAlertDto>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        alerts.take(3).forEach { alert ->
            val (color, softColor) = when (alert.type.uppercase()) {
                "CRITICAL" -> VColors.coral to VColors.coralSoft
                "WARNING" -> VColors.gold to VColors.goldSoft
                else -> VColors.sky to VColors.skySoft
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(VShapes.md)
                    .background(softColor)
                    .border(1.dp, color.copy(alpha = 0.2f), VShapes.md)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = alert.title,
                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = VColors.ink,
                    )
                    if (alert.value.isNotBlank()) {
                        Text(
                            text = alert.value,
                            style = VTypography.caption,
                            color = VColors.ink2,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NudgeCard(
    nudge: PewsParentNudgeDto,
    onAction: (PewsParentActionDto) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.violetSoft)
            .border(1.dp, VColors.violet.copy(alpha = 0.15f), VShapes.lg)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = VIcons.Heart,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = nudge.headline,
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = nudge.message,
            style = VTypography.bodySmall,
            color = VColors.ink2,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            nudge.actions.firstOrNull()?.let { action ->
                VButton(
                    text = action.label,
                    onClick = { onAction(action) },
                    size = com.littlebridge.enrollplus.ui.v2.components.VButtonSize.Sm,
                )
            }
            VButton(
                text = "Dismiss",
                onClick = onDismiss,
                variant = com.littlebridge.enrollplus.ui.v2.components.VButtonVariant.Secondary,
                size = com.littlebridge.enrollplus.ui.v2.components.VButtonSize.Sm,
            )
        }
    }
}

@Composable
private fun LearningSummaryCard(
    summary: ParentDailySummaryData?,
    isLoading: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(VShapes.md)
                    .background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = VIcons.School,
                    contentDescription = null,
                    tint = VColors.violet,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Today's learning",
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.ink,
                )
                Text(
                    text = summary?.date?.ifBlank { "Today" } ?: "Today",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
            if (summary?.entries?.isNotEmpty() == true) {
                Icon(
                    imageVector = if (expanded) VIcons.ChevronUp else VIcons.ChevronDown,
                    contentDescription = null,
                    tint = VColors.ink3,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { expanded = !expanded },
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(48.dp).clip(VShapes.md).background(VColors.lineSoft))
        } else if (summary == null || summary.entries.isEmpty()) {
            Text(
                text = "No learning summary available yet",
                style = VTypography.bodySmall,
                color = VColors.ink2,
            )
        } else {
            Text(
                text = summary.aiSummary ?: summary.entries.joinToString(", ") { it.subject },
                style = VTypography.bodySmall,
                color = VColors.ink2,
            )

            AnimatedContent(
                targetState = expanded,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "learningExpand",
            ) { show ->
                if (show) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Spacer(Modifier.height(4.dp))
                        summary.entries.forEach { entry ->
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(VColors.violet)
                                        .padding(top = 6.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = entry.subject,
                                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                                        color = VColors.ink,
                                    )
                                    if (entry.summaryText.isNotBlank()) {
                                        Text(
                                            text = entry.summaryText,
                                            style = VTypography.bodySmall,
                                            color = VColors.ink2,
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.height(0.dp))
                }
            }
        }
    }
}

@Composable
private fun InsightsRow(
    attendance: ParentAttendanceData?,
    latestMark: ParentMarkDto?,
    fees: FeeData?,
    onOpenAcademics: () -> Unit,
    onOpenFees: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Quick insights",
            style = VTypography.body.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InsightCard(
                icon = VIcons.School,
                label = "Attendance",
                value = "${attendance?.attendanceRate ?: 0}%",
                subtext = "${attendance?.presentDays ?: 0}/${attendance?.totalDays ?: 0} days",
                color = VColors.mint,
                softColor = VColors.mintSoft,
                modifier = Modifier.weight(1f),
                onClick = onOpenAcademics,
            )
            InsightCard(
                icon = VIcons.Star,
                label = "Latest marks",
                value = markDisplay(latestMark),
                subtext = latestMark?.subject?.ifBlank { "—" } ?: "—",
                color = VColors.sky,
                softColor = VColors.skySoft,
                modifier = Modifier.weight(1f),
                onClick = onOpenAcademics,
            )
        }
        InsightCard(
            icon = VIcons.Wallet,
            label = "Fee status",
            value = fees?.outstandingFees?.ifBlank { "₹0" } ?: "₹0",
            subtext = if ((fees?.overdueCount ?: 0) > 0) "Overdue" else "Up to date",
            color = if ((fees?.overdueCount ?: 0) > 0) VColors.coral else VColors.gold,
            softColor = if ((fees?.overdueCount ?: 0) > 0) VColors.coralSoft else VColors.goldSoft,
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenFees,
        )
    }
}

@Composable
private fun InsightCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtext: String,
    color: Color,
    softColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(VShapes.md)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.md)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(VShapes.md)
                .background(softColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                style = VTypography.caption,
                color = VColors.ink3,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = VTypography.h3,
                color = color,
            )
            Text(
                text = subtext,
                style = VTypography.caption,
                color = VColors.ink2,
            )
        }
        Icon(
            imageVector = VIcons.ChevronRight,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun DayTimeline(periods: List<LivePeriod>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Today's schedule",
            style = VTypography.body.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        if (periods.isEmpty()) {
            Text(
                text = "No timetable available for today",
                style = VTypography.bodySmall,
                color = VColors.ink2,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                periods.forEach { period ->
                    val (color, softColor) = when (period.relation) {
                        0 -> VColors.violet to VColors.violetSoft
                        -1 -> VColors.mint to VColors.mintSoft
                        else -> VColors.gold to VColors.goldSoft
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(VShapes.md)
                            .background(VColors.surfaceCard)
                            .border(1.dp, if (period.relation == 0) color.copy(alpha = 0.3f) else VColors.line, VShapes.md)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = period.subject,
                                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = VColors.ink,
                            )
                            if (period.teacherName.isNotBlank()) {
                                Text(
                                    text = period.teacherName,
                                    style = VTypography.caption,
                                    color = VColors.ink2,
                                )
                            }
                        }
                        Text(
                            text = "${period.startTime} – ${period.endTime}",
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickAccessGrid(
    onOpenFees: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenPulse: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenTutor: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Quick access",
            style = VTypography.body.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 2,
        ) {
            val items = listOf(
                "Fees" to VIcons.Wallet to onOpenFees,
                "Messages" to VIcons.Chat to onOpenMessages,
                "Attendance" to VIcons.School to onOpenAcademics,
                "Transport" to VIcons.MapPin to onOpenTransport,
                "Health" to VIcons.Heart to onOpenPulse,
                "Library" to VIcons.BookOpen to onOpenLibrary,
                "Scholarships" to VIcons.Sparkles to onOpenScholarships,
                "ID Card" to VIcons.IdCard to onOpenIdCard,
                "Events" to VIcons.Calendar to onOpenEvents,
                "AI Tutor" to VIcons.Target to onOpenTutor,
            )
            items.forEach { (pair, onClick) ->
                val (label, icon) = pair
                QuickTile(label = label, icon = icon, onClick = onClick)
            }
        }
    }
}

@Composable
private fun QuickTile(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clip(VShapes.md)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.md)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            Modifier
                .size(38.dp)
                .clip(VShapes.md)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = label,
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
    }
}

@Composable
private fun AnnouncementsPreview(
    announcements: List<com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement>,
    isLoading: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Announcements",
            style = VTypography.body.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
        )
        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(80.dp).clip(VShapes.md).background(VColors.lineSoft))
        } else if (announcements.isEmpty()) {
            Text(
                text = "No announcements yet",
                style = VTypography.bodySmall,
                color = VColors.ink2,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                announcements.take(5).forEach { announcement ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(VShapes.md)
                            .background(VColors.surfaceCard)
                            .border(1.dp, VColors.line, VShapes.md)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(38.dp)
                                .clip(VShapes.md)
                                .background(VColors.skySoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = VIcons.Megaphone,
                                contentDescription = null,
                                tint = VColors.sky,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = announcement.title,
                                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = VColors.ink,
                                maxLines = 1,
                            )
                            if (announcement.description.isNotBlank()) {
                                Text(
                                    text = announcement.description,
                                    style = VTypography.caption,
                                    color = VColors.ink2,
                                    maxLines = 2,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private data class StatusVisual(val label: String, val color: Color, val softColor: Color)

private fun statusVisualFor(state: AttendanceDayState): StatusVisual = when (state) {
    AttendanceDayState.Present -> StatusVisual("Present", VColors.mint, VColors.mintSoft)
    AttendanceDayState.Absent -> StatusVisual("Absent", VColors.coral, VColors.coralSoft)
    AttendanceDayState.Late -> StatusVisual("Late", VColors.gold, VColors.goldSoft)
    AttendanceDayState.Holiday -> StatusVisual("Holiday", VColors.sky, VColors.skySoft)
    AttendanceDayState.Sunday -> StatusVisual("Sunday", VColors.sky, VColors.skySoft)
    AttendanceDayState.Vacation -> StatusVisual("Vacation", VColors.sky, VColors.skySoft)
    AttendanceDayState.NoData -> StatusVisual("No update yet", VColors.ink3, VColors.lineSoft)
}

private fun markDisplay(mark: ParentMarkDto?): String {
    if (mark == null) return "—"
    val pct = if (mark.maxMarks > 0) ((mark.marks ?: 0.0) / mark.maxMarks * 100).roundToInt() else 0
    return "$pct%"
}

