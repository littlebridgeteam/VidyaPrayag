package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.pews.presentation.ParentNudgeViewModel
import com.littlebridge.enrollplus.presentation.PermissionViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

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
    onOpenScholarships: () -> Unit = {},
    onOpenIdCard: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    viewModel: ParentDashboardViewModel = koinViewModel(),
    academicsViewModel: ParentAcademicsViewModel = koinViewModel(),
    announcementsViewModel: ParentAnnouncementViewModel = koinViewModel(),
    nudgeViewModel: ParentNudgeViewModel = koinViewModel(),
    permissionVm: PermissionViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val academics by academicsViewModel.state.collectAsStateV2()
    val announcements by announcementsViewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) {
        permissionVm.checkNotificationPermission()
        if (state.children.isEmpty()) viewModel.load()
    }

    LaunchedEffect(state.selectedChild?.id) {
        state.selectedChild?.id?.let {
            academicsViewModel.selectChild(it)
            academicsViewModel.loadDailySummary()
        }
    }

    ParentHomeContent(
        state = state,
        academics = academics,
        announcements = announcements,
        onRetry = viewModel::load,
        onSelectChild = viewModel::selectChild,
        onOpenNotifications = onOpenNotifications,
        onOpenFees = onOpenFees,
        onOpenAcademics = onOpenAcademics,
        onOpenMessages = onOpenMessages,
        onDiscoverSchools = onDiscoverSchools,
        unreadNotificationsCount = unreadNotificationsCount,
        modifier = modifier,
    )
}

@Composable
private fun ParentHomeContent(
    state: ParentDashboardState,
    academics: ParentAcademicsState,
    announcements: com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementState,
    onRetry: () -> Unit,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
    onDiscoverSchools: () -> Unit,
    unreadNotificationsCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp),
    ) {
        ParentPortalHeader(
            label = "Home",
            children = state.children,
            selectedChild = state.selectedChild,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            unreadNotificationsCount = unreadNotificationsCount,
        )

        when {
            state.isLoading && state.children.isEmpty() -> HomeSkeleton()
            state.error != null && state.children.isEmpty() -> HomeError(message = state.error ?: "", onRetry = onRetry)
            state.children.isEmpty() -> HomeEmpty(onDiscoverSchools = onDiscoverSchools)
            else -> HomeLoaded(
                state = state,
                academics = academics,
                announcements = announcements,
                onOpenFees = onOpenFees,
                onOpenAcademics = onOpenAcademics,
                onOpenMessages = onOpenMessages,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SKELETON / ERROR / EMPTY
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                        .clip(VShapes.lg)
                        .background(VColors.lineSoft),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
    }
}

@Composable
private fun HomeError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(VColors.errorSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.AlertTriangle,
                contentDescription = null,
                tint = VColors.error,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "Couldn't load home",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Text(
            text = message,
            style = VTypography.caption,
            color = VColors.ink2,
        )
        Text(
            text = "Retry",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.violet,
            modifier = Modifier.clickable { onRetry() },
        )
    }
}

