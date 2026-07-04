# Enroll+ — Restructure Changelog & Backend Mapping

> **Companion to:** `ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md` (FILE 1)
> **Total restructure entries:** 22

---

## Entry #1 — SuperAdmin shares SchoolAdmin portal (AUTH-003)

**BEFORE:** `EntryRole.SuperAdmin` falls through to `SchoolPortalV2` with no distinct UI or feature gating. SuperAdmin sees same interface as single-school admin.

**WHY:** Iteration 2 — SuperAdmin role exists but has no differentiated portal. Audit AUTH-003.

**AFTER:** Add role-based feature gating within `SchoolPortalV2`. SuperAdmin sees "All Schools" card on Home + "Dev Tools" in Settings. SchoolAdmin sees only their school. FILE 1 ref: A-0, A-5.

**OLD backend:** All `/api/v1/school/*` endpoints accept JWT role but don't differentiate SuperAdmin. DevTools routes use `requireSuperAdmin` per-request DB read (AUTH-012).

**NEW backend:** (a) Reuses existing endpoints. (b) Cache role check with `AtomicReference` + TTL for DevTools. No new endpoints.

**Breaking risk:** LOW — additive only. No deep link changes.

**Migration steps:** 1) Frontend: pass `entryRole` to `SchoolPortalV2`, conditionally render SuperAdmin UI. 2) Backend: cache DevTools role check. 3) QA: verify SuperAdmin sees extra features, SchoolAdmin does not.

**Regression checklist:**
- [ ] SchoolAdmin → no "All Schools" card, no DevTools
- [ ] SuperAdmin → "All Schools" card + DevTools visible
- [ ] DevTools endpoints reject SchoolAdmin (403)

---

## Entry #2 — Notifications screen shared across 3 portals (intentional)

**BEFORE:** `NotificationsScreenV2` called from 3 portals with role-specific data. Entry points inconsistent across portals.

**WHY:** Iteration 2 — flagged as duplication, confirmed intentional (different permission scopes). Standardize entry points.

**AFTER:** All portals: header bell → Notifications overlay. All: deep link `/role/notifications`. Admin also: Comms tab + Settings. FILE 1 ref: D-3.

**OLD backend:** `GET /api/v1/notifications` (role-scoped). `POST /api/v1/notifications/{id}/read` — silent `.catch(() => {})` (AUTH-023).

**NEW backend:** (a) Reuses endpoints. Fix AUTH-023/024: replace silent catch with `AppLogger.w()` + retry.

**Breaking risk:** None — entry point standardization is additive.

**Migration steps:** 1) Verify all 3 portals have header bell wiring. 2) Fix AUTH-023/024 logging. 3) QA.

**Regression checklist:**
- [ ] All 3 portals → bell → Notifications overlay with correct scope
- [ ] markNotificationRead error → logged
- [ ] Offline → cached notifications

---

## Entry #3 — Legacy Calendar vs Academic Calendar Platform (Admin)

**BEFORE:** Admin has two calendar UIs: `AcademicCalendarScreenV2` (legacy, header icon) and `AcademicCalendarPlatformScreenV2` (premium, Home/Comms). Two different layouts for same functionality.

**WHY:** Iteration 2 — same action (view calendar) reachable via two UIs. Confusing for Principal persona.

**AFTER:** Admin header calendar icon → remapped to `AcademicCalendarPlatformScreenV2`. Teacher/Parent keep legacy (view-only). FILE 1 ref: A-6.02, A-6.03, D-3.

**OLD backend:** Both call `GET /api/v1/calendar/events`, `POST/PUT/DELETE /api/v1/calendar/events`.

**NEW backend:** (a) Reuses endpoints. Frontend: change `SchoolOverlay.Calendar` composable mapping.

**Breaking risk:** LOW — same overlay enum, different composable. Old builds still render legacy.

