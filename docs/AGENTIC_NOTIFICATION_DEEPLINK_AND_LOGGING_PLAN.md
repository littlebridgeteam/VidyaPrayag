# Agentic Notification Deep-Linking & Backend Log Viewer Plan

> **God Mode Ecosystem Integration Protocol** — Phase 0 audit complete.
> See `ECOSYSTEM_MAP.md` for the living feature/surface/event registry.

---

## 0. Executive Summary

Two features delivered as one integrated initiative:

1. **Backend Log Viewer** — Super admin web portal gains a "Logs" tab in Dev Tools that streams server-side structured logs (HTTP, AI calls, errors, jobs) in a developer-tool-like console with filtering, search, and severity levels.

2. **Universal Notification Deep-Linking** — Every notification (system push or in-app, any role, any category) becomes tappable and navigates the user to the relevant screen. If a target screen doesn't exist, it is created. Covers ALL categories: `attendance`, `marks`, `homework`, `announcement`, `leave`, `fees`, `link`, `pews_audit`, `dev_tools`, `general`, and any future category. Admin/teacher web portal activity feed items also become clickable.

---

## 1. One-Line Assumptions

1. Backend uses SLF4J + `println`; we introduce a structured `server_logs` table for queryable persistence.
2. `notifications` table already has `deep_link`, `ref_type`, `ref_id` columns — the gap is `NotificationDto` doesn't return them. Fix is additive.
3. Super admin portal is the Next.js `website/` app at `/admin/dev-tools`.
4. AI provider logs (e.g. Groq CSV: request_id, model, tokens, latency, status) should be captured in `server_logs` under category `ai`.

---

## 2. Phase 0 — Pre-Build Audit (Complete)

### 2.1 Existing Infrastructure

| Component | Location | Status |
|---|---|---|
| `NotificationsTable` | `server/.../db/Tables.kt:1433` | Has `deep_link`, `ref_type`, `ref_id` columns |
| `Notify.toUsers` | `server/.../notifications/Notify.kt:46` | Accepts `deepLink`, `refType`, `refId` params |
| `NotificationDto` | `server/.../notifications/NotificationsRouting.kt:51` | **Does NOT return** `deep_link`, `ref_type`, `ref_id` |
| `parseDeepLink()` | `composeApp/.../NavGraphV2.kt:175` | Handles `/parent`, `/teacher`, `/school`, `/alumni`, `/transport`, `/report-card`, `/tutor`, `/library`, `/events` |
| Admin bell dropdown | `website/.../Topbar.tsx:84` | Shows notifications; items NOT clickable |
| Activity feed | `website/.../ActivityFeed.tsx` | Displays activities; items NOT clickable |
| `NotificationsScreenV2` | `composeApp/.../NotificationsScreenV2.kt` | Click only calls `markRead` — no navigation |
| `NotificationsViewModel` | `shared/.../NotificationsViewModel.kt` | `NotificationItem` has no `deepLink` field |
| DevTools routing | `server/.../devtools/DevToolsRouting.kt` | Super-admin endpoints; no log endpoint |
| DevTools web page | `website/.../admin/dev-tools/page.tsx` | OTP, Pulse, Send-Notification, PEWS cards; no Logs |
| `AuditLogger` | `server/.../pews/core/AuditLogger.kt` | Writes to `notifications` with `refType`/`refId` |
| `LibraryAuditLogTable` | `server/.../db/Tables.kt:3208` | Pattern reference for `server_logs` |

### 2.2 Gap Analysis

| Gap | Fix |
|---|---|
| `NotificationDto` omits deep link fields | Add `deepLink`, `refType`, `refId` to DTO + client models |
| `NotificationRow` click only marks read | Add navigation via deep link |
| Admin bell items not clickable | Make items `<Link>`, navigate to admin route |
| Activity feed items not clickable | Add `onActivityClick` with route mapping |
| No `server_logs` table | New table + writer + API + UI |
| No log streaming endpoint | SSE endpoint for super admin |
| `parseDeepLink` missing `messages`, `leave`, `fees`, `announcement-detail`, `pews` | Extend parser |
| Synth bridge items (`ann_*`, `fee_*`) have no deep links | Generate them server-side |
| AI provider calls not logged in DB | Intercept AI gateway, write to `server_logs` |

### 2.3 Reuse Map

