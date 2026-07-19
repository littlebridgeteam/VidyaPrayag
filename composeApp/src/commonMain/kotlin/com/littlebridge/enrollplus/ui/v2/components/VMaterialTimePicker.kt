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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
    return "${h12.toString().padStart(2, '0')}:$minute $period"
}

/** Prototype-faithful single-field trigger with a three-column time wheel sheet. */
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
        label?.let {
            Text(it, style = VTheme.type.inputLabel.colored(c.ink2), modifier = Modifier.padding(bottom = 7.dp))
        }
        Row(
            Modifier.fillMaxWidth().clip(VTheme.dimens.shapeInput).background(c.card)
                .border(1.dp, c.hairline, VTheme.dimens.shapeInput)
                .clickable(enabled = enabled) { open = true }.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                formatClock12h(hour, minute),
                style = VTheme.type.body.colored(if (enabled) c.ink else c.ink3),
                modifier = Modifier.weight(1f),
            )
            Icon(VIcons.Clock, contentDescription = null, tint = c.ink3, modifier = Modifier.size(18.dp))
        }
    }
    if (open) {
        TimeWheelSheet(hour, minute, onDismiss = { open = false }) { selectedHour, selectedMinute ->
            onHourChange(selectedHour)
            onMinuteChange(selectedMinute)
            open = false
        }
    }
}

@Composable
private fun TimeWheelSheet(
    initialHour: Int,
    initialMinute: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit,
) {
    val c = VTheme.colors
    var selectedPeriod by remember { mutableStateOf(if (initialHour < 12) "AM" else "PM") }
    var selectedHour12 by remember {
        mutableStateOf(when { initialHour == 0 -> 12; initialHour > 12 -> initialHour - 12; else -> initialHour })
    }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    val minutes = listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")

    VBottomSheet(visible = true, onDismiss = onDismiss) {
        VBottomSheetHeader(title = "Select Time")
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
            Text(selectedHour12.toString().padStart(2, '0'), style = VTheme.type.h1.copy(fontSize = 38.sp, fontWeight = FontWeight.ExtraBold).colored(c.ink))
            Text(":", style = VTheme.type.h1.copy(fontSize = 38.sp, fontWeight = FontWeight.ExtraBold).colored(c.accent))
            Text(selectedMinute, style = VTheme.type.h1.copy(fontSize = 38.sp, fontWeight = FontWeight.ExtraBold).colored(c.ink))
            Text(selectedPeriod, style = VTheme.type.body.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold).colored(c.ink3), modifier = Modifier.padding(start = 7.dp, bottom = 5.dp))
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            WheelColumn("HOUR", (1..12).toList(), selectedHour12, { it.toString().padStart(2, '0') }) { selectedHour12 = it }
            Text(":", style = VTheme.type.h2.colored(c.ink3), modifier = Modifier.padding(top = 48.dp, start = 7.dp, end = 7.dp))
            WheelColumn("MIN", minutes, selectedMinute, { it }) { selectedMinute = it }
            Spacer(Modifier.width(10.dp))
            WheelColumn("AM/PM", listOf("AM", "PM"), selectedPeriod, { it }) { selectedPeriod = it }
        }
        Spacer(Modifier.height(14.dp))
        VButton(
            text = "Done",
            onClick = {
                val hour24 = when {
                    selectedPeriod == "AM" && selectedHour12 == 12 -> 0
                    selectedPeriod == "PM" && selectedHour12 != 12 -> selectedHour12 + 12
                    else -> selectedHour12
                }
                onConfirm(hour24, selectedMinute)
            },
            full = true,
            variant = VButtonVariant.Primary,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun <T> WheelColumn(
    label: String,
    values: List<T>,
    selected: T,
    text: (T) -> String,
    onSelect: (T) -> Unit,
) {
    val c = VTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = VTheme.type.caption.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = .5.sp).colored(c.ink3))
        Spacer(Modifier.height(6.dp))
        LazyColumn(Modifier.width(62.dp).height(150.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            items(values) { value ->
                val active = value == selected
                Box(
                    Modifier.fillMaxWidth().padding(vertical = 2.dp).clip(RoundedCornerShape(9.dp))
                        .background(if (active) c.accent else c.card)
                        .clickable { onSelect(value) }.padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text(value), style = VTheme.type.body.copy(fontSize = 15.sp, fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium).colored(if (active) c.card else c.ink3), textAlign = TextAlign.Center)
                }
            }
        }
    }
}
