/*
 * File: TeacherSyllabusRouting.kt
 * Module: feature.teacher
 *
 * T-402/T-403 (Doc 08 §1.2/§2/§3) — the canonical, typed, lifecycle-aware
 * SYLLABUS plane. It REPLACED the legacy free-text `/syllabus` GET+PATCH handlers
 * that lived in TeacherRoutingTasks.kt (which supported only a coverage toggle —
 * NO create, the B-SYL-1 dead-empty defect; and scoped by free-text
 * class_name/subject, D-SYL-3). Those legacy handlers are DELETED (T-403) and this
 * plane now OWNS the canonical `/api/v1/teacher/syllabus` paths.
 *
 * THE TEMPLATE/PROGRESS SPLIT LIVES HERE (Doc 08 §1.2). A unit is a row in
 * `curriculum_units` (the template, per class+subject, hierarchical chapter ▸
 * topic). Its per-section COVERAGE is a row in `syllabus_progress`, keyed UNIQUE
 * on (unit, section, assignment) so two sections of the same class track coverage
 * independently. This plane:
 *   • GET    …/syllabus?assignmentId=  → units + this assignment's coverage,
 *                                        hierarchical, with covered/total counts.
 *   • POST   …/syllabus/units          → create a chapter or topic (B-SYL-1 fix).
 *   • PATCH  …/syllabus/units/{id}     → rename / reorder (edit-mode).
 *   • PATCH  …/syllabus/progress       → the ONE-TAP toggle. Idempotent upsert;
 *                                        stamps typed covered_on (D-SYL-2/X-3) +
 *                                        covered_by; section comes from the
 *                                        authorizing assignment, never the client.
 *
 * Every read and write is scope-bound to the authorizing teacher_subject_assignment
 * via requireOwnedAssignment (X-1) — never a free-text class/section/subject. The
 * unit's class_id + subject_id MUST match the owned assignment's typed scope, so a
 * teacher cannot toggle/rename/create against a curriculum row outside their
 * allocation even if they guess its id. Scoping is enforced at THREE levels (the
 * constitution): the SQL only ever touches owned curriculum + this assignment's
 * progress (query), the response only carries that scope (API), and the screen
 * reaches this pre-scoped (UI, T-403).
 *
 * PATH NOTE (converged, T-403):
 *   This plane was staged under a temporary `/api/v1/teacher/syllabus`
 *   prefix in T-402 to avoid colliding with the legacy `teacherTaskRoutes()`
 *   `GET/PATCH /api/v1/teacher/syllabus` (Ktor forbids two handlers on the same
 *   method+path) — the T-203 `/attendance-typed`→`/attendance` and T-303
 *   `/gradebook`→`/assessments` staging precedent. T-403 DELETED the legacy
 *   handler and CONVERGED this plane to the canonical `/api/v1/teacher/syllabus`
 *   paths from Doc 08 §3; the shared TeacherApi client points here.
 *
 * DTOs are defined server-side (the :server module does NOT depend on :shared)
 * and mirror shared/.../teacher/domain/model/TeacherModels.kt field-for-field
 * (SyllabusLoadResponse/SyllabusLoadDto/SyllabusNodeDto/CreateSyllabusUnitRequest/
 *  UpdateSyllabusUnitRequest/ToggleSyllabusProgressRequest/SyllabusUnitMutationResponse).
 */
package com.littlebridge.enrollplus.feature.teacher

import com.littlebridge.enrollplus.core.OwnedAssignment
import com.littlebridge.enrollplus.core.TeacherContext
import com.littlebridge.enrollplus.core.created
import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.requireOwnedAssignment
import com.littlebridge.enrollplus.core.requireTeacherContext
import com.littlebridge.enrollplus.db.CurriculumUnitsTable
import com.littlebridge.enrollplus.db.DailyClassLogTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SyllabusPopupPrefsTable
import com.littlebridge.enrollplus.db.SyllabusProgressTable
import com.littlebridge.enrollplus.db.SyllabusSourcesTable
import com.littlebridge.enrollplus.feature.ai.NcertReferenceService
import com.littlebridge.enrollplus.feature.ai.SyllabusAiService
import com.littlebridge.enrollplus.feature.ai.SyllabusPaceService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import org.slf4j.LoggerFactory

// ─────────────────────────────────────────────────────────────────────────────
// Server-side DTOs — mirror shared/.../teacher/domain/model/TeacherModels.kt
// (SyllabusLoadDto / SyllabusNodeDto / CreateSyllabusUnitRequest /
//  UpdateSyllabusUnitRequest / ToggleSyllabusProgressRequest /
//  SyllabusUnitMutationResponse) field-for-field.
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class SylNodeDto(
    val id: String,
    @SerialName("parent_id") val parentId: String? = null,
    val title: String,
    val position: Int = 0,
    val depth: Int = 0,
    @SerialName("is_chapter") val isChapter: Boolean = false,
    @SerialName("is_covered") val isCovered: Boolean = false,
    @SerialName("covered_on") val coveredOn: String? = null,
    val note: String? = null,
    @SerialName("approval_status") val approvalStatus: String = "APPROVED",
)

@Serializable
data class SylLoadDto(
    @SerialName("assignment_id") val assignmentId: String? = null,
    @SerialName("class_name") val className: String = "",
    val section: String = "",
    val subject: String = "",
    val units: List<SylNodeDto> = emptyList(),
    @SerialName("covered_count") val coveredCount: Int = 0,
    @SerialName("total_count") val totalCount: Int = 0,
)

@Serializable
data class SylCreateUnitRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    val title: String = "",
    @SerialName("parent_id") val parentId: String? = null,
)

@Serializable
data class SylUpdateUnitRequest(
    val title: String? = null,
    val position: Int? = null,
)

@Serializable
data class SylToggleProgressRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    @SerialName("unit_id") val unitId: String = "",
    @SerialName("is_covered") val isCovered: Boolean = false,
    @SerialName("covered_on") val coveredOn: String? = null,
    val note: String? = null,
    @SerialName("coverage_percent") val coveragePercent: Int? = null,
)

// (SylMutationData removed — unit create/update/toggle now return the SylNodeDto directly
//  via call.ok/created, letting the canonical envelope provide the single
//  { success, message, data } layer the client's SyllabusUnitMutationResponse expects.)

// ── Agentic Syllabus: Parse DTOs ────────────────────────────────────────────

@Serializable
data class SylParseRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    @SerialName("source_type") val sourceType: String = "",  // IMAGE | TEXT
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("raw_text") val rawText: String? = null,
)

@Serializable
data class SylParsedSubtopicDto(
    val title: String,
)

@Serializable
data class SylParsedTopicDto(
    val title: String,
    val subtopics: List<SylParsedSubtopicDto> = emptyList(),
)

@Serializable
data class SylParsedChapterDto(
    val title: String,
    val topics: List<SylParsedTopicDto> = emptyList(),
)

@Serializable
data class SylParseResultDto(
    val chapters: List<SylParsedChapterDto> = emptyList(),
    @SerialName("ai_provider") val aiProvider: String = "",
)

@Serializable
data class SylParseConfirmRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    val chapters: List<SylParsedChapterDto> = emptyList(),
)

@Serializable
data class SylParseConfirmResultDto(
    @SerialName("units_created") val unitsCreated: Int = 0,
)

// ── Agentic Syllabus: Auto-fill & Approval DTOs ─────────────────────────────

@Serializable
data class SylAutoFillRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
)

@Serializable
data class SylAutoFillChapterDto(
    val title: String,
    val topics: List<SylAutoFillTopicDto> = emptyList(),
)

