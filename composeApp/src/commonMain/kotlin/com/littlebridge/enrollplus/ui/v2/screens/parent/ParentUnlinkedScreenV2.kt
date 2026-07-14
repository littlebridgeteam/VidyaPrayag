package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.auth.ParentLinkChildScreenV2
import com.littlebridge.enrollplus.ui.v2.screens.discovery.DiscoveryScreenV2
import com.littlebridge.enrollplus.ui.v2.theme.VMotion

/**
 * ParentUnlinkedScreenV2 — the first-run experience for a parent with no child linked yet.
 *
 * Flow:
 *   1. Premium feature carousel (Notion-style artifact cards, 75% screen coverage, peeking edges).
 *   2. Last slide CTA slides down to launch the school marketplace.
 *   3. Marketplace: search, filter, compare schools.
 *   4. From marketplace the parent can jump into the guided child-link flow.
 */
private enum class UnlinkedStep { Carousel, Marketplace, LinkChild }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ParentUnlinkedScreenV2(
    onLinked: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var step by remember { mutableStateOf(UnlinkedStep.Carousel) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                when {
                    targetState == UnlinkedStep.Marketplace && initialState == UnlinkedStep.Carousel ->
                        slideInVertically(initialOffsetY = { it }) + fadeIn(tween(500)) togetherWith
                            slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut(tween(400))
                    targetState == UnlinkedStep.Carousel && initialState == UnlinkedStep.Marketplace ->
                        slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn(tween(400)) togetherWith
                            slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(500))
                    targetState == UnlinkedStep.LinkChild ->
                        slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(350)) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut(tween(350))
                    else ->
                        slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn(tween(350)) togetherWith
                            slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(350))
                }
            },
            label = "unlinked-step",
        ) { current ->
            when (current) {
                UnlinkedStep.Carousel -> ParentFeatureCarousel(
                    onLaunchMarketplace = { step = UnlinkedStep.Marketplace },
                    onLaunchLinkChild = { step = UnlinkedStep.LinkChild },
                    modifier = Modifier.fillMaxSize(),
                )
                UnlinkedStep.Marketplace -> DiscoveryScreenV2(
                    embedded = true,
                    onExit = { step = UnlinkedStep.Carousel },
                    onAlreadyLinked = { step = UnlinkedStep.LinkChild },
                    onOpenSchool = { step = UnlinkedStep.LinkChild },
                    modifier = Modifier.fillMaxSize(),
                )
                UnlinkedStep.LinkChild -> ParentLinkChildScreenV2(
                    onDone = onLinked,
                    onBack = { step = UnlinkedStep.Marketplace },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private data class FeatureSlide(
    val title: String,
    val subtitle: String,
    val accent: Color,
    val artifact: @Composable () -> Unit,
)

@Composable
private fun ParentFeatureCarousel(
    onLaunchMarketplace: () -> Unit,
    onLaunchLinkChild: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val slides = remember {
        listOf(
            FeatureSlide(
                title = "AI-Powered Learning",
                subtitle = "Personalised AI tutor, smart reports and daily lesson summaries for every subject.",
                accent = VColors.violet,
                artifact = { AiLearningArtifact() },
            ),
            FeatureSlide(
                title = "Live Attendance & Safety",
                subtitle = "Know when your child reaches school, track the bus in real-time and get instant alerts.",
                accent = VColors.mint,
                artifact = { SafetyArtifact() },
            ),
            FeatureSlide(
                title = "Direct Communication",
                subtitle = "Chat with teachers and the school office in one threaded, WhatsApp-style inbox.",
                accent = VColors.coral,
                artifact = { ConversationsArtifact() },
            ),
            FeatureSlide(
                title = "Fees & Academics",
                subtitle = "Pay fees, view report cards, track syllabus progress and never miss a deadline.",
                accent = VColors.gold,
                artifact = { AcademicsArtifact() },
            ),
            FeatureSlide(
                title = "Link Your Child",
                subtitle = "Find your school in our premium marketplace and connect your child in minutes.",
                accent = VColors.violet,
                artifact = { LinkChildArtifact() },
            ),
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val isLast = pagerState.currentPage == slides.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .navigationBarsPadding(),
    ) {
        PortalTopHeaderMinimal(
            onOpenNotifications = {},
            unreadNotificationsCount = 0,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp),
        )

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "Welcome to Enroll+",
                style = VTypography.caption.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.violet,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "The school experience, reimagined.",
                style = VTypography.h2.copy(fontSize = 26.sp, color = VColors.ink),
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "Swipe to explore what your child's school can do.",
                style = VTypography.caption.copy(fontSize = 14.sp, color = VColors.ink3),
            )
        }

        Spacer(Modifier.height(24.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            pageSpacing = 16.dp,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 36.dp),
            beyondViewportPageCount = 2,
        ) { page ->
            FeatureSlideCard(
                slide = slides[page],
                isLast = page == slides.lastIndex,
                onLaunchMarketplace = onLaunchMarketplace,
                onLaunchLinkChild = onLaunchLinkChild,
                pageOffset = ((page - pagerState.currentPage) + pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SlideIndicator(count = slides.size, current = pagerState.currentPage)
            Spacer(Modifier.height(24.dp))
            AnimatedContent(
                targetState = isLast,
                transitionSpec = { VMotion.quietFade() },
                label = "carousel-cta",
            ) { last ->
                if (last) {
                    VButton(
                        text = "Link your child",
                        onClick = onLaunchLinkChild,
                        variant = VButtonVariant.Primary,
                        tone = VButtonTone.Lavender,
                        size = VButtonSize.Lg,
                        full = true,
                        trailing = { Icon(VIcons.ArrowRight, null, modifier = Modifier.size(18.dp)) },
                    )
                } else {
                    VButton(
                        text = "Skip to marketplace",
                        onClick = onLaunchMarketplace,
                        variant = VButtonVariant.Ghost,
                        tone = VButtonTone.Lavender,
                        size = VButtonSize.Lg,
                        full = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureSlideCard(
    slide: FeatureSlide,
    isLast: Boolean,
    onLaunchMarketplace: () -> Unit,
    onLaunchLinkChild: () -> Unit,
    pageOffset: Float,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = 1f - (0.08f * pageOffset.absoluteValue()),
        animationSpec = spring(dampingRatio = 0.8f),
        label = "slide-scale",
    )
    val alpha by animateFloatAsState(
        targetValue = 1f - (0.25f * pageOffset.absoluteValue()),
        animationSpec = spring(dampingRatio = 0.8f),
        label = "slide-alpha",
    )

    Column(
        modifier = modifier
            .fillMaxHeight(0.85f)
            .clip(VShapes.xxl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xxl)
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.alpha = alpha
            }
            .clickable(enabled = isLast, onClick = onLaunchLinkChild)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(VShapes.xxl)
                .background(VColors.creamDeep)
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            slide.artifact()
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = slide.title,
                    style = VTypography.h2.copy(fontSize = 22.sp, color = VColors.ink),
                    fontWeight = FontWeight.ExtraBold,
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(slide.accent),
                )
            }
            Text(
                text = slide.subtitle,
                style = VTypography.caption.copy(fontSize = 14.sp, color = VColors.ink2),
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun SlideIndicator(count: Int, current: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val active = index == current
            val width by animateDpAsState(
                targetValue = if (active) 24.dp else 8.dp,
                animationSpec = spring(dampingRatio = 0.8f),
                label = "indicator-width",
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(VShapes.full)
                    .background(if (active) VColors.violet else VColors.line),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// NOTION-STYLE ARTIFACTS — composed premium visuals, no external illustrations.
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun AiLearningArtifact() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Background glow.
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(VColors.violet.copy(alpha = 0.08f)),
        )
        // Central card stack.
        Column(
            modifier = Modifier.fillMaxWidth(0.75f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ArtifactCard(height = 70.dp, color = VColors.violet) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(VShapes.md)
                            .background(VColors.white.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = VIcons.Sparkles,
                            contentDescription = null,
                            tint = VColors.white,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.width(80.dp).height(8.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.4f)))
                        Box(Modifier.width(120.dp).height(6.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.25f)))
                    }
                }
            }
            ArtifactCard(height = 90.dp, color = VColors.mint) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.width(140.dp).height(8.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.4f)))
                    Box(Modifier.fillMaxWidth().height(6.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.25f)))
                    Box(Modifier.fillMaxWidth(0.7f).height(6.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.25f)))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.width(40.dp).height(18.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.2f)))
                        Box(Modifier.width(50.dp).height(18.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.2f)))
                    }
                }
            }
            ArtifactCard(height = 60.dp, color = VColors.coral) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(40.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.2f)))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.width(100.dp).height(8.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.4f)))
                        Box(Modifier.width(70.dp).height(6.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.25f)))
                    }
                }
            }
        }
    }
}

