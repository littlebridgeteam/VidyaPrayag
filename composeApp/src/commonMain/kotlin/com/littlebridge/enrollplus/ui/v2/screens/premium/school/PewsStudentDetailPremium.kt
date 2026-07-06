package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.littlebridge.enrollplus.feature.pews.presentation.PewsStudentDetailViewModel
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
fun PewsStudentDetailPremium(
    studentCode: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PewsStudentDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(studentCode) { viewModel.load(studentCode) }

    VStateHostPremium(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.detail?.current == null && !state.isLoading,
        emptyTitle = "Student not found",
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
        val student = state.detail?.current ?: return@VStateHostPremium

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VBackHeader(title = student.name, onBack = onBack)

            VGradientHeroPremium(
                title = student.name,
                subtitle = "${student.className} - ${student.section}",
                stats = listOf(
                    HeroStatPremium("${student.riskScore}", "Risk Score"),
                    HeroStatPremium(student.riskLevel.replaceFirstChar { it.uppercase() }, "Risk Level"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Metrics", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                student.signals.forEach { signal ->
                    VListTilePremium(title = signal.kind, subtitle = signal.label, onClick = {}, leadingIcon = VIcons.AlertCircle)
                }

                Text("Interventions", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                state.interventions.forEach { iv ->
                    VListTilePremium(
                        title = iv.name,
                        subtitle = "${iv.status} - ${iv.openedAt}",
                        onClick = { viewModel.updateIntervention(iv.id, status = "done") },
                        leadingIcon = VIcons.Check,
                        trailingText = iv.status,
                    )
                }

                if (student.aiNarrative != null) {
                    val narrative = student.aiNarrative!!
                    Text("AI Analysis", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VStatCardPremium(value = "AI Insight", label = narrative, onClick = {}, icon = VIcons.Sparkles)
                }
            }
        }
    }
}
