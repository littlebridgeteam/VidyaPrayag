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
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.reportcard.domain.model.ReportCardModels
import com.littlebridge.enrollplus.feature.reportcard.presentation.AdminReportPublishViewModel
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography

/**
 * AdminReportPublishScreen — admin oversight for report card generation
 * across all classes. Shows draft status counts and allows publishing
 * approved drafts per class.
 */
@Composable
fun AdminReportPublishScreen(
    onBack: () -> Unit,
    viewModel: AdminReportPublishViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
        var termInput by remember { mutableStateOf("Term 1") }

    LaunchedEffect(Unit) { viewModel.loadTermConfig() }

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
            Text(appString(StringKeys.SCH_REPORT_CARD_PUBLISHING), style = VTypography.h3.copy(color = VColors.ink))
        }

        // Term input + load
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = termInput,
                onValueChange = { termInput = it },
                label = { Text(appString(StringKeys.SCH_TERM)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            VButton(
                text = appString(StringKeys.COMMON_BUTTON_APPLY),
                onClick = { viewModel.loadOversight(termInput) },
                size = VButtonSize.Sm,
            )
        }

        // Term config info
        state.termConfig?.let { config ->
            VCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ConfigChip(appString(StringKeys.SCH_ENABLED), if (config.enabled) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO))
                    ConfigChip(appString(StringKeys.SCH_CURRENT_TERM), config.currentTerm ?: appString(StringKeys.SCH_NOT_SET))
                    ConfigChip(appString(StringKeys.SCH_CONCURRENCY), config.batchConcurrency.toString())
                    ConfigChip(appString(StringKeys.SCH_FALLBACK), if (config.fallbackOnAiFail) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO))
                }
            }
        }

        state.publishedCount?.let { count ->
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                VCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(VIcons.Check, contentDescription = null, tint = VColors.success, modifier = Modifier.size(16.dp))
                        Text(appString(StringKeys.SCH_N_REPORTS_PUBLISHED, "count" to count.toString()), style = VTypography.body.copy(color = VColors.ink))
                    }
                }
            }
        }

        when {
            state.isLoading -> {
                SkeletonList(rows = 4, withAvatar = false)
            }
            state.error != null -> {
                VStateHost(loading = false, error = state.error, isEmpty = false, onRetry = { viewModel.loadOversight(termInput) }) {}
            }
            state.oversight != null -> {
                val oversight = state.oversight ?: return
                VPullRefresh(isRefreshing = state.publishing, onRefresh = { viewModel.loadOversight(termInput) }) {
                    LazyColumn(
                        Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(oversight.classes) { row ->
                            OversightClassCard(
                                row = row,
                                publishing = state.publishing,
                                onPublish = { viewModel.publishClass(row.className, row.section, row.term) },
                                modifier = Modifier.staggeredItemEntrance(oversight.classes.indexOf(row), oversight.classes.isNotEmpty()),
                            )
                        }
                    }
                }
            }
            else -> {
                VStateHost(loading = false, error = null, isEmpty = true, emptyTitle = appString(StringKeys.SCH_NO_DATA_YET), onRetry = { viewModel.loadOversight(termInput) }) {}
            }
        }
    }
}

@Composable
private fun ConfigChip(label: String, value: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = VTypography.body.copy(color = VColors.ink).copy(fontWeight = FontWeight.Medium, fontSize = 13.sp))
        Text(label, style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 10.sp))
    }
}

@Composable
private fun OversightClassCard(
    row: ReportCardModels.ClassOversightRow,
    publishing: Boolean,
    onPublish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("${row.className} ${row.section}", style = VTypography.h3.copy(color = VColors.ink).copy(fontSize = 15.sp))
                Text("${row.totalDrafts} " + appString(StringKeys.SCH_DRAFTS), style = VTypography.caption.copy(color = VColors.ink2))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(appString(StringKeys.SCH_DRAFT), row.draftCount, VColors.gold)
                StatusChip(appString(StringKeys.SCH_FLAGGED), row.flaggedCount, VColors.error)
                StatusChip(appString(StringKeys.SCH_APPROVED), row.approvedCount, VColors.success)
                StatusChip(appString(StringKeys.SCH_PUBLISHED), row.publishedCount, VColors.violet)
            }

            if (row.approvedCount > 0 && row.publishedCount == 0) {
                VButton(
                    text = if (publishing) appString(StringKeys.SCH_PUBLISHING) else appString(StringKeys.SCH_PUBLISH_N_APPROVED, "count" to row.approvedCount.toString()),
                    onClick = onPublish,
                    size = VButtonSize.Sm,
                    enabled = !publishing,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
        Row(
        Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("$count", style = VTypography.body.copy(color = color).copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
        Text(label, style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 10.sp))
    }
}
