/*
 * File: OrganizationService.kt
 * Module: feature.organization
 *
 * Core service for Multi-Branch / School Chain Support (MULTI_BRANCH_SPEC.md).
 *
 * Handles:
 *   - Organization CRUD (super admin creates/edits orgs)
 *   - Branch management (link/unlink schools to an org, set branch names)
 *   - Org admin assignment (promote a school_admin to org_admin)
 *   - Aggregate dashboard (cross-branch student/teacher/fee counts)
 *   - Branch comparison (side-by-side metrics)
 *   - Shared settings (org-wide configuration propagated to branches)
 *
 * All queries are org-scoped via the OrgContext resolved by SchoolAccess.
 */
package com.littlebridge.enrollplus.feature.organization

import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SchoolOrganizationsTable
import com.littlebridge.enrollplus.db.SchoolsTable
import com.littlebridge.enrollplus.db.StudentsTable
import com.littlebridge.enrollplus.db.FeeRecordsTable
import com.littlebridge.enrollplus.db.AttendanceRecordsTable
import com.littlebridge.enrollplus.db.FacultyTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

// ── DTOs ─────────────────────────────────────────────────────────────────────

@Serializable
data class OrganizationDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val isActive: Boolean = true,
    val branchCount: Int = 0,
    val totalStudents: Int = 0,
    val totalTeachers: Int = 0,
    val createdAt: String? = null,
)

@Serializable
data class CreateOrganizationRequest(
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
)

@Serializable
data class UpdateOrganizationRequest(
    val name: String? = null,
    val description: String? = null,
    val logoUrl: String? = null,
    val isActive: Boolean? = null,
)

@Serializable
data class BranchDto(
    val schoolId: String,
    val schoolName: String,
    val branchName: String? = null,
    val city: String,
    val district: String,
    val studentCount: Int = 0,
    val teacherCount: Int = 0,
    val isActive: Boolean = true,
)

@Serializable
data class LinkBranchRequest(
    val schoolId: String,
    val branchName: String? = null,
)

@Serializable
data class PromoteOrgAdminRequest(
    val userId: String,
)

@Serializable
data class BranchSummaryDto(
    val schoolId: String,
    val schoolName: String,
    val branchName: String? = null,
    val studentCount: Int,
    val teacherCount: Int,
    val totalFees: Double,
    val collectedFees: Double,
    val pendingFees: Double,
    val attendanceRate: Double,
)

@Serializable
data class OrgDashboardDto(
    val organization: OrganizationDto,
    val branches: List<BranchSummaryDto>,
    val aggregate: AggregateStatsDto,
)

@Serializable
data class AggregateStatsDto(
    val totalBranches: Int,
    val totalStudents: Int,
    val totalTeachers: Int,
    val totalFees: Double,
    val collectedFees: Double,
    val pendingFees: Double,
    val averageAttendance: Double,
)

@Serializable
data class BranchComparisonDto(
    val branches: List<BranchSummaryDto>,
    val bestAttendanceBranch: String? = null,
    val bestFeeCollectionBranch: String? = null,
)

// ── Service ──────────────────────────────────────────────────────────────────

class OrganizationService {

    // ── Organization CRUD ────────────────────────────────────────────────

    suspend fun createOrganization(req: CreateOrganizationRequest): OrganizationDto = dbQuery {
        val now = Instant.now()
        val orgId = UUID.randomUUID()
        SchoolOrganizationsTable.insert {
            it[id] = orgId
            it[name] = req.name.trim()
            it[description] = req.description?.trim()
            it[logoUrl] = req.logoUrl
            it[isActive] = true
            it[createdAt] = now
            it[updatedAt] = now
        }
        rowToOrgDto(
            id = orgId,
            name = req.name.trim(),
            description = req.description?.trim(),
            logoUrl = req.logoUrl,
            isActive = true,
            createdAt = now.toString(),
        )
    }

