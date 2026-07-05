package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayViewModel
import com.littlebridge.enrollplus.ui.v2.components.navigation.NavItem
import com.littlebridge.enrollplus.ui.v2.components.navigation.VBottomNav
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherPortalShell(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    todayViewModel: TeacherTodayViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    var tab by rememberSaveable { mutableStateOf(0) }
    val state by todayViewModel.state.collectAsStateV2()

    val tabs = remember {
        listOf(
            NavItem("Home", icon = { TabIcon(Icons.Filled.Home) }),
            NavItem("Classes", icon = { TabIcon(Icons.Filled.MenuBook) }),
            NavItem("Schedule", icon = { TabIcon(Icons.Filled.CalendarMonth) }),
            NavItem("Profile", icon = { TabIcon(Icons.Filled.Person) }),
        )
    }

    Column(modifier = modifier.fillMaxSize().background(VColors.Surface)) {
        TeacherHeader(
            teacherName = state.teacherName,
            onBellClick = { },
            modifier = Modifier.fillMaxWidth(),
        )

        Box(Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(300)).togetherWith(fadeOut(tween(200))) },
                label = "teacherTab",
            ) { current ->
                when (current) {
                    0 -> TeacherHomeScreen()
                    1 -> TeacherClassesScreen()
                    2 -> TeacherTimetableScreen()
                    3 -> TeacherProfileScreen(onLogout = onLogout)
                }
            }
        }

        VBottomNav(
            items = tabs,
            activeIndex = tab,
            onItemClick = { tab = it },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TeacherHeader(
    teacherName: String,
    onBellClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(VColors.Surface)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                teacherName.firstOrNull()?.toString() ?: "T",
                style = VTypography.SectionHeader.copy(color = VColors.OnPrimaryContainer),
            )
        }
        Column(Modifier.weight(1f)) {
            Text("Welcome back", style = VTypography.Eyebrow.copy(color = VColors.OnSurfaceVariant))
            Text(
                teacherName.ifBlank { "Teacher" },
                style = VTypography.GreetingTitle.copy(color = VColors.OnSurface),
            )
        }
        val bellInteraction = remember { MutableInteractionSource() }
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.SurfaceContainerHigh)
                .pressScale(bellInteraction, pressedScale = 0.9f)
                .clickable(interactionSource = bellInteraction, indication = null, onClick = onBellClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun TabIcon(icon: ImageVector) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
}
