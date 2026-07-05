package com.littlebridge.enrollplus.ui.v2.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.buttons.VSecondaryButton
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun VConfirmDialogPremium(
    visible: Boolean,
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelLabel: String = "Cancel",
    icon: ImageVector? = null,
    isDestructive: Boolean = true,
) {
    if (!visible) return
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxWidth()
                .radialGlow(120.dp, (-40).dp, 200.dp, VColors.HeroGlowTopRight)
                .clip(VShapes.TwoXl)
                .background(VColors.SurfaceContainerLowest)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (icon != null) {
                Box(
                    Modifier.size(56.dp).clip(CircleShape).background(
                        if (isDestructive) VColors.ErrorContainer else VColors.PrimaryContainer
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        icon, contentDescription = null,
                        tint = if (isDestructive) VColors.Error else VColors.Primary,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
            Text(title, style = VTypography.SectionHeader.copy(color = VColors.OnSurface), textAlign = TextAlign.Center)
            Text(
                message,
                style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 320.dp),
            )
            Spacer(Modifier.height(8.dp))
            VPrimaryButton(
                text = confirmLabel,
                onClick = onConfirm,
            )
            VSecondaryButton(
                text = cancelLabel,
                onClick = onDismiss,
            )
        }
    }
}
