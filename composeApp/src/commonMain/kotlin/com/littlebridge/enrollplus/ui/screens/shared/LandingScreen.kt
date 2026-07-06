package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class LandingSlide(
    val accentLabel: String,
    val accentColor: androidx.compose.ui.graphics.Color,
    val headlineBold: String,
    val headlineLight: String,
    val subtitle: String,
    val ctaText: String,
    val targetRoute: String,
)

@Composable
fun LandingScreen(
    onNavigate: (String) -> Unit,
) {
    // TODO: i18n, these slides should come from the backend (GET /api/v1/config/landing-slides)
    val slides = remember {
        listOf(
            LandingSlide(
                accentLabel = "For Parents",
                accentColor = VColors.coral,
                headlineBold = "Every step of your child's journey, ",
                headlineLight = "in your pocket.",
                subtitle = "Attendance, fees, progress reports and direct messages, all in one place.",
                ctaText = "Continue as Parent",
                targetRoute = "ParentLogin",
            ),
            LandingSlide(
                accentLabel = "For Schools",
                accentColor = VColors.violet,
                headlineBold = "Run your entire school ",
                headlineLight = "from one screen.",
                subtitle = "Students, staff, attendance, fees and communication, a single commanding dashboard.",
                ctaText = "Continue as School Staff",
                targetRoute = "AdminLogin",
            ),
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { pageIndex ->
            val slide = slides[pageIndex]
            LandingSlideContent(
                slide = slide,
                slideIndex = pageIndex,
                totalSlides = slides.size,
            )
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Progress segments
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(slides.size) { index ->
                    val isActive = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                if (isActive) VColors.violet else VColors.line,
                                RoundedCornerShape(1.dp),
                            )
                            .clickable {
                                // Will be handled by pagerState.scrollToPage in LaunchedEffect
                            },
                    )
                }
            }

            // CTA
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VButton(
                    text = slides[pagerState.currentPage].ctaText,
                    onClick = { onNavigate(slides[pagerState.currentPage].targetRoute) },
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "By continuing you agree to our ",
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                    Text(
                        text = "Terms",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                        color = VColors.violet,
                        modifier = Modifier.clickable { onNavigate("Terms") },
                    )
                    Text(
                        text = " & ",
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                    Text(
                        text = "Privacy Policy",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                        color = VColors.violet,
                        modifier = Modifier.clickable { onNavigate("PrivacyPolicy") },
                    )
                }
            }
        }
    }
}

@Composable
private fun LandingSlideContent(
    slide: LandingSlide,
    slideIndex: Int,
    totalSlides: Int,
) {
    // Stagger animation values
    val labelAlpha = remember { Animatable(0f) }
    val headlineAlpha = remember { Animatable(0f) }
    val subAlpha = remember { Animatable(0f) }
    val labelOffset = remember { Animatable(24f) }
    val headlineOffset = remember { Animatable(24f) }
    val subOffset = remember { Animatable(24f) }
    val illustrationAlpha = remember { Animatable(0f) }
    val illustrationScale = remember { Animatable(0.92f) }

    LaunchedEffect(slideIndex) {
        labelAlpha.snapTo(0f)
        headlineAlpha.snapTo(0f)
        subAlpha.snapTo(0f)
        labelOffset.snapTo(24f)
        headlineOffset.snapTo(24f)
        subOffset.snapTo(24f)
        illustrationAlpha.snapTo(0f)
        illustrationScale.snapTo(0.92f)
        kotlinx.coroutines.coroutineScope {
            launch {
                illustrationAlpha.animateTo(1f, tween(500, easing = VMotion.ease))
                illustrationScale.animateTo(1f, tween(500, easing = VMotion.ease))
            }
            launch {
                delay(100)
                labelAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
                labelOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
            }
            launch {
                delay(220)
                headlineAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
                headlineOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
            }
            launch {
                delay(340)
                subAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
                subOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .padding(horizontal = 32.dp),
    ) {
        // Top bar, wordmark + slide counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Enroll")
                    withStyle(SpanStyle(color = VColors.violet)) { append("+") }
                },
                style = VTypography.wordmark,
                color = VColors.ink,
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = VColors.ink, fontWeight = FontWeight.Bold)) {
                        append(String.format("%02d", slideIndex + 1))
                    }
                    append(" / ${String.format("%02d", totalSlides)}")
                },
                style = VTypography.slideCounter,
                color = VColors.ink3,
            )
        }

        // Illustration area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .alpha(illustrationAlpha.value)
                .scale(illustrationScale.value),
            contentAlignment = Alignment.Center,
        ) {
            if (slide.accentLabel == "For Parents") {
                ParentIllustration(
                    modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1f),
                )
            } else {
                SchoolIllustration(
                    modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1f),
                )
            }
        }

        // Text content, bottom anchored
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Accent label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier
                    .graphicsLayer(translationY = labelOffset.value)
                    .alpha(labelAlpha.value),
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(slide.accentColor, CircleShape),
                )
                Text(
                    text = slide.accentLabel,
                    style = VTypography.accentLabel,
                    color = slide.accentColor,
                )
            }

            // Headline
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VColors.ink)) {
                        append(slide.headlineBold)
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                        append(slide.headlineLight)
                    }
                },
                style = VTypography.h1,
                modifier = Modifier
                    .graphicsLayer(translationY = headlineOffset.value)
                    .alpha(headlineAlpha.value),
                maxLines = 3,
            )

            // Subtitle
            Text(
                text = slide.subtitle,
                style = VTypography.body,
                color = VColors.ink2,
                modifier = Modifier
                    .graphicsLayer(translationY = subOffset.value)
                    .alpha(subAlpha.value),
                maxLines = 3,
            )
        }
    }
}
