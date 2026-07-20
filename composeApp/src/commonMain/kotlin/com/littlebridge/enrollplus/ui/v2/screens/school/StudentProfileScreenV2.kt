package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentParentDto
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentProfileDto
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentTeacherDto
import com.littlebridge.enrollplus.feature.admin.presentation.StudentProfileUiState
import com.littlebridge.enrollplus.feature.admin.presentation.StudentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VActionCard
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonProfile
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * RA-45 / RA-SP: StudentProfileScreenV2 — a single student's record for the
 * admin, redesigned as a modern student-dashboard experience (matching the
 * Teacher Profile redesign).
 *
 * Structure (top → bottom): Hero banner · KPI carousel · Academic overview ·
 * Teacher connections · Parent connections · Attendance overview · Insights ·
 * Recent activity timeline · Marks · Leave · Fees · Contact info ·
 * Administrative info · Danger zone.
 *
 * [studentId] is passed by the caller and loaded via [StudentProfileViewModel.load]
 * in a LaunchedEffect. Three states via [VStateHost] (LAW 3). Portal overlay —
 * back returns to the roster.
 */
@Composable
fun StudentProfileScreenV2(
    studentId: String,
    onBack: () -> Unit = {},
    onRemoved: () -> Unit = onBack,
    onOpenHealth: ((String, String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: StudentProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(studentId) { viewModel.load(studentId) }
    LaunchedEffect(state.removed) { if (state.removed) onRemoved() }

    var showEditSheet by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(
            title = appString(StringKeys.SCH_STUDENT),
            onBack = onBack,
            action = {
                androidx.compose.material3.IconButton(
                    onClick = { showEditSheet = true },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(VIcons.Edit3, contentDescription = "Edit", tint = VColors.ink, modifier = Modifier.size(18.dp))
                }
            },
        )
        StudentProfileContent(
            state = state,
            onRetry = viewModel::retry,
            onRemove = { viewModel.remove(studentId) },
            onOpenHealth = onOpenHealth,
            modifier = Modifier.fillMaxSize(),
        )
    }

    if (showEditSheet && state.profile != null) {
        val p = state.profile!!
        EditStudentSheet(
            fullName = p.student.fullName,
            className = p.student.className,
            section = p.student.section,
            rollNumber = p.student.rollNumber,
            admissionDate = p.admissionDate,
            isSaving = state.isEditing,
            error = state.editError,
            success = state.editSuccess,
            onDismiss = { showEditSheet = false; viewModel.clearEditError() },
            onSave = { name, cls, sec, roll, admission ->
                viewModel.updateStudent(studentId, name, cls, sec, roll, admission)
            },
        )
    }
}

@Composable
private fun StudentProfileContent(
    state: StudentProfileUiState,
    onRetry: () -> Unit,
    onRemove: () -> Unit,
    onOpenHealth: ((String, String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var confirmRemove by remember { mutableStateOf(false) }
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.profile == null && !state.isLoading && state.error == null,
            emptyTitle = appString(StringKeys.SCH_NO_PROFILE),
            emptyBody = appString(StringKeys.SCH_NO_STUDENT_PROFILE_DESC),
            emptyIcon = VIcons.User,
            onRetry = onRetry,
            skeleton = { SkeletonProfile() },
        ) {
            val p = state.profile ?: return@VStateHost
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                StudentProfileBody(p)

                if (onOpenHealth != null) {
                    VSectionHeader(title = appString(StringKeys.SCH_HEALTH_RECORDS))
                    VActionCard(
                        title = appString(StringKeys.SCH_HEALTH_RECORDS),
                        subtitle = appString(StringKeys.SCH_HEALTH_RECORDS_DESC),
                        icon = VIcons.Heart,
                        onClick = { onOpenHealth(p.student.id, p.student.fullName) },
                    )
                }

                Spacer(Modifier.height(8.dp))
                DangerZone(
                    isRemoving = state.isRemoving,
                    removeError = state.removeError,
                    onRequestRemove = { confirmRemove = true },
                )
            }
        }
    }

    VConfirmDialog(
        visible = confirmRemove,
        title = appString(StringKeys.SCH_REMOVE_STUDENT),
        message = appString(StringKeys.SCH_REMOVE_STUDENT_MSG, "name" to (state.profile?.student?.fullName ?: appString(StringKeys.SCH_THIS_STUDENT))),
        confirmLabel = appString(StringKeys.SCH_REMOVE),
        icon = VIcons.AlertTriangle,
        onConfirm = { confirmRemove = false; onRemove() },
        onDismiss = { confirmRemove = false },
    )
}

