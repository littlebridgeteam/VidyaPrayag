/*
 * File: BrandingService.kt
 * Module: feature.branding
 *
 * Core service for the School Branding Kit (SCHOOL_BRANDING_KIT_SPEC.md).
 *
 * Handles:
 *   - Branding CRUD (get/update per school)
 *   - Color validation (hex format)
 *   - Subdomain management (check availability, assign, remove)
 *   - Brand asset URL storage (logos, icons, splash — upload handled by routing layer)
 *   - Default fallback when no branding row exists
 *
 * Plug-and-play: routing calls this service, no direct DB access from routing layer.
 */
package com.littlebridge.enrollplus.feature.branding

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SchoolBrandingTable
import com.littlebridge.enrollplus.db.SchoolsTable
import com.littlebridge.enrollplus.feature.media.SupabaseStorage
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

// ── DTOs ─────────────────────────────────────────────────────────────────────

@Serializable
data class SchoolBrandingDto(
    val schoolId: String,
    val schoolName: String,
    val logoUrl: String? = null,
    val logoDarkUrl: String? = null,
    val faviconUrl: String? = null,
    val appIconUrl: String? = null,
    val splashScreenUrl: String? = null,
    val primaryColor: String = "#2563EB",
    val secondaryColor: String = "#1E40AF",
    val accentColor: String = "#3B82F6",
    val customSubdomain: String? = null,
    val loginBackgroundUrl: String? = null,
    val isCustomized: Boolean = false,
)

@Serializable
data class UpdateBrandingRequest(
    val primaryColor: String? = null,
    val secondaryColor: String? = null,
    val accentColor: String? = null,
    val logoUrl: String? = null,
    val logoDarkUrl: String? = null,
    val faviconUrl: String? = null,
    val appIconUrl: String? = null,
    val splashScreenUrl: String? = null,
    val loginBackgroundUrl: String? = null,
    val isCustomized: Boolean? = null,
)

@Serializable
data class SubdomainRequest(
    val subdomain: String,
)

@Serializable
data class SubdomainResponse(
    val subdomain: String,
)

@Serializable
data class SubdomainResolutionDto(
    val schoolId: String,
    val schoolName: String,
    val branding: SchoolBrandingDto,
)

// ── Service ──────────────────────────────────────────────────────────────────

class BrandingService {

    private val hexRegex = Regex("^#[0-9A-Fa-f]{6}$")
    private val subdomainRegex = SUBDOMAIN_REGEX

    companion object {
        val SUBDOMAIN_REGEX = Regex("^[a-z0-9][a-z0-9-]{2,30}[a-z0-9]$")
    }

    // ── Read ──────────────────────────────────────────────────────────────

    suspend fun getBranding(schoolId: UUID): SchoolBrandingDto = dbQuery {
        val schoolName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq schoolId }
            .singleOrNull()?.get(SchoolsTable.name) ?: "Unknown School"

        val row = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .singleOrNull()

