package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.util.MONTH_LONG
import com.littlebridge.enrollplus.util.daysInMonth as utilDaysInMonth
import com.littlebridge.enrollplus.util.dayOfWeek as utilDayOfWeek
import com.littlebridge.enrollplus.util.isoOf
import com.littlebridge.enrollplus.util.parseIsoDate
import com.littlebridge.enrollplus.util.todayIso

@Composable
fun VDatePicker(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialDate: String? = null,
) {
    val todayIso = todayIso()
    val (todayY, todayM, todayD) = parseIsoDate(todayIso) ?: Triple(2025, 1, 1)
    val (initY, initM, initD) = initialDate?.let { parseIsoDate(it) } ?: Triple(todayY, todayM, todayD)
    var displayedYear by remember { mutableStateOf(initY) }
    var displayedMonth by remember { mutableStateOf(initM) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val monthNames = MONTH_LONG

    val dim = utilDaysInMonth(displayedYear, displayedMonth)
    val firstDow = utilDayOfWeek(displayedYear, displayedMonth, 1)
    val firstDayOfWeek = if (firstDow == 0) 7 else firstDow

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .background(VColors.white, VShapes.lg)
            .shadow(4.dp, VShapes.lg)
            .padding(20.dp),
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(VColors.surfaceTint, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        if (displayedMonth == 1) {
                            displayedMonth = 12; displayedYear--
                        } else displayedMonth--
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null, tint = VColors.ink, modifier = Modifier.size(18.dp))
            }
            Text(
                text = "${monthNames[displayedMonth - 1]} $displayedYear",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.ink,
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(VColors.surfaceTint, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        if (displayedMonth == 12) {
                            displayedMonth = 1; displayedYear++
                        } else displayedMonth++
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = VColors.ink, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Day headers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                Text(
                    text = it,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.ink3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Calendar grid
        val totalCells = ((firstDayOfWeek - 1) + dim + 6) / 7 * 7
        val days = (1..totalCells).map { idx ->
            val dayNum = idx - (firstDayOfWeek - 1)
            if (dayNum in 1..dim) dayNum else 0
        }

        days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                week.forEach { dayNum ->
                    val dateIso = if (dayNum > 0) isoOf(displayedYear, displayedMonth, dayNum) else null
                    val isSelected = dateIso != null && dateIso == selectedDate
                    val isToday = dateIso != null && dateIso == todayIso

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(
                                if (isSelected) VColors.violet else Color.Transparent,
                                CircleShape,
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                dateIso?.let {
                                    selectedDate = it
                                    onDateSelected(it)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (dayNum > 0) {
                            Text(
                                text = dayNum.toString(),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else if (isToday) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isSelected -> VColors.white
                                    isToday -> VColors.violet
                                    else -> VColors.ink
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
