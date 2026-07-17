# Gamification Feature — Comprehensive Audit Report

**Date:** 2026-07-16  
**Auditor:** Cascade AI  
**Scope:** Teacher, Admin, Parent portals — full chain from server routes to UI buttons  
**Spec:** `GAMIFICATION_SYSTEM_SPEC.md`

---

## Executive Summary

The gamification feature is **structurally wired end-to-end** — all 3 rounds confirm that server routes, DTOs, API client, repository, ViewModels, UI screens, navigation, and buttons are connected. However, there are **19 issues** ranging from CRITICAL data corruption to MEDIUM UX problems.

**The #1 root cause** of the user-reported "450 XP in teacher, 15 XP in parent" bug is a **childId/studentId mismatch** in the parent portal's gamification stats endpoint. The parent route passes `ChildrenTable.id` to `GamificationService.getStudentStats()` which expects `StudentsTable.id`. These are different UUIDs.

**The #2 root cause** of "buttons not visible" is the `TeacherClassGamificationCard` cramming 10+ sections (overview, leaderboard, goals, pep talk, goal creator, shoutouts, mentors, mentor form, study buddies, buddy form) into a single `VtCard` inside a `LazyColumn` item, causing excessive height and potential clipping.

---

## Plan Summary Table

| Spec Feature | Server | API | Repo | VM | UI | Status |
|---|---|---|---|---|---|---|
| XP Engine + Levels | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Badges (award + view) | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Quests (assign + view) | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Houses | ✅ | ✅ | ✅ | ✅ | ✅ (admin only) | Partial |
| Rewards + Redemptions | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Leaderboards | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Shoutouts | ✅ | ✅ | ✅ | ✅ | ⚠️ Names missing | Partial |
| Class Goals | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Mentor System | ✅ | ✅ | ✅ | ✅ | ⚠️ UUID input | Partial |
| Study Buddy | ✅ | ✅ | ✅ | ✅ | ⚠️ UUID input | Partial |
| XP Boosts | ✅ | ✅ | ✅ | ✅ | ⚠️ Missing targetId | Partial |
| Seasonal Events | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Kill Switch / Flags | ✅ | ✅ | ✅ | ✅ | ✅ | Working |
| Analytics | ✅ | ✅ | ✅ | ✅ | ⚠️ No empty state | Partial |
| Combos | ❌ | ❌ | ❌ | ❌ | ❌ | Missing |
| Catch-up Mechanic | ❌ | ❌ | ❌ | ❌ | ❌ | Missing |
| Admin CRUD (badges/levels/houses/rewards/quests/events) | ❌ | ❌ | ❌ | ❌ | ❌ | Missing |
| Student Titles | ❌ | ❌ | ❌ | ❌ | ❌ | Missing |

---

## Round 1: Full-Chain Wiring Audit

### 1.1 Server Routes (`GamificationRouting.kt`)

**Route registration:** `gamificationRouting()` called in `Application.kt:744` ✅

| Route Group | Endpoints | Status |
|---|---|---|
| Parent `/api/v1/parent/gamification` | 12 GET + 1 POST | ✅ All present |
| Teacher `/api/v1/teacher/gamification` | 10 GET + 8 POST/PUT/DELETE | ✅ All present |
| Admin `/api/v1/admin/gamification` | 8 GET + 3 PUT/POST | ✅ All present |
| Parent events `/api/v1/parent/gamification/events` | 1 GET | ✅ Present |

### 1.2 Shared Models (`GamificationModels.kt`)
All 17 domain models present and serializable ✅

### 1.3 API Client (`GamificationApi.kt`)
All 38 API methods match server routes — HTTP method, path, body, and response type ✅

### 1.4 Repository (`GamificationRepository.kt` + `GamificationRepositoryImpl.kt`)
Interface defines all methods; Impl delegates to API ✅

