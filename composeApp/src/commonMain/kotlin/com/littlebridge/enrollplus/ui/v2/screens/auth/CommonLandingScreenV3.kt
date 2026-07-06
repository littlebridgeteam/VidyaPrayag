package com.littlebridge.enrollplus.ui.v2.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.VBrandLogo
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.i18n.domain.model.SUPPORTED_LANGUAGES
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.koinInject
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.VThemeRegistry
import com.littlebridge.enrollplus.ui.v2.theme.colored
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Easing
// ─────────────────────────────────────────────────────────────────────────────

private val EaseOutCubic = androidx.compose.animation.core.CubicBezierEasing(0.215f, 0.61f, 0.355f, 1f)

// ─────────────────────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────────────────────

private data class EcosystemDomain(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val metrics: List<String>,
)

@Composable
private fun SchoolDomains() = listOf(
    EcosystemDomain(VIcons.ListChecks, appString(StringKeys.LV3_ECO_S1_T), appString(StringKeys.LV3_ECO_S1_S),
        listOf(appString(StringKeys.LV3_ECO_S1_M1), appString(StringKeys.LV3_ECO_S1_M2), appString(StringKeys.LV3_ECO_S1_M3))),
    EcosystemDomain(VIcons.GraduationCap, appString(StringKeys.LV3_ECO_S2_T), appString(StringKeys.LV3_ECO_S2_S),
        listOf(appString(StringKeys.LV3_ECO_S2_M1), appString(StringKeys.LV3_ECO_S2_M2), appString(StringKeys.LV3_ECO_S2_M3))),
    EcosystemDomain(VIcons.Heart, appString(StringKeys.LV3_ECO_S3_T), appString(StringKeys.LV3_ECO_S3_S),
        listOf(appString(StringKeys.LV3_ECO_S3_M1), appString(StringKeys.LV3_ECO_S3_M2), appString(StringKeys.LV3_ECO_S3_M3))),
    EcosystemDomain(VIcons.TrendingUp, appString(StringKeys.LV3_ECO_S4_T), appString(StringKeys.LV3_ECO_S4_S),
        listOf(appString(StringKeys.LV3_ECO_S4_M1), appString(StringKeys.LV3_ECO_S4_M2), appString(StringKeys.LV3_ECO_S4_M3))),
)

@Composable
private fun ParentDomains() = listOf(
    EcosystemDomain(VIcons.Calendar, appString(StringKeys.LV3_ECO_P1_T), appString(StringKeys.LV3_ECO_P1_S),
        listOf(appString(StringKeys.LV3_ECO_P1_M1), appString(StringKeys.LV3_ECO_P1_M2), appString(StringKeys.LV3_ECO_P1_M3))),
    EcosystemDomain(VIcons.BookOpen, appString(StringKeys.LV3_ECO_P2_T), appString(StringKeys.LV3_ECO_P2_S),
        listOf(appString(StringKeys.LV3_ECO_P2_M1), appString(StringKeys.LV3_ECO_P2_M2), appString(StringKeys.LV3_ECO_P2_M3))),
    EcosystemDomain(VIcons.Wallet, appString(StringKeys.LV3_ECO_P3_T), appString(StringKeys.LV3_ECO_P3_S),
        listOf(appString(StringKeys.LV3_ECO_P3_M1), appString(StringKeys.LV3_ECO_P3_M2), appString(StringKeys.LV3_ECO_P3_M3))),
    EcosystemDomain(VIcons.Chat, appString(StringKeys.LV3_ECO_P4_T), appString(StringKeys.LV3_ECO_P4_S),
        listOf(appString(StringKeys.LV3_ECO_P4_M1), appString(StringKeys.LV3_ECO_P4_M2), appString(StringKeys.LV3_ECO_P4_M3))),
)

private data class TimelineEvent(val time: String, val title: String, val detail: String)

@Composable
private fun SchoolDay() = listOf(
    TimelineEvent("08:00", appString(StringKeys.LV3_TL_S1_T), appString(StringKeys.LV3_TL_S1_D)),
    TimelineEvent("09:15", appString(StringKeys.LV3_TL_S2_T), appString(StringKeys.LV3_TL_S2_D)),
    TimelineEvent("11:30", appString(StringKeys.LV3_TL_S3_T), appString(StringKeys.LV3_TL_S3_D)),
    TimelineEvent("02:00", appString(StringKeys.LV3_TL_S4_T), appString(StringKeys.LV3_TL_S4_D)),
    TimelineEvent("04:30", appString(StringKeys.LV3_TL_S5_T), appString(StringKeys.LV3_TL_S5_D)),
)

