package com.littlebridge.enrollplus.ui.v2.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

private val HOURS = (0..23).map { it.toString().padStart(2, '0') }
private val MINUTES = listOf("00", "15", "30", "45")

private fun formatClock12h(hour: Int, minute: String): String {
    val period = if (hour < 12) "AM" else "PM"
    val h12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$h12:$minute $period"
}

/**
 * Premium time picker — hour:minute dropdown fields with 12h display.
 * Uses M3 Expressive tokens.
 */
@Composable
fun VTimePickerPremium(
    hour: Int,
    minute: String,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
) {
    Column(modifier) {
        if (label != null) {
            Text(
                text = label,
                style = VTypography.FormLabelPortal.copy(color = VColors.OnSurfaceVariant),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                tint = if (enabled) VColors.Primary else VColors.Outline,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(4.dp))
            DropdownFieldPremium(
                value = hour.toString().padStart(2, '0'),
                options = HOURS,
                onSelect = { onHourChange(it.toInt()) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            Text(":", style = VTypography.HeroStatValue.copy(color = VColors.OnSurfaceVariant))
            DropdownFieldPremium(
                value = minute,
                options = MINUTES,
                onSelect = onMinuteChange,
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                formatClock12h(hour, minute),
                style = VTypography.UpdateTime.copy(color = VColors.OnSurfaceVariant),
            )
        }
    }
}

@Composable
private fun DropdownFieldPremium(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }

    Box(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(VShapes.Md)
                .background(VColors.SurfaceContainerLow)
                .border(1.dp, VColors.OutlineVariant, VShapes.Md)
                .pressScale(interaction, pressedScale = 0.97f)
                .clickable(interactionSource = interaction, enabled = enabled) { expanded = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = value,
                style = VTypography.FormInput.copy(color = if (enabled) VColors.OnSurface else VColors.Outline),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt, style = VTypography.FormInput.copy(color = VColors.OnSurface)) },
                    onClick = { onSelect(opt); expanded = false },
                )
            }
        }
    }
}
