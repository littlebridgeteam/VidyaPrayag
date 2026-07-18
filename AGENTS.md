# AGENTS.md — Vidya Prayag (Enroll+)

## Project Overview

Vidya Prayag (branded as **Enroll+** / **EnRollPlus**) is a full-stack **Kotlin Multiplatform** school management platform targeting Android, iOS, Desktop (JVM), Web (Wasm/JS), and a Ktor backend server, plus a Next.js marketing website.

- **Package**: `com.littlebridge.enrollplus`
- **Org**: `littlebridge`
- **Deployment**: Render (PostgreSQL + Ktor + Next.js)

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| UI | Compose Multiplatform | 1.10.3 |
| Language | Kotlin | 2.2.10 |
| Backend | Ktor (Netty) | 3.4.3 |
| ORM | JetBrains Exposed | 0.50.0 |
| Database | PostgreSQL (prod), SQLite (dev) | — |
| DI | Koin | 4.0.0 |
| Marketing Site | Next.js + React 18 + Tailwind | 14.2.35 |
| Android SDK | compileSdk 36, minSdk 24 | AGP 9.2.1 |
| Build | Gradle (Kotlin DSL) | — |
| Migrations | Flyway | 11.1.0 |

## Architecture

### Client-side (composeApp + shared)
- **MVVM + Clean Architecture** with feature-based modularization (24 feature modules)
- Each feature: `data/remote/` → `data/repository/` → `domain/repository/` → `domain/usecase/` → `presentation/ViewModel`
- **Koin DI** — all modules registered in `shared/di/Koin.kt`
- **No Jetpack Navigation** — hand-rolled `AnimatedContent` state machine (`NavGraphV2`)
- **Session-scoped ViewModelStore** — prevents ViewModel bleed across logout/re-login
- **Role-based navigation**: Admin, Teacher, Parent portals

### Server-side (server/)
- **Ktor plugin-based** modular architecture (41 feature modules, 22 core modules)
- **Exposed ORM** — 150+ tables, 4760 lines in `Tables.kt`
- **JWT auth** with refresh token rotation (30-day refresh, 1-24hr access)
- **Background jobs**: notifications, PEWS, pulse, report cards, transport, library, exams, skill tests
- **Graceful shutdown**: SIGTERM handler cancels jobs before closing HikariCP

## Directory Structure

