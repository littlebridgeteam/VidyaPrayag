package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherCardDto
import com.littlebridge.enrollplus.feature.alumni.domain.model.GraduateStudentsRequest
import com.littlebridge.enrollplus.feature.alumni.domain.repository.AlumniRepository
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.presentation.RiskStudent
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolTeachersState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolTeachersViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StaffRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StaffViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentAnalyticsState
import com.littlebridge.enrollplus.feature.admin.presentation.StudentAnalyticsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterViewModel
import com.littlebridge.enrollplus.ui.v2.components.VActionCard
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VStatusDot
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.AppStrings
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

private enum class PeopleSubTab {
    Teachers, Students, Staff, Alumni;

    @Composable
    fun label(): String = when (this) {
        Teachers -> appString(StringKeys.PPL_TAB_TEACHERS)
        Students -> appString(StringKeys.PPL_TAB_STUDENTS)
        Staff    -> appString(StringKeys.PPL_TAB_STAFF)
        Alumni   -> appString(StringKeys.PPL_TAB_ALUMNI)
    }
}

/**
 * SchoolPeopleScreenV2 — RA-S17 rebuild.
 *
 * The People tab is now a [VTopTabs]-driven 3-sub-tab surface — **Teachers /
 * Students / Non-teaching staff** — each with a search field and tappable,
 * DB-backed rows that open the person's profile. Deletion has been removed from
 * the rows entirely: it now lives inside each profile behind a confirm dialog
 * (RA-S17 directive). The parent→child link-request queue stays as a top entry.
 *
 * Data: teachers via [SchoolTeachersViewModel] (`/school/teachers`), students via
 * [StudentRosterViewModel] (`/school/students?q=&class=`), staff via [StaffViewModel]
 * (`/school/staff?q=&department=`), cohort analytics via [StudentAnalyticsViewModel]
 * (`/student-cohort`). All three states come from [VStateHost] (LAW 2/3/6); no MockV2.
 */
@Composable
fun SchoolPeopleScreenV2(
    modifier: Modifier = Modifier,
    onOpenLinkRequests: () -> Unit = {},
    onOpenStudent: (String) -> Unit = {},
    onOpenTeacher: (String) -> Unit = {},
    // RA-TAM — overflow "Assign classes" opens the reusable assignment module.
    onAssignClasses: (String) -> Unit = {},
    onOpenStaff: (String) -> Unit = {},
    onOpenAlumni: () -> Unit = {},
    onGraduateStudents: (List<String>, Int) -> Unit = { _, _ -> },
    viewModel: StudentAnalyticsViewModel = koinViewModel(),
    teachersViewModel: SchoolTeachersViewModel = koinViewModel(),
    studentsViewModel: StudentRosterViewModel = koinViewModel(),
    staffViewModel: StaffViewModel = koinViewModel(),
    teacherRefreshKey: Int,
    studentRefreshKey: Int,
) {
    val analyticsState by viewModel.state.collectAsStateV2()
    val teachersState by teachersViewModel.state.collectAsStateV2()
    val studentsState by studentsViewModel.state.collectAsStateV2()
    val staffState by staffViewModel.state.collectAsStateV2()

    LaunchedEffect(teacherRefreshKey){
        teachersViewModel.load()
    }
    LaunchedEffect(studentRefreshKey){
        studentsViewModel.load()
    }
    SchoolPeopleContent(
        analyticsState = analyticsState,
        onAnalyticsRetry = viewModel::load,
        teachersState = teachersState,
        onTeachersRetry = teachersViewModel::load,
        onAddTeacher = teachersViewModel::addTeacher,
        onLoadMoreTeachers = teachersViewModel::loadMore,
        onDeactivateTeacher = teachersViewModel::removeTeacher,
        studentsState = studentsState,
        onStudentsRetry = studentsViewModel::load,
        onStudentSearch = { studentsViewModel.load() }, // students VM reloads full list; client-side filter below
        onAddStudent = studentsViewModel::addStudent,
        onImportStudentsCsv = studentsViewModel::importStudentsCsv,
        onClearStudentMessages = studentsViewModel::clearMessages,
        staffState = staffState,
        onStaffRetry = staffViewModel::load,
        onStaffSearch = staffViewModel::onQueryChange,
        onAddStaff = staffViewModel::addStaff,
        onOpenLinkRequests = onOpenLinkRequests,
        onOpenStudent = onOpenStudent,
        onOpenTeacher = onOpenTeacher,
        onAssignClasses = onAssignClasses,
        onOpenStaff = onOpenStaff,
        onOpenAlumni = onOpenAlumni,
        onGraduateStudents = onGraduateStudents,
        modifier = modifier,
    )
}

