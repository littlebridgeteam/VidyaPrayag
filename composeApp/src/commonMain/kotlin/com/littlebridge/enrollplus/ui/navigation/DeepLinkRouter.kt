package com.littlebridge.enrollplus.ui.navigation

sealed class DeepLinkTarget {
    object None : DeepLinkTarget()

    data class TeacherTab(val tab: TeacherDeepLinkTab, val params: Map<String, String> = emptyMap()) : DeepLinkTarget()
    object ParentDashboard : DeepLinkTarget()
    object AuthGate : DeepLinkTarget()
}

enum class TeacherDeepLinkTab {
    Home, Update, Classes, Timetable, Profile
}

fun parseDeepLink(path: String?, role: String?): DeepLinkTarget {
    if (path.isNullOrBlank()) return DeepLinkTarget.None

    val cleanPath = path.removePrefix("/").removePrefix("vidyaprayag://app/")
    val segments = cleanPath.split("?")
    val routeSegments = segments[0].split("/").filter { it.isNotBlank() }
    val query = if (segments.size > 1) parseQuery(segments[1]) else emptyMap()

    val root = routeSegments.getOrNull(0)?.lowercase() ?: return DeepLinkTarget.None

    val isTeacher = role?.equals("teacher", ignoreCase = true) == true

    if (isTeacher || root == "teacher") {
        val teacherSection = if (root == "teacher") routeSegments.getOrNull(1)?.lowercase() else root
        val tab = when (teacherSection) {
            "home", "dashboard" -> TeacherDeepLinkTab.Home
            "update", "attendance", "marks", "syllabus", "homework", "lesson" -> TeacherDeepLinkTab.Update
            "classes", "class" -> TeacherDeepLinkTab.Classes
            "timetable", "schedule" -> TeacherDeepLinkTab.Timetable
            "profile", "leave", "settings" -> TeacherDeepLinkTab.Profile
            else -> null
        }
        if (tab != null) return DeepLinkTarget.TeacherTab(tab, query)
    }

    if (root == "parent" || (!isTeacher && root in listOf("attendance", "marks", "homework", "announcements", "fees", "leave", "pews", "report-card", "dashboard"))) {
        return DeepLinkTarget.ParentDashboard
    }

    return DeepLinkTarget.None
}

private fun parseQuery(queryString: String): Map<String, String> {
    return queryString.split("&")
        .filter { it.contains("=") }
        .associate {
            val (key, value) = it.split("=", limit = 2)
            key to value
        }
}
