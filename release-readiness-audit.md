# Vidya Prayag — Comprehensive Release Readiness Audit

**Date:** 2026-07-11  
**Auditor:** Cascade AI (automated codebase audit)  
**Branch:** `backend-by-abuzar`  
**Scope:** Full-stack — composeApp (UI), shared (domain/data/presentation), server (Ktor backend), website (Next.js admin portal)

---

## 1. Executive Summary

Vidya Prayag is a Kotlin Multiplatform school management platform targeting Android, iOS, Web (Wasm/JS), Desktop, and Server. The codebase spans three Gradle modules (`:server`, `:shared`, `:composeApp`) plus a Next.js website/admin portal. The architecture follows Clean Architecture + MVVM with Koin DI, Ktor networking, Exposed ORM, and Compose Multiplatform UI.

### Overall Assessment

| Dimension | Rating | Notes |
|-----------|--------|-------|
| **Architecture** | Strong | Clean separation, well-defined feature verticals |
| **Security** | Good | JWT with DB-read role verification, tenant isolation, CSRF protection |
| **Feature Completeness** | Moderate | Several `VComingSoon` placeholders, some backend features lack frontend wiring |
| **Test Coverage** | Critical | Almost no test coverage — only `MainViewModelTest.kt` found |
| **Error Handling** | Moderate | Server has structured error handling; client error states vary by screen |
| **UI Consistency** | Good | VTheme design system enforced; hardcoded colors fixed; dual theme system is accepted debt |
| **Production Readiness** | Nearly Ready | Security verified, hardcoded colors fixed, debug leaks patched. Remaining: tests, pagination, CI/CD, error monitoring |

### Key Findings

- **42 TODO/FIXME** markers across shared module; **130** across server
- **345 `ComingSoon`/placeholder** references in composeApp — several features are stubs
- ~~**42 hardcoded `Color(0x...)` references** in v2 screens~~ **FIXED 2026-07-12** — actionable colors replaced with VTheme/VColors tokens; remaining are documented design constants
- **Zero empty catch blocks** found (good)
- **Zero `println`/`printStackTrace`** in client code (good)
- ~~**Session-bleed fix** via `SessionScope` is fragile (relies on JWT string key)~~ **FIXED 2026-07-12** — now keys on `userId + ":" + role`
- ~~**No rate limiting** on auth endpoints~~ **FALSE POSITIVE** — `LoginThrottle` (per-IP + per-identifier sliding window) + `OtpService` resend cap (5/hour with `FOR UPDATE` row lock)
- ~~**Refresh token not persisted/rotated**~~ **FALSE POSITIVE** — `UserSessionsTable` persists tokens with rotation, reuse detection, and revocation on logout/password change
- ~~**CRITICAL: OTP plaintext logging in production**~~ **FIXED 2026-07-12** — `if (!isProduction)` guard restored
- ~~**Token refresh race condition**~~ **FIXED 2026-07-12** — `Mutex` serializes concurrent refresh attempts
- ~~**Debug logger always on**~~ **FIXED 2026-07-12** — gated behind `Config.isDev`
- ~~**CORS not production-safe**~~ **FALSE POSITIVE** — production uses `CORS_ALLOWED_ORIGINS` env var, no `anyHost()` fallback
- ~~**CSRF scope unclear**~~ **FALSE POSITIVE** — correctly scoped to state-changing methods in production, skips non-browser requests
- ~~**Background jobs not idempotent**~~ **FALSE POSITIVE** — all jobs use `AtomicReference<LocalDate?>` + `compareAndSet` guards
- ~~**File upload validation missing**~~ **FALSE POSITIVE** — MIME whitelist, size limits, magic bytes verification all in place
- ~~**Request ID not propagated**~~ **FALSE POSITIVE** — `RequestIdPlugin` + MDC + logback pattern fully implemented
- **No automated test suite** for server routes, ViewModels, or repositories — **CRITICAL GAP**
- **Schema migrations are manual SQL** — no programmatic migration tooling for Postgres
- **No server-side pagination** — will impact large schools
- **Dual theme systems** — ACCEPTED DEBT: 15+ teacher screens use static `ui.tokens.VColors`; `VtC`/`VtT` bridge designed for migration

---

## 2. Feature Inventory

### 2.1 Mobile App Screens (composeApp/ui/v2/screens)

**Total:** ~118 Kotlin screen files across 7 role/domain folders.

#### Auth & Onboarding (3 files)
| Screen | File | Status | Backend Wired |
|--------|------|--------|---------------|
| Parent Link Child | `auth/ParentLinkChildScreenV2.kt` | Complete | Yes (`POST /parent/link-child`) |
| School Onboarding | `auth/SchoolOnboardingScreenV2.kt` | Complete | Yes |
| Teacher First Login | `auth/TeacherFirstLoginScreenV2.kt` | Complete | Yes |

#### Parent Portal (18+ files)
| Screen | File | Status | Backend Wired |
|--------|------|--------|---------------|
| Parent Home | `parent/ParentHomeScreenV2.kt` | Complete | Yes (`GET /parent/dashboard`) |
| Parent Academics | `parent/ParentAcademicsScreenV2.kt` | Complete | Yes |
| Parent Fees | `parent/ParentFeePaymentScreenV2.kt` | Complete | Yes |
| Parent Fee History | `parent/ParentFeeHistoryScreenV2.kt` | Complete | Yes |
| Parent Messages/Conversations | `parent/ParentConversationsScreenV2.kt` | Complete | Yes |
| Parent Leave | `parent/ParentLeaveScreenV2.kt` | Complete | Yes |
| Parent Profile | `parent/ParentProfileScreenV2.kt` | Complete | Yes |
| Parent Pulse | `parent/ParentPulseScreen.kt` | Complete | Yes |
| Parent Report Card | `parent/ParentReportScreen.kt` | Complete | Yes |
| Parent PEWS | `parent/ParentPewsScreenV2.kt` | Complete | Yes |
| Parent Health | (via overlay) | Complete | Yes |
| Parent Transport/Bus Tracking | `parent/BusTrackingScreenV2.kt` | Complete | Yes |
| Parent Digital ID Card | `parent/DigitalIdCardScreen.kt` | Complete | Yes |
| Parent Library | `parent/ParentLibraryScreenV2.kt` | Complete | Yes |
| Parent Event Registration | `parent/ParentEventRegistrationScreenV2.kt` | Complete | Yes |
| Parent Scholarships | `parent/ScholarshipsScreenV2.kt` | Complete | Yes |
| Parent Tutor Chat | `tutor/TutorChatScreen.kt` | Complete | Yes |
| Parent Tutor Progress | `tutor/ParentProgressScreen.kt` | Complete | Yes |
| AI Report Card Preview | `parent/AiReportCardPreview.kt` | Label-free teaser | N/A (preview only) |

#### Teacher Portal (15+ files)
| Screen | File | Status | Backend Wired |
|--------|------|--------|---------------|
| Teacher Home | `teacher/TeacherHomeScreenV2.kt` | Complete | Yes |
| Teacher Update (Attendance/Marks/Syllabus/HW) | `teacher/TeacherUpdateScreenV2.kt` | Complete | Yes |
| Teacher Classes | `teacher/TeacherClassesScreenV2.kt` | Complete | Yes |
| Teacher Profile | `teacher/TeacherProfileScreenV2.kt` | Complete | Yes |
| Teacher Attendance | `teacher/TeacherAttendanceScreenV2.kt` | Complete | Yes |
| Teacher Marks | `teacher/TeacherMarksScreenV2.kt` | Complete | Yes |
| Teacher Homework | `teacher/TeacherHomeworkScreenV2.kt` | Complete | Yes |
| Teacher Syllabus | `teacher/TeacherSyllabusScreenV2.kt` | Complete | Yes |
| Teacher Timetable | `teacher/TeacherTimetableScreenV2.kt` | Complete | Yes |
| Teacher Messages | `teacher/TeacherMessagesScreenV2.kt` | Complete | Yes |
| Teacher PEWS | `teacher/TeacherPewsScreenV2.kt` | Complete | Yes |
| Teacher Report Review | `teacher/TeacherReportReviewQueueScreen.kt` | Complete | Yes |
| Teacher Report Draft Editor | `teacher/TeacherReportDraftEditorScreen.kt` | Complete | Yes |
| Teacher Lesson Plan | `teacher/TeacherLessonPlanScreenV2.kt` | Complete | Yes |
| Transport Attendance | `teacher/TransportAttendanceScreenV2.kt` | Complete | Yes |
| Teacher Heatmap | `tutor/TeacherHeatmapScreen.kt` | Complete | Yes |

