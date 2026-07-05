package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformTestCasesTable
import com.littlebridge.enrollplus.db.PlatformTestAttachmentsTable
import com.littlebridge.enrollplus.db.PlatformFeaturesTable
import com.littlebridge.enrollplus.db.AppUsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.util.UUID
import java.time.Instant

object TestCaseService {

    suspend fun list(
        page: Int = 1,
        pageSize: Int = 25,
        featureId: String? = null,
        status: String? = null,
        assignedTo: String? = null,
        priority: String? = null,
        environment: String? = null,
        search: String? = null,
    ): Pair<List<TestCaseDto>, Long> = dbQuery {
        val conditions = Op.build {
            (if (featureId != null) PlatformTestCasesTable.featureId eq UUID.fromString(featureId) else Op.TRUE) and
            (if (status != null) PlatformTestCasesTable.status eq status else Op.TRUE) and
            (if (assignedTo != null) PlatformTestCasesTable.assignedTo eq UUID.fromString(assignedTo) else Op.TRUE) and
            (if (priority != null) PlatformTestCasesTable.priority eq priority else Op.TRUE) and
            (if (environment != null) PlatformTestCasesTable.environment eq environment else Op.TRUE) and
            (if (search != null) {
                (PlatformTestCasesTable.title like "%$search%") or
                (PlatformTestCasesTable.caseId like "%$search%")
            } else Op.TRUE)
        }
        val total = PlatformTestCasesTable.selectAll().where { conditions }.count()
        val items = PlatformTestCasesTable.selectAll().where { conditions }
            .orderBy(PlatformTestCasesTable.createdAt, SortOrder.DESC)
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .map { row -> enrichTestCase(row) }
        items to total
    }

    suspend fun listMy(userId: UUID): List<TestCaseDto> = dbQuery {
        PlatformTestCasesTable.selectAll()
            .where { PlatformTestCasesTable.assignedTo eq userId }
            .orderBy(PlatformTestCasesTable.createdAt, SortOrder.DESC)
            .map { row -> enrichTestCase(row) }
    }

