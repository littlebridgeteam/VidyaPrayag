package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.health.domain.model.HealthAlertDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsStudentDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherMessageThreadDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherSelfLeaveDto
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors

enum class TeacherOverlayType {
    Messages, HealthAlerts, PEWS, Leave, ComingSoon
}

@Composable
fun TeacherGenericOverlay(
    viewModel: TeacherViewModel,
    visible: Boolean,
    type: TeacherOverlayType,
    title: String,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(visible, type) {
        if (visible) {
            when (type) {
                TeacherOverlayType.Messages -> viewModel.loadMessageThreads()
                TeacherOverlayType.HealthAlerts -> viewModel.loadHealthAlerts()
                TeacherOverlayType.PEWS -> viewModel.loadPewsStudents()
                TeacherOverlayType.Leave -> viewModel.loadMyLeave()
                TeacherOverlayType.ComingSoon -> {}
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VColors.cream),
        ) {
            OverlayHeader(title = title, onBack = onDismiss)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                when (type) {
                    TeacherOverlayType.Messages -> {
                        val state by viewModel.messageThreadsState.collectAsState()
                        when (val s = state) {
                            is UiState.Loading -> LoadingText()
                            is UiState.Error -> ErrorText(s.message)
                            is UiState.Success -> {
                                val threads = s.data.data.threads
                                if (threads.isEmpty()) EmptyText("No messages")
                                else threads.forEach { MessageThreadItem(it) }
                            }
                        }
                    }
                    TeacherOverlayType.HealthAlerts -> {
                        val state by viewModel.healthAlertsState.collectAsState()
                        when (val s = state) {
                            is UiState.Loading -> LoadingText()
                            is UiState.Error -> ErrorText(s.message)
                            is UiState.Success -> {
                                val alerts = s.data.alerts
                                if (alerts.isEmpty()) EmptyText("No health alerts")
                                else alerts.forEach { HealthAlertItem(it) }
                            }
                        }
                    }
                    TeacherOverlayType.PEWS -> {
                        val state by viewModel.pewsStudentsState.collectAsState()
                        when (val s = state) {
                            is UiState.Loading -> LoadingText()
                            is UiState.Error -> ErrorText(s.message)
                            is UiState.Success -> {
                                val students = s.data
                                if (students.isEmpty()) EmptyText("No at-risk students")
                                else students.forEach { PewsStudentItem(it) }
                            }
                        }
                    }
                    TeacherOverlayType.Leave -> {
                        val state by viewModel.myLeaveState.collectAsState()
                        when (val s = state) {
                            is UiState.Loading -> LoadingText()
                            is UiState.Error -> ErrorText(s.message)
                            is UiState.Success -> {
                                val requests = s.data.data.requests
                                if (requests.isEmpty()) EmptyText("No leave applications")
                                else requests.forEach { LeaveItem(it) }
                            }
                        }
                    }
                    TeacherOverlayType.ComingSoon -> {
                        EmptyText("This feature is coming soon")
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlayHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.white)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(VColors.surfaceTint, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = TIChevronRight,
                contentDescription = "Back",
                tint = VColors.ink,
                modifier = Modifier
                    .size(17.dp)
                    .graphicsLayer(rotationZ = 180f),
            )
        }
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = VColors.ink,
        )
    }
}

@Composable
private fun LoadingText() {
    Text(
        text = "Loading…",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = VColors.ink3,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
    )
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = VColors.error,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
    )
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = VColors.ink3,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
    )
}

@Composable
private fun MessageThreadItem(thread: TeacherMessageThreadDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.white)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(VColors.surfaceTint, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = thread.senderName.take(2).uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.ink2,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = thread.senderName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                )
                Text(
                    text = thread.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
            Text(
                text = thread.lastMessage,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (thread.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .background(VColors.violet, CircleShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = thread.unreadCount.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.white,
                )
            }
        }
    }
}

@Composable
private fun HealthAlertItem(alert: HealthAlertDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(VColors.white, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(VColors.coralSoft, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TIAlert,
                    contentDescription = null,
                    tint = VColors.coral,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.studentName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                )
                Text(
                    text = "${alert.className}-${alert.section}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
        if (alert.allergies != "[]") {
            Text(
                text = "Allergies: ${alert.allergies}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        if (alert.chronicConditions != "[]") {
            Text(
                text = "Conditions: ${alert.chronicConditions}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun PewsStudentItem(student: PewsStudentDto) {
    val riskColor = when (student.riskLevel.lowercase()) {
        "high" -> VColors.coral
        "medium" -> VColors.gold
        else -> VColors.sky
    }
    val riskBg = when (student.riskLevel.lowercase()) {
        "high" -> VColors.coralSoft
        "medium" -> VColors.goldSoft
        else -> VColors.skySoft
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(VColors.white, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                )
                Text(
                    text = "${student.className}-${student.section}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
            Box(
                modifier = Modifier
                    .background(riskBg, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = student.riskLevel.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = riskColor,
                )
            }
        }
        student.aiNarrative?.let {
            Text(
                text = it,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun LeaveItem(leave: TeacherSelfLeaveDto) {
    val statusColor = when (leave.status.lowercase()) {
        "approved" -> VColors.success
        "pending" -> VColors.gold
        "rejected" -> VColors.error
        else -> VColors.ink3
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(VColors.white, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${leave.dateFrom} → ${leave.dateTo}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                )
                Text(
                    text = leave.reason,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink2,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.1f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = leave.status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                )
            }
        }
    }
}