@Composable
private fun ParentDay() = listOf(
    TimelineEvent("07:30", appString(StringKeys.LV3_TL_P1_T), appString(StringKeys.LV3_TL_P1_D)),
    TimelineEvent("09:00", appString(StringKeys.LV3_TL_P2_T), appString(StringKeys.LV3_TL_P2_D)),
    TimelineEvent("12:30", appString(StringKeys.LV3_TL_P3_T), appString(StringKeys.LV3_TL_P3_D)),
    TimelineEvent("03:30", appString(StringKeys.LV3_TL_P4_T), appString(StringKeys.LV3_TL_P4_D)),
    TimelineEvent("05:00", appString(StringKeys.LV3_TL_P5_T), appString(StringKeys.LV3_TL_P5_D)),
)

private data class TrustMetric(val value: String, val label: String)

@Composable
private fun SchoolTrust() = listOf(
    TrustMetric(appString(StringKeys.LV3_TRUST_S1_V), appString(StringKeys.LV3_TRUST_S1_L)),
    TrustMetric("12,000+", appString(StringKeys.LV3_TRUST_S2_L)),
    TrustMetric("99.9%", appString(StringKeys.LV3_TRUST_S3_L)),
)

@Composable
private fun ParentTrust() = listOf(
    TrustMetric(appString(StringKeys.LV3_TRUST_P1_V), appString(StringKeys.LV3_TRUST_P1_L)),
    TrustMetric(appString(StringKeys.LV3_TRUST_P2_V), appString(StringKeys.LV3_TRUST_P2_L)),
    TrustMetric(appString(StringKeys.LV3_TRUST_P3_V), appString(StringKeys.LV3_TRUST_P3_L)),
)

@Composable
private fun SchoolMorph() = listOf(
    appString(StringKeys.LV3_SCHOOL_MORPH_1),
    appString(StringKeys.LV3_SCHOOL_MORPH_2),
    appString(StringKeys.LV3_SCHOOL_MORPH_3),
    appString(StringKeys.LV3_SCHOOL_MORPH_4),
)

@Composable
private fun ParentMorph() = listOf(
    appString(StringKeys.LV3_PARENT_MORPH_1),
    appString(StringKeys.LV3_PARENT_MORPH_2),
    appString(StringKeys.LV3_PARENT_MORPH_3),
    appString(StringKeys.LV3_PARENT_MORPH_4),
)

@Composable
private fun SchoolAi() = listOf(
    appString(StringKeys.LV3_AI_S1),
    appString(StringKeys.LV3_AI_S2),
    appString(StringKeys.LV3_AI_S3),
)

@Composable
private fun ParentAi() = listOf(
    appString(StringKeys.LV3_AI_P1),
    appString(StringKeys.LV3_AI_P2),
    appString(StringKeys.LV3_AI_P3),
)

private data class TestimonialData(
    val quote: String,
    val role: String,
    val org: String,
)

@Composable
private fun SchoolTestimonial() = TestimonialData(
    appString(StringKeys.LV3_TEST_S_QUOTE),
    appString(StringKeys.LV3_TEST_S_ROLE), appString(StringKeys.LV3_TEST_S_ORG),
)

@Composable
private fun ParentTestimonial() = TestimonialData(
    appString(StringKeys.LV3_TEST_P_QUOTE),
    appString(StringKeys.LV3_TEST_P_ROLE), appString(StringKeys.LV3_TEST_P_ORG),
)

