/*
 * File: OrganizationRouting.kt
 * Module: feature.organization
 *
 * API endpoints for Multi-Branch / School Chain Support (MULTI_BRANCH_SPEC.md §8).
 *
 *   Super Admin (JWT + requirePlatformAdmin):
 *     GET    /api/admin/organizations                         — list all orgs
 *     POST   /api/admin/organizations                         — create org
 *     PUT    /api/admin/organizations/{orgId}                 — update org
 *     GET    /api/admin/organizations/{orgId}                 — get org detail
 *     POST   /api/admin/organizations/{orgId}/branches        — link a school as branch
 *     DELETE /api/admin/organizations/{orgId}/branches/{schoolId} — unlink branch
 *     POST   /api/admin/organizations/{orgId}/promote-admin   — promote user to org admin
 *     DELETE /api/admin/organizations/{orgId}/admins/{userId} — revoke org admin
 *
 *   Org Admin (JWT + requireOrgAdminContext):
 *     GET    /api/v1/organization/dashboard                   — aggregate dashboard
 *     GET    /api/v1/organization/branches                    — list branches
 *     GET    /api/v1/organization/compare                     — branch comparison
 *     GET    /api/v1/organization/transfers                   — list transfers
 *     POST   /api/v1/organization/transfers                   — initiate transfer
 *     POST   /api/v1/organization/transfers/{id}/approve      — approve transfer
 *     POST   /api/v1/organization/transfers/{id}/reject       — reject transfer
 */
