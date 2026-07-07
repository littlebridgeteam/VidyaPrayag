package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VNavItem

/**
 * ParentDock — the rebuilt, premium parent bottom navigation.
 *
 * Calm, tactile, and always legible:
 * - White glass bar floating on the cream canvas with a subtle shadow.
 * - Every tab shows icon + label so parents never hunt for meaning.
 * - Active tab sits inside a soft violet lozenge; no bouncing springs.
 * - Smooth damped tween motion only; single haptic tick on selection.
 * - 48dp minimum touch targets; real unread badges ride the icons.
 */
@Composable
fun ParentDock(
    items: List<VNavItem>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    val itemXs = remember { mutableStateMapOf<String, Dp>() }
    val itemWidths = remember { mutableStateMapOf<String, Dp>() }
    val targetX = itemXs[selected] ?: 0.dp
    val targetW = itemWidths[selected] ?: 0.dp

    val pillX by animateDpAsState(
        targetValue = targetX,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "dockPillX",
    )
    val pillW by animateDpAsState(
        targetValue = targetW,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "dockPillW",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = VShapes.xxl,
                    ambientColor = VColors.ink.copy(alpha = 0.05f),
                    spotColor = VColors.ink.copy(alpha = 0.07f),
                )
                .clip(VShapes.xxl)
                .background(VColors.surfaceCard)
                .border(1.dp, VColors.line, VShapes.xxl)
                .padding(horizontal = 6.dp, vertical = 6.dp),
        ) {
            if (pillW > 0.dp) {
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = pillX)
                        .width(pillW)
                        .height(48.dp)
                        .clip(VShapes.full)
                        .background(VColors.violetSoft),
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEach { item ->
                    val active = item.id == selected
                    DockItem(
                        item = item,
                        active = active,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .onGloballyPositioned { coords ->
                                itemXs[item.id] = with(density) { coords.boundsInParent().left.toDp() }
                                itemWidths[item.id] = with(density) { coords.size.width.toDp() }
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                if (!active) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelect(item.id)
                                }
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun DockItem(
    item: VNavItem,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val iconTint = if (active) VColors.violet else VColors.ink3
    val labelColor = if (active) VColors.violet else VColors.ink3

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
            if (item.badge > 0) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(VColors.coral)
                        .border(1.5.dp, VColors.surfaceCard, CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = if (item.badge > 9) "9+" else item.badge.toString(),
                        style = VTypography.caption.copy(
                            fontWeight = FontWeight.Bold,
                            color = VColors.white,
                        ),
                    )
                }
            }
        }
        Text(
            text = item.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            style = VTypography.label.copy(
                fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                color = labelColor,
            ),
            modifier = Modifier.padding(start = 5.dp),
        )
    }
}
