package com.littlebridge.enrollplus.ui.v2.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

/**
 * Surface card — generic surface-container-lowest, shape-xl, press animation.
 *
 * Used as the base for marks cards, homework cards, payment items, account rows, etc.
 *
 * HTML: .mark-card / .hw-card / .payment-item / .account-row
 *   background: var(--surface-container-lowest); border-radius: var(--shape-xl);
 *   :active { transform: scale(0.98); border-radius: var(--shape-2xl); }
 */
@Composable
fun VSurfaceCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = VColors.SurfaceContainerLowest,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    var base = modifier
        .fillMaxWidth()
        .pressScale(interaction, pressedScale = 0.98f)
        .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
        .clip(VShapes.Xl)
        .background(backgroundColor)

    if (onClick != null) {
        base = base.clickable(interactionSource = interaction, indication = null, onClick = onClick)
    }

    Column(
        modifier = base.padding(horizontal = 20.dp, vertical = 18.dp),
        content = content,
    )
}
