# Offline Mode Implementation Plan

## Goal

Every screen in the app should:
1. **Open instantly** showing last cached data from Room (no skeleton if cache exists)
2. **Background fetch** fresh data from server
3. **Server responds** → update cache, UI refreshes seamlessly
4. **Network fails** → keep showing cached data with a subtle "offline" indicator
5. **No cache + no network** → show "Unable to load" with retry button
6. **No cache + server loads** → show skeleton briefly, then fresh data

---

## Current State

### What works offline today
- **Schools list** — `SchoolRepositoryImpl` reads from Room via `RoomSchoolLocalDataSource` (but `refresh()` is never called automatically, so cache may be stale)
- **Library** — `LibraryRepositoryImpl` has full cache-on-success + fallback-on-error pattern (the gold standard)

### What's dead code (built but not wired)
- `OfflineAwareEventRepository` — full cache+outbox for events, not registered in Koin
- `EventSyncEngine` — polls outbox every 30s, not started anywhere
- `AnnouncementDao`, `TeacherDayCacheDao`, `OutboxOperationDao` — registered in DI, no repository consumes them

### What has zero offline support (every open = network call + skeleton)
- **Parent**: Dashboard, Track Progress, Fees, Scholarships, Announcements, Notifications, Attendance, Marks, Syllabus, Timetable, Leave, Messages, Pulse, Daily Summary, Quiz
- **Teacher**: Today, Week, Classes, Class Detail, Student Profile, Attendance, Syllabus, Homework, Gradebook, Messages, Lesson Plans, Leave, Check-in, Obligations, Profile, Timetable
- **Admin**: Dashboard, Students, Teachers, Staff, Admissions, Announcements, Messages, Calendar, Attendance, Leave, Analytics, Results, Onboarding, School Profile, Classes, School Day Config
- **Cross-feature**: Health, Transport, PEWS, Report Card, Alumni, Scholarship, Branding, ID Card, Tutor, Scheduling, Events, i18n

### Architecture summary
- **22 repository implementations** across all features
- **~70 ViewModels** registered in Koin
- **Room DB v3** with 9 entities, `fallbackToDestructiveMigration(dropAllTables = true)`
- All repos return `NetworkResult<T>` (Success / Error / ConnectionError)
- All VMs expose either `UiState<T>` (Loading/Success/Error) or custom `XxxUiState(isLoading, error, data)`

---

## Architecture: Generic JSON Cache

### Why not per-feature entities?
Creating 20+ entity classes + DAOs for every feature would be a massive effort. Instead, we use a **single generic key-value cache** — one entity, one DAO, one helper class. Any repository adopts offline mode with ~5 lines of change per method.

### New components

#### 1. `CacheEntity` (Room entity)
```
key: String (PK)     — e.g. "parent_dashboard", "teacher_today_2026-07-09"
dataJson: String     — serialized response JSON
cachedAt: Long       — System.currentTimeMillis()
ttlMs: Long          — time-to-live (0 = never expire, or e.g. 24h for daily data)
```

#### 2. `CacheDao`
```kotlin
@Dao interface CacheDao {
    @Query("SELECT * FROM cache_entity WHERE `key` = :key")
    suspend fun get(key: String): CacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: CacheEntity)

    @Query("DELETE FROM cache_entity WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM cache_entity WHERE cachedAt < :before")
    suspend fun evictOlderThan(before: Long)

    @Query("SELECT COUNT(*) FROM cache_entity")
    suspend fun count(): Int
}
```

#### 3. `CacheManager` (Koin singleton)
```kotlin
class CacheManager(private val dao: CacheDao, private val json: Json) {

    // Read cache by key, deserialize to T
    suspend fun <T> read(key: String, deserializer: KSerializer<T>): T?

    // Write cache by key
    suspend fun <T> write(key: String, data: T, serializer: KSerializer<T>, ttlMs: Long = 0)

    // Check if cache exists and is not expired
    suspend fun isFresh(key: String): Boolean

    // Delete cache entry
    suspend fun evict(key: String)

    // Periodic cleanup
    suspend fun cleanup()
}
```

