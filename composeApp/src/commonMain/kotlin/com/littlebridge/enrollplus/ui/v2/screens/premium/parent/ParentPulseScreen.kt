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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentPulseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Pulse Score", onBack = onBack, modifier = modifier) {
        val pulseScore = 87
        val ringColor = VColors.Tertiary
        val trackColor = VColors.TertiaryContainer
        val sweep = (pulseScore / 100f) * 360f
        VStaggeredItem(delayMs = 0) {
            // Ring card — centered, 120dp ring with score
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Box(
                Modifier.size(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.size(120.dp).drawBehind {
                        drawCircle(trackColor, style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
                        drawArc(ringColor, startAngle = -90f, sweepAngle = sweep, useCenter = false, style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
                    },
                )
                Text("$pulseScore", style = VTypography.StatValue.copy(color = ringColor, fontSize = 32.sp))
            }
            Spacer(Modifier.height(20.dp))
            Text("Healthy", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp))
            Spacer(Modifier.height(4.dp))
            Text("Early warning score based on attendance, academics & engagement", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
        }
        Spacer(Modifier.height(16.dp))

        VStaggeredItem(delayMs = 80) {
            // Metrics card 1 — Attendance + Academics
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                PulseMetricBar("Attendance", 94, "Good", VColors.Tertiary)
                Spacer(Modifier.height(16.dp))
                PulseMetricBar("Academics", 88, "Good", VColors.Primary)
            }
        }
        Spacer(Modifier.height(16.dp))

        VStaggeredItem(delayMs = 160) {
            // Metrics card 2 — Engagement + Wellness
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                PulseMetricBar("Engagement", 79, "Fair", VColors.WarmOrange)
                Spacer(Modifier.height(16.dp))
                PulseMetricBar("Wellness", 85, "Good", VColors.Tertiary)
            }
        }
        Spacer(Modifier.height(16.dp))

        VStaggeredItem(delayMs = 240) {
            // AI Recommendations card
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.PrimaryContainer).padding(20.dp),
            ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(20.dp))
                Text("AI Recommendations", style = VTypography.UpdateTitle.copy(color = VColors.OnPrimaryContainer, fontWeight = FontWeight.Bold))
            }
            Spacer(Modifier.height(12.dp))
            Text("• Engagement score is below 80%. Consider increasing study sessions to 4x/week.", style = VTypography.UpdateText.copy(color = VColors.OnPrimaryContainer))
            Spacer(Modifier.height(8.dp))
            Text("• Attendance is excellent. Keep up the current routine.", style = VTypography.UpdateText.copy(color = VColors.OnPrimaryContainer))
            Spacer(Modifier.height(8.dp))
            Text("• Academic performance trending up. Focus on Science for further improvement.", style = VTypography.UpdateText.copy(color = VColors.OnPrimaryContainer))
            }
        }
    }
}

@Composable
private fun PulseMetricBar(label: String, pct: Int, rating: String, color: Color) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh),
        ) {
            Box(
                Modifier.fillMaxWidth(pct / 100f).height(6.dp).clip(VShapes.Full).background(color),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("$pct% · $rating", style = VTypography.NavLabel.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}
