package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentThreadDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    threadId: String? = null,
) = PremiumTheme(isDark = false) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Text("Class Teacher", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))
        repeat(4) { i ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = if (i % 2 == 0) Arrangement.Start else Arrangement.End,
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .clip(VShapes.Lg)
                        .background(if (i % 2 == 0) VColors.SurfaceContainerLow else VColors.Primary)
                        .padding(12.dp),
                ) {
                    Text(
                        if (i % 2 == 0) "How is my child doing?" else "Your child is doing well in class!",
                        style = VTypography.UpdateText.copy(color = if (i % 2 == 0) VColors.OnSurface else VColors.OnPrimary),
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(20.dp))
        VTextInput(value = "", onValueChange = { /* TODO: bind reply text */ }, label = "Reply", placeholder = "Type a message...", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VPrimaryButton(text = "Send", onClick = { /* TODO: send reply message */ }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))
    }
}
