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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Shared premium portal header used by Home, Academics, Fees, Conversations and Profile tabs.
 * Matches the screenshot: uppercase label, child-name dropdown with real children, notification bell.
 */
@Composable
fun ParentPortalHeader(
    label: String,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
) {
    var expanded by remember { mutableStateOf(false) }
    val childName = selectedChild?.name?.ifBlank { null } ?: "Your Child"

    Column(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 14.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    label.uppercase(),
                    style = VTheme.type.caption.copy(
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                    ),
                    color = VTheme.colors.ink3,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = children.size > 1,
                    ) { expanded = true },
                ) {
                    Text(
                        childName,
                        style = VTheme.type.h2.copy(fontSize = 20.sp),
                        color = VTheme.colors.ink,
                        fontWeight = FontWeight.Bold,
                    )
                    if (children.size > 1) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Select child",
                            tint = VTheme.colors.ink3,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = VTheme.colors.white,
                    shape = RoundedCornerShape(18.dp),
                ) {
                    children.forEach { child ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    child.name,
                                    style = VTheme.type.body,
                                    color = if (child.id == selectedChild?.id) VTheme.colors.violet else VTheme.colors.ink,
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

            // Notification bell with badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(VTheme.colors.white)
                    .border(1.dp, VTheme.colors.line, RoundedCornerShape(50))
                    .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = VTheme.colors.ink,
                    modifier = Modifier.size(20.dp),
                )
                if (unreadNotificationsCount > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(VTheme.colors.error),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            unreadNotificationsCount.coerceAtMost(99).toString(),
                            style = VTheme.type.caption.copy(fontSize = 9.sp),
                            color = VTheme.colors.white,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PortalQuickActionChips(
    chips: List<QuickActionChipSpec>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        chips.forEach { chip ->
            PortalQuickActionChip(
                icon = chip.icon,
                iconColor = chip.iconColor,
                iconBg = chip.iconBg,
                title = chip.title,
                onClick = chip.onClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

data class QuickActionChipSpec(
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color,
    val title: String,
    val onClick: () -> Unit,
)

@Composable
fun PortalQuickActionChip(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.white)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = "", tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Text(
            title,
            style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, lineHeight = 16.sp),
            color = VTheme.colors.ink,
        )
    }
}

@Composable
fun PortalTabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) VTheme.colors.white else VTheme.colors.surfaceTint
    val fg = if (selected) VTheme.colors.ink else VTheme.colors.ink3

    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        color = fg,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .border(1.dp, if (selected) VTheme.colors.line else VTheme.colors.lineSoft, RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/**
 * Shared premium header for full-screen parent overlays.
 * Circular bordered back button, bold title, optional trailing action.
 */
@Composable
fun PremiumOverlayHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(VTheme.colors.surfaceCard)
                        .border(1.dp, VTheme.colors.line, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = VTheme.colors.ink,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    title,
                    style = VTheme.type.body.copy(fontWeight = FontWeight.Bold),
                    color = VTheme.colors.ink,
                )
            }
            if (action != null) {
                Box(Modifier.height(40.dp).wrapContentWidth(), contentAlignment = Alignment.Center) {
                    action()
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(VTheme.colors.line).padding(horizontal = 24.dp))
    }
}

/**
 * Premium surface card used across parent overlays.
 * White card with a subtle 1dp border ΓÇö matches the Academics/Fees design language.
 */
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    var m = modifier
        .fillMaxWidth()
        .clip(shape)
        .background(VTheme.colors.surfaceCard)
        .border(1.dp, VTheme.colors.line, shape)
    if (onClick != null) {
        m = m.clickable(onClick = onClick)
    }
    Box(m.padding(padding)) {
        content()
    }
}
