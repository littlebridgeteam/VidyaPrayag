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
    modifier: Modifier = Modifier
) {
    var activeSubtab by remember { mutableIntStateOf(0) }
    val subtabs = listOf("Coverage", "Pace", "Attendance", "Marks", "Fee", "Documents")

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
                RecordsSummary(
                    label = "Overall Syllabus Coverage",
                    value = "78%",
                    sub = "Across all classes and subjects",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                RecordsBarChart(
                    bars = listOf(
                        BarData("Mathematics", 85, 131, "85%", BarFillType.GOOD),
                        BarData("English", 82, 131, "82%", BarFillType.GOOD),
                        BarData("Science", 74, 131, "74%", BarFillType.MID),
                        BarData("Social Studies", 71, 123, "71%", BarFillType.MID),
                        BarData("Hindi", 68, 131, "68%", BarFillType.LOW)
                    ),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                SectionLabel(
                    text = "Alerts",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 6.dp)
                )
                RecordsAlertCard(
                    title = "Class 8-B Hindi — 15% behind",
                    meta = "Unit 3 incomplete · Target was Jan 10",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )
                RecordsAlertCard(
                    title = "Class 6-A Science — 8% behind",
                    meta = "Practical sessions pending",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )
            }
            1 -> { // Pace
                RecordsSummary(
                    label = "Pace Status — All Classes",
                    value = "12 On Track",
                    sub = "3 ahead · 2 behind · 12 on track",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                RecordsAlertCard(
                    title = "Class 8-B Hindi — Behind by 3 days",
                    meta = "Deviation: -12% · Teacher: Sunita Nair",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )
                RecordsAlertCard(
                    title = "Class 6-A Science — Behind by 2 days",
                    meta = "Deviation: -8% · Teacher: Rajesh Kumar",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )
                RecordsAlertCard(
                    title = "Class 7-A Mathematics — Ahead by 2 days",
                    meta = "Deviation: +6% · Teacher: Priya Sharma",
                    isPositive = true,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )
                RecordsAlertCard(
                    title = "Class 7-B English — Ahead by 1 day",
                    meta = "Deviation: +4% · Teacher: Meera Iyer",
                    isPositive = true,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                )
            }
            2 -> { // Attendance
                RecordsSummary(
                    label = "Overall Attendance",
                    value = "95%",
                    sub = "1,247 of 1,312 present today",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                RecordsBarChart(
                    bars = listOf(
                        BarData("Class 6-A", 96, 131, "96%", BarFillType.GOOD),
                        BarData("Class 6-B", 94, 131, "94%", BarFillType.GOOD),
                        BarData("Class 7-A", 91, 131, "91%", BarFillType.GOOD),
                        BarData("Class 7-B", 88, 131, "88%", BarFillType.MID),
                        BarData("Class 8-A", 85, 131, "85%", BarFillType.MID),
                        BarData("Class 8-B", 72, 131, "72%", BarFillType.LOW)
                    ),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }
            3 -> { // Marks
                RecordsSummary(
                    label = "Overall Average — Term 3",
                    value = "76.4%",
                    sub = "Across 24 assessments · 1,312 students",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                RecordsBarChart(
                    bars = listOf(
                        BarData("Class 6-A", 82, 131, "82%", BarFillType.GOOD),
                        BarData("Class 6-B", 78, 131, "78%", BarFillType.GOOD),
                        BarData("Class 7-A", 75, 131, "75%", BarFillType.MID),
                        BarData("Class 7-B", 74, 131, "74%", BarFillType.MID),
                        BarData("Class 8-A", 73, 131, "73%", BarFillType.MID),
                        BarData("Class 8-B", 68, 131, "68%", BarFillType.LOW)
                    ),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }
            4 -> { // Fee
                FeeRecordsContent(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
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
