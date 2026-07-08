# EnRoll+ Ecosystem Map

> Auto-generated during the Pre-Build Audit for the School Branding Kit integration.
> This is a living document — update it as features are added or integration points change.

## Architecture Overview

**Three Gradle modules:**
- `:server` — Ktor 3.4.3 backend (Postgres prod / SQLite dev, Exposed ORM)
- `:shared` — KMP domain/data layer (models, repos, use cases, ViewModels, Koin DI)
- `:composeApp` — Compose Multiplatform UI (Android, iOS, Web, Desktop)

**Three user roles:** School Admin, Teacher, Parent (multi-tenant, `school_id` from JWT)

**Auth:** JWT bearer tokens. `school_id` resolved from JWT, never from request body. Multi-tenant isolation enforced server-side via `SchoolAccess`.

---

## Feature Verticals

| Feature | Server Route File | Spec | Status |
|---------|------------------|------|--------|
| Auth (OTP + password) | `feature/auth/AuthRouting.kt` | `docs/backend/03-auth.md` | ✅ Shipping |
| Onboarding | `feature/onboarding/OnboardingRouting.kt` | — | ✅ Shipping |
| School Dashboard | `feature/school/SchoolDashboardRouting.kt` | — | ✅ Shipping |
| Admin Dashboard | `feature/school/AdminDashboardRouting.kt` | — | ✅ Shipping |
| School Intelligence | `feature/school/SchoolIntelligenceRouting.kt` | — | ✅ Shipping |
| School Analytics | `feature/school/SchoolAnalyticsRouting.kt` | — | ✅ Shipping |
| School Profile | `feature/school/SchoolProfileRouting.kt` | — | ✅ Shipping |
| School Students | `feature/school/SchoolStudentsRouting.kt` | — | ✅ Shipping |
| School Classes | `feature/school/SchoolClassesRouting.kt` | — | ✅ Shipping |
| School Timetable | `feature/school/SchoolTimetableRouting.kt` | — | ✅ Shipping |
| Teacher Assignments | `feature/school/TeacherAssignmentRouting.kt` | — | ✅ Shipping |
| Teacher Provisioning | `feature/school/TeacherProvisioningRouting.kt` | — | ✅ Shipping |
| Non-Teaching Staff | `feature/school/NonTeachingStaffRouting.kt` | — | ✅ Shipping |
| Lesson Plans | `feature/school/SchoolLessonPlanRouting.kt` | `LESSON_PLANNING_SPEC.md` | ✅ Shipping |
| Parent Dashboard | `feature/parent/ParentDashboardRouting.kt` | — | ✅ Shipping |
| Parent Link | `feature/parent/ParentLinkRouting.kt` | — | ✅ Shipping |
| Parent Academics | `feature/parent/ParentAcademicsRouting.kt` | — | ✅ Shipping |
| Parent Fees | `feature/parent/ParentFeesRouting.kt` | — | ✅ Shipping |
| Parent Leave | `feature/parent/ParentLeaveRouting.kt` | — | ✅ Shipping |
| Teacher Portal | `feature/teacher/TeacherRouting.kt` | — | ✅ Shipping |
| Teacher Day/Week | `feature/teacher/TeacherDayRouting.kt` | — | ✅ Shipping |
| Teacher Attendance | `feature/teacher/TeacherAttendanceRouting.kt` | — | ✅ Shipping |
| Teacher Gradebook | `feature/teacher/TeacherGradebookRouting.kt` | — | ✅ Shipping |
| Teacher Syllabus | `feature/teacher/TeacherSyllabusRouting.kt` | — | ✅ Shipping |
| Teacher Homework | `feature/teacher/TeacherHomeworkRouting.kt` | — | ✅ Shipping |
| Teacher Classes | `feature/teacher/TeacherClassesRouting.kt` | — | ✅ Shipping |
| Teacher Student | `feature/teacher/TeacherStudentRouting.kt` | — | ✅ Shipping |
| Teacher Leave | `feature/teacher/TeacherLeaveRouting.kt` | — | ✅ Shipping |
| Teacher Messages | `feature/teacher/TeacherMessagesRouting.kt` | — | ✅ Shipping |
| Teacher Lesson Plans | `feature/teacher/TeacherLessonPlanRouting.kt` | — | ✅ Shipping |
| Messages | `feature/school/MessagesRouting.kt` | `MESSAGING_SYSTEM_SPEC.md` | ✅ Shipping |
| Message Scheduling | `feature/scheduling/ScheduledMessageRouting.kt` | `MESSAGE_SCHEDULING_PLAN.md` | ✅ Shipping |
| Notifications | `feature/notifications/NotificationsRouting.kt` | `NOTIFICATION_SYSTEM_SPEC.md` | ✅ Shipping |
| Push (FCM) | `feature/notification/NotificationRouting.kt` | — | ✅ Shipping |
| Announcements | `feature/announcements/AnnouncementRouting.kt` | — | ✅ Shipping |
| Admissions | `feature/admissions/AdmissionRouting.kt` | — | ✅ Shipping |
| Media Upload | `feature/media/MediaRouting.kt` | — | ✅ Shipping |
| Academic Calendar | `feature/calendar/AcademicCalendarRouting.kt` | — | ✅ Shipping |
| Academic Years | `feature/calendar/AcademicYearRouting.kt` | — | ✅ Shipping |
| Event Registration | `feature/event/EventRegistrationRouting.kt` | `EVENT_REGISTRATION_PLAN.md` | ✅ Shipping |
| PTM | `feature/school/PtmRouting.kt` | — | ✅ Shipping |
| Leave Requests | `feature/school/LeaveRequestsRouting.kt` | — | ✅ Shipping |
| Results | `feature/school/ResultsRouting.kt` | — | ✅ Shipping |
| Health Records | `feature/health/HealthRouting.kt` | `HEALTH_RECORDS_SPEC.md` | ✅ Shipping |
| ID Cards | `feature/idcard/IdCardRouting.kt` | `ID_CARD_GENERATION_SPEC.md` | ✅ Shipping |
| Library | `feature/library/LibraryRouting.kt` | `LIBRARY_MANAGEMENT_SPEC.md` | ✅ Shipping |
| Transport | `feature/transport/TransportRouting.kt` | `TRANSPORT_TRACKING_SPEC.md` | ✅ Shipping |
| Scholarships | `feature/scholarship/ScholarshipRouting.kt` | `SCHOLARSHIP_WORKFLOW_SPEC.md` | ✅ Shipping |
| Alumni | `feature/alumni/AlumniRouting.kt` | `ALUMNI_MANAGEMENT_SPEC.md` | ✅ Shipping |
| Parent Pulse | `feature/pulse/PulseRouting.kt` | `PARENT_PULSE_SPEC.md` | ✅ Shipping |
| AI Gateway | `feature/ai/AiRouting.kt` | `AI_INFRASTRUCTURE_SPEC.md` | ✅ Shipping |
| PEWS | `feature/pews/PewsRouting.kt` | `PEWS_2.0_AGENTIC_REDESIGN.md` | ✅ Shipping |
| AI Report Card | `feature/reportcard/ReportCardRouting.kt` | `AI_REPORT_CARD_2.0_AGENTIC_REDESIGN.md` | ✅ Shipping |
| AI Tutor | `feature/tutor/TutorRouting.kt` | `AI_TUTOR_2.0_AGENTIC_REDESIGN.md` | ✅ Shipping |
| School Branding | `feature/branding/BrandingRouting.kt` | `SCHOOL_BRANDING_KIT_SPEC.md` | ✅ Shipping (M-1/M-2/M-3) |
| Multi-Branch | `feature/organization/OrganizationRouting.kt` | `MULTI_BRANCH_SPEC.md` | ✅ Backend done (this PR) |
| School Day Config | `feature/school/SchoolDayConfigRouting.kt` | — | ✅ Shipping |
| Timetable Import | `feature/school/TimetableImportRouting.kt` | — | ✅ Shipping |
| Timetable Change Requests | `feature/school/TimetableChangeRequestRouting.kt` | — | ✅ Shipping |
| User Details | `feature/user/UserDetailsRouting.kt` | — | ✅ Shipping |
| User Profile | `feature/user/UserProfileRouting.kt` | — | ✅ Shipping |
| Dev Tools | `feature/devtools/DevToolsRouting.kt` | — | ✅ Shipping |
| Health Check | `feature/healthcheck/HealthCheckRouting.kt` | — | ✅ Shipping |
| Gateway (SMS) | `feature/gateway/GatewayRouting.kt` | — | ✅ Shipping |
| Content/Landing | `feature/content/LandingRouting.kt` | — | ✅ Shipping |
| Config/App Status | `feature/config/AppStatusRouting.kt` | — | ✅ Shipping |

