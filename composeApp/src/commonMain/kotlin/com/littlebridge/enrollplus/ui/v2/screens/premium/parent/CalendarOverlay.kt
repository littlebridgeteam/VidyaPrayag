package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarViewModel
import com.littlebridge.enrollplus.feature.school.domain.model.CalendarEventDto
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.core.qualifier.named
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalendarOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AcademicCalendarViewModel = koinViewModel(qualifier = named("parentCalendar")),
) {
    val state by viewModel.state.collectAsStateV2()

    ParentOverlayScaffold(
        title = "Calendar",
        onBack = onBack,
        modifier = modifier,
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month", tint = VColors.OnSurface)
            }
            Text(
                text = state.currentMonth.ifBlank { "Loading..." },
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
            )
            IconButton(onClick = { viewModel.goToNextMonth() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month", tint = VColors.OnSurface)
            }
        }

        // Summary row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CalendarStat(
                label = "Working",
                value = state.workingDays.toString(),
                color = VColors.Primary,
                modifier = Modifier.weight(1f),
            )
            CalendarStat(
                label = "Holidays",
                value = state.holidays.toString(),
                color = VColors.WarmOrange,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(16.dp))

        // Events list
        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.calendarEvents.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No calendar events this month",
            emptyIcon = Icons.Filled.CalendarMonth,
            onRetry = { viewModel.loadCalendar() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(4) { VShimmerBoxPremium(height = 72.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            state.calendarEvents.forEach { event ->
                CalendarEventCard(event = event)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CalendarStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Text(
            text = value,
            style = VTypography.QuickStatValue.copy(color = color),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = VTypography.QuickStatLabel.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

@Composable
private fun CalendarEventCard(event: CalendarEventDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = event.day.take(2),
                style = VTypography.QuickStatValue.copy(color = VColors.OnPrimaryContainer),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.eventTitle,
                style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
            )
            if (event.eventDescription.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = event.eventDescription,
                    style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = event.date,
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }
    }
}
