package com.littlebridge.enrollplus.ui.v2.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * FAB — floating action button with expandable menu items.
 *
 * HTML: .fab
 *   width: 60px; height: 60px; border-radius: var(--shape-xl);
 *   background: var(--primary); box-shadow: 0 8px 28px rgba(103,80,246,0.35);
 *   :active { border-radius: var(--shape-full); transform: scale(0.92); }
 *   .fab-container.open .fab { border-radius: var(--shape-full); }
 *   .fab-container.open .fab svg { transform: rotate(135deg); }
 *
 * Menu items:
 *   .fab-menu-item
 *   opacity: 0; transform: translateY(12px) scale(0.8);
 *   .fab-container.open .fab-menu-item { opacity: 1; transform: translateY(0) scale(1); }
 *   staggered delays: 0ms, 50ms, 100ms
 */
@Composable
fun VFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    menuItems: List<VFabMenuItem> = emptyList(),
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Menu items (appear above the FAB)
        menuItems.forEachIndexed { index, item ->
            val menuAlpha by animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                animationSpec = tween(
                    VMotion.DurMedium2,
                    delayMillis = if (expanded) VMotion.FabMenuDelays.getOrElse(index) { 0 } else 0,
                    easing = VMotion.EaseEmphasized,
                ),
                label = "fabMenuAlpha$index",
            )
            if (menuAlpha > 0.01f) {
                Row(
                    modifier = Modifier
                        .clip(VShapes.Lg)
                        .background(VColors.SurfaceContainerLowest)
                        .shadow(8.dp, VShapes.Lg)
                        .pressScale(remember { MutableInteractionSource() }, pressedScale = 0.95f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { item.onClick() }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (item.icon != null) {
                        item.icon!!()
                    }
                    Text(
                        text = item.label,
                        style = VTypography.SocialButton.copy(color = VColors.OnSurface),
                    )
                }
            }
        }

        // FAB button
        val interaction = remember { MutableInteractionSource() }
        val rotation by animateFloatAsState(
            targetValue = if (expanded) 135f else 0f,
            animationSpec = tween(VMotion.DurMedium2, easing = VMotion.EaseEmphasized),
            label = "fabRotation",
        )
        val radius by animateFloatAsState(
            targetValue = if (expanded) 999f else 24f,
            animationSpec = tween(VMotion.DurMedium2, easing = VMotion.EaseEmphasized),
            label = "fabRadius",
        )

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(radius.dp))
                .background(VColors.Primary)
                .shadow(
                    28.dp,
                    RoundedCornerShape(radius.dp),
                    ambientColor = VColors.FabShadow,
                    spotColor = VColors.FabShadow,
                )
                .pressScale(interaction, pressedScale = 0.92f)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                ) {
                    if (menuItems.isEmpty()) {
                        onClick()
                    } else {
                        expanded = !expanded
                        onClick()
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.rotate(rotation),
                ) {
                    icon()
                }
            }
        }
    }
}

data class VFabMenuItem(
    val label: String,
    val onClick: () -> Unit,
    val icon: (@Composable () -> Unit)? = null,
)