@Composable
private fun SchoolPeopleContent(
    analyticsState: StudentAnalyticsState,
    onAnalyticsRetry: () -> Unit,
    teachersState: SchoolTeachersState,
    onTeachersRetry: () -> Unit,
    onAddTeacher: (name: String, identifier: String, initialPassword: String?, onAdded: (() -> Unit)?) -> Unit,
    onLoadMoreTeachers: () -> Unit,
    onDeactivateTeacher: (String) -> Unit,
    studentsState: StudentRosterState,
    onStudentsRetry: () -> Unit,
    onStudentSearch: (String) -> Unit,
    onAddStudent: (name: String, className: String, section: String, rollNumber: String, parentPhone: String) -> Unit,
    onImportStudentsCsv: (String) -> Unit,
    onClearStudentMessages: () -> Unit,
    staffState: StaffRosterState,
    onStaffRetry: () -> Unit,
    onStaffSearch: (String) -> Unit,
    onAddStaff: (name: String, role: String, department: String, phone: String, email: String) -> Unit,
    onOpenLinkRequests: () -> Unit,
    onOpenStudent: (String) -> Unit,
    onOpenTeacher: (String) -> Unit,
    onAssignClasses: (String) -> Unit,
    onOpenStaff: (String) -> Unit,
    onOpenAlumni: () -> Unit,
    onGraduateStudents: (List<String>, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var subTab by remember { mutableStateOf(PeopleSubTab.Teachers) }
    var showAddTeacher by remember { mutableStateOf(false) }
    var showAddStaff by remember { mutableStateOf(false) }
    var showAddStudent by remember { mutableStateOf(false) }
    var showImportStudents by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val anyLoading = teachersState.isLoading || studentsState.isLoading || staffState.isLoading || analyticsState.isLoading
    LaunchedEffect(anyLoading) {
        if (!anyLoading) isRefreshing = false
    }

    // Stagger entrance
    val headerAlpha = remember { Animatable(0f) }
    val headerOffset = remember { Animatable(20f) }
    LaunchedEffect(Unit) {
        headerAlpha.snapTo(0f); headerOffset.snapTo(20f)
        launch {
            delay(100)
            headerAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
            headerOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
        }
    }

    // Auto-close the student dialogs once the VM confirms success.
    LaunchedEffect(studentsState.infoMessage) {
        val msg = studentsState.infoMessage
        if (msg != null && (msg == "Student added" || msg.startsWith("Imported"))) {
            showAddStudent = false
            showImportStudents = false
            onClearStudentMessages()
        }
    }

    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            when (subTab) {
                PeopleSubTab.Teachers -> onTeachersRetry()
                PeopleSubTab.Students -> { onStudentsRetry(); onAnalyticsRetry() }
                PeopleSubTab.Staff -> onStaffRetry()
                PeopleSubTab.Alumni -> { isRefreshing = false }
            }
        },
    ) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Premium header
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .graphicsLayer(translationY = headerOffset.value)
                .alpha(headerAlpha.value),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(VColors.violet))
                Text(appString(StringKeys.PPL_TITLE), style = VTypography.accentLabel, color = VColors.violet)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VColors.ink)) {
                        append("People")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                        append(" Directory")
                    }
                },
                style = VTypography.h2,
            )
        }

        VActionCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = appString(StringKeys.PPL_LINK_REQUESTS_TITLE),
            subtitle = appString(StringKeys.PPL_LINK_REQUESTS_SUB),
            icon = VIcons.Plus,
            onClick = onOpenLinkRequests,
        )

        // ── RA-S17: sub-tabs ─────────────────────────────────────────────────
        val subTabLabels = PeopleSubTab.entries.map { it.label() }
        VTopTabs(
            tabs = subTabLabels,
            selected = subTabLabels[subTab.ordinal],
            onSelect = { label -> subTab = PeopleSubTab.entries[subTabLabels.indexOf(label)] },
        )

        Column(
            Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (subTab) {
                PeopleSubTab.Teachers -> TeachersSubTab(
                    state = teachersState,
                    onRetry = onTeachersRetry,
                    onAddClick = { showAddTeacher = true },
                    onOpenTeacher = onOpenTeacher,
                    onLoadMore = onLoadMoreTeachers,
                    onDeactivate = onDeactivateTeacher,
                    onAssignClass = onAssignClasses,
                )
                PeopleSubTab.Students -> StudentsSubTab(
                    state = studentsState,
                    onRetry = onStudentsRetry,
                    onOpenStudent = onOpenStudent,
                    onAddClick = { showAddStudent = true },
                    onImportClick = { showImportStudents = true },
                    onGraduateClick = { studentIds, year -> onGraduateStudents(studentIds, year) },
                    analyticsState = analyticsState,
                    onAnalyticsRetry = onAnalyticsRetry,
                )
                PeopleSubTab.Staff -> StaffSubTab(
                    state = staffState,
                    onRetry = onStaffRetry,
                    onSearch = onStaffSearch,
                    onAddClick = { showAddStaff = true },
                    onOpenStaff = onOpenStaff,
                )
                PeopleSubTab.Alumni -> {
                    VActionCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = appString(StringKeys.PPL_ALUMNI_MGMT_TITLE),
                        subtitle = appString(StringKeys.PPL_ALUMNI_MGMT_SUB),
                        icon = VIcons.UsersGroup,
                        onClick = onOpenAlumni,
                    )
                }
            }
        }
    }
    }

    // ── Add-teacher dialog (RA-22) ─────────────────────────────────────────
    if (showAddTeacher) {
        AddTeacherDialog(
            isSubmitting = teachersState.isMutating,
            onDismiss = { showAddTeacher = false },
            onSubmit = { name, identifier, password ->
                onAddTeacher(name, identifier, password) { showAddTeacher = false }
            },
        )
    }

    // ── Add-staff dialog (RA-S17) ──────────────────────────────────────────
    if (showAddStaff) {
        AddStaffDialog(
            isSubmitting = staffState.isSaving,
            onDismiss = { showAddStaff = false },
            onSubmit = { name, role, dept, phone, email ->
                onAddStaff(name, role, dept, phone, email)
                showAddStaff = false
            },
        )
    }

    // ── Add-student dialog (manual single add) ─────────────────────────────
    if (showAddStudent) {
        AddStudentPeopleDialog(
            isSubmitting = studentsState.isSaving,
            error = studentsState.addError,
            onDismiss = { showAddStudent = false; onClearStudentMessages() },
            onSubmit = { name, cls, sec, roll, phone -> onAddStudent(name, cls, sec, roll, phone) },
        )
    }

    // ── Import-students dialog (CSV / paste) ───────────────────────────────
    if (showImportStudents) {
        ImportStudentsDialog(
            isSubmitting = studentsState.isImporting,
            error = studentsState.importError,
            onDismiss = { showImportStudents = false; onClearStudentMessages() },
            onSubmit = { csv -> onImportStudentsCsv(csv) },
        )
    }
}

