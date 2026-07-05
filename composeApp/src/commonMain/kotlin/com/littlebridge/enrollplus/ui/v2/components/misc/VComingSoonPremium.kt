package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.cards.VBadgePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VBadgeTonePremium
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun VComingSoonPremium(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onNotifyMe: (() -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .radialGlow(120.dp, (-40).dp, 200.dp, VColors.LandingGlowPrimary)
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        VBadgePremium(text = "PREVIEW", tone = VBadgeTonePremium.Tertiary)
        if (icon != null) {
            Box(
                Modifier.size(56.dp).clip(VShapes.Full).background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(26.dp))
            }
        }
        Text(title, style = VTypography.SectionHeader.copy(color = VColors.OnSurface), textAlign = TextAlign.Center)
        Text(description, style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant), textAlign = TextAlign.Center)
        if (onNotifyMe != null) {
            Box(
                Modifier
                    .pressScale(interaction, pressedScale = 0.95f)
                    .shapeMorph(interaction, VShapes.FullDp, VShapes.MdDp, VMotion.DurShort2)
                    .clip(VShapes.Full)
                    .background(VColors.SurfaceContainerHigh)
                    .clickable(interactionSource = interaction, indication = null, onClick = onNotifyMe)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text("Notify me when ready", style = VTypography.UpdateAction.copy(color = VColors.OnSurfaceVariant))
            }
        }
    }
}
