package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.presentation.FeeState
import com.littlebridge.enrollplus.feature.parent.presentation.FeeViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import org.koin.compose.viewmodel.koinViewModel

/**
 * ParentFeesScreenV2 — a pixel-faithful copy of `Parent.tsx → Fees`.
 *
 * Navy-gradient balance hero (outstanding fees + collection progress bar) and the school's
 * fee-related announcements feed. **Wired to the real [FeeViewModel]** (`shared/`) →
 * `ParentRepository.getFees` → `GET /api/v1/parent/fees`. MockV2 is no longer referenced; the
 * three UI states (loading / error / empty) are handled by [VStateHost].
 */
@Composable
fun ParentFeesScreenV2(
    modifier: Modifier = Modifier,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    viewModel: FeeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentFeesContent(
        state = state,
        children = children,
        selectedChild = selectedChild,
        onSelectChild = onSelectChild,
        onOpenNotifications = onOpenNotifications,
        unreadNotificationsCount = unreadNotificationsCount,
        modifier = modifier,
    )
}

/** Stateless body — also used by the @Preview with seeded state (no MockV2 in the live path). */
@Composable
private fun ParentFeesContent(
    state: FeeState,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors
    val d = VTheme.dimens

    val isEmpty = state.announcements.isEmpty() &&
        state.outstandingFees.isBlank() &&
        state.totalCollected.isBlank()

    // LAYOUT FIX: state legs render inside the scrollable Column so the premium header + chips
    // are always visible, matching the Academics tab pattern.
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 130.dp),
    ) {
        ParentPortalHeader(
            label = "Fees",
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
                    iconColor = VColors.violet,
                    iconBg = VColors.violetSoft,
                    title = "Pay\nNow",
                    onClick = { /* TODO: wire payment flow */ },
                ),
                QuickActionChipSpec(
                    icon = Icons.Filled.History,
                    iconColor = VColors.success,
                    iconBg = VColors.successSoft,
                    title = "Fee\nHistory",
                    onClick = { /* TODO: wire history flow */ },
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
                    androidx.compose.material3.CircularProgressIndicator(color = c.accent, modifier = Modifier.size(36.dp))
                }

            state.error != null && isEmpty ->
                Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    com.littlebridge.enrollplus.ui.v2.screens.VErrorState(
                        message = state.error ?: "",
                        onRetry = null,
                    )
                }

            isEmpty ->
                Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    com.littlebridge.enrollplus.ui.v2.components.VEmptyState(
                        title = "No fee records yet",
                        body = "Once your school publishes fees, they'll appear here.",
                    )
                }

            else ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(d.sm + 4.dp),
                ) {
                    Text(appString(StringKeys.PF_FEES), style = VTheme.type.h1.colored(c.ink))
                    // ── Hero: outstanding balance + collection progress ─────────────────
                    VCard(padding = 0.dp) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(Brush.linearGradient(listOf(c.navy, Color(0xFF3B3870))))
                                .padding(20.dp),
                        ) {
                            Column {
                                VLabel("Balance due", color = Color.White.copy(alpha = 0.7f))
                                Text(
                                    state.outstandingFees,
                                    style = VTheme.type.dataLg.colored(Color.White)
                                        .copy(fontSize = 36.sp, fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                                if (state.overdueCount > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(top = 4.dp),
                                    ) {
                                        Box(Modifier.size(6.dp).clip(RoundedCornerShape(999.dp)).background(c.warning))
                                        Text(
                                            "${state.overdueCount} overdue",
                                            style = VTheme.type.caption.colored(Color.White.copy(alpha = 0.78f)),
                                        )
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        // RA-PP-THEME: website-accent violet CTA (was green teal).
                                        .background(c.accent)
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(appString(StringKeys.PF_PAY_NOW), style = VTheme.type.bodyStrong.colored(Color.White))
                                        Text(appString(StringKeys.PF_COMING_SOON), style = VTheme.type.caption.colored(Color.White.copy(alpha = 0.8f)))
                                    }
                                }
                            }
                        }
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                VLabel("Collected this term")
                                Text(state.totalCollected, style = VTheme.type.data.colored(c.ink).copy(fontWeight = FontWeight.SemiBold))
                            }
                            VProgressBar(value = (state.collectionProgress * 100f), tone = VBadgeTone.Accent)
                        }
                    }

                    // ── Fee announcements ──────────────────────────────────────────────
                    if (state.announcements.isNotEmpty()) {
                        VLabel("Fee notices")
                        state.announcements.forEach { a ->
                            VCard {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(a.title, style = VTheme.type.bodyStrong.colored(c.ink))
                                        Text("${a.description} • ${a.time}", style = VTheme.type.caption.colored(c.ink2))
                                    }
                                    VBadge(
                                        text = a.type,
                                        tone = when (a.type) {
                                            "Emergency" -> VBadgeTone.Danger
                                            "Payment" -> VBadgeTone.Warning
                                            else -> VBadgeTone.Neutral
                                        },
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        "Have a question about your fees? Message the school →",
                        style = VTheme.type.caption.colored(c.accentDeep).copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                } // end content Column
        }
    }
}
