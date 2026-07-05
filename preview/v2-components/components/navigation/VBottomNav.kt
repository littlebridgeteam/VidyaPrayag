package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Bottom navigation bar — 5 items, icon+label, active pill bg, badge count, 84dp height.
 *
 * HTML: .nav-bar
 *   height: 84px; background: var(--surface-container-lowest);
 *   display: flex; justify-content: space-around;
 *   .nav-item.active { background: var(--primary-container); }
 *   .nav-item:active { transform: scale(0.93); }
 *   .nav-badge { background: var(--error); color: #fff; font-size: 10px; font-weight: 800; }
 */
@Composable
fun VBottomNav(
    items: List<NavItem>,
    activeIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(VColors.SurfaceContainerLowest)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val isActive = index == activeIndex
            val interaction = remember { MutableInteractionSource() }
            val bg = if (isActive) VColors.PrimaryContainer else Color.Transparent
            val iconColor = if (isActive) VColors.Primary else VColors.OnSurfaceVariant
            val labelColor = if (isActive) VColors.OnPrimaryContainer else VColors.OnSurfaceVariant
            val labelStyle = if (isActive) VTypography.NavLabelActive else VTypography.NavLabel

            Box(
                modifier = Modifier
                    .clip(VShapes.Lg)
                    .background(bg)
                    .pressScale(interaction, pressedScale = 0.93f)
                    .clickable(interactionSource = interaction, indication = null) { onItemClick(index) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        item.icon?.invoke()
                        if (item.badgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(VColors.Error)
                                    .padding(horizontal = 5.dp, vertical = 0.dp)
                                    .height(18.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = item.badgeCount.toString(),
                                    style = VTypography.NavBadge.copy(color = Color.White),
                                )
                            }
                        }
                    }
                    Text(
                        text = item.label,
                        style = labelStyle.copy(color = labelColor),
                    )
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val badgeCount: Int = 0,
    val icon: (@Composable () -> Unit)? = null,
)
