package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentLibraryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Library", onBack = onBack, modifier = modifier) {
        VStaggeredItem(delayMs = 0) {
            Text("Books borrowed by your child.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        }
        Spacer(Modifier.height(20.dp))
        VStaggeredItem(delayMs = 60) { LibraryBookRow("Mathematics Textbook", "Borrowed: Jan 10", "Due: Feb 10") }
        Spacer(Modifier.height(8.dp))
        VStaggeredItem(delayMs = 120) { LibraryBookRow("Science Explorer", "Borrowed: Jan 15", "Due: Feb 15") }
        Spacer(Modifier.height(8.dp))
        VStaggeredItem(delayMs = 180) { LibraryBookRow("English Literature", "Borrowed: Jan 20", "Due: Feb 20") }
    }
}

@Composable
private fun LibraryBookRow(title: String, borrowed: String, due: String) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { /* TODO: view book detail */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Lg).background(VColors.TertiaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = VColors.OnTertiaryContainer, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(borrowed, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            Text(due, style = VTypography.NavLabel.copy(color = VColors.WarmOrange))
        }
    }
}
