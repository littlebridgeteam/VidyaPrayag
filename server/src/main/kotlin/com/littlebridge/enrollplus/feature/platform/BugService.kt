package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformBugsTable
import com.littlebridge.enrollplus.db.PlatformBugCommentsTable
import com.littlebridge.enrollplus.db.PlatformBugActivityTable
import com.littlebridge.enrollplus.db.PlatformTestAttachmentsTable
import com.littlebridge.enrollplus.db.PlatformFeaturesTable
import com.littlebridge.enrollplus.db.AppUsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.util.UUID
import java.time.Instant
import java.time.Duration

object BugService {

    private val VALID_TRANSITIONS = mapOf(
        "reported" to setOf("triaged", "rejected"),
        "triaged" to setOf("assigned", "duplicate"),
        "assigned" to setOf("in_progress", "reassigned"),
        "in_progress" to setOf("fixed", "blocked"),
        "fixed" to setOf("ready_for_qa"),
        "ready_for_qa" to setOf("retest"),
        "retest" to setOf("verified", "failed"),
        "verified" to setOf("closed"),
        "failed" to setOf("reopened"),
        "reopened" to setOf("in_progress"),
        "blocked" to setOf("in_progress"),
    )

    private val QA_ALLOWED_TRANSITIONS = setOf(
        "ready_for_qa" to "retest",
        "retest" to "verified",
        "retest" to "failed",
        "verified" to "closed",
        "failed" to "reopened",
    )

    private val SLA_HOURS = mapOf(
        "critical" to 4L,
        "high" to 24L,
        "medium" to 72L,
        "low" to 168L,
    )

    fun isValidTransition(from: String, to: String, isQa: Boolean): Boolean {
        if (to == "blocked") return from in setOf("triaged", "assigned", "in_progress")
        if (isQa) return (from to to) in QA_ALLOWED_TRANSITIONS
        return VALID_TRANSITIONS[from]?.contains(to) == true
    }

    private fun slaDueAt(priority: String, createdAt: Instant): Instant? {
        val hours = SLA_HOURS[priority] ?: return null
        return createdAt.plusSeconds(hours * 3600L)
    }

    suspend fun nextBugId(): String = dbQuery {
        val count = PlatformBugsTable.selectAll().count()
        "BUG-%05d".format(count + 1)
    }

    suspend fun list(
        page: Int = 1,
        pageSize: Int = 25,
        status: String? = null,
        priority: String? = null,
        severity: String? = null,
        featureId: String? = null,
        assignedTo: String? = null,
        search: String? = null,
    ): Pair<List<BugSummaryDto>, Long> = dbQuery {
        val conditions = Op.build {
            (if (status != null) PlatformBugsTable.status eq status else Op.TRUE) and
            (if (priority != null) PlatformBugsTable.priority eq priority else Op.TRUE) and
            (if (severity != null) PlatformBugsTable.severity eq severity else Op.TRUE) and
            (if (featureId != null) PlatformBugsTable.featureId eq UUID.fromString(featureId) else Op.TRUE) and
            (if (assignedTo != null) PlatformBugsTable.assignedTo eq UUID.fromString(assignedTo) else Op.TRUE) and
            (if (search != null) {
                (PlatformBugsTable.title like "%$search%") or
                (PlatformBugsTable.bugId like "%$search%")
            } else Op.TRUE)
        }
        val total = PlatformBugsTable.selectAll().where { conditions }.count()
        val items = PlatformBugsTable.selectAll().where { conditions }
            .orderBy(PlatformBugsTable.createdAt, SortOrder.DESC)
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .map { row -> enrichBug(row) }
        items to total
    }

    suspend fun kanban(): Map<String, List<BugSummaryDto>> = dbQuery {
        val allBugs = PlatformBugsTable.selectAll()
            .orderBy(PlatformBugsTable.priority, SortOrder.ASC)
            .map { row -> enrichBug(row) }
        allBugs.groupBy { it.status }
    }