@Serializable
data class SylAutoFillTopicDto(
    val title: String,
    val subtopics: List<SylAutoFillSubtopicDto> = emptyList(),
)

@Serializable
data class SylAutoFillSubtopicDto(
    val title: String,
)

@Serializable
data class SylAutoFillResultDto(
    val found: Boolean = false,
    val source: String = "",
    @SerialName("class_level") val classLevel: String = "",
    val subject: String = "",
    val chapters: List<SylAutoFillChapterDto> = emptyList(),
)

@Serializable
data class SylAutoFillConfirmRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    val chapters: List<SylAutoFillChapterDto> = emptyList(),
)

@Serializable
data class SylApproveRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    @SerialName("unit_ids") val unitIds: List<String> = emptyList(),
)

@Serializable
data class SylApproveResultDto(
    @SerialName("approved_count") val approvedCount: Int = 0,
)

@Serializable
data class SylRejectRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    @SerialName("unit_ids") val unitIds: List<String> = emptyList(),
)

@Serializable
data class SylPaceWarningDto(
    val level: String = "ON_TRACK",
    @SerialName("expected_pct") val expectedPct: Int = 0,
    @SerialName("actual_pct") val actualPct: Int = 0,
    @SerialName("deviation_pct") val deviationPct: Int = 0,
    val message: String = "",
    @SerialName("weekly_periods") val weeklyPeriods: Int = 0,
    @SerialName("classes_elapsed") val classesElapsed: Int = 0,
    @SerialName("classes_remaining") val classesRemaining: Int = 0,
    @SerialName("estimated_completion_date") val estimatedCompletionDate: String = "",
    @SerialName("topics_per_class") val topicsPerClass: Double = 0.0,
    @SerialName("holiday_days_counted") val holidayDaysCounted: Int = 0,
    @SerialName("avg_coverage_per_class") val avgCoveragePerClass: Double = 0.0,
)

// ── Agentic Syllabus: Daily Log DTOs ────────────────────────────────────────

@Serializable
data class SylDailyLogRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    val date: String = "",  // YYYY-MM-DD
    @SerialName("topic_ids") val topicIds: List<String> = emptyList(),
    @SerialName("summary_text") val summaryText: String = "",
    @SerialName("coverage_pct") val coveragePct: Int = 0,
)

@Serializable
data class SylDailyLogDto(
    val id: String,
    val date: String,
    @SerialName("topic_ids") val topicIds: List<String> = emptyList(),
    @SerialName("summary_text") val summaryText: String = "",
    @SerialName("coverage_pct") val coveragePct: Int = 0,
    val source: String = "TEACHER",
    @SerialName("is_ai_estimated") val isAiEstimated: Boolean = false,
)

@Serializable
data class SylDailyLogListDto(
    val logs: List<SylDailyLogDto> = emptyList(),
)

@Serializable
data class SylShouldShowPopupDto(
    @SerialName("should_show") val shouldShow: Boolean = false,
    val reason: String = "",
)

// ── Agentic Syllabus: Popup Prefs DTOs ──────────────────────────────────────

@Serializable
data class SylPopupPrefsRequest(
    @SerialName("assignment_id") val assignmentId: String = "",
    @SerialName("suppress_mode") val suppressMode: String = "off",  // off | week | permanent
)

@Serializable
data class SylPopupPrefsDto(
    @SerialName("suppress_mode") val suppressMode: String = "off",
    @SerialName("suppressed_until") val suppressedUntil: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Helpers.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Resolve a curriculum unit id to its row, asserting it belongs to the caller's
 * school AND to the SAME typed scope (class_id + subject_id) as the owned
 * assignment. This is the syllabus scope gate — a unit is reachable ONLY through
 * an owned assignment whose class+subject matches it (X-1). Responds + returns
 * null on 400/404/403.
 */
private suspend fun ApplicationCall.requireOwnedUnit(
    asg: OwnedAssignment,
    schoolId: UUID,
    unitId: String?,
): ResultRow? {
    val id = unitId?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
        fail("A valid unit id is required", HttpStatusCode.BadRequest, "BAD_UNIT_ID")
        return null
    }
    val row = dbQuery {
        CurriculumUnitsTable.selectAll().where {
            (CurriculumUnitsTable.id eq id) and
                (CurriculumUnitsTable.schoolId eq schoolId) and
                (CurriculumUnitsTable.isActive eq true)
        }.singleOrNull()
    } ?: run {
        fail("Syllabus unit not found in your school", HttpStatusCode.NotFound, "UNIT_NOT_FOUND")
        return null
    }
    // The unit's typed class+subject MUST equal the owned assignment's scope
    // (X-1). Guards against toggling/renaming a unit outside the allocation.
    val scopeMatches = asg.classId != null && asg.subjectId != null &&
        row[CurriculumUnitsTable.classId] == asg.classId &&
        row[CurriculumUnitsTable.subjectId] == asg.subjectId
    if (!scopeMatches) {
        fail("This unit is not in your assigned class/subject", HttpStatusCode.Forbidden, "NOT_IN_SCOPE")
        return null
    }
    return row
}

/**
 * Build the hierarchical, ordered node list for an owned assignment: every active
 * curriculum unit for the assignment's typed class+subject, each carrying its own
 * coverage state for THIS assignment's section. Chapters (parent_id null) first in
 * `position` order; their topics nested immediately after, also in `position`
 * order. depth 0 = chapter, 1 = topic (hierarchy is at most 2 deep, Doc 08).
 */
private suspend fun loadSyllabusNodes(asg: OwnedAssignment): List<SylNodeDto> {
    val classId = asg.classId ?: return emptyList()
    val subjectId = asg.subjectId ?: return emptyList()
    return dbQuery {
        val units = CurriculumUnitsTable.selectAll().where {
            (CurriculumUnitsTable.classId eq classId) and
                (CurriculumUnitsTable.subjectId eq subjectId) and
                (CurriculumUnitsTable.isActive eq true)
        }.orderBy(CurriculumUnitsTable.position, SortOrder.ASC).toList()
        if (units.isEmpty()) return@dbQuery emptyList<SylNodeDto>()

        // This assignment's coverage rows for those units (section-scoped).
        val unitIds = units.map { it[CurriculumUnitsTable.id].value }
        val progressByUnit = SyllabusProgressTable.selectAll().where {
            (SyllabusProgressTable.assignmentId eq asg.assignmentId) and
                (SyllabusProgressTable.section eq asg.section) and
                (SyllabusProgressTable.unitId inList unitIds)
        }.associateBy { it[SyllabusProgressTable.unitId] }

        fun nodeOf(row: ResultRow, depth: Int): SylNodeDto {
            val uid = row[CurriculumUnitsTable.id].value
            val prog = progressByUnit[uid]
            return SylNodeDto(
                id = uid.toString(),
                parentId = row[CurriculumUnitsTable.parentId]?.toString(),
                title = row[CurriculumUnitsTable.title],
                position = row[CurriculumUnitsTable.position],
                depth = depth,
                isChapter = row[CurriculumUnitsTable.parentId] == null,
                isCovered = prog?.get(SyllabusProgressTable.isCovered) ?: false,
                coveredOn = prog?.get(SyllabusProgressTable.coveredOn)?.toString(),
                note = prog?.get(SyllabusProgressTable.note),
                approvalStatus = row[CurriculumUnitsTable.approvalStatus],
            )
        }

        // Chapters in order, each followed by its topics in order, each topic
        // followed by its subtopics in order. 3-level hierarchy (depth 0/1/2).
        // Orphan units (parent missing/inactive) sink to the end.
        val byParent = units.groupBy { it[CurriculumUnitsTable.parentId] }
        val chapters = (byParent[null] ?: emptyList())
            .sortedBy { it[CurriculumUnitsTable.position] }
        val result = mutableListOf<SylNodeDto>()
        for (chapter in chapters) {
            result += nodeOf(chapter, depth = 0)
            val cid = chapter[CurriculumUnitsTable.id].value
            val topics = (byParent[cid] ?: emptyList())
                .sortedBy { it[CurriculumUnitsTable.position] }
            for (topic in topics) {
                result += nodeOf(topic, depth = 1)
                val tid = topic[CurriculumUnitsTable.id].value
                (byParent[tid] ?: emptyList())
                    .sortedBy { it[CurriculumUnitsTable.position] }
                    .forEach { result += nodeOf(it, depth = 2) }
            }
        }
        // Topics whose parent isn't an active chapter in this scope → append flat.
        val placed = result.map { it.id }.toSet()
        units.filter { it[CurriculumUnitsTable.id].value.toString() !in placed }
            .sortedBy { it[CurriculumUnitsTable.position] }
            .forEach { result += nodeOf(it, depth = it[CurriculumUnitsTable.depth]) }
        result
    }
}

