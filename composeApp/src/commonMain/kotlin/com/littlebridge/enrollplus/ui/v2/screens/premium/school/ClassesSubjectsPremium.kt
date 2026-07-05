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
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolClassDto
import com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClassesSubjectsPremium(
    onBack: () -> Unit,
    onOpenClassDetail: (SchoolClassDto) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ClassesSubjectsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) {
        viewModel.loadClasses()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Classes & Subjects", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.classes.isEmpty() && state.subjectsByClass.isEmpty() && !state.isLoading,
            emptyTitle = "No classes or subjects",
            onRetry = { viewModel.loadClasses() },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 4)
                }
            },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = "Classes & Subjects",
                    subtitle = "${state.classes.size} classes | ${state.subjectsByClass.size} subject entries",
                    stats = listOf(
                        HeroStatPremium("${state.classes.size}", "Classes"),
                        HeroStatPremium("${state.subjectsByClass.size}", "Subjects"),
                        HeroStatPremium("${state.teachers.size}", "Teachers"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Classes", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.classes.forEach { c ->
                        VListTilePremium(
                            title = c.name,
                            subtitle = "${c.sections.size} sections | ${c.subjectCount} subjects",
                            onClick = { onOpenClassDetail(c) },
                            leadingIcon = VIcons.Users,
                        )
                    }

                    Text("Subjects by Class", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.subjectsByClass.forEach { (classId, subjects) ->
                        VListTilePremium(
                            title = "Class $classId",
                            subtitle = "${subjects.size} subjects: ${subjects.joinToString(", ") { it.name }}",
                            onClick = { viewModel.loadSubjects(classId) },
                            leadingIcon = VIcons.BookOpen,
                        )
                    }
                }
            }
        }
    }
}