// ───────────────────────── Teachers sub-tab ─────────────────────────

@Composable
private fun TeachersSubTab(
    state: SchoolTeachersState,
    onRetry: () -> Unit,
    onAddClick: () -> Unit,
    onOpenTeacher: (String) -> Unit,
    onLoadMore: () -> Unit,
    onDeactivate: (String) -> Unit,
    onAssignClass: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(appString(StringKeys.PPL_TAB_TEACHERS), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
        VButton(
            text = appString(StringKeys.PPL_ADD_TEACHER),
            onClick = onAddClick,
            variant = VButtonVariant.Secondary,
            size = VButtonSize.Sm,
            leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(14.dp)) },
            enabled = !state.isMutating,
        )
    }
    VInput(
        value = query,
        onValueChange = { query = it },
        label = "",
        placeholder = appString(StringKeys.PPL_SEARCH_TEACHERS),
        leadingIcon = VIcons.Search,
        modifier = Modifier.fillMaxWidth(),
    )

    // Card DTOs have no phone/contact field — search the human-facing summary
    // instead: name, role label, assigned subjects and grades.
    val filtered = state.teachers.filter { t ->
        query.isBlank() ||
            t.profile.name.contains(query, ignoreCase = true) ||
            t.profile.role.contains(query, ignoreCase = true) ||
            t.academicAssignment.subjects.any { it.contains(query, ignoreCase = true) } ||
            t.academicAssignment.grades.any { it.contains(query, ignoreCase = true) }
    }

    VStateHost(
        loading = state.isLoading,
        error = state.errorMessage,
        isEmpty = filtered.isEmpty(),
        emptyTitle = if (state.teachers.isEmpty()) appString(StringKeys.PPL_NO_TEACHERS) else appString(StringKeys.PPL_NO_MATCHES),
        emptyBody = if (state.teachers.isEmpty())
            appString(StringKeys.PPL_NO_TEACHERS_BODY)
        else appString(StringKeys.PPL_NO_TEACHER_MATCHES, "query" to query),
        emptyIcon = VIcons.Users,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 5) },
    ) {
        val ready = filtered.isNotEmpty() && !state.isLoading
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            filtered.forEachIndexed { index, t ->
                TeacherCard(
                        teacher = t,
                        isMutating = state.isMutating,
                        onViewProfile = { onOpenTeacher(t.id) },
                        onDeactivate = { onDeactivate(t.id) },
                        onAssignClass = { onAssignClass(t.id) },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
                    )
            }

            // Pagination: only meaningful when NOT filtering locally (a local
            // search filters just the loaded page; loading more would surprise).
            if (query.isBlank() && state.hasNext) {
                VButton(
                    text = if (state.isLoadingMore) appString(StringKeys.PPL_LOADING) else appString(StringKeys.PPL_LOAD_MORE),
                    onClick = onLoadMore,
                    variant = VButtonVariant.Ghost,
                    size = VButtonSize.Sm,
                    full = true,
                    enabled = !state.isLoadingMore && !state.isLoading,
                    loading = state.isLoadingMore,
                )
            }
        }
    }
}

/**
 * Teacher summary CARD — the redesigned School-Admin teacher list row. Every
 * card is self-contained: header (avatar + name + role + status), academic
 * assignment (grades / subjects), workload (classes / students), activity
 * (attendance % + last active), and a footer with View Profile plus an overflow
 * menu. Every action is driven by the backend `actions` flags — nothing is
 * hardcoded — and every data section degrades gracefully when empty.
 */
