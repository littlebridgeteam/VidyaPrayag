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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherAssignmentViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherAssignmentPremium(
    teacherId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherAssignmentViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(teacherId) { viewModel.load(teacherId) }

    VStateHostPremium(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.overview == null && !state.isLoading,
        emptyTitle = "No assignments found",
        modifier = modifier.fillMaxSize(),
        skeleton = {
            Column(
                Modifier.fillMaxSize().statusBarsPadding().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VShimmerListPremium(itemCount = 5)
            }
        },
    ) {
        val overview = state.overview ?: return@VStateHostPremium
        val summary = overview.summary

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
            VBackHeader(title = "Assignment Management", onBack = onBack)

            VGradientHeroPremium(
                title = summary.teacherName.ifBlank { "Teacher" },
                subtitle = "${summary.classCount} classes - ${summary.studentCount} students",
                stats = listOf(
                    HeroStatPremium("${summary.classCount}", "Classes"),
                    HeroStatPremium("${summary.studentCount}", "Students"),
                    HeroStatPremium("${summary.subjectCount}", "Subjects"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Current Assignments", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                overview.assignments.forEach { a ->
                    VListTilePremium(
                        title = a.subject,
                        subtitle = "${a.className} - ${a.section}",
                        onClick = {},
                        leadingIcon = VIcons.Bookmark,
                        trailingText = "${a.studentCount} students",
                    )
                }

                Text("Insights", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                overview.insights.forEach { insight ->
                    VStatCardPremium(value = "Insight", label = insight, onClick = {}, icon = VIcons.Sparkles)
                }
            }
        }
    }
}