| Need | Reuse From |
|---|---|
| Structured log table | `LibraryAuditLogTable` pattern |
| Log API endpoint | `DevToolsRouting.kt` pattern (`requireSuperAdmin`) |
| Log viewer UI | `ActivityFeed.tsx` layout |
| Deep-link parsing | `parseDeepLink()` — extend, don't replace |
| Notification click → navigate | Existing `deepLinkTarget` flow in `NavGraphV2` |
| Admin web notification click | `next/navigation` `useRouter` |

### 2.4 TRUE Blast Radius

- **Server**: `NotificationsRouting.kt`, `Notify.kt`, `DevToolsRouting.kt`, all routes calling `Notify.*`
- **Shared**: `NotificationsViewModel.kt`, `ParentFeatureModels.kt`, `NotificationRepository.kt`
- **Compose App**: `NotificationsScreenV2.kt`, `NavGraphV2.kt`, all three portal files
- **Web Admin**: `Topbar.tsx`, `ActivityFeed.tsx`, `dev-tools/page.tsx`, `nav.ts`, `types.ts`, `client.ts`
- **New files**: `ServerLogsTable.kt`, `ServerLogWriter.kt`, `ServerLogRouting.kt`, `LogViewer.tsx`, detail screens

---

## 3. Feature 1: Backend Log Viewer

### 3.1 Database Schema — New Table: `server_logs`

```sql
CREATE TABLE server_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID,                        -- nullable: system-wide logs have no school
    timestamp       TIMESTAMP NOT NULL,
    level           VARCHAR(8) NOT NULL,         -- TRACE | DEBUG | INFO | WARN | ERROR
    category        VARCHAR(32) NOT NULL,        -- http | ai | job | auth | notification | pews | sync | general
    message         TEXT NOT NULL,
    actor_id        UUID,                        -- who triggered it (nullable = system)
    endpoint        TEXT,                        -- e.g. "POST /api/v1/school/attendance"
    status_code     INTEGER,                     -- HTTP status if applicable
    duration_ms     BIGINT,                      -- request duration if applicable
    details_json    TEXT DEFAULT '{}',           -- structured context (request body, error stack, AI tokens, etc.)
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_sl_timestamp ON server_logs(timestamp DESC);
CREATE INDEX idx_sl_level ON server_logs(level);
CREATE INDEX idx_sl_category ON server_logs(category);
CREATE INDEX idx_sl_school ON server_logs(school_id);
```

#### Exposed Table Definition

```kotlin
object ServerLogsTable : UUIDTable("server_logs", "id") {
    val schoolId    = uuid("school_id").nullable()
    val timestamp   = timestamp("timestamp")
    val level       = varchar("level", 8)
    val category    = varchar("category", 32)
    val message     = text("message")
    val actorId     = uuid("actor_id").nullable()
    val endpoint    = text("endpoint").nullable()
    val statusCode  = integer("status_code").nullable()
    val durationMs  = long("duration_ms").nullable()
    val detailsJson = text("details_json").default("{}")
    val createdAt   = timestamp("created_at")
}
```

### 3.2 Backend: ServerLogWriter

Singleton — same pattern as `AuditLogger`. Fire-and-forget via `CoroutineScope(Dispatchers.IO).launch`.

```kotlin
object ServerLogWriter {
    suspend fun write(
        level: String,          // TRACE|DEBUG|INFO|WARN|ERROR
        category: String,       // http|ai|job|auth|notification|pews|sync|general
        message: String,
        schoolId: UUID? = null,
        actorId: UUID? = null,
        endpoint: String? = null,
        statusCode: Int? = null,
        durationMs: Long? = null,
        details: Map<String, Any?> = emptyMap(),
    )
}
```

**Key behaviors:**
- Non-blocking coroutine — never blocks the caller.
- Dual-write: structured DB row + SLF4J console/file log.
- Auto-truncates `message` to 2000 chars; `detailsJson` to 8000 chars.
- Rate-limited: max 1000 rows/minute (drops with SLF4J warn).
- Daily cleanup job deletes logs older than 30 days (configurable via `app_config.log_retention_days`).
- Table cap: 100,000 rows; oldest pruned first.

### 3.3 Backend: ServerLogRouting

Super-admin-only endpoints mounted in `DevToolsRouting.kt`:

