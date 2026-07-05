# ═══════════════════════════════════════════════════════════════════════════
# GOD MODE PROMPT — ADMIN PORTAL DATA INTEGRITY & FULL-STACK VERIFICATION
# ═══════════════════════════════════════════════════════════════════════════
#
# ┌─────────────────────────────────────────────────────────────────────────┐
# │  YOU ARE THE GOD OF DATA INTEGRITY.                                     │
# │                                                                         │
# │  You don't just "write Compose code." You AUDIT the entire data chain.  │
# │  Every pixel on screen traces back to a database row through an         │
# │  unbroken chain: DB → Exposed Table → Ktor Route → DTO → KMP Repo →     │
# │  ViewModel StateFlow → Compose @Composable. If ANY link is fake,        │
# │  hardcoded, or missing, you FIND it and you FIX it.                     │
# │                                                                         │
# │  A god sees everything. You see the backend routes that have no UI.     │
# │  You see the UI screens that have no backend. You see the notification  │
# │  that doesn't deep-link. You see the onClick = { } that goes nowhere.   │
# │  You see the hardcoded list that should be an API call.                 │
# │                                                                         │
# │  Your mission:                                                          │
# │  1. ZERO HARDCODED DATA — every value flows from DB → API → ViewModel   │
# │  2. COMPLETE UI↔BACKEND MAPPING — every UI maps to a real endpoint      │
# │  3. UNIVERSAL DEEP LINKING — every notification navigates to a screen   │
# │  4. FULL-STACK AUDIT — no orphaned endpoint, no fake screen             │
# └─────────────────────────────────────────────────────────────────────────┘
#
# Sections:
#  1.  THE DATA INTEGRITY BIBLE
#  2.  BACKEND ROUTE INVENTORY
#  3.  ADMIN UI SCREEN INVENTORY
#  4.  UI ↔ BACKEND MAPPING MATRIX
#  5.  BACKEND ENDPOINTS WITHOUT FRONTEND
#  6.  UI SCREENS WITHOUT PROPER BACKEND
#  7.  NOTIFICATION DEEP-LINKING SYSTEM
#  8.  DATA INTEGRITY AUDIT RITUAL
#  9.  CRITICAL RULES — Zero Tolerance
# 10.  EXECUTION PLAN
# 11.  VERIFICATION CHECKLIST
# 12.  KEY FILE REFERENCE

# ═══════════════════════════════════════════════════════════════════════════
# 1. THE DATA INTEGRITY BIBLE — WHAT "REAL DATA" MEANS
# ═══════════════════════════════════════════════════════════════════════════
#
#  THE UNBROKEN CHAIN:
#
#    PostgreSQL/SQLite
#        ↓ Exposed ORM Table (Tables.kt — 80+ tables)
#        ↓ Ktor Route Handler (feature/*/Routing.kt)
#        ↓ DTO Serialization (@Serializable, @SerialName snake_case)
#        ↓ KMP API Client (shared/.../data/remote/*Api.kt — safeApiCall)
#        ↓ KMP Repository (shared/.../data/repository/*RepositoryImpl.kt)
#        ↓ ViewModel StateFlow (shared/.../presentation/*ViewModel.kt)
#        ↓ Compose @Composable (composeApp/.../screens/premium/school/*.kt)
#        ↓ Pixel on screen
#
#  If ANY link in this chain is broken, the data is FAKE.
#
#  THE FIVE DATA SINS (zero tolerance):
#
#  SIN 1 — HARDCODED LIST: listOf("Math","Science") in a Composable when
#    subjects should come from /api/v1/school/subjects.
#  SIN 2 — STATIC METRIC: Text("87%") that never changes — not from ViewModel.
#  SIN 3 — EMPTY CALLBACK: onClick = { } — the button looks alive but is dead.
#  SIN 4 — MISSING VIEWMODEL: Screen renders from hardcoded state, not StateFlow.
#  SIN 5 — FAKE LOADING: Thread.sleep pretending to load — data was hardcoded.
#
#  THE FOUR STATES OF TRUTH (every screen must implement all four):
#    1. LOADING — skeleton matching content shape (VShimmerCardPremium)
#    2. ERROR — icon + message + "Retry" (VStateHostPremium)
#    3. EMPTY — actionable message + button ("No teachers — Add Teacher")
#    4. LOADED — polished content, every value from state.* not a literal
#
#  THE DATA TRACE RULE:
#    For every Text(), every metric, every list item on screen, you must be
#    able to answer: "Which DTO field from which endpoint from which table
#    column does this value come from?" If you cannot answer, the data is fake.
#
#  THE MULTI-TENANCY RULE:
#    Every backend query is scoped by school_id from JWT — NEVER from request
#    body. A principal at School A must NEVER see School B's data. This is
#    enforced at the Ktor route level via requireSchoolAdmin() guard.
#    Source: docs/ECOSYSTEM_MAP.md §Cross-Cutting Concerns

