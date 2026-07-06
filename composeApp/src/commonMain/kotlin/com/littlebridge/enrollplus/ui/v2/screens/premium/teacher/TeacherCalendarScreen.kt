package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun TeacherCalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Calendar", onBack = onBack, modifier = modifier) {
        Text("January 2026", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(day, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold))
            }
        }
        Spacer(Modifier.height(8.dp))
        repeat(4) { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                repeat(7) { day ->
                    val dayNum = week * 7 + day + 1
                    val isToday = dayNum == 15
                    Box(
                        Modifier.size(36.dp).clip(CircleShape)
                            .background(if (isToday) VColors.Primary else VColors.Surface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(dayNum.toString(), style = VTypography.NavLabel.copy(color = if (isToday) VColors.OnPrimary else VColors.OnSurface, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Upcoming", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(12.dp))
        CalendarEventRow("Jan 20", "Republic Day", VColors.Error)
        Spacer(Modifier.height(8.dp))
        CalendarEventRow("Jan 26", "Annual Day", VColors.Primary)
        Spacer(Modifier.height(8.dp))
        CalendarEventRow("Feb 14", "Sports Day", VColors.Tertiary)
    }
}

@Composable
private fun CalendarEventRow(date: String, title: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(4.dp).clip(CircleShape).background(color))
        Text(date, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        Text(title, style = VTypography.UpdateText.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}