**Migration steps:** 1) In `SchoolPortalV2.kt`, change `SchoolOverlay.Calendar` → `AcademicCalendarPlatformScreenV2`. 2) Optional `KillSwitchGuard`. 3) QA.

**Regression checklist:**
- [ ] Admin → header calendar → premium platform
- [ ] Teacher/Parent → calendar overlay → legacy (view-only)
- [ ] Deep links work for all roles

---

## Entry #4 — Messages: 3 separate composables with same UI pattern

**BEFORE:** `MessagesScreenV2` (admin), `TeacherMessagesScreenV2` (teacher), `ParentMessagesScreenV2` (parent) — 3 composables implementing same WhatsApp-style inbox+conversation pattern.

**WHY:** Iteration 2 — visual duplication. Any UI change must be made in 3 places. All 3 personas benefit from WhatsApp mental model.

**AFTER:** Unify into `UnifiedMessagesScreen(scope: MessageScope)`. Each portal passes scope. FILE 1 ref: D-3.

**OLD backend:** `GET /api/v1/messages/threads`, `GET /api/v1/teacher/messages/threads`, `GET /api/v1/parent/messages/threads` — 3 route files. Pagination clamped (DFL-031).

**NEW backend:** (a) Reuses existing endpoints. Scope determines which API client to call.

**Breaking risk:** LOW — deep links unchanged. Old builds use old composables.

**Migration steps:** 1) Create `UnifiedMessagesScreen.kt` with `MessageScope` param. 2) Extract shared UI (thread list, conversation, compose bar). 3) Update 3 portal files. 4) Update Conversations tab. 5) QA.

**Regression checklist:**
- [ ] All 3 portals → correct scoped threads
- [ ] Send/receive works
- [ ] Offline queue works
- [ ] Pagination works

---

## Entry #5 — Library: School (14 tabs) vs Parent (9 tabs)

**BEFORE:** School library has 14 tabs, parent has 9. Overlapping tab names (Browse/Books, History, Settings) with different labels.

**WHY:** Iteration 2 — overlapping tabs with inconsistent names. Different permission scopes justify separation. Standardize shared tab names.

**AFTER:** Keep 2 separate screens. Rename: Books/Browse → "Catalog" in both. History → "Issue History" (school) / "My History" (parent). Settings → "Library Settings" / "Preferences". FILE 1 ref: A-6.34, C-6.15.

**OLD backend:** `LibraryRouting.kt` — same routes, different permission checks. Rate limiter in-memory (AUTH-026).

**NEW backend:** (a) Reuses endpoints. Fix AUTH-026: migrate to Redis rate limiting.

**Breaking risk:** LOW — cosmetic renames only.

**Migration steps:** 1) Rename tabs in both `VTopTabs` lists. 2) Migrate rate limiter (separate follow-up). 3) QA.

**Regression checklist:**
- [ ] Admin → 14 tabs with renamed labels
- [ ] Parent → 9 tabs with renamed labels
- [ ] Both → "Catalog" tab renders correctly

---

## Entry #6 — Transport Attendance route validation (AUTH-004)

**BEFORE:** Teacher transport attendance overlay has no client-side route assignment validation. Teacher could mark attendance for any route.

**WHY:** Iteration 2 — security gap. AUTH-004. Backend already fixed AUTH-016 (`requireSchoolAdmin`).

**AFTER:** Add route assignment check. Filter route selector to assigned routes only. Add permission-denied state. FILE 1 ref: B-6.03.

**OLD backend:** `GET /api/v1/transport/assignments`, `POST /api/v1/transport/attendance`.

**NEW backend:** (b) Add teacher route assignment check to `POST /api/v1/transport/attendance` → 403 if not assigned. (a) Reuses assignments endpoint for filtering.

**Breaking risk:** LOW — old builds get 403 from backend.

**Migration steps:** 1) Backend: add assignment check. 2) Frontend: filter route selector + permission-denied state. 3) QA.

