# Parent Portal — In-Depth Data Review & API Mapping

> Identify hardcoded data, features with backend but no screens, and map endpoints to UI.

---

## 1. Architecture Overview

```
UI Screen (Compose) → ViewModel (StateFlow) → Repository → API (Ktor) → Server Route → Database
```

| Layer | Path |
|-------|------|
| UI Screens | `composeApp/.../ui/v2/screens/parent/` |
| ViewModels | `shared/.../feature/parent/presentation/` |
| API Interface | `shared/.../feature/parent/data/remote/ParentApi.kt` |
| Repository | `shared/.../feature/parent/domain/repository/ParentRepository.kt` |
| Repo Impl | `shared/.../feature/parent/data/repository/ParentRepositoryImpl.kt` |
| Server Routes | `server/.../feature/parent/` + `server/.../feature/user/` |

### Tab Structure

| Tab | Screen | ViewModel(s) |
|-----|--------|-------------|
| Home | `ParentHomeScreenV2.kt` | `ParentDashboardVM`, `ParentAcademicsVM`, `ParentAnnouncementVM`, `TrackProgressVM`, `TransportVM` |
| Academics | `ParentAcademicsScreenV2.kt` | `TrackProgressVM`, `ParentAcademicsVM` |
| Fees | `ParentFeesScreenV2.kt` | `FeeVM` |
| Conversations | `ParentConversationsScreenV2.kt` | `ParentMessageVM` |
| Profile | `ParentProfileScreenV2.kt` + `ParentProfileCardScreenV2.kt` | `ParentProfileVM`, `ParentDashboardVM`, `ParentAcademicsVM`, `TrackProgressVM` |

### Overlay Screens

Notifications, Calendar, Scholarships, Leave, Messages, LinkChild, Discovery, Health, Pulse, Transport, TutorChat, TutorProgress, DigitalIdCard, Library, EventRegistration, FeePayment, FeeHistory, ReportCard, PEWS.

---

## 2. Hardcoded Data Inventory

### 2.1 — ParentHomeScreenV2.kt

| ID | Line | Hardcoded Value | Should Come From | Severity |
|----|------|----------------|-----------------|----------|
| H-01 | 643 | `"Bus arriving soon"` | `TransportVM` state (ETA, route status) | **HIGH** |
| H-02 | 648 | `"GPS tracking active"` | `TransportVM` state (tracking enabled flag) | **HIGH** |
| H-03 | 669 | `listOf("All","Academics","Fees","Attendance","Transport","Library")` | School feature flags + `StringKeys` | **MEDIUM** |
| H-04 | 384 | `val unreadMessages = 0` (TODO comment) | `ParentMessageVM.state` unread count | **HIGH** |
| H-05 | 769 | `"Q4 Tuition · Due soon"` subtitle | `FeeData` period name + due date | **MEDIUM** |
| H-06 | 1265 | `"School Admin"` as announcement author | `ParentAnnouncement.author` from backend | **MEDIUM** |
| H-07 | 1336 | `"AI Report"` button → `onOpenScholarships` | Should open report card overlay | **HIGH** — wrong nav |
| H-08 | 1343 | `"PEWS"` button → `onOpenIdCard` | Should open PEWS overlay | **HIGH** — wrong nav |
| H-09 | 294-348 | Error/empty state strings | `StringKeys` | LOW |
| H-10 | 419-442 | Section headers: "Priority", "Today's Schedule", "Today's Summary", "School Updates", "Premium Features" | `StringKeys` | LOW |
| H-11 | 976-978 | "Live", "Done", "Upcoming" | `StringKeys` | LOW |
| H-12 | 1327-1348 | "AI Tutor", "AI Report", "PEWS", "Library" labels | `StringKeys` | LOW |

### 2.2 — ParentAcademicsScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| A-01 | 189 | `listOf("Overview","Attendance","Marks","Syllabus","Quizzes","Homework")` | `StringKeys` + school config | MEDIUM |
| A-02 | 1359 | `subjectPalette` — 4 hardcoded colors | School/subject config | LOW |

### 2.3 — ParentPortalV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| P-01 | 336 | `onPay = { /* TODO */ }` | Wire to `FeeVM.payFee()` → `POST /api/v1/parent/fees/pay` | **HIGH** |
| P-02 | 350-355 | 5 nav items hardcoded | Could use school feature flags | LOW |

