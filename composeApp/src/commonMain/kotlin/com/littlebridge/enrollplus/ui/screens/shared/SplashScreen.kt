package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
) {
    val splashNameAlpha = Animatable(0f)
    val accentWidth = Animatable(0f)

    LaunchedEffect(Unit) {
        launch {
            splashNameAlpha.animateTo(1f, tween(VMotion.durSlow, easing = VMotion.ease))
        }
        launch {
            delay(400)
            accentWidth.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
        }
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream)
            .clickable { onTimeout() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Enroll")
                    withStyle(SpanStyle(color = VColors.violet)) { append("+") }
                },
                style = VTypography.splashName,
                color = VColors.ink,
                modifier = Modifier
                    .alpha(splashNameAlpha.value)
                    .scale(0.96f + 0.04f * splashNameAlpha.value),
            )
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(48.dp * accentWidth.value)
                    .background(VColors.violet, RoundedCornerShape(2.dp)),
            )
        }
    }
}