# ═══════════════════════════════════════════════════════════════════════════
# 2. BACKEND ROUTE INVENTORY — Complete API Surface
# ═══════════════════════════════════════════════════════════════════════════
#
#  Every route registered in Application.kt routing{} block.
#  Source: server/src/main/kotlin/.../Application.kt lines 471-686
#
#  ──────────────────────────────────────────────────────────────────────
#  PUBLIC (no auth)
#  ──────────────────────────────────────────────────────────────────────
#  | Route Function           | Path Pattern                              |
#  |--------------------------|-------------------------------------------|
#  | landingRouting()          | / — landing page content                  |
#  | appStatusRouting()       | /api/v1/config/app-status                 |
#  | versionRouting()         | /api/v1/config/version                    |
#  | authRouting()            | /api/v1/auth/{check-user,send-otp,        |
#  |                          |   verify-otp,signup,login,refresh,        |
#  |                          |   register-school,change-password,logout} |
#  | supportRouting()         | /api/v1/support/...                       |
#
#  ──────────────────────────────────────────────────────────────────────
#  AUTHENTICATED — User & Profile
#  ──────────────────────────────────────────────────────────────────────
#  | userDetailsRouting()     | GET /api/v1/user/details                  |
#  |                          | → onboarding status, personal info,       |
#  |                          |   menu flags, theme pref                  |
#  | userProfileRouting()     | GET /api/v1/user/profile                  |
#  |                          | PUT /philosophy, /media, /visibility      |
#  | i18nRouting()            | /api/v1/user/language-pref                |
#  |                          | /api/v1/user/language-history             |
#  |                          | /api/v1/school/language-distribution      |
#  |                          | /api/v1/school/users-language-pref        |
#  |                          | /api/admin/language-adoption              |
#  |                          | /api/admin/users-by-language              |
#  |                          | /api/admin/server-strings/...             |
#
#  ──────────────────────────────────────────────────────────────────────
#  ADMIN/SCHOOL ECOSYSTEM — Core
#  ──────────────────────────────────────────────────────────────────────
#  | schoolDashboardRouting() | GET /api/v1/school/dashboard              |
#  | adminDashboardRouting()  | /api/admin/dashboard/{summary,           |
#  |                          |   analytics,activity}                     |
#  | adminDashboardOverview   | GET /api/admin/dashboard/overview         |
#  |                          | → consolidated command-center payload     |
#  | schoolIntelligenceRouting| /api/v1/school/dashboard/intelligence     |
#  |                          | → attendance timeline, anomalies,         |
#  |                          |   early-warning, academic health,         |
#  |                          |   activity feed (ALL real data)           |
#  | schoolAnalyticsRouting() | /api/v1/school/analytics/                 |
#  |                          |   {overview, class-performance,           |
#  |                          |    teacher-performance, student/{id},     |
#  |                          |    syllabus-coverage}                     |
#  | schoolProfileRouting()   | GET/PUT /api/v1/school/profile            |
#  | schoolStudentsRouting()  | /api/v1/school/students[...]              |
#  |                          | + /api/v1/school/teachers/{id}            |
#  | schoolClassesRouting()   | /api/v1/school/classes[...]               |
#  |                          | + /api/v1/school/subjects[...]            |
#  | schoolTimetableRouting() | /api/v1/school/timetable[...]             |
#  | periodExceptionRouting() | /api/v1/school/timetable/exceptions[...]  |
#  | timetableChangeRequest   | /api/v1/school/timetable-requests[...]    |
#  | schoolLessonPlanRouting()| GET /api/v1/school/lesson-plans           |
#  | schoolRecordsRouting()   | /api/v1/school/{attendance/summary,       |
#  |                          |   marks/summary,fees/ledger}              |
#  | teacherAssignmentRouting | /api/v1/school/teacher-assignments[...]   |
#  | teacherProvisioningRouting| /api/v1/school/teachers[...]             |
#  | nonTeachingStaffRouting()| /api/v1/school/staff[...]                 |
#  | schoolDayConfigRouting() | /api/v1/school/day-config                 |
#  | timetableImportRouting() | /api/v1/school/timetable-import/...       |
#  | mediaRouting()           | /api/v1/school/media/upload[...]           |
#  | brandingRouting()        | /api/v1/school/branding[...]               |
#  | syllabusPaceRouting()    | /api/v1/school/pace/                      |
#  |                          |   {snapshots,alerts,alerts/{id}/resolve}  |
#  | organizationRouting()    | /api/admin/organizations[...]              |
#  |                          | + /api/v1/organization/                   |
#  |                          |   {dashboard,branches,compare,transfers}  |
#
#  ──────────────────────────────────────────────────────────────────────
#  ACADEMIC CALENDAR & EVENTS
#  ──────────────────────────────────────────────────────────────────────
#  | academicCalendarRouting()| /api/admin/calendar/                      |
#  |                          |   {dashboard,events[...],                 |
#  |                          |    events/{id}/duplicate}                 |
#  | academicYearRouting()    | /api/admin/academic-years[...]             |
#  | eventRegistrationRouting | /api/v1/{parent,teacher,school}/events/... |
#  | ptmRouting()             | /api/v1/school/ptm                        |
#
#  ──────────────────────────────────────────────────────────────────────
#  COMMUNICATIONS
#  ──────────────────────────────────────────────────────────────────────
#  | announcementRouting()    | /api/v1/announcements[...]                |
#  | messagesRouting()        | /api/v1/school/messages[...]               |
#  | scheduledMessageRouting()| /api/v1/school/scheduled-messages         |
#  | notificationsRouting()   | /api/v1/notifications                     |
#  |                          | GET / (list), GET /summary (bell count)   |
#  |                          | PATCH /{id}/read, POST /read-all          |
#  | notificationPreferences  | /api/v1/notifications/preferences         |
#  | notificationRouting()    | /api/device-tokens (FCM register)         |
#  |                          | /api/admin/notifications/send (broadcast) |
#  | devToolsRouting()        | /api/v1/admin/dev/                        |
#  |                          |   {send-notification,trigger-pulse,       |
#  |                          |    trigger-pews,otp-providers}            |
#  | serverLogRouting()       | /api/v1/admin/dev/logs                    |
#  |                          | GET / (paginated), GET /stream (SSE)      |
#  |                          | GET /stats (aggregate stats)              |
#
#  ──────────────────────────────────────────────────────────────────────
#  AI FEATURES
#  ──────────────────────────────────────────────────────────────────────
#  | aiRouting()              | /api/v1/school/ai/usage (school-admin)    |
#  |                          | /api/v1/admin/ai/                         |
#  |                          |   {providers,health,rotate} (platform)    |
#  | featureFlagRouting()     | /api/v1/admin/flags                       |
#  | pewsRouting()            | /api/v1/{school,teacher,parent}/pews/...  |
#  | pewsModuleRouting()      | PEWS 2.0 module routes                    |
#  | reportCardRouting()      | /api/v1/report-card/                      |
#  |                          |   {generate,review-queue,drafts,          |
#  |                          |    approve,publish,published,learn/*}     |
#  | tutorRouting()           | /api/v1/tutor/                            |
#  |                          |   {doubt,practice,plan,learner-bundle,    |
#  |                          |    teacher-heatmap,...}                   |
#
#  ──────────────────────────────────────────────────────────────────────
#  OPERATIONS VERTICALS
#  ──────────────────────────────────────────────────────────────────────
#  | leaveRequestsRouting()   | /api/v1/school/leave-requests[...]        |
#  | resultsRouting()         | /api/v1/school/results                    |
#  | healthRouting()          | /api/v1/school/health/                    |
#  |                          |   {profiles,immunizations,incidents}      |
#  | idCardRouting()          | /api/v1/school/id-cards/                  |
#  |                          |   {templates,generate}                    |
#  | libraryRouting()         | /api/v1/school/library/*                  |
#  |                          | → books, issues, categories, settings,    |
#  |                          |   dashboard, audit, announcements,        |
#  |                          |   acquisitions                            |
#  | transportRouting()       | /api/v1/school/transport/                 |
#  |                          |   {routes,vehicles,assignments,           |
#  |                          |    attendance,fees}                       |
#  | scholarshipRouting()     | /api/v1/school/scholarships[...]           |
#  |                          | /api/v1/school/scholarship-applications   |
#  |                          | /api/v1/school/scholarship-renewals       |
#  | alumniRouting()          | /api/v1/school/alumni/* (admin)           |
#  |                          | /api/v1/alumni/* (self-service)           |
#  |                          | /api/v1/alumni/register (public)          |
#
#  TOTAL: ~60 routing functions, ~300+ HTTP endpoints across all roles.

