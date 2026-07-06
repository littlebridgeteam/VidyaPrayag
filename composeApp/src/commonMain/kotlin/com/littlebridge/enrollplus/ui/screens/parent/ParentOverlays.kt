package com.littlebridge.enrollplus.ui.screens.parent

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.event.domain.model.ParentEventDto
import com.littlebridge.enrollplus.feature.health.domain.model.HealthIncidentDto
import com.littlebridge.enrollplus.feature.health.domain.model.ImmunizationDto
import com.littlebridge.enrollplus.feature.health.domain.model.ParentHealthResponse
import com.littlebridge.enrollplus.feature.idcard.domain.model.IdCardDto
import com.littlebridge.enrollplus.feature.library.domain.model.LibraryBookDto
import com.littlebridge.enrollplus.feature.library.domain.model.LibraryIssueDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentNotificationDto
import com.littlebridge.enrollplus.feature.schools.data.remote.DiscoveredSchoolDto
import com.littlebridge.enrollplus.feature.transport.domain.model.RouteProgress
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.util.formatDate
import com.littlebridge.enrollplus.util.formatDateDisplay
import com.littlebridge.enrollplus.util.todayIso

@Composable
fun OvSectionTitle(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VColors.ink3, letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp))
}

@Composable
fun OvCard(content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(16.dp)) { content() }
}

@Composable
fun OvStatRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = VColors.ink)
    }
}

@Composable
fun OvLoading() { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("Loading...", color = VColors.ink3) } }

@Composable
fun OvError(msg: String) { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text(msg, color = VColors.coral) } }

// ── Notifications Overlay ──
@Composable
fun NotificationsOverlay(viewModel: ParentViewModel) {
    val state by viewModel.notificationsState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                if (s.data.unreadCount > 0) {
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.End) {
                        Text("Mark all read", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VColors.violet,
                            modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.markAllNotificationsRead() })
                    }
                }
                s.data.notifications.forEach { NotificationItem(it) { viewModel.markNotificationRead(it.id) } }
                if (s.data.notifications.isEmpty()) { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("No notifications", color = VColors.ink3) } }
            }
        }
    }
}

