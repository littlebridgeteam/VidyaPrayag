package com.littlebridge.enrollplus.ui.v2.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Search field — icon + input, surface-container-high, full radius, focus ring.
 *
 * HTML: .search-field (parent-portal)
 *   padding: 16px 20px; border-radius: var(--shape-full);
 *   background: var(--surface-container-high);
 *   :focus-within { background: var(--surface-container-highest); box-shadow: 0 0 0 2px var(--primary); }
 *
 * HTML: .search-field (auth-flow / school select)
 *   padding: 14px 20px; border-radius: var(--shape-full);
 *   background: var(--surface-container-low);
 */
@Composable
fun VSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    icon: (@Composable () -> Unit)? = null,
    portalStyle: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()

    val bgColor = if (portalStyle) {
        if (focused) VColors.SurfaceContainerHighest else VColors.SurfaceContainerHigh
    } else {
        VColors.SurfaceContainerLow
    }
    val focusBorder = if (focused && portalStyle) 2.dp else 0.dp
    val focusBorderColor = if (focused && portalStyle) VColors.Primary else androidx.compose.ui.graphics.Color.Transparent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(focusBorder, focusBorderColor, VShapes.Full)
            .padding(focusBorder)
            .clip(VShapes.Full)
            .background(bgColor)
            .padding(horizontal = 20.dp, vertical = if (portalStyle) 16.dp else 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
        }
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = (if (portalStyle) VTypography.SearchInput else VTypography.SearchInputAuth)
                    .copy(color = VColors.OnSurfaceVariant),
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = (if (portalStyle) VTypography.SearchInput else VTypography.SearchInputAuth)
                .copy(color = VColors.OnSurface),
            cursorBrush = SolidColor(VColors.Primary),
            interactionSource = interaction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
