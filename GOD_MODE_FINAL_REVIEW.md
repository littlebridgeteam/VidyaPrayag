# God Mode Final Review — Bug Report (117 bugs)

**Date:** 2026-07-03 | **Scope:** server, shared, composeApp, website

---

## 1. Server: Security & Authorization (10)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 001 | CRITICAL | TeacherSyllabusRouting.kt:1549 | `fetchImageAsBase64` SSRF — no URL validation, accepts any URL including internal IPs/metadata |
| 002 | HIGH | TeacherSyllabusRouting.kt:1547 | `imageHttpClient` has no timeout — tarpit DoS |
| 003 | HIGH | TeacherSyllabusRouting.kt:1551 | `readRawBytes()` no size cap — OOM on large response |
| 004 | MEDIUM | NcertReferenceService.kt:75 | `ensureSeeded` not thread-safe — `var seeded` data race |
| 005 | MEDIUM | NcertReferenceService.kt:84-97 | Per-row insert loop not transactional — partial seed on crash |
| 006 | MEDIUM | SyllabusPaceRouting.kt:90 | `section` defaults to "A" without `sectionResolved` check |
| 007 | LOW | SchoolAccess.kt | `requireSchoolContext` extra DB query per request (cacheable) |
| 008 | LOW | TeacherAccess.kt:106-137 | `requireTeacherContext` same extra DB query pattern |
| 009 | LOW | ParentAcademicsRouting.kt:244-258 | `requireOwnedChild` two separate DB queries (Children + Students) |
| 010 | MEDIUM | SyllabusPaceRouting.kt:71-74 | Alert resolve delegates school scoping to service — no route-level guard |

## 2. Server: N+1 Queries & Performance (9)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 011 | HIGH | ParentAcademicsRouting.kt:391-404 | Marks: N+1 — per-assessment `AssessmentMarksTable` query inside `map` |
| 012 | HIGH | ParentAcademicsRouting.kt:639-663 | Syllabus V2: N+1 — per-assignment `CurriculumUnitsTable` + `SyllabusProgressTable` |
| 013 | MEDIUM | ParentAcademicsRouting.kt:562-592 | Daily summary: N+1 — assignments query then separate logs query |
| 014 | MEDIUM | TeacherSyllabusRouting.kt:730-750 | Delete cascade: 3 sequential queries (children, grandchildren, update) |
| 015 | HIGH | TeacherSyllabusRouting.kt:1321-1356 | Daily log: N+1 — per-topic select-then-update/insert for progress |
| 016 | MEDIUM | ParentAcademicsRouting.kt:320-336 | Attendance: loads ALL records then counts in memory (should be SQL aggregate) |
| 017 | LOW | ParentAcademicsRouting.kt:471-473 | Timetable: loads ALL school users for teacher name map (no role filter) |
| 018 | LOW | TeacherSyllabusRouting.kt:359-421 | `loadSyllabusNodes` loads all units then groups in memory |
| 019 | LOW | ParentAcademicsRouting.kt:339-350 | Holidays: separate query from attendance (same schoolId scope) |

## 3. Server: Race Conditions & Concurrency (8)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 020 | MEDIUM | TeacherSyllabusRouting.kt:671-704 | Toggle progress: check-then-act race (select then insert/update) |
| 021 | MEDIUM | TeacherSyllabusRouting.kt:1287-1317 | Daily log create: check-then-act race on existing log |
| 022 | LOW | TeacherSyllabusRouting.kt:1484-1509 | Popup prefs: check-then-act race |
| 023 | MEDIUM | TeacherSyllabusRouting.kt:543-565 | Create unit: position race (max+1 computed then insert) |
| 024 | MEDIUM | TeacherSyllabusRouting.kt:868-935 | Parse confirm: same position race |
| 025 | MEDIUM | TeacherSyllabusRouting.kt:1012-1080 | Auto-fill confirm: same position race |
| 026 | LOW | EventSyncEngine.kt:24,37 | `isDraining` plain boolean — not thread-safe |
| 027 | LOW | OfflineAwareEventRepository.kt:43-45 | Cache delete-then-insert — concurrent read sees empty |

