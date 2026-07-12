@file:OptIn(ExperimentalMaterial3Api::class)

package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored

/**
 * VBottomSheet — a theme-aware wrapper around Material3 [ModalBottomSheet].
 *
 * Rises from the bottom of the screen, is anchored to the bottom on all targets
 * (Android/iOS/Desktop/Web/Wasm), and reuses the existing [VTheme] tokens. It
 * supports scrim-tap, drag-down, and system-back dismiss. The content column is
 * automatically padded for navigation bars and the IME so primary CTAs stay
 * visible when the keyboard is open.
 */
@Composable
fun VBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    skipPartiallyExpanded: Boolean = true,
    dragHandle: @Composable (() -> Unit)? = { VSheetDragHandle() },
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return

    val c = VTheme.colors
    val d = VTheme.dimens
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val shape = RoundedCornerShape(
        topStart = d.radiusSheet,
        topEnd = d.radiusSheet,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        shape = shape,
        containerColor = c.card,
        contentColor = c.ink,
        scrimColor = c.shadowTint.copy(alpha = 0.6f),
        dragHandle = dragHandle,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = d.screenPadding, end = d.screenPadding, top = d.sm)
                .navigationBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

/**
 * Standard drag handle for the bottom sheet.
 *
 * 40dp × 4dp rounded capsule in [VTheme.colors.ink3] at 25% opacity.
 */
@Composable
fun VSheetDragHandle(modifier: Modifier = Modifier) {
    val c = VTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(c.ink3.copy(alpha = 0.25f)),
        )
    }
}

/**
 * Header for bottom-sheet panels.
 *
 * Title uses [VTheme.type.h3], optional subtitle uses [VTheme.type.caption], and
 * an optional close button is rendered as a small [VButton] with a ghost variant.
 */
@Composable
fun VBottomSheetHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClose: (() -> Unit)? = null,
) {
    val c = VTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
        ) {
            Text(title, style = VTheme.type.h3.colored(c.ink))
            if (subtitle != null) {
                Text(subtitle, style = VTheme.type.caption.colored(c.ink2))
            }
        }
        if (onClose != null) {
            Spacer(Modifier.width(8.dp))
            VButton(
                text = "",
                onClick = onClose,
                variant = VButtonVariant.Ghost,
                size = VButtonSize.Sm,
                leading = {
                    Icon(
                        VIcons.Close,
                        contentDescription = "Close",
                        tint = c.ink2,
                        modifier = Modifier.height(20.dp),
                    )
                },
            )
        }
    }
}
