package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
fun VInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorMessage: String? = null,
    prefix: String? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = when {
        isError -> VColors.error
        isFocused -> VColors.violet
        else -> VColors.line
    }
    val shadowModifier = if (isFocused && !isError) {
        Modifier.shadow(0.dp, VShapes.md, ambientColor = VColors.violetSoft, spotColor = VColors.violetSoft)
    } else Modifier

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = VTypography.label,
            color = VColors.ink2,
            modifier = Modifier.padding(start = 2.dp, bottom = 6.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .then(shadowModifier)
                .background(VColors.surfaceCard, VShapes.md)
                .border(1.5.dp, borderColor, VShapes.md),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (prefix != null) {
                    Text(
                        text = prefix,
                        style = VTypography.body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = VColors.ink,
                        modifier = Modifier
                            .background(VColors.surfaceTint)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    )
                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .fillMaxHeight()
                            .background(VColors.line),
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 14.dp), // weight is RowScope extension
                    textStyle = VTypography.body.copy(color = VColors.ink),
                    cursorBrush = SolidColor(VColors.violet),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = visualTransformation,
                    singleLine = true,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    decorationBox = { inner ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = VTypography.body,
                                color = VColors.ink3,
                            )
                        }
                        inner()
                    },
                )
            }
        }
        if (isError && errorMessage != null) {
            Spacer(Modifier.padding(top = 4.dp))
            Text(
                text = errorMessage,
                style = VTypography.caption,
                color = VColors.error,
                modifier = Modifier.padding(start = 2.dp),
            )
        }
    }
}