@Composable
private fun StudentProfileBody(p: StudentProfileDto) {
    // 1:1 with #view-student-profile in people-tab-premium.html:
    // Hero · KPI carousel · Academic overview · Teacher connections ·
    // Parent connections · Attendance overview · Recent marks · Leave records ·
    // Fees status · Contact information. (Health action + Danger zone appended
    // by the caller.)
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        HeroBanner(p)                   // Hero profile banner
        KpiCarousel(p)                  // KPI carousel (Overview)
        AcademicOverview(p)             // Academic overview
        TeacherConnections(p.teachers)  // Teacher connections
        ParentConnections(p.parents)    // Parent connections
        AttendanceOverview(p)           // Attendance overview (Overall + This Term)
        MarksSection(p)                 // Recent marks
        LeaveSection(p)                 // Leave records
        FeesSection(p)                  // Fees status
        ContactInformation(p)           // Contact information
    }
}

// ───────────────────────── 1. Hero profile banner ─────────────────────────

@Composable
private fun HeroBanner(p: StudentProfileDto) {
    val s = p.student
    val active = p.status.equals("active", ignoreCase = true)
    VCard(padding = 20.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VAvatar(name = s.fullName, src = s.profilePhotoUrl, size = 76.dp, ring = true)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(s.fullName, style = VTypography.h2, color = VColors.ink)
                Text(
                    "Class ${s.className} · Sec ${s.section}",
                    style = VTypography.caption, color = VColors.ink2,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VBadge(
                        text = if (active) appString(StringKeys.SCH_ACTIVE) else appString(StringKeys.SCH_INACTIVE),
                        tone = if (active) VBadgeTone.Success else VBadgeTone.Neutral,
                        leadingIcon = VIcons.Check,
                    )
                    if (p.isNewAdmission) VBadge(text = appString(StringKeys.SCH_NEW_ADMISSION), tone = VBadgeTone.Arctic)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            HeroFact(VIcons.Bookmark, appString(StringKeys.SCH_ADMISSION_NO), s.studentCode)
            HeroFact(VIcons.User, appString(StringKeys.SCH_ROLL_NO), s.rollNumber)
        }
    }
}

@Composable
private fun HeroFact(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(VColors.violet.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(value, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
            Text(label, style = VTypography.label, color = VColors.ink3)
        }
    }
}

// ──────────────────────────── 2. KPI carousel ─────────────────────────────

private data class KpiCardData(
    val label: String,
    val value: String,
    val support: String,
    val icon: ImageVector,
    val tone: VBadgeTone,
)

@Composable
private fun KpiCarousel(p: StudentProfileDto) {
    val kpis = buildList {
        add(KpiCardData(appString(StringKeys.SCH_ATTENDANCE), "${p.attendancePercent.toInt()}%", appString(StringKeys.SCH_OVERALL), VIcons.Check, VBadgeTone.Success))
        add(KpiCardData(appString(StringKeys.SCH_TEACHERS), p.teacherCount.toString(), appString(StringKeys.SCH_CONNECTED), VIcons.Users, VBadgeTone.Arctic))
        add(KpiCardData(appString(StringKeys.SCH_PARENTS), p.parentCount.toString(), appString(StringKeys.SCH_LINKED), VIcons.Heart, VBadgeTone.Warning))
        add(KpiCardData(appString(StringKeys.SCH_SUBJECTS), p.subjectCount.toString(), appString(StringKeys.SCH_STUDIED), VIcons.BookOpen, VBadgeTone.Arctic))
        p.academicScore?.let {
            add(KpiCardData(appString(StringKeys.SCH_ACADEMIC_SCORE), "${it.toInt()}%", appString(StringKeys.SCH_AVERAGE), VIcons.Star, VBadgeTone.Success))
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_OVERVIEW))
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            kpis.forEach { kpi -> KpiCard(kpi) }
        }
    }
}


