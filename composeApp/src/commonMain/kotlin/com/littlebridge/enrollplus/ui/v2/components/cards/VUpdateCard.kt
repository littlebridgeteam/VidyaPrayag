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
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Update card — avatar, source, title, text, timestamp, action buttons.
 *
 * HTML: .update-item
 *   background: var(--surface-container-lowest); border-radius: var(--shape-xl);
 *   :active { transform: scale(0.98); border-radius: var(--shape-2xl); }
 */
@Composable
fun VUpdateCard(
    source: String,
    timestamp: String,
    title: String,
    text: String,
    avatarIcon: (@Composable () -> Unit)? = null,
    actions: List<UpdateAction> = emptyList(),
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        // Top section
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(VShapes.Md)
                    .background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                avatarIcon?.invoke()
            }
            // Body
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = source,
                        style = VTypography.UpdateSource.copy(color = VColors.OnSurfaceVariant),
                    )
                    Text(
                        text = timestamp,
                        style = VTypography.UpdateTime.copy(color = VColors.Outline),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = title,
                    style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = text,
                    style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }
        // Action buttons
        if (actions.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(start = 74.dp, end = 20.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                actions.forEach { action ->
                    val actInteraction = remember { MutableInteractionSource() }
                    val bg = if (action.isPrimary) VColors.Primary else VColors.SurfaceContainerLow
                    val fg = if (action.isPrimary) VColors.OnPrimary else VColors.OnSurfaceVariant
                    Text(
                        text = action.label,
                        style = VTypography.UpdateAction.copy(color = fg),
                        modifier = Modifier
                            .clip(VShapes.Full)
                            .background(bg)
                            .pressScale(actInteraction, pressedScale = 0.95f)
                            .clickable(interactionSource = actInteraction, indication = null, onClick = action.onClick)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}

data class UpdateAction(val label: String, val isPrimary: Boolean, val onClick: () -> Unit)