### 2.4 — ParentProfileCardScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| PC-01 | 441 | `"↑ 2% this month"` attendance trend | Backend delta or remove | **HIGH** — fabricated |
| PC-02 | 448 | `"↑ 5% this term"` marks trend | Backend delta or remove | **HIGH** — fabricated |
| PC-03 | 457 | `"↑ ${...} this week"` XP trend | Backend delta or remove | **HIGH** — fabricated |
| PC-04 | 464 | `"↑ 3 this week"` quizzes trend | Backend delta or remove | **HIGH** — fabricated |
| PC-05 | 318 | `val xpMax = 5000` | `TrackProgressState` from backend | MEDIUM |
| PC-06 | 378 | `"🏆 Level $level Scholar"` | `heroSection.levelLabel` from backend | MEDIUM |
| PC-07 | 365 | `"Student"` label | `DashboardChildSummary` role/grade | LOW |

### 2.5 — ParentFeesScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| F-01 | 245 | `"Collected this term"` | `FeeData.termName` from backend | MEDIUM |
| F-02 | 116-131 | `"Pay\nNow"`, `"Fee\nHistory"` | `StringKeys` | LOW |
| F-03 | 141 | `"Overview"` tab | `StringKeys` | LOW |
| F-04 | 158-165 | Error/empty state strings | `StringKeys` | LOW |

### 2.6 — ParentFeePaymentScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| FP-01 | 134 | `"Secure Razorpay gateway"` | School payment gateway config | MEDIUM |
| FP-02 | 60-107 | Various labels | `StringKeys` | LOW |

### 2.7 — ParentFeeHistoryScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| FH-01 | entire | **Always shows empty state** — no history API exists | Add `GET /api/v1/parent/fees/history` endpoint + wire VM | **HIGH** — non-functional |

### 2.8 — ParentProfileScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| PR-01 | 194 | `"Notification preferences"` — `onClick = null` | Create notification settings screen | **HIGH** — dead tap |
| PR-02 | 195 | `"Change password"` — `onClick = null` | Create change password screen | **HIGH** — dead tap |
| PR-03 | 199 | `"support@vidyaprayag.in"` | School config/branding | MEDIUM |

### 2.9 — ParentLibraryScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| L-01 | 67-69 | `ParentLibraryTab` enum labels | Use `StringKeys` for display labels | LOW |
| L-02 | greeting | `appString(StringKeys.PL_PARENT)` fallback "Parent" | Use `TrackProgressVM.accountName` | MEDIUM |

### 2.10 — ParentEventRegistrationScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| E-01 | 74 | `"Event Detail"`, `"Events"` | `StringKeys` | LOW |
| E-02 | 134-139 | `"Upcoming Events"`, `"My Registrations"` | `StringKeys` | LOW |
| E-03 | 152 | `"No upcoming events with registration"` | `StringKeys` | LOW |
| E-04 | 217-442 | Various labels: "Venue:", "Register by:", "Registration open", etc. | `StringKeys` | LOW |

### 2.11 — ParentPewsScreenV2.kt

| ID | Line | Hardcoded Value | Fix | Severity |
|----|------|----------------|-----|----------|
| PW-01 | 94-95 | `"All good!"`, empty state body | `StringKeys` | LOW |
| PW-02 | 180-185 | `"All on track"`, all-clear body | `StringKeys` | LOW |

### 2.12 — Clean Screens (No Hardcoded Data)

- `ParentPulseScreen.kt` — all data from `ParentPulseVM`
- `ParentHealthScreenV2.kt` — all data from `ParentHealthVM`
- `BusTrackingScreenV2.kt` — all data from `TransportVM`
- `DigitalIdCardScreen.kt` — all data from `IdCardVM`
- `ParentReportScreen.kt` — all data from `ParentReportVM`
- `ParentLeaveScreenV2.kt` — all data from `ParentLeaveVM`
- `ParentConversationsScreenV2.kt` — all data from `ParentMessageVM`

---

## 3. Features With Backend But No/Incomplete Screens

### 3.1 — Fee Payment Gateway

