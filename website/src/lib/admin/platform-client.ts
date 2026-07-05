"use client";

/**
 * Platform API client for Feature & QA Management.
 * Uses the same authenticated fetch pattern as the admin client.
 */

import { API_BASE_URL, ApiError, type ApiEnvelope } from "@/lib/api";
import { readSession } from "@/lib/admin/session";

async function platformFetch<T>(
  path: string,
  options: { method?: string; body?: unknown } = {},
): Promise<T> {
  const session = readSession();
  const token = session?.token ?? null;

  const res = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      Platform: "web",
    },
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  const json: ApiEnvelope<T> = await res.json().catch(() => ({
    success: false,
    message: "Network error",
  }));

  if (!res.ok || !json.success) {
    throw new ApiError(
      json.message ?? "Request failed",
      res.status,
      json.error_code,
    );
  }

  return json.data as T;
}

// ── Types ────────────────────────────────────────────────────────────────

export interface FeatureDto {
  id: string;
  feature_id: string;
  name: string;
  description?: string;
  business_goal?: string;
  product_area?: string;
  category?: string;
  module?: string;
  parent_id?: string;
  status: string;
  completion_pct: number;
  priority: string;
  severity?: string;
  business_impact?: string;
  tech_complexity?: string;
  risk_level?: string;
  dependencies: string;
  blockers?: string;
  estimated_effort?: string;
  owner_id?: string;
  owner_name?: string;
  team?: string;
  sprint?: string;
  version_intro?: string;
  target_release?: string;
  release_status?: string;
  tags: string;
  metadata: string;
  legacy_imported: boolean;
  is_archived: boolean;
  created_at: string;
  updated_at: string;
}

export interface FeatureDetailDto {
  feature: FeatureDto;
  flows: FlowDto[];
  screens: ScreenDto[];
  apis: ApiMappingDto[];
  test_cases: TestCaseDto[];
  bugs: BugSummaryDto[];
  children: FeatureDto[];
}

