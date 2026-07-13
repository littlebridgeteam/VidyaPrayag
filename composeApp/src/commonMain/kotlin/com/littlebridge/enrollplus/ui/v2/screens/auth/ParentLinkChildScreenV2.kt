package com.littlebridge.enrollplus.ui.v2.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.LinkChildState
import com.littlebridge.enrollplus.feature.parent.presentation.LinkChildViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.SchoolMatch
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.components.VProgressBarSegments
import com.littlebridge.enrollplus.ui.v2.components.VTag
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.colored
import org.koin.compose.viewmodel.koinViewModel

/**
 * ParentLinkChildScreenV2 — pixel-faithful Compose copy of `Auth.tsx → ParentLinkChild`.
 *
 * The 3-step "link your child" wizard the Figma prototype renders:
 *   1. **Tell us about you** — full name + preferred language tags.
 *   2. **Find your child's school** — search field + a real "Match" school result card.
 *   3. **Link your child** — roll/admission field + the resolved-child preview card.
 *
 * **Wired to the real [LinkChildViewModel]** (`shared/`) →
 * `GET /api/v1/parent/schools/search` + `POST /api/v1/parent/link-child`. MockV2 is no longer
 * referenced (report §5.3, SWEEP-A). The bottom CTA searches in step 2, links in step 3, and only
 * calls [onDone] once the backend confirms the link.
 */
@Composable
fun ParentLinkChildScreenV2(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: LinkChildViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentLinkChildContent(
        state = state,
        onDone = onDone,
        onBack = onBack,
        onFullNameChange = viewModel::onFullNameChange,
        onLanguageChange = viewModel::onLanguageChange,
        onSchoolQueryChange = viewModel::onSchoolQueryChange,
        // ROOT FIX: let the parent explicitly tap their child's school so they're
        // never silently locked onto the alphabetically-first search result.
        onSelectSchool = viewModel::selectSchool,
        onRollNumberChange = viewModel::onRollNumberChange,
        // ISSUE 2c: guided step-3 inputs.
        onChildNameChange = viewModel::onChildNameChange,
        onClassNameChange = viewModel::onClassNameChange,
        onSectionChange = viewModel::onSectionChange,
        onParentPhoneChange = viewModel::onParentPhoneChange,
        onSearch = viewModel::searchSchools,
        onLink = viewModel::linkChild,
        modifier = modifier.statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    )
}