/** Re-read one unit as a node DTO for the mutation response (covered state for this section). */
private suspend fun nodeForUnit(asg: OwnedAssignment, unitId: UUID): SylNodeDto? = dbQuery {
    val row = CurriculumUnitsTable.selectAll().where {
        CurriculumUnitsTable.id eq unitId
    }.singleOrNull() ?: return@dbQuery null
    val prog = SyllabusProgressTable.selectAll().where {
        (SyllabusProgressTable.unitId eq unitId) and
            (SyllabusProgressTable.assignmentId eq asg.assignmentId) and
            (SyllabusProgressTable.section eq asg.section)
    }.singleOrNull()
    SylNodeDto(
        id = unitId.toString(),
        parentId = row[CurriculumUnitsTable.parentId]?.toString(),
        title = row[CurriculumUnitsTable.title],
        position = row[CurriculumUnitsTable.position],
        depth = row[CurriculumUnitsTable.depth],
        isChapter = row[CurriculumUnitsTable.parentId] == null,
        isCovered = prog?.get(SyllabusProgressTable.isCovered) ?: false,
        coveredOn = prog?.get(SyllabusProgressTable.coveredOn)?.toString(),
        note = prog?.get(SyllabusProgressTable.note),
        approvalStatus = row[CurriculumUnitsTable.approvalStatus],
    )
}

