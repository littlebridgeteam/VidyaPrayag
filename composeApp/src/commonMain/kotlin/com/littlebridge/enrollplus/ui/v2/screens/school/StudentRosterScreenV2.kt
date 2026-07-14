package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentDto
import com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VStatusDot
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import androidx.compose.ui.text.font.FontWeight
import org.koin.compose.viewmodel.koinViewModel

/**
 * RA-45: StudentRosterScreenV2 — the admin's live student roster.
 *
 * Wired to [StudentRosterViewModel] (`GET /api/v1/school/students`, school-scoped).
 * Admin can add a student (`POST`) and remove one (`DELETE`, soft-delete, gated by
 * a confirm dialog). Tapping a row opens that student's profile. Three states via
 * [VStateHost] (LAW 3). Portal overlay — back returns to the People tab.
 */
@Composable
fun StudentRosterScreenV2(
    onBack: () -> Unit = {},
    onOpenStudent: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StudentRosterViewModel = koinViewModel(),
    classesViewModel: ClassesSubjectsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val classesState by classesViewModel.state.collectAsStateV2()
    var showAdd by remember { mutableStateOf(false) }
    var pendingRemoval by remember { mutableStateOf<StudentDto?>(null) }

    Column(modifier.fillMaxSize().statusBarsPadding()
        .imePadding()
        .navigationBarsPadding()) {
        VBackHeader(
            title = appString(StringKeys.SCH_STUDENTS_HEADER),
            onBack = onBack,
            action = {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CREATE),
                    onClick = { showAdd = true },
                    variant = VButtonVariant.Secondary,
                    size = VButtonSize.Sm,
                    enabled = !state.isSaving,
                )
            },
        )
        VPullRefresh(isRefreshing = state.isLoading && state.students.isNotEmpty(), onRefresh = { viewModel.load() }) {
            StudentRosterContent(
                state = state,
                onRetry = viewModel::load,
                onOpenStudent = onOpenStudent,
                onRemoveClick = { pendingRemoval = it },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    // Auto-close the add dialog once a student is successfully added.
    LaunchedEffect(state.infoMessage) {
        if (showAdd && state.infoMessage != null) {
            showAdd = false
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(Unit) {
        classesViewModel.loadClasses()
    }

    if (showAdd) {
        AddStudentSheet(
            isSubmitting = state.isSaving,
            error = state.addError,
            onDismiss = { showAdd = false; viewModel.clearMessages() },
            onSubmit = { name, cls, sec, roll, phone, admission -> viewModel.addStudent(name, cls, sec, roll, phone, admission) },
            availableClassNames = classesState.classes.map { it.name },
        )
    }

    val removal = pendingRemoval
    VConfirmDialog(
        visible = removal != null,
        title = appString(StringKeys.SCH_REMOVE_STUDENT),
        message = appString(StringKeys.SCH_REMOVE_STUDENT_ROSTER_MSG, "name" to (removal?.fullName ?: appString(StringKeys.SCH_THIS_STUDENT))),
        confirmLabel = appString(StringKeys.SCH_REMOVE),
        icon = VIcons.AlertTriangle,
        onConfirm = {
            removal?.let { viewModel.removeStudent(it.id) }
            pendingRemoval = null
        },
        onDismiss = { pendingRemoval = null },
    )
}

@Composable
private fun StudentRosterContent(
    state: StudentRosterState,
    onRetry: () -> Unit,
    onOpenStudent: (String) -> Unit,
    onRemoveClick: (StudentDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.students.isEmpty(),
            emptyTitle = appString(StringKeys.SCH_NO_STUDENTS_YET),
            emptyBody = appString(StringKeys.SCH_NO_STUDENTS_YET_DESC),
            emptyIcon = VIcons.Users,
            onRetry = onRetry,
            skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 8) },
        ) {
            state.students.forEachIndexed { i, s ->
                StudentCard(
                    student = s,
                    removing = state.removingIds.contains(s.id),
                    onOpen = { onOpenStudent(s.id) },
                    onRemove = { onRemoveClick(s) },
                    modifier = Modifier.staggeredItemEntrance(i, state.students.isNotEmpty()),
                )
            }
        }
    }
}

/**
 * RA-SP: enriched student card matching the People tab prototype.
 */
