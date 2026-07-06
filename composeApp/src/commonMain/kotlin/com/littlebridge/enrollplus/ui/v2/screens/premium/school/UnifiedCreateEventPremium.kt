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
import com.littlebridge.enrollplus.feature.admin.presentation.UnifiedCreateEventViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UnifiedCreateEventPremium(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UnifiedCreateEventViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(
            title = "Create Event",
            onBack = { if (state.step > 1) viewModel.back() else onBack() },
        )

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VGradientHeroPremium(
                title = "Create Event",
                subtitle = "Step ${state.step} of ${state.totalSteps}",
                stats = listOf(
                    HeroStatPremium("${state.step}", "Step"),
                    HeroStatPremium("${state.totalSteps}", "Total"),
                    HeroStatPremium(state.form.type, "Type"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (state.step) {
                    1 -> {
                        Text("What", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        VListTilePremium(
                            title = "Event Type",
                            subtitle = state.form.type,
                            onClick = {},
                            leadingIcon = VIcons.Bookmark,
                        )
                        VListTilePremium(
                            title = "Title",
                            subtitle = state.form.title.ifBlank { "Enter event title" },
                            onClick = {},
                            leadingIcon = VIcons.Edit3,
                        )
                        VListTilePremium(
                            title = "Description",
                            subtitle = state.form.description.ifBlank { "Add description" },
                            onClick = {},
                            leadingIcon = VIcons.FileText,
                        )
                    }
                    2 -> {
                        Text("When", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        VListTilePremium(
                            title = "Date",
                            subtitle = state.form.date,
                            onClick = {},
                            leadingIcon = VIcons.Calendar,
                        )
                        VListTilePremium(
                            title = "Schedule",
                            subtitle = if (state.form.isScheduled) "Scheduled for later" else "Post immediately",
                            onClick = {},
                            leadingIcon = VIcons.Clock,
                        )
                    }
                    3 -> {
                        Text("Who", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        VListTilePremium(
                            title = "Audience",
                            subtitle = state.form.audienceType,
                            onClick = {},
                            leadingIcon = VIcons.Users,
                        )
                        VListTilePremium(
                            title = "Post as Announcement",
                            subtitle = if (state.form.postAsAnnouncement) "Visible to parents" else "Calendar only",
                            onClick = {},
                            leadingIcon = VIcons.Sparkles,
                        )
                    }
                }

                state.errorMessage?.let {
                    Text(it, style = VTypography.HeroSubtitle.copy(color = VColors.Error))
                }
            }
        }
    }
}
