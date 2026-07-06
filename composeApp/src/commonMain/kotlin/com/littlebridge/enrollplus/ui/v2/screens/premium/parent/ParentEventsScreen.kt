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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentEventsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "School Events", onBack = onBack, modifier = modifier) {
        Text("Upcoming events at your child's school.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        EventCard("Annual Sports Day", "Feb 14, 2026", "School Grounds", "9:00 AM - 4:00 PM")
        Spacer(Modifier.height(12.dp))
        EventCard("Parent-Teacher Meeting", "Feb 20, 2026", "School Auditorium", "10:00 AM - 1:00 PM")
        Spacer(Modifier.height(12.dp))
        EventCard("Science Exhibition", "Mar 5, 2026", "Science Block", "11:00 AM - 3:00 PM")
    }
}

@Composable
private fun EventCard(title: String, date: String, venue: String, time: String) {
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(20.dp),
    ) {
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column { Text("Date", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant)); Text(date, style = VTypography.UpdateText.copy(color = VColors.OnSurface)) }
            Column { Text("Time", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant)); Text(time, style = VTypography.UpdateText.copy(color = VColors.OnSurface)) }
        }
        Spacer(Modifier.height(8.dp))
        Text("Venue: $venue", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Register", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
