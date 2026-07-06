package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion

@Composable
fun ParentOverlayScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        VBackHeader(
            title = title,
            onBack = onBack,
            modifier = Modifier.fillMaxWidth(),
            trailing = trailing,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            content()
            Spacer(Modifier.height(140.dp))
        }
    }
}

@Composable
fun ParentOverlayContainer(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(VMotion.durSlow, easing = VMotion.ease),
            initialOffsetX = { it },
        ) + fadeIn(tween(VMotion.durSlow)),
        exit = slideOutHorizontally(
            animationSpec = tween(VMotion.durSlow, easing = VMotion.ease),
            targetOffsetX = { it },
        ) + fadeOut(tween(VMotion.durSlow)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VColors.cream)
                .navigationBarsPadding(),
        ) {
            ParentOverlayScaffold(
                title = title,
                onBack = onDismiss,
            ) {
                content()
            }
        }
    }
}