@Composable
private fun TeacherCard(
    teacher: TeacherCardDto,
    isMutating: Boolean,
    onViewProfile: () -> Unit,
    onDeactivate: () -> Unit,
    onAssignClass: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isActive = teacher.profile.status.equals("ACTIVE", ignoreCase = true)
    var menuOpen by remember { mutableStateOf(false) }
    val hasOverflow = teacher.actions.canAssignClass || teacher.actions.canDeactivate

    VCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // ── Header: avatar · name · role · status ──────────────────────
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VAvatar(
                    name = teacher.profile.name,
                    src = teacher.profile.avatarUrl?.takeIf { it.isNotBlank() },
                    size = 46.dp,
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        teacher.profile.name.ifBlank { appString(StringKeys.PPL_UNNAMED_TEACHER) },
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    if (teacher.profile.role.isNotBlank()) {
                        Text(teacher.profile.role, style = VTypography.caption, color = VColors.ink3)
                    }
                }
                VBadge(
                    text = if (isActive) appString(StringKeys.PPL_ACTIVE) else appString(StringKeys.PPL_INACTIVE),
                    tone = if (isActive) VBadgeTone.Success else VBadgeTone.Neutral,
                )
            }

            CardDivider()

            // ── Academic assignment: grades + subjects ─────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LabeledChipsRow(
                    label = appString(StringKeys.PPL_GRADES),
                    values = teacher.academicAssignment.grades,
                    emptyText = appString(StringKeys.PPL_NO_GRADES),
                )
                LabeledChipsRow(
                    label = appString(StringKeys.PPL_SUBJECTS),
                    values = teacher.academicAssignment.subjects,
                    emptyText = appString(StringKeys.PPL_NO_SUBJECTS),
                )
            }

            CardDivider()

            // ── Workload: classes + students (side by side) ────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WorkloadStat(
                    label = appString(StringKeys.PPL_CLASSES),
                    value = teacher.workload.totalClasses.toString(),
                    icon = VIcons.BookOpen,
                    modifier = Modifier.weight(1f),
                )
                WorkloadStat(
                    label = appString(StringKeys.PPL_STUDENTS_LABEL),
                    value = teacher.workload.totalStudents.toString(),
                    icon = VIcons.Users,
                    modifier = Modifier.weight(1f),
                )
            }

            CardDivider()

            // ── Activity: attendance % + last active ───────────────────────
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(VIcons.TrendingUp, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
                    Text(
                        teacher.activity.attendancePercentage
                            ?.let { appString(StringKeys.PPL_ATTENDANCE_PCT, "pct" to it) }
                            ?: appString(StringKeys.PPL_ATTENDANCE_NONE),
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(VIcons.Clock, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
                    Text(
                        lastActiveLabel(teacher.activity.lastActiveAt),
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                }
            }

            // ── Actions: View Profile + overflow (backend-driven) ──────────
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (teacher.actions.canViewProfile) {
                    VButton(
                        text = appString(StringKeys.PPL_VIEW_PROFILE),
                        onClick = onViewProfile,
                        variant = VButtonVariant.Secondary,
                        size = VButtonSize.Sm,
                        modifier = Modifier.weight(1f),
                        leading = { Icon(VIcons.Eye, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }

                if (hasOverflow) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VColors.cream)
                                .clickable(enabled = !isMutating) { menuOpen = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(VIcons.More, contentDescription = appString(StringKeys.PPL_MORE_ACTIONS), tint = VColors.ink2, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            if (teacher.actions.canAssignClass) {
                                DropdownMenuItem(
                                    text = { Text(appString(StringKeys.PPL_ASSIGN_CLASSES)) },
                                    onClick = { menuOpen = false; onAssignClass() },
                                    leadingIcon = { Icon(VIcons.GraduationCap, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                )
                            }
                            if (teacher.actions.canDeactivate) {
                                DropdownMenuItem(
                                    text = { Text(appString(StringKeys.PPL_DEACTIVATE), style = VTypography.bodySmall, color = VColors.coral) },
                                    onClick = { menuOpen = false; onDeactivate() },
                                    leadingIcon = { Icon(VIcons.Close, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(16.dp)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Thin hairline divider used between a teacher card's sections. */
@Composable
private fun CardDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
}

/**
 * A labelled wrapped row of value "chips" (e.g. Grades / Subjects). Renders
 * [emptyText] in a muted tone when [values] is empty so an unassigned teacher
 * still reads cleanly.
 */
@Composable
private fun LabeledChipsRow(
    label: String,
    values: List<String>,
    emptyText: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = VTypography.caption, color = VColors.ink3)
        if (values.isEmpty()) {
            Text(emptyText, style = VTypography.caption, color = VColors.ink3)
        } else {
            Text(
                values.joinToString("  •  "),
                style = VTypography.bodySmall,
                color = VColors.ink,
            )
        }
    }
}

@Composable
private fun WorkloadStat(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(VColors.cream)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
            Text(label, style = VTypography.caption, color = VColors.ink3)
        }
        Text(value, style = VTypography.h3.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
    }
}

/**
 * Humanise the ISO-8601 lastActiveAt into a short label. We intentionally keep
 * this dependency-free (no kotlinx-datetime parsing here): a null/blank value
 * → "Never active"; otherwise we surface the calendar date portion, falling
 * back to the raw value if it is not in the expected shape.
 */
private fun lastActiveLabel(iso: String?): String {
    if (iso.isNullOrBlank()) return AppStrings.get(StringKeys.PPL_NEVER_ACTIVE, "en")
    // ISO-8601 UTC like "2026-06-16T09:30:00Z" → "Active 2026-06-16".
    val datePart = iso.substringBefore('T').takeIf { it.length == 10 && it.count { ch -> ch == '-' } == 2 }
    return datePart?.let { AppStrings.get(StringKeys.PPL_ACTIVE_DATE, "en").replace("{date}", it) } ?: AppStrings.get(StringKeys.PPL_ACTIVE_DATE, "en").replace("{date}", "")
}

// ───────────────────────── Students sub-tab ─────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudentsSubTab(
    state: StudentRosterState,
    onRetry: () -> Unit,
    onOpenStudent: (String) -> Unit,
    onAddClick: () -> Unit,
    onImportClick: () -> Unit,
    onGraduateClick: (List<String>, Int) -> Unit,
    analyticsState: StudentAnalyticsState,
    onAnalyticsRetry: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var showGraduate by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(appString(StringKeys.PPL_TAB_STUDENTS), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VButton(
                text = appString(StringKeys.PPL_ADD_STUDENT),
                onClick = onAddClick,
                variant = VButtonVariant.Secondary,
                size = VButtonSize.Sm,
                leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(14.dp)) },
                enabled = !state.isSaving && !state.isImporting,
            )
            VButton(
                text = appString(StringKeys.PPL_IMPORT_CSV),
                onClick = onImportClick,
                variant = VButtonVariant.Ghost,
                size = VButtonSize.Sm,
                leading = { Icon(VIcons.Upload, contentDescription = null, modifier = Modifier.size(14.dp)) },
                enabled = !state.isImporting && !state.isSaving,
            )
            VButton(
                text = appString(StringKeys.PPL_GRADUATE),
                onClick = { showGraduate = true },
                variant = VButtonVariant.Ghost,
                size = VButtonSize.Sm,
                leading = { Icon(VIcons.Users, contentDescription = null, modifier = Modifier.size(14.dp)) },
                enabled = !state.isLoading && state.students.isNotEmpty(),
            )
        }
    }
    VInput(
        value = query,
        onValueChange = { query = it },
        label = "",
        placeholder = appString(StringKeys.PPL_SEARCH_STUDENTS),
        leadingIcon = VIcons.Search,
        modifier = Modifier.fillMaxWidth(),
    )

    val filtered = state.students.filter {
        query.isBlank() ||
            it.fullName.contains(query, ignoreCase = true) ||
            it.rollNumber.contains(query, ignoreCase = true) ||
            it.studentCode.contains(query, ignoreCase = true) ||
            it.className.contains(query, ignoreCase = true)
    }

    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = filtered.isEmpty(),
        emptyTitle = if (state.students.isEmpty()) appString(StringKeys.PPL_NO_STUDENTS) else appString(StringKeys.PPL_NO_MATCHES),
        emptyBody = if (state.students.isEmpty())
            appString(StringKeys.PPL_NO_STUDENTS_BODY)
        else appString(StringKeys.PPL_NO_STUDENT_MATCHES, "query" to query),
        emptyIcon = VIcons.Users,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 6) },
    ) {
        val ready = filtered.isNotEmpty() && !state.isLoading
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filtered.forEachIndexed { index, s ->
                PersonRow(
                        name = s.fullName,
                        subtitle = "${s.className} · Sec ${s.section} · Roll ${s.rollNumber}",
                        src = s.profilePhotoUrl,
                        onClick = { onOpenStudent(s.id) },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
                    )
            }
        }
    }

    // ── Cohort analytics (kept under Students) ──────────────────────────────
    Spacer(Modifier.height(8.dp))
    Text(appString(StringKeys.PPL_COHORT_ANALYTICS), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
    VStateHost(
        loading = analyticsState.isLoading,
        error = analyticsState.errorMessage,
        isEmpty = analyticsState.atRiskStudents.isEmpty() &&
            analyticsState.subjectEngagements.isEmpty() &&
            analyticsState.criticalRiskCount == 0 &&
            analyticsState.mediumRiskCount == 0 &&
            analyticsState.lowRiskCount == 0,
        emptyTitle = appString(StringKeys.PPL_NO_COHORT_DATA),
        emptyBody = appString(StringKeys.PPL_NO_COHORT_BODY),
        emptyIcon = VIcons.Users,
        onRetry = onAnalyticsRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 4) },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            VCard {
                Text(appString(StringKeys.PPL_RISK_DISTRIBUTION), style = VTypography.caption, color = VColors.ink3)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RiskTile(appString(StringKeys.PPL_CRITICAL), analyticsState.criticalRiskCount, VColors.coral, Modifier.weight(1f))
                    RiskTile(appString(StringKeys.PPL_MEDIUM), analyticsState.mediumRiskCount, VColors.gold, Modifier.weight(1f))
                    RiskTile(appString(StringKeys.PPL_LOW), analyticsState.lowRiskCount, VColors.success, Modifier.weight(1f))
                }
            }
            if (analyticsState.atRiskStudents.isNotEmpty()) {
                Column {
                    Text(appString(StringKeys.PPL_AT_RISK_STUDENTS), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.padding(bottom = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        analyticsState.atRiskStudents.forEach { RiskStudentRow(it) }
                    }
                }
            }
            if (analyticsState.subjectEngagements.isNotEmpty()) {
                VCard {
                    Text(appString(StringKeys.PPL_SUBJECT_ENGAGEMENT), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        analyticsState.subjectEngagements.forEach { e ->
                            Column {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(e.name, style = VTypography.bodySmall, color = VColors.ink)
                                    Text("${e.percentage.roundToInt()}%", style = VTypography.caption, color = VColors.ink2)
                                }
                                Spacer(Modifier.height(4.dp))
                                VProgressBar(
                                    value = e.percentage,
                                    tone = if (e.percentage < 60f) VBadgeTone.Warning else VBadgeTone.Arctic,
                                )
                                val status = e.status
                                if (!status.isNullOrBlank()) {
                                    Text(status, style = VTypography.caption, color = VColors.ink3)
                                }
                            }
                        }
                    }
                }
            }
            if (analyticsState.cohortComparison.isNotEmpty()) {
                VCard {
                    Text(appString(StringKeys.PPL_COHORT_COMPARISON), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        analyticsState.cohortComparison.forEachIndexed { i, v ->
                            val label = analyticsState.cohortLabels.getOrNull(i) ?: appString(StringKeys.PPL_GRADE_N, "n" to (i + 1))
                            Column {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(label, style = VTypography.bodySmall, color = VColors.ink)
                                    Text("${v.roundToInt()}%", style = VTypography.caption, color = VColors.ink2)
                                }
                                Spacer(Modifier.height(4.dp))
                                VProgressBar(value = v, tone = VBadgeTone.Arctic)
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Graduation dialog ─────────────────────────────────────────────────
    if (showGraduate) {
        var gradYear by remember { mutableStateOf("") }
        val currentYear = 2026
        Dialog(onDismissRequest = { showGraduate = false }) {
            VCard {
                Text(appString(StringKeys.PPL_MARK_ALUMNI), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                Spacer(Modifier.height(8.dp))
                Text(
                    appString(StringKeys.PPL_MARK_ALUMNI_BODY, "count" to filtered.size),
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
                Spacer(Modifier.height(16.dp))
                VInput(
                    value = gradYear,
                    onValueChange = { gradYear = it.filter { ch -> ch.isDigit() }.take(4) },
                    label = appString(StringKeys.PPL_GRADUATION_YEAR),
                    placeholder = currentYear.toString(),
                    keyboardType = KeyboardType.Number,
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                        onClick = { showGraduate = false },
                        variant = VButtonVariant.Ghost,
                        size = VButtonSize.Sm,
                        modifier = Modifier.weight(1f),
                    )
                    VButton(
                        text = appString(StringKeys.PPL_GRADUATE),
                        onClick = {
                            val year = gradYear.toIntOrNull() ?: currentYear
                            onGraduateClick(filtered.map { it.id }, year)
                            showGraduate = false
                        },
                        variant = VButtonVariant.Primary,
                        size = VButtonSize.Sm,
                        modifier = Modifier.weight(1f),
                        enabled = filtered.isNotEmpty(),
                    )
                }
            }
        }
    }
}

// ─────────────────────── Non-teaching-staff sub-tab ───────────────────────

@Composable
private fun StaffSubTab(
    state: StaffRosterState,
    onRetry: () -> Unit,
    onSearch: (String) -> Unit,
    onAddClick: () -> Unit,
    onOpenStaff: (String) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(appString(StringKeys.PPL_TAB_STAFF), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
        VButton(
            text = appString(StringKeys.PPL_ADD_STAFF),
            onClick = onAddClick,
            variant = VButtonVariant.Secondary,
            size = VButtonSize.Sm,
            leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(14.dp)) },
            enabled = !state.isSaving,
        )
    }
    VInput(
        value = state.query,
        onValueChange = onSearch,
        label = "",
        placeholder = appString(StringKeys.PPL_SEARCH_STAFF),
        leadingIcon = VIcons.Search,
        modifier = Modifier.fillMaxWidth(),
    )

    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.staff.isEmpty(),
        emptyTitle = if (state.query.isBlank()) appString(StringKeys.PPL_NO_STAFF) else appString(StringKeys.PPL_NO_MATCHES),
        emptyBody = if (state.query.isBlank())
            appString(StringKeys.PPL_NO_STAFF_BODY)
        else appString(StringKeys.PPL_NO_STAFF_MATCHES, "query" to state.query),
        emptyIcon = VIcons.Users,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 5) },
    ) {
        val ready = state.staff.isNotEmpty() && !state.isLoading
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.staff.forEachIndexed { index, s ->
                PersonRow(
                        name = s.fullName,
                        subtitle = listOfNotNull(s.role, s.department?.takeIf { it.isNotBlank() }).joinToString(" · "),
                        src = s.photoUrl,
                        onClick = { onOpenStaff(s.id) },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
                    )
            }
        }
    }
}

