package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

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
fun ParentTimetableScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Timetable", onBack = onBack, modifier = modifier) {
        Text("Monday", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))
        PeriodRow("08:00", "09:00", "Mathematics", "Room 12")
        Spacer(Modifier.height(8.dp))
        PeriodRow("09:00", "10:00", "Science", "Lab 3")
        Spacer(Modifier.height(8.dp))
        PeriodRow("10:15", "11:15", "English", "Room 12")
        Spacer(Modifier.height(8.dp))
        PeriodRow("11:15", "12:15", "Hindi", "Room 12")
        Spacer(Modifier.height(8.dp))
        PeriodRow("13:00", "14:00", "Social Studies", "Room 15")
        Spacer(Modifier.height(8.dp))
        PeriodRow("14:00", "15:00", "Computer Science", "Lab 1")
    }
}

@Composable
private fun PeriodRow(start: String, end: String, subject: String, room: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(4.dp).clip(CircleShape).background(VColors.Primary))
        Column(Modifier.weight(1f)) {
            Text(subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text("$start - $end · $room", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
    }
}
