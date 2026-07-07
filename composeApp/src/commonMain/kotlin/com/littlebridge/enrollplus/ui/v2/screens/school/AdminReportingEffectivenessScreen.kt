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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.feature.reportcard.domain.model.ReportCardModels
import com.littlebridge.enrollplus.feature.reportcard.presentation.AdminReportEffectivenessViewModel
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography

/**
 * AdminReportingEffectivenessScreen — shows the Learn/Flywheel data:
 * effectiveness priors, projection accuracy, and cohort patterns.
 */
@Composable
fun AdminReportingEffectivenessScreen(
    onBack: () -> Unit,
    viewModel: AdminReportEffectivenessViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
        var currentTerm by remember { mutableStateOf("Term 2") }
    var previousTerm by remember { mutableStateOf("Term 1") }

    LaunchedEffect(Unit) { viewModel.loadEffectiveness() }

    Column(
        Modifier.fillMaxSize().background(VColors.surface),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VButton(text = appString(StringKeys.COMMON_BUTTON_BACK), onClick = onBack, variant = VButtonVariant.Secondary, size = VButtonSize.Sm)
            Text(appString(StringKeys.SCH_REPORTING_EFFECTIVENESS), style = VTypography.h3.copy(color = VColors.ink))
        }

        // Flywheel trigger
        VCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(appString(StringKeys.SCH_RUN_FLYWHEEL), style = VTypography.label.copy(color = VColors.ink).copy(fontWeight = FontWeight.Bold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = currentTerm,
                        onValueChange = { currentTerm = it },
                        label = { Text(appString(StringKeys.SCH_CURRENT)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = previousTerm,
                        onValueChange = { previousTerm = it },
                        label = { Text(appString(StringKeys.SCH_PREVIOUS)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                VButton(
                    text = if (state.runningFlywheel) appString(StringKeys.SCH_RUNNING) else appString(StringKeys.SCH_RUN_FLYWHEEL_BTN),
                    onClick = { viewModel.runFlywheel(currentTerm, previousTerm) },
                    enabled = !state.runningFlywheel,
                    size = VButtonSize.Sm,
                )
            }
        }

        state.flywheelResult?.let { results ->
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                VCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(VIcons.Check, contentDescription = null, tint = VColors.success, modifier = Modifier.size(16.dp))
                        Text(appString(StringKeys.SCH_FLYWHEEL_COMPLETE, "count" to results.size.toString()), style = VTypography.body.copy(color = VColors.ink))
                    }
                }
            }
        }

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VColors.violet)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error!!, style = VTypography.body.copy(color = VColors.error))
                }
            }
            else -> {
                LazyColumn(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.effectiveness) { eff ->
                        EffectivenessCard(eff)
                    }
                }
            }
        }
    }
}

@Composable
private fun EffectivenessCard(eff: ReportCardModels.EffectivenessReport) {
        val scoreColor = when {
        eff.effectivenessScore >= 0.7 -> VColors.success
        eff.effectivenessScore >= 0.4 -> VColors.gold
        else -> VColors.error
    }
    VCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(eff.focusArea, style = VTypography.body.copy(color = VColors.ink).copy(fontWeight = FontWeight.Medium))
                Text("${(eff.effectivenessScore * 100).toInt()}%", style = VTypography.h3.copy(color = scoreColor).copy(fontSize = 16.sp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(appString(StringKeys.SCH_N_IMPROVED, "improved" to eff.studentsImproved.toString(), "targeted" to eff.studentsTargeted.toString()),
                    style = VTypography.caption.copy(color = VColors.ink2))
                VBadge(text = eff.confidence, tone = when (eff.confidence) {
                    "high" -> VBadgeTone.Success
                    "medium" -> VBadgeTone.Arctic
                    "low" -> VBadgeTone.Warning
                    else -> VBadgeTone.Neutral
                })
            }
        }
    }
}