/** Stateless body — also used by the @Preview (no MockV2 in the live path). */
@Composable
private fun ParentLinkChildContent(
    state: LinkChildState,
    onDone: () -> Unit,
    onBack: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onSchoolQueryChange: (String) -> Unit,
    onSelectSchool: (SchoolMatch) -> Unit,
    onRollNumberChange: (String) -> Unit,
    // ISSUE 2c: guided step-3 inputs.
    onChildNameChange: (String) -> Unit,
    onClassNameChange: (String) -> Unit,
    onSectionChange: (String) -> Unit,
    onParentPhoneChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLink: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors
    val d = VTheme.dimens
    val total = 3
    val theSchoolStr = appString(StringKeys.LINK_THE_SCHOOL)
    val yourSchoolStr = appString(StringKeys.LINK_YOUR_SCHOOL)

    var step by remember { mutableIntStateOf(1) }
    val fullName = state.fullName
    val language = state.language
    val schoolQuery = state.schoolQuery
    val rollNo = state.rollNumber

    Column(
        modifier
            .fillMaxSize()
            .background(VTheme.colors.cream)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(
            onBack = {
                if (step > 1 && !state.isSearching && !state.isLinking) step-- else onBack()
            },
        )

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            VLabel(appString(StringKeys.LINK_STEP_OF).replace("{step}", step.toString()).replace("{total}", total.toString()))
            Spacer(Modifier.height(8.dp))
            VProgressBarSegments(total = total, current = step)
            Spacer(Modifier.height(16.dp))

            val titles = listOf(
                appString(StringKeys.LINK_STEP1_TITLE),
                appString(StringKeys.LINK_STEP2_TITLE),
                appString(StringKeys.LINK_STEP3_TITLE),
            )
            val subtitles = listOf(
                appString(StringKeys.LINK_STEP1_SUB),
                appString(StringKeys.LINK_STEP2_SUB),
                appString(StringKeys.LINK_STEP3_SUB).replace("{school}", state.selectedSchool?.name ?: theSchoolStr),
            )
            Text(titles[step - 1], style = VTheme.type.h2.colored(c.ink))
            Text(subtitles[step - 1], style = VTheme.type.caption.colored(c.ink2))
            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val forward = targetState > initialState
                    val dur = 280
                    val enter = slideInHorizontally(
                        animationSpec = tween(dur),
                        initialOffsetX = { if (forward) it / 4 else -it / 4 },
                    ) + fadeIn(tween(dur))
                    val exit = slideOutHorizontally(
                        animationSpec = tween(dur),
                        targetOffsetX = { if (forward) -it / 4 else it / 4 },
                    ) + fadeOut(tween(dur))
                    enter togetherWith exit
                },
                label = "linkStep",
                modifier = Modifier.fillMaxWidth(),
            ) { current ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    when (current) {
                        1 -> {
                            Spacer(Modifier.height(d.md))
                            VInput(
                                value = fullName,
                                onValueChange = onFullNameChange,
                                label = appString(StringKeys.LINK_FULL_NAME),
                                placeholder = appString(StringKeys.LINK_FULL_NAME_PH),
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(d.md))
                            VLabel(appString(StringKeys.LINK_PREF_LANG))
                            Spacer(Modifier.height(d.sm))
                            Row(horizontalArrangement = Arrangement.spacedBy(d.sm)) {
                                VTag(text = "English", active = language == "English", onClick = { onLanguageChange("English") })
                                VTag(text = "हिन्दी", active = language == "हिन्दी", onClick = { onLanguageChange("हिन्दी") })
                            }
                        }

                        2 -> {
                            Spacer(Modifier.height(d.md))
                            VInput(
                                value = schoolQuery,
                                onValueChange = onSchoolQueryChange,
                                placeholder = appString(StringKeys.LINK_SEARCH_PH),
                                leadingIcon = VIcons.Search,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(d.sm))
                            // §5: search action — runs the real GET /schools/search.
                            VButton(
                                text = if (state.isSearching) appString(StringKeys.LINK_SEARCHING) else appString(StringKeys.LINK_SEARCH),
                                onClick = onSearch,
                                full = true,
                                size = VButtonSize.Md,
                                tone = VButtonTone.Navy,
                                soft = true,
                                enabled = !state.isSearching && schoolQuery.isNotBlank(),
                            )
                            Spacer(Modifier.height(d.sm))
                            when {
                                state.searchError != null -> {
                                    Text(
                                        state.searchError ?: appString(StringKeys.LINK_SEARCH_ERR),
                                        style = VTheme.type.caption.colored(c.dangerInk),
                                    )
                                }
                                state.matches.isEmpty() -> {
                                    Text(
                                        appString(StringKeys.LINK_SEARCH_PROMPT),
                                        style = VTheme.type.caption.colored(c.ink2),
                                    )
                                }
                                else -> {
                                    // ROOT FIX: when several schools match, the parent MUST pick
                                    // their child's school — tapping a card selects it. Auto-select
                                    // only happens for a single result (see LinkChildViewModel).
                                    if (state.matches.size > 1) {
                                        Text(
                                            appString(StringKeys.LINK_TAP_SELECT),
                                            style = VTheme.type.caption.colored(c.ink2),
                                        )
                                        Spacer(Modifier.height(d.sm))
                                    }
                                    state.matches.forEach { match ->
                                        val selected = state.selectedSchool?.id == match.id
                                        VCard(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = { onSelectSchool(match) },
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                // §5: React match-icon circle = solid var(--arctic)=teal, dark glyph (Auth.tsx L294).
                                                Box(
                                                    Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(c.teal),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Icon(VIcons.GraduationCap, contentDescription = "Student", tint = c.ink, modifier = Modifier.size(18.dp))
                                                }
                                                Spacer(Modifier.width(d.md))
                                                Column(Modifier.weight(1f)) {
                                                    Text(match.name, style = VTheme.type.bodyStrong.colored(c.ink))
                                                    Text("${match.city} • ${match.board}", style = VTheme.type.caption.colored(c.ink2))
                                                }
                                                if (selected) {
                                                    VBadge(text = appString(StringKeys.LINK_MATCH), tone = VBadgeTone.Arctic)
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(d.sm))
                                    }
                                }
                            }
                        }

                        else -> {
                            Spacer(Modifier.height(d.md))
                            // ISSUE 2c: guided, real-time-formatted inputs (school already chosen).
                            // 1) Child's name.
                            VInput(
                                value = state.childName,
                                onValueChange = onChildNameChange,
                                label = appString(StringKeys.LINK_CHILD_NAME),
                                placeholder = appString(StringKeys.LINK_CHILD_NAME_PH),
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(d.md))
                            // 2) Class + Section on one row (the VM auto-peels a trailing
                            //    section letter typed into the class field into Section).
                            Row(horizontalArrangement = Arrangement.spacedBy(d.md)) {
                                VInput(
                                    value = state.className,
                                    onValueChange = onClassNameChange,
                                    label = appString(StringKeys.LINK_CLASS),
                                    placeholder = appString(StringKeys.LINK_CLASS_PH),
                                    modifier = Modifier.weight(2f),
                                )
                                VInput(
                                    value = state.section,
                                    onValueChange = onSectionChange,
                                    label = appString(StringKeys.LINK_SECTION),
                                    placeholder = appString(StringKeys.LINK_SECTION_PH),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Spacer(Modifier.height(d.md))
                            // 3) Roll / admission number.
                            VInput(
                                value = rollNo,
                                onValueChange = onRollNumberChange,
                                label = appString(StringKeys.LINK_ROLL),
                                placeholder = appString(StringKeys.LINK_ROLL_PH),
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(d.md))
                            // 4) Parent phone — used to verify against the student's record.
                            //    Optional: if your school doesn't have your number on record,
                            //    you can leave this blank. A school admin will still review the request.
                            VInput(
                                value = state.parentPhone,
                                onValueChange = onParentPhoneChange,
                                label = appString(StringKeys.LINK_PHONE_OPT),
                                placeholder = appString(StringKeys.LINK_PHONE_PH),
                                keyboardType = KeyboardType.Phone,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(d.md))
                            val linked = state.linkedChild
                            when {
                                state.linkError != null -> {
                                    Text(
                                        state.linkError ?: appString(StringKeys.LINK_ERR),
                                        style = VTheme.type.caption.colored(c.dangerInk),
                                    )
                                }
                                // RA-48: a submitted request that the school admin must approve.
                                // We DON'T route into the dashboard; we confirm it's awaiting review.
                                state.linkPending && linked != null -> {
                                    VCard(modifier = Modifier.fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            VAvatar(name = linked.childName, src = linked.profilePhotoUrl, size = 48.dp)
                                            Spacer(Modifier.width(d.md))
                                            Column(Modifier.weight(1f)) {
                                                Text(linked.childName, style = VTheme.type.bodyStrong.colored(c.ink))
                                                // ISSUE 2d: a phone mismatch lands in the school's
                                                // "needs review" queue — say so explicitly so the
                                                // parent knows it may take an extra check.
                                                // Prefer the school the SERVER matched the child to
                                                // (linked.schoolName). The matcher can self-heal a
                                                // wrong/duplicate school pick by binding to the
                                                // student's REAL school, so this name is authoritative
                                                // over the one the parent tapped in step 2.
                                                val matchedSchool = linked.schoolName.takeIf { it.isNotBlank() }
                                                    ?: state.selectedSchool?.name ?: theSchoolStr
                                                val msg = if (state.linkNeedsReview) {
                                                    appString(StringKeys.LINK_REVIEW_MSG).replace("{school}", matchedSchool)
                                                } else {
                                                    appString(StringKeys.LINK_PENDING_MSG).replace("{school}", matchedSchool)
                                                }
                                                Text(msg, style = VTheme.type.caption.colored(c.ink2))
                                            }
                                            Icon(VIcons.Clock, contentDescription = "Pending", tint = c.warningInk, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                                linked != null -> {
                                    // §5: resolved-child preview — only shown once the backend confirms the link.
                                    VCard(modifier = Modifier.fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            VAvatar(name = linked.childName, src = linked.profilePhotoUrl, size = 48.dp)
                                            Spacer(Modifier.width(d.md))
                                            Column(Modifier.weight(1f)) {
                                                Text(linked.childName, style = VTheme.type.bodyStrong.colored(c.ink))
                                                Text(
                                                    appString(StringKeys.LINK_CLASS_ROLL).replace("{class}", linked.className).replace("{roll}", linked.roll),
                                                    style = VTheme.type.caption.colored(c.ink2),
                                                )
                                            }
                                            // §5: React resolved-child check = #155e3a (Auth.tsx L319).
                                            Icon(VIcons.Check, contentDescription = "Approved", tint = c.successInk, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                                else -> {
                                    Text(
                                        appString(StringKeys.LINK_MATCH_PROMPT).replace("{school}", state.selectedSchool?.name ?: yourSchoolStr),
                                        style = VTheme.type.caption.colored(c.ink2),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // §5: React has a SINGLE CTA (Continue / Finish) with a trailing ArrowRight; no Back button.
        // Step 2 requires a selected school before advancing; step 3 links via the backend and only
        // calls onDone() once the link is confirmed (handled in the VM's onSuccess callback).
        val ctaText = when {
            step < total -> appString(StringKeys.LINK_CONTINUE)
            state.isLinking -> appString(StringKeys.LINK_LINKING)
            // RA-48: once a request is pending approval the only forward action is
            // to leave the wizard; a fresh roll cannot be re-submitted from here.
            state.linkPending -> appString(StringKeys.LINK_DONE)
            else -> appString(StringKeys.LINK_FINISH)
        }
        val ctaEnabled = when {
            step == 2 -> state.selectedSchool != null
            step < total -> true
            state.linkPending -> true
            // ISSUE 2c: every guided field (name + class + roll + valid phone) is required.
            else -> !state.isLinking && state.step3Valid
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(d.xl))
            VButton(
                text = ctaText,
                onClick = {
                    when {
                        step < total -> step++
                        // RA-48: a pending request returns the parent to wherever onDone
                        // routes (typically the parent home), where they'll see no child
                        // yet and the "awaiting approval" empty state.
                        state.linkPending -> onDone()
                        else -> onLink(onDone)
                    }
                },
                full = true,
                size = VButtonSize.Lg,
                tone = VButtonTone.Teal,
                soft = false,
                enabled = ctaEnabled,
                trailing = { Icon(VIcons.ArrowRight, contentDescription = "Continue", modifier = Modifier.size(16.dp)) },
            )
            Spacer(Modifier.height(d.xl))
        }
    }
}

