package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.components.FilterChip
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.components.VProgressBarSegments
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

/**
 * School onboarding — multi-step wizard for new school registration.
 * Uses the cream-based design language matching the landing/login screens.
 * Steps: School Details → Admin Details → Classes → Review → Done
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchoolOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    resumeStep: String = "BASIC",
) {
    val initialStep = remember(resumeStep) {
        when (resumeStep.uppercase()) {
            "BRANDING" -> 2
            "ACADEMIC" -> 3
            "REVIEW" -> 4
            else -> 1
        }
    }
    var step by remember { mutableIntStateOf(initialStep) }
    val total = 4

    // Form state
    var schoolName by remember { mutableStateOf("") }
    var board by remember { mutableStateOf("CBSE") }
    var schoolType by remember { mutableStateOf("Private Unaided") }
    var city by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var adminEmail by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("") }
    var classes by remember { mutableStateOf(listOf("Nursery", "LKG", "UKG", "Grade 1-5", "Grade 6-8", "Grade 9-10", "Grade 11-12")) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            VBackHeader(
                title = "School Onboarding",
                onBack = { if (step > 1) step-- else onBack() },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp, bottom = 48.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // Step indicator
                VProgressBarSegments(total = total, current = step - 1)
                Text(
                    text = "Step $step of $total",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )

                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        (slideInHorizontally(tween(300)) { it / 3 } + fadeIn(tween(300)))
                            .togetherWith(slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(300)))
                    },
                    label = "onboardStep",
                ) { current ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        when (current) {
                            1 -> {
                                Text(
                                    text = "School Details",
                                    style = VTypography.h2,
                                    color = VColors.ink,
                                )
                                Text(
                                    text = "Tell us about your school.",
                                    style = VTypography.bodySmall,
                                    color = VColors.ink2,
                                )
                                Spacer(Modifier.height(8.dp))
                                VInput(
                                    label = "School Name",
                                    value = schoolName,
                                    onValueChange = { schoolName = it },
                                    placeholder = "Saraswati Vidya Mandir",
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Text(
                                    text = "Board",
                                    style = VTypography.label,
                                    color = VColors.ink2,
                                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp),
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("CBSE", "ICSE", "UP State", "Other").forEach { b ->
                                        FilterChip(label = b, selected = board == b, onClick = { board = b })
                                    }
                                }
                                Text(
                                    text = "School Type",
                                    style = VTypography.label,
                                    color = VColors.ink2,
                                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp),
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Private Unaided", "Private Aided", "Government", "Other").forEach { t ->
                                        FilterChip(label = t, selected = schoolType == t, onClick = { schoolType = t })
                                    }
                                }
                                VInput(
                                    label = "City",
                                    value = city,
                                    onValueChange = { city = it },
                                    placeholder = "Lucknow",
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            2 -> {
                                Text(
                                    text = "Administrator Details",
                                    style = VTypography.h2,
                                    color = VColors.ink,
                                )
                                Text(
                                    text = "The primary admin for this school account.",
                                    style = VTypography.bodySmall,
                                    color = VColors.ink2,
                                )
                                Spacer(Modifier.height(8.dp))
                                VInput(
                                    label = "Your Name",
                                    value = adminName,
                                    onValueChange = { adminName = it },
                                    placeholder = "Dr. Anita Verma",
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                VInput(
                                    label = "Work Email",
                                    value = adminEmail,
                                    onValueChange = { adminEmail = it },
                                    placeholder = "office@svm.edu.in",
                                    keyboardType = KeyboardType.Email,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                VInput(
                                    label = "Phone Number",
                                    value = adminPhone,
                                    onValueChange = { adminPhone = it },
                                    placeholder = "+91 98XXX XXXXX",
                                    keyboardType = KeyboardType.Phone,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            3 -> {
                                Text(
                                    text = "Classes & Sections",
                                    style = VTypography.h2,
                                    color = VColors.ink,
                                )
                                Text(
                                    text = "Select the classes offered at your school.",
                                    style = VTypography.bodySmall,
                                    color = VColors.ink2,
                                )
                                Spacer(Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    listOf("Nursery", "LKG", "UKG", "Grade 1-5", "Grade 6-8", "Grade 9-10", "Grade 11-12").forEach { c ->
                                        val active = classes.contains(c)
                                        FilterChip(label = c, selected = active, onClick = {
                                            classes = if (active) classes - c else classes + c
                                        })
                                    }
                                }
                            }
                            4 -> {
                                Text(
                                    text = "Review & Confirm",
                                    style = VTypography.h2,
                                    color = VColors.ink,
                                )
                                Text(
                                    text = "Please review the details before completing onboarding.",
                                    style = VTypography.bodySmall,
                                    color = VColors.ink2,
                                )
                                Spacer(Modifier.height(8.dp))
                                ReviewRow("School Name", schoolName.ifBlank { "—" })
                                ReviewRow("Board", board)
                                ReviewRow("School Type", schoolType)
                                ReviewRow("City", city.ifBlank { "—" })
                                ReviewRow("Admin Name", adminName.ifBlank { "—" })
                                ReviewRow("Admin Email", adminEmail.ifBlank { "—" })
                                ReviewRow("Admin Phone", adminPhone.ifBlank { "—" })
                                ReviewRow("Classes", classes.joinToString(", "))
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    Modifier.fillMaxWidth()
                                        .background(VColors.successSoft, VShapes.lg)
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = VColors.success, modifier = Modifier.size(24.dp))
                                    Text(
                                        text = "Ready to complete! Tap 'Finish' to create your school account.",
                                        style = VTypography.bodySmall,
                                        color = VColors.success,
                                    )
                                }
                            }
                        }
                    }
                }

                VButton(
                    text = if (step < total) "Continue" else "Finish Onboarding",
                    onClick = {
                        if (step < total) step++
                        else onComplete()
                    },
                    enabled = when (step) {
                        1 -> schoolName.isNotBlank()
                        2 -> adminName.isNotBlank() && adminEmail.isNotBlank()
                        3 -> classes.isNotEmpty()
                        else -> true
                    },
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                )
            }
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = VTypography.bodySmall, color = VColors.ink2)
        Text(value, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
    }
}
