package com.littlebridge.enrollplus.feature.gamification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.gamification.domain.model.*
import com.littlebridge.enrollplus.feature.gamification.domain.repository.GamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParentGamificationState(
    val stats: StudentStats? = null,
    val badges: List<StudentBadge> = emptyList(),
    val quests: List<StudentQuest> = emptyList(),
    val house: House? = null,
    val leaderboard: LeaderboardResponse? = null,
    val xpHistory: List<XpHistoryEntry> = emptyList(),
    val activeBoosts: List<XpBoost> = emptyList(),
    val classGoals: List<ClassGoal> = emptyList(),
    val rewards: List<Reward> = emptyList(),
    val redemptions: List<RewardRedemption> = emptyList(),
    val events: List<SeasonalEvent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ParentGamificationViewModel(
    private val repository: GamificationRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ParentGamificationState())
    val state: StateFlow<ParentGamificationState> = _state.asStateFlow()

    fun load(childId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            var hadError = false
            var errorMsg: String? = null

            val stats = safeCall { repository.getStats(token, childId) }
            val badges = safeCall { repository.getBadges(token, childId) }
            val quests = safeCall { repository.getQuests(token, childId) }
            val house = safeCall { repository.getHouse(token, childId) }
            val leaderboard = safeCall { repository.getLeaderboard(token, childId) }
            val xpHistory = safeCall { repository.getXpHistory(token, childId) }
            val activeBoosts = safeCall { repository.getActiveBoosts(token, childId) }
            val classGoals = safeCall { repository.getClassGoalsForChild(token, childId) }
            val rewards = safeCall { repository.getRewards(token, childId) }
            val redemptions = safeCall { repository.getRedemptions(token, childId) }
            val events = safeCall { repository.getActiveEvents(token) }

            _state.update {
                it.copy(
                    isLoading = false,
                    stats = stats,
                    badges = badges ?: emptyList(),
                    quests = quests ?: emptyList(),
                    house = house,
                    leaderboard = leaderboard,
                    xpHistory = xpHistory ?: emptyList(),
                    activeBoosts = activeBoosts ?: emptyList(),
                    classGoals = classGoals ?: emptyList(),
                    rewards = rewards ?: emptyList(),
                    redemptions = redemptions ?: emptyList(),
                    events = events ?: emptyList(),
                    error = errorMsg,
                )
            }
        }
    }

    fun redeemReward(childId: String, rewardId: String) {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            repository.redeemReward(token, childId, rewardId)
            load(childId)
        }
    }

    private inline fun <T> safeCall(block: () -> NetworkResult<ApiResponse<T>>): T? {
        return when (val result = block()) {
            is NetworkResult.Success -> result.data.data
            is NetworkResult.Error -> null
            is NetworkResult.ConnectionError -> null
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TEACHER GAMIFICATION VIEW MODEL
// ═══════════════════════════════════════════════════════════════════════════════

data class TeacherGamificationState(
    val overview: Map<String, *>? = null,
    val classLeaderboard: List<LeaderboardEntry> = emptyList(),
    val classGoals: List<Map<String, *>> = emptyList(),
    val availableQuests: List<QuestDefinition> = emptyList(),
    val availableBadges: List<BadgeDefinition> = emptyList(),
    val shoutouts: List<Map<String, *>> = emptyList(),
    val mentorAssignments: List<Map<String, *>> = emptyList(),
    val studyBuddyPairs: List<Map<String, *>> = emptyList(),
    val studentBadges: List<StudentBadge> = emptyList(),
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val error: String? = null,
    val actionMessage: String? = null,
)

class TeacherGamificationViewModel(
    private val repository: GamificationRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherGamificationState())
    val state: StateFlow<TeacherGamificationState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }

            val overview = safeCall { repository.getGamificationOverview(token) }
            val leaderboard = safeCall { repository.getClassLeaderboard(token) }
            val classGoals = safeCall { repository.getClassGoals(token) }
            val quests = safeCall { repository.getTeacherQuests(token) }
            val badges = safeCall { repository.getBadgeDefinitions(token) }
            val shoutouts = safeCall { repository.getShoutouts(token) }
            val mentors = safeCall { repository.getMentorAssignments(token) }
            val buddies = safeCall { repository.getStudyBuddyPairs(token) }

            _state.update {
                it.copy(
                    isLoading = false,
                    overview = overview,
                    classLeaderboard = leaderboard ?: emptyList(),
                    classGoals = classGoals ?: emptyList(),
                    availableQuests = quests ?: emptyList(),
                    availableBadges = badges ?: emptyList(),
                    shoutouts = shoutouts ?: emptyList(),
                    mentorAssignments = mentors ?: emptyList(),
                    studyBuddyPairs = buddies ?: emptyList(),
                )
            }
        }
    }

    fun loadStudentBadges(studentId: String) {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            val badges = safeCall { repository.getStudentBadges(token, studentId) }
            _state.update { it.copy(studentBadges = badges ?: emptyList()) }
        }
    }

    fun encourageStudent(studentId: String, amount: Int = 10, reason: String = "Keep up the great work!") {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.encourageStudent(token, EncourageRequest(studentId, amount, reason))
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Encouragement sent! +${amount} XP awarded"
                        else -> "Failed to encourage student"
                    },
                )
            }
            load()
        }
    }

    fun spotlightStudent(studentId: String, reason: String = "Spotlight award for improvement") {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.spotlightStudent(token, studentId, reason)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Student spotlighted!"
                        else -> "Failed to spotlight student"
                    },
                )
            }
            load()
        }
    }

    fun awardBadge(studentId: String, badgeId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.awardBadge(token, studentId, badgeId)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Badge awarded!"
                        else -> "Failed to award badge"
                    },
                )
            }
            loadStudentBadges(studentId)
        }
    }

    fun sendShoutout(receiverId: String, message: String, templateId: Int = 0, isPublic: Boolean = true) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.sendShoutout(token, receiverId, message, templateId, isPublic)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Shoutout sent!"
                        else -> "Failed to send shoutout"
                    },
                )
            }
            load()
        }
    }

    fun assignQuest(studentId: String, questId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.assignQuest(token, studentId, questId)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Quest assigned!"
                        else -> "Failed to assign quest"
                    },
                )
            }
        }
    }

    fun pepTalk(className: String, section: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.pepTalk(token, className, section)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Pep talk sent to class!"
                        else -> "Failed to send pep talk"
                    },
                )
            }
        }
    }

    fun createClassGoal(goalType: String, target: Int, reward: String, className: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val request = mapOf(
                "goalType" to goalType,
                "target" to target,
                "reward" to reward,
                "className" to className,
            )
            val result = repository.createClassGoal(token, request)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Class goal created!"
                        else -> "Failed to create class goal"
                    },
                )
            }
            load()
        }
    }

    fun updateClassGoalProgress(goalId: String, progress: Int) {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            repository.updateClassGoalProgress(token, goalId, progress)
            load()
        }
    }

    fun deleteShoutout(shoutoutId: String) {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            repository.deleteShoutout(token, shoutoutId)
            load()
        }
    }

    fun sendParentAlert(studentId: String, message: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.sendParentAlert(token, studentId, message)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Parent alert sent!"
                        else -> "Failed to send parent alert"
                    },
                )
            }
        }
    }

    fun assignMentor(mentorId: String, menteeId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.assignMentor(token, mentorId, menteeId)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Mentor assigned!"
                        else -> "Failed to assign mentor"
                    },
                )
            }
            load()
        }
    }

    fun unassignMentor(assignmentId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.unassignMentor(token, assignmentId)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Mentor unassigned!"
                        else -> "Failed to unassign mentor"
                    },
                )
            }
            load()
        }
    }

    fun assignStudyBuddy(student1Id: String, student2Id: String, classId: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.assignStudyBuddy(token, student1Id, student2Id, classId)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Study buddy pair created!"
                        else -> "Failed to create study buddy pair"
                    },
                )
            }
            load()
        }
    }

    fun unassignStudyBuddy(pairId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.unassignStudyBuddy(token, pairId)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Study buddy pair removed!"
                        else -> "Failed to remove study buddy pair"
                    },
                )
            }
            load()
        }
    }

    fun clearActionMessage() {
        _state.update { it.copy(actionMessage = null) }
    }

    private inline fun <T> safeCall(block: () -> NetworkResult<ApiResponse<T>>): T? {
        return when (val result = block()) {
            is NetworkResult.Success -> result.data.data
            is NetworkResult.Error -> null
            is NetworkResult.ConnectionError -> null
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ADMIN GAMIFICATION VIEW MODEL
// ═══════════════════════════════════════════════════════════════════════════════

data class AdminGamificationState(
    val flags: GamificationFlags? = null,
    val badgeDefinitions: List<BadgeDefinition> = emptyList(),
    val levelDefinitions: List<LevelDefinition> = emptyList(),
    val houses: List<House> = emptyList(),
    val rewards: List<Reward> = emptyList(),
    val quests: List<QuestDefinition> = emptyList(),
    val events: List<SeasonalEvent> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val redemptions: List<Map<String, *>> = emptyList(),
    val boosts: List<Map<String, *>> = emptyList(),
    val analytics: Map<String, *>? = null,
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val error: String? = null,
    val actionMessage: String? = null,
)

class AdminGamificationViewModel(
    private val repository: GamificationRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AdminGamificationState())
    val state: StateFlow<AdminGamificationState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferenceRepository.getUserToken().first()
            if (token == null) {
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }

            var hasError = false
            var errorMsg: String? = null

            val flags = safeCall { repository.getFlags(token) }
            if (flags == null) { hasError = true; errorMsg = "Failed to load gamification flags" }
            val badges = safeCall { repository.getBadgeDefinitions(token) }
            val levels = safeCall { repository.getLevelDefinitions(token) }
            val houses = safeCall { repository.getHouses(token) }
            val rewards = safeCall { repository.getAdminRewards(token) }
            val quests = safeCall { repository.getAdminQuests(token) }
            val events = safeCall { repository.getAdminEvents(token) }
            val leaderboard = safeCall { repository.getAdminLeaderboard(token) }
            val redemptions = safeCall { repository.getAdminRedemptions(token) }
            val boosts = safeCall { repository.getAdminBoosts(token) }
            val analytics = safeCall { repository.getAnalytics(token) }

            _state.update {
                it.copy(
                    isLoading = false,
                    error = if (hasError) errorMsg else null,
                    flags = flags,
                    badgeDefinitions = badges ?: emptyList(),
                    levelDefinitions = levels ?: emptyList(),
                    houses = houses ?: emptyList(),
                    rewards = rewards ?: emptyList(),
                    quests = quests ?: emptyList(),
                    events = events ?: emptyList(),
                    leaderboard = leaderboard ?: emptyList(),
                    redemptions = redemptions ?: emptyList(),
                    boosts = boosts ?: emptyList(),
                    analytics = analytics,
                )
            }
        }
    }

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.setEnabled(token, enabled)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> if (enabled) "Gamification enabled" else "Gamification disabled"
                        else -> "Failed to update gamification setting"
                    },
                )
            }
            load()
        }
    }

    fun setGranularFlag(flagKey: String, enabled: Boolean) {
        val currentFlags = _state.value.flags
        if (currentFlags != null) {
            val updated = when (flagKey) {
                "gamification_leaderboards" -> currentFlags.copy(gamificationLeaderboards = enabled)
                "gamification_rewards" -> currentFlags.copy(gamificationRewards = enabled)
                "gamification_houses" -> currentFlags.copy(gamificationHouses = enabled)
                "gamification_quests" -> currentFlags.copy(gamificationQuests = enabled)
                "gamification_mentor" -> currentFlags.copy(gamificationMentor = enabled)
                "gamification_shoutouts" -> currentFlags.copy(gamificationShoutouts = enabled)
                "gamification_events" -> currentFlags.copy(gamificationEvents = enabled)
                "gamification_class_goals" -> currentFlags.copy(gamificationClassGoals = enabled)
                "gamification_combos" -> currentFlags.copy(gamificationCombos = enabled)
                "gamification_boosts" -> currentFlags.copy(gamificationBoosts = enabled)
                else -> currentFlags
            }
            _state.update { it.copy(flags = updated) }
        }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first() ?: return@launch
            val result = repository.setGranularFlag(token, flagKey, enabled)
            _state.update {
                it.copy(
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "${flagKey.removePrefix("gamification_").replaceFirstChar { it.uppercase() }} ${if (enabled) "enabled" else "disabled"}"
                        else -> "Failed to update ${flagKey.removePrefix("gamification_")}"
                    },
                )
            }
            if (result !is NetworkResult.Success) load()
        }
    }

    fun updateRedemptionStatus(redemptionId: String, status: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.updateRedemptionStatus(token, redemptionId, status)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Redemption $status"
                        else -> "Failed to update redemption"
                    },
                )
            }
            load()
        }
    }

    fun createBoost(boostType: String, multiplier: Float, targetScope: String, targetId: String?, durationHours: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, actionMessage = null) }
            val token = preferenceRepository.getUserToken().first() ?: run {
                _state.update { it.copy(isActionLoading = false) }
                return@launch
            }
            val result = repository.createBoost(token, boostType, multiplier, targetScope, targetId, durationHours)
            _state.update {
                it.copy(
                    isActionLoading = false,
                    actionMessage = when (result) {
                        is NetworkResult.Success -> "Boost created!"
                        else -> "Failed to create boost"
                    },
                )
            }
            load()
        }
    }

    fun clearActionMessage() {
        _state.update { it.copy(actionMessage = null) }
    }

    private inline fun <T> safeCall(block: () -> NetworkResult<ApiResponse<T>>): T? {
        return when (val result = block()) {
            is NetworkResult.Success -> result.data.data
            is NetworkResult.Error -> null
            is NetworkResult.ConnectionError -> null
        }
    }
}