---

## Cross-Cutting Concerns

### Authentication & Authorization
- **JWT:** `core/JwtConfig.kt` — HMAC256, role-based expiry (admin 30min, others 24h)
- **School Access:** `core/SchoolAccess.kt` — `requireSchoolContext()`, `requireSchoolAdmin()`, `requireSchoolOrTeacherContext()`, `requirePlatformAdmin()`, `requireOrgAdminContext()`
- **Teacher Access:** `core/TeacherAccess.kt` — assignment-scoped guard
- **Security Module:** `core/SecurityModule.kt` — Ktor JWT plugin wiring

### Database
- **Tables:** `db/Tables.kt` — all Exposed table definitions (80+ tables)
- **Database Factory:** `db/DatabaseFactory.kt` — HikariCP pool, Postgres/SQLite, schema validation
- **Migrations:** `docs/db/` — SQL migration files, run manually in prod

### Notification Spine
- **Notify:** `feature/notifications/Notify.kt` — single write-path for cross-user notifications
- **Push Bridge:** FCM via `NotificationService` — fire-and-forget
- **Preferences:** per-category opt-out, rate limiting (50/day/user, 10/hr/category)

### Response Envelope
- **ApiResponse:** `core/ApiResponse.kt` — `{ success, message, data }`
- **Extensions:** `core/ResponseExtensions.kt` — `call.ok()`, `call.fail()`, `call.created()`

