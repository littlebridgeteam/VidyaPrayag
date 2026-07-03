/*
 * File: StudentTransferService.kt
 * Module: feature.organization
 *
 * Handles cross-branch student transfers (MULTI_BRANCH_SPEC.md §7).
 *
 * Transfer workflow:
 *   1. Org admin (or school admin) initiates a transfer request → status=pending
 *   2. Org admin approves → status=approved
 *   3. On approval, the student's school_id is migrated to the new branch,
 *      related children/enrollment records are updated, and the transfer
 *      record is marked completed.
 *   4. Alternatively, the request can be rejected → status=rejected
 *
 * All mutations happen inside a single transaction for atomicity.
 */
package com.littlebridge.enrollplus.feature.organization

import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.EnrollmentsTable
import com.littlebridge.enrollplus.db.SchoolsTable
import com.littlebridge.enrollplus.db.StudentTransfersTable
import com.littlebridge.enrollplus.db.StudentsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// ── DTOs ─────────────────────────────────────────────────────────────────────

@Serializable
data class TransferRequestDto(
    val id: String,
    val studentId: String,
    val studentName: String,
    val fromSchoolId: String,
    val fromSchoolName: String,
    val toSchoolId: String,
    val toSchoolName: String,
    val transferDate: String,
    val reason: String? = null,
    val status: String,
    val approvedBy: String? = null,
    val createdAt: String,
)

@Serializable
data class InitiateTransferRequest(
    val studentId: String,
    val fromSchoolId: String,
    val toSchoolId: String,
    val transferDate: String,
    val reason: String? = null,
)

// ── Service ──────────────────────────────────────────────────────────────────

class StudentTransferService {

    suspend fun initiateTransfer(
        req: InitiateTransferRequest,
        approvedBy: UUID
    ): TransferRequestDto = dbQuery {
        val studentId = UUID.fromString(req.studentId)
        val fromSchoolId = UUID.fromString(req.fromSchoolId)
        val toSchoolId = UUID.fromString(req.toSchoolId)

        // Validate student exists and belongs to fromSchool
        val student = StudentsTable.selectAll()
            .where { (StudentsTable.id eq studentId) and (StudentsTable.schoolId eq fromSchoolId) }
            .singleOrNull() ?: throw IllegalArgumentException("Student not found in source school")

        // Validate target school exists and is active
        val toSchool = SchoolsTable.selectAll()
            .where { (SchoolsTable.id eq toSchoolId) and (SchoolsTable.isActive eq true) }
            .singleOrNull() ?: throw IllegalArgumentException("Target school not found or inactive")

        // Prevent transfer to the same school
        if (fromSchoolId == toSchoolId) {
            throw IllegalArgumentException("Cannot transfer to the same school")
        }

        // Check for existing pending transfer for this student
        val existingPending = StudentTransfersTable.selectAll()
            .where {
                (StudentTransfersTable.studentId eq studentId) and
                    (StudentTransfersTable.status eq "pending")
            }
            .singleOrNull()
        if (existingPending != null) {
            throw IllegalStateException("A pending transfer already exists for this student")
        }

        val now = Instant.now()
        val transferId = UUID.randomUUID()
        StudentTransfersTable.insert {
            it[id] = transferId
            it[StudentTransfersTable.studentId] = studentId
            it[StudentTransfersTable.fromSchoolId] = fromSchoolId
            it[StudentTransfersTable.toSchoolId] = toSchoolId
            it[transferDate] = LocalDate.parse(req.transferDate)
            it[reason] = req.reason?.trim()
            it[status] = "pending"
            it[StudentTransfersTable.approvedBy] = null
            it[createdAt] = now
        }

        rowToTransferDto(
            id = transferId,
            studentId = studentId,
            studentName = student[StudentsTable.fullName],
            fromSchoolId = fromSchoolId,
            fromSchoolName = SchoolsTable.selectAll()
                .where { SchoolsTable.id eq fromSchoolId }
                .single()[SchoolsTable.name],
            toSchoolId = toSchoolId,
            toSchoolName = toSchool[SchoolsTable.name],
            transferDate = req.transferDate,
            reason = req.reason?.trim(),
            status = "pending",
            approvedBy = null,
            createdAt = now.toString(),
        )
    }

