package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.littlebridge.enrollplus.ui.tokens.VColors

@Composable
fun VScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    useSafeArea: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .then(if (useSafeArea) Modifier.statusBarsPadding() else Modifier),
    ) {
        content()
        if (topBar != null) {
            topBar()
        }
        if (bottomBar != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .then(if (useSafeArea) Modifier.navigationBarsPadding() else Modifier),
            ) {
                bottomBar()
            }
        }
    }
}