# ═══════════════════════════════════════════════════════════════════════════
# 3. ADMIN UI SCREEN INVENTORY — Complete Compose + Web Surface
# ═══════════════════════════════════════════════════════════════════════════
#
#  COMPOSE APP — Admin/School Portal Screens
#  Location: composeApp/.../ui/v2/screens/premium/school/
#
#  SHELL & NAVIGATION:
#  | File                              | Purpose                          |
#  |-----------------------------------|----------------------------------|
#  | SchoolPortalPremium.kt            | Main shell, tabs, overlays,      |
#  |                                   | deep-link routing                |
#  | SchoolOverlayScaffold.kt          | Overlay scaffold (header+content)|
#  | SchoolNotificationsScreen.kt      | Admin notification inbox         |
#
#  TAB SCREENS (5 bottom-nav tabs):
#  | File                              | Tab     | Sub-tabs              |
#  |-----------------------------------|---------|-----------------------|
#  | SchoolHomePremium.kt              | Home    | (single view)         |
#  | SchoolPeoplePremium.kt            | People  | Teachers, Students,   |
#  |                                   |         | Staff                 |
#  | SchoolRecordsPremium.kt           | Records | Attendance, Marks,    |
#  |                                   |         | Fees, Pace, Coverage  |
#  | SchoolCommsPremium.kt             | Comms   | Announcements,        |
#  |                                   |         | Messages              |
#  | SchoolSettingsPremium.kt          | Settings| (single view)         |
#
#  OVERLAY SCREENS (28+ files):
#  | AcademicCalendarPlatformPremium   | Calendar management              |
#  | AcademicYearManagementPremium     | Academic year/term CRUD          |
#  | AdminEventRegistrationPremium     | Event management + slots + RSVP  |
#  | AdminReportPublishPremium         | Report card publish flow         |
#  | AdminReportingEffectivenessPremium| Report delivery stats            |
#  | AdmissionsCrmPremium              | Admissions pipeline              |
#  | AlumniCampaignPremium             | Alumni campaign details          |
#  | AlumniDetailPremium               | Alumni profile detail            |
#  | AlumniPremium                     | Alumni directory                 |
#  | AnalyticsDashboardPremium         | Analytics + charts + filters     |
#  | BrandingSettingsPremium           | Branding kit (logo, colors,      |
#  |                                   | subdomain, preview)              |
#  | ClassDetailPremium                | Class detail (students,          |
#  |                                   | subjects, timetable, teachers)   |
#  | ClassPerformancePremium           | Class performance metrics        |
#  | ClassesSubjectsPremium            | Classes, subjects, schedule,     |
#  |                                   | exceptions & requests            |
#  | DailyAttendancePremium            | Daily attendance per class       |
#  | EditSchoolProfilePremium          | School profile edit              |
#  | HealthRecordsPremium              | Health profiles + incidents      |
#  | IdCardPremium                     | ID card templates/generate/cards |
#  | LeaveRequestsPremium              | Leave request approve/reject     |
#  | LinkRequestsPremium               | Parent link approve/reject       |
#  | MessagesPremium                   | Admin messaging (threads)        |
#  | PaceAlertsPremium                 | Syllabus pace alerts + resolve   |
#  | PewsCohortPremium                 | PEWS cohort overview             |
#  | PewsEffectivenessPremium          | PEWS effectiveness (ORPHANED)    |
#  | PewsStudentDetailPremium          | PEWS student detail              |
#  | ResultsPublishPremium             | Results publish (DEAD CODE)      |
#  | SchedulePtmPremium                | PTM scheduling + booking         |
#  | SchoolDayConfigPremium            | School day configuration         |
#  | SchoolLibraryPremium              | Library management               |
#  | SchoolRecordsPremium              | Records rollups                  |
#  | TransportManagementPremium        | Transport routes/vehicles        |
#  | (+ Scholarship, ScheduledMessages,|                                  |
#  |   StudentProfile, TeacherProfile, |                                  |
#  |   TeacherAssignments, StaffProfile|                                  |
#  |   — verify existence)             |                                  |
#
#  WEB ADMIN — Next.js Pages (37 pages)
#  Location: website/src/app/admin/
#  | /admin (dashboard)            | /admin/fees                   |
#  | /admin/academics              | /admin/health-records         |
#  | /admin/admissions             | /admin/id-cards               |
#  | /admin/alumni + [id] +        | /admin/language + /strings    |
#  |   analytics + campaigns       | /admin/leave + /leave-requests|
#  | /admin/announcements          | /admin/library                |
#  | /admin/attendance             | /admin/link-requests          |
#  | /admin/branding               | /admin/logs                   |
#  | /admin/calendar               | /admin/marks                  |
#  | /admin/classes                | /admin/messages               |
#  | /admin/dashboard              | /admin/pace-alerts            |
#  | /admin/dev-tools              | /admin/people                 |
#  | /admin/early-warning          | /admin/ptm                    |
#  | /admin/events                 | /admin/report-card            |
#  |                               | /admin/scheduled-messages     |
#  |                               | /admin/scholarships           |
#  |                               | /admin/settings               |
#  |                               | /admin/transport              |
#  |                               | /admin/tutor                  |
#
#  TOTAL COMPOSE: ~36 files | TOTAL WEB: 37 pages | TOTAL BACKEND: ~300+ endpoints
