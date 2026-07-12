package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformFeaturesTable
import com.littlebridge.enrollplus.db.PlatformFeatureFlowsTable
import com.littlebridge.enrollplus.db.PlatformScreensTable
import com.littlebridge.enrollplus.db.PlatformFeatureApisTable
import com.littlebridge.enrollplus.db.PlatformTestCasesTable
import com.littlebridge.enrollplus.db.PlatformBugsTable
import com.littlebridge.enrollplus.db.AppUsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.util.UUID
import java.time.Instant

internal fun ResultRow.toFeatureDto(ownerName: String? = null): FeatureDto = FeatureDto(
    id = this[PlatformFeaturesTable.id].value.toString(),
    feature_id = this[PlatformFeaturesTable.featureId],
    name = this[PlatformFeaturesTable.name],
    description = this[PlatformFeaturesTable.description],
    business_goal = this[PlatformFeaturesTable.businessGoal],
    product_area = this[PlatformFeaturesTable.productArea],
    category = this[PlatformFeaturesTable.category],
    module = this[PlatformFeaturesTable.module],
    parent_id = this[PlatformFeaturesTable.parentId]?.toString(),
    status = this[PlatformFeaturesTable.status],
    completion_pct = this[PlatformFeaturesTable.completionPct],
    priority = this[PlatformFeaturesTable.priority],
    severity = this[PlatformFeaturesTable.severity],
    business_impact = this[PlatformFeaturesTable.businessImpact],
    tech_complexity = this[PlatformFeaturesTable.techComplexity],
    risk_level = this[PlatformFeaturesTable.riskLevel],
    dependencies = this[PlatformFeaturesTable.dependencies],
    blockers = this[PlatformFeaturesTable.blockers],
    estimated_effort = this[PlatformFeaturesTable.estimatedEffort],
    owner_id = this[PlatformFeaturesTable.ownerId]?.toString(),
    owner_name = ownerName,
    team = this[PlatformFeaturesTable.team],
    sprint = this[PlatformFeaturesTable.sprint],
    version_intro = this[PlatformFeaturesTable.versionIntro],
    target_release = this[PlatformFeaturesTable.targetRelease],
    release_status = this[PlatformFeaturesTable.releaseStatus],
    tags = this[PlatformFeaturesTable.tags],
    metadata = this[PlatformFeaturesTable.metadata],
    legacy_imported = this[PlatformFeaturesTable.legacyImported],
    is_archived = this[PlatformFeaturesTable.isArchived],
    created_at = this[PlatformFeaturesTable.createdAt].toString(),
    updated_at = this[PlatformFeaturesTable.updatedAt].toString(),
)

object FeatureRegistryService {

    suspend fun list(
        page: Int = 1,
        pageSize: Int = 25,
        status: String? = null,
        priority: String? = null,
        productArea: String? = null,
        ownerId: String? = null,
        search: String? = null,
        tag: String? = null,
        sort: String? = null,
    ): Pair<List<FeatureDto>, Long> = dbQuery {
        val conditions = Op.build {
            (if (status != null) PlatformFeaturesTable.status eq status else Op.TRUE) and
            (if (priority != null) PlatformFeaturesTable.priority eq priority else Op.TRUE) and
            (if (productArea != null) PlatformFeaturesTable.productArea eq productArea else Op.TRUE) and
            (if (ownerId != null) PlatformFeaturesTable.ownerId eq UUID.fromString(ownerId) else Op.TRUE) and
            (if (search != null) {
                (PlatformFeaturesTable.name like "%$search%") or
                (PlatformFeaturesTable.featureId like "%$search%") or
                (PlatformFeaturesTable.description like "%$search%")
            } else Op.TRUE) and
            (PlatformFeaturesTable.isArchived eq false)
        }
        val total = PlatformFeaturesTable.selectAll().where { conditions }.count()
        val sortPair = parseSort(sort)
        val items = PlatformFeaturesTable.selectAll().where { conditions }
            .orderBy(sortPair)
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .map { row ->
                val ownerName = row[PlatformFeaturesTable.ownerId]?.let { oid ->
                    AppUsersTable.selectAll().where { AppUsersTable.id eq oid }
                        .singleOrNull()?.get(AppUsersTable.fullName)
                }
                row.toFeatureDto(ownerName)
            }
        items to total
    }

