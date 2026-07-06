package com.littlebridge.enrollplus.ui.screens.admin.records

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.littlebridge.enrollplus.presentation.admin.AdminRecordsViewModel
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography
import com.littlebridge.enrollplus.ui.screens.admin.components.BarFillType
import com.littlebridge.enrollplus.ui.screens.admin.components.CardSurface
import com.littlebridge.enrollplus.ui.screens.admin.components.PillButton
import com.littlebridge.enrollplus.ui.screens.admin.components.SectionLabel
import com.littlebridge.enrollplus.ui.screens.admin.components.SubtabPill

// ═══════════════════════════════════════════════════════════════
// RecordsScreen
// ═══════════════════════════════════════════════════════════════

@Composable
fun RecordsScreen(
    viewModel: AdminRecordsViewModel,
    modifier: Modifier = Modifier,
) {
    var activeSubtab by remember { mutableIntStateOf(0) }
    val subtabs = listOf("Coverage", "Pace", "Attendance", "Marks", "Fee", "Documents")

    val coverageState by viewModel.coverageState.collectAsState()
    val paceState by viewModel.paceState.collectAsState()
    val attendanceState by viewModel.attendanceState.collectAsState()
    val marksState by viewModel.marksState.collectAsState()
    val feeState by viewModel.feeState.collectAsState()

    LaunchedEffect(activeSubtab) {
        when (activeSubtab) {
            0 -> viewModel.loadCoverage()
            1 -> viewModel.loadPace()
            2 -> viewModel.loadAttendance()
            3 -> viewModel.loadMarks()
            4 -> viewModel.loadFees()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Subtab pill — 277×30, margin:0/24/16
        SubtabPill(
            tabs = subtabs,
            activeIndex = activeSubtab,
            onTabSelect = { activeSubtab = it },
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
        )

        // Summary card — changes per subtab
        when (activeSubtab) {
            0 -> { // Coverage
                when (val s = coverageState) {
                    is UiState.Loading -> Text("Loading coverage...", modifier = Modifier.padding(24.dp))
                    is UiState.Error -> Text(s.message, color = AdminColors.alertRed, modifier = Modifier.padding(24.dp))
                    is UiState.Success -> {
                        val data = s.data.data
                        val avgPct = if (data.snapshots.isNotEmpty()) data.snapshots.map { it.actualPct }.average().toInt() else 0
                        RecordsSummary(
                            label = "Overall Syllabus Coverage",
                            value = "$avgPct%",
                            sub = "Across ${data.snapshots.size} classes",
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                        RecordsBarChart(
                            bars = data.snapshots.take(6).map { snap ->
                                val pct = snap.actualPct
                                BarData(
                                    "${snap.className}-${snap.section}",
                                    pct,
                                    100,
                                    "${pct}%",
                                    if (pct >= 80) BarFillType.GOOD else if (pct >= 60) BarFillType.MID else BarFillType.LOW
                                )
                            },
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                    }
                }
            }
            1 -> { // Pace
                when (val s = paceState) {
                    is UiState.Loading -> Text("Loading pace...", modifier = Modifier.padding(24.dp))
                    is UiState.Error -> Text(s.message, color = AdminColors.alertRed, modifier = Modifier.padding(24.dp))
                    is UiState.Success -> {
                        val data = s.data.data
                        val onTrack = data.alerts.count { it.level == "AHEAD" }
                        val behind = data.alerts.count { it.level == "BEHIND" || it.level == "CRITICAL" }
                        RecordsSummary(
                            label = "Pace Status — All Classes",
                            value = "$onTrack Ahead",
                            sub = "$behind behind · $onTrack ahead",
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                        data.alerts.forEach { alert ->
                            RecordsAlertCard(
                                title = "${alert.className} ${alert.subject} — ${alert.level}",
                                meta = alert.message.ifBlank { "Teacher: ${alert.teacherName}" },
                                isPositive = alert.level == "AHEAD",
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
            }
            2 -> { // Attendance
                when (val s = attendanceState) {
                    is UiState.Loading -> Text("Loading attendance...", modifier = Modifier.padding(24.dp))
                    is UiState.Error -> Text(s.message, color = AdminColors.alertRed, modifier = Modifier.padding(24.dp))
                    is UiState.Success -> {
                        val data = s.data
                        RecordsSummary(
                            label = "Overall Attendance",
                            value = "${data.rate}%",
                            sub = "${data.present} of ${data.total} present",
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                        RecordsBarChart(
                            bars = data.byClass.take(6).map { row ->
                                BarData(
                                    row.grade,
                                    row.rate,
                                    100,
                                    "${row.rate}%",
                                    if (row.rate >= 85) BarFillType.GOOD else if (row.rate >= 70) BarFillType.MID else BarFillType.LOW
                                )
                            },
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                    }
                }
            }
            3 -> { // Marks
                when (val s = marksState) {
                    is UiState.Loading -> Text("Loading marks...", modifier = Modifier.padding(24.dp))
                    is UiState.Error -> Text(s.message, color = AdminColors.alertRed, modifier = Modifier.padding(24.dp))
                    is UiState.Success -> {
                        val data = s.data
                        RecordsSummary(
                            label = "Overall Average",
                            value = "${data.overallAveragePct}%",
                            sub = "Across ${data.assessmentCount} assessments",
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                        RecordsBarChart(
                            bars = data.assessments.take(6).map { row ->
                                val pct = if (row.maxMarks > 0) (row.average / row.maxMarks * 100).toInt() else 0
                                BarData(
                                    row.subject,
                                    pct,
                                    100,
                                    "${pct}%",
                                    if (pct >= 80) BarFillType.GOOD else if (pct >= 60) BarFillType.MID else BarFillType.LOW
                                )
                            },
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                    }
                }
            }
            4 -> { // Fee
                when (val s = feeState) {
                    is UiState.Loading -> Text("Loading fees...", modifier = Modifier.padding(24.dp))
                    is UiState.Error -> Text(s.message, color = AdminColors.alertRed, modifier = Modifier.padding(24.dp))
                    is UiState.Success -> {
                        val data = s.data
                        RecordsSummary(
                            label = "Fee Collection",
                            value = "${data.currency} ${data.paidTotal.toInt()}",
                            sub = "${data.paidCount} paid · ${data.dueCount} pending · ${data.overdueCount} overdue",
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                        FeeRecordsContent(
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                        )
                    }
                }
            }
            5 -> { // Documents
                RecordsDocumentsEmpty(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// RecordsSummary — 277×90, padding:18/20, bg:white, radius:14
// ═══════════════════════════════════════════════════════════════

@Composable
fun RecordsSummary(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        radius = 14
    ) {
        Column {
            Text(
                text = label,
                color = AdminColors.inkSecondary,
                style = AdminTypography.summaryLabel
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = AdminColors.inkPrimary,
                style = AdminTypography.summaryVal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sub,
                color = AdminColors.inkSecondary,
                style = AdminTypography.summarySub
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// RecordsBarChart — card with bar rows
// ═══════════════════════════════════════════════════════════════

data class BarData(
    val name: String,
    val fillWidth: Int,
    val trackWidth: Int,
    val percent: String,
    val fillType: BarFillType
)

@Composable
fun RecordsBarChart(
    bars: List<BarData>,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        radius = 14
    ) {
        Column {
            bars.forEach { bar ->
                RecordsBarRow(bar)
            }
        }
    }
}

@Composable
private fun RecordsBarRow(data: BarData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Name — ~68dp wide
        Text(
            text = data.name,
            color = AdminColors.inkPrimary,
            style = AdminTypography.barName,
            modifier = Modifier.width(75.dp)
        )

        // Track + fill
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .background(AdminColors.trackBg, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(data.fillWidth / 131f)
                    .background(data.fillType.color, RoundedCornerShape(50))
            )
        }

        // Percent — 34dp
        Text(
            text = data.percent,
            color = AdminColors.inkTertiary,
            style = AdminTypography.barPct,
            modifier = Modifier.width(34.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// RecordsAlertCard — 277×56, gap:10, padding:12/16, bg:white, radius:14
// ═══════════════════════════════════════════════════════════════

@Composable
fun RecordsAlertCard(
    title: String,
    meta: String,
    modifier: Modifier = Modifier,
    isPositive: Boolean = false
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        radius = 14
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Alert icon — 24×24, bg:rgb(252,232,226), radius:10
            val iconBg = if (isPositive) AdminColors.goodGreenBg else AdminColors.alertRedBg
            val iconColor = if (isPositive) AdminColors.goodGreen else AdminColors.alertRed
            val iconChar = if (isPositive) "↑" else "⚠"
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconChar,
                    fontSize = 10.sp,
                    color = iconColor
                )
            }

            // Info block
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.alertTitle
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = meta,
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.metaText
                )
            }

            // Resolve button
            if (!isPositive) {
                PillButton(
                    text = "Resolve",
                    bg = AdminColors.siennaBg,
                    color = AdminColors.sienna,
                    fontSize = 10,
                    fontWeight = FontWeight.Bold,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// FeeRecordsContent — Fee hero + recent payments + defaulters
// ═══════════════════════════════════════════════════════════════

@Composable
fun FeeRecordsContent(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CardSurface(
            padding = PaddingValues(20.dp),
            radius = 18
        ) {
            Column {
                Text(
                    text = "Total Collected — Term 3",
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.heroLabel
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "₹4,20,000",
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.heroBig
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Outstanding: ₹6,80,000 · Due by March 15",
                    color = AdminColors.alertRed,
                    style = AdminTypography.heroSubStrong
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FeeStat("842", "Paid")
                    FeeStat("470", "Pending")
                    FeeStat("38%", "Collected")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AdminColors.sienna, RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send Reminders →",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionLabel(
            text = "Recent Payments",
            modifier = Modifier.padding(bottom = 6.dp)
        )
        CardSurface(
            padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            radius = 14
        ) {
            Column {
                FeePaymentItem("Aarav Sharma (7-B)", "Paid today · UPI · TXN88421", "₹12,500")
                FeePaymentItem("Sneha Reddy (8-A)", "Paid yesterday · Card · TXN88398", "₹14,200")
                FeePaymentItem("Mohit Kumar (6-A)", "Paid 2 days ago · UPI · TXN88377", "₹11,800")
                FeePaymentItem("Priya Desai (7-A)", "Paid 3 days ago · Net Banking · TXN88345", "₹13,500")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionLabel(
            text = "Defaulters — Top 5",
            modifier = Modifier.padding(bottom = 6.dp)
        )
        CardSurface(
            padding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            radius = 14
        ) {
            Column {
                FeeDefaulterItem("RG", "Rohan Gupta (8-B)", "₹14,200 pending · 45 days overdue")
                FeeDefaulterItem("AT", "Ananya Tiwari (6-B)", "₹12,500 pending · 30 days overdue")
                FeeDefaulterItem("KS", "Karan Singh (7-A)", "₹11,800 pending · 20 days overdue")
            }
        }
    }
}

@Composable
private fun FeeStat(num: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = num,
            color = AdminColors.inkPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = AdminColors.inkSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FeePaymentItem(name: String, meta: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(AdminColors.goodGreenBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✓", color = AdminColors.goodGreen, fontSize = 12.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = AdminColors.inkPrimary, style = AdminTypography.alertTitle)
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = meta, color = AdminColors.inkSecondary, style = AdminTypography.metaText)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Download Receipt", color = AdminColors.sienna, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Text(text = amount, color = AdminColors.inkPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun FeeDefaulterItem(avatar: String, name: String, meta: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(AdminColors.alertRedBg, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = avatar, color = AdminColors.alertRed, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = AdminColors.inkPrimary, style = AdminTypography.alertTitle)
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = meta, color = AdminColors.inkSecondary, style = AdminTypography.metaText)
        }
        Box(
            modifier = Modifier
                .background(AdminColors.alertRedBg, RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(text = "Overdue", color = AdminColors.alertRed, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun RecordsDocumentsEmpty(
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(32.dp),
        radius = 14
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Document Library",
                color = AdminColors.inkPrimary,
                style = AdminTypography.alertTitle
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Circulars, timetables and holiday lists are uploaded via Announcements and the Academic Calendar.",
                color = AdminColors.inkSecondary,
                style = AdminTypography.metaText
            )
        }
    }
}
