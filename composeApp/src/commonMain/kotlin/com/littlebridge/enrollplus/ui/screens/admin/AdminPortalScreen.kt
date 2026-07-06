package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.presentation.admin.AdminHomeViewModel
import com.littlebridge.enrollplus.presentation.admin.AdminPeopleViewModel
import com.littlebridge.enrollplus.presentation.admin.AdminRecordsViewModel
import com.littlebridge.enrollplus.presentation.admin.AdminCommsViewModel
import com.littlebridge.enrollplus.presentation.admin.AdminSettingsViewModel
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTab
import com.littlebridge.enrollplus.ui.screens.admin.records.RecordsScreen
import com.littlebridge.enrollplus.ui.screens.admin.people.PeopleScreen
import com.littlebridge.enrollplus.ui.screens.admin.settings.SettingsScreen
import com.littlebridge.enrollplus.ui.screens.admin.comms.CommsScreen
import com.littlebridge.enrollplus.ui.screens.admin.comms.HomeScreen

// ═══════════════════════════════════════════════════════════════
// AdminPortalScreen — Root composable for admin portal
// Shell: Row(Sidebar + PhoneWrap → PhoneFrame → ScreenArea)
// ═══════════════════════════════════════════════════════════════

@Composable
fun AdminPortalScreen(
    homeViewModel: AdminHomeViewModel,
    peopleViewModel: AdminPeopleViewModel,
    recordsViewModel: AdminRecordsViewModel,
    commsViewModel: AdminCommsViewModel,
    settingsViewModel: AdminSettingsViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeTab by remember { mutableStateOf(AdminTab.HOME) }
    var showNotifications by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Sidebar — 260dp
        AdminSidebar(
            activeTab = activeTab,
            onTabSelect = { activeTab = it }
        )

        // Phone wrap — center the phone
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            PhoneFrame {
                ScreenArea {
                    // StatusBar
                    StatusBar()

                    // Portal header
                    PortalHeader(
                        schoolName = "Delhi Public School",
                        dashboardTitle = "Admin Dashboard",
                        badgeCount = 5,
                        onBellClick = { showNotifications = true }
                    )

                    // Tab content (scrollable)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp)
                    ) {
                        when (activeTab) {
                            AdminTab.HOME -> HomeScreen(viewModel = homeViewModel)
                            AdminTab.PEOPLE -> PeopleScreen(viewModel = peopleViewModel)
                            AdminTab.RECORDS -> RecordsScreen(viewModel = recordsViewModel)
                            AdminTab.COMMS -> CommsScreen(viewModel = commsViewModel)
                            AdminTab.SETTINGS -> SettingsScreen(viewModel = settingsViewModel, onLogout = onLogout)
                        }
                    }

                    // Bottom nav
                    Box(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        )
                    ) {
                        BottomNav(
                            activeTab = activeTab,
                            onTabSelect = { activeTab = it },
                            commsBadge = 5
                        )
                    }

                    // Overlay (on top of everything)
                    if (showNotifications) {
                        NotificationsOverlay(
                            onBack = { showNotifications = false }
                        )
                    }
                }
            }
        }
    }
}
