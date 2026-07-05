package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun TeacherPtmScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Parent-Teacher Meeting", onBack = onBack, modifier = modifier) {
        Text("Schedule PTM", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))
        VTextInput(value = "", onValueChange = {}, label = "Date", placeholder = "YYYY-MM-DD", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Time", placeholder = "HH:MM", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Duration", placeholder = "30 minutes", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Notes", placeholder = "Agenda or notes", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Schedule PTM", onClick = {}, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        Text("Upcoming PTMs", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(12.dp))
        PtmRow("Feb 20, 2026", "10:00 AM", "Grade 5-A")
        Spacer(Modifier.height(8.dp))
        PtmRow("Feb 22, 2026", "11:00 AM", "Grade 5-B")
    }
}

@Composable
private fun PtmRow(date: String, time: String, className: String) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Text("$date · $time", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Text(className, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
    }
}