```
Vidya Prayag/
├── composeApp/           # Compose Multiplatform UI
│   └── src/
│       ├── commonMain/   # Shared UI (all platforms)
│       │   └── com/littlebridge/enrollplus/
│       │       ├── App.kt              # Root composable + SessionScope
│       │       ├── ui/v2/              # PRIMARY DESIGN SYSTEM (V2)
│       │       │   ├── components/     # 30 reusable components
│       │       │   ├── navigation/     # NavGraphV2 (role-based state machine)
│       │       │   ├── screens/        # 153 screens across portals
│       │       │   │   ├── auth/       # Login, signup, link-child, first-login
│       │       │   │   ├── discovery/  # School discovery, academic calendar
│       │       │   │   ├── library/    # Library UI components
│       │       │   │   ├── notifications/
│       │       │   │   ├── parent/     # 42 parent portal screens
│       │       │   │   ├── school/     # 55 school admin screens
│       │       │   │   ├── teacher/    # 35+ teacher portal screens
│       │       │   │   ├── student/    # Student library
│       │       │   │   └── tutor/      # AI tutor screens
│       │       │   ├── theme/          # VTheme, VThemeRegistry, BrandingColorMapper
│       │       │   └── tokens/         # VColors, VMotion, VDimens, VType
│       │       └── platform/           # Platform-specific code
│       ├── androidMain/  # Android (Koin Android module, FCM, SplashScreen)
│       ├── iosMain/      # iOS (Darwin HTTP client)
│       ├── jvmMain/      # Desktop JVM (OkHttp, sqlite-bundled)
│       ├── jsMain/       # JavaScript target
│       └── wasmJsMain/   # Wasm JS target
│
├── shared/               # KMP shared business logic
│   └── src/
│       ├── commonMain/   # 24 feature modules + core services
│       │   └── com/littlebridge/enrollplus/
│       │       ├── core/              # Cache, network, locale, prefs, state
│       │       ├── di/                # Koin.kt (all DI modules)
│       │       ├── feature/           # 24 feature modules
│       │       │   ├── auth/          # OTP + JWT authentication
│       │       │   ├── admin/         # School admin features
│       │       │   ├── teacher/       # Teacher vertical
│       │       │   ├── parent/        # Parent ecosystem
│       │       │   ├── exam/          # Exam ecosystem
│       │       │   ├── library/       # Library management
│       │       │   ├── transport/     # GPS bus tracking
│       │       │   ├── pews/          # Predictive Early Warning
│       │       │   ├── reportcard/    # AI Report Card 2.0
│       │       │   ├── tutor/         # AI Tutor 2.0
│       │       │   ├── gamification/  # XP, badges, levels
│       │       │   ├── scholarship/   # Scholarship workflow
│       │       │   ├── alumni/        # Alumni management
│       │       │   ├── health/        # Student health records
│       │       │   ├── idcard/        # ID card generation
│       │       │   ├── branding/      # School branding/theming
│       │       │   ├── export/        # PDF/CSV exports
│       │       │   ├── notification/  # Push notification foundation
│       │       │   ├── scheduling/    # Message scheduling
│       │       │   ├── event/         # Event registration & RSVP
│       │       │   ├── schools/       # School discovery marketplace
│       │       │   ├── i18n/          # Multi-language support
│       │       │   ├── content/       # Landing page content
│       │       │   └── school/        # Shared school domain models
│       │       └── presentation/      # MainViewModel, PermissionViewModel
│       └── roomMain/     # Room/SQLite cache (announcements, events, library, schools, teachers)
│
├── server/               # Ktor backend
│   └── src/main/kotlin/com/littlebridge/enrollplus/
│       ├── Application.kt         # Entry point + all route wiring
│       ├── core/                  # 22 modules (JWT, CORS, CSRF, error handling, etc.)
│       ├── db/                    # DatabaseFactory, Tables.kt (150+ tables), Seed
│       └── feature/               # 41 feature modules (auth, admin, teacher, parent, etc.)
│
├── website/              # Next.js marketing site (src/app/, src/components/)
├── iosApp/               # iOS Xcode project shell
├── database/migrations/  # 15 SQL migration files
├── scripts/              # Build/deploy scripts
├── brand-assets/         # Logos, brand assets
├── docs/                 # Documentation
├── docker-compose.yml    # Full stack: PostgreSQL + Ktor + Next.js
├── Dockerfile            # Server multi-stage build
└── .github/workflows/    # CI + Release APK GitHub Actions
```

## App Flow

### 1. Launch → Splash → Auth Decision
```
App.kt (root composable)
  → KoinContext (DI)
  → MainViewModel (app-lifetime, never torn down)
  → Splash screen (1600ms minimum)
  → Auth check via StateFlow<UserSession>
    → Has token? → SessionScope(key=JWT) → NavGraphV2
    → No token? → AuthNavGraph
```

### 2. Unauthenticated Flow (AuthNavGraph)
```
LandingScreen (HorizontalPager — 2 slides)
  ├─ "For Parents" → ParentLogin → OTP verify → onAuthSuccess
  └─ "For Schools" → StaffLogin (email+password) → onAuthSuccess
                     → SchoolRegistration (signup) → onAuthSuccess
```

