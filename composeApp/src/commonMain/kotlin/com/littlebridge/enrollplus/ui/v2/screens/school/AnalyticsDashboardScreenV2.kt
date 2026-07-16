package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewFeeAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewKpi
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewParentEngagement
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewSchoolPulse
import com.littlebridge.enrollplus.feature.admin.presentation.AnalyticsCardData
import com.littlebridge.enrollplus.feature.admin.presentation.AnalyticsDashboardState
import com.littlebridge.enrollplus.feature.admin.presentation.AnalyticsDashboardViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.InsightItem
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnalyticsDashboardScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AnalyticsDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    Column(modifier.fillMaxSize().statusBarsPadding()
        .imePadding()
        .navigationBarsPadding()) {
        VBackHeader(title = appString(StringKeys.SCH_ANALYTICS), onBack = onBack, pinRouteId = "overlay_analytics")
        VPullRefresh(isRefreshing = state.isLoading && state.cards.isNotEmpty(), onRefresh = { viewModel.loadOverview() }) {
            AnalyticsContent(
                state = state,
                onRetry = viewModel::loadOverview,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun AnalyticsContent(
    state: AnalyticsDashboardState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.performanceTrend.isEmpty() &&
                state.cards.isEmpty() &&
                state.insights.isEmpty() &&
                state.overview == null,
            emptyTitle = appString(StringKeys.SCH_NO_ANALYTICS),
            emptyBody = appString(StringKeys.SCH_NO_ANALYTICS_DESC),
            emptyIcon = VIcons.TrendingUp,
            onRetry = onRetry,
            skeleton = { SkeletonDashboard() },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                state.overview?.let { ov ->
                    if (ov.schoolPulse.score > 0) {
                        SchoolPulseSection(pulse = ov.schoolPulse)
                    }

                    val availableKpis = ov.kpis.filter { it.available }
                    if (availableKpis.isNotEmpty()) {
                        KpiStripRow(kpis = availableKpis)
                    }
                }

                if (state.performanceTrend.isNotEmpty()) {
                    PerformanceTrendSection(
                        values = state.performanceTrend,
                        labels = state.trendLabels,
                        growth = state.currentGrowth,
                    )
                }

                state.overview?.feeAnalytics?.takeIf { it.available }?.let { fa ->
                    FeeAnalyticsSection(fa = fa)
                }

                state.overview?.parentEngagement?.takeIf { it.available }?.let { pe ->
                    ParentEngagementSection(pe = pe)
                }

                if (state.cards.isNotEmpty()) {
                    VSectionHeader(title = "Subject Analytics")
                    val pairs = state.cards.chunked(2)
                    pairs.forEach { pair ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.weight(1f)) { AnalyticsCard(pair[0]) }
                            if (pair.size > 1) {
                                Box(Modifier.weight(1f)) { AnalyticsCard(pair[1]) }
                            } else {
                                Box(Modifier.weight(1f))
                            }
                        }
                    }
                }

                if (state.insights.isNotEmpty()) {
                    VSectionHeader(title = appString(StringKeys.SCH_INSIGHTS))
                    state.insights.forEach { item -> InsightCard(item) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// School Pulse — composite health score with category breakdown
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SchoolPulseSection(pulse: OverviewSchoolPulse) {
    val statusColor = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.success
        "HEALTHY" -> VColors.mint
        "WATCH" -> VColors.gold
        "CRITICAL" -> VColors.coral
        else -> VColors.ink3
    }
    val statusBg = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.successSoft
        "HEALTHY" -> VColors.mintSoft
        "WATCH" -> VColors.goldSoft
        "CRITICAL" -> VColors.coralSoft
        else -> VColors.surfaceTint
    }

    SectionCard(tint = statusBg) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScoreRing(score = pulse.score, color = statusColor, modifier = Modifier.size(72.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "School Pulse",
                    style = VTypography.label.copy(color = VColors.ink3),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = pulse.message.ifBlank { "School health overview" },
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = pulse.status.lowercase().replaceFirstChar { it.uppercase() },
                    style = VTypography.caption.copy(fontWeight = FontWeight.Bold, color = statusColor),
                )
            }
        }

        val availableCats = pulse.categories.filter { it.available }
        if (availableCats.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            availableCats.forEachIndexed { idx, cat ->
                if (idx > 0) Spacer(Modifier.height(10.dp))
                PulseCategoryBar(
                    label = cat.label,
                    score = cat.score,
                    weight = cat.weight,
                    color = statusColor,
                )
            }
        }
    }
}

