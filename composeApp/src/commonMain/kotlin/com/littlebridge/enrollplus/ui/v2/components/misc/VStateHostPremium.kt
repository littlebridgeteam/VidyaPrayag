package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

private enum class VStatePhasePremium { Loading, Error, Empty, Content }

@Composable
fun VStateHostPremium(
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    emptyTitle: String = "Nothing here yet",
    emptyBody: String? = null,
    emptyIcon: ImageVector? = null,
    onRetry: (() -> Unit)? = null,
    skeleton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val phase = when {
        loading -> VStatePhasePremium.Loading
        error != null -> VStatePhasePremium.Error
        isEmpty -> VStatePhasePremium.Empty
        else -> VStatePhasePremium.Content
    }

    if (skeleton == null) {
        when (phase) {
            VStatePhasePremium.Loading -> {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VColors.Primary, strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
                }
            }
            VStatePhasePremium.Error -> VErrorStatePremium(message = error ?: "", onRetry = onRetry, modifier = modifier)
            VStatePhasePremium.Empty -> VEmptyStatePremium(title = emptyTitle, icon = emptyIcon, body = emptyBody, modifier = modifier)
            VStatePhasePremium.Content -> content()
        }
        return
    }

    AnimatedContent(
        targetState = phase,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
        label = "state-host-premium",
        modifier = modifier,
    ) { p ->
        when (p) {
            VStatePhasePremium.Loading -> skeleton()
            VStatePhasePremium.Error -> VErrorStatePremium(message = error ?: "", onRetry = onRetry)
            VStatePhasePremium.Empty -> VEmptyStatePremium(title = emptyTitle, icon = emptyIcon, body = emptyBody)
            VStatePhasePremium.Content -> content()
        }
    }
}

@Composable
fun VErrorStatePremium(
    message: String,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    VEmptyStatePremium(
        title = "Something went wrong",
        body = message,
        modifier = modifier,
        action = if (onRetry != null) {
            { VPrimaryButton(text = "Retry", onClick = onRetry) }
        } else null,
    )
}
