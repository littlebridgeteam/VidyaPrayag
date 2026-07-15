package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.reportcard.presentation.TeacherReportDraftEditorViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2

/**
 * TeacherReportDraftEditorScreen — allows teachers to edit the AI-generated
 * narrative before approving. Shows the full draft JSON in an editable text field.
 */
@Composable
fun TeacherReportDraftEditorScreen(
    draftId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TeacherReportDraftEditorViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VtC

    LaunchedEffect(draftId) { viewModel.loadDraft(draftId) }

    Column(
        Modifier.fillMaxSize().background(c.background)
            .statusBarsPadding().imePadding().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VBackHeader(title = appString(StringKeys.TC_EDIT_DRAFT), onBack = onBack)

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TeacherSpinner()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error ?: "", style = VtT.body.coloredV(c.danger))
                }
            }
            state.draft != null -> {
                val draft = state.draft ?: return
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Draft metadata
                    VCard(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${draft.className} ${draft.section} • ${draft.term}",
                                style = VtT.body.coloredV(c.ink).copy(fontWeight = FontWeight.Medium))
                            Text(appString(StringKeys.TC_STATUS_COLON, "status" to draft.status), style = VtT.caption.coloredV(c.ink2))
                            Text(appString(StringKeys.TC_LANGUAGE_COLON, "lang" to draft.language), style = VtT.caption.coloredV(c.ink3))
                        }
                    }

                    // Editable draft content
                    Text(appString(StringKeys.TC_AI_NARRATIVE_EDITABLE), style = VtT.label.coloredV(c.ink).copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = state.editedContent,
                        onValueChange = { viewModel.updateContent(it) },
                        modifier = Modifier.fillMaxWidth().height(280.dp),
                        textStyle = VtT.body.coloredV(c.ink),
                    )

                    if (state.saved) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(VIcons.Check, contentDescription = null, tint = c.success, modifier = Modifier.size(16.dp))
                            Text(appString(StringKeys.TC_SAVED_SUCCESSFULLY), style = VtT.body.coloredV(c.success))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VButton(
                            text = if (state.saving) appString(StringKeys.TC_SAVING) else appString(StringKeys.TC_SAVE_DRAFT_BTN),
                            onClick = { viewModel.saveDraft() },
                            enabled = !state.saving,
                        )
                        VButton(
                            text = appString(StringKeys.TC_SAVE_AND_BACK),
                            onClick = { viewModel.saveDraft(); onSaved() },
                            variant = VButtonVariant.Secondary,
                            enabled = !state.saving,
                        )
                    }
                }
            }
        }
    }
}
