package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class ParentTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Rounded.Home),
    Academics("Academics", Icons.AutoMirrored.Rounded.MenuBook),
    Fees("Fees", Icons.Rounded.CurrencyRupee),
    Conversations("Chats", Icons.AutoMirrored.Rounded.Chat),
    Profile("Profile", Icons.Rounded.Person),
}

enum class ParentOverlay(val title: String) {
    Notifications("Notifications"),
    Calendar("Calendar"),
    Scholarships("Scholarships"),
    AccountSettings("Account Settings"),
    Leave("Apply Leave"),
    Discovery("Discover Schools"),
    SchoolDetail("School Detail"),
    Health("Health Records"),
    Pulse("Pulse Score"),
    Transport("Transport"),
    TutorChat("AI Tutor"),
    TutorProgress("Tutor Progress"),
    DigitalIdCard("Digital ID Card"),
    Library("Library"),
    Events("Events"),
    LinkChild("Link Your Child"),
}

data class ParentDeepLink(
    val tab: ParentTab,
    val subTab: String? = null,
    val overlay: ParentOverlay? = null,
    val params: Map<String, String> = emptyMap(),
)

object ParentDeepLinkParser {
    fun parse(path: String?): ParentDeepLink? {
        if (path.isNullOrBlank()) return null
        val clean = path.removePrefix("/").removePrefix("vidyaprayag://app/")
        val segments = clean.split("?")
        val routeSegments = segments[0].split("/").filter { it.isNotBlank() }
        if (routeSegments.isEmpty()) return null
        val root = routeSegments[0].lowercase()
        if (root != "parent") return null

        val section = routeSegments.getOrNull(1)?.lowercase() ?: return ParentDeepLink(ParentTab.Home)
        val subSection = routeSegments.getOrNull(2)?.lowercase()
        val query = if (segments.size > 1) parseQuery(segments[1]) else emptyMap()

        return when (section) {
            "home" -> ParentDeepLink(ParentTab.Home, params = query)
            "academics" -> ParentDeepLink(ParentTab.Academics, subTab = subSection, params = query)
            "fees" -> ParentDeepLink(ParentTab.Fees, params = query)
            "conversations" -> ParentDeepLink(ParentTab.Conversations, subTab = subSection, params = query)
            "profile" -> ParentDeepLink(ParentTab.Profile, params = query)
            "notifications" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Notifications, params = query)
            "transport" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Transport, params = query)
            "leave" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Leave, params = query)
            "scholarships" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Scholarships, params = query)
            "health" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Health, params = query)
            "pulse" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Pulse, params = query)
            "tutor" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.TutorChat, params = query)
            "tutor-progress" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.TutorProgress, params = query)
            "id-card" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.DigitalIdCard, params = query)
            "library" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Library, params = query)
            "events" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Events, params = query)
            "calendar" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Calendar, params = query)
            "link-child" -> ParentDeepLink(ParentTab.Profile, overlay = ParentOverlay.LinkChild, params = query)
            "account-settings" -> ParentDeepLink(ParentTab.Profile, overlay = ParentOverlay.AccountSettings, params = query)
            "discovery" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.Discovery, params = query)
            "school-detail" -> ParentDeepLink(ParentTab.Home, overlay = ParentOverlay.SchoolDetail, params = query)
            "messages" -> {
                val threadId = subSection
                ParentDeepLink(ParentTab.Conversations, subTab = threadId, params = query)
            }
            else -> ParentDeepLink(ParentTab.Home, params = query)
        }
    }

    private fun parseQuery(qs: String): Map<String, String> =
        qs.split("&").filter { it.contains("=") }.associate {
            val (k, v) = it.split("=", limit = 2)
            k to v
        }
}