    suspend fun getById(id: UUID): TestCaseDetailDto? = dbQuery {
        val row = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.id eq id }
            .singleOrNull() ?: return@dbQuery null
        val tc = enrichTestCase(row)
        val attachments = PlatformTestAttachmentsTable.selectAll()
            .where { PlatformTestAttachmentsTable.testCaseId eq id }
            .map { a ->
                AttachmentDto(
                    id = a[PlatformTestAttachmentsTable.id].value.toString(),
                    file_name = a[PlatformTestAttachmentsTable.fileName],
                    file_url = a[PlatformTestAttachmentsTable.fileUrl],
                    file_type = a[PlatformTestAttachmentsTable.fileType],
                    mime_type = a[PlatformTestAttachmentsTable.mimeType],
                    file_size_bytes = a[PlatformTestAttachmentsTable.fileSizeBytes],
                    uploaded_by = a[PlatformTestAttachmentsTable.uploadedBy]?.toString(),
                    created_at = a[PlatformTestAttachmentsTable.createdAt].toString(),
                )
            }
        TestCaseDetailDto(tc, attachments)
    }

    suspend fun create(req: CreateTestCaseRequest, userId: UUID): UUID = dbQuery {
        val now = Instant.now()
        PlatformTestCasesTable.insert {
            it[PlatformTestCasesTable.caseId] = req.case_id
            it[PlatformTestCasesTable.featureId] = UUID.fromString(req.feature_id)
            it[PlatformTestCasesTable.screenId] = req.screen_id?.let { UUID.fromString(it) }
            it[PlatformTestCasesTable.apiId] = req.api_id?.let { UUID.fromString(it) }
            it[PlatformTestCasesTable.title] = req.title
            it[PlatformTestCasesTable.description] = req.description
            it[PlatformTestCasesTable.preconditions] = req.preconditions
            it[PlatformTestCasesTable.testSteps] = req.test_steps
            it[PlatformTestCasesTable.expectedResult] = req.expected_result
            it[PlatformTestCasesTable.priority] = req.priority
            it[PlatformTestCasesTable.testType] = req.test_type
            it[PlatformTestCasesTable.status] = "not_run"
            it[PlatformTestCasesTable.assignedTo] = req.assigned_to?.let { UUID.fromString(it) }
            it[PlatformTestCasesTable.buildVersion] = req.build_version
            it[PlatformTestCasesTable.environment] = req.environment
            it[PlatformTestCasesTable.devices] = req.devices
            it[PlatformTestCasesTable.osVersions] = req.os_versions
            it[PlatformTestCasesTable.platform] = req.platform
            it[PlatformTestCasesTable.createdAt] = now
            it[PlatformTestCasesTable.updatedAt] = now
            it[PlatformTestCasesTable.createdBy] = userId
        }[PlatformTestCasesTable.id].value
    }

    suspend fun update(id: UUID, req: UpdateTestCaseRequest): Boolean = dbQuery {
        PlatformTestCasesTable.update(where = { PlatformTestCasesTable.id eq id }) {
            req.title?.let { v -> it[PlatformTestCasesTable.title] = v }
            req.description?.let { v -> it[PlatformTestCasesTable.description] = v }
            req.preconditions?.let { v -> it[PlatformTestCasesTable.preconditions] = v }
            req.test_steps?.let { v -> it[PlatformTestCasesTable.testSteps] = v }
            req.expected_result?.let { v -> it[PlatformTestCasesTable.expectedResult] = v }
            req.priority?.let { v -> it[PlatformTestCasesTable.priority] = v }
            req.test_type?.let { v -> it[PlatformTestCasesTable.testType] = v }
            req.assigned_to?.let { v -> it[PlatformTestCasesTable.assignedTo] = UUID.fromString(v) }
            req.build_version?.let { v -> it[PlatformTestCasesTable.buildVersion] = v }
            req.environment?.let { v -> it[PlatformTestCasesTable.environment] = v }
            req.devices?.let { v -> it[PlatformTestCasesTable.devices] = v }
            req.os_versions?.let { v -> it[PlatformTestCasesTable.osVersions] = v }
            req.platform?.let { v -> it[PlatformTestCasesTable.platform] = v }
            req.screen_id?.let { v -> it[PlatformTestCasesTable.screenId] = UUID.fromString(v) }
            req.api_id?.let { v -> it[PlatformTestCasesTable.apiId] = UUID.fromString(v) }
            it[PlatformTestCasesTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun updateStatus(id: UUID, req: UpdateTestCaseStatusRequest, userId: UUID): Boolean = dbQuery {
        PlatformTestCasesTable.update(where = { PlatformTestCasesTable.id eq id }) {
            it[PlatformTestCasesTable.status] = req.status
            it[PlatformTestCasesTable.failureReason] = req.failure_reason
            req.build_version?.let { v -> it[PlatformTestCasesTable.buildVersion] = v }
            req.environment?.let { v -> it[PlatformTestCasesTable.environment] = v }
            it[PlatformTestCasesTable.lastTestedAt] = Instant.now()
            it[PlatformTestCasesTable.lastTestedBy] = userId
            it[PlatformTestCasesTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        PlatformTestCasesTable.deleteWhere { PlatformTestCasesTable.id eq id } > 0
    }

    suspend fun listAttachments(testCaseId: UUID): List<AttachmentDto> = dbQuery {
        PlatformTestAttachmentsTable.selectAll()
            .where { PlatformTestAttachmentsTable.testCaseId eq testCaseId }
            .map { a ->
                AttachmentDto(
                    id = a[PlatformTestAttachmentsTable.id].value.toString(),
                    file_name = a[PlatformTestAttachmentsTable.fileName],
                    file_url = a[PlatformTestAttachmentsTable.fileUrl],
                    file_type = a[PlatformTestAttachmentsTable.fileType],
                    mime_type = a[PlatformTestAttachmentsTable.mimeType],
                    file_size_bytes = a[PlatformTestAttachmentsTable.fileSizeBytes],
                    uploaded_by = a[PlatformTestAttachmentsTable.uploadedBy]?.toString(),
                    created_at = a[PlatformTestAttachmentsTable.createdAt].toString(),
                )
            }
    }

    private fun enrichTestCase(row: ResultRow): TestCaseDto {
        val featureName = row[PlatformTestCasesTable.featureId].let { fid ->
            PlatformFeaturesTable.selectAll().where { PlatformFeaturesTable.id eq fid }
                .singleOrNull()?.get(PlatformFeaturesTable.name)
        }
        val assignedName = row[PlatformTestCasesTable.assignedTo]?.let { aid ->
            AppUsersTable.selectAll().where { AppUsersTable.id eq aid }
                .singleOrNull()?.get(AppUsersTable.fullName)
        }
        return TestCaseDto(
            id = row[PlatformTestCasesTable.id].value.toString(),
            case_id = row[PlatformTestCasesTable.caseId],
            feature_id = row[PlatformTestCasesTable.featureId].toString(),
            feature_name = featureName,
            screen_id = row[PlatformTestCasesTable.screenId]?.toString(),
            api_id = row[PlatformTestCasesTable.apiId]?.toString(),
            title = row[PlatformTestCasesTable.title],
            description = row[PlatformTestCasesTable.description],
            preconditions = row[PlatformTestCasesTable.preconditions],
            test_steps = row[PlatformTestCasesTable.testSteps],
            expected_result = row[PlatformTestCasesTable.expectedResult],
            priority = row[PlatformTestCasesTable.priority],
            test_type = row[PlatformTestCasesTable.testType],
            status = row[PlatformTestCasesTable.status],
            assigned_to = row[PlatformTestCasesTable.assignedTo]?.toString(),
            assigned_to_name = assignedName,
            build_version = row[PlatformTestCasesTable.buildVersion],
            environment = row[PlatformTestCasesTable.environment],
            devices = row[PlatformTestCasesTable.devices],
            os_versions = row[PlatformTestCasesTable.osVersions],
            platform = row[PlatformTestCasesTable.platform],
            last_tested_at = row[PlatformTestCasesTable.lastTestedAt]?.toString(),
            last_tested_by = row[PlatformTestCasesTable.lastTestedBy]?.toString(),
            failure_reason = row[PlatformTestCasesTable.failureReason],
            metadata = row[PlatformTestCasesTable.metadata],
            created_at = row[PlatformTestCasesTable.createdAt].toString(),
            updated_at = row[PlatformTestCasesTable.updatedAt].toString(),
        )
    }
}