@Composable
private fun KpiCard(data: KpiCardData) {
    val tint = when (data.tone) {
        VBadgeTone.Arctic, VBadgeTone.Accent -> VColors.violet
        VBadgeTone.Success -> VColors.success
        VBadgeTone.Warning -> VColors.gold
        VBadgeTone.Danger -> VColors.error
        VBadgeTone.Neutral -> VColors.ink3
    }
    VCard(modifier = Modifier.width(150.dp), padding = 16.dp) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(data.icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(data.value, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp), color = VColors.ink)
        Text(data.label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
        Text(data.support, style = VTypography.label, color = VColors.ink3)
    }
}

// ─────────────────────────── 3. Academic overview ─────────────────────────

@Composable
private fun AcademicOverview(p: StudentProfileDto) {
    val s = p.student
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_ACADEMIC_OVERVIEW))
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailRow(VIcons.School, appString(StringKeys.SCH_CLASS), s.className)
                DetailRow(VIcons.Bookmark, appString(StringKeys.SCH_SECTION), s.section)
                DetailRow(VIcons.User, appString(StringKeys.SCH_ROLL_NUMBER), s.rollNumber)
                DetailRow(VIcons.Calendar, appString(StringKeys.SCH_ADMISSION_DATE), p.admissionDate?.takeIf { it.isNotBlank() } ?: "—")
            }
        }
    }
}

// ───────────────────────── 4. Teacher connections ─────────────────────────
// HTML .dr rows: 28dp gradient-initial mini avatar + teacher name (left) +
// subject (right), hairline-separated inside one card.

@Composable
private fun TeacherConnections(teachers: List<StudentTeacherDto>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_TEACHER_CONNECTIONS))
        if (teachers.isEmpty()) {
            EmptyCard(VIcons.Users, appString(StringKeys.SCH_NO_TEACHERS_CONNECTED))
        } else {
            VCard(padding = 18.dp) {
                teachers.forEachIndexed { index, t ->
                    ConnectionRow(name = t.name, trailing = t.subject)
                    if (index != teachers.lastIndex) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
                    }
                }
            }
        }
    }
}

// ───────────────────────── 5. Parent connections ──────────────────────────
// HTML .dr rows: 28dp mini avatar + "Name (Relation)" left + phone right.

@Composable
private fun ParentConnections(parents: List<StudentParentDto>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_PARENT_CONNECTIONS))
        if (parents.isEmpty()) {
            EmptyCard(VIcons.Heart, appString(StringKeys.SCH_NO_PARENTS_LINKED))
        } else {
            VCard(padding = 18.dp) {
                parents.forEachIndexed { index, parent ->
                    ConnectionRow(
                        name = "${parent.name} (${parent.relation})",
                        trailing = parent.phone?.takeIf { it.isNotBlank() } ?: "—",
                    )
                    if (index != parents.lastIndex) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
                    }
                }
            }
        }
    }
}

/** Shared connection detail-row: 28dp gradient avatar + name (left) + value (right). */
@Composable
private fun ConnectionRow(name: String, trailing: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VAvatar(name = name, size = 28.dp)
        Text(
            name,
            style = VTypography.caption,
            color = VColors.ink,
            modifier = Modifier.weight(1f),
        )
        Text(
            trailing,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
    }
}

// ───────────────────────── 6. Attendance overview ─────────────────────────

@Composable
private fun AttendanceOverview(p: StudentProfileDto) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_ATTENDANCE_OVERVIEW))
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Overall Attendance — mint (--success #A8E6CF) fill, matching prototype.
                MetricBar(
                    label = appString(StringKeys.SCH_OVERALL_ATTENDANCE),
                    percent = p.attendancePercent.toInt(),
                    fillColor = Color(0xFFA8E6CF),
                )
                // This Term — accent (violet) fill.
                MetricBar(
                    label = appString(StringKeys.SCH_THIS_TERM),
                    percent = p.thisTermAttendance,
                    fillColor = VColors.violet,
                )
            }
        }
    }
}

@Composable
private fun MetricBar(label: String, percent: Int, fillColor: Color) {
    val clamped = percent.coerceIn(0, 100)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
            Text("$clamped%", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
        }
        // .btr track + .bf fill (8px, pill radius) — drawn directly for exact fill colors.
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(VColors.cream),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(clamped / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(fillColor),
            )
        }
    }
}

