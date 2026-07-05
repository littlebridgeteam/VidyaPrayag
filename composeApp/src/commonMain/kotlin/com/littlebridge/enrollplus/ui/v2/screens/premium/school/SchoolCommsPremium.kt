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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VTopTabsPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolCommsPremium(
    onOpenMessages: () -> Unit = {},
    onOpenPtm: () -> Unit = {},
    onOpenScheduledMessages: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onCreateEvent: () -> Unit = {},
    viewModel: SchoolAnnouncementsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var tab by remember { mutableStateOf(0) }
    val tabLabels = listOf("Announcements", "Messages", "PTM", "Notifications")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Communications", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface), modifier = Modifier.padding(horizontal = 20.dp))
        VTopTabsPremium(tabs = tabLabels, selected = tabLabels[tab], onSelect = { label -> tab = tabLabels.indexOf(label) })

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (tab) {
                0 -> {
                    VStateHostPremium(
                        loading = state.isLoading,
                        error = state.errorMessage,
                        isEmpty = state.announcements.isEmpty(),
                        emptyTitle = "No announcements yet",
                        skeleton = { VShimmerListPremium(itemCount = 4) },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            state.announcements.forEach { a ->
                                VListTilePremium(
                                    title = a.title,
                                    subtitle = a.category,
                                    onClick = {},
                                    trailingText = a.date,
                                )
                            }
                        }
                    }
                }
                1 -> VListTilePremium(
                    title = "Messages",
                    subtitle = "Open parent messaging threads",
                    onClick = onOpenMessages,
                    leadingIcon = VIcons.Send,
                )
                2 -> VListTilePremium(
                    title = "Schedule PTM",
                    subtitle = "Parent-teacher meeting scheduling",
                    onClick = onOpenPtm,
                    leadingIcon = VIcons.Calendar,
                )
                3 -> VListTilePremium(
                    title = "Notifications",
                    subtitle = "View notification center",
                    onClick = onOpenNotifications,
                    leadingIcon = VIcons.Bell,
                )
            }
        }
    }
}