// ───────────────────────────── shared row ─────────────────────────────

/**
 * RA-S17: a tap-to-open person row. There is intentionally **no** delete button
 * here — deletion lives inside the profile behind a confirm dialog.
 */
@Composable
private fun PersonRow(
    name: String,
    subtitle: String,
    src: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VAvatar(name = name, src = src, size = 42.dp)
            Column(Modifier.weight(1f)) {
                Text(name, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, style = VTypography.caption, color = VColors.ink3)
                }
            }
            Icon(VIcons.ArrowRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        }
    }
}

// ───────────────────────────── dialogs ─────────────────────────────

/**
 * RA-22: add-teacher form. A teacher is provisioned by email (with an initial
 * password) or by phone (OTP login). Frozen primitives only.
 */
@Composable
private fun AddTeacherDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (name: String, identifier: String, initialPassword: String?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isEmail = identifier.contains("@")
    val canSubmit = name.isNotBlank() &&
        identifier.isNotBlank() &&
        (!isEmail || password.isNotBlank()) &&
        !isSubmitting

    Dialog(onDismissRequest = onDismiss) {
        VCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(appString(StringKeys.PPL_ADD_TEACHER), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                VInput(
                    value = name,
                    onValueChange = { name = it },
                    label = appString(StringKeys.PPL_FULL_NAME),
                    placeholder = appString(StringKeys.PPL_NAME_PH_TEACHER),
                    leadingIcon = VIcons.User,
                )
                VInput(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = appString(StringKeys.PPL_EMAIL_OR_PHONE),
                    placeholder = appString(StringKeys.PPL_EMAIL_PHONE_PH),
                    leadingIcon = if (isEmail) VIcons.Mail else VIcons.Phone,
                    keyboardType = if (isEmail) KeyboardType.Email else KeyboardType.Text,
                )
                if (isEmail) {
                    VInput(
                        value = password,
                        onValueChange = { password = it },
                        label = appString(StringKeys.PPL_INITIAL_PASSWORD),
                        placeholder = appString(StringKeys.PPL_PASSWORD_PH),
                        leadingIcon = VIcons.Lock,
                        isPassword = true,
                    )
                } else {
                    Text(
                        appString(StringKeys.PPL_OTP_HINT),
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                }
                Spacer(Modifier.height(4.dp))
                VButton(
                    text = appString(StringKeys.PPL_ADD_TEACHER),
                    onClick = {
                        onSubmit(name, identifier, password.takeIf { isEmail && it.isNotBlank() })
                    },
                    variant = VButtonVariant.Primary,
                    full = true,
                    enabled = canSubmit,
                    loading = isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Ghost,
                    full = true,
                    enabled = !isSubmitting,
                )
            }
        }
    }
}

