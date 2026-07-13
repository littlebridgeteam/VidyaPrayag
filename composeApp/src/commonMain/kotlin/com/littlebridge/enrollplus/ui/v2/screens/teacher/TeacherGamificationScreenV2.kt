package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
            VtEyebrow("Gamification Tools", dot = c.accent)

            // Student badges row
            if (state.studentBadges.isNotEmpty()) {
                Text("Earned Badges", style = VTypography.label, color = c.ink3)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.studentBadges) { badge ->
                        BadgeChip(badge = badge)
                    }
                }
            }

            // Action buttons row — Encourage + Spotlight
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                VButton(
                    "Encourage",
                    onClick = { gamificationViewModel.encourageStudent(studentId) },
                    modifier = Modifier.weight(1f),
                    size = VButtonSize.Md,
                    tone = VButtonTone.Lavender,
                    loading = state.isActionLoading,
                )
                VButton(
                    "Spotlight",
                    onClick = { gamificationViewModel.spotlightStudent(studentId) },
                    modifier = Modifier.weight(1f),
                    size = VButtonSize.Md,
                    tone = VButtonTone.Sand,
                    loading = state.isActionLoading,
                )
            }

            // Shoutout toggle
            VButton(
                if (showShoutoutField) "Cancel Shoutout" else "Send Shoutout",
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
                    placeholder = { Text("Type a shoutout message...", color = c.ink3) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    textStyle = VTypography.body.copy(color = VColors.ink),
                    shape = VShapes.md,
                )
            }

            // Quest assignment toggle
            VButton(
                if (showQuestPicker) "Cancel Quest" else "Assign Quest",
                onClick = { showQuestPicker = !showQuestPicker },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Peach,
                variant = if (showQuestPicker) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showQuestPicker && state.availableQuests.isNotEmpty()) {
                state.availableQuests.forEach { quest ->
                    VButton(
                        "${quest.name} · +${quest.xpReward} XP",
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
                if (showBadgePicker) "Cancel Badge" else "Award Badge",
                onClick = { showBadgePicker = !showBadgePicker },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Sand,
                variant = if (showBadgePicker) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showBadgePicker && state.availableBadges.isNotEmpty()) {
                state.availableBadges.forEach { badge ->
                    VButton(
                        "${badge.name} · ${badge.category}",
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
                if (showParentAlert) "Cancel Alert" else "Parent Alert",
                onClick = { showParentAlert = !showParentAlert },
                full = true,
                size = VButtonSize.Md,
                tone = VButtonTone.Rose,
                variant = if (showParentAlert) VButtonVariant.Ghost else VButtonVariant.Secondary,
                loading = state.isActionLoading,
            )

            if (showParentAlert) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = parentAlertMsg,
                        onValueChange = { parentAlertMsg = it },
                        placeholder = { Text("Type a positive message to the parent...", color = c.ink3) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    VButton(
                        "Send Alert",
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

            // Action feedback message
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
    LaunchedEffect(className) { gamificationViewModel.load() }

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

    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VtEyebrow("Class Gamification", dot = c.accent)

            // Overview stats
            state.overview?.let { ov ->
                val totalXp = ov["totalXp"] as? Int ?: 0
                val totalBadges = ov["totalBadgesAwarded"] as? Int ?: 0
                val activeQuests = ov["activeQuests"] as? Int ?: 0
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    VtMetricTile("$totalXp", "Total XP", c.accent, Modifier.weight(1f))
                    VtMetricTile("$totalBadges", "Badges", VColors.gold, Modifier.weight(1f))
                    VtMetricTile("$activeQuests", "Quests", c.teal, Modifier.weight(1f))
                }
            }

            // Class leaderboard (top 5)
            if (state.classLeaderboard.isNotEmpty()) {
                Text("Class Leaderboard", style = VTypography.label, color = c.ink3)
                state.classLeaderboard.take(5).forEachIndexed { idx, entry ->
                    LeaderboardRow(entry = entry, rank = idx + 1)
                }
            }

            // Class goals
            if (state.classGoals.isNotEmpty()) {
                Text("Class Goals", style = VTypography.label, color = c.ink3)
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
                if (showPepTalkConfirm) "Confirm Pep Talk" else "Send Pep Talk",
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
                    "Send a motivational pep talk to $className${section?.let { " · $it" } ?: ""}?",
                    style = VTypography.caption, color = c.ink2,
                )
            }

            // Create class goal
            VButton(
                if (showGoalCreator) "Cancel" else "Create Class Goal",
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
                        placeholder = { Text("Goal type (e.g. attendance)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = goalTarget,
                        onValueChange = { goalTarget = it },
                        placeholder = { Text("Target (e.g. 90)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = goalReward,
                        onValueChange = { goalReward = it },
                        placeholder = { Text("Reward (e.g. 500 XP)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    VButton(
                        "Create Goal",
                        onClick = {
                            val target = goalTarget.toIntOrNull() ?: 0
                            if (goalType.isNotBlank() && target > 0) {
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

            // Shoutout moderation
            if (state.shoutouts.isNotEmpty()) {
                Text("Recent Shoutouts", style = VTypography.label, color = c.ink3)
                state.shoutouts.take(5).forEach { shoutout ->
                    ShoutoutRow(
                        shoutout = shoutout,
                        onDelete = { id -> gamificationViewModel.deleteShoutout(id) },
                    )
                }
            }

            // Mentor assignments
            if (state.mentorAssignments.isNotEmpty()) {
                Text("Mentor Assignments", style = VTypography.label, color = c.ink3)
                state.mentorAssignments.take(5).forEach { assignment ->
                    val aId = assignment["id"]?.toString() ?: ""
                    val mId = assignment["mentorId"]?.toString() ?: ""
                    val meId = assignment["menteeId"]?.toString() ?: ""
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
                            Text("Mentor: ${mId.take(8)}...", style = VTypography.bodySmall, color = c.navyDeep, fontWeight = FontWeight.SemiBold)
                            Text("Mentee: ${meId.take(8)}...", style = VTypography.caption, color = c.ink3)
                        }
                        Text(
                            "Remove",
                            style = VTypography.caption, color = VColors.error, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { gamificationViewModel.unassignMentor(aId) },
                        )
                    }
                }
            }

            // Mentor assignment form
            VButton(
                if (showMentorForm) "Cancel" else "Assign Mentor",
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
                        placeholder = { Text("Mentor student ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = menteeId,
                        onValueChange = { menteeId = it },
                        placeholder = { Text("Mentee student ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    VButton(
                        "Assign",
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
                Text("Study Buddy Pairs", style = VTypography.label, color = c.ink3)
                state.studyBuddyPairs.take(5).forEach { pair ->
                    val pId = pair["id"]?.toString() ?: ""
                    val s1 = pair["student1Id"]?.toString() ?: ""
                    val s2 = pair["student2Id"]?.toString() ?: ""
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
                            Text("${s1.take(8)}... & ${s2.take(8)}...", style = VTypography.bodySmall, color = c.navyDeep, fontWeight = FontWeight.SemiBold)
                            Text("Study buddies", style = VTypography.caption, color = c.ink3)
                        }
                        Text(
                            "Remove",
                            style = VTypography.caption, color = VColors.error, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { gamificationViewModel.unassignStudyBuddy(pId) },
                        )
                    }
                }
            }

            // Study buddy form
            VButton(
                if (showBuddyForm) "Cancel" else "Pair Study Buddies",
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
                        placeholder = { Text("Student 1 ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    OutlinedTextField(
                        value = buddy2Id,
                        onValueChange = { buddy2Id = it },
                        placeholder = { Text("Student 2 ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = VTypography.body.copy(color = VColors.ink),
                        shape = VShapes.md,
                    )
                    VButton(
                        "Pair Them",
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
            Text("$rank", style = VTypography.bodySmall, color = rankColor, fontWeight = FontWeight.Bold)
        }
        Text(
            "Student #${entry.studentId.takeLast(6)}",
            style = VTypography.bodySmall, color = c.navyDeep, fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            "${entry.totalXp} XP",
            style = VTypography.bodySmall,
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
    val goalType = goal["goalType"]?.toString() ?: "Goal"
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
                style = VTypography.bodySmall, color = c.navyDeep, fontWeight = FontWeight.SemiBold,
            )
            Text(
                "$current/$target",
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
                "Reward: $reward",
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
    val senderName = shoutout["senderName"]?.toString() ?: "Unknown"
    val receiverName = shoutout["receiverName"]?.toString() ?: "Unknown"
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
                "$senderName → $receiverName",
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
            contentDescription = "Delete shoutout",
            tint = c.danger,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .clickable(interactionSource = ix, indication = null) { onDelete(id) },
        )
    }
}
