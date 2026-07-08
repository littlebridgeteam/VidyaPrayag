package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons

@Composable
fun PortalTopHeader(
    parentName: String,
    childName: String,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    modifier: Modifier = Modifier,
    showGreeting: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = if (showGreeting) 18.dp else 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Enroll+",
                style = VTypography.wordmark.copy(
                    fontSize = 22.sp,
                    color = VColors.violet,
                ),
                fontWeight = FontWeight.ExtraBold,
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(VShapes.full)
                    .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = VIcons.BellStroke,
                    contentDescription = "Notifications",
                    tint = VColors.ink,
                    modifier = Modifier.size(24.dp),
                )
                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(VColors.error)
                            .border(2.dp, VColors.cream, CircleShape),
                    )
                }
            }
        }

        if (showGreeting) {
            Spacer(Modifier.height(18.dp))

            Text(
                text = "Hi ${parentName.takeWhile { it != ' ' }.ifBlank { parentName }}",
                style = VTypography.caption.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = VColors.violet,
            )

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = children.size > 1,
                ) { expanded = true },
            ) {
                Text(
                    text = "here's",
                    style = VTypography.h2.copy(fontSize = 28.sp),
                    color = VColors.ink,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "$childName's",
                    style = VTypography.h2.copy(fontSize = 28.sp),
                    color = VColors.violet,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "day",
                    style = VTypography.h2.copy(fontSize = 28.sp),
                    color = VColors.ink,
                    fontWeight = FontWeight.ExtraBold,
                )
                if (children.size > 1) {
                    Icon(
                        imageVector = VIcons.ChevronDown,
                        contentDescription = "Select child",
                        tint = VColors.ink3,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = VColors.white,
            shape = VShapes.lg,
        ) {
            children.forEach { child ->
                DropdownMenuItem(
                    text = {
                        Text(
                            child.name,
                            style = VTypography.body,
                            color = if (child.id == selectedChild?.id) VColors.violet else VColors.ink,
                            fontWeight = if (child.id == selectedChild?.id) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onSelectChild(child.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun PortalTopHeaderMinimal(
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                onBack?.let { onBackClick ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(VShapes.full)
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = VIcons.ArrowLeft,
                            contentDescription = "Back",
                            tint = VColors.ink,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                Text(
                    text = "Enroll+",
                    style = VTypography.wordmark.copy(
                        fontSize = 22.sp,
                        color = VColors.violet,
                    ),
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(VShapes.full)
                    .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = VIcons.BellStroke,
                    contentDescription = "Notifications",
                    tint = VColors.ink,
                    modifier = Modifier.size(24.dp),
                )
                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(VColors.error)
                            .border(2.dp, VColors.cream, CircleShape),
                    )
                }
            }
        }
    }
}
