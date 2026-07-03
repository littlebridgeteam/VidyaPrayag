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
