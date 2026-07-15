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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentFeeItemDto
import com.littlebridge.enrollplus.feature.parent.presentation.FeeAnnouncement
import com.littlebridge.enrollplus.feature.parent.presentation.FeeState
import com.littlebridge.enrollplus.feature.parent.presentation.FeeViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonFee
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

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
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(state.refreshEpoch) {
        if (state.refreshEpoch > 0) isRefreshing = false
    }
    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.reload()
        },
        modifier = modifier.fillMaxSize(),
    ) {
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
        )
    }
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
            .background(VColors.cream)
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
            greetingLead = "${selectedChild?.name?.ifBlank { null } ?: "Your Child"}'s",
            greetingAccent = "fees",
        )

        PortalQuickActionChips(
            chips = listOf(
                QuickActionChipSpec(
                    icon = Icons.Filled.Payment,
                    iconColor = VColors.violet,
                    iconBg = VColors.violetSoft,
                    title = "Pay\nNow",
                    onClick = onPayNow,
                ),
                QuickActionChipSpec(
                    icon = Icons.Filled.History,
                    iconColor = VColors.success,
                    iconBg = VColors.successSoft,
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
                SkeletonFee()

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
                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = VColors.ink,
                    )

                    // ── Balance + collection card ──────────────────────────────────────
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(VShapes.lg)
                            .background(VColors.surfaceCard)
                            .border(1.dp, VColors.line, VShapes.lg)
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
                                    style = VTypography.caption,
                                    color = VColors.ink3,
                                )
                                Text(
                                    state.outstandingFees,
                                    style = VTypography.h2.copy(fontSize = 28.sp),
                                    color = VColors.ink,
                                )
                                if (state.overdueCount > 0) {
                                    Spacer(Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Box(Modifier.size(6.dp).clip(CircleShape).background(VColors.error))
                                        Text(
                                            "${state.overdueCount} overdue",
                                            style = VTypography.caption,
                                            color = VColors.error,
                                        )
                                    }
                                }
                            }
                            Box(
                                Modifier
                                    .clip(VShapes.full)
                                    .background(VColors.violetSoft)
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    "Pay Now",
                                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                                    color = VColors.violet,
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
                                style = VTypography.caption,
                                color = VColors.ink3,
                            )
                            Text(
                                state.totalCollected,
                                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = VColors.success,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        CollectionProgressBar(state.collectionProgress)
                    }

                    // ── Fee announcements ────────────────────────────────────────────
                    if (state.announcements.isNotEmpty()) {
                        Text(
                            "Fee notices",
                            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = VColors.ink,
                        )
                        state.announcements.forEach { a ->
                            FeeAnnouncementCard(a)
                        }
                    }

                    // ── Fee items breakdown ───────────────────────────────────────
                    if (state.feeItems.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Fee breakdown",
                            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = VColors.ink,
                        )
                        state.feeItems.forEach { item ->
                            FeeItemCard(item)
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
            .background(VColors.line),
    ) {
        Box(
            Modifier
                .fillMaxWidth(clamped)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(VColors.violet),
        )
    }
}

@Composable
private fun FeeAnnouncementCard(a: FeeAnnouncement) {
    val badgeColor = when (a.type) {
        "Emergency" -> VColors.error to VColors.errorSoft
        "Payment" -> VColors.gold to VColors.goldSoft
        else -> VColors.ink3 to VColors.creamDeep
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                a.title,
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            Text(
                "${a.description} • ${a.time}",
                style = VTypography.caption,
                color = VColors.ink2,
            )
        }
        Box(
            Modifier
                .clip(VShapes.full)
                .background(badgeColor.second)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                a.type,
                style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = badgeColor.first,
            )
        }
    }
}

@Composable
private fun FeeItemCard(item: ParentFeeItemDto) {
    val statusColor = when (item.status) {
        "PAID" -> VColors.success to VColors.successSoft
        "OVERDUE" -> VColors.error to VColors.errorSoft
        else -> VColors.gold to VColors.goldSoft
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                item.title,
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            item.description?.let {
                Text(it, style = VTypography.caption, color = VColors.ink2)
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "${item.currency} ${"%,.0f".format(item.amount)}",
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.violet,
                )
                item.month?.let {
                    Text("• $it", style = VTypography.caption, color = VColors.ink3)
                }
            }
        }
        Box(
            Modifier
                .clip(VShapes.full)
                .background(statusColor.second)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                item.status,
                style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = statusColor.first,
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
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(56.dp).clip(CircleShape).background(VColors.creamDeep),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(title, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
        Spacer(Modifier.height(4.dp))
        Text(body, style = VTypography.caption, color = VColors.ink2)
    }
}