**Regression checklist:**
- [ ] Assigned teacher → can mark attendance
- [ ] Unassigned teacher → "No routes assigned" state
- [ ] Backend rejects unassigned → 403

---

## Entry #7 — PEWS: Admin cohort vs Teacher class-scoped

**BEFORE:** `PewsCohortScreenV2` (admin, school-wide) and `TeacherPewsScreenV2` (teacher, class-scoped) — separate composables with duplicated UI patterns (risk cards, badges, factor breakdowns).

**WHY:** Iteration 2 — shared UI patterns across 2 composables. Different data scopes justify separation. Extract shared components. AUTH-018: PEWS endpoint parent-only, needs teacher/admin access.

**AFTER:** Keep 2 screens. Extract `PewsRiskCard`, `PewsRiskBadge`, `PewsFactorBreakdown` as shared composables. FILE 1 ref: A-6.19, B-6.04.

**OLD backend:** `GET /api/v1/pews/cohort` (admin). `GET /api/v1/pews/student/{id}` — parent-only check (AUTH-018).

**NEW backend:** (b) Add teacher/admin access to student endpoint. (c) New: `GET /api/v1/teacher/pews/students` for teacher's class at-risk students.

**Breaking risk:** LOW — additive. KillSwitchGuard for teacher endpoint.

**Migration steps:** 1) Backend: fix AUTH-018 + create teacher endpoint. 2) Frontend: extract shared components. 3) Feature flag: `KillSwitchGuard("pews_teacher")`. 4) QA.

**Regression checklist:**
- [ ] Admin → school-wide cohort
- [ ] Teacher → only class students
- [ ] Parent → only their child
- [ ] Feature disabled → KillSwitchGuard state

---

## Entry #8 — Digital ID Card shared (intentional)

**BEFORE:** `DigitalIdCardScreen` shared by teacher and parent. Different data (teacher vs student ID).

**WHY:** Iteration 2 — confirmed intentional sharing. Same visual pattern, different data.

**AFTER:** No change. FILE 1 ref: D-3.

**OLD backend:** `GET /api/v1/id-cards/teacher/{id}`, `GET /api/v1/id-cards/student/{id}`.

**NEW backend:** (a) Reuses as-is.

**Migration steps:** None.

---

## Entry #9 — Scheduled Messages shared (intentional)

**BEFORE:** `ScheduledMessagesScreenV2` shared by admin and teacher. Different data scopes.

**WHY:** Iteration 2 — confirmed intentional.

**AFTER:** No change. FILE 1 ref: D-3.

**Migration steps:** None.

---

## Entry #10 — Orphaned ScholarshipsScreenV2.kt

**BEFORE:** `ScholarshipsScreenV2.kt` exists but not referenced by any `ParentOverlay` enum. `ParentOverlay.Scholarships` maps to `ScholarshipWorkflowScreenV2` instead.

**WHY:** Iteration 2 — orphaned dead code. Confusing for developers.

**AFTER:** Delete `ScholarshipsScreenV2.kt`. Canonical screen is `ScholarshipWorkflowScreenV2` (already wired). FILE 1 ref: C-6.03.

**OLD backend:** Verify API calls in file before deletion. `ScholarshipWorkflowScreenV2` calls `GET /api/v1/scholarships`, `POST /api/v1/scholarships/apply`.

**NEW backend:** (a) Reuses existing endpoints.

**Migration steps:** 1) `grep -r "ScholarshipsScreenV2"` — confirm zero references. 2) Delete file. 3) Build. 4) QA.

**Regression checklist:**
- [ ] File deleted, build succeeds
- [ ] Parent → Scholarships overlay → `ScholarshipWorkflowScreenV2` renders

---

## Entry #11 — Parent "Profile" naming collision

**BEFORE:** Profile tab = gamified player card (`ParentProfileCardScreenV2`). Profile overlay = account settings form (`ParentProfileScreenV2`). Two screens named "Profile" with different purposes.