#### 4. `cacheFirst()` helper function
```kotlin
/**
 * Cache-first pattern: try cache → emit immediately → fetch from API → update cache → return.
 * If no cache, just fetch from API.
 * If API fails and cache exists, return cache.
 * If API fails and no cache, return the error.
 */
suspend fun <T> cacheFirst(
    cache: CacheManager,
    cacheKey: String,
    serializer: KSerializer<T>,
    ttlMs: Long = 0,
    networkCall: suspend () -> NetworkResult<T>,
): NetworkResult<T>
```

**Flow:**
```
1. Read cache by key
2. If cache exists AND is fresh → return NetworkResult.Success(cachedData)
   (UI shows cached data immediately)
3. Call networkCall()
4. If network succeeds → write to cache → return NetworkResult.Success(freshData)
   (UI updates with fresh data)
5. If network fails AND cache exists (even if stale) → return NetworkResult.Success(cachedData)
   (UI keeps showing cached data, offline indicator can be shown)
6. If network fails AND no cache → return the NetworkResult.Error/ConnectionError
   (UI shows "Unable to load" with retry)
```

**For background refresh (stale-while-revalidate):**
```kotlin
/**
 * Stale-while-revalidate: return cache immediately if exists,
 * then fetch from network in background and update.
 */
suspend fun <T> swr(
    cache: CacheManager,
    cacheKey: String,
    serializer: KSerializer<T>,
    ttlMs: Long = 0,
    networkCall: suspend () -> NetworkResult<T>,
): SwrResult<T>

data class SwrResult<T>(
    val data: T,           // either cached or fresh
    val isStale: Boolean,  // true if data came from cache (background refresh may be in progress)
    val isOffline: Boolean,// true if network failed and we're showing cache
)
```

---

## UiState Enhancement

### Current `UiState`
```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### Enhanced `UiState`
```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(
        val data: T,
        val isStale: Boolean = false,    // true if showing cached data while refreshing
        val isOffline: Boolean = false,  // true if network failed and showing cache
    ) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

- `isStale = true` → UI shows a subtle "Updating..." indicator
- `isOffline = true` → UI shows a subtle "Offline" banner
- Both false → fresh data, no indicator

### For custom UiState classes (e.g. `StudentProfileUiState`)
Add two fields:
```kotlin
data class StudentProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profile: StudentProfileDto? = null,
    val isStale: Boolean = false,    // NEW
    val isOffline: Boolean = false,  // NEW
    // ... existing fields
)
```

---

## VStateHost Enhancement (UI layer)

The `VStateHost` composable needs to handle the new stale/offline states:

