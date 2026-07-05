package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.misc.VBrandLogoPremium
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) = PremiumTheme(isDark = false) {
    val logoScale = remember { Animatable(0.8f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(12f) }
    val barAlpha = remember { Animatable(0f) }
    val progressTransition = rememberInfiniteTransition(label = "progress")
    val progress by progressTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "progressBar",
    )

    LaunchedEffect(Unit) {
        launch { logoScale.animateTo(1f, tween(VMotion.DurLong2, easing = FastOutSlowInEasing)) }
        launch { logoAlpha.animateTo(1f, tween(VMotion.DurLong1)) }
        delay(200)
        launch { textAlpha.animateTo(1f, tween(VMotion.DurMedium2)) }
        launch { textOffset.animateTo(0f, tween(VMotion.DurMedium2)) }
        delay(300)
        launch { barAlpha.animateTo(1f, tween(VMotion.DurMedium1)) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(VColors.Primary, VColors.PrimaryMid, VColors.PrimaryDeep),
                ),
            )
            .radialGlow(offsetX = 240.dp, offsetY = 180.dp, radius = 280.dp, color = VColors.HeroGlowTopRight)
            .radialGlow(offsetX = (-80).dp, offsetY = 620.dp, radius = 220.dp, color = VColors.HeroGlowBottomLeft),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            VBrandLogoPremium(
                size = 120.dp,
                modifier = Modifier.graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                    alpha = logoAlpha.value
                },
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "VidyaSetu",
                style = VTypography.BrandText.copy(
                    color = VColors.OnPrimary,
                    fontSize = 28.sp,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                    translationY = textOffset.value
                },
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Bridge between school & home",
                style = VTypography.LoginHeroSub.copy(
                    color = VColors.OnPrimary.copy(alpha = 0.7f),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                    translationY = textOffset.value
                },
            )
        }

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .padding(horizontal = 80.dp)
                .fillMaxWidth()
                .graphicsLayer { alpha = barAlpha.value },
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50)),
                color = VColors.OnPrimary.copy(alpha = 0.8f),
                trackColor = VColors.OnPrimary.copy(alpha = 0.12f),
            )
        }
    }
}
