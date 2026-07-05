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
 * Top app bar — title + icon buttons, 20sp font-weight 800.
 *
 * HTML: .top-bar
 *   display: flex; align-items: center; gap: 8px; padding: 12px 20px;
 *   .top-bar-title { font-size: 20px; font-weight: 800; letter-spacing: -0.03em; }
 */
@Composable
fun VTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcons: List<(@Composable () -> Unit)> = emptyList(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
        }
        Text(
            text = title,
            style = VTypography.TopBarTitle.copy(color = VColors.OnSurface),
            modifier = Modifier.weight(1f),
        )
        trailingIcons.forEach { icon ->
            icon()
        }
    }
}
