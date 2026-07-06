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
fun ParentHomeworkScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Homework", onBack = onBack, modifier = modifier) {
        HomeworkRow("Mathematics", "Exercise 5.2 — Problems 1-10", "Due: Jan 22", false)
        Spacer(Modifier.height(8.dp))
        HomeworkRow("Science", "Read Chapter 8: Photosynthesis", "Due: Jan 23", false)
        Spacer(Modifier.height(8.dp))
        HomeworkRow("English", "Write essay on 'My Village'", "Due: Jan 20", true)
        Spacer(Modifier.height(8.dp))
        HomeworkRow("Hindi", "पाठ 5 के प्रश्न", "Due: Jan 25", false)
    }
}

@Composable
private fun HomeworkRow(subject: String, task: String, due: String, completed: Boolean) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(20.dp).clip(CircleShape).background(if (completed) VColors.Tertiary else VColors.SurfaceContainerHigh))
        Column(Modifier.weight(1f)) {
            Text(subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(task, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            Text(due, style = VTypography.NavLabel.copy(color = if (completed) VColors.Tertiary else VColors.WarmOrange))
        }
    }
}
