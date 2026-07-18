package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.ui.v2.theme.shapeInput
import com.littlebridge.enrollplus.util.parseIsoDate
import com.littlebridge.enrollplus.util.todayIso
import com.littlebridge.enrollplus.util.isoOf
import com.littlebridge.enrollplus.util.MONTH_SHORT

private fun prettyDate(iso: String): String {
    val (y, m, d) = parseIsoDate(iso) ?: return iso
    return "$d ${MONTH_SHORT[m - 1]} $y"
}

private fun isoToMillis(iso: String): Long? {
    val (y, m, d) = parseIsoDate(iso) ?: return null
    val year = y.toLong()
    val month = m.toLong()
    val day = d.toLong()
    val a = (14 - month) / 12
    val y2 = year + 4800 - a
    val m2 = month + 12 * a - 3
    val julianDay = day + (153 * m2 + 2) / 5 + 365 * y2 + y2 / 4 - y2 / 100 + y2 / 400 - 32045
    val epochDay = julianDay - 2440588
    return epochDay * 86400000L
}

private fun millisToIso(millis: Long): String {
    val epochDay = millis / 86400000
    val julianDay = epochDay + 2440588
    val a = julianDay + 32044L
    val b = (4 * a + 3) / 146097
    val c = a - (146097 * b) / 4
    val d = (4 * c + 3) / 1461
    val e = c - (1461 * d) / 4
    val m = (5 * e + 2) / 153
    val day = e - (153 * m + 2) / 5 + 1
    val month = m + 3 - 12 * (m / 10)
    val year = 100 * b + d - 4800 + m / 10
    return isoOf(year.toInt(), month.toInt(), day.toInt())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VMaterialDatePicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Select date",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null,
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
        androidx.compose.foundation.layout.Box(
            Modifier
                .fillMaxWidth()
                .clip(VTheme.dimens.shapeInput)
                .background(c.cream)
                .border(1.dp, if (isError) c.danger else c.hairline, VTheme.dimens.shapeInput)
                .clickable(enabled = enabled) { open = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    VIcons.Calendar,
                    contentDescription = null,
                    tint = if (enabled) c.tealDeep else c.ink3,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = value.takeIf { it.isNotBlank() }?.let(::prettyDate) ?: placeholder,
                    style = if (value.isBlank()) VTheme.type.body.colored(c.ink3)
                    else VTheme.type.body.colored(c.ink),
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
        val initialMillis = remember(value) {
            isoToMillis(value.takeIf { it.isNotBlank() } ?: todayIso()) ?: System.currentTimeMillis()
        }

        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onValueChange(millisToIso(millis))
                    }
                    open = false
                }) {
                    Text("OK", color = c.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) {
                    Text("Cancel", color = c.ink2)
                }
            },
            colors = androidx.compose.material3.DatePickerDefaults.colors(
                containerColor = c.card,
                selectedDayContainerColor = c.accent,
                todayContentColor = c.accent,
                todayDateBorderColor = c.accent,
            ),
        ) {
            DatePicker(
                state = datePickerState,
                colors = androidx.compose.material3.DatePickerDefaults.colors(
                    containerColor = c.card,
                    selectedDayContainerColor = c.accent,
                    selectedDayContentColor = c.card,
                    todayContentColor = c.accent,
                    todayDateBorderColor = c.accent,
                    dayContentColor = c.ink,
                    headlineContentColor = c.ink,
                    titleContentColor = c.ink,
                    weekdayContentColor = c.ink3,
                    navigationContentColor = c.ink,
                    subheadContentColor = c.ink2,
                ),
            )
        }
    }
}