---

## Integration Points (Blast Radius)

### School Branding Kit
- **DB:** `school_branding` table (per-school logo, colors, subdomain, splash, login background, app icon, favicon)
- **Multi-campus:** `school_branding.organization_id` — org-level branding inherited by branches unless `is_customized=true`
- **Server:** `feature/branding/BrandingService.kt` + `BrandingRouting.kt` + `BrandingHooks.kt`
  - `POST /api/v1/school/branding/assets` — multipart asset upload (logo, favicon, app icon, splash, login background)
  - `DELETE /api/v1/school/branding/assets?field=xxx` — asset removal
  - `PATCH /api/v1/school/branding` — color updates
  - `POST /api/v1/school/branding/reset` — reset to defaults
  - `GET /api/v1/branding/{schoolId}` — public branding lookup
  - `GET /api/v1/branding/subdomain/{subdomain}` — subdomain resolution
  - Notify: `branding.updated` event emitted to co-admins on changes
- **Client (shared):** `feature/branding/` — DTOs, repo, ViewModel, theme manager, branding cache
  - `BrandingApi.uploadAsset()` / `deleteAsset()` — multipart asset endpoints
  - `BrandingThemeManager.loadCached()` — pre-auth branding from DataStore/LocalStorage
  - `PreferenceRepository.getCachedBranding()` / `setCachedBranding()` — branding JSON persistence
- **Client (UI):** `BrandingSettingsScreen` (asset upload UI), `BrandingThemeManager`, `BrandingColorMapper`
  - `AuthScaffoldV2` — branding-aware (school logo, primary color hero, login background)
  - `SplashScreenV2` — branding-aware (school logo, primary color, school name)
