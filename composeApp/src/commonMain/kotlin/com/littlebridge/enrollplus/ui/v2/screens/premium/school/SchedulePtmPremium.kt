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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.SchedulePTMViewModel
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
fun SchedulePtmPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SchedulePTMViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Parent-Teacher Meetings", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.history.isEmpty() && state.activeEventTitle.isBlank() && !state.isLoading,
            emptyTitle = "No PTM data",
            onRetry = { viewModel.loadPtm() },
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
                    title = state.activeEventTitle.ifBlank { "No Active PTM" },
                    subtitle = "${state.activeEventDate} - ${state.activeEventSlot}",
                    stats = listOf(
                        HeroStatPremium("${state.checkedInParents}/${state.expectedParents}", "Checked In"),
                        HeroStatPremium("${state.invitesDelivered}", "Invites"),
                        HeroStatPremium("${state.readReceipts}", "Read"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("History", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.history.forEach { h ->
                        VListTilePremium(
                            title = h.title,
                            subtitle = "${h.date} | Turnout: ${h.turnout}/${h.totalMet}",
                            onClick = {},
                            leadingIcon = VIcons.Calendar,
                        )
                    }

                    Text("Class Progress", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.classProgress.forEach { c ->
                        VStatCardPremium(
                            value = "${c.metCount}/${c.totalCount}",
                            label = "${c.className} - ${c.teacherName}",
                            onClick = {},
                            icon = VIcons.Users,
                        )
                    }
                }
            }
        }
    }
}
