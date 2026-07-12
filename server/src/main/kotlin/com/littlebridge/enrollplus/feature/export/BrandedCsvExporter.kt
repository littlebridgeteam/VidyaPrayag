package com.littlebridge.enrollplus.feature.export

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object BrandedCsvExporter {

    fun generate(
        branding: ExportBranding,
        title: String,
        subtitle: String,
        columns: List<String>,
        rows: List<List<String>>,
    ): String {
        val sb = StringBuilder()

        // ── Metadata header rows (prefixed with #) ──
        sb.append("# ").append(branding.schoolName).append("\n")
        if (!branding.address.isNullOrBlank()) {
            sb.append("# ").append(branding.address).append("\n")
        }
        val cityLine = listOfNotNull(branding.city, branding.state, branding.pincode)
            .joinToString(", ")
        if (cityLine.isNotBlank()) {
            sb.append("# ").append(cityLine).append("\n")
        }
        if (!branding.contactPhone.isNullOrBlank()) {
            sb.append("# Phone: ").append(branding.contactPhone).append("\n")
        }
        val fullTitle = if (subtitle.isNotBlank()) "$title — $subtitle" else title
        sb.append("# Report: ").append(fullTitle).append("\n")
        sb.append("# Generated: ")
            .append(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .append("\n")
        sb.append("#\n")

        // ── CSV header row ──
        sb.append(columns.joinToString(",") { escapeCsv(it) }).append("\n")

        // ── Data rows ──
        for (row in rows) {
            sb.append(row.joinToString(",") { escapeCsv(it) }).append("\n")
        }

        return sb.toString()
    }

    private fun escapeCsv(value: String): String {
        if (value.isEmpty()) return ""
        val needsQuoting = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuoting) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
