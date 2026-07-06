package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarViewModel
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.core.qualifier.Qualifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolAcademicCalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModelQualifier: Qualifier? = null,
    viewModel: AcademicCalendarViewModel = koinViewModel(qualifier = viewModelQualifier),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    SchoolOverlayScaffold(title = "Academic Calendar", onBack = onBack, modifier = modifier) {
        // Month navigation
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).clip(androidx.compose.foundation.shape.CircleShape).background(VColors.SurfaceContainerHigh)
                    .clickable { viewModel.goToPreviousMonth() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous", tint = VColors.OnSurface, modifier = Modifier.size(24.dp))
            }
            Text(
                state.currentMonth.ifBlank { "Loading..." },
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold),
            )
            Box(
                Modifier.size(40.dp).clip(androidx.compose.foundation.shape.CircleShape).background(VColors.SurfaceContainerHigh)
                    .clickable { viewModel.goToNextMonth() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", tint = VColors.OnSurface, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Summary stats
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryCard("Working Days", state.workingDays.toString(), Modifier.weight(1f))
            SummaryCard("Holidays", state.holidays.toString(), Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        if (state.isLoading) {
            StatusBox("Loading calendar...")
        } else if (state.errorMessage != null) {
            StatusBox(state.errorMessage!!, isError = true)
        } else if (state.calendarEvents.isEmpty()) {
            StatusBox("No events this month")
        } else {
            state.calendarEvents.forEach { event ->
                Row(
                    Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(VColors.Primary))
                    Column(Modifier.weight(1f)) {
                        Text(event.eventTitle, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
                        Text("${event.date} - ${event.day}", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                        if (event.eventDescription.isNotBlank()) {
                            Text(event.eventDescription, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant), maxLines = 2)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VTypography.SectionHeader.copy(color = VColors.Primary, fontWeight = FontWeight.Bold))
        Text(label, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.Error else VColors.OnSurfaceVariant))
    }
}
