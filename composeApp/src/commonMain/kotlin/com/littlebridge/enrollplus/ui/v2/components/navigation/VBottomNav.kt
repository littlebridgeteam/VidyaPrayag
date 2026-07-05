package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

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
            .background(VColors.SurfaceContainer)
            .padding(horizontal = 8.dp)
            .navigationBarsPadding()
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val isActive = index == activeIndex
            val interaction = remember { MutableInteractionSource() }

            Column(
                modifier = Modifier
                    .clip(VShapes.Lg)
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                    ) { onItemClick(index) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(VShapes.Full)
                        .background(if (isActive) VColors.SecondaryContainer else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (item.iconVector != null) {
                        Icon(
                            imageVector = item.iconVector,
                            contentDescription = item.label,
                            tint = if (isActive) VColors.OnSecondaryContainer else VColors.OnSurfaceVariant,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        item.icon?.invoke()
                    }
                }

                AnimatedVisibility(
                    visible = isActive,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f),
                ) {
                    Text(
                        text = item.label,
                        style = VTypography.NavLabelActive.copy(
                            color = VColors.OnSecondaryContainer,
                        ),
                    )
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val badgeCount: Int = 0,
    val iconVector: ImageVector? = null,
    val icon: (@Composable () -> Unit)? = null,
)
