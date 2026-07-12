/*
 * File: ExamSyllabusRouting.kt
 * Module: feature.exam
 *
 * Exam syllabus mapping: teacher maps curriculum units to an assessment,
 * parent reads the syllabus for a specific exam.
 *
 * Endpoints (JWT):
 *   GET  /api/v1/exam/syllabus/{assessmentId}           — get mapped units (teacher)
 *   PUT  /api/v1/exam/syllabus/{assessmentId}            — update mapping (teacher)
 *   GET  /api/v1/exam/parent/{childId}/syllabus/{assessmentId} — parent reads exam syllabus
 */
package com.littlebridge.enrollplus.feature.exam

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.principalUserId
import com.littlebridge.enrollplus.core.requireTeacherContext
import com.littlebridge.enrollplus.db.AssessmentsTable
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.CurriculumUnitsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.ExamSyllabusMappingTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

// ── DTOs ─────────────────────────────────────────────────────────────────────

@Serializable
data class CurriculumUnitDto(
    val id: String,
    val title: String,
    @SerialName("parent_id") val parentId: String? = null,
    val depth: Int = 0,
    val position: Int = 0,
    @SerialName("is_mapped") val isMapped: Boolean = false,
)

@Serializable
data class ExamSyllabusResponse(
    @SerialName("assessment_id") val assessmentId: String,
    @SerialName("exam_name") val examName: String,
    val subject: String,
    @SerialName("class_name") val className: String,
    val section: String,
    val units: List<CurriculumUnitDto>,
)

@Serializable
data class ExamSyllabusUpdateRequest(
    @SerialName("unit_ids") val unitIds: List<String>,
)

// ── Routing ──────────────────────────────────────────────────────────────────

