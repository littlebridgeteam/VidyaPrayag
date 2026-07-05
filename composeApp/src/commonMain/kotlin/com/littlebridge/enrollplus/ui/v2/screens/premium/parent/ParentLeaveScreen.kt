package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun ParentLeaveScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Leave Application", onBack = onBack, modifier = modifier) {
        Text("Apply for leave for your child.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        VTextInput(value = "", onValueChange = {}, label = "Reason", placeholder = "e.g. Family function", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VTextInput(value = "", onValueChange = {}, label = "From Date", placeholder = "DD/MM/YYYY", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VTextInput(value = "", onValueChange = {}, label = "To Date", placeholder = "DD/MM/YYYY", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        VPrimaryButton(text = "Submit Request", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