    suspend fun getById(id: UUID): BugDetailDto? = dbQuery {
        val row = PlatformBugsTable.selectAll().where { PlatformBugsTable.id eq id }
            .singleOrNull() ?: return@dbQuery null
        val summary = enrichBug(row)

        val comments = PlatformBugCommentsTable.selectAll()
            .where { PlatformBugCommentsTable.bugId eq id }
            .orderBy(PlatformBugCommentsTable.createdAt, SortOrder.ASC)
            .map { c ->
                val authorName = c[PlatformBugCommentsTable.authorId]?.let { aid ->
                    AppUsersTable.selectAll().where { AppUsersTable.id eq aid }
                        .singleOrNull()?.get(AppUsersTable.fullName)
                }
                BugCommentDto(
                    id = c[PlatformBugCommentsTable.id].value.toString(),
                    bug_id = id.toString(),
                    author_id = c[PlatformBugCommentsTable.authorId]?.toString(),
                    author_name = authorName,
                    body = c[PlatformBugCommentsTable.body],
                    mentions = c[PlatformBugCommentsTable.mentions],
                    is_internal = c[PlatformBugCommentsTable.isInternal],
                    created_at = c[PlatformBugCommentsTable.createdAt].toString(),
                )
            }

        val activity = PlatformBugActivityTable.selectAll()
            .where { PlatformBugActivityTable.bugId eq id }
            .orderBy(PlatformBugActivityTable.createdAt, SortOrder.DESC)
            .map { a ->
                val actorName = a[PlatformBugActivityTable.actorId]?.let { aid ->
                    AppUsersTable.selectAll().where { AppUsersTable.id eq aid }
                        .singleOrNull()?.get(AppUsersTable.fullName)
                }
                BugActivityDto(
                    id = a[PlatformBugActivityTable.id].value.toString(),
                    bug_id = id.toString(),
                    actor_id = a[PlatformBugActivityTable.actorId]?.toString(),
                    actor_name = actorName,
                    action = a[PlatformBugActivityTable.action],
                    field = a[PlatformBugActivityTable.field],
                    old_value = a[PlatformBugActivityTable.oldValue],
                    new_value = a[PlatformBugActivityTable.newValue],
                    created_at = a[PlatformBugActivityTable.createdAt].toString(),
                )
            }

        val attachments = PlatformTestAttachmentsTable.selectAll()
            .where { PlatformTestAttachmentsTable.bugId eq id }
            .map { at ->
                AttachmentDto(
                    id = at[PlatformTestAttachmentsTable.id].value.toString(),
                    file_name = at[PlatformTestAttachmentsTable.fileName],
                    file_url = at[PlatformTestAttachmentsTable.fileUrl],
                    file_type = at[PlatformTestAttachmentsTable.fileType],
                    mime_type = at[PlatformTestAttachmentsTable.mimeType],
                    file_size_bytes = at[PlatformTestAttachmentsTable.fileSizeBytes],
                    uploaded_by = at[PlatformTestAttachmentsTable.uploadedBy]?.toString(),
                    created_at = at[PlatformTestAttachmentsTable.createdAt].toString(),
                )
            }

        BugDetailDto(
            bug = summary,
            description = row[PlatformBugsTable.description],
            reproducibility = row[PlatformBugsTable.reproducibility],
            environment = row[PlatformBugsTable.environment],
            build_version = row[PlatformBugsTable.buildVersion],
            platform = row[PlatformBugsTable.platform],
            device = row[PlatformBugsTable.device],
            os_version = row[PlatformBugsTable.osVersion],
            steps_to_reproduce = row[PlatformBugsTable.stepsToReproduce],
            expected_result = row[PlatformBugsTable.expectedResult],
            actual_result = row[PlatformBugsTable.actualResult],
            screen_id = row[PlatformBugsTable.screenId]?.toString(),
            api_id = row[PlatformBugsTable.apiId]?.toString(),
            test_case_id = row[PlatformBugsTable.testCaseId]?.toString(),
            triaged_by = row[PlatformBugsTable.triagedBy]?.toString(),
            fixed_by = row[PlatformBugsTable.fixedBy]?.toString(),
            verified_by = row[PlatformBugsTable.verifiedBy]?.toString(),
            resolved_at = row[PlatformBugsTable.resolvedAt]?.toString(),
            closed_at = row[PlatformBugsTable.closedAt]?.toString(),
            tags = row[PlatformBugsTable.tags],
            metadata = row[PlatformBugsTable.metadata],
            comments = comments,
            activity = activity,
            attachments = attachments,
        )
    }