    suspend fun updateOrganization(orgId: UUID, req: UpdateOrganizationRequest): OrganizationDto = dbQuery {
        val now = Instant.now()
        val existing = SchoolOrganizationsTable.selectAll()
            .where { SchoolOrganizationsTable.id eq orgId }
            .singleOrNull() ?: throw IllegalStateException("Organization not found")

        SchoolOrganizationsTable.update({ SchoolOrganizationsTable.id eq orgId }) {
            req.name?.let { n -> it[name] = n.trim() }
            req.description?.let { d -> it[description] = d.trim() }
            req.logoUrl?.let { u -> it[logoUrl] = u }
            req.isActive?.let { a -> it[isActive] = a }
            it[updatedAt] = now
        }

        val updated = SchoolOrganizationsTable.selectAll()
            .where { SchoolOrganizationsTable.id eq orgId }
            .single()
        rowToOrgDto(
            id = updated[SchoolOrganizationsTable.id].value,
            name = updated[SchoolOrganizationsTable.name],
            description = updated[SchoolOrganizationsTable.description],
            logoUrl = updated[SchoolOrganizationsTable.logoUrl],
            isActive = updated[SchoolOrganizationsTable.isActive],
            createdAt = updated[SchoolOrganizationsTable.createdAt].toString(),
        )
    }

    suspend fun getOrganization(orgId: UUID): OrganizationDto = dbQuery {
        val row = SchoolOrganizationsTable.selectAll()
            .where { SchoolOrganizationsTable.id eq orgId }
            .singleOrNull() ?: throw IllegalStateException("Organization not found")

        val branchCount = SchoolsTable.selectAll()
            .where { (SchoolsTable.organizationId eq orgId) and (SchoolsTable.isActive eq true) }
            .count().toInt()

        val branchIds = SchoolsTable.selectAll()
            .where { (SchoolsTable.organizationId eq orgId) and (SchoolsTable.isActive eq true) }
            .map { it[SchoolsTable.id].value }

        val totalStudents = if (branchIds.isNotEmpty()) {
            StudentsTable.selectAll()
                .where { (StudentsTable.schoolId inList branchIds) and (StudentsTable.isActive eq true) }
                .count().toInt()
        } else 0

        val totalTeachers = if (branchIds.isNotEmpty()) {
            FacultyTable.selectAll()
                .where { FacultyTable.schoolId inList branchIds }
                .count().toInt()
        } else 0

        rowToOrgDto(
            id = row[SchoolOrganizationsTable.id].value,
            name = row[SchoolOrganizationsTable.name],
            description = row[SchoolOrganizationsTable.description],
            logoUrl = row[SchoolOrganizationsTable.logoUrl],
            isActive = row[SchoolOrganizationsTable.isActive],
            createdAt = row[SchoolOrganizationsTable.createdAt].toString(),
            branchCount = branchCount,
            totalStudents = totalStudents,
            totalTeachers = totalTeachers,
        )
    }

    suspend fun listOrganizations(): List<OrganizationDto> = dbQuery {
        SchoolOrganizationsTable.selectAll()
            .orderBy(SchoolOrganizationsTable.createdAt, SortOrder.DESC)
            .map { row ->
                val orgId = row[SchoolOrganizationsTable.id].value
                val branchCount = SchoolsTable.selectAll()
                    .where { (SchoolsTable.organizationId eq orgId) and (SchoolsTable.isActive eq true) }
                    .count().toInt()
                rowToOrgDto(
                    id = orgId,
                    name = row[SchoolOrganizationsTable.name],
                    description = row[SchoolOrganizationsTable.description],
                    logoUrl = row[SchoolOrganizationsTable.logoUrl],
                    isActive = row[SchoolOrganizationsTable.isActive],
                    createdAt = row[SchoolOrganizationsTable.createdAt].toString(),
                    branchCount = branchCount,
                )
            }
    }

    // ── Branch Management ────────────────────────────────────────────────