@Composable
private fun ScoreRing(score: Int, color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 6f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * (score / 100f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp),
                color = color,
            )
            Text(
                text = "/ 100",
                style = VTypography.caption.copy(fontSize = 9.sp, color = VColors.ink3),
            )
        }
    }
}

@Composable
private fun PulseCategoryBar(label: String, score: Int, weight: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = VTypography.caption.copy(color = VColors.ink2),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${score}%",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold, color = color),
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color.copy(alpha = 0.12f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// KPI Strip — compact horizontal metrics row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun KpiStripRow(kpis: List<OverviewKpi>) {
    val displayKpis = kpis.take(4)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        displayKpis.forEach { kpi ->
            KpiChip(kpi = kpi, modifier = Modifier.weight(1f))
        }
        if (displayKpis.size < 4) {
            repeat(4 - displayKpis.size) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KpiChip(kpi: OverviewKpi, modifier: Modifier = Modifier) {
    val accentColor = when (kpi.deltaDirection) {
        "up" -> VColors.success
        "down" -> VColors.coral
        else -> VColors.violet
    }
    val icon = when (kpi.key) {
        "students" -> VIcons.UsersGroup
        "teachers" -> VIcons.GraduationCap
        "attendance" -> VIcons.ListChecks
        "fees" -> VIcons.Wallet
        "parents" -> VIcons.Heart
        "approvals" -> VIcons.ShieldCheck
        "events" -> VIcons.Calendar
        else -> VIcons.Target
    }

    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(VShapes.sm)
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(13.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = formatKpiValue(kpi),
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
            color = VColors.ink,
            maxLines = 1,
        )
        Text(
            text = kpi.label,
            style = VTypography.caption.copy(fontSize = 9.sp, color = VColors.ink3),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun formatKpiValue(kpi: OverviewKpi): String {
    val v = kpi.value
    return when {
        kpi.unit == "%" -> "$v%"
        kpi.unit == "\u20B9" || kpi.unit == "INR" -> "\u20B9${if (v > 99999) "${v / 1000}k" else v}"
        v > 999 -> "${v / 1000}.${(v % 1000) / 100}k"
        else -> v.toString()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Performance Trend — area chart with grid lines and y-axis
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PerformanceTrendSection(
    values: List<Float>,
    labels: List<String>,
    growth: String,
) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Attendance Trend",
                    style = VTypography.label.copy(color = VColors.ink3),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = growth,
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = VColors.ink),
                )
            }
            VBadge(text = "6 months", tone = VBadgeTone.Arctic)
        }
        Spacer(Modifier.height(16.dp))
        TrendChart(values = values, labels = labels)
    }
}

@Composable
private fun TrendChart(values: List<Float>, labels: List<String>) {
    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
        ) {
            if (values.size < 2) return@Canvas
            val min = 0f
            val max = 1f
            val range = (max - min).takeIf { it > 0f } ?: 1f
            val stepX = size.width / (values.size - 1).coerceAtLeast(1)
            val chartHeight = size.height
            val gridColor = VColors.line.copy(alpha = 0.5f)

            // Horizontal grid lines at 25%, 50%, 75%, 100%
            for (pct in listOf(0.25f, 0.5f, 0.75f, 1.0f)) {
                val y = chartHeight - pct * chartHeight
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                )
            }

            // Build the area path
            val linePath = Path()
            values.forEachIndexed { i, v ->
                val x = stepX * i
                val y = chartHeight - ((v - min) / range) * chartHeight
                if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
            }

            // Area fill
            val areaPath = Path().apply {
                addPath(linePath)
                lineTo(stepX * (values.size - 1), chartHeight)
                lineTo(0f, chartHeight)
                close()
            }
            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(VColors.sky.copy(alpha = 0.25f), VColors.sky.copy(alpha = 0.0f)),
                ),
            )

            // Line stroke
            drawPath(
                path = linePath,
                color = VColors.sky,
                style = Stroke(width = 2.5f),
            )

            // Data point dots
            values.forEachIndexed { i, v ->
                val x = stepX * i
                val y = chartHeight - ((v - min) / range) * chartHeight
                drawCircle(color = VColors.white, radius = 5f, center = Offset(x, y))
                drawCircle(color = VColors.sky, radius = 3f, center = Offset(x, y))
            }
        }
        if (labels.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                labels.forEach { l ->
                    Text(
                        l,
                        style = VTypography.caption.copy(fontSize = 10.sp, color = VColors.ink3),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Fee Analytics — collected/pending/rate + monthly bar chart
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FeeAnalyticsSection(fa: OverviewFeeAnalytics) {
    val rateColor = when {
        fa.collectionRate >= 90 -> VColors.success
        fa.collectionRate >= 70 -> VColors.violet
        else -> VColors.gold
    }

    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(rateColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${fa.collectionRate}%",
                    style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
                    color = rateColor,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fee Collection",
                    style = VTypography.label.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "\u20B9${formatAmount(fa.totalCollected)} collected",
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink2),
                )
                Text(
                    text = "\u20B9${formatAmount(fa.pending)} pending",
                    style = VTypography.caption.copy(color = VColors.coral),
                )
            }
        }

        if (fa.trend.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            FeeTrendBars(trend = fa.trend, barColor = rateColor)
        }
    }
}