```
GET  /api/v1/admin/dev/logs          — paginated log query
  ?level=ERROR &category=ai &search=keyword &schoolId=uuid &since=iso &until=iso &limit=100 &offset=0

GET  /api/v1/admin/dev/logs/stream   — SSE stream (real-time)
  ?level=WARN &category=ai

GET  /api/v1/admin/dev/logs/stats    — aggregate stats (counts by level/category, top errors, AI token usage)
```

**DTOs:**

```kotlin
@Serializable
data class ServerLogDto(
    val id: String,
    val timestamp: String,
    val level: String,
    val category: String,
    val message: String,
    val actorId: String? = null,
    val endpoint: String? = null,
    val statusCode: Int? = null,
    val durationMs: Long? = null,
    val details: JsonObject = JsonObject(emptyMap()),
)

@Serializable
data class ServerLogsPageDto(
    val logs: List<ServerLogDto>,
    val total: Int,
    val offset: Int,
    val limit: Int,
)

@Serializable
data class ServerLogStatsDto(
    val byLevel: Map<String, Int>,
    val byCategory: Map<String, Int>,
    val totalLast24h: Int,
    val topErrors: List<ServerLogDto>,
    val aiTokenUsage: AiTokenUsageSummary,
)

@Serializable
data class AiTokenUsageSummary(
    val totalRequests: Int,
    val totalInputTokens: Int,
    val totalOutputTokens: Int,
    val totalErrors: Int,
    val avgLatencyMs: Double,
    val byModel: Map<String, Int>,
)
```

### 3.4 Backend: AI Call Logging

Intercept AI gateway calls (Groq/OpenAI) and write to `server_logs` with category `ai`. Captures data matching the Groq CSV format: model, status_code, input_tokens, output_tokens, latency, error details.

```kotlin
ServerLogWriter.write(
    level = if (statusCode == 200) "INFO" else "WARN",
    category = "ai",
    message = "AI call: $model → $status (${durationMs}ms)",
    endpoint = "groq://chat/completions",
    statusCode = statusCode,
    durationMs = durationMs,
    details = mapOf(
        "model" to model,
        "input_tokens" to inputTokens,
        "output_tokens" to outputTokens,
        "request_id" to requestId,
        "ttft_ms" to timeToFirstToken,
    ),
)
```

Gives super admin real-time visibility into: model usage, token consumption, error rates (including 429 rate limits), latency.

### 3.5 Backend: HTTP Request Logging Middleware

Ktor intercept plugin that logs every HTTP request:

```kotlin
intercept(ApplicationCallPipeline.Monitoring) {
    val start = System.currentTimeMillis()
    try { proceed() } finally {
        val duration = System.currentTimeMillis() - start
        val status = call.response.status()?.value ?: 0
        val level = when {
            status >= 500 -> "ERROR"
            status >= 400 -> "WARN"
            else -> "INFO"
        }
        ServerLogWriter.write(
            level = level, category = "http",
            message = "${call.request.httpMethod.value} ${call.request.uri} → $status (${duration}ms)",
            actorId = call.principalUserUuid(),
            endpoint = "${call.request.httpMethod.value} ${call.request.uri}",
            statusCode = status, durationMs = duration,
        )
    }
}
```

### 3.6 Web UI: LogViewer Component

New tab in Dev Tools page. Developer-tool-like console:

```
┌──────────────────────────────────────────────────────────────────────┐
│  SERVER LOGS                                          [Live ●] [⏸]  │
│                                                                      │
│  [Level: ALL ▾] [Category: ALL ▾] [Search: ___________] [Refresh]   │
│                                                                      │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │ 13:28:17  ERROR  ai       AI call: llama-3.3-70b → 429        │  │
│  │   rate_limit_exceeded: TPD limit 100000, used 97760           │  │
│  │   model: llama-3.3-70b-versatile | tokens: 0 | latency: 4ms   │  │
│  │   [details ▾]                                                 │  │
│  ├────────────────────────────────────────────────────────────────┤  │
│  │ 13:25:42  WARN   ai       AI call: gpt-oss-120b → 400        │  │
│  │   tool_use_failed: tool 'TutorTurn' not in request.tools      │  │
│  │   model: openai/gpt-oss-120b | input: 2134 | output: 614     │  │
│  │   [details ▾]                                                 │  │
│  ├────────────────────────────────────────────────────────────────┤  │
│  │ 13:20:05  INFO   http     POST /api/v1/school/attendance → 200│  │
│  │   45ms | actor: a3f4... | school: b2c1...                    │  │
│  │   [details ▾]                                                 │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  Stats: 1,247 logs (24h) | 3 ERROR | 12 WARN | 1,232 INFO           │
│  AI: 89 reqs | 45.2k in | 12.1k out | 2 errors | avg 520ms          │
│                                                                      │
│  [Load more] or [Auto-scroll on new logs]                            │
└──────────────────────────────────────────────────────────────────────┘
```