    suspend fun linkBranch(orgId: UUID, req: LinkBranchRequest): BranchDto = dbQuery {
        val schoolId = UUID.fromString(req.schoolId)
        val school = SchoolsTable.selectAll()
            .where { SchoolsTable.id eq schoolId }
            .singleOrNull() ?: throw IllegalArgumentException("School not found")

        // Check if school is already linked to another org
        val existingOrg = school[SchoolsTable.organizationId]
        if (existingOrg != null && existingOrg != orgId) {
            throw IllegalStateException("School is already linked to another organization")
        }

        val now = Instant.now()
        SchoolsTable.update({ SchoolsTable.id eq schoolId }) {
            it[organizationId] = orgId
            req.branchName?.let { bn -> it[branchName] = bn.trim() }
            it[updatedAt] = now
        }

        val studentCount = StudentsTable.selectAll()
            .where { (StudentsTable.schoolId eq schoolId) and (StudentsTable.isActive eq true) }
            .count().toInt()

        val teacherCount = FacultyTable.selectAll()
            .where { FacultyTable.schoolId eq schoolId }
            .count().toInt()

        BranchDto(
            schoolId = schoolId.toString(),
            schoolName = school[SchoolsTable.name],
            branchName = req.branchName?.trim() ?: school[SchoolsTable.branchName],
            city = school[SchoolsTable.city],
            district = school[SchoolsTable.district],
            studentCount = studentCount,
            teacherCount = teacherCount,
            isActive = school[SchoolsTable.isActive],
        )
    }

    suspend fun unlinkBranch(orgId: UUID, schoolId: UUID): Boolean = dbQuery {
        val now = Instant.now()
        val updated = SchoolsTable.update({
            (SchoolsTable.id eq schoolId) and (SchoolsTable.organizationId eq orgId)
        }) {
            it[organizationId] = null
            it[branchName] = null
            it[updatedAt] = now
        }
        updated > 0
    }

    suspend fun listBranches(orgId: UUID): List<BranchDto> = dbQuery {
        SchoolsTable.selectAll()
            .where { (SchoolsTable.organizationId eq orgId) and (SchoolsTable.isActive eq true) }
            .orderBy(SchoolsTable.name)
            .map { row ->
                val schoolId = row[SchoolsTable.id].value
                val studentCount = StudentsTable.selectAll()
                    .where { (StudentsTable.schoolId eq schoolId) and (StudentsTable.isActive eq true) }
                    .count().toInt()
                val teacherCount = FacultyTable.selectAll()
                    .where { FacultyTable.schoolId eq schoolId }
                    .count().toInt()
                BranchDto(
                    schoolId = schoolId.toString(),
                    schoolName = row[SchoolsTable.name],
                    branchName = row[SchoolsTable.branchName],
                    city = row[SchoolsTable.city],
                    district = row[SchoolsTable.district],
                    studentCount = studentCount,
                    teacherCount = teacherCount,
                    isActive = row[SchoolsTable.isActive],
                )
            }
    }

    // ── Org Admin Promotion ──────────────────────────────────────────────

    suspend fun promoteOrgAdmin(orgId: UUID, req: PromoteOrgAdminRequest): Boolean = dbQuery {
        val userId = UUID.fromString(req.userId)
        val userRow = AppUsersTable.selectAll()
            .where { AppUsersTable.id eq userId }
            .singleOrNull() ?: throw IllegalArgumentException("User not found")

        // Only school_admin or admin roles can be promoted
        val role = userRow[AppUsersTable.role]
        if (role != "school_admin" && role != "admin") {
            throw IllegalArgumentException("Only school admins can be promoted to org admin")
        }

        val now = Instant.now()
        AppUsersTable.update({ AppUsersTable.id eq userId }) {
            it[organizationId] = orgId
            it[orgAdminRole] = "org_admin"
            it[updatedAt] = now
        }
        true
    }

    suspend fun revokeOrgAdmin(userId: UUID): Boolean = dbQuery {
        val now = Instant.now()
        val updated = AppUsersTable.update({
            (AppUsersTable.id eq userId) and (AppUsersTable.orgAdminRole.isNotNull())
        }) {
            it[organizationId] = null
            it[orgAdminRole] = null
            it[updatedAt] = now
        }
        updated > 0
    }

    // ── Aggregate Dashboard ──────────────────────────────────────────────