- **Branding Hooks:** `BrandingHooks.kt` — stubs for report card header + email template branding
- **Connected to:**
  - `schools` table (logoUrl, brandColor columns — legacy branding fields)
  - Onboarding wizard (BRANDING step writes to schools + school_branding)
  - VTheme system (dynamic theming from branding colors)
  - Public API (no auth — `/api/v1/branding/{schoolId}`)
  - Subdomain routing (`/api/v1/branding/subdomain/{subdomain}`)
  - Supabase Storage (asset upload/delete via `SupabaseStorage`)
  - Notify (branding.updated event to co-admins)
  - Report card header branding (`BrandingHooks.getHeader()`)
  - Email template branding (`BrandingHooks.emailHeaderHtml()`)
  - ID Card generation (reads branding for logo + colors)
  - Multi-Branch (org-level branding inheritance via `organization_id`)

### Multi-Branch / School Chain
- **DB:** `school_organizations`, `student_transfers` tables; `schools.organization_id`, `schools.branch_name`, `app_users.organization_id`, `app_users.org_admin_role`
- **Server:** `feature/organization/OrganizationService.kt`, `StudentTransferService.kt`, `OrganizationRouting.kt`
- **Auth:** `core/SchoolAccess.kt` — `OrgContext`, `requireOrgAdminContext()`, `resolveBranchSchoolIds()`
- **JWT:** `core/JwtConfig.kt` — `issueTokenWithOrg()` with org claims
- **Connected to:**
  - `schools` table (organization_id link)
  - `app_users` table (org admin promotion)
  - `students` table (cross-branch transfer migration)
  - `enrollments` table (status → transferred on migration)
  - `children` table (school_id update on transfer)
  - `faculty` table (teacher count per branch)
  - `fee_records` table (aggregate fee stats)
  - `attendance_records` table (aggregate attendance rate)
  - `Notify` (transfer approval notifications)
  - School Branding Kit (future: org-level branding propagation to branches)

---

## Key Conventions

- **API base path:** `/api/v1/` (school/teacher/parent), `/api/admin/` (platform admin)
- **DTO conventions:** `@Serializable`, `@SerialName` snake_case
- **Error handling:** `core/ErrorHandling.kt` + StatusPages, `DEBUG_ERRORS=true` for dev
- **Multi-tenancy:** every query scoped by `school_id` from JWT, never from request body
- **Schema migrations:** manual SQL in `docs/db/`, run via Supabase SQL Editor
- **DI (client):** Koin in `shared/.../di/Koin.kt` — repos as `single`, use cases as `factory`, VMs as `factory`
- **UI design system:** `VTheme` (colors, type, dimens) — never hardcode colors
- **Three-state screens:** loading skeleton + error + empty via `VStateHost`

---

> Living document. Every feature build appends an entry here so the NEXT feature
> inherits awareness of what exists, what events it emits, and what surfaces it
> touches. **Append only — never rewrite.**

---

## Feature Index

### 1. Notification Spine (RA-41/42/46/50)
- **Module**: `feature.notifications` (server) + `feature.notification` (shared client)
- **Tables**: `notifications`, `notification_preferences`, `device_tokens`
- **Events emitted**: `notification.created` (implicit via `Notify.toUsers`)
- **Key APIs**: `GET /api/v1/notifications`, `GET /api/v1/notifications/summary`, `PATCH /api/v1/notifications/{id}/read`, `POST /api/v1/notifications/read-all`
- **Surfaces**: Parent bell + inbox, Teacher bell + overlay, Admin web bell + dropdown, Admin dashboard activity feed
- **Deep link support**: `deep_link` column on `notifications` table; `refType` + `refId` for entity linking
- **Cross-feature**: Used by attendance, marks, homework, announcements, leave, fees, link-child, PEWS, report-card, alumni
- **Gaps**: NotificationDto does NOT return `deep_link`, `ref_type`, `ref_id` to clients — clients cannot deep-link from in-app notification taps. Activity feed items are not clickable.