## 4. Server: Input Validation & Data Integrity (8)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 028 | LOW | SyllabusPaceRouting.kt:88-89 | `classId` not UUID-validated (string comparison only) |
| 029 | LOW | NcertReferenceService.kt:102-105 | `normalizeClassLevel` accepts "Class 999" — no range check |
| 030 | LOW | TeacherSyllabusRouting.kt:1557-1565 | `guessMimeType` defaults to JPEG for unknown extensions |
| 031 | LOW | TeacherSyllabusRouting.kt:884 | Parse confirm: no title length validation |
| 032 | LOW | TeacherSyllabusRouting.kt:525-528 | Create unit: no title length validation |
| 033 | LOW | TeacherSyllabusRouting.kt:1567-1574 | `parseTopicIdsJson` silently returns empty on corruption |
| 034 | LOW | ParentAcademicsRouting.kt:336 | Attendance rate counts "late" as present (undocumented) |
| 035 | MEDIUM | SyllabusPaceRouting.kt:110-119 | `paceRecalculate` no rate limiting — DoS via repeated calls |

## 5. Server: SSRF & External Requests (3)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 036 | MEDIUM | LibraryCoverService.kt:155-173 | URL validator doesn't resolve DNS — DNS rebinding bypass |
| 037 | MEDIUM | LibraryCoverService.kt:167-172 | No IPv6 loopback/private range check (`::1` bypass) |
| 038 | LOW | LibraryCoverService.kt:167-172 | `0.0.0.0` not blocked |

## 6. Server: AI Services (4)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 039 | MEDIUM | SyllabusAiService.kt:88-122 | No base64 size validation before AI call |
| 040 | LOW | SyllabusAiService.kt:103 | User-controlled `classLevel`/`subject` in AI system prompt (prompt injection) |
| 041 | HIGH | ParentAcademicsRouting.kt:594-602 | AI `generateDailySummary` called on every parent request — no caching, costly |
| 042 | LOW | NcertReferenceService.kt:83 | All NCERT data concatenated in memory on cold start |

## 7. Shared: Offline Sync Engine (11)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 043 | CRITICAL | EventSyncEngine.kt:62 | Hardcoded token `"offline-sync"` — ALL sync ops get 401, nothing ever syncs |
| 044 | MEDIUM | EventSyncEngine.kt:28-33 | No exponential backoff or jitter (unlike main SyncEngine) |
| 045 | MEDIUM | EventSyncEngine.kt:110-112 | `ConnectionError` doesn't increment attempts — never dead-letters |
| 046 | CRITICAL | EventSyncEngine.kt:62 | No token refresh handling — expired token during offline = permanent failure |
| 047 | MEDIUM | EventSyncEngine.kt:86 | RESCHEDULE force-unwraps `op.slotId!!` — NPE if null |
| 048 | MEDIUM | OfflineAwareEventRepository.kt:79 | Online register passes `null` clientRequestId — no idempotency on retry |
| 049 | MEDIUM | OfflineAwareEventRepository.kt:43-45 | Cache delete+insert not transactional — concurrent read sees empty |
| 050 | LOW | OfflineAwareEventRepository.kt:32 | Cache stores `schoolId=""` — not scoped to school |
| 051 | LOW | EventSyncEngine.kt:21 | `POLL_INTERVAL_MS` hardcoded, not adaptive |
| 052 | LOW | EventOutboxDao.kt:25-26 | `cleanSynced` never called — SYNCED rows grow forever |
| 053 | LOW | EventSyncEngine.kt | No observable sync state for UI (no StateFlow) |

## 8. Shared: Network & Auth (4)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 054 | LOW | TokenAuthenticator.kt | Refresh failure on network error → logout (should retry network blip) |
| 055 | LOW | SessionManager.kt:1-39 | `clearAuthCache` depends on Ktor internals — fragile |
| 056 | LOW | Koin.kt:50-100 | Main + refresh client JSON config duplicated |
| 057 | MEDIUM | Koin.kt | HTTP timeout values not verified for long-running endpoints (AI, CSV) |

## 9. Shared: Repositories & Caching (4)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 058 | LOW | EventRegistrationRepositoryImpl.kt | Thin pass-through, no error mapping or logging |
| 059 | MEDIUM | AuthRepositoryImpl.kt:115-148 | `refresh` failure doesn't clear session — zombie session risk |
| 060 | MEDIUM | OfflineAwareEventRepository.kt:98,126,154 | Offline-queued ops return `NetworkResult.Error` not `ConnectionError` — UI shows error not "queued" |