### 1.5 ViewModels (`ParentGamificationViewModel.kt`)
- `ParentGamificationViewModel`: 2 functions (load, redeemReward) ✅
- `TeacherGamificationViewModel`: 16 functions ✅
- `AdminGamificationViewModel`: 5 functions ✅

### 1.6 Koin DI (`Koin.kt`)
- `GamificationApi`: single ✅
- `GamificationRepository`: single ✅
- 3 ViewModels: factory ✅

### 1.7 UI Screens
| Portal | Screen | Embedded In | Line | Status |
|---|---|---|---|---|
| Teacher | `TeacherStudentGamificationCard` | `TeacherStudentProfileScreenV2` | 121 | ✅ |
| Teacher | `TeacherClassGamificationCard` | `TeacherClassesScreenV2` | 605 | ✅ |
| Admin | `AdminGamificationScreenV2` | `SchoolPortalV2` (overlay) | 632 | ✅ |
| Parent | `GamificationCollapsibleSection` | `ParentProfileCardScreenV2` | 348 | ✅ |

### 1.8 Navigation
- Admin: `SchoolOverlay.GamificationManagement` + deep link "gamification" + `onOpenGamification` button ✅
- Teacher: Embedded in student profile and class detail — no separate overlay ✅
- Parent: Embedded in profile card — no separate overlay ✅

### 1.9 Button Inventory

**Teacher Student Card** (6 action buttons):
1. Encourage ✅ 2. Spotlight ✅ 3. Send Shoutout ✅ 4. Assign Quest ✅ 5. Award Badge ✅ 6. Parent Alert ✅

**Teacher Class Card** (5 action buttons):
1. Pep Talk ✅ 2. Create Class Goal ✅ 3. Assign Mentor ✅ 4. Pair Study Buddies ✅ 5. Delete Shoutout ✅

**Admin Screen** (4 action areas):
1. Enable/Disable toggle ✅ 2. 10 granular flag toggles ✅ 3. Approve/Reject redemption ✅ 4. Create Boost ✅

**Parent Section** (1 action button):
1. Redeem Reward ✅

---

## Round 2: Functional & UX Audit

### Complete Issue Table

| ID | Severity | Layer | Portal | Issue | Root Cause |
|---|---|---|---|---|---|
| GAM-001 | **CRITICAL** | Server | Parent | XP data mismatch: teacher sees 450 XP, parent sees 15 XP | Parent route passes `ChildrenTable.id` to `getStudentStats()` which expects `StudentsTable.id` — no join via `studentCode` |
| GAM-002 | **CRITICAL** | UI | Teacher | Buttons hidden/clipped in class gamification card | 10+ sections in single `VtCard` inside `LazyColumn` item — excessive height causes clipping |
| GAM-003 | **HIGH** | UI | Teacher | Mentor/study-buddy forms require raw UUID input | Teachers must type student UUIDs in text fields — no student name picker |
| GAM-004 | **HIGH** | Server+UI | Teacher | Shoutout list shows "Unknown" for sender/receiver names | `ShoutoutDto` returns UUIDs, not names — UI expects `senderName`/`receiverName` |
| GAM-005 | **HIGH** | Server+UI | Admin | Redemption list shows "Unknown" for reward names | `AdminRedemptionDto` missing `rewardName` field — UI expects it |
| GAM-006 | **HIGH** | UI | Teacher | Student gamification card doesn't show XP/level/streak | VM never calls `GET /teacher/gamification/student/{id}/stats` — only loads badges |
| GAM-007 | **HIGH** | UI | Parent | XP fallback calculation shows fabricated XP when API fails | `gameStats?.totalXp ?: (track.overallProgress * 5000)` — produces arbitrary number |
| GAM-008 | **MEDIUM** | UI | Admin | No CRUD for badges, levels, houses, rewards, quests, events | Server has only GET endpoints; no create/update/delete routes |
| GAM-009 | **MEDIUM** | UI | Admin | Boost form missing target ID field | `targetId` hardcoded to `null` — needed when scope is STUDENT or CLASS |
| GAM-010 | **MEDIUM** | UI | Admin | Hardcoded English strings in card titles | "Badge Catalog", "Level Definitions", etc. — not using StringKeys |
| GAM-011 | **MEDIUM** | UI | Parent | Gamification section collapsed by default | `expanded = false` — parents may not discover gamification data |
| GAM-012 | **MEDIUM** | UI | Parent | XP progress bar uses fixed 5000 max | `xpMax = 5000` — bar caps at 100% for students with >5000 XP |
| GAM-013 | **MEDIUM** | UI | Parent | Redeem button has no confirmation dialog | Irreversible XP spend without confirmation |
| GAM-014 | **MEDIUM** | UI | Teacher | Mentor/buddy assignments show UUID fragments instead of names | `mId.take(8)` — shows "a1b2c3d4" instead of student name |
| GAM-015 | **LOW** | UI | Admin | Analytics card silently disappears if analytics is null | `?: return` — no empty state |
| GAM-016 | **LOW** | UI | Admin | No pull-to-refresh on admin gamification screen | Missing `VPullRefresh` wrapper |
| GAM-017 | **LOW** | Spec | All | Combos system not implemented | Spec describes combo multipliers — no server routes or UI |
| GAM-018 | **LOW** | Spec | All | Catch-up mechanic not implemented | Spec describes bottom-25% boost — no server logic |
| GAM-019 | **LOW** | Spec | All | Student titles system not implemented | Spec describes earnable titles — no UI to select/display |

