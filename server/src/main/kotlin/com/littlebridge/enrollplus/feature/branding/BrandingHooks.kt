package com.littlebridge.enrollplus.feature.branding

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SchoolBrandingTable
import com.littlebridge.enrollplus.db.SchoolsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

/**
 * Branding hooks for downstream features that need school branding data.
 *
 * These are read-only stubs that resolve branding for a given school.
 * Currently used by:
 * - Report card header rendering (logo + school name + brand colors)
 * - Email template branding (logo + brand color in HTML email header)
 *
 * Future: PDF generation, certificate generation, notification templates.
 */
@Serializable
data class BrandingHeader(
    val schoolName: String,
    val logoUrl: String?,
    val primaryColor: String,
    val secondaryColor: String,
    val accentColor: String,
)

object BrandingHooks {

    /**
     * Resolve branding header data for a school.
     * Falls back to school table defaults if no branding row exists.
     */
    suspend fun getHeader(schoolId: UUID): BrandingHeader = dbQuery {
        val school = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq schoolId }
            .singleOrNull()

        val branding = SchoolBrandingTable.selectAll()
            .where { SchoolBrandingTable.schoolId eq schoolId }
            .singleOrNull()

        BrandingHeader(
            schoolName = school?.get(SchoolsTable.name) ?: "Unknown School",
            logoUrl = branding?.get(SchoolBrandingTable.logoUrl)
                ?: school?.get(SchoolsTable.logoUrl),
            primaryColor = branding?.get(SchoolBrandingTable.primaryColor)
                ?: school?.get(SchoolsTable.brandColor)
                ?: "#2563EB",
            secondaryColor = branding?.get(SchoolBrandingTable.secondaryColor) ?: "#1E40AF",
            accentColor = branding?.get(SchoolBrandingTable.accentColor) ?: "#3B82F6",
        )
    }

    /**
     * Build an HTML header block for branded emails.
     * Returns a self-contained HTML snippet that can be prepended to any email body.
     */
    fun emailHeaderHtml(header: BrandingHeader): String {
        val logoHtml = header.logoUrl?.let { url ->
            """<img src="$url" alt="${header.schoolName}" style="height:40px;margin-bottom:12px;object-fit:contain;"/>"""
        } ?: ""
        return """
            <div style="background:${header.primaryColor};padding:20px 24px;border-radius:12px 12px 0 0;text-align:center;">
                $logoHtml
                <h1 style="color:#ffffff;margin:0;font-size:20px;font-weight:600;">${header.schoolName}</h1>
            </div>
        """.trimIndent()
    }

    /**
     * Build a text header for report card documents.
     * Returns school name + branding metadata for PDF/print rendering.
     */
    fun reportCardHeaderText(header: BrandingHeader): String {
        return header.schoolName
    }
}