    private fun parseSort(sort: String?): Pair<Column<*>, SortOrder> {
        if (sort.isNullOrBlank()) return PlatformFeaturesTable.createdAt to SortOrder.DESC
        val parts = sort.split(":")
        val field = parts[0]
        val direction = if (parts.getOrNull(1) == "asc") SortOrder.ASC else SortOrder.DESC
        return when (field) {
            "name" -> PlatformFeaturesTable.name to direction
            "status" -> PlatformFeaturesTable.status to direction
            "priority" -> PlatformFeaturesTable.priority to direction
            "completion_pct" -> PlatformFeaturesTable.completionPct to direction
            "updated_at" -> PlatformFeaturesTable.updatedAt to direction
            "created_at" -> PlatformFeaturesTable.createdAt to direction
            else -> PlatformFeaturesTable.createdAt to SortOrder.DESC
        }
    }

    suspend fun getById(id: UUID): FeatureDetailDto? = dbQuery {
        val featureRow = PlatformFeaturesTable.selectAll().where { PlatformFeaturesTable.id eq id }
            .singleOrNull() ?: return@dbQuery null
        val ownerId = featureRow[PlatformFeaturesTable.ownerId]
        val ownerName = ownerId?.let { oid ->
            AppUsersTable.selectAll().where { AppUsersTable.id eq oid }
                .singleOrNull()?.get(AppUsersTable.fullName)
        }
        val feature = featureRow.toFeatureDto(ownerName)

        val flows = PlatformFeatureFlowsTable.selectAll()
            .where { PlatformFeatureFlowsTable.featureId eq id }
            .orderBy(PlatformFeatureFlowsTable.sortOrder, SortOrder.ASC)
            .map { row ->
                FlowDto(
                    id = row[PlatformFeatureFlowsTable.id].value.toString(),
                    feature_id = id.toString(),
                    flow_name = row[PlatformFeatureFlowsTable.flowName],
                    flow_description = row[PlatformFeatureFlowsTable.flowDescription],
                    flow_steps = row[PlatformFeatureFlowsTable.flowSteps],
                    entry_points = row[PlatformFeatureFlowsTable.entryPoints],
                    exit_points = row[PlatformFeatureFlowsTable.exitPoints],
                    deep_links = row[PlatformFeatureFlowsTable.deepLinks],
                    edge_cases = row[PlatformFeatureFlowsTable.edgeCases],
                    sort_order = row[PlatformFeatureFlowsTable.sortOrder],
                )
            }

        val screens = PlatformScreensTable.selectAll()
            .where { PlatformScreensTable.featureId eq id }
            .orderBy(PlatformScreensTable.sortOrder, SortOrder.ASC)
            .map { row -> row.toScreenDto() }

        val apis = PlatformFeatureApisTable.selectAll()
            .where { PlatformFeatureApisTable.featureId eq id }
            .orderBy(PlatformFeatureApisTable.sortOrder, SortOrder.ASC)
            .map { row -> row.toApiMappingDto() }

        val testCases = PlatformTestCasesTable.selectAll()
            .where { PlatformTestCasesTable.featureId eq id }
            .orderBy(PlatformTestCasesTable.createdAt, SortOrder.DESC)
            .map { row -> row.toTestCaseDto() }

        val bugs = PlatformBugsTable.selectAll()
            .where { PlatformBugsTable.featureId eq id }
            .orderBy(PlatformBugsTable.createdAt, SortOrder.DESC)
            .map { row -> row.toBugSummaryDto() }

        val children = PlatformFeaturesTable.selectAll()
            .where { (PlatformFeaturesTable.parentId eq id) and (PlatformFeaturesTable.isArchived eq false) }
            .map { row -> row.toFeatureDto() }

        FeatureDetailDto(feature, flows, screens, apis, testCases, bugs, children)
    }

