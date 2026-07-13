package com.littlebridge.enrollplus.core

import io.ktor.server.application.ApplicationCall
import org.slf4j.LoggerFactory

private val versionLog = LoggerFactory.getLogger("ApiVersion")

data class ApiDeprecation(
    val version: String,
    val sunsetDate: String? = null,
)

object ApiVersionConfig {
    @Volatile
    private var _deprecatedVersions: Map<String, ApiDeprecation> = emptyMap()
    val deprecatedVersions: Map<String, ApiDeprecation> get() = _deprecatedVersions

    @Synchronized
    fun setDeprecated(version: String, sunsetDate: String? = null) {
        _deprecatedVersions = _deprecatedVersions + (version to ApiDeprecation(version, sunsetDate))
    }
}

fun extractApiVersion(path: String): String? {
    if (!path.startsWith("/api/")) return null
    val segments = path.removePrefix("/api/").split("/")
    val first = segments.firstOrNull() ?: return null
    return if (first.matches(Regex("v\\d+"))) first else null
}

fun ApplicationCall.applyApiVersionHeaders() {
    val path = this.request.local.uri
    val version = extractApiVersion(path)
    if (version != null) {
        val deprecation = ApiVersionConfig.deprecatedVersions[version]
        if (deprecation != null) {
            this.response.headers.append("Deprecation", "true")
            deprecation.sunsetDate?.let { sunset ->
                this.response.headers.append("Sunset", sunset)
            }
            versionLog.warn("Deprecated API version {} called: {}", version, path)
        }
    }
}
