/*
 * File: JwtConfig.kt
 * Module: core
 * Purpose:
 *   Centralised HMAC256 JWT issuance + verification for VidyaPrayag.
 *   Wraps com.auth0:java-jwt (transitively pulled in by ktor-server-auth-jwt).
 *
 * Reads (with safe dev defaults):
 *   - JWT_SECRET       → HMAC signing key (REQUIRED in production)
 *   - JWT_ISSUER       → default "vidyaprayag-api"
 *   - JWT_AUDIENCE     → default "vidyaprayag-app"
 *   - JWT_REALM        → default "vidyaprayag"
 *   - Access-token TTL is role-based: 30 min for admin roles, 24 h for others
 *
 * Token claims:
 *   - sub        : userId (UUID string)
 *   - role       : ADMIN | PARENT | TEACHER | ALUMNI
 *   - name       : display name (convenience)
 *
 * Used by:
 *   - feature/auth/AuthRouting.kt    (signup, login → issues token)
 *   - core/SecurityModule.kt         (installs Ktor JWT auth)
 *   - any handler that does `call.principalUserId()`
 *
 * NOTE FOR DEVOPS (manual step you must do):
 *   Set JWT_SECRET to a strong random 256-bit value in production .env.
 *   In dev (no DATABASE_URL), an ephemeral random secret is generated
 *   automatically — tokens will not survive a restart.
 */
package com.littlebridge.enrollplus.core

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.util.Base64
import java.util.Date

object JwtConfig {
    private val log = LoggerFactory.getLogger("JwtConfig")

    private fun env(name: String, default: String): String =
        EnvConfig.get(name) ?: default

    private fun rawEnv(name: String): String? =
        EnvConfig.get(name)

    val secret: String by lazy {
        val configured = rawEnv("JWT_SECRET")
        if (RuntimeEnvironment.isProduction) {
            if (configured.isNullOrBlank()) {
                throw IllegalStateException(
                    "FATAL: JWT_SECRET environment variable is required in production. " +
                    "Refusing to boot without a signing key — this would allow trivial token forgery."
                )
            }
            if (configured.length < 32) {
                throw IllegalStateException(
                    "FATAL: JWT_SECRET must be at least 32 characters in production. " +
                    "Current length: ${configured.length}. Use: openssl rand -hex 64"
                )
            }
            configured
        } else {
            if (configured.isNullOrBlank()) {
                val ephemeral = Base64.getEncoder().encodeToString(ByteArray(64).also { SecureRandom().nextBytes(it) })
                log.warn("WARNING: Using ephemeral JWT secret for dev mode. Tokens will not survive restart.")
                ephemeral
            } else {
                configured
            }
        }
    }

    val issuer: String   by lazy { env("JWT_ISSUER", "vidyaprayag-api") }
    val audience: String by lazy { env("JWT_AUDIENCE", "vidyaprayag-app") }
    val realm: String    by lazy { env("JWT_REALM", "vidyaprayag") }
    private val algorithm by lazy { Algorithm.HMAC256(secret) }

    val verifier: com.auth0.jwt.JWTVerifier by lazy {
        JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }

    // Spec §16 Session timeout: admin/librarian 30 min, student/parent 24 hours.
    private val ADMIN_ROLES = setOf("school_admin", "school_staff", "admin", "super_admin")
    private const val ADMIN_EXPIRY_SECS = 30L * 60          // 30 minutes
    private const val DEFAULT_EXPIRY_SECS = 24L * 60 * 60   // 24 hours

    /** Issue a signed access token with role-based expiry. */
    fun issueToken(userId: String, role: String, name: String): String {
        val ttl = if (role in ADMIN_ROLES) ADMIN_EXPIRY_SECS else DEFAULT_EXPIRY_SECS
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(userId)
            .withClaim("role", role)
            .withClaim("name", name)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + ttl * 1000))
            .sign(algorithm)
    }

    /**
     * Multi-Branch (MULTI_BRANCH_SPEC.md §8.3): issue a token with org admin
     * claims. The organization_id and org_admin_role are read from app_users
     * at login time and embedded as JWT claims so downstream guards can scope
     * queries by organization without an extra DB round-trip.
     */
    fun issueTokenWithOrg(
        userId: String,
        role: String,
        name: String,
        organizationId: String?,
        orgAdminRole: String?
    ): String {
        val ttl = if (role in ADMIN_ROLES) ADMIN_EXPIRY_SECS else DEFAULT_EXPIRY_SECS
        val builder = JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(userId)
            .withClaim("role", role)
            .withClaim("name", name)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + ttl * 1000))
        organizationId?.let { builder.withClaim("organization_id", it) }
        orgAdminRole?.let { builder.withClaim("org_admin_role", it) }
        return builder.sign(algorithm)
    }

    /** Issue an opaque refresh token. In production, persist + rotate it. */
    fun issueRefreshToken(userId: String): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(userId)
            .withClaim("type", "refresh")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000)) // 30 days
            .sign(algorithm)
}
