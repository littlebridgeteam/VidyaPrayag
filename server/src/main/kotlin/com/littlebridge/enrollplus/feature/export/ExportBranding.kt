package com.littlebridge.enrollplus.feature.export

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SchoolBrandingTable
import com.littlebridge.enrollplus.db.SchoolsTable
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

data class ExportBranding(
    val schoolName: String,
    val logoUrl: String?,
    val address: String?,
    val city: String,
    val state: String,
    val pincode: String?,
    val contactPhone: String?,
    val primaryColor: String,
)

suspend fun getExportBranding(schoolId: UUID): ExportBranding = dbQuery {
    val school = SchoolsTable.selectAll()
        .where { SchoolsTable.id eq schoolId }
        .single()

    val branding = SchoolBrandingTable.selectAll()
        .where { SchoolBrandingTable.schoolId eq schoolId }
        .singleOrNull()

    ExportBranding(
        schoolName = school[SchoolsTable.name],
        logoUrl = branding?.get(SchoolBrandingTable.logoUrl) ?: school[SchoolsTable.logoUrl],
        address = school[SchoolsTable.fullAddress],
        city = school[SchoolsTable.city],
        state = school[SchoolsTable.state],
        pincode = school[SchoolsTable.pincode],
        contactPhone = school[SchoolsTable.contactPhone],
        primaryColor = branding?.get(SchoolBrandingTable.primaryColor) ?: school[SchoolsTable.brandColor],
    )
}