### 3. Post-Auth Gate (NavGraphV2)
```
EntryRole.from(role) → typed enum
  → Theme resolution (VTheme + school branding)
  → Deep link parsing (vidyaprayag://app/<path>)

AuthedFlow decision:
  Parent       → ParentPortalV2 (direct)
  Teacher      → profileCompleted? → Portal : TeacherFirstLoginScreenV2 → Portal
  SchoolAdmin  → OnboardingGateViewModel (server truth) → Portal
  SuperAdmin   → same as SchoolAdmin
  Alumni       → ParentPortalV2 (fallback)
```

### 4. Three Role Portals

**SchoolPortalV2** — 5 tabs + ~40 overlays
- Home, People, Records, Comms, Settings
- Dashboard, admissions CRM, announcements, attendance, branding, classes, events, exams, fees, gamification, health, ID cards, leave, library, messages, PEWS, report cards, results, scholarships, staff, student roster, timetable, transport

**TeacherPortalV2** — 5 tabs + ~25 overlays
- Home, Update, Classes, Timetable, Profile
- Today view, attendance, check-in, gradebook, homework, lesson plans, leave, messages, PEWS, profile, salary, report drafting, syllabus, timetable, transport attendance, quizzes, fee escalation

**ParentPortalV2** — 5 tabs + ~30 overlays
- Home, Academics, Fees, Conversations, Profile
- Dashboard, attendance, bus tracking, conversations, digital ID, exams, events, fees, gamification, health, homework, leave, library, messages, notifications, PEWS, profile, pulse, report cards, scholarships, skill tests, tutor

### 5. School Dashboard UI Flow (SchoolPortalV2)

#### Portal Shell (SchoolPortalV2.kt)
- **Bottom Nav**: Home | People | Records | Comms | Settings (`VCreamBottomNav`)
- **Overlay state machine**: `SchoolOverlay` enum (40+ full-screen overlays)
- **Comms badge**: Real unread thread count from `MessagesViewModel`
- **Back handler**: Overlay → pop to tabs; root → no-op (prevents app exit)
- **Deep links**: `vidyaprayag://app/<path>` parsed per overlay

#### Tab 1: Home (SchoolHomeScreenV2.kt — 1699 lines)
```
CommandDesk layout (vertical scroll):
  1. HomeHeader — hamburger menu, greeting, school name, bell (unread badge), avatar
  2. SearchBar — opens CommandPalette
  3. TodayOverviewCard — purple gradient, AI daily digest, task actions
     → Pending Notifications → Notifications overlay
     → Mark Attendance → PEWS overlay
     → View All Tasks → CommandPalette
  4. QuickActionsGrid (2×2):
     → Announce → CreateEvent overlay (pre-selects "Update" type)
     → Add Event → CreateEvent overlay
     → Reports → ReportPublish overlay
     → Calendar → AcademicCalendarPlatform overlay
  5. StatsRow — Students/Teachers KPI cards with sparkline charts → Analytics
  6. AttendanceFeesRow — circular progress cards → Analytics
  7. AttentionSection — PEWS at-risk items (sorted by severity) → PEWS Cohort
  8. FeeEventsRow — Fee Collection card + Upcoming Events card
  9. RecentActivitySection — activity feed
  10. SchoolHealthBar — overall school pulse → Analytics
```

#### Tab 2: People (SchoolPeopleScreenV2.kt — 1312 lines)
```
3-sub-tab HorizontalPager:
  Teachers → SchoolTeachersViewModel → tap opens TeacherProfile overlay
    → overflow: "Assign Classes" → TeacherAssignments overlay
  Students → StudentRosterViewModel → tap opens StudentProfile overlay
    → overflow: message / graduate to alumni
  Staff    → StaffViewModel → tap opens StaffProfile overlay
Top entry: Link Requests banner → LinkRequests overlay
```

