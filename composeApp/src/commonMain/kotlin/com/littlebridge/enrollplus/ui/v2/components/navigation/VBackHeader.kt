package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VIconButton
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Back header — circular back button + centered title.
 *
 * HTML: .login-hero-back / .overlay-header
 *   display: flex; align-items: center; gap: 8px; padding: 12px 12px 12px 8px;
 */
@Composable
fun VBackHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    backIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VIconButton(
            onClick = onBack,
            icon = backIcon,
        )
        Text(
            text = title,
            style = VTypography.OverlayTitle.copy(color = VColors.OnSurface),
            modifier = Modifier.weight(1f),
        )
        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}
