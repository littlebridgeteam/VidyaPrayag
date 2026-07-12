package com.littlebridge.enrollplus.ui.v2.screens.teacher.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.exam.domain.model.ExamTimetable
import com.littlebridge.enrollplus.feature.exam.presentation.ExamPublishState
import com.littlebridge.enrollplus.feature.exam.presentation.ExamTimetablesViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * ExamTimetableDetailScreen — shows a timetable with its entries.
 * If draft, shows a Publish button. Each entry with an assessmentId
 * links to the SyllabusMappingScreen.
 */
@Composable
fun ExamTimetableDetailScreen(
    timetableId: String,
    onBack: () -> Unit = {},
    onMapSyllabus: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ExamTimetablesViewModel = koinViewModel(),
) {
    val detailState by viewModel.detailState.collectAsStateV2()
    val publishState by viewModel.publishState.collectAsStateV2()

    LaunchedEffect(timetableId) {
        viewModel.loadTimetable(timetableId)
    }

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(title = detailState.timetable?.name ?: "Exam Timetable", onBack = onBack)

        VStateHost(
            loading = detailState.isLoading,
            error = detailState.error,
            isEmpty = detailState.timetable == null && !detailState.isLoading,
            emptyTitle = "Timetable not found",
            onRetry = { viewModel.loadTimetable(timetableId) },
            modifier = Modifier.fillMaxSize(),
        ) {
            val tt = detailState.timetable!!
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ── Header card ──────────────────────────────────────────────
                VCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(tt.name, style = VTypography.titleLarge, fontWeight = FontWeight.Bold, color = VColors.textPrimary)
                            VBadge(
                                text = tt.status,
                                tone = if (tt.status == "published") VBadgeTone.Success else VBadgeTone.Warning,
                            )
                        }
                        Text("${tt.className} - ${tt.section}", style = VTypography.bodyMedium, color = VColors.textSecondary)
                        tt.term?.let { Text(it, style = VTypography.bodySmall, color = VColors.textTertiary) }
                    }
                }

                // ── Entries ──────────────────────────────────────────────────
                tt.entries.forEach { entry ->
                    VCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(entry.examDate, style = VTypography.bodyMedium, fontWeight = FontWeight.Medium, color = VColors.textPrimary)
                                Text("${entry.maxMarks} marks", style = VTypography.bodySmall, color = VColors.textTertiary)
                            }
                            Text(entry.subject, style = VTypography.titleMedium, color = VColors.textPrimary)
                            Text(entry.examName, style = VTypography.bodySmall, color = VColors.textSecondary)
                            entry.startTime?.let {
                                Text("$it${entry.endTime?.let { e -> " - $e" } ?: ""}", style = VTypography.bodySmall, color = VColors.textTertiary)
                            }
                            entry.room?.let { Text("Room: $it", style = VTypography.bodySmall, color = VColors.textTertiary) }

                            // If published and has assessment, show syllabus mapping button
                            if (tt.status == "published" && entry.assessmentId != null) {
                                VButton(
                                    text = "Map Syllabus",
                                    onClick = { onMapSyllabus(entry.assessmentId!!) },
                                    variant = VButtonVariant.Secondary,
                                    size = VButtonSize.Sm,
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                )
                            }
                        }
                    }
                }

                // ── Publish button ───────────────────────────────────────────
                if (tt.status == "draft") {
                    VButton(
                        text = if (publishState.isPublishing) "Publishing..." else "Publish Timetable",
                        onClick = { viewModel.publishTimetable(tt.id) },
                        variant = VButtonVariant.Primary,
                        size = VButtonSize.Lg,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                publishState.message?.let {
                    Text(it, style = VTypography.bodyMedium, color = VColors.success)
                }
                publishState.error?.let {
                    Text(it, style = VTypography.bodyMedium, color = VColors.error)
                }
            }
        }
    }
}
