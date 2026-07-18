package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardState
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardViewModel
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.cardPressScale
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
internal fun GenerateTab(
    state: IdCardState,
    viewModel: IdCardViewModel,
) {
    var selectedTemplateId by remember { mutableStateOf<String?>(null) }
    var selectedScope by remember { mutableStateOf("all_students") }
    var classIdInput by remember { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        state.error?.let { err ->
            ErrorBanner(err) { viewModel.clearMessages(); viewModel.loadCards() }
            Spacer(modifier = Modifier.height(8.dp))
        }
        state.infoMessage?.let { msg ->
            InfoBanner(msg) { viewModel.clearMessages() }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.templates.isEmpty()) {
            VEmptyState(
                title = appString(StringKeys.SCH_NO_TEMPLATES),
                body = appString(StringKeys.SCH_NO_TEMPLATES_DESC),
                icon = Icons.Filled.School,
                modifier = Modifier.padding(top = 48.dp),
            )
            Spacer(modifier = Modifier.height(32.dp))
            return
        }

        // ── Section: Choose Template ──
        SectionLabel(
            title = appString(StringKeys.SCH_SELECT_TEMPLATE),
            subtitle = "Pick a template to generate cards from",
        )
        Spacer(modifier = Modifier.height(10.dp))

        state.templates.forEachIndexed { index, template ->
            val isSelected = selectedTemplateId == template.id
            val interactionSource = remember { MutableInteractionSource() }
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) VColors.violet else Color.Transparent,
                animationSpec = spring(stiffness = 300f),
                label = "border",
            )
            val bgAlpha by animateFloatAsState(
                targetValue = if (isSelected) 0.06f else 0f,
                animationSpec = spring(stiffness = 300f),
                label = "bg",
            )
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.6f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
                label = "icon",
            )

            VCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .cardPressScale(interactionSource)
                    .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                    .background(VColors.violet.copy(alpha = bgAlpha), RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { selectedTemplateId = template.id },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected)
                                    Brush.linearGradient(listOf(VColors.violet, VColors.violetInk))
                                else
                                    Brush.linearGradient(listOf(VColors.line, VColors.line))
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        val roleIcon = when (template.roleType) {
                            "student" -> Icons.Filled.Person
                            "teacher" -> Icons.Filled.People
                            else -> Icons.Filled.School
                        }
                        Icon(
                            roleIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .scale(iconScale),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            template.name,
                            style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                            color = VColors.ink,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            template.roleType.replaceFirstChar { it.uppercase() } +
                                    if (template.isActive) " \u00B7 Active" else " \u00B7 Inactive",
                            style = VTypography.caption.copy(color = VColors.ink3, fontSize = 12.sp),
                        )
                    }
                    AnimatedVisibility(visible = isSelected, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(VColors.violet),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Section: Who to Generate For ──
        SectionLabel(
            title = appString(StringKeys.SCH_SELECT_SCOPE),
            subtitle = "Choose which group to generate ID cards for",
        )
        Spacer(modifier = Modifier.height(10.dp))

        val scopeOptions = listOf(
            Triple("all_students", appString(StringKeys.SCH_ALL_STUDENTS), Icons.Filled.Person),
            Triple("all_staff", appString(StringKeys.SCH_ALL_STAFF), Icons.Filled.People),
            Triple("class", appString(StringKeys.SCH_BY_CLASS), Icons.Filled.School),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            scopeOptions.forEach { (scope, label, icon) ->
                val isSelected = selectedScope == scope
                val interactionSource = remember { MutableInteractionSource() }
                val cardColor by animateColorAsState(
                    targetValue = if (isSelected) VColors.violet else VColors.surfaceCard,
                    animationSpec = spring(stiffness = 300f),
                    label = "scopeBg",
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else VColors.ink,
                    animationSpec = spring(stiffness = 300f),
                    label = "scopeText",
                )

                VCard(
                    modifier = Modifier
                        .weight(1f)
                        .cardPressScale(interactionSource)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) { selectedScope = scope },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardColor, RoundedCornerShape(12.dp))
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = label,
                            style = VTypography.caption.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                            ),
                            color = textColor,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        // ── Class ID Input ──
        AnimatedVisibility(
            visible = selectedScope == "class",
            enter = expandVertically(tween(300, easing = FastOutSlowInEasing)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(150)),
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                VCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = "Class ID",
                            style = VTypography.caption.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            ),
                            color = VColors.ink3,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, VColors.line, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        ) {
                            Text(
                                text = classIdInput.ifBlank { "Enter class UUID..." },
                                style = VTypography.caption.copy(
                                    fontSize = 13.sp,
                                    color = if (classIdInput.isBlank()) VColors.ink3 else VColors.ink,
                                ),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Generate Button ──
        val canGenerate = selectedTemplateId != null &&
                (selectedScope != "class" || classIdInput.isNotBlank()) &&
                !state.isGenerating

        val pulseAlpha by if (state.isGenerating) {
            infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "pulse",
            )
        } else {
            remember { mutableStateOf(1f) }
        }

        VButton(
            text = if (state.isGenerating) appString(StringKeys.SCH_GENERATING) else appString(StringKeys.SCH_GENERATE_CARDS),
            onClick = {
                selectedTemplateId?.let { tid ->
                    viewModel.clearMessages()
                    val classId = if (selectedScope == "class" && classIdInput.isNotBlank()) classIdInput else null
                    viewModel.generateCards(tid, selectedScope, classId)
                }
            },
            variant = VButtonVariant.Primary,
            enabled = canGenerate,
            modifier = Modifier.fillMaxWidth(),
        )

        // ── Progress ──
        AnimatedVisibility(
            visible = state.isGenerating,
            enter = expandVertically(tween(300)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(150)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = VColors.violet,
                    trackColor = VColors.violet.copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                        label = "dot",
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .scale(dotScale)
                            .clip(CircleShape)
                            .background(VColors.violet),
                    )
                    Text(
                        text = appString(StringKeys.SCH_RENDERING_CARDS),
                        style = VTypography.caption.copy(
                            color = VColors.ink3,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionLabel(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            color = VColors.ink,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = VTypography.caption.copy(fontSize = 12.sp),
            color = VColors.ink3,
        )
    }
}

@Composable
private fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VColors.coral.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = message,
            style = VTypography.caption.copy(color = VColors.coral, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            modifier = Modifier.weight(1f),
            maxLines = 2,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(VColors.coral.copy(alpha = 0.12f))
                .clickable(onClick = onRetry)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text("Retry", style = VTypography.caption.copy(color = VColors.coral, fontWeight = FontWeight.Bold, fontSize = 11.sp))
        }
    }
}

@Composable
private fun InfoBanner(message: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VColors.mint.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = message,
            style = VTypography.caption.copy(color = VColors.mint, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            modifier = Modifier.weight(1f),
            maxLines = 2,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onDismiss)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Dismiss",
                tint = VColors.mint,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}
