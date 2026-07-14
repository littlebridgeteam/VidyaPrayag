package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.gamification.domain.model.BadgeDefinition
import com.littlebridge.enrollplus.feature.gamification.domain.model.House
import com.littlebridge.enrollplus.feature.gamification.domain.model.LeaderboardEntry
import com.littlebridge.enrollplus.feature.gamification.domain.model.LevelDefinition
import com.littlebridge.enrollplus.feature.gamification.domain.model.QuestDefinition
import com.littlebridge.enrollplus.feature.gamification.domain.model.Reward
import com.littlebridge.enrollplus.feature.gamification.domain.model.SeasonalEvent
import com.littlebridge.enrollplus.feature.gamification.presentation.AdminGamificationState
import com.littlebridge.enrollplus.feature.gamification.presentation.AdminGamificationViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminGamificationScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminGamificationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) { viewModel.load() }

    LaunchedEffect(state.actionMessage) {
        if (state.actionMessage != null) {
            delay(3000)
            viewModel.clearActionMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        AdminGamificationHeader(onBack = onBack)

        val actionMsg = state.actionMessage
        if (actionMsg != null) {
            ActionMessageBanner(
                message = actionMsg,
                isError = actionMsg.startsWith("Failed"),
            )
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
            }
        } else if (state.error != null && state.flags == null) {
            val errorMsg = state.error ?: ""
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(VIcons.AlertTriangle, contentDescription = null, tint = VColors.error, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(12.dp))
                Text(errorMsg, style = VTypography.body, color = VColors.ink2)
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clip(VShapes.md)
                        .background(VColors.violet)
                        .clickable { viewModel.load() }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    Text("Retry", style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = Color.White))
                }
            }
        } else {
            val isEmpty = state.flags == null &&
                state.badgeDefinitions.isEmpty() &&
                state.levelDefinitions.isEmpty() &&
                state.houses.isEmpty() &&
                state.rewards.isEmpty() &&
                state.quests.isEmpty() &&
                state.events.isEmpty() &&
                state.leaderboard.isEmpty()
            if (isEmpty) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No gamification data yet", style = VTypography.body, color = VColors.ink2)
                    Spacer(Modifier.height(4.dp))
                    Text("Configure feature flags and create badges, levels, and rewards to get started.", style = VTypography.caption, color = VColors.ink3)
                }
            } else {
                LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { FeatureFlagsCard(state = state, onToggle = viewModel::setEnabled) }
                item { AnalyticsCard(state = state) }
                item { BadgeDefinitionsCard(badges = state.badgeDefinitions) }
                item { LevelDefinitionsCard(levels = state.levelDefinitions) }
                item { HousesCard(houses = state.houses) }
                item { RewardsCard(rewards = state.rewards) }
                item { QuestsCard(quests = state.quests) }
                item { EventsCard(events = state.events) }
                item { LeaderboardCard(leaderboard = state.leaderboard) }
                item { RedemptionsCard(state = state, onApprove = { id -> viewModel.updateRedemptionStatus(id, "APPROVED") }, onReject = { id -> viewModel.updateRedemptionStatus(id, "REJECTED") }) }
                item { BoostsCard(state = state, onCreateBoost = { type, mult, scope, tid, hrs -> viewModel.createBoost(type, mult, scope, tid, hrs) }) }
            }
            }
        }
    }
}

@Composable
private fun AdminGamificationHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.ChevronLeft, contentDescription = "Back", tint = VColors.violet, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(VColors.violet))
                Text("Gamification", style = VTypography.accentLabel, color = VColors.violet)
            }
            Spacer(Modifier.height(2.dp))
            Text("Management Console", style = VTypography.h2.copy(fontSize = 20.sp), color = VColors.ink)
        }
    }
}

@Composable
private fun ActionMessageBanner(message: String, isError: Boolean) {
    val bg = if (isError) VColors.error.copy(alpha = 0.1f) else VColors.mint.copy(alpha = 0.1f)
    val fg = if (isError) VColors.error else VColors.mint
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .clip(VShapes.md)
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(message, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = fg)
    }
}

@Composable
private fun AdminCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
        content()
    }
}