- **Backend**: `POST /api/v1/parent/fees/pay` exists (`ParentFeesRouting.kt`, `PayFeeRequest` DTO)
- **API/Repo**: `ParentApi.payFee()`, `ParentRepository.payFee()` defined
- **Screen**: `ParentFeePaymentScreenV2.kt` — `onPay` is `/* TODO */`
- **Fix**: Wire `onPay` → `FeeVM.payFee()`, add payment gateway SDK, handle success/failure

### 3.2 — Fee History

- **Backend**: No `GET /api/v1/parent/fees/history` endpoint
- **Screen**: `ParentFeeHistoryScreenV2.kt` always shows empty state
- **Fix**: Add server endpoint, API method, VM call, render transaction list

### 3.3 — Notification Preferences

- **Backend**: Notification system exists
- **Screen**: `ParentProfileScreenV2` row "Notification preferences" → `onClick = null`
- **Fix**: Create settings screen + `GET/PUT /api/v1/parent/notification-preferences`

### 3.4 — Change Password

- **Screen**: `ParentProfileScreenV2` row "Change password" → `onClick = null`
- **Fix**: Create change password screen + `POST /api/v1/auth/change-password`

### 3.5 — Track Progress (Partial Rendering)

- **Backend**: `GET /api/v1/parent/track-progress` returns `academicCore.competencies`, `emotionalIntelligence` (description + metrics), `playDiscovery` indicators
- **VM**: `TrackProgressVM` fetches ALL data into state
- **Screen**: `ParentProfileCardScreenV2` only shows level, progress, badges, XP
- **Missing**: Academic competencies, EI metrics, play indicators — fetched but never rendered
- **Fix**: Add sections to Profile or Academics Overview for these 3 data sets

### 3.6 — Unread Messages on Home

- **Backend**: `GET /api/v1/parent/messages/threads` returns `unreadCount`
- **VM**: `ParentMessageVM` holds thread data
- **Screen**: `ParentHomeScreenV2` line 384: `val unreadMessages = 0 // TODO`
- **Fix**: Inject `ParentMessageVM` into home, compute total unread

### 3.7 — Transport Card on Home

- **VM**: `TransportVM` injected, `loadChildRoute()` called
- **Screen**: `TransportTrackingCard` shows hardcoded "Bus arriving soon" / "GPS tracking active"
- **Fix**: Pass `TransportVM.state` to card — show real ETA, route name, tracking status

### 3.8 — Premium Features Grid Wrong Wiring

- **Screen**: `ParentHomeScreenV2` lines 1336, 1343
- "AI Report" → `onOpenScholarships` (wrong), "PEWS" → `onOpenIdCard` (wrong)
- **Fix**: Add `onOpenReportCard` and `onOpenPews` callbacks, wire in `ParentPortalV2`

### 3.9 — Announcement Author

- **Screen**: `UpdateItem` line 1265 always shows `"School Admin"`
- **Fix**: Add `author`/`source` to announcement DTO, use in UI

### 3.10 — Stats Trend Data (Fabricated)

- **Screen**: `ParentProfileCardScreenV2` `StatsGrid` — all 4 trend strings are fabricated
- **Fix**: Remove fabricated trends immediately. Add backend deltas later.

---

## 4. Complete API → Screen Mapping

### ParentApi Endpoints