@Composable
private fun NotificationItem(notif: ParentNotificationDto, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md)
        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }.padding(14.dp)) {
        Box(Modifier.size(34.dp).background(VColors.violetSoft, VShapes.sm), Alignment.Center) { Icon(Icons.Rounded.Notifications, null, tint = VColors.violet, modifier = Modifier.size(15.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(notif.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
            Text(notif.body, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(notif.time, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink3, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// ── Health Overlay ──
@Composable
fun HealthOverlay(viewModel: ParentViewModel, childId: String) {
    val state by viewModel.healthState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> HealthContent(s.data)
        }
    }
}

@Composable
private fun HealthContent(data: ParentHealthResponse) {
    OvSectionTitle("Health Profile")
    OvCard {
        data.profile?.let { p ->
            OvStatRow("Blood Group", p.bloodGroup ?: "—")
            OvStatRow("Height", p.heightCm?.let { "${it.toInt()} cm" } ?: "—")
            OvStatRow("Weight", p.weightKg?.let { "${it.toInt()} kg" } ?: "—")
            OvStatRow("Allergies", p.allergies)
            OvStatRow("Chronic Conditions", p.chronicConditions)
            OvStatRow("Emergency Contact", p.emergencyContactName ?: "—")
            OvStatRow("Emergency Phone", p.emergencyContactPhone ?: "—")
            OvStatRow("Doctor", p.doctorName ?: "—")
            OvStatRow("Doctor Phone", p.doctorPhone ?: "—")
        } ?: Text("No health profile on record", fontSize = 13.sp, color = VColors.ink3)
    }
    if (data.immunizations.isNotEmpty()) {
        OvSectionTitle("Immunizations")
        data.immunizations.forEach { ImmItem(it) }
    }
    if (data.incidents.isNotEmpty()) {
        OvSectionTitle("Medical Incidents")
        data.incidents.forEach { IncidentItem(it) }
    }
}

@Composable
private fun ImmItem(imm: ImmunizationDto) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
        Box(Modifier.size(30.dp).background(VColors.mintSoft, VShapes.sm), Alignment.Center) { Icon(Icons.Rounded.CheckCircle, null, tint = VColors.success, modifier = Modifier.size(14.dp)) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(imm.vaccineName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
            Text("Dose ${imm.doseNumber} · ${formatDate(imm.dateAdministered)}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
            imm.nextDueDate?.let { Text("Next due: ${formatDate(it)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VColors.gold) }
        }
    }
}

@Composable
private fun IncidentItem(inc: HealthIncidentDto) {
    val sevColor = when (inc.severity) { "major" -> VColors.coral; "moderate" -> VColors.gold; else -> VColors.success }
    val sevBg = when (inc.severity) { "major" -> VColors.coralSoft; "moderate" -> VColors.goldSoft; else -> VColors.mintSoft }
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
        Box(Modifier.size(30.dp).background(sevBg, VShapes.sm), Alignment.Center) { Icon(Icons.Rounded.LocalHospital, null, tint = sevColor, modifier = Modifier.size(14.dp)) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(inc.description, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink, modifier = Modifier.weight(1f))
                Box(Modifier.background(sevBg, VShapes.full).padding(horizontal = 7.dp, vertical = 2.dp)) { Text(inc.severity, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = sevColor) }
            }
            Text("${formatDate(inc.date)}${inc.time?.let { " · $it" } ?: ""}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
            inc.treatment?.let { Text("Treatment: $it", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, modifier = Modifier.padding(top = 2.dp)) }
        }
    }
}

// ── Transport Overlay ──
@Composable
fun TransportOverlay(viewModel: ParentViewModel, childId: String) {
    val liveState by viewModel.transportLiveState.collectAsState()
    val routeState by viewModel.transportRouteState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        when (val s = liveState) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                val rp = s.data
                OvSectionTitle("Live Tracking")
                OvCard {
                    OvStatRow("Route", rp.routeName)
                    OvStatRow("Bus", rp.busNumber)
                    rp.etaMinutes?.let { OvStatRow("ETA", "$it min") }
                    rp.nextStop?.let { OvStatRow("Next Stop", it.name) }
                }
                if (rp.stops.isNotEmpty()) {
                    OvSectionTitle("Route Stops")
                    rp.stops.forEach { stop ->
                        val isNext = rp.nextStop?.id == stop.id
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
                            Box(Modifier.size(10.dp).background(if (isNext) VColors.violet else VColors.surfaceTint, CircleShape))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(stop.name, fontSize = 13.sp, fontWeight = if (isNext) FontWeight.ExtraBold else FontWeight.SemiBold, color = if (isNext) VColors.violet else VColors.ink)
                                stop.estimatedTime?.let { Text("Est. $it", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3) }
                            }
                            if (isNext) { Box(Modifier.background(VColors.violetSoft, VShapes.full).padding(horizontal = 7.dp, vertical = 2.dp)) { Text("NEXT", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = VColors.violet) } }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// ── Digital ID Card Overlay ──
@Composable
fun DigitalIdOverlay(viewModel: ParentViewModel, childId: String) {
    val state by viewModel.idCardState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> IdCardContent(s.data)
        }
    }
}

@Composable
private fun IdCardContent(card: IdCardDto) {
    Box(Modifier.fillMaxWidth().padding(8.dp).background(VColors.violet, VShapes.lg).padding(24.dp)) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("EnrollPlus", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = VColors.white)
                Box(Modifier.size(40.dp).background(VColors.white.copy(alpha = 0.15f), VShapes.sm), Alignment.Center) { Icon(Icons.Rounded.QrCode, null, tint = VColors.white, modifier = Modifier.size(20.dp)) }
            }
            Spacer(Modifier.height(24.dp))
            Text("Student ID Card", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VColors.white.copy(alpha = 0.7f), letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Text(card.personName, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = VColors.white, letterSpacing = (-0.5).sp)
            Spacer(Modifier.height(6.dp))
            Text("ID: ${card.personId}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.white.copy(alpha = 0.8f))
            card.validTill?.let { Text("Valid till: ${formatDateDisplay(it)}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.white.copy(alpha = 0.8f)) }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.background(VColors.white.copy(alpha = 0.15f), VShapes.sm).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(card.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = VColors.white, letterSpacing = 1.sp)
            }
        }
    }
}

// ── Events Overlay ──
@Composable
fun EventsOverlay(viewModel: ParentViewModel) {
    val state by viewModel.eventsState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                if (s.data.isEmpty()) { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("No upcoming events", color = VColors.ink3) } }
                else s.data.forEach { EventItem(it) }
            }
        }
    }
}

@Composable
private fun EventItem(ev: ParentEventDto) {
    val (bg, tint) = when (ev.type.lowercase()) { "ptm" -> VColors.skySoft to VColors.sky; "holiday" -> VColors.mintSoft to VColors.success; "sports" -> VColors.goldSoft to VColors.gold; else -> VColors.violetSoft to VColors.violet }
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).background(bg, VShapes.sm), Alignment.Center) { Icon(Icons.Rounded.Event, null, tint = tint, modifier = Modifier.size(15.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(ev.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                Text(formatDateDisplay(ev.startDate), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
            }
            Box(Modifier.background(bg, VShapes.full).padding(horizontal = 8.dp, vertical = 2.dp)) { Text(ev.type, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = tint) }
        }
        if (ev.description.isNotBlank()) { Text(ev.description, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp)) }
        ev.venue?.let { Text("Venue: $it", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink3, modifier = Modifier.padding(top = 4.dp)) }
    }
}

// ── Scholarships Overlay ──
@Composable
fun ScholarshipsOverlay(viewModel: ParentViewModel) {
    val state by viewModel.scholarshipsState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                if (s.data.scholarships.isEmpty()) { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("No scholarships available", color = VColors.ink3) } }
                else s.data.scholarships.forEach { sch ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(16.dp)) {
                        Text(sch.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                        Text(sch.description, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Time left: ${sch.timeLeft}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (sch.isCritical) VColors.coral else VColors.ink3)
                            val (bg, tint) = if (sch.isCritical) VColors.coralSoft to VColors.coral else VColors.violetSoft to VColors.violet
                            Box(Modifier.background(bg, VShapes.full).padding(horizontal = 8.dp, vertical = 2.dp)) { Text(sch.category, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = tint) }
                        }
                    }
                }
            }
        }
    }
}

