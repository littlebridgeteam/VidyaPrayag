package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolClassDto
import com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolDashboardViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolTeachersState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolTeachersViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StaffRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StaffViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VSnackbar
import com.littlebridge.enrollplus.ui.v2.components.VSnackbarTone
import com.littlebridge.enrollplus.ui.v2.components.VSheetPicker
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

// Bottom clearance for the floating VCreamBottomNav (inner Box 60dp + 12dp*2
// vertical padding ≈ 84dp) plus a comfortable gap so the final card never sits
// flush against — or behind — the nav. Combined with .navigationBarsPadding()
// this keeps every card fully tappable above the bar.
private val PeopleBottomNavClearance = 112.dp

// Minimum height reserved for the per-tab state area so VStateHost's loading /
// error / empty legs (which fill their box) still render inside the single
// verticalScroll page, where an unconstrained fillMaxSize would collapse to 0.
private val PeopleStateMinHeight = 360.dp

private enum class PeopleSubTab {
    Teachers, Students, Staff;

    @Composable
    fun label(): String = when (this) {
        Teachers -> appString(StringKeys.PPL_TAB_TEACHERS)
        Students -> appString(StringKeys.PPL_TAB_STUDENTS)
        // Rebranded from the global "Non-teaching staff" string to the shorter
        // "Non-teaching" for the tab chip — keeps the AppStrings i18n value intact
        // everywhere else while giving the tab a tighter, single-word-ish label.
        Staff    -> "Non-teaching"
    }
}

@Composable
private fun PeopleDirectoryHeader(
    adminName: String,
    greeting: String,
    unreadNotificationCount: Int,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Enroll+",
                color = VColors.violet,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(VColors.white)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onOpenNotifications,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Bell, contentDescription = "Notifications", tint = VColors.ink, modifier = Modifier.size(18.dp))
                if (unreadNotificationCount > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(3.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(VColors.coral)
                            .border(1.5.dp, VColors.white, CircleShape),
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        val greetingText = listOf(greeting.ifBlank { "Hi" }, adminName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
        Text(greetingText, color = VColors.violet, fontSize = 15.4.sp, fontWeight = FontWeight.SemiBold)
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VColors.ink)) { append("People") }
                withStyle(SpanStyle(fontWeight = FontWeight.Light, color = VColors.ink3)) { append(" Directory") }
            },
            fontSize = 26.4.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.6).sp,
        )
    }
}

@Composable
private fun PremiumPeopleAddButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(42.dp)
            .shadow(3.dp, RoundedCornerShape(14.dp), ambientColor = VColors.violet.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(14.dp))
            .background(VColors.violet)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .alpha(if (enabled) 1f else 0.42f)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(VIcons.Plus, contentDescription = null, tint = VColors.white, modifier = Modifier.size(14.dp))
        Text("Add", color = VColors.white, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PremiumPeopleMoreButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x0D26234D))
            .clip(RoundedCornerShape(14.dp))
            .background(VColors.white)
            .border(1.dp, Color(0x0F26234D), RoundedCornerShape(14.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(VIcons.More, contentDescription = "More actions", tint = VColors.ink, modifier = Modifier.size(20.dp))
    }
}

/**
 * Premium segmented control for the People sub-tabs. The track itself is the
 * "surrounding border" (a soft cream capsule with a hairline outline); the
 * active segment lifts onto a violet gradient with a subtle glow and reveals a
 * leading glyph, while inactive segments stay quiet ink-3 text. Equal-weight
 * segments keep "Non-teaching staff" on a single line — no awkward wrapping.
 */