@Composable
private fun SafetyArtifact() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Route line.
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(4.dp)
                .clip(VShapes.full)
                .background(VColors.mint.copy(alpha = 0.3f)),
        )
        // Bus icon moving along.
        Box(
            modifier = Modifier
                .offset(x = 40.dp, y = (-40).dp)
                .size(56.dp)
                .clip(VShapes.xxl)
                .background(VColors.mint)
                .border(2.dp, VColors.white, VShapes.xxl),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.MapPin,
                contentDescription = null,
                tint = VColors.white,
                modifier = Modifier.size(28.dp),
            )
        }
        // Pulse rings.
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .size((80 + i * 40).dp)
                    .clip(CircleShape)
                    .background(VColors.mint.copy(alpha = 0.08f / (i + 1))),
            )
        }
        // Student badge.
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = 40.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(VColors.white)
                .border(2.dp, VColors.mint, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.User,
                contentDescription = null,
                tint = VColors.mint,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun ConversationsArtifact() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth(0.75f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            repeat(3) { index ->
                val alignRight = index == 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (alignRight) Arrangement.End else Arrangement.Start,
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (alignRight) 140.dp else 180.dp)
                            .height(56.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomStart = if (alignRight) 20.dp else 4.dp,
                                    bottomEnd = if (alignRight) 4.dp else 20.dp,
                                )
                            )
                            .background(if (alignRight) VColors.coral else VColors.surfaceCard)
                            .border(1.dp, VColors.line, RoundedCornerShape(20.dp))
                            .padding(12.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(Modifier.width(80.dp).height(6.dp).clip(VShapes.full).background(if (alignRight) VColors.white.copy(alpha = 0.4f) else VColors.ink.copy(alpha = 0.2f)))
                            Box(Modifier.fillMaxWidth(0.8f).height(6.dp).clip(VShapes.full).background(if (alignRight) VColors.white.copy(alpha = 0.25f) else VColors.ink.copy(alpha = 0.12f)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AcademicsArtifact() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Report card.
            ArtifactCard(height = 110.dp, color = VColors.gold) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(VShapes.md).background(VColors.white.copy(alpha = 0.2f)))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(Modifier.width(90.dp).height(8.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.4f)))
                            Box(Modifier.width(60.dp).height(6.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.25f)))
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(6.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.25f)))
                    Box(Modifier.fillMaxWidth(0.6f).height(6.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.25f)))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.width(50.dp).height(22.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.2f)))
                        Box(Modifier.width(40.dp).height(22.dp).clip(VShapes.full).background(VColors.white.copy(alpha = 0.2f)))
                    }
                }
            }
            // Circular progress.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ArtifactCard(height = 80.dp, color = VColors.violet, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(VColors.white.copy(alpha = 0.2f)),
                        )
                        Text(
                            text = "88%",
                            style = VTypography.caption.copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = VColors.white),
                        )
                    }
                }
                ArtifactCard(height = 80.dp, color = VColors.mint, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = VIcons.Check,
                            contentDescription = null,
                            tint = VColors.white,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkChildArtifact() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Central glow.
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(VColors.violet.copy(alpha = 0.08f)),
        )
        // Floating school cards.
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = (-40).dp)
                .size(64.dp)
                .clip(VShapes.xl)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = VIcons.School, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(28.dp))
        }
        Box(
            modifier = Modifier
                .offset(x = 60.dp, y = (-20).dp)
                .size(64.dp)
                .clip(VShapes.xl)
                .background(VColors.mintSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = VIcons.MapPin, contentDescription = null, tint = VColors.mint, modifier = Modifier.size(28.dp))
        }
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = 50.dp)
                .size(64.dp)
                .clip(VShapes.xl)
                .background(VColors.coralSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = VIcons.User, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(28.dp))
        }
        // Center CTA orb.
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(VColors.violet)
                .border(4.dp, VColors.white, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = VIcons.UserPlus,
                    contentDescription = null,
                    tint = VColors.white,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Link",
                    style = VTypography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = VColors.white),
                )
            }
        }
    }
}

@Composable
private fun ArtifactCard(
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(VShapes.xxl)
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private fun Float.absoluteValue(): Float = if (this < 0) -this else this
