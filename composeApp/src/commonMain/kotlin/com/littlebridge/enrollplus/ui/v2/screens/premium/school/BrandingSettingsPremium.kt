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
import com.littlebridge.enrollplus.feature.branding.presentation.BrandingViewModel
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
fun BrandingSettingsPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BrandingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.loadBranding() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Branding Settings", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.branding == null && !state.isLoading,
            emptyTitle = "No branding data",
            onRetry = { viewModel.loadBranding() },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 3)
                }
            },
        ) {
            val b = state.branding ?: return@VStateHostPremium

            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = b.schoolName,
                    subtitle = "Branding & Theme",
                    stats = listOf(
                        HeroStatPremium(b.primaryColor, "Primary"),
                        HeroStatPremium(b.secondaryColor, "Secondary"),
                        HeroStatPremium(b.customSubdomain ?: "—", "Subdomain"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Theme Colors", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = "Primary Color", subtitle = b.primaryColor, onClick = {}, leadingIcon = VIcons.Settings)
                    VListTilePremium(title = "Secondary Color", subtitle = b.secondaryColor, onClick = {}, leadingIcon = VIcons.Settings)
                    VListTilePremium(title = "Accent Color", subtitle = b.accentColor, onClick = {}, leadingIcon = VIcons.Settings)

                    Text("Assets", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(title = "Logo", subtitle = b.logoUrl ?: "Not uploaded", onClick = {}, leadingIcon = VIcons.FileText)
                    VListTilePremium(title = "Favicon", subtitle = b.faviconUrl ?: "Not uploaded", onClick = {}, leadingIcon = VIcons.FileText)
                    VListTilePremium(title = "App Icon", subtitle = b.appIconUrl ?: "Not uploaded", onClick = {}, leadingIcon = VIcons.FileText)
                    VListTilePremium(title = "Splash Screen", subtitle = b.splashScreenUrl ?: "Not uploaded", onClick = {}, leadingIcon = VIcons.FileText)

                    Text("Subdomain", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VListTilePremium(
                        title = b.customSubdomain ?: "No subdomain",
                        subtitle = if (b.customSubdomain != null) "Custom subdomain active" else "Assign a custom subdomain",
                        onClick = {},
                        leadingIcon = VIcons.Share,
                    )
                }
            }
        }
    }
}