- **`Success(isStale=true)`** → show content + small "Updating..." pill at top
- **`Success(isOffline=true)`** → show content + "Offline — showing saved data" banner
- **`Loading` + has previous data** → keep showing previous data with a subtle loading indicator (don't flash skeleton)
- **`Loading` + no previous data** → show skeleton (first launch only)
- **`Error`** → show "Unable to load" with retry

---

## Database Migration

### Current: v3 with 9 entities
### Target: v4 with 10 entities (add `CacheEntity`)

Since `fallbackToDestructiveMigration(dropAllTables = true)` is used, bumping to v4 will drop all tables and recreate. This is acceptable during development. For production, we'd add a proper migration, but for now destructive migration is fine.

### Changes:
1. Add `CacheEntity` to `AppDatabase` entities array
2. Add `abstract fun cacheDao(): CacheDao` to `AppDatabase`
3. Bump version to 4
4. Register `CacheDao` in all 3 platform modules (Android, iOS, JVM)
5. Register `CacheManager` in `commonModule`

---

## Implementation Phases

### Phase 1: Infrastructure (foundation)
**Files to create:**
- `shared/src/roomMain/.../core/database/CacheEntity.kt` — entity + DAO
- `shared/src/commonMain/.../core/cache/CacheManager.kt` — singleton helper
- `shared/src/commonMain/.../core/cache/CacheFirst.kt` — `cacheFirst()` + `swr()` helper functions

**Files to modify:**
- `shared/src/roomMain/.../core/database/AppDatabase.kt` — add entity, bump to v4
- `shared/src/androidMain/.../di/PlatformModule.android.kt` — register `cacheDao`
- `shared/src/iosMain/.../di/PlatformModule.ios.kt` — register `cacheDao`
- `shared/src/jvmMain/.../di/PlatformModule.jvm.kt` — register `cacheDao`
- `shared/src/commonMain/.../di/Koin.kt` — register `CacheManager` singleton
- `shared/src/commonMain/.../domain/util/UIState.kt` — add `isStale`, `isOffline` to `Success`

**Verification:** Build all 4 targets green. No behavior change yet.

---

### Phase 2: Parent Portal (highest user impact)
Parent portal is the most frequently opened portal. Every parent opens the app daily to check child updates.

**Repository changes** — `ParentRepositoryImpl`:
Inject `CacheManager` and wrap each GET method:

| Method | Cache Key | TTL |
|---|---|---|
| `getDashboard` | `parent_dashboard` | 0 (always refresh) |
| `getTrackProgress` | `parent_track_progress` | 1h |
| `getFees` | `parent_fees_{childId}` | 1h |
| `getScholarships` | `parent_scholarships` | 24h |
| `getAnnouncements` | `parent_announcements` | 0 |
| `getNotifications` | `parent_notifications` | 0 |
| `getChildAttendance` | `parent_attendance_{childId}` | 1h |
| `getChildMarks` | `parent_marks_{childId}` | 1h |
| `getChildSyllabus` | `parent_syllabus_{childId}` | 24h |
| `getChildTimetable` | `parent_timetable_{childId}` | 24h |
| `getLeaveRequests` | `parent_leave_requests` | 0 |
| `getMessageThreads` | `parent_message_threads` | 0 |
| `getThreadMessages` | `parent_thread_messages_{threadId}` | 0 |
| `getLatestPulse` | `parent_pulse_{childId}` | 24h |
| `getPulseHistory` | `parent_pulse_history_{childId}` | 24h |
| `getDailySummary` | `parent_daily_summary_{childId}_{date}` | 24h |
| `getSyllabusV2` | `parent_syllabus_v2_{childId}` | 24h |
| `getQuizList` | `parent_quiz_list_{childId}` | 1h |
| `getQuizDetail` | `parent_quiz_detail_{quizId}` | 1h |
| `getQuizLeaderboard` | `parent_quiz_leaderboard_{childId}_{quizId}` | 1h |
| `getQuizResult` | `parent_quiz_result_{childId}_{quizId}` | 24h |
| `getMessageRecipients` | `parent_message_recipients` | 24h |
| `getUnreadCount` | `parent_unread_count` | 0 (always fresh) |

**Write operations** (NOT cached, but queue for offline sync if needed):
- `markNotificationRead`, `markAllNotificationsRead`, `markNotificationByRef`
- `clearReadNotifications`, `clearAllNotifications`
- `applyLeave`, `sendMessage`, `linkChild`, `submitQuiz`, `searchSchools`

**ViewModel changes** — each parent VM:
- `ParentHomeViewModel` — emit cached `Success(isStale=true)` immediately, then fresh `Success(isStale=false)`
- `FeeViewModel`, `ScholarshipsViewModel`, `ParentAnnouncementViewModel`, `NotificationsViewModel`
- `TrackProgressViewModel`, `ParentAcademicsViewModel`, `ParentDashboardViewModel`
- `ParentLeaveViewModel`, `ParentMessageViewModel`, `ParentPulseViewModel`
- `ParentProfileViewModel`, `LinkChildViewModel`

**Files to modify:**
- `shared/src/commonMain/.../feature/parent/data/repository/ParentRepositoryImpl.kt`
- `shared/src/commonMain/.../di/Koin.kt` (add `CacheManager` param to `ParentRepositoryImpl`)
- All parent ViewModels (~12 files)

**Verification:** Open parent app → dashboard shows instantly from cache → background refresh updates if needed. Kill network → reopen → still shows data.

---

### Phase 3: Teacher Portal
Teacher portal is opened multiple times daily for attendance, marks, homework.

**Repository changes** — `TeacherRepositoryImpl`:

| Method | Cache Key | TTL |
|---|---|---|
| `getDay` | `teacher_day_{date}` | 0 (always refresh, time-sensitive) |
| `getWeek` | `teacher_week_{date}` | 0 |
| `listClassesV2` | `teacher_classes_v2` | 24h |
| `getClassDetailV2` | `teacher_class_detail_{assignmentId}` | 1h |
| `getStudentProfileV2` | `teacher_student_profile_{studentId}` | 1h |
| `loadAttendance` | `teacher_attendance_{assignmentId}_{date}` | 24h |
| `loadSyllabus` | `teacher_syllabus_{assignmentId}` | 24h |
| `listHomework` | `teacher_homework_{assignmentId}` | 0 |
| `getHomeworkBoard` | `teacher_homework_board_{homeworkId}_{assignmentId}` | 0 |
| `listAssessments` | `teacher_assessments_{assignmentId}_{status}` | 0 |
| `getAssessmentMarks` | `teacher_assessment_marks_{assessmentId}` | 0 |
| `getAssessmentHistory` | `teacher_assessment_history_{assignmentId}` | 24h |
| `getCheckInStatus` | `teacher_checkin_status_{date}` | 0 |
| `getObligations` | `teacher_obligations` | 0 |
| `getProfile` | `teacher_profile` | 24h |
| `getMessageThreads` | `teacher_message_threads` | 0 |
| `getThreadMessages` | `teacher_thread_messages_{threadId}` | 0 |
| `getUnreadCount` | `teacher_unread_count` | 0 |
| `getLeaveRequests` | `teacher_leave_requests_{status}` | 0 |
| `getMyLeave` | `teacher_my_leave_{status}` | 0 |
| `listLessonPlans` | `teacher_lesson_plans_{assignmentId}_{status}` | 0 |
| `getLessonPlan` | `teacher_lesson_plan_{planId}` | 0 |
| `getLessonCalendar` | `teacher_lesson_calendar_{assignmentId}_{month}` | 24h |
| `listLessonTemplates` | `teacher_lesson_templates_{assignmentId}` | 24h |
| `listDailyLogs` | `teacher_daily_logs_{assignmentId}` | 24h |
| `shouldShowDailyLogPopup` | `teacher_daily_log_popup` | 0 |
| `getPopupPrefs` | `teacher_popup_prefs` | 24h |
| `listQuizzes` | `teacher_quizzes_{assignmentId}` | 0 |
| `getQuizResults` | `teacher_quiz_results_{quizId}` | 0 |
| `getQuizLeaderboard` | `teacher_quiz_leaderboard_{quizId}` | 0 |
| `getTimetableChangeRequests` | `teacher_timetable_change_requests` | 0 |
| `getPaceWarning` | `teacher_pace_warning_{assignmentId}` | 0 |

**Write operations** (NOT cached):
- `saveAttendance`, `assignHomework`, `reviewHomeworkSubmission`, `closeHomework`, `grantHomeworkExtension`
- `createAssessmentV2`, `saveAssessmentMarks`, `publishAssessment`, `unpublishAssessment`
- `createSyllabusUnit`, `updateSyllabusUnit`, `toggleSyllabusProgress`, `deleteSyllabusUnit`
- `checkIn`, `broadcastToClass`, `sendMessage`, `markThreadRead`
- `decideLeaveRequest`, `applyMyLeave`
- `createLessonPlan`, `updateLessonPlan`, `deleteLessonPlan`, `completeLessonPlan`, `skipLessonPlan`
- `saveLessonTemplate`, `deleteLessonTemplate`, `instantiateLessonFromTemplate`
- `submitTimetableChangeRequest`, `createDailyLog`, `setPopupPrefs`
- `parseSyllabus`, `confirmParsedSyllabus`, `autoFillSyllabus`, `confirmAutoFillSyllabus`
- `approveSyllabus`, `rejectSyllabus`
- `generateQuiz`, `publishQuiz`, `updateQuizQuestion`, `addQuizQuestion`, `regenerateQuiz`

**ViewModel changes** — each teacher VM:
- `TeacherTodayViewModel`, `TeacherCheckInViewModel`, `TeacherObligationsViewModel`
- `TeacherClassesViewModel`, `TeacherStudentProfileViewModel`, `TeacherAttendanceViewModel`
- `TeacherGradebookViewModel`, `TeacherSyllabusViewModel`, `TeacherHomeworkViewModel`
- `TeacherMessageViewModel`, `TeacherLessonPlanViewModel`, `TeacherProfileViewModel`
- `TeacherProfileActionsViewModel`, `TeacherLeaveViewModel`, `TeacherTimetableViewModel`

**Files to modify:**
- `shared/src/commonMain/.../feature/teacher/data/repository/TeacherRepositoryImpl.kt`
- `shared/src/commonMain/.../di/Koin.kt` (add `CacheManager` param)
- All teacher ViewModels (~15 files)

**Verification:** Open teacher app → Today screen shows instantly from cache → background refresh updates.

---

### Phase 4: Admin Portal
Admin portal is opened for management tasks. Less frequent but still needs offline for dashboards.

**Repository changes** — multiple admin repositories:

| Repository | Key GET methods to cache | TTL |
|---|---|---|
| `AdminDashboardRepositoryImpl` | `getDashboard` | 0 |
| `StudentsRepositoryImpl` | `getStudentRoster`, `getStudentProfile`, `getTeacherProfile` | 1h |
| `TeachersRepositoryImpl` | `getTeacherRoster` | 1h |
| `StaffRepositoryImpl` | `getStaffList` | 1h |
| `AdmissionRepositoryImpl` | `getApplications`, `getApplicationDetail` | 0 |
| `AnnouncementsRepositoryImpl` | `listAnnouncements` | 0 |
| `MessagesRepositoryImpl` | `getThreads`, `getThreadMessages` | 0 |
| `PtmRepositoryImpl` | `listPtms`, `getPtmDetail` | 0 |
| `CalendarRepositoryImpl` | `getEvents` | 0 |
| `AcademicCalendarPlatformRepositoryImpl` | `getCalendarEvents` | 0 |
| `AcademicYearRepositoryImpl` | `listAcademicYears` | 24h |
| `AttendanceRepositoryImpl` | `getDailyAttendance` | 0 |
| `LeaveRequestsRepositoryImpl` | `getLeaveRequests` | 0 |
| `LinkRequestsRepositoryImpl` | `getLinkRequests` | 0 |
| `AnalyticsRepositoryImpl` | `getAnalytics`, `getStudentAnalytics`, `getTeacherPerformance`, `getClassPerformance`, `getSyllabusCoverage`, `getPaceAlerts` | 1h |
| `ResultsRepositoryImpl` | `getResults` | 0 |
| `SchoolProfileRepositoryImpl` | `getSchoolProfile` | 24h |
| `SchoolClassesRepositoryImpl` | `getClasses`, `getSubjects` | 24h |
| `SchoolDayConfigRepositoryImpl` | `getSchoolDayConfig` | 24h |
| `OnboardingRepositoryImpl` | `getOnboardingStatus` | 24h |
| `UserProfileRepositoryImpl` | `getUserProfile` | 24h |
| `TeacherAssignmentRepositoryImpl` | `getOverview`, `getOptions` | 1h |

**ViewModel changes** — ~25 admin ViewModels

**Files to modify:**
- ~20 admin repository implementations
- `shared/src/commonMain/.../di/Koin.kt` (add `CacheManager` param to each repo)
- ~25 admin ViewModels

**Verification:** Open admin app → dashboard shows instantly from cache → background refresh updates.

---

### Phase 5: Cross-Feature Repositories
Features shared across portals.

| Repository | Key GET methods to cache | TTL |
|---|---|---|
| `HealthRepositoryImpl` | `getHealthRecords`, `getHealthAlerts` | 0 |
| `TransportRepositoryImpl` | `getRoutes`, `getLiveTracking` | 0 |
| `PewsRepositoryImpl` | `getCohorts`, `getStudentDetail`, `getEffectiveness` | 1h |
| `ReportCardRepositoryImpl` | `getReports`, `getDraft`, `getEffectiveness` | 0 |
| `AlumniRepositoryImpl` | `getAlumniList` | 24h |
| `ScholarshipRepositoryImpl` | `getScholarships` | 24h |
| `BrandingRepositoryImpl` | `getBrandingKit` | 24h |
| `IdCardRepositoryImpl` | `getIdCards` | 24h |
| `TutorRepositoryImpl` | `getSubjects`, `getChatHistory`, `getPlan`, `getPractice` | 0 |
| `ScheduledMessageRepositoryImpl` | `getScheduledMessages` | 0 |
| `EventRegistrationRepositoryImpl` | `listParentEvents`, `getTeacherPtmEvents`, `listAdminEvents` | 0 |
| `NotificationRepositoryImpl` | (notifications already cached via ParentRepository) | — |
| `LanguageRepositoryImpl` | `getLanguagePref` | 24h |
| `ContentRepositoryImpl` | `getLandingContent` | 24h |

**ViewModel changes** — ~20 cross-feature ViewModels

**Files to modify:**
- ~14 cross-feature repository implementations
- `shared/src/commonMain/.../di/Koin.kt`
- ~20 ViewModels

**Verification:** Open any cross-feature screen → shows cached data instantly.

---

### Phase 6: UI Polish + Offline Indicator
Enhance the UI layer to show stale/offline states.

**Changes to `VStateHost` (or equivalent state host):**
- When `Success(isStale=true)` → show content + subtle "Updating..." indicator
- When `Success(isOffline=true)` → show content + "Offline" banner
- When `Loading` + previous data exists → keep showing previous data with loading indicator (no skeleton flash)
- When `Error` + no previous data → show "Unable to load" with retry

**New component: `VOfflineBanner`**
- Small dismissible banner at top of screen
- Shows "You're offline — showing saved data"
- Auto-hides when connection restored

**New component: `VStaleIndicator`**
- Small pill/spinner in app bar or top of content
- Shows "Updating..." while background refresh runs
- Disappears when fresh data arrives

**Files to create/modify:**
- `composeApp/src/commonMain/.../ui/v2/components/VOfflineBanner.kt` (new)
- `composeApp/src/commonMain/.../ui/v2/components/VStaleIndicator.kt` (new)
- `composeApp/src/commonMain/.../ui/v2/screens/Shared.kt` (modify `VStateHost`)

**Verification:** Kill network → reopen app → see cached data with "Offline" banner. Restore network → banner disappears, data refreshes.

---

### Phase 7: Offline Write Queue (Future Enhancement)
For write operations when offline (e.g., teacher marks attendance offline).

**Already partially built:**
- `EventOutboxDao` + `EventSyncEngine` (for events, not wired)
- `OutboxOperationDao` (generic, not consumed)
- `LibraryPendingActionDao` (for library, wired)

**Plan:**
- Wire `EventSyncEngine` into Koin and start it on app launch
- Create a generic `WriteOutboxManager` that any feature can use
- Queue write operations when `NetworkResult.ConnectionError`
- Sync engine polls every 30s, replays pending operations
- UI shows "Pending sync" indicator when outbox has items

**Scope:** This is a larger effort and can be done after the read-side offline mode is complete. The read-side cache (Phases 1-6) is the priority — it solves the "skeleton on every open" problem.

---

## Cache Key Naming Convention

```
{portal}_{feature}_{params}

Examples:
  parent_dashboard
  parent_fees_child123
  parent_attendance_child456
  teacher_day_2026-07-09
  teacher_classes_v2
  admin_dashboard
  admin_student_roster
  cross_health_records
  cross_transport_routes
```

Rules:
- Lowercase, underscore-separated
- Portal prefix: `parent_`, `teacher_`, `admin_`, `cross_`
- Feature name: `dashboard`, `fees`, `attendance`, etc.
- Parameters: child ID, date, assignment ID, etc. appended with `_`
- No spaces, no special characters

---

## TTL Strategy

| TTL | Use Case | Examples |
|---|---|---|
| **0 (always refresh)** | Time-sensitive, frequently changing data | Dashboard, notifications, messages, attendance, today's schedule |
| **1 hour** | Moderately fresh data | Fees, marks, assessments, homework, leave requests |
| **24 hours** | Rarely changing data | Profile, syllabus, timetable, classes list, academic year, branding |

TTL determines how long cache is considered "fresh":
- **Fresh cache** → return immediately, still fetch from network in background (SWR)
- **Stale cache (past TTL)** → return immediately, fetch from network (cache is better than nothing)
- **No cache** → show skeleton, fetch from network

Even with TTL=0, cache is still used if network fails. TTL only controls whether we bother hitting the network when we have fresh cache. With TTL=0, we always hit the network but show cache first.

---

## Cache Eviction

- **On logout** → `CacheManager.evictAll()` — clear all cached data when user logs out (different user shouldn't see previous user's data)
- **On user switch** → clear portal-specific caches
- **Periodic cleanup** → `CacheManager.cleanup()` called on app launch, evicts entries older than 7 days
- **Manual eviction** → when a write operation succeeds, evict the related cache key so next read gets fresh data (e.g., after `saveAttendance`, evict `teacher_attendance_{assignmentId}_{date}`)

---

## Serialization Considerations

The app uses `kotlinx.serialization` with `Json` configured in Koin. The `CacheManager` will use the same `Json` instance for serialization/deserialization.

**Challenge:** Some response types are wrapped in `ApiResponse<T>` and some are direct types. The `CacheManager` needs to handle both:
- `NetworkResult<ApiResponse<T>>` → serialize/deserialize the full `ApiResponse<T>`
- `NetworkResult<T>` → serialize/deserialize `T` directly

**Solution:** The `cacheFirst()` helper is generic — it takes a `KSerializer<T>` and handles serialization of the actual `NetworkResult.Success.data` payload. The caller specifies what to serialize.

---

## Risk Mitigation

| Risk | Mitigation |
|---|---|
| Cache schema mismatch after API changes | `fallbackToDestructiveMigration` handles DB schema. For JSON mismatch, wrap deserialization in try-catch → if fails, treat as no cache |
| Cache grows too large | Periodic cleanup (7-day eviction) + manual eviction on logout |
| Stale data shown as "fresh" | `isStale` flag in UiState → UI always indicates when data is from cache |
| Security: cached data persists after logout | `evictAll()` on logout in `AuthRepositoryImpl.logout()` |
| Cross-user data leak | Cache keys include portal prefix but NOT user ID. On logout, all cache is cleared. On login, fresh data is fetched. |
| Build breaks from serialization | Use `Json { ignoreUnknownKeys = true }` (already configured) — extra fields in cached JSON won't break deserialization |

---

## File Impact Summary

| Phase | New Files | Modified Files | Estimated Lines Changed |
|---|---|---|---|
| Phase 1: Infrastructure | 3 | 6 | ~200 |
| Phase 2: Parent | 0 | ~14 | ~400 |
| Phase 3: Teacher | 0 | ~16 | ~500 |
| Phase 4: Admin | 0 | ~45 | ~800 |
| Phase 5: Cross-feature | 0 | ~34 | ~500 |
| Phase 6: UI Polish | 2 | 1 | ~200 |
| Phase 7: Write Queue | 3 | ~10 | ~400 (future) |
| **Total (Phases 1-6)** | **5** | **~116** | **~2,600** |

---

## Execution Order

```
Phase 1 (Infrastructure)
  ↓ Build green
Phase 2 (Parent Portal)
  ↓ Build green + manual test
Phase 3 (Teacher Portal)
  ↓ Build green + manual test
Phase 4 (Admin Portal)
  ↓ Build green + manual test
Phase 5 (Cross-Feature)
  ↓ Build green + manual test
Phase 6 (UI Polish)
  ↓ Build green + manual test
Phase 7 (Write Queue — future)
```

Each phase is independently shippable. After Phase 1, the infrastructure exists but no behavior changes. After Phase 2, parent portal has offline mode. Each subsequent phase adds offline to one more portal.

---

## Testing Checklist (Per Phase)

- [ ] Build all 4 targets green (server, shared JVM, shared Android, composeApp Android)
- [ ] Open app with network → data loads normally
- [ ] Close app, kill network, reopen → cached data shows instantly
- [ ] Close app, reopen with network → cached data shows, then updates with fresh data
- [ ] Clear app data (no cache), open with network → skeleton shows briefly, then data
- [ ] Clear app data, open without network → "Unable to load" with retry
- [ ] Logout → cache cleared → login as different user → no stale data from previous user
- [ ] No crashes, no ANRs, no memory leaks