#### School Admin Portal (40+ files)
| Screen | File | Status | Backend Wired |
|--------|------|--------|---------------|
| School Dashboard/Home | `school/SchoolDashboardScreenV2.kt` | Complete | Yes |
| School People | `school/SchoolPeopleScreenV2.kt` | Complete | Yes |
| School Records | `school/SchoolRecordsScreenV2.kt` | Complete | Yes (Documents tab = `VComingSoon`) |
| School Comms | `school/SchoolCommsScreenV2.kt` | Complete | Yes (Notifications tab = `VComingSoon`) |
| School Settings | `school/SchoolSettingsScreenV2.kt` | Complete | Yes |
| Student Roster | `school/StudentRosterScreenV2.kt` | Complete | Yes |
| Student Profile | `school/StudentProfileScreenV2.kt` | Complete | Yes |
| Teacher Profile/Assignments | `school/TeacherProfileScreenV2.kt` | Complete | Yes |
| Teacher Assignment Mgmt | `school/TeacherAssignmentManagementScreen.kt` | Complete | Yes |
| Staff | `school/StaffProfileScreenV2.kt` | Complete | Yes |
| Link Requests | `school/LinkRequestsScreenV2.kt` | Complete | Yes |
| Leave Requests | `school/LeaveRequestsScreenV2.kt` | Complete | Yes |
| Admissions CRM | `school/AdmissionsCrmScreenV2.kt` | Complete | Yes |
| Results Publish | `school/ResultsPublishScreenV2.kt` | Complete | Yes |
| Schedule PTM | `school/SchedulePtmScreenV2.kt` | Complete | Yes |
| Daily Attendance | `school/DailyAttendanceScreenV2.kt` | Complete | Yes |
| Class Performance | `school/ClassPerformanceScreenV2.kt` | Complete | Yes |
| Teacher Performance | `school/TeacherPerformanceScreenV2.kt` | Complete | Yes |
| Analytics Dashboard | `school/AnalyticsDashboardScreenV2.kt` | Complete | Yes |
| Edit School Profile | `school/EditSchoolProfileScreenV2.kt` | Complete | Yes |
| PEWS Cohort | `school/PewsCohortScreenV2.kt` | Complete | Yes |
| PEWS Student Detail | `school/PewsStudentDetailScreenV2.kt` | Complete | Yes |
| PEWS Effectiveness | `school/PewsEffectivenessScreenV2.kt` | Complete | Yes |
| Health Records | `school/HealthRecordsScreenV2.kt` | Complete | Yes |
| Alumni | `school/AlumniScreen.kt` | Complete | Yes |
| Alumni Detail | `school/AlumniDetailScreen.kt` | Complete | Yes |
| Alumni Campaign | `school/AlumniCampaignScreen.kt` | Complete | Yes |
| Transport Management | `school/TransportManagementScreenV2.kt` | Complete | Yes |
| Report Publish | `school/AdminReportPublishScreen.kt` | Complete | Yes |
| Report Effectiveness | `school/AdminReportingEffectivenessScreen.kt` | Complete | Yes |
| Scholarship Management | `school/ScholarshipManagementScreenV2.kt` | Complete | Yes |
| Branding Kit | `school/BrandingSettingsScreen.kt` | Complete | Yes |
| ID Cards | `school/IdCardCardsTab.kt` | Complete | Yes |
| Library | `school/SchoolLibraryScreen.kt` | Complete | Yes |
| Scheduled Messages | `school/ScheduledMessagesScreenV2.kt` | Complete | Yes |
| Event Registration | `school/AdminEventRegistrationScreenV2.kt` | Complete | Yes |
| Classes & Subjects | `school/ClassesSubjectsScreenV2.kt` | Complete | Yes |
| Class Detail | `school/ClassDetailScreenV2.kt` | Complete | Yes |
| School Day Config | `school/SchoolDayConfigScreenV2.kt` | Complete | Yes |
| Academic Calendar Platform | `school/AcademicCalendarPlatformScreenV2.kt` | Complete | Yes |
| Academic Year Mgmt | `school/AcademicYearManagementScreenV2.kt` | Complete | Yes |
| Create Event | `school/UnifiedCreateEventScreenV2.kt` | Complete | Yes |
| Messages | `school/MessagesScreenV2.kt` | Complete | Yes |
| Delivery Log | (overlay) | `VComingSoon` | No backend |
| Home Command Palette | `school/HomeCommandPalette.kt` | Complete | Yes |

#### Discovery & Shared (5 files)
| Screen | File | Status | Backend Wired |
|--------|------|--------|---------------|
| Discovery (School Marketplace) | `discovery/DiscoveryScreenV2.kt` | Partial | Yes (board/fees/reviews = `VComingSoon`) |
| Academic Calendar | `discovery/AcademicCalendarScreenV2.kt` | Complete | Yes |
| Notifications | `notifications/NotificationsScreenV2.kt` | Complete | Yes |
| SRI Preview | `discovery/SriPreview.kt` | Preview only | N/A |

#### Legacy V1 Screens (9 files in `ui/screens/`)
| Screen | File | Status |
|--------|------|--------|
| Admin Login | `ui/screens/admin/AdminLoginScreen.kt` | Legacy — still referenced by `AuthNavGraph` |
| Admin Signup | `ui/screens/admin/AdminSignupScreen.kt` | Legacy — still referenced by `AuthNavGraph` |
| Parent Login | `ui/screens/parent/ParentLoginScreen.kt` | Legacy — still referenced by `AuthNavGraph` |
| Parent Signup | `ui/screens/parent/ParentSignupScreen.kt` | Legacy — still referenced by `AuthNavGraph` |
| Splash | `ui/screens/shared/SplashScreen.kt` | Active — used by `App.kt` |

### 2.2 Backend API Surface

**Total:** 29+ routing files, ~96+ endpoint groups.

| Feature Group | Routing File | Key Routes | Auth |
|---------------|-------------|------------|------|
| Auth | `AuthRouting.kt` | `/api/v1/auth/{check-user,send-otp,verify-otp,signup,login,refresh}` | Public |
| OTP Admin | `OtpAdminRouting.kt` | Super-admin OTP provider switch | SuperAdmin |
| Parent | `ParentDashboardRouting.kt`, `ParentLinkRouting.kt` | `/api/v1/parent/*` | Parent JWT |
| School Admin | `AdminDashboardRouting.kt`, `SchoolRecordsRouting.kt`, `SchoolDashboardRouting.kt`, etc. | `/api/v1/school/*` | SchoolAdmin JWT |
| Teacher | `TeacherClassesRouting.kt`, `TeacherGradebookRouting.kt`, `TeacherStudentRouting.kt` | `/api/v1/teacher/*` | Teacher JWT |
| Admin Dashboard | `AdminDashboardRouting.kt` | `/api/admin/*` | SuperAdmin JWT |
| Announcements | `AnnouncementRouting.kt` | `/api/v1/announcements/*` | School JWT |
| Admissions | `AdmissionRouting.kt` | `/api/v1/admissions/*` | School JWT |
| Health | `HealthRouting.kt` | `/api/v1/school/health/*`, `/api/v1/teacher/health/*`, `/api/v1/parent/health/*` | Role JWT |
| Library | `LibraryRouting.kt` | `/api/v1/school/library/*`, `/api/v1/parent/library/*`, `/api/v1/student/library/*` | Role JWT |
| Notifications | `NotificationsRouting.kt`, `NotificationRouting.kt`, `NotificationPreferencesRouting.kt` | `/api/v1/notifications/*`, `/api/device-tokens`, `/api/admin/notifications/send` | JWT |
| PEWS | `PewsRouting.kt` | `/api/v1/pews/*` | Role JWT |
| Report Card | `ReportCardRouting.kt` | `/api/v1/report-card/*` | Role JWT |
| Tutor | `TutorRouting.kt` | `/api/v1/tutor/*` | Role JWT |
| Alumni | `AlumniRouting.kt` | `/api/v1/school/alumni/*`, `/api/v1/alumni/*` | Role JWT |
| Transport | `TransportRouting.kt` | `/api/v1/school/transport/*`, `/api/v1/transport/*`, `/api/v1/parent/transport/*` | Role JWT |
| Scholarship | `ScholarshipRouting.kt` | `/api/v1/school/scholarships/*`, `/api/v1/parent/scholarships/*` | Role JWT |
| ID Card | `IdCardRouting.kt` | `/api/v1/school/id-cards/*`, `/api/v1/parent/id-card/*` | Role JWT |
| Branding | `BrandingRouting.kt` | `/api/v1/school/branding/*`, `/api/v1/branding/*` | School JWT |
| Scheduling | `ScheduledMessageRouting.kt` | `/api/v1/school/scheduled-messages` | School+Teacher JWT |
| Event Registration | `EventRegistrationRouting.kt` | `/api/v1/school/events/*`, `/api/v1/parent/events/*`, `/api/v1/teacher/events/*` | Role JWT |
| School Day Config | `SchoolDayConfigRouting.kt` | `/api/v1/school/day-config` | School JWT |
| Timetable Import | `TimetableImportRouting.kt` | `/api/v1/school/timetable/*` | School JWT |
| Organization | `OrganizationRouting.kt` | `/api/admin/organizations/*`, `/api/v1/organization/*` | OrgAdmin JWT |
| Syllabus Pace | `SyllabusPaceRouting.kt` | `/api/v1/school/pace/*` | School JWT |
| i18n | `I18nRouting.kt` | `/api/v1/user/language-*`, `/api/admin/language-*` | JWT |
| Platform (QA) | `PlatformRouting.kt` | `/api/admin/platform/*` | SuperAdmin+QA JWT |
| Media | `MediaRouting.kt` | `/api/v1/media/*` | JWT |
| Dev Tools | `DevToolsRouting.kt` | `/api/v1/admin/dev/*` | SuperAdmin |
| Server Logs | `ServerLogRouting.kt` | `/api/v1/admin/dev/logs/*` | SuperAdmin |
| Health Check | `HealthCheckRouting.kt` | `/api/v1/health` | Public |
| Landing | `LandingRouting.kt` | `/api/v1/landing/*` | Public |
| Support | `SupportRouting.kt` | `/api/v1/support/*` | Public |
| AI | `AiRouting.kt` | `/api/v1/ai/*` | JWT |
| Gateway | `GatewayRouting.kt` | `/api/v1/gateway/*` | Public |
| Academic Calendar | `AcademicCalendarRouting.kt` | `/api/v1/school/calendar/*` | School JWT |
| Academic Year | `AcademicYearRouting.kt` | `/api/v1/school/academic-years/*` | School JWT |
| Feature Flags | `FeatureFlagRouting.kt` | `/api/v1/feature-flags/*` | JWT |
| OpenAPI | `OpenApiRouting.kt` | `/api/openapi` | Public |
| App Status | `AppStatusRouting.kt` | `/api/v1/app-status` | Public |
| Version | `VersionRouting.kt` | `/api/v1/version/*` | Public |