@Composable
private fun FeatureFlagsCard(state: AdminGamificationState, onToggle: (Boolean) -> Unit) {
    val flags = state.flags
    AdminCard(title = "Feature Flags") {
        if (flags == null) {
            Text("Unable to load flags", style = VTypography.caption, color = VColors.ink3)
            return@AdminCard
        }
        FlagToggleRow(
            label = "Enable Gamification",
            description = "Master kill switch — turns entire system on/off",
            checked = flags.isGamificationEnabled,
            onCheckedChange = onToggle,
        )
        if (flags.isGamificationEnabled) {
            Spacer(Modifier.height(4.dp))
            Text("Granular Toggles", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
            FlagToggleRow("Leaderboards", "Class & school rankings", flags.gamificationLeaderboards)
            FlagToggleRow("Rewards Shop", "Spend XP on real rewards", flags.gamificationRewards)
            FlagToggleRow("House System", "Guilds & collective competition", flags.gamificationHouses)
            FlagToggleRow("Quests", "Daily, weekly & seasonal quests", flags.gamificationQuests)
            FlagToggleRow("Mentor System", "Peer mentor & study buddy", flags.gamificationMentor)
            FlagToggleRow("Shout-Outs", "Peer encouragement", flags.gamificationShoutouts)
            FlagToggleRow("Seasonal Events", "Limited-edition badges", flags.gamificationEvents)
            FlagToggleRow("Class Goals", "Collective rewards", flags.gamificationClassGoals)
            FlagToggleRow("Combos", "Consecutive activity multipliers", flags.gamificationCombos)
            FlagToggleRow("XP Boosts", "Time-limited multipliers", flags.gamificationBoosts)
        }
    }
}

@Composable
private fun FlagToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink)
            Text(description, style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = onCheckedChange != null,
        )
    }
}

@Composable
private fun AnalyticsCard(state: AdminGamificationState) {
    val analytics = state.analytics ?: return
    val totalXp = analytics["totalXpAwarded"]?.toString() ?: "—"
    val totalBadges = analytics["totalBadgesEarned"]?.toString() ?: "—"
    val activeQuests = analytics["activeQuests"]?.toString() ?: "—"
    val redemptionRate = analytics["redemptionRate"]?.toString() ?: "—"

    AdminCard(title = "Analytics Overview") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AnalyticsMetric("Total XP", totalXp, VColors.violet)
            AnalyticsMetric("Badges", totalBadges, VColors.gold)
            AnalyticsMetric("Quests", activeQuests, VColors.coral)
            AnalyticsMetric("Redemptions", redemptionRate, VColors.mint)
        }
    }
}

@Composable
private fun AnalyticsMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = VTypography.h2.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold), color = color)
        Text(label, style = VTypography.caption.copy(fontSize = 10.sp), color = VColors.ink3)
    }
}

@Composable
private fun BadgeDefinitionsCard(badges: List<BadgeDefinition>) {
    if (badges.isEmpty()) return
    AdminCard(title = "Badge Catalog (${badges.size})") {
        badges.forEach { badge ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    Modifier.size(32.dp).clip(CircleShape).background(VColors.goldSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.Star, contentDescription = null, tint = VColors.gold, modifier = Modifier.size(16.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(badge.name, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink)
                    Text("${badge.category} · ${badge.rarity} · ${badge.xpRequirement} XP", style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
                }
                if (badge.isSeasonal) {
                    StatusPill("Seasonal", VColors.coral)
                }
            }
        }
    }
}

@Composable
private fun LevelDefinitionsCard(levels: List<LevelDefinition>) {
    if (levels.isEmpty()) return
    AdminCard(title = "Level Definitions (${levels.size})") {
        levels.forEach { level ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    Modifier.size(28.dp).clip(CircleShape).background(VColors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${level.level}", style = VTypography.body.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = VColors.violet)
                }
                Text(level.title, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink, modifier = Modifier.weight(1f))
                Text("${level.xpRequired} XP", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
            }
        }
    }
}

@Composable
private fun HousesCard(houses: List<House>) {
    if (houses.isEmpty()) return
    AdminCard(title = "Houses (${houses.size})") {
        houses.forEach { house ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(VColors.violet))
                Column(Modifier.weight(1f)) {
                    Text(house.name, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink)
                    Text("${house.memberCount} members · ${house.totalPoints} pts", style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
                }
            }
        }
    }
}

@Composable
private fun RewardsCard(rewards: List<Reward>) {
    if (rewards.isEmpty()) return
    AdminCard(title = "Rewards Catalog (${rewards.size})") {
        rewards.forEach { reward ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(reward.name, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink)
                    Text(reward.description, style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3, maxLines = 1)
                }
                Text("${reward.xpCost} XP", style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = VColors.violet)
                if (reward.isActive) StatusPill("Active", VColors.mint) else StatusPill("Inactive", VColors.ink3)
            }
        }
    }
}

@Composable
private fun QuestsCard(quests: List<QuestDefinition>) {
    if (quests.isEmpty()) return
    AdminCard(title = "Quest Pool (${quests.size})") {
        quests.forEach { quest ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(quest.name, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink)
                    Text("${quest.questType} · ${quest.xpReward} XP", style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
                }
                if (quest.isActive) StatusPill("Active", VColors.mint) else StatusPill("Inactive", VColors.ink3)
            }
        }
    }
}