| # | API Method | HTTP | Endpoint | ViewModel | Screen | Status |
|---|-----------|------|----------|-----------|--------|--------|
| 1 | `getDashboard()` | GET | `/api/v1/parent/dashboard` | `ParentDashboardVM.load()` | Home | ✅ |
| 2 | `getFees(token, childId)` | GET | `/api/v1/parent/fees?childId={id}` | `FeeVM` | Fees tab | ✅ |
| 3 | `payFee(...)` | POST | `/api/v1/parent/fees/pay` | `FeeVM.payFee()` | FeePayment overlay | ⚠️ TODO |
| 4 | `getScholarships(token)` | GET | `/api/v1/parent/scholarships` | `ScholarshipVM` | Scholarships overlay | ✅ |
| 5 | `getAnnouncements(token)` | GET | `/api/v1/parent/announcements` | `ParentAnnouncementVM` | Home updates + Conversations | ✅ |
| 6 | `getNotifications(...)` | GET | `/api/v1/parent/notifications` | `NotificationsVM` | Notifications overlay | ✅ |
| 7 | `getChildAttendance(...)` | GET | `/api/v1/parent/child/{id}/attendance` | `ParentDashboardVM` + `ParentAcademicsVM` | Home hero + Academics Attendance | ✅ |
| 8 | `getChildMarks(...)` | GET | `/api/v1/parent/child/{id}/marks` | `ParentDashboardVM` + `ParentAcademicsVM` | Home hero + Academics Marks | ✅ |
| 9 | `getChildSyllabus(...)` | GET | `/api/v1/parent/child/{id}/syllabus` | `ParentDashboardVM` + `ParentAcademicsVM` | Home covered-today + Academics Syllabus | ✅ |
| 10 | `getChildTimetable(...)` | GET | `/api/v1/parent/child/{id}/timetable` | `ParentDashboardVM` | Home schedule card | ✅ |
| 11 | `getDailySummary(...)` | GET | `/api/v1/parent/child/{id}/daily-summary` | `ParentAcademicsVM` | Home summary + Academics Overview | ✅ |
| 12 | `getSyllabusV2(...)` | GET | `/api/v1/parent/child/{id}/syllabus-v2` | `ParentAcademicsVM` | Academics Syllabus tab | ✅ |
| 13 | `getQuizzes(...)` | GET | `/api/v1/parent/child/{id}/quizzes` | `ParentAcademicsVM` | Academics Quizzes tab | ✅ |
| 14 | `getQuizDetail(...)` | GET | `/api/v1/parent/child/{id}/quizzes/{quizId}` | `ParentAcademicsVM` | Quiz detail dialog | ✅ |
| 15 | `submitQuiz(...)` | POST | `/api/v1/parent/child/{id}/quizzes/{quizId}/submit` | `ParentAcademicsVM` | Quiz submission | ✅ |
| 16 | `getQuizResult(...)` | GET | `/api/v1/parent/child/{id}/quizzes/{quizId}/result` | `ParentAcademicsVM` | Quiz result view | ✅ |
| 17 | `getLeaderboard(...)` | GET | `/api/v1/parent/child/{id}/quizzes/{quizId}/leaderboard` | `ParentAcademicsVM` | Quiz leaderboard | ✅ |
| 18 | `getTrackProgress(token)` | GET | `/api/v1/parent/track-progress` | `TrackProgressVM` | Profile card + Academics Overview | ⚠️ Partial |
| 19 | `searchSchools(...)` | GET | `/api/v1/parent/schools/search` | `DiscoveryVM` | Discovery overlay | ✅ |
| 20 | `linkChild(...)` | POST | `/api/v1/parent/link-child` | Auth flow | LinkChild overlay | ✅ |
| 21 | `getMessageThreads(...)` | GET | `/api/v1/parent/messages/threads` | `ParentMessageVM` | Conversations tab | ✅ |
| 22 | `getMessages(...)` | GET | `/api/v1/parent/messages/threads/{threadId}` | `ParentMessageVM` | Conversation detail | ✅ |
| 23 | `markThreadRead(...)` | POST | `/api/v1/parent/messages/threads/{threadId}/read` | `ParentMessageVM` | Thread open | ✅ |
| 24 | `sendMessage(...)` | POST | `/api/v1/parent/messages/threads/{threadId}` | `ParentMessageVM` | Compose/send | ✅ |
| 25 | `getPulse(...)` | GET | `/api/v1/parent/pulse/{childId}` | `ParentPulseVM` | Pulse overlay | ✅ |
| 26 | `getPulseHistory(...)` | GET | `/api/v1/parent/pulse/{childId}/history` | `ParentPulseVM` | Pulse history | ✅ |
| 27 | `getLeaveRequests(...)` | GET | `/api/v1/parent/leave` | `ParentLeaveVM` | Leave overlay | ✅ |
| 28 | `applyLeave(...)` | POST | `/api/v1/parent/leave` | `ParentLeaveVM` | Leave form | ✅ |

### Cross-Feature APIs