fun Route.examSyllabusRouting() {
    authenticate("jwt") {

        // ── Teacher: get / update syllabus mapping ──────────────────────────
        route("/api/v1/exam/syllabus/{assessmentId}") {

            // GET — list all curriculum units for the assessment's class+subject,
            // flagged with isMapped
            get {
                val ctx = call.requireTeacherContext() ?: return@get
                val assessmentId = call.parameters["assessmentId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid assessment id", HttpStatusCode.BadRequest)
                    return@get
                }

                val assessment = dbQuery {
                    AssessmentsTable.selectAll()
                        .where {
                            (AssessmentsTable.id eq assessmentId) and
                                (AssessmentsTable.schoolId eq ctx.schoolId)
                        }
                        .singleOrNull()
                } ?: run {
                    call.fail("Assessment not found", HttpStatusCode.NotFound)
                    return@get
                }

                val className = assessment[AssessmentsTable.className]
                val section = assessment[AssessmentsTable.section]
                val subject = assessment[AssessmentsTable.subject]
                val examName = assessment[AssessmentsTable.name]

                // Find the teacher_subject_assignment to get classId/subjectId
                val units = dbQuery {
                    // Get mapped unit IDs for this assessment
                    val mappedUnitIds = ExamSyllabusMappingTable.selectAll()
                        .where { ExamSyllabusMappingTable.assessmentId eq assessmentId }
                        .map { it[ExamSyllabusMappingTable.curriculumUnitId] }
                        .toSet()

                    // Get all curriculum units for this class+subject
                    // We need to resolve classId from className — query school_classes
                    // For now, use the className text to match via CurriculumUnitsTable
                    // which is scoped by classId. We'll join via teacher_subject_assignments.
                    val tsas = com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.selectAll()
                        .where {
                            (com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.schoolId eq ctx.schoolId) and
                                (com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.className eq className) and
                                (com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.subject eq subject)
                        }
                        .firstOrNull()

                    val classId = tsas?.get(com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.classId)
                    val subjectId = tsas?.get(com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.subjectId)

                    if (classId != null && subjectId != null) {
                        CurriculumUnitsTable.selectAll()
                            .where {
                                (CurriculumUnitsTable.schoolId eq ctx.schoolId) and
                                    (CurriculumUnitsTable.classId eq classId) and
                                    (CurriculumUnitsTable.subjectId eq subjectId) and
                                    (CurriculumUnitsTable.isActive eq true)
                            }
                            .orderBy(CurriculumUnitsTable.depth to org.jetbrains.exposed.sql.SortOrder.ASC, CurriculumUnitsTable.position to org.jetbrains.exposed.sql.SortOrder.ASC)
                            .map { row ->
                                val unitId = row[CurriculumUnitsTable.id].value
                                CurriculumUnitDto(
                                    id = unitId.toString(),
                                    title = row[CurriculumUnitsTable.title],
                                    parentId = row[CurriculumUnitsTable.parentId]?.toString(),
                                    depth = row[CurriculumUnitsTable.depth],
                                    position = row[CurriculumUnitsTable.position],
                                    isMapped = unitId in mappedUnitIds,
                                )
                            }
                    } else {
                        emptyList()
                    }
                }

                call.ok(
                    ExamSyllabusResponse(
                        assessmentId = assessmentId.toString(),
                        examName = examName,
                        subject = subject,
                        className = className,
                        section = section,
                        units = units,
                    ),
                    message = "Syllabus mapping loaded",
                )
            }

            // PUT — replace the mapping (delete all, insert new)
            put {
                val ctx = call.requireTeacherContext() ?: return@put
                val assessmentId = call.parameters["assessmentId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid assessment id", HttpStatusCode.BadRequest)
                    return@put
                }
                val req = call.receive<ExamSyllabusUpdateRequest>()

                // Verify assessment exists and belongs to school
                val assessment = dbQuery {
                    AssessmentsTable.selectAll()
                        .where {
                            (AssessmentsTable.id eq assessmentId) and
                                (AssessmentsTable.schoolId eq ctx.schoolId)
                        }
                        .singleOrNull()
                } ?: run {
                    call.fail("Assessment not found", HttpStatusCode.NotFound)
                    return@put
                }

                // Replace mapping
                dbQuery {
                    ExamSyllabusMappingTable.deleteWhere {
                        ExamSyllabusMappingTable.assessmentId eq assessmentId
                    }
                    req.unitIds.forEach { unitIdStr ->
                        val unitId = runCatching { UUID.fromString(unitIdStr) }.getOrNull() ?: return@forEach
                        ExamSyllabusMappingTable.insert {
                            it[ExamSyllabusMappingTable.assessmentId] = assessmentId
                            it[curriculumUnitId] = unitId
                            it[schoolId] = ctx.schoolId
                            it[createdAt] = java.time.Instant.now()
                        }
                    }
                }

                call.okMessage("Syllabus mapping updated (${req.unitIds.size} units)")
            }
        }

        // ── Parent: read exam syllabus for a child ──────────────────────────
        route("/api/v1/exam/parent/{childId}/syllabus/{assessmentId}") {
            get {
                val uid = call.principalUserId()?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized)
                    return@get
                }
                val childId = call.parameters["childId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid child id", HttpStatusCode.BadRequest)
                    return@get
                }
                val assessmentId = call.parameters["assessmentId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid assessment id", HttpStatusCode.BadRequest)
                    return@get
                }

                // Verify child belongs to parent
                val child = dbQuery {
                    ChildrenTable.selectAll()
                        .where {
                            (ChildrenTable.id eq childId) and
                                (ChildrenTable.parentId eq uid) and
                                (ChildrenTable.isActive eq true)
                        }
                        .singleOrNull()
                } ?: run {
                    call.fail("Child not found", HttpStatusCode.NotFound)
                    return@get
                }

                val schoolId = child[ChildrenTable.schoolId] ?: run {
                    call.fail("Child not linked to a school", HttpStatusCode.BadRequest)
                    return@get
                }

                val assessment = dbQuery {
                    AssessmentsTable.selectAll()
                        .where {
                            (AssessmentsTable.id eq assessmentId) and
                                (AssessmentsTable.schoolId eq schoolId)
                        }
                        .singleOrNull()
                } ?: run {
                    call.fail("Assessment not found", HttpStatusCode.NotFound)
                    return@get
                }

                val className = assessment[AssessmentsTable.className]
                val subject = assessment[AssessmentsTable.subject]
                val examName = assessment[AssessmentsTable.name]
                val section = assessment[AssessmentsTable.section]

                val units = dbQuery {
                    val mappedUnitIds = ExamSyllabusMappingTable.selectAll()
                        .where { ExamSyllabusMappingTable.assessmentId eq assessmentId }
                        .map { it[ExamSyllabusMappingTable.curriculumUnitId] }
                        .toSet()

                    val tsas = com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.selectAll()
                        .where {
                            (com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.schoolId eq schoolId) and
                                (com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.className eq className) and
                                (com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.subject eq subject)
                        }
                        .firstOrNull()

                    val classId = tsas?.get(com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.classId)
                    val subjectId = tsas?.get(com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable.subjectId)

                    if (classId != null && subjectId != null) {
                        CurriculumUnitsTable.selectAll()
                            .where {
                                (CurriculumUnitsTable.schoolId eq schoolId) and
                                    (CurriculumUnitsTable.classId eq classId) and
                                    (CurriculumUnitsTable.subjectId eq subjectId) and
                                    (CurriculumUnitsTable.isActive eq true) and
                                    (CurriculumUnitsTable.id inList mappedUnitIds.map { org.jetbrains.exposed.dao.id.EntityID(it, CurriculumUnitsTable) })
                            }
                            .orderBy(CurriculumUnitsTable.depth to org.jetbrains.exposed.sql.SortOrder.ASC, CurriculumUnitsTable.position to org.jetbrains.exposed.sql.SortOrder.ASC)
                            .map { row ->
                                CurriculumUnitDto(
                                    id = row[CurriculumUnitsTable.id].value.toString(),
                                    title = row[CurriculumUnitsTable.title],
                                    parentId = row[CurriculumUnitsTable.parentId]?.toString(),
                                    depth = row[CurriculumUnitsTable.depth],
                                    position = row[CurriculumUnitsTable.position],
                                    isMapped = true,
                                )
                            }
                    } else {
                        emptyList()
                    }
                }

                call.ok(
                    ExamSyllabusResponse(
                        assessmentId = assessmentId.toString(),
                        examName = examName,
                        subject = subject,
                        className = className,
                        section = section,
                        units = units,
                    ),
                    message = "Exam syllabus loaded",
                )
            }
        }
    }
}
