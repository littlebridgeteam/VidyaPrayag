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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Profile hero card — gradient, avatar, name, class, badges, XP bar.
 *
 * HTML: .profile-hero
 *   border-radius: var(--shape-2xl);
 *   background: linear-gradient(140deg, var(--primary) 0%, #544AB8 50%, #3D35A0 100%);
 *   :active { border-radius: var(--shape-xl); }
 */
@Composable
fun VProfileHeroCard(
    initials: String,
    name: String,
    className: String,
    levelText: String,
    xpText: String,
    xpProgress: Float,
    badge: String? = null,
    onClick: () -> Unit,
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
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(VShapes.Xl)
                        .background(VColors.GlassWhite20),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initials,
                        style = VTypography.HeroName.copy(color = VColors.OnPrimary),
                    )
                }
                Column {
                    Text(
                        text = name,
                        style = VTypography.ProfileName.copy(color = VColors.OnPrimary),
                    )
                    Text(
                        text = className,
                        style = VTypography.ProfileMeta.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)),
                    )
                    if (badge != null) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(VShapes.Full)
                                .background(VColors.GlassWhite15)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = badge,
                                style = VTypography.ProfileBadge.copy(color = VColors.OnPrimary),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Level + XP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = levelText,
                    style = VTypography.ProfileLevelText.copy(color = VColors.OnPrimary),
                )
                Text(
                    text = xpText,
                    style = VTypography.ProfileXp.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)),
                )
            }
            Spacer(Modifier.height(8.dp))
            // XP bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(VShapes.Full)
                    .background(VColors.GlassWhite15),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(xpProgress)
                        .height(8.dp)
                        .clip(VShapes.Full)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(VColors.XpBarStart, VColors.XpBarEnd),
                            ),
                        ),
                )
            }
        }
    }
}