// ── Leave Overlay ──
@Composable
fun LeaveOverlay(viewModel: ParentViewModel, childId: String) {
    val state by viewModel.leaveState.collectAsState()
    var showForm by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.End) {
            Text(if (showForm) "Cancel" else "Apply for Leave", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.violet,
                modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showForm = !showForm })
        }
        if (showForm) { LeaveForm(viewModel, childId) }
        OvSectionTitle("Leave History")
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                if (s.data.requests.isEmpty()) { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("No leave requests", color = VColors.ink3) } }
                else s.data.requests.forEach { lr ->
                    val (bg, tint) = when (lr.status.lowercase()) { "approved" -> VColors.mintSoft to VColors.success; "rejected" -> VColors.coralSoft to VColors.coral; else -> VColors.goldSoft to VColors.gold }
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("${formatDate(lr.dateFrom)} → ${formatDate(lr.dateTo)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                            Text(lr.reason, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Box(Modifier.background(bg, VShapes.full).padding(horizontal = 8.dp, vertical = 2.dp)) { Text(lr.status, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = tint) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaveForm(viewModel: ParentViewModel, childId: String) {
    var dateFrom by rememberSaveable { mutableStateOf(todayIso()) }
    var dateTo by rememberSaveable { mutableStateOf(todayIso()) }
    var reason by rememberSaveable { mutableStateOf("") }
    OvCard {
        Text("Date From", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
        Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(dateFrom, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VColors.ink) }
        Spacer(Modifier.height(12.dp))
        Text("Date To", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
        Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(dateTo, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VColors.ink) }
        Spacer(Modifier.height(12.dp))
        Text("Reason", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
        Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(reason.ifBlank { "Enter reason..." }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (reason.isBlank()) VColors.ink3 else VColors.ink) }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().background(VColors.violet, VShapes.md).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            if (reason.isNotBlank()) { viewModel.applyLeave(childId, dateFrom, dateTo, reason) }
        }.padding(vertical = 12.dp), Alignment.Center) { Text("Submit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.white) }
    }
}

// ── Pulse Overlay ──
@Composable
fun PulseOverlay(viewModel: ParentViewModel, childId: String) {
    val state by viewModel.pulseState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                val p = s.data
                OvSectionTitle("Week of ${p.weekRange}")
                OvCard {
                    Text(p.aiNarrative, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VColors.ink, lineHeight = 19.sp)
                }
                OvSectionTitle("Weekly Stats")
                OvCard {
                    p.attendancePercentage?.let { OvStatRow("Attendance", "${it.toInt()}%") }
                    OvStatRow("Homework Pending", "${p.homeworkPending}")
                    OvStatRow("Homework Completed", "${p.homeworkCompleted}")
                    OvStatRow("Announcements", "${p.announcementsCount}")
                    OvStatRow("Unread Messages", "${p.unreadMessages}")
                }
                if (p.actionableItems.isNotEmpty()) {
                    OvSectionTitle("Action Items")
                    p.actionableItems.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
                            Box(Modifier.size(8.dp).background(VColors.violet, CircleShape))
                            Spacer(Modifier.width(12.dp))
                            Text(item, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VColors.ink, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ── Link Child Overlay ──
@Composable
fun LinkChildOverlay(viewModel: ParentViewModel, onLinked: () -> Unit) {
    var schoolId by rememberSaveable { mutableStateOf("") }
    var rollNumber by rememberSaveable { mutableStateOf("") }
    var childName by rememberSaveable { mutableStateOf("") }
    var parentName by rememberSaveable { mutableStateOf("") }
    var parentPhone by rememberSaveable { mutableStateOf("") }
    var resultMsg by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        OvCard {
            Text("School ID", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
            Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(schoolId.ifBlank { "Enter school ID" }, fontSize = 14.sp, color = if (schoolId.isBlank()) VColors.ink3 else VColors.ink) }
            Spacer(Modifier.height(12.dp))
            Text("Roll Number", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
            Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(rollNumber.ifBlank { "Enter roll number" }, fontSize = 14.sp, color = if (rollNumber.isBlank()) VColors.ink3 else VColors.ink) }
            Spacer(Modifier.height(12.dp))
            Text("Child Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
            Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(childName.ifBlank { "Enter child name" }, fontSize = 14.sp, color = if (childName.isBlank()) VColors.ink3 else VColors.ink) }
            Spacer(Modifier.height(12.dp))
            Text("Parent Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
            Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(parentName.ifBlank { "Enter parent name" }, fontSize = 14.sp, color = if (parentName.isBlank()) VColors.ink3 else VColors.ink) }
            Spacer(Modifier.height(12.dp))
            Text("Parent Phone", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
            Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(parentPhone.ifBlank { "Enter phone number" }, fontSize = 14.sp, color = if (parentPhone.isBlank()) VColors.ink3 else VColors.ink) }
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().background(VColors.violet, VShapes.md).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                if (schoolId.isNotBlank() && rollNumber.isNotBlank()) {
                    viewModel.linkChild(schoolId, rollNumber, null, null, childName.ifBlank { null }, parentPhone.ifBlank { null }, parentName.ifBlank { null }) { success, msg ->
                        resultMsg = msg; isSuccess = success
                        if (success) onLinked()
                    }
                }
            }.padding(vertical = 12.dp), Alignment.Center) { Text("Link Child", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.white) }
            resultMsg?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (isSuccess) VColors.success else VColors.coral)
            }
        }
    }
}