## 10. ComposeApp: ViewModels & State (12)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 061 | MEDIUM | TeacherSyllabusViewModel.kt:175-222 | `toggleUnit` optimistic update uses stale `s0` snapshot for revert — concurrent toggles can revert wrong state |
| 062 | MEDIUM | TeacherSyllabusViewModel.kt:358-385 | `deleteUnit` optimistic removal only goes 2 levels deep (children + grandchildren) — deeper hierarchy leaves orphans |
| 063 | LOW | TeacherSyllabusViewModel.kt:136-166 | `load` doesn't cancel previous in-flight load — rapid assignment switches cause race on state |
| 064 | LOW | TeacherSyllabusViewModel.kt:238-270 | `submitAdd` calls `load()` after success but doesn't clear `isAdding` if load fails |
| 065 | LOW | TeacherSyllabusViewModel.kt:416-443 | `saveDailyLog` calls `load()` after success — reloads entire syllabus just to update daily logs |
| 066 | LOW | TeacherSyllabusViewModel.kt:445-456 | `dismissDailyLogPopup` fire-and-forget — no error handling on prefs save |
| 067 | LOW | TeacherSyllabusViewModel.kt:458-469 | `loadDailyLogs` silently swallows errors (`else -> Unit`) |
| 068 | LOW | TeacherSyllabusViewModel.kt:513-524 | `loadQuizzes` silently swallows errors |
| 069 | LOW | TeacherSyllabusViewModel.kt:526-535 | `publishQuiz` silently swallows errors — no user feedback on failure |
| 070 | MEDIUM | TeacherSyllabusViewModel.kt:658-676 | `loadPaceWarning` silently swallows errors — pace warning never shows if API fails |
| 071 | LOW | TeacherSyllabusViewModel.kt:77-83 | `LaunchedEffect` calls `load`, `loadQuizzes`, `loadPaceWarning` sequentially — could be parallel |
| 072 | MEDIUM | TeacherSyllabusViewModel.kt:273-297 | `renameUnit` optimistic update has no `updatingUnitId` flag — user can spam rename during in-flight request |

## 11. ComposeApp: UI & Navigation (6)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 073 | LOW | TeacherSyllabusScreenV2.kt:77-83 | `LaunchedEffect(assignmentId)` doesn't cancel on recomposition with different ID |
| 074 | LOW | App.kt:73-246 | SessionScope key is JWT token — token refresh creates new scope, destroying all ViewModels |
| 075 | LOW | Shared.kt:1-60 | `VLoadingState` has no error or retry UI — just spinner |
| 076 | LOW | TeacherSyllabusScreenV2.kt:87-96 | Loading/error states check `state.units.isEmpty()` — briefly shows loading on reload after data exists |
| 077 | MEDIUM | App.kt | Session scope uses raw JWT string as key — long string as map key is inefficient |
| 078 | LOW | VidyaPrayagFirebaseMessagingService.kt | FCM token sync uses dedicated scope but no retry on network failure |

## 12. Website: React Hooks & State (10)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 079 | MEDIUM | PewsWorkspace.tsx:54-78 | `pollJob` callback depends on `jobId` — interval recreated on every jobId change, but `setInterval` closure may capture stale `jobId` |
| 080 | LOW | PewsWorkspace.tsx:82-104 | `recompute` function not wrapped in `useCallback` — recreated every render |
| 081 | LOW | PewsStudentPanel.tsx:48-55 | `useSWR` key uses `studentCode` but fetcher casts `as string` — if studentCode becomes null between render and fetch, crash |
| 082 | LOW | PewsStudentPanel.tsx:91-105 | `onUpdate`/`onSendParentMessage` async callbacks not memoized — new function identity each render |
| 083 | MEDIUM | SchoolDayConfigPanel.tsx:68-130 | `create`/`update`/`deactivate` functions not wrapped in `useCallback` — recreated every render, could cause child re-renders |
| 084 | LOW | hooks.ts:122-123 | `usePewsCohort` passes `minLevel` to `adminApi.pewsCohort` but SWR key uses `minLevel ?? ""` — undefined vs "" key mismatch |
| 085 | LOW | hooks.ts:144-146 | `useReportCardOversight` SWR key includes `academicYearId ?? ""` but fetcher passes `academicYearId` (possibly undefined) |
| 086 | LOW | session.tsx:90-99 | `useEffect` with `[]` deps — `setSession`/`setReady` called in effect, React 18 strict mode double-invokes |
| 087 | MEDIUM | session.tsx:105-132 | `signOut` sets `signingOut.current = false` in finally — if `router.replace` throws, ref stays true, blocking future signOut |
| 088 | LOW | client.ts:105-131 | `doRefresh` dedupes concurrent refreshes but `refreshing` is module-level — shared across all components, could block unrelated tabs |

