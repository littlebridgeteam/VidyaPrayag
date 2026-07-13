/*
 * File: PewsEffectivenessScreenV2.kt
 * Module: ui.v2.screens.school
 *
 * The LEARN dashboard — shows intervention effectiveness rollup
 * (improved / unchanged / worsened) and cohort risk trend over time.
 * Admin-only, accessible from the PEWS cohort screen.
 */
package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsEffectivenessDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsTrendPointDto
import com.littlebridge.enrollplus.feature.pews.presentation.PewsEffectivenessState
import com.littlebridge.enrollplus.feature.pews.presentation.PewsEffectivenessViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

@Composable
fun PewsEffectivenessScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PewsEffectivenessViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(title = appString(StringKeys.SCH_EFFECTIVENESS), onBack = onBack)
        VPullRefresh(isRefreshing = state.isLoading && !state.isEmpty, onRefresh = { viewModel.load() }) {
            PewsEffectivenessContent(
                state = state,
                onRetry = viewModel::load,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PewsEffectivenessContent(
    state: PewsEffectivenessState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
        VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.isEmpty,
        emptyIcon = VIcons.ShieldCheck,
        emptyTitle = appString(StringKeys.SCH_NO_DATA_YET),
        emptyBody = appString(StringKeys.SCH_EFFECTIVENESS_DESC),
        onRetry = onRetry,
        modifier = modifier,
        skeleton = { SkeletonDashboard() },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.trend?.effectiveness?.let { eff ->
                item { EffectivenessSummaryCard(eff, modifier = Modifier.staggeredItemEntrance(0, true)) }
            }
            state.trend?.points?.let { points ->
                if (points.size > 1) {
                    item { TrendChartCard(points, modifier = Modifier.staggeredItemEntrance(1, true)) }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun EffectivenessSummaryCard(eff: PewsEffectivenessDto, modifier: Modifier = Modifier) {
        val resolved = eff.done + eff.dismissed
    val total = eff.improved + eff.unchanged + eff.worsened
    val improvedRate = if (total > 0) (eff.improved * 100 / total) else 0

    VCard(modifier.fillMaxWidth()) {
        Text(
            appString(StringKeys.SCH_INTERVENTION_OUTCOMES),
            style = VTheme.type.label.copy(color = VTheme.colors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
        )
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniStat(appString(StringKeys.SCH_OPEN), "${eff.open}", VTheme.colors.ink)
            MiniStat(appString(StringKeys.SCH_RESOLVED), "$resolved", VTheme.colors.ink)
            MiniStat(appString(StringKeys.SCH_IMPROVED), "$improvedRate%", VTheme.colors.success)
        }

        Spacer(Modifier.height(16.dp))

        OutcomeBar(appString(StringKeys.SCH_IMPROVED), eff.improved, eff.total, VTheme.colors.success)
        Spacer(Modifier.height(6.dp))
        OutcomeBar(appString(StringKeys.SCH_NO_CHANGE), eff.unchanged, eff.total, VTheme.colors.ink3)
        Spacer(Modifier.height(6.dp))
        OutcomeBar(appString(StringKeys.SCH_WORSENED), eff.worsened, eff.total, VTheme.colors.error)
    }
}

@Composable
private fun TrendChartCard(points: List<PewsTrendPointDto>, modifier: Modifier = Modifier) {
        val maxTotal = points.maxOfOrNull { it.total } ?: 0

    VCard(modifier.fillMaxWidth()) {
        Text(
            appString(StringKeys.SCH_RISK_TREND_30),
            style = VTheme.type.label.copy(color = VTheme.colors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
        )
        Spacer(Modifier.height(12.dp))

        points.takeLast(15).forEach { p ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    p.runDate.takeLast(5),
                    style = VTheme.type.caption.copy(color = VTheme.colors.ink3).copy(fontSize = 10.sp),
                    modifier = Modifier.weight(0.25f),
                )
                Box(
                    Modifier.weight(0.75f).height(16.dp).clip(RoundedCornerShape(4.dp)).background(VTheme.colors.cream),
                ) {
                    Row(Modifier.fillMaxSize()) {
                        val highFrac = if (maxTotal > 0) p.high.toFloat() / maxTotal else 0f
                        val medFrac = if (maxTotal > 0) p.medium.toFloat() / maxTotal else 0f
                        val watchFrac = if (maxTotal > 0) p.watch.toFloat() / maxTotal else 0f
                        Box(Modifier.fillMaxWidth(highFrac).fillMaxSize().background(VTheme.colors.error))
                        Box(Modifier.fillMaxWidth(medFrac).fillMaxSize().background(VTheme.colors.gold))
                        Box(Modifier.fillMaxWidth(watchFrac).fillMaxSize().background(VTheme.colors.success))
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TrendLegend(appString(StringKeys.SCH_HIGH), VTheme.colors.error)
            TrendLegend(appString(StringKeys.SCH_MEDIUM), VTheme.colors.gold)
            TrendLegend(appString(StringKeys.SCH_WATCH), VTheme.colors.success)
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = color).copy(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold),
        )
        Text(label, style = VTheme.type.caption.copy(color = VTheme.colors.ink3).copy(fontSize = 10.sp))
    }
}

@Composable
private fun OutcomeBar(label: String, value: Int, total: Int, color: androidx.compose.ui.graphics.Color) {
        val pct = if (total > 0) (value * 100 / total) else 0
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = VTheme.type.caption.copy(color = VTheme.colors.ink2).copy(fontSize = 12.sp))
            Text("$value", style = VTheme.type.caption.copy(color = VTheme.colors.ink).copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold))
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(VTheme.colors.cream),
        ) {
            Box(Modifier.fillMaxWidth(pct / 100f).fillMaxSize().clip(RoundedCornerShape(3.dp)).background(color))
        }
    }
}

@Composable
private fun TrendLegend(label: String, color: androidx.compose.ui.graphics.Color) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Text(label, style = VTheme.type.caption.copy(color = VTheme.colors.ink3).copy(fontSize = 10.sp))
    }
}
