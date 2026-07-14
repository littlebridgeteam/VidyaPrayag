package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion

enum class VButtonVariant { Primary, Secondary, Ghost, Outline, Destructive }

@Composable
fun VButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: VButtonVariant = VButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed && enabled) 0.98f else 1f
    val arrowOffset = if (isPressed && enabled) 3f else 0f

    val (bgColor, fgColor, borderColor) = when (variant) {
        VButtonVariant.Primary -> Triple(VColors.violet, VColors.white, null)
        VButtonVariant.Secondary -> Triple(VColors.surfaceCard, VColors.ink, VColors.line)
        VButtonVariant.Ghost -> Triple(Color.Transparent, VColors.violet, null)
        VButtonVariant.Outline -> Triple(Color.Transparent, VColors.ink2, VColors.line)
        VButtonVariant.Destructive -> Triple(VColors.error, VColors.white, null)
    }

    val shadowModifier = if (variant == VButtonVariant.Primary && enabled) {
        Modifier.shadow(
            elevation = 3.dp,
            shape = VShapes.md,
            ambientColor = VColors.violet.copy(alpha = 0.25f),
            spotColor = VColors.violet.copy(alpha = 0.25f),
        )
    } else Modifier

    val alpha = if (enabled) 1f else 0.35f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .scale(scale)
            .then(shadowModifier)
            .background(bgColor, VShapes.md)
            .then(
                if (borderColor != null) Modifier.border(1.5.dp, borderColor, VShapes.md)
                else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 15.dp),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = fgColor,
                )
            } else {
                Text(
                    text = text,
                    style = VTypography.body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = fgColor.copy(alpha = alpha),
                    textAlign = TextAlign.Center,
                )
                if (icon != null) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = fgColor,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer(translationX = arrowOffset),
                    )
                }
            }
        }
    }
}
