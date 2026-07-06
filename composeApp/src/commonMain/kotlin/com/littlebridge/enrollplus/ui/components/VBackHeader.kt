package com.littlebridge.enrollplus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
fun VBackHeader(
    title: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.92f else 1f
    val bg by animateColorAsState(
        targetValue = if (isPressed) VColors.surfaceTint else Color.Transparent,
        animationSpec = tween(150),
        label = "backBg",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(pressScale)
                .background(bg, VShapes.full)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = VColors.ink,
                modifier = Modifier.size(22.dp),
            )
        }
        if (title != null) {
            Spacer(Modifier.padding(start = 8.dp))
            Text(
                text = title,
                style = VTypography.body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                color = VColors.ink,
            )
        }
        Spacer(Modifier.weight(1f))
        if (trailing != null) {
            trailing()
        }
    }
}
