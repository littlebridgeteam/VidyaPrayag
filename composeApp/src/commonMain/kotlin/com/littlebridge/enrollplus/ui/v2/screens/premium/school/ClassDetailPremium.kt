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
fun ClassDetailPremium(
    classId: String,
    className: String,
    onBack: () -> Unit,
    onOpenStudent: (String) -> Unit = {},
    onOpenTeacher: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = className, onBack = onBack)

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VGradientHeroPremium(
                title = className,
                subtitle = "Class detail overview",
                stats = listOf(
                    HeroStatPremium("—", "Students"),
                    HeroStatPremium("—", "Sections"),
                    HeroStatPremium("—", "Subjects"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Class Info", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(title = "Class ID", subtitle = classId, onClick = {}, leadingIcon = VIcons.Bookmark)
                VListTilePremium(title = "Class Name", subtitle = className, onClick = {}, leadingIcon = VIcons.Users)

                Text("Quick Actions", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(title = "View Students", subtitle = "Open student roster", onClick = { onOpenStudent("") }, leadingIcon = VIcons.Users)
                VListTilePremium(title = "View Teachers", subtitle = "Open teacher list", onClick = { onOpenTeacher("") }, leadingIcon = VIcons.User)
            }
        }
    }
}
