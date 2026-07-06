package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    // TODO: i18n — these slides should come from the backend (GET /api/v1/config/landing-slides)
    val slides = remember {
        listOf(
            LandingSlide(
                accentLabel = "For Parents",
                accentColor = VColors.coral,
                headlineBold = "Every step of your child's journey, ",
                headlineLight = "in your pocket.",
                subtitle = "Attendance, fees, progress reports and direct messages — all in one place.",
                ctaText = "Continue as Parent",
                targetRoute = "ParentLogin",
            ),
            LandingSlide(
                accentLabel = "For Schools",
                accentColor = VColors.violet,
                headlineBold = "Run your entire school ",
                headlineLight = "from one screen.",
                subtitle = "Students, staff, attendance, fees and communication — a single commanding dashboard.",
                ctaText = "Continue as School Staff",
                targetRoute = "AdminLogin",
            ),
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })
    var hintVisible by remember { mutableStateOf(true) }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage > 0) hintVisible = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
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

        // Swipe hint
        AnimatedVisibility(
            visible = hintVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 48.dp),
            ) {
                Text(
                    text = "Swipe",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = VColors.ink3,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
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
                Text(
                    text = buildAnnotatedString {
                        append("By continuing you agree to our ")
                        withStyle(SpanStyle(color = VColors.violet, fontWeight = FontWeight.Bold)) { append("Terms") }
                        append(" & ")
                        withStyle(SpanStyle(color = VColors.violet, fontWeight = FontWeight.Bold)) { append("Privacy Policy") }
                    },
                    style = VTypography.caption,
                    color = VColors.ink3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
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
    val labelAlpha = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(VMotion.durSlower, easing = VMotion.ease),
        label = "label",
    )
    val headlineAlpha = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(VMotion.durSlower, delayMillis = 120, easing = VMotion.ease),
        label = "headline",
    )
    val subAlpha = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(VMotion.durSlower, delayMillis = 240, easing = VMotion.ease),
        label = "sub",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
    ) {
        // Top bar — wordmark + slide counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp),
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

        // Main content — bottom anchored
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            // Accent label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier
                    .alpha(labelAlpha.value)
                    .padding(bottom = 16.dp),
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
                    .alpha(headlineAlpha.value)
                    .padding(bottom = 16.dp),
                maxLines = 3,
            )

            // Subtitle
            Text(
                text = slide.subtitle,
                style = VTypography.body,
                color = VColors.ink2,
                modifier = Modifier.alpha(subAlpha.value),
                maxLines = 3,
            )
        }
    }
}
