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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.progress.VProgressRing
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentPulseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Engagement Pulse", onBack = onBack, modifier = modifier) {
        Row(
            Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.TertiaryContainer).padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            VProgressRing(progress = 0.78f, label = "78%", size = 80.dp)
            Column(Modifier.weight(1f)) {
                Text("Engagement Score", style = VTypography.GreetingTitle.copy(color = VColors.OnTertiaryContainer))
                Text("Your child's participation is above average", style = VTypography.UpdateText.copy(color = VColors.OnTertiaryContainer.copy(alpha = 0.7f)))
            }
        }
        Spacer(Modifier.height(20.dp))
        PulseMetric("Class Participation", "High", Icons.Filled.Mood, VColors.Tertiary)
        Spacer(Modifier.height(8.dp))
        PulseMetric("Homework Completion", "92%", Icons.Filled.Favorite, VColors.Primary)
        Spacer(Modifier.height(8.dp))
        PulseMetric("Attendance Rate", "95%", Icons.Filled.Mood, VColors.Tertiary)
        Spacer(Modifier.height(8.dp))
        PulseMetric("Peer Interaction", "Good", Icons.Filled.Favorite, VColors.Primary)
    }
}

@Composable
private fun PulseMetric(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: androidx.compose.ui.graphics.Color) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant), modifier = Modifier.weight(1f))
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}