**WHY:** Iteration 2 — inconsistent terminology. Confusing for low-literacy parent.

**AFTER:** Rename overlay label to "Account Settings". Tab keeps "Profile". FILE 1 ref: C-5, C-6.04.

**OLD backend:** `GET/PUT /api/v1/parent/profile` — both screens call same endpoints.

**NEW backend:** (a) Reuses as-is. Frontend label change only.

**Migration steps:** 1) Change overlay label in `ParentPortalV2.kt`. 2) QA.

**Regression checklist:**
- [ ] Profile tab → player card
- [ ] "Account Settings" → settings form
- [ ] No two screens labeled "Profile"

---

## Entry #12 — Parent Messages: overlay vs Conversations tab

**BEFORE:** `ParentOverlay.Messages` → `ParentMessagesScreenV2` (overlay, deep-link entry). Conversations tab → Messages segment → same composable (embedded). Back-press from overlay → Conversations tab → same messages — confusing.

**WHY:** Iteration 2 — same screen in two containers. Redundant overlay.

**AFTER:** Remove `ParentOverlay.Messages` from enum. Deep link `/parent/messages` → switches to Conversations tab + Messages segment. FILE 1 ref: C-4, D-6.

**OLD backend:** `GET /api/v1/parent/messages/threads`, `POST /api/v1/parent/messages/send`.

**NEW backend:** (a) Reuses as-is. Frontend: update `parseDeepLink` for `/parent/messages`.

**Breaking risk:** LOW — old builds still open overlay (works, just redundant).

**Migration steps:** 1) Update `parseDeepLink` → tab switch. 2) Remove `Messages` from `ParentOverlay` enum. 3) Remove overlay case. 4) QA.

**Regression checklist:**
- [ ] Deep link `/parent/messages` → Conversations tab
- [ ] No `ParentOverlay.Messages` in codebase
- [ ] Build succeeds

---

## Entry #13 — Discovery + LinkChild shared across 3 entry points (intentional)

**BEFORE:** `DiscoveryScreenV2` and `ParentLinkChildScreenV2` called from UnauthFlow, unlinked gate, and parent overlay. Different back-navigation contexts.

**WHY:** Iteration 2 — confirmed intentional sharing. Different entry points, same screen.

**AFTER:** No change. Document back-navigation differences. FILE 1 ref: D-3.

**Migration steps:** None.

---

## Entry #14 — Orphaned CommonLandingScreenV2.kt

**BEFORE:** `CommonLandingScreenV2.kt` superseded by `CommonLandingScreenV3.kt`. Stale import in `LegalInfoScreenV2.kt`.

**WHY:** Iteration 2 — orphaned dead code + stale reference.

**AFTER:** Delete `CommonLandingScreenV2.kt`. Fix stale import. FILE 1 ref: D-2.

**Migration steps:** 1) `grep -r "CommonLandingScreenV2"`. 2) Remove stale import from `LegalInfoScreenV2.kt`. 3) Delete file. 4) Build. 5) QA.

**Regression checklist:**
- [ ] File deleted, no references remain
- [ ] Build succeeds
- [ ] Landing (V3) + Legal Info render correctly

---

## Entry #15 — Event Registration: Admin/Teacher vs Parent (intentional)

**BEFORE:** Admin/teacher share management view. Parent has consumer view. Different actions.

**WHY:** Iteration 2 — confirmed intentional separation (manage vs register).

**AFTER:** No change. FILE 1 ref: D-3.

**Migration steps:** None.

---

## Entry #16 — Orphaned SchoolDayConfigScreenV2.kt

**BEFORE:** File exists but not wired into any navigation. School day config (bell schedule, period timings) is needed by timetable system.

**WHY:** Iteration 2 — orphaned screen with needed functionality.

**AFTER:** Wire into Classes & Subjects → Schedule tab → "Day Configuration" button → `SchoolDayConfigScreenV2`. FILE 1 ref: A-6.37.