/**
 * RA-S17: add-staff form for a non-teaching-staff member. Name + role required;
 * department / phone / email optional. Frozen primitives only.
 */
@Composable
private fun AddStaffDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (name: String, role: String, department: String, phone: String, email: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val canSubmit = name.isNotBlank() && role.isNotBlank() && !isSubmitting

    Dialog(onDismissRequest = onDismiss) {
        VCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(appString(StringKeys.PPL_ADD_STAFF_MEMBER), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                VInput(
                    value = name,
                    onValueChange = { name = it },
                    label = appString(StringKeys.PPL_FULL_NAME),
                    placeholder = appString(StringKeys.PPL_NAME_PH_STAFF),
                    leadingIcon = VIcons.User,
                )
                VInput(
                    value = role,
                    onValueChange = { role = it },
                    label = appString(StringKeys.PPL_ROLE),
                    placeholder = appString(StringKeys.PPL_ROLE_PH),
                    leadingIcon = VIcons.User,
                )
                VInput(
                    value = department,
                    onValueChange = { department = it },
                    label = appString(StringKeys.PPL_DEPT_OPTIONAL),
                    placeholder = appString(StringKeys.PPL_DEPT_PH),
                    leadingIcon = VIcons.Bookmark,
                )
                VInput(
                    value = phone,
                    onValueChange = { phone = it },
                    label = appString(StringKeys.PPL_PHONE_OPTIONAL),
                    placeholder = appString(StringKeys.PPL_PHONE_PH),
                    leadingIcon = VIcons.Phone,
                    keyboardType = KeyboardType.Phone,
                )
                VInput(
                    value = email,
                    onValueChange = { email = it },
                    label = appString(StringKeys.PPL_EMAIL_OPTIONAL),
                    placeholder = appString(StringKeys.PPL_EMAIL_PH),
                    leadingIcon = VIcons.Mail,
                    keyboardType = KeyboardType.Email,
                )
                Spacer(Modifier.height(4.dp))
                VButton(
                    text = appString(StringKeys.PPL_ADD_STAFF),
                    onClick = { onSubmit(name, role, department, phone, email) },
                    variant = VButtonVariant.Primary,
                    full = true,
                    enabled = canSubmit,
                    loading = isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Ghost,
                    full = true,
                    enabled = !isSubmitting,
                )
            }
        }
    }
}

