package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.DailyAttendanceViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DailyAttendancePremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DailyAttendanceViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Daily Attendance", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.attendees.isEmpty() && !state.isLoading,
            emptyTitle = "No attendance data",
            onRetry = { viewModel.selectClass(state.selectedClass) },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 5)
                }
            },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = state.selectedClass,
                    subtitle = "Attendance: ${state.attendancePercentage}",
                    stats = listOf(
                        HeroStatPremium("${state.presentCount}", "Present"),
                        HeroStatPremium("${state.totalCount - state.presentCount}", "Absent"),
                        HeroStatPremium(state.attendancePercentage, "Rate"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Roster (${state.attendanceType})", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.attendees.forEach { a ->
                        VListTilePremium(
                            title = a.name,
                            subtitle = a.status.name,
                            onClick = { viewModel.updateStatus(a.id, nextStatus(a.status)) },
                            leadingIcon = VIcons.Users,
                            trailingText = a.status.name,
                        )
                    }
                }
            }
        }
    }
}

private fun nextStatus(current: com.littlebridge.enrollplus.feature.admin.presentation.AttendanceStatus) =
    when (current) {
        com.littlebridge.enrollplus.feature.admin.presentation.AttendanceStatus.PRESENT ->
            com.littlebridge.enrollplus.feature.admin.presentation.AttendanceStatus.ABSENT
        com.littlebridge.enrollplus.feature.admin.presentation.AttendanceStatus.ABSENT ->
            com.littlebridge.enrollplus.feature.admin.presentation.AttendanceStatus.LATE
        com.littlebridge.enrollplus.feature.admin.presentation.AttendanceStatus.LATE ->
            com.littlebridge.enrollplus.feature.admin.presentation.AttendanceStatus.PRESENT
    }
