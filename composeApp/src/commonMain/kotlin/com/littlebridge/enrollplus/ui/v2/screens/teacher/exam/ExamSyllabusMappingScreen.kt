package com.littlebridge.enrollplus.ui.v2.screens.teacher.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.exam.domain.model.CurriculumUnitDto
import com.littlebridge.enrollplus.feature.exam.presentation.ExamSyllabusViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * ExamSyllabusMappingScreen — teacher maps curriculum units to an exam.
 * Shows all units for the exam's class+subject, with checkboxes to
 * toggle which units are included in the exam syllabus.
 */
@Composable
fun ExamSyllabusMappingScreen(
    assessmentId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ExamSyllabusViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val selectedUnitIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(assessmentId) {
        viewModel.loadSyllabus(assessmentId)
    }

    // Sync loaded syllabus into local selection
    LaunchedEffect(state.syllabus) {
        state.syllabus?.let { syllabus ->
            selectedUnitIds.clear()
            selectedUnitIds.addAll(syllabus.units.filter { it.isMapped }.map { it.id })
        }
    }

    // Auto-reload after save to reflect changes
    LaunchedEffect(state.saveMessage) {
        if (state.saveMessage != null) {
            viewModel.clearSaveMessage()
        }
    }

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(
            title = state.syllabus?.let { "${it.examName} — ${it.subject}" } ?: "Syllabus Mapping",
            onBack = onBack,
        )

        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.syllabus?.units.isNullOrEmpty() && !state.isLoading,
            emptyTitle = "No curriculum units found",
            emptyBody = "Add curriculum units for this class+subject first",
            onRetry = { viewModel.loadSyllabus(assessmentId) },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Info bar
                state.syllabus?.let { syllabus ->
                    Text(
                        text = "${syllabus.className} - ${syllabus.section} · ${selectedUnitIds.size} units selected",
                        style = VTypography.bodyMedium,
                        color = VColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                // Unit list
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.syllabus?.units ?: emptyList()) { unit ->
                        UnitCheckboxRow(
                            unit = unit,
                            isChecked = unit.id in selectedUnitIds,
                            onToggle = {
                                if (it) selectedUnitIds.add(unit.id) else selectedUnitIds.remove(unit.id)
                            },
                        )
                    }
                }

                // Save bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VButton(
                        text = if (state.isSaving) "Saving..." else "Save Mapping",
                        onClick = { viewModel.updateSyllabus(assessmentId, selectedUnitIds.toList()) },
                        variant = VButtonVariant.Primary,
                        size = VButtonSize.Lg,
                        modifier = Modifier.weight(1f),
                    )
                }

                state.saveMessage?.let {
                    Text(
                        it,
                        style = VTypography.bodyMedium,
                        color = VColors.success,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitCheckboxRow(
    unit: CurriculumUnitDto,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    VCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onToggle,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.title,
                    style = VTypography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = VColors.textPrimary,
                )
                if (unit.depth > 0) {
                    Text("Topic", style = VTypography.bodySmall, color = VColors.textTertiary)
                }
            }
        }
    }
}
