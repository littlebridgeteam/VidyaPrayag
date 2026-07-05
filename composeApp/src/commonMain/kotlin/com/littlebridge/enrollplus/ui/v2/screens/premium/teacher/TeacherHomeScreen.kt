package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.ResolvedPeriodUi
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayViewModel
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: TeacherTodayViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        if (state.isLoading && state.day == null) {
            StatusBox("Loading your schedule...")
            return@Column
        }

        if (state.error != null && state.day == null) {
            StatusBox(state.error!!, isError = true)
            return@Column
        }

        val day = state.day
        if (day == null) {
            StatusBox("No schedule loaded")
            return@Column
        }

        if (day.isHoliday) {
            HolidayCard(day.holidayName ?: "Holiday")
            return@Column
        }

        if (day.periods.isEmpty()) {
            StatusBox("No classes scheduled today")
            return@Column
        }

        VSectionHeader("Today's Schedule")
        Spacer(Modifier.height(12.dp))

        day.periods.forEach { period ->
            PeriodCard(period)
            Spacer(Modifier.height(8.dp))
        }

        if (day.calendar.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            VSectionHeader("Calendar Events")
            Spacer(Modifier.height(12.dp))
            day.calendar.forEach { evt ->
                Row(
                    Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(VColors.Primary))
                    Column(Modifier.weight(1f)) {
                        Text(evt.title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
                        Text(evt.audience, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PeriodCard(period: ResolvedPeriodUi) {
    val isNow = false
    val isCancelled = period.isCancelled
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg)
            .background(if (isCancelled) VColors.ErrorContainer else VColors.SurfaceContainerLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(8.dp).clip(CircleShape)
                .background(if (isCancelled) VColors.Error else VColors.Primary),
        )
        Column(Modifier.weight(1f)) {
            Text(
                period.subject,
                style = VTypography.UpdateTitle.copy(
                    color = if (isCancelled) VColors.OnErrorContainer else VColors.OnSurface,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Text(
                "${period.classLabel} · Room ${period.room}",
                style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant),
            )
            Text(
                "${period.startTime} - ${period.endTime}",
                style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant),
            )
        }
        if (period.attendanceMarked) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VColors.Tertiary, modifier = Modifier.size(20.dp))
        }
        if (isCancelled) {
            Text("CANCELLED", style = VTypography.NavLabel.copy(color = VColors.Error, fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun HolidayCard(name: String) {
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.PrimaryContainer).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.Schedule, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text(name, style = VTypography.GreetingTitle.copy(color = VColors.OnPrimaryContainer))
        Text("No classes today", style = VTypography.UpdateText.copy(color = VColors.OnPrimaryContainer.copy(alpha = 0.7f)))
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg)
            .background(if (isError) VColors.ErrorContainer else VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.OnErrorContainer else VColors.OnSurfaceVariant))
    }
}