## 13. Website: API Client & Session (8)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 089 | MEDIUM | client.ts:165-189 | `authRequest` on 401 after refresh failure erases session — but SWR may have in-flight requests that don't get the memo |
| 090 | LOW | client.ts:153-161 | `rawRequest` reads `res.text()` then parses — large responses held in memory twice (text + JSON) |
| 091 | LOW | client.ts:149 | `cache: "no-store"` on every request — no browser cache for static data |
| 092 | MEDIUM | client.ts:112-117 | `doRefresh` sends `refresh_token` in body — if server logs request bodies, refresh token leaks |
| 093 | LOW | session.tsx:35-44 | `readSession` no expiry check — stale session with expired token sits in localStorage until 401 |
| 094 | LOW | session.tsx:60-67 | `patchTokens` reads then writes localStorage — race between concurrent refreshes (mitigated by `doRefresh` dedup but not fully) |
| 095 | LOW | client.ts:189 | `env?.data ?? (env as unknown)` — if response is not enveloped, casts entire env as data (fragile) |
| 096 | LOW | session.tsx:113 | `signOut` uses raw `fetch` not `authRequest` — bypasses any future interceptors/logging |

## 14. Website: Components & UX (8)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 097 | LOW | PewsWorkspace.tsx:262-263 | `TrendCard` uses `Math.max(...points.map(...), 1)` — stack overflow on very large arrays |
| 098 | LOW | PewsWorkspace.tsx:450-475 | `ConfigCard` `save` sets `setDraft(null)` after mutate — if mutate fails, draft is lost |
| 099 | LOW | SchoolDayConfigPanel.tsx:76-94 | `create` calls `mutate("school-day-configs")` — global mutate key must match SWR key exactly |
| 100 | LOW | PewsStudentPanel.tsx:58 | `mine` filter on interventions runs every render — should be `useMemo` |
| 101 | LOW | settings/page.tsx | Form seeds when `!isDirty` — if data arrives after user starts typing, seed is skipped (correct) but no loading state shown |
| 102 | LOW | PewsWorkspace.tsx:80 | `students` derived from `cohort?.students ?? []` every render — should be `useMemo` |
| 103 | LOW | SchoolDayConfigPanel.tsx | `slots` state array updated via `map`/`filter` — new array each edit, causes full re-render of slot list |
| 104 | LOW | PewsWorkspace.tsx:50 | `jobStatus` state set but never displayed in the UI — dead state |

## 15. Database & Schema (6)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 105 | MEDIUM | Tables.kt:322-325 | `TeacherSubjectAssignmentsTable` unique index on `(schoolId, className, section, subject, teacherName)` — uses display `teacherName` not `teacherId`, so same teacher with name variant creates duplicates |
| 106 | LOW | DatabaseFactory.kt | SQLite auto-creates schema in dev but production uses manual migrations — schema drift risk if migrations are missed |
| 107 | LOW | DatabaseFactory.kt | Read replica configured but no failover logic if replica is down |
| 108 | MEDIUM | Tables.kt | `SyllabusProgressTable` — if no unique constraint on `(unitId, section, assignmentId)`, the check-then-act races (BUG-020) create duplicates |
| 109 | LOW | Tables.kt | `DailyClassLogTable` — if no unique constraint on `(assignmentId, date)`, the check-then-act race (BUG-021) creates duplicates |
| 110 | LOW | DatabaseFactory.kt | CMS/demo seeding is idempotent by name check but not by slug — duplicate slugs possible |

## 16. Configuration & Deployment (7)

| # | Severity | File | Bug |
|---|----------|------|-----|
| 111 | MEDIUM | Application.kt | CORS `isProduction` check uses `DATABASE_URL` env var presence — fragile heuristic, could misconfigure if DATABASE_URL is set in dev |
| 112 | LOW | Application.kt | CORS allows `anyHost()` in dev — if dev server is exposed, any origin can call |
| 113 | LOW | JwtConfig.kt | JWT expiry is role-based (30min admin, 24h others) — long expiry for non-admins increases token theft risk |
| 114 | MEDIUM | Application.kt | `MAX_JSON_BODY_BYTES` (1MB) exempts multipart — multipart upload size is unbounded |
| 115 | LOW | Application.kt | No rate limiting on auth endpoints (`/login`, `/signup`, `/refresh`) — brute force / token spam |
| 116 | LOW | Application.kt | `StatusPages` exception handler may leak stack traces in non-production |
| 117 | LOW | .env.example | No documentation of required env vars for production deployment checklist |

---

## Top 5 Critical/High Priorities

1. **BUG-043/046** — EventSyncEngine hardcoded token: offline event sync is completely broken
2. **BUG-001/002/003** — SSRF + no timeout + no size limit on `fetchImageAsBase64`
3. **BUG-011/012/015** — N+1 queries in parent academics and teacher daily log (performance)
4. **BUG-041** — AI `generateDailySummary` called on every parent request (cost + latency)
5. **BUG-020/021/023** — Check-then-act race conditions in syllabus progress/daily log/unit creation