    suspend fun create(req: CreateFeatureRequest, userId: UUID): UUID = dbQuery {
        val now = Instant.now()
        PlatformFeaturesTable.insert {
            it[PlatformFeaturesTable.featureId] = req.feature_id
            it[PlatformFeaturesTable.name] = req.name
            it[PlatformFeaturesTable.description] = req.description
            it[PlatformFeaturesTable.businessGoal] = req.business_goal
            it[PlatformFeaturesTable.productArea] = req.product_area
            it[PlatformFeaturesTable.category] = req.category
            it[PlatformFeaturesTable.module] = req.module
            it[PlatformFeaturesTable.parentId] = req.parent_id?.let { UUID.fromString(it) }
            it[PlatformFeaturesTable.status] = req.status
            it[PlatformFeaturesTable.completionPct] = req.completion_pct
            it[PlatformFeaturesTable.priority] = req.priority
            it[PlatformFeaturesTable.severity] = req.severity
            it[PlatformFeaturesTable.businessImpact] = req.business_impact
            it[PlatformFeaturesTable.techComplexity] = req.tech_complexity
            it[PlatformFeaturesTable.riskLevel] = req.risk_level
            it[PlatformFeaturesTable.dependencies] = req.dependencies
            it[PlatformFeaturesTable.blockers] = req.blockers
            it[PlatformFeaturesTable.estimatedEffort] = req.estimated_effort
            it[PlatformFeaturesTable.ownerId] = req.owner_id?.let { UUID.fromString(it) }
            it[PlatformFeaturesTable.team] = req.team
            it[PlatformFeaturesTable.sprint] = req.sprint
            it[PlatformFeaturesTable.versionIntro] = req.version_intro
            it[PlatformFeaturesTable.targetRelease] = req.target_release
            it[PlatformFeaturesTable.releaseStatus] = req.release_status
            it[PlatformFeaturesTable.tags] = req.tags
            it[PlatformFeaturesTable.metadata] = req.metadata
            it[PlatformFeaturesTable.legacyImported] = false
            it[PlatformFeaturesTable.isArchived] = false
            it[PlatformFeaturesTable.createdAt] = now
            it[PlatformFeaturesTable.updatedAt] = now
            it[PlatformFeaturesTable.createdBy] = userId
            it[PlatformFeaturesTable.updatedBy] = userId
        }[PlatformFeaturesTable.id].value
    }

    suspend fun update(id: UUID, req: UpdateFeatureRequest, userId: UUID): Boolean = dbQuery {
        val rows = PlatformFeaturesTable.update(where = { PlatformFeaturesTable.id eq id }) {
            req.name?.let { v -> it[PlatformFeaturesTable.name] = v }
            req.description?.let { v -> it[PlatformFeaturesTable.description] = v }
            req.business_goal?.let { v -> it[PlatformFeaturesTable.businessGoal] = v }
            req.product_area?.let { v -> it[PlatformFeaturesTable.productArea] = v }
            req.category?.let { v -> it[PlatformFeaturesTable.category] = v }
            req.module?.let { v -> it[PlatformFeaturesTable.module] = v }
            req.parent_id?.let { v -> it[PlatformFeaturesTable.parentId] = UUID.fromString(v) }
            req.status?.let { v -> it[PlatformFeaturesTable.status] = v }
            req.completion_pct?.let { v -> it[PlatformFeaturesTable.completionPct] = v }
            req.priority?.let { v -> it[PlatformFeaturesTable.priority] = v }
            req.severity?.let { v -> it[PlatformFeaturesTable.severity] = v }
            req.business_impact?.let { v -> it[PlatformFeaturesTable.businessImpact] = v }
            req.tech_complexity?.let { v -> it[PlatformFeaturesTable.techComplexity] = v }
            req.risk_level?.let { v -> it[PlatformFeaturesTable.riskLevel] = v }
            req.dependencies?.let { v -> it[PlatformFeaturesTable.dependencies] = v }
            req.blockers?.let { v -> it[PlatformFeaturesTable.blockers] = v }
            req.estimated_effort?.let { v -> it[PlatformFeaturesTable.estimatedEffort] = v }
            req.owner_id?.let { v -> it[PlatformFeaturesTable.ownerId] = UUID.fromString(v) }
            req.team?.let { v -> it[PlatformFeaturesTable.team] = v }
            req.sprint?.let { v -> it[PlatformFeaturesTable.sprint] = v }
            req.version_intro?.let { v -> it[PlatformFeaturesTable.versionIntro] = v }
            req.target_release?.let { v -> it[PlatformFeaturesTable.targetRelease] = v }
            req.release_status?.let { v -> it[PlatformFeaturesTable.releaseStatus] = v }
            req.tags?.let { v -> it[PlatformFeaturesTable.tags] = v }
            req.metadata?.let { v -> it[PlatformFeaturesTable.metadata] = v }
            it[PlatformFeaturesTable.updatedAt] = Instant.now()
            it[PlatformFeaturesTable.updatedBy] = userId
        }
        rows > 0
    }