| # | Feature | API | HTTP | Endpoint | ViewModel | Screen | Status |
|---|---------|-----|------|----------|-----------|--------|--------|
| 29 | Health | `HealthApi` | GET | `/api/v1/health/{childId}` | `ParentHealthVM` | Health overlay | ✅ |
| 30 | Transport | `TransportApi` | GET | `/api/v1/transport/child/{childId}/route` | `TransportVM` | BusTracking overlay | ✅ |
| 31 | Transport | `TransportApi` | GET | `/api/v1/transport/{routeId}/live` | `TransportVM` | BusTracking overlay | ✅ |
| 32 | ID Card | `IdCardApi` | GET | `/api/v1/idcard/{childId}` | `IdCardVM` | DigitalIdCard overlay | ✅ |
| 33 | Report Card | `ReportCardApi` | GET | `/api/v1/report-card/parent/{childId}` | `ParentReportVM` | ReportScreen | ✅ |
| 34 | Report Card | `ReportCardApi` | GET | `/api/v1/report-card/parent/{childId}/conference-pack` | `ParentReportVM` | ReportScreen conference card | ✅ |
| 35 | Events | `EventRegistrationApi` | GET | `/api/v1/events/parent` | `ParentEventRegVM` | EventRegistration overlay | ✅ |
| 36 | Events | `EventRegistrationApi` | GET | `/api/v1/events/parent/{eventId}` | `ParentEventRegVM` | Event detail | ✅ |
| 37 | Events | `EventRegistrationApi` | POST | `/api/v1/events/parent/{eventId}/register` | `ParentEventRegVM` | Register button | ✅ |
| 38 | Events | `EventRegistrationApi` | POST | `/api/v1/events/parent/{eventId}/cancel` | `ParentEventRegVM` | Cancel button | ✅ |
| 39 | Events | `EventRegistrationApi` | POST | `/api/v1/events/parent/{eventId}/reschedule` | `ParentEventRegVM` | Reschedule button | ✅ |
| 40 | Events | `EventRegistrationApi` | GET | `/api/v1/events/parent/registrations` | `ParentEventRegVM` | My Registrations | ✅ |
| 41 | PEWS | `PewsApi` | GET | `/api/v1/parent/pews/{childId}` | `ParentNudgeVM` | PEWS overlay | ✅ |
| 42 | Library | `LibraryApi` | GET | `/api/v1/library/books` | `ParentLibraryVM` | Library Browse tab | ✅ |
| 43 | Library | `LibraryApi` | GET | `/api/v1/library/issued` | `ParentLibraryVM` | Library MyBooks tab | ✅ |
| 44 | Library | `LibraryApi` | GET | `/api/v1/library/reservations` | `ParentLibraryVM` | Library Reservations tab | ✅ |
| 45 | Scholarships | `ScholarshipApi` | GET | `/api/v1/parent/scholarships` | `ScholarshipVM` | Scholarships overlay | ✅ |
| 46 | Tutor | `TutorApi` | GET | `/api/v1/tutor/modules` | `TutorChatVM` | TutorChat overlay | ✅ |
| 47 | Calendar | `CalendarApi` | GET | `/api/admin/calendar` | `CalendarVM` | Calendar overlay | ✅ |
| 48 | User Details | `AuthApi` | GET | `/api/v1/user/details` | `ParentProfileVM` | Profile screen | ✅ |

---

## 5. Server-Side Route Inventory