// ───────────────────────── Student add / import dialogs ─────────────────────

/** Manual single-student add from the People → Students sub-tab. */
@Composable
private fun AddStudentPeopleDialog(
    isSubmitting: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (name: String, className: String, section: String, rollNumber: String, parentPhone: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var roll by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }

    val phoneDigits = parentPhone.count { it.isDigit() }
    // Parent phone is optional — only validate when the admin has entered something.
    val phoneOk = parentPhone.isBlank() || phoneDigits >= 10
    val canSubmit = name.isNotBlank() && className.isNotBlank() && roll.isNotBlank() &&
        phoneOk && !isSubmitting

    Dialog(onDismissRequest = onDismiss) {
        VCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(appString(StringKeys.PPL_ADD_STUDENT), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                VInput(name, { name = it }, label = appString(StringKeys.PPL_FULL_NAME), placeholder = appString(StringKeys.PPL_NAME_PH_STUDENT), leadingIcon = VIcons.User)
                VInput(className, { className = it }, label = appString(StringKeys.PPL_CLASS), placeholder = appString(StringKeys.PPL_CLASS_PH))
                VInput(section, { section = it }, label = appString(StringKeys.PPL_SECTION), placeholder = appString(StringKeys.PPL_SECTION_PH))
                VInput(roll, { roll = it }, label = appString(StringKeys.PPL_ROLL_NUMBER), placeholder = appString(StringKeys.PPL_ROLL_PH), keyboardType = KeyboardType.Number)
                VInput(
                    parentPhone,
                    { parentPhone = it },
                    label = appString(StringKeys.PPL_PARENT_PHONE),
                    placeholder = appString(StringKeys.PPL_PARENT_PHONE_PH),
                    keyboardType = KeyboardType.Phone,
                )
                if (error != null) {
                    Text(error, style = VTypography.caption, color = VColors.coral)
                }
                Spacer(Modifier.height(2.dp))
                VButton(
                    text = appString(StringKeys.PPL_ADD_STUDENT),
                    onClick = { onSubmit(name, className, section, roll, parentPhone) },
                    variant = VButtonVariant.Primary,
                    full = true,
                    enabled = canSubmit,
                    loading = isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Ghost,
                    full = true,
                    enabled = !isSubmitting,
                )
            }
        }
    }
}

