package com.littlebridge.enrollplus.feature.branding

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

/**
 * Contract tests for the School Branding Kit.
 *
 * Guards against DTO drift, validates serialization shapes, and pins
 * the asset field set and subdomain regex — all without DB/network.
 */
class BrandingContractTest {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
    private val projectRoot = java.io.File(System.getProperty("user.dir")).parentFile
    private val serverMain = java.io.File(projectRoot, "server/src/main/kotlin/com/littlebridge/enrollplus")

    private fun source(relative: String): String {
        val f = java.io.File(serverMain, relative)
        assertTrue(f.exists(), "Expected source file to exist: ${f.path}")
        return f.readText()
    }

    // ── DTO Serialization ────────────────────────────────────────────────

    @Test
    fun schoolBrandingDto_serializesAllFields() {
        val dto = SchoolBrandingDto(
            schoolId = "school-123",
            schoolName = "Test School",
            logoUrl = "https://example.com/logo.png",
            logoDarkUrl = "https://example.com/logo-dark.png",
            faviconUrl = "https://example.com/favicon.ico",
            appIconUrl = "https://example.com/icon.png",
            splashScreenUrl = "https://example.com/splash.png",
            primaryColor = "#2563EB",
            secondaryColor = "#1E40AF",
            accentColor = "#3B82F6",
            customSubdomain = "testschool",
            loginBackgroundUrl = "https://example.com/bg.png",
            isCustomized = true,
        )
        val encoded = json.encodeToString(SchoolBrandingDto.serializer(), dto)

        // Server DTOs use camelCase (no @SerialName annotations)
        assertTrue(encoded.contains("\"schoolId\""), "Must serialize schoolId. Got: $encoded")
        assertTrue(encoded.contains("\"schoolName\""), "Must serialize schoolName. Got: $encoded")
        assertTrue(encoded.contains("\"logoUrl\""), "Must serialize logoUrl. Got: $encoded")
        assertTrue(encoded.contains("\"logoDarkUrl\""), "Must serialize logoDarkUrl. Got: $encoded")
        assertTrue(encoded.contains("\"faviconUrl\""), "Must serialize faviconUrl. Got: $encoded")
        assertTrue(encoded.contains("\"appIconUrl\""), "Must serialize appIconUrl. Got: $encoded")
        assertTrue(encoded.contains("\"splashScreenUrl\""), "Must serialize splashScreenUrl. Got: $encoded")
        assertTrue(encoded.contains("\"primaryColor\""), "Must serialize primaryColor. Got: $encoded")
        assertTrue(encoded.contains("\"secondaryColor\""), "Must serialize secondaryColor. Got: $encoded")
        assertTrue(encoded.contains("\"accentColor\""), "Must serialize accentColor. Got: $encoded")
        assertTrue(encoded.contains("\"customSubdomain\""), "Must serialize customSubdomain. Got: $encoded")
        assertTrue(encoded.contains("\"loginBackgroundUrl\""), "Must serialize loginBackgroundUrl. Got: $encoded")
        assertTrue(encoded.contains("\"isCustomized\""), "Must serialize isCustomized. Got: $encoded")
    }

    @Test
    fun schoolBrandingDto_defaultsAreCorrect() {
        val dto = SchoolBrandingDto(
            schoolId = "s1",
            schoolName = "School",
        )
        assertEquals("#2563EB", dto.primaryColor)
        assertEquals("#1E40AF", dto.secondaryColor)
        assertEquals("#3B82F6", dto.accentColor)
        assertFalse(dto.isCustomized)
        assertNull(dto.logoUrl)
        assertNull(dto.splashScreenUrl)
        assertNull(dto.loginBackgroundUrl)
    }

