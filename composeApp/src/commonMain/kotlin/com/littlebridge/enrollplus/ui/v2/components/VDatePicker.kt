package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.util.MONTH_LONG
import com.littlebridge.enrollplus.util.WEEKDAY_SHORT
import com.littlebridge.enrollplus.util.dayOfWeek
import com.littlebridge.enrollplus.util.daysInMonth
import com.littlebridge.enrollplus.util.isoOf
import com.littlebridge.enrollplus.util.parseIsoDate
import com.littlebridge.enrollplus.util.todayIso

private val MONTH_SHORT = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

private fun prettyDate(iso: String): String {
    val (y, m, d) = parseIsoDate(iso) ?: return iso
    return "$d ${MONTH_SHORT[m - 1]} $y"
}

private val YEAR_RANGE = (2000..2099).toList()

/**
 * VDatePicker — premium date picker with month/year dropdown selectors.
 *
 * Features:
 * - Read-only field that opens a bottom sheet calendar
 * - Month and year selectable via dropdowns (no endless prev/next tapping)
 * - Clean day grid with selected/today states
 * - ISO "YYYY-MM-DD" value on the wire, friendly display in the field
 */
@Composable
fun VDatePicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Select date",
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }

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
        Box(
            Modifier
                .fillMaxWidth()
                .clip(VShapes.md)
                .background(VColors.white)
                .border(1.5.dp, if (isError) VColors.error else VColors.lineSoft, VShapes.md)
                .clickable(enabled = enabled) { open = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value.takeIf { it.isNotBlank() }?.let(::prettyDate) ?: placeholder,
                    style = VTypography.body.copy(fontSize = 15.sp),
                    color = if (value.isBlank()) VColors.ink3 else VColors.ink,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = VColors.ink2,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    if (open) {
        DatePickerSheet(
            initialIso = value.takeIf { it.isNotBlank() } ?: todayIso(),
            onDismiss = { open = false },
            onPick = { iso -> onValueChange(iso); open = false },
        )
    }
}

@Composable
private fun DatePickerSheet(
    initialIso: String,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
) {
    val start = parseIsoDate(initialIso) ?: parseIsoDate(todayIso())!!
    var viewYear by remember { mutableStateOf(start.first) }
    var viewMonth by remember { mutableStateOf(start.second) }
    var selectedDay by remember { mutableStateOf(start.third) }
    var selectedIso by remember { mutableStateOf(initialIso) }

    // Scrim
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9999.dp)
            .background(VColors.ink.copy(alpha = 0.4f))
            .clickable { onDismiss() },
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.white, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Select date",
                style = VTypography.h3.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = VColors.ink,
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(VShapes.md)
                    .background(VColors.surfaceTint)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "✕",
                    style = VTypography.body.copy(fontSize = 14.sp),
                    color = VColors.ink2,
                )
            }
        }

        // Month + Year dropdown selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Month dropdown
            MonthDropdown(
                selectedMonth = viewMonth,
                onSelect = { viewMonth = it },
                modifier = Modifier.weight(1f),
            )
            // Year dropdown
            YearDropdown(
                selectedYear = viewYear,
                onSelect = { viewYear = it },
                modifier = Modifier.weight(1f),
            )
        }

        // Weekday header
        Row(Modifier.fillMaxWidth()) {
            WEEKDAY_SHORT.forEach { d ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = d,
                        style = VTypography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                        color = VColors.ink3,
                    )
                }
            }
        }

        // Day grid
        val firstDow = dayOfWeek(viewYear, viewMonth, 1)
        val total = daysInMonth(viewYear, viewMonth)
        val cells = firstDow + total
        val rows = (cells + 6) / 7
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            for (row in 0 until rows) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val day = cellIndex - firstDow + 1
                        Box(
                            Modifier.weight(1f).aspectRatio(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (day in 1..total) {
                                val iso = isoOf(viewYear, viewMonth, day)
                                val isSelected = iso == selectedIso
                                val isToday = iso == todayIso()
                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) VColors.violet else VColors.surfaceWarm)
                                        .border(
                                            if (isToday && !isSelected) 1.5.dp else 0.dp,
                                            if (isToday && !isSelected) VColors.violet else VColors.surfaceWarm,
                                            RoundedCornerShape(10.dp),
                                        )
                                        .clickable {
                                            selectedDay = day
                                            selectedIso = iso
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = VTypography.body.copy(
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        ),
                                        color = if (isSelected) VColors.white else VColors.ink,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Confirm + Cancel buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp)
                    .border(1.5.dp, VColors.violet, VShapes.md)
                    .clickable { onDismiss() }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Cancel",
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.violet,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp)
                    .clip(VShapes.md)
                    .background(VColors.violet)
                    .clickable { onPick(selectedIso) }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Confirm",
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.white,
                )
            }
        }
    }
}

@Composable
private fun MonthDropdown(
    selectedMonth: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.md)
                .background(VColors.surfaceWarm)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = MONTH_LONG[selectedMonth - 1],
                style = VTypography.body.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = VColors.ink2,
                modifier = Modifier.size(20.dp),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MONTH_LONG.forEachIndexed { idx, name ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            style = VTypography.body.copy(fontSize = 15.sp),
                            color = if (idx + 1 == selectedMonth) VColors.violet else VColors.ink,
                            fontWeight = if (idx + 1 == selectedMonth) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = { onSelect(idx + 1); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun YearDropdown(
    selectedYear: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.md)
                .background(VColors.surfaceWarm)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = selectedYear.toString(),
                style = VTypography.body.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = VColors.ink2,
                modifier = Modifier.size(20.dp),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            YEAR_RANGE.forEach { yr ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = yr.toString(),
                            style = VTypography.body.copy(fontSize = 15.sp),
                            color = if (yr == selectedYear) VColors.violet else VColors.ink,
                            fontWeight = if (yr == selectedYear) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    onClick = { onSelect(yr); expanded = false },
                )
            }
        }
    }
}
