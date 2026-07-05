package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.progress.VProgressBar
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentSyllabusV2Screen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Curriculum Units", onBack = onBack, modifier = modifier) {
        CurriculumUnit("Mathematics", "Number Systems", "Covered", 1.0f)
        Spacer(Modifier.height(12.dp))
        CurriculumUnit("Mathematics", "Algebra", "In Progress", 0.6f)
        Spacer(Modifier.height(12.dp))
        CurriculumUnit("Science", "Living Things", "Covered", 1.0f)
        Spacer(Modifier.height(12.dp))
        CurriculumUnit("Science", "Matter & Materials", "Upcoming", 0f)
        Spacer(Modifier.height(12.dp))
        CurriculumUnit("English", "Grammar Basics", "Covered", 1.0f)
        Spacer(Modifier.height(12.dp))
        CurriculumUnit("English", "Creative Writing", "In Progress", 0.3f)
    }
}

@Composable
private fun CurriculumUnit(subject: String, unit: String, status: String, progress: Float) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
                Text(unit, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            Text(status, style = VTypography.NavLabel.copy(color = when (status) { "Covered" -> VColors.Tertiary; "In Progress" -> VColors.Primary; else -> VColors.Outline }, fontWeight = FontWeight.SemiBold))
        }
        if (progress > 0f) {
            Spacer(Modifier.height(8.dp))
            VProgressBar(progress = progress, modifier = Modifier.fillMaxWidth())
        }
    }
}
