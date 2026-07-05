package com.littlebridge.enrollplus.ui.v2.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

enum class VSnackbarTonePremium { Success, Error, Info, Warning }

private fun VSnackbarTonePremium.toIcon(): ImageVector = when (this) {
    VSnackbarTonePremium.Success -> Icons.Filled.Check
    VSnackbarTonePremium.Error -> Icons.Filled.Error
    VSnackbarTonePremium.Warning -> Icons.Filled.Warning
    VSnackbarTonePremium.Info -> Icons.Filled.Info
}

@Composable
private fun VSnackbarTonePremium.toColor(): androidx.compose.ui.graphics.Color = when (this) {
    VSnackbarTonePremium.Success -> VColors.Tertiary
    VSnackbarTonePremium.Error -> VColors.Error
    VSnackbarTonePremium.Warning -> VColors.WarmOrange
    VSnackbarTonePremium.Info -> VColors.Primary
}

/**
 * Premium snackbar — bottom-anchored transient message with icon, message, optional action.
 * Uses M3 Expressive tokens: inverse-surface bg, rounded-2xl shape, press-scale on action.
 */
@Composable
fun VSnackbarPremium(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    tone: VSnackbarTonePremium = VSnackbarTonePremium.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val iconColor = tone.toColor()

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(VShapes.Lg)
                .background(VColors.InverseSurface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = tone.toIcon(),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = message,
                style = VTypography.UpdateText.copy(color = VColors.InverseOnSurface),
                modifier = Modifier.weight(1f),
            )
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = {
                    onAction()
                    onDismiss()
                }) {
                    Text(actionLabel, color = VColors.InversePrimary)
                }
            }
        }
    }
}