@Composable
private fun StudentCard(
    student: StudentDto,
    removing: Boolean,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val lowAttendance = student.attendancePercent in 0.1f..74.9f
    val homeworkLow = student.homeworkPercent < 80f

    VCard(modifier = modifier.fillMaxWidth(), padding = 0.dp, onClick = onOpen) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header: avatar · name · class badge · more
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VAvatar(name = student.fullName, src = student.profilePhotoUrl, size = 52.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(student.fullName, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink, maxLines = 1)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Roll #${student.rollNumber}",
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                        Text(
                            "Admission #${student.studentCode}",
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                    }
                }
                VBadge(
                    text = "Class ${student.className}-${student.section}",
                    tone = VBadgeTone.Arctic,
                )
                // Overflow menu (quick actions).
                Box {
                    Box(
                        Modifier.size(34.dp).clip(RoundedCornerShape(10.dp))
                            .background(VColors.ink.copy(alpha = 0.06f))
                            .clickable(enabled = !removing) { menuOpen = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(VIcons.More, contentDescription = "Actions", tint = VColors.ink2, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text(appString(StringKeys.SCH_VIEW_PROFILE)) },
                            onClick = { menuOpen = false; onOpen() },
                            leadingIcon = { Icon(VIcons.User, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        )
                        DropdownMenuItem(
                            text = { Text(appString(StringKeys.COMMON_BUTTON_EDIT)) },
                            onClick = { menuOpen = false; onOpen() },
                            leadingIcon = { Icon(VIcons.Settings, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        )
                        DropdownMenuItem(
                            text = { Text(appString(StringKeys.SCH_CONTACT_PARENT)) },
                            onClick = { menuOpen = false; onOpen() },
                            leadingIcon = { Icon(VIcons.Phone, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        )
                        DropdownMenuItem(
                            text = { Text(appString(StringKeys.SCH_REMOVE), style = VTypography.body.copy(color = VColors.error)) },
                            onClick = { menuOpen = false; onRemove() },
                            leadingIcon = { Icon(VIcons.Close, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        )
                    }
                }
            }

            // Parent contact row
            if (student.parentName != null || student.parentPhone != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    student.parentName?.takeIf { it.isNotBlank() }?.let { parentName ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(VIcons.User, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
                            Text(parentName, style = VTypography.caption, color = VColors.ink2)
                        }
                    }
                    student.parentPhone?.takeIf { it.isNotBlank() }?.let { phone ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(VIcons.Phone, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
                            Text(maskStudentPhone(phone), style = VTypography.caption, color = VColors.ink2)
                        }
                    }
                }
            }

            // Status alerts
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VStatusDot(
                    color = when {
                        student.status.equals("active", ignoreCase = true) && !lowAttendance && !homeworkLow && !student.feesPending && !student.isNewAdmission -> VColors.mint
                        lowAttendance || student.feesPending -> VColors.coral
                        homeworkLow || student.isNewAdmission -> VColors.gold
                        else -> VColors.sky
                    },
                    ring = false,
                )
                Text(
                    when {
                        student.status.equals("inactive", ignoreCase = true) -> appString(StringKeys.SCH_INACTIVE)
                        lowAttendance -> appString(StringKeys.SCH_LOW_ATTENDANCE)
                        student.feesPending -> "Fees Pending"
                        student.isNewAdmission -> appString(StringKeys.SCH_NEW_ADMISSION)
                        homeworkLow -> "Homework Due"
                        else -> "Healthy"
                    },
                    style = VTypography.caption.copy(fontWeight = FontWeight.Medium),
                    color = VColors.ink,
                )
            }

            // Attendance + Homework bars
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StudentProgressLabel(
                    label = "ATTENDANCE",
                    value = student.attendancePercent,
                    valueText = "${student.attendancePercent.toInt()}%",
                    tone = if (lowAttendance) VBadgeTone.Warning else VBadgeTone.Success,
                    modifier = Modifier.weight(1f),
                )
                StudentProgressLabel(
                    label = "HOMEWORK",
                    value = student.homeworkPercent,
                    valueText = "${student.homeworkPercent.toInt()}%",
                    tone = if (homeworkLow) VBadgeTone.Warning else VBadgeTone.Success,
                    modifier = Modifier.weight(1f),
                )
            }

            // Metric strip: parents + teachers
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricChip(
                    icon = VIcons.Heart,
                    value = student.parentCount.toString(),
                    label = appString(StringKeys.SCH_PARENTS),
                    modifier = Modifier.weight(1f),
                )
                MetricChip(
                    icon = VIcons.Users,
                    value = student.teacherCount.toString(),
                    label = appString(StringKeys.SCH_TEACHERS),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StudentProgressLabel(
    label: String,
    value: Float,
    valueText: String,
    tone: VBadgeTone,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold), color = VColors.ink3)
            Text(valueText, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
        }
        VProgressBar(value = value, tone = tone, height = 6.dp)
    }
}

@Composable
private fun MetricChip(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(VColors.cream).padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(icon, contentDescription = null, tint = VColors.sky, modifier = Modifier.size(16.dp))
        Text(value, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
        Text(label, style = VTypography.label.copy(color = VColors.ink3), maxLines = 1)
    }
}

private fun maskStudentPhone(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return if (digits.length > 4) "+${digits.take(digits.length - 4)}XXXX${digits.takeLast(2)}" else phone
}

/** RA-45: add-student form. Class + roll + name required; section defaults A. */
@Composable
private fun AddStudentSheet(
    isSubmitting: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (name: String, className: String, section: String, rollNumber: String, parentPhone: String, admissionDate: String) -> Unit,
    availableClassNames: List<String> = emptyList(),
) {
    var name by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var roll by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var admissionDate by remember { mutableStateOf("") }
    var classDropdownExpanded by remember { mutableStateOf(false) }
    val phoneDigits = parentPhone.filter { it.isDigit() }
    val phoneOk = parentPhone.isBlank() || phoneDigits.length >= 10
    val classValid = className.isNotBlank() && (availableClassNames.isEmpty() || availableClassNames.any { it.equals(className, ignoreCase = true) })

    val canSubmit = name.isNotBlank() && classValid &&
        roll.isNotBlank() && phoneOk && !isSubmitting

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = appString(StringKeys.SCH_ADD_STUDENT))
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VInput(name, { name = it }, label = appString(StringKeys.SCH_FULL_NAME), placeholder = appString(StringKeys.SCH_FULL_NAME_PH), leadingIcon = VIcons.User)
            Box {
                VInput(
                    className,
                    { className = it },
                    label = appString(StringKeys.SCH_CLASS),
                    placeholder = appString(StringKeys.SCH_CLASS_PH),
                    modifier = Modifier.fillMaxWidth().clickable { classDropdownExpanded = true },
                )
                DropdownMenu(
                    expanded = classDropdownExpanded,
                    onDismissRequest = { classDropdownExpanded = false },
                    modifier = Modifier.background(VColors.cream, RoundedCornerShape(14.dp)),
                ) {
                    availableClassNames.forEach { cn ->
                        DropdownMenuItem(
                            text = { Text(cn) },
                            onClick = { className = cn; classDropdownExpanded = false },
                        )
                    }
                }
            }
            if (className.isNotBlank() && !classValid) {
                Text("Please select a configured class", style = VTypography.caption, color = VColors.coral)
            }
            VInput(section, { section = it }, label = appString(StringKeys.SCH_SECTION), placeholder = "A")
            VInput(roll, { roll = it }, label = appString(StringKeys.SCH_ROLL_NUMBER), placeholder = appString(StringKeys.SCH_ROLL_NUMBER_PH), keyboardType = KeyboardType.Number)
            VInput(
                admissionDate,
                { admissionDate = it },
                label = "Admission Date",
                placeholder = "YYYY-MM-DD (optional)",
            )
            VInput(
                parentPhone,
                { input -> parentPhone = input.filter { it.isDigit() || it == '+' || it == ' ' || it == '-' } },
                label = appString(StringKeys.SCH_PARENT_PHONE_OPTIONAL),
                placeholder = appString(StringKeys.SCH_PARENT_PHONE_PH),
                keyboardType = KeyboardType.Phone,
            )
            if (parentPhone.isNotBlank() && !phoneOk) {
                Text(appString(StringKeys.SCH_PHONE_MIN_DIGITS), style = VTypography.label.copy(color = VColors.error))
            }
            if (error != null) {
                Text(error, style = VTypography.body.copy(color = VColors.error))
            }
            Spacer(Modifier.height(2.dp))
            VButton(
                text = appString(StringKeys.SCH_ADD_STUDENT),
                onClick = { onSubmit(name, className, section, roll, parentPhone, admissionDate) },
                variant = VButtonVariant.Primary,
                full = true,
                enabled = canSubmit,
                loading = isSubmitting,
            )
            VButton(
                text = appString(StringKeys.COMMON_BUTTON_CLOSE),
                onClick = onDismiss,
                variant = VButtonVariant.Ghost,
                full = true,
                enabled = !isSubmitting,
            )
        }
    }
}
