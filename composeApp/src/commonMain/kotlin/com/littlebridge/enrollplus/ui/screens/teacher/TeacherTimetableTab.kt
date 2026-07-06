package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedDayDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedPeriodDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedWeekDto
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

private enum class TimetableSubTab { Week, Requests }

@Composable
fun TeacherTimetableTab(viewModel: TeacherViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadWeek()
        viewModel.loadChangeRequests()
    }

    val weekState by viewModel.weekState.collectAsState()
    val changeRequestsState by viewModel.changeRequestsState.collectAsState()

    var subTab by rememberSaveable { mutableStateOf(TimetableSubTab.Week) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            SubTabButton("This Week", subTab == TimetableSubTab.Week) {
                subTab = TimetableSubTab.Week
            }
            SubTabButton("Change Requests", subTab == TimetableSubTab.Requests) {
                subTab = TimetableSubTab.Requests
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            when (subTab) {
                TimetableSubTab.Week -> WeekContent(weekState)
                TimetableSubTab.Requests -> RequestsContent(changeRequestsState)
            }
        }
    }
}

@Composable
private fun SubTabButton(text: String, isActive: Boolean, onClick: () -> Unit) {
    val color = if (isActive) VColors.violet else VColors.ink3
    val weight = if (isActive) FontWeight.Bold else FontWeight.Medium
    Box(
        modifier = Modifier
            .padding(end = 24.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(vertical = 8.dp),
    ) {
        Column {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = weight,
                color = color,
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(if (isActive) 24.dp else 0.dp)
                    .height(3.dp)
                    .background(VColors.violet, VShapes.full),
            )
        }
    }
}

@Composable
private fun WeekContent(weekState: UiState<com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedWeekResponse>) {
    val weekData = (weekState as? UiState.Success)?.data?.data
    val days = weekData?.days ?: emptyList()
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weekdayIndex = listOf(1, 2, 3, 4, 5, 6, 7)

    var selectedDay by rememberSaveable { mutableStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        weekdayIndex.forEach { idx ->
            val dayLabel = dayLabels[idx - 1]
            val isSelected = idx == selectedDay
            val bg = if (isSelected) VColors.violet else Color.Transparent
            val fg = if (isSelected) VColors.white else VColors.ink2
            val border = if (isSelected) VColors.violet else VColors.line
            Box(
                modifier = Modifier
                    .border(1.5.dp, border, VShapes.full)
                    .background(bg, VShapes.full)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { selectedDay = idx }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text(
                    text = dayLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = fg,
                )
            }
        }
    }

    val selectedDayData = days.find { it.weekday == selectedDay }
    val periods = selectedDayData?.periods ?: emptyList()

    if (periods.isEmpty()) {
        Text(
            text = when {
                weekState is UiState.Loading -> "Loading…"
                selectedDayData?.isHoliday == true -> "Holiday: ${selectedDayData.holidayName ?: "No classes"}"
                else -> "No periods scheduled"
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )
    } else {
        periods.forEachIndexed { index, period ->
            TimetablePeriodRow(period, index + 1)
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun TimetablePeriodRow(period: ResolvedPeriodDto, num: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(VColors.surfaceTint, VShapes.full),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = num.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.ink2,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${period.className}${if (period.section.isNotBlank()) "-${period.section}" else ""} · ${period.subject}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.ink,
            )
            Text(
                text = period.room.ifBlank { "No room" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Text(
            text = "${period.startTime} — ${period.endTime}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
        )
    }
}

@Composable
private fun RequestsContent(changeRequestsState: UiState<com.littlebridge.enrollplus.feature.admin.domain.model.ChangeRequestListResponse>) {
    val requests = (changeRequestsState as? UiState.Success)?.data
    val dayLabels = listOf("", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Submitted Requests",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.ink,
        )
        Text(
            text = "+ New request",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = VColors.violet,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {},
        )
    }
    if (requests == null) {
        Text(
            text = when (changeRequestsState) {
                is UiState.Loading -> "Loading…"
                is UiState.Error -> (changeRequestsState as UiState.Error).message
                else -> "No requests"
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )
    } else {
        val reqList = requests.requests
        if (reqList.isEmpty()) {
            Text(
                text = "No change requests submitted",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
        } else {
            reqList.forEach { req ->
                val status = req.status
                val (statusBg, statusFg) = when (status.lowercase()) {
                    "pending" -> VColors.goldSoft to VColors.gold
                    "approved" -> VColors.mintSoft to VColors.success
                    "rejected" -> VColors.errorSoft to VColors.error
                    else -> VColors.surfaceTint to VColors.ink2
                }
                val dayLabel = if (req.weekday in 1..7) dayLabels[req.weekday] else ""
                ChangeRequestCard(
                    type = req.kind.replaceFirstChar { it.uppercase() },
                    period = "$dayLabel · ${req.startTime ?: ""} — ${req.endTime ?: ""}",
                    status = status.replaceFirstChar { it.uppercase() },
                    statusBg = statusBg,
                    statusFg = statusFg,
                    reason = req.reason,
                    date = "Submitted ${req.createdAt}",
                )
            }
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun ChangeRequestCard(
    type: String,
    period: String,
    status: String,
    statusBg: Color,
    statusFg: Color,
    reason: String,
    date: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = type,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.ink,
                )
                Text(
                    text = period,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .background(statusBg, VShapes.full)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusFg,
                )
            }
        }
        Text(
            text = reason,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink2,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = date,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