---

## Round 3: Plan Optimization & Clutter Reduction

### Feature Completeness vs Spec
- **Missing**: Combos, catch-up mechanic, admin CRUD, student titles, badge criteria editor
- **Partial**: Mentor/study-buddy (works but unusable with UUID input), shoutouts (missing names), houses (admin view only, no assignment UI)

### UI Clutter Audit
- **Teacher `TeacherClassGamificationCard`**: 10+ sections in one card — should be split into tabbed sub-sections or separate cards
- **Admin screen**: 11 cards in single LazyColumn — could benefit from section grouping (Config, Content, Operations)
- **Parent `GamificationCollapsibleSection`**: 8 sub-sections in expanded view — acceptable since collapsible

### Navigation Flattening
- Teacher gamification is embedded in student profile and class detail — no dedicated gamification overlay. This is fine for now but limits discoverability.
- Admin gamification is a single overlay — appropriate.
- Parent gamification is a collapsible section in profile — appropriate but collapsed by default.

### Consistency Audit
- XP display inconsistent: Teacher shows overview totals, parent shows `totalXp` with fallback, admin shows analytics totals
- Student identification inconsistent: Teacher uses UUIDs, parent uses child name, admin uses `studentId.takeLast(6)`
- No shared gamification summary component across portals

### Dead Code
- `StudentStats.catchUpActive` — field exists but never used in any UI
- `XpAwardResult` — model exists but result details never shown to teacher (only generic message)
- `getLevelDefinitions()` — called by admin VM but levels only shown as a flat list with no actions

---

## Fix Priority Order

### Batch 1 — Critical Data Fix (do first)
1. **GAM-001**: Fix parent portal childId→studentId mapping in gamification stats
   - **File**: `GamificationRouting.kt` lines 256-276
   - **Fix**: After verifying child ownership via `ChildrenTable`, join to `StudentsTable` via `studentCode` to get the real `studentId`, then pass that to `getStudentStats()`
   - **Also fix**: All parent gamification endpoints (badges, quests, house, xp-history, boosts, class-goals, leaderboard) that pass `childId` directly — they all need the same `childId → studentId` resolution

2. **GAM-007**: Remove fabricated XP fallback in parent portal
   - **File**: `ParentProfileCardScreenV2.kt` line 319
   - **Fix**: Replace `(track.overallProgress * 5000).roundToInt()` with `0` — show "0 XP" when API fails, not a fake number

