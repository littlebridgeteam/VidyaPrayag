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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Stat tile — value + label, surface-container bg, trend indicator.
 *
 * HTML: .stat-card
 *   background: var(--surface-container-lowest); border-radius: var(--shape-xl);
 *   padding: 20px;
 *   :active { transform: scale(0.96); border-radius: var(--shape-2xl); }
 */
@Composable
fun VStatTile(
    value: String,
    label: String,
    trend: String? = null,
    trendUp: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .pressScale(interaction, pressedScale = 0.96f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(20.dp),
    ) {
        Text(
            text = value,
            style = VTypography.StatValue.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = VTypography.StatLabel.copy(color = VColors.OnSurfaceVariant),
        )
        if (trend != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = trend,
                style = VTypography.StatTrend.copy(
                    color = if (trendUp) VColors.Tertiary else VColors.Error,
                ),
            )
        }
    }
}
