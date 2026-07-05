package com.littlebridge.enrollplus.ui.v2.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

enum class VBadgeTonePremium { Primary, Tertiary, Error, Warning, Neutral }

@Composable
fun VBadgePremium(
    text: String,
    modifier: Modifier = Modifier,
    tone: VBadgeTonePremium = VBadgeTonePremium.Primary,
    leadingIcon: ImageVector? = null,
) {
    val bg: Color
    val fg: Color
    when (tone) {
        VBadgeTonePremium.Primary -> {
            bg = VColors.PrimaryContainer
            fg = VColors.OnPrimaryContainer
        }
        VBadgeTonePremium.Tertiary -> {
            bg = VColors.TertiaryContainer
            fg = VColors.OnTertiaryContainer
        }
        VBadgeTonePremium.Error -> {
            bg = VColors.ErrorContainer
            fg = VColors.OnErrorContainer
        }
        VBadgeTonePremium.Warning -> {
            bg = VColors.WarmOrangeContainer
            fg = VColors.WarmOrangeDarkest
        }
        VBadgeTonePremium.Neutral -> {
            bg = VColors.SurfaceContainerHigh
            fg = VColors.OnSurfaceVariant
        }
    }
    Row(
        modifier = modifier
            .clip(VShapes.Full)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            androidx.compose.material3.Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.padding(end = 0.dp),
            )
        }
        Text(
            text = text,
            style = VTypography.LivePill.copy(color = fg),
        )
    }
}
