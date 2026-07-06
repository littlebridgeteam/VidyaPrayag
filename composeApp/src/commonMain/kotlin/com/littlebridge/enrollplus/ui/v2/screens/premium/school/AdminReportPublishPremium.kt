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
import com.littlebridge.enrollplus.feature.reportcard.presentation.AdminReportPublishViewModel
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
fun AdminReportPublishPremium(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminReportPublishViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var term by remember { mutableStateOf("Term 1") }
    LaunchedEffect(term) { viewModel.loadOversight(term) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Report Publishing", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.oversight == null && !state.isLoading,
            emptyTitle = "No oversight data",
            onRetry = { viewModel.loadOversight(term) },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 4)
                }
            },
        ) {
            val oversight = state.oversight ?: return@VStateHostPremium

            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = "Report Card Publishing",
                    subtitle = "Term: ${state.term}",
                    stats = listOf(
                        HeroStatPremium("${oversight.classes.size}", "Classes"),
                        HeroStatPremium("${oversight.classes.sumOf { it.publishedCount }}", "Published"),
                        HeroStatPremium("${oversight.classes.sumOf { it.draftCount }}", "Drafts"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Classes", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    oversight.classes.forEach { c ->
                        VListTilePremium(
                            title = "${c.className} - ${c.section}",
                            subtitle = "Drafts: ${c.draftCount} | Approved: ${c.approvedCount} | Published: ${c.publishedCount} | Flagged: ${c.flaggedCount}",
                            onClick = { viewModel.publishClass(c.className, c.section, state.term) },
                            leadingIcon = VIcons.ClipboardList,
                            trailingText = if (c.publishedCount > 0) "Published" else "Pending",
                        )
                    }
                }
            }
        }
    }
}
