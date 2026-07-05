package com.littlebridge.enrollplus.ui.v2.modifiers

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Press scale modifier — scales the composable to [pressedScale] on press,
 * springs back to 1.0f on release. Reproduces HTML `:active { transform: scale(x) }`.
 *
 * Different elements use different scale values:
 * - .icon-btn:active → scale(0.9)
 * - .btn-primary:active → scale(0.97)
 * - .chip:active → scale(0.93)
 * - .feature-card:active → scale(0.97)
 * - .fab:active → scale(0.92)
 * - .nav-item:active → scale(0.93)
 * - .hero-stat:active → scale(0.95)
 * - .ls-card:active → scale(0.95)
 * - .qs-card:active → scale(0.96)
 * - .stat-card:active → scale(0.96)
 * - .badge-card:active → scale(0.96)
 * - .social-btn:active → scale(0.96)
 * - .update-item:active → scale(0.98)
 * - .sched-card:active → scale(0.98)
 * - .mark-card:active → scale(0.98)
 * - .role-tile:active → scale(0.97)
 * - .school-card-full:active → scale(0.98)
 * - .account-row:active → scale(0.98)
 * - .school-option:active → scale(0.98)
 * - .hw-card:active → scale(0.98)
 * - .payment-item:active → scale(0.98)
 * - .seg-btn:active → scale(0.95)
 * - .fab-menu-item:active → scale(0.95) (implicit from transition)
 * - .fc-badge:active → scale(0.95)
 * - .update-action-btn:active → scale(0.95)
 * - .action-card:active → scale(0.96)
 *
 * Uses graphicsLayer so it composes cleanly with clickable + ripple.
 *
 * Usage:
 *   val interaction = remember { MutableInteractionSource() }
 *   Box(
 *       Modifier
 *           .pressScale(interaction, pressedScale = 0.97f)
 *           .clickable(interactionSource = interaction, indication = ripple()) { }
 *   )
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.97f,
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "pressScale",
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
