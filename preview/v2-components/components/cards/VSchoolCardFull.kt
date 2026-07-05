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
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * School card full — gradient header, rating pill, logo, tags, stats, action button.
 *
 * HTML: .school-card-full
 *   border-radius: var(--shape-2xl);
 *   :active { transform: scale(0.98); border-radius: var(--shape-xl); }
 */
@Composable
fun VSchoolCardFull(
    name: String,
    address: String,
    logoText: String,
    rating: String,
    tags: List<SchoolTag>,
    stats: List<SchoolStat>,
    headerGradient: List<androidx.compose.ui.graphics.Color>,
    onActionClick: () -> Unit,
    actionText: String = "Apply Now",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shapeMorph(interaction, VShapes.TwoXlDp, VShapes.XlDp, VMotion.DurMedium2)
            .pressScale(interaction, pressedScale = 0.98f)
            .clip(VShapes.TwoXl)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        // Header (140dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Brush.linearGradient(headerGradient)),
        ) {
            // Dark overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color(0x1A000000),
                                androidx.compose.ui.graphics.Color(0x8C000000),
                            ),
                        ),
                    ),
            )
            // Rating pill (top-right)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 14.dp, end = 14.dp)
                    .clip(VShapes.Full)
                    .background(VColors.GlassWhite95)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = rating,
                    style = VTypography.SchoolRating.copy(color = VColors.OnSurface),
                )
            }
            // Logo (bottom-left)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 14.dp)
                    .size(52.dp)
                    .clip(VShapes.Lg)
                    .background(VColors.GlassWhite95),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = logoText,
                    style = VTypography.SchoolOptionLogo.copy(color = VColors.Primary),
                )
            }
        }

        // Body
        Column(modifier = Modifier.padding(20.dp).background(VColors.SurfaceContainerLowest)) {
            Text(
                text = name,
                style = VTypography.SchoolName.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = address,
                style = VTypography.SchoolAddr.copy(color = VColors.OnSurfaceVariant),
            )
            Spacer(Modifier.height(14.dp))
            // Tags
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tags.forEach { tag ->
                    Text(
                        text = tag.label,
                        style = VTypography.SchoolTag.copy(color = tag.textColor),
                        modifier = Modifier
                            .clip(VShapes.Full)
                            .background(tag.bgColor)
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                stats.forEach { stat ->
                    Column {
                        Text(
                            text = stat.value,
                            style = VTypography.SchoolStatVal.copy(color = VColors.OnSurface),
                        )
                        Text(
                            text = stat.label,
                            style = VTypography.SchoolStatLabel.copy(color = VColors.OnSurfaceVariant),
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            VPrimaryButton(
                text = actionText,
                onClick = onActionClick,
            )
        }
    }
}

data class SchoolTag(val label: String, val bgColor: androidx.compose.ui.graphics.Color, val textColor: androidx.compose.ui.graphics.Color)
data class SchoolStat(val value: String, val label: String)