    suspend fun archive(id: UUID): Boolean = dbQuery {
        PlatformFeaturesTable.update(where = { PlatformFeaturesTable.id eq id }) {
            it[PlatformFeaturesTable.isArchived] = true
            it[PlatformFeaturesTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun unarchive(id: UUID): Boolean = dbQuery {
        PlatformFeaturesTable.update(where = { PlatformFeaturesTable.id eq id }) {
            it[PlatformFeaturesTable.isArchived] = false
            it[PlatformFeaturesTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun tree(): List<FeatureTreeNode> = dbQuery {
        val allFeatures = PlatformFeaturesTable.selectAll()
            .where { PlatformFeaturesTable.isArchived eq false }
            .orderBy(PlatformFeaturesTable.name, SortOrder.ASC)
            .map { it.toFeatureDto() }
        buildTree(allFeatures, null)
    }

    private fun buildTree(all: List<FeatureDto>, parentId: String?): List<FeatureTreeNode> =
        all.filter { it.parent_id == parentId }
            .map { FeatureTreeNode(it, buildTree(all, it.id)) }

    // ── Flow CRUD ────────────────────────────────────────────────────────

    suspend fun listFlows(featureId: UUID): List<FlowDto> = dbQuery {
        PlatformFeatureFlowsTable.selectAll()
            .where { PlatformFeatureFlowsTable.featureId eq featureId }
            .orderBy(PlatformFeatureFlowsTable.sortOrder, SortOrder.ASC)
            .map { row ->
                FlowDto(
                    id = row[PlatformFeatureFlowsTable.id].value.toString(),
                    feature_id = featureId.toString(),
                    flow_name = row[PlatformFeatureFlowsTable.flowName],
                    flow_description = row[PlatformFeatureFlowsTable.flowDescription],
                    flow_steps = row[PlatformFeatureFlowsTable.flowSteps],
                    entry_points = row[PlatformFeatureFlowsTable.entryPoints],
                    exit_points = row[PlatformFeatureFlowsTable.exitPoints],
                    deep_links = row[PlatformFeatureFlowsTable.deepLinks],
                    edge_cases = row[PlatformFeatureFlowsTable.edgeCases],
                    sort_order = row[PlatformFeatureFlowsTable.sortOrder],
                )
            }
    }

    suspend fun createFlow(featureId: UUID, req: CreateFlowRequest): UUID = dbQuery {
        PlatformFeatureFlowsTable.insert {
            it[PlatformFeatureFlowsTable.featureId] = featureId
            it[PlatformFeatureFlowsTable.flowName] = req.flow_name
            it[PlatformFeatureFlowsTable.flowDescription] = req.flow_description
            it[PlatformFeatureFlowsTable.flowSteps] = req.flow_steps
            it[PlatformFeatureFlowsTable.entryPoints] = req.entry_points
            it[PlatformFeatureFlowsTable.exitPoints] = req.exit_points
            it[PlatformFeatureFlowsTable.deepLinks] = req.deep_links
            it[PlatformFeatureFlowsTable.edgeCases] = req.edge_cases
            it[PlatformFeatureFlowsTable.sortOrder] = req.sort_order
            it[PlatformFeatureFlowsTable.createdAt] = Instant.now()
            it[PlatformFeatureFlowsTable.updatedAt] = Instant.now()
        }[PlatformFeatureFlowsTable.id].value
    }

    suspend fun updateFlow(flowId: UUID, req: UpdateFlowRequest): Boolean = dbQuery {
        PlatformFeatureFlowsTable.update(where = { PlatformFeatureFlowsTable.id eq flowId }) {
            req.flow_name?.let { v -> it[PlatformFeatureFlowsTable.flowName] = v }
            req.flow_description?.let { v -> it[PlatformFeatureFlowsTable.flowDescription] = v }
            req.flow_steps?.let { v -> it[PlatformFeatureFlowsTable.flowSteps] = v }
            req.entry_points?.let { v -> it[PlatformFeatureFlowsTable.entryPoints] = v }
            req.exit_points?.let { v -> it[PlatformFeatureFlowsTable.exitPoints] = v }
            req.deep_links?.let { v -> it[PlatformFeatureFlowsTable.deepLinks] = v }
            req.edge_cases?.let { v -> it[PlatformFeatureFlowsTable.edgeCases] = v }
            req.sort_order?.let { v -> it[PlatformFeatureFlowsTable.sortOrder] = v }
            it[PlatformFeatureFlowsTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun deleteFlow(flowId: UUID): Boolean = dbQuery {
        PlatformFeatureFlowsTable.deleteWhere { PlatformFeatureFlowsTable.id eq flowId } > 0
    }

    // ── API Mapping CRUD ─────────────────────────────────────────────────

    suspend fun listApis(featureId: UUID): List<ApiMappingDto> = dbQuery {
        PlatformFeatureApisTable.selectAll()
            .where { PlatformFeatureApisTable.featureId eq featureId }
            .orderBy(PlatformFeatureApisTable.sortOrder, SortOrder.ASC)
            .map { it.toApiMappingDto() }
    }

    suspend fun createApi(featureId: UUID, req: CreateApiMappingRequest): UUID = dbQuery {
        PlatformFeatureApisTable.insert {
            it[PlatformFeatureApisTable.featureId] = featureId
            it[PlatformFeatureApisTable.endpoint] = req.endpoint
            it[PlatformFeatureApisTable.method] = req.method
            it[PlatformFeatureApisTable.description] = req.description
            it[PlatformFeatureApisTable.dbEntities] = req.db_entities
            it[PlatformFeatureApisTable.caching] = req.caching
            it[PlatformFeatureApisTable.featureFlag] = req.feature_flag
            it[PlatformFeatureApisTable.analyticsEvents] = req.analytics_events
            it[PlatformFeatureApisTable.notifications] = req.notifications
            it[PlatformFeatureApisTable.isDocumented] = req.is_documented
            it[PlatformFeatureApisTable.sortOrder] = req.sort_order
            it[PlatformFeatureApisTable.createdAt] = Instant.now()
            it[PlatformFeatureApisTable.updatedAt] = Instant.now()
        }[PlatformFeatureApisTable.id].value
    }

    suspend fun updateApi(apiId: UUID, req: UpdateApiMappingRequest): Boolean = dbQuery {
        PlatformFeatureApisTable.update(where = { PlatformFeatureApisTable.id eq apiId }) {
            req.endpoint?.let { v -> it[PlatformFeatureApisTable.endpoint] = v }
            req.method?.let { v -> it[PlatformFeatureApisTable.method] = v }
            req.description?.let { v -> it[PlatformFeatureApisTable.description] = v }
            req.db_entities?.let { v -> it[PlatformFeatureApisTable.dbEntities] = v }
            req.caching?.let { v -> it[PlatformFeatureApisTable.caching] = v }
            req.feature_flag?.let { v -> it[PlatformFeatureApisTable.featureFlag] = v }
            req.analytics_events?.let { v -> it[PlatformFeatureApisTable.analyticsEvents] = v }
            req.notifications?.let { v -> it[PlatformFeatureApisTable.notifications] = v }
            req.is_documented?.let { v -> it[PlatformFeatureApisTable.isDocumented] = v }
            req.sort_order?.let { v -> it[PlatformFeatureApisTable.sortOrder] = v }
            it[PlatformFeatureApisTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun deleteApi(apiId: UUID): Boolean = dbQuery {
        PlatformFeatureApisTable.deleteWhere { PlatformFeatureApisTable.id eq apiId } > 0
    }

    // ── Helpers for joined DTOs ──────────────────────────────────────────

    internal fun ResultRow.toScreenDto(featureName: String? = null): ScreenDto = ScreenDto(
        id = this[PlatformScreensTable.id].value.toString(),
        screen_id = this[PlatformScreensTable.screenId],
        name = this[PlatformScreensTable.name],
        route = this[PlatformScreensTable.route],
        module = this[PlatformScreensTable.module],
        purpose = this[PlatformScreensTable.purpose],
        screenshot_url = this[PlatformScreensTable.screenshotUrl],
        permissions = this[PlatformScreensTable.permissions],
        user_actions = this[PlatformScreensTable.userActions],
        connected_screens = this[PlatformScreensTable.connectedScreens],
        empty_state = this[PlatformScreensTable.emptyState],
        loading_state = this[PlatformScreensTable.loadingState],
        error_state = this[PlatformScreensTable.errorState],
        feature_id = this[PlatformScreensTable.featureId]?.toString(),
        feature_name = featureName,
        sort_order = this[PlatformScreensTable.sortOrder],
        metadata = this[PlatformScreensTable.metadata],
    )

    internal fun ResultRow.toApiMappingDto(): ApiMappingDto = ApiMappingDto(
        id = this[PlatformFeatureApisTable.id].value.toString(),
        feature_id = this[PlatformFeatureApisTable.featureId].toString(),
        endpoint = this[PlatformFeatureApisTable.endpoint],
        method = this[PlatformFeatureApisTable.method],
        description = this[PlatformFeatureApisTable.description],
        db_entities = this[PlatformFeatureApisTable.dbEntities],
        caching = this[PlatformFeatureApisTable.caching],
        feature_flag = this[PlatformFeatureApisTable.featureFlag],
        analytics_events = this[PlatformFeatureApisTable.analyticsEvents],
        notifications = this[PlatformFeatureApisTable.notifications],
        is_documented = this[PlatformFeatureApisTable.isDocumented],
        sort_order = this[PlatformFeatureApisTable.sortOrder],
    )

    internal fun ResultRow.toTestCaseDto(featureName: String? = null, assignedName: String? = null): TestCaseDto = TestCaseDto(
        id = this[PlatformTestCasesTable.id].value.toString(),
        case_id = this[PlatformTestCasesTable.caseId],
        feature_id = this[PlatformTestCasesTable.featureId].toString(),
        feature_name = featureName,
        screen_id = this[PlatformTestCasesTable.screenId]?.toString(),
        api_id = this[PlatformTestCasesTable.apiId]?.toString(),
        title = this[PlatformTestCasesTable.title],
        description = this[PlatformTestCasesTable.description],
        preconditions = this[PlatformTestCasesTable.preconditions],
        test_steps = this[PlatformTestCasesTable.testSteps],
        expected_result = this[PlatformTestCasesTable.expectedResult],
        priority = this[PlatformTestCasesTable.priority],
        test_type = this[PlatformTestCasesTable.testType],
        status = this[PlatformTestCasesTable.status],
        assigned_to = this[PlatformTestCasesTable.assignedTo]?.toString(),
        assigned_to_name = assignedName,
        build_version = this[PlatformTestCasesTable.buildVersion],
        environment = this[PlatformTestCasesTable.environment],
        devices = this[PlatformTestCasesTable.devices],
        os_versions = this[PlatformTestCasesTable.osVersions],
        platform = this[PlatformTestCasesTable.platform],
        last_tested_at = this[PlatformTestCasesTable.lastTestedAt]?.toString(),
        last_tested_by = this[PlatformTestCasesTable.lastTestedBy]?.toString(),
        failure_reason = this[PlatformTestCasesTable.failureReason],
        metadata = this[PlatformTestCasesTable.metadata],
        created_at = this[PlatformTestCasesTable.createdAt].toString(),
        updated_at = this[PlatformTestCasesTable.updatedAt].toString(),
    )

    internal fun ResultRow.toBugSummaryDto(featureName: String? = null, assignedName: String? = null, reportedName: String? = null): BugSummaryDto = BugSummaryDto(
        id = this[PlatformBugsTable.id].value.toString(),
        bug_id = this[PlatformBugsTable.bugId],
        title = this[PlatformBugsTable.title],
        status = this[PlatformBugsTable.status],
        priority = this[PlatformBugsTable.priority],
        severity = this[PlatformBugsTable.severity],
        feature_id = this[PlatformBugsTable.featureId]?.toString(),
        feature_name = featureName,
        assigned_to = this[PlatformBugsTable.assignedTo]?.toString(),
        assigned_to_name = assignedName,
        reported_by = this[PlatformBugsTable.reportedBy]?.toString(),
        reported_by_name = reportedName,
        sla_due_at = this[PlatformBugsTable.slaDueAt]?.toString(),
        created_at = this[PlatformBugsTable.createdAt].toString(),
        updated_at = this[PlatformBugsTable.updatedAt].toString(),
    )
}