    suspend fun getDashboard(orgId: UUID): OrgDashboardDto = dbQuery {
        val org = getOrganization(orgId)

        val branchIds = SchoolsTable.selectAll()
            .where { (SchoolsTable.organizationId eq orgId) and (SchoolsTable.isActive eq true) }
            .map { it[SchoolsTable.id].value }

        val branches = if (branchIds.isNotEmpty()) {
            branchIds.map { schoolId ->
                val schoolRow = SchoolsTable.selectAll()
                    .where { SchoolsTable.id eq schoolId }
                    .single()

                val studentCount = StudentsTable.selectAll()
                    .where { (StudentsTable.schoolId eq schoolId) and (StudentsTable.isActive eq true) }
                    .count().toInt()

                val teacherCount = FacultyTable.selectAll()
                    .where { FacultyTable.schoolId eq schoolId }
                    .count().toInt()

                val feeRows = FeeRecordsTable.selectAll()
                    .where { FeeRecordsTable.schoolId eq schoolId }
                    .toList()

                val totalFees = feeRows.sumOf { it.getOrNull(FeeRecordsTable.amount) ?: 0.0 }
                val collectedFees = feeRows.filter { it.getOrNull(FeeRecordsTable.status) == "PAID" }
                    .sumOf { it.getOrNull(FeeRecordsTable.amount) ?: 0.0 }

                val attendanceRows = AttendanceRecordsTable.selectAll()
                    .where { AttendanceRecordsTable.schoolId eq schoolId }
                    .toList()
                val presentCount = attendanceRows.count { it.getOrNull(AttendanceRecordsTable.status) == "present" }
                val attendanceRate = if (attendanceRows.isNotEmpty()) {
                    presentCount.toDouble() / attendanceRows.size * 100.0
                } else 0.0

                BranchSummaryDto(
                    schoolId = schoolId.toString(),
                    schoolName = schoolRow[SchoolsTable.name],
                    branchName = schoolRow[SchoolsTable.branchName],
                    studentCount = studentCount,
                    teacherCount = teacherCount,
                    totalFees = totalFees,
                    collectedFees = collectedFees,
                    pendingFees = totalFees - collectedFees,
                    attendanceRate = attendanceRate,
                )
            }
        } else emptyList()

        val aggregate = AggregateStatsDto(
            totalBranches = branches.size,
            totalStudents = branches.sumOf { it.studentCount },
            totalTeachers = branches.sumOf { it.teacherCount },
            totalFees = branches.sumOf { it.totalFees },
            collectedFees = branches.sumOf { it.collectedFees },
            pendingFees = branches.sumOf { it.pendingFees },
            averageAttendance = if (branches.isNotEmpty()) {
                branches.map { it.attendanceRate }.average()
            } else 0.0,
        )

        OrgDashboardDto(organization = org, branches = branches, aggregate = aggregate)
    }

    // ── Branch Comparison ────────────────────────────────────────────────

    suspend fun compareBranches(orgId: UUID): BranchComparisonDto = dbQuery {
        val dashboard = getDashboard(orgId)
        val branches = dashboard.branches

        val bestAttendance = branches.maxByOrNull { it.attendanceRate }
        val bestFee = branches.maxByOrNull {
            if (it.totalFees > 0) it.collectedFees / it.totalFees else 0.0
        }

        BranchComparisonDto(
            branches = branches,
            bestAttendanceBranch = bestAttendance?.schoolName,
            bestFeeCollectionBranch = bestFee?.schoolName,
        )
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun rowToOrgDto(
        id: UUID,
        name: String,
        description: String?,
        logoUrl: String?,
        isActive: Boolean,
        createdAt: String?,
        branchCount: Int = 0,
        totalStudents: Int = 0,
        totalTeachers: Int = 0,
    ): OrganizationDto = OrganizationDto(
        id = id.toString(),
        name = name,
        description = description,
        logoUrl = logoUrl,
        isActive = isActive,
        branchCount = branchCount,
        totalStudents = totalStudents,
        totalTeachers = totalTeachers,
        createdAt = createdAt,
    )
}