### 2. DevTools (Super Admin)
- **Module**: `feature.devtools` (server) + `website/src/app/admin/dev-tools` (web)
- **Key APIs**: `POST /api/v1/admin/dev/send-notification`, `POST /api/v1/admin/dev/trigger-pulse`, `POST /api/v1/admin/dev/trigger-pews`, `GET/PUT /api/v1/admin/dev/otp-providers`
- **Surfaces**: Admin web portal → Dev Tools page (super_admin only)
- **Cross-feature**: Send-notification uses `Notify.toUser` with `deep_link`; AI Token Monitor (`/api/v1/admin/ai/*`)
- **Gaps**: No server log viewing endpoint or UI. Backend uses `println` + SLF4J but no structured log table or streaming API.

### 3. PEWS (Predictive Early Warning System)
- **Module**: `feature.pews` (server + shared)
- **Tables**: `pews_snapshots`, `pews_interventions`, `pews_config`
- **Events**: PEWS audit logs via `AuditLogger` → writes to `notifications` table (category=`pews_audit`)
- **Surfaces**: Admin web (`/admin/early-warning`), Admin app (SchoolPortalV2 → PewsCohort/PewsStudentDetail overlays), Parent app (ParentPewsScreenV2)
- **Cross-feature**: Uses `Notify.toUsers` for parent messaging; interventions tracked with `refType`/`refId`

### 4. AI Report Card 2.0
- **Module**: `feature.reportcard` (server) + admin web + teacher app
- **Tables**: `report_card_drafts`, `report_card_term_config`
- **Surfaces**: Admin web (`/admin/report-card`), Teacher app (ReportReview/ReportDraftEditor overlays)
- **Deep links**: `/teacher/report-review?className=8&section=A&term=Term 1` — parsed in `NavGraphV2.parseDeepLink`

### 5. AI Tutor 2.0
- **Module**: `feature.tutor` (server) + teacher/admin app
- **Tables**: `tutor_heatmap`, `tutor_efficacy`
- **Surfaces**: Admin web (`/admin/tutor`), Teacher app (Heatmap overlay)
- **Deep links**: `/tutor` — role-aware routing in `parseDeepLink`

### 6. Announcements
- **Module**: `feature.school` (server) — `SchoolAnnouncementsRouting`
- **Tables**: `announcements`
- **Surfaces**: Admin web (`/admin/announcements`), Parent app (announcements feed), Admin app (SchoolCommsScreenV2)
- **Cross-feature**: Triggers `Notify.toUsers` for broadcast; parent synth bridge in notification list (`ann_*` ids)
- **Gaps**: Announcement notifications in parent app are not clickable to a detail view

### 7. Messaging System
- **Module**: `feature.admin.presentation.MessagesViewModel` (shared) + messaging routes (server)
- **Tables**: `message_threads`, `message_messages`
- **Surfaces**: Admin app (Messages overlay), Teacher app (Messages overlay), Parent app (Messages overlay)
- **Gaps**: No deep link from notification to specific chat thread

### 8. Leave Requests
- **Module**: `feature.school` (server) — leave request routes
- **Tables**: `leave_requests`
- **Surfaces**: Admin web (`/admin/leave`), Admin app (LeaveRequests overlay), Teacher/Parent app (leave apply/status)
- **Cross-feature**: Triggers `Notify.toUsers` on apply/decide
- **Gaps**: Leave notification does not deep-link to the specific leave request

### 9. Fees
- **Module**: `feature.school` (server) — fee routes
- **Tables**: `fee_records`
- **Surfaces**: Admin web (`/admin/fees`), Parent app (fees screen)
- **Cross-feature**: Parent synth bridge in notification list (`fee_*` ids); triggers `Notify.toUsers` on status change
- **Gaps**: Fee notifications not clickable to fee detail

### 10. Alumni Management
- **Module**: `feature.alumni` (server + shared)
- **Tables**: `alumni`, `alumni_campaigns`, `alumni_donations`, `alumni_mentorships`, `alumni_mentorship_requests`
- **Surfaces**: Admin web (`/admin/alumni`), Admin app (Alumni/AlumniDetail/AlumniCampaign overlays)
- **Deep links**: `/alumni/directory/{id}` — parsed in `parseDeepLink`