    suspend fun create(req: CreateBugRequest, userId: UUID): UUID = dbQuery {
        val now = Instant.now()
        val bugId = "BUG-%05d".format(PlatformBugsTable.selectAll().count() + 1)
        val sla = slaDueAt(req.priority, now)
        PlatformBugsTable.insert {
            it[PlatformBugsTable.bugId] = bugId
            it[PlatformBugsTable.title] = req.title
            it[PlatformBugsTable.description] = req.description
            it[PlatformBugsTable.featureId] = req.feature_id?.let { UUID.fromString(it) }
            it[PlatformBugsTable.screenId] = req.screen_id?.let { UUID.fromString(it) }
            it[PlatformBugsTable.apiId] = req.api_id?.let { UUID.fromString(it) }
            it[PlatformBugsTable.testCaseId] = req.test_case_id?.let { UUID.fromString(it) }
            it[PlatformBugsTable.status] = "reported"
            it[PlatformBugsTable.priority] = req.priority
            it[PlatformBugsTable.severity] = req.severity
            it[PlatformBugsTable.reproducibility] = req.reproducibility
            it[PlatformBugsTable.environment] = req.environment
            it[PlatformBugsTable.buildVersion] = req.build_version
            it[PlatformBugsTable.platform] = req.platform
            it[PlatformBugsTable.device] = req.device
            it[PlatformBugsTable.osVersion] = req.os_version
            it[PlatformBugsTable.stepsToReproduce] = req.steps_to_reproduce
            it[PlatformBugsTable.expectedResult] = req.expected_result
            it[PlatformBugsTable.actualResult] = req.actual_result
            it[PlatformBugsTable.reportedBy] = userId
            it[PlatformBugsTable.slaDueAt] = sla
            it[PlatformBugsTable.tags] = req.tags
            it[PlatformBugsTable.createdAt] = now
            it[PlatformBugsTable.updatedAt] = now
        }[PlatformBugsTable.id].value
    }

