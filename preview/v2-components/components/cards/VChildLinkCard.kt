package com.littlebridge.enrollplus.ui.v2.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Child link card — avatar, info, linked badge.
 *
 * HTML: .child-card
 *   padding: 24px 20px; border-radius: var(--shape-xl);
 *   background: var(--surface-container-lowest);
 *   .child-avatar { background: linear-gradient(135deg, var(--primary), var(--tertiary)); }
 *   .linked-badge { background: var(--tertiary-container); color: var(--on-tertiary-container); }
 */
@Composable
fun VChildLinkCard(
    initials: String,
    name: String,
    meta: String,
    linked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(VShapes.Full)
                    .background(Brush.linearGradient(listOf(VColors.Primary, VColors.Tertiary))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = VTypography.ChildName.copy(color = androidx.compose.ui.graphics.Color.White),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = VTypography.ChildName.copy(color = VColors.OnSurface),
                )
                Text(
                    text = meta,
                    style = VTypography.ChildMeta.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }
        if (linked) {
            Row(
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(VColors.TertiaryContainer)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "✓",
                    style = VTypography.LinkedBadge.copy(color = VColors.OnTertiaryContainer),
                )
                Text(
                    text = "Linked",
                    style = VTypography.LinkedBadge.copy(color = VColors.OnTertiaryContainer),
                )
            }
        }
    }
}
