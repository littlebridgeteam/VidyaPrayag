package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.StudentPerformanceDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.StudentProfileData
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherStudentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherSpinner

/**
 * Scoped student-profile drill-down (read-only). Loads `GET /teacher/students/{id}`
 * via [TeacherStudentProfileViewModel]; a 403 surfaces as a polite "not your
 * student" wall rather than an error. Attendance / performance / server-computed
 * flags / privacy-gated parent contact, in the Parents-Portal card vocabulary.
 *
 * Each pane gets its OWN VM instance (keyed by studentId via koinViewModel below
 * is shared, so we re-load on studentId change through LaunchedEffect).
 */
@Composable
fun TeacherStudentProfilePane(
    studentId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherStudentProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(studentId) { viewModel.load(studentId) }

    val c = VtC
    Column(modifier.fillMaxSize()) {
        TeacherSubHeader(
            title = state.profile?.name ?: appString(StringKeys.TC_STUDENT),
            subtitle = state.profile?.let { "${it.className} · ${it.section}" },
            onBack = onBack,
        )
        when {
            state.isLoading -> TeacherCenterState { TeacherSpinner() }
            state.forbidden -> TeacherCenterState {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    VtIconDisc(VIcons.Lock, tint = c.ink3, bg = c.cream, size = 56.dp, glyph = 26.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(appString(StringKeys.TC_NOT_YOUR_STUDENT), style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        appString(StringKeys.TC_NOT_YOUR_STUDENT_DESC),
                        style = VTypography.caption,
                        color = c.ink3,
                    )
                }
            }
            state.error != null -> TeacherCenterState {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(appString(StringKeys.TC_COULDNT_LOAD_PROFILE), style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(state.error ?: "", style = VTypography.caption, color = c.ink3)
                    Spacer(Modifier.height(14.dp))
                    VButton(appString(StringKeys.TC_TRY_AGAIN), onClick = { viewModel.retry() }, size = VButtonSize.Sm, tone = VButtonTone.Lavender)
                }
            }
            state.profile != null -> {
                val profile = state.profile ?: return
                StudentProfileBody(profile)
            }
            else -> TeacherCenterState { TeacherSpinner() }
        }
    }
}

@Composable
private fun StudentProfileBody(p: StudentProfileData) {
    val c = VtC
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { IdentityCard(p) }
        item { AttendanceProfileCard(p) }
        if (p.performance.isNotEmpty()) item { PerformanceCard(p.performance) }
        if (p.flags.isNotEmpty()) item { FlagsCard(p.flags) }
        p.parentContact?.let { pc ->
            if (!pc.name.isNullOrBlank() || !pc.phone.isNullOrBlank()) {
                item { ParentContactCard(pc.name, pc.phone) }
            }
        }
        item { TeacherStudentGamificationCard(studentId = p.studentId, studentName = p.name) }
    }
}

@Composable
private fun IdentityCard(p: StudentProfileData) {
    val c = VtC
    VtCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).clip(CircleShape).background(c.lavenderLight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    p.name.take(1).uppercase(),
                    style = VTypography.h2,
                    color = c.accentDeep,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(p.name, style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(
                    buildString {
                        append("${p.className} · ${p.section}")
                        p.roll?.let { append(" · ${appString(StringKeys.TC_ROLL_LABEL)} $it") }
                    },
                    style = VTypography.caption,
                    color = c.ink2,
                )
            }
        }
    }
}