    suspend fun update(id: UUID, req: UpdateBugRequest): Boolean = dbQuery {
        PlatformBugsTable.update(where = { PlatformBugsTable.id eq id }) {
            req.title?.let { v -> it[PlatformBugsTable.title] = v }
            req.description?.let { v -> it[PlatformBugsTable.description] = v }
            req.feature_id?.let { v -> it[PlatformBugsTable.featureId] = UUID.fromString(v) }
            req.screen_id?.let { v -> it[PlatformBugsTable.screenId] = UUID.fromString(v) }
            req.api_id?.let { v -> it[PlatformBugsTable.apiId] = UUID.fromString(v) }
            req.test_case_id?.let { v -> it[PlatformBugsTable.testCaseId] = UUID.fromString(v) }
            req.priority?.let { v -> it[PlatformBugsTable.priority] = v }
            req.severity?.let { v -> it[PlatformBugsTable.severity] = v }
            req.reproducibility?.let { v -> it[PlatformBugsTable.reproducibility] = v }
            req.environment?.let { v -> it[PlatformBugsTable.environment] = v }
            req.build_version?.let { v -> it[PlatformBugsTable.buildVersion] = v }
            req.platform?.let { v -> it[PlatformBugsTable.platform] = v }
            req.device?.let { v -> it[PlatformBugsTable.device] = v }
            req.os_version?.let { v -> it[PlatformBugsTable.osVersion] = v }
            req.steps_to_reproduce?.let { v -> it[PlatformBugsTable.stepsToReproduce] = v }
            req.expected_result?.let { v -> it[PlatformBugsTable.expectedResult] = v }
            req.actual_result?.let { v -> it[PlatformBugsTable.actualResult] = v }
            req.tags?.let { v -> it[PlatformBugsTable.tags] = v }
            it[PlatformBugsTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun updateStatus(id: UUID, newStatus: String, userId: UUID, isQa: Boolean): Boolean = dbQuery {
        val row = PlatformBugsTable.selectAll().where { PlatformBugsTable.id eq id }.singleOrNull()
            ?: return@dbQuery false
        val currentStatus = row[PlatformBugsTable.status]
        if (!isValidTransition(currentStatus, newStatus, isQa)) return@dbQuery false

        // Log activity
        PlatformBugActivityTable.insert {
            it[PlatformBugActivityTable.bugId] = id
            it[PlatformBugActivityTable.actorId] = userId
            it[PlatformBugActivityTable.action] = "status_changed"
            it[PlatformBugActivityTable.field] = "status"
            it[PlatformBugActivityTable.oldValue] = currentStatus
            it[PlatformBugActivityTable.newValue] = newStatus
            it[PlatformBugActivityTable.createdAt] = Instant.now()
        }

        val now = Instant.now()
        PlatformBugsTable.update(where = { PlatformBugsTable.id eq id }) {
            it[PlatformBugsTable.status] = newStatus
            when (newStatus) {
                "verified" -> it[PlatformBugsTable.verifiedBy] = userId
                "fixed" -> it[PlatformBugsTable.fixedBy] = userId
                "triaged" -> it[PlatformBugsTable.triagedBy] = userId
            }
            if (newStatus in setOf("verified", "closed")) it[PlatformBugsTable.resolvedAt] = now
            if (newStatus == "closed") it[PlatformBugsTable.closedAt] = now
            it[PlatformBugsTable.updatedAt] = now
        } > 0
    }

    suspend fun assign(id: UUID, assignedTo: UUID, actorId: UUID): Boolean = dbQuery {
        PlatformBugActivityTable.insert {
            it[PlatformBugActivityTable.bugId] = id
            it[PlatformBugActivityTable.actorId] = actorId
            it[PlatformBugActivityTable.action] = "assigned"
            it[PlatformBugActivityTable.field] = "assigned_to"
            it[PlatformBugActivityTable.newValue] = assignedTo.toString()
            it[PlatformBugActivityTable.createdAt] = Instant.now()
        }
        PlatformBugsTable.update(where = { PlatformBugsTable.id eq id }) {
            it[PlatformBugsTable.assignedTo] = assignedTo
            it[PlatformBugsTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun addComment(bugId: UUID, req: CreateBugCommentRequest, authorId: UUID): UUID = dbQuery {
        PlatformBugCommentsTable.insert {
            it[PlatformBugCommentsTable.bugId] = bugId
            it[PlatformBugCommentsTable.authorId] = authorId
            it[PlatformBugCommentsTable.body] = req.body
            it[PlatformBugCommentsTable.mentions] = req.mentions
            it[PlatformBugCommentsTable.isInternal] = req.is_internal
            it[PlatformBugCommentsTable.createdAt] = Instant.now()
            it[PlatformBugCommentsTable.updatedAt] = Instant.now()
        }[PlatformBugCommentsTable.id].value
    }

    suspend fun listComments(bugId: UUID): List<BugCommentDto> = dbQuery {
        PlatformBugCommentsTable.selectAll()
            .where { PlatformBugCommentsTable.bugId eq bugId }
            .orderBy(PlatformBugCommentsTable.createdAt, SortOrder.ASC)
            .map { c ->
                val authorName = c[PlatformBugCommentsTable.authorId]?.let { aid ->
                    AppUsersTable.selectAll().where { AppUsersTable.id eq aid }
                        .singleOrNull()?.get(AppUsersTable.fullName)
                }
                BugCommentDto(
                    id = c[PlatformBugCommentsTable.id].value.toString(),
                    bug_id = bugId.toString(),
                    author_id = c[PlatformBugCommentsTable.authorId]?.toString(),
                    author_name = authorName,
                    body = c[PlatformBugCommentsTable.body],
                    mentions = c[PlatformBugCommentsTable.mentions],
                    is_internal = c[PlatformBugCommentsTable.isInternal],
                    created_at = c[PlatformBugCommentsTable.createdAt].toString(),
                )
            }
    }

    suspend fun listActivity(bugId: UUID): List<BugActivityDto> = dbQuery {
        PlatformBugActivityTable.selectAll()
            .where { PlatformBugActivityTable.bugId eq bugId }
            .orderBy(PlatformBugActivityTable.createdAt, SortOrder.DESC)
            .map { a ->
                val actorName = a[PlatformBugActivityTable.actorId]?.let { aid ->
                    AppUsersTable.selectAll().where { AppUsersTable.id eq aid }
                        .singleOrNull()?.get(AppUsersTable.fullName)
                }
                BugActivityDto(
                    id = a[PlatformBugActivityTable.id].value.toString(),
                    bug_id = bugId.toString(),
                    actor_id = a[PlatformBugActivityTable.actorId]?.toString(),
                    actor_name = actorName,
                    action = a[PlatformBugActivityTable.action],
                    field = a[PlatformBugActivityTable.field],
                    old_value = a[PlatformBugActivityTable.oldValue],
                    new_value = a[PlatformBugActivityTable.newValue],
                    created_at = a[PlatformBugActivityTable.createdAt].toString(),
                )
            }
    }

    private fun enrichBug(row: ResultRow): BugSummaryDto {
        val featureName = row[PlatformBugsTable.featureId]?.let { fid ->
            PlatformFeaturesTable.selectAll().where { PlatformFeaturesTable.id eq fid }
                .singleOrNull()?.get(PlatformFeaturesTable.name)
        }
        val assignedName = row[PlatformBugsTable.assignedTo]?.let { aid ->
            AppUsersTable.selectAll().where { AppUsersTable.id eq aid }
                .singleOrNull()?.get(AppUsersTable.fullName)
        }
        val reportedName = row[PlatformBugsTable.reportedBy]?.let { rid ->
            AppUsersTable.selectAll().where { AppUsersTable.id eq rid }
                .singleOrNull()?.get(AppUsersTable.fullName)
        }
        return BugSummaryDto(
            id = row[PlatformBugsTable.id].value.toString(),
            bug_id = row[PlatformBugsTable.bugId],
            feature_id = row[PlatformBugsTable.featureId]?.toString(),
            feature_name = featureName,
            screen_id = row[PlatformBugsTable.screenId]?.toString(),
            api_id = row[PlatformBugsTable.apiId]?.toString(),
            test_case_id = row[PlatformBugsTable.testCaseId]?.toString(),
            title = row[PlatformBugsTable.title],
            description = row[PlatformBugsTable.description],
            status = row[PlatformBugsTable.status],
            priority = row[PlatformBugsTable.priority],
            severity = row[PlatformBugsTable.severity],
            reported_by = row[PlatformBugsTable.reportedBy]?.toString(),
            reported_by_name = reportedName,
            assigned_to = row[PlatformBugsTable.assignedTo]?.toString(),
            assigned_to_name = assignedName,
            sla_due_at = row[PlatformBugsTable.slaDueAt]?.toString(),
            created_at = row[PlatformBugsTable.createdAt].toString(),
            updated_at = row[PlatformBugsTable.updatedAt].toString(),
        )
    }
}