### 2.3 ViewModels (shared module)

**Total:** ~99 ViewModel files registered in Koin.

### 2.4 Website/Admin Portal (Next.js)

**Total:** ~42 `.tsx` page files under `website/src/app/`.

### 2.5 Database Tables

**Total:** 80+ Exposed table objects in `Tables.kt` (4162 lines).

---

## 3. Test Matrix

### 3.1 Existing Tests

| Test File | Scope | Status |
|-----------|-------|--------|
| `shared/src/commonTest/.../MainViewModelTest.kt` | Auth state VM | Only test found (10 TODO references) |

### 3.2 Required Test Matrix (Per Feature)

| Feature | Unit Tests | Integration Tests | E2E Tests | Status |
|---------|-----------|-------------------|-----------|--------|
| Auth (OTP/Login/Signup) | None | None | None | **Critical Gap** |
| Parent→Child Link | None | None | None | **Critical Gap** |
| Teacher Assignment Scoping | None | None | None | **Critical Gap** |
| Attendance Marking | None | None | None | **Critical Gap** |
| Marks Entry | None | None | None | **Critical Gap** |
| Homework | None | None | None | **Critical Gap** |
| Fee Payment | None | None | None | **Critical Gap** |
| Messaging | None | None | None | **Critical Gap** |
| Notifications | None | None | None | **Critical Gap** |
| Report Card (AI) | None | None | None | **Critical Gap** |
| Tutor (AI) | None | None | None | **Critical Gap** |
| PEWS | None | None | None | **Critical Gap** |
| Library | None | None | None | **Critical Gap** |
| Transport | None | None | None | **Critical Gap** |
| Scholarship | None | None | None | **Critical Gap** |
| Alumni | None | None | None | **Critical Gap** |
| Health Records | None | None | None | **Critical Gap** |
| Branding | None | None | None | **Critical Gap** |
| ID Cards | None | None | None | **Critical Gap** |
| Event Registration | None | None | None | **Critical Gap** |
| Multi-Branch/Org | None | None | None | **Critical Gap** |
| i18n | None | None | None | **Critical Gap** |
| Platform QA | None | None | None | **Critical Gap** |
| JWT Issuance/Verification | None | None | None | **Critical Gap** |
| School Access Guards | None | None | None | **Critical Gap** |
| Teacher Access Guards | None | None | None | **Critical Gap** |
| Error Handling | None | None | None | **Critical Gap** |

---

## 4. Backend Audit

### 4.1 Server Architecture

- **Entry point:** `Application.kt` (704 lines) — wires all plugins + routes
- **Plugins:** CORS, ContentNegotiation (JSON, 1MB max), Authentication (JWT), StatusPages, CSRF, Metrics, GracefulShutdown
- **Database:** Postgres (prod, HikariCP), SQLite (dev, auto-create)
- **Background jobs:** NotificationScheduler, PulseWeeklyJob, PEWS jobs, AI Report Card jobs, IdCardExpiryCheckJob, Transport/Library schedulers

### 4.2 Issues Found

#### B-01: No Rate Limiting on Auth Endpoints — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `AuthRouting.kt`, `LoginThrottle.kt`, `OtpService.kt`
- **Status:** FALSE POSITIVE — Verified 2026-07-12. Rate limiting is fully implemented:
  - **OTP send:** `OtpService.send()` enforces max 5 resends/hour per identifier with `SELECT FOR UPDATE` row locking (RA-38). Returns HTTP 429 `OTP_RATE_LIMITED`.
  - **Login (email/password):** `LoginThrottle` implements per-IP AND per-identifier sliding window (8 attempts/15 min, configurable via `LOGIN_MAX_ATTEMPTS`/`LOGIN_WINDOW_SECONDS`). Returns HTTP 429 `LOGIN_THROTTLED`.
  - **OTP verify:** Brute-force lock after `maxAttempts` (default 3) wrong guesses.

#### B-02: Refresh Token Not Persisted or Rotated — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `JwtConfig.kt`, `AuthRouting.kt`, `UserSessionsTable`
- **Status:** FALSE POSITIVE — Verified 2026-07-12. Refresh tokens are fully persisted, rotated, and revocable:
  - **Persistence:** `UserSessionsTable` stores SHA-256 hash of refresh token with device info, IP, expiry, and revocation timestamp.
  - **Rotation:** `/refresh` endpoint revokes old token, issues + persists new one (RA-35).
  - **Reuse detection:** Presenting a revoked token revokes ALL sessions for the user.
  - **Revocation:** `/logout` revokes specific or all sessions. `/change-password` revokes all sessions.
  - **Deactivation kill-switch:** Deactivated users' sessions are revoked on refresh attempt (RA-34).

#### B-03: JWT Expiry Configuration Conflict — ✅ FIXED 2026-07-12
- **Severity:** Low
- **Location:** `JwtConfig.kt`
- **Status:** FIXED. Removed dead `expirySecs` field. Fixed misleading comment from "7 days" to reflect actual role-based expiry (30 min admin, 24 h others). `issueToken()` uses `ADMIN_EXPIRY_SECS`/`DEFAULT_EXPIRY_SECS` as intended.

#### B-04: No DB Transaction Boundary on Multi-Step Operations — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `ParentLinkRouting.kt` (approve flow), `AuthRouting.kt` (signup, register-school, refresh, change-password, logout)
- **Status:** FALSE POSITIVE — Verified 2026-07-12. `dbQuery` uses `newSuspendedTransaction(Dispatchers.IO) { block() }` which wraps the entire block in a single Exposed transaction. All multi-step operations (approve link, register school, refresh token rotation, change password + revoke sessions, logout) are inside single `dbQuery` blocks. `StudentAggregationService.enforceSinglePrimaryGuardian` does NOT open its own `dbQuery` — it runs raw Exposed ops within the caller's transaction.

#### B-05: No Input Validation Framework
- **Severity:** Medium
- **Location:** All routing files
- **Issue:** Request body validation is manual (null checks, string length). No validation framework (e.g. Kotlin Validation or custom DSL). Inconsistent validation across routes — some check for blank strings, some don't.
- **Recommendation:** Introduce a lightweight validation DSL or at minimum consistent null/blank checks on all required fields.

#### B-06: Exposed Table Joins Not Used — In-Memory Stitching
- **Severity:** Medium
- **Location:** `TeacherAccess.kt:236-267` (`enrollmentsFor`)
- **Issue:** The codebase deliberately avoids Exposed join queries, instead doing two-step lookups + in-memory stitching. This works for small schools but will degrade with large rosters (N+1 query pattern).
- **Recommendation:** Add Exposed join queries for hot paths (enrollments+students, assignments+classes).

#### B-07: No API Versioning Strategy Beyond v1
- **Severity:** Low
- **Location:** All routes under `/api/v1/`
- **Issue:** All routes are v1 with no plan for v2. Breaking changes would require URL-level versioning.
- **Recommendation:** Document versioning strategy; consider header-based negotiation for minor versions.

#### B-08: Background Jobs Not Idempotent — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `Application.kt` — job startup section
- **Status:** FALSE POSITIVE — Verified 2026-07-12. All background jobs use `AtomicReference<LocalDate?>` with `compareAndSet` guards preventing duplicate runs on the same day:
  - `PulseWeeklyJob`: `lastRunDate` guard (Sunday-only, hourly check)
  - `PewsDailyJob`: `lastRunDate` guard + per-school frequency check
  - `DailySummaryAutoJob`: `lastRunDate` guard + existing-log check (skips if teacher already logged)
  - `TransportJobScheduler`: `lastFinalizationDate` guard
  - `LibraryJobScheduler`: `lastDailyRunDate`, `lastMonthlyRunDate`, `lastBadgeRunDate` guards
  Jobs are inherently idempotent — they check for existing records before creating new ones. Server restarts reset the AtomicReferences, but the hourly check pattern + existing-record checks prevent duplicates.

