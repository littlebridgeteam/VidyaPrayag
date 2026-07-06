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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

data class HeroStatPremium(val value: String, val label: String)

@Composable
fun VGradientHeroPremium(
    title: String,
    subtitle: String,
    stats: List<HeroStatPremium>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    livePillText: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val onPrimary = VColors.OnPrimary
    val primary = VColors.Primary
    val primaryMid = VColors.PrimaryMid
    val primaryDeep = VColors.PrimaryDeep
    val heroGlow = VColors.HeroGlowTopRight
    val tertiaryGlow = VColors.HeroGlowBottomLeft
    val glassWhite15 = VColors.GlassWhite15

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.TwoXlDp, VShapes.XlDp, VMotion.DurLong1)
            .clip(VShapes.TwoXl)
            .background(Brush.linearGradient(listOf(primary, primaryMid, primaryDeep)))
            .radialGlow(280.dp, (-100).dp, 280.dp, heroGlow)
            .radialGlow((-80).dp, 300.dp, 240.dp, tertiaryGlow)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (livePillText != null) {
                    Row(
                        modifier = Modifier
                            .clip(VShapes.Full)
                            .background(glassWhite15)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.size(8.dp).clip(VShapes.Full).background(VColors.LiveCyan),
                        )
                        Text(livePillText, style = VTypography.LivePill.copy(color = onPrimary))
                    }
                }
                if (trailingIcon != null) {
                    trailingIcon()
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingContent != null) {
                    leadingContent()
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = VTypography.HeroName.copy(color = onPrimary))
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, style = VTypography.HeroSubtitle.copy(color = onPrimary.copy(alpha = 0.7f)))
                }
            }
            if (stats.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.Lg)
                        .background(VColors.White08),
                ) {
                    stats.forEachIndexed { index, stat ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 18.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(stat.value, style = VTypography.HeroStatValue.copy(color = onPrimary))
                            Spacer(Modifier.height(4.dp))
                            Text(stat.label, style = VTypography.HeroStatLabel.copy(color = onPrimary.copy(alpha = 0.55f)))
                        }
                        if (index < stats.size - 1) {
                            Box(
                                Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(onPrimary.copy(alpha = 0.12f)),
                            )
                        }
                    }
                }
            }
        }
    }
}