// ── Discovery Overlay ──
@Composable
fun DiscoveryOverlay(viewModel: ParentViewModel) {
    var query by rememberSaveable { mutableStateOf("") }
    val state by viewModel.schoolSearchState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        OvCard {
            Text("Search Schools", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
            Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) { Text(query.ifBlank { "Enter school name..." }, fontSize = 14.sp, color = if (query.isBlank()) VColors.ink3 else VColors.ink) }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().background(VColors.violet, VShapes.md).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                if (query.isNotBlank()) viewModel.searchSchools(query)
            }.padding(vertical = 10.dp), Alignment.Center) { Text("Search", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.white) }
        }
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                OvSectionTitle("Results (${s.data.schools.size})")
                s.data.schools.forEach { school ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
                        Box(Modifier.size(34.dp).background(VColors.violetSoft, VShapes.sm), Alignment.Center) { Icon(Icons.Rounded.School, null, tint = VColors.violet, modifier = Modifier.size(15.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(school.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                            Text("${school.city} · ${school.board}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
                        }
                    }
                }
                if (s.data.schools.isEmpty()) { Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) { Text("No schools found", color = VColors.ink3) } }
            }
        }
    }
}

// ── Calendar Overlay ──
@Composable
fun CalendarOverlay(viewModel: ParentViewModel) {
    val timetableState by viewModel.timetableState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        OvSectionTitle("Weekly Schedule")
        when (val s = timetableState) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                s.data.weekdays.forEach { day ->
                    val dayName = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun").getOrNull(day.weekday - 1) ?: "Day"
                    OvSectionTitle(dayName)
                    day.periods.forEach { p ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(12.dp)) {
                            Text(p.startTime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink3, modifier = Modifier.width(60.dp))
                            Text(p.subject, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink, modifier = Modifier.weight(1f))
                            p.teacherName?.let { Text(it, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3) }
                        }
                    }
                    if (day.periods.isEmpty()) { Text("No classes", fontSize = 12.sp, color = VColors.ink3, modifier = Modifier.padding(8.dp)) }
                }
            }
        }
    }
}

