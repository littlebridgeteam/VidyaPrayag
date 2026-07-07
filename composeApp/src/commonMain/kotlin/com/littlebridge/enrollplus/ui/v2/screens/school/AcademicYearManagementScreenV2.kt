package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.domain.model.AcademicYearDto
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicYearViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * AcademicYearManagementScreenV2 — the REAL replacement for the Settings
 * "Academic Year (Coming Soon)" stub. Lets a school admin:
 *   - Create a new academic year (optionally activating it)
 *   - Activate / Archive existing years (exactly one active at a time)
 *   - View historical (archived) years
 */
@Composable
fun AcademicYearManagementScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AcademicYearViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    
    var showCreate by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize().background(VColors.surface).statusBarsPadding().imePadding().navigationBarsPadding()) {
        VBackHeader(
            title = appString(StringKeys.SCH_ACADEMIC_YEAR),
            onBack = onBack,
            action = {
                VButton(
                    text = if (showCreate) appString(StringKeys.SCH_CLOSE) else appString(StringKeys.SCH_NEW),
                    onClick = { showCreate = !showCreate },
                    size = VButtonSize.Sm,
                    tone = VButtonTone.Teal,
                )
            },
        )

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.isEmpty && !showCreate,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            emptyTitle = appString(StringKeys.SCH_NO_ACADEMIC_YEARS),
            emptyBody = appString(StringKeys.SCH_NO_ACADEMIC_YEARS_DESC),
            emptyIcon = VIcons.Calendar,
            onRetry = { viewModel.load() },
        ) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (showCreate) {
                    VLabel(appString(StringKeys.SCH_CREATE_ACADEMIC_YEAR))
                    VCard {
                        VInput(value = name, onValueChange = { name = it }, label = appString(StringKeys.SCH_NAME), placeholder = appString(StringKeys.SCH_YEAR_NAME_PH))
                        Spacer(Modifier.height(10.dp))
                        VDatePicker(value = start, onValueChange = { start = it }, label = appString(StringKeys.SCH_START_DATE))
                        Spacer(Modifier.height(10.dp))
                        VDatePicker(value = end, onValueChange = { end = it }, label = appString(StringKeys.SCH_END_DATE))
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            VButton(
                                text = appString(StringKeys.SCH_SAVE_DRAFT),
                                onClick = {
                                    viewModel.createYear(name, start, end, activate = false) {
                                        showCreate = false; name = ""; start = ""; end = ""
                                    }
                                },
                                variant = VButtonVariant.Secondary,
                                tone = VButtonTone.Navy,
                                loading = state.isMutating,
                                modifier = Modifier.weight(1f),
                            )
                            VButton(
                                text = appString(StringKeys.SCH_CREATE_ACTIVATE),
                                onClick = {
                                    viewModel.createYear(name, start, end, activate = true) {
                                        showCreate = false; name = ""; start = ""; end = ""
                                    }
                                },
                                tone = VButtonTone.Teal,
                                loading = state.isMutating,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                state.activeYear?.let { active ->
                    VLabel(appString(StringKeys.SCH_ACTIVE_YEAR))
                    YearCard(active, isActive = true, onActivate = {}, onArchive = { viewModel.archive(active.id) })
                }

                val historical = state.historicalYears
                if (historical.isNotEmpty()) {
                    VLabel(appString(StringKeys.SCH_HISTORICAL_DRAFTS))
                    historical.forEach { y ->
                        YearCard(
                            y,
                            isActive = false,
                            onActivate = { viewModel.activate(y.id) },
                            onArchive = { viewModel.archive(y.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearCard(
    year: AcademicYearDto,
    isActive: Boolean,
    onActivate: () -> Unit,
    onArchive: () -> Unit,
) {
        VCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(year.name, style = VTypography.h3.copy(color = VColors.ink))
                Text("${year.startDate} → ${year.endDate}", style = VTypography.caption.copy(color = VColors.ink2))
            }
            VBadge(
                text = year.status,
                tone = when (year.status.uppercase()) {
                    "ACTIVE" -> VBadgeTone.Success
                    "ARCHIVED" -> VBadgeTone.Neutral
                    else -> VBadgeTone.Warning
                },
            )
        }
        if (year.academicDays != null || year.holidayDays != null) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                year.academicDays?.let { Text(appString(StringKeys.SCH_SCHOOL_DAYS, "count" to it.toString()), style = VTypography.caption.copy(color = VColors.ink3)) }
                year.holidayDays?.let { Text(appString(StringKeys.SCH_HOLIDAYS, "count" to it.toString()), style = VTypography.caption.copy(color = VColors.ink3)) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!isActive) {
                VButton(
                    text = appString(StringKeys.SCH_ACTIVATE),
                    onClick = onActivate,
                    size = VButtonSize.Sm,
                    tone = VButtonTone.Teal,
                )
            }
            VButton(
                text = appString(StringKeys.SCH_ARCHIVE),
                onClick = onArchive,
                size = VButtonSize.Sm,
                variant = VButtonVariant.Secondary,
                tone = VButtonTone.Navy,
            )
        }
    }
}
