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
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.components.overlay.VConfirmDialogPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherProfilePremium(
    teacherId: String,
    onBack: () -> Unit = {},
    onRemoved: () -> Unit = onBack,
    onOpenAssignments: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(teacherId) { viewModel.load(teacherId) }
    LaunchedEffect(state.removed) { if (state.removed) onRemoved() }

    var showDelete by remember { mutableStateOf(false) }

    VStateHostPremium(
        loading = state.isLoading,
        error = state.error,
        isEmpty = false,
        modifier = modifier.fillMaxSize(),
        skeleton = {
            Column(
                Modifier.fillMaxSize().statusBarsPadding().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VShimmerListPremium(itemCount = 6)
            }
        },
    ) {
        val profile = state.profile ?: return@VStateHostPremium

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VBackHeader(title = profile.name, onBack = onBack)

            VGradientHeroPremium(
                title = profile.name,
                subtitle = profile.role.ifBlank { "Teacher" },
                stats = listOf(
                    HeroStatPremium("${profile.classCount}", "Classes"),
                    HeroStatPremium("${profile.studentCount}", "Students"),
                    HeroStatPremium("${profile.attendancePercent.toInt()}%", "Attendance"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Performance", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VStatCardPremium(value = "${profile.attendancePercent.toInt()}%", label = "Attendance Rate", onClick = {})
                VStatCardPremium(value = "${profile.assignmentCompletionPercent.toInt()}%", label = "Assignment Completion", onClick = {})

                Text("Assignments", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(
                    title = "Manage Assignments",
                    subtitle = "Classes, subjects & sections",
                    onClick = onOpenAssignments,
                    leadingIcon = VIcons.Bookmark,
                )

                Text("Activity", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                profile.recentActivities.forEach { a ->
                    VListTilePremium(title = a.title, subtitle = a.createdAt, onClick = {}, leadingIcon = VIcons.Clock)
                }

                Text("Achievements", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                profile.achievements.forEach { ach ->
                    VListTilePremium(title = ach.title, subtitle = ach.description, onClick = {}, leadingIcon = VIcons.Star)
                }

                VListTilePremium(
                    title = "Remove Teacher",
                    subtitle = "Soft-delete this teacher record",
                    onClick = { showDelete = true },
                    leadingIcon = VIcons.Close,
                )
            }
        }
    }

    VConfirmDialogPremium(
        visible = showDelete,
        title = "Remove Teacher",
        message = "Are you sure you want to remove ${state.profile?.name ?: "this teacher"}? This is a soft-delete.",
        confirmLabel = "Remove",
        onConfirm = { viewModel.remove(teacherId); showDelete = false },
        onDismiss = { showDelete = false },
        isDestructive = true,
    )
}
