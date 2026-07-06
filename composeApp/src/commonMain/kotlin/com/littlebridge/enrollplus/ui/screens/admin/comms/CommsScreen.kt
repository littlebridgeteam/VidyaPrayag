package com.littlebridge.enrollplus.ui.screens.admin.comms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.admin.domain.model.AnnouncementListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.LeaveRequestsResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.MessageThread
import com.littlebridge.enrollplus.feature.admin.domain.model.PtmResponse
import com.littlebridge.enrollplus.presentation.admin.AdminCommsViewModel
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography
import com.littlebridge.enrollplus.ui.screens.admin.components.BarFillType
import com.littlebridge.enrollplus.ui.screens.admin.components.CardSurface
import com.littlebridge.enrollplus.ui.screens.admin.components.IconBox
import com.littlebridge.enrollplus.ui.screens.admin.components.PillButton
import com.littlebridge.enrollplus.ui.screens.admin.components.SectionLabel
import com.littlebridge.enrollplus.ui.screens.admin.components.SubtabPill

// ═══════════════════════════════════════════════════════════════
// CommsScreen — Communications tab with 4 subtabs
// ═══════════════════════════════════════════════════════════════

@Composable
fun CommsScreen(
    viewModel: AdminCommsViewModel,
    modifier: Modifier = Modifier,
) {
    var activeSubtab by remember { mutableIntStateOf(0) }
    val subtabs = listOf("Announcements", "Messages", "PTM", "Notifications")

    val announcementsState by viewModel.announcementsState.collectAsState()
    val messagesState by viewModel.messagesState.collectAsState()
    val ptmState by viewModel.ptmState.collectAsState()
    val leaveState by viewModel.leaveState.collectAsState()

    LaunchedEffect(activeSubtab) {
        when (activeSubtab) {
            0 -> viewModel.loadAnnouncements()
            1 -> viewModel.loadMessages()
            2 -> viewModel.loadPtm()
            3 -> viewModel.loadLeaveRequests()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        SubtabPill(
            tabs = subtabs,
            activeIndex = activeSubtab,
            onTabSelect = { activeSubtab = it },
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
        )

        when (activeSubtab) {
            0 -> CommsAnnouncementsTab(announcementsState)
            1 -> CommsMessagesTab(messagesState)
            2 -> CommsPTMTab(ptmState)
            3 -> CommsNotificationsTab(leaveState)
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// CommsHero — 277×158, padding:24/20, radius:18
// ═══════════════════════════════════════════════════════════════

data class HeroBar(val fillPct: Int, val type: BarFillType)

@Composable
fun CommsHero(
    label: String,
    bigValue: String,
    total: String,
    subText: String,
    ringPct: Int,
    bars: List<HeroBar>,
    barLabels: List<String>,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        radius = 18
    ) {
        Column {
            // Top row — left info + right ring
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left — 182×85
                Column(modifier = Modifier.width(182.dp)) {
                    Text(
                        text = label,
                        color = AdminColors.inkSecondary,
                        style = AdminTypography.heroLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = bigValue,
                            color = AdminColors.inkPrimary,
                            style = AdminTypography.heroBig
                        )
                        Text(
                            text = total,
                            color = AdminColors.inkSecondary,
                            style = AdminTypography.heroTotal
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = subText,
                            color = AdminColors.alertRed,
                            style = AdminTypography.heroSubStrong
                        )
                    }
                }

                // Right — ring 72×72
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Ring (simplified — circle with text)
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.Transparent, RoundedCornerShape(50))
                            .border(5.dp, AdminColors.sienna.copy(alpha = 0.15f), RoundedCornerShape(50))
                    )
                    // Inner ring arc (simplified)
                    Box(
                        modifier = Modifier
                            .size(58.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${ringPct}%",
                                color = AdminColors.sienna,
                                style = AdminTypography.ringNum
                            )
                            Text(
                                text = "attendance",
                                color = AdminColors.inkSecondary,
                                style = AdminTypography.ringSmall
                            )
                        }
                    }
                }
            }

            // Bars row — 243×4, gap:4, margin-top:16
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                bars.forEach { bar ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(AdminColors.trackBg, RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(bar.fillPct / 100f)
                                .background(bar.type.color, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }

            // Bar labels — 243×10, gap:4, margin-top:6
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                barLabels.forEach { label ->
                    Text(
                        text = label,
                        color = AdminColors.inkSecondary,
                        style = AdminTypography.barLabel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// QuickActionRow — 277×74, gap:8
// ═══════════════════════════════════════════════════════════════

data class QuickAction(val label: String, val iconBg: Color, val iconChar: String)

@Composable
fun QuickActionRow(
    actions: List<QuickAction>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        actions.forEach { action ->
            QuickActionButton(
                label = action.label,
                iconBg = action.iconBg,
                iconChar = action.iconChar,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    iconBg: Color,
    iconChar: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(74.dp)
            .shadow(1.dp, AdminShapes.card, ambientColor = Color(0x0A1A1614), spotColor = Color(0x0F1A1614))
            .background(AdminColors.cardWhite, AdminShapes.card)
            .border(1.6.dp, Color.Black.copy(alpha = 0.05f), AdminShapes.card)
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Icon — 36×36, radius:10
        IconBox(size = 36, bg = iconBg, radius = 10) {
            Text(text = iconChar, fontSize = 15.sp)
        }
        Text(
            text = label,
            color = AdminColors.inkTertiary,
            style = AdminTypography.microLabel
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// PulseScrollRow — 358×121, gap:10, horizontal scroll
// ═══════════════════════════════════════════════════════════════

data class PulseCardData(
    val iconChar: String,
    val iconBg: Color,
    val trend: TrendType,
    val value: String,
    val unit: String,
    val label: String,
    val subText: String,
    val stripe: PulseStripe = PulseStripe.SIENNA
)

enum class TrendType { UP, ALERT, DOWN, FLAT }
enum class PulseStripe { SIENNA, CORAL, MINT, SKY, PURPLE }

@Composable
fun PulseScrollRow(
    cards: List<PulseCardData>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        cards.forEach { card ->
            PulseCard(card)
        }
    }
}

@Composable
private fun PulseCard(data: PulseCardData) {
    val stripeColor = when (data.stripe) {
        PulseStripe.SIENNA -> AdminColors.sienna
        PulseStripe.CORAL -> AdminColors.alertRed
        PulseStripe.MINT -> AdminColors.goodGreen
        PulseStripe.SKY -> AdminColors.skyBlue
        PulseStripe.PURPLE -> AdminColors.purple
    }
    Box(
        modifier = Modifier.width(150.dp)
            .shadow(1.dp, AdminShapes.card, ambientColor = Color(0x0A1A1614), spotColor = Color(0x0F1A1614))
            .background(AdminColors.cardWhite, AdminShapes.card)
            .clickable { }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row — icon + trend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBox(size = 32, bg = data.iconBg, radius = 10) {
                    Text(text = data.iconChar, fontSize = 14.sp)
                }
                // Trend badge
                val trendBg = when (data.trend) {
                    TrendType.UP -> AdminColors.goodGreenBg
                    TrendType.ALERT -> AdminColors.alertRedBg
                    TrendType.DOWN -> AdminColors.alertRedBg
                    TrendType.FLAT -> AdminColors.pillBg
                }
                val trendColor = when (data.trend) {
                    TrendType.UP -> AdminColors.goodGreen
                    TrendType.ALERT -> AdminColors.alertRed
                    TrendType.DOWN -> AdminColors.alertRed
                    TrendType.FLAT -> AdminColors.inkSecondary
                }
                Row(
                    modifier = Modifier
                        .background(trendBg, RoundedCornerShape(50))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (data.trend == TrendType.UP) {
                        Text(
                            text = "↑",
                            color = trendColor,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = when (data.trend) {
                            TrendType.UP -> "+12%"
                            TrendType.ALERT -> "Action"
                            TrendType.DOWN -> "-8%"
                            TrendType.FLAT -> "Steady"
                        },
                        color = trendColor,
                        style = AdminTypography.pulseTrend
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Number + unit
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = data.value,
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.pulseNumWithLine
                )
                if (data.unit.isNotEmpty()) {
                    Text(
                        text = data.unit,
                        color = AdminColors.inkTertiary,
                        style = AdminTypography.pulseUnit
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Label
            Text(
                text = data.label,
                color = AdminColors.inkSecondary,
                style = AdminTypography.pulseLabel
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Sub text
            Text(
                text = data.subText,
                color = AdminColors.inkSecondary,
                style = AdminTypography.pulseSub
            )
        }
        // Bottom accent stripe — 3px
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.dp)
                .background(stripeColor.copy(alpha = 0.6f), RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// FeeCollectionCard — 277×87, padding:18/20, radius:14
// Top: title (13sp w800) + amount (18sp w900) + sm (12sp w600)
// Bar: 243×7, track bg, fill sienna, radius:full
// Meta: space-between, 12sp w500 with strong spans
// ═══════════════════════════════════════════════════════════════

@Composable
fun FeeCollectionCard(
    title: String,
    amount: String,
    amountSm: String,
    barFillFraction: Float,
    metaLeft: String,
    metaRight: String,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        radius = 14
    ) {
        Column {
            // Top row — title + amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.feeTitle
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = amount,
                        color = AdminColors.inkPrimary,
                        style = AdminTypography.feeAmt
                    )
                    Text(
                        text = amountSm,
                        color = AdminColors.inkSecondary,
                        style = AdminTypography.feeAmtSm
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bar — 243×8, track + fill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(AdminColors.trackBg, RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barFillFraction)
                        .height(8.dp)
                        .background(AdminColors.sienna, RoundedCornerShape(50))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Meta row — space-between
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = metaLeft,
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.feeMeta
                )
                Text(
                    text = metaRight,
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.feeMeta
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PriorityInboxSection — section head + 3 inbox items
// ═══════════════════════════════════════════════════════════════

data class InboxItemData(
    val dotColor: Color,
    val title: String,
    val meta: String,
    val tagText: String,
    val tagBg: Color,
    val tagColor: Color
)

@Composable
fun PriorityInboxSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section head — 277×13, space-between, margin-bottom:10
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Priority Inbox",
                color = AdminColors.inkSecondary,
                style = AdminTypography.sectionTitle
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "View all",
                    color = AdminColors.sienna,
                    style = AdminTypography.sectionLink
                )
                Text(
                    text = "→",
                    color = AdminColors.sienna,
                    fontSize = 11.sp
                )
            }
        }

        // Inbox items
        val items = listOf(
            InboxItemData(
                AdminColors.inboxDotUrgent,
                "Leave Request — Aarav Sharma (7-B)",
                "Submitted by parent · 2 days",
                "Urgent",
                AdminColors.inboxTagUrgentBg,
                AdminColors.inboxTagUrgentColor
            ),
            InboxItemData(
                AdminColors.inboxDotNew,
                "New Admission — Riya Patel (Grade 5)",
                "Application received · 3 hours ago",
                "New",
                AdminColors.inboxTagNewBg,
                AdminColors.inboxTagNewColor
            ),
            InboxItemData(
                AdminColors.inboxDotPending,
                "Transfer Certificate — Rohan Gupta (alum)",
                "Requested by parent · 1 day",
                "Pending",
                AdminColors.inboxTagPendingBg,
                AdminColors.inboxTagPendingColor
            )
        )

        items.forEach { item ->
            InboxItemCard(item, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
private fun InboxItemCard(
    item: InboxItemData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, AdminShapes.card, ambientColor = Color(0x0A1A1614), spotColor = Color(0x0F1A1614))
            .background(AdminColors.cardWhite, AdminShapes.card)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Dot — 8×8, margin-left:3
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(item.dotColor, RoundedCornerShape(50))
        )

        // Body — title + meta
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = AdminColors.inkPrimary,
                style = AdminTypography.inboxTitle
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.meta,
                color = AdminColors.inkSecondary,
                style = AdminTypography.inboxMeta
            )
        }

        // Tag — pill
        Box(
            modifier = Modifier
                .background(item.tagBg, RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = item.tagText,
                color = item.tagColor,
                style = AdminTypography.inboxTag
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// RecentActivitySection — section head + card with activity items
// ═══════════════════════════════════════════════════════════════

data class ActivityItemData(
    val iconBg: Color,
    val iconChar: String,
    val title: String,
    val meta: String,
    val time: String
)

@Composable
fun RecentActivitySection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section head — margin-bottom:10
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                color = AdminColors.inkSecondary,
                style = AdminTypography.sectionTitle
            )
        }

        // Activity card — padding:12/16, radius:14
        CardSurface(
            padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            radius = 14
        ) {
            Column {
                val activities = listOf(
                    ActivityItemData(AdminColors.siennaBg, "💰", "Fee payment received — ₹12,500", "Term 3 · Aarav Sharma (7-B) · UPI", "10 min ago"),
                    ActivityItemData(AdminColors.goodGreenBg, "✓", "New admission approved — Riya Patel", "Grade 5-A · Roll 28 · Parent linked", "1 hour ago"),
                    ActivityItemData(AdminColors.skyBlueBg, "�", "Announcement sent — Sports Day Registration", "Delivered to 1,312 parents · 847 opened", "2 hours ago"),
                    ActivityItemData(AdminColors.goldBg, "�", "Leave approved — Sneha Reddy (8-A)", "2 days · Medical · Parent notified", "3 hours ago"),
                    ActivityItemData(AdminColors.purpleBg, "�", "PTM scheduled — Jan 22, 10 AM to 1 PM", "Slot booking opens Jan 18 · 60 slots", "5 hours ago")
                )

                activities.forEach { activity ->
                    ActivityItemRow(activity)
                }
            }
        }
    }
}

@Composable
private fun ActivityItemRow(
    item: ActivityItemData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon — 38×38, radius:10
        IconBox(size = 38, bg = item.iconBg, radius = 10) {
            Text(text = item.iconChar, fontSize = 15.sp)
        }

        // Body — title + meta + time
        Column(modifier = Modifier.weight(1f).padding(top = 2.dp)) {
            Text(
                text = item.title,
                color = AdminColors.inkPrimary,
                style = AdminTypography.actTitle
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.meta,
                color = AdminColors.inkSecondary,
                style = AdminTypography.actMeta
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = item.time,
                color = AdminColors.inkSecondary,
                style = AdminTypography.actTime
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// CommsAnnouncementsTab
// ═══════════════════════════════════════════════════════════════

@Composable
fun CommsAnnouncementsTab(state: UiState<AnnouncementListResponse>) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp)) {
        // Compose button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(AdminColors.sienna, RoundedCornerShape(12.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+ Compose Announcement",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is UiState.Loading -> Text("Loading announcements...", color = AdminColors.inkSecondary, modifier = Modifier.padding(16.dp))
            is UiState.Error -> Text(state.message, color = AdminColors.alertRed, modifier = Modifier.padding(16.dp))
            is UiState.Success -> {
                state.data.announcements.forEach { ann ->
                    val iconChar = when (ann.type.lowercase()) {
                        "holiday" -> "📅"
                        "ptm" -> "📋"
                        "fee", "reminder" -> "💰"
                        else -> "📢"
                    }
                    val iconBg = when (ann.type.lowercase()) {
                        "holiday" -> AdminColors.goodGreenBg
                        "ptm" -> AdminColors.purpleBg
                        "fee", "reminder" -> AdminColors.siennaBg
                        else -> AdminColors.skyBlueBg
                    }
                    val iconColor = when (ann.type.lowercase()) {
                        "holiday" -> AdminColors.goodGreen
                        "ptm" -> AdminColors.purple
                        "fee", "reminder" -> AdminColors.sienna
                        else -> AdminColors.skyBlue
                    }
                    CardSurface(
                        modifier = Modifier.padding(bottom = 8.dp),
                        padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        radius = 14
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconBox(size = 36, bg = iconBg, radius = 10) {
                                Text(text = iconChar, fontSize = 14.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = ann.title, color = AdminColors.inkPrimary, style = AdminTypography.alertTitle)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "${ann.audienceType} · ${ann.date}", color = AdminColors.inkSecondary, style = AdminTypography.metaText)
                                if (ann.subTitle != null) {
                                    val sub = ann.subTitle!!
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(text = sub, color = AdminColors.sienna, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class AnnounceData(
    val title: String,
    val audience: String,
    val time: String,
    val stats: String,
    val iconBg: Color,
    val iconColor: Color,
    val iconChar: String
)

// ═══════════════════════════════════════════════════════════════
// CommsMessagesTab — Approvals + Conversations
// ═══════════════════════════════════════════════════════════════

@Composable
fun CommsMessagesTab(state: UiState<List<MessageThread>>) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp)) {
        SectionLabel(text = "Conversations", modifier = Modifier.padding(bottom = 6.dp))

        when (state) {
            is UiState.Loading -> Text("Loading messages...", color = AdminColors.inkSecondary, modifier = Modifier.padding(16.dp))
            is UiState.Error -> Text(state.message, color = AdminColors.alertRed, modifier = Modifier.padding(16.dp))
            is UiState.Success -> {
                state.data.forEach { thread ->
                    CardSurface(
                        modifier = Modifier.padding(bottom = 8.dp),
                        padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        radius = 14
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(AdminColors.siennaBg, RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = thread.senderName.take(2).uppercase(), color = AdminColors.sienna, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${thread.senderName} (${thread.senderRole})", color = AdminColors.inkPrimary, style = AdminTypography.alertTitle)
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(text = thread.lastMessage, color = AdminColors.inkSecondary, style = AdminTypography.metaText)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = thread.time, color = AdminColors.inkTertiary, fontSize = 10.sp)
                            }
                            if (thread.unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(AdminColors.alertRed, RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = thread.unreadCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ApprovalData(
    val name: String,
    val type: String,
    val meta: String,
    val tag: String,
    val tagBg: Color,
    val tagColor: Color
)

private data class ConvoData(
    val name: String,
    val preview: String,
    val time: String,
    val avatar: String,
    val avatarBg: Color,
    val avatarColor: Color,
    val unread: Int
)

// ═══════════════════════════════════════════════════════════════
// CommsPTMTab — PTM scheduling
// ═══════════════════════════════════════════════════════════════

@Composable
fun CommsPTMTab(state: UiState<PtmResponse>) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp)) {
        when (state) {
            is UiState.Loading -> Text("Loading PTM...", color = AdminColors.inkSecondary, modifier = Modifier.padding(16.dp))
            is UiState.Error -> Text(state.message, color = AdminColors.alertRed, modifier = Modifier.padding(16.dp))
            is UiState.Success -> {
                val ptm = state.data
                val active = ptm.activeEvent
                if (active != null) {
                    CardSurface(
                        padding = PaddingValues(20.dp),
                        radius = 18
                    ) {
                        Column {
                            Text(text = "Next PTM", color = AdminColors.inkSecondary, style = AdminTypography.heroLabel)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "${active.title} — ${active.date}", color = AdminColors.inkPrimary, style = AdminTypography.heroBig)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Slot: ${active.slot} · ${active.checkedInParents}/${active.expectedParents} checked in", color = AdminColors.sienna, style = AdminTypography.heroSubStrong)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AdminColors.sienna, RoundedCornerShape(12.dp))
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "Manage Slots →", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (ptm.classProgress.isNotEmpty()) {
                    SectionLabel(text = "Class Progress", modifier = Modifier.padding(bottom = 6.dp))
                    ptm.classProgress.forEach { cp ->
                        CardSurface(
                            modifier = Modifier.padding(bottom = 6.dp),
                            padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            radius = 14
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(AdminColors.goodGreenBg, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📅", fontSize = 12.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = cp.className, color = AdminColors.inkPrimary, style = AdminTypography.alertTitle)
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(text = "${cp.metCount}/${cp.totalCount} met · ${cp.teacherName}", color = AdminColors.inkSecondary, style = AdminTypography.metaText)
                                }
                            }
                        }
                    }
                }

                if (ptm.history.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionLabel(text = "History", modifier = Modifier.padding(bottom = 6.dp))
                    ptm.history.forEach { h ->
                        CardSurface(
                            modifier = Modifier.padding(bottom = 6.dp),
                            padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            radius = 14
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${h.title} — ${h.date}", color = AdminColors.inkPrimary, style = AdminTypography.alertTitle)
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(text = "Turnout: ${h.turnout}% · ${h.totalMet} meetings", color = AdminColors.inkSecondary, style = AdminTypography.metaText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class SlotData(
    val time: String,
    val parent: String,
    val detail: String,
    val statusBg: Color,
    val statusColor: Color
)

// ═══════════════════════════════════════════════════════════════
// CommsNotificationsTab — Notification log
// ═══════════════════════════════════════════════════════════════

@Composable
fun CommsNotificationsTab(state: UiState<LeaveRequestsResponse>) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp)) {
        SectionLabel(text = "Leave Requests", modifier = Modifier.padding(bottom = 6.dp))

        when (state) {
            is UiState.Loading -> Text("Loading leave requests...", color = AdminColors.inkSecondary, modifier = Modifier.padding(16.dp))
            is UiState.Error -> Text(state.message, color = AdminColors.alertRed, modifier = Modifier.padding(16.dp))
            is UiState.Success -> {
                if (state.data.requests.isEmpty()) {
                    Text("No pending leave requests", color = AdminColors.inkTertiary, modifier = Modifier.padding(16.dp))
                } else {
                    state.data.requests.forEach { req ->
                        CardSurface(
                            modifier = Modifier.padding(bottom = 6.dp),
                            padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            radius = 14
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconBox(size = 36, bg = AdminColors.alertRedBg, radius = 10) {
                                    Text(text = "📋", fontSize = 14.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = req.requesterName, color = AdminColors.inkPrimary, style = AdminTypography.alertTitle)
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(text = "${req.requesterRole} · ${req.dateFrom} to ${req.dateTo}", color = AdminColors.inkSecondary, style = AdminTypography.metaText)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "Reason: ${req.reason} · Status: ${req.status}", color = AdminColors.inkTertiary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class NotifData(
    val title: String,
    val channel: String,
    val time: String,
    val iconBg: Color,
    val iconColor: Color,
    val iconChar: String
)
