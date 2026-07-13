package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

/**
 * TeacherConfirmSheet — a calm, on-brand confirm sheet used for the deliberate, irreversible
 * teacher actions (publish marks & notify parents, close homework, …). Rendered as a
 * [VBottomSheet] so it shares the same scrim/drag/back behaviour as the rest of the app.
 */
@Composable
fun TeacherConfirmSheet(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    val c = VtC
    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        VBottomSheetHeader(title = title)
        Text(body, style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.5.sp))
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                variant = VButtonVariant.Ghost,
                size = VButtonSize.Md,
            )
            VButton(
                text = confirmLabel,
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                variant = if (destructive) VButtonVariant.Destructive else VButtonVariant.Primary,
                tone = if (destructive) VButtonTone.Rose else VButtonTone.Lavender,
                size = VButtonSize.Md,
            )
        }
    }
}
