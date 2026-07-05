package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors

@Composable
fun VScreenScaffoldPremium(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val floatingBottomBarPadding = if (bottomBar != null) {
        112.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    } else {
        0.dp
    }
    Box(
        modifier = modifier.fillMaxSize().background(VColors.Surface),
        contentAlignment = Alignment.TopCenter,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().background(VColors.Surface)) {
            val adaptiveMaxWidth = when {
                maxWidth >= 840.dp -> 720.dp
                maxWidth >= 600.dp -> 560.dp
                else -> 440.dp
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .widthIn(max = adaptiveMaxWidth)
                    .align(Alignment.TopCenter)
                    .background(VColors.Surface),
            ) {
                Column(Modifier.fillMaxSize()) {
                    topBar?.invoke()
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        content(
                            PaddingValues(
                                start = 20.dp,
                                top = 8.dp,
                                end = 20.dp,
                                bottom = 8.dp + floatingBottomBarPadding,
                            ),
                        )
                    }
                }
                if (bottomBar != null) {
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                    ) {
                        bottomBar()
                    }
                }
            }
        }
    }
}