// ───────────────────────────── Recent marks ───────────────────────────────
// HTML .marks-row: subject name left (SemiBold ink), score "92/100" right
// (SemiBold, accent/violet). Rows separated by hairlines inside one card.

@Composable
private fun MarksSection(p: StudentProfileDto) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_MARKS))
        if (p.marks.isEmpty()) {
            EmptyCard(VIcons.BookOpen, appString(StringKeys.SCH_NO_MARKS_RECORDED))
        } else {
            VCard(padding = 18.dp) {
                p.marks.forEachIndexed { index, m ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            m.subject,
                            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                            color = VColors.ink,
                        )
                        val score = m.marks?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "—"
                        Text(
                            "$score/${m.maxMarks}",
                            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                            color = VColors.violet,
                        )
                    }
                    if (index != p.marks.lastIndex) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
                    }
                }
            }
        }
    }
}

// ───────────────────────────── Leave records ──────────────────────────────
// HTML: two aggregate detail rows — Total Leave Days · Pending Requests.

@Composable
private fun LeaveSection(p: StudentProfileDto) {
    val totalLeaveDays = p.leave.sumOf { leaveDayCount(it.dateFrom, it.dateTo) }
    val pending = p.leave.count { it.status.equals("pending", ignoreCase = true) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_LEAVE))
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailRow(VIcons.Calendar, appString(StringKeys.SCH_TOTAL_LEAVE_DAYS), totalLeaveDays.toString())
                DetailRow(VIcons.AlertTriangle, appString(StringKeys.SCH_PENDING_REQUESTS), pending.toString())
            }
        }
    }
}

/** Inclusive day count between two ISO dates (yyyy-MM-dd). Falls back to 1. */
private fun leaveDayCount(from: String, to: String): Int = runCatching {
    val f = epochDay(from.take(10))
    val t = epochDay(to.take(10))
    (t - f + 1).toInt().coerceAtLeast(1)
}.getOrDefault(1)

/** Days since 1970-01-01 for an ISO date, via a proleptic-Gregorian formula. */
private fun epochDay(iso: String): Long {
    val parts = iso.split("-")
    val y = parts[0].toLong()
    val m = parts[1].toInt()
    val d = parts[2].toInt()
    var total = 0L
    var year = 1970L
    if (y >= 1970) {
        while (year < y) { total += if (isLeap(year)) 366 else 365; year++ }
    } else {
        while (year > y) { year--; total -= if (isLeap(year)) 366 else 365 }
    }
    val daysInMonth = intArrayOf(31, if (isLeap(y)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    for (mm in 1 until m) total += daysInMonth[mm - 1]
    total += (d - 1)
    return total
}

private fun isLeap(y: Long): Boolean = (y % 4 == 0L && y % 100 != 0L) || (y % 400 == 0L)

// ────────────────────────────── Fees status ───────────────────────────────
// HTML .dr with $ icon chip: title left, status value colored right
// (Paid → success, Pending/Overdue → warning/danger).

@Composable
private fun FeesSection(p: StudentProfileDto) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_FEES))
        if (p.fees.isEmpty()) {
            EmptyCard(VIcons.Wallet, appString(StringKeys.SCH_NO_FEE_RECORDS))
        } else {
            VCard(padding = 18.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    p.fees.forEach { f ->
                        val statusColor = when (f.status.uppercase()) {
                            "PAID" -> VColors.success
                            "OVERDUE" -> VColors.error
                            else -> VColors.gold
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(VColors.cream),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(VIcons.Wallet, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(16.dp))
                            }
                            Text(f.title, style = VTypography.caption, color = VColors.ink3, modifier = Modifier.weight(1f))
                            Text(
                                f.status.replaceFirstChar { it.uppercase() },
                                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                                color = statusColor,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────── Contact information ───────────────────────────
// HTML: Father's Phone · Mother's Phone · Address. Phones derived from the
// parents list by relationship; address from the student record (V15).

@Composable
private fun ContactInformation(p: StudentProfileDto) {
    val father = p.parents.firstOrNull { it.relation.contains("father", ignoreCase = true) }
    val mother = p.parents.firstOrNull { it.relation.contains("mother", ignoreCase = true) }
    val address = p.student.address?.takeIf { it.isNotBlank() }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_CONTACT_INFORMATION))
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailRow(
                    VIcons.Phone,
                    appString(StringKeys.SCH_FATHERS_PHONE),
                    father?.phone?.takeIf { it.isNotBlank() } ?: "—",
                )
                DetailRow(
                    VIcons.Phone,
                    appString(StringKeys.SCH_MOTHERS_PHONE),
                    mother?.phone?.takeIf { it.isNotBlank() } ?: "—",
                )
                if (address != null) {
                    DetailRow(VIcons.MapPin, appString(StringKeys.SCH_ADDRESS), address)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(VColors.cream),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(16.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = VTypography.label, color = VColors.ink3)
            Text(value, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
        }
    }
}

// ─────────────────────────────── Danger zone ──────────────────────────────

@Composable
private fun DangerZone(
    isRemoving: Boolean,
    removeError: String?,
    onRequestRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_DANGER_ZONE))
        VCard(padding = 18.dp, border = true) {
            Text(appString(StringKeys.SCH_REMOVE_STUDENT), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.error)
            Spacer(Modifier.height(4.dp))
            Text(
                appString(StringKeys.SCH_REMOVE_STUDENT_DANGER),
                style = VTypography.caption, color = VColors.ink2,
            )
            Spacer(Modifier.height(14.dp))
            removeError?.let { err ->
                Text(err, style = VTypography.caption, color = VColors.error)
                Spacer(Modifier.height(8.dp))
            }
            VButton(
                text = appString(StringKeys.SCH_REMOVE_FROM_SCHOOL),
                onClick = onRequestRemove,
                variant = VButtonVariant.Destructive,
                full = true,
                enabled = !isRemoving,
                loading = isRemoving,
                leading = { Icon(VIcons.Close, contentDescription = null, modifier = Modifier.size(16.dp)) },
            )
        }
    }
}