    @Test
    fun updateBrandingRequest_allFieldsNullable() {
        val req = UpdateBrandingRequest()
        val encoded = json.encodeToString(UpdateBrandingRequest.serializer(), req)

        assertTrue(encoded.contains("\"primaryColor\":null"))
        assertTrue(encoded.contains("\"secondaryColor\":null"))
        assertTrue(encoded.contains("\"accentColor\":null"))
        assertTrue(encoded.contains("\"isCustomized\":null"))
    }

    @Test
    fun updateBrandingRequest_serializesColors() {
        val req = UpdateBrandingRequest(
            primaryColor = "#FF0000",
            secondaryColor = "#00FF00",
            accentColor = "#0000FF",
            isCustomized = true,
        )
        val encoded = json.encodeToString(UpdateBrandingRequest.serializer(), req)

        assertTrue(encoded.contains("\"primaryColor\":\"#FF0000\""))
        assertTrue(encoded.contains("\"secondaryColor\":\"#00FF00\""))
        assertTrue(encoded.contains("\"accentColor\":\"#0000FF\""))
        assertTrue(encoded.contains("\"isCustomized\":true"))
    }

    @Test
    fun subdomainRequest_serializesCorrectly() {
        val req = SubdomainRequest(subdomain = "dpsrkpuram")
        val encoded = json.encodeToString(SubdomainRequest.serializer(), req)

        assertTrue(encoded.contains("\"subdomain\":\"dpsrkpuram\""))
    }

    // ── Asset Field Validation ───────────────────────────────────────────

    @Test
    fun validAssetFields_containsAllExpectedFields() {
        val fields = BrandingService().validAssetFields()

        assertEquals(6, fields.size)
        assertTrue("logo" in fields)
        assertTrue("logo_dark" in fields)
        assertTrue("favicon" in fields)
        assertTrue("app_icon" in fields)
        assertTrue("splash_screen" in fields)
        assertTrue("login_background" in fields)
    }

    // ── Subdomain Regex ──────────────────────────────────────────────────

    @Test
    fun subdomainRegex_acceptsValidSubdomains() {
        val regex = BrandingService.SUBDOMAIN_REGEX

        assertTrue(regex.matches("dpsrkpuram"))
        assertTrue(regex.matches("dps-rk"))
        assertTrue(regex.matches("abc123"))
        assertTrue(regex.matches("a-b-c-123"))
    }

    @Test
    fun subdomainRegex_rejectsInvalidSubdomains() {
        val regex = BrandingService.SUBDOMAIN_REGEX

        assertFalse(regex.matches("ab"), "Too short (< 4 chars)")
        assertFalse(regex.matches("-abc"), "Starts with hyphen")
        assertFalse(regex.matches("abc-"), "Ends with hyphen")
        assertFalse(regex.matches("ABC"), "Uppercase not allowed")
        assertFalse(regex.matches("a_b"), "Underscore not allowed")
        assertFalse(regex.matches("a.b"), "Dot not allowed")
    }

    // ── BrandingHooks ────────────────────────────────────────────────────

    @Test
    fun brandingHooks_emailHeaderHtml_containsSchoolName() {
        val header = BrandingHeader(
            schoolName = "Test School",
            logoUrl = null,
            primaryColor = "#2563EB",
            secondaryColor = "#1E40AF",
            accentColor = "#3B82F6",
        )
        val html = BrandingHooks.emailHeaderHtml(header)

        assertTrue(html.contains("Test School"))
        assertTrue(html.contains("#2563EB"))
        assertTrue(html.contains("<div"))
    }

    @Test
    fun brandingHooks_emailHeaderHtml_includesLogoWhenPresent() {
        val header = BrandingHeader(
            schoolName = "Test School",
            logoUrl = "https://example.com/logo.png",
            primaryColor = "#2563EB",
            secondaryColor = "#1E40AF",
            accentColor = "#3B82F6",
        )
        val html = BrandingHooks.emailHeaderHtml(header)

        assertTrue(html.contains("https://example.com/logo.png"))
        assertTrue(html.contains("<img"))
    }

