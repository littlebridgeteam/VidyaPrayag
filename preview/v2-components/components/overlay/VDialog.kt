package com.littlebridge.enrollplus.ui.v2.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

/**
 * Dialog — centered card, scrim backdrop.
 *
 * Used for confirmation dialogs, selection dialogs, etc.
 * Scrim is a semi-transparent black overlay that dismisses on click.
 */
@Composable
fun VDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (visible) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            // Scrim
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(VMotion.DurMedium2)),
                exit = fadeOut(tween(VMotion.DurMedium2)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VColors.Scrim.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                        ) { onDismiss() },
                )
            }
            // Dialog content
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(tween(VMotion.DurMedium2, easing = VMotion.EaseEmphasized)) +
                    fadeIn(tween(VMotion.DurMedium2)),
                exit = scaleOut(tween(VMotion.DurMedium2, easing = VMotion.EaseEmphasized)) +
                    fadeOut(tween(VMotion.DurMedium2)),
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .clip(VShapes.TwoXl)
                        .background(VColors.SurfaceContainerLowest)
                        .padding(24.dp),
                    content = content,
                )
            }
        }
    }
}