fun Route.teacherSyllabusRouting() {
    authenticate("jwt") {
        // CONVERGED (T-403) to the canonical `/api/v1/teacher/syllabus` paths
        // (Doc 08 §3). The legacy `teacherTaskRoutes()` `/syllabus` GET+PATCH
        // handlers have been DELETED, so this typed plane now solely OWNS this
        // method+path space — the T-203/T-303 convergence precedent.
        route("/api/v1/teacher/syllabus") {
            syllabusLoad()
            syllabusCreateUnit()
            syllabusUpdateUnit()
            syllabusDeleteUnit()
            syllabusToggleProgress()
            syllabusParse()
            syllabusParseConfirm()
            syllabusAutoFill()
            syllabusAutoFillConfirm()
            syllabusApprove()
            syllabusReject()
            syllabusPaceWarning()
            syllabusDailyLogCreate()
            syllabusDailyLogList()
            syllabusDailyLogShouldShow()
            syllabusPopupPrefsSet()
            syllabusPopupPrefsGet()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/teacher/syllabus?assignmentId=   (units + progress, hierarchical)
// Doc 08 §3.
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusLoad() {
    get {
        val ctx = call.requireTeacherContext() ?: return@get
        val assignmentParam = call.request.queryParameters["assignmentId"]
            ?: call.request.queryParameters["assignment_id"]
        val asg = call.requireOwnedAssignment(ctx, assignmentParam) ?: return@get

        val nodes = loadSyllabusNodes(asg)
        val covered = nodes.count { it.isCovered }
        call.ok(
            SylLoadDto(
                assignmentId = asg.assignmentId.toString(),
                className = "${asg.className}-${asg.section}".trim('-'),
                section = asg.section,
                subject = asg.subject,
                units = nodes,
                coveredCount = covered,
                totalCount = nodes.size,
            ),
            message = if (nodes.isEmpty()) "No syllabus yet" else "Syllabus loaded",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/units   (create a chapter or topic) — B-SYL-1 fix.
// Doc 08 §3. parentId null → chapter; otherwise a topic under that chapter (the
// parent must be an owned chapter in the same scope).
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusCreateUnit() {
    post("/units") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylCreateUnitRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post
        // Capture the typed scope as non-null locals (smart-cast doesn't cross the
        // dbQuery closure boundary on a data-class property).
        val scopeClassId = asg.classId
        val scopeSubjectId = asg.subjectId
        if (scopeClassId == null || scopeSubjectId == null) {
            call.fail("This class is not fully configured yet", HttpStatusCode.Conflict, "CLASS_NOT_CONFIGURED")
            return@post
        }
        val title = req.title.trim()
        if (title.isBlank()) {
            call.fail("Unit title is required", HttpStatusCode.BadRequest, "BAD_TITLE"); return@post
        }

        // If parentId given, it must be an owned chapter (parent_id null) in the
        // same typed scope — a topic cannot nest under a topic (≤2 deep, Doc 08).
        val parentUuid: UUID? = if (!req.parentId.isNullOrBlank()) {
            val parentRow = call.requireOwnedUnit(asg, ctx.schoolId, req.parentId) ?: return@post
            if (parentRow[CurriculumUnitsTable.parentId] != null) {
                call.fail("A topic cannot be nested under another topic", HttpStatusCode.BadRequest, "PARENT_NOT_CHAPTER")
                return@post
            }
            parentRow[CurriculumUnitsTable.id].value
        } else null

        val now = Instant.now()
        val newId = UUID.randomUUID()
        dbQuery {
            // Append at the end of its sibling group (max position + 1).
            val siblingMax = CurriculumUnitsTable.selectAll().where {
                (CurriculumUnitsTable.classId eq scopeClassId) and
                    (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                    (CurriculumUnitsTable.isActive eq true) and
                    (if (parentUuid == null) CurriculumUnitsTable.parentId.isNull()
                    else (CurriculumUnitsTable.parentId eq parentUuid))
            }.maxOfOrNull { it[CurriculumUnitsTable.position] } ?: -1

            CurriculumUnitsTable.insert {
                it[id] = newId
                it[schoolId] = ctx.schoolId
                it[classId] = scopeClassId
                it[subjectId] = scopeSubjectId
                it[parentId] = parentUuid
                it[CurriculumUnitsTable.title] = title
                it[position] = siblingMax + 1
                it[isActive] = true
                it[createdAt] = now
                it[updatedAt] = now
            }
        }
        val node = nodeForUnit(asg, newId)
        if (node == null) {
            call.fail("Unit created but could not be reloaded", HttpStatusCode.InternalServerError, "RELOAD_FAILED")
            return@post
        }
        // Pass the node DIRECTLY to call.created — call.ok/created already wrap it in the
        // canonical { success, message, data } envelope, so the client's
        // SyllabusUnitMutationResponse.data resolves to the node. (Previously this wrapped
        // the node a SECOND time in SylMutationData, producing { data: { success, data: node } }
        // and the client crashed: "Fields [id, title] missing at path $.data".)
        call.created(
            node,
            message = if (parentUuid == null) "Chapter added" else "Topic added",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PATCH /api/v1/teacher/syllabus/units/{id}   (rename / reorder)
// Doc 08 §3.
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusUpdateUnit() {
    patch("/units/{id}") {
        val ctx = call.requireTeacherContext() ?: return@patch
        // We need an owned assignment to scope the unit. The unit's scope is
        // derived from itself, but ownership is proven via an assignment the
        // caller holds for that class+subject. Resolve via assignmentId query/body
        // if present, else fall back to matching one of the caller's assignments.
        val assignmentParam = call.request.queryParameters["assignmentId"]
            ?: call.request.queryParameters["assignment_id"]
        val req = runCatching { call.receive<SylUpdateUnitRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@patch
        }
        val asg = call.requireOwnedAssignment(ctx, assignmentParam) ?: return@patch
        val unitRow = call.requireOwnedUnit(asg, ctx.schoolId, call.parameters["id"]) ?: return@patch
        val unitId = unitRow[CurriculumUnitsTable.id].value

        val newTitle = req.title?.trim()?.takeIf { it.isNotBlank() }
        if (req.title != null && newTitle == null) {
            call.fail("Unit title cannot be blank", HttpStatusCode.BadRequest, "BAD_TITLE"); return@patch
        }
        if (newTitle == null && req.position == null) {
            call.fail("Nothing to update", HttpStatusCode.BadRequest, "NO_OP"); return@patch
        }

        val now = Instant.now()
        dbQuery {
            CurriculumUnitsTable.update({ CurriculumUnitsTable.id eq unitId }) {
                if (newTitle != null) it[title] = newTitle
                if (req.position != null) it[position] = req.position.coerceAtLeast(0)
                it[updatedAt] = now
            }
        }
        val node = nodeForUnit(asg, unitId)
        if (node == null) {
            call.fail("Unit updated but could not be reloaded", HttpStatusCode.InternalServerError, "RELOAD_FAILED")
            return@patch
        }
        // Direct payload — single envelope (see syllabusCreateUnit for the double-wrap bug fixed here).
        call.ok(node, message = "Unit updated")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PATCH /api/v1/teacher/syllabus/progress   (the ONE-TAP toggle)
// Doc 08 §3. Idempotent upsert on (unit, section, assignment); stamps typed
// covered_on (today unless an explicit past date is given) + covered_by.
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusToggleProgress() {
    patch("/progress") {
        val ctx = call.requireTeacherContext() ?: return@patch
        val req = runCatching { call.receive<SylToggleProgressRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@patch
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@patch
        val unitRow = call.requireOwnedUnit(asg, ctx.schoolId, req.unitId) ?: return@patch
        val unitId = unitRow[CurriculumUnitsTable.id].value

        // Section is the AUTHORITATIVE one from the owned assignment, never the
        // client (X-1) — a teacher can only mark coverage for the section they own.
        val section = asg.section.ifBlank { "A" }

        // covered_on: when covering, use the explicit (past) date if valid, else
        // today (typed, D-SYL-2). When un-covering, clear it.
        val coveredOn: LocalDate? = if (!req.isCovered) {
            null
        } else {
            req.coveredOn?.takeIf { it.isNotBlank() }?.let {
                runCatching { LocalDate.parse(it) }.getOrNull() ?: run {
                    call.fail("covered_on must be YYYY-MM-DD", HttpStatusCode.BadRequest, "BAD_DATE")
                    return@patch
                }
            } ?: todayIst()
        }
        // A future covered_on is nonsensical (you can't have covered it tomorrow).
        if (coveredOn != null && coveredOn.isAfter(todayIst())) {
            call.fail("covered_on cannot be in the future", HttpStatusCode.BadRequest, "DATE_FUTURE")
            return@patch
        }
        val note = req.note?.takeIf { it.isNotBlank() }
        val coveragePct = req.coveragePercent?.coerceIn(0, 100) ?: if (req.isCovered) 100 else 0
        val now = Instant.now()

        dbQuery {
            val existing = SyllabusProgressTable.selectAll().where {
                (SyllabusProgressTable.unitId eq unitId) and
                    (SyllabusProgressTable.section eq section) and
                    (SyllabusProgressTable.assignmentId eq asg.assignmentId)
            }.singleOrNull()

            if (existing != null) {
                SyllabusProgressTable.update({
                    SyllabusProgressTable.id eq existing[SyllabusProgressTable.id]
                }) {
                    it[isCovered] = req.isCovered
                    it[SyllabusProgressTable.coveredOn] = coveredOn
                    it[coveredBy] = if (req.isCovered) ctx.userId else null
                    if (note != null || !req.isCovered) it[SyllabusProgressTable.note] = note
                    it[coveragePercent] = coveragePct
                    it[updatedAt] = now
                }
            } else {
                SyllabusProgressTable.insert {
                    it[id] = UUID.randomUUID()
                    it[SyllabusProgressTable.unitId] = unitId
                    it[SyllabusProgressTable.section] = section
                    it[assignmentId] = asg.assignmentId
                    it[isCovered] = req.isCovered
                    it[SyllabusProgressTable.coveredOn] = coveredOn
                    it[coveredBy] = if (req.isCovered) ctx.userId else null
                    it[SyllabusProgressTable.note] = note
                    it[coveragePercent] = coveragePct
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            }
        }
        val node = nodeForUnit(asg, unitId)
        if (node == null) {
            call.fail("Progress saved but unit could not be reloaded", HttpStatusCode.InternalServerError, "RELOAD_FAILED")
            return@patch
        }
        // Direct payload — single envelope (see syllabusCreateUnit for the double-wrap bug fixed here).
        call.ok(
            node,
            message = if (req.isCovered) "Marked covered" else "Marked not covered",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DELETE /api/v1/teacher/syllabus/units/{id}   (soft-delete: isActive = false)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusDeleteUnit() {
    delete("/units/{id}") {
        val ctx = call.requireTeacherContext() ?: return@delete
        val assignmentParam = call.request.queryParameters["assignmentId"]
            ?: call.request.queryParameters["assignment_id"]
        val asg = call.requireOwnedAssignment(ctx, assignmentParam) ?: return@delete
        val unitRow = call.requireOwnedUnit(asg, ctx.schoolId, call.parameters["id"]) ?: return@delete
        val unitId = unitRow[CurriculumUnitsTable.id].value

        dbQuery {
            // Soft-delete the unit and cascade to all descendants (children + grandchildren)
            val childIds = CurriculumUnitsTable.selectAll().where {
                (CurriculumUnitsTable.parentId eq unitId) and
                    (CurriculumUnitsTable.isActive eq true)
            }.map { it[CurriculumUnitsTable.id].value }

            val grandchildIds = if (childIds.isNotEmpty()) {
                CurriculumUnitsTable.selectAll().where {
                    (CurriculumUnitsTable.parentId inList childIds) and
                        (CurriculumUnitsTable.isActive eq true)
                }.map { it[CurriculumUnitsTable.id].value }
            } else emptyList()

            val allIds = listOf(unitId) + childIds + grandchildIds
            val entityIdList = allIds.map { org.jetbrains.exposed.dao.id.EntityID(it, CurriculumUnitsTable) }
            CurriculumUnitsTable.update({ CurriculumUnitsTable.id inList entityIdList }) {
                it[isActive] = false
                it[updatedAt] = Instant.now()
            }
        }
        call.okMessage("Unit deleted")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/parse   (AI parse syllabus image or text)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusParse() {
    post("/parse") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylParseRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post

        val sourceType = req.sourceType.uppercase()
        if (sourceType != "IMAGE" && sourceType != "TEXT") {
            call.fail("source_type must be IMAGE or TEXT", HttpStatusCode.BadRequest, "BAD_SOURCE_TYPE"); return@post
        }
        if (sourceType == "IMAGE" && req.sourceUrl.isNullOrBlank()) {
            call.fail("source_url is required for IMAGE parse", HttpStatusCode.BadRequest, "MISSING_URL"); return@post
        }
        if (sourceType == "TEXT" && req.rawText.isNullOrBlank()) {
            call.fail("raw_text is required for TEXT parse", HttpStatusCode.BadRequest, "MISSING_TEXT"); return@post
        }

        val classLevel = asg.className
        val subject = asg.subject
        val parsed = if (sourceType == "IMAGE") {
            val imageBase64 = fetchImageAsBase64(req.sourceUrl!!)
            if (imageBase64 == null) {
                call.fail("Could not fetch image from URL", HttpStatusCode.BadGateway, "IMAGE_FETCH_FAILED"); return@post
            }
            val mimeType = guessMimeType(req.sourceUrl)
            SyllabusAiService.parseSyllabusImage(imageBase64, mimeType, classLevel, subject, ctx.schoolId)
        } else {
            SyllabusAiService.parseSyllabusText(req.rawText!!, classLevel, subject, ctx.schoolId)
        }

        if (parsed == null) {
            call.fail("AI is currently unavailable. Please try again or enter units manually.", HttpStatusCode.ServiceUnavailable, "AI_UNAVAILABLE"); return@post
        }

        val now = Instant.now()
        val parsedJsonStr = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.builtins.ListSerializer(SylParsedChapterDto.serializer()),
            parsed.chapters.map { ch ->
                SylParsedChapterDto(
                    title = ch.title,
                    topics = ch.topics.map { tp ->
                        SylParsedTopicDto(
                            title = tp.title,
                            subtopics = tp.subtopics.map { st -> SylParsedSubtopicDto(st.title) }
                        )
                    }
                )
            }
        )
        dbQuery {
            SyllabusSourcesTable.insert {
                it[id] = UUID.randomUUID()
                it[schoolId] = ctx.schoolId
                it[assignmentId] = asg.assignmentId
                it[SyllabusSourcesTable.sourceType] = sourceType
                it[sourceUrl] = req.sourceUrl
                it[rawText] = req.rawText
                it[parsedJson] = parsedJsonStr
                it[aiProvider] = parsed.providerUsed
                it[createdAt] = now
                it[updatedAt] = now
            }
        }

        call.ok(
            SylParseResultDto(
                chapters = parsed.chapters.map { ch ->
                    SylParsedChapterDto(
                        title = ch.title,
                        topics = ch.topics.map { tp ->
                            SylParsedTopicDto(
                                title = tp.title,
                                subtopics = tp.subtopics.map { st -> SylParsedSubtopicDto(st.title) }
                            )
                        }
                    )
                },
                aiProvider = parsed.providerUsed,
            ),
            message = "Syllabus parsed",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/parse/confirm   (create curriculum_units from parsed hierarchy)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusParseConfirm() {
    post("/parse/confirm") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylParseConfirmRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post
        val scopeClassId = asg.classId
        val scopeSubjectId = asg.subjectId
        if (scopeClassId == null || scopeSubjectId == null) {
            call.fail("This class is not fully configured yet", HttpStatusCode.Conflict, "CLASS_NOT_CONFIGURED"); return@post
        }
        if (req.chapters.isEmpty()) {
            call.fail("At least one chapter is required", HttpStatusCode.BadRequest, "EMPTY_HIERARCHY"); return@post
        }

        var unitsCreated = 0
        val now = Instant.now()
        dbQuery {
            var chapterPos = CurriculumUnitsTable.selectAll().where {
                (CurriculumUnitsTable.classId eq scopeClassId) and
                    (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                    (CurriculumUnitsTable.isActive eq true) and
                    CurriculumUnitsTable.parentId.isNull()
            }.maxOfOrNull { it[CurriculumUnitsTable.position] } ?: -1

            for (chapter in req.chapters) {
                val chapterId = UUID.randomUUID()
                chapterPos++
                CurriculumUnitsTable.insert {
                    it[id] = chapterId
                    it[schoolId] = ctx.schoolId
                    it[classId] = scopeClassId
                    it[subjectId] = scopeSubjectId
                    it[parentId] = null
                    it[CurriculumUnitsTable.title] = chapter.title.trim()
                    it[position] = chapterPos
                    it[isActive] = true
                    it[depth] = 0
                    it[approvalStatus] = "DRAFT"
                    it[createdAt] = now
                    it[updatedAt] = now
                }
                unitsCreated++

                var topicPos = -1
                for (topic in chapter.topics) {
                    val topicId = UUID.randomUUID()
                    topicPos++
                    CurriculumUnitsTable.insert {
                        it[id] = topicId
                        it[schoolId] = ctx.schoolId
                        it[classId] = scopeClassId
                        it[subjectId] = scopeSubjectId
                        it[parentId] = chapterId
                        it[CurriculumUnitsTable.title] = topic.title.trim()
                        it[position] = topicPos
                        it[isActive] = true
                        it[depth] = 1
                        it[approvalStatus] = "DRAFT"
                        it[createdAt] = now
                        it[updatedAt] = now
                    }
                    unitsCreated++

                    var subtopicPos = -1
                    for (subtopic in topic.subtopics) {
                        subtopicPos++
                        CurriculumUnitsTable.insert {
                            it[id] = UUID.randomUUID()
                            it[schoolId] = ctx.schoolId
                            it[classId] = scopeClassId
                            it[subjectId] = scopeSubjectId
                            it[parentId] = topicId
                            it[CurriculumUnitsTable.title] = subtopic.title.trim()
                            it[position] = subtopicPos
                            it[isActive] = true
                            it[depth] = 2
                            it[approvalStatus] = "DRAFT"
                            it[createdAt] = now
                            it[updatedAt] = now
                        }
                        unitsCreated++
                    }
                }
            }
        }
        call.ok(SylParseConfirmResultDto(unitsCreated = unitsCreated), message = "$unitsCreated units created as DRAFT — review and approve")

        // Initialize pace plan for this assignment
        SyllabusPaceService.recalcForAssignment(asg.assignmentId, ctx.schoolId)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/auto-fill   (look up NCERT reference for class+subject)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusAutoFill() {
    post("/auto-fill") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylAutoFillRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post

        val className = asg.className
        val subject = asg.subject

        log.info("Auto-fill lookup: className='{}' subject='{}'", className, subject)

        val ncertSyllabus = NcertReferenceService.getSyllabus(className, subject)
        if (ncertSyllabus == null || ncertSyllabus.chapters.isEmpty()) {
            log.info("Auto-fill: no NCERT reference found for '{}' '{}'", className, subject)
            call.ok(SylAutoFillResultDto(found = false, source = "", classLevel = className, subject = subject),
                message = "No NCERT reference found for $className $subject")
            return@post
        }

        val chaptersDto = ncertSyllabus.chapters.map { ch ->
            SylAutoFillChapterDto(
                title = ch.title,
                topics = ch.topics.map { t ->
                    SylAutoFillTopicDto(
                        title = t.title,
                        subtopics = t.subtopics.map { st -> SylAutoFillSubtopicDto(st.title) },
                    )
                },
            )
        }
        call.ok(
            SylAutoFillResultDto(
                found = true,
                source = "NCERT",
                classLevel = ncertSyllabus.classLevel,
                subject = ncertSyllabus.subjectName,
                chapters = chaptersDto,
            ),
            message = "Found ${chaptersDto.size} chapters from NCERT reference",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/auto-fill/confirm   (create DRAFT units from NCERT ref)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusAutoFillConfirm() {
    post("/auto-fill/confirm") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylAutoFillConfirmRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post
        val scopeClassId = asg.classId
        val scopeSubjectId = asg.subjectId
        if (scopeClassId == null || scopeSubjectId == null) {
            call.fail("This class is not fully configured yet", HttpStatusCode.Conflict, "CLASS_NOT_CONFIGURED"); return@post
        }
        if (req.chapters.isEmpty()) {
            call.fail("At least one chapter is required", HttpStatusCode.BadRequest, "EMPTY_HIERARCHY"); return@post
        }

        var unitsCreated = 0
        val now = Instant.now()
        dbQuery {
            var chapterPos = CurriculumUnitsTable.selectAll().where {
                (CurriculumUnitsTable.classId eq scopeClassId) and
                    (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                    (CurriculumUnitsTable.isActive eq true) and
                    CurriculumUnitsTable.parentId.isNull()
            }.maxOfOrNull { it[CurriculumUnitsTable.position] } ?: -1

            for (chapter in req.chapters) {
                val chapterId = UUID.randomUUID()
                chapterPos++
                CurriculumUnitsTable.insert {
                    it[id] = chapterId
                    it[schoolId] = ctx.schoolId
                    it[classId] = scopeClassId
                    it[subjectId] = scopeSubjectId
                    it[parentId] = null
                    it[CurriculumUnitsTable.title] = chapter.title.trim()
                    it[position] = chapterPos
                    it[isActive] = true
                    it[depth] = 0
                    it[approvalStatus] = "DRAFT"
                    it[createdAt] = now
                    it[updatedAt] = now
                }
                unitsCreated++

                var topicPos = -1
                for (topic in chapter.topics) {
                    val topicId = UUID.randomUUID()
                    topicPos++
                    CurriculumUnitsTable.insert {
                        it[id] = topicId
                        it[schoolId] = ctx.schoolId
                        it[classId] = scopeClassId
                        it[subjectId] = scopeSubjectId
                        it[parentId] = chapterId
                        it[CurriculumUnitsTable.title] = topic.title.trim()
                        it[position] = topicPos
                        it[isActive] = true
                        it[depth] = 1
                        it[approvalStatus] = "DRAFT"
                        it[createdAt] = now
                        it[updatedAt] = now
                    }
                    unitsCreated++

                    var subtopicPos = -1
                    for (subtopic in topic.subtopics) {
                        val subtopicId = UUID.randomUUID()
                        subtopicPos++
                        CurriculumUnitsTable.insert {
                            it[id] = subtopicId
                            it[schoolId] = ctx.schoolId
                            it[classId] = scopeClassId
                            it[subjectId] = scopeSubjectId
                            it[parentId] = topicId
                            it[CurriculumUnitsTable.title] = subtopic.title.trim()
                            it[position] = subtopicPos
                            it[isActive] = true
                            it[depth] = 2
                            it[approvalStatus] = "DRAFT"
                            it[createdAt] = now
                            it[updatedAt] = now
                        }
                        unitsCreated++
                    }
                }
            }
        }
        call.ok(SylParseConfirmResultDto(unitsCreated = unitsCreated),
            message = "$unitsCreated units created as DRAFT — review and approve")

        SyllabusPaceService.recalcForAssignment(asg.assignmentId, ctx.schoolId)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/approve   (DRAFT → APPROVED, visible to parents)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusApprove() {
    post("/approve") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylApproveRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post
        val scopeClassId = asg.classId
        val scopeSubjectId = asg.subjectId
        if (scopeClassId == null || scopeSubjectId == null) {
            call.fail("This class is not fully configured yet", HttpStatusCode.Conflict, "CLASS_NOT_CONFIGURED"); return@post
        }

        val now = Instant.now()
        val approvedCount = dbQuery {
            if (req.unitIds.isEmpty()) {
                // Approve ALL draft units for this class+subject
                CurriculumUnitsTable.update({
                    (CurriculumUnitsTable.classId eq scopeClassId) and
                        (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                        (CurriculumUnitsTable.isActive eq true) and
                        (CurriculumUnitsTable.approvalStatus eq "DRAFT")
                }) {
                    it[approvalStatus] = "APPROVED"
                    it[updatedAt] = now
                }
            } else {
                // Approve specific unit IDs (must be in scope)
                val ids = req.unitIds.mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }
                CurriculumUnitsTable.update({
                    (CurriculumUnitsTable.classId eq scopeClassId) and
                        (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                        (CurriculumUnitsTable.isActive eq true) and
                        (CurriculumUnitsTable.approvalStatus eq "DRAFT") and
                        (CurriculumUnitsTable.id inList ids.map { org.jetbrains.exposed.dao.id.EntityID(it, CurriculumUnitsTable) })
                }) {
                    it[approvalStatus] = "APPROVED"
                    it[updatedAt] = now
                }
            }
        }
        call.ok(SylApproveResultDto(approvedCount = approvedCount),
            message = "$approvedCount units approved and now visible to parents")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/reject   (DRAFT → REJECTED, soft delete)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusReject() {
    post("/reject") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylRejectRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post
        val scopeClassId = asg.classId
        val scopeSubjectId = asg.subjectId
        if (scopeClassId == null || scopeSubjectId == null) {
            call.fail("This class is not fully configured yet", HttpStatusCode.Conflict, "CLASS_NOT_CONFIGURED"); return@post
        }

        val now = Instant.now()
        val rejectedCount = dbQuery {
            if (req.unitIds.isEmpty()) {
                // Reject ALL draft units (mark REJECTED + soft delete)
                CurriculumUnitsTable.update({
                    (CurriculumUnitsTable.classId eq scopeClassId) and
                        (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                        (CurriculumUnitsTable.isActive eq true) and
                        (CurriculumUnitsTable.approvalStatus eq "DRAFT")
                }) {
                    it[approvalStatus] = "REJECTED"
                    it[isActive] = false
                    it[updatedAt] = now
                }
            } else {
                val ids = req.unitIds.mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }
                CurriculumUnitsTable.update({
                    (CurriculumUnitsTable.classId eq scopeClassId) and
                        (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                        (CurriculumUnitsTable.isActive eq true) and
                        (CurriculumUnitsTable.approvalStatus eq "DRAFT") and
                        (CurriculumUnitsTable.id inList ids.map { org.jetbrains.exposed.dao.id.EntityID(it, CurriculumUnitsTable) })
                }) {
                    it[approvalStatus] = "REJECTED"
                    it[isActive] = false
                    it[updatedAt] = now
                }
            }
        }
        call.ok(SylApproveResultDto(approvedCount = rejectedCount),
            message = "$rejectedCount units rejected and removed")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/teacher/syllabus/pace-warning?assignmentId=   (inline pace check)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusPaceWarning() {
    get("/pace-warning") {
        val ctx = call.requireTeacherContext() ?: return@get
        val assignmentParam = call.request.queryParameters["assignmentId"]
        if (assignmentParam.isNullOrBlank()) {
            call.fail("assignmentId is required", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@get
        }
        val assignmentId = runCatching { UUID.fromString(assignmentParam) }.getOrNull() ?: run {
            call.fail("Invalid assignmentId", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@get
        }

        val snapshot = SyllabusPaceService.recalcForAssignment(assignmentId, ctx.schoolId)
        if (snapshot == null) {
            call.ok(SylPaceWarningDto(level = "ON_TRACK", message = "No pace data yet"),
                message = "OK")
            return@get
        }

        val msg = when (snapshot.level) {
            "CRITICAL" -> "Syllabus coverage is critically behind expected pace. Please accelerate or adjust."
            "BEHIND" -> "Syllabus coverage is behind the expected pace. Consider reviewing your teaching speed."
            "AHEAD" -> "Syllabus coverage is ahead of the expected pace. You may slow down or revise."
            else -> "Syllabus is on track."
        }
        call.ok(
            SylPaceWarningDto(
                level = snapshot.level,
                expectedPct = snapshot.expectedPct,
                actualPct = snapshot.actualPct,
                deviationPct = snapshot.deviationPct,
                message = msg,
                weeklyPeriods = snapshot.weeklyPeriods,
                classesElapsed = snapshot.classesElapsed,
                classesRemaining = snapshot.classesRemaining,
                estimatedCompletionDate = snapshot.estimatedCompletionDate,
                topicsPerClass = snapshot.topicsPerClass,
                holidayDaysCounted = snapshot.holidayDaysCounted,
                avgCoveragePerClass = snapshot.avgCoveragePerClass,
            ),
            message = "OK",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/daily-log   (create/update daily class log)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusDailyLogCreate() {
    post("/daily-log") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylDailyLogRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post

        val logDate = runCatching { LocalDate.parse(req.date) }.getOrNull() ?: run {
            call.fail("date must be YYYY-MM-DD", HttpStatusCode.BadRequest, "BAD_DATE"); return@post
        }
        if (logDate.isAfter(todayIst())) {
            call.fail("date cannot be in the future", HttpStatusCode.BadRequest, "DATE_FUTURE"); return@post
        }
        if (req.coveragePct !in 0..100) {
            call.fail("coverage_pct must be 0-100", HttpStatusCode.BadRequest, "BAD_COVERAGE"); return@post
        }

        val topicIds = req.topicIds.mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }
        if (topicIds.isNotEmpty()) {
            val scopeClassId = asg.classId
            val scopeSubjectId = asg.subjectId
            if (scopeClassId != null && scopeSubjectId != null) {
                val ownedCount = dbQuery {
                    CurriculumUnitsTable.selectAll().where {
                        (CurriculumUnitsTable.id inList topicIds.map { org.jetbrains.exposed.dao.id.EntityID(it, CurriculumUnitsTable) }) and
                            (CurriculumUnitsTable.classId eq scopeClassId) and
                            (CurriculumUnitsTable.subjectId eq scopeSubjectId) and
                            (CurriculumUnitsTable.isActive eq true)
                    }.count()
                }.toInt()
                if (ownedCount != topicIds.size) {
                    call.fail("One or more topic_ids do not belong to your assignment", HttpStatusCode.Forbidden, "TOPIC_NOT_IN_SCOPE"); return@post
                }
            }
        }

        val topicIdsJson = topicIds.joinToString(prefix = "[", postfix = "]", separator = ",") { "\"$it\"" }
        val now = Instant.now()
        val logId = UUID.randomUUID()

        val summaryText = if (req.summaryText.isNotBlank()) {
            req.summaryText
        } else {
            val topicTitles = dbQuery {
                if (topicIds.isEmpty()) emptyList()
                else CurriculumUnitsTable.selectAll().where {
                    CurriculumUnitsTable.id inList topicIds.map { org.jetbrains.exposed.dao.id.EntityID(it, CurriculumUnitsTable) }
                }.map { it[CurriculumUnitsTable.title] }
            }
            SyllabusAiService.generateDailySummary(topicTitles, asg.className, asg.subject, ctx.schoolId) ?: ""
        }

        dbQuery {
            val existing = DailyClassLogTable.selectAll().where {
                (DailyClassLogTable.assignmentId eq asg.assignmentId) and
                    (DailyClassLogTable.date eq logDate)
            }.singleOrNull()

            if (existing != null) {
                DailyClassLogTable.update({
                    DailyClassLogTable.id eq existing[DailyClassLogTable.id]
                }) {
                    it[DailyClassLogTable.topicIds] = topicIdsJson
                    it[DailyClassLogTable.summaryText] = summaryText
                    it[DailyClassLogTable.coveragePct] = req.coveragePct
                    it[DailyClassLogTable.logSource] = "TEACHER"
                    it[DailyClassLogTable.isAiEstimated] = false
                    it[DailyClassLogTable.updatedAt] = now
                }
            } else {
                DailyClassLogTable.insert {
                    it[DailyClassLogTable.id] = logId
                    it[DailyClassLogTable.schoolId] = ctx.schoolId
                    it[DailyClassLogTable.assignmentId] = asg.assignmentId
                    it[DailyClassLogTable.date] = logDate
                    it[DailyClassLogTable.topicIds] = topicIdsJson
                    it[DailyClassLogTable.summaryText] = summaryText
                    it[DailyClassLogTable.coveragePct] = req.coveragePct
                    it[DailyClassLogTable.logSource] = "TEACHER"
                    it[DailyClassLogTable.isAiEstimated] = false
                    it[DailyClassLogTable.createdAt] = now
                    it[DailyClassLogTable.updatedAt] = now
                }
            }

            // Update syllabus_progress for each selected topic
            val section = asg.section.ifBlank { "A" }
            for (topicId in topicIds) {
                val progExisting = SyllabusProgressTable.selectAll().where {
                    (SyllabusProgressTable.unitId eq topicId) and
                        (SyllabusProgressTable.section eq section) and
                        (SyllabusProgressTable.assignmentId eq asg.assignmentId)
                }.singleOrNull()

                val fullyCovered = req.coveragePct >= 100
                val coveredOnDate: LocalDate? = if (fullyCovered) logDate else null
                if (progExisting != null) {
                    SyllabusProgressTable.update({
                        SyllabusProgressTable.id eq progExisting[SyllabusProgressTable.id]
                    }) {
                        it[coveragePercent] = req.coveragePct
                        if (fullyCovered) {
                            it[isCovered] = true
                            it[SyllabusProgressTable.coveredOn] = coveredOnDate
                            it[coveredBy] = ctx.userId
                        }
                        it[updatedAt] = now
                    }
                } else {
                    SyllabusProgressTable.insert {
                        it[id] = UUID.randomUUID()
                        it[SyllabusProgressTable.unitId] = topicId
                        it[SyllabusProgressTable.section] = section
                        it[assignmentId] = asg.assignmentId
                        it[isCovered] = fullyCovered
                        it[SyllabusProgressTable.coveredOn] = coveredOnDate
                        it[coveredBy] = if (fullyCovered) ctx.userId else null
                        it[coveragePercent] = req.coveragePct
                        it[createdAt] = now
                        it[updatedAt] = now
                    }
                }
            }
        }

        // Trigger pace recalculation for this assignment
        SyllabusPaceService.recalcForAssignment(asg.assignmentId, ctx.schoolId)

        call.ok(
            SylDailyLogDto(
                id = logId.toString(),
                date = logDate.toString(),
                topicIds = topicIds.map { it.toString() },
                summaryText = summaryText,
                coveragePct = req.coveragePct,
                source = "TEACHER",
                isAiEstimated = false,
            ),
            message = "Daily log saved",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/teacher/syllabus/daily-log?assignmentId=&from=&to=   (list logs)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusDailyLogList() {
    get("/daily-log") {
        val ctx = call.requireTeacherContext() ?: return@get
        val assignmentParam = call.request.queryParameters["assignmentId"]
            ?: call.request.queryParameters["assignment_id"]
        val asg = call.requireOwnedAssignment(ctx, assignmentParam) ?: return@get

        val fromStr = call.request.queryParameters["from"]
        val toStr = call.request.queryParameters["to"]
        val fromDate = fromStr?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val toDate = toStr?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

        val logs = dbQuery {
            var pred: org.jetbrains.exposed.sql.Op<Boolean> =
                (DailyClassLogTable.assignmentId eq asg.assignmentId)
            if (fromDate != null) pred = pred and (DailyClassLogTable.date greaterEq fromDate)
            if (toDate != null) pred = pred and (DailyClassLogTable.date lessEq toDate)
            DailyClassLogTable.selectAll().where { pred }
                .orderBy(DailyClassLogTable.date, SortOrder.DESC).map { row ->
                SylDailyLogDto(
                    id = row[DailyClassLogTable.id].value.toString(),
                    date = row[DailyClassLogTable.date].toString(),
                    topicIds = parseTopicIdsJson(row[DailyClassLogTable.topicIds]),
                    summaryText = row[DailyClassLogTable.summaryText],
                    coveragePct = row[DailyClassLogTable.coveragePct],
                    source = row[DailyClassLogTable.logSource],
                    isAiEstimated = row[DailyClassLogTable.isAiEstimated],
                )
            }
        }
        call.ok(SylDailyLogListDto(logs = logs), message = "Daily logs loaded")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/teacher/syllabus/daily-log/should-show?assignmentId=
//   Returns whether the daily check-in popup should show for this assignment today.
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusDailyLogShouldShow() {
    get("/daily-log/should-show") {
        val ctx = call.requireTeacherContext() ?: return@get
        val assignmentParam = call.request.queryParameters["assignmentId"]
            ?: call.request.queryParameters["assignment_id"]
        val asg = call.requireOwnedAssignment(ctx, assignmentParam) ?: return@get

        val today = todayIst()
        val alreadyLogged = dbQuery {
            DailyClassLogTable.selectAll().where {
                (DailyClassLogTable.assignmentId eq asg.assignmentId) and
                    (DailyClassLogTable.date eq today)
            }.count()
        } > 0

        if (alreadyLogged) {
            call.ok(SylShouldShowPopupDto(shouldShow = false, reason = "already_logged"), message = "OK"); return@get
        }

        val prefs = dbQuery {
            SyllabusPopupPrefsTable.selectAll().where {
                (SyllabusPopupPrefsTable.teacherId eq ctx.userId) and
                    (SyllabusPopupPrefsTable.assignmentId eq asg.assignmentId)
            }.singleOrNull()
        }
        if (prefs != null) {
            val mode = prefs[SyllabusPopupPrefsTable.suppressMode]
            val suppressedUntil = prefs[SyllabusPopupPrefsTable.suppressedUntil]
            when (mode) {
                "permanent" -> {
                    call.ok(SylShouldShowPopupDto(shouldShow = false, reason = "suppressed_permanent"), message = "OK"); return@get
                }
                "week" -> {
                    if (suppressedUntil != null && !today.isAfter(suppressedUntil)) {
                        call.ok(SylShouldShowPopupDto(shouldShow = false, reason = "suppressed_until_${suppressedUntil}"), message = "OK"); return@get
                    }
                }
            }
        }
        call.ok(SylShouldShowPopupDto(shouldShow = true, reason = "ok"), message = "OK")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/v1/teacher/syllabus/popup-prefs   (set suppression prefs)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusPopupPrefsSet() {
    post("/popup-prefs") {
        val ctx = call.requireTeacherContext() ?: return@post
        val req = runCatching { call.receive<SylPopupPrefsRequest>() }.getOrNull()
        if (req == null) {
            call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
        }
        val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post

        val mode = req.suppressMode.lowercase()
        if (mode !in setOf("off", "week", "permanent")) {
            call.fail("suppress_mode must be off, week, or permanent", HttpStatusCode.BadRequest, "BAD_MODE"); return@post
        }

        val suppressedUntil: LocalDate? = when (mode) {
            "week" -> todayIst().plusDays(7)
            else -> null
        }
        val now = Instant.now()

        dbQuery {
            val existing = SyllabusPopupPrefsTable.selectAll().where {
                (SyllabusPopupPrefsTable.teacherId eq ctx.userId) and
                    (SyllabusPopupPrefsTable.assignmentId eq asg.assignmentId)
            }.singleOrNull()

            if (existing != null) {
                SyllabusPopupPrefsTable.update({
                    SyllabusPopupPrefsTable.id eq existing[SyllabusPopupPrefsTable.id]
                }) {
                    it[SyllabusPopupPrefsTable.suppressMode] = mode
                    it[SyllabusPopupPrefsTable.suppressedUntil] = suppressedUntil
                    it[updatedAt] = now
                }
            } else {
                SyllabusPopupPrefsTable.insert {
                    it[id] = UUID.randomUUID()
                    it[teacherId] = ctx.userId
                    it[assignmentId] = asg.assignmentId
                    it[SyllabusPopupPrefsTable.suppressMode] = mode
                    it[SyllabusPopupPrefsTable.suppressedUntil] = suppressedUntil
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            }
        }
        call.ok(
            SylPopupPrefsDto(suppressMode = mode, suppressedUntil = suppressedUntil?.toString()),
            message = "Popup preferences saved",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GET /api/v1/teacher/syllabus/popup-prefs?assignmentId=   (get suppression prefs)
// ─────────────────────────────────────────────────────────────────────────────
private fun Route.syllabusPopupPrefsGet() {
    get("/popup-prefs") {
        val ctx = call.requireTeacherContext() ?: return@get
        val assignmentParam = call.request.queryParameters["assignmentId"]
            ?: call.request.queryParameters["assignment_id"]
        val asg = call.requireOwnedAssignment(ctx, assignmentParam) ?: return@get

        val prefs = dbQuery {
            SyllabusPopupPrefsTable.selectAll().where {
                (SyllabusPopupPrefsTable.teacherId eq ctx.userId) and
                    (SyllabusPopupPrefsTable.assignmentId eq asg.assignmentId)
            }.singleOrNull()
        }
        call.ok(
            SylPopupPrefsDto(
                suppressMode = prefs?.get(SyllabusPopupPrefsTable.suppressMode) ?: "off",
                suppressedUntil = prefs?.get(SyllabusPopupPrefsTable.suppressedUntil)?.toString(),
            ),
            message = "Popup preferences loaded",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers for the agentic endpoints.
// ─────────────────────────────────────────────────────────────────────────────

private val imageHttpClient by lazy { HttpClient(CIO) }
private val log = LoggerFactory.getLogger("TeacherSyllabusRouting")

private suspend fun fetchImageAsBase64(url: String): String? = try {
    val resp = imageHttpClient.get(url)
    val bytes = resp.readRawBytes()
    java.util.Base64.getEncoder().encodeToString(bytes)
} catch (e: Exception) {
    null
}

private fun guessMimeType(url: String): String {
    val lower = url.lowercase()
    return when {
        lower.endsWith(".png") -> "image/png"
        lower.endsWith(".webp") -> "image/webp"
        lower.endsWith(".gif") -> "image/gif"
        else -> "image/jpeg"
    }
}

private fun parseTopicIdsJson(jsonStr: String): List<String> {
    return try {
        val arr = kotlinx.serialization.json.Json.parseToJsonElement(jsonStr).jsonArray
        arr.map { it.jsonPrimitive.content }
    } catch (e: Exception) {
        emptyList()
    }
}
