package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolTeachersViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StaffViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentAnalyticsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.form.VSearchField
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VTopTabsPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

private enum class PeopleTabPremium { Teachers, Students, Staff, Alumni }

@Composable
fun SchoolPeoplePremium(
    teacherRefreshKey: Int = 0,
    studentRefreshKey: Int = 0,
    onOpenLinkRequests: () -> Unit = {},
    onOpenStudent: (String) -> Unit = {},
    onOpenTeacher: (String) -> Unit = {},
    onAssignClasses: (String) -> Unit = {},
    onOpenStaff: (String) -> Unit = {},
    onOpenAlumni: () -> Unit = {},
    onGraduateStudents: (List<String>, Int) -> Unit = { _, _ -> },
    viewModel: StudentAnalyticsViewModel = koinViewModel(),
    teachersViewModel: SchoolTeachersViewModel = koinViewModel(),
    studentsViewModel: StudentRosterViewModel = koinViewModel(),
    staffViewModel: StaffViewModel = koinViewModel(),
) {
    val teachersState by teachersViewModel.state.collectAsStateV2()
    val studentsState by studentsViewModel.state.collectAsStateV2()
    val staffState by staffViewModel.state.collectAsStateV2()

    LaunchedEffect(teacherRefreshKey) { teachersViewModel.load() }
    LaunchedEffect(studentRefreshKey) { studentsViewModel.load() }

    var subTab by remember { mutableStateOf(PeopleTabPremium.Teachers) }
    var query by remember { mutableStateOf("") }

    val tabLabels = listOf("Teachers", "Students", "Staff", "Alumni")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("People", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface), modifier = Modifier.padding(horizontal = 20.dp))

        VListTilePremium(
            title = "Link Requests",
            subtitle = "Review parent-child link approvals",
            onClick = onOpenLinkRequests,
            leadingIcon = VIcons.Plus,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        VTopTabsPremium(
            tabs = tabLabels,
            selected = tabLabels[subTab.ordinal],
            onSelect = { label -> subTab = PeopleTabPremium.entries[tabLabels.indexOf(label)] },
        )

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search ${tabLabels[subTab.ordinal].lowercase()}...",
            )

            when (subTab) {
                PeopleTabPremium.Teachers -> {
                    val filtered = teachersState.teachers.filter { t ->
                        query.isBlank() || t.profile.name.contains(query, ignoreCase = true) || t.profile.role.contains(query, ignoreCase = true)
                    }
                    VStateHostPremium(
                        loading = teachersState.isLoading,
                        error = teachersState.errorMessage,
                        isEmpty = filtered.isEmpty(),
                        emptyTitle = if (teachersState.teachers.isEmpty()) "No teachers yet" else "No matches",
                        skeleton = { VShimmerListPremium(itemCount = 5) },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filtered.forEach { t ->
                                VListTilePremium(
                                    title = t.profile.name.ifBlank { "Unnamed Teacher" },
                                    subtitle = t.profile.role.ifBlank { "No role assigned" },
                                    onClick = { onOpenTeacher(t.id) },
                                    leadingIcon = VIcons.GraduationCap,
                                    trailingText = "${t.workload.totalClasses} classes",
                                )
                            }
                        }
                    }
                }
                PeopleTabPremium.Students -> {
                    val filtered = studentsState.students.filter { s ->
                        query.isBlank() || s.fullName.contains(query, ignoreCase = true) || s.className.contains(query, ignoreCase = true)
                    }
                    VStateHostPremium(
                        loading = studentsState.isLoading,
                        error = studentsState.error,
                        isEmpty = filtered.isEmpty(),
                        emptyTitle = if (studentsState.students.isEmpty()) "No students yet" else "No matches",
                        skeleton = { VShimmerListPremium(itemCount = 5) },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filtered.forEach { s ->
                                VListTilePremium(
                                    title = s.fullName,
                                    subtitle = "${s.className} - ${s.section}",
                                    onClick = { onOpenStudent(s.id) },
                                    leadingIcon = VIcons.Users,
                                )
                            }
                        }
                    }
                }
                PeopleTabPremium.Staff -> {
                    val filtered = staffState.staff.filter { s ->
                        query.isBlank() || s.fullName.contains(query, ignoreCase = true) || s.role.contains(query, ignoreCase = true)
                    }
                    VStateHostPremium(
                        loading = staffState.isLoading,
                        error = staffState.error,
                        isEmpty = filtered.isEmpty(),
                        emptyTitle = if (staffState.staff.isEmpty()) "No staff yet" else "No matches",
                        skeleton = { VShimmerListPremium(itemCount = 5) },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filtered.forEach { s ->
                                VListTilePremium(
                                    title = s.fullName,
                                    subtitle = s.role.ifBlank { "No role" },
                                    onClick = { onOpenStaff(s.id) },
                                    leadingIcon = VIcons.Users,
                                )
                            }
                        }
                    }
                }
                PeopleTabPremium.Alumni -> {
                    VListTilePremium(
                        title = "Alumni Management",
                        subtitle = "View and manage graduated students",
                        onClick = onOpenAlumni,
                        leadingIcon = VIcons.Users,
                    )
                }
            }
        }
    }
}
