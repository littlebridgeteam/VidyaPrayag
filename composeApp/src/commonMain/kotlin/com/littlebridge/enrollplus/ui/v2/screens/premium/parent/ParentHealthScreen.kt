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
fun ParentHealthScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    childId: String = "",
) {
    ParentOverlayScaffold(title = "Health Records", onBack = onBack, modifier = modifier) {
        VStaggeredItem(delayMs = 0) {
            // Health Profile card
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                Text("Health Profile", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))
                HealthRow("Blood Group", "O+", VColors.OnSurface)
                Spacer(Modifier.height(12.dp))
                HealthRow("Allergies", "Peanuts", VColors.Error)
                Spacer(Modifier.height(12.dp))
                HealthRow("Conditions", "None", VColors.OnSurface)
                Spacer(Modifier.height(12.dp))
                HealthRow("Immunizations", "Up to date", VColors.Tertiary)
                Spacer(Modifier.height(12.dp))
                HealthRow("Emergency Contact", "Priya Sharma (Mother)", VColors.OnSurface)
            }
        }
        Spacer(Modifier.height(16.dp))

        VStaggeredItem(delayMs = 80) {
            // Pulse Score card with ring
            val pulseScore = 87
            val ringColor = VColors.Tertiary
            val trackColor = VColors.TertiaryContainer
            val sweep = (pulseScore / 100f) * 360f
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                Text("Pulse Score", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier.size(80.dp).drawBehind {
                                drawCircle(trackColor, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))
                                drawArc(ringColor, startAngle = -90f, sweepAngle = sweep, useCenter = false, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))
                            },
                        )
                        Text("$pulseScore", style = VTypography.StatValue.copy(color = ringColor, fontSize = 22.sp))
                    }
                    Column {
                        Text("Healthy", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                        Text("No risk factors detected", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthRow(label: String, value: String, valueColor: Color) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Text(value, style = VTypography.UpdateText.copy(color = valueColor, fontWeight = FontWeight.Bold))
    }
}
