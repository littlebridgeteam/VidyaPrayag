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
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.progress.VProgressBar
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentTutorProgressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Tutor Progress", onBack = onBack, modifier = modifier) {
        VStaggeredItem(delayMs = 0) {
            Text("Your child's tutoring progress.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        }
        Spacer(Modifier.height(20.dp))
        VStaggeredItem(delayMs = 60) { SubjectProgress("Mathematics", 0.85f) }
        Spacer(Modifier.height(12.dp))
        VStaggeredItem(delayMs = 120) { SubjectProgress("Science", 0.72f) }
        Spacer(Modifier.height(12.dp))
        VStaggeredItem(delayMs = 180) { SubjectProgress("English", 0.90f) }
        Spacer(Modifier.height(20.dp))
        VStaggeredItem(delayMs = 240) { Text("Recent Sessions", style = VTypography.SectionHeader.copy(color = VColors.OnSurface)) }
        Spacer(Modifier.height(12.dp))
        VStaggeredItem(delayMs = 300) { SessionRow("Jan 20", "Mathematics", "Algebra basics", "Completed") }
        Spacer(Modifier.height(8.dp))
        VStaggeredItem(delayMs = 360) { SessionRow("Jan 18", "Science", "Photosynthesis", "Completed") }
        Spacer(Modifier.height(8.dp))
        VStaggeredItem(delayMs = 420) { SessionRow("Jan 15", "English", "Essay writing", "Completed") }
    }
}

@Composable
private fun SubjectProgress(subject: String, progress: Float) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text("${(progress * 100).toInt()}%", style = VTypography.SectionLink.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
        }
        Spacer(Modifier.height(8.dp))
        VProgressBar(progress = progress, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun SessionRow(date: String, subject: String, topic: String, status: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(VColors.Tertiary))
        Column(Modifier.weight(1f)) {
            Text("$subject · $topic", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(date, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Text(status, style = VTypography.NavLabel.copy(color = VColors.Tertiary, fontWeight = FontWeight.SemiBold))
    }
}
