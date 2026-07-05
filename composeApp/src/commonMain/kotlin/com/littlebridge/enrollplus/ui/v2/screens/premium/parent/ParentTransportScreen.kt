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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentTransportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    childId: String = "",
) {
    ParentOverlayScaffold(title = "Transport Tracking", onBack = onBack, modifier = modifier) {
        VStaggeredItem(delayMs = 0) {
            // Live banner
            Row(
                Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.TertiaryContainer).padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(VColors.Tertiary), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = VColors.OnTertiary, modifier = Modifier.size(24.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("Bus arriving in 8 minutes", style = VTypography.UpdateTitle.copy(color = VColors.OnTertiaryContainer, fontWeight = FontWeight.Bold))
                    Text("Route 12 · GPS tracking active", style = VTypography.NavLabel.copy(color = VColors.OnTertiaryContainer.copy(alpha = 0.7f)))
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        VStaggeredItem(delayMs = 80) {
            // Route timeline card
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                Text("Route 12 — DPS to Home", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))

                // Stop 1 — School (departed)
                TimelineStop("School — DPS", "Departed 3:30 PM", VColors.Tertiary, false)
                Spacer(Modifier.height(12.dp))

                // Stop 2 — Current location
                TimelineStop("Stop 3 — MG Road", "Current location · ETA 8 min", VColors.Primary, true)
                Spacer(Modifier.height(12.dp))

                // Stop 3 — Home (upcoming)
                TimelineStop("Home — Sector 14", "ETA 3:52 PM", VColors.Outline, false, dimmed = true)
            }
        }
        Spacer(Modifier.height(20.dp))

        VStaggeredItem(delayMs = 160) {
            // Bus details card
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                Text("Bus Details", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(12.dp))
                Text("Driver: Ramesh Yadav\nBus: DL-01-AB-4521\nPhone: +91 98xxx xxx21", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
        }
        Spacer(Modifier.height(20.dp))

        VStaggeredItem(delayMs = 240) {
            // Map placeholder
            Box(
                Modifier.fillMaxWidth().height(180.dp).clip(VShapes.Xl).background(VColors.SurfaceContainerLow),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Map view", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
                    Text("GPS tracking active", style = VTypography.NavLabel.copy(color = VColors.Outline))
                }
            }
        }
    }
}

@Composable
private fun TimelineStop(
    name: String,
    detail: String,
    dotColor: androidx.compose.ui.graphics.Color,
    isCurrent: Boolean,
    dimmed: Boolean = false,
) {
    val alpha = if (dimmed) 0.4f else 1f
    Row(
        Modifier.fillMaxWidth().then(if (dimmed) Modifier.alpha(alpha) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(dotColor)
                .then(if (isCurrent) Modifier.padding(4.dp) else Modifier),
        ) {
            if (isCurrent) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(alpha = 0.2f)),
                )
            }
        }
        Column {
            Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp))
            Text(detail, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
    }
}
