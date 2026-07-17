package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.repository.AdminDashboardRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.AnalyticsRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

// ---------------------------------------------------------------------------
// UI data classes — consumed by AnalyticsDashboardScreen.kt
// These are intentionally plain data classes (no @Serializable) because they
// live in the presentation layer and are never serialised over the wire.
// ---------------------------------------------------------------------------

data class AnalyticsCardData(
    val title: String,
    val value: String,
    val subValue: String,
    val iconUrl: String,
    val trend: String? = null
)

data class InsightItem(
    val title: String,
    val description: String,
    val iconName: String,
    val iconColor: Long        // 0xAARRGGBB hex color as Long, e.g. 0xFF4CAF50
)

// ---------------------------------------------------------------------------
// State
// ---------------------------------------------------------------------------

data class AnalyticsDashboardState(
    val performanceTrend: List<Float> = emptyList(),
    val trendLabels: List<String> = emptyList(),
    val currentGrowth: String = "0%",
    val cards: List<AnalyticsCardData> = emptyList(),
    val insights: List<InsightItem> = emptyList(),
    val overview: AdminDashboardOverview? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val parseWarning: String? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

class AnalyticsDashboardViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val adminDashboardRepository: AdminDashboardRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsDashboardState())
    val state: StateFlow<AnalyticsDashboardState> = _state.asStateFlow()

    init { loadOverview() }

    fun loadOverview() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isLoading = false)
                return@launch
            }

            // Fetch analytics overview (trend, cards, insights) and admin dashboard
            // overview (KPIs, fee analytics, parent engagement, school pulse) in parallel.
            val analyticsResult = analyticsRepository.getOverview(token)
            val overviewResult = adminDashboardRepository.getOverview(token)

            when (analyticsResult) {
                is NetworkResult.Success -> {
                    val data = analyticsResult.data.data
                    val rawCards = data?.cards ?: emptyList()
                    val rawInsights = data?.insights ?: emptyList()
                    val parsedCards = rawCards.mapNotNull { parseCard(it) }
                    val parsedInsights = rawInsights.mapNotNull { parseInsight(it) }
                    val cardFailures = rawCards.size - parsedCards.size
                    val insightFailures = rawInsights.size - parsedInsights.size
                    val warning = if (cardFailures > 0 || insightFailures > 0) {
                        "Some data could not be displayed ($cardFailures card(s), $insightFailures insight(s) failed to parse)"
                    } else null

                    val overviewData = (overviewResult as? NetworkResult.Success)?.data?.data

                    _state.value = _state.value.copy(
                        performanceTrend = data?.performanceTrend?.map { it.toFloat() } ?: emptyList(),
                        trendLabels      = data?.trendLabels ?: emptyList(),
                        currentGrowth    = data?.currentGrowth ?: "0%",
                        cards            = parsedCards,
                        insights         = parsedInsights,
                        overview         = overviewData,
                        isLoading        = false,
                        parseWarning     = warning,
                        isStale          = analyticsResult.isStale,
                        isOffline        = analyticsResult.isOffline,
                    )
                }
                is NetworkResult.Error -> {
                    AppLogger.e("AnalyticsDashboardVM", "getOverview error: ${analyticsResult.message}")
                    _state.value = _state.value.copy(isLoading = false, errorMessage = analyticsResult.message)
                }
                is NetworkResult.ConnectionError -> {
                    AppLogger.e("AnalyticsDashboardVM", "getOverview connection error")
                    _state.value = _state.value.copy(isLoading = false, errorMessage = "Connection error. Check your internet.")
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // JSON parsing helpers — JsonElement is only touched here, inside `shared`,
    // where kotlinx-serialization-json is on the classpath.
    // composeApp only ever sees the typed data classes above.
    // -----------------------------------------------------------------------

    private fun parseCard(element: JsonElement): AnalyticsCardData? {
        return try {
            val obj   = element.jsonObject
            val title = obj["title"]?.jsonPrimitive?.contentOrNull ?: return null
            AnalyticsCardData(
                title    = title,
                value    = obj["value"]?.jsonPrimitive?.contentOrNull    ?: "",
                subValue = obj["sub_value"]?.jsonPrimitive?.contentOrNull ?: "",
                iconUrl  = obj["icon_url"]?.jsonPrimitive?.contentOrNull  ?: "",
                trend    = obj["trend"]?.jsonPrimitive?.contentOrNull
            )
        } catch (e: Exception) {
            AppLogger.e("AnalyticsDashboardVM", "parseCard failed: ${e.message}")
            null
        }
    }

    private fun parseInsight(element: JsonElement): InsightItem? {
        return try {
            val obj   = element.jsonObject
            val title = obj["title"]?.jsonPrimitive?.contentOrNull ?: return null
            InsightItem(
                title       = title,
                description = obj["description"]?.jsonPrimitive?.contentOrNull ?: "",
                iconName    = obj["icon_name"]?.jsonPrimitive?.contentOrNull   ?: "insights",
                iconColor   = obj["icon_color"]?.jsonPrimitive?.contentOrNull?.let(::parseIconColor) ?: 0xFF6200EE
            )
        } catch (e: Exception) {
            AppLogger.e("AnalyticsDashboardVM", "parseInsight failed: ${e.message}")
            null
        }
    }

    /**
     * Backend/CMS may send icon colors as either an ARGB hex string ("#10B981")
     * or a raw Long. Convert either to a Compose Color-compatible Long.
     */
    private fun parseIconColor(raw: String): Long {
        val s = raw.trim()
        return runCatching {
            if (s.startsWith("#")) {
                // RGB hex without alpha -> prepend opaque alpha.
                val rgb = s.substring(1).toLong(16)
                if (s.length == 7) rgb or 0xFF000000L else rgb
            } else {
                s.toLong()
            }
        }.getOrDefault(0xFF6200EE)
    }
}
