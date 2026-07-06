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
import com.littlebridge.enrollplus.feature.alumni.presentation.AlumniViewModel
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
fun AlumniCampaignPremium(
    campaignId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlumniViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(campaignId) { viewModel.loadCampaignDetail(campaignId) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Campaign Detail", onBack = onBack)

        VStateHostPremium(
            loading = state.isCampaignLoading,
            error = state.error,
            isEmpty = state.selectedCampaign == null && !state.isCampaignLoading,
            emptyTitle = "Campaign not found",
            onRetry = { viewModel.loadCampaignDetail(campaignId) },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 3)
                }
            },
        ) {
            val c = state.selectedCampaign ?: return@VStateHostPremium
            val progress = if (c.targetAmount > 0) "${(c.amountRaised / c.targetAmount * 100).toInt()}%" else "—"

            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = c.title,
                    subtitle = c.description ?: c.cause ?: "",
                    stats = listOf(
                        HeroStatPremium("${c.amountRaised}", "Raised"),
                        HeroStatPremium("${c.targetAmount}", "Goal"),
                        HeroStatPremium(progress, "Progress"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Campaign Info", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = "Status", subtitle = c.status, onClick = {}, leadingIcon = VIcons.Activity)
                    VListTilePremium(title = "Start Date", subtitle = c.startDate, onClick = {}, leadingIcon = VIcons.Calendar)
                    c.endDate?.let { VListTilePremium(title = "End Date", subtitle = it, onClick = {}, leadingIcon = VIcons.Calendar) }
                    c.targetBatchYear?.let { VListTilePremium(title = "Target Batch", subtitle = "${it}", onClick = {}, leadingIcon = VIcons.Users) }

                    Text("Donations (${state.campaignDonations.size})", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.campaignDonations.forEach { d ->
                        VListTilePremium(
                            title = d.alumniName,
                            subtitle = "${d.amount} | ${d.donationDate}",
                            onClick = {},
                            leadingIcon = VIcons.Heart,
                        )
                    }
                }
            }
        }
    }
}