**Key UI behaviors:**
- **Live mode**: SSE connection; auto-scrolls to bottom. Pause/Resume button.
- **Level filter**: ALL, TRACE, DEBUG, INFO, WARN, ERROR.
- **Category filter**: ALL, http, ai, job, auth, notification, pews, sync, general.
- **Search**: Full-text on `message` (server-side ILIKE).
- **Expandable rows**: Click `[details ▾]` to expand `details_json`.
- **Color coding**: ERROR=red, WARN=amber, INFO=gray, DEBUG=blue, TRACE=light gray.
- **Stats bar**: Aggregate counts + AI token usage summary.
- **Export**: Download filtered set as CSV.
- **Reuses**: `Card`, `Badge`, `FadeIn`, `EmptyState` from existing admin components. No new UI pattern.

### 3.7 Log Retention

- Daily cleanup job (2 AM) deletes logs older than 30 days.
- Configurable via `app_config` key `log_retention_days` (default `30`).
- Table cap: 100,000 rows; oldest entries pruned first.

---

## 4. Feature 2: Universal Notification Deep-Linking

### 4.1 Principle

**Every notification is tappable. Every tap navigates. Every navigation lands on a meaningful screen.**

No exceptions. If a notification category has no target screen today, we create one. The deep link flows from backend → DTO → ViewModel → UI → navigation graph.

### 4.2 Backend: Extend NotificationDto

**File**: `server/.../notifications/NotificationsRouting.kt`

Add three fields to `NotificationDto`:

```kotlin
@Serializable
data class NotificationDto(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean = true,
    val deepLink: String? = null,      // NEW
    val refType: String? = null,       // NEW
    val refId: String? = null,         // NEW
)
```

Update the GET list endpoint mapping to include the three columns from `NotificationsTable`.

### 4.3 Backend: Deep Links for Synth Bridge Items

Parent synth bridge items (`ann_*`, `fee_*`) currently have no deep links. Generate them:

```kotlin
// Announcement synth:
deepLink = "/parent/announcements/${row[AnnouncementsTable.id].value}",
refType = "announcement",
refId = row[AnnouncementsTable.id].value.toString(),

// Fee synth:
deepLink = "/parent/fees/${row[FeeRecordsTable.id].value}",
refType = "fee",
refId = row[FeeRecordsTable.id].value.toString(),
```

### 4.4 Backend: Audit All Notify.toUsers Callers

Every `Notify.toUsers` / `Notify.toUser` call must pass `deepLink`. Mechanical audit — grep for all call sites.

| Caller | Required deepLink |
|---|---|
| Attendance | `/parent/attendance?childId={childId}&date={date}` |
| Marks | `/parent/marks?childId={childId}&examId={examId}` |
| Homework | `/parent/homework?homeworkId={hwId}` |
| Announcement | `/parent/announcements/{annId}` |
| Leave apply | `/teacher/leave?requestId={reqId}` |
| Leave decided | `/parent/leave?requestId={reqId}` |
| Fee status | `/parent/fees/{feeId}` |
| Link-child | `/parent/dashboard` |
| PEWS alert | `/parent/pews?childId={childId}` |
| Report card | `/parent/report-card?childId={childId}&term={term}` |
| Alumni campaign | `/alumni/campaign/{campaignId}` |
| DevTools send | Already passed by user |

### 4.5 Shared (KMP): Extend Client Models

**`shared/.../parent/domain/model/ParentFeatureModels.kt`** — add `deepLink`, `refType`, `refId` to `ParentNotificationDto`.

**`shared/.../parent/presentation/NotificationsViewModel.kt`** — add `deepLink` to `NotificationItem`:

```kotlin
data class NotificationItem(
    ...
    val deepLink: String? = null,      // NEW
)
```

