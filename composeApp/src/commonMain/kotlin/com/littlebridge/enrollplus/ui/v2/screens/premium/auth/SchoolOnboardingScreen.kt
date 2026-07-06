package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Premium school onboarding — multi-step wizard for new school registration.
 * Steps: School Details → Admin Details → Classes → Review → Done
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchoolOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    resumeStep: String = "BASIC",
) = PremiumTheme(isDark = false) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(40.dp))

        // Back button
        val backInteraction = remember { MutableInteractionSource() }
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.SurfaceContainerHigh)
                .pressScale(backInteraction, pressedScale = 0.9f)
                .clickable(
                    interactionSource = backInteraction,
                    indication = null,
                    onClick = { if (step > 1) step-- else onBack() },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Step indicator
        Text("Onboarding · Step $step of $total", style = VTypography.Eyebrow.copy(color = VColors.Primary))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(total) { i ->
                Box(
                    Modifier.weight(1f).height(4.dp).clip(VShapes.Full)
                        .background(if (i < step) VColors.Primary else VColors.SurfaceContainerHigh),
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (slideInHorizontally(tween(300)) { it / 3 } + fadeIn(tween(300)))
                    .togetherWith(slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(300)))
            },
            label = "onboardStep",
        ) { current ->
            Column {
                when (current) {
                    1 -> {
                        Text("School Details", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
                        Spacer(Modifier.height(8.dp))
                        Text("Tell us about your school.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
                        Spacer(Modifier.height(24.dp))
                        VTextInput(value = schoolName, onValueChange = { schoolName = it }, label = "School Name", placeholder = "Saraswati Vidya Mandir", authStyle = true, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(16.dp))
                        Text("Board", style = VTypography.FormLabelAuth.copy(color = VColors.OnSurfaceVariant), modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("CBSE", "ICSE", "UP State", "Other").forEach { b ->
                                VFilterChip(label = b, active = board == b, onClick = { board = b })
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("School Type", style = VTypography.FormLabelAuth.copy(color = VColors.OnSurfaceVariant), modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Private Unaided", "Private Aided", "Government", "Other").forEach { t ->
                                VFilterChip(label = t, active = schoolType == t, onClick = { schoolType = t })
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        VTextInput(value = city, onValueChange = { city = it }, label = "City", placeholder = "Lucknow", authStyle = true, modifier = Modifier.fillMaxWidth())
                    }
                    2 -> {
                        Text("Administrator Details", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
                        Spacer(Modifier.height(8.dp))
                        Text("The primary admin for this school account.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
                        Spacer(Modifier.height(24.dp))
                        VTextInput(value = adminName, onValueChange = { adminName = it }, label = "Your Name", placeholder = "Dr. Anita Verma", authStyle = true, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(16.dp))
                        VTextInput(value = adminEmail, onValueChange = { adminEmail = it }, label = "Work Email", placeholder = "office@svm.edu.in", keyboardType = KeyboardType.Email, authStyle = true, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(16.dp))
                        VTextInput(value = adminPhone, onValueChange = { adminPhone = it }, label = "Phone Number", placeholder = "+91 98XXX XXXXX", keyboardType = KeyboardType.Phone, authStyle = true, modifier = Modifier.fillMaxWidth())
                    }
                    3 -> {
                        Text("Classes & Sections", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
                        Spacer(Modifier.height(8.dp))
                        Text("Select the classes offered at your school.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
                        Spacer(Modifier.height(24.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Nursery", "LKG", "UKG", "Grade 1-5", "Grade 6-8", "Grade 9-10", "Grade 11-12").forEach { c ->
                                val active = classes.contains(c)
                                VFilterChip(label = c, active = active, onClick = {
                                    classes = if (active) classes - c else classes + c
                                })
                            }
                        }
                    }
                    4 -> {
                        Text("Review & Confirm", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
                        Spacer(Modifier.height(8.dp))
                        Text("Please review the details before completing onboarding.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
                        Spacer(Modifier.height(24.dp))
                        ReviewRow("School Name", schoolName.ifBlank { "—" })
                        ReviewRow("Board", board)
                        ReviewRow("School Type", schoolType)
                        ReviewRow("City", city.ifBlank { "—" })
                        ReviewRow("Admin Name", adminName.ifBlank { "—" })
                        ReviewRow("Admin Email", adminEmail.ifBlank { "—" })
                        ReviewRow("Admin Phone", adminPhone.ifBlank { "—" })
                        ReviewRow("Classes", classes.joinToString(", "))
                        Spacer(Modifier.height(16.dp))
                        Row(
                            Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.TertiaryContainer).padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = VColors.OnTertiaryContainer, modifier = Modifier.size(24.dp))
                            Text("Ready to complete! Tap 'Finish' to create your school account.", style = VTypography.UpdateText.copy(color = VColors.OnTertiaryContainer))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        VPrimaryButton(
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
            trailing = {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = VColors.OnPrimary, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}
