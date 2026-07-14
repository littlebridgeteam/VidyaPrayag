package com.littlebridge.enrollplus.ui.v2.screens.teacher.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
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
import com.littlebridge.enrollplus.feature.exam.domain.model.ExamTimetableCreateRequest
import com.littlebridge.enrollplus.feature.exam.domain.model.ExamTimetableEntry
import com.littlebridge.enrollplus.feature.exam.presentation.ExamTimetablesViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * ExamTimetableUploadScreen — teacher uploads an exam timetable via:
 * 1. Image upload → AI OCR extraction
 * 2. Paste text → AI text parsing
 * 3. Manual entry
 *
 * After extraction, entries are listed for review/edit before creating
 * a draft timetable.
 */
@Composable
fun ExamTimetableUploadScreen(
    onBack: () -> Unit = {},
    onCreated: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ExamTimetablesViewModel = koinViewModel(),
) {
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("A") }
    var timetableName by remember { mutableStateOf("") }
    var term by remember { mutableStateOf("") }
    var pastedText by remember { mutableStateOf("") }
    var showTextImport by remember { mutableStateOf(false) }
    val entries = remember { mutableStateListOf<ExamTimetableEntry>() }

    val ocrState by viewModel.ocrState.collectAsStateV2()
    val detailState by viewModel.detailState.collectAsStateV2()

    // Sync OCR results into local entries list
    LaunchedEffect(ocrState.entries) {
        if (ocrState.entries.isNotEmpty()) {
            entries.clear()
            entries.addAll(ocrState.entries)
        }
    }

    // Navigate on create success
    LaunchedEffect(detailState.timetable) {
        detailState.timetable?.let { onCreated(it.id) }
    }

    Column(modifier.fillMaxSize().statusBarsPadding().imePadding().navigationBarsPadding()) {
        VBackHeader(title = "New Exam Timetable", onBack = onBack)

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Basic info ───────────────────────────────────────────────────
            VCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Details", style = VTypography.h2.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    OutlinedTextField(
                        value = timetableName,
                        onValueChange = { timetableName = it },
                        label = { Text("Timetable name (e.g. Mid Term 2026)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedTextField(
                            value = className,
                            onValueChange = { className = it },
                            label = { Text("Class") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = section,
                            onValueChange = { section = it },
                            label = { Text("Section") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                    }
                    OutlinedTextField(
                        value = term,
                        onValueChange = { term = it },
                        label = { Text("Term (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            }

            // ── Import methods ───────────────────────────────────────────────
            VCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Import", style = VTypography.h2.copy(fontWeight = FontWeight.Bold), color = VColors.ink)

                    if (showTextImport) {
                        OutlinedTextField(
                            value = pastedText,
                            onValueChange = { pastedText = it },
                            label = { Text("Paste exam timetable text") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                        )
                        VButton(
                            text = "Parse Text",
                            onClick = {
                                if (className.isNotBlank() && pastedText.isNotBlank()) {
                                    viewModel.importText(pastedText, className, section)
                                }
                            },
                            variant = VButtonVariant.Primary,
                            size = VButtonSize.Md,
                            full = true,
                        )
                    } else {
                        VButton(
                            text = "Upload Image (OCR)",
                            onClick = {
                                // In a real implementation, this would open a file picker
                                // and convert the image to base64. For now, it's a placeholder
                                // that would integrate with the platform MediaPicker.
                            },
                            variant = VButtonVariant.Secondary,
                            size = VButtonSize.Md,
                            full = true,
                        )
                        VButton(
                            text = "Paste Text Instead",
                            onClick = { showTextImport = true },
                            variant = VButtonVariant.Ghost,
                            size = VButtonSize.Md,
                            full = true,
                        )
                    }

                    if (ocrState.isLoading) {
                        Text("Extracting entries...", style = VTypography.body, color = VColors.ink2)
                    }
                    if (ocrState.error != null) {
                        Text(ocrState.error ?: "", style = VTypography.body, color = VColors.error)
                    }
                }
            }

            // ── Entries review ───────────────────────────────────────────────
            if (entries.isNotEmpty()) {
                VCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Extracted Entries (${entries.size})", style = VTypography.h2.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                        entries.forEach { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${entry.examDate} - ${entry.subject}", style = VTypography.body, color = VColors.ink)
                                    Text(entry.examName, style = VTypography.caption, color = VColors.ink2)
                                }
                                Text("${entry.maxMarks} marks", style = VTypography.caption, color = VColors.ink3)
                            }
                        }
                    }
                }
            }

            // ── Create button ────────────────────────────────────────────────
            VButton(
                text = "Create Draft Timetable",
                onClick = {
                    if (timetableName.isNotBlank() && className.isNotBlank() && entries.isNotEmpty()) {
                        viewModel.createTimetable(
                            ExamTimetableCreateRequest(
                                className = className,
                                section = section,
                                name = timetableName,
                                term = term.ifBlank { null },
                                entries = entries.toList(),
                            ),
                        )
                    }
                },
                variant = VButtonVariant.Primary,
                size = VButtonSize.Lg,
                full = true,
            )

            if (detailState.error != null) {
                Text(detailState.error ?: "", style = VTypography.body, color = VColors.error)
            }
        }
    }
}