#### B-09: CSRF Protection Scope — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `Application.kt` — CSRF install, `CsrfProtection.kt`
- **Status:** FALSE POSITIVE — Verified 2026-07-12. CSRF protection is correctly scoped:
  - Only applies to state-changing methods (POST/PUT/PATCH/DELETE) in production
  - Skips non-browser requests (no `Origin` header → mobile apps, curl)
  - Validates `Origin` against `CORS_ALLOWED_ORIGINS` env var
  - Returns 403 with `CSRF_NO_ALLOWED_ORIGINS` or `CSRF_ORIGIN_MISMATCH` error codes
  - Dev mode is skipped entirely for DX convenience
  - JWT bearer tokens are inherently CSRF-resistant (browsers don't auto-attach them); this is defense-in-depth for the web admin portal.

#### B-10: No Health Check for Background Jobs
- **Severity:** Low
- **Location:** `HealthCheckRouting.kt`
- **Issue:** The `/api/v1/health` endpoint likely checks DB connectivity but not background job health. A job could silently fail while the health check passes.
- **Recommendation:** Add job health metrics to the health check endpoint.

#### B-11: 130 TODO/FIXME Markers in Server
- **Severity:** Medium
- **Location:** 53 server files
- **Issue:** 130 TODO/FIXME markers indicate incomplete implementations or known technical debt. Key files: `AlumniService.kt` (7), `CsvImportService.kt` (7), `CaseworkerTools.kt` (6), `IdCardRenderer.kt` (5), `LibraryRepository.kt` (5).
- **Recommendation:** Triage all TODOs before release; either resolve, document as accepted debt, or create tickets.

#### B-12: No Request ID Propagation — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `RequestIdPlugin.kt`, `ErrorHandling.kt`, `logback.xml`
- **Status:** FALSE POSITIVE — Verified 2026-07-12. Request ID propagation is fully implemented:
  - `RequestIdPlugin` generates/accepts `X-Request-ID` header on every request
  - Request ID stored in call attributes and MDC (`MDC.put("requestId", id)`)
  - `logback.xml` pattern includes `[%X{requestId:-}]` so every log line is stamped
  - All error responses include `requestId` field via `requestIdSafe()`
  - MDC is cleaned up on `ResponseSent` and `CallFailed`

---

## 5. Frontend Audit (composeApp)

### 5.1 Architecture

- **Entry point:** `App.kt` (289 lines) — KoinContext → MainViewModel → SessionScope → NavGraphV2
- **Navigation:** `NavGraphV2.kt` (635 lines) — role-based portal selection with deep link parsing
- **Design system:** VTheme (colors, typography, dimens) with Material3 bridge
- **Image loading:** Coil 3 with Ktor fetcher + Supabase token stripping
- **Session management:** `SessionScope` with JWT-keyed ViewModelStore

### 5.2 Issues Found

#### F-01: Legacy V1 Screens Still Referenced
- **Severity:** Medium
- **Location:** `ui/screens/admin/`, `ui/screens/parent/`, `ui/screens/shared/`
- **Issue:** 9 legacy V1 screen files remain in the codebase. `App.kt` imports `AuthNavGraph` which uses V1 login/signup screens. These V1 screens have hardcoded colors, don't use VTheme, and may have inconsistent UX.
- **Recommendation:** Migrate auth screens to V2 design system or confirm V1 auth screens are intentionally retained.

#### F-02: Hardcoded Colors Bypass VTheme — ✅ FIXED 2026-07-12
- **Severity:** N/A
- **Location:** `ParentLinkChildScreenV2.kt`, `ParentHomeScreenV2.kt`, `IdCardCardsTab.kt`
- **Status:** FIXED. Replaced actionable hardcoded colors with VTheme/VColors tokens:
  - `ParentLinkChildScreenV2.kt`: `Color(0xFF7A1C18)` → `c.dangerInk`, `Color(0xFFB7791F)` → `c.warningInk`, `Color(0xFF155E3A)` → `c.successInk`
  - `ParentHomeScreenV2.kt`: `Color(0xFF4A30C4)` → `VColors.violetHover`
  - `IdCardCardsTab.kt`: `Color(0xFFD32F2F)` → `VColors.error`
- **Intentional exceptions (documented):** SRI pill colors in `DiscoveryScreenV2.kt` (`SriInk`/`SriBg`), notification tile colors in `NotificationsScreenV2.kt`, and avatar gradient colors in `PeopleCards.kt` are documented design constants mirroring the React source — not theme-aware by design.

#### F-03: VComingSoon Placeholders in Production Paths
- **Severity:** Medium
- **Location:** Multiple screens
- **Issue:** Several user-facing features render `VComingSoon`:
  - School Records → Documents tab
  - School Comms → Notifications tab
  - School Portal → Delivery Log overlay
  - Discovery → School Profile sections (About, Fee Structure, SRI Breakdown, Parent Reviews, On Map)
- **Recommendation:** Either implement these features before release or clearly mark them as "not available" in release notes.

#### F-04: No Pagination on List Screens
- **Severity:** Medium
- **Location:** All LazyColumn/LazyRow usage (168 matches across 52 files)
- **Issue:** Lists load all data at once — no server-side pagination. Large schools with 500+ students will experience slow loads and high memory usage.
- **Recommendation:** Add server-side pagination for student rosters, library books, alumni, notifications, messages, and announcements.

#### F-05: No Offline Mode / Caching Strategy
- **Severity:** Medium
- **Location:** Shared module — repository layer
- **Issue:** Room is configured (`shared/src/roomMain/`) but only School and Library local data sources were found. Most features fetch from network only with no offline cache. Network failures show error states with no cached fallback.
- **Recommendation:** Implement offline-first pattern for critical features (dashboard, attendance, messages).

#### F-06: SessionScope Keyed on JWT String — ✅ FIXED 2026-07-12
- **Severity:** N/A
- **Location:** `App.kt:204`, `MainViewModel.kt:12-17`
- **Status:** FIXED. SessionScope now keys on `userId + ":" + role` instead of the raw JWT string. Added `userId` field to `AuthState` and included it in the `combine()` flow in `MainViewModel`. Token refreshes (same user, new JWT) no longer tear down the SessionScope, eliminating the screen flash and ViewModel reload. The key still changes on logout (userId → null) or role switch (different user/role), preserving the session-bleed fix.

#### F-07: Deep Link Parsing Fragility — ✅ VERIFIED 2026-07-12
- **Severity:** Low
- **Location:** `NavGraphV2.kt` — `parseDeepLink()`
- **Status:** VERIFIED SAFE. `parseDeepLink()` is robust: it strips query strings before segment splitting, filters blank segments, and has a catch-all `DeepLinkTarget.Generic(currentRole, path)` fallback for any unrecognized path. No crash or security risk from malformed deep links. The function handles all role-based routing correctly and unknown segments gracefully default to a generic target. No formal URI scheme validation is needed since the function operates on path strings, not raw URIs.

#### F-08: No Loading Timeout
- **Severity:** Low
- **Location:** ViewModels across shared module
- **Issue:** Network calls have a 60s timeout (Ktor client config) but there's no UI-level loading timeout. A slow server will show indefinite loading skeletons.
- **Recommendation:** Add a 15-20s loading timeout that transitions to an error/retry state.

#### F-09: Debug Banner in Production — ✅ VERIFIED 2026-07-12
- **Severity:** N/A
- **Location:** `App.kt:227-241`
- **Status:** VERIFIED SAFE. `Config.isDev` is an `expect`/`actual` property that is `true` only for the `dev` Android build flavor and always `false` on non-Android targets. It cannot be misconfigured via env vars — it's compile-time determined by the Gradle build variant. The banner is never visible in production/release builds.

#### F-10: Coil Debug Logger Enabled — ✅ FIXED 2026-07-12
- **Severity:** N/A
- **Location:** `App.kt:127`
- **Status:** FIXED. Coil `DebugLogger()` is now gated behind `Config.isDev`: `.logger(if (Config.isDev) coil3.util.DebugLogger() else null)`. Production builds will not log image loading details.

---

## 6. UI Review

### 6.1 Design System

- **VTheme** provides colors (Light/Night), typography, dimens, and motion tokens via CompositionLocals.
- **Material3 bridge** maps VTheme colors to `MaterialTheme.colorScheme` for Material components.
- **Components:** 20 V-prefixed components (VButton, VCard, VInput, VScreenScaffold, VBottomNav2, VTopTabs, VAvatar, VBadge, VProgressBar, VProgressRing, VDonut, VBars, VDivider, VLabel, VStatusDot, ShimmerBox, VDatePicker, VPullRefresh, VConfirmDialog, VEmptyState, VComingSoon)

### 6.2 UI Issues

#### UI-01: Inconsistent Theme System — Dual Token Systems — ACCEPTED DEBT
- **Severity:** Medium (accepted)
- **Location:** `TeacherPortalV2.kt` imports `com.littlebridge.enrollplus.ui.tokens.VColors` (a separate token system) while `SchoolPortalV2.kt` and `ParentPortalV2.kt` use `com.littlebridge.enrollplus.ui.v2.theme.VTheme`.
- **Status:** ACCEPTED DEBT — Verified 2026-07-12. Two parallel color/typography systems exist:
  - `ui.tokens.VColors` — static object, warm cream/violet palette, single light theme, no dark mode support. Used by 15+ teacher screen files.
  - `ui.v2.theme.VColors` — data class with Light/Night/HighContrast variants, provided via CompositionLocals, theme-aware. Used by School and Parent portals.
  - The `VtC`/`VtT` bridge in `TeacherKitV2.kt` maps VTheme semantic names to `ui.tokens.VColors` values, designed as a migration bridge.
  - The `EnrollTokens.kt` bridge maps the loop task vocabulary to `VTheme` (the v2 system).
  - **Migration path:** Replace `VtC`/`VtT` references with `VTheme.colors`/`VTheme.type` in each teacher screen, swap `ui.tokens.VColors.xxx` → `VTheme.colors.yyy` using the VtC mapping table, remove `VtC`/`VtT` bridge. 15+ files need migration.
  - **Risk:** High — different property names between systems, every color ref needs individual mapping. Should be done as a dedicated PR, not a batch fix.

#### UI-02: PeopleCards.kt Has 20 Hardcoded Colors — ✅ VERIFIED 2026-07-12
- **Severity:** N/A
- **Location:** `school/PeopleCards.kt`
- **Status:** VERIFIED — The hardcoded `Color(0x...)` references in `PeopleCards.kt` are deterministic pastel avatar background gradients, computed from name hash. These are intentional design constants for the avatar component, not theme-aware colors. They are independent of the warm/night remap and are the same as the React source's avatar gradient system. No action needed.

#### UI-03: VComingSoon Has No "Notify Me" Backend
- **Severity:** Low
- **Location:** `VComingSoon` component accepts `onNotifyMe: (() -> Unit)?` but no screen wires this callback.
- **Issue:** The "Notify Me" feature is a UI affordance with no backend implementation.
- **Recommendation:** Either wire to a notification preference API or remove the parameter.

#### UI-04: No Accessibility Audit
- **Severity:** Medium
- **Location:** All screens
- **Issue:** No `contentDescription` audit has been done. Many `Icon` calls use `contentDescription = null`. No semantic roles, large touch target verification, or screen reader testing evident.
- **Recommendation:** Conduct accessibility audit; add content descriptions, verify touch target sizes (min 48dp), test with TalkBack/VoiceOver.

#### UI-05: Font Scale Support Exists but Untested
- **Severity:** Low
- **Location:** `VTheme.kt:68-69` — `LocalFontScale`
- **Issue:** Font scale support is implemented via `LocalFontScale` and `typography.scaleBy(fontScale)` but no screen-level testing has been done at 200% scale. Text overflow, layout clipping, and truncation are likely at high scale factors.
- **Recommendation:** Test all screens at 1.0x, 1.5x, and 2.0x font scale.

---

## 7. UX Review

### 7.1 Navigation UX

#### UX-01: Overlay-Based Navigation Instead of Stack
- **Severity:** Medium
- **Location:** All three portals (School, Teacher, Parent)
- **Issue:** Navigation uses enum-based overlays (`SchoolOverlay`, `ParentOverlay`, `TeacherOverlay`) instead of a navigation stack. This means:
  - No back stack history (can't press back to go to a previous overlay)
  - Deep link routing sets overlay state but doesn't push onto a stack
  - Only one overlay can be active at a time — no nested navigation within overlays
- **Recommendation:** Consider migrating to Navigation Compose with a proper back stack for complex flows.

#### UX-02: No Search in Admin Screens
- **Severity:** Low
- **Location:** Student Roster, Staff, Alumni screens
- **Issue:** Large list screens have no in-page search or filter. Users must scroll through all entries.
- **Recommendation:** Add search bars to list screens with >20 items.

#### UX-03: No Confirmation on Destructive Actions (Inconsistent)
- **Severity:** Medium
- **Location:** Various screens
- **Issue:** `VConfirmDialog` exists and RA-21 mandates "every destructive action must route through this," but it's unclear if all destructive actions (delete teacher, reject link request, withdraw student, cancel event) actually use it.
- **Recommendation:** Audit every destructive action endpoint and verify VConfirmDialog usage.

#### UX-04: No Haptic Feedback on Non-Tab Actions
- **Severity:** Low
- **Location:** VBottomNav2 has haptic feedback; other components don't
- **Issue:** Haptic feedback is only implemented on bottom tab navigation. Button presses, card taps, and swipe actions have no haptic feedback.
- **Recommendation:** Add subtle haptic feedback to primary action buttons and destructive confirmations.

#### UX-05: No Skeleton → Content Transition Animation
- **Severity:** Low
- **Location:** All screens using ShimmerBox
- **Issue:** Loading skeletons (ShimmerBox) abruptly switch to content when data loads. No crossfade or shimmer-to-content transition.
- **Recommendation:** Add a crossfade transition from skeleton to content.

---

## 8. API Integration Review

### 8.1 HTTP Client Configuration

- Single authenticated `HttpClient` registered as Koin `single`
- ContentNegotiation (JSON), HttpRedirect, HttpTimeout (60s)
- Bearer token auth with auto-refresh on 401
- Separate plain client for refresh-token exchange

### 8.2 Issues Found

#### API-01: No Request Retry Logic
- **Severity:** Medium
- **Location:** `Koin.kt` — HTTP client setup
- **Issue:** No retry on transient network failures (5xx, connection timeout). A single failed request shows an error state.
- **Recommendation:** Add retry with exponential backoff for idempotent GET requests.

#### API-02: 60-Second Timeout May Be Too Long — ACCEPTED
- **Severity:** Low (accepted)
- **Location:** Ktor client `HttpTimeout` config
- **Status:** ACCEPTED — Verified 2026-07-12. The 60s timeout is intentional for AI operations (report card generation, OCR, voice transcription) and file uploads that legitimately need long timeouts. Reducing to 15-20s globally would break AI features. Per-request timeout configuration would be the ideal solution but requires refactoring every API call. Document as accepted debt.

#### API-03: No Request/Response Logging in Dev
- **Severity:** Low
- **Location:** HTTP client config
- **Issue:** No logging plugin installed on the Ktor client. Dev debugging requires manual network inspection.
- **Recommendation:** Add `Logging` plugin with `LogLevel.HEADERS` in dev builds.

#### API-04: Token Refresh Race Condition
- **Severity:** Medium
- **Location:** `installTokenAuth` in shared core
- **Issue:** If multiple concurrent requests get 401 simultaneously, each may trigger a token refresh. This can cause refresh token reuse or race conditions.
- **Recommendation:** Implement a mutex/lock around token refresh so only one refresh happens at a time.

#### API-05: No API Contract Validation
- **Severity:** Medium
- **Location:** All API clients (e.g. `ParentApi`, `KtorSchoolApi`, etc.)
- **Issue:** API responses are deserialized directly into DTOs. If the server adds/removes a field, deserialization may fail silently or crash. No schema validation or version negotiation.
- **Recommendation:** Make DTO fields nullable with defaults; add response validation logging.

#### API-06: AppConfig.schoolBaseUrl Is Global
- **Severity:** Low
- **Location:** `shared/.../util/AppConfig.kt`
- **Issue:** All API clients use `AppConfig.schoolBaseUrl` which is a global. No per-school URL switching (though branding/subdomain routing exists on the server, the client always hits one base URL).
- **Recommendation:** Verify multi-tenant URL routing works end-to-end with branding subdomains.

---

## 9. Validation Audit

### 9.1 Client-Side Validation

#### V-01: Form Validation Inconsistent
- **Severity:** Medium
- **Location:** Various screens (ParentLinkChild, SchoolOnboarding, TeacherFirstLogin, AdminSignup)
- **Issue:** Form validation is done per-screen with ad-hoc logic. No shared validation utility. Some screens validate on submit, some on change, some on blur. Error messages are inconsistent.
- **Recommendation:** Create a shared validation DSL (e.g. `FormField<T>` with validation rules).

#### V-02: No Phone Number Validation
- **Severity:** Medium
- **Location:** Auth screens, Parent Link Child
- **Issue:** Phone numbers are sent as-is to the server. The server normalizes E.164 but the client doesn't validate format before sending. Invalid phone numbers may cause confusing server errors.
- **Recommendation:** Add client-side phone number format validation.

#### V-03: No Email Format Validation on Client
- **Severity:** Low
- **Location:** Auth signup screens
- **Issue:** Email validation appears to be server-side only.
- **Recommendation:** Add basic RFC-compliant email validation on the client.

### 9.2 Server-Side Validation

#### V-04: Inconsistent Required Field Validation
- **Severity:** Medium
- **Location:** All routing files
- **Issue:** Some routes use `requireNotNull()` / `?: return ...`, others use `?: ""` defaults. No consistent pattern for validating required body fields.
- **Recommendation:** Standardize on a validation helper or use Kotlin Serialization's `@Required` annotation.

#### V-05: File Upload Validation — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `MediaRouting.kt`, `MessageAttachmentUpload.kt`, `LibraryCoverService.kt`, `BrandingRouting.kt`
- **Status:** FALSE POSITIVE — Verified 2026-07-12. File upload validation is robust across all upload endpoints:
  - **MediaRouting.kt:** 25MB max, content-type allowlist via `SupabaseStorage.extensionFor()`
  - **MessageAttachmentUpload.kt:** Per-type limits (10MB image, 25MB video/doc, 10MB audio), content-type allowlist per attachment type
  - **LibraryCoverService.kt:** 5MB max, content-type allowlist (JPG/PNG/WebP only), **magic bytes verification** (prevents content-type spoofing), dimension validation (min/max)
  - **BrandingRouting.kt:** 10MB max, content-type allowlist via `SupabaseStorage.extensionFor()`
  - All endpoints reject empty files, unsupported types, and oversized uploads with appropriate HTTP status codes (413, 415).

---

## 10. Performance Audit

#### P-01: No Pagination (Server or Client)
- **Severity:** High
- **Location:** All list endpoints and screens
- **Issue:** No evidence of cursor-based or offset-based pagination on any endpoint. Schools with 500+ students, 1000+ alumni, or 10,000+ notifications will experience severe performance degradation.
- **Recommendation:** Add pagination to all list endpoints (students, notifications, messages, announcements, library books, alumni, audit logs).

#### P-02: In-Memory Data Stitching Instead of SQL Joins
- **Severity:** Medium
- **Location:** `TeacherAccess.kt:236-267`
- **Issue:** Two-step query + in-memory stitch for enrollments+students. O(N) memory and 2 DB round-trips per lookup.
- **Recommendation:** Use Exposed join queries for hot paths.

#### P-03: No Database Index Audit
- **Severity:** Medium
- **Location:** `Tables.kt`
- **Issue:** Tables define unique indexes but query patterns may need additional indexes (e.g. `parent_child_links` by `parent_id + status`, `enrollments` by `class_id + section + status`).
- **Recommendation:** Audit all query `where` clauses and verify corresponding indexes exist.

#### P-04: Coil Image Cache at 512MB Disk — ✅ FIXED 2026-07-12
- **Severity:** N/A
- **Location:** `App.kt:124`
- **Status:** FIXED. Reduced disk cache from 512MB to 256MB, more appropriate for low-end Android devices with 4-8GB storage.

#### P-05: No Memory Profiling for Large Lists
- **Severity:** Low
- **Location:** LazyColumn screens
- **Issue:** While LazyColumn is used (good), the underlying data models loaded into memory are not paginated. A 1000-item list loads all DTOs into memory even if only 10 are visible.
- **Recommendation:** Combine LazyColumn with paginated data loading.

#### P-06: Background Jobs on Main Server Thread
- **Severity:** Low
- **Location:** `Application.kt` — job startup
- **Issue:** Background jobs are launched at server startup. If a job is CPU-intensive (AI report generation, PEWS scoring), it could compete with request handling for resources.
- **Recommendation:** Verify jobs run on appropriate dispatchers (Dispatchers.Default for CPU, Dispatchers.IO for DB).

---

## 11. Security Audit

### 11.1 Authentication

#### S-01: No Rate Limiting on OTP/Login — ✅ FALSE POSITIVE (see B-01)
- **Severity:** N/A
- **Status:** FALSE POSITIVE — Verified 2026-07-12. See B-01 for full details.

#### S-02: Refresh Token Not Revocable — ✅ FALSE POSITIVE (see B-02)
- **Severity:** N/A
- **Status:** FALSE POSITIVE — Verified 2026-07-12. See B-02 for full details.

#### S-03: OTP Dev Code Leak — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `OtpService.kt:145-148`
- **Status:** FALSE POSITIVE — Verified 2026-07-12. `devReturnCode` is already hard-gated: `!isProduction && env("OTP_DEV_RETURN_CODE", "false").equals("true", true)`. Even if `OTP_DEV_RETURN_CODE=true` is set in production, `RuntimeEnvironment.isProduction` prevents the dev code from being returned.
- **NEW CRITICAL BUG FOUND & FIXED:** While verifying S-03, discovered that `OtpService.kt:186` had the `if (!isProduction)` guard on plaintext OTP **logging** commented out. This meant OTP codes were logged in ALL environments via `log.info(...)`. **FIXED 2026-07-12** — guard restored.

#### S-04: JWT Secret in Dev Mode
- **Severity:** Low
- **Location:** `JwtConfig.kt:64-68`
- **Issue:** In dev mode, an ephemeral random secret is generated. This is safe but tokens won't survive restarts, which could confuse developers.
- **Recommendation:** Document this behavior clearly; already logged as a warning.

### 11.2 Authorization

#### S-05: Role Read from DB (Good Practice)
- **Severity:** N/A (Positive)
- **Location:** `SchoolAccess.kt:96-104`, `TeacherAccess.kt:111-122`
- **Note:** Roles are read from the database, not the JWT claim. This prevents stale/forged JWT claims from widening access. This is a strong security pattern.

#### S-06: is_active Check on Every Guard (Good Practice)
- **Severity:** N/A (Positive)
- **Location:** All access guards (`requireSchoolContext`, `requireTeacherContext`, `requirePlatformUser`, `requirePlatformAdmin`, `requireOrgAdminContext`)
- **Note:** Every guard checks `is_active` on the user row. Deactivated accounts are immediately blocked.

#### S-07: School Staff Excluded from Privileged Writes (Good Practice)
- **Severity:** N/A (Positive)
- **Location:** `SchoolAccess.kt:48` — `SCHOOL_ADMIN_ROLES` excludes `school_staff`
- **Note:** Delegated staff can operate day-to-day but cannot manage accounts/credentials or broadcast announcements.

#### S-08: No Endpoint-Level Permission Matrix
- **Severity:** Medium
- **Location:** All routing files
- **Issue:** Permissions are enforced per-route via guards (`requireSchoolContext`, `requireSchoolAdmin`, etc.) but there's no centralized permission matrix. Adding a new route requires the developer to remember to add the correct guard.
- **Recommendation:** Create a declarative permission annotation or DSL that makes the required role explicit at the route definition site.

### 11.3 Data Protection

#### S-09: Password Hashing — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `PasswordHasher.kt`
- **Status:** FALSE POSITIVE — Verified 2026-07-12. Password hashing is properly implemented:
  - **Algorithm:** PBKDF2WithHmacSHA256, 120,000 iterations, 16-byte random salt, 256-bit derived key
  - **Format:** PHC-compatible: `pbkdf2_sha256$<iterations>$<base64-salt>$<base64-key>`
  - **Verification:** Constant-time comparison
  - **Legacy migration:** Auto-upgrades old `sha256Hex("pwd:$p")` hashes on successful login via `needsRehash()`

#### S-10: No Encryption at Rest for Sensitive Data
- **Severity:** Low
- **Location:** Database tables
- **Issue:** Student health records, parent phone numbers, and financial data (fees) are stored in plaintext. No column-level encryption.
- **Recommendation:** Consider encrypting health records and financial data at rest.

#### S-11: CORS Configuration — ✅ FALSE POSITIVE
- **Severity:** N/A
- **Location:** `Application.kt` — CORS install
- **Status:** FALSE POSITIVE — Verified 2026-07-12. CORS is production-safe:
  - **Production:** Only allows origins from `CORS_ALLOWED_ORIGINS` env var (parsed into host+scheme, registered via `allowHost()`). If unset, all cross-origin requests are rejected (no `anyHost()` fallback).
  - **Dev:** `anyHost()` with explicit warning log.
  - Allowed headers: `Content-Type`, `Authorization`, `App-Version`, `Platform`, `Device-Id`, `Accept-Language`, `X-Request-Id`.
  - Allowed methods: GET, POST, PUT, PATCH, DELETE, OPTIONS.

### 11.4 Multi-Tenant Isolation

#### S-12: School ID Isolation (Good Practice)
- **Severity:** N/A (Positive)
- **Location:** `SchoolAccess.kt`, all school-scoped queries
- **Note:** Every school-scoped query filters by `school_id` from the JWT-resolved context. Cross-tenant data access is prevented at the guard level.

#### S-13: Teacher Assignment Ownership (Good Practice)
- **Severity:** N/A (Positive)
- **Location:** `TeacherAccess.kt` — `ownsAssignment()` + `requireOwnedAssignment()`
- **Note:** Teachers can only access classes where they have an active `teacher_subject_assignments` row. ID-first ownership (T-003) prevents display-name collision attacks.

---

## 12. Platform Audit

### 12.1 Android

#### PL-01: Firebase Messaging Integration
- **Status:** Implemented
- **Notes:** `Firebase Messaging 25.0.1` for push notifications. Device token registration via `/api/device-tokens`.

#### PL-02: Google Maps Integration
- **Status:** Implemented
- **Notes:** `Google Maps Compose 6.4.1` for bus tracking. Need to verify API key is not hardcoded.

#### PL-03: Deep Link Handling
- **Status:** Implemented
- **Notes:** `App.kt` accepts `deepLink` parameter. `NavGraphV2.parseDeepLink()` routes to correct screen. Need to verify AndroidManifest.xml intent filters.

### 12.2 iOS

#### PL-04: iOS Build Not Verified
- **Severity:** Medium
- **Issue:** No evidence of iOS build testing. Koin has `initKoin()` for iOS. Need to verify iOS-specific implementations (push notifications, maps, image loading) work.
- **Recommendation:** Build and test on iOS simulator.

### 12.3 Web (Wasm/JS)

#### PL-05: Web Build Not Verified
- **Severity:** Medium
- **Issue:** Wasm/JS targets exist but no evidence of web build testing. Firebase, Google Maps, and platform-specific APIs may not work on web.
- **Recommendation:** Build and test `wasmJsBrowserDevelopmentRun`.

### 12.4 Desktop (JVM)

#### PL-06: Desktop Build Not Verified
- **Severity:** Low
- **Issue:** Desktop target exists but is likely not a release target.
- **Recommendation:** Verify it builds; deprioritize desktop testing.

---

## 13. Known Bugs

### 13.1 Previously Fixed (Documented in Code)

| Bug ID | Description | Fix Location | Status |
|--------|-------------|-------------|--------|
| Session-bleed | Admin logout → Parent login shows Admin dashboard | `App.kt:137-288` — SessionScope | Fixed |
| 404 clobber | StatusPages 404 handler overwrote deliberate 404s from route handlers | `ErrorHandling.kt:144-145` — RouteHandledResponseKey | Fixed |
| Link-child "Endpoint not found" | Same as 404 clobber | Same fix | Fixed |
| Track-progress crash | EI field crash on parent header | `ParentPortalV2.kt:78-83` — switched to dashboard VM | Fixed |
| Teacher name collision | Display-name collision granted assignment access | `TeacherAccess.kt:150-163` — ID-first ownership | Fixed |

### 13.2 Suspected/Potential Bugs

| Bug ID | Description | Location | Severity |
|--------|-------------|----------|----------|
| BUG-01 | ~~Token refresh race condition~~ **FIXED 2026-07-12** — added `Mutex` to serialize concurrent refresh attempts | `TokenAuthenticator.kt` | N/A |
| BUG-02 | ~~SessionScope teardown on token refresh causes screen flash~~ **FIXED 2026-07-12** — key on userId+role | `App.kt:207` | N/A |
| BUG-03 | ~~Multi-step DB operations without enclosing transaction~~ FALSE POSITIVE — all in single `dbQuery` | `ParentLinkRouting.kt` | N/A |
| BUG-04 | ~~OTP dev code may leak in production~~ FALSE POSITIVE — hard-gated via `RuntimeEnvironment.isProduction` | `OtpService.kt:145-148` | N/A |
| BUG-05 | VComingSoon features may confuse users as real features | Multiple screens | Low |
| BUG-06 | Legacy V1 auth screens may have different validation/error patterns | `ui/screens/` | Low |
| BUG-07 | Dual theme systems (tokens vs v2.theme) — ACCEPTED DEBT: `VtC`/`VtT` bridge in place; migration path documented | Teacher portal vs others | Medium |
| BUG-08 | No pagination causes timeout on large datasets | All list endpoints | High |
| BUG-09 | ~~Background jobs may duplicate work on server restart~~ **FALSE POSITIVE** — all jobs use `AtomicReference<LocalDate?>` + `compareAndSet` guards | `Application.kt` job startup | N/A |
| BUG-10 | ~~`expirySecs` in JwtConfig is dead code~~ **FIXED 2026-07-12** — removed dead field | `JwtConfig.kt` | N/A |

---

## 14. Missing Features

| Feature | Expected Location | Status | Priority |
|---------|------------------|--------|----------|
| ~~Rate limiting~~ | ~~Auth endpoints~~ | ✅ Implemented (`LoginThrottle` + `OtpService` resend cap) | N/A |
| ~~Refresh token persistence/revocation~~ | ~~Server DB + auth~~ | ✅ Implemented (`UserSessionsTable` with rotation + reuse detection) | N/A |
| Server-side pagination | All list endpoints | Not implemented | High |
| Offline mode / caching | Shared repositories | Partial (School, Library only) | Medium |
| Request retry logic | HTTP client | Not implemented | Medium |
| Automated test suite | All modules | Nearly absent | High |
| API contract/schema validation | Client DTOs | Not implemented | Medium |
| File upload validation | Media routes | ✅ VERIFIED — MIME whitelist, size limits, magic bytes check | N/A |
| Accessibility audit | All screens | Not done | Medium |
| Search in admin list screens | School admin screens | Not implemented | Low |
| Delivery Log | School portal | `VComingSoon` | Low |
| Documents library | School records | `VComingSoon` | Low |
| School profile details (Discovery) | Discovery screen | `VComingSoon` (About, Fees, SRI, Reviews, Map) | Low |
| Notifications tab (School Comms) | School comms | `VComingSoon` | Low |
| "Notify Me" backend for VComingSoon | Server | Not implemented | Low |
| CI/CD pipeline | Repo config | Needs verification | Medium |
| Error monitoring (Sentry) | Server + client | Not integrated | Medium |
| API documentation (OpenAPI) | Server | `OpenApiRouting.kt` exists — needs verification | Low |

---

## 15. Technical Debt

| ID | Description | Location | Impact |
|----|-------------|----------|--------|
| TD-01 | 42 TODO/FIXME in shared module | 16 files | Incomplete implementations |
| TD-02 | 130 TODO/FIXME in server | 53 files | Incomplete implementations |
| TD-03 | Legacy V1 screens (9 files) | `ui/screens/` | Maintenance burden, inconsistent UX |
| TD-04 | Dual theme token systems — ACCEPTED DEBT: 15+ teacher screens use `ui.tokens.VColors` (static, no dark mode); `VtC`/`VtT` bridge designed for migration | `ui/tokens/` vs `ui/v2/theme/` | Visual inconsistency, no dark mode for teacher portal |
| TD-05 | ~~42 hardcoded colors~~ **FIXED 2026-07-12** — actionable colors replaced with VTheme/VColors tokens; remaining are documented design constants | 12 v2 screen files | N/A |
| TD-06 | No Exposed join queries | Server data layer | Performance on large datasets |
| TD-07 | Manual SQL migrations | `docs/db/` | Human error risk, no rollback |
| TD-08 | ~~`expirySecs` dead code~~ **FIXED 2026-07-12** | `JwtConfig.kt` | N/A |
| TD-09 | In-memory data stitching | `TeacherAccess.kt` | N+1 query pattern |
| TD-10 | No centralized validation | All modules | Inconsistent validation |
| TD-11 | Overlay-based navigation | All portals | No back stack, limited deep linking |
| TD-12 | `runCatching` used for error swallowing | 6 shared files | Silent failures |
| TD-13 | ~~Debug logger always on~~ **FIXED 2026-07-12** — gated behind `Config.isDev` | `App.kt:127` | N/A |
| TD-14 | ~~Debug banner gated on `Config.isDev`~~ **VERIFIED 2026-07-12** — `isDev` is compile-time `expect`/`actual`, true only for dev Android flavor | `App.kt:227` | N/A |
| TD-15 | No dependency injection test helpers | Shared module | Hard to unit test VMs |

---

## 16. Release Blockers

### Critical (Must Fix Before Release)

| ID | Blocker | Reason |
|----|---------|--------|
| RB-01 | ~~No rate limiting on auth endpoints~~ **FALSE POSITIVE** — `LoginThrottle` + `OtpService` resend cap | N/A |
| RB-02 | ~~Refresh token not revocable~~ **FALSE POSITIVE** — `UserSessionsTable` with rotation + reuse detection | N/A |
| RB-03 | No automated tests | Cannot verify correctness of any feature |
| RB-04 | No server-side pagination | Will fail on schools with 100+ students |
| RB-05 | ~~OTP dev code not hard-gated~~ **FALSE POSITIVE** — `!isProduction` guard in `devReturnCode` | N/A |
| **RB-NEW** | **OTP plaintext logging in production** — **FIXED 2026-07-12** — guard restored | Was Critical — now Fixed |

### High (Should Fix Before Release)

| ID | Blocker | Reason |
|----|---------|--------|
| RB-06 | No error monitoring (Sentry/crashlytics) | Cannot detect production issues |
| RB-07 | ~~Multi-step DB operations lack transaction boundary~~ **FALSE POSITIVE** — `dbQuery` wraps in single `newSuspendedTransaction` | N/A |
| RB-08 | ~~Token refresh race condition~~ **FIXED 2026-07-12** — `Mutex` serializes concurrent refresh attempts | N/A |
| RB-09 | 130 server TODOs untriaged | Unknown incomplete implementations |
| RB-10 | iOS and Web builds unverified | May not compile or run |
| RB-11 | No CI/CD pipeline | No automated build/test/deploy |

### Medium (Fix Before Public Release)

| ID | Blocker | Reason |
|----|---------|--------|
| RB-12 | ~~Hardcoded colors break dark mode~~ **FIXED 2026-07-12** — actionable colors replaced; remaining are documented design constants | N/A |
| RB-13 | Legacy V1 auth screens | UX inconsistency |
| RB-14 | No offline mode | Poor UX on flaky networks |
| RB-15 | Dual theme systems — ACCEPTED DEBT: `VtC`/`VtT` bridge in place; migration path documented | Medium — teacher portal lacks dark mode |
| RB-16 | No accessibility audit | Legal compliance risk |
| RB-17 | No input validation framework | Data integrity risk |

---

## 17. Master QA Checklist

### Pre-Release Manual Testing

#### Authentication & Onboarding
- [ ] Parent signup with phone OTP (real SMS, not dev code)
- [ ] Parent signup with email + password
- [ ] School admin signup + onboarding flow (all steps)
- [ ] Teacher first login + password change
- [ ] Login with phone OTP
- [ ] Login with email + password
- [ ] Token refresh flow (wait for token expiry → auto-refresh)
- [ ] Logout → verify session cleared (no session bleed)
- [ ] Logout → re-login as different role → verify clean state
- [ ] Deactivated account login attempt → verify 403
- [ ] OTP rate limiting (if implemented)
- [ ] Refresh token revocation on logout (if implemented)

#### Parent→Child Link
- [ ] Link child with correct roll number + class + section
- [ ] Link child with incorrect roll number → verify error
- [ ] Link child with phone mismatch → verify `needs_review` status
- [ ] Cross-school self-heal (child at different school)
- [ ] Multiple pending link requests (throttle at 3)
- [ ] Admin approve link → verify child appears on parent dashboard
- [ ] Admin reject link → verify parent notified
- [ ] Primary guardian enforcement (only one per student)
- [ ] Link to same child from two parents → verify both can link

#### School Admin
- [ ] Dashboard loads with real data
- [ ] Student roster CRUD (add, edit, deactivate)
- [ ] Teacher assignment CRUD
- [ ] Class + subject management
- [ ] Attendance summary view
- [ ] Marks summary view
- [ ] Fee ledger view
- [ ] Announcements create + broadcast
- [ ] Messages (individual + group)
- [ ] PTM scheduling
- [ ] Link requests approve/reject
- [ ] Leave requests approve/reject
- [ ] Academic calendar create event (7-step wizard)
- [ ] Academic year management
- [ ] School day configuration
- [ ] Timetable import
- [ ] PEWS cohort view
- [ ] PEWS student detail
- [ ] Health records CRUD
- [ ] Alumni management + campaign
- [ ] Transport management (routes, vehicles, assignments)
- [ ] Scholarship scheme CRUD + application review
- [ ] Branding kit (logo, colors, subdomain)
- [ ] ID card generation
- [ ] Library management (books, issues, returns, categories, acquisitions, audit, announcements)
- [ ] Scheduled messages
- [ ] Event registration + RSVP
- [ ] Report card publish
- [ ] Report card effectiveness
- [ ] Analytics dashboard
- [ ] Class/teacher performance
- [ ] Edit school profile
- [ ] School settings (theme, language)
- [ ] Multi-branch organization views (if applicable)

#### Teacher
- [ ] Home screen loads with today's schedule
- [ ] Attendance marking (class/section/subject scope gate)
- [ ] Marks entry
- [ ] Homework assignment
- [ ] Syllabus coverage update
- [ ] Lesson plan
- [ ] Class roster view
- [ ] Student profile drill-down
- [ ] Messages (to parents, to admins)
- [ ] Leave apply + status
- [ ] Timetable view
- [ ] PEWS student view
- [ ] Report card review queue
- [ ] Report card draft editor
- [ ] Tutor heatmap
- [ ] Transport attendance
- [ ] Event registration
- [ ] Scheduled messages
- [ ] Profile + password change
- [ ] First-login password change gate

#### Parent
- [ ] Home dashboard loads with child summary
- [ ] Academics tab (attendance, marks, homework, syllabus, timetable)
- [ ] Fee payment + history
- [ ] Messages (to teacher, to admin)
- [ ] Leave application
- [ ] Profile + digital ID card
- [ ] Notifications (read, mark-all-read, clear)
- [ ] Bus tracking (live location)
- [ ] Library (search, reserve, reservations)
- [ ] Event registration + RSVP
- [ ] Scholarships (browse, apply, renew, view applications)
- [ ] Tutor chat
- [ ] Tutor progress
- [ ] Report card view
- [ ] PEWS nudges
- [ ] Health records view
- [ ] Discovery (school marketplace)
- [ ] Theme switcher (light/dark/midnight)
- [ ] Language switcher

#### Cross-Role
- [ ] Deep link from push notification → correct screen
- [ ] Push notification delivery (FCM)
- [ ] Notification read state sync
- [ ] Multi-language: verify all screens in each supported language
- [ ] Dark mode: verify all screens (especially hardcoded color screens)
- [ ] Midnight theme: verify all screens
- [ ] Font scale 2.0x: verify no text clipping
- [ ] Offline behavior: verify error states + retry
- [ ] Network timeout: verify error state appears
- [ ] Background app → foreground: verify state restoration
- [ ] Low memory: verify no crashes

#### Backend
- [ ] All endpoints respond with correct status codes
- [ ] Tenant isolation: cross-school data access returns 403/404
- [ ] Teacher scoping: unassigned class access returns 403
- [ ] File upload (profile pic, media) works
- [ ] Background jobs run without errors
- [ ] Server health check passes
- [ ] OpenAPI spec is accurate
- [ ] CORS headers correct for website
- [ ] CSRF protection doesn't block legitimate requests
- [ ] Graceful shutdown completes pending requests

#### Website/Admin Portal
- [ ] All admin pages load with real data
- [ ] Login flow works
- [ ] Onboarding flow works
- [ ] Platform QA management (if implemented)
- [ ] Dev tools (super admin only)
- [ ] Server log viewer
- [ ] Language management
- [ ] Multi-branch views

---

## 18. Final Recommendations

### Priority 1 — Release Blockers (Must Do)

1. **Implement rate limiting** on `/api/v1/auth/send-otp` and `/api/v1/auth/login`. Max 3 OTPs per 10 minutes per identifier; max 5 login attempts per 15 minutes.

2. **Persist and rotate refresh tokens** in a `refresh_tokens` table. On refresh, invalidate the old token and issue a new one. On logout, revoke all refresh tokens for the user.

3. **Hard-gate `OTP_DEV_RETURN_CODE`** with `RuntimeEnvironment.isProduction` — same pattern as `DEBUG_ERRORS`.

4. **Add server-side pagination** to all list endpoints. Start with student roster, notifications, messages, announcements, library books, and alumni.

5. **Set up a CI/CD pipeline** with at minimum: compile check, lint, and build verification for `:server` and `:composeApp:assembleDebug`.

6. **Write integration tests** for critical paths: auth flow, parent-child link, attendance marking, fee payment, messaging.

### Priority 2 — High Impact (Should Do)

7. **Integrate error monitoring** (Sentry for server, Crashlytics for Android). Production issues are currently invisible.

8. **Wrap multi-step DB mutations** in a single transaction. Audit `ParentLinkRouting.kt` approve flow, scholarship disbursement, and alumni campaign operations.

9. **Fix token refresh race condition** with a mutex/lock in `installTokenAuth`.

10. **Triage all 130 server TODOs** — resolve, document as accepted debt, or create tickets.

11. **Verify iOS and Web builds** compile and basic flows work.

12. **Add request retry logic** for idempotent GET requests with exponential backoff.

### Priority 3 — Quality Improvements (Nice to Have)

13. **~~Replace all hardcoded `Color(0x...)` references~~** ✅ FIXED — actionable colors replaced; remaining are documented design constants.

14. **Unify theme systems** — merge `ui/tokens/` into `ui/v2/theme/` to eliminate the dual system. ACCEPTED DEBT — `VtC`/`VtT` bridge in place; 15+ teacher screens need migration.

15. **Migrate legacy V1 auth screens** to V2 design system.

16. **Add offline caching** for dashboard, attendance, and messages using Room.

17. **Conduct accessibility audit** — content descriptions, touch targets, screen reader testing.

18. **Add search bars** to large list screens (student roster, alumni, library).

19. **~~Reduce HTTP timeout~~** ACCEPTED — 60s needed for AI ops; per-request timeout is the ideal solution.

20. **~~Gate Coil debug logger~~** ✅ FIXED — gated behind `Config.isDev`.

21. **~~Key SessionScope on userId+role~~** ✅ FIXED — keys on `userId + ":" + role`.

22. **Add database indexes** for common query patterns (enrollments by class_id+section+status, parent_child_links by parent_id+status).

23. **~~Reduce Coil disk cache~~** ✅ FIXED — reduced from 512MB to 256MB.

---

### Appendix A: File Counts

| Module | Files | Lines (approx) |
|--------|-------|----------------|
| composeApp/ui/v2/screens | ~118 | — |
| composeApp/ui/v2/components | 20 | — |
| composeApp/ui/v2/theme | 11 | — |
| composeApp/ui/screens (legacy) | 9 | — |
| shared/feature/*/presentation | ~99 ViewModels | — |
| server/feature/*/Routing.kt | 29+ | — |
| server/db/Tables.kt | 1 | 4,162 |
| server/Application.kt | 1 | 704 |
| shared/di/Koin.kt | 1 | 735 |
| website/src/app | ~42 .tsx | — |

### Appendix B: Dependency Versions

| Dependency | Version |
|------------|---------|
| Kotlin | 2.2.10 |
| Compose Multiplatform | 1.10.3 |
| Ktor (client+server) | 3.4.3 |
| Koin | 4.0.0 |
| Exposed | 0.50.0 |
| HikariCP | 5.1.0 |
| Room | 2.7.0-alpha12 |
| Coil | 3.4.0 |
| Firebase Admin | 9.4.3 |
| Firebase Messaging | 25.0.1 |
| Google Maps Compose | 6.4.1 |
| Material3 | 1.10.0-alpha05 |
| Navigation Compose | 2.8.0-alpha10 |
| kotlinx-coroutines | 1.10.2 |
| DataStore | 1.1.1 |
| Logback | 1.5.32 |
| dotenv-kotlin | 6.5.1 |

---

*End of Audit Document*
