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
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolProfileViewModel
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
fun EditSchoolProfilePremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SchoolProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Institutional Profile", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.loadError,
            isEmpty = state.name.isBlank() && !state.isLoading,
            emptyTitle = "No profile data",
            onRetry = { viewModel.load() },
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
                    title = state.name.ifBlank { "School Profile" },
                    subtitle = "${state.board} | ${state.medium}",
                    stats = listOf(
                        HeroStatPremium(state.schoolGender, "Type"),
                        HeroStatPremium(state.city, "City"),
                        HeroStatPremium(state.pincode, "Pincode"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Contact", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = "Phone", subtitle = state.contactPhone, onClick = {}, leadingIcon = VIcons.Phone)
                    VListTilePremium(title = "Email", subtitle = state.contactEmail, onClick = {}, leadingIcon = VIcons.Mail)

                    Text("Principal", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = state.principalName, subtitle = state.principalPhone, onClick = {}, leadingIcon = VIcons.Users)
                    VListTilePremium(title = "Principal Email", subtitle = state.principalEmail, onClick = {}, leadingIcon = VIcons.Mail)

                    Text("Address", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = state.fullAddress, subtitle = "${state.city}, ${state.district}, ${state.state} - ${state.pincode}", onClick = {}, leadingIcon = VIcons.MapPin)
                }
            }
        }
    }
}
