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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

private val TIME_SLOTS: List<Pair<Int, String>> by lazy {
    (0..23).flatMap { h ->
        listOf("00", "15", "30", "45").map { m -> h to m }
    }
}

private fun format12h(hour: Int, minute: String): String {
    val period = if (hour < 12) "AM" else "PM"
    val h12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$h12:$minute $period"
}

private fun format24h(hour: Int, minute: String): String {
    return "${hour.toString().padStart(2, '0')}:$minute"
}

/**
 * VTimePicker — premium single-field time selector.
 *
 * One tappable field opens a scrollable popup listing all 96 quarter-hour
 * slots (00:00 through 23:45) in 12-hour format with AM/PM indicators.
 * The user picks a single time in one tap — no separate hour/minute dropdowns.
 *
 * API: [hour] (0..23) + [minute] ("00"|"15"|"30"|"45") in, callbacks out.
 */
@Composable
fun VTimePicker(
    hour: Int,
    minute: String,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = VTypography.caption.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = VColors.ink2,
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
        Box {
            // Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.md)
                    .background(VColors.white)
                    .border(1.5.dp, VColors.lineSoft, VShapes.md)
                    .clickable(enabled = enabled) { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = format12h(hour, minute),
                    style = VTypography.body.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    color = if (enabled) VColors.ink else VColors.ink3,
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = VColors.ink2,
                    modifier = Modifier.size(20.dp),
                )
            }

            // Popup
            if (expanded) {
                TimePickerPopup(
                    currentHour = hour,
                    currentMinute = minute,
                    onSelect = { h, m ->
                        onHourChange(h)
                        onMinuteChange(m)
                        expanded = false
                    },
                    onDismiss = { expanded = false },
                )
            }
        }
    }
}

@Composable
private fun TimePickerPopup(
    currentHour: Int,
    currentMinute: String,
    onSelect: (Int, String) -> Unit,
    onDismiss: () -> Unit,
) {
    // Scrim
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9999.dp)
            .background(VColors.ink.copy(alpha = 0.3f))
            .clickable { onDismiss() },
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.white, RoundedCornerShape(16.dp))
            .padding(8.dp),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Select time",
                style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = VColors.ink,
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(VShapes.md)
                    .background(VColors.surfaceTint)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✕",
                    style = VTypography.body.copy(fontSize = 12.sp),
                    color = VColors.ink2,
                )
            }
        }

        // Scrollable time slot list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 280.dp),
        ) {
            items(TIME_SLOTS) { (h, m) ->
                val isSelected = h == currentHour && m == currentMinute
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.md)
                        .background(if (isSelected) VColors.violet.copy(alpha = 0.08f) else VColors.white)
                        .clickable { onSelect(h, m) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = format12h(h, m),
                        style = VTypography.body.copy(
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        ),
                        color = if (isSelected) VColors.violet else VColors.ink,
                    )
                    Text(
                        text = format24h(h, m),
                        style = VTypography.caption.copy(fontSize = 12.sp),
                        color = VColors.ink3,
                    )
                }
            }
        }
    }
}