#### Tab 3: Records (SchoolRecordsScreenV2.kt — 846 lines)
```
6-sub-tab VTopTabs:
  Coverage   → SyllabusCoverageViewModel → dept progress, lagging alerts
  Pace       → PaceAlertsViewModel → syllabus pace alerts, resolve/recalculate
  Attendance → SchoolRecordsViewModel → school-wide attendance summary
  Marks      → SchoolRecordsViewModel → school-wide marks summary
  Fee        → SchoolRecordsViewModel → fee ledger, collection stats
  Documents  → VComingSoon (Phase D/E)
```

#### Tab 4: Comms (SchoolCommsScreenV2.kt — 523 lines)
```
4-sub-tab HorizontalPager:
  Announcements → SchoolAnnouncementsViewModel → category filter → detail
  Messages      → entry card → Messages overlay
  PTM           → entry card → SchedulePTM overlay
  Notifications → VComingSoon (Phase D/E)
```

#### Tab 5: Settings (SchoolSettingsScreenV2.kt — 653 lines)
```
Telegram-style collapsing header:
  School Profile header → EditProfile overlay
  Quick access grid:
    → Classes & Subjects → ClassesSubjects overlay
    → Academic Year → AcademicYear overlay
    → Transport → TransportManagement overlay
    → Scholarships → ScholarshipManagement overlay
    → Branding → BrandingKit overlay
    → ID Cards → IdCards overlay
    → Library → Library overlay
    → Gamification → GamificationManagement overlay
    → Fee & Salary → FeeSalaryManagement overlay
  Preferences: Theme picker, Language picker
  Logout button
```

#### Full Overlay Map (SchoolOverlay enum)
```
Notifications, Calendar, AcademicCalendarPlatform, CreateEvent, AcademicYear,
Messages, LeaveRequests, LinkRequests, AdmissionsCRM, Results, SchedulePTM,
DeliveryLog, DailyAttendance, ClassPerformance, TeacherPerformance,
AnalyticsDashboard, EditProfile, StudentRoster, StudentProfile,
PewsCohort, PewsStudentDetail, TeacherProfile, TeacherAssignments,
Staff, HealthRecords, Alumni, AlumniDetail, AlumniCampaign,
TransportManagement, ReportPublish, ReportEffectiveness,
ScholarshipManagement, BrandingKit, IdCards, Library, ScheduledMessages,
EventRegistration, ClassesSubjects, ClassDetail, GamificationManagement,
NotificationPreferences, FeeSalaryManagement
```

## Build & Run

### APK (Build + Install + Launch on All Devices)
```bash
# Staging flavor (connects to staging server)
./scripts/buildStag.sh

# Dev flavor (connects to local/dev server)
./scripts/build-apk.sh
```

### Server
```bash
# Full build + run
./gradlew :server:run

# Server-only mode (skips KMP/AGP/Compose — faster)
./gradlew -Pserver-only=true :server:run
```

### Other Platforms
```bash
# Desktop (JVM)
./gradlew :composeApp:run

# Web (Wasm — modern browsers)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Web (JS — older browsers)
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### Testing
```bash
# Server tests
./gradlew :server:test