/**
 * Bulk CSV import. The admin pastes (or, on platforms with a file picker,
 * loads) CSV rows. The first line must be a header; accepted columns:
 *   full_name, class_name, roll_number, section, student_code
 * `section` and `student_code` are optional. Sent verbatim to
 * POST /api/v1/school/students/import, which parses + validates each row.
 */
@Composable
private fun ImportStudentsDialog(
    isSubmitting: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (csv: String) -> Unit,
) {
    var csv by remember {
        mutableStateOf("full_name,class_name,section,roll_number\n")
    }
    val canSubmit = csv.lineSequence().drop(1).any { it.isNotBlank() } && !isSubmitting

    Dialog(onDismissRequest = onDismiss) {
        VCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(appString(StringKeys.PPL_IMPORT_STUDENTS_CSV), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                Text(
                    appString(StringKeys.PPL_IMPORT_INSTRUCTIONS),
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
                VInput(
                    value = csv,
                    onValueChange = { csv = it },
                    label = appString(StringKeys.PPL_CSV_CONTENT),
                    placeholder = appString(StringKeys.PPL_CSV_PH),
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                )
                if (error != null) {
                    Text(error, style = VTypography.caption, color = VColors.coral)
                }
                Spacer(Modifier.height(2.dp))
                VButton(
                    text = appString(StringKeys.PPL_IMPORT),
                    onClick = { onSubmit(csv) },
                    variant = VButtonVariant.Primary,
                    full = true,
                    enabled = canSubmit,
                    loading = isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Ghost,
                    full = true,
                    enabled = !isSubmitting,
                )
            }
        }
    }
}

// ───────────────────────────── analytics bits ─────────────────────────────

@Composable
private fun RiskTile(label: String, count: Int, tone: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(VColors.cream).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(count.toString(), style = VTypography.h3.copy(fontWeight = FontWeight.SemiBold), color = tone)
        Text(label, style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
    }
}

@Composable
private fun RiskStudentRow(s: RiskStudent) {
    val tone = when (s.riskLevel.lowercase()) {
        "critical" -> VColors.coral
        "medium" -> VColors.gold
        else -> VColors.success
    }
    val badgeTone = when (s.riskLevel.lowercase()) {
        "critical" -> VBadgeTone.Danger
        "medium" -> VBadgeTone.Warning
        else -> VBadgeTone.Success
    }
    VCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            VAvatar(name = s.name, src = s.imageUrl.ifBlank { null }, size = 42.dp)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VStatusDot(color = tone)
                    Text(s.name, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                }
                if (s.masteryTrend.isNotBlank()) {
                    Text(appString(StringKeys.PPL_MASTERY, "trend" to s.masteryTrend), style = VTypography.caption, color = VColors.ink2)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                VBadge(text = s.riskLevel, tone = badgeTone)
                Text(appString(StringKeys.PPL_RISK_PCT, "risk" to s.retentionRisk), style = VTypography.caption.copy(fontSize = 10.sp), color = VColors.ink3)
            }
        }
    }
}
