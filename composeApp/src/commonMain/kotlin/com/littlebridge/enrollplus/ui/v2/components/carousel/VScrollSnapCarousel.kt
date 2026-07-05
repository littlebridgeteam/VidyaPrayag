package com.littlebridge.enrollplus.ui.v2.components.carousel

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
 * Scroll-snap carousel — horizontal scroll-snap, 280dp min-width cards, gap 16dp.
 *
 * HTML: .carousel
 *   display: flex; gap: 16px; overflow-x: auto; scroll-snap-type: x mandatory;
 *   .feature-card { min-width: 280px; scroll-snap-align: start; }
 *   .feature-card:active { transform: scale(0.97); border-radius: var(--shape-2xl); }
 */
@Composable
fun VScrollSnapCarousel(
    items: List<CarouselItem>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items) { item ->
            val interaction = remember { MutableInteractionSource() }
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .pressScale(interaction, pressedScale = 0.97f)
                    .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
                    .clip(VShapes.Xl)
                    .background(item.backgroundColor)
                    .clickable(interactionSource = interaction, indication = null) {
                        onItemClick(items.indexOf(item))
                    }
                    .padding(24.dp),
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(VShapes.Lg)
                        .background(item.iconBgColor),
                    contentAlignment = Alignment.Center,
                ) {
                    item.icon?.invoke()
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = item.title,
                    style = VTypography.FeatureTitle.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.subtitle,
                    style = VTypography.FeatureSubtitle.copy(color = VColors.OnSurfaceVariant),
                )
                Spacer(Modifier.height(20.dp))
                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.amount,
                        style = VTypography.FeatureAmount.copy(color = VColors.OnSurface),
                    )
                    // Badge button
                    val badgeInteraction = remember { MutableInteractionSource() }
                    Text(
                        text = item.badgeLabel,
                        style = VTypography.FeatureBadge.copy(color = item.badgeTextColor),
                        modifier = Modifier
                            .clip(VShapes.Full)
                            .background(item.badgeBgColor)
                            .pressScale(badgeInteraction, pressedScale = 0.95f)
                            .clickable(interactionSource = badgeInteraction, indication = null) {}
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

data class CarouselItem(
    val title: String,
    val subtitle: String,
    val amount: String,
    val badgeLabel: String,
    val backgroundColor: Color,
    val iconBgColor: Color,
    val badgeBgColor: Color,
    val badgeTextColor: Color,
    val icon: (@Composable () -> Unit)? = null,
)