Map it from the DTO in the repository/ViewModel mapping.

### 4.6 Compose App: Notification Tap → Navigate

**`composeApp/.../NotificationsScreenV2.kt`** — add `onDeepLink: (String) -> Unit` callback. `NotificationRow` click now marks read AND calls `onDeepLink`:

```kotlin
.clickable {
    viewModel.markRead(item.id)
    item.deepLink?.let { onDeepLink(it) }
}
```

**Each portal** (`ParentPortalV2`, `TeacherPortalV2`, `SchoolPortalV2`) wires `onDeepLink`:

```kotlin
NotificationsScreenV2(
    onDeepLink = { deepLinkString ->
        val target = parseDeepLink(deepLinkString, role)
        overlay = Overlay.None  // close notifications
        deepLinkTarget = target // trigger navigation
    }
)
```

### 4.7 Compose App: Extend parseDeepLink

**`composeApp/.../NavGraphV2.kt`** — add missing path patterns:

| Path Pattern | DeepLinkTarget |
|---|---|
| `/messages` or `/{role}/messages?threadId={id}` | `DeepLinkTarget.Messages(threadId)` |
| `/{role}/leave?requestId={id}` | Role-aware screen target |
| `/{role}/fees/{id}` | `DeepLinkTarget.ParentTab("fees", params)` |
| `/parent/announcements/{id}` | `DeepLinkTarget.ParentTab("announcements", params)` |
| `/{role}/pews?childId={id}` | Role-aware screen target |
| `/{role}/report-card?...` | Role-aware screen target |

Add new sealed class subtype:

```kotlin
data class Messages(val threadId: String? = null) : DeepLinkTarget()
```

### 4.8 Compose App: New Detail Screens

For categories with no existing target screen, create minimal detail screens using existing design system (VTheme, VCard, VScreenScaffold, VSubHeader):

| Screen | Deep Link Source | Content |
|---|---|---|
| `AnnouncementDetailScreen` | `/parent/announcements/{id}` | Full announcement text, image, date |
| `FeeDetailScreen` | `/parent/fees/{id}` | Fee breakdown, due date, payment status |
| `LeaveDetailScreen` | `/parent/leave?requestId={id}` | Leave request status, dates, reason |

These are NOT new UI patterns — they reuse `VSubHeader` + `VCard` + `VTheme` exactly like existing overlay screens.

### 4.9 Web Admin: Notification Bell Clickable

**`website/.../Topbar.tsx`** — change notification items from `<div>` to `<Link>`:

```tsx
<Link
  key={n.id}
  href={mapDeepLinkToAdminRoute(n.deep_link, n.category)}
  onClick={() => { if (n.unread) adminApi.markNotificationRead(n.id); }}
  className="flex gap-3 px-4 py-3 hover:bg-navy/[0.03] cursor-pointer ..."
>
```

**Web admin deep link mapping** (mobile paths → web admin routes):

```typescript
function mapDeepLinkToAdminRoute(deepLink: string | null, category: string): string {
  const routeMap: Record<string, string> = {
    announcement: "/admin/announcements",
    leave: "/admin/leave",
    fees: "/admin/fees",
    attendance: "/admin/attendance",
    marks: "/admin/marks",
    homework: "/admin/academics",
    pews_audit: "/admin/early-warning",
    "report-card": "/admin/report-card",
  };
  return routeMap[category] ?? "/admin/dashboard";
}
```

### 4.10 Web Admin: Activity Feed Clickable

**`website/.../ActivityFeed.tsx`** — add `onActivityClick` callback to each item:

```tsx
<div onClick={() => onActivityClick(item)} className="... cursor-pointer hover:bg-navy/[0.03]">
```

Uses the same `mapDeepLinkToAdminRoute` function.

### 4.11 Web Admin: Extend Types

**`website/.../admin/types.ts`** — add to notification type:

```typescript
deep_link?: string;
ref_type?: string;
ref_id?: string;
```

---

## 5. Phase 1 — Integration Dimensions Checklist

### 5.1 Data
- `server_logs` table with indexes on timestamp, level, category, school_id
- `NotificationDto` extended with `deepLink`, `refType`, `refId`
- All client-side DTOs and UI models extended
- Synth bridge items get generated deep links
- Log retention policy (30 days, 100k cap)