@Composable
private fun PeopleDirectoryTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val icons = listOf(VIcons.School, VIcons.GraduationCap, VIcons.UsersGroup)
    // Individual chip pills — each tab is its own free-standing rounded chip with
    // its own selected / unselected state and a smooth cross-fade between them.
    // No shared segmented "track" behind them anymore.
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, label ->
            PeopleDirectoryTabChip(
                label = label,
                icon = icons[index],
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * A single directory tab rendered as a stand-alone pill. Selected → violet
 * gradient fill with a soft violet glow + white icon/label; unselected → quiet
 * white chip with a hairline border and muted ink. Colour, border, elevation
 * and the icon reveal all animate so tapping between tabs feels smooth rather
 * than snapping.
 */
@Composable
private fun PeopleDirectoryTabChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(13.dp)
    val anim = tween<Color>(220, easing = FastOutSlowInEasing)
    val bg by animateColorAsState(
        if (selected) VColors.violet else VColors.white,
        animationSpec = anim,
        label = "tabBg",
    )
    val fg by animateColorAsState(
        if (selected) VColors.white else VColors.ink3,
        animationSpec = anim,
        label = "tabFg",
    )
    val borderColor by animateColorAsState(
        if (selected) Color.Transparent else Color(0x1A26234D),
        animationSpec = anim,
        label = "tabBorder",
    )
    // Icon glyph fades + grows in as the chip becomes selected.
    val iconScale by animateFloatAsState(
        if (selected) 1f else 0.6f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "tabIconScale",
    )
    val elevation by animateFloatAsState(
        if (selected) 8f else 0f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "tabElev",
    )

    Row(
        modifier = modifier
            .shadow(
                elevation.dp,
                shape,
                ambientColor = VColors.violet.copy(alpha = 0.40f),
                spotColor = VColors.violet.copy(alpha = 0.40f),
            )
            .clip(shape)
            .background(bg)
            .border(1.dp, borderColor, shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = fg,
            modifier = Modifier
                .size(13.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                    alpha = iconScale
                },
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            color = fg,
            fontSize = 11.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            letterSpacing = (-0.3).sp,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Clip,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

/**
 * SchoolPeopleScreenV2 — RA-S17 rebuild.
 *
 * The People tab is a pager-driven 3-sub-tab surface — **Teachers /
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
    onOpenNotifications: () -> Unit = {},
    onOpenStudent: (String) -> Unit = {},
    onOpenTeacher: (String) -> Unit = {},
    // RA-TAM — overflow "Assign classes" opens the reusable assignment module.
    onAssignClasses: (String) -> Unit = {},
    onOpenStaff: (String) -> Unit = {},
    onOpenMessages: (String?) -> Unit = {},
    onGraduateStudents: (List<String>, Int) -> Unit = { _, _ -> },
    deepLinkDestination: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    teachersViewModel: SchoolTeachersViewModel = koinViewModel(),
    studentsViewModel: StudentRosterViewModel = koinViewModel(),
    staffViewModel: StaffViewModel = koinViewModel(),
    classesViewModel: ClassesSubjectsViewModel = koinViewModel(),
    dashboardViewModel: SchoolDashboardViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    teacherRefreshKey: Int,
    studentRefreshKey: Int,
) {
    val teachersState by teachersViewModel.state.collectAsStateV2()
    val studentsState by studentsViewModel.state.collectAsStateV2()
    val staffState by staffViewModel.state.collectAsStateV2()
    val classesState by classesViewModel.state.collectAsStateV2()
    val dashboardState by dashboardViewModel.state.collectAsStateV2()
    val notificationsState by notificationsViewModel.state.collectAsStateV2()

    LaunchedEffect(teacherRefreshKey){
        teachersViewModel.load()
    }
    LaunchedEffect(Unit) {
        teachersViewModel.load()
    }
    LaunchedEffect(studentRefreshKey){
        studentsViewModel.load()
    }
    LaunchedEffect(Unit) {
        staffViewModel.load()
        classesViewModel.loadClasses()
    }
    SchoolPeopleContent(
        teachersState = teachersState,
        onTeachersRetry = teachersViewModel::load,
        onAddTeacher = teachersViewModel::addTeacher,
        onDeactivateTeacher = teachersViewModel::removeTeacher,
        onClearTeacherMessages = teachersViewModel::clearMessages,
        studentsState = studentsState,
        onStudentsRetry = studentsViewModel::load,
        onAddStudent = { name, cls, sec, roll, phone, admission -> studentsViewModel.addStudent(name, cls, sec, roll, phone, admission) },
        onImportStudentsCsv = studentsViewModel::importStudentsCsv,
        onClearStudentMessages = studentsViewModel::clearMessages,
        availableClasses = classesState.classes,
        staffState = staffState,
        onStaffRetry = staffViewModel::load,
        onStaffSearch = staffViewModel::onQueryChange,
        onAddStaff = staffViewModel::addStaff,
        onClearStaffMessages = staffViewModel::clearMessages,
        onOpenLinkRequests = onOpenLinkRequests,
        onOpenNotifications = onOpenNotifications,
        adminName = dashboardState.overview?.header?.adminName?.takeIf { it.isNotBlank() }
            ?: dashboardState.adminName,
        greeting = dashboardState.overview?.header?.greeting.orEmpty(),
        unreadNotificationCount = notificationsState.unreadCount,
        onOpenStudent = onOpenStudent,
        onOpenTeacher = onOpenTeacher,
        onAssignClasses = onAssignClasses,
        onOpenStaff = onOpenStaff,
        onOpenMessages = onOpenMessages,
        onGraduateStudents = onGraduateStudents,
        deepLinkDestination = deepLinkDestination,
        onDeepLinkConsumed = onDeepLinkConsumed,
        modifier = modifier,
    )
}

@Composable
private fun SchoolPeopleContent(
    teachersState: SchoolTeachersState,
    onTeachersRetry: () -> Unit,
    onAddTeacher: (name: String, identifier: String, initialPassword: String?, onAdded: (() -> Unit)?) -> Unit,
    onDeactivateTeacher: (String) -> Unit,
    onClearTeacherMessages: () -> Unit,
    studentsState: StudentRosterState,
    onStudentsRetry: () -> Unit,
    onAddStudent: (name: String, className: String, section: String, rollNumber: String, parentPhone: String, admissionDate: String) -> Unit,
    onImportStudentsCsv: (String) -> Unit,
    onClearStudentMessages: () -> Unit,
    availableClasses: List<SchoolClassDto> = emptyList(),
    staffState: StaffRosterState,
    onStaffRetry: () -> Unit,
    onStaffSearch: (String) -> Unit,
    onAddStaff: (name: String, role: String, department: String, phone: String, email: String, onAdded: (() -> Unit)?) -> Unit,
    onClearStaffMessages: () -> Unit,
    onOpenLinkRequests: () -> Unit,
    onOpenNotifications: () -> Unit,
    adminName: String,
    greeting: String,
    unreadNotificationCount: Int,
    onOpenStudent: (String) -> Unit,
    onOpenTeacher: (String) -> Unit,
    onAssignClasses: (String) -> Unit,
    onOpenStaff: (String) -> Unit,
    onOpenMessages: (String?) -> Unit,
    onGraduateStudents: (List<String>, Int) -> Unit,
    deepLinkDestination: String?,
    onDeepLinkConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialSubTab = when (deepLinkDestination) {
        "add_students" -> PeopleSubTab.Students
        "add_staff" -> PeopleSubTab.Staff
        else -> PeopleSubTab.Teachers
    }
    var subTab by remember { mutableStateOf(initialSubTab) }
    var showAddTeacher by remember { mutableStateOf(false) }
    var showAddStaff by remember { mutableStateOf(false) }
    var showAddStudent by remember { mutableStateOf(false) }
    var showImportStudents by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(deepLinkDestination) {
        when (deepLinkDestination) {
            "add_teachers" -> {
                subTab = PeopleSubTab.Teachers
                showAddTeacher = true
            }
            "add_students" -> {
                subTab = PeopleSubTab.Students
                showAddStudent = true
            }
            "add_staff" -> {
                subTab = PeopleSubTab.Staff
                showAddStaff = true
            }
            else -> return@LaunchedEffect
        }
        onDeepLinkConsumed()
    }

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
    val scrollState = rememberScrollState()

    // CRITICAL FIX (RA-S17.5): the ENTIRE screen — header, tabs, search, filters
    // and card list — scrolls as ONE unit inside a single verticalScroll, exactly
    // like the `.screen` element in the prototype. No pager, no frozen top: tab
    // switching is a pure tap (matches people-tab-premium.js `switchSubTab`), and
    // switching resets the scroll to the top of the page.
    LaunchedEffect(subTab) { scrollState.scrollTo(0) }

    VPullRefresh(
        isRefreshing = teachersState.isLoading || studentsState.isLoading || staffState.isLoading,
        onRefresh = {
            when (subTab) {
                PeopleSubTab.Teachers -> onTeachersRetry()
                PeopleSubTab.Students -> onStudentsRetry()
                PeopleSubTab.Staff -> onStaffRetry()
            }
        },
    ) {
        Column(
            modifier
                .fillMaxSize()
                .background(VColors.cream)
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                // CRITICAL FIX (RA-S17.5): clear the floating VCreamBottomNav so the
                // last card is never hidden behind it. navigationBarsPadding() covers
                // the system inset; PeopleBottomNavClearance covers the nav bar itself.
                .navigationBarsPadding()
                .padding(
                    start = 20.dp,
                    top = 14.dp,
                    end = 20.dp,
                    bottom = PeopleBottomNavClearance,
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PeopleDirectoryHeader(
                adminName = adminName,
                greeting = greeting,
                unreadNotificationCount = unreadNotificationCount,
                onOpenNotifications = onOpenNotifications,
                modifier = Modifier
                    .graphicsLayer(translationY = headerOffset.value)
                    .alpha(headerAlpha.value),
            )

            PeopleDirectoryTabs(
                tabs = subTabLabels,
                selectedIndex = subTab.ordinal,
                onSelect = { subTab = PeopleSubTab.entries[it] },
            )

            when (subTab) {
                PeopleSubTab.Teachers -> TeachersSubTab(
                    state = teachersState,
                    onRetry = onTeachersRetry,
                    onAddClick = { showAddTeacher = true },
                    onOpenTeacher = onOpenTeacher,
                    onOpenMessages = onOpenMessages,
                    onDeactivate = onDeactivateTeacher,
                    onAssignClass = onAssignClasses,
                )
                PeopleSubTab.Students -> StudentsSubTab(
                    state = studentsState,
                    onRetry = onStudentsRetry,
                    onOpenStudent = onOpenStudent,
                    onOpenLinkRequests = onOpenLinkRequests,
                    onOpenMessages = onOpenMessages,
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

    // ── Add-teacher dialog (RA-22) ─────────────────────────────────────────
    if (showAddTeacher) {
        AddTeacherSheet(
            isSubmitting = teachersState.isMutating,
            error = teachersState.errorMessage,
            availableClasses = availableClasses,
            onDismiss = { showAddTeacher = false; onClearTeacherMessages() },
            onSubmit = { name, identifier, password ->
                onAddTeacher(name, identifier, password) { showAddTeacher = false }
            },
        )
    }

    // ── Add-staff dialog (RA-S17) ──────────────────────────────────────────
    if (showAddStaff) {
        AddStaffSheet(
            isSubmitting = staffState.isSaving,
            error = staffState.addError,
            departments = staffState.staff.mapNotNull { it.department }.filter { it.isNotBlank() }.distinct().sorted(),
            onDismiss = { showAddStaff = false; onClearStaffMessages() },
            onSubmit = { name, role, dept, phone, email ->
                onAddStaff(name, role, dept, phone, email) { showAddStaff = false }
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
            availableClasses = availableClasses,
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
    onOpenMessages: (String?) -> Unit,
    onDeactivate: (String) -> Unit,
    onAssignClass: (String) -> Unit,
) {
    val phoneHelper = rememberPhoneHelper()
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

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            PremiumPeopleAddButton(
                enabled = !state.isMutating,
                onClick = onAddClick,
            )
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
            modifier = Modifier.fillMaxWidth().heightIn(min = PeopleStateMinHeight),
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
            // No inner verticalScroll here — the whole People screen is one scroll.
            // No "Load more": every teacher is rendered directly in the list.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                filtered.forEachIndexed { index, t ->
                    TeacherCard(
                        teacher = t,
                        onViewProfile = { onOpenTeacher(t.id) },
                        onCall = { t.profile.phone?.let(phoneHelper::dialPhone) },
                        onMessage = { onOpenMessages(t.id) },
                        onAssignClass = { onAssignClass(t.id) },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
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
    onOpenMessages: (String?) -> Unit,
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

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                PremiumPeopleMoreButton(onClick = { menuExpanded = true })
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(VColors.white, RoundedCornerShape(14.dp)),
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
            modifier = Modifier.fillMaxWidth().heightIn(min = PeopleStateMinHeight),
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
            // No inner verticalScroll — the whole People screen is one scroll.
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                            onOpenMessages(s.parentUserId)
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
            PremiumPeopleSheetHeader(
                title = appString(StringKeys.PPL_MARK_ALUMNI),
                subtitle = "Move the selected students into alumni records",
                onClose = { showGraduate = false },
            )
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
                        variant = VButtonVariant.Secondary,
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
                        tone = VButtonTone.Lavender,
                        size = VButtonSize.Sm,
                        soft = false,
                        modifier = Modifier.weight(1f),
                        enabled = filtered.isNotEmpty(),
                    )
                }
            }
        }
    }

    // ── Snackbar for no-phone warning ────────────────────────────────────
    // A Popup keeps the toast floating at the bottom of the window regardless of
    // where this sub-tab sits inside the single verticalScroll page (a plain
    // fillMaxSize Box would collapse to zero height inside the scroll).
    snackMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
            snackMessage = null
        }
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { snackMessage = null },
            properties = PopupProperties(focusable = false),
        ) {
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = PeopleBottomNavClearance)) {
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
    var snackMessage by remember { mutableStateOf<String?>(null) }

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

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            PremiumPeopleAddButton(
                enabled = !state.isSaving,
                onClick = onAddClick,
            )
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
            modifier = Modifier.fillMaxWidth().heightIn(min = PeopleStateMinHeight),
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
            // No inner verticalScroll — the whole People screen is one scroll.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                filtered.forEachIndexed { index, s ->
                    val phone = s.phone?.takeIf { it.isNotBlank() }
                    StaffCard(
                        staff = s,
                        onOpen = { onOpenStaff(s.id) },
                        onCall = {
                            if (phone != null) phoneHelper.dialPhone(phone)
                            else snackMessage = "No phone available for ${s.fullName}"
                        },
                        onMessage = {
                            if (phone != null) phoneHelper.sendSms(phone)
                            else snackMessage = "No phone available for ${s.fullName}"
                        },
                        modifier = Modifier.staggeredItemEntrance(index, ready),
                    )
                }
            }
        }
    }

    // ── Snackbar for no-phone warning ────────────────────────────────────
    // A Popup keeps the toast floating at the bottom of the window regardless of
    // where this sub-tab sits inside the single verticalScroll page (a plain
    // fillMaxSize Box would collapse to zero height inside the scroll).
    snackMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
            snackMessage = null
        }
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { snackMessage = null },
            properties = PopupProperties(focusable = false),
        ) {
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = PeopleBottomNavClearance)) {
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
}

// ───────────────────────────── dialogs ─────────────────────────────

@Composable
private fun PremiumPeopleSheetHeader(
    title: String,
    subtitle: String,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, color = VColors.ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = VColors.ink3, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(VColors.cream)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClose,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Close, contentDescription = "Close", tint = VColors.ink2, modifier = Modifier.size(16.dp))
        }
    }
}

/**
 * RA-22: add-teacher form. A teacher is provisioned by email (with an initial
 * password) or by phone (OTP login). Frozen primitives only.
 */
@Composable
private fun AddTeacherSheet(
    isSubmitting: Boolean,
    error: String?,
    availableClasses: List<SchoolClassDto>,
    onDismiss: () -> Unit,
    onSubmit: (name: String, identifier: String, initialPassword: String?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    val selectedClass = availableClasses.firstOrNull { it.name == className }

    val isEmail = identifier.contains("@")
    val canSubmit = name.isNotBlank() &&
        identifier.isNotBlank() &&
        (!isEmail || password.isNotBlank()) &&
        !isSubmitting

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        PremiumPeopleSheetHeader(
            title = appString(StringKeys.PPL_ADD_TEACHER),
            subtitle = "Create a new teacher account",
            onClose = onDismiss,
        )
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VInput(
                value = name,
                onValueChange = { name = it },
                label = appString(StringKeys.PPL_FULL_NAME),
                placeholder = appString(StringKeys.PPL_NAME_PH_TEACHER),
            )
            VInput(
                value = identifier,
                onValueChange = { identifier = it },
                label = appString(StringKeys.PPL_EMAIL_OR_PHONE),
                placeholder = appString(StringKeys.PPL_EMAIL_PHONE_PH),
                keyboardType = if (isEmail) KeyboardType.Email else KeyboardType.Text,
            )
            VInput(
                value = password,
                onValueChange = { password = it },
                label = appString(StringKeys.PPL_INITIAL_PASSWORD),
                placeholder = appString(StringKeys.PPL_PASSWORD_PH),
                isPassword = true,
                enabled = isEmail,
            )
            VSheetPicker(
                label = "Assign Class",
                value = className,
                options = availableClasses.map { it.name },
                onSelect = { className = it; section = "" },
                placeholder = "Select class",
                searchable = true,
            )
            VSheetPicker(
                label = "Section",
                value = section,
                options = selectedClass?.sections.orEmpty(),
                onSelect = { section = it },
                placeholder = if (className.isBlank()) "Select class first" else "Select section",
                enabled = selectedClass != null,
            )
            if (!isEmail && identifier.isNotBlank()) {
                Text(appString(StringKeys.PPL_OTP_HINT), style = VTypography.caption, color = VColors.ink2)
            }
            Spacer(Modifier.height(4.dp))
            if (error != null) {
                Text(
                    error,
                    style = VTypography.caption,
                    color = VColors.coral,
                )
                Spacer(Modifier.height(4.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Secondary,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = !isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.PPL_ADD_TEACHER),
                    onClick = {
                        onSubmit(name, identifier, password.takeIf { isEmail && it.isNotBlank() })
                    },
                    variant = VButtonVariant.Primary,
                    tone = VButtonTone.Lavender,
                    soft = false,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = canSubmit,
                    loading = isSubmitting,
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
private fun AddStaffSheet(
    isSubmitting: Boolean,
    error: String?,
    departments: List<String>,
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
        PremiumPeopleSheetHeader(
            title = appString(StringKeys.PPL_ADD_STAFF_MEMBER),
            subtitle = "Create a non-teaching staff record",
            onClose = onDismiss,
        )
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VInput(
                value = name,
                onValueChange = { name = it },
                label = appString(StringKeys.PPL_FULL_NAME),
                placeholder = appString(StringKeys.PPL_NAME_PH_STAFF),
            )
            VInput(
                value = role,
                onValueChange = { role = it },
                label = appString(StringKeys.PPL_ROLE),
                placeholder = appString(StringKeys.PPL_ROLE_PH),
            )
            VSheetPicker(
                label = appString(StringKeys.PPL_DEPT_OPTIONAL),
                value = department,
                options = departments,
                onSelect = { department = it },
                placeholder = appString(StringKeys.PPL_DEPT_PH),
                searchable = true,
            )
            VInput(
                value = phone,
                onValueChange = { phone = it },
                label = appString(StringKeys.PPL_PHONE_OPTIONAL),
                placeholder = appString(StringKeys.PPL_PHONE_PH),
                keyboardType = KeyboardType.Phone,
            )
            VInput(
                value = email,
                onValueChange = { email = it },
                label = appString(StringKeys.PPL_EMAIL_OPTIONAL),
                placeholder = appString(StringKeys.PPL_EMAIL_PH),
                keyboardType = KeyboardType.Email,
            )
            Spacer(Modifier.height(4.dp))
            if (error != null) {
                Text(
                    error,
                    style = VTypography.caption,
                    color = VColors.coral,
                )
                Spacer(Modifier.height(4.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Secondary,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = !isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.PPL_ADD_STAFF),
                    onClick = { onSubmit(name, role, department, phone, email) },
                    variant = VButtonVariant.Primary,
                    tone = VButtonTone.Lavender,
                    soft = false,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = canSubmit,
                    loading = isSubmitting,
                )
            }
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
    availableClasses: List<SchoolClassDto> = emptyList(),
) {
    var name by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var roll by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var admissionDate by remember { mutableStateOf("") }
    val selectedClass = availableClasses.firstOrNull { it.name == className }

    val phoneDigits = parentPhone.count { it.isDigit() }
    val phoneOk = parentPhone.isBlank() || phoneDigits >= 10
    val classValid = className.isNotBlank() && selectedClass != null
    val canSubmit = name.isNotBlank() && classValid && roll.isNotBlank() &&
        phoneOk && !isSubmitting

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        PremiumPeopleSheetHeader(
            title = appString(StringKeys.PPL_ADD_STUDENT),
            subtitle = "Manually add a single student",
            onClose = onDismiss,
        )
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VInput(name, { name = it }, label = "Student Name", placeholder = appString(StringKeys.PPL_NAME_PH_STUDENT))
            VSheetPicker(
                label = appString(StringKeys.PPL_CLASS),
                value = className,
                options = availableClasses.map { it.name },
                onSelect = { className = it; section = "" },
                placeholder = appString(StringKeys.PPL_CLASS_PH),
                searchable = true,
            )
            if (className.isNotBlank() && !classValid) {
                Text("Please select a configured class", style = VTypography.caption, color = VColors.coral)
            }
            VSheetPicker(
                label = appString(StringKeys.PPL_SECTION),
                value = section,
                options = selectedClass?.sections.orEmpty(),
                onSelect = { section = it },
                placeholder = if (className.isBlank()) "Select class first" else appString(StringKeys.PPL_SECTION_PH),
                enabled = selectedClass != null,
            )
            VInput(roll, { roll = it }, label = appString(StringKeys.PPL_ROLL_NUMBER), placeholder = appString(StringKeys.PPL_ROLL_PH), keyboardType = KeyboardType.Number)
            VInput(
                parentPhone,
                { parentPhone = it },
                label = appString(StringKeys.PPL_PARENT_PHONE),
                placeholder = appString(StringKeys.PPL_PARENT_PHONE_PH),
                keyboardType = KeyboardType.Phone,
            )
            VInput(
                admissionDate,
                { admissionDate = it },
                label = "Admission Date",
                placeholder = "dd-mm-yyyy",
            )
            if (error != null) {
                Text(error, style = VTypography.caption, color = VColors.coral)
            }
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Secondary,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = !isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.PPL_ADD_STUDENT),
                    onClick = { onSubmit(name, className, section, roll, parentPhone, admissionDate) },
                    variant = VButtonVariant.Primary,
                    tone = VButtonTone.Lavender,
                    soft = false,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = canSubmit,
                    loading = isSubmitting,
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
        PremiumPeopleSheetHeader(
            title = appString(StringKeys.PPL_IMPORT_STUDENTS_CSV),
            subtitle = "Bulk import via CSV file",
            onClose = onDismiss,
        )
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Primary: file upload gateway — dashed cream drop zone (matches prototype .csv-upload)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.cream)
                    .drawBehind {
                        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(10.dp.toPx(), 8.dp.toPx()), 0f,
                            ),
                        )
                        drawRoundRect(
                            color = VColors.line,
                            style = stroke,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                        )
                    }
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { csvPicker.launch() }
                    .padding(vertical = 24.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                    if (fileName != null) "Tap to replace file" else "Click to browse or drag & drop",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
            // Secondary: paste area (collapsible-style, still visible)
            Text("Or paste CSV content", style = VTypography.caption, color = VColors.ink3)
            VInput(
                value = csv,
                onValueChange = { csv = it; fileName = null },
                label = appString(StringKeys.PPL_CSV_CONTENT),
                placeholder = appString(StringKeys.PPL_CSV_PH),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().height(120.dp),
            )
            Text(
                "Download CSV Template",
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.violet,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { fileSaver.launch(baseName = "student_import_template", extension = "csv", bytes = csvTemplate.encodeToByteArray()) }
                    .padding(vertical = 2.dp),
            )
            if (error != null) {
                Text(error, style = VTypography.caption, color = VColors.coral)
            }
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Secondary,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = !isSubmitting,
                )
                VButton(
                    text = appString(StringKeys.PPL_IMPORT),
                    onClick = { onSubmit(csv) },
                    variant = VButtonVariant.Primary,
                    tone = VButtonTone.Lavender,
                    soft = false,
                    modifier = Modifier.weight(1f).height(52.dp),
                    enabled = canSubmit,
                    loading = isSubmitting,
                )
            }
        }
    }
}

