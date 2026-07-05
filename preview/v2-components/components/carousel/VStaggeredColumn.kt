package com.littlebridge.enrollplus.ui.v2.components.carousel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import kotlinx.coroutines.delay

/**
 * Staggered column — children animate with slideUp at staggered delays.
 *
 * HTML: .tab-page.active > *:nth-child(N) { animation: slideUp ... delayMs both; }
 *   Delays: 0, 30, 60, 100, 150, 200, 250, 300 ms
 *   Items 4 and 7 use springIn instead of slideUp.
 */
@Composable
fun VStaggeredColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Simple approach: use AnimatedVisibility with staggered delays
    Column(modifier = modifier) {
        // The staggered animation is handled by the caller using VStaggeredItem
        content()
    }
}

/**
 * Individual staggered item — appears with slideUp animation after [delayMs].
 *
 * Usage:
 *   VStaggeredColumn {
 *       VStaggeredItem(delayMs = 0) { Text("First") }
 *       VStaggeredItem(delayMs = 30) { Text("Second") }
 *       VStaggeredItem(delayMs = 60, useSpringIn = true) { Text("Third") }
 *   }
 */
@Composable
fun VStaggeredItem(
    delayMs: Int = 0,
    useSpringIn: Boolean = false,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = if (useSpringIn) {
            fadeIn(tween(VMotion.SpringInDuration, easing = VMotion.EaseEmphasized)) +
                slideInVertically(
                    tween(VMotion.SpringInDuration, easing = VMotion.EaseEmphasized),
                    initialOffsetY = { it / 3 },
                )
        } else {
            fadeIn(tween(VMotion.SlideUpDuration, easing = VMotion.EaseEmphasized)) +
                slideInVertically(
                    tween(VMotion.SlideUpDuration, easing = VMotion.EaseEmphasized),
                    initialOffsetY = { it / 2 },
                )
        },
    ) {
        content()
    }
}