// ────────────────────────────── shared bits ───────────────────────────────

@Composable
private fun EmptyCard(icon: ImageVector, message: String) {
    VCard(padding = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(VColors.cream),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(17.dp))
            }
            Text(message, style = VTypography.body, color = VColors.ink2)
        }
    }
}

// ────────────────────────────── edit sheet ───────────────────────────────

@Composable
private fun EditStudentSheet(
    fullName: String,
    className: String,
    section: String,
    rollNumber: String,
    admissionDate: String?,
    isSaving: Boolean,
    error: String?,
    success: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit,
) {
    var name by remember { mutableStateOf(fullName) }
    var cls by remember { mutableStateOf(className) }
    var sec by remember { mutableStateOf(section) }
    var roll by remember { mutableStateOf(rollNumber) }
    var admission by remember { mutableStateOf(admissionDate ?: "") }

    VBottomSheet(visible = true, onDismiss = onDismiss) {
        VBottomSheetHeader(title = "Edit Student", subtitle = "Update student details", onClose = onDismiss)
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VInput(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                placeholder = "Student name",
            )
            VInput(
                value = cls,
                onValueChange = { cls = it },
                label = "Class",
                placeholder = "e.g. Class 10",
            )
            VInput(
                value = sec,
                onValueChange = { sec = it },
                label = "Section",
                placeholder = "e.g. A",
            )
            VInput(
                value = roll,
                onValueChange = { roll = it },
                label = "Roll Number",
                placeholder = "e.g. 15",
                keyboardType = KeyboardType.Number,
            )
            VInput(
                value = admission,
                onValueChange = { admission = it },
                label = "Admission Date",
                placeholder = "YYYY-MM-DD",
            )
            error?.let {
                Text(it, style = VTypography.caption, color = VColors.error)
            }
            if (success) {
                Text("Saved successfully", style = VTypography.caption, color = VColors.success)
            }
            Spacer(Modifier.height(4.dp))
            // Prototype sheet footer: Cancel (outline) + Save Changes (violet primary).
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    variant = VButtonVariant.Secondary,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f),
                )
                VButton(
                    text = "Save Changes",
                    onClick = { onSave(name, cls, sec, roll, admission) },
                    variant = VButtonVariant.Primary,
                    enabled = !isSaving,
                    loading = isSaving,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
