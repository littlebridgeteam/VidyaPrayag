package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsState
import com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolTeachersState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolTeachersViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StaffRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StaffViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterViewModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VSnackbar
import com.littlebridge.enrollplus.ui.v2.components.VSnackbarTone
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.platform.rememberPhoneHelper
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private enum class PeopleSubTab {
    Teachers, Students, Staff;

    @Composable
    fun label(): String = when (this) {
        Teachers -> appString(StringKeys.PPL_TAB_TEACHERS)
        Students -> appString(StringKeys.PPL_TAB_STUDENTS)
        Staff    -> appString(StringKeys.PPL_TAB_STAFF)
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
 * (`/school/staff?q=&department=`). All states come from [VStateHost] (LAW 2/3/6); no MockV2.
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
    onGraduateStudents: (List<String>, Int) -> Unit = { _, _ -> },
    teachersViewModel: SchoolTeachersViewModel = koinViewModel(),
    studentsViewModel: StudentRosterViewModel = koinViewModel(),
    staffViewModel: StaffViewModel = koinViewModel(),
    classesViewModel: ClassesSubjectsViewModel = koinViewModel(),
    teacherRefreshKey: Int,
    studentRefreshKey: Int,
) {
    val teachersState by teachersViewModel.state.collectAsStateV2()
    val studentsState by studentsViewModel.state.collectAsStateV2()
    val staffState by staffViewModel.state.collectAsStateV2()
    val classesState by classesViewModel.state.collectAsStateV2()

    LaunchedEffect(teacherRefreshKey){
        teachersViewModel.load()
    }
    LaunchedEffect(studentRefreshKey){
        studentsViewModel.load()
    }
    LaunchedEffect(Unit) {
        classesViewModel.loadClasses()
    }
    SchoolPeopleContent(
        teachersState = teachersState,
        onTeachersRetry = teachersViewModel::load,
        onAddTeacher = teachersViewModel::addTeacher,
        onLoadMoreTeachers = teachersViewModel::loadMore,
        onDeactivateTeacher = teachersViewModel::removeTeacher,
        studentsState = studentsState,
        onStudentsRetry = studentsViewModel::load,
        onStudentSearch = { studentsViewModel.load() }, // students VM reloads full list; client-side filter below
        onAddStudent = { name, cls, sec, roll, phone, admission -> studentsViewModel.addStudent(name, cls, sec, roll, phone, admission) },
        onImportStudentsCsv = studentsViewModel::importStudentsCsv,
        onClearStudentMessages = studentsViewModel::clearMessages,
        availableClassNames = classesState.classes.map { it.name },
        staffState = staffState,
        onStaffRetry = staffViewModel::load,
        onStaffSearch = staffViewModel::onQueryChange,
        onAddStaff = staffViewModel::addStaff,
        onOpenLinkRequests = onOpenLinkRequests,
        onOpenStudent = onOpenStudent,
        onOpenTeacher = onOpenTeacher,
        onAssignClasses = onAssignClasses,
        onOpenStaff = onOpenStaff,
        onGraduateStudents = onGraduateStudents,
        modifier = modifier,
    )
}

@Composable
private fun SchoolPeopleContent(
    teachersState: SchoolTeachersState,
    onTeachersRetry: () -> Unit,
    onAddTeacher: (name: String, identifier: String, initialPassword: String?, onAdded: (() -> Unit)?) -> Unit,
    onLoadMoreTeachers: () -> Unit,
    onDeactivateTeacher: (String) -> Unit,
    studentsState: StudentRosterState,
    onStudentsRetry: () -> Unit,
    onStudentSearch: (String) -> Unit,
    onAddStudent: (name: String, className: String, section: String, rollNumber: String, parentPhone: String, admissionDate: String) -> Unit,
    onImportStudentsCsv: (String) -> Unit,
    onClearStudentMessages: () -> Unit,
    availableClassNames: List<String> = emptyList(),
    staffState: StaffRosterState,
    onStaffRetry: () -> Unit,
    onStaffSearch: (String) -> Unit,
    onAddStaff: (name: String, role: String, department: String, phone: String, email: String) -> Unit,
    onOpenLinkRequests: () -> Unit,
    onOpenStudent: (String) -> Unit,
    onOpenTeacher: (String) -> Unit,
    onAssignClasses: (String) -> Unit,
    onOpenStaff: (String) -> Unit,
    onGraduateStudents: (List<String>, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var subTab by remember { mutableStateOf(PeopleSubTab.Teachers) }
    var showAddTeacher by remember { mutableStateOf(false) }
    var showAddStaff by remember { mutableStateOf(false) }
    var showAddStudent by remember { mutableStateOf(false) }
    var showImportStudents by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val anyLoading = teachersState.isLoading || studentsState.isLoading || staffState.isLoading
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

    val subTabLabels = PeopleSubTab.entries.map { it.label() }
    val pagerState = rememberPagerState(pageCount = { PeopleSubTab.entries.size })
    val scope = rememberCoroutineScope()

    // Sync tab taps with pager.
    LaunchedEffect(subTab) {
        val page = subTab.ordinal
        if (pagerState.currentPage != page) {
            pagerState.animateScrollToPage(page)
        }
    }
    // Sync pager swipes with tabs.
    LaunchedEffect(pagerState.currentPage) {
        subTab = PeopleSubTab.entries[pagerState.currentPage]
    }

    VPullRefresh(
        isRefreshing = teachersState.isLoading || studentsState.isLoading || staffState.isLoading,
        onRefresh = {
            when (PeopleSubTab.entries[pagerState.currentPage]) {
                PeopleSubTab.Teachers -> onTeachersRetry()
                PeopleSubTab.Students -> onStudentsRetry()
                PeopleSubTab.Staff -> onStaffRetry()
            }
        },
    ) {
        Column(
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Premium header
            Column(
                modifier = Modifier
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

            // ── RA-S17: sub-tabs ─────────────────────────────────────────────────
            VTopTabs(
                tabs = subTabLabels,
                selected = subTabLabels[subTab.ordinal],
                onSelect = { label ->
                    subTab = PeopleSubTab.entries[subTabLabels.indexOf(label)]
                },
                activeColor = VColors.violet,
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                beyondViewportPageCount = 1,
            ) { page ->
                when (PeopleSubTab.entries[page]) {
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
                        onOpenLinkRequests = onOpenLinkRequests,
                        onAddClick = { showAddStudent = true },
                        onImportClick = { showImportStudents = true },
                        onGraduateClick = { studentIds, year -> onGraduateStudents(studentIds, year) },
                    )
                    PeopleSubTab.Staff -> StaffSubTab(
                        state = staffState,
                        onRetry = onStaffRetry,
                        onSearch = onStaffSearch,
                        onAddClick = { showAddStaff = true },
                        onOpenStaff = onOpenStaff,
                    )
                }
            }
        }
    }

    // ── Add-teacher dialog (RA-22) ─────────────────────────────────────────
    if (showAddTeacher) {
        AddTeacherSheet(
            isSubmitting = teachersState.isMutating,
            onDismiss = { showAddTeacher = false },
            onSubmit = { name, identifier, password ->
                onAddTeacher(name, identifier, password) { showAddTeacher = false }
            },
        )
    }

    // ── Add-staff dialog (RA-S17) ──────────────────────────────────────────
    if (showAddStaff) {
        AddStaffSheet(
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
        AddStudentPeopleSheet(
            isSubmitting = studentsState.isSaving,
            error = studentsState.addError,
            onDismiss = { showAddStudent = false; onClearStudentMessages() },
            onSubmit = { name, cls, sec, roll, phone, admission -> onAddStudent(name, cls, sec, roll, phone, admission) },
            availableClassNames = availableClassNames,
        )
    }

    // ── Import-students dialog (CSV / paste) ───────────────────────────────
    if (showImportStudents) {
        ImportStudentsSheet(
            isSubmitting = studentsState.isImporting,
            error = studentsState.importError,
            onDismiss = { showImportStudents = false; onClearStudentMessages() },
            onSubmit = { csv -> onImportStudentsCsv(csv) },
        )
    }
}

// ───────────────────────── Teachers sub-tab ─────────────────────────

@OptIn(ExperimentalLayoutApi::class)
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
    var selectedSubjects by remember { mutableStateOf(setOf<String>()) }
    var selectedGrades by remember { mutableStateOf(setOf<String>()) }
    var selectedAvailability by remember { mutableStateOf(setOf<String>()) }

    val subjects = remember(state.teachers) { state.teachers.flatMap { it.academicAssignment.subjects }.distinct().sorted() }
    val grades = remember(state.teachers) { state.teachers.flatMap { it.academicAssignment.grades }.distinct().sorted() }
    val availabilities = remember(state.teachers) { state.teachers.map { it.availability }.distinct().sorted() }

    val allFilters = selectedSubjects + selectedGrades + selectedAvailability

    val filtered = state.teachers.filter { t ->
        val q = query.isBlank() ||
            t.profile.name.contains(query, ignoreCase = true) ||
            t.profile.role.contains(query, ignoreCase = true) ||
            t.academicAssignment.subjects.any { it.contains(query, ignoreCase = true) } ||
            t.academicAssignment.grades.any { it.contains(query, ignoreCase = true) }
        val subjectOk = selectedSubjects.isEmpty() || t.academicAssignment.subjects.any { it in selectedSubjects }
        val gradeOk = selectedGrades.isEmpty() || t.academicAssignment.grades.any { it in selectedGrades }
        val availabilityOk = selectedAvailability.isEmpty() || t.availability in selectedAvailability
        q && subjectOk && gradeOk && availabilityOk
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VInput(
                value = query,
                onValueChange = { query = it },
                label = "",
                placeholder = appString(StringKeys.PPL_SEARCH_TEACHERS),
                leadingIcon = VIcons.Search,
                modifier = Modifier.weight(1f),
            )
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VColors.surfaceCard)
                        .clickable { menuExpanded = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.More, contentDescription = "More", tint = VColors.ink, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(VColors.surfaceCard, RoundedCornerShape(14.dp)),
                ) {
                    DropdownMenuItem(
                        text = { Text(appString(StringKeys.PPL_ADD_TEACHER)) },
                        onClick = { menuExpanded = false; onAddClick() },
                        enabled = !state.isMutating,
                        leadingIcon = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                }
            }
        }
        FilterChipRow(
            chips = listOf(
                FilterChipSpec(
                    label = "Subject",
                    options = subjects,
                    selected = selectedSubjects,
                    onToggle = { selectedSubjects = if (it in selectedSubjects) selectedSubjects - it else selectedSubjects + it },
                ),
                FilterChipSpec(
                    label = "Grade",
                    options = grades,
                    selected = selectedGrades,
                    onToggle = { selectedGrades = if (it in selectedGrades) selectedGrades - it else selectedGrades + it },
                ),
                FilterChipSpec(
                    label = "Availability",
                    options = availabilities,
                    selected = selectedAvailability,
                    onToggle = { selectedAvailability = if (it in selectedAvailability) selectedAvailability - it else selectedAvailability + it },
                ),
            ),
        )
        ActiveFilterChips(
            selected = allFilters,
            onRemove = {
                selectedSubjects = selectedSubjects - it
                selectedGrades = selectedGrades - it
                selectedAvailability = selectedAvailability - it
            },
            onClearAll = {
                selectedSubjects = emptySet()
                selectedGrades = emptySet()
                selectedAvailability = emptySet()
            },
        )
        VStateHost(
            modifier = Modifier.fillMaxWidth().weight(1f),
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
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                filtered.forEachIndexed { index, t ->
                    TeacherCard(
                        teacher = t,
                        onViewProfile = { onOpenTeacher(t.id) },
                        onDeactivate = { onDeactivate(t.id) },
                        onAssignClass = { onAssignClass(t.id) },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
                    )
                }

                // Pagination: only meaningful when NOT filtering locally.
                if (query.isBlank() && allFilters.isEmpty() && state.hasNext) {
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
}


// ───────────────────────── Students sub-tab ─────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudentsSubTab(
    state: StudentRosterState,
    onRetry: () -> Unit,
    onOpenStudent: (String) -> Unit,
    onOpenLinkRequests: () -> Unit,
    onAddClick: () -> Unit,
    onImportClick: () -> Unit,
    onGraduateClick: (List<String>, Int) -> Unit,
) {
    val phoneHelper = rememberPhoneHelper()
    var query by remember { mutableStateOf("") }
    var showGraduate by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf<String?>(null) }
    var selectedClasses by remember { mutableStateOf(setOf<String>()) }
    var selectedSections by remember { mutableStateOf(setOf<String>()) }
    var selectedStatus by remember { mutableStateOf(setOf<String>()) }

    val classes = remember(state.students) { state.students.map { it.className }.distinct().sorted() }
    val sections = remember(state.students) { state.students.map { it.section }.distinct().sorted() }
    val statuses = remember(state.students) { state.students.map { it.status }.distinct().sorted() }
    val allFilters = selectedClasses + selectedSections + selectedStatus

    val filtered = state.students.filter { s ->
        val q = query.isBlank() ||
            s.fullName.contains(query, ignoreCase = true) ||
            s.rollNumber.contains(query, ignoreCase = true) ||
            s.studentCode.contains(query, ignoreCase = true) ||
            s.className.contains(query, ignoreCase = true)
        val classOk = selectedClasses.isEmpty() || s.className in selectedClasses
        val sectionOk = selectedSections.isEmpty() || s.section in selectedSections
        val statusOk = selectedStatus.isEmpty() || s.status in selectedStatus
        q && classOk && sectionOk && statusOk
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VInput(
                value = query,
                onValueChange = { query = it },
                label = "",
                placeholder = appString(StringKeys.PPL_SEARCH_STUDENTS),
                leadingIcon = VIcons.Search,
                modifier = Modifier.weight(1f),
            )
            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VColors.surfaceCard)
                        .clickable { menuExpanded = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.More, contentDescription = "More", tint = VColors.ink, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(VColors.surfaceCard, RoundedCornerShape(14.dp)),
                ) {
                    DropdownMenuItem(
                        text = { Text(appString(StringKeys.PPL_ADD_STUDENT)) },
                        onClick = { menuExpanded = false; onAddClick() },
                        leadingIcon = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                    DropdownMenuItem(
                        text = { Text(appString(StringKeys.PPL_IMPORT_CSV)) },
                        onClick = { menuExpanded = false; onImportClick() },
                        leadingIcon = { Icon(VIcons.Upload, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                    DropdownMenuItem(
                        text = { Text(appString(StringKeys.PPL_GRADUATE)) },
                        onClick = { menuExpanded = false; showGraduate = true },
                        enabled = !state.isLoading && state.students.isNotEmpty(),
                        leadingIcon = { Icon(VIcons.Users, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                }
            }
        }
        FilterChipRow(
            chips = listOf(
                FilterChipSpec(
                    label = "Class",
                    options = classes,
                    selected = selectedClasses,
                    onToggle = { selectedClasses = if (it in selectedClasses) selectedClasses - it else selectedClasses + it },
                ),
                FilterChipSpec(
                    label = "Section",
                    options = sections,
                    selected = selectedSections,
                    onToggle = { selectedSections = if (it in selectedSections) selectedSections - it else selectedSections + it },
                ),
                FilterChipSpec(
                    label = "Status",
                    options = statuses,
                    selected = selectedStatus,
                    onToggle = { selectedStatus = if (it in selectedStatus) selectedStatus - it else selectedStatus + it },
                ),
            ),
        )
        ActiveFilterChips(
            selected = allFilters,
            onRemove = {
                selectedClasses = selectedClasses - it
                selectedSections = selectedSections - it
                selectedStatus = selectedStatus - it
            },
            onClearAll = {
                selectedClasses = emptySet()
                selectedSections = emptySet()
                selectedStatus = emptySet()
            },
        )
        LinkRequestsBanner(
            count = state.linkRequestCount,
            onClick = onOpenLinkRequests,
        )
        VStateHost(
            modifier = Modifier.fillMaxWidth().weight(1f),
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
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                filtered.forEachIndexed { index, s ->
                    val phone = s.parentPhone?.takeIf { it.isNotBlank() }
                    StudentCard(
                        student = s,
                        onOpen = { onOpenStudent(s.id) },
                        onCall = {
                            if (phone != null) phoneHelper.dialPhone(phone)
                            else snackMessage = "No parent phone available for ${s.fullName}"
                        },
                        onMessage = {
                            if (phone != null) phoneHelper.sendSms(phone)
                            else snackMessage = "No parent phone available for ${s.fullName}"
                        },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
                    )
                }
            }
        }
    }

    // ── Graduation dialog ─────────────────────────────────────────────────
    if (showGraduate) {
        var gradYear by remember { mutableStateOf("") }
        val currentYear = 2026
        VBottomSheet(
            visible = true,
            onDismiss = { showGraduate = false },
        ) {
            VBottomSheetHeader(title = appString(StringKeys.PPL_MARK_ALUMNI))
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

    // ── Snackbar for no-phone warning ────────────────────────────────────
    snackMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
            snackMessage = null
        }
        Box(Modifier.fillMaxSize()) {
            VSnackbar(
                message = msg,
                visible = true,
                onDismiss = { snackMessage = null },
                tone = VSnackbarTone.Warning,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

// ─────────────────────── Non-teaching-staff sub-tab ───────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StaffSubTab(
    state: StaffRosterState,
    onRetry: () -> Unit,
    onSearch: (String) -> Unit,
    onAddClick: () -> Unit,
    onOpenStaff: (String) -> Unit,
) {
    val phoneHelper = rememberPhoneHelper()
    var selectedDepartments by remember { mutableStateOf(setOf<String>()) }
    var selectedRoles by remember { mutableStateOf(setOf<String>()) }
    var selectedStatuses by remember { mutableStateOf(setOf<String>()) }

    val departments = remember(state.staff) { state.staff.mapNotNull { it.department }.filter { it.isNotBlank() }.distinct().sorted() }
    val roles = remember(state.staff) { state.staff.map { it.role }.distinct().sorted() }
    val statuses = remember(state.staff) { state.staff.map { it.status }.distinct().sorted() }
    val allFilters = selectedDepartments + selectedRoles + selectedStatuses

    val filtered = state.staff.filter { s ->
        val q = state.query.isBlank() ||
            s.fullName.contains(state.query, ignoreCase = true) ||
            s.role.contains(state.query, ignoreCase = true) ||
            s.department?.contains(state.query, ignoreCase = true) == true
        val deptOk = selectedDepartments.isEmpty() || s.department in selectedDepartments
        val roleOk = selectedRoles.isEmpty() || s.role in selectedRoles
        val statusOk = selectedStatuses.isEmpty() || s.status in selectedStatuses
        q && deptOk && roleOk && statusOk
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VInput(
                value = state.query,
                onValueChange = onSearch,
                label = "",
                placeholder = appString(StringKeys.PPL_SEARCH_STAFF),
                leadingIcon = VIcons.Search,
                modifier = Modifier.weight(1f),
            )
            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VColors.surfaceCard)
                        .clickable { menuExpanded = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.More, contentDescription = "More", tint = VColors.ink, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(VColors.surfaceCard, RoundedCornerShape(14.dp)),
                ) {
                    DropdownMenuItem(
                        text = { Text(appString(StringKeys.PPL_ADD_STAFF)) },
                        onClick = { menuExpanded = false; onAddClick() },
                        enabled = !state.isSaving,
                        leadingIcon = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                }
            }
        }
        FilterChipRow(
            chips = listOf(
                FilterChipSpec(
                    label = "Department",
                    options = departments,
                    selected = selectedDepartments,
                    onToggle = { selectedDepartments = if (it in selectedDepartments) selectedDepartments - it else selectedDepartments + it },
                ),
                FilterChipSpec(
                    label = "Role",
                    options = roles,
                    selected = selectedRoles,
                    onToggle = { selectedRoles = if (it in selectedRoles) selectedRoles - it else selectedRoles + it },
                ),
                FilterChipSpec(
                    label = "Status",
                    options = statuses,
                    selected = selectedStatuses,
                    onToggle = { selectedStatuses = if (it in selectedStatuses) selectedStatuses - it else selectedStatuses + it },
                ),
            ),
        )
        ActiveFilterChips(
            selected = allFilters,
            onRemove = {
                selectedDepartments = selectedDepartments - it
                selectedRoles = selectedRoles - it
                selectedStatuses = selectedStatuses - it
            },
            onClearAll = {
                selectedDepartments = emptySet()
                selectedRoles = emptySet()
                selectedStatuses = emptySet()
            },
        )
        VStateHost(
            modifier = Modifier.fillMaxWidth().weight(1f),
            loading = state.isLoading,
            error = state.error,
            isEmpty = filtered.isEmpty(),
            emptyTitle = if (state.query.isBlank() && allFilters.isEmpty()) appString(StringKeys.PPL_NO_STAFF) else appString(StringKeys.PPL_NO_MATCHES),
            emptyBody = if (state.query.isBlank() && allFilters.isEmpty())
                appString(StringKeys.PPL_NO_STAFF_BODY)
            else appString(StringKeys.PPL_NO_STAFF_MATCHES, "query" to state.query),
            emptyIcon = VIcons.Users,
            onRetry = onRetry,
            skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 5) },
        ) {
            val ready = filtered.isNotEmpty() && !state.isLoading
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                filtered.forEachIndexed { index, s ->
                    StaffCard(
                        staff = s,
                        onOpen = { onOpenStaff(s.id) },
                        onCall = { phoneHelper.dialPhone(s.phone ?: "") },
                        onMessage = { phoneHelper.sendSms(s.phone ?: "") },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
                    )
                }
            }
        }
    }
}

// ───────────────────────────── dialogs ─────────────────────────────

/**
 * RA-22: add-teacher form. A teacher is provisioned by email (with an initial
 * password) or by phone (OTP login). Frozen primitives only.
 */
@Composable
private fun AddTeacherSheet(
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

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = appString(StringKeys.PPL_ADD_TEACHER))
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

/**
 * RA-S17: add-staff form for a non-teaching-staff member. Name + role required;
 * department / phone / email optional. Frozen primitives only.
 */
@Composable
private fun AddStaffSheet(
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

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = appString(StringKeys.PPL_ADD_STAFF_MEMBER))
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

// ───────────────────────── Student add / import dialogs ─────────────────────

/** Manual single-student add from the People → Students sub-tab. */
@Composable
private fun AddStudentPeopleSheet(
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

    val phoneDigits = parentPhone.count { it.isDigit() }
    val phoneOk = parentPhone.isBlank() || phoneDigits >= 10
    val classValid = className.isNotBlank() && (availableClassNames.isEmpty() || availableClassNames.any { it.equals(className, ignoreCase = true) })
    val canSubmit = name.isNotBlank() && classValid && roll.isNotBlank() &&
        phoneOk && !isSubmitting

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = appString(StringKeys.PPL_ADD_STUDENT))
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VInput(name, { name = it }, label = appString(StringKeys.PPL_FULL_NAME), placeholder = appString(StringKeys.PPL_NAME_PH_STUDENT), leadingIcon = VIcons.User)
            Box {
                VInput(
                    className,
                    { className = it },
                    label = appString(StringKeys.PPL_CLASS),
                    placeholder = appString(StringKeys.PPL_CLASS_PH),
                    modifier = Modifier.fillMaxWidth().clickable { classDropdownExpanded = true },
                )
                DropdownMenu(
                    expanded = classDropdownExpanded,
                    onDismissRequest = { classDropdownExpanded = false },
                    modifier = Modifier.background(VColors.surfaceCard, RoundedCornerShape(14.dp)),
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
            VInput(section, { section = it }, label = appString(StringKeys.PPL_SECTION), placeholder = appString(StringKeys.PPL_SECTION_PH))
            VInput(roll, { roll = it }, label = appString(StringKeys.PPL_ROLL_NUMBER), placeholder = appString(StringKeys.PPL_ROLL_PH), keyboardType = KeyboardType.Number)
            VInput(
                admissionDate,
                { admissionDate = it },
                label = "Admission Date",
                placeholder = "YYYY-MM-DD (optional)",
            )
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
                onClick = { onSubmit(name, className, section, roll, parentPhone, admissionDate) },
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

/**
 * Bulk CSV import. The admin pastes (or, on platforms with a file picker,
 * loads) CSV rows. The first line must be a header; accepted columns:
 *   full_name, class_name, roll_number, section, student_code
 * `section` and `student_code` are optional. Sent verbatim to
 * POST /api/v1/school/students/import, which parses + validates each row.
 */
@Composable
private fun ImportStudentsSheet(
    isSubmitting: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (csv: String) -> Unit,
) {
    var csv by remember {
        mutableStateOf("full_name,class_name,section,roll_number\n")
    }
    var fileName by remember { mutableStateOf<String?>(null) }
    val canSubmit = csv.lineSequence().drop(1).any { it.isNotBlank() } && !isSubmitting
    val scope = rememberCoroutineScope()
    val csvTemplate = "full_name,class_name,section,roll_number,parent_phone\n"
    val fileSaver = rememberFileSaverLauncher() { _ -> }
    val csvPicker = rememberFilePickerLauncher(
        type = PickerType.File(),
        mode = PickerMode.Single,
        title = "Choose a CSV file",
    ) { platformFile ->
        if (platformFile != null) {
            scope.launch {
                val bytes = platformFile.readBytes()
                csv = bytes.decodeToString()
                fileName = platformFile.name
            }
        }
    }

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = appString(StringKeys.PPL_IMPORT_STUDENTS_CSV), subtitle = appString(StringKeys.PPL_IMPORT_INSTRUCTIONS))
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Primary: file upload gateway
            VCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { csvPicker.launch() },
                padding = 20.dp,
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(VColors.violet.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(VIcons.Upload, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(24.dp))
                    }
                    Text(
                        if (fileName != null) "File: $fileName" else "Upload CSV File",
                        style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    Text(
                        if (fileName != null) "Tap to replace file" else "Tap to choose a .csv file from your device",
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
            }
            // Secondary: paste area (collapsible-style, still visible)
            Text("or paste CSV content manually", style = VTypography.caption, color = VColors.ink3)
            VInput(
                value = csv,
                onValueChange = { csv = it; fileName = null },
                label = appString(StringKeys.PPL_CSV_CONTENT),
                placeholder = appString(StringKeys.PPL_CSV_PH),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = "Download Template",
                    onClick = { fileSaver.launch(baseName = "student_import_template", extension = "csv", bytes = csvTemplate.encodeToByteArray()) },
                    variant = VButtonVariant.Secondary,
                    modifier = Modifier.weight(1f),
                )
            }
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

