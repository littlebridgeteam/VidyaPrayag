package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.littlebridge.enrollplus.feature.parent.presentation.ParentPulseViewModel
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentPulseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentPulseViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentOverlayScaffold(title = "Pulse Score", onBack = onBack, modifier = modifier) {
        if (state.isLoading) {
            VStaggeredItem(delayMs = 0) { SkeletonCard(variant = "card") }
            VStaggeredItem(delayMs = 60) { SkeletonCard(variant = "card") }
            return@ParentOverlayScaffold
        }
        if (state.error != null) {
            ErrorStateCard(
                message = state.error ?: "Unknown error",
                onRetry = null,
                modifier = Modifier.padding(vertical = 24.dp),
            )
            return@ParentOverlayScaffold
        }
        val pulse = state.latestPulse
        if (pulse == null) {
            EmptyStateCard(
                title = "No Pulse Data",
                body = "Weekly AI pulse score will appear here once available.",
                icon = Icons.AutoMirrored.Filled.MenuBook,
            )
            return@ParentOverlayScaffold
        }

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            val pulseScore = pulse.attendancePercentage?.toInt() ?: 0
            val ringColor = VColors.Tertiary
            val trackColor = VColors.TertiaryContainer
            val sweep = (pulseScore / 100f) * 360f
            VStaggeredItem(delayMs = 0) {
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
                    Text(pulse.weekRange, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                    Spacer(Modifier.height(4.dp))
                    Text(pulse.aiNarrative, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
                }
            }
            Spacer(Modifier.height(16.dp))

            VStaggeredItem(delayMs = 80) {
                Column(
                    Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
                ) {
                    PulseMetricBar("Attendance", pulse.attendancePercentage?.toInt() ?: 0, pulse.attendanceTrend ?: "—", VColors.Tertiary)
                    Spacer(Modifier.height(16.dp))
                    PulseMetricBar("Homework Done", pulse.homeworkCompleted, "${pulse.homeworkPending} pending", VColors.Primary)
                }
            }
            Spacer(Modifier.height(16.dp))

            if (pulse.actionableItems.isNotEmpty()) {
                VStaggeredItem(delayMs = 160) {
                    Column(
                        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.PrimaryContainer).padding(20.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(20.dp))
                            Text("Actionable Items", style = VTypography.UpdateTitle.copy(color = VColors.OnPrimaryContainer, fontWeight = FontWeight.Bold))
                        }
                        Spacer(Modifier.height(12.dp))
                        pulse.actionableItems.forEach { item ->
                            Text("• $item", style = VTypography.UpdateText.copy(color = VColors.OnPrimaryContainer))
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
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
                Modifier.fillMaxWidth((pct / 100f).coerceIn(0f, 1f)).height(6.dp).clip(VShapes.Full).background(color),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("$pct% · $rating", style = VTypography.NavLabel.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}
