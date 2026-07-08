/*
 * File: BrandingRouting.kt
 * Module: feature.branding
 *
 * API endpoints for the School Branding Kit (SCHOOL_BRANDING_KIT_SPEC.md §9).
 *
 *   Admin (JWT + requireSchoolAdmin):
 *     GET    /api/v1/school/branding                    — get own school branding
 *     PATCH  /api/v1/school/branding                    — update colors/assets
 *     POST   /api/v1/school/branding/reset              — reset to defaults
 *     POST   /api/v1/school/branding/subdomain          — set custom subdomain
 *     DELETE /api/v1/school/branding/subdomain          — remove subdomain
 *     GET    /api/v1/school/branding/subdomain/check     — check availability
 *     POST   /api/v1/school/branding/assets              — upload logo/favicon/icon/splash (multipart)
 *     DELETE /api/v1/school/branding/assets?field=logo   — remove a specific asset
 *
 *   Public (no auth):
 *     GET    /api/v1/branding/{schoolId}                — public branding read
 *     GET    /api/v1/branding/subdomain/{subdomain}      — resolve subdomain
 */
package com.littlebridge.enrollplus.feature.branding

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.requireSchoolAdmin
import com.littlebridge.enrollplus.core.requireSchoolContext
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.feature.media.SupabaseStorage
import com.littlebridge.enrollplus.feature.notifications.Notify
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import com.littlebridge.enrollplus.feature.branding.BrandingService.Companion.SUBDOMAIN_REGEX
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import java.util.UUID

private val subdomainRegex = SUBDOMAIN_REGEX
private val brandingLogger = LoggerFactory.getLogger("BrandingRouting")

private fun guessContentType(fileName: String): String? {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "svg" -> "image/svg+xml"
        "ico" -> "image/x-icon"
        else -> null
    }
}