### 5.2 Event
- `ServerLogWriter.write()` is the single write-path for structured logs
- HTTP middleware auto-logs every request
- AI gateway interceptor logs every AI call with token counts
- `Notify.toUsers` calls now consistently pass `deepLink`
- Log entries queryable via API (not just console)

### 5.3 Intelligence
- AI token usage summary in log stats endpoint
- Top errors surfaced in stats
- Log category `ai` captures model, tokens, latency for cost analysis
- Future hook: anomaly detection on log patterns (see §7)

### 5.4 UI
- LogViewer uses existing Card/Badge/FadeIn/EmptyState components
- Notification bell items become `<Link>` elements (no new pattern)
- Activity feed items become clickable (no new pattern)
- Mobile notification rows get `clickable` modifier (existing pattern)
- New detail screens use VTheme/VCard/VScreenScaffold (existing design system)
- Log level color coding uses existing Tailwind palette

### 5.5 Cross-Surface
- Mobile (Compose MP): notification tap → deep link → portal navigation
- Web admin (Next.js): notification bell → route navigation
- Web admin: activity feed → route navigation
- Web admin: log viewer → dev tools section
- Push notification (FCM): already carries `deep_link` in payload data

### 5.6 Navigation
- `parseDeepLink` extended with `messages`, `leave`, `fees`, `announcements/{id}`, `pews`, `report-card`
- `DeepLinkTarget` sealed class extended with `Messages` subtype
- Web admin has `mapDeepLinkToAdminRoute` for mobile→web path translation
- All three mobile portals accept and apply deep link targets

### 5.7 Permissions
- Log viewer: super admin only (`requireSuperAdmin()`)
- Log API: super admin only
- Notification deep linking: role-aware — `parseDeepLink` takes `role` param
- School-scoped logs: super admin sees all; school admin sees own school (future)
- Notification read/mark: already scoped to `jwt.sub` (existing)

### 5.8 State
- Notification read state: existing `isRead`/`readAt`, no change
- Log viewer: ephemeral filter state (React useState)
- Live stream: SSE in useEffect, cleaned up on unmount
- Mobile deep link: applied via `LaunchedEffect(deepLinkTarget)` — existing pattern

### 5.9 Performance
- Log query: paginated (default 100, max 500), indexed on timestamp DESC
- Log write: fire-and-forget coroutine, never blocks request
- Log retention: daily cleanup, 30-day retention, 100k row cap
- SSE stream: filtered server-side (level + category)
- Notification list: existing 200-row limit, no change
- Deep link parsing: in-memory string parsing, no I/O

---

## 6. Phase 2 — Build Rules

### 6.1 Full Loop Rule
Every notification sent must carry a deep link. Every deep link must resolve to a screen. Every screen must show meaningful content. No dead-end notifications.

### 6.2 No Extra UI Steps
- Tapping a notification marks read AND navigates in one action.
- Log viewer loads with sensible defaults (last 100, INFO+, all categories). No mandatory filter selection.

### 6.3 No New UI Patterns
- LogViewer reuses `Card`, `Badge`, `FadeIn`, `EmptyState`.
- New mobile screens reuse `VSubHeader`, `VCard`, `VScreenScaffold`, `VTheme`.
- Bell items change from `<div>` to `<Link>` — same layout, just clickable.
- Activity items add `onClick` — same layout.

### 6.4 Conflict-Free
- `NotificationDto` fields are additive (nullable/optional) — no breaking change.
- `server_logs` is a new table — no migration of existing data.
- `parseDeepLink` extensions are additive — existing paths still resolve.
- HTTP logging middleware is a new intercept — does not modify existing handling.

---

## 7. Hooks Left Open for Future Features

| Hook | Location | Future Use |
|---|---|---|
| `server_logs.category = "sync"` | ServerLogWriter | Offline sync engine telemetry |
| `server_logs.category = "job"` | ServerLogWriter | Scheduled job execution logging |
| `server_logs.details_json` | ServerLogWriter | Arbitrary structured context for any feature |
| `DeepLinkTarget.Messages(threadId)` | NavGraphV2 | Deep link to specific chat thread |
| `mapDeepLinkToAdminRoute` | Web admin | Extend with new admin pages |
| Log stats endpoint | ServerLogRouting | Foundation for anomaly detection alerts |
| AI token usage summary | ServerLogStatsDto | Foundation for AI cost dashboard and budget alerts |
| `app_config.log_retention_days` | app_config | Configurable retention without code change |
| SSE log stream | ServerLogRouting | Foundation for real-time admin dashboard widgets |

