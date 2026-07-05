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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.tokens.rememberLivePulse

/**
 * Hero card — gradient bg, radial glows, live pill, avatar, stats grid.
 *
 * HTML: .hero-card
 *   border-radius: var(--shape-2xl);
 *   background: linear-gradient(140deg, var(--primary) 0%, #544AB8 50%, #3D35A0 100%);
 *   :active { border-radius: var(--shape-xl); }
 *   ::before — radial glow top-right (rgba(205,189,255,0.25))
 *   ::after — radial glow bottom-left (rgba(0,191,160,0.12))
 */
@Composable
fun VHeroCard(
    studentInitials: String,
    studentName: String,
    studentClass: String,
    stats: List<HeroStat>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    liveLabel: String = "LIVE",
    onIconClick: () -> Unit = {},
    iconContent: (@Composable () -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val (ringScale, ringAlpha) = rememberLivePulse()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shapeMorph(interaction, VShapes.TwoXlDp, VShapes.XlDp, VMotion.DurLong1)
            .pressScale(interaction, pressedScale = 1f)
            .background(
                Brush.linearGradient(
                    colors = listOf(VColors.Primary, VColors.PrimaryMid, VColors.PrimaryDeep),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.MAX_VALUE, Float.MAX_VALUE),
                ),
            )
            .radialGlow(offsetX = 280.dp, offsetY = (-100).dp, radius = 280.dp, color = VColors.HeroGlowTopRight)
            .radialGlow(offsetX = (-80).dp, offsetY = 600.dp, radius = 240.dp, color = VColors.HeroGlowBottomLeft)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            // Top row: live pill + glass icon button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Live pill
                Row(
                    modifier = Modifier
                        .clip(VShapes.Full)
                        .background(VColors.GlassWhite15)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Live dot with pulse
                    Box {
                        Box(
                            modifier = Modifier
                                .size((ringScale * 2).dp)
                                .clip(CircleShape)
                                .background(VColors.LiveCyan.copy(alpha = ringAlpha)),
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(VColors.LiveCyan),
                        )
                    }
                    Text(
                        text = liveLabel,
                        style = VTypography.LivePill.copy(color = VColors.OnPrimary),
                    )
                }
                // Glass icon button — .hero-icon-btn: 44dp, rgba(255,255,255,0.12)
                val iconInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(VColors.GlassWhite12)
                        .pressScale(iconInteraction, pressedScale = 0.9f)
                        .clickable(interactionSource = iconInteraction, indication = null, onClick = onIconClick),
                    contentAlignment = Alignment.Center,
                ) {
                    iconContent?.invoke()
                }
            }

            Spacer(Modifier.height(24.dp))

            // Student row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(VShapes.Xl)
                        .background(VColors.GlassWhite20)
                        .clip(VShapes.Xl),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = studentInitials,
                        style = VTypography.HeroName.copy(color = VColors.OnPrimary),
                    )
                }
                Column {
                    Text(
                        text = studentName,
                        style = VTypography.HeroName.copy(color = VColors.OnPrimary),
                    )
                    Text(
                        text = studentClass,
                        style = VTypography.HeroSubtitle.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Stats grid (3 columns)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Lg)
                    .background(VColors.White08),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                stats.forEach { stat ->
                    val statInteraction = remember { MutableInteractionSource() }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(VShapes.Lg)
                            .background(VColors.White06)
                            .pressScale(statInteraction, pressedScale = 0.95f)
                            .clickable(interactionSource = statInteraction, indication = null) {}
                            .padding(vertical = 18.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stat.value,
                            style = VTypography.HeroStatValue.copy(color = VColors.OnPrimary),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stat.label,
                            style = VTypography.HeroStatLabel.copy(color = VColors.OnPrimary.copy(alpha = 0.55f)),
                        )
                    }
                }
            }
        }
    }
}

data class HeroStat(val value: String, val label: String)
