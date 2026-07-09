package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VNavItem
import com.littlebridge.enrollplus.ui.v2.theme.VElevationLevel
import com.littlebridge.enrollplus.ui.v2.theme.vElevation

/**
 * TeacherDock — the Teacher Portal's signature **floating dock**, REBUILT for a more
 * premium, system-native feel (a modern filled-capsule navigation bar):
 *
 *   • A SOLID violet gradient capsule glides horizontally under the active tab and
 *     grows to seat the tab label beside its icon. Because it is a real filled pill
 *     (not a faint tint), the active tab reads instantly — the same language as
 *     Material-3 / iOS segmented navigation.
 *   • The active icon + label are painted WHITE and sit ON the capsule; resting tabs
 *     are quiet grey glyphs. The active glyph springs (scale + lift); selection fires
 *     one crisp haptic tick.
 *   • The bar itself is clean near-white glass with a soft top highlight, a hairline
 *     ring and a raised shadow, floating over a cream band that covers the system
 *     navigation-bar inset (so no lavender bleeds through).
 *   • Real obligation badges ride the icons; a badge on the active tab flips to a
 *     white pill so it stays legible on the violet capsule.
 */
@Composable
fun TeacherDock(
    items: List<VNavItem>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = VtC
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val accent = c.accentDeep

    // Each tab reports its bounds so the lozenge can slide+resize toward the active tab.
    val itemXs = remember { mutableStateMapOf<String, Dp>() }
    val itemWidths = remember { mutableStateMapOf<String, Dp>() }
    val targetX = itemXs[selected] ?: 0.dp
    val targetW = itemWidths[selected] ?: 0.dp
    val pillX by animateDpAsState(
        targetValue = targetX,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "tDockPillX",
    )
    val pillW by animateDpAsState(
        targetValue = targetW,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "tDockPillW",
    )

    Box(
        modifier
            .fillMaxWidth()
            // Paint the same warm cream the tabs use across the WHOLE dock band
            // (including the system navigation-bar inset) so the lavender scaffold
            // background never bleeds through as a purple bar behind the floating
            // dock. A soft top-fade blends the band into the scrolling content.
            .background(
                Brush.verticalGradient(
                    0f to VColors.cream.copy(alpha = 0f),
                    0.45f to VColors.cream,
                    1f to VColors.cream,
                ),
            )
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .vElevation(VElevationLevel.Raised, radius = 32.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(c.card.copy(alpha = if (c.isNight) 1f else 0.99f))
                .drawBehind {
                    // Soft top sheen for a glassy, dimensional surface.
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = if (c.isNight) 0.05f else 0.7f), Color.Transparent),
                            endY = size.height * 0.55f,
                        ),
                    )
                }
                .border(1.dp, c.hairline, RoundedCornerShape(32.dp))
                .padding(horizontal = 7.dp, vertical = 7.dp),
        ) {
            // The gliding SOLID capsule — a real filled violet pill that seats the
            // active tab's icon + label. It springs horizontally and resizes as the
            // selection moves, so the active state reads instantly.
            if (pillW > 0.dp) {
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = pillX)
                        .width(pillW)
                        .height(46.dp)
                        .vElevation(VElevationLevel.Raised, radius = 999.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(listOf(c.accent, accent)),
                        ),
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
                        accent = accent,
                        modifier = Modifier
                            .weight(if (active) 1.35f else 1f)
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
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val c = VtC
    // Active glyph + label sit ON the solid violet capsule, so they turn white.
    val tint = if (active) Color.White else c.ink3
    val iconScale by animateFloatAsState(
        targetValue = if (active) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "tDockIconScale",
    )
    val iconLift by animateFloatAsState(
        targetValue = if (active) -1.5f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "tDockIconLift",
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tDockLabelAlpha",
    )

    Row(
        modifier.height(44.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box {
            Icon(
                item.icon,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                        translationY = iconLift * this.density
                    },
            )
            if (item.badge > 0) {
                // On the active (violet) capsule the red badge would clash, so it
                // flips to a white pill with violet text; elsewhere it's the usual
                // danger dot ringed in the bar colour.
                val badgeBg = if (active) Color.White else c.dangerInk
                val badgeRing = if (active) accent else c.card
                val badgeFg = if (active) accent else Color.White
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 7.dp, y = (-3).dp)
                        .clip(CircleShape)
                        .background(badgeBg)
                        .border(1.5.dp, badgeRing, CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        if (item.badge > 9) "9+" else item.badge.toString(),
                        style = VtT.dataSm.coloredV(badgeFg).copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
        if (active && labelAlpha > 0.01f) {
            Spacer(Modifier.width(7.dp))
            Text(
                item.label,
                maxLines = 1,
                style = VtT.label.coloredV(Color.White).copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                ),
                modifier = Modifier.graphicsLayer { alpha = labelAlpha },
            )
        }
    }
}
