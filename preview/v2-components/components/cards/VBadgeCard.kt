package com.littlebridge.enrollplus.ui.v2.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SweepGradient
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Badge card — earned: conic-gradient ring + icon. Locked: progress bar.
 *
 * HTML: .badge-card (168px wide)
 *   .badge-card.earned .badge-icon-ring {
 *     background: conic-gradient(from 0deg, var(--primary), var(--tertiary), var(--primary));
 *     padding: 3px;
 *   }
 *   .badge-card.locked .badge-icon-ring { background: var(--surface-container-high); }
 */
@Composable
fun VBadgeCard(
    name: String,
    description: String,
    earned: Boolean,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    progressText: String = "",
) {
    val interaction = remember { MutableInteractionSource() }
    val bgColor = if (earned) VColors.SurfaceContainerLowest else VColors.SurfaceContainerLow

    Box(
        modifier = modifier
            .width(168.dp)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .pressScale(interaction, pressedScale = 0.96f)
            .clip(VShapes.Xl)
            .background(bgColor)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        Column {
            // Badge top
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (earned) {
                        // Conic gradient ring (approximated with SweepGradient)
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(
                                        colors = listOf(
                                            VColors.Primary, VColors.Tertiary, VColors.Primary,
                                        ),
                                    ),
                                )
                                .padding(3.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(VColors.SurfaceContainerLowest),
                                contentAlignment = Alignment.Center,
                            ) {
                                icon?.invoke()
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(VColors.SurfaceContainerHigh),
                            contentAlignment = Alignment.Center,
                        ) {
                            icon?.invoke()
                        }
                    }
                }
                Text(
                    text = name,
                    style = VTypography.BadgeName.copy(color = VColors.OnSurface),
                )
            }

            // Badge body
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = description,
                    style = VTypography.BadgeDesc.copy(color = VColors.OnSurfaceVariant),
                )
                if (earned) {
                    Text(
                        text = "EARNED",
                        style = VTypography.BadgeEarnedTag.copy(color = VColors.Tertiary),
                    )
                } else {
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(VShapes.Full)
                            .background(VColors.SurfaceContainerHigh),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(5.dp)
                                .clip(VShapes.Full)
                                .background(VColors.Primary),
                        )
                    }
                    Text(
                        text = progressText,
                        style = VTypography.BadgeProgressText.copy(color = VColors.OnSurfaceVariant),
                    )
                }
            }
        }
    }
}