# Shared module tests
./gradlew :shared:test
```

### Linting
```bash
# Detekt static analysis (server module)
./gradlew :server:detekt
```

## Key Design Decisions

1. **No NavHost/NavController** — All navigation is state-driven via `AnimatedContent` + mutable state enums
2. **Session-scoped ViewModelStore** — `SessionScope` composable keyed on JWT token prevents cross-session bleed
3. **App-lifetime MainViewModel** — Resolved above SessionScope, drives `authState` reactively across logins
4. **Server-truth onboarding** — Admins use `GET /api/v1/onboarding/status` (DB-derived) instead of local flag
5. **Law 4: No back to auth/splash** — Back-press at portal root is a no-op; once authenticated, never back-navigate to auth
6. **Overlay pattern** — Tabs + full-screen overlays replace navigation stacks
7. **Dual deep link parsers** — Legacy (`DeepLinkRouter.kt`) + comprehensive (`NavGraphV2.kt`)

## Auth Flow (JWT)

```
1. POST /api/v1/auth/check-user → determines OTP vs PASSWORD
2a. OTP: POST /api/v1/auth/send-otp → POST /api/v1/auth/verify-otp
2b. PASSWORD: skip to login
3. POST /api/v1/auth/login → access_token + refresh_token
4. POST /api/v1/auth/refresh → rotation (new access + new refresh, old revoked)
5. Client: TokenAuthenticator (401 retry) + SilentTokenRefreshManager (proactive refresh)
```

- Access token: 1hr (admin) / 24hr (others)
- Refresh token: 30 days with rotation + reuse detection
- Password: PBKDF2-HMAC-SHA256 (auto-upgrades legacy hashes)

## Database

- **Production**: PostgreSQL (Supabase-hosted) via JDBC + HikariCP
- **Local dev**: SQLite (`data.db` in CWD) with auto-schema
- **ORM**: Jetbrains Exposed (150+ tables)
- **Migrations**: Flyway (`database/migrations/`)
- **Key domains**: auth, users, schools, students, teachers, attendance, marks, fees, exams, timetable, transport, library, gamification, PEWS, report cards, tutor, health, scholarships, alumni, notifications, messaging, events, branding, ID cards

## Background Jobs (Server)

| Job | Schedule | Purpose |
|---|---|---|
| NotificationScheduler | Every 1hr | Fee reminders, calendar event reminders |
| MessageDispatchScheduler | Every 1min | Scheduled message dispatch |
| PulseWeeklyJob | Sunday 6PM IST | Weekly AI parent digest |
| PewsDailyJob | Daily 00:00 UTC | Full PEWS pipeline (Sense→Triage→Caseworker→Act→Learn) |
| ExamReminderJob | Daily 6-7PM IST | Exam reminder push notifications |
| TransportJobScheduler | At boot | GPS staleness check, attendance finalization |
| LibraryJobScheduler | At boot | Overdue notifications, reservation expiry |
| SkillTestJobScheduler | At boot | Weekly AI question generation |
| ReportCardJob | At boot | Async batch AI report card generation |

## Conventions

- **Language**: Kotlin (2.2.10), no Java
- **UI**: 100% Compose Multiplatform, no XML layouts
- **Design system**: V2 (`ui/v2/`) with custom tokens (VColors, VDimens, VMotion, VType)
- **Theming**: Role-specific themes + school branding via `BrandingThemeManager`
- **Naming**: PascalCase for composables, camelCase for functions/variables
- **Comments**: Do not add comments unless explicitly asked
- **File organization**: Feature-based modules with data/domain/presentation layers
- **Serialization**: Kotlinx Serialization JSON throughout
- **HTTP**: Ktor Client (OkHttp on Android/JVM, Darwin on iOS)
- **Images**: Coil 3 with 512MB disk cache
- **Offline**: Room cache (announcements, events, library, schools, teachers) + CacheManager

## Environment Variables (Server)

| Variable | Purpose | Default |
|---|---|---|
| `DATABASE_URL` | PostgreSQL JDBC URL | SQLite fallback |
| `DB_POOL_SIZE` | HikariCP pool size | 10 |
| `JWT_SECRET` | HMAC256 signing key | Random (dev) |
| `JWT_ISSUER` | Token issuer | `vidyaprayag-api` |
| `JWT_AUDIENCE` | Token audience | `vidyaprayag-app` |
| `CORS_ALLOWED_ORIGINS` | Allowed origins | `anyHost()` (dev) |
| `OTP_GATEWAY_TOKEN` | OTP sender auth | — |
| `OTP_ADMIN_TOKEN` | OTP admin ops | — |
| `PEWS_ENABLED` | Enable PEWS pipeline | `false` |

## CI/CD

- **ci.yml**: Server build+test, shared module build+test, Android compile check
- **release-apk.yml**: Builds APK on push to `main`/`development_v1.0.0`, uploads to GitHub Artifacts + Release + Slack
