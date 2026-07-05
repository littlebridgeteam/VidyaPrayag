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
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Trust badge — icon + label chip, surface-container-low bg.
 *
 * HTML: .trust-badge
 *   padding: 8px 14px; border-radius: var(--shape-full);
 *   background: var(--surface-container-low);
 *   font-size: 12px; font-weight: 600; color: var(--on-surface-variant);
 *   svg { width: 14px; height: 14px; color: var(--tertiary); }
 */
@Composable
fun VTrustBadge(
    label: String,
    icon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(VShapes.Full)
            .background(VColors.SurfaceContainerLow)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
        }
        Text(
            text = label,
            style = VTypography.TrustBadge.copy(color = VColors.OnSurfaceVariant),
        )
    }
}
