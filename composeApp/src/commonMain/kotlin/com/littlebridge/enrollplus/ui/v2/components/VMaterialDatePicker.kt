package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.ui.v2.theme.shapeInput
import com.littlebridge.enrollplus.util.MONTH_SHORT
import com.littlebridge.enrollplus.util.isoOf
import com.littlebridge.enrollplus.util.parseIsoDate
import com.littlebridge.enrollplus.util.todayIso

private fun prettyDate(iso: String): String {
    val (y, m, d) = parseIsoDate(iso) ?: return iso
    return "${d.toString().padStart(2, '0')} ${MONTH_SHORT[m - 1]} $y"
}

private fun isoToMillis(iso: String): Long? {
    val (y, m, d) = parseIsoDate(iso) ?: return null
    val a = (14L - m) / 12L
    val y2 = y.toLong() + 4800L - a
    val m2 = m.toLong() + 12L * a - 3L
    val julianDay = d + (153L * m2 + 2L) / 5L + 365L * y2 + y2 / 4L - y2 / 100L + y2 / 400L - 32045L
    return (julianDay - 2440588L) * 86400000L
}

private fun millisToIso(millis: Long): String {
    val julianDay = millis / 86400000L + 2440588L
    val a = julianDay + 32044L
    val b = (4L * a + 3L) / 146097L
    val c = a - (146097L * b) / 4L
    val d = (4L * c + 3L) / 1461L
    val e = c - (1461L * d) / 4L
    val m = (5L * e + 2L) / 153L
    val day = e - (153L * m + 2L) / 5L + 1L
    val month = m + 3L - 12L * (m / 10L)
    val year = 100L * b + d - 4800L + m / 10L
    return isoOf(year.toInt(), month.toInt(), day.toInt())
}

/** Prototype-faithful calendar trigger and rounded bottom-sheet calendar. */
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
        label?.let {
            Text(it, style = VTheme.type.inputLabel.colored(c.ink2), modifier = Modifier.padding(bottom = 7.dp))
        }
        Row(
            Modifier.fillMaxWidth().clip(VTheme.dimens.shapeInput).background(c.card)
                .border(1.dp, if (isError) c.danger else c.hairline, VTheme.dimens.shapeInput)
                .clickable(enabled = enabled) { open = true }.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                value.takeIf(String::isNotBlank)?.let(::prettyDate) ?: placeholder,
                style = VTheme.type.body.colored(if (value.isBlank()) c.ink3 else c.ink),
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(8.dp))
            Icon(VIcons.Calendar, contentDescription = null, tint = c.ink3, modifier = Modifier.size(18.dp))
        }
        if (isError && errorText != null) {
            Text(errorText, style = VTheme.type.caption.colored(c.dangerInk), modifier = Modifier.padding(top = 6.dp))
        }
    }

    if (open) {
        val initialMillis = remember(value) { isoToMillis(value.ifBlank(::todayIso)) ?: 0L }
        val pickerState = androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        VBottomSheet(visible = true, onDismiss = { open = false }) {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                VBottomSheetHeader(title = "Select Date")
                DatePicker(
                    state = pickerState,
                    showModeToggle = false,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 360.dp),
                    colors = DatePickerDefaults.colors(
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
                VButton(
                    text = "Done",
                    onClick = {
                        pickerState.selectedDateMillis?.let { onValueChange(millisToIso(it)) }
                        open = false
                    },
                    full = true,
                    variant = VButtonVariant.Primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
        }
    }
}