**OLD backend:** Verify API calls in file. Likely `GET/PUT /api/v1/school/day-config`.

**NEW backend:** (a) Reuses if exists. (c) If missing: `GET/PUT /api/v1/school/{id}/day-config`.

**Migration steps:** 1) Inspect file for API calls. 2) Add "Day Configuration" button to Schedule tab. 3) Wire to composable. 4) Verify/create backend endpoint. 5) QA.

**Regression checklist:**
- [ ] Schedule tab → "Day Configuration" button visible
- [ ] Tap → screen renders, save works
- [ ] Timetable uses configured day schedule

---

## Entry #17 — Orphaned PewsEffectivenessScreenV2.kt

**BEFORE:** File exists but not wired. PEWS effectiveness metrics (intervention outcomes, risk improvement) unreachable.

**WHY:** Iteration 2 — orphaned screen with valuable analytics.

**AFTER:** `SchoolOverlay.ReportEffectiveness` is ALREADY wired to `AdminReportingEffectivenessScreen` (A-6.30) — NOT to `PewsEffectivenessScreenV2`. These are two DIFFERENT files. `PewsEffectivenessScreenV2.kt` is truly orphaned. Resolution: either (a) delete `PewsEffectivenessScreenV2.kt` if `AdminReportingEffectivenessScreen` covers the same functionality, or (b) merge unique PEWS-specific analytics from `PewsEffectivenessScreenV2` into `AdminReportingEffectivenessScreen` as a sub-tab. FILE 1 ref: A-6.30, D-6.

**OLD backend:** Verify API calls. Likely `GET /api/v1/pews/effectiveness`.

**NEW backend:** Reuse existing `/api/v1/report/effectiveness` if overlapping. If PEWS-specific, add `GET /api/v1/pews/effectiveness?schoolId=X`.

**Migration steps:** 1) Compare `PewsEffectivenessScreenV2.kt` vs `AdminReportingEffectivenessScreen.kt` for overlap. 2) If overlapping → delete `PewsEffectivenessScreenV2.kt`. 3) If unique PEWS metrics exist → add as sub-tab in `AdminReportingEffectivenessScreen`. 4) Verify/create endpoint. 5) QA.

**Regression checklist:**
- [ ] Home → "Report Effectiveness" card → `AdminReportingEffectivenessScreen` renders
- [ ] Data loads (delivery stats, engagement metrics)
- [ ] If merged: PEWS-specific metrics visible as sub-tab
- [ ] `PewsEffectivenessScreenV2.kt` deleted or merged

---

## Entry #18 — Orphaned TutorPlanScreen.kt and TutorPracticeScreen.kt

**BEFORE:** Both files exist but not wired. Tutor system only has chat (wired) and progress (wired). Plan and practice are missing from navigation.

**WHY:** Iteration 2 — orphaned screens completing the tutor loop.

**AFTER:** Add `VTopTabs` (Chat · Plan · Practice) to `TutorChatScreen`. Embed `TutorPlanScreen` and `TutorPracticeScreen` as tab content. FILE 1 ref: C-6.12.

**OLD backend:** Verify API calls. Likely `GET/POST /api/v1/tutor/plan`, `GET/POST /api/v1/tutor/practice`.

**NEW backend:** (a) Reuses if exists. (c) If missing: `GET /api/v1/tutor/plan?studentId=X`, `GET /api/v1/tutor/practice?studentId=X`, `POST /api/v1/tutor/practice/submit`.

**Migration steps:** 1) Inspect both files. 2) Add `VTopTabs` to `TutorChatScreen`. 3) Embed as tab content. 4) Verify/create endpoints. 5) `KillSwitchGuard("tutor_plan_practice")`. 6) QA.

**Regression checklist:**
- [ ] TutorChat → 3 tabs: Chat · Plan · Practice
- [ ] Plan tab → study plan renders
- [ ] Practice tab → problems render, submit works
- [ ] Deep link `/parent/tutor` → Chat tab active
- [ ] Feature disabled → Plan/Practice show "Coming soon"

