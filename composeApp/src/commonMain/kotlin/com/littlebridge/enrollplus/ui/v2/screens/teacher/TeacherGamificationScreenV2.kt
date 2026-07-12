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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.gamification.domain.model.LeaderboardEntry
import com.littlebridge.enrollplus.feature.gamification.domain.model.StudentBadge
import com.littlebridge.enrollplus.feature.gamification.presentation.TeacherGamificationViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
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
                Text("Earned Badges", style = VtT.label.coloredV(c.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.studentBadges) { badge ->
                        BadgeChip(badge = badge)
                    }
                }
            }

            // Action buttons row — Encourage + Spotlight
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                GamificationActionButton(
                    label = "Encourage",
                    icon = VIcons.Heart,
                    tint = c.accent,
                    modifier = Modifier.weight(1f),
                    isLoading = state.isActionLoading,
                    onClick = { gamificationViewModel.encourageStudent(studentId) },
                )
                GamificationActionButton(
                    label = "Spotlight",
                    icon = VIcons.Star,
                    tint = VColors.gold,
                    modifier = Modifier.weight(1f),
                    isLoading = state.isActionLoading,
                    onClick = { gamificationViewModel.spotlightStudent(studentId) },
                )
            }

            // Shoutout toggle
            GamificationActionButton(
                label = if (showShoutoutField) "Cancel Shoutout" else "Send Shoutout",
                icon = VIcons.Megaphone,
                tint = c.teal,
                isLoading = state.isActionLoading,
                onClick = {
                    if (showShoutoutField && shoutoutMsg.isNotBlank()) {
                        gamificationViewModel.sendShoutout(studentId, shoutoutMsg)
                        shoutoutMsg = ""
                    }
                    showShoutoutField = !showShoutoutField
                },
            )

            if (showShoutoutField) {
                OutlinedTextField(
                    value = shoutoutMsg,
                    onValueChange = { shoutoutMsg = it },
                    placeholder = { Text("Type a shoutout message...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    textStyle = VTypography.body.copy(color = VColors.ink),
                    shape = VShapes.md,
                )
            }

            // Quest assignment toggle
            GamificationActionButton(
                label = if (showQuestPicker) "Cancel Quest" else "Assign Quest",
                icon = VIcons.Target,
                tint = c.warmOrange,
                isLoading = state.isActionLoading,
                onClick = { showQuestPicker = !showQuestPicker },
            )

            if (showQuestPicker && state.availableQuests.isNotEmpty()) {
                state.availableQuests.forEach { quest ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(VShapes.md)
                            .background(c.cream)
                            .clickable {
                                gamificationViewModel.assignQuest(studentId, quest.id)
                                showQuestPicker = false
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(quest.name, style = VtT.body.coloredV(c.navyDeep).copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp))
                            Text(quest.description, style = VtT.caption.coloredV(c.ink3).copy(fontSize = 11.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text("+${quest.xpReward} XP", style = VtT.caption.coloredV(c.tealDeep).copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                    }
                }
            }

            // Badge award toggle
            GamificationActionButton(
                label = if (showBadgePicker) "Cancel Badge" else "Award Badge",
                icon = VIcons.Star,
                tint = VColors.gold,
                isLoading = state.isActionLoading,
                onClick = { showBadgePicker = !showBadgePicker },
            )

            if (showBadgePicker && state.availableBadges.isNotEmpty()) {
                state.availableBadges.forEach { badge ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(VShapes.md)
                            .background(c.cream)
                            .clickable {
                                gamificationViewModel.awardBadge(studentId, badge.id)
                                showBadgePicker = false
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(badge.name, style = VtT.body.coloredV(c.navyDeep).copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp))
                            Text("${badge.category} · ${badge.rarity}", style = VtT.caption.coloredV(c.ink3).copy(fontSize = 11.sp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text("+${badge.xpRequirement} XP", style = VtT.caption.coloredV(VColors.gold).copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                    }
                }
            }

            // Parent alert toggle
            GamificationActionButton(
                label = if (showParentAlert) "Cancel Alert" else "Parent Alert",
                icon = VIcons.Megaphone,
                tint = c.warmOrange,
                isLoading = state.isActionLoading,
                onClick = { showParentAlert = !showParentAlert },
            )

            if (showParentAlert) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = parentAlertMsg,
                        onValueChange = { parentAlertMsg = it },
                        placeholder = { Text("Type a positive message to the parent...") },
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
                        size = VButtonSize.Sm,
                        tone = VButtonTone.Lavender,
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
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        msg,
                        style = VtT.caption.coloredV(if (isSuccess) c.successInk else c.dangerInk).copy(fontWeight = FontWeight.Medium, fontSize = 12.sp),
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
                Text("Class Leaderboard", style = VtT.label.coloredV(c.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                state.classLeaderboard.take(5).forEachIndexed { idx, entry ->
                    LeaderboardRow(entry = entry, rank = idx + 1)
                }
            }

            // Class goals
            if (state.classGoals.isNotEmpty()) {
                Text("Class Goals", style = VtT.label.coloredV(c.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
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
            GamificationActionButton(
                label = if (showPepTalkConfirm) "Confirm Pep Talk" else "Send Pep Talk",
                icon = VIcons.Megaphone,
                tint = c.accent,
                isLoading = state.isActionLoading,
                onClick = {
                    if (showPepTalkConfirm) {
                        gamificationViewModel.pepTalk(className, section)
                        showPepTalkConfirm = false
                    } else {
                        showPepTalkConfirm = true
                    }
                },
            )

            if (showPepTalkConfirm) {
                Text(
                    "Send a motivational pep talk to $className${section?.let { " · $it" } ?: ""}?",
                    style = VtT.caption.coloredV(c.ink2).copy(fontSize = 12.sp),
                )
            }

            // Create class goal
            GamificationActionButton(
                label = if (showGoalCreator) "Cancel" else "Create Class Goal",
                icon = VIcons.Target,
                tint = c.warmOrange,
                isLoading = state.isActionLoading,
                onClick = { showGoalCreator = !showGoalCreator },
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
                Text("Recent Shoutouts", style = VtT.label.coloredV(c.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                state.shoutouts.take(5).forEach { shoutout ->
                    ShoutoutRow(
                        shoutout = shoutout,
                        onDelete = { id -> gamificationViewModel.deleteShoutout(id) },
                    )
                }
            }

            // Mentor assignments
            if (state.mentorAssignments.isNotEmpty()) {
                Text("Mentor Assignments", style = VtT.label.coloredV(c.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
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
                            Text("Mentor: ${mId.take(8)}...", style = VtT.body.coloredV(c.navyDeep).copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp))
                            Text("Mentee: ${meId.take(8)}...", style = VtT.caption.coloredV(c.ink3).copy(fontSize = 11.sp))
                        }
                        Text(
                            "Remove",
                            style = VtT.caption.coloredV(VColors.error).copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                            modifier = Modifier.clickable { gamificationViewModel.unassignMentor(aId) },
                        )
                    }
                }
            }

            // Mentor assignment form
            GamificationActionButton(
                label = if (showMentorForm) "Cancel" else "Assign Mentor",
                icon = VIcons.Star,
                tint = c.accent,
                isLoading = state.isActionLoading,
                onClick = { showMentorForm = !showMentorForm },
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
                Text("Study Buddy Pairs", style = VtT.label.coloredV(c.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
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
                            Text("${s1.take(8)}... & ${s2.take(8)}...", style = VtT.body.coloredV(c.navyDeep).copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp))
                            Text("Study buddies", style = VtT.caption.coloredV(c.ink3).copy(fontSize = 11.sp))
                        }
                        Text(
                            "Remove",
                            style = VtT.caption.coloredV(VColors.error).copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                            modifier = Modifier.clickable { gamificationViewModel.unassignStudyBuddy(pId) },
                        )
                    }
                }
            }

            // Study buddy form
            GamificationActionButton(
                label = if (showBuddyForm) "Cancel" else "Pair Study Buddies",
                icon = VIcons.Heart,
                tint = c.teal,
                isLoading = state.isActionLoading,
                onClick = { showBuddyForm = !showBuddyForm },
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
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        msg,
                        style = VtT.caption.coloredV(if (isSuccess) c.successInk else c.dangerInk).copy(fontWeight = FontWeight.Medium, fontSize = 12.sp),
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
private fun GamificationActionButton(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val ix = remember { MutableInteractionSource() }
    Row(
        modifier
            .clip(VShapes.md)
            .background(tint.copy(alpha = 0.10f))
            .clickable(interactionSource = ix, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = tint)
        } else {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Text(
            label,
            style = VtT.body.coloredV(tint).copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
        )
    }
}

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
            style = VtT.caption.coloredV(VColors.gold).copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
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
            Text("$rank", style = VtT.bodyStrong.coloredV(rankColor).copy(fontSize = 13.sp))
        }
        Text(
            "Student #${entry.studentId.takeLast(6)}",
            style = VtT.body.coloredV(c.navyDeep).copy(fontWeight = FontWeight.Medium, fontSize = 13.sp),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            "${entry.totalXp} XP",
            style = VtT.bodyStrong.coloredV(c.accent).copy(fontSize = 13.sp),
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
                style = VtT.body.coloredV(c.navyDeep).copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            )
            Text(
                "$current/$target",
                style = VtT.caption.coloredV(c.accent).copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
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
                style = VtT.caption.coloredV(c.ink3).copy(fontSize = 11.sp),
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
                style = VtT.caption.coloredV(c.ink2).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
            )
            Text(
                message,
                style = VtT.caption.coloredV(c.ink3).copy(fontSize = 11.sp),
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
