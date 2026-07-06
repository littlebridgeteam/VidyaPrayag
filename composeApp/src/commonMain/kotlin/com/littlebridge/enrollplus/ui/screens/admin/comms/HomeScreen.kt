package com.littlebridge.enrollplus.ui.screens.admin.comms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.presentation.admin.AdminHomeViewModel
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography
import com.littlebridge.enrollplus.ui.screens.admin.components.BarFillType

// ═══════════════════════════════════════════════════════════════
// HomeScreen — Admin dashboard (Home tab)
// ═══════════════════════════════════════════════════════════════

@Composable
fun HomeScreen(
    viewModel: AdminHomeViewModel,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) { viewModel.loadOverview() }
    val overviewState by viewModel.overviewState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        when (val state = overviewState) {
            is UiState.Loading -> {
                Text(
                    text = "Loading dashboard...",
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.greeting,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 2.dp)
                )
            }
            is UiState.Error -> {
                Text(
                    text = state.message,
                    color = AdminColors.alertRed,
                    style = AdminTypography.greeting,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 2.dp)
                )
            }
            is UiState.Success -> {
                val overview = state.data
                // Greeting
                Text(
                    text = overview.header.greeting + ", " + overview.header.adminName,
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.greeting,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 2.dp)
                )

                // Hero — use first KPI or pulse score
                val pulse = overview.schoolPulse
                CommsHero(
                    label = "School Pulse",
                    bigValue = pulse.score.toString(),
                    total = "/ 100",
                    subText = pulse.message,
                    ringPct = pulse.score,
                    bars = pulse.categories.take(6).map { cat ->
                        HeroBar(cat.score, if (cat.score >= 80) BarFillType.GOOD else if (cat.score >= 60) BarFillType.MID else BarFillType.LOW)
                    },
                    barLabels = pulse.categories.take(6).map { it.label.take(2) },
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )

                // Quick actions
                QuickActionRow(
                    actions = listOf(
                        QuickAction("Announce", AdminColors.skyBlueBg, "📢"),
                        QuickAction("Calendar", AdminColors.purpleBg, "📅"),
                        QuickAction("Approve", AdminColors.goodGreenBg, "✓"),
                        QuickAction("Remind", AdminColors.siennaBg, "⏰"),
                        QuickAction("Reports", AdminColors.goldBg, "📊")
                    ),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )

                // Pulse scroll — KPIs
                PulseScrollRow(
                    cards = overview.kpis.map { kpi ->
                        val trend = when (kpi.deltaDirection) {
                            "up" -> TrendType.UP
                            "down" -> TrendType.DOWN
                            else -> TrendType.FLAT
                        }
                        PulseCardData(
                            icon = "�",
                            bg = AdminColors.purpleBg,
                            trend = trend,
                            big = kpi.value.toString(),
                            small = kpi.unit,
                            title = kpi.label,
                            sub = kpi.deltaLabel,
                            stripe = PulseStripe.PURPLE
                        )
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Fee collection card
                val fee = overview.feeAnalytics
                if (fee.available) {
                    FeeCollectionCard(
                        title = "Fee Collection",
                        amount = "${fee.totalCollected.toInt()}",
                        amountSm = " ${fee.currency}",
                        barFillFraction = fee.collectionRate / 100f,
                        metaLeft = "${fee.pending.toInt()} pending",
                        metaRight = "${fee.collectionRate}% collected",
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                    )
                }

                // Priority Inbox section
                PriorityInboxSection(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )

                // Recent Activity section
                RecentActivitySection(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }
        }
    }
}