---

## Entry #19 — Dead SchoolOverlay.Calendar enum value

**BEFORE:** `SchoolOverlay.Calendar` exists in the enum and has a `when` branch rendering `AcademicCalendarScreenV2`, but NO code ever sets `overlay = SchoolOverlay.Calendar`. The Home tab's `onOpenCalendar` is wired to `SchoolOverlay.AcademicCalendarPlatform` (A-6.03). Deep link `/school/calendar` also maps to `AcademicCalendarPlatform`. This is dead code.

**WHY:** Iteration 8 (God-Mode Audit) — unreachable enum value with a dead `when` branch.

**AFTER:** Remove `SchoolOverlay.Calendar` from the enum and delete its `when` branch in `SchoolPortalV2.kt`. FILE 1 ref: A-6.02, D-6.

**OLD backend:** None — no API changes.

**NEW backend:** None.

**Migration steps:** 1) Remove `Calendar` from `SchoolOverlay` enum. 2) Remove the `when` branch for `SchoolOverlay.Calendar`. 3) Verify no deep link references. 4) QA: Home calendar → AcademicCalendarPlatform renders.

**Regression checklist:**
- [ ] `SchoolOverlay.Calendar` removed from enum
- [ ] No compile errors
- [ ] Home → calendar icon → AcademicCalendarPlatform (not legacy)
- [ ] Deep link `/school/calendar` → AcademicCalendarPlatform

---

## Entry #20 — Dead SchoolOverlay.Results enum value

**BEFORE:** `SchoolOverlay.Results` exists in the enum and has a `when` branch rendering `ResultsPublishScreenV2`, but NO code ever sets `overlay = SchoolOverlay.Results`. Deep links for `/school/report-card` map to `SchoolOverlay.ReportPublish` (A-6.29) instead. This is dead code.

**WHY:** Iteration 8 (God-Mode Audit) — unreachable enum value with a dead `when` branch.

**AFTER:** Remove `SchoolOverlay.Results` from the enum and delete its `when` branch. Either delete `ResultsPublishScreenV2.kt` or merge its functionality into `AdminReportPublishScreen` (A-6.29). FILE 1 ref: A-6.10, D-6.

**OLD backend:** None — no API changes.

**NEW backend:** None.

**Migration steps:** 1) Compare `ResultsPublishScreenV2.kt` vs `AdminReportPublishScreen.kt` for overlap. 2) If overlapping → delete `ResultsPublishScreenV2.kt`. 3) If unique → merge into `AdminReportPublishScreen`. 4) Remove `Results` from `SchoolOverlay` enum. 5) Remove the `when` branch. 6) QA.

**Regression checklist:**
- [ ] `SchoolOverlay.Results` removed from enum
- [ ] `ResultsPublishScreenV2.kt` deleted or merged
- [ ] No compile errors
- [ ] Home → "Publish Reports" → `AdminReportPublishScreen` renders
- [ ] Deep link `/school/report-card` → ReportPublish overlay

---

## Entry #21 — Orphaned ParentPewsScreenV2.kt

**BEFORE:** `ParentPewsScreenV2.kt` exists but is only referenced within itself. No `ParentOverlay` enum value maps to it. `ParentOverlay.Pulse` maps to `ParentPulseScreen` (a DIFFERENT file). This screen is completely unreachable.

**WHY:** Iteration 8 (God-Mode Audit) — orphaned screen not caught in earlier iterations.

**AFTER:** Delete `ParentPewsScreenV2.kt`. If PEWS data for parents is needed, it should be surfaced via `ParentPulseScreen` (which is wired) or a new `ParentOverlay.Pews` enum value. FILE 1 ref: D-6.

**OLD backend:** Verify if any API calls are unique to this file.

