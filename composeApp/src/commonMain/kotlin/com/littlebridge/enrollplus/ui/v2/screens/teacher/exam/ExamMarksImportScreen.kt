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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherGradebookViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.platform.rememberMediaPicker
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherSpinner
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherSpinner

/**
 * ExamMarksImportScreen — AI-powered marks import overlay.
 *
 * Two import modes:
 *  1. Image upload → AI OCR extraction (teacher photographs a marks sheet)
 *  2. Paste text → AI text parsing (teacher types or pastes marks as text)
 *
 * After extraction, entries are matched against the class roster. The teacher
 * reviews matched/unmatched entries, then taps "Apply to Grid" to fill the
 * marks grid. Only matched entries update the grid; unmatched entries are
 * shown for manual review. The teacher then verifies and saves/publishes as usual.
 */
@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ExamMarksImportScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherGradebookViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var showTextImport by remember { mutableStateOf(false) }
    var pastedText by remember { mutableStateOf("") }

    var pickerUnsupportedMsg by remember { mutableStateOf<String?>(null) }

    val mediaPicker = rememberMediaPicker(
        onPicked = { bytes, mimeType, _ ->
            val base64 = Base64.encode(bytes)
            viewModel.importMarksOcr(base64, mimeType)
        },
        onUnsupported = { message ->
            pickerUnsupportedMsg = message
        },
    )

    Column(modifier.fillMaxSize().statusBarsPadding().imePadding().navigationBarsPadding()) {
        VBackHeader(title = "Import Marks", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Assessment context ───────────────────────────────────────────
            state.activeAssessment?.let { a ->
                item {
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(a.name, style = VTypography.h3.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                            Text(
                                "${a.className} - ${a.section} · ${a.subject}",
                                style = VTypography.caption,
                                color = VColors.ink3,
                            )
                            Text("Max marks: ${a.maxMarks}", style = VTypography.caption, color = VColors.ink3)
                        }
                    }
                }
            }

            // ── Import method selector ───────────────────────────────────────
            if (state.importEntries.isEmpty() && !state.isImporting) {
                item {
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Import Method", style = VTypography.h2.copy(fontWeight = FontWeight.Bold), color = VColors.ink)

                            if (showTextImport) {
                                OutlinedTextField(
                                    value = pastedText,
                                    onValueChange = { pastedText = it },
                                    label = { Text("Paste marks sheet text") },
                                    modifier = Modifier.fillMaxWidth().height(120.dp),
                                )
                                VButton(
                                    text = "Parse Text",
                                    onClick = {
                                        if (pastedText.isNotBlank()) {
                                            viewModel.importMarksText(pastedText)
                                        }
                                    },
                                    variant = VButtonVariant.Primary,
                                    size = VButtonSize.Md,
                                    full = true,
                                )
                                VButton(
                                    text = "Use Image Instead",
                                    onClick = { showTextImport = false },
                                    variant = VButtonVariant.Ghost,
                                    size = VButtonSize.Md,
                                    full = true,
                                )
                            } else {
                                VButton(
                                    text = "Upload Marks Sheet Image (OCR)",
                                    onClick = { mediaPicker.launchImage() },
                                    variant = VButtonVariant.Primary,
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
                        }
                    }
                }
            }

            // ── Loading state ────────────────────────────────────────────────
            if (state.isImporting) {
                item {
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TeacherSpinner(32.dp)
                            Text("Extracting marks...", style = VTypography.body, color = VColors.ink2)
                            Text("This may take a few seconds.", style = VTypography.caption, color = VColors.ink3)
                        }
                    }
                }
            }

            // ── Error state ──────────────────────────────────────────────────
            state.importError?.let { err ->
                item {
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Import Failed", style = VTypography.h2.copy(fontWeight = FontWeight.Bold), color = VColors.coral)
                            Text(err, style = VTypography.body, color = VColors.ink2)
                            VButton(
                                text = "Try Again",
                                onClick = { viewModel.clearImport() },
                                variant = VButtonVariant.Secondary,
                                size = VButtonSize.Sm,
                                full = true,
                            )
                        }
                    }
                }
            }

            // ── Picker unsupported state ────────────────────────────────────
            pickerUnsupportedMsg?.let { msg ->
                if (state.importError == null && !state.isImporting) {
                    item {
                        VCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Image Picker Unavailable", style = VTypography.h2.copy(fontWeight = FontWeight.Bold), color = VColors.coral)
                                Text(msg, style = VTypography.body, color = VColors.ink2)
                                VButton(
                                    text = "Paste Text Instead",
                                    onClick = {
                                        pickerUnsupportedMsg = null
                                        showTextImport = true
                                    },
                                    variant = VButtonVariant.Ghost,
                                    size = VButtonSize.Sm,
                                    full = true,
                                )
                            }
                        }
                    }
                }
            }

            // ── Results: matched/unmatched summary + entries ─────────────────
            if (state.importEntries.isNotEmpty()) {
                item {
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Extraction Results", style = VTypography.h2.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                VBadge(
                                    text = "${state.importMatchedCount} matched",
                                    tone = VBadgeTone.Success,
                                )
                                if (state.importUnmatchedCount > 0) {
                                    VBadge(
                                        text = "${state.importUnmatchedCount} unmatched",
                                        tone = VBadgeTone.Warning,
                                    )
                                }
                            }
                            Text(
                                "Review the extracted marks below. Only matched students will be filled into the grid. Unmatched entries are shown for your reference.",
                                style = VTypography.caption,
                                color = VColors.ink3,
                            )
                        }
                    }
                }

                // ── Entry rows ───────────────────────────────────────────────
                items(state.importEntries, key = { it.studentId ?: it.name }) { entry ->
                    VCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.name,
                                    style = VTypography.body.copy(fontWeight = FontWeight.Medium),
                                    color = VColors.ink,
                                )
                                if (entry.rollNo.isNotBlank()) {
                                    Text("Roll: ${entry.rollNo}", style = VTypography.caption, color = VColors.ink3)
                                }
                            }
                            if (entry.isAbsent) {
                                VBadge(text = "AB", tone = VBadgeTone.Danger)
                            } else {
                                Text(
                                    entry.marks?.toString() ?: "—",
                                    style = VTypography.h3.copy(fontWeight = FontWeight.Bold),
                                    color = VColors.ink,
                                )
                            }
                            if (entry.matched) {
                                VBadge(text = "Matched", tone = VBadgeTone.Success)
                            } else {
                                VBadge(text = "Unmatched", tone = VBadgeTone.Warning)
                            }
                        }
                    }
                }

                // ── Action buttons ────────────────────────────────────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VButton(
                            text = "Apply to Grid",
                            onClick = {
                                viewModel.applyImportedMarks()
                                onBack()
                            },
                            variant = VButtonVariant.Primary,
                            size = VButtonSize.Lg,
                            full = true,
                        )
                        VButton(
                            text = "Discard",
                            onClick = { viewModel.clearImport() },
                            variant = VButtonVariant.Ghost,
                            size = VButtonSize.Md,
                            full = true,
                        )
                    }
                }
            }
        }
    }
}