export interface FeatureListResponse {
  items: FeatureDto[];
  total: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export interface FeatureTreeNode {
  feature: FeatureDto;
  children: FeatureTreeNode[];
}

export interface FlowDto {
  id: string;
  feature_id: string;
  flow_name: string;
  flow_description?: string;
  flow_steps: string;
  entry_points: string;
  exit_points: string;
  deep_links: string;
  edge_cases: string;
  sort_order: number;
}

export interface ScreenDto {
  id: string;
  screen_id: string;
  name: string;
  route?: string;
  module?: string;
  purpose?: string;
  screenshot_url?: string;
  permissions: string;
  user_actions: string;
  connected_screens: string;
  empty_state?: string;
  loading_state?: string;
  error_state?: string;
  feature_id?: string;
  feature_name?: string;
  sort_order: number;
  metadata: string;
}

export interface ScreenListResponse {
  items: ScreenDto[];
  total: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export interface ApiMappingDto {
  id: string;
  feature_id: string;
  endpoint: string;
  method: string;
  description?: string;
  db_entities: string;
  caching?: string;
  feature_flag?: string;
  analytics_events: string;
  notifications: string;
  is_documented: boolean;
  sort_order: number;
}

export interface TestCaseDto {
  id: string;
  case_id: string;
  feature_id: string;
  feature_name?: string;
  screen_id?: string;
  api_id?: string;
  title: string;
  description?: string;
  preconditions?: string;
  test_steps: string;
  expected_result?: string;
  priority: string;
  test_type: string;
  status: string;
  assigned_to?: string;
  assigned_to_name?: string;
  build_version?: string;
  environment?: string;
  devices: string;
  os_versions: string;
  platform: string;
  last_tested_at?: string;
  last_tested_by?: string;
  failure_reason?: string;
  metadata: string;
  created_at: string;
  updated_at: string;
}

export interface TestCaseListResponse {
  items: TestCaseDto[];
  total: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export interface BugSummaryDto {
  id: string;
  bug_id: string;
  title: string;
  status: string;
  priority: string;
  severity?: string;
  feature_id?: string;
  feature_name?: string;
  assigned_to?: string;
  assigned_to_name?: string;
  reported_by?: string;
  reported_by_name?: string;
  sla_due_at?: string;
  created_at: string;
  updated_at: string;
}

export interface BugListResponse {
  items: BugSummaryDto[];
  total: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export interface BugCommentDto {
  id: string;
  bug_id: string;
  author_id?: string;
  author_name?: string;
  body: string;
  mentions: string;
  is_internal: boolean;
  created_at: string;
}

export interface BugActivityDto {
  id: string;
  bug_id: string;
  actor_id?: string;
  actor_name?: string;
  action: string;
  field?: string;
  old_value?: string;
  new_value?: string;
  created_at: string;
}

export interface AttachmentDto {
  id: string;
  file_name: string;
  file_url: string;
  file_type: string;
  mime_type?: string;
  file_size_bytes?: number;
  uploaded_by?: string;
  created_at: string;
}

export interface BugDetailDto {
  bug: BugSummaryDto;
  description?: string;
  reproducibility?: string;
  environment?: string;
  build_version?: string;
  platform?: string;
  device?: string;
  os_version?: string;
  steps_to_reproduce: string;
  expected_result?: string;
  actual_result?: string;
  screen_id?: string;
  api_id?: string;
  test_case_id?: string;
  triaged_by?: string;
  fixed_by?: string;
  verified_by?: string;
  resolved_at?: string;
  closed_at?: string;
  tags: string;
  metadata: string;
  comments: BugCommentDto[];
  activity: BugActivityDto[];
  attachments: AttachmentDto[];
}

export interface AuditLogDto {
  id: string;
  actor_id?: string;
  actor_name?: string;
  action: string;
  entity_type: string;
  entity_id?: string;
  old_snapshot?: string;
  new_snapshot?: string;
  ip_address?: string;
  user_agent?: string;
  created_at: string;
}

export interface AuditListResponse {
  items: AuditLogDto[];
  total: number;
  page: number;
  page_size: number;
  total_pages: number;
}

export interface DiscoveredScreenDto {
  id: string;
  screen_id: string;
  name: string;
  module: string;
  file_path: string;
  portal?: string;
  overlay_enum?: string;
  deep_link_path?: string;
  is_mapped: boolean;
  mapped_screen_id?: string;
  discovered_at: string;
  last_seen_at: string;
  file_modified_at?: string;
}

export interface DiscoveredApiDto {
  id: string;
  method: string;
  path: string;
  file_path: string;
  feature_package?: string;
  description?: string;
  is_mapped: boolean;
  mapped_api_id?: string;
  is_alive?: boolean;
  last_checked_at?: string;
  response_ms?: number;
  status_code?: number;
  discovered_at: string;
  last_seen_at: string;
}

export interface HealthSummaryDto {
  total: number;
  alive: number;
  down: number;
  slow: number;
  untested: number;
}

export interface HealthCheckDto {
  id: string;
  discovered_api_id: string;
  checked_at: string;
  status_code?: number;
  response_ms?: number;
  is_alive?: boolean;
  error_message?: string;
}

export interface DashboardHealthDto {
  overall_score: number;
  feature_completion: number;
  testing_progress: number;
  release_readiness: number;
  bug_health: number;
  bug_density: string;
  regression_risk: string;
  total_features: number;
  total_test_cases: number;
  total_bugs: number;
  open_bugs: number;
}

export interface ChartDatumDto {
  label: string;
  value: number;
  color?: string;
}

export interface TestingProgressDto {
  passed: number;
  failed: number;
  pending: number;
  blocked: number;
  need_retest: number;
  in_progress: number;
}

export interface BugSummaryBySeverityDto {
  critical: number;
  major: number;
  normal: number;
  minor: number;
  cosmetic: number;
}

export interface RecentActivityDto {
  id: string;
  actor_id?: string;
  actor_name?: string;
  action: string;
  entity_type: string;
  entity_id?: string;
  created_at: string;
}

export interface RiskIndicatorDto {
  blocked_features: number;
  critical_bugs: number;
  sla_breaches: number;
  high_risk_features: number;
  apis_down: number;
}

export interface PlatformUserDto {
  id: string;
  name: string;
  role: string;
}

export interface NotificationSummaryDto {
  unread_count: number;
}

export interface PlatformNotificationDto {
  id: string;
  category: string;
  title: string;
  body: string;
  entity_type?: string;
  entity_id?: string;
  deep_link?: string;
  is_read: boolean;
  created_at: string;
}

// ── API calls ────────────────────────────────────────────────────────────

export const platformApi = {
  // Dashboard
  dashboardHealth: () => platformFetch<DashboardHealthDto>("/api/admin/platform/dashboard/health"),
  dashboardFeaturesByStatus: () => platformFetch<ChartDatumDto[]>("/api/admin/platform/dashboard/features-by-status"),
  dashboardFeaturesByPriority: () => platformFetch<ChartDatumDto[]>("/api/admin/platform/dashboard/features-by-priority"),
  dashboardTestingProgress: () => platformFetch<TestingProgressDto>("/api/admin/platform/dashboard/testing-progress"),
  dashboardBugSummary: () => platformFetch<BugSummaryBySeverityDto>("/api/admin/platform/dashboard/bug-summary"),
  dashboardRecentActivity: () => platformFetch<RecentActivityDto[]>("/api/admin/platform/dashboard/recent-activity"),
  dashboardRiskIndicators: () => platformFetch<RiskIndicatorDto>("/api/admin/platform/dashboard/risk-indicators"),

  // Features
  listFeatures: (params?: Record<string, string | number | undefined>) => {
    const qs = new URLSearchParams();
    if (params) Object.entries(params).forEach(([k, v]) => { if (v != null) qs.set(k, String(v)); });
    return platformFetch<FeatureListResponse>(`/api/admin/platform/features?${qs}`);
  },
  getFeature: (id: string) => platformFetch<FeatureDetailDto>(`/api/admin/platform/features/${id}`),
  createFeature: (body: unknown) => platformFetch<FeatureDetailDto>("/api/admin/platform/features", { method: "POST", body }),
  updateFeature: (id: string, body: unknown) => platformFetch<FeatureDetailDto>(`/api/admin/platform/features/${id}`, { method: "PUT", body }),
  archiveFeature: (id: string) => platformFetch(`/api/admin/platform/features/${id}/archive`, { method: "POST" }),
  featureTree: () => platformFetch<FeatureTreeNode[]>("/api/admin/platform/features/tree"),

  // Screens
  listScreens: (params?: Record<string, string | number | undefined>) => {
    const qs = new URLSearchParams();
    if (params) Object.entries(params).forEach(([k, v]) => { if (v != null) qs.set(k, String(v)); });
    return platformFetch<ScreenListResponse>(`/api/admin/platform/screens?${qs}`);
  },

  // Test Cases
  listTestCases: (params?: Record<string, string | number | undefined>) => {
    const qs = new URLSearchParams();
    if (params) Object.entries(params).forEach(([k, v]) => { if (v != null) qs.set(k, String(v)); });
    return platformFetch<TestCaseListResponse>(`/api/admin/platform/test-cases?${qs}`);
  },
  myTestCases: () => platformFetch<TestCaseDto[]>("/api/admin/platform/test-cases/my"),

  // Bugs
  listBugs: (params?: Record<string, string | number | undefined>) => {
    const qs = new URLSearchParams();
    if (params) Object.entries(params).forEach(([k, v]) => { if (v != null) qs.set(k, String(v)); });
    return platformFetch<BugListResponse>(`/api/admin/platform/bugs?${qs}`);
  },
  bugKanban: () => platformFetch<Record<string, BugSummaryDto[]>>("/api/admin/platform/bugs/kanban"),

  // Notifications
  notificationSummary: () => platformFetch<NotificationSummaryDto>("/api/admin/platform/notifications/summary"),
  listNotifications: () => platformFetch<PlatformNotificationDto[]>("/api/admin/platform/notifications"),
  markNotificationRead: (id: string) => platformFetch(`/api/admin/platform/notifications/${id}/read`, { method: "POST" }),
  markAllNotificationsRead: () => platformFetch("/api/admin/platform/notifications/read-all", { method: "POST" }),

  // Users
  listUsers: () => platformFetch<PlatformUserDto[]>("/api/admin/platform/users"),

  // CSV Import
  importCsv: () => platformFetch<{ imported: number; skipped: number; errors: string[] }>("/api/admin/platform/import/csv", { method: "POST" }),

  // Screen Discovery
  scanScreens: () => platformFetch<{ discovered: number; updated: number; stale: number; errors: string[] }>("/api/admin/platform/discovery/screens/scan", { method: "POST" }),
  listDiscoveredScreens: (params?: Record<string, string | number | undefined>) => {
    const qs = new URLSearchParams();
    if (params) Object.entries(params).forEach(([k, v]) => { if (v != null) qs.set(k, String(v)); });
    return platformFetch<{ items: DiscoveredScreenDto[]; total: number; page: number; page_size: number }>(`/api/admin/platform/discovery/screens?${qs}`);
  },
  linkDiscoveredScreen: (id: string, featureId: string, screenName?: string) =>
    platformFetch(`/api/admin/platform/discovery/screens/${id}/link`, { method: "POST", body: { feature_id: featureId, screen_name: screenName } }),

  // API Discovery
  scanApis: () => platformFetch<{ discovered: number; updated: number; stale: number; errors: string[] }>("/api/admin/platform/discovery/apis/scan", { method: "POST" }),
  listDiscoveredApis: (params?: Record<string, string | number | undefined>) => {
    const qs = new URLSearchParams();
    if (params) Object.entries(params).forEach(([k, v]) => { if (v != null) qs.set(k, String(v)); });
    return platformFetch<{ items: DiscoveredApiDto[]; total: number; page: number; page_size: number }>(`/api/admin/platform/discovery/apis?${qs}`);
  },
  linkDiscoveredApi: (id: string, featureId: string, description?: string) =>
    platformFetch(`/api/admin/platform/discovery/apis/${id}/link`, { method: "POST", body: { feature_id: featureId, description } }),

  // Git Change Tracking
  refreshGit: () => platformFetch<{ updated: number; errors: string[] }>("/api/admin/platform/discovery/git/refresh", { method: "POST" }),

  // API Health Checks
  healthSummary: () => platformFetch<HealthSummaryDto>("/api/admin/platform/discovery/health/summary"),
  healthRecent: (limit?: number) => platformFetch<HealthCheckDto[]>(`/api/admin/platform/discovery/health/recent?limit=${limit ?? 50}`),
  healthCheckAll: () => platformFetch<{ discovered: number; updated: number; stale: number; errors: string[] }>("/api/admin/platform/discovery/health/check-all", { method: "POST" }),
  healthCheckOne: (id: string) => platformFetch<HealthCheckDto>(`/api/admin/platform/discovery/apis/${id}/check`, { method: "POST" }),
};
