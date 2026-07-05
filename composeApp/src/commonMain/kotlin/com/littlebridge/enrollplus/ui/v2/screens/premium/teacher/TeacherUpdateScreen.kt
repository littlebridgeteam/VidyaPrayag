package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun TeacherUpdateScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Class Update", onBack = onBack, modifier = modifier) {
        Text("Post a class update for parents", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        VTextInput(value = "", onValueChange = {}, label = "Title", placeholder = "Update title", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Update", placeholder = "What did you cover today?", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Class", placeholder = "Select class", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Post Update", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