    @Test
    fun brandingHooks_reportCardHeaderText_returnsSchoolName() {
        val header = BrandingHeader(
            schoolName = "Delhi Public School",
            logoUrl = null,
            primaryColor = "#2563EB",
            secondaryColor = "#1E40AF",
            accentColor = "#3B82F6",
        )
        assertEquals("Delhi Public School", BrandingHooks.reportCardHeaderText(header))
    }

    // ── Source-level guards (pin endpoints exist in routing) ─────────────

    @Test
    fun brandingRouting_hasAssetUploadEndpoint() {
        val src = source("feature/branding/BrandingRouting.kt")

        assertTrue(src.contains("post(\"/assets\")"), "Must have POST /assets endpoint")
        assertTrue(src.contains("delete(\"/assets\")"), "Must have DELETE /assets endpoint")
        assertTrue(src.contains("receiveMultipart"), "Must use multipart for asset upload")
    }

    @Test
    fun brandingRouting_hasNotifyBrandingChanged() {
        val src = source("feature/branding/BrandingRouting.kt")

        assertTrue(src.contains("notifyBrandingChanged"), "Must have notifyBrandingChanged helper")
        assertTrue(src.contains("Notify.toUsers"), "Must call Notify.toUsers")
        assertTrue(src.contains("\"BRANDING\""), "Must use BRANDING category")
    }

    @Test
    fun brandingRouting_emitsNotifyOnAllMutations() {
        val src = source("feature/branding/BrandingRouting.kt")

        assertTrue(src.contains("notifyBrandingChanged(ctx.schoolId, ctx.userId, \"Branding colors updated\")"))
        assertTrue(src.contains("notifyBrandingChanged(ctx.schoolId, ctx.userId, \"Branding reset to defaults\")"))
        assertTrue(src.contains("notifyBrandingChanged(ctx.schoolId, ctx.userId, \"Branding asset uploaded"))
        assertTrue(src.contains("notifyBrandingChanged(ctx.schoolId, ctx.userId, \"Branding asset removed"))
    }

    @Test
    fun brandingService_hasUploadAndDeleteAssetMethods() {
        val src = source("feature/branding/BrandingService.kt")

        assertTrue(src.contains("suspend fun uploadAsset"), "Must have uploadAsset method")
        assertTrue(src.contains("suspend fun deleteAsset"), "Must have deleteAsset method")
        assertTrue(src.contains("SupabaseStorage.upload"), "Must call SupabaseStorage.upload")
        assertTrue(src.contains("SupabaseStorage.delete"), "Must call SupabaseStorage.delete for cleanup")
    }

    @Test
    fun schoolBrandingTable_hasOrganizationIdColumn() {
        val src = source("db/Tables.kt")

        assertTrue(src.contains("val organizationId"), "SchoolBrandingTable must have organizationId")
        assertTrue(src.contains("idx_school_branding_org"), "Must have org index")
    }

    @Test
    fun brandingHooks_fileExists() {
        val f = java.io.File(serverMain, "feature/branding/BrandingHooks.kt")
        assertTrue(f.exists(), "BrandingHooks.kt must exist")
        val src = f.readText()
        assertTrue(src.contains("object BrandingHooks"), "Must define BrandingHooks object")
        assertTrue(src.contains("fun getHeader"), "Must have getHeader function")
        assertTrue(src.contains("fun emailHeaderHtml"), "Must have emailHeaderHtml function")
    }

    @Test
    fun migration_052_exists() {
        val f = java.io.File(projectRoot, "docs/db/migration_052_branding_org.sql")
        assertTrue(f.exists(), "migration_052_branding_org.sql must exist at ${f.path}")
        val sql = f.readText()
        assertTrue(sql.contains("organization_id"), "Migration must add organization_id column")
        assertTrue(sql.contains("idx_school_branding_org"), "Migration must create org index")
    }
}
