package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.ClassPTMProgress
import com.littlebridge.enrollplus.feature.admin.presentation.PTMHistoryItem
import com.littlebridge.enrollplus.feature.admin.presentation.SchedulePTMState
import com.littlebridge.enrollplus.feature.admin.presentation.SchedulePTMViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

/**
 * SchedulePtmScreenV2 — Parent-Teacher Meeting console (admin overlay).
 *
 * Wired to [SchedulePTMViewModel] (`GET /api/v1/school/ptm`, `POST /api/v1/school/ptm`).
 *
 * Layout:
 *   • Active event banner VCard (title + date + slot + KPIs: expected/checked-in,
 *     invites delivered, read receipts)
 *   • PTM history list (date · title · turnout/total)
 *   • Per-class progress list (class · teacher · metCount/total + VProgressBar)
 *   • "Schedule new PTM" VButton opens an inline composer (title / date / slot
 *     VInputs + Create VButton with loading state).
 *
 * Three states via [VStateHost] (LAW 3).
 */
@Composable
fun SchedulePtmScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SchedulePTMViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    Column(modifier.fillMaxSize().statusBarsPadding()
        .imePadding()
        .navigationBarsPadding()) {
        VBackHeader(title = appString(StringKeys.SCH_SCHEDULE_PTM), onBack = onBack, pinRouteId = "overlay_schedule_ptm")
        VPullRefresh(isRefreshing = state.isLoading && state.activeEventTitle.isNotBlank(), onRefresh = { viewModel.loadPtm() }) {
            SchedulePtmContent(
                state = state,
                onCreate = { title, date, slot, onDone -> viewModel.createPtm(title, date, slot, onDone) },
                onRetry = viewModel::loadPtm,
                onClearMessages = viewModel::clearMessages,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SchedulePtmContent(
    state: SchedulePTMState,
    onCreate: (String, String, String, (() -> Unit)?) -> Unit,
    onRetry: () -> Unit,
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier,
) {
        var composerOpen by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var slot by remember { mutableStateOf("") }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage.takeIf { !composerOpen },
            isEmpty = state.activeEventTitle.isBlank() &&
                state.history.isEmpty() &&
                state.classProgress.isEmpty() &&
                !composerOpen,
            emptyTitle = appString(StringKeys.SCH_NO_PTMS_YET),
            emptyBody = appString(StringKeys.SCH_NO_PTMS_DESC),
            emptyIcon = VIcons.Calendar,
            onRetry = onRetry,
            skeleton = { SkeletonDashboard() },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Schedule new PTM CTA / composer
                if (composerOpen) {
                    VCard {
                        Text(appString(StringKeys.SCH_NEW_PTM), style = VTypography.h3.copy(color = VColors.ink))
                        Spacer(Modifier.height(12.dp))
                        VInput(value = title, onValueChange = { title = it }, label = appString(StringKeys.SCH_TITLE), placeholder = appString(StringKeys.SCH_TITLE_PH))
                        Spacer(Modifier.height(8.dp))
                        VDatePicker(value = date, onValueChange = { date = it }, label = appString(StringKeys.SCH_DATE), placeholder = appString(StringKeys.SCH_PTM_DATE_PH))
                        Spacer(Modifier.height(8.dp))
                        VInput(value = slot, onValueChange = { slot = it }, label = appString(StringKeys.SCH_SLOT), placeholder = appString(StringKeys.SCH_SLOT_PH))
                        val info = state.infoMessage
                        if (info != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(info, style = VTypography.caption.copy(color = VColors.success))
                        }
                        val err = state.errorMessage
                        if (err != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(err, style = VTypography.caption.copy(color = VColors.coral))
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                VButton(
                                    text = appString(StringKeys.SCH_CLOSE),
                                    onClick = {
                                        composerOpen = false
                                        title = ""; date = ""; slot = ""
                                        onClearMessages()
                                    },
                                    full = true,
                                    variant = VButtonVariant.Secondary,
                                    tone = VButtonTone.Navy,
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                VButton(
                                    text = appString(StringKeys.SCH_CREATE),
                                    onClick = {
                                        onCreate(title, date, slot) {
                                            composerOpen = false
                                            title = ""; date = ""; slot = ""
                                        }
                                    },
                                    full = true,
                                    variant = VButtonVariant.Primary,
                                    tone = VButtonTone.Teal,
                                    loading = state.isCreating,
                                    enabled = title.isNotBlank() && date.isNotBlank() && slot.isNotBlank() && !state.isCreating,
                                )
                            }
                        }
                    }
                } else {
                    VButton(
                        text = appString(StringKeys.SCH_SCHEDULE_NEW_PTM),
                        onClick = { composerOpen = true },
                        full = true,
                        variant = VButtonVariant.Primary,
                        tone = VButtonTone.Teal,
                    )
                }

                // Active event banner
                if (state.activeEventTitle.isNotBlank()) {
                    VCard {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            VBadge(text = appString(StringKeys.SCH_ACTIVE), tone = VBadgeTone.Success)
                            Text(state.activeEventTitle, style = VTypography.h3.copy(color = VColors.ink))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${state.activeEventDate} · ${state.activeEventSlot}",
                            style = VTypography.caption.copy(color = VColors.ink2),
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                KpiTile(label = appString(StringKeys.SCH_EXPECTED), value = state.expectedParents.toString())
                            }
                            Box(Modifier.weight(1f)) {
                                KpiTile(label = appString(StringKeys.SCH_CHECKED_IN), value = state.checkedInParents.toString())
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                KpiTile(label = appString(StringKeys.SCH_INVITES_SENT), value = state.invitesDelivered.toString())
                            }
                            Box(Modifier.weight(1f)) {
                                KpiTile(label = appString(StringKeys.SCH_READ), value = state.readReceipts.toString())
                            }
                        }
                    }
                }

                // History
                if (state.history.isNotEmpty()) {
                    VSectionHeader(title = appString(StringKeys.SCH_HISTORY))
                    VCard {
                        state.history.forEachIndexed { i, h ->
                            if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
                            HistoryRow(h, modifier = Modifier.staggeredItemEntrance(i, state.history.isNotEmpty()))
                        }
                    }
                }

                // Per-class progress
                if (state.classProgress.isNotEmpty()) {
                    VSectionHeader(title = appString(StringKeys.SCH_CLASS_PROGRESS))
                    state.classProgress.forEachIndexed { i, cp -> ClassProgressCard(cp, modifier = Modifier.staggeredItemEntrance(i, state.classProgress.isNotEmpty())) }
                }
            }
        }
    }
}

@Composable
private fun KpiTile(label: String, value: String) {
        Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(VColors.cream)
            .padding(12.dp),
    ) {
        Text(label, style = VTypography.label.copy(color = VColors.ink3))
        Spacer(Modifier.height(4.dp))
        Text(value, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = VColors.ink))
    }
}

@Composable
private fun HistoryRow(h: PTMHistoryItem, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(h.title, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Text(h.date, style = VTypography.caption.copy(color = VColors.ink3))
        }
        Text(
            "${h.turnout}/${h.totalMet}",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink2),
        )
    }
}

@Composable
private fun ClassProgressCard(cp: ClassPTMProgress, modifier: Modifier = Modifier) {
    VCard(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(cp.className, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                Spacer(Modifier.height(2.dp))
                Text(cp.teacherName, style = VTypography.caption.copy(color = VColors.ink3))
            }
            Text(
                "${cp.metCount}/${cp.totalCount}",
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink2),
            )
        }
        Spacer(Modifier.height(10.dp))
        VProgressBar(value = (cp.progress * 100f).coerceIn(0f, 100f))
    }
}
