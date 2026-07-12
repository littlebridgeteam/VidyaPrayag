package com.littlebridge.enrollplus.ui.v2.screens.parent.exam

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.exam.presentation.ParentExamViewModel
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
 * ParentExamDetailScreen — parent views exam syllabus for a specific assessment.
 * If no syllabus has been mapped, shows a "Request Syllabus" button that
 * sends a notification to the teacher.
 */
@Composable
fun ParentExamDetailScreen(
    childId: String,
    assessmentId: String,
    examTitle: String = "Exam Details",
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ParentExamViewModel = koinViewModel(),
) {
    val syllabusState by viewModel.syllabusState.collectAsStateV2()
    val isRequesting by viewModel.isRequesting.collectAsStateV2()
    val requestState by viewModel.requestState.collectAsStateV2()

    LaunchedEffect(childId, assessmentId) {
        viewModel.loadExamSyllabus(childId, assessmentId)
    }

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(title = examTitle, onBack = onBack)

        VStateHost(
            loading = syllabusState.isLoading,
            error = syllabusState.error,
            isEmpty = syllabusState.syllabus?.units.isNullOrEmpty() && !syllabusState.isLoading,
            emptyTitle = "No syllabus mapped yet",
            emptyBody = "Request the teacher to add the exam syllabus",
            onRetry = { viewModel.loadExamSyllabus(childId, assessmentId) },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val syllabus = syllabusState.syllabus!!

                // ── Exam info ─────────────────────────────────────────────────
                VCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(syllabus.examName, style = VTypography.h3.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                        Text(syllabus.subject, style = VTypography.h2, color = VColors.ink2)
                        Text("${syllabus.className} - ${syllabus.section}", style = VTypography.body, color = VColors.ink3)
                    }
                }

                // ── Syllabus units ────────────────────────────────────────────
                Text(
                    "Topics to Study (${syllabus.units.size})",
                    style = VTypography.h2.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                    modifier = Modifier.padding(top = 8.dp),
                )

                syllabus.units.forEach { unit ->
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(unit.title, style = VTypography.body.copy(fontWeight = FontWeight.Medium), color = VColors.ink)
                                if (unit.depth > 0) {
                                    Text("Topic", style = VTypography.caption, color = VColors.ink3)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Request syllabus button (shown when no units or always as fallback)
        if (syllabusState.syllabus?.units.isNullOrEmpty() && !syllabusState.isLoading) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                VButton(
                    text = if (isRequesting) "Sending..." else "Request Syllabus from Teacher",
                    onClick = { viewModel.requestSyllabus(assessmentId) },
                    variant = VButtonVariant.Primary,
                    size = VButtonSize.Lg,
                    full = true,
                )
                requestState?.let {
                    Text(
                        it.message,
                        style = VTypography.body,
                        color = if (it.success) VColors.success else VColors.error,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}
