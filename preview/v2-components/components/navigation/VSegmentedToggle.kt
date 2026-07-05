package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Segmented toggle — parent/teacher switch, pill container, active/inactive states.
 *
 * HTML: .signup-type
 *   border-radius: var(--shape-full); background: var(--surface-container); padding: 4px;
 *   .signup-type-btn.active { background: var(--surface-container-lowest); box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
 *
 * Also matches .seg-btn (conversations) and .conv-segment patterns.
 */
@Composable
fun VSegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(VShapes.Full)
            .background(VColors.SurfaceContainer)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        options.forEachIndexed { index, label ->
            val isActive = index == selectedIndex
            val interaction = remember { MutableInteractionSource() }
            val bg = if (isActive) VColors.SurfaceContainerLowest else androidx.compose.ui.graphics.Color.Transparent
            val fg = if (isActive) VColors.OnSurface else VColors.OnSurfaceVariant

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(VShapes.Full)
                    .background(bg)
                    .clickable(interactionSource = interaction, indication = null) { onSelect(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = VTypography.SignupTypeBtn.copy(color = fg),
                )
            }
        }
    }
}
