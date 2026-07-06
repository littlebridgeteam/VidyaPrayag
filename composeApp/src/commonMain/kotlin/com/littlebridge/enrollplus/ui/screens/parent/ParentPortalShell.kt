package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VMotion

@Composable
fun ParentPortalScreen(
    viewModel: ParentViewModel,
    onLogout: () -> Unit = {},
    initialDeepLink: ParentDeepLink? = null,
) {
    LaunchedEffect(Unit) {
        viewModel.loadAll()
    }

    val dashboardState by viewModel.dashboardState.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val notificationsState by viewModel.notificationsState.collectAsState()
    val isUnlinked by viewModel.isUnlinked.collectAsState()

    val notificationCount = when (val s = notificationsState) {
        is UiState.Success -> s.data.unreadCount
        else -> 0
    }

    var selectedTab by rememberSaveable { mutableStateOf(initialDeepLink?.tab ?: ParentTab.Home) }
    var activeOverlay by rememberSaveable { mutableStateOf<ParentOverlay?>(initialDeepLink?.overlay) }

    LaunchedEffect(initialDeepLink) {
        initialDeepLink?.let { dl ->
            selectedTab = dl.tab
            activeOverlay = dl.overlay
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        if (isUnlinked) {
            UnlinkedParentGate(
                onLinkChild = { activeOverlay = ParentOverlay.LinkChild },
                onDiscoverSchools = { activeOverlay = ParentOverlay.Discovery },
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VColors.cream),
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(tween(VMotion.durDefault)) togetherWith fadeOut(tween(VMotion.durDefault))
                    },
                    label = "parentTab",
                    modifier = Modifier.weight(1f),
                ) { tab ->
                    when (tab) {
                        ParentTab.Home -> ParentHomeTab(
                            viewModel = viewModel,
                            onOverlayOpen = { activeOverlay = it },
                            onTabSwitch = { selectedTab = it },
                        )
                        ParentTab.Academics -> ParentAcademicsTab(
                            viewModel = viewModel,
                            onOverlayOpen = { activeOverlay = it },
                        )
                        ParentTab.Fees -> ParentFeesTab(
                            viewModel = viewModel,
                            onPayClick = { activeOverlay = ParentOverlay.Leave },
                        )
                        ParentTab.Conversations -> ParentConversationsTab(
                            viewModel = viewModel,
                            onThreadClick = { threadId ->
                                viewModel.loadThreadMessages(threadId)
                                activeOverlay = ParentOverlay.TutorChat
                            },
                        )
                        ParentTab.Profile -> ParentProfileTab(
                            viewModel = viewModel,
                            onSettingsClick = { activeOverlay = ParentOverlay.AccountSettings },
                            onLinkChildClick = { activeOverlay = ParentOverlay.LinkChild },
                            onDiscoverSchoolsClick = { activeOverlay = ParentOverlay.Discovery },
                            onLogout = onLogout,
                        )
                    }
                }

                ParentBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    unreadCount = unreadCount,
                    notificationCount = notificationCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                )
            }
        }

        // Overlay host
        val selectedChildId = viewModel.selectedChildId.collectAsState().value
        LaunchedEffect(activeOverlay, selectedChildId) {
            if (selectedChildId != null) {
                when (activeOverlay) {
                    ParentOverlay.Health -> viewModel.loadHealth(selectedChildId)
                    ParentOverlay.Transport -> { viewModel.loadTransportLive(selectedChildId); viewModel.loadTransportRoute(selectedChildId) }
                    ParentOverlay.DigitalIdCard -> viewModel.loadIdCard(selectedChildId)
                    ParentOverlay.Pulse -> viewModel.loadPulse(selectedChildId)
                    ParentOverlay.Leave -> viewModel.loadLeaveRequests()
                    ParentOverlay.Events -> viewModel.loadEvents()
                    ParentOverlay.Scholarships -> viewModel.loadScholarships()
                    ParentOverlay.Calendar -> viewModel.loadTimetable(selectedChildId)
                    ParentOverlay.TutorChat -> viewModel.loadTutorSubjects(selectedChildId)
                    ParentOverlay.TutorProgress -> viewModel.loadTutorSubjects(selectedChildId)
                    ParentOverlay.Library -> viewModel.loadLibraryIssued(selectedChildId)
                    else -> {}
                }
            }
            when (activeOverlay) {
                ParentOverlay.SchoolDetail -> viewModel.discoverSchools()
                else -> {}
            }
        }
        ParentOverlayContainer(
            visible = activeOverlay != null,
            onDismiss = { activeOverlay = null },
            title = activeOverlay?.title ?: "",
        ) {
            when (activeOverlay) {
                ParentOverlay.Notifications -> NotificationsOverlay(viewModel)
                ParentOverlay.Calendar -> CalendarOverlay(viewModel)
                ParentOverlay.Scholarships -> ScholarshipsOverlay(viewModel)
                ParentOverlay.AccountSettings -> AccountSettingsOverlay()
                ParentOverlay.Leave -> selectedChildId?.let { LeaveOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.Discovery -> DiscoveryOverlay(viewModel)
                ParentOverlay.SchoolDetail -> SchoolDetailOverlay(viewModel)
                ParentOverlay.Health -> selectedChildId?.let { HealthOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.Pulse -> selectedChildId?.let { PulseOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.Transport -> selectedChildId?.let { TransportOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.TutorChat -> selectedChildId?.let { TutorChatOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.TutorProgress -> selectedChildId?.let { TutorProgressOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.DigitalIdCard -> selectedChildId?.let { DigitalIdOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.Library -> selectedChildId?.let { LibraryOverlay(viewModel, it) } ?: OvLoading()
                ParentOverlay.Events -> EventsOverlay(viewModel)
                ParentOverlay.LinkChild -> LinkChildOverlay(viewModel) { activeOverlay = null }
                null -> {}
            }
        }
    }
}

@Composable
private fun ParentBottomNav(
    selectedTab: ParentTab,
    onTabSelected: (ParentTab) -> Unit,
    unreadCount: Int,
    notificationCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(VColors.surfaceCard)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        ParentTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val iconColor = if (isSelected) VColors.violet else VColors.ink3
            val labelColor = if (isSelected) VColors.violet else VColors.ink3
            val labelWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium

            val badgeCount = when (tab) {
                ParentTab.Conversations -> unreadCount
                ParentTab.Home -> notificationCount
                else -> 0
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onTabSelected(tab) }
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isSelected) VColors.violetSoft else Color.Transparent,
                                VShapes.sm,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    if (badgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(17.dp)
                                .background(VColors.coral, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = badgeCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = VColors.white,
                            )
                        }
                    }
                }
                Text(
                    text = tab.label,
                    fontSize = 11.sp,
                    fontWeight = labelWeight,
                    color = labelColor,
                )
            }
        }
    }
}

