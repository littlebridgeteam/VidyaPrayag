package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Filter chip — active: on-surface bg. Inactive: surface-container bg. Scale 0.93 on press.
 *
 * HTML: .chip / .sub-tab / .discover-filter
 *   padding: 10px 18px; border-radius: var(--shape-full);
 *   .active { background: var(--on-surface); color: var(--surface); }
 *   .inactive { background: var(--surface-container); color: var(--on-surface-variant); }
 *   :active { transform: scale(0.93); }
 */
@Composable
fun VFilterChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val bg = if (active) VColors.OnSurface else VColors.SurfaceContainer
    val fg = if (active) VColors.Surface else VColors.OnSurfaceVariant

    Text(
        text = label,
        style = VTypography.Chip.copy(color = fg),
        modifier = modifier
            .clip(VShapes.Full)
            .background(bg)
            .pressScale(interaction, pressedScale = 0.93f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    )
}
