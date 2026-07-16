package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.gamification.domain.model.LeaderboardEntry
import com.littlebridge.enrollplus.feature.gamification.domain.model.StudentBadge
import com.littlebridge.enrollplus.feature.gamification.presentation.TeacherGamificationViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

// ═══════════════════════════════════════════════════════════════════════════════
// TEACHER GAMIFICATION — STUDENT ACTIONS PANEL
// Embedded inside TeacherStudentProfileScreenV2 to provide per-student
// gamification actions: encourage, spotlight, badge award, shoutout, quest assign.
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TeacherStudentGamificationCard(
    studentId: String,
    studentName: String,
    gamificationViewModel: TeacherGamificationViewModel = koinViewModel(),
) {
    val state by gamificationViewModel.state.collectAsStateV2()
    LaunchedEffect(studentId) {
        gamificationViewModel.loadStudentBadges(studentId)
        gamificationViewModel.loadStudentStats(studentId)
    }

    val c = VtC
    var showShoutoutField by remember { mutableStateOf(false) }
    var shoutoutMsg by remember { mutableStateOf("") }
    var showQuestPicker by remember { mutableStateOf(false) }
    var showBadgePicker by remember { mutableStateOf(false) }
    var showParentAlert by remember { mutableStateOf(false) }
    var parentAlertMsg by remember { mutableStateOf("") }

    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VtEyebrow(appString(StringKeys.GAM_TOOLS), dot = c.accent)

            // Student gamification stats (XP, Level, Streak)
            state.studentStats?.let { stats ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    VtMetricTile("${stats.totalXp}", appString(StringKeys.GAM_TOTAL_XP), c.accent, Modifier.weight(1f))
                    VtMetricTile("${stats.currentLevel}", appString(StringKeys.GAM_LEVEL), VColors.gold, Modifier.weight(1f))
                    VtMetricTile("${stats.streakDays}", appString(StringKeys.GAM_STREAK), c.teal, Modifier.weight(1f))
                }
            }

            // Student badges row
            if (state.studentBadges.isNotEmpty()) {
                Text(appString(StringKeys.GAM_EARNED_BADGES), style = VTypography.label, color = c.ink3)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.studentBadges) { badge ->
                        BadgeChip(badge = badge)
                    }
                }
            }

            // Action buttons row — Encourage + Spotlight
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                VButton(
                    appString(StringKeys.GAM_ENCOURAGE),
                    onClick = { gamificationViewModel.encourageStudent(studentId) },
                    modifier = Modifier.weight(1f),
                    size = VButtonSize.Md,
                    tone = VButtonTone.Lavender,
                    loading = state.isActionLoading,
                )
                VButton(
                    appString(StringKeys.GAM_SPOTLIGHT),
                    onClick = { gamificationViewModel.spotlightStudent(studentId) },
                    modifier = Modifier.weight(1f),
                    size = VButtonSize.Md,
                    tone = VButtonTone.Sand,
                    loading = state.isActionLoading,
                )
            }

            // Shoutout toggle
            VButton(
                if (showShoutoutField) appString(StringKeys.GAM_CANCEL_SHOUTOUT) else appString(StringKeys.GAM_SEND_SHOUTOUT),
                onClick = {
                    if (showShoutoutField && shoutoutMsg.isNotBlank()) {
                        gamificationViewModel.sendShoutout(studentId, shoutoutMsg)
                        shoutoutMsg = ""
                    }
                    showShoutoutField = !showShoutoutField
                },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Teal,
                variant = if (showShoutoutField) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showShoutoutField) {
                OutlinedTextField(
                    value = shoutoutMsg,
                    onValueChange = { shoutoutMsg = it },
                    placeholder = { Text(appString(StringKeys.GAM_SHOUTOUT_PH), color = c.ink3) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    textStyle = VTypography.body.copy(color = VColors.ink),
                    shape = VShapes.md,
                )
            }

            // Action feedback message — placed right after action buttons so it's visible
            state.actionMessage?.let { msg ->
                val isSuccess = !msg.contains("Failed")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.md)
                        .background(if (isSuccess) VColors.mintSoft else VColors.errorSoft)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        msg,
                        style = VTypography.caption,
                        color = if (isSuccess) c.successInk else c.dangerInk,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(3000)
                    gamificationViewModel.clearActionMessage()
                }
            }

            // Quest assignment toggle
            VButton(
                if (showQuestPicker) appString(StringKeys.GAM_CANCEL_QUEST) else appString(StringKeys.GAM_ASSIGN_QUEST),
                onClick = {
                    showQuestPicker = !showQuestPicker
                    if (showQuestPicker) showBadgePicker = false
                },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Peach,
                variant = if (showQuestPicker) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showQuestPicker && state.availableQuests.isNotEmpty()) {
                state.availableQuests.forEach { quest ->
                    VButton(
                        appString(StringKeys.GAM_QUEST_BUTTON, "name" to quest.name, "xp" to quest.xpReward),
                        onClick = {
                            gamificationViewModel.assignQuest(studentId, quest.id)
                            showQuestPicker = false
                        },
                        full = true,
                        size = VButtonSize.Sm,
                        tone = VButtonTone.Mint,
                        variant = VButtonVariant.Secondary,
                        loading = state.isActionLoading,
                    )
                }
            }

            // Badge award toggle
            VButton(
                if (showBadgePicker) appString(StringKeys.GAM_CANCEL_BADGE) else appString(StringKeys.GAM_AWARD_BADGE),
                onClick = {
                    showBadgePicker = !showBadgePicker
                    if (showBadgePicker) showQuestPicker = false
                },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Sand,
                variant = if (showBadgePicker) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showBadgePicker && state.availableBadges.isNotEmpty()) {
                state.availableBadges.forEach { badge ->
                    VButton(
                        appString(StringKeys.GAM_BADGE_BUTTON, "name" to badge.name, "category" to badge.category),
                        onClick = {
                            gamificationViewModel.awardBadge(studentId, badge.id)
                            showBadgePicker = false
                        },
                        full = true,
                        size = VButtonSize.Sm,
                        tone = VButtonTone.Sand,
                        variant = VButtonVariant.Secondary,
                        loading = state.isActionLoading,
                    )
                }
            }

            // Parent alert toggle
            VButton(
                if (showParentAlert) appString(StringKeys.GAM_CANCEL_ALERT) else appString(StringKeys.GAM_PARENT_ALERT),
                onClick = { showParentAlert = !showParentAlert },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Rose,
                variant = if (showParentAlert) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showParentAlert) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = parentAlertMsg,
                        onValueChange = { parentAlertMsg = it },
                        placeholder = { Text(appString(StringKeys.GAM_PARENT_ALERT_PH), color = c.ink3) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    VButton(
                        appString(StringKeys.GAM_SEND_ALERT),
                        onClick = {
                            if (parentAlertMsg.isNotBlank()) {
                                gamificationViewModel.sendParentAlert(studentId, parentAlertMsg)
                                parentAlertMsg = ""
                                showParentAlert = false
                            }
                        },
                        full = true,
                        size = VButtonSize.Md,
                        tone = VButtonTone.Rose,
                        loading = state.isActionLoading,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TEACHER GAMIFICATION — CLASS-LEVEL PANEL
// Embedded inside TeacherClassesScreenV2 ClassDetailPane to provide
// class leaderboard, class goals, pep talk, and shoutout moderation.
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TeacherClassGamificationCard(
    className: String,
    section: String?,
    gamificationViewModel: TeacherGamificationViewModel = koinViewModel(),
) {
    val state by gamificationViewModel.state.collectAsStateV2()
    LaunchedEffect(className) { gamificationViewModel.load(className) }

    val c = VtC
    var showPepTalkConfirm by remember { mutableStateOf(false) }
    var showGoalCreator by remember { mutableStateOf(false) }
    var goalType by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }
    var goalReward by remember { mutableStateOf("") }
    var showMentorForm by remember { mutableStateOf(false) }
    var mentorId by remember { mutableStateOf("") }
    var menteeId by remember { mutableStateOf("") }
    var showBuddyForm by remember { mutableStateOf(false) }
    var buddy1Id by remember { mutableStateOf("") }
    var buddy2Id by remember { mutableStateOf("") }
    var goalError by remember { mutableStateOf("") }

    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VtEyebrow(appString(StringKeys.GAM_CLASS_GAMIFICATION), dot = c.accent)

            // Overview stats
            state.overview?.let { ov ->
                val totalXp = ov["totalXp"] as? Int ?: 0
                val totalBadges = ov["totalBadgesAwarded"] as? Int ?: 0
                val activeQuests = ov["activeQuests"] as? Int ?: 0
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    VtMetricTile("$totalXp", appString(StringKeys.GAM_TOTAL_XP), c.accent, Modifier.weight(1f))
                    VtMetricTile("$totalBadges", appString(StringKeys.GAM_BADGES), VColors.gold, Modifier.weight(1f))
                    VtMetricTile("$activeQuests", appString(StringKeys.GAM_QUESTS), c.teal, Modifier.weight(1f))
                }
            }

            // Class leaderboard (top 5)
            if (state.classLeaderboard.isNotEmpty()) {
                Text(appString(StringKeys.GAM_CLASS_LEADERBOARD), style = VTypography.label, color = c.ink3)
                state.classLeaderboard.take(5).forEachIndexed { idx, entry ->
                    LeaderboardRow(entry = entry, rank = idx + 1)
                }
            }
        }
    }

    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Class goals
            if (state.classGoals.isNotEmpty()) {
                Text(appString(StringKeys.GAM_CLASS_GOALS), style = VTypography.label, color = c.ink3)
                state.classGoals.forEach { goal ->
                    ClassGoalRow(
                        goal = goal,
                        onUpdateProgress = { goalId, progress ->
                            gamificationViewModel.updateClassGoalProgress(goalId, progress)
                        },
                    )
                }
            }

            // Pep Talk button
            VButton(
                if (showPepTalkConfirm) appString(StringKeys.GAM_CONFIRM_PEP_TALK) else appString(StringKeys.GAM_SEND_PEP_TALK),
                onClick = {
                    if (showPepTalkConfirm) {
                        gamificationViewModel.pepTalk(className, section)
                        showPepTalkConfirm = false
                    } else {
                        showPepTalkConfirm = true
                    }
                },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Lavender,
                variant = if (showPepTalkConfirm) VButtonVariant.Primary else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showPepTalkConfirm) {
                Text(
                    appString(StringKeys.GAM_PEP_TALK_CONFIRM, "className" to className, "section" to (section?.let { " · $it" } ?: "")),
                    style = VTypography.caption, color = c.ink2,
                )
            }

            // Create class goal
            VButton(
                if (showGoalCreator) appString(StringKeys.COMMON_BUTTON_CANCEL) else appString(StringKeys.GAM_CREATE_CLASS_GOAL),
                onClick = { showGoalCreator = !showGoalCreator },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Peach,
                variant = if (showGoalCreator) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showGoalCreator) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = goalType,
                        onValueChange = { goalType = it },
                        placeholder = { Text(appString(StringKeys.GAM_GOAL_TYPE_PH)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = goalTarget,
                        onValueChange = { goalTarget = it },
                        placeholder = { Text(appString(StringKeys.GAM_GOAL_TARGET_NUM_PH)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = goalReward,
                        onValueChange = { goalReward = it },
                        placeholder = { Text(appString(StringKeys.GAM_GOAL_REWARD_PH)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    if (goalError.isNotBlank()) {
                        Text(
                            goalError,
                            style = VTypography.caption,
                            color = VColors.error,
                        )
                    }
                    VButton(
                        appString(StringKeys.GAM_CREATE_GOAL),
                        onClick = {
                            val target = goalTarget.toIntOrNull() ?: 0
                            if (goalType.isBlank() || target <= 0) {
                                goalError = "Please enter a goal type and a valid target number"
                            } else {
                                goalError = ""
                                gamificationViewModel.createClassGoal(goalType, target, goalReward, className)
                                goalType = ""
                                goalTarget = ""
                                goalReward = ""
                                showGoalCreator = false
                            }
                        },
                        size = VButtonSize.Sm,
                        tone = VButtonTone.Lavender,
                    )
                }
            }
        }
    }

    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Shoutout moderation
            if (state.shoutouts.isNotEmpty()) {
                Text(appString(StringKeys.GAM_RECENT_SHOUTOUTS), style = VTypography.label, color = c.ink3)
                state.shoutouts.take(5).forEach { shoutout ->
                    ShoutoutRow(
                        shoutout = shoutout,
                        onDelete = { id -> gamificationViewModel.deleteShoutout(id) },
                    )
                }
            }
        }
    }

    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.mentorAssignments.isNotEmpty()) {
                Text(appString(StringKeys.GAM_MENTOR_ASSIGNMENTS), style = VTypography.label, color = c.ink3)
                state.mentorAssignments.take(5).forEach { assignment ->
                    val aId = assignment["id"]?.toString() ?: ""
                    val mName = assignment["mentorName"]?.toString()?.takeIf { it.isNotBlank() } ?: assignment["mentorId"]?.toString()?.take(8) ?: ""
                    val meName = assignment["menteeName"]?.toString()?.takeIf { it.isNotBlank() } ?: assignment["menteeId"]?.toString()?.take(8) ?: ""
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(VShapes.md)
                            .background(c.cream)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(appString(StringKeys.GAM_MENTOR_PREFIX, "id" to mName), style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.SemiBold)
                            Text(appString(StringKeys.GAM_MENTEE_PREFIX, "id" to meName), style = VTypography.caption, color = c.ink3)
                        }
                        Text(
                            appString(StringKeys.GAM_REMOVE),
                            style = VTypography.caption, color = VColors.error, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { gamificationViewModel.unassignMentor(aId) },
                        )
                    }
                }
            }

            // Mentor assignment form
            VButton(
                if (showMentorForm) appString(StringKeys.COMMON_BUTTON_CANCEL) else appString(StringKeys.GAM_ASSIGN_MENTOR),
                onClick = { showMentorForm = !showMentorForm },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Lavender,
                variant = if (showMentorForm) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showMentorForm) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mentorId,
                        onValueChange = { mentorId = it },
                        placeholder = { Text(appString(StringKeys.GAM_MENTOR_ID_PH)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = menteeId,
                        onValueChange = { menteeId = it },
                        placeholder = { Text(appString(StringKeys.GAM_MENTEE_ID_PH)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    VButton(
                        appString(StringKeys.GAM_ASSIGN),
                        onClick = {
                            if (mentorId.isNotBlank() && menteeId.isNotBlank()) {
                                gamificationViewModel.assignMentor(mentorId, menteeId)
                                mentorId = ""
                                menteeId = ""
                                showMentorForm = false
                            }
                        },
                        size = VButtonSize.Sm,
                        tone = VButtonTone.Lavender,
                    )
                }
            }

            // Study buddy pairs
            if (state.studyBuddyPairs.isNotEmpty()) {
                Text(appString(StringKeys.GAM_STUDY_BUDDY_PAIRS), style = VTypography.label, color = c.ink3)
                state.studyBuddyPairs.take(5).forEach { pair ->
                    val pId = pair["id"]?.toString() ?: ""
                    val s1Name = pair["student1Name"]?.toString()?.takeIf { it.isNotBlank() } ?: pair["student1Id"]?.toString()?.take(8) ?: ""
                    val s2Name = pair["student2Name"]?.toString()?.takeIf { it.isNotBlank() } ?: pair["student2Id"]?.toString()?.take(8) ?: ""
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(VShapes.md)
                            .background(c.cream)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("$s1Name & $s2Name", style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.SemiBold)
                            Text(appString(StringKeys.GAM_STUDY_BUDDIES), style = VTypography.caption, color = c.ink3)
                        }
                        Text(
                            appString(StringKeys.GAM_REMOVE),
                            style = VTypography.caption, color = VColors.error, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { gamificationViewModel.unassignStudyBuddy(pId) },
                        )
                    }
                }
            }

            // Study buddy form
            VButton(
                if (showBuddyForm) appString(StringKeys.COMMON_BUTTON_CANCEL) else appString(StringKeys.GAM_PAIR_STUDY_BUDDIES),
                onClick = { showBuddyForm = !showBuddyForm },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Teal,
                variant = if (showBuddyForm) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showBuddyForm) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = buddy1Id,
                        onValueChange = { buddy1Id = it },
                        placeholder = { Text(appString(StringKeys.GAM_STUDENT1_ID_PH)) },
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minWidth = 120.dp),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = buddy2Id,
                        onValueChange = { buddy2Id = it },
                        placeholder = { Text(appString(StringKeys.GAM_STUDENT2_ID_PH)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    VButton(
                        appString(StringKeys.GAM_PAIR_THEM),
                        onClick = {
                            if (buddy1Id.isNotBlank() && buddy2Id.isNotBlank()) {
                                gamificationViewModel.assignStudyBuddy(buddy1Id, buddy2Id)
                                buddy1Id = ""
                                buddy2Id = ""
                                showBuddyForm = false
                            }
                        },
                        size = VButtonSize.Sm,
                        tone = VButtonTone.Lavender,
                    )
                }
            }

            // Action feedback
            state.actionMessage?.let { msg ->
                val isSuccess = !msg.contains("Failed")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.md)
                        .background(if (isSuccess) VColors.mintSoft else VColors.errorSoft)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        msg,
                        style = VTypography.caption,
                        color = if (isSuccess) c.successInk else c.dangerInk,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(3000)
                    gamificationViewModel.clearActionMessage()
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun BadgeChip(badge: StudentBadge) {
    val c = VtC
    Row(
        modifier = Modifier
            .clip(VShapes.full)
            .background(VColors.goldSoft)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(VIcons.Star, contentDescription = null, tint = VColors.gold, modifier = Modifier.size(12.dp))
        Text(
            badge.badgeName,
            style = VTypography.caption,
            color = VColors.gold,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry, rank: Int) {
    val c = VtC
    val rankColor = when (rank) {
        1 -> VColors.gold
        2 -> c.ink3
        3 -> c.warmOrange
        else -> c.ink3
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(rankColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("$rank", style = VTypography.caption, color = rankColor, fontWeight = FontWeight.Bold)
        }
        Text(
            entry.studentName,
            style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            appString(StringKeys.GAM_XP_VALUE, "xp" to entry.totalXp),
            style = VTypography.caption,
            color = c.accent,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ClassGoalRow(
    goal: Map<String, *>,
    onUpdateProgress: (String, Int) -> Unit,
) {
    val c = VtC
    val goalId = goal["id"]?.toString() ?: ""
    val goalType = goal["goalType"]?.toString() ?: appString(StringKeys.GAM_GOAL)
    val target = goal["target"] as? Int ?: 0
    val current = goal["currentProgress"] as? Int ?: 0
    val reward = goal["reward"]?.toString() ?: ""
    val progress = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                goalType,
                style = VTypography.caption, color = c.navyDeep, fontWeight = FontWeight.SemiBold,
            )
            Text(
                appString(StringKeys.GAM_PROGRESS_FRACTION, "current" to current, "target" to target),
                style = VTypography.caption, color = c.accent, fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(VShapes.full)
                .background(VColors.lineSoft),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(5.dp)
                    .clip(VShapes.full)
                    .background(c.accent),
            )
        }
        if (reward.isNotBlank()) {
            Text(
                appString(StringKeys.GAM_REWARD_PREFIX, "reward" to reward),
                style = VTypography.caption, color = c.ink3,
            )
        }
    }
}

@Composable
private fun ShoutoutRow(
    shoutout: Map<String, *>,
    onDelete: (String) -> Unit,
) {
    val c = VtC
    val id = shoutout["id"]?.toString() ?: ""
    val senderName = shoutout["senderName"]?.toString() ?: appString(StringKeys.GAM_UNKNOWN)
    val receiverName = shoutout["receiverName"]?.toString() ?: appString(StringKeys.GAM_UNKNOWN)
    val message = shoutout["message"]?.toString() ?: ""
    val ix = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.md)
            .background(c.cream)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                appString(StringKeys.GAM_SHOUTOUT_FROM_TO, "sender" to senderName, "receiver" to receiverName),
                style = VTypography.caption, color = c.ink2, fontWeight = FontWeight.Bold,
            )
            Text(
                message,
                style = VTypography.caption, color = c.ink3,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            VIcons.Close,
            contentDescription = appString(StringKeys.GAM_DELETE_SHOUTOUT),
            tint = c.danger,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .clickable(interactionSource = ix, indication = null) { onDelete(id) },
        )
    }
}