@Composable
private fun HomeEmpty(onDiscoverSchools: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.HomePremium,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "No child linked yet",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Text(
            text = "Link a child to see their day, attendance, and school updates.",
            style = VTypography.caption,
            color = VColors.ink2,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Discover schools",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.violet,
            modifier = Modifier.clickable { onDiscoverSchools() },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LOADED — premium SaaS dashboard layout
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeLoaded(
    state: ParentDashboardState,
    academics: ParentAcademicsState,
    announcements: com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementState,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
) {
    val child = state.selectedChild
    val childName = child?.name?.ifBlank { null } ?: "Your Child"
    val attendanceRate = state.attendance?.attendanceRate
    val feesDue = state.fees?.outstandingFees
    val markPct = state.latestMark?.let { m ->
        if (m.maxMarks > 0) ((m.marks ?: 0.0) / m.maxMarks * 100).roundToInt() else null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Hero snapshot card (matches AcademicSnapshotCard design) ──
        HomeHeroCard(
            childName = childName,
            level = child?.currentLevel ?: 0,
            attendanceStatus = child?.attendanceStatus ?: "",
            attendanceRate = attendanceRate,
            markPct = markPct,
            feesDue = feesDue,
        )

        // ── Quick actions (2x2 grid like Academics Overview) ──
        SectionLabel("Quick Actions")
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickActionTile(
                icon = VIcons.WalletPremium,
                iconColor = VColors.violet,
                title = "Pay Fees",
                onClick = onOpenFees,
                modifier = Modifier.weight(1f),
            )
            QuickActionTile(
                icon = VIcons.Academic,
                iconColor = VColors.gold,
                title = "Academics",
                onClick = onOpenAcademics,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickActionTile(
                icon = VIcons.ChatPremium,
                iconColor = VColors.coral,
                title = "Messages",
                onClick = onOpenMessages,
                modifier = Modifier.weight(1f),
            )
            QuickActionTile(
                icon = VIcons.Calendar,
                iconColor = VColors.sky,
                title = "Timetable",
                onClick = onOpenAcademics,
                modifier = Modifier.weight(1f),
            )
        }

        // ── Today's learning ──
        TodayLearningCard(
            childName = childName,
            summary = academics.dailySummary,
            isLoading = academics.dailySummaryLoading,
        )

        // ── Announcements ──
        AnnouncementsCard(
            announcements = announcements.announcements,
            isLoading = announcements.isLoading,
        )

        Spacer(Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HERO CARD — identity + inline 3-column stats (matches AcademicSnapshotCard)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeHeroCard(
    childName: String,
    level: Int,
    attendanceStatus: String,
    attendanceRate: Int?,
    markPct: Int?,
    feesDue: String?,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(20.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SimpleAvatar(name = childName, size = 44.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    childName,
                    style = VTypography.h3.copy(fontSize = 16.sp),
                    color = VColors.ink,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                if (level > 0) {
                    MiniBadge(text = "Level $level", color = VColors.violet, bg = VColors.violetSoft)
                } else {
                    Text("Daily overview", style = VTypography.caption, color = VColors.ink3)
                }
            }
            val isPresent = attendanceStatus.lowercase() == "present"
            Box(
                modifier = Modifier
                    .clip(VShapes.full)
                    .background(if (isPresent) VColors.successSoft else VColors.creamDeep)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = if (isPresent) "Present" else "—",
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                    color = if (isPresent) VColors.success else VColors.ink3,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = VColors.lineSoft)
        Spacer(Modifier.height(14.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            InlineStat(
                value = attendanceRate?.let { "$it%" } ?: "—",
                label = "Attendance",
                modifier = Modifier.weight(1f),
            )
            InlineStat(
                value = markPct?.let { "$it%" } ?: "—",
                label = "Latest",
                modifier = Modifier.weight(1f),
            )
            InlineStat(
                value = feesDue ?: "—",
                label = "Fees Due",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SimpleAvatar(name: String, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(VColors.violetSoft),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = VTypography.h3.copy(fontSize = 16.sp),
            color = VColors.violet,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun MiniBadge(text: String, color: Color, bg: Color) {
    Box(
        modifier = Modifier
            .clip(VShapes.sm)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
            color = color,
        )
    }
}

@Composable
private fun InlineStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value,
            style = VTypography.h3.copy(fontSize = 16.sp),
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTypography.caption.copy(fontSize = 10.sp), color = VColors.ink3)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED COMPONENTS — match Academics screen exactly
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = VTypography.label,
        color = VColors.ink,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(VShapes.sm).background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Text(
            title,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TODAY'S LEARNING — premium card with divider + entries
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TodayLearningCard(
    childName: String,
    summary: ParentDailySummaryData?,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Today's Learning",
                style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
            )
            if (summary != null && summary.entries.isNotEmpty()) {
                Text(
                    text = "${summary.entries.size} classes",
                    style = VTypography.caption.copy(fontSize = 11.sp),
                    color = VColors.ink3,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = VColors.lineSoft)
        Spacer(Modifier.height(14.dp))

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(24.dp))
            }
            summary == null || summary.entries.isEmpty() -> Text(
                text = "No classes scheduled for $childName today.",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                summary.entries.take(4).forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(VColors.violet),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.subject ?: "Subject",
                                style = VTypography.body.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                                color = VColors.ink,
                            )
                            if (entry.summaryText.isNotBlank()) {
                                Text(
                                    text = entry.summaryText,
                                    style = VTypography.caption.copy(fontSize = 11.sp),
                                    color = VColors.ink2,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Text(
                            text = "${entry.coveragePct}%",
                            style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = VColors.violet,
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ANNOUNCEMENTS — premium card with divider + entries
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun AnnouncementsCard(
    announcements: List<com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement>,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(20.dp),
    ) {
        Text(
            text = "Announcements",
            style = VTypography.body.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
        )

        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = VColors.lineSoft)
        Spacer(Modifier.height(14.dp))

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(24.dp))
            }
            announcements.isEmpty() -> Text(
                text = "No announcements yet",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                announcements.take(3).forEach { announcement ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(VColors.violet),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = announcement.title,
                                style = VTypography.body.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp),
                                color = VColors.ink,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = announcement.description,
                                style = VTypography.caption.copy(fontSize = 11.sp),
                                color = VColors.ink2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}
