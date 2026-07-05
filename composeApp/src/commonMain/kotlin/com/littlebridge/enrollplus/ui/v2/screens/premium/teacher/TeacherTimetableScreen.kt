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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTimetableViewModel
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherTimetableScreen(
    modifier: Modifier = Modifier,
    viewModel: TeacherTimetableViewModel = koinViewModel(),
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

        if (state.isLoading && state.week.isEmpty()) {
            StatusBox("Loading timetable...")
            return@Column
        }

        if (state.errorMessage != null && state.week.isEmpty()) {
            StatusBox(state.errorMessage!!, isError = true)
            return@Column
        }

        if (state.week.isEmpty()) {
            StatusBox("No timetable available")
            return@Column
        }

        val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        state.week.forEach { day ->
            if (day.periods.isNotEmpty()) {
                VSectionHeader(dayNames.getOrNull(day.weekday - 1) ?: "Day ${day.weekday}")
                Spacer(Modifier.height(12.dp))
                day.periods.forEach { period ->
                    Row(
                        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(Modifier.size(4.dp).clip(CircleShape).background(VColors.Primary))
                        Column(Modifier.weight(1f)) {
                            Text(period.subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
                            Text("${period.className}-${period.section} · Room ${period.room}", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                        }
                        Text("${period.startTime} - ${period.endTime}", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
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