**NEW backend:** None if deleted. If PEWS-for-parent is desired, reuse `/api/v1/pews/student/[code]` with parent-scoped auth.

**Migration steps:** 1) Inspect `ParentPewsScreenV2.kt` for unique API calls or UI. 2) If nothing unique → delete file. 3) If PEWS-for-parent is a desired feature → add `ParentOverlay.Pews` enum value + wire in `ParentPortalV2.kt`. 4) QA.

**Regression checklist:**
- [ ] `ParentPewsScreenV2.kt` deleted or wired
- [ ] No compile errors
- [ ] Parent portal tabs all render correctly
- [ ] `ParentPulseScreen` (the WIRED pulse screen) still works

---

## Entry #22 — Orphaned StudentLibraryScreen.kt

**BEFORE:** `StudentLibraryScreen.kt` (1019 lines) exists but is only referenced within itself. No overlay enum maps to it. The parent library overlay (C-6.15) uses `ParentLibraryScreenV2.kt` (different file). This screen is completely unreachable.

**WHY:** Iteration 9 (Re-Audit) — orphaned screen not caught in earlier iterations.

**AFTER:** Either (a) delete `StudentLibraryScreen.kt` if `ParentLibraryScreenV2` covers the same functionality, or (b) wire it as a student-scoped library view within the Parent Library overlay (C-6.15) as a sub-tab. FILE 1 ref: D-6.

**OLD backend:** Verify if any API calls are unique to this file.

**NEW backend:** None if deleted. If student-scoped library is desired, reuse `/api/v1/library/student?studentId=X`.

**Migration steps:** 1) Compare `StudentLibraryScreen.kt` vs `ParentLibraryScreenV2.kt` for overlap. 2) If overlapping → delete `StudentLibraryScreen.kt`. 3) If unique student-scoped features exist → add as sub-tab in `ParentLibraryScreenV2`. 4) QA.

**Regression checklist:**
- [ ] `StudentLibraryScreen.kt` deleted or wired
- [ ] No compile errors
- [ ] Parent Library overlay still renders correctly

---

## Summary

| # | Issue | Type | Severity |
|---|---|---|---|
| 1 | SuperAdmin role gating | Role gap | Medium |
| 2 | Notifications sharing | Intentional | Low |
| 3 | Legacy vs premium calendar | Duplication | Medium |
| 4 | 3 message composables | Code duplication | Medium |
| 5 | Library tab naming | Inconsistent labels | Low |
| 6 | Transport route validation | Security gap | High |
| 7 | PEWS UI duplication + AUTH-018 | Code + Security | Medium |
| 8 | Digital ID Card sharing | Intentional | None |
| 9 | Scheduled Messages sharing | Intentional | None |
| 10 | Orphaned ScholarshipsScreenV2 | Dead code | Low |
| 11 | Profile naming collision | UX confusion | Medium |
| 12 | Messages overlay vs tab | Redundancy | Medium |
| 13 | Discovery + LinkChild sharing | Intentional | None |
| 14 | Orphaned CommonLandingScreenV2 | Dead code | Low |
| 15 | Event Registration variants | Intentional | None |
| 16 | Orphaned SchoolDayConfig | Dead code | Medium |
| 17 | Orphaned PewsEffectiveness | Dead code | Medium |
| 18 | Orphaned Tutor plan/practice | Dead code | Medium |
| 19 | Dead SchoolOverlay.Calendar enum | Dead code | Medium |
| 20 | Dead SchoolOverlay.Results enum | Dead code | Medium |
| 21 | Orphaned ParentPewsScreenV2 | Dead code | Medium |
| 22 | Orphaned StudentLibraryScreen | Dead code | Medium |

**Intentional duplications (keep):** #2, #5, #8, #9, #13, #15
**Restructure actions:** #1, #3, #4, #6, #7, #10, #11, #12, #14, #16, #17, #18, #19, #20, #21, #22

---

*End of FILE 2 — ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md*