// ── Account Settings Overlay ──
@Composable
fun AccountSettingsOverlay() {
    Column(Modifier.fillMaxWidth()) {
        OvSectionTitle("Preferences")
        OvCard { OvStatRow("Language", "English"); OvStatRow("Notifications", "Enabled"); OvStatRow("Theme", "Light") }
        OvSectionTitle("About")
        OvCard { OvStatRow("Version", "1.0.0"); OvStatRow("Account Type", "Parent") }
    }
}

// ── Library Overlay ──
@Composable
fun LibraryOverlay(viewModel: ParentViewModel, childId: String) {
    var query by rememberSaveable { mutableStateOf("") }
    val searchState by viewModel.librarySearchState.collectAsState()
    val issuedState by viewModel.libraryIssuedState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        OvSectionTitle("Search Books")
        OvCard {
            Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) {
                Text(query.ifBlank { "Search by title or author..." }, fontSize = 14.sp, color = if (query.isBlank()) VColors.ink3 else VColors.ink)
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().background(VColors.violet, VShapes.md).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                if (query.isNotBlank()) viewModel.searchLibraryBooks(query)
            }.padding(vertical = 10.dp), Alignment.Center) { Text("Search", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.white) }
        }
        when (val s = searchState) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                if (s.data.isNotEmpty()) {
                    OvSectionTitle("Results (${s.data.size})")
                    s.data.forEach { book -> BookItem(book) }
                }
            }
        }
        OvSectionTitle("Issued Books")
        when (val s = issuedState) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                if (s.data.isEmpty()) { Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) { Text("No books issued", color = VColors.ink3) } }
                else s.data.forEach { issue -> IssuedBookItem(issue) }
            }
        }
    }
}

@Composable
private fun BookItem(book: LibraryBookDto) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
        Box(Modifier.size(34.dp).background(VColors.violetSoft, VShapes.sm), Alignment.Center) { Icon(Icons.Rounded.School, null, tint = VColors.violet, modifier = Modifier.size(15.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(book.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
            book.author?.let { Text(it, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink3) }
            val avail = if (book.availableCopies > 0) "${book.availableCopies}/${book.totalCopies} available" else "Unavailable"
            Text(avail, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (book.availableCopies > 0) VColors.success else VColors.coral)
        }
    }
}

@Composable
private fun IssuedBookItem(issue: LibraryIssueDto) {
    val isOverdue = issue.status == "issued" && issue.returnDate == null
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
        Text(issue.bookTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Issued: ${formatDate(issue.issueDate)}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
            Text("Due: ${formatDate(issue.dueDate)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isOverdue) VColors.coral else VColors.ink2)
        }
        if (issue.fineAmount > 0) {
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fine: ${issue.fineAmount}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VColors.coral)
                val (bg, tint) = when (issue.fineStatus) { "paid" -> VColors.mintSoft to VColors.success; "waived" -> VColors.skySoft to VColors.sky; else -> VColors.coralSoft to VColors.coral }
                Box(Modifier.background(bg, VShapes.full).padding(horizontal = 7.dp, vertical = 2.dp)) { Text(issue.fineStatus, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = tint) }
            }
        }
        Box(Modifier.background(VColors.surfaceTint, VShapes.full).padding(horizontal = 7.dp, vertical = 2.dp)) { Text(issue.status, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = VColors.ink2) }
    }
}

