package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.ui.v2.theme.shapeInput

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
 * VMaterialTimePicker — a read-only field that opens a bottom-sheet time selector
 * with VTheme-aware colours. Uses a clock-style hour/minute selector.
 *
 * Shows 12-hour formatted preview on the field, opens a sheet with hour grid + minute grid.
 */
@Composable
fun VMaterialTimePicker(
    hour: Int,
    minute: String,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .clip(VTheme.dimens.shapeInput)
                    .background(c.cream)
                    .border(1.dp, c.hairline, VTheme.dimens.shapeInput)
                    .clickable(enabled = enabled) { open = true }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = hour.toString().padStart(2, '0'),
                    style = VTheme.type.h3.colored(if (enabled) c.ink else c.ink3),
                )
            }
            Text(":", style = VTheme.type.h3.colored(c.ink2))
            Box(
                Modifier
                    .weight(1f)
                    .clip(VTheme.dimens.shapeInput)
                    .background(c.cream)
                    .border(1.dp, c.hairline, VTheme.dimens.shapeInput)
                    .clickable(enabled = enabled) { open = true }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = minute,
                    style = VTheme.type.h3.colored(if (enabled) c.ink else c.ink3),
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                formatClock12h(hour, minute),
                style = VTheme.type.body.colored(c.ink3),
            )
        }
    }

    if (open) {
        TimePickerSheet(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = { open = false },
            onConfirm = { h, m -> onHourChange(h); onMinuteChange(m); open = false },
        )
    }
}

@Composable
private fun TimePickerSheet(
    initialHour: Int,
    initialMinute: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit,
) {
    val c = VTheme.colors
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = "Select time")

        Spacer(Modifier.size(8.dp))

        Text(
            text = "HOUR",
            style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp).colored(c.ink3),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 180.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items((0..23).toList()) { h ->
                val isSelected = h == selectedHour
                val bgColor = if (isSelected) c.accent else c.cream
                val textColor = if (isSelected) c.card else c.ink

                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .clickable { selectedHour = h }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = h.toString().padStart(2, '0'),
                        style = VTheme.type.body.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        ).colored(textColor),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.size(12.dp))

        Text(
            text = "MINUTES",
            style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp).colored(c.ink3),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        val minutes = listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(minutes) { m ->
                val isSelected = m == selectedMinute
                val bgColor = if (isSelected) c.accent else c.cream
                val textColor = if (isSelected) c.card else c.ink

                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .clickable { selectedMinute = m }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = m,
                        style = VTheme.type.body.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        ).colored(textColor),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.size(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                variant = VButtonVariant.Ghost,
                modifier = Modifier.weight(1f),
            )
            VButton(
                text = "Confirm",
                onClick = { onConfirm(selectedHour, selectedMinute) },
                variant = VButtonVariant.Primary,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.size(8.dp))
    }
}
