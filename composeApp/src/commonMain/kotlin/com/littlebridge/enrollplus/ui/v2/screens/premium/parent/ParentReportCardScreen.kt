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
fun ParentReportCardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    draftId: String? = null,
) {
    ParentOverlayScaffold(title = "Report Card", onBack = onBack, modifier = modifier) {
        Text("Term 1 · 2025-2026", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text("Overall Grade: A", style = VTypography.SectionLink.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(20.dp))
        ReportRow("Mathematics", "92", "A")
        Spacer(Modifier.height(8.dp))
        ReportRow("Science", "88", "A")
        Spacer(Modifier.height(8.dp))
        ReportRow("English", "85", "A")
        Spacer(Modifier.height(8.dp))
        ReportRow("Hindi", "90", "A")
        Spacer(Modifier.height(8.dp))
        ReportRow("Social Studies", "82", "B+")
        Spacer(Modifier.height(8.dp))
        ReportRow("Computer Science", "95", "A+")
        Spacer(Modifier.height(20.dp))
        Text("Teacher's Remarks", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text("Excellent progress this term. Keep up the good work in mathematics and science.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun ReportRow(subject: String, marks: String, grade: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Text(marks, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.size(16.dp))
        Box(Modifier.padding(horizontal = 10.dp, vertical = 4.dp).clip(VShapes.Full).background(VColors.PrimaryContainer)) {
            Text(grade, style = VTypography.NavLabel.copy(color = VColors.OnPrimaryContainer, fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 6.dp))
        }
    }
}