fun Route.brandingRouting() {
    // ── Public endpoints (no auth) ──────────────────────────────────────
    route("/api/v1/branding") {

        get("/{schoolId}") {
            val schoolId = call.parameters["schoolId"]?.let {
                runCatching { UUID.fromString(it) }.getOrNull()
            } ?: run { call.fail("Invalid school id"); return@get }

            val branding = BrandingService().getPublicBranding(schoolId)
            if (branding != null) call.ok(branding, "School branding")
            else call.fail("School not found", HttpStatusCode.NotFound, "BRANDING_NOT_FOUND")
        }

        get("/subdomain/{subdomain}") {
            val subdomain = call.parameters["subdomain"]
                ?: run { call.fail("Subdomain is required"); return@get }

            val result = BrandingService().resolveSubdomain(subdomain)
            if (result != null) call.ok(result, "Subdomain resolved")
            else call.fail("School not found for this subdomain", HttpStatusCode.NotFound, "SUBDOMAIN_NOT_FOUND")
        }
    }

    // ── Admin endpoints (JWT + school admin) ────────────────────────────
    authenticate("jwt") {
        route("/api/v1/school/branding") {

            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val branding = BrandingService().getBranding(ctx.schoolId)
                call.ok(branding, "School branding")
            }

            patch {
                val ctx = call.requireSchoolAdmin() ?: return@patch
                val req = runCatching { call.receive<UpdateBrandingRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@patch }

                try {
                    val updated = BrandingService().updateBranding(ctx.schoolId, req)
                    notifyBrandingChanged(ctx.schoolId, ctx.userId, "Branding colors updated")
                    call.ok(updated, "Branding updated")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "Invalid color format", HttpStatusCode.BadRequest, "INVALID_COLOR")
                }
            }

            post("/reset") {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val result = BrandingService().resetBranding(ctx.schoolId)
                notifyBrandingChanged(ctx.schoolId, ctx.userId, "Branding reset to defaults")
                call.ok(result, "Branding reset to defaults")
            }

            // ── Subdomain management ───────────────────────────────────
            get("/subdomain/check") {
                val ctx = call.requireSchoolAdmin() ?: return@get
                val subdomain = call.request.queryParameters["subdomain"]
                    ?: run { call.fail("Subdomain parameter is required"); return@get }

                if (!subdomain.matches(subdomainRegex)) {
                    call.fail(
                        "Invalid subdomain. Use lowercase letters, numbers, and hyphens (4-32 chars).",
                        HttpStatusCode.BadRequest,
                        "INVALID_SUBDOMAIN",
                    )
                    return@get
                }

                val available = BrandingService().checkSubdomainAvailable(ctx.schoolId, subdomain)
                call.ok(mapOf("available" to available), if (available) "Subdomain available" else "Subdomain already taken")
            }

            post("/subdomain") {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = runCatching { call.receive<SubdomainRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                if (!req.subdomain.matches(subdomainRegex)) {
                    call.fail(
                        "Invalid subdomain. Use lowercase letters, numbers, and hyphens (4-32 chars).",
                        HttpStatusCode.BadRequest,
                        "INVALID_SUBDOMAIN",
                    )
                    return@post
                }

                try {
                    val result = BrandingService().updateSubdomain(ctx.schoolId, req.subdomain)
                    call.ok(result, "Subdomain assigned")
                } catch (e: IllegalStateException) {
                    call.fail(e.message ?: "Subdomain already taken", HttpStatusCode.Conflict, "SUBDOMAIN_TAKEN")
                }
            }

            delete("/subdomain") {
                val ctx = call.requireSchoolAdmin() ?: return@delete
                val removed = BrandingService().removeSubdomain(ctx.schoolId)
                if (removed) call.ok(mapOf("removed" to true), "Subdomain removed")
                else call.fail("No subdomain set", HttpStatusCode.NotFound, "SUBDOMAIN_NOT_FOUND")
            }

            // ── Asset Upload (M-1: FR-001) ───────────────────────────────
            // POST /api/v1/school/branding/assets
            //   multipart: field=logo|logo_dark|favicon|app_icon|splash_screen|login_background
            //              file=<binary>
            //   → uploads to Supabase + updates branding row atomically
            post("/assets") {
                val ctx = call.requireSchoolAdmin() ?: return@post

                if (!SupabaseStorage.isConfigured()) {
                    call.fail(
                        "Media storage is not configured on the server. " +
                            "Set SUPABASE_URL and SUPABASE_SERVICE_KEY env vars.",
                        status = HttpStatusCode.ServiceUnavailable,
                        errorCode = "STORAGE_NOT_CONFIGURED",
                    )
                    return@post
                }

                val service = BrandingService()
                val validFields = service.validAssetFields()
                var field: String? = null
                var fileBytes: ByteArray? = null
                var contentType: String? = null

                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "field") {
                                val v = part.value.trim().lowercase()
                                if (v in validFields) field = v
                            }
                        }
                        is PartData.FileItem -> {
                            if (part.name == "file" && fileBytes == null) {
                                contentType = part.contentType?.toString()
                                    ?: part.originalFileName?.let { guessContentType(it) }
                                @Suppress("DEPRECATION")
                                fileBytes = part.streamProvider().use { it.readBytes() }
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                val f = field
                val bytes = fileBytes
                val ct = contentType
                if (f == null) {
                    call.fail("Missing or invalid 'field' (expected one of: ${validFields.joinToString(", ")})")
                    return@post
                }
                if (bytes == null || bytes.isEmpty()) {
                    call.fail("No file part found (expected multipart field 'file').")
                    return@post
                }
                if (bytes.size > 10L * 1024 * 1024) {
                    call.fail("File too large (${bytes.size / (1024 * 1024)} MB). Max 10 MB for branding assets.",
                        status = HttpStatusCode.PayloadTooLarge)
                    return@post
                }
                if (ct == null || SupabaseStorage.extensionFor(ct, "BRANDING") == null) {
                    call.fail("Unsupported file type '${ct ?: "unknown"}'.",
                        status = HttpStatusCode.UnsupportedMediaType)
                    return@post
                }

                val result = service.uploadAsset(ctx.schoolId, f, bytes, ct)
                if (result != null) {
                    notifyBrandingChanged(ctx.schoolId, ctx.userId, "Branding asset uploaded: $f")
                    call.ok(result, "Asset uploaded")
                } else {
                    call.fail("Upload to storage failed. Please try again.",
                        status = HttpStatusCode.BadGateway,
                        errorCode = "STORAGE_UPLOAD_FAILED")
                }
            }

            // DELETE /api/v1/school/branding/assets?field=logo
            delete("/assets") {
                val ctx = call.requireSchoolAdmin() ?: return@delete
                val field = call.request.queryParameters["field"]
                    ?: run { call.fail("'field' query parameter is required"); return@delete }

                val service = BrandingService()
                if (field !in service.validAssetFields()) {
                    call.fail("Invalid asset field: $field", HttpStatusCode.BadRequest, "INVALID_FIELD")
                    return@delete
                }

                val result = service.deleteAsset(ctx.schoolId, field)
                notifyBrandingChanged(ctx.schoolId, ctx.userId, "Branding asset removed: $field")
                call.ok(result, "Asset removed")
            }
        }
    }
}

/**
 * Notify other school admins (excluding the actor) that branding was changed.
 * This is the event/signal layer: branding changes affect the entire school's
 * appearance, so co-admins should be aware.
 */
private suspend fun notifyBrandingChanged(schoolId: UUID, actorId: UUID, message: String) {
    try {
        val coAdmins = com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery {
            AppUsersTable.selectAll()
                .where {
                    (AppUsersTable.schoolId eq schoolId) and
                        (AppUsersTable.role inList listOf("school_admin", "admin"))
                }
                .map { it[AppUsersTable.id].value }
                .filter { it != actorId }
        }
        if (coAdmins.isNotEmpty()) {
            Notify.toUsers(
                userIds = coAdmins,
                category = "BRANDING",
                title = "Branding Updated",
                body = message,
                schoolId = schoolId,
                actorId = actorId,
                deepLink = "/school/branding",
                refType = "school_branding",
                refId = schoolId.toString(),
            )
        }
    } catch (e: Exception) {
        brandingLogger.warn("[BrandingRouting] notifyBrandingChanged failed: {}", e.message, e)
    }
}
