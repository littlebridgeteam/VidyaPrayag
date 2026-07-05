package com.littlebridge.enrollplus.ui.v2.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VLandingButton
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Fees hero card — gradient, amount, due date, glass pay button.
 *
 * HTML: .fees-hero
 *   border-radius: var(--shape-2xl);
 *   background: linear-gradient(140deg, var(--primary) 0%, #544AB8 50%, #3D35A0 100%);
 *   :active { border-radius: var(--shape-xl); }
 */
@Composable
fun VFeesHeroCard(
    label: String,
    amount: String,
    dueDate: String,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shapeMorph(interaction, VShapes.TwoXlDp, VShapes.XlDp, VMotion.DurLong1)
            .background(
                Brush.linearGradient(
                    colors = listOf(VColors.Primary, VColors.PrimaryMid, VColors.PrimaryDeep),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.MAX_VALUE, Float.MAX_VALUE),
                ),
            )
            .radialGlow(offsetX = 220.dp, offsetY = (-80).dp, radius = 220.dp, color = VColors.FeesGlowTopRight)
            .clickable(interactionSource = interaction, indication = null, onClick = onPayClick),
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Text(
                text = label,
                style = VTypography.FeesHeroLabel.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = amount,
                style = VTypography.FeesAmount.copy(color = VColors.OnPrimary),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = dueDate,
                style = VTypography.FeesDue.copy(color = VColors.OnPrimary.copy(alpha = 0.8f)),
            )
            Spacer(Modifier.height(24.dp))
            VLandingButton(
                text = "Pay Now",
                onClick = onPayClick,
                glass = true,
            )
        }
    }
}
