package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherGradebookViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherMarksScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    assignmentId: String = "",
    viewModel: TeacherGradebookViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    TeacherOverlayScaffold(title = "Marks Entry", onBack = onBack, modifier = modifier) {
        Text(state.scopeHint, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))

        if (state.isMarksLoading) {
            StatusBox("Loading students...")
            return@TeacherOverlayScaffold
        }

        if (state.marksError != null) {
            StatusBox(state.marksError!!, isError = true)
            return@TeacherOverlayScaffold
        }

        if (state.students.isEmpty()) {
            StatusBox("No students to grade")
            return@TeacherOverlayScaffold
        }

        val maxMarks = state.activeAssessment?.maxMarks?.toString() ?: "100"
        state.students.forEach { student ->
            MarksRow(student.name, student.marks?.toString() ?: "—", maxMarks)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(20.dp))
        VPrimaryButton(text = "Save Marks", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun MarksRow(name: String, marks: String, maxMarks: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Text("$marks / $maxMarks", style = VTypography.UpdateText.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg)
            .background(if (isError) VColors.ErrorContainer else VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.OnErrorContainer else VColors.OnSurfaceVariant))
    }
}
