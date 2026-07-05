package com.littlebridge.enrollplus.feature.platform

import kotlinx.serialization.Serializable

// ── Feature DTOs ────────────────────────────────────────────────────────

@Serializable
data class FeatureDto(
    val id: String,
    val feature_id: String,
    val name: String,
    val description: String? = null,
    val business_goal: String? = null,
    val product_area: String? = null,
    val category: String? = null,
    val module: String? = null,
    val parent_id: String? = null,
    val status: String = "planned",
    val completion_pct: Int = 0,
    val priority: String = "medium",
    val severity: String? = null,
    val business_impact: String? = null,
    val tech_complexity: String? = null,
    val risk_level: String? = null,
    val dependencies: String = "[]",
    val blockers: String? = null,
    val estimated_effort: String? = null,
    val owner_id: String? = null,
    val owner_name: String? = null,
    val team: String? = null,
    val sprint: String? = null,
    val version_intro: String? = null,
    val target_release: String? = null,
    val release_status: String? = null,
    val tags: String = "[]",
    val metadata: String = "{}",
    val legacy_imported: Boolean = false,
    val is_archived: Boolean = false,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class FeatureDetailDto(
    val feature: FeatureDto,
    val flows: List<FlowDto> = emptyList(),
    val screens: List<ScreenDto> = emptyList(),
    val apis: List<ApiMappingDto> = emptyList(),
    val test_cases: List<TestCaseDto> = emptyList(),
    val bugs: List<BugSummaryDto> = emptyList(),
    val children: List<FeatureDto> = emptyList(),
)

@Serializable
data class CreateFeatureRequest(
    val feature_id: String,
    val name: String,
    val description: String? = null,
    val business_goal: String? = null,
    val product_area: String? = null,
    val category: String? = null,
    val module: String? = null,
    val parent_id: String? = null,
    val status: String = "planned",
    val completion_pct: Int = 0,
    val priority: String = "medium",
    val severity: String? = null,
    val business_impact: String? = null,
    val tech_complexity: String? = null,
    val risk_level: String? = null,
    val dependencies: String = "[]",
    val blockers: String? = null,
    val estimated_effort: String? = null,
    val owner_id: String? = null,
    val team: String? = null,
    val sprint: String? = null,
    val version_intro: String? = null,
    val target_release: String? = null,
    val release_status: String? = null,
    val tags: String = "[]",
    val metadata: String = "{}",
)

@Serializable
data class UpdateFeatureRequest(
    val name: String? = null,
    val description: String? = null,
    val business_goal: String? = null,
    val product_area: String? = null,
    val category: String? = null,
    val module: String? = null,
    val parent_id: String? = null,
    val status: String? = null,
    val completion_pct: Int? = null,
    val priority: String? = null,
    val severity: String? = null,
    val business_impact: String? = null,
    val tech_complexity: String? = null,
    val risk_level: String? = null,
    val dependencies: String? = null,
    val blockers: String? = null,
    val estimated_effort: String? = null,
    val owner_id: String? = null,
    val team: String? = null,
    val sprint: String? = null,
    val version_intro: String? = null,
    val target_release: String? = null,
    val release_status: String? = null,
    val tags: String? = null,
    val metadata: String? = null,
)

@Serializable
data class FeatureListResponse(
    val items: List<FeatureDto>,
    val total: Long,
    val page: Int,
    val page_size: Int,
    val total_pages: Int,
)

@Serializable
data class FeatureTreeNode(
    val feature: FeatureDto,
    val children: List<FeatureTreeNode> = emptyList(),
)

// ── Flow DTOs ───────────────────────────────────────────────────────────

@Serializable
data class FlowDto(
    val id: String,
    val feature_id: String,
    val flow_name: String,
    val flow_description: String? = null,
    val flow_steps: String = "[]",
    val entry_points: String = "[]",
    val exit_points: String = "[]",
    val deep_links: String = "[]",
    val edge_cases: String = "[]",
    val sort_order: Int = 0,
)

@Serializable
data class CreateFlowRequest(
    val flow_name: String,
    val flow_description: String? = null,
    val flow_steps: String = "[]",
    val entry_points: String = "[]",
    val exit_points: String = "[]",
    val deep_links: String = "[]",
    val edge_cases: String = "[]",
    val sort_order: Int = 0,
)

@Serializable
data class UpdateFlowRequest(
    val flow_name: String? = null,
    val flow_description: String? = null,
    val flow_steps: String? = null,
    val entry_points: String? = null,
    val exit_points: String? = null,
    val deep_links: String? = null,
    val edge_cases: String? = null,
    val sort_order: Int? = null,
)

// ── Screen DTOs ─────────────────────────────────────────────────────────

@Serializable
data class ScreenDto(
    val id: String,
    val screen_id: String,
    val name: String,
    val route: String? = null,
    val module: String? = null,
    val purpose: String? = null,
    val screenshot_url: String? = null,
    val permissions: String = "[]",
    val user_actions: String = "[]",
    val connected_screens: String = "[]",
    val empty_state: String? = null,
    val loading_state: String? = null,
    val error_state: String? = null,
    val feature_id: String? = null,
    val feature_name: String? = null,
    val sort_order: Int = 0,
    val metadata: String = "{}",
)

@Serializable
data class CreateScreenRequest(
    val screen_id: String,
    val name: String,
    val route: String? = null,
    val module: String? = null,
    val purpose: String? = null,
    val screenshot_url: String? = null,
    val permissions: String = "[]",
    val user_actions: String = "[]",
    val connected_screens: String = "[]",
    val empty_state: String? = null,
    val loading_state: String? = null,
    val error_state: String? = null,
    val feature_id: String? = null,
    val sort_order: Int = 0,
    val metadata: String = "{}",
)

@Serializable
data class UpdateScreenRequest(
    val name: String? = null,
    val route: String? = null,
    val module: String? = null,
    val purpose: String? = null,
    val screenshot_url: String? = null,
    val permissions: String? = null,
    val user_actions: String? = null,
    val connected_screens: String? = null,
    val empty_state: String? = null,
    val loading_state: String? = null,
    val error_state: String? = null,
    val feature_id: String? = null,
    val sort_order: Int? = null,
    val metadata: String? = null,
)

@Serializable
data class ScreenListResponse(
    val items: List<ScreenDto>,
    val total: Long,
    val page: Int,
    val page_size: Int,
    val total_pages: Int,
)

// ── API Mapping DTOs ────────────────────────────────────────────────────

@Serializable
data class ApiMappingDto(
    val id: String,
    val feature_id: String,
    val endpoint: String,
    val method: String,
    val description: String? = null,
    val db_entities: String = "[]",
    val caching: String? = null,
    val feature_flag: String? = null,
    val analytics_events: String = "[]",
    val notifications: String = "[]",
    val is_documented: Boolean = false,
    val sort_order: Int = 0,
)

@Serializable
data class CreateApiMappingRequest(
    val endpoint: String,
    val method: String,
    val description: String? = null,
    val db_entities: String = "[]",
    val caching: String? = null,
    val feature_flag: String? = null,
    val analytics_events: String = "[]",
    val notifications: String = "[]",
    val is_documented: Boolean = false,
    val sort_order: Int = 0,
)

@Serializable
data class UpdateApiMappingRequest(
    val endpoint: String? = null,
    val method: String? = null,
    val description: String? = null,
    val db_entities: String? = null,
    val caching: String? = null,
    val feature_flag: String? = null,
    val analytics_events: String? = null,
    val notifications: String? = null,
    val is_documented: Boolean? = null,
    val sort_order: Int? = null,
)

// ── Test Case DTOs ──────────────────────────────────────────────────────

@Serializable
data class TestCaseDto(
    val id: String,
    val case_id: String,
    val feature_id: String,
    val feature_name: String? = null,
    val screen_id: String? = null,
    val api_id: String? = null,
    val title: String,
    val description: String? = null,
    val preconditions: String? = null,
    val test_steps: String = "[]",
    val expected_result: String? = null,
    val priority: String = "medium",
    val test_type: String = "functional",
    val status: String = "not_run",
    val assigned_to: String? = null,
    val assigned_to_name: String? = null,
    val build_version: String? = null,
    val environment: String? = null,
    val devices: String = "[]",
    val os_versions: String = "[]",
    val platform: String = "all",
    val last_tested_at: String? = null,
    val last_tested_by: String? = null,
    val failure_reason: String? = null,
    val metadata: String = "{}",
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class TestCaseDetailDto(
    val test_case: TestCaseDto,
    val attachments: List<AttachmentDto> = emptyList(),
)

@Serializable
data class CreateTestCaseRequest(
    val case_id: String,
    val feature_id: String,
    val screen_id: String? = null,
    val api_id: String? = null,
    val title: String,
    val description: String? = null,
    val preconditions: String? = null,
    val test_steps: String = "[]",
    val expected_result: String? = null,
    val priority: String = "medium",
    val test_type: String = "functional",
    val assigned_to: String? = null,
    val build_version: String? = null,
    val environment: String? = null,
    val devices: String = "[]",
    val os_versions: String = "[]",
    val platform: String = "all",
)

@Serializable
data class UpdateTestCaseRequest(
    val title: String? = null,
    val description: String? = null,
    val preconditions: String? = null,
    val test_steps: String? = null,
    val expected_result: String? = null,
    val priority: String? = null,
    val test_type: String? = null,
    val assigned_to: String? = null,
    val build_version: String? = null,
    val environment: String? = null,
    val devices: String? = null,
    val os_versions: String? = null,
    val platform: String? = null,
    val screen_id: String? = null,
    val api_id: String? = null,
)

@Serializable
data class UpdateTestCaseStatusRequest(
    val status: String,
    val failure_reason: String? = null,
    val build_version: String? = null,
    val environment: String? = null,
)

@Serializable
data class TestCaseListResponse(
    val items: List<TestCaseDto>,
    val total: Long,
    val page: Int,
    val page_size: Int,
    val total_pages: Int,
)

// ── Bug DTOs ────────────────────────────────────────────────────────────

@Serializable
data class BugSummaryDto(
    val id: String,
    val bug_id: String,
    val title: String,
    val description: String? = null,
    val status: String = "reported",
    val priority: String = "medium",
    val severity: String? = null,
    val feature_id: String? = null,
    val feature_name: String? = null,
    val screen_id: String? = null,
    val api_id: String? = null,
    val test_case_id: String? = null,
    val assigned_to: String? = null,
    val assigned_to_name: String? = null,
    val reported_by: String? = null,
    val reported_by_name: String? = null,
    val sla_due_at: String? = null,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class BugDetailDto(
    val bug: BugSummaryDto,
    val description: String? = null,
    val reproducibility: String? = null,
    val environment: String? = null,
    val build_version: String? = null,
    val platform: String? = null,
    val device: String? = null,
    val os_version: String? = null,
    val steps_to_reproduce: String = "[]",
    val expected_result: String? = null,
    val actual_result: String? = null,
    val screen_id: String? = null,
    val api_id: String? = null,
    val test_case_id: String? = null,
    val triaged_by: String? = null,
    val fixed_by: String? = null,
    val verified_by: String? = null,
    val resolved_at: String? = null,
    val closed_at: String? = null,
    val tags: String = "[]",
    val metadata: String = "{}",
    val comments: List<BugCommentDto> = emptyList(),
    val activity: List<BugActivityDto> = emptyList(),
    val attachments: List<AttachmentDto> = emptyList(),
)

@Serializable
data class CreateBugRequest(
    val title: String,
    val description: String? = null,
    val feature_id: String? = null,
    val screen_id: String? = null,
    val api_id: String? = null,
    val test_case_id: String? = null,
    val priority: String = "medium",
    val severity: String? = null,
    val reproducibility: String? = null,
    val environment: String? = null,
    val build_version: String? = null,
    val platform: String? = null,
    val device: String? = null,
    val os_version: String? = null,
    val steps_to_reproduce: String = "[]",
    val expected_result: String? = null,
    val actual_result: String? = null,
    val tags: String = "[]",
)

@Serializable
data class UpdateBugRequest(
    val title: String? = null,
    val description: String? = null,
    val feature_id: String? = null,
    val screen_id: String? = null,
    val api_id: String? = null,
    val test_case_id: String? = null,
    val priority: String? = null,
    val severity: String? = null,
    val reproducibility: String? = null,
    val environment: String? = null,
    val build_version: String? = null,
    val platform: String? = null,
    val device: String? = null,
    val os_version: String? = null,
    val steps_to_reproduce: String? = null,
    val expected_result: String? = null,
    val actual_result: String? = null,
    val tags: String? = null,
)

@Serializable
data class BugStatusUpdateRequest(
    val status: String,
)

@Serializable
data class BugAssignRequest(
    val assigned_to: String,
)

@Serializable
data class BugCommentDto(
    val id: String,
    val bug_id: String,
    val author_id: String? = null,
    val author_name: String? = null,
    val body: String,
    val mentions: String = "[]",
    val is_internal: Boolean = false,
    val created_at: String,
)

@Serializable
data class CreateBugCommentRequest(
    val body: String,
    val mentions: String = "[]",
    val is_internal: Boolean = false,
)

@Serializable
data class BugActivityDto(
    val id: String,
    val bug_id: String,
    val actor_id: String? = null,
    val actor_name: String? = null,
    val action: String,
    val field: String? = null,
    val old_value: String? = null,
    val new_value: String? = null,
    val created_at: String,
)

@Serializable
data class BugListResponse(
    val items: List<BugSummaryDto>,
    val total: Long,
    val page: Int,
    val page_size: Int,
    val total_pages: Int,
)

@Serializable
data class BugKanbanResponse(
    val columns: Map<String, List<BugSummaryDto>>,
)

// ── Attachment DTOs ─────────────────────────────────────────────────────

@Serializable
data class AttachmentDto(
    val id: String,
    val file_name: String,
    val file_url: String,
    val file_type: String,
    val mime_type: String? = null,
    val file_size_bytes: Long? = null,
    val uploaded_by: String? = null,
    val created_at: String,
)

// ── Dashboard DTOs ──────────────────────────────────────────────────────

@Serializable
data class DashboardHealthDto(
    val overall_score: Double,
    val feature_completion: Double,
    val testing_progress: Double,
    val release_readiness: Double,
    val bug_health: Double,
    val bug_density: String,
    val regression_risk: String,
    val total_features: Int,
    val total_test_cases: Int,
    val total_bugs: Int,
    val open_bugs: Int,
)

@Serializable
data class ChartDatumDto(
    val label: String,
    val value: Long,
    val color: String? = null,
)

@Serializable
data class TestingProgressDto(
    val passed: Long,
    val failed: Long,
    val pending: Long,
    val blocked: Long,
    val need_retest: Long,
    val in_progress: Long,
)

@Serializable
data class BugSummaryBySeverityDto(
    val critical: Long,
    val major: Long,
    val normal: Long,
    val minor: Long,
    val cosmetic: Long,
)

@Serializable
data class RecentActivityDto(
    val id: String,
    val actor_id: String? = null,
    val actor_name: String? = null,
    val action: String,
    val entity_type: String,
    val entity_id: String? = null,
    val created_at: String,
)

@Serializable
data class RiskIndicatorDto(
    val blocked_features: Long,
    val critical_bugs: Long,
    val sla_breaches: Long,
    val high_risk_features: Long,
    val apis_down: Long,
)

@Serializable
data class UpcomingReleaseDto(
    val target_release: String,
    val features: List<FeatureDto>,
)

// ── Audit DTOs ──────────────────────────────────────────────────────────

@Serializable
data class AuditLogDto(
    val id: String,
    val actor_id: String? = null,
    val actor_name: String? = null,
    val action: String,
    val entity_type: String,
    val entity_id: String? = null,
    val old_snapshot: String? = null,
    val new_snapshot: String? = null,
    val ip_address: String? = null,
    val user_agent: String? = null,
    val created_at: String,
)

@Serializable
data class AuditListResponse(
    val items: List<AuditLogDto>,
    val total: Long,
    val page: Int,
    val page_size: Int,
    val total_pages: Int,
)

// ── Notification DTOs ───────────────────────────────────────────────────

@Serializable
data class PlatformNotificationDto(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
    val entity_type: String? = null,
    val entity_id: String? = null,
    val deep_link: String? = null,
    val is_read: Boolean = false,
    val created_at: String,
)

@Serializable
data class NotificationSummaryDto(
    val unread_count: Long,
)

// ── User DTOs ───────────────────────────────────────────────────────────

@Serializable
data class PlatformUserDto(
    val id: String,
    val name: String,
    val role: String,
)

// ── Discovery DTOs ──────────────────────────────────────────────────────

@Serializable
data class DiscoveredScreenDto(
    val id: String,
    val screen_id: String,
    val name: String,
    val module: String,
    val file_path: String,
    val portal: String? = null,
    val overlay_enum: String? = null,
    val deep_link_path: String? = null,
    val is_mapped: Boolean = false,
    val mapped_screen_id: String? = null,
    val discovered_at: String,
    val last_seen_at: String,
    val file_modified_at: String? = null,
)

@Serializable
data class DiscoveredApiDto(
    val id: String,
    val method: String,
    val path: String,
    val file_path: String,
    val feature_package: String? = null,
    val description: String? = null,
    val is_mapped: Boolean = false,
    val mapped_api_id: String? = null,
    val is_alive: Boolean? = null,
    val last_checked_at: String? = null,
    val response_ms: Int? = null,
    val status_code: Int? = null,
    val discovered_at: String,
    val last_seen_at: String,
)

@Serializable
data class LinkDiscoveredScreenRequest(
    val feature_id: String,
    val screen_name: String? = null,
)

@Serializable
data class LinkDiscoveredApiRequest(
    val feature_id: String,
    val description: String? = null,
)

@Serializable
data class FeatureFileDto(
    val id: String,
    val feature_id: String,
    val file_path: String,
    val file_type: String,
    val last_modified_at: String? = null,
    val last_commit_sha: String? = null,
    val last_commit_msg: String? = null,
    val last_commit_author: String? = null,
)

@Serializable
data class LinkFileRequest(
    val file_path: String,
    val file_type: String,
)

@Serializable
data class HealthCheckDto(
    val id: String,
    val discovered_api_id: String,
    val checked_at: String,
    val status_code: Int? = null,
    val response_ms: Int? = null,
    val is_alive: Boolean? = null,
    val error_message: String? = null,
)

@Serializable
data class HealthSummaryDto(
    val total: Long,
    val alive: Long,
    val down: Long,
    val slow: Long,
    val untested: Long,
)

@Serializable
data class DiscoveryScanResult(
    val discovered: Int,
    val updated: Int,
    val stale: Int,
)

@Serializable
data class DiscoveredScreenListResponse(
    val items: List<DiscoveredScreenDto>,
    val total: Long,
    val page: Int,
    val page_size: Int,
)

@Serializable
data class DiscoveredApiListResponse(
    val items: List<DiscoveredApiDto>,
    val total: Long,
    val page: Int,
    val page_size: Int,
)