package com.littlebridge.enrollplus.feature.organization

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.requireOrgAdminContext
import com.littlebridge.enrollplus.core.requirePlatformAdmin
import com.littlebridge.enrollplus.feature.notifications.Notify
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.organizationRouting() {
    // ── Super Admin endpoints ────────────────────────────────────────────
    authenticate("jwt") {
        route("/api/admin/organizations") {

            get {
                val uid = call.requirePlatformAdmin() ?: return@get
                val orgs = OrganizationService().listOrganizations()
                call.ok(orgs, "Organizations")
            }

            post {
                val uid = call.requirePlatformAdmin() ?: return@post
                val req = runCatching { call.receive<CreateOrganizationRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                try {
                    val org = OrganizationService().createOrganization(req)
                    call.ok(org, "Organization created")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "Invalid input", HttpStatusCode.BadRequest, "INVALID_INPUT")
                }
            }

            get("/{orgId}") {
                val uid = call.requirePlatformAdmin() ?: return@get
                val orgId = call.parameters["orgId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid organization id"); return@get }

                try {
                    val org = OrganizationService().getOrganization(orgId)
                    call.ok(org, "Organization details")
                } catch (e: IllegalStateException) {
                    call.fail(e.message ?: "Organization not found", HttpStatusCode.NotFound, "ORG_NOT_FOUND")
                }
            }

            put("/{orgId}") {
                val uid = call.requirePlatformAdmin() ?: return@put
                val orgId = call.parameters["orgId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid organization id"); return@put }

                val req = runCatching { call.receive<UpdateOrganizationRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@put }

                try {
                    val org = OrganizationService().updateOrganization(orgId, req)
                    call.ok(org, "Organization updated")
                } catch (e: IllegalStateException) {
                    call.fail(e.message ?: "Organization not found", HttpStatusCode.NotFound, "ORG_NOT_FOUND")
                }
            }

            post("/{orgId}/branches") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val orgId = call.parameters["orgId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid organization id"); return@post }

                val req = runCatching { call.receive<LinkBranchRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                try {
                    val branch = OrganizationService().linkBranch(orgId, req)
                    call.ok(branch, "Branch linked")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "School not found", HttpStatusCode.NotFound, "SCHOOL_NOT_FOUND")
                } catch (e: IllegalStateException) {
                    call.fail(e.message ?: "School already linked", HttpStatusCode.Conflict, "ALREADY_LINKED")
                }
            }

            delete("/{orgId}/branches/{schoolId}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val orgId = call.parameters["orgId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid organization id"); return@delete }
                val schoolId = call.parameters["schoolId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid school id"); return@delete }

                val removed = OrganizationService().unlinkBranch(orgId, schoolId)
                if (removed) call.ok(mapOf("removed" to true), "Branch unlinked")
                else call.fail("Branch not found in this organization", HttpStatusCode.NotFound, "BRANCH_NOT_FOUND")
            }

            post("/{orgId}/promote-admin") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val orgId = call.parameters["orgId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid organization id"); return@post }

                val req = runCatching { call.receive<PromoteOrgAdminRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                try {
                    OrganizationService().promoteOrgAdmin(orgId, req)
                    call.ok(mapOf("promoted" to true), "User promoted to org admin")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "User not found", HttpStatusCode.BadRequest, "INVALID_USER")
                }
            }

            delete("/{orgId}/admins/{userId}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val userId = call.parameters["userId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid user id"); return@delete }

                val revoked = OrganizationService().revokeOrgAdmin(userId)
                if (revoked) call.ok(mapOf("revoked" to true), "Org admin revoked")
                else call.fail("User is not an org admin", HttpStatusCode.NotFound, "NOT_ORG_ADMIN")
            }
        }

        // ── Org Admin endpoints ──────────────────────────────────────────
        route("/api/v1/organization") {

            get("/dashboard") {
                val ctx = call.requireOrgAdminContext() ?: return@get
                val dashboard = OrganizationService().getDashboard(ctx.organizationId)
                call.ok(dashboard, "Organization dashboard")
            }

            get("/branches") {
                val ctx = call.requireOrgAdminContext() ?: return@get
                val branches = OrganizationService().listBranches(ctx.organizationId)
                call.ok(branches, "Organization branches")
            }

            get("/compare") {
                val ctx = call.requireOrgAdminContext() ?: return@get
                val comparison = OrganizationService().compareBranches(ctx.organizationId)
                call.ok(comparison, "Branch comparison")
            }

            // ── Student Transfers ───────────────────────────────────────
            get("/transfers") {
                val ctx = call.requireOrgAdminContext() ?: return@get
                val status = call.request.queryParameters["status"]
                val transfers = StudentTransferService().listTransfers(ctx.organizationId, status)
                call.ok(transfers, "Student transfers")
            }

            post("/transfers") {
                val ctx = call.requireOrgAdminContext() ?: return@post
                val req = runCatching { call.receive<InitiateTransferRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid request body"); return@post }

                try {
                    val transfer = StudentTransferService().initiateTransfer(req, ctx.userId)
                    call.ok(transfer, "Transfer initiated")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "Invalid transfer request", HttpStatusCode.BadRequest, "INVALID_TRANSFER")
                } catch (e: IllegalStateException) {
                    call.fail(e.message ?: "Transfer conflict", HttpStatusCode.Conflict, "TRANSFER_CONFLICT")
                }
            }

            post("/transfers/{id}/approve") {
                val ctx = call.requireOrgAdminContext() ?: return@post
                val transferId = call.parameters["id"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid transfer id"); return@post }

                try {
                    val transfer = StudentTransferService().approveTransfer(transferId, ctx.userId)

                    // Notify admins of both branches
                    Notify.toUser(
                        userId = ctx.userId,
                        category = "transfer",
                        title = "Transfer Approved",
                        body = "Student ${transfer.studentName} has been transferred from ${transfer.fromSchoolName} to ${transfer.toSchoolName}.",
                        schoolId = ctx.schoolId,
                        actorId = ctx.userId,
                        deepLink = "/organization/transfers",
                        refType = "student_transfer",
                        refId = transfer.id,
                    )

                    call.ok(transfer, "Transfer approved and completed")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "Transfer not found", HttpStatusCode.NotFound, "TRANSFER_NOT_FOUND")
                } catch (e: IllegalStateException) {
                    call.fail(e.message ?: "Transfer not pending", HttpStatusCode.Conflict, "TRANSFER_NOT_PENDING")
                }
            }

            post("/transfers/{id}/reject") {
                val ctx = call.requireOrgAdminContext() ?: return@post
                val transferId = call.parameters["id"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid transfer id"); return@post }

                try {
                    val transfer = StudentTransferService().rejectTransfer(transferId, ctx.userId)
                    call.ok(transfer, "Transfer rejected")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "Transfer not found", HttpStatusCode.NotFound, "TRANSFER_NOT_FOUND")
                } catch (e: IllegalStateException) {
                    call.fail(e.message ?: "Transfer not pending", HttpStatusCode.Conflict, "TRANSFER_NOT_PENDING")
                }
            }
        }
    }
}