### 11. Offline Mode & Sync
- **Module**: `shared/feature/offline` + `shared/feature/sync`
- **Tables**: Room entities (`SchoolEntity`, `OutboxOperationEntity`, `AnnouncementEntity`, `TeacherDayCacheEntity`)
- **Surfaces**: All mobile portals (offline read cache + write outbox)
- **Cross-feature**: SyncEngine drains outbox; telemetry via `oldestPendingAgeMs`

### 12. Academic Calendar
- **Module**: `feature.school` (server) — calendar routes
- **Tables**: `academic_calendar`, `holidays`, `academic_years`
- **Surfaces**: Admin web calendar, Admin app (AcademicCalendarPlatform overlay)
- **Deep links**: `/calendar` — generic routing

### 13. Library
- **Module**: `feature.library` (server)
- **Tables**: `library_books`, `library_loans`, `library_audit_log`
- **Surfaces**: Admin/Teacher app (Library overlay)
- **Deep links**: `/library` — role-aware routing

### 14. Transport
- **Module**: transport routes (server)
- **Surfaces**: Admin app (TransportManagement overlay), Teacher app (TransportAttendance overlay), Parent app (transport overlay)
- **Deep links**: `/transport` — role-aware routing

### 15. School Day Configuration & Timetable
- **Module**: `feature.school` (server) — day-config + timetable routes
- **Tables**: `school_day_configs`, `teacher_periods`
- **Surfaces**: Admin web (`/admin/academics`), Admin app (ClassesSubjects/ClassDetail overlays)

---

## Event Registry

| Event | Emitted By | Mechanism | Subscribers |
|---|---|---|---|
| `notification.created` | `Notify.toUsers` / `Notify.toUser` | DB insert + FCM push | Bell count, activity feed |
| `pews.audit` | `AuditLogger.log` | DB insert to `notifications` (category=`pews_audit`) | Admin oversight |
| `announcement.posted` | Announcement routing | `Notify.toUsers` call | Parent/teacher notification |
| `attendance.marked` | Attendance routing | `Notify.toUsers` call | Parent notification |
| `marks.published` | Marks routing | `Notify.toUsers` call | Parent notification |
| `homework.assigned` | Homework routing | `Notify.toUsers` call | Parent notification |
| `leave.applied` | Leave routing | `Notify.toUsers` call | Admin/teacher notification |
| `leave.decided` | Leave routing | `Notify.toUsers` call | Applicant notification |
| `fee.status_changed` | Fee routing | `Notify.toUsers` call | Parent notification |
| `link_child.decided` | Link-child routing | `Notify.toUsers` call | Parent notification |

---

## Surface Registry

| Surface | Platform | Role | Notifiable |
|---|---|---|---|
| Parent Portal (app) | Compose MP | Parent | Yes — bell + inbox |
| Teacher Portal (app) | Compose MP | Teacher | Yes — bell + overlay |
| School Portal (app) | Compose MP | SchoolAdmin/SuperAdmin | Yes — bell + overlay |
| Admin Web Portal | Next.js | SchoolAdmin/SuperAdmin | Yes — bell dropdown |
| Activity Feed (web) | Next.js | SchoolAdmin | Merged server-side (notifications + leave + announcements) |

---

## Deep Link Registry

| Path Pattern | Target | Status |
|---|---|---|
| `/parent/{tab}/{overlay}` | ParentPortalV2 tab + overlay | Implemented |
| `/teacher/{screen}?{params}` | TeacherPortalV2 overlay | Implemented (transport, report-review, tutor, events) |
| `/school/{screen}?{params}` | SchoolPortalV2 overlay | Implemented (basic) |
| `/alumni/{screen}/{id}` | SchoolPortalV2 Alumni overlay | Implemented |
| `/announcements` | Generic | Stub — no detail screen |
| `/calendar` | Generic | Stub |
| `/transport` | Role-aware | Implemented |
| `/report-card` | Role-aware | Implemented |
| `/tutor` | Role-aware | Implemented |
| `/library` | Role-aware | Implemented |
| `/events` | Role-aware | Implemented |

---

## Feature Index (Appended)

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
