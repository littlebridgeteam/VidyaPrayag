package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.presentation.FeeAnnouncement
import com.littlebridge.enrollplus.feature.parent.presentation.FeeState
import com.littlebridge.enrollplus.feature.parent.presentation.FeeViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

/**
 * ParentFeesScreenV2 — premium Fees tab matching the Academics tab design language.
 *
 * Cream base, white surface cards with subtle borders, violet accent, inline loading/error/empty
 * states, and quick-action chips wired to the real [FeeViewModel].
 */
@Composable
fun ParentFeesScreenV2(
    modifier: Modifier = Modifier,
    parentName: String = "",
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    onPayNow: () -> Unit = {},
    onFeeHistory: () -> Unit = {},
    viewModel: FeeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentFeesContent(
        state = state,
        parentName = parentName,
        children = children,
        selectedChild = selectedChild,
        onSelectChild = onSelectChild,
        onOpenNotifications = onOpenNotifications,
        unreadNotificationsCount = unreadNotificationsCount,
        onPayNow = onPayNow,
        onFeeHistory = onFeeHistory,
        modifier = modifier,
    )
}

/** Stateless body — also used by the @Preview with seeded state (no MockV2 in the live path). */
@Composable
private fun ParentFeesContent(
    state: FeeState,
    parentName: String,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    onPayNow: () -> Unit,
    onFeeHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEmpty = state.announcements.isEmpty() &&
        state.outstandingFees.isBlank() &&
        state.totalCollected.isBlank()

    Column(
        modifier
            .fillMaxSize()
            .background(VTheme.colors.cream)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 130.dp),
    ) {
        PortalTopHeader(
            parentName = parentName,
            childName = selectedChild?.name?.ifBlank { null } ?: "Your Child",
            children = children,
            selectedChild = selectedChild,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            unreadNotificationsCount = unreadNotificationsCount,
        )

        PortalQuickActionChips(
            chips = listOf(
                QuickActionChipSpec(
                    icon = Icons.Filled.Payment,
                    iconColor = VTheme.colors.violet,
                    iconBg = VTheme.colors.violetSoft,
                    title = "Pay\nNow",
                    onClick = onPayNow,
                ),
                QuickActionChipSpec(
                    icon = Icons.Filled.History,
                    iconColor = VTheme.colors.success,
                    iconBg = VTheme.colors.successSoft,
                    title = "Fee\nHistory",
                    onClick = onFeeHistory,
                ),
            ),
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                PortalTabChip(
                    label = "Overview",
                    selected = true,
                    onClick = { },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading && isEmpty ->
                Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VTheme.colors.violet, modifier = Modifier.size(36.dp))
                }

            state.error != null && isEmpty ->
                FeeStateCard(
                    title = "Couldn't load fees",
                    body = state.error ?: "",
                    icon = VIcons.Wallet,
                )

            isEmpty ->
                FeeStateCard(
                    title = "No fee records yet",
                    body = "Once your school publishes fees, they'll appear here.",
                    icon = VIcons.Wallet,
                )

            else ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        "Fee Overview",
                        style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                        color = VTheme.colors.ink,
                    )

                    // ── Balance + collection card ──────────────────────────────────────
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(VTheme.colors.surfaceCard)
                            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
                            .padding(20.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column {
                                Text(
                                    "Balance due",
                                    style = VTheme.type.caption,
                                    color = VTheme.colors.ink3,
                                )
                                Text(
                                    state.outstandingFees,
                                    style = VTheme.type.h2.copy(fontSize = 28.sp),
                                    color = VTheme.colors.ink,
                                )
                                if (state.overdueCount > 0) {
                                    Spacer(Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Box(Modifier.size(6.dp).clip(CircleShape).background(VTheme.colors.error))
                                        Text(
                                            "${state.overdueCount} overdue",
                                            style = VTheme.type.caption,
                                            color = VTheme.colors.error,
                                        )
                                    }
                                }
                            }
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(VTheme.colors.violetSoft)
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    "Pay Now",
                                    style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold),
                                    color = VTheme.colors.violet,
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "Collected this term",
                                style = VTheme.type.caption,
                                color = VTheme.colors.ink3,
                            )
                            Text(
                                state.totalCollected,
                                style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                                color = VTheme.colors.success,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        CollectionProgressBar(state.collectionProgress)
                    }

                    // ── Fee announcements ────────────────────────────────────────────
                    if (state.announcements.isNotEmpty()) {
                        Text(
                            "Fee notices",
                            style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                            color = VTheme.colors.ink,
                        )
                        state.announcements.forEach { a ->
                            FeeAnnouncementCard(a)
                        }
                    }
                } // end content Column
        }
    }
}

@Composable
private fun CollectionProgressBar(progress: Float) {
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(VTheme.colors.line),
    ) {
        Box(
            Modifier
                .fillMaxWidth(clamped)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(VTheme.colors.violet),
        )
    }
}

@Composable
private fun FeeAnnouncementCard(a: FeeAnnouncement) {
    val badgeColor = when (a.type) {
        "Emergency" -> VTheme.colors.error to VTheme.colors.errorSoft
        "Payment" -> VTheme.colors.gold to VTheme.colors.goldSoft
        else -> VTheme.colors.ink3 to VTheme.colors.creamDeep
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                a.title,
                style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                color = VTheme.colors.ink,
            )
            Text(
                "${a.description} • ${a.time}",
                style = VTheme.type.caption,
                color = VTheme.colors.ink2,
            )
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(badgeColor.second)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                a.type,
                style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = badgeColor.first,
            )
        }
    }
}

@Composable
private fun FeeStateCard(
    title: String,
    body: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(56.dp).clip(CircleShape).background(VTheme.colors.creamDeep),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(title, style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink)
        Spacer(Modifier.height(4.dp))
        Text(body, style = VTheme.type.caption, color = VTheme.colors.ink2)
    }
}
