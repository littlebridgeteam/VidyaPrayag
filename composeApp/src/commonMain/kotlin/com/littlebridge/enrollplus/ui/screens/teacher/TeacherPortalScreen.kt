package com.littlebridge.enrollplus.ui.screens.teacher

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
// Custom icons from TeacherIcons.kt
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion

enum class TeacherTab(val label: String, val icon: ImageVector) {
    Home("Home", TIHome),
    Update("Update", TIEdit),
    Classes("Classes", TIClasses),
    Timetable("Timetable", TICalendar),
    Profile("Profile", TIUser),
}

@Composable
fun TeacherPortalScreen(
    teacherName: String = "Priya Sharma",
    notificationCount: Int = 3,
    onLogout: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(TeacherTab.Home) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        // Portal Header
        PortalHeader(
            teacherName = teacherName,
            notificationCount = notificationCount,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
        )

        // Tab Content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(tween(VMotion.durDefault)) togetherWith fadeOut(tween(VMotion.durDefault))
            },
            label = "teacherTab",
            modifier = Modifier.weight(1f),
        ) { tab ->
            when (tab) {
                TeacherTab.Home -> TeacherHomeTab(
                    onNavigateTab = { selectedTab = it },
                )
                TeacherTab.Update -> TeacherUpdateTab()
                TeacherTab.Classes -> TeacherClassesTab()
                TeacherTab.Timetable -> TeacherTimetableTab()
                TeacherTab.Profile -> TeacherProfileTab(
                    onLogout = onLogout,
                )
            }
        }

        // Bottom Nav
        TeacherBottomNav(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun PortalHeader(
    teacherName: String,
    notificationCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Good morning",
                style = VTypography.caption.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.3.sp,
                ),
                color = VColors.ink3,
            )
            Text(
                text = teacherName,
                style = VTypography.h2.copy(fontSize = 20.sp),
                color = VColors.ink,
            )
        }
        Box {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(1.dp, CircleShape)
                    .background(VColors.white, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TIBell,
                    contentDescription = "Notifications",
                    tint = VColors.ink2,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(17.dp)
                        .background(VColors.coral, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = notificationCount.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VColors.white,
                    )
                }
            }
        }
    }
}

@Composable
private fun TeacherBottomNav(
    selectedTab: TeacherTab,
    onTabSelected: (TeacherTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(VColors.white)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        TeacherTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val iconColor = if (isSelected) VColors.violet else VColors.ink3
            val labelColor = if (isSelected) VColors.violet else VColors.ink3
            val labelWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium

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
