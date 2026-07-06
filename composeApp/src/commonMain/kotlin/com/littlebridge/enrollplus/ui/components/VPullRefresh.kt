package com.littlebridge.enrollplus.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import kotlin.math.roundToInt

@Composable
fun VPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val pullThreshold = 80.dp
    var pullDistance by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                if (isRefreshing) return@pointerInput
                detectVerticalDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        if (pullDistance >= pullThreshold.toPx()) {
                            onRefresh()
                        }
                        pullDistance = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        pullDistance = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        if (dragAmount > 0 && pullDistance >= 0) {
                            pullDistance = (pullDistance + dragAmount * 0.5f).coerceAtMost(pullThreshold.toPx() * 1.5f)
                        }
                    },
                )
            },
    ) {
        val indicatorOffset = if (isRefreshing) {
            pullThreshold.toPx()
        } else {
            pullDistance
        }

        // Refresh indicator
        if (indicatorOffset > 0 || isRefreshing) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, ((indicatorOffset - 30.dp.toPx()) / 2).roundToInt()) }
                    .align(Alignment.TopCenter)
                    .size(36.dp)
                    .background(VColors.violetSoft, VShapes.full),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = VColors.violet,
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .offset { IntOffset(0, if (isRefreshing) pullThreshold.toPx().roundToInt() else 0) },
        ) {
            content()
        }
    }
}
