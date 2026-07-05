package com.littlebridge.enrollplus.core

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object CsrfProtection {

    private val STATE_CHANGING_METHODS = setOf(HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete)

    fun Application.installCsrfProtection() {
        intercept(ApplicationCallPipeline.Plugins) {
            val method = call.request.httpMethod
            if (method !in STATE_CHANGING_METHODS) return@intercept proceed()

            val isProduction = RuntimeEnvironment.isProduction
            if (!isProduction) return@intercept proceed()

            val origin = call.request.headers[HttpHeaders.Origin]
            val allowedOrigins = EnvConfig.get("CORS_ALLOWED_ORIGINS")
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                .orEmpty()

            // No Origin header → not a browser request (mobile app, curl, etc.).
            // CSRF via Origin validation is a browser-specific defense; mobile
            // clients use JWT bearer tokens which are inherently CSRF-resistant.
            if (origin == null) return@intercept proceed()

            if (allowedOrigins.isEmpty()) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    ApiError(
                        message = "Server not configured for cross-origin requests.",
                        errorCode = "CSRF_NO_ALLOWED_ORIGINS",
                    ),
                )
                return@intercept finish()
            }

            if (origin !in allowedOrigins) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    ApiError(
                        message = "Cross-origin request blocked.",
                        errorCode = "CSRF_ORIGIN_MISMATCH",
                    ),
                )
                return@intercept finish()
            }

            proceed()
        }
    }
}