---

## 8. Phase 3 — Post-Build Verification

### 8.1 Feature Wired In
- [ ] `GET /api/v1/notifications` returns `deepLink`, `refType`, `refId` for every notification
- [ ] Every `Notify.toUsers` / `Notify.toUser` call passes a `deepLink` parameter
- [ ] `GET /api/v1/admin/dev/logs` returns paginated logs with filtering
- [ ] `GET /api/v1/admin/dev/logs/stream` returns SSE stream
- [ ] `GET /api/v1/admin/dev/logs/stats` returns aggregate stats
- [ ] HTTP middleware logs every request to `server_logs`
- [ ] AI gateway logs every AI call to `server_logs`
- [ ] Log viewer tab visible in Dev Tools (super admin only)
- [ ] Notification bell items in web admin are clickable `<Link>` elements
- [ ] Activity feed items in web admin are clickable
- [ ] Mobile notification rows navigate on tap (not just mark read)
- [ ] `parseDeepLink` handles all notification category paths
- [ ] New detail screens render for announcement, fee, leave detail

### 8.2 Feels Native
- [ ] Log viewer matches admin portal design language
- [ ] Notification tap animation consistent with other tap interactions
- [ ] New detail screens use VTheme colors, VCard layout, VSubHeader
- [ ] No "loading..." flash on notification tap
- [ ] Log viewer live mode auto-scrolls smoothly

### 8.3 Ecosystem Map Updated
- [ ] `ECOSYSTEM_MAP.md` updated with new entries (see §10)

---

## 9. Phase 4 — Build Execution Order

### Phase 4A: Backend Log Viewer (Server → Web)

| Step | File(s) | Action |
|---|---|---|
| 1 | `docs/db/` | Create migration SQL for `server_logs` table |
| 2 | `server/.../db/Tables.kt` | Add `ServerLogsTable` Exposed object |
| 3 | `server/.../db/DatabaseFactory.kt` | Add `ServerLogsTable` to schema creation |
| 4 | `server/.../feature/logging/ServerLogWriter.kt` | NEW — singleton log writer |
| 5 | `server/.../feature/logging/ServerLogRouting.kt` | NEW — log query/stream/stats endpoints |
| 6 | `server/.../feature/devtools/DevToolsRouting.kt` | Mount `serverLogRouting()` |
| 7 | `server/.../Application.kt` | Install HTTP logging intercept |
| 8 | `server/.../feature/ai/` | Add `ServerLogWriter.write` calls for AI |
| 9 | `website/.../admin/types.ts` | Add `ServerLogDto`, `ServerLogsPageDto`, `ServerLogStatsDto` types |
| 10 | `website/.../admin/client.ts` | Add `adminApi.getLogs()`, `getLogStats()` |
| 11 | `website/.../components/admin/LogViewer.tsx` | NEW — log viewer component |
| 12 | `website/.../admin/dev-tools/page.tsx` | Add "Server Logs" card/tab |

### Phase 4B: Notification Deep-Linking (Server → Shared → Mobile → Web)

| Step | File(s) | Action |
|---|---|---|
| 14 | `server/.../notifications/NotificationsRouting.kt` | Extend `NotificationDto`; update list mapping; add synth deep links |
| 15 | Server feature routes (all) | Audit and add `deepLink` to every `Notify.toUsers` call |
| 16 | `shared/.../parent/domain/model/ParentFeatureModels.kt` | Add `deepLink`, `refType`, `refId` to `ParentNotificationDto` |
| 17 | `shared/.../parent/presentation/NotificationsViewModel.kt` | Add `deepLink` to `NotificationItem`; update mapping |
| 18 | `composeApp/.../navigation/NavGraphV2.kt` | Extend `parseDeepLink`; add `DeepLinkTarget.Messages` |
| 19 | `composeApp/.../screens/notifications/NotificationsScreenV2.kt` | Add `onDeepLink` callback; make rows navigate |
| 20 | `composeApp/.../screens/parent/ParentPortalV2.kt` | Wire `onDeepLink` |
| 21 | `composeApp/.../screens/teacher/TeacherPortalV2.kt` | Wire `onDeepLink` |
| 22 | `composeApp/.../screens/school/SchoolPortalV2.kt` | Wire `onDeepLink` |
| 23 | `composeApp/.../screens/parent/AnnouncementDetailScreen.kt` | NEW — announcement detail |
| 24 | `composeApp/.../screens/parent/FeeDetailScreen.kt` | NEW — fee detail |
| 25 | `composeApp/.../screens/parent/LeaveDetailScreen.kt` | NEW — leave detail |
| 26 | `website/.../admin/types.ts` | Add `deep_link`, `ref_type`, `ref_id` to notification type |
| 27 | `website/.../components/admin/Topbar.tsx` | Make notification items clickable `<Link>` |
| 28 | `website/.../components/admin/ActivityFeed.tsx` | Make activity items clickable |
| 29 | `website/.../lib/admin/utils.ts` | Add `mapDeepLinkToAdminRoute` function |
| 30 | `docs/ECOSYSTEM_MAP.md` | Append new entries |

