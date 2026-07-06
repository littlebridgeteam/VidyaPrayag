package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarPlatformViewModel
import com.littlebridge.enrollplus.presentation.PermissionViewModel
import com.littlebridge.enrollplus.platform.rememberNotificationPermissionLauncher
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.components.overlay.VConfirmDialogPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolHomePremium(
    onOpenNotifications: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenPews: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenReportPublish: () -> Unit = {},
    onOpenReportEffectiveness: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    onCreateEvent: () -> Unit = {},
    onExit: () -> Unit = {},
    viewModel: SchoolDashboardViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    calendarViewModel: AcademicCalendarPlatformViewModel = koinViewModel(),
    permissionVm: PermissionViewModel = koinViewModel(),
) {
    val dashState by viewModel.state.collectAsStateV2()
    val notifications by notificationsViewModel.state.collectAsStateV2()

    val showRationale by permissionVm.showNotificationRationale.collectAsStateV2()
    val launchPermission by permissionVm.launchPermissionRequest.collectAsStateV2()

    val permissionLauncher = rememberNotificationPermissionLauncher { granted ->
        permissionVm.onPermissionResult(granted)
    }

    androidx.compose.runtime.LaunchedEffect(launchPermission) {
        if (launchPermission) {
            permissionVm.consumeLaunchPermissionRequest()
            permissionLauncher.launch()
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        permissionVm.checkNotificationPermission()
    }

    val overview = dashState.overview
    val header = overview?.header
    val adminName = header?.adminName?.takeIf { it.isNotBlank() } ?: dashState.adminName
    val schoolName = header?.schoolName?.takeIf { it.isNotBlank() } ?: appString(StringKeys.HOME_YOUR_SCHOOL)

    VStateHostPremium(
        loading = dashState.isLoading && overview == null,
        error = if (overview == null) dashState.errorMessage else null,
        isEmpty = false,
        modifier = Modifier.fillMaxSize(),
        skeleton = {
            Column(
                Modifier.fillMaxSize().statusBarsPadding().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VShimmerCardPremium()
                VShimmerCardPremium()
                VShimmerListPremium(itemCount = 4)
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            VGradientHeroPremium(
                title = "Welcome, $adminName",
                subtitle = schoolName,
                stats = listOf(
                    HeroStatPremium("${overview?.kpis?.find { it.key == "students" }?.value ?: 0}", "Students"),
                    HeroStatPremium("${overview?.kpis?.find { it.key == "teachers" }?.value ?: 0}", "Teachers"),
                    HeroStatPremium("${notifications.unreadCount}", "Unread"),
                ),
                onClick = onExit,
                livePillText = if (notifications.unreadCount > 0) "${notifications.unreadCount} new" else null,
                trailingIcon = {
                    Icon(
                        VIcons.Bell,
                        contentDescription = "Notifications",
                        tint = VColors.OnPrimary,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp),
                    )
                },
            )

            val kpis = overview?.kpis.orEmpty().filter { it.available }
            if (kpis.isNotEmpty()) {
                Text("Key Metrics", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                kpis.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { kpi ->
                            VStatCardPremium(
                                value = "${kpi.value}${kpi.unit}",
                                label = kpi.label,
                                onClick = onOpenAnalytics,
                                modifier = Modifier.weight(1f),
                                trend = if (kpi.deltaDirection != "flat" && kpi.deltaValue > 0.0) {
                                    (if (kpi.deltaDirection == "up") "+" else "-") + kpi.deltaValue.toInt().toString() + "%"
                                } else null,
                                trendUp = kpi.deltaDirection != "down",
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            overview?.schoolPulse?.let { pulse ->
                VGradientHeroPremium(
                    title = "School Pulse",
                    subtitle = pulse.message,
                    stats = listOf(HeroStatPremium("${pulse.score}", "/ 100")),
                    onClick = onOpenAnalytics,
                )
            }

            overview?.communication?.let { comm ->
                VStatCardPremium(
                    value = "${comm.unreadMessages}",
                    label = "Unread Messages",
                    onClick = onOpenNotifications,
                    icon = VIcons.Megaphone,
                )
            }

            overview?.events?.takeIf { it.available }?.let { ev ->
                VStatCardPremium(
                    value = "${ev.upcoming.size}",
                    label = "Upcoming Events",
                    onClick = onOpenCalendar,
                    icon = VIcons.Calendar,
                )
            }

            VStatCardPremium(
                value = "Analytics",
                label = "View Dashboard",
                onClick = onOpenAnalytics,
                icon = VIcons.TrendingUp,
            )
            VStatCardPremium(
                value = "At-Risk",
                label = "PEWS Cohort",
                onClick = onOpenPews,
                icon = VIcons.AlertTriangle,
            )
            VStatCardPremium(
                value = "Transport",
                label = "Manage Routes",
                onClick = onOpenTransport,
                icon = VIcons.MapPin,
            )
            VStatCardPremium(
                value = "Reports",
                label = "Publish Report Cards",
                onClick = onOpenReportPublish,
                icon = VIcons.FileText,
            )
            VStatCardPremium(
                value = "Events",
                label = "Event Registration",
                onClick = onOpenEvents,
                icon = VIcons.Calendar,
            )
        }
    }

    VConfirmDialogPremium(
        visible = showRationale,
        title = appString(StringKeys.HOME_NOTIF_RATIONALE_TITLE),
        message = appString(StringKeys.HOME_NOTIF_RATIONALE_MSG),
        confirmLabel = appString(StringKeys.HOME_NOTIF_ENABLE),
        onConfirm = permissionVm::requestNotificationPermission,
        onDismiss = permissionVm::declineNotifications,
        cancelLabel = appString(StringKeys.HOME_NOTIF_NOT_NOW),
        icon = VIcons.Bell,
        isDestructive = false,
    )
}
