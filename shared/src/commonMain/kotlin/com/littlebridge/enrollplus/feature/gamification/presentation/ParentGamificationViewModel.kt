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
    val xpHistory: List<Map<String, *>> = emptyList(),
    val activeBoosts: List<Map<String, *>> = emptyList(),
    val classGoals: List<Map<String, *>> = emptyList(),
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