---

## 10. Edge Cases

| Scenario | Handling |
|---|---|
| Notification has no `deepLink` (null) | Tap only marks read; no navigation. No chevron indicator. |
| Deep link path not recognized | Falls to `DeepLinkTarget.Generic` — shows notification body. |
| Deep link target data not loaded | Screen shows loading state, fetches by `refId`. 404 → "content no longer available". |
| SSE connection drops | Auto-reconnect with exponential backoff (1s→2s→4s→8s→max 30s). "Reconnecting..." indicator. |
| `server_logs` exceeds 100k rows | Daily job prunes oldest. `ServerLogWriter` checks count before insert. |
| AI gateway call fails before response | `ServerLogWriter.write` in `finally` block with `statusCode=0`, `level=ERROR`. |
| Notification refers to deleted entity | Detail screen fetches by ID; 404 → "content no longer available" with back button. |
| Parent has multiple children | Deep link carries `childId`; target screen scopes to that child. |
| Log search returns no results | `EmptyState` with "No logs match your filters". |
| Messages thread doesn't exist | Messages overlay opens to thread list; 404 → falls back to list. |
| Concurrent log writes | PostgreSQL handles concurrent inserts safely; no locking needed. |
| `details_json` exceeds 8000 chars | Truncated with `...[truncated]` suffix; full detail in SLF4J file log. |

---

## 11. Updated ECOSYSTEM_MAP.md Entry (Append Only)

```markdown
### 16. Backend Log Viewer
- **Module**: `feature.logging` (server) + `website/.../components/admin/LogViewer.tsx` (web)
- **Tables**: `server_logs`
- **Events emitted**: `log.write` (implicit via `ServerLogWriter.write`)
- **Key APIs**: `GET /api/v1/admin/dev/logs`, `GET /api/v1/admin/dev/logs/stream` (SSE), `GET /api/v1/admin/dev/logs/stats`
- **Surfaces**: Admin web portal → Dev Tools → Logs tab (super_admin only)
- **Cross-feature**: HTTP middleware logs all requests; AI gateway logs all AI calls; scheduled jobs log execution
- **Hooks**: `server_logs.category` extensible; `details_json` for structured context; AI token usage for cost dashboard

### 17. Universal Notification Deep-Linking
- **Module**: Extends `feature.notifications` (server) + `NotificationsViewModel` (shared) + `NotificationsScreenV2` (compose) + `Topbar.tsx`/`ActivityFeed.tsx` (web)
- **Tables**: No new table — extends `NotificationDto` with existing `deep_link`, `ref_type`, `ref_id` columns
- **Events emitted**: No new events — enhances existing `notification.created` with deep link data in payload
- **Key APIs**: No new endpoints — extends existing `GET /api/v1/notifications` response
- **Surfaces**: All notification surfaces (parent bell, teacher bell, admin bell, activity feed, mobile notification screens)
- **Cross-feature**: All features that call `Notify.toUsers` now pass `deepLink`; `parseDeepLink` handles all categories
- **Deep links**: `/parent/announcements/{id}`, `/parent/fees/{id}`, `/parent/leave?requestId={id}`, `/messages?threadId={id}`, `/{role}/pews?childId={id}`, `/{role}/report-card?...`
- **New screens**: `AnnouncementDetailScreen`, `FeeDetailScreen`, `LeaveDetailScreen` (all reuse existing design system)
```
