package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.core.*
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.io.File
import java.util.UUID

fun Route.platformRouting() {
    route("/api/admin/platform") {
        authenticate("jwt") {

            // ── Dashboard ──────────────────────────────────────────────────
            get("/dashboard/health") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.health())
            }
            get("/dashboard/features-by-status") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.featuresByStatus())
            }
            get("/dashboard/features-by-priority") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.featuresByPriority())
            }
            get("/dashboard/features-by-team") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.featuresByTeam())
            }
            get("/dashboard/testing-progress") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.testingProgress())
            }
            get("/dashboard/bug-summary") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.bugSummary())
            }
            get("/dashboard/recent-activity") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.recentActivity())
            }
            get("/dashboard/upcoming-releases") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.upcomingReleases())
            }
            get("/dashboard/risk-indicators") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformDashboardService.riskIndicators())
            }

            // ── Features ───────────────────────────────────────────────────
            get("/features") {
                val uid = call.requirePlatformUser() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 25
                val status = call.request.queryParameters["status"]
                val priority = call.request.queryParameters["priority"]
                val productArea = call.request.queryParameters["product_area"]
                val ownerId = call.request.queryParameters["owner_id"]
                val search = call.request.queryParameters["search"]
                val tag = call.request.queryParameters["tag"]
                val sort = call.request.queryParameters["sort"]
                val (items, total) = FeatureRegistryService.list(page, pageSize, status, priority, productArea, ownerId, search, tag, sort)
                val totalPages = if (pageSize > 0) ((total + pageSize - 1) / pageSize).toInt() else 1
                call.ok(FeatureListResponse(items, total, page, pageSize, totalPages))
            }
            get("/features/tree") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(FeatureRegistryService.tree())
            }
            get("/features/{id}") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                val detail = FeatureRegistryService.getById(id)
                if (detail == null) {
                    call.fail("Feature not found", HttpStatusCode.NotFound, "NOT_FOUND")
                } else {
                    call.ok(detail)
                }
            }
            post("/features") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val req = call.receive<CreateFeatureRequest>()
                val id = FeatureRegistryService.create(req, uid)
                PlatformAuditService.log(uid, "feature.created", "feature", id, null, null, call)
                call.created(FeatureRegistryService.getById(id)!!)
            }
            put("/features/{id}") {
                val uid = call.requirePlatformAdmin() ?: return@put
                val id = UUID.fromString(call.parameters["id"] ?: return@put call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<UpdateFeatureRequest>()
                val updated = FeatureRegistryService.update(id, req, uid)
                if (updated) {
                    PlatformAuditService.log(uid, "feature.updated", "feature", id, null, null, call)
                    call.ok(FeatureRegistryService.getById(id)!!)
                } else {
                    call.fail("Feature not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            post("/features/{id}/archive") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                if (FeatureRegistryService.archive(id)) {
                    PlatformAuditService.log(uid, "feature.archived", "feature", id, null, null, call)
                    call.okMessage("Feature archived")
                } else {
                    call.fail("Feature not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            post("/features/{id}/unarchive") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                if (FeatureRegistryService.unarchive(id)) {
                    PlatformAuditService.log(uid, "feature.unarchived", "feature", id, null, null, call)
                    call.okMessage("Feature unarchived")
                } else {
                    call.fail("Feature not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            delete("/features/{id}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val id = UUID.fromString(call.parameters["id"] ?: return@delete call.fail("Missing id", HttpStatusCode.BadRequest))
                if (FeatureRegistryService.archive(id)) {
                    PlatformAuditService.log(uid, "feature.deleted", "feature", id, null, null, call)
                    call.okMessage("Feature archived")
                } else {
                    call.fail("Feature not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            // ── Feature Flows ──────────────────────────────────────────────
            get("/features/{id}/flows") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                call.ok(FeatureRegistryService.listFlows(id))
            }
            post("/features/{id}/flows") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<CreateFlowRequest>()
                val flowId = FeatureRegistryService.createFlow(id, req)
                PlatformAuditService.log(uid, "flow.created", "flow", flowId, null, null, call)
                call.created(FeatureRegistryService.listFlows(id).last())
            }
            put("/flows/{flowId}") {
                val uid = call.requirePlatformAdmin() ?: return@put
                val flowId = UUID.fromString(call.parameters["flowId"] ?: return@put call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<UpdateFlowRequest>()
                if (FeatureRegistryService.updateFlow(flowId, req)) {
                    call.okMessage("Flow updated")
                } else {
                    call.fail("Flow not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            delete("/flows/{flowId}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val flowId = UUID.fromString(call.parameters["flowId"] ?: return@delete call.fail("Missing id", HttpStatusCode.BadRequest))
                if (FeatureRegistryService.deleteFlow(flowId)) {
                    call.okMessage("Flow deleted")
                } else {
                    call.fail("Flow not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            // ── Screens ────────────────────────────────────────────────────
            get("/screens") {
                val uid = call.requirePlatformUser() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 25
                val module = call.request.queryParameters["module"]
                val featureId = call.request.queryParameters["feature_id"]
                val search = call.request.queryParameters["search"]
                val (items, total) = ScreenRegistryService.list(page, pageSize, module, featureId, search)
                val totalPages = if (pageSize > 0) ((total + pageSize - 1) / pageSize).toInt() else 1
                call.ok(ScreenListResponse(items, total, page, pageSize, totalPages))
            }
            get("/screens/{id}") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                val screen = ScreenRegistryService.getById(id)
                if (screen == null) {
                    call.fail("Screen not found", HttpStatusCode.NotFound, "NOT_FOUND")
                } else {
                    call.ok(screen)
                }
            }
            post("/screens") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val req = call.receive<CreateScreenRequest>()
                val id = ScreenRegistryService.create(req)
                PlatformAuditService.log(uid, "screen.created", "screen", id, null, null, call)
                call.created(ScreenRegistryService.getById(id)!!)
            }
            put("/screens/{id}") {
                val uid = call.requirePlatformAdmin() ?: return@put
                val id = UUID.fromString(call.parameters["id"] ?: return@put call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<UpdateScreenRequest>()
                if (ScreenRegistryService.update(id, req)) {
                    PlatformAuditService.log(uid, "screen.updated", "screen", id, null, null, call)
                    call.ok(ScreenRegistryService.getById(id)!!)
                } else {
                    call.fail("Screen not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            delete("/screens/{id}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val id = UUID.fromString(call.parameters["id"] ?: return@delete call.fail("Missing id", HttpStatusCode.BadRequest))
                if (ScreenRegistryService.delete(id)) {
                    PlatformAuditService.log(uid, "screen.deleted", "screen", id, null, null, call)
                    call.okMessage("Screen deleted")
                } else {
                    call.fail("Screen not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            // ── Feature API Mappings ───────────────────────────────────────
            get("/features/{id}/apis") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                call.ok(FeatureRegistryService.listApis(id))
            }
            post("/features/{id}/apis") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<CreateApiMappingRequest>()
                val apiId = FeatureRegistryService.createApi(id, req)
                PlatformAuditService.log(uid, "api_mapping.created", "api_mapping", apiId, null, null, call)
                call.created(FeatureRegistryService.listApis(id).last())
            }
            put("/apis/{apiId}") {
                val uid = call.requirePlatformAdmin() ?: return@put
                val apiId = UUID.fromString(call.parameters["apiId"] ?: return@put call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<UpdateApiMappingRequest>()
                if (FeatureRegistryService.updateApi(apiId, req)) {
                    call.okMessage("API mapping updated")
                } else {
                    call.fail("API mapping not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            delete("/apis/{apiId}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val apiId = UUID.fromString(call.parameters["apiId"] ?: return@delete call.fail("Missing id", HttpStatusCode.BadRequest))
                if (FeatureRegistryService.deleteApi(apiId)) {
                    call.okMessage("API mapping deleted")
                } else {
                    call.fail("API mapping not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            // ── Test Cases ─────────────────────────────────────────────────
            get("/test-cases") {
                val uid = call.requirePlatformUser() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 25
                val featureId = call.request.queryParameters["feature_id"]
                val status = call.request.queryParameters["status"]
                val assignedTo = call.request.queryParameters["assigned_to"]
                val priority = call.request.queryParameters["priority"]
                val environment = call.request.queryParameters["environment"]
                val search = call.request.queryParameters["search"]
                val (items, total) = TestCaseService.list(page, pageSize, featureId, status, assignedTo, priority, environment, search)
                val totalPages = if (pageSize > 0) ((total + pageSize - 1) / pageSize).toInt() else 1
                call.ok(TestCaseListResponse(items, total, page, pageSize, totalPages))
            }
            get("/test-cases/my") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(TestCaseService.listMy(uid))
            }
            get("/test-cases/{id}") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                val detail = TestCaseService.getById(id)
                if (detail == null) {
                    call.fail("Test case not found", HttpStatusCode.NotFound, "NOT_FOUND")
                } else {
                    call.ok(detail)
                }
            }
            post("/test-cases") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val req = call.receive<CreateTestCaseRequest>()
                val id = TestCaseService.create(req, uid)
                PlatformAuditService.log(uid, "test_case.created", "test_case", id, null, null, call)
                call.created(TestCaseService.getById(id)!!)
            }
            put("/test-cases/{id}") {
                val uid = call.requirePlatformAdmin() ?: return@put
                val id = UUID.fromString(call.parameters["id"] ?: return@put call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<UpdateTestCaseRequest>()
                if (TestCaseService.update(id, req)) {
                    call.ok(TestCaseService.getById(id)!!)
                } else {
                    call.fail("Test case not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            delete("/test-cases/{id}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val id = UUID.fromString(call.parameters["id"] ?: return@delete call.fail("Missing id", HttpStatusCode.BadRequest))
                if (TestCaseService.delete(id)) {
                    PlatformAuditService.log(uid, "test_case.deleted", "test_case", id, null, null, call)
                    call.okMessage("Test case deleted")
                } else {
                    call.fail("Test case not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            post("/test-cases/{id}/status") {
                val uid = call.requirePlatformUser() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<UpdateTestCaseStatusRequest>()
                if (TestCaseService.updateStatus(id, req, uid)) {
                    PlatformAuditService.log(uid, "test_case.status_changed", "test_case", id, null, null, call)
                    call.ok(TestCaseService.getById(id)!!)
                } else {
                    call.fail("Test case not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            get("/test-cases/{id}/attachments") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                call.ok(TestCaseService.listAttachments(id))
            }

            // ── Bugs ───────────────────────────────────────────────────────
            get("/bugs") {
                val uid = call.requirePlatformUser() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 25
                val status = call.request.queryParameters["status"]
                val priority = call.request.queryParameters["priority"]
                val severity = call.request.queryParameters["severity"]
                val featureId = call.request.queryParameters["feature_id"]
                val assignedTo = call.request.queryParameters["assigned_to"]
                val search = call.request.queryParameters["search"]
                val (items, total) = BugService.list(page, pageSize, status, priority, severity, featureId, assignedTo, search)
                val totalPages = if (pageSize > 0) ((total + pageSize - 1) / pageSize).toInt() else 1
                call.ok(BugListResponse(items, total, page, pageSize, totalPages))
            }
            get("/bugs/kanban") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(BugKanbanResponse(BugService.kanban()))
            }
            get("/bugs/{id}") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                val detail = BugService.getById(id)
                if (detail == null) {
                    call.fail("Bug not found", HttpStatusCode.NotFound, "NOT_FOUND")
                } else {
                    call.ok(detail)
                }
            }
            post("/bugs") {
                val uid = call.requirePlatformUser() ?: return@post
                val req = call.receive<CreateBugRequest>()
                val id = BugService.create(req, uid)
                PlatformAuditService.log(uid, "bug.created", "bug", id, null, null, call)
                call.created(BugService.getById(id)!!)
            }
            put("/bugs/{id}") {
                val uid = call.requirePlatformAdmin() ?: return@put
                val id = UUID.fromString(call.parameters["id"] ?: return@put call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<UpdateBugRequest>()
                if (BugService.update(id, req)) {
                    PlatformAuditService.log(uid, "bug.updated", "bug", id, null, null, call)
                    call.ok(BugService.getById(id)!!)
                } else {
                    call.fail("Bug not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            patch("/bugs/{id}/status") {
                val uid = call.requirePlatformUser() ?: return@patch
                val id = UUID.fromString(call.parameters["id"] ?: return@patch call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<BugStatusUpdateRequest>()
                val role = dbQuery {
                    AppUsersTable.selectAll().where { AppUsersTable.id eq uid }.singleOrNull()?.get(AppUsersTable.role)
                }
                val isQa = role == "qa"
                if (BugService.updateStatus(id, req.status, uid, isQa)) {
                    PlatformAuditService.log(uid, "bug.status_changed", "bug", id, null, null, call)
                    call.ok(BugService.getById(id)!!)
                } else {
                    call.fail("Invalid status transition or bug not found", HttpStatusCode.BadRequest, "INVALID_TRANSITION")
                }
            }
            post("/bugs/{id}/assign") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<BugAssignRequest>()
                if (BugService.assign(id, UUID.fromString(req.assigned_to), uid)) {
                    PlatformAuditService.log(uid, "bug.assigned", "bug", id, null, null, call)
                    call.ok(BugService.getById(id)!!)
                } else {
                    call.fail("Bug not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
            get("/bugs/{id}/comments") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                call.ok(BugService.listComments(id))
            }
            post("/bugs/{id}/comments") {
                val uid = call.requirePlatformUser() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<CreateBugCommentRequest>()
                val commentId = BugService.addComment(id, req, uid)
                call.created(BugService.listComments(id).last())
            }
            get("/bugs/{id}/activity") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                call.ok(BugService.listActivity(id))
            }

            // ── Audit ──────────────────────────────────────────────────────
            get("/audit") {
                val uid = call.requirePlatformAdmin() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 25
                val actorId = call.request.queryParameters["actor_id"]?.let { UUID.fromString(it) }
                val entityType = call.request.queryParameters["entity_type"]
                val action = call.request.queryParameters["action"]
                val (items, total) = PlatformAuditService.list(actorId, entityType, action, page, pageSize)
                val totalPages = if (pageSize > 0) ((total + pageSize - 1) / pageSize).toInt() else 1
                call.ok(AuditListResponse(items, total, page, pageSize, totalPages))
            }

            // ── Notifications ──────────────────────────────────────────────
            get("/notifications") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(PlatformNotificationService.listForUser(uid))
            }
            get("/notifications/summary") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(NotificationSummaryDto(PlatformNotificationService.unreadCount(uid)))
            }
            post("/notifications/{id}/read") {
                val uid = call.requirePlatformUser() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                PlatformNotificationService.markRead(id, uid)
                call.okMessage("Marked read")
            }
            post("/notifications/read-all") {
                val uid = call.requirePlatformUser() ?: return@post
                PlatformNotificationService.markAllRead(uid)
                call.okMessage("All notifications marked read")
            }

            // ── Users ──────────────────────────────────────────────────────
            get("/users") {
                val uid = call.requirePlatformUser() ?: return@get
                val users = dbQuery {
                    AppUsersTable.selectAll()
                        .where { AppUsersTable.role inList listOf("super_admin", "qa", "admin") }
                        .map { row ->
                            PlatformUserDto(
                                id = row[AppUsersTable.id].value.toString(),
                                name = row[AppUsersTable.fullName],
                                role = row[AppUsersTable.role],
                            )
                        }
                }
                call.ok(users)
            }

            // ── CSV Import ─────────────────────────────────────────────────
            post("/import/csv") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val csvPath = System.getenv("FEATURE_AUDIT_CSV_PATH")
                    ?: listOf("feature_audit.csv", "../feature_audit.csv", "../../feature_audit.csv")
                        .map { File(it) }
                        .firstOrNull { it.exists() }
                        ?.path
                    ?: "feature_audit.csv"
                val result = CsvImportService.importFromCsv(csvPath, uid)
                PlatformAuditService.log(uid, "csv.import", "feature", null, null, null, call)
                call.ok(result)
            }

            // ── Screen Discovery ───────────────────────────────────────────
            post("/discovery/screens/scan") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val result = ScreenDiscoveryService.scan()
                PlatformAuditService.log(uid, "discovery.screens_scanned", "discovered_screen", null, null, null, call)
                call.ok(result)
            }
            get("/discovery/screens") {
                val uid = call.requirePlatformUser() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 50
                val module = call.request.queryParameters["module"]
                val isMapped = call.request.queryParameters["is_mapped"]?.toBooleanStrictOrNull()
                val (items, total) = ScreenDiscoveryService.listDiscovered(page, pageSize, module, isMapped)
                call.ok(DiscoveredScreenListResponse(items, total, page, pageSize))
            }
            post("/discovery/screens/{id}/link") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<LinkDiscoveredScreenRequest>()
                val featureId = UUID.fromString(req.feature_id)
                if (ScreenDiscoveryService.linkToFeature(id, featureId, req.screen_name)) {
                    PlatformAuditService.log(uid, "discovery.screen_linked", "discovered_screen", id, null, null, call)
                    call.okMessage("Screen linked to feature")
                } else {
                    call.fail("Discovered screen not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            // ── API Discovery ──────────────────────────────────────────────
            post("/discovery/apis/scan") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val result = ApiDiscoveryService.scan()
                PlatformAuditService.log(uid, "discovery.apis_scanned", "discovered_api", null, null, null, call)
                call.ok(result)
            }
            get("/discovery/apis") {
                val uid = call.requirePlatformUser() ?: return@get
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 50
                val isMapped = call.request.queryParameters["is_mapped"]?.toBooleanStrictOrNull()
                val method = call.request.queryParameters["method"]
                val (items, total) = ApiDiscoveryService.listDiscovered(page, pageSize, isMapped, method)
                call.ok(DiscoveredApiListResponse(items, total, page, pageSize))
            }
            post("/discovery/apis/{id}/link") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<LinkDiscoveredApiRequest>()
                val featureId = UUID.fromString(req.feature_id)
                if (ApiDiscoveryService.linkToFeature(id, featureId, req.description)) {
                    PlatformAuditService.log(uid, "discovery.api_linked", "discovered_api", id, null, null, call)
                    call.okMessage("API linked to feature")
                } else {
                    call.fail("Discovered API not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            // ── Git Change Tracking ────────────────────────────────────────
            post("/discovery/git/refresh") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val result = GitChangeTrackingService.refreshAll()
                PlatformAuditService.log(uid, "git.refreshed", "feature_file", null, null, null, call)
                call.ok(result)
            }
            get("/features/{id}/files") {
                val uid = call.requirePlatformUser() ?: return@get
                val id = UUID.fromString(call.parameters["id"] ?: return@get call.fail("Missing id", HttpStatusCode.BadRequest))
                call.ok(GitChangeTrackingService.listFiles(id))
            }
            post("/features/{id}/files") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val req = call.receive<LinkFileRequest>()
                val fileId = GitChangeTrackingService.linkFile(id, req.file_path, req.file_type)
                PlatformAuditService.log(uid, "file.linked", "feature_file", fileId, null, null, call)
                call.created(GitChangeTrackingService.listFiles(id).last())
            }
            delete("/features/files/{fileId}") {
                val uid = call.requirePlatformAdmin() ?: return@delete
                val fileId = UUID.fromString(call.parameters["fileId"] ?: return@delete call.fail("Missing id", HttpStatusCode.BadRequest))
                if (GitChangeTrackingService.unlinkFile(fileId)) {
                    call.okMessage("File unlinked")
                } else {
                    call.fail("File not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            // ── API Health Checks ──────────────────────────────────────────
            get("/discovery/health/summary") {
                val uid = call.requirePlatformUser() ?: return@get
                call.ok(ApiHealthCheckService.summary())
            }
            get("/discovery/health/recent") {
                val uid = call.requirePlatformUser() ?: return@get
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 50
                call.ok(ApiHealthCheckService.recentChecks(limit))
            }
            post("/discovery/health/check-all") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val result = ApiHealthCheckService.checkAll()
                PlatformAuditService.log(uid, "health.checked_all", "discovered_api", null, null, null, call)
                call.ok(result)
            }
            post("/discovery/apis/{id}/check") {
                val uid = call.requirePlatformAdmin() ?: return@post
                val id = UUID.fromString(call.parameters["id"] ?: return@post call.fail("Missing id", HttpStatusCode.BadRequest))
                val result = ApiHealthCheckService.checkApi(id)
                if (result != null) {
                    call.ok(result)
                } else {
                    call.fail("Discovered API not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
        }
    }
}
