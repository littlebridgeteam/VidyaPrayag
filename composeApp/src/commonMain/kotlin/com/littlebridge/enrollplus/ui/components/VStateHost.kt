package com.littlebridge.enrollplus.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Refresh
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
fun <T> VStateHost(
    state: UiState<T>,
    skeleton: @Composable () -> Unit,
    onRetry: (() -> Unit)? = null,
    empty: (@Composable () -> Unit)? = null,
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(tween(300, easing = VMotion.easeEmphasized)) togetherWith
                fadeOut(tween(300, easing = VMotion.easeEmphasized))
        },
        label = "vStateHost",
    ) { s ->
        when (s) {
            is UiState.Loading -> skeleton()
            is UiState.Error -> VErrorState(
                message = s.message,
                onRetry = onRetry,
            )
            is UiState.Success -> {
                if (empty != null && _isEmpty(s.data)) {
                    empty()
                } else {
                    content(s.data)
                }
            }
        }
    }
}

private fun _isEmpty(data: Any?): Boolean {
    return when (data) {
        is Collection<*> -> data.isEmpty()
        is Array<*> -> data.isEmpty()
        is String -> data.isBlank()
        else -> false
    }
}

@Composable
fun VErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(VColors.coralSoft, VShapes.full),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = VColors.coral,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink2,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .background(VColors.violet, VShapes.full)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onRetry() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Refresh,
                    contentDescription = null,
                    tint = VColors.white,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Retry",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.white,
                )
            }
        }
    }
}
