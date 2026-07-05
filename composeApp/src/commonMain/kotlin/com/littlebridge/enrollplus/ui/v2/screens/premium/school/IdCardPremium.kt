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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun IdCardPremium(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "ID Card Management", onBack = onBack)

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VGradientHeroPremium(
                title = "ID Card Studio",
                subtitle = "Design, generate, and print student & staff ID cards",
                stats = listOf(
                    HeroStatPremium("3", "Templates"),
                    HeroStatPremium("PDF", "Export"),
                    HeroStatPremium("Bulk", "Generate"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Features", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(title = "Card Templates", subtitle = "Choose from pre-designed ID card templates", onClick = {}, leadingIcon = VIcons.ClipboardList)
                VListTilePremium(title = "Generate Cards", subtitle = "Generate ID cards for students and staff in bulk", onClick = {}, leadingIcon = VIcons.Sparkles)
                VListTilePremium(title = "Card Preview", subtitle = "Preview cards before printing with live data", onClick = {}, leadingIcon = VIcons.Eye)
                VListTilePremium(title = "Export to PDF", subtitle = "Export generated cards as print-ready PDF files", onClick = {}, leadingIcon = VIcons.FileText)
                VListTilePremium(title = "Template Editor", subtitle = "Customize templates with school branding and fields", onClick = {}, leadingIcon = VIcons.Settings)
            }
        }
    }
}
