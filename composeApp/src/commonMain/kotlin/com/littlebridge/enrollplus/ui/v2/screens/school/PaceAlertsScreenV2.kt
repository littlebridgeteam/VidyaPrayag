package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.littlebridge.enrollplus.feature.admin.presentation.PaceAlertsState
import com.littlebridge.enrollplus.feature.admin.presentation.PaceAlertsViewModel
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceAlertDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceSnapshotDto
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.screens.VErrorState
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PaceAlertsScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaceAlertsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VTheme.colors

    Box(modifier.fillMaxSize().padding(16.dp)) {
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = c.accent, modifier = Modifier.size(36.dp))
                }
            }
            state.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    VErrorState(
                        message = state.errorMessage ?: "",
                        onRetry = { viewModel.load() },
                    )
                }
            }
            else -> Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Pace Alerts", style = VTheme.type.h1.colored(c.ink))
                    VBadge(
                        text = "${state.alerts.size} active",
                        tone = if (state.alerts.isEmpty()) VBadgeTone.Accent else VBadgeTone.Warning,
                    )
                }

                if (state.alerts.isNotEmpty()) {
                    VLabel("Active Alerts")
                    state.alerts.forEach { alert -> PaceAlertCard(alert, state.resolvingAlertId, viewModel) }
                }

                if (state.snapshots.isNotEmpty()) {
                    VLabel("Pace Snapshots")
                    state.snapshots.forEach { snap -> PaceSnapshotCard(snap) }
                }

                if (state.alerts.isEmpty() && state.snapshots.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "No pace alerts or snapshots. All classes are on track.",
                            style = VTheme.type.body.colored(c.ink2),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaceAlertCard(
    alert: PaceAlertDto,
    resolvingId: String?,
    viewModel: PaceAlertsViewModel,
) {
    val c = VTheme.colors
    val tone = when (alert.level) {
        "CRITICAL" -> VBadgeTone.Danger
        "BEHIND" -> VBadgeTone.Warning
        "AHEAD" -> VBadgeTone.Accent
        else -> VBadgeTone.Neutral
    }
    VCard {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${alert.className} - ${alert.section}", style = VTheme.type.bodyStrong.colored(c.ink))
                VBadge(text = alert.level, tone = tone)
            }
            Text("Subject: ${alert.subject}", style = VTheme.type.caption.colored(c.ink2))
            Text("Teacher: ${alert.teacherName}", style = VTheme.type.caption.colored(c.ink2))
            if (alert.message.isNotBlank()) {
                Text(alert.message, style = VTheme.type.body.colored(c.ink))
            }
            if (alert.resolvedAt == null) {
                val isResolving = resolvingId == alert.id
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.End) {
                    VBadge(
                        text = if (isResolving) "Resolving..." else "Tap to resolve",
                        tone = VBadgeTone.Neutral,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaceSnapshotCard(snap: PaceSnapshotDto) {
    val c = VTheme.colors
    val statusTone = when (snap.status) {
        "ON_TRACK" -> VBadgeTone.Accent
        "BEHIND" -> VBadgeTone.Warning
        "CRITICAL" -> VBadgeTone.Danger
        "AHEAD" -> VBadgeTone.Accent
        else -> VBadgeTone.Neutral
    }
    VCard {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${snap.className} - ${snap.section} • ${snap.subject}", style = VTheme.type.bodyStrong.colored(c.ink))
                VBadge(text = snap.status, tone = statusTone)
            }
            Text("Teacher: ${snap.teacherName}", style = VTheme.type.caption.colored(c.ink2))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VLabel("Covered: ${snap.coveredTopics}/${snap.totalTopics}")
                VLabel("Expected: ${snap.expectedPct}%")
                VLabel("Actual: ${snap.actualPct}%")
                VLabel("Deviation: ${snap.deviationPct}%")
            }
        }
    }
}
