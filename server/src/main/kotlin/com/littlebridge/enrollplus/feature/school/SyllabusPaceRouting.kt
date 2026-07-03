/*
 * File: SyllabusPaceRouting.kt
 * Module: feature.school
 *
 * Admin-facing endpoints for syllabus pace monitoring.
 *   GET  /api/v1/school/syllabus-pace/snapshots       — all pace snapshots for the school
 *   GET  /api/v1/school/syllabus-pace/alerts          — active pace alerts
 *   POST /api/v1/school/syllabus-pace/alerts/{id}/resolve — resolve an alert
 *   GET  /api/v1/school/syllabus-pace/coverage?classId=&section= — per-subject coverage
 *   POST /api/v1/school/syllabus-pace/recalculate — manually trigger pace recalculation
 */
package com.littlebridge.enrollplus.feature.school

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.requireSchoolAdmin
import com.littlebridge.enrollplus.feature.ai.SyllabusPaceService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.syllabusPaceRouting() {
    authenticate("jwt") {
        route("/api/v1/school/syllabus-pace") {
            paceSnapshots()
            paceAlerts()
            paceAlertResolve()
            paceCoverage()
            paceRecalculate()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/school/syllabus-pace/snapshots   — all pace snapshots for the school
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.paceSnapshots() {
    get("/snapshots") {
        val ctx = call.requireSchoolAdmin() ?: return@get
        val snapshots = SyllabusPaceService.snapshotsForSchool(ctx.schoolId)
        call.ok(
            SyllabusPaceService.PaceSnapshotsDto(snapshots = snapshots),
            message = "Pace snapshots loaded",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/school/syllabus-pace/alerts   — active pace alerts for the school
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.paceAlerts() {
    get("/alerts") {
        val ctx = call.requireSchoolAdmin() ?: return@get
        val alerts = SyllabusPaceService.activeAlertsForSchool(ctx.schoolId)
        call.ok(
            SyllabusPaceService.AlertsDto(alerts = alerts),
            message = "Active alerts loaded",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/school/syllabus-pace/alerts/{id}/resolve   — resolve an active alert
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.paceAlertResolve() {
    post("/alerts/{id}/resolve") {
        val ctx = call.requireSchoolAdmin() ?: return@post
        val alertId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
            call.fail("A valid alert id is required", HttpStatusCode.BadRequest, "BAD_ALERT_ID"); return@post
        }
        val resolved = SyllabusPaceService.resolveAlert(alertId, ctx.schoolId)
        if (!resolved) {
            call.fail("Alert not found or already resolved", HttpStatusCode.NotFound, "ALERT_NOT_FOUND"); return@post
        }
        call.okMessage("Alert resolved")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/school/syllabus-pace/coverage?classId=&section=   — per-subject coverage
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.paceCoverage() {
    get("/coverage") {
        val ctx = call.requireSchoolAdmin() ?: return@get
        val classIdStr = call.request.queryParameters["classId"]
            ?: call.request.queryParameters["class_id"]
        val section = call.request.queryParameters["section"] ?: "A"

        val snapshots = SyllabusPaceService.snapshotsForSchool(ctx.schoolId)
        val filtered = if (classIdStr != null) {
            snapshots.filter { s ->
                s.className.equals(classIdStr, ignoreCase = true) &&
                    s.section.equals(section, ignoreCase = true)
            }
        } else snapshots

        call.ok(
            SyllabusPaceService.PaceSnapshotsDto(snapshots = filtered),
            message = "Coverage loaded",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
// POST /api/v1/school/syllabus-pace/recalculate   — manually trigger pace recalculation
// ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
private fun Route.paceRecalculate() {
    post("/recalculate") {
        val ctx = call.requireSchoolAdmin() ?: return@post
        val results = SyllabusPaceService.recalcForSchool(ctx.schoolId)
        call.ok(
            SyllabusPaceService.PaceSnapshotsDto(snapshots = results),
            message = "Pace recalculated for ${results.size} assignments",
        )
    }
}