// ─────────────────────────────────────────────────────────────────────────────
// Main screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CommonLandingScreenV3(
    onParent: () -> Unit,
    onAdmin: () -> Unit,
    modifier: Modifier = Modifier,
    onLegal: (LegalDoc) -> Unit = {},
) = VTheme(themeDef = VThemeRegistry.resolve("light")) {
    val c = VTheme.colors
    val d = VTheme.dimens

    var tab by remember { mutableStateOf(0) }
    var prevTab by remember { mutableStateOf(0) }

    val enterAlpha = remember { Animatable(0f) }
    val enterY = remember { Animatable(24f) }
    LaunchedEffect(Unit) {
        launch { enterAlpha.animateTo(1f, tween(500, easing = EaseOutCubic)) }
        launch { enterY.animateTo(0f, tween(500, easing = EaseOutCubic)) }
    }

    Box(modifier.fillMaxSize().background(c.background)) {
        AmbientBackground()

        Column(
            Modifier
                .fillMaxSize()
                .widthIn(max = d.maxContentWidth)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    alpha = enterAlpha.value
                    translationY = enterY.value * density
                },
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                ImmersiveHero(tab = tab, prevTab = prevTab, onTabChange = { newTab ->
                    if (newTab != tab) { prevTab = tab; tab = newTab }
                })
                Spacer(Modifier.height(d.xl))
                CommandCenterPreview(tab = tab, prevTab = prevTab)
                Spacer(Modifier.height(d.xxl))
                EcosystemSection(tab = tab, prevTab = prevTab)
                Spacer(Modifier.height(d.xxl))
                AIInsightSection(tab = tab, prevTab = prevTab)
                Spacer(Modifier.height(d.xxl))
                SchoolTimelineSection(tab = tab, prevTab = prevTab)
                Spacer(Modifier.height(d.xxl))
                TrustMetricsSection(tab = tab, prevTab = prevTab)
                Spacer(Modifier.height(d.xxl))
                TestimonialSection(tab = tab, prevTab = prevTab)
                Spacer(Modifier.height(d.xl))
            }

            PremiumCtaDock(
                tab = tab,
                onAdmin = onAdmin,
                onParent = onParent,
                onLegal = onLegal,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Ambient background — floating blurred circles + subtle mesh
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AmbientBackground() {
    val c = VTheme.colors
    val transition = rememberInfiniteTransition(label = "ambient")

    val y1 by transition.animateFloat(
        initialValue = 0f, targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float-y1",
    )
    val y2 by transition.animateFloat(
        initialValue = 0f, targetValue = -25f,
        animationSpec = infiniteRepeatable(tween(5000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float-y2",
    )
    val y3 by transition.animateFloat(
        initialValue = 0f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float-y3",
    )

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .offset(x = (-40).dp, y = (80 + y1).dp)
                .size(180.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(c.teal.copy(alpha = 0.08f)),
        )
        Box(
            Modifier
                .offset(x = 260.dp, y = (200 + y2).dp)
                .size(140.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(c.accent.copy(alpha = 0.06f)),
        )
        Box(
            Modifier
                .offset(x = 30.dp, y = (500 + y3).dp)
                .size(200.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(c.tealDeep.copy(alpha = 0.05f)),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Immersive hero — brand + morphing headline + audience pills
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ImmersiveHero(
    tab: Int,
    prevTab: Int,
    onTabChange: (Int) -> Unit,
) {
    val c = VTheme.colors
    val d = VTheme.dimens

    val logoScale = remember { Animatable(0.8f) }
    val logoGlow = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(100)
        launch { logoScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 220f)) }
        launch { logoGlow.animateTo(1f, tween(800, easing = EaseOutCubic)) }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to c.teal,
                    0.5f to c.teal.copy(alpha = 0.92f),
                    1f to c.background,
                )
            )
            .statusBarsPadding()
            .padding(bottom = d.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(d.lg))

        // Logo with glow
        Box(contentAlignment = Alignment.Center) {
            // Glow behind logo
            Box(
                Modifier
                    .size(90.dp)
                    .graphicsLayer {
                        alpha = logoGlow.value * 0.4f
                        scaleX = 1f + logoGlow.value * 0.5f
                        scaleY = 1f + logoGlow.value * 0.5f
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(Brush.radialGradient(listOf(c.teal.copy(alpha = 0.6f), Color.Transparent))),
            )
            Box(
                Modifier.graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                },
                contentAlignment = Alignment.Center,
            ) {
                VBrandLogo(size = 56.dp, cornerRadius = 16.dp)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Brand name
        Text(
            appString(StringKeys.LV3_BRAND),
            style = VTheme.type.h1.colored(Color.White).copy(fontWeight = FontWeight.ExtraBold),
        )

        Spacer(Modifier.height(20.dp))

        // Morphing word
        MorphingWord(tab = tab)

        Spacer(Modifier.height(8.dp))

        // Tagline
        AnimatedContent(
            targetState = tab,
            transitionSpec = { tabSlide(tab, prevTab) },
            label = "hero-tagline",
        ) { t ->
            Text(
                if (t == 0)
                    appString(StringKeys.LV3_SCHOOL_TAGLINE)
                else
                    appString(StringKeys.LV3_PARENT_TAGLINE),
                style = VTheme.type.body.colored(Color.White.copy(alpha = 0.85f)),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(d.lg))

        // Audience pills
        AudiencePills(selected = tab, onSelect = onTabChange)

        Spacer(Modifier.height(d.md))

        // Context line that swaps with tab
        AnimatedContent(
            targetState = tab,
            transitionSpec = { tabSlide(tab, prevTab) },
            label = "hero-context",
        ) { t ->
            Text(
                if (t == 0) appString(StringKeys.LV3_SCHOOL_CONTEXT)
                else appString(StringKeys.LV3_PARENT_CONTEXT),
                style = VTheme.type.caption.colored(Color.White.copy(alpha = 0.7f)),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MorphingWord(tab: Int) {
    val words = if (tab == 0) SchoolMorph() else ParentMorph()
    var wordIndex by remember { mutableStateOf(0) }
    LaunchedEffect(tab) {
        wordIndex = 0
        while (true) {
            delay(2200)
            wordIndex = (wordIndex + 1) % words.size
        }
    }
    AnimatedContent(
        targetState = wordIndex,
        transitionSpec = {
            (fadeIn(tween(400)) + scaleIn(tween(400, easing = FastOutSlowInEasing), initialScale = 0.8f)) togetherWith
                fadeOut(tween(300))
        },
        label = "morph-word",
    ) { i ->
        Text(
            words[i],
            style = VTheme.type.h1.colored(Color.White).copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
            ),
        )
    }
}

@Composable
private fun AudiencePills(
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val labels = listOf(appString(StringKeys.LV3_PILL_SCHOOLS), appString(StringKeys.LV3_PILL_PARENTS))
    val icons = listOf(VIcons.School, VIcons.User)

    Row(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        labels.forEachIndexed { i, label ->
            val isSelected = selected == i
            val interaction = remember { MutableInteractionSource() }
            val isPressed by interaction.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "pill-scale-$i",
            )

            Row(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .then(
                        if (isSelected) Modifier.background(Color.White)
                        else Modifier.background(Color.Transparent)
                    )
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                    ) { onSelect(i) }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    icons[i],
                    contentDescription = null,
                    tint = if (isSelected) VTheme.colors.tealDeep else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    label,
                    style = VTheme.type.h4.colored(
                        if (isSelected) VTheme.colors.tealDeep else Color.White.copy(alpha = 0.9f)
                    ).copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Command center preview — fake live dashboard with floating metrics
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommandCenterPreview(tab: Int, prevTab: Int) {
    val c = VTheme.colors
    val d = VTheme.dimens

    val revealAlpha = remember { Animatable(0f) }
    val revealY = remember { Animatable(30f) }
    LaunchedEffect(Unit) {
        delay(600)
        launch { revealAlpha.animateTo(1f, tween(600, easing = EaseOutCubic)) }
        launch { revealY.animateTo(0f, tween(600, easing = EaseOutCubic)) }
    }

    val floatTransition = rememberInfiniteTransition(label = "cmd-float")
    val floatY by floatTransition.animateFloat(
        initialValue = 0f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "cmd-float-y",
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = d.lg)
            .graphicsLayer {
                alpha = revealAlpha.value
                translationY = revealY.value * density
            },
    ) {
        AnimatedContent(
            targetState = tab,
            transitionSpec = { tabSlide(tab, prevTab) },
            label = "cmd-center",
        ) { t ->
            if (t == 0) {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        appString(StringKeys.LV3_CMD_SCHOOL_EYEBROW),
                        style = VTheme.type.label.colored(c.tealDeep),
                        modifier = Modifier.padding(bottom = 12.dp),
                    )

                    // Main command card
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .shadow(16.dp, RoundedCornerShape(d.radiusXl))
                            .clip(RoundedCornerShape(d.radiusXl))
                            .background(
                                Brush.linearGradient(
                                    listOf(c.tealDeep, c.navy),
                                )
                            ),
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    appString(StringKeys.LV3_CMD_SCHOOL_TITLE),
                                    style = VTheme.type.h3.colored(Color.White),
                                )
                                val pulse by floatTransition.animateFloat(
                                    initialValue = 0.4f, targetValue = 1f,
                                    animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
                                    label = "pulse",
                                )
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .graphicsLayer { alpha = pulse }
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(Color(0xFF4ADE80)),
                                    )
                                    Text("LIVE", style = VTheme.type.label.colored(Color(0xFF4ADE80)))
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            CommandMetricRow(appString(StringKeys.LV3_CMD_STUDENTS), "1,240", 0.92f)
                            Spacer(Modifier.height(10.dp))
                            CommandMetricRow(appString(StringKeys.LV3_CMD_TEACHERS), "84", 0.75f)
                            Spacer(Modifier.height(10.dp))
                            CommandMetricRow(appString(StringKeys.LV3_CMD_ATTENDANCE), "96%", 0.96f)
                            Spacer(Modifier.height(10.dp))
                            CommandMetricRow(appString(StringKeys.LV3_CMD_FEE), "₹12,40,000", 0.85f)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        FloatingMiniMetric(
                            title = appString(StringKeys.LV3_CMD_ADMISSIONS),
                            value = "+32",
                            trend = appString(StringKeys.LV3_CMD_ADMISSIONS_TREND),
                            modifier = Modifier.weight(1f).graphicsLayer { translationY = floatY * density },
                        )
                        FloatingMiniMetric(
                            title = appString(StringKeys.LV3_CMD_SATISFACTION),
                            value = "98%",
                            trend = appString(StringKeys.LV3_CMD_SATISFACTION_TREND),
                            modifier = Modifier.weight(1f).graphicsLayer { translationY = -floatY * density },
                        )
                    }
                }
            } else {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        appString(StringKeys.LV3_CMD_PARENT_EYEBROW),
                        style = VTheme.type.label.colored(c.tealDeep),
                        modifier = Modifier.padding(bottom = 12.dp),
                    )

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .shadow(16.dp, RoundedCornerShape(d.radiusXl))
                            .clip(RoundedCornerShape(d.radiusXl))
                            .background(
                                Brush.linearGradient(
                                    listOf(c.tealDeep, c.navy),
                                )
                            ),
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    appString(StringKeys.LV3_CMD_PARENT_TITLE),
                                    style = VTheme.type.h3.colored(Color.White),
                                )
                                val pulse2 by floatTransition.animateFloat(
                                    initialValue = 0.4f, targetValue = 1f,
                                    animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
                                    label = "pulse2",
                                )
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .graphicsLayer { alpha = pulse2 }
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(Color(0xFF4ADE80)),
                                    )
                                    Text(appString(StringKeys.LV3_LIVE), style = VTheme.type.label.colored(Color(0xFF4ADE80)))
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            CommandMetricRow(appString(StringKeys.LV3_CMD_P_ATTENDANCE), appString(StringKeys.LV3_CMD_P_ATTENDANCE_V), 0.96f)
                            Spacer(Modifier.height(10.dp))
                            CommandMetricRow(appString(StringKeys.LV3_CMD_P_LAST_TEST), "87%", 0.87f)
                            Spacer(Modifier.height(10.dp))
                            CommandMetricRow(appString(StringKeys.LV3_CMD_P_FEES), appString(StringKeys.LV3_CMD_P_FEES_V), 1.0f)
                            Spacer(Modifier.height(10.dp))
                            CommandMetricRow(appString(StringKeys.LV3_CMD_P_MESSAGES), "3 new", 0.3f)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        FloatingMiniMetric(
                            title = appString(StringKeys.LV3_CMD_HOMEWORK),
                            value = "2",
                            trend = appString(StringKeys.LV3_CMD_HOMEWORK_TREND),
                            modifier = Modifier.weight(1f).graphicsLayer { translationY = floatY * density },
                        )
                        FloatingMiniMetric(
                            title = appString(StringKeys.LV3_CMD_PTM),
                            value = "Fri",
                            trend = appString(StringKeys.LV3_CMD_PTM_TREND),
                            modifier = Modifier.weight(1f).graphicsLayer { translationY = -floatY * density },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandMetricRow(label: String, value: String, barProgress: Float) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = VTheme.type.body.colored(Color.White.copy(alpha = 0.7f)))
        Text(value, style = VTheme.type.dataLg.colored(Color.White).copy(fontWeight = FontWeight.Bold))
    }
    Spacer(Modifier.height(4.dp))
    // Progress bar
    Box(
        Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.12f)),
    ) {
        Box(
            Modifier
                .fillMaxWidth(barProgress)
                .height(4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Brush.horizontalGradient(listOf(VTheme.colors.teal, Color.White))),
        )
    }
}

@Composable
private fun FloatingMiniMetric(
    title: String,
    value: String,
    trend: String,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors
    Box(
        modifier
            .shadow(8.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(c.card)
            .border(1.dp, c.hairline, RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Column {
            Text(title, style = VTheme.type.caption.colored(c.ink3))
            Spacer(Modifier.height(4.dp))
            Text(value, style = VTheme.type.h2.colored(c.tealDeep).copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(2.dp))
            Text(trend, style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 10.sp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Ecosystem section — four domain cards
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EcosystemSection(tab: Int, prevTab: Int) {
    val c = VTheme.colors
    val d = VTheme.dimens
    val domains = if (tab == 0) SchoolDomains() else ParentDomains()

    Column(Modifier.fillMaxWidth().padding(horizontal = d.lg)) {
        AnimatedContent(
            targetState = tab,
            transitionSpec = { tabSlide(tab, prevTab) },
            label = "eco-eyebrow",
        ) { t ->
            Text(if (t == 0) appString(StringKeys.LV3_ECO_SCHOOL_EYEBROW) else appString(StringKeys.LV3_ECO_PARENT_EYEBROW),
                style = VTheme.type.label.colored(c.tealDeep))
        }
        Spacer(Modifier.height(16.dp))

        domains.forEachIndexed { i, domain ->
            EcosystemCard(domain, index = i)
            if (i != domains.lastIndex) Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EcosystemCard(domain: EcosystemDomain, index: Int) {
    val c = VTheme.colors
    val d = VTheme.dimens
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "eco-press-$index",
    )

    val revealAlpha = remember { Animatable(0f) }
    val revealY = remember { Animatable(20f) }
    LaunchedEffect(Unit) {
        delay(800 + index * 100L)
        launch { revealAlpha.animateTo(1f, tween(400, easing = EaseOutCubic)) }
        launch { revealY.animateTo(0f, tween(400, easing = EaseOutCubic)) }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = revealAlpha.value
                translationY = revealY.value * density
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(6.dp, RoundedCornerShape(d.radiusCard))
            .clip(RoundedCornerShape(d.radiusCard))
            .background(c.card)
            .border(1.dp, c.hairline, RoundedCornerShape(d.radiusCard))
            .clickable(interactionSource = interaction, indication = null) {}
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Icon with gradient circle
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(c.teal.copy(alpha = 0.15f), c.accent.copy(alpha = 0.1f)))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(domain.icon, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(24.dp))
        }

        Column(Modifier.weight(1f)) {
            Text(domain.title, style = VTheme.type.h3.colored(c.ink).copy(fontSize = 16.sp))
            Spacer(Modifier.height(2.dp))
            Text(domain.subtitle, style = VTheme.type.caption.colored(c.ink3))
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                domain.metrics.forEach { metric ->
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(c.cream)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(Modifier.size(4.dp).clip(RoundedCornerShape(999.dp)).background(c.teal))
                        Text(metric, style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 10.sp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AI insight — glass card with typing animation
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AIInsightSection(tab: Int, prevTab: Int) {
    val c = VTheme.colors
    val d = VTheme.dimens
    val insights = if (tab == 0) SchoolAi() else ParentAi()

    var insightIndex by remember(tab) { mutableStateOf(0) }
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(tab) {
        insightIndex = 0
        while (true) {
            val fullText = insights[insightIndex]
            displayedText = ""
            for (ch in fullText) {
                displayedText += ch
                delay(30)
            }
            delay(2500)
            insightIndex = (insightIndex + 1) % insights.size
        }
    }

    val glowTransition = rememberInfiniteTransition(label = "ai-glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow-alpha",
    )

    Column(Modifier.fillMaxWidth().padding(horizontal = d.lg)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(VIcons.Sparkles, contentDescription = null, tint = c.accent, modifier = Modifier.size(18.dp))
            Text(appString(StringKeys.LV3_AI_TITLE), style = VTheme.type.h4.colored(c.accentDeep).copy(fontWeight = FontWeight.Bold))
        }
        Spacer(Modifier.height(12.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(d.radiusCard))
                .clip(RoundedCornerShape(d.radiusCard))
                .background(
                    Brush.verticalGradient(
                        listOf(c.accentTint, c.card),
                    )
                )
                .border(
                    BorderStroke(1.dp, c.accent.copy(alpha = glowAlpha * 0.5f)),
                    RoundedCornerShape(d.radiusCard),
                )
                .padding(20.dp),
        ) {
            Column {
                // AI indicator
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .graphicsLayer { alpha = glowAlpha }
                            .clip(RoundedCornerShape(999.dp))
                            .background(c.accent),
                    )
                    Text(appString(StringKeys.LV3_AI_LABEL), style = VTheme.type.label.colored(c.accentDeep))
                }
                Spacer(Modifier.height(12.dp))
                // Typing text
                Text(
                    displayedText,
                    style = VTheme.type.bodyStrong.colored(c.ink).copy(lineHeight = 22.sp),
                    minLines = 2,
                )
                // Cursor
                Spacer(Modifier.height(2.dp))
                Box(
                    Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(c.accent),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// School timeline — a day in the life
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SchoolTimelineSection(tab: Int, prevTab: Int) {
    val c = VTheme.colors
    val d = VTheme.dimens
    val events = if (tab == 0) SchoolDay() else ParentDay()

    Column(Modifier.fillMaxWidth().padding(horizontal = d.lg)) {
        AnimatedContent(
            targetState = tab,
            transitionSpec = { tabSlide(tab, prevTab) },
            label = "timeline-eyebrow",
        ) { t ->
            Text(if (t == 0) appString(StringKeys.LV3_TL_SCHOOL_EYEBROW) else appString(StringKeys.LV3_TL_PARENT_EYEBROW),
                style = VTheme.type.label.colored(c.tealDeep))
        }
        Spacer(Modifier.height(16.dp))

        events.forEachIndexed { i, event ->
            TimelineRow(event, isLast = i == events.lastIndex)
        }
    }
}

@Composable
private fun TimelineRow(event: TimelineEvent, isLast: Boolean) {
    val c = VTheme.colors
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Timeline dot + line
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(c.teal)
                    .border(2.dp, c.teal.copy(alpha = 0.2f), RoundedCornerShape(999.dp)),
            )
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(c.hairline),
                )
            }
        }

        // Content
        Column(Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 8.dp)) {
            Text(event.time, style = VTheme.type.caption.colored(c.tealDeep).copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(2.dp))
            Text(event.title, style = VTheme.type.bodyStrong.colored(c.ink))
            Spacer(Modifier.height(2.dp))
            Text(event.detail, style = VTheme.type.caption.colored(c.ink3))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Trust metrics — large animated numbers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TrustMetricsSection(tab: Int, prevTab: Int) {
    val c = VTheme.colors
    val d = VTheme.dimens
    val metrics = if (tab == 0) SchoolTrust() else ParentTrust()

    val revealAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(1000)
        revealAlpha.animateTo(1f, tween(500, easing = EaseOutCubic))
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = d.lg)
            .graphicsLayer { alpha = revealAlpha.value },
    ) {
        AnimatedContent(
            targetState = tab,
            transitionSpec = { tabSlide(tab, prevTab) },
            label = "trust-eyebrow",
        ) { t ->
            Text(if (t == 0) appString(StringKeys.LV3_TRUST_SCHOOL_EYEBROW) else appString(StringKeys.LV3_TRUST_PARENT_EYEBROW),
                style = VTheme.type.label.colored(c.tealDeep))
        }
        Spacer(Modifier.height(16.dp))

        metrics.forEach { metric ->
            TrustMetricRow(metric)
            if (metric != metrics.last()) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(c.hairline).padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun TrustMetricRow(metric: TrustMetric) {
    val c = VTheme.colors
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            metric.value,
            style = VTheme.type.h1.colored(c.tealDeep).copy(
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
            ),
        )
        Text(
            metric.label,
            style = VTheme.type.body.colored(c.ink2),
            textAlign = TextAlign.End,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Testimonial — large quote
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TestimonialSection(tab: Int, prevTab: Int) {
    val c = VTheme.colors
    val d = VTheme.dimens
    val data = if (tab == 0) SchoolTestimonial() else ParentTestimonial()

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = d.lg)
            .shadow(8.dp, RoundedCornerShape(d.radiusCard))
            .clip(RoundedCornerShape(d.radiusCard))
            .background(Brush.verticalGradient(listOf(c.teal.copy(alpha = 0.08f), c.card)))
            .border(1.dp, c.hairline, RoundedCornerShape(d.radiusCard))
            .padding(24.dp),
    ) {
        AnimatedContent(
            targetState = tab,
            transitionSpec = { tabSlide(tab, prevTab) },
            label = "testimonial",
        ) { t ->
            val td = if (t == 0) SchoolTestimonial() else ParentTestimonial()
            Column {
                Text(
                    "\u201C",
                    style = VTheme.type.h1.colored(c.teal).copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    td.quote,
                    style = VTheme.type.h2.colored(c.ink).copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(c.teal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(VIcons.User, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text(td.role, style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 13.sp))
                        Text(td.org, style = VTheme.type.caption.colored(c.ink3))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Premium CTA dock
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PremiumCtaDock(
    tab: Int,
    onAdmin: () -> Unit,
    onParent: () -> Unit,
    onLegal: (LegalDoc) -> Unit,
) {
    val c = VTheme.colors
    val d = VTheme.dimens

    val dockAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(1200)
        dockAlpha.animateTo(1f, tween(400, easing = EaseOutCubic))
    }

    Column(
        Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = dockAlpha.value }
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.card)
            .padding(top = 20.dp)
            .padding(horizontal = d.lg)
            .navigationBarsPadding()
            .padding(bottom = 12.dp),
    ) {
        Text(
            appString(StringKeys.LV3_CTA_PROMPT),
            style = VTheme.type.h4.colored(c.ink2).copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Primary button — 60%
            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "cta-swap",
                modifier = Modifier.weight(0.6f),
            ) { t ->
                if (t == 0) {
                    VButton(
                        text = appString(StringKeys.LV3_CTA_ENTER),
                        onClick = onAdmin,
                        variant = VButtonVariant.Primary,
                        tone = VButtonTone.Teal,
                        soft = false,
                        size = VButtonSize.Lg,
                        full = true,
                        leading = {
                            Icon(VIcons.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        },
                    )
                } else {
                    VButton(
                        text = appString(StringKeys.LV3_CTA_ENTER),
                        onClick = onParent,
                        variant = VButtonVariant.Primary,
                        tone = VButtonTone.Teal,
                        soft = false,
                        size = VButtonSize.Lg,
                        full = true,
                        leading = {
                            Icon(VIcons.User, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        },
                    )
                }
            }

            // Secondary button — 40%
            val secondaryText = if (tab == 0) appString(StringKeys.LV3_CTA_PARENT) else appString(StringKeys.LV3_CTA_SCHOOL)
            val secondaryAction = if (tab == 0) onParent else onAdmin
            VButton(
                text = secondaryText,
                onClick = secondaryAction,
                variant = VButtonVariant.Secondary,
                tone = VButtonTone.Teal,
                soft = false,
                size = VButtonSize.Lg,
                full = true,
                modifier = Modifier.weight(0.4f),
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                appString(StringKeys.LV3_FOOTER_PREFIX),
                style = VTheme.type.caption.colored(c.ink3),
                textAlign = TextAlign.Center,
            )
            Text(
                appString(StringKeys.LV3_FOOTER_TERMS),
                style = VTheme.type.caption.colored(c.tealDeep).copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onLegal(LegalDoc.Terms) },
            )
            Text(appString(StringKeys.LV3_FOOTER_AND), style = VTheme.type.caption.colored(c.ink3))
            Text(
                appString(StringKeys.LV3_FOOTER_PRIVACY),
                style = VTheme.type.caption.colored(c.tealDeep).copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onLegal(LegalDoc.Privacy) },
            )
        }

        Spacer(Modifier.height(10.dp))
        LanguageSwitcherRow()
    }
}

@Composable
private fun LanguageSwitcherRow() {
    val localeManager = koinInject<LocaleManager>()
    val currentLocale by localeManager.currentLocale.collectAsState()
    val c = VTheme.colors

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SUPPORTED_LANGUAGES.forEach { lang ->
            val isSelected = lang.code == currentLocale
            Text(
                text = lang.nativeName,
                style = VTheme.type.caption.colored(if (isSelected) c.tealDeep else c.ink3).copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) c.tealDeep.copy(alpha = 0.08f) else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { localeManager.setLocale(lang.code) }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
            if (lang.code != SUPPORTED_LANGUAGES.last().code) {
                Text(
                    text = "·",
                    style = VTheme.type.caption.colored(c.ink3),
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Motion helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun tabSlide(target: Int, previous: Int): ContentTransform =
    if (target >= previous) {
        (slideInHorizontally(tween(280, easing = EaseOutCubic)) { it / 6 } + fadeIn(tween(280))) togetherWith
            (slideOutHorizontally(tween(280, easing = EaseOutCubic)) { -it / 6 } + fadeOut(tween(280)))
    } else {
        (slideInHorizontally(tween(280, easing = EaseOutCubic)) { -it / 6 } + fadeIn(tween(280))) togetherWith
            (slideOutHorizontally(tween(280, easing = EaseOutCubic)) { it / 6 } + fadeOut(tween(280)))
    }
