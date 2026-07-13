package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardState
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import androidx.compose.ui.text.font.FontWeight
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

@Composable
internal fun GenerateTab(
    state: IdCardState,
    viewModel: IdCardViewModel,
) {
        var selectedTemplateId by remember { mutableStateOf<String?>(null) }
    var selectedScope by remember { mutableStateOf("all_students") }
    var classIdInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        state.error?.let { err ->
            VCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(err, color = VTheme.colors.error, style = VTheme.type.body)
            }
        }
        state.infoMessage?.let { msg ->
            VCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(msg, color = VTheme.colors.success, style = VTheme.type.body)
            }
        }

        if (state.templates.isEmpty()) {
            VEmptyState(
                title = appString(StringKeys.SCH_NO_TEMPLATES),
                body = appString(StringKeys.SCH_NO_TEMPLATES_DESC),
                icon = Icons.Filled.School,
                modifier = Modifier.padding(top = 48.dp),
            )
            Spacer(modifier = Modifier.height(32.dp))
            return
        }

        Text(appString(StringKeys.SCH_SELECT_TEMPLATE), style = VTheme.type.label.copy(color = VTheme.colors.ink3))
        Spacer(modifier = Modifier.height(8.dp))

        state.templates.forEach { template ->
            val isSelected = selectedTemplateId == template.id
            VCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .then(
                        if (isSelected) Modifier.border(2.dp, VTheme.colors.violet, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { selectedTemplateId = template.id },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(template.name, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                        Text(
                            appString(StringKeys.SCH_TEMPLATE_STATUS, "role" to template.roleType.replaceFirstChar { it.uppercase() }, "status" to if (template.isActive) appString(StringKeys.SCH_ACTIVE_LABEL) else appString(StringKeys.SCH_INACTIVE)),
                            style = VTheme.type.caption.copy(color = VTheme.colors.ink2),
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Filled.Check, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(appString(StringKeys.SCH_SELECT_SCOPE), style = VTheme.type.label.copy(color = VTheme.colors.ink3))
        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            "all_students" to appString(StringKeys.SCH_ALL_STUDENTS),
            "all_staff" to appString(StringKeys.SCH_ALL_STAFF),
            "class" to appString(StringKeys.SCH_BY_CLASS),
        ).forEach { (scope, label) ->
            val isSelected = selectedScope == scope
            VCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .then(
                        if (isSelected) Modifier.border(2.dp, VTheme.colors.violet, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { selectedScope = scope },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(label, style = VTheme.type.body.copy(color = VTheme.colors.ink))
                    if (isSelected) {
                        Icon(Icons.Filled.Check, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        if (selectedScope == "class") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = classIdInput,
                onValueChange = { classIdInput = it },
                label = { Text(appString(StringKeys.SCH_CLASS_ID_UUID)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        VButton(
            text = if (state.isGenerating) appString(StringKeys.SCH_GENERATING) else appString(StringKeys.SCH_GENERATE_CARDS),
            onClick = {
                selectedTemplateId?.let { tid ->
                    viewModel.clearMessages()
                    val classId = if (selectedScope == "class" && classIdInput.isNotBlank()) classIdInput else null
                    viewModel.generateCards(tid, selectedScope, classId)
                }
            },
            variant = VButtonVariant.Primary,
            enabled = !state.isGenerating && selectedTemplateId != null &&
                (selectedScope != "class" || classIdInput.isNotBlank()),
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isGenerating) {
            Spacer(modifier = Modifier.height(12.dp))
            VProgressBar(value = 50f, tone = VBadgeTone.Accent, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = appString(StringKeys.SCH_RENDERING_CARDS),
                style = VTheme.type.caption.copy(color = VTheme.colors.ink3),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
