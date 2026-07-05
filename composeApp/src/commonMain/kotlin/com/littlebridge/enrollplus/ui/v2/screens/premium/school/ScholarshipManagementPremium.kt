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
import com.littlebridge.enrollplus.feature.scholarship.presentation.ScholarshipViewModel
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
fun ScholarshipManagementPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ScholarshipViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.loadSchemes() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Scholarship Management", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.schemes.isEmpty() && state.applications.isEmpty() && !state.isLoading,
            emptyTitle = "No scholarship data",
            onRetry = { viewModel.loadSchemes() },
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
                    title = "Scholarship Management",
                    subtitle = "${state.schemes.size} schemes | ${state.applications.size} applications",
                    stats = listOf(
                        HeroStatPremium("${state.schemes.size}", "Schemes"),
                        HeroStatPremium("${state.applications.size}", "Applications"),
                        HeroStatPremium("${state.renewals.size}", "Renewals"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Scholarship Schemes", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.schemes.forEach { s ->
                        VListTilePremium(
                            title = s.title,
                            subtitle = "${s.amount} | ${s.category} | ${s.eligibilityCriteria}",
                            onClick = {},
                            leadingIcon = VIcons.Star,
                            trailingText = s.scholarshipType,
                        )
                    }

                    if (state.applications.isNotEmpty()) {
                        Text("Applications", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.applications.forEach { a ->
                            VListTilePremium(
                                title = a.studentName ?: "Student",
                                subtitle = "${a.scholarshipTitle ?: "—"} | ${a.institution}",
                                onClick = {},
                                leadingIcon = VIcons.FileText,
                                trailingText = a.status,
                            )
                        }
                    }

                    if (state.renewals.isNotEmpty()) {
                        Text("Renewals", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.renewals.forEach { r ->
                            VListTilePremium(
                                title = r.scholarshipTitle ?: "Scholarship",
                                subtitle = "Status: ${r.status} | Applied: ${r.appliedAt}",
                                onClick = {},
                                leadingIcon = VIcons.Activity,
                                trailingText = r.status,
                            )
                        }
                    }
                }
            }
        }
    }
}
