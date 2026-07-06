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
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

private enum class TimetableSubTab { Week, Requests }

@Composable
fun TeacherTimetableTab() {
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
                TimetableSubTab.Week -> WeekContent()
                TimetableSubTab.Requests -> RequestsContent()
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
private fun WeekContent() {
    var selectedDay by rememberSaveable { mutableStateOf("Tue") }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        days.forEach { day ->
            val isSelected = day == selectedDay
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
                    ) { selectedDay = day }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text(
                    text = day,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = fg,
                )
            }
        }
    }

    val periods = remember {
        listOf(
            PeriodData("1", "Class 7-B · Mathematics", "Room 204", "8:00 — 8:45"),
            PeriodData("2", "Class 8-A · Mathematics", "Room 312", "9:00 — 9:45"),
            PeriodData("3", "Class 9-C · Algebra", "Room 108", "10:00 — 10:45"),
            PeriodData("4", "Class 7-B · Mathematics", "Room 204", "11:15 — 12:00"),
            PeriodData("5", "Class 8-A · Mathematics", "Room 312", "1:00 — 1:45"),
        )
    }
    periods.forEach { period ->
        TimetablePeriodRow(period)
    }
    Spacer(Modifier.height(24.dp))
}

private data class PeriodData(val num: String, val className: String, val room: String, val time: String)

@Composable
private fun TimetablePeriodRow(period: PeriodData) {
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
                text = period.num,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.ink2,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = period.className,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.ink,
            )
            Text(
                text = period.room,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Text(
            text = period.time,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
        )
    }
}

@Composable
private fun RequestsContent() {
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
        )
    }
    ChangeRequestCard("Modify Period 3", "Wednesday · 10:00 — 10:45", "Pending", VColors.goldSoft, VColors.gold, "Requesting room change from 108 to 205 due to smartboard maintenance.", "Submitted 12 Jul 2026")
    ChangeRequestCard("New Period", "Friday · 2:00 — 2:45", "Approved", VColors.mintSoft, VColors.success, "Additional remedial session for Class 9-C before unit test.", "Submitted 8 Jul 2026")
    ChangeRequestCard("Delete Period 5", "Monday · 1:00 — 1:45", "Rejected", VColors.errorSoft, VColors.error, "Request to cancel Monday afternoon session for personal leave.", "Submitted 5 Jul 2026")
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