| Route File | Function | Endpoints |
|-----------|----------|-----------|
| `ParentDashboardRouting.kt` | `parentDashboardRouting()` | `GET /dashboard`, `GET /child/{id}/attendance`, `GET /child/{id}/timetable`, `GET /child/{id}/syllabus`, `GET /child/{id}/marks` |
| `ParentFeesRouting.kt` | `parentFeesRouting()` | `GET /fees`, `POST /fees/pay` |
| `ParentAcademicsRouting.kt` | `parentAcademicsRouting()` | `GET /child/{id}/daily-summary`, `GET /child/{id}/syllabus-v2`, `GET /child/{id}/quizzes`, `GET /child/{id}/quizzes/{quizId}`, `POST /child/{id}/quizzes/{quizId}/submit`, `GET /child/{id}/quizzes/{quizId}/result`, `GET /child/{id}/quizzes/{quizId}/leaderboard` |
| `TrackProgressRouting.kt` | `trackProgressRouting()` | `GET /track-progress` |
| `ParentLinkRouting.kt` | `parentLinkRouting()` | `POST /link-child` |
| `ParentLeaveRouting.kt` | `parentLeaveRouting()` | `GET /leave`, `POST /leave` |
| `ParentMessagesRouting.kt` | (in user/) | `GET /messages/threads`, `GET /messages/threads/{id}`, `POST /messages/threads/{id}/read`, `POST /messages/threads/{id}` |
| `ParentRouting.kt` | (in user/) | `GET /scholarships`, `GET /announcements` |
| `HealthRouting.kt` | `healthRouting()` | `GET /health/{childId}` |
| `TransportRouting.kt` | `transportRouting()` | `GET /transport/child/{id}/route`, `GET /transport/{routeId}/live` |
| `IdCardRouting.kt` | `idCardRouting()` | `GET /idcard/{childId}` |
| `ReportCardRouter.kt` | `reportCardRouting()` | `GET /report-card/parent/{childId}`, `GET /report-card/parent/{childId}/conference-pack` |
| `EventRegistrationRouting.kt` | `eventRegistrationRouting()` | `GET /events/parent`, `GET /events/parent/{id}`, `POST /events/parent/{id}/register`, `POST /events/parent/{id}/cancel`, `POST /events/parent/{id}/reschedule`, `GET /events/parent/registrations` |
| `PewsRouting.kt` | `pewsRouting()` | `GET /parent/pews/{childId}` |
| `LibraryRouting.kt` | `libraryRouting()` | `GET /library/books`, `GET /library/issued`, `GET /library/reservations` |
| `ScholarshipRouting.kt` | `scholarshipRouting()` | `GET /parent/scholarships` |
| `TutorRouter.kt` | `tutorRouting()` | `GET /tutor/modules` |
| `AcademicCalendarRouting.kt` | `academicCalendarRouting()` | `GET /api/admin/calendar` |

---

## 6. ViewModel → API → Screen Wiring Diagram

### Home Tab
```
ParentDashboardVM ──→ getDashboard() ──→ GET /api/v1/parent/dashboard
  ├── children[], greeting, alerts ──→ PortalTopHeader, HeroCard
  ├── loadAttendance() ──→ getChildAttendance() ──→ GET /child/{id}/attendance
  │   └── attendanceRate, today status ──→ HeroCard stats
  ├── loadTimetable() ──→ getChildTimetable() ──→ GET /child/{id}/timetable
  │   └── todayPeriods ──→ TodayScheduleCard
  ├── loadSyllabus() ──→ getChildSyllabus() ──→ GET /child/{id}/syllabus
  │   └── coveredToday ──→ (used internally)
  ├── loadMarks() ──→ getChildMarks() ──→ GET /child/{id}/marks
  │   └── latestMark, markTrend ──→ HeroCard avgGrade
  └── loadFees() ──→ getFees() ──→ GET /api/v1/parent/fees
      └── outstandingFees, overdueCount ──→ PriorityCarousel

ParentAcademicsVM ──→ loadDailySummary() ──→ GET /child/{id}/daily-summary
  └── dailySummary ──→ TodaySummaryCard

ParentAnnouncementVM ──→ getAnnouncements() ──→ GET /api/v1/parent/announcements
  └── announcements[] ──→ UpdatesCard

TrackProgressVM ──→ getTrackProgress() ──→ GET /api/v1/parent/track-progress
  └── accountName ──→ PortalTopHeader parentName

TransportVM ──→ loadChildRoute() ──→ GET /transport/child/{id}/route
  └── childRoute != null ──→ TransportTrackingCard (visibility only)
      ⚠️ Card content is HARDCODED — VM state NOT used for display
```

### Academics Tab
```
TrackProgressVM ──→ getTrackProgress() ──→ GET /api/v1/parent/track-progress
  ├── overallProgress, currentLevel ──→ Overview tab
  ├── badges[] ──→ (not rendered here, on Profile)
  ├── academicCompetencies[] ──→ ⚠️ FETCHED BUT NOT RENDERED
  ├── emotionalIntelligence ──→ ⚠️ FETCHED BUT NOT RENDERED
  └── playIndicators[] ──→ ⚠️ FETCHED BUT NOT RENDERED

ParentAcademicsVM ──→ multiple endpoints
  ├── loadAttendance() ──→ GET /child/{id}/attendance ──→ Attendance tab
  ├── loadMarks() ──→ GET /child/{id}/marks ──→ Marks tab
  ├── loadSyllabus() / loadSyllabusV2() ──→ GET /child/{id}/syllabus[-v2] ──→ Syllabus tab
  ├── loadQuizzes() ──→ GET /child/{id}/quizzes ──→ Quizzes tab
  ├── loadQuizDetail() ──→ GET /child/{id}/quizzes/{quizId} ──→ Quiz detail
  ├── submitQuiz() ──→ POST /child/{id}/quizzes/{quizId}/submit ──→ Quiz submit
  ├── loadQuizResult() ──→ GET /child/{id}/quizzes/{quizId}/result ──→ Quiz result
  └── loadLeaderboard() ──→ GET /child/{id}/quizzes/{quizId}/leaderboard ──→ Leaderboard
```

