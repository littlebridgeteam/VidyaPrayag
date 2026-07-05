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
fun AlumniPremium(
    onBack: () -> Unit = {},
    onOpenAlumni: (String) -> Unit = {},
    onOpenCampaign: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlumniViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.loadAlumni() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Alumni", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.alumni.isEmpty() && state.campaigns.isEmpty() && !state.isLoading,
            emptyTitle = "No alumni data",
            onRetry = { viewModel.loadAlumni() },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 5)
                }
            },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = "Alumni Network",
                    subtitle = "${state.alumni.size} alumni | ${state.campaigns.size} campaigns",
                    stats = listOf(
                        HeroStatPremium("${state.alumni.size}", "Alumni"),
                        HeroStatPremium("${state.pendingVerifications.size}", "Pending"),
                        HeroStatPremium("${state.campaigns.size}", "Campaigns"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (state.pendingVerifications.isNotEmpty()) {
                        Text("Pending Verifications", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.pendingVerifications.forEach { a ->
                            VListTilePremium(
                                title = a.name,
                                subtitle = "Batch ${a.graduationYear} | ${a.verificationStatus}",
                                onClick = { onOpenAlumni(a.id) },
                                leadingIcon = VIcons.AlertCircle,
                                trailingText = a.verificationStatus,
                            )
                        }
                    }

                    Text("Alumni Directory", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.alumni.forEach { a ->
                        VListTilePremium(
                            title = a.name,
                            subtitle = "Batch ${a.graduationYear} | ${a.currentProfession ?: "—"} | ${a.company ?: "—"}",
                            onClick = { onOpenAlumni(a.id) },
                            leadingIcon = VIcons.Users,
                            trailingText = if (a.isFeatured) "Featured" else null,
                        )
                    }

                    if (state.campaigns.isNotEmpty()) {
                        Text("Donation Campaigns", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.campaigns.forEach { c ->
                            val progress = if (c.targetAmount > 0) "${(c.amountRaised / c.targetAmount * 100).toInt()}%" else "—"
                            VListTilePremium(
                                title = c.title,
                                subtitle = "${c.amountRaised}/${c.targetAmount} | ${c.donorCount} donors | $progress",
                                onClick = { onOpenCampaign(c.id) },
                                leadingIcon = VIcons.Heart,
                                trailingText = c.status,
                            )
                        }
                    }
                }
            }
        }
    }
}
