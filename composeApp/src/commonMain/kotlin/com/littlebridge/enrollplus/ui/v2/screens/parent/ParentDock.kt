package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VNavItem

/**
 * ParentDock — clean, full-width bottom tab bar.
 *
 * - No floating pill, no bounce, no spring animation.
 * - Selected item shows label; unselected shows icon only.
 * - Equal-width columns so labels never truncate.
 */
@Composable
fun ParentDock(
    items: List<VNavItem>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard)
            .border(width = 1.dp, color = VColors.line, shape = RoundedCornerShape(0.dp))
            .navigationBarsPadding()
            .padding(top = 8.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val active = item.id == selected
            DockItem(
                item = item,
                active = active,
                onClick = { onSelect(item.id) },
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
private fun DockItem(
    item: VNavItem,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (active) VColors.violet else VColors.ink3
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .height(58.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp)
            .padding(top = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (active) VColors.violetSoft else VColors.surfaceCard)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            BadgedBox(
                badge = {
                    if (item.badge > 0) {
                        Badge(
                            containerColor = VColors.error,
                            contentColor = VColors.white,
                            modifier = Modifier.size(16.dp),
                        ) {
                            Text(
                                text = if (item.badge > 99) "99+" else item.badge.toString(),
                                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Text(
            text = item.label,
            style = VTypography.caption.copy(
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 11.sp,
            ),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
