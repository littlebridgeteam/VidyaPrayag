package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentComposeMessageScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "New Message", onBack = onBack, modifier = modifier) {
        VTextInput(value = "", onValueChange = { /* TODO: bind recipient */ }, label = "To", placeholder = "Select recipient", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VTextInput(value = "", onValueChange = { /* TODO: bind subject */ }, label = "Subject", placeholder = "Message subject", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VTextInput(value = "", onValueChange = { /* TODO: bind message body */ }, label = "Message", placeholder = "Type your message...", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        VPrimaryButton(text = "Send Message", onClick = { /* TODO: send composed message */ }, modifier = Modifier.fillMaxWidth())
    }
}
