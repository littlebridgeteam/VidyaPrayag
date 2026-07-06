package com.littlebridge.enrollplus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
fun VOTPInput(
    length: Int = 6,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        runCatching { focusRequester.requestFocus() }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(length) { index ->
            val char = value.getOrNull(index)?.toString() ?: ""
            val isFilled = char.isNotEmpty()
            val borderColor by animateColorAsState(
                targetValue = when {
                    isError -> VColors.error
                    isFilled -> VColors.violet
                    else -> VColors.line
                },
                animationSpec = tween(200),
                label = "otpBorder$index",
            )
            val bgColor by animateColorAsState(
                targetValue = if (isFilled && !isError) VColors.violetSoft else VColors.surfaceCard,
                animationSpec = tween(200),
                label = "otpBg$index",
            )
            Box(
                modifier = Modifier
                    .size(48.dp, 56.dp)
                    .background(bgColor, VShapes.md)
                    .border(1.5.dp, borderColor, VShapes.md),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Text(
                    text = char,
                    style = VTypography.otpBox,
                    color = if (isFilled) VColors.violetInk else VColors.ink,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    // Hidden TextField to capture keyboard input
    TextField(
        value = value,
        onValueChange = { newValue ->
            val filtered = newValue.filter { it.isDigit() }.take(length)
            onValueChange(filtered)
        },
        modifier = Modifier
            .size(1.dp)
            .alpha(0f)
            .focusRequester(focusRequester),
        textStyle = VTypography.otpBox,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = VColors.violet,
        ),
    )
}