        if (row != null) {
            rowToDto(row, schoolName)
        } else {
            // EC-1: No branding row → return defaults
            defaultDto(schoolId, schoolName)
        }
    }

    // ── Public read (no auth) ─────────────────────────────────────────────

    suspend fun getPublicBranding(schoolId: UUID): SchoolBrandingDto? = dbQuery {
        val schoolRow = SchoolsTable.selectAll()
            .where { (SchoolsTable.id eq schoolId) and (SchoolsTable.isActive eq true) }
            .singleOrNull() ?: return@dbQuery null

        val schoolName = schoolRow[SchoolsTable.name]
        val row = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .singleOrNull()

        if (row != null) rowToDto(row, schoolName) else defaultDto(schoolId, schoolName)
    }

    // ── Update ────────────────────────────────────────────────────────────

    suspend fun updateBranding(schoolId: UUID, req: UpdateBrandingRequest): SchoolBrandingDto = dbQuery {
        // Validate colors if provided
        req.primaryColor?.let { requireHex(it, "primary_color") }
        req.secondaryColor?.let { requireHex(it, "secondary_color") }
        req.accentColor?.let { requireHex(it, "accent_color") }

        // Validate URL fields are non-empty if provided
        req.logoUrl?.let { requireNonEmptyUrl(it, "logo_url") }
        req.logoDarkUrl?.let { requireNonEmptyUrl(it, "logo_dark_url") }
        req.faviconUrl?.let { requireNonEmptyUrl(it, "favicon_url") }
        req.appIconUrl?.let { requireNonEmptyUrl(it, "app_icon_url") }
        req.splashScreenUrl?.let { requireNonEmptyUrl(it, "splash_screen_url") }
        req.loginBackgroundUrl?.let { requireNonEmptyUrl(it, "login_background_url") }

        val now = Instant.now()
        val existing = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .singleOrNull()

        if (existing != null) {
            SchoolBrandingTable.update(
                { SchoolBrandingTable.schoolId eq schoolId }
            ) {
                req.primaryColor?.let { c -> it[primaryColor] = c }
                req.secondaryColor?.let { c -> it[secondaryColor] = c }
                req.accentColor?.let { c -> it[accentColor] = c }
                req.logoUrl?.let { u -> it[logoUrl] = u }
                req.logoDarkUrl?.let { u -> it[logoDarkUrl] = u }
                req.faviconUrl?.let { u -> it[faviconUrl] = u }
                req.appIconUrl?.let { u -> it[appIconUrl] = u }
                req.splashScreenUrl?.let { u -> it[splashScreenUrl] = u }
                req.loginBackgroundUrl?.let { u -> it[loginBackgroundUrl] = u }
                req.isCustomized?.let { flag -> it[isCustomized] = flag }
                it[updatedAt] = now
            }
        } else {
            SchoolBrandingTable.insert {
                it[SchoolBrandingTable.schoolId] = schoolId
                req.primaryColor?.let { c -> it[primaryColor] = c }
                req.secondaryColor?.let { c -> it[secondaryColor] = c }
                req.accentColor?.let { c -> it[accentColor] = c }
                req.logoUrl?.let { u -> it[logoUrl] = u }
                req.logoDarkUrl?.let { u -> it[logoDarkUrl] = u }
                req.faviconUrl?.let { u -> it[faviconUrl] = u }
                req.appIconUrl?.let { u -> it[appIconUrl] = u }
                req.splashScreenUrl?.let { u -> it[splashScreenUrl] = u }
                req.loginBackgroundUrl?.let { u -> it[loginBackgroundUrl] = u }
                it[isCustomized] = req.isCustomized ?: true
                it[createdAt] = now
                it[updatedAt] = now
            }
        }

        // Sync legacy fields to schools table so institutional profile reflects branding changes
        if (req.logoUrl != null || req.primaryColor != null) {
            SchoolsTable.update({ SchoolsTable.id eq schoolId }) {
                req.logoUrl?.let { url -> it[logoUrl] = url }
                req.primaryColor?.let { color -> it[brandColor] = color }
                it[updatedAt] = now
            }
        }

        val schoolName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq schoolId }
            .singleOrNull()?.get(SchoolsTable.name) ?: "Unknown School"

        SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .single().let { rowToDto(it, schoolName) }
    }

    // ── Reset to default ──────────────────────────────────────────────────

    suspend fun resetBranding(schoolId: UUID): SchoolBrandingDto = dbQuery {
        val now = Instant.now()
        val existing = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .singleOrNull()

        if (existing != null) {
            SchoolBrandingTable.update(
                { SchoolBrandingTable.schoolId eq schoolId }
            ) {
                it[primaryColor] = "#2563EB"
                it[secondaryColor] = "#1E40AF"
                it[accentColor] = "#3B82F6"
                it[isCustomized] = false
                it[updatedAt] = now
            }
        } else {
            SchoolBrandingTable.insert {
                it[SchoolBrandingTable.schoolId] = schoolId
                it[primaryColor] = "#2563EB"
                it[secondaryColor] = "#1E40AF"
                it[accentColor] = "#3B82F6"
                it[isCustomized] = false
                it[createdAt] = now
                it[updatedAt] = now
            }
        }

        // Reset legacy fields in schools table to defaults
        SchoolsTable.update({ SchoolsTable.id eq schoolId }) {
            it[logoUrl] = null
            it[brandColor] = "#2563EB"
            it[updatedAt] = now
        }

        val schoolName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq schoolId }
            .singleOrNull()?.get(SchoolsTable.name) ?: "Unknown School"

        val row = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .single()

        rowToDto(row, schoolName)
    }

    // ── Subdomain ─────────────────────────────────────────────────────────

    suspend fun checkSubdomainAvailable(schoolId: UUID, subdomain: String): Boolean = dbQuery {
        if (!subdomainRegex.matches(subdomain)) return@dbQuery false
        val existing = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.customSubdomain eq subdomain }
            .singleOrNull()
        existing == null || existing[SchoolBrandingTable.schoolId] == schoolId
    }

    suspend fun updateSubdomain(schoolId: UUID, subdomain: String): SubdomainResponse = dbQuery {
        if (!subdomainRegex.matches(subdomain)) {
            throw IllegalArgumentException("Invalid subdomain format")
        }
        // Check uniqueness
        val taken = SchoolBrandingTable.selectAll()
            .where {
                (SchoolBrandingTable.customSubdomain eq subdomain) and
                    (SchoolBrandingTable.schoolId neq schoolId)
            }
            .singleOrNull()
        if (taken != null) {
            throw IllegalStateException("Subdomain already taken")
        }

        val now = Instant.now()
        val existing = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .singleOrNull()

        if (existing != null) {
            SchoolBrandingTable.update(
                { SchoolBrandingTable.schoolId eq schoolId }
            ) {
                it[customSubdomain] = subdomain
                it[isCustomized] = true
                it[updatedAt] = now
            }
        } else {
            SchoolBrandingTable.insert {
                it[SchoolBrandingTable.schoolId] = schoolId
                it[customSubdomain] = subdomain
                it[isCustomized] = true
                it[createdAt] = now
                it[updatedAt] = now
            }
        }
        SubdomainResponse(subdomain)
    }

    suspend fun removeSubdomain(schoolId: UUID): Boolean = dbQuery {
        val now = Instant.now()
        val updated = SchoolBrandingTable.update(
            { SchoolBrandingTable.schoolId eq schoolId }
        ) {
            it[customSubdomain] = null
            it[updatedAt] = now
        }
        updated > 0
    }

    suspend fun resolveSubdomain(subdomain: String): SubdomainResolutionDto? = dbQuery {
        val row = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.customSubdomain eq subdomain }
            .singleOrNull() ?: return@dbQuery null

        val schoolId = row[SchoolBrandingTable.schoolId]
        val schoolRow = SchoolsTable.selectAll()
            .where { (SchoolsTable.id eq schoolId) and (SchoolsTable.isActive eq true) }
            .singleOrNull() ?: return@dbQuery null

        val schoolName = schoolRow[SchoolsTable.name]

        SubdomainResolutionDto(
            schoolId = schoolId.toString(),
            schoolName = schoolName,
            branding = rowToDto(row, schoolName),
        )
    }

    // ── Asset Upload (M-1: FR-001) ───────────────────────────────────────

    private val assetFields = mapOf(
        "logo" to SchoolBrandingTable.logoUrl,
        "logo_dark" to SchoolBrandingTable.logoDarkUrl,
        "favicon" to SchoolBrandingTable.faviconUrl,
        "app_icon" to SchoolBrandingTable.appIconUrl,
        "splash_screen" to SchoolBrandingTable.splashScreenUrl,
        "login_background" to SchoolBrandingTable.loginBackgroundUrl,
    )

    fun validAssetFields(): Set<String> = assetFields.keys

    /**
     * Upload a branding asset (logo, favicon, app icon, splash, etc.) to Supabase
     * Storage and atomically update the matching column in school_branding.
     *
     * @param schoolId  tenant scope
     * @param field     one of [validAssetFields]
     * @param bytes     file content
     * @param contentType MIME type from multipart part
     * @return updated branding DTO, or null if storage isn't configured
     */
    suspend fun uploadAsset(
        schoolId: UUID,
        field: String,
        bytes: ByteArray,
        contentType: String,
    ): SchoolBrandingDto? {
        val column = assetFields[field]
            ?: throw IllegalArgumentException("Invalid asset field: $field")

        if (!SupabaseStorage.isConfigured()) return null

        // Upload to Supabase under BRANDING kind for path isolation
        val result = SupabaseStorage.upload(schoolId, "BRANDING", bytes, contentType)
            ?: return null

        // Delete the old asset if it was a Supabase URL (prevent orphaned bytes)
        val oldRow = dbQuery {
            SchoolBrandingTable.selectAll()
                .where { SchoolBrandingTable.schoolId eq schoolId }
                .singleOrNull()
        }
        oldRow?.getOrNull(column)?.let { oldUrl ->
            SupabaseStorage.objectPathFromPublicUrl(oldUrl)?.let { path ->
                SupabaseStorage.delete(path)
            }
        }

        // Update the branding row with the new URL
        return dbQuery {
            val now = Instant.now()
            val existing = SchoolBrandingTable.selectAll()
                .where { SchoolBrandingTable.schoolId eq schoolId }
                .singleOrNull()

            if (existing != null) {
                SchoolBrandingTable.update(
                    { SchoolBrandingTable.schoolId eq schoolId }
                ) {
                    it[column] = result.url
                    it[isCustomized] = true
                    it[updatedAt] = now
                }
            } else {
                SchoolBrandingTable.insert {
                    it[SchoolBrandingTable.schoolId] = schoolId
                    it[column] = result.url
                    it[isCustomized] = true
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            }

            // Sync logo to schools table if the uploaded asset is the logo
            if (field == "logo") {
                SchoolsTable.update({ SchoolsTable.id eq schoolId }) {
                    it[logoUrl] = result.url
                    it[updatedAt] = now
                }
            }

            val schoolName = SchoolsTable.selectAll()
                .where { SchoolsTable.id eq schoolId }
                .singleOrNull()?.get(SchoolsTable.name) ?: "Unknown School"

            SchoolBrandingTable.selectAll()
                .where { SchoolBrandingTable.schoolId eq schoolId }
                .single().let { rowToDto(it, schoolName) }
        }
    }

    /**
     * Remove a specific branding asset (set the column to null and delete from storage).
     */
    suspend fun deleteAsset(schoolId: UUID, field: String): SchoolBrandingDto = dbQuery {
        val column = assetFields[field]
            ?: throw IllegalArgumentException("Invalid asset field: $field")

        val now = Instant.now()
        val existing = SchoolBrandingTable.selectAll()
                .where { SchoolBrandingTable.schoolId eq schoolId }
                .singleOrNull()

        if (existing != null) {
            // Best-effort delete from storage
            existing.getOrNull(column)?.let { oldUrl ->
                SupabaseStorage.objectPathFromPublicUrl(oldUrl)?.let { path ->
                    SupabaseStorage.delete(path)
                }
            }
            SchoolBrandingTable.update(
                { SchoolBrandingTable.schoolId eq schoolId }
            ) {
                it[column] = null
                it[updatedAt] = now
            }
        }

        // Sync logo removal to schools table if the deleted asset is the logo
        if (field == "logo") {
            SchoolsTable.update({ SchoolsTable.id eq schoolId }) {
                it[logoUrl] = null
                it[updatedAt] = now
            }
        }

        val schoolName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq schoolId }
            .singleOrNull()?.get(SchoolsTable.name) ?: "Unknown School"

        SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .singleOrNull()?.let { rowToDto(it, schoolName) } ?: defaultDto(schoolId, schoolName)
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun requireHex(value: String, field: String) {
        if (!hexRegex.matches(value)) {
            throw IllegalArgumentException("Invalid hex color for $field: $value")
        }
    }

    private fun requireNonEmptyUrl(value: String, field: String) {
        if (value.isBlank()) {
            throw IllegalArgumentException("URL for $field cannot be empty")
        }
    }

    private fun rowToDto(row: ResultRow, schoolName: String): SchoolBrandingDto = SchoolBrandingDto(
        schoolId = row[SchoolBrandingTable.schoolId].toString(),
        schoolName = schoolName,
        logoUrl = row[SchoolBrandingTable.logoUrl],
        logoDarkUrl = row[SchoolBrandingTable.logoDarkUrl],
        faviconUrl = row[SchoolBrandingTable.faviconUrl],
        appIconUrl = row[SchoolBrandingTable.appIconUrl],
        splashScreenUrl = row[SchoolBrandingTable.splashScreenUrl],
        primaryColor = row[SchoolBrandingTable.primaryColor],
        secondaryColor = row[SchoolBrandingTable.secondaryColor],
        accentColor = row[SchoolBrandingTable.accentColor],
        customSubdomain = row[SchoolBrandingTable.customSubdomain],
        loginBackgroundUrl = row[SchoolBrandingTable.loginBackgroundUrl],
        isCustomized = row[SchoolBrandingTable.isCustomized],
    )

    private fun defaultDto(schoolId: UUID, schoolName: String): SchoolBrandingDto = SchoolBrandingDto(
        schoolId = schoolId.toString(),
        schoolName = schoolName,
        primaryColor = "#2563EB",
        secondaryColor = "#1E40AF",
        accentColor = "#3B82F6",
        isCustomized = false,
    )
}
