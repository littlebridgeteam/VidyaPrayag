package com.littlebridge.enrollplus.ui.v2.screens.teacher

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.health.domain.model.HealthAlertDto
import com.littlebridge.enrollplus.feature.health.presentation.TeacherHealthAlertsViewModel
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherHealthAlertsScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherHealthAlertsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VtC

    Column(
        modifier
            .fillMaxSize()
            .background(c.cream)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = appString(StringKeys.TC_HEALTH_ALERTS), onBack = onBack)

        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.alerts.isEmpty(),
            onRetry = viewModel::load,
            emptyTitle = appString(StringKeys.TC_NO_HEALTH_ALERTS),
            emptyBody = appString(StringKeys.TC_NO_HEALTH_ALERTS_DESC),
            emptyIcon = VIcons.Heart,
            modifier = Modifier.fillMaxSize(),
        ) {
            TeacherHealthAlertsContent(alerts = state.alerts)
        }
    }
}

@Composable
private fun TeacherHealthAlertsContent(alerts: List<HealthAlertDto>) {
    val c = VtC
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            appString(StringKeys.TC_HEALTH_ALERTS_LIST_DESC),
            style = VtT.caption.coloredV(c.ink2),
        )

        alerts.forEach { alert ->
            VCard {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(c.danger.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(VIcons.AlertTriangle, contentDescription = null, tint = c.dangerInk, modifier = Modifier.size(20.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(alert.studentName, style = VtT.bodyStrong.coloredV(c.ink))
                        Text("${alert.className} - ${alert.section}", style = VtT.caption.coloredV(c.ink3))
                        val allergies = parseJsonArray(alert.allergies)
                        val conditions = parseJsonArray(alert.chronicConditions)
                        if (allergies.isNotEmpty()) {
                            Text(appString(StringKeys.TC_ALLERGIES_LABEL, "list" to allergies.joinToString(", ")), style = VtT.caption.coloredV(c.dangerInk))
                        }
                        if (conditions.isNotEmpty()) {
                            Text(appString(StringKeys.TC_CONDITIONS_LABEL, "list" to conditions.joinToString(", ")), style = VtT.caption.coloredV(c.ink2))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(120.dp))
    }
}

private fun parseJsonArray(json: String): List<String> {
    if (json.isBlank() || json == "[]") return emptyList()
    return runCatching {
        json
            .removeSurrounding("[", "]")
            .split(",")
            .map { it.trim().removeSurrounding("\"") }
            .filter { it.isNotBlank() }
    }.getOrDefault(emptyList())
}
