package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.LinkChildState
import com.littlebridge.enrollplus.feature.parent.presentation.LinkChildViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.SchoolMatch
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VSearchField
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent link-child wizard — 3-step flow:
 * 1. Tell us about you (name + language)
 * 2. Find your child's school (search + select)
 * 3. Link your child (roll number + confirm)
 *
 * Reuses LinkChildViewModel for all data operations.
 */
@Composable
fun ParentLinkChildScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: LinkChildViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()
    var step by remember { mutableIntStateOf(1) }
    val total = 3

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
                .clickable(interactionSource = backInteraction, indication = null, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Step indicator
        Text("Step $step of $total", style = VTypography.Eyebrow.copy(color = VColors.Primary))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(total) { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(VShapes.Full)
                        .background(if (i < step) VColors.Primary else VColors.SurfaceContainerHigh),
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Crossfade(targetState = step, animationSpec = tween(240), label = "linkStep") { current ->
            Column {
                when (current) {
                    1 -> Step1AboutYou(state, viewModel)
                    2 -> Step2FindSchool(state, viewModel)
                    3 -> Step3LinkChild(state, viewModel)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // CTA
        VPrimaryButton(
            text = when (step) {
                1 -> "Continue"
                2 -> if (state.isSearching) "Searching..." else "Search Schools"
                else -> if (state.isLinking) "Linking..." else "Link My Child"
            },
            onClick = {
                when (step) {
                    1 -> { step = 2 }
                    2 -> {
                        if (state.selectedSchool != null) step = 3
                        else viewModel.searchSchools()
                    }
                    3 -> { viewModel.linkChild { onDone() } }
                }
            },
            enabled = when (step) {
                1 -> state.fullName.isNotBlank()
                2 -> !state.isSearching
                else -> !state.isLinking && state.selectedSchool != null
            },
            trailing = {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = VColors.OnPrimary, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.fillMaxWidth(),
        )

        if (step > 1) {
            Spacer(Modifier.height(12.dp))
            val backStepInteraction = remember { MutableInteractionSource() }
            Text(
                "Back",
                style = VTypography.ButtonText.copy(color = VColors.OnSurfaceVariant),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .pressScale(backStepInteraction, pressedScale = 0.95f)
                    .clickable(interactionSource = backStepInteraction, indication = null) { step-- }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        if (state.linkError != null) {
            Spacer(Modifier.height(12.dp))
            Text(state.linkError!!, style = VTypography.UpdateText.copy(color = VColors.Error))
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Step1AboutYou(state: LinkChildState, vm: LinkChildViewModel) {
    Text("Tell us about you", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
    Spacer(Modifier.height(8.dp))
    Text("We'll use this to personalize your experience.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    Spacer(Modifier.height(24.dp))
    VTextInput(
        value = state.fullName,
        onValueChange = vm::onFullNameChange,
        label = "Full Name",
        placeholder = "Enter your full name",
        keyboardType = KeyboardType.Text,
        authStyle = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(16.dp))
    Text("Preferred Language", style = VTypography.FormLabelAuth.copy(color = VColors.OnSurfaceVariant), modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("English", "Hindi", "Marathi", "Tamil").forEach { lang ->
            VFilterChip(label = lang, active = state.language == lang, onClick = { vm.onLanguageChange(lang) })
        }
    }
}

@Composable
private fun Step2FindSchool(state: LinkChildState, vm: LinkChildViewModel) {
    Text("Find your child's school", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
    Spacer(Modifier.height(8.dp))
    Text("Search by school name or city.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    Spacer(Modifier.height(24.dp))
    VSearchField(
        value = state.schoolQuery,
        onValueChange = vm::onSchoolQueryChange,
        modifier = Modifier.fillMaxWidth(),
    )
    if (state.searchError != null) {
        Spacer(Modifier.height(8.dp))
        Text(state.searchError!!, style = VTypography.UpdateText.copy(color = VColors.Error))
    }
    if (state.matches.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))
        Text("Results", style = VTypography.FormLabelAuth.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(8.dp))
        state.matches.forEach { school ->
            SchoolResultCard(school, isSelected = state.selectedSchool?.id == school.id, onClick = { vm.selectSchool(school) })
            Spacer(Modifier.height(8.dp))
        }
    }
    if (state.selectedSchool != null) {
        Spacer(Modifier.height(12.dp))
        Text("Selected: ${state.selectedSchool!!.name}", style = VTypography.UpdateTitle.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun Step3LinkChild(state: LinkChildState, vm: LinkChildViewModel) {
    Text("Link your child", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
    Spacer(Modifier.height(8.dp))
    Text("Enter your child's details to link them to your account.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    Spacer(Modifier.height(24.dp))
    VTextInput(
        value = state.childName,
        onValueChange = vm::onChildNameChange,
        label = "Child's Name",
        placeholder = "Enter child's full name",
        keyboardType = KeyboardType.Text,
        authStyle = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(16.dp))
    VTextInput(
        value = state.className,
        onValueChange = vm::onClassNameChange,
        label = "Class",
        placeholder = "e.g. Grade 5",
        keyboardType = KeyboardType.Text,
        authStyle = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(16.dp))
    VTextInput(
        value = state.section,
        onValueChange = vm::onSectionChange,
        label = "Section",
        placeholder = "e.g. A",
        keyboardType = KeyboardType.Text,
        authStyle = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(16.dp))
    VTextInput(
        value = state.rollNumber,
        onValueChange = vm::onRollNumberChange,
        label = "Roll Number / Admission ID",
        placeholder = "e.g. 10234",
        keyboardType = KeyboardType.Text,
        authStyle = true,
        modifier = Modifier.fillMaxWidth(),
    )
    if (state.linkedChild != null) {
        Spacer(Modifier.height(16.dp))
        Text("Linked: ${state.linkedChild!!.childName} — ${state.linkedChild!!.className}", style = VTypography.UpdateTitle.copy(color = VColors.Tertiary, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun SchoolResultCard(school: SchoolMatch, isSelected: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(if (isSelected) VColors.PrimaryContainer else VColors.SurfaceContainerLow)
            .pressScale(interaction, pressedScale = 0.97f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Lg).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.School, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(school.name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(2.dp))
            Text("${school.board} · ${school.city}", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        if (isSelected) {
            Icon(Icons.Filled.School, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(20.dp))
        }
    }
}
