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
fun AlumniDetailPremium(
    alumniId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlumniViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(alumniId) { viewModel.loadAlumniDetail(alumniId) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Alumni Detail", onBack = onBack)

        VStateHostPremium(
            loading = state.isDetailLoading,
            error = state.error,
            isEmpty = state.selectedAlumni == null && !state.isDetailLoading,
            emptyTitle = "Alumni not found",
            onRetry = { viewModel.loadAlumniDetail(alumniId) },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 3)
                }
            },
        ) {
            val a = state.selectedAlumni ?: return@VStateHostPremium

            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = a.name,
                    subtitle = "Batch ${a.graduationYear} | ${a.currentProfession ?: "—"}",
                    stats = listOf(
                        HeroStatPremium("${a.graduationYear}", "Batch"),
                        HeroStatPremium(a.verificationStatus, "Status"),
                        HeroStatPremium(if (a.isMentor) "Yes" else "No", "Mentor"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Contact", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = "Email", subtitle = a.email ?: "—", onClick = {}, leadingIcon = VIcons.Mail)
                    VListTilePremium(title = "Phone", subtitle = a.phone ?: "—", onClick = {}, leadingIcon = VIcons.Phone)
                    VListTilePremium(title = "LinkedIn", subtitle = a.linkedinUrl ?: "—", onClick = {}, leadingIcon = VIcons.Share)

                    Text("Professional", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = "Profession", subtitle = a.currentProfession ?: "—", onClick = {}, leadingIcon = VIcons.User)
                    VListTilePremium(title = "Company", subtitle = a.company ?: "—", onClick = {}, leadingIcon = VIcons.School)
                    VListTilePremium(title = "City", subtitle = a.city ?: "—", onClick = {}, leadingIcon = VIcons.MapPin)

                    if (a.isMentor) {
                        Text("Mentorship", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        VListTilePremium(title = "Mentor Expertise", subtitle = a.mentorExpertise ?: "—", onClick = {}, leadingIcon = VIcons.Sparkles)
                    }

                    if (state.selectedAlumniDonations.isNotEmpty()) {
                        Text("Donations", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.selectedAlumniDonations.forEach { d ->
                            VListTilePremium(
                                title = "${d.amount}",
                                subtitle = d.donationDate,
                                onClick = {},
                                leadingIcon = VIcons.Heart,
                            )
                        }
                    }
                }
            }
        }
    }
}
