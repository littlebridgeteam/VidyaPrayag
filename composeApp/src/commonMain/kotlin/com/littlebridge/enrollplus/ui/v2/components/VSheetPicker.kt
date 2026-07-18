package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.ui.v2.theme.shapeInput

/**
 * VSheetPicker — iOS-style bottom-sheet selector with optional search.
 *
 * Tapping the trigger field opens a [VBottomSheet] with a search bar and a
 * scrollable list of options. Each option shows a checkmark when selected.
 * Replaces the flat Material3 DropdownMenu with a premium sheet-based picker.
 */
@Composable
fun VSheetPicker(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Select",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null,
    searchable: Boolean = false,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    val c = VTheme.colors
    var open by remember { mutableStateOf(false) }

    Column(modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = VTheme.type.inputLabel.colored(c.ink2),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .clip(VTheme.dimens.shapeInput)
                .background(c.cream)
                .border(
                    1.dp,
                    if (isError) c.danger else c.hairline,
                    VTheme.dimens.shapeInput,
                )
                .clickable(enabled = enabled) { open = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    Icon(
                        leadingIcon,
                        contentDescription = null,
                        tint = if (enabled) c.tealDeep else c.ink3,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(10.dp))
                }
                Text(
                    text = value.takeIf { it.isNotBlank() } ?: placeholder,
                    style = if (value.isBlank()) VTheme.type.body.colored(c.ink3)
                    else VTheme.type.body.colored(c.ink),
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    VIcons.ChevronDown,
                    contentDescription = null,
                    tint = c.ink3,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        if (isError && errorText != null) {
            Text(
                text = errorText,
                style = VTheme.type.caption.colored(c.dangerInk),
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }

    if (open) {
        VSheetPickerDialog(
            title = label ?: placeholder,
            options = options,
            selected = value,
            onSelect = { onSelect(it); open = false },
            onDismiss = { open = false },
            searchable = searchable,
        )
    }
}

@Composable
private fun VSheetPickerDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    searchable: Boolean = false,
) {
    val c = VTheme.colors
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val filtered = remember(query, options) {
        if (query.isBlank()) options
        else options.filter { it.contains(query, ignoreCase = true) }
    }

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = title)

        if (searchable) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(VTheme.dimens.shapeInput)
                    .background(c.cream)
                    .border(1.dp, c.hairline, VTheme.dimens.shapeInput)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        VIcons.Search,
                        contentDescription = null,
                        tint = c.ink3,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(10.dp))
                    BasicTextField(
                        value = query,
                        onValueChange = { newQuery -> query = newQuery },
                        textStyle = VTheme.type.body.colored(c.ink),
                        cursorBrush = SolidColor(c.tealDeep),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                    )
                    AnimatedVisibility(
                        visible = query.isNotBlank(),
                        enter = fadeIn(tween(150)),
                        exit = fadeOut(tween(150)),
                    ) {
                        Icon(
                            VIcons.Close,
                            contentDescription = "Clear",
                            tint = c.ink3,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable { query = "" }
                                .padding(2.dp),
                        )
                    }
                }
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(filtered, key = { it }) { option ->
                val isSelected = option == selected
                val bgColor by animateColorAsState(
                    if (isSelected) c.accentTint else c.card,
                    tween(150),
                    label = "itemBg",
                )
                val textColor = if (isSelected) c.accentDeep else c.ink

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VTheme.dimens.shapeInput)
                        .background(bgColor)
                        .clickable { onSelect(option) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = option,
                        style = VTheme.type.body.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        ).colored(textColor),
                        modifier = Modifier.weight(1f),
                    )
                    if (isSelected) {
                        Icon(
                            VIcons.Check,
                            contentDescription = null,
                            tint = c.accentDeep,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
