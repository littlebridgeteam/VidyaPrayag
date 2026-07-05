package com.littlebridge.enrollplus.ui.v2.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.littlebridge.enrollplus.ui.v2.components.navigation.VBackHeader
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion

/**
 * Full-screen overlay — slide-in from right, back header, scrollable content.
 *
 * HTML: .overlay
 *   position: absolute; inset: 0; background: var(--surface); z-index: 200;
 *   transform: translateX(100%);
 *   .overlay.active { transform: translateX(0); }
 *   transition: transform var(--dur-medium-2) var(--ease-emphasized);
 */
@Composable
fun VFullScreenOverlay(
    visible: Boolean,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    backIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            tween(VMotion.DurMedium2, easing = VMotion.EaseEmphasized),
            initialOffsetX = { it },
        ) + fadeIn(tween(VMotion.DurMedium2)),
        exit = slideOutHorizontally(
            tween(VMotion.DurMedium2, easing = VMotion.EaseEmphasized),
            targetOffsetX = { it },
        ) + fadeOut(tween(VMotion.DurMedium2)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VColors.Surface),
        ) {
            VBackHeader(
                title = title,
                onBack = onBack,
                backIcon = backIcon,
                trailingIcon = trailingIcon,
            )
            content()
        }
    }
}
