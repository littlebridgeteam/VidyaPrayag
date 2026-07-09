package com.littlebridge.enrollplus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import kotlinx.coroutines.delay

enum class VSnackbarTone { Success, Error, Warning, Info }

@Composable
fun VSnackbar(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    tone: VSnackbarTone = VSnackbarTone.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    durationMs: Long = 3500,
) {
    val (icon, iconTint, bg) = when (tone) {
        VSnackbarTone.Success -> Triple(Icons.Rounded.CheckCircle, VColors.success, VColors.successSoft)
        VSnackbarTone.Error -> Triple(Icons.Rounded.Error, VColors.error, VColors.errorSoft)
        VSnackbarTone.Warning -> Triple(Icons.Rounded.Warning, VColors.gold, VColors.goldSoft)
        VSnackbarTone.Info -> Triple(Icons.Rounded.Info, VColors.sky, VColors.skySoft)
    }

    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMs)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it },
        ) + fadeIn(tween(300)),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it },
        ) + fadeOut(tween(300)),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg, VShapes.md)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = message,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
            if (actionLabel != null && onAction != null) {
                Text(
                    text = actionLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.violet,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onAction() },
                )
            }
        }
    }
}