### Fees Tab
```
FeeVM ──→ getFees() ──→ GET /api/v1/parent/fees
  ├── outstandingFees, totalCollected, collectionProgress ──→ Fee overview
  ├── overdueCount ──→ Overdue indicator
  └── announcements[] ──→ Fee notices

FeeVM ──→ payFee() ──→ POST /api/v1/parent/fees/pay
  ⚠️ NOT WIRED — onPay callback is TODO in ParentPortalV2.kt
```

### Conversations Tab
```
ParentMessageVM ──→ getMessageThreads() ──→ GET /api/v1/parent/messages/threads
  └── threads[] ──→ Message list

ParentMessageVM ──→ getMessages() ──→ GET /api/v1/parent/messages/threads/{threadId}
  └── messages[] ──→ Conversation detail

ParentMessageVM ──→ sendMessage() ──→ POST /api/v1/parent/messages/threads/{threadId}
  └── sends message

ParentMessageVM ──→ markThreadRead() ──→ POST /api/v1/parent/messages/threads/{threadId}/read
```

### Profile Tab
```
ParentProfileVM ──→ authRepository.getUserDetails() ──→ GET /api/v1/user/details
  └── profile (name, email, phone, photoUrl) ──→ Profile screen

ParentDashboardVM ──→ (same as Home) ──→ child data for Profile card
TrackProgressVM ──→ (same as Home) ──→ level, badges, XP
ParentAcademicsVM ──→ (same as Academics) ──→ quiz count

⚠️ StatsGrid trends are ALL FABRICATED — no backend source
```

---

## 7. Summary of Critical Issues

### Immediate Fixes (HIGH severity)

1. **H-04**: Wire `unreadMessages` from `ParentMessageVM` — currently always 0
2. **H-07, H-08**: Fix PremiumFeaturesGrid wiring — "AI Report" → report card, "PEWS" → PEWS overlay
3. **P-01**: Wire fee payment `onPay` to `FeeVM.payFee()` → `POST /api/v1/parent/fees/pay`
4. **PC-01 to PC-04**: Remove fabricated trend strings from StatsGrid
5. **FH-01**: Fee History screen is non-functional — needs backend endpoint + API + VM wiring
6. **PR-01, PR-02**: "Notification preferences" and "Change password" rows are dead taps
7. **H-01, H-02**: TransportTrackingCard shows hardcoded text instead of `TransportVM` state

### Medium Priority

8. **H-05**: Fee priority card subtitle "Q4 Tuition · Due soon" — use real fee period
9. **H-06**: Announcement author "School Admin" — use real author from DTO
10. **F-01**: "Collected this term" — use real term name from `FeeData`
11. **FP-01**: "Secure Razorpay gateway" — use school's configured gateway
12. **L-02**: Library greeting uses "Parent" fallback — use real parent name
13. **PC-05, PC-06**: XP max and "Scholar" label — use backend values
14. **Track Progress**: 3 data sets fetched but not rendered (competencies, EI, play indicators)

### Low Priority (i18n)

15. ~60+ string literals across all screens need `StringKeys` migration for localization
16. `FilterChips` list and `staticTabs` list should use `StringKeys`
17. `subjectPalette` could be config-driven

### Missing Backend Endpoints

| Needed Endpoint | Purpose |
|----------------|---------|
| `GET /api/v1/parent/fees/history` | Fee payment transaction history |
| `GET/PUT /api/v1/parent/notification-preferences` | Notification settings |
| `POST /api/v1/auth/change-password` | Password change |
| Trend fields in existing responses | Attendance/marks/XP/quiz deltas |
| `author` field in announcement DTO | Real announcement source |
| `termName` field in `FeeData` | Fee period label |