### Batch 2 — Server DTO Fixes
3. **GAM-004**: Add `senderName`/`receiverName` to `ShoutoutDto`
   - **File**: `GamificationRouting.kt` — shoutout list endpoint (line 768-789)
   - **Fix**: Join `GameShoutoutsTable` with `StudentsTable` (or `AppUsersTable`) to resolve names

4. **GAM-005**: Add `rewardName` to `AdminRedemptionDto`
   - **File**: `GamificationRouting.kt` — admin redemptions endpoint (line 1160-1178)
   - **Fix**: Join `GameRewardRedemptionsTable` with `GameRewardsTable` to get reward name

### Batch 3 — Teacher UI Fixes
5. **GAM-002**: Split `TeacherClassGamificationCard` into sub-cards or tabs
   - **File**: `TeacherGamificationScreenV2.kt` lines 274-616
   - **Fix**: Break into 3-4 separate `VtCard` composables: (1) Overview + Leaderboard, (2) Class Goals, (3) Shoutouts, (4) Mentors + Study Buddies

6. **GAM-006**: Add student stats display to `TeacherStudentGamificationCard`
   - **File**: `TeacherGamificationScreenV2.kt` lines 58-266
   - **Fix**: Call `repository.getStudentStats()` (new VM function) and display XP/level/streak at top of card

7. **GAM-003 + GAM-014**: Replace UUID text fields with student name pickers
   - **Files**: `TeacherGamificationScreenV2.kt` lines 479-513, 555-589
   - **Fix**: Use dropdown/dropdown menu populated with class roster student names

### Batch 4 — Parent UI Fixes
8. **GAM-011**: Expand gamification section by default
   - **File**: `ParentProfileCardScreenV2.kt` line 1322
   - **Fix**: `var expanded by remember { mutableStateOf(true) }`

9. **GAM-012**: Use level-based XP progress instead of fixed 5000
   - **File**: `ParentProfileCardScreenV2.kt` lines 396-397
   - **Fix**: Use `currentXp` / `xpRequiredForNextLevel` from `StudentStats` instead of `totalXp / 5000`

10. **GAM-013**: Add confirmation dialog for reward redemption
    - **File**: `ParentProfileCardScreenV2.kt` lines 1021-1034
    - **Fix**: Show `AlertDialog` before calling `onRedeem()`

### Batch 5 — Admin UI Fixes
11. **GAM-009**: Add target ID field to boost form
    - **File**: `AdminGamificationScreenV2.kt` lines 578-638
    - **Fix**: Add `OutlinedTextField` for target ID, show only when scope is STUDENT or CLASS

12. **GAM-015**: Add empty state for analytics card
    - **File**: `AdminGamificationScreenV2.kt` line 295
    - **Fix**: Replace `?: return` with empty state composable

13. **GAM-016**: Add pull-to-refresh to admin screen
    - **File**: `AdminGamificationScreenV2.kt` line 149
    - **Fix**: Wrap `LazyColumn` with `VPullRefresh`

14. **GAM-010**: Replace hardcoded strings with StringKeys
    - **File**: `AdminGamificationScreenV2.kt` — multiple lines
    - **Fix**: Add StringKeys entries and use `appString()`

### Batch 6 — Spec Gap Features (lower priority)
15. **GAM-008**: Admin CRUD for badges, levels, houses, rewards, quests, events
    - Requires server POST/PUT/DELETE routes + API methods + VM functions + UI forms
16. **GAM-017**: Combos system
17. **GAM-018**: Catch-up mechanic
18. **GAM-019**: Student titles system

---

## Verification Commands

After fixes, verify with:
```bash
# Server compile
./gradlew :server:compileKotlin

# Shared compile
./gradlew :shared:compileKotlinJvm

# composeApp compile
./gradlew :composeApp:compileDebugKotlinAndroid

# Full build
./gradlew build
```