@Composable
private fun AttendanceProfileCard(p: StudentProfileData) {
    val c = VtC
    val a = p.attendance
    val pct = a.rate?.let { (it * 100).toInt() }
    VtCard {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                VtEyebrow(appString(StringKeys.TC_ATTENDANCE))
                TrendPill(a.trend)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            if (pct == null) "—" else "$pct%",
                            style = VTypography.h3,
                            color = attendanceColor(c, pct),
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            appString(StringKeys.TC_PERCENT_PRESENT_OVERALL, "pct" to (pct ?: 0).toString()),
                            style = VTypography.caption,
                            color = c.ink3,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                        drawRoundRect(color = c.cream, size = size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                        val w = size.width * (pct ?: 0).coerceIn(0, 100) / 100f
                        drawRoundRect(color = attendanceColor(c, pct), size = size.copy(width = w), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                    }
                    if (a.recent.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(appString(StringKeys.TC_RECENT), style = VTypography.label, color = c.ink3)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            a.recent.take(8).forEach { day ->
                                Box(
                                    Modifier.size(18.dp).clip(RoundedCornerShape(5.dp)).background(attendanceDayColor(c, day.status)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendPill(trend: String) {
    val c = VtC
    val (bg, fg, label) = when (trend.lowercase()) {
        "improving" -> Triple(c.success.copy(alpha = 0.16f), c.successInk, appString(StringKeys.TC_IMPROVING))
        "declining" -> Triple(c.danger.copy(alpha = 0.12f), c.dangerInk, appString(StringKeys.TC_DECLINING))
        "flat" -> Triple(c.cream, c.ink2, appString(StringKeys.TC_STEADY))
        else -> return
    }
    VtPill(label, bg = bg, fg = fg)
}

private fun attendanceColor(c: VtC, pct: Int?): Color = when {
    pct == null -> c.ink3
    pct >= 85 -> c.success
    pct >= 70 -> c.warning
    else -> c.danger
}

private fun attendanceDayColor(c: VtC, status: String): Color = when (status.lowercase()) {
    "present" -> c.success
    "late" -> c.warning
    "leave" -> c.accent
    "absent" -> c.danger
    else -> c.hairline
}

@Composable
private fun PerformanceCard(perf: List<StudentPerformanceDto>) {
    val c = VtC
    VtCard {
        Column {
            VtEyebrow(appString(StringKeys.TC_PERFORMANCE))
            Spacer(Modifier.height(10.dp))
            perf.forEachIndexed { i, e ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(e.assessmentName, style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            buildString {
                                append(e.subject)
                                if (!e.date.isNullOrBlank()) append(" · ${prettyDateShort(e.date)}")
                            },
                            style = VTypography.caption,
                            color = c.ink3,
                        )
                    }
                    when {
                        e.isAbsent -> VtPill(appString(StringKeys.TC_ABSENT), bg = c.danger.copy(alpha = 0.12f), fg = c.dangerInk)
                        e.marks == null -> VtPill(appString(StringKeys.TC_PENDING), bg = c.cream, fg = c.ink2)
                        else -> {
                            val mk = e.marks ?: 0.0
                            val ratio = if (e.max > 0) mk / e.max else 0.0
                            val tint = when {
                                ratio >= 0.6 -> c.successInk
                                ratio >= 0.4 -> c.warningInk
                                else -> c.dangerInk
                            }
                            Text(
                                "${fmt1(mk.toFloat())}/${e.max}",
                                style = VTypography.caption,
                                color = tint,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlagsCard(flags: List<String>) {
    val c = VtC
    VtCard {
        Column {
            VtEyebrow(appString(StringKeys.TC_FLAGS), dot = c.warning)
            Spacer(Modifier.height(10.dp))
            flags.forEachIndexed { i, code ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                val (tint, bg) = flagMetaColors(c, code)
                val text = flagMetaKey(code).let { if (it.isNotEmpty()) appString(it) else code.replace('_', ' ').replaceFirstChar { ch -> ch.uppercase() } }
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(bg).padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(VIcons.AlertTriangle, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
                    Text(text, style = VTypography.caption, color = tint, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private fun flagMetaKey(code: String): String = when (code) {
    "low_attendance" -> StringKeys.TC_LOW_ATTENDANCE
    "recent_absences" -> StringKeys.TC_RECENT_ABSENCES
    "failing_trend" -> StringKeys.TC_FAILING_TREND
    "dropping" -> StringKeys.TC_MARKS_DROPPING
    "no_data" -> StringKeys.TC_NOT_ENOUGH_DATA
    else -> ""
}

private fun flagMetaColors(c: VtC, code: String): Pair<Color, Color> = when (code) {
    "low_attendance" -> c.dangerInk to c.danger.copy(alpha = 0.10f)
    "recent_absences" -> c.dangerInk to c.danger.copy(alpha = 0.10f)
    "failing_trend" -> c.dangerInk to c.danger.copy(alpha = 0.10f)
    "dropping" -> c.warningInk to c.warning.copy(alpha = 0.14f)
    "no_data" -> c.ink2 to c.cream
    else -> c.ink2 to c.cream
}

@Composable
private fun ParentContactCard(name: String?, phone: String?) {
    val c = VtC
    VtCard {
        Column {
            VtEyebrow(appString(StringKeys.TC_PARENT_CONTACT))
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                VtIconDisc(VIcons.User, tint = c.tealDeep, bg = c.teal.copy(alpha = 0.14f), size = 40.dp, glyph = 18.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(name ?: appString(StringKeys.TC_PARENT_GUARDIAN), style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.Bold)
                    if (!phone.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(VIcons.Phone, contentDescription = null, tint = c.ink3, modifier = Modifier.size(14.dp))
                            Text(phone, style = VTypography.caption, color = c.ink2)
                        }
                    }
                }
            }
        }
    }
}
