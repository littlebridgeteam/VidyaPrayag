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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
            .padding(bottom = 110.dp),
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

@Composable
private fun HomeSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(VShapes.lg)
                    .background(VColors.lineSoft),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(VShapes.lg)
                        .background(VColors.lineSoft),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
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
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            text = "Discover schools",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.violet,
            modifier = Modifier.clickable { onDiscoverSchools() },
        )
    }
}

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
    val childName = child?.name ?: "Your child"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ChildSummaryCard(child = child, attendance = state.attendance)

        HomeQuickActions(
            onPayFees = onOpenFees,
            onAcademics = onOpenAcademics,
            onMessages = onOpenMessages,
        )

        LearningSummaryCard(
            childName = childName,
            summary = academics.dailySummary,
            isLoading = academics.dailySummaryLoading,
        )

        StatsGrid(
            attendance = state.attendance,
            latestMark = state.latestMark,
            fees = state.fees,
            quizzes = academics.quizzes,
        )

        AnnouncementsCard(
            announcements = announcements.announcements,
            isLoading = announcements.isLoading,
        )

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ChildSummaryCard(
    child: DashboardChildSummary?,
    attendance: ParentAttendanceData?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = child?.name?.take(1)?.uppercase() ?: "C",
                style = VTypography.h2.copy(fontWeight = FontWeight.Bold),
                color = VColors.violet,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = child?.name ?: "Your child",
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            Text(
                text = "Level ${child?.currentLevel ?: 1} · ${child?.attendanceStatus?.replaceFirstChar { it.uppercase() } ?: "No data"}",
                style = VTypography.caption,
                color = VColors.ink2,
            )
        }
        val isPresent = child?.attendanceStatus?.lowercase() == "present"
        Box(
            modifier = Modifier
                .clip(VShapes.full)
                .background(if (isPresent) VColors.successSoft else VColors.creamDeep)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = if (isPresent) "Present" else "No data",
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = if (isPresent) VColors.success else VColors.ink3,
            )
        }
    }
}

@Composable
private fun HomeQuickActions(
    onPayFees: () -> Unit,
    onAcademics: () -> Unit,
    onMessages: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickActionCard(
            icon = VIcons.WalletPremium,
            label = "Pay fees",
            iconBg = VColors.violetSoft,
            iconColor = VColors.violet,
            onClick = onPayFees,
            modifier = Modifier.weight(1f),
        )
        QuickActionCard(
            icon = VIcons.Academic,
            label = "Academics",
            iconBg = VColors.successSoft,
            iconColor = VColors.success,
            onClick = onAcademics,
            modifier = Modifier.weight(1f),
        )
        QuickActionCard(
            icon = VIcons.ChatPremium,
            label = "Messages",
            iconBg = VColors.creamDeep,
            iconColor = VColors.ink,
            onClick = onMessages,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LearningSummaryCard(
    childName: String,
    summary: ParentDailySummaryData?,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Today's learning",
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            if (summary != null) {
                Text(
                    text = summary.entries.size.toString() + " classes",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
        }

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(28.dp))
            }
            summary == null || summary.entries.isEmpty() -> Text(
                text = "No classes scheduled for $childName today.",
                style = VTypography.caption,
                color = VColors.ink2,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                summary.entries.take(4).forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(VColors.success),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.subject ?: "Subject",
                                style = VTypography.body.copy(fontWeight = FontWeight.Medium),
                                color = VColors.ink,
                            )
                            if (entry.summaryText.isNotBlank()) {
                                Text(
                                    text = entry.summaryText,
                                    style = VTypography.caption,
                                    color = VColors.ink2,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Text(
                            text = "${entry.coveragePct}%",
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsGrid(
    attendance: ParentAttendanceData?,
    latestMark: ParentMarkDto?,
    fees: FeeData?,
    quizzes: List<com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDto>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick stats",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Attendance",
                value = if (attendance != null) "${attendance.attendanceRate}%" else "—",
                subtext = "This term",
                icon = VIcons.Calendar,
                iconBg = VColors.violetSoft,
                iconColor = VColors.violet,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "Latest marks",
                value = markDisplay(latestMark),
                subtext = "Recent assessment",
                icon = VIcons.Star,
                iconBg = VColors.goldSoft,
                iconColor = VColors.gold,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Fees due",
                value = fees?.outstandingFees ?: "₹0",
                subtext = if ((fees?.outstandingFees ?: "₹0") == "₹0") "All clear" else "Due",
                icon = VIcons.WalletPremium,
                iconBg = VColors.successSoft,
                iconColor = VColors.success,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "Quizzes",
                value = quizzes.size.toString(),
                subtext = "Pending",
                icon = VIcons.Academic,
                iconBg = VColors.creamDeep,
                iconColor = VColors.ink,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = value,
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.ink2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtext,
            style = VTypography.caption.copy(fontWeight = FontWeight.Medium),
            color = VColors.ink3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AnnouncementsCard(
    announcements: List<com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement>,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Announcements",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(28.dp))
            }
            announcements.isEmpty() -> Text(
                text = "No announcements yet",
                style = VTypography.caption,
                color = VColors.ink2,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                .background(VColors.violet)
                                .padding(top = 6.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = announcement.title,
                                style = VTypography.body.copy(fontWeight = FontWeight.Medium),
                                color = VColors.ink,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = announcement.description,
                                style = VTypography.caption,
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

private fun markDisplay(mark: ParentMarkDto?): String {
    if (mark == null) return "—"
    val pct = if (mark.maxMarks > 0) ((mark.marks ?: 0.0) / mark.maxMarks * 100).roundToInt() else 0
    return "$pct%"
}