@Composable
private fun EventsCard(events: List<SeasonalEvent>) {
    if (events.isEmpty()) return
    AdminCard(title = "Seasonal Events (${events.size})") {
        events.forEach { event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(event.name, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink)
                    Text("${event.startDate} → ${event.endDate}", style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
                }
                if (event.isActive) StatusPill("Active", VColors.mint) else StatusPill("Ended", VColors.ink3)
            }
        }
    }
}

@Composable
private fun LeaderboardCard(leaderboard: List<LeaderboardEntry>) {
    if (leaderboard.isEmpty()) return
    AdminCard(title = "School Leaderboard (Top ${leaderboard.size})") {
        leaderboard.take(10).forEach { entry ->
            val rankColor = when (entry.rank) {
                1 -> VColors.gold
                2 -> Color(0xFF94A3B8)
                3 -> VColors.coral
                else -> VColors.ink3
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(rankColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${entry.rank}", style = VTypography.body.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = rankColor)
                }
                Text("Student #${entry.studentId.takeLast(6)}", style = VTypography.body.copy(fontSize = 13.sp), color = VColors.ink, modifier = Modifier.weight(1f))
                Text("Lv ${entry.currentLevel}", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
                Spacer(Modifier.size(8.dp))
                Text("${entry.totalXp} XP", style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = VColors.violet)
            }
        }
    }
}

@Composable
private fun RedemptionsCard(
    state: AdminGamificationState,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
) {
    if (state.redemptions.isEmpty()) return
    AdminCard(title = "Redemption Approvals (${state.redemptions.size})") {
        state.redemptions.forEach { redemption ->
            val id = redemption["id"]?.toString() ?: return@forEach
            val rewardName = redemption["rewardName"]?.toString() ?: "Unknown"
            val status = redemption["status"]?.toString() ?: "PENDING"
            val xpSpent = redemption["xpSpent"]?.toString() ?: "0"

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(rewardName, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink)
                    Text("$xpSpent XP · $status", style = VTypography.caption.copy(fontSize = 11.sp), color = VColors.ink3)
                }
                if (status == "PENDING") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(VColors.mint.copy(alpha = 0.12f))
                            .clickable { onApprove(id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("Approve", style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = VColors.mint)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(VColors.error.copy(alpha = 0.12f))
                            .clickable { onReject(id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("Reject", style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = VColors.error)
                    }
                } else {
                    StatusPill(status, if (status == "APPROVED") VColors.mint else VColors.error)
                }
            }
        }
    }
}

@Composable
private fun BoostsCard(
    state: AdminGamificationState,
    onCreateBoost: (String, Float, String, String?, Int) -> Unit,
) {
    var showForm by remember { mutableStateOf(false) }
    var boostType by remember { mutableStateOf("WEEKEND_DOUBLE") }
    var multiplier by remember { mutableStateOf("2.0") }
    var targetScope by remember { mutableStateOf("ALL") }
    var durationHours by remember { mutableStateOf("24") }

    AdminCard(title = "XP Boosts (${state.boosts.size})") {
        state.boosts.forEach { boost ->
            val type = boost["boostType"]?.toString() ?: "Unknown"
            val mult = boost["multiplier"]?.toString() ?: "1.0"
            val active = boost["isActive"]?.toString()?.toBoolean() ?: false
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(type, style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = VColors.ink, modifier = Modifier.weight(1f))
                Text("${mult}x", style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = VColors.violet)
                if (active) StatusPill("Active", VColors.mint) else StatusPill("Expired", VColors.ink3)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(VColors.violetSoft)
                .clickable { showForm = !showForm }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (showForm) "Cancel" else "+ Create New Boost",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                color = VColors.violet,
            )
        }

        if (showForm) {
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = boostType,
                onValueChange = { boostType = it },
                label = { Text("Boost Type") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = VTypography.body.copy(color = VColors.ink),
                shape = VShapes.md,
                singleLine = true,
            )
            OutlinedTextField(
                value = multiplier,
                onValueChange = { multiplier = it },
                label = { Text("Multiplier (e.g. 2.0)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = VTypography.body.copy(color = VColors.ink),
                shape = VShapes.md,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            OutlinedTextField(
                value = targetScope,
                onValueChange = { targetScope = it },
                label = { Text("Target Scope (ALL / CLASS / STUDENT)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = VTypography.body.copy(color = VColors.ink),
                shape = VShapes.md,
                singleLine = true,
            )
            OutlinedTextField(
                value = durationHours,
                onValueChange = { durationHours = it },
                label = { Text("Duration (hours)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = VTypography.body.copy(color = VColors.ink),
                shape = VShapes.md,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.md)
                    .background(VColors.violet)
                    .clickable {
                        val mult = multiplier.toFloatOrNull() ?: 1.0f
                        val hrs = durationHours.toIntOrNull() ?: 24
                        onCreateBoost(boostType, mult, targetScope, null, hrs)
                        showForm = false
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (state.isActionLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                } else {
                    Text("Create Boost", style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = Color.White))
                }
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(text, style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold), color = color)
    }
}
