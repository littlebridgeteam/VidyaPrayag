package com.littlebridge.enrollplus.ui.v2.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Text input — label, surface-container-low bg, outline-variant border, focus glow.
 *
 * HTML (auth-flow): .form-label / .form-input
 *   .form-label: 12px / 600 / uppercase / 0.04em / on-surface-variant
 *   .form-input: padding 16px 20px; border-radius: var(--shape-lg);
 *     border: 1.5px solid var(--outline-variant);
 *     background: var(--surface-container-low);
 *     :focus { border-color: var(--primary); box-shadow: 0 0 0 3px rgba(103,80,246,0.12); }
 *
 * HTML (parent-portal): .form-label: 13px / 600 / on-surface-variant
 *   .form-input: border: 1px solid var(--outline-variant); border-radius: var(--shape-lg);
 *     :focus { border-color: var(--primary); box-shadow: 0 0 0 2px var(--primary-container); }
 */
@Composable
fun VTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    authStyle: Boolean = true,
) {
    val c = VColors
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()

    val borderColor = if (focused) c.Primary else c.OutlineVariant
    val bgColor = if (focused && authStyle) c.SurfaceContainerLowest else c.SurfaceContainerLow
    val focusGlow = if (focused) c.PrimaryFocusGlow else Color.Transparent
    val focusGlowWidth = if (focused) 3.dp else 0.dp

    val labelStyle = if (authStyle) VTypography.FormLabelAuth else VTypography.FormLabelPortal

    Column(modifier) {
        if (label != null) {
            Text(
                text = label,
                style = labelStyle.copy(color = c.OnSurfaceVariant),
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .border(focusGlowWidth, focusGlow, VShapes.Lg)
                .padding(focusGlowWidth)
                .clip(VShapes.Lg)
                .background(bgColor)
                .border(
                    width = if (authStyle) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = VShapes.Lg,
                )
                .padding(horizontal = if (authStyle) 20.dp else 16.dp, vertical = if (authStyle) 16.dp else 14.dp),
        ) {
            if (value.isEmpty() && placeholder != null) {
                Text(
                    text = placeholder,
                    style = VTypography.FormInput.copy(color = c.Outline),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                textStyle = VTypography.FormInput.copy(color = c.OnSurface),
                cursorBrush = SolidColor(c.Primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                interactionSource = interaction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