// ── Tutor Chat Overlay ──
@Composable
fun TutorChatOverlay(viewModel: ParentViewModel, childId: String) {
    val subjectsState by viewModel.tutorSubjectsState.collectAsState()
    val doubtState by viewModel.tutorDoubtState.collectAsState()
    var selectedSubject by rememberSaveable { mutableStateOf("") }
    var question by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxWidth()) {
        OvSectionTitle("Select Subject")
        when (val s = subjectsState) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                s.data.forEach { subj ->
                    val isSelected = selectedSubject == subj.subjectId
                    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).background(if (isSelected) VColors.violetSoft else VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedSubject = subj.subjectId }.padding(12.dp)) {
                        Text(subj.subjectName, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) VColors.violet else VColors.ink)
                    }
                }
                if (s.data.isEmpty()) { Text("No subjects available", fontSize = 12.sp, color = VColors.ink3, modifier = Modifier.padding(8.dp)) }
            }
        }
        if (selectedSubject.isNotBlank()) {
            OvSectionTitle("Ask a Question")
            OvCard {
                Box(Modifier.fillMaxWidth().background(VColors.surfaceTint, VShapes.sm).padding(12.dp)) {
                    Text(question.ifBlank { "Type your question..." }, fontSize = 14.sp, color = if (question.isBlank()) VColors.ink3 else VColors.ink)
                }
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth().background(VColors.violet, VShapes.md).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    if (question.isNotBlank()) viewModel.askDoubt(childId, selectedSubject, question)
                }.padding(vertical = 12.dp), Alignment.Center) { Text("Ask AI Tutor", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.white) }
            }
        }
        when (val s = doubtState) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                s.data.data?.turn?.studentFacing?.let { sf ->
                    OvSectionTitle("AI Tutor Response")
                    OvCard { Text(sf.text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VColors.ink, lineHeight = 19.sp) }
                    sf.nextPrompt?.let { Text(it, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.violet, modifier = Modifier.padding(12.dp)) }
                }
            }
        }
    }
}

// ── Tutor Progress Overlay ──
@Composable
fun TutorProgressOverlay(viewModel: ParentViewModel, childId: String) {
    val subjectsState by viewModel.tutorSubjectsState.collectAsState()
    val progressState by viewModel.tutorProgressState.collectAsState()
    var selectedSubject by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxWidth()) {
        OvSectionTitle("Select Subject")
        when (val s = subjectsState) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                s.data.forEach { subj ->
                    val isSelected = selectedSubject == subj.subjectId
                    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).background(if (isSelected) VColors.violetSoft else VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            selectedSubject = subj.subjectId
                            viewModel.loadTutorProgress(childId, subj.subjectId)
                        }.padding(12.dp)) {
                        Text(subj.subjectName, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) VColors.violet else VColors.ink)
                    }
                }
            }
        }
        if (selectedSubject.isNotBlank()) {
            OvSectionTitle("Progress Card")
            when (val s = progressState) {
                is UiState.Loading -> OvLoading()
                is UiState.Error -> OvError(s.message)
                is UiState.Success -> {
                    s.data.data?.let { pc ->
                        OvCard {
                            OvStatRow("Doubts Resolved", "${pc.totalDoubtsResolved}")
                            OvStatRow("Answers Given", "${pc.totalAnswersGiven}")
                            OvStatRow("Total Sessions", "${pc.totalSessions}")
                            OvStatRow("Safety Flags", "${pc.safetyFlags}")
                        }
                        if (pc.topics.isNotEmpty()) {
                            OvSectionTitle("Topic Mastery")
                            pc.topics.forEach { tp ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(14.dp)) {
                                    Text(tp.topicId, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink, modifier = Modifier.weight(1f))
                                    Text("${(tp.currentMastery * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = VColors.violet)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── School Detail / Discovery Overlay ──
@Composable
fun SchoolDetailOverlay(viewModel: ParentViewModel) {
    val state by viewModel.schoolDiscoveryState.collectAsState()
    Column(Modifier.fillMaxWidth()) {
        OvSectionTitle("Discover Schools")
        when (val s = state) {
            is UiState.Loading -> OvLoading()
            is UiState.Error -> OvError(s.message)
            is UiState.Success -> {
                if (s.data.isEmpty()) { Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) { Text("No schools found nearby", color = VColors.ink3) } }
                else s.data.forEach { school -> DiscoveredSchoolItem(school) }
            }
        }
    }
}

@Composable
private fun DiscoveredSchoolItem(school: DiscoveredSchoolDto) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(VColors.white, VShapes.md).shadow(1.dp, VShapes.md).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(school.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VColors.ink, modifier = Modifier.weight(1f))
            Box(Modifier.background(VColors.goldSoft, VShapes.full).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text("${school.rating}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = VColors.gold)
            }
        }
        Text(school.location, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink3, modifier = Modifier.padding(top = 4.dp))
        school.board?.let { Text("Board: $it", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2, modifier = Modifier.padding(top = 2.dp)) }
        school.distanceKm?.let { Text("${it} km away", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VColors.violet, modifier = Modifier.padding(top = 2.dp)) }
        school.medium?.let { Text("Medium: $it", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3, modifier = Modifier.padding(top = 2.dp)) }
    }
}