    suspend fun approveTransfer(transferId: UUID, approvedBy: UUID): TransferRequestDto = dbQuery {
        val transfer = StudentTransfersTable.selectAll()
            .where { StudentTransfersTable.id eq transferId }
            .singleOrNull() ?: throw IllegalArgumentException("Transfer request not found")

        if (transfer[StudentTransfersTable.status] != "pending") {
            throw IllegalStateException("Transfer is not pending (current: ${transfer[StudentTransfersTable.status]})")
        }

        val studentId = transfer[StudentTransfersTable.studentId]
        val fromSchoolId = transfer[StudentTransfersTable.fromSchoolId]
        val toSchoolId = transfer[StudentTransfersTable.toSchoolId]
        val now = Instant.now()

        // 1. Migrate student to new school
        StudentsTable.update({ StudentsTable.id eq studentId }) {
            it[schoolId] = toSchoolId
        }

        // 2. Mark old enrollments as transferred
        EnrollmentsTable.update({
            (EnrollmentsTable.studentId eq studentId) and
                (EnrollmentsTable.status eq "active")
        }) {
            it[status] = "transferred"
            it[endDate] = LocalDate.now()
        }

        // 3. Update children records linked to this student
        val studentCode = StudentsTable.selectAll()
            .where { StudentsTable.id eq studentId }
            .single()[StudentsTable.studentCode]

        ChildrenTable.update({
            (ChildrenTable.studentCode eq studentCode) and
                (ChildrenTable.schoolId eq fromSchoolId)
        }) {
            it[schoolId] = toSchoolId
            it[updatedAt] = now
        }

        // 4. Mark transfer as completed
        StudentTransfersTable.update({ StudentTransfersTable.id eq transferId }) {
            it[status] = "completed"
            it[StudentTransfersTable.approvedBy] = approvedBy
        }

        val studentName = StudentsTable.selectAll()
            .where { StudentsTable.id eq studentId }
            .single()[StudentsTable.fullName]

        val fromName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq fromSchoolId }
            .single()[SchoolsTable.name]
        val toName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq toSchoolId }
            .single()[SchoolsTable.name]

        rowToTransferDto(
            id = transferId,
            studentId = studentId,
            studentName = studentName,
            fromSchoolId = fromSchoolId,
            fromSchoolName = fromName,
            toSchoolId = toSchoolId,
            toSchoolName = toName,
            transferDate = transfer[StudentTransfersTable.transferDate].toString(),
            reason = transfer[StudentTransfersTable.reason],
            status = "completed",
            approvedBy = approvedBy.toString(),
            createdAt = transfer[StudentTransfersTable.createdAt].toString(),
        )
    }

    suspend fun rejectTransfer(transferId: UUID, rejectedBy: UUID): TransferRequestDto = dbQuery {
        val transfer = StudentTransfersTable.selectAll()
            .where { StudentTransfersTable.id eq transferId }
            .singleOrNull() ?: throw IllegalArgumentException("Transfer request not found")

        if (transfer[StudentTransfersTable.status] != "pending") {
            throw IllegalStateException("Transfer is not pending (current: ${transfer[StudentTransfersTable.status]})")
        }

        StudentTransfersTable.update({ StudentTransfersTable.id eq transferId }) {
            it[status] = "rejected"
            it[StudentTransfersTable.approvedBy] = rejectedBy
        }

        val studentId = transfer[StudentTransfersTable.studentId]
        val fromSchoolId = transfer[StudentTransfersTable.fromSchoolId]
        val toSchoolId = transfer[StudentTransfersTable.toSchoolId]

        val studentName = StudentsTable.selectAll()
            .where { StudentsTable.id eq studentId }
            .single()[StudentsTable.fullName]

        val fromName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq fromSchoolId }
            .single()[SchoolsTable.name]
        val toName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq toSchoolId }
            .single()[SchoolsTable.name]

        rowToTransferDto(
            id = transferId,
            studentId = studentId,
            studentName = studentName,
            fromSchoolId = fromSchoolId,
            fromSchoolName = fromName,
            toSchoolId = toSchoolId,
            toSchoolName = toName,
            transferDate = transfer[StudentTransfersTable.transferDate].toString(),
            reason = transfer[StudentTransfersTable.reason],
            status = "rejected",
            approvedBy = rejectedBy.toString(),
            createdAt = transfer[StudentTransfersTable.createdAt].toString(),
        )
    }

    suspend fun listTransfers(
        orgId: UUID,
        status: String? = null
    ): List<TransferRequestDto> = dbQuery {
        // Get all branch school IDs for this org
        val branchIds = SchoolsTable.selectAll()
            .where { (SchoolsTable.organizationId eq orgId) and (SchoolsTable.isActive eq true) }
            .map { it[SchoolsTable.id].value }

        if (branchIds.isEmpty()) return@dbQuery emptyList()

        val query = StudentTransfersTable.selectAll()
            .where {
                (StudentTransfersTable.fromSchoolId inList branchIds) or
                    (StudentTransfersTable.toSchoolId inList branchIds)
            }

        val filtered = if (status != null) {
            query.andWhere { StudentTransfersTable.status eq status }
        } else query

        filtered.orderBy(StudentTransfersTable.createdAt, SortOrder.DESC)
            .map { row -> transferRowToDto(row) }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun transferRowToDto(row: ResultRow): TransferRequestDto {
        val studentId = row[StudentTransfersTable.studentId]
        val fromSchoolId = row[StudentTransfersTable.fromSchoolId]
        val toSchoolId = row[StudentTransfersTable.toSchoolId]

        val studentName = StudentsTable.selectAll()
            .where { StudentsTable.id eq studentId }
            .singleOrNull()?.get(StudentsTable.fullName) ?: "Unknown"

        val fromName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq fromSchoolId }
            .singleOrNull()?.get(SchoolsTable.name) ?: "Unknown"

        val toName = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq toSchoolId }
            .singleOrNull()?.get(SchoolsTable.name) ?: "Unknown"

        return TransferRequestDto(
            id = row[StudentTransfersTable.id].value.toString(),
            studentId = studentId.toString(),
            studentName = studentName,
            fromSchoolId = fromSchoolId.toString(),
            fromSchoolName = fromName,
            toSchoolId = toSchoolId.toString(),
            toSchoolName = toName,
            transferDate = row[StudentTransfersTable.transferDate].toString(),
            reason = row[StudentTransfersTable.reason],
            status = row[StudentTransfersTable.status],
            approvedBy = row[StudentTransfersTable.approvedBy]?.toString(),
            createdAt = row[StudentTransfersTable.createdAt].toString(),
        )
    }

    private fun rowToTransferDto(
        id: UUID,
        studentId: UUID,
        studentName: String,
        fromSchoolId: UUID,
        fromSchoolName: String,
        toSchoolId: UUID,
        toSchoolName: String,
        transferDate: String,
        reason: String?,
        status: String,
        approvedBy: String?,
        createdAt: String,
    ): TransferRequestDto = TransferRequestDto(
        id = id.toString(),
        studentId = studentId.toString(),
        studentName = studentName,
        fromSchoolId = fromSchoolId.toString(),
        fromSchoolName = fromSchoolName,
        toSchoolId = toSchoolId.toString(),
        toSchoolName = toSchoolName,
        transferDate = transferDate,
        reason = reason,
        status = status,
        approvedBy = approvedBy,
        createdAt = createdAt,
    )
}