@Composable
private fun FeeTrendBars(trend: List<OverviewFeePoint>, barColor: Color) {
    val maxVal = (trend.maxOfOrNull { it.value } ?: 100).coerceAtLeast(1)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            trend.forEach { point ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height((point.value / maxVal.toFloat() * 60f).coerceAtLeast(2f).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(barColor.copy(alpha = 0.7f)),
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = point.label,
                        style = VTypography.caption.copy(fontSize = 9.sp, color = VColors.ink3),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

private fun formatAmount(v: Double): String = when {
    v >= 10000000 -> "${(v / 10000000).let { kotlin.math.round(it * 10) / 10 }}Cr"
    v >= 100000 -> "${(v / 100000).let { kotlin.math.round(it * 10) / 10 }}L"
    v >= 1000 -> "${(v / 1000).toInt()}k"
    else -> v.toInt().toString()
}

// ─────────────────────────────────────────────────────────────────────────────
// Parent Engagement — active % + class leaderboard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ParentEngagementSection(pe: OverviewParentEngagement) {
    val engagementColor = if (pe.activeParentsPct >= 70) VColors.success else VColors.gold

    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(engagementColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${pe.activeParentsPct}%",
                    style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
                    color = engagementColor,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Parent Engagement",
                    style = VTypography.label.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${pe.activeParents} of ${pe.totalParents} parents active",
                    style = VTypography.caption.copy(color = VColors.ink2),
                )
                if (pe.mostEngagedClass.isNotBlank()) {
                    Text(
                        text = "Top: ${pe.mostEngagedClass}",
                        style = VTypography.caption.copy(color = VColors.violet, fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }

        if (pe.leaderboard.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            pe.leaderboard.take(5).forEachIndexed { idx, lc ->
                if (idx > 0) Spacer(Modifier.height(8.dp))
                LeaderboardBar(
                    rank = idx + 1,
                    label = lc.className,
                    score = lc.score,
                    color = engagementColor,
                )
            }
        }
    }
}

@Composable
private fun LeaderboardBar(rank: Int, label: String, score: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "$rank",
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold, color = VColors.ink3),
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            style = VTypography.caption.copy(color = VColors.ink2),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color.copy(alpha = 0.12f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color),
            )
        }
        Text(
            text = "${score}%",
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold, color = color),
            modifier = Modifier.width(36.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Analytics Cards — subject-level metrics
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AnalyticsCard(card: AnalyticsCardData) {
    VCard {
        Text(
            text = card.title,
            style = VTypography.label.copy(color = VColors.ink3),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = card.value,
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = VColors.ink),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (card.subValue.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = card.subValue,
                style = VTypography.caption.copy(color = VColors.ink2),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val trend = card.trend
        if (!trend.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            VBadge(text = trend, tone = VBadgeTone.Arctic)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Insights — actionable, prioritized
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun InsightCard(item: InsightItem) {
    VCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(item.iconColor).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(item.iconColor)),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.description.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        style = VTypography.caption.copy(color = VColors.ink2),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared section card — consistent container with optional tint
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    tint: Color = VColors.surfaceCard,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(tint, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) {
        content()
    }
}
