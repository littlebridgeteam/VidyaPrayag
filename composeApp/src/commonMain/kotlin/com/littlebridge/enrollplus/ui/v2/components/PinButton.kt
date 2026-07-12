package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

/**
 * A pin/unpin toggle for the home-screen shortcuts feature.
 *
 * @param pinned Whether this screen is currently pinned.
 * @param contentDescription Optional accessibility label.
 * @param onClick Callback invoked when the button is pressed.
 */
@Composable
fun PinButton(
    pinned: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(36.dp),
    ) {
        Icon(
            imageVector = if (pinned) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = contentDescription ?: if (pinned) "Unpin from home" else "Pin to home",
            tint = VTheme.colors.accent,
            modifier = Modifier.size(20.dp),
        )
    }
}
