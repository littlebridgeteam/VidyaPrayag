package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentEventsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "School Events", onBack = onBack, modifier = modifier) {
        VStaggeredItem(delayMs = 0) {
            Text("Upcoming events at your child's school.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        }
        Spacer(Modifier.height(20.dp))
        VStaggeredItem(delayMs = 60) {
            EmptyStateCard(
                title = "No Upcoming Events",
                body = "School events will appear here when scheduled.",
                icon = Icons.AutoMirrored.Filled.MenuBook,
            )
        }
    }
}

@Composable
private fun EventCard(title: String, date: String, venue: String, time: String) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(20.dp),
    ) {
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column { Text("Date", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant)); Text(date, style = VTypography.UpdateText.copy(color = VColors.OnSurface)) }
            Column { Text("Time", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant)); Text(time, style = VTypography.UpdateText.copy(color = VColors.OnSurface)) }
        }
        Spacer(Modifier.height(8.dp))
        Text("Venue: $venue", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Register", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
