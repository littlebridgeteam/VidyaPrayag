package com.littlebridge.enrollplus.ui.v2.components.cards

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Role tile — gradient bg, icon box, name, desc, chevron arrow.
 *
 * HTML: .role-tile
 *   border-radius: var(--shape-xl); padding: 20px;
 *   background: var(--surface-container-lowest);
 *   :active { transform: scale(0.97); border-radius: var(--shape-2xl); }
 *   .role-tile.parent { background: linear-gradient(135deg, var(--primary-container) 0%, var(--surface-container-lowest) 60%); }
 *   .role-tile.staff { background: linear-gradient(135deg, var(--tertiary-container) 0%, var(--surface-container-lowest) 60%); }
 */
@Composable
fun VRoleTile(
    name: String,
    description: String,
    icon: (@Composable () -> Unit)? = null,
    isParent: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val gradient = if (isParent) {
        listOf(VColors.PrimaryContainer, VColors.SurfaceContainerLowest)
    } else {
        listOf(VColors.TertiaryContainer, VColors.SurfaceContainerLowest)
    }
    val iconGradient = if (isParent) {
        listOf(VColors.Primary, VColors.PrimaryMid)
    } else {
        listOf(VColors.Tertiary, VColors.TertiaryDeep)
    }
    val glowColor = if (isParent) {
        Color(0x1A6750F6) // rgba(103,80,246,0.1)
    } else {
        Color(0x1A00BFA0) // rgba(0,191,160,0.1)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, pressedScale = 0.97f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clip(VShapes.Xl)
            .background(Brush.linearGradient(gradient))
            .radialGlow(offsetX = 300.dp, offsetY = (-30).dp, radius = 100.dp, color = glowColor)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Icon box
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(VShapes.Lg)
                .background(Brush.linearGradient(iconGradient)),
            contentAlignment = Alignment.Center,
        ) {
            icon?.invoke()
        }
        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = VTypography.RoleTileName.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = description,
                style = VTypography.RoleTileDesc.copy(color = VColors.OnSurfaceVariant),
            )
        }
        // Arrow
        Text(
            text = "›",
            style = VTypography.RoleTileName.copy(color = VColors.Outline),
        )
    }
}
