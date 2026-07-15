# Teacher Portal Deep Debug Audit Plan

> **Created**: 2026-07-15  
> **Scope**: Every tab, sub-tab, overlay, and shared component in the Teacher Portal  
> **Method**: Round 1 (18 checks) + Round 2 (12 checks) per screen  
> **Status**: IN PROGRESS  

---

## Portal Architecture Overview

### 5 Bottom-Nav Tabs
| Tab | Screen File | ViewModel |
|-----|------------|-----------|
| Home | TeacherHomeScreenV2.kt | TeacherTodayViewModel, TeacherCheckInViewModel, TeacherObligationsViewModel, TeacherClassesViewModel, TeacherEventRegistrationViewModel, TeacherInsightsViewModel |
| Update | TeacherUpdateScreenV2.kt | TeacherClassesViewModel (+ sub-tool VMs) |
| Classes | TeacherClassesScreenV2.kt | TeacherClassesViewModel, TeacherStudentProfileViewModel |
| Timetable | TeacherTimetableScreenV2.kt | TeacherTimetableViewModel |
| Profile | TeacherProfileScreenV2.kt | TeacherProfileViewModel, TeacherProfileActionsViewModel |

### Update Tab Sub-Tools (ToolRail)
| Tool | Screen File | ViewModel |
|------|------------|-----------|
| Attendance | TeacherAttendanceScreenV2.kt | TeacherAttendanceViewModel |
| Marks | TeacherMarksScreenV2.kt | TeacherGradebookViewModel |
| Syllabus | TeacherSyllabusScreenV2.kt | TeacherSyllabusViewModel |
| Homework | TeacherHomeworkScreenV2.kt | TeacherHomeworkViewModel |
| LessonPlan | TeacherLessonPlanScreenV2.kt | TeacherLessonPlanViewModel |

### Classes Tab Sub-Navigation
| Level | Screen File | ViewModel |
|-------|------------|-----------|
| Class List | TeacherClassesScreenV2.kt (inline) | TeacherClassesViewModel |
| Student Profile | TeacherStudentProfileScreenV2.kt | TeacherStudentProfileViewModel |

### 22 Overlay Screens
| # | Overlay | Screen File | Entry Point |
|---|---------|------------|-------------|
| 1 | Notifications | NotificationsScreenV2 | Header bell (all tabs) |
| 2 | NotificationPreferences | NotificationPreferencesScreenV2 | Notifications → gear icon |
| 3 | HealthAlerts | TeacherHealthAlertsScreenV2 | Home → health card |
| 4 | TransportAttendance | TransportAttendanceScreenV2 | Home → transport card |
| 5 | Pews | TeacherPewsScreenV2 | Home → PEWS card |
| 6 | ReportReview | TeacherReportReviewQueueScreen | Home → report card |
| 7 | ReportDraftEditor | TeacherReportDraftEditorScreen | ReportReview → edit draft |
| 8 | Heatmap | TeacherHeatmapScreen | Home → tutor/heatmap |
| 9 | DigitalIdCard | DigitalIdCardScreen | Home → ID card |
| 10 | ScheduledMessages | ScheduledMessagesScreenV2 | Home → scheduled messages |
| 11 | EventRegistration | TeacherPtmEventRegistrationScreenV2 | Home → events |
| 12 | Messages | TeacherMessagesScreenV2 | Home → messages |
| 13 | Calendar | AcademicCalendarScreenV2 | Home → calendar |
| 14 | AnnouncementDetail | TeacherAnnouncementDetailScreen | Home → announcement |
| 15 | LeaveRequests | TeacherLeaveRequestsScreenV2 | Home/Profile → leave |
| 16 | ExamTimetableList | ExamTimetableListScreen | Home → exam timetable |
| 17 | ExamTimetableUpload | ExamTimetableUploadScreen | ExamTimetableList → New |
| 18 | ExamTimetableDetail | ExamTimetableDetailScreen | ExamTimetableList → open |
| 19 | ExamSyllabusMapping | ExamSyllabusMappingScreen | ExamTimetableDetail → map syllabus |
| 20 | ExamMarksImport | ExamMarksImportScreen | Update → import marks |
| 21 | Export | ExportScreen | Home → export |
| 22 | SalaryHistory | TeacherSalaryOverlayScreen | Profile → salary |

### Shared Components
| Component | File |
|-----------|------|
| TeacherPremiumHeader | TeacherHeader.kt |
| TeacherDock | TeacherDock.kt |
| TeacherScopeSelector | TeacherScopeSelector.kt |
| TeacherCheckInPopup | TeacherCheckInPopup.kt |
| TeacherDialogs | TeacherDialogs.kt |
| TeacherKitV2 (shared utils) | TeacherKitV2.kt |
| TeacherKit (legacy utils) | TeacherKit.kt |

---

## Audit Checklist — Round 1 (Per Screen)

| # | Check | Description |
|---|-------|-------------|
| 1 | Backend route exists and is registered | Route in server routing file |
| 2 | Server DTOs match shared models | Field-for-field match |
| 3 | Server logic is implemented (not stubbed) | No TODO/stub/placeholder |
| 4 | API client method calls correct endpoint | TeacherApi.kt method → correct URL |
| 5 | Repository interface + implementation both exist | Interface + impl in shared |
| 6 | ViewModel has state property + function calling repository | VM → repo → API chain |
| 7 | ViewModel registered in Koin | Koin.kt module |
| 8 | UI screen uses correct design tokens | VColors, VTypography, VCard, VButton |
| 9 | VCard has no double padding | No Modifier.padding inside VCard |
| 10 | VButton uses `full = true` not `Modifier.fillMaxWidth()` | System pattern |
| 11 | Loading state shows proper spinner | Not text-only |
| 12 | Error state shows message + retry button | VStateHost or equivalent |
| 13 | Empty state shows actionable message + icon | Not blank |
| 14 | Overlay enum + rendering branch both exist | TeacherOverlay + when branch |
| 15 | Callback chain complete | No broken links |
| 16 | Source button visible and tappable | Not hidden/buried |
| 17 | All elements within screen bounds | No overflow |
| 18 | Zero hardcoded/fake data in UI | All data from VM |

## Audit Checklist — Round 2 (Per Screen)

| # | Check | Description |
|---|-------|-------------|
| 1 | Happy path works end-to-end | Tap → navigate → load → interact → success |
| 2 | API error → error state + retry | Error shown, retry re-calls API |
| 3 | Network failure → no crash | Error or cached data, no blank screen |
| 4 | Empty data → actionable empty state | No crash, no blank |
| 5 | Loading → content smooth | No flicker, no flash of empty |
| 6 | Back navigation works | Returns to correct screen |
| 7 | Primary button accessible on scroll | Pinned or visible at end |
| 8 | Long content truncates properly | No elements pushed off-screen |
| 9 | State persistence on return | Reloads or persists correctly |
| 10 | Double-tap safety | No double-submit |
| 11 | Small screen (320dp) fits | No overflow |
| 12 | Entry point reachable in ≤2 taps | No dead-ends |

---

## Audit Execution Order

### Phase 1: Tab Screens (5 tabs)
- [ ] **1.1** Home Tab — TeacherHomeScreenV2
- [ ] **1.2** Update Tab — TeacherUpdateScreenV2 (shell + scope gate)
- [ ] **1.3** Update → Attendance — TeacherAttendanceScreenV2
- [ ] **1.4** Update → Marks — TeacherMarksScreenV2
- [ ] **1.5** Update → Syllabus — TeacherSyllabusScreenV2
- [ ] **1.6** Update → Homework — TeacherHomeworkScreenV2
- [ ] **1.7** Update → LessonPlan — TeacherLessonPlanScreenV2
- [ ] **1.8** Classes Tab — TeacherClassesScreenV2
- [ ] **1.9** Classes → Student Profile — TeacherStudentProfileScreenV2
- [ ] **1.10** Timetable Tab — TeacherTimetableScreenV2
- [ ] **1.11** Profile Tab — TeacherProfileScreenV2

### Phase 2: Overlay Screens (22 overlays)
- [ ] **2.1** Notifications
- [ ] **2.2** NotificationPreferences
- [ ] **2.3** HealthAlerts
- [ ] **2.4** TransportAttendance
- [ ] **2.5** Pews
- [ ] **2.6** ReportReview
- [ ] **2.7** ReportDraftEditor
- [ ] **2.8** Heatmap
- [ ] **2.9** DigitalIdCard
- [ ] **2.10** ScheduledMessages
- [ ] **2.11** EventRegistration
- [ ] **2.12** Messages
- [ ] **2.13** Calendar
- [ ] **2.14** AnnouncementDetail
- [ ] **2.15** LeaveRequests
- [ ] **2.16** ExamTimetableList
- [ ] **2.17** ExamTimetableUpload
- [ ] **2.18** ExamTimetableDetail
- [ ] **2.19** ExamSyllabusMapping
- [ ] **2.20** ExamMarksImport
- [ ] **2.21** Export
- [ ] **2.22** SalaryHistory

### Phase 3: Shared Components
- [ ] **3.1** TeacherPremiumHeader
- [ ] **3.2** TeacherDock
- [ ] **3.3** TeacherScopeSelector
- [ ] **3.4** TeacherCheckInPopup
- [ ] **3.5** TeacherDialogs
- [ ] **3.6** TeacherKitV2

### Phase 4: Cross-Cutting Concerns
- [ ] **4.1** Deep link routing completeness
- [ ] **4.2** Back navigation hierarchy
- [ ] **4.3** Offline/online banner handling
- [ ] **4.4** Koin module registration completeness
- [ ] **4.5** Overlay state management (no stale state on reopen)

---

## Findings — Issues Found & Suggested Fixes

> Based on full code review of all 30 teacher screen files. Issues categorized by severity: **CRITICAL** (crash/broken flow), **MAJOR** (wrong pattern/UX problem), **MINOR** (style/consistency).

---

### 1. Home Tab — TeacherHomeScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MINOR**: `VTypography.bodySmall` used in several places (lines 198, 218, 221). `bodySmall` is not in the canonical token set (`h2`, `h3`, `body`, `caption`, `label`). **Fix**: Replace with `VTypography.caption` or `VTypography.body` depending on context.
- **MINOR**: `subjectColor()` helper duplicates the same logic as `vtSubjectColor()` in `TeacherKitV2.kt`. **Fix**: Remove local helper, use the shared `vtSubjectColor()` from `TeacherKitV2`.
- **MINOR**: `SkeletonClassRow` and `SkeletonEventRow` use raw `VColors.surfaceTint` boxes instead of a proper skeleton component. **Fix**: Use `SkeletonList` from `VStateHost` or create a shared `VSkeletonRow` component.
**Status**: COMPLETE

---

### 2. Update Tab — TeacherUpdateScreenV2.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: `VTypography.bodySmall` used at line 198, 218, 221. Same token issue as Home. **Fix**: Replace with `VTypography.caption`.
- **MINOR**: `VTypography.label` used for tool pill text (line 355) — correct, but `bodySmall` used for scope description (line 198). **Fix**: Use `VTypography.caption` for consistency.
**Status**: COMPLETE

---

### 3. Attendance — TeacherAttendanceScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: No double-tap protection on `StatusChip` clicks. A teacher could rapidly tap Present/Absent and send multiple API calls. **Fix**: Add `enabled = !state.isMarking` or debounce on the VM side.
- **MINOR**: `AbsentNotifyBanner` uses `VColors.coral` directly for text, but the canonical danger color is `VColors.error`. **Fix**: Use `VColors.error` for error/danger text, `VColors.coral` is an accent color.
- **MINOR**: `AttendanceInsightsBody` PEWS view doesn't have a loading state of its own — relies on parent `AttendanceBody` loading. If PEWS loads separately, no spinner shows. **Fix**: Add `pewsState.isLoading` check in `AttendanceInsightsBody`.
**Status**: NEEDS REWORK (double-tap fix)

---

### 4. Marks — TeacherMarksScreenV2.kt
**Round 1**: PASS
**Issues Found**:
- **MAJOR**: `MarkInput` `OutlinedTextField` has no debounce — every keystroke could trigger a save if `onMarkChange` calls the VM directly. **Fix**: Buffer input locally and save on focus lost or after a delay.
- **MINOR**: `VTypography.bodySmall` used in `AssessmentRow` for subtitle text. **Fix**: Replace with `VTypography.caption`.
- **MINOR**: `CreateAssessmentComposer` uses raw `OutlinedTextField` instead of a `VTextField` component if one exists. **Fix**: Use shared input component for consistency.
**Status**: NEEDS REWORK (mark input debounce)

---

### 5. Syllabus — TeacherSyllabusScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: `ParseSyllabusSheet` AI parsing has no error state for when the AI returns garbage/unparseable text. The sheet shows a loading spinner but no "parse failed" fallback. **Fix**: Add parse error state in the VM and show a retry button in the sheet.
- **MINOR**: `DailyLogPopup` uses `verticalScroll` inside a `VBottomSheet` — this can conflict with the sheet's own drag-to-dismiss gesture. **Fix**: Use `LazyColumn` or `weight(1f)` with bounded height inside the sheet.
- **MINOR**: `QuizSheet` quiz question rendering uses raw `Column` with `forEach` instead of `LazyColumn` for questions. If a quiz has 50+ questions, this could cause performance issues. **Fix**: Use `LazyColumn` for question lists.
**Status**: NEEDS REWORK (parse error state)

---

### 6. Homework — TeacherHomeworkScreenV2.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: `HomeworkComposer` date picker uses raw `OutlinedTextField` with no date validation (e.g., due date in the past). **Fix**: Add minimum date validation.
- **MINOR**: `BoardStudentRow` submission status uses raw `Box` with background color instead of `VBadge` for status. **Fix**: Use `VBadge` with appropriate `VBadgeTone` for submitted/graded/pending states.
- **MINOR**: `ExtensionSheet` has no max extension date validation. **Fix**: Add a reasonable upper bound (e.g., end of term).
**Status**: COMPLETE

---

### 7. LessonPlan — TeacherLessonPlanScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: `LessonPlanEditorMode` has a large form with multiple `OutlinedTextField`s inside a `verticalScroll`. On small screens, the keyboard could cover the bottom fields with no scroll-to-focused-field logic. **Fix**: Add `scrollToFocusedField` or use `LazyColumn` with `imePadding`.
- **MINOR**: `LessonPlanCalendarMode` uses a simple `Row` of day cells — not a real calendar grid. This is a functional but not premium calendar. **Fix**: Use a proper calendar component or `VCalendar` if available.
- **MINOR**: `SaveTemplateDialog` uses raw `AlertDialog` instead of `VBottomSheet` or `VDialog`. **Fix**: Use the design system's dialog/sheet component.
**Status**: NEEDS REWORK (keyboard scroll issue)

---

### 8. Classes — TeacherClassesScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Uses `TCard`, `TEyebrow`, `TIconDisc`, `TPill`, `TRing` — these are aliases defined locally in the file, not from the shared `TeacherKitV2`. This creates a parallel component system. **Fix**: Replace local aliases with `VtCard`, `VtEyebrow`, `VtIconDisc`, `VtPill` from `TeacherKitV2.kt`.
- **MINOR**: `RosterRow` uses `VtT.bodySmall` which is not a canonical token. **Fix**: Use `VtT.caption` or `VtT.body`.
- **MINOR**: `primaryFlag()` helper duplicates flag logic that should be centralized. **Fix**: Move to a shared `StudentFlags` utility.
**Status**: NEEDS REWORK (local component aliases)

---

### 9. StudentProfile — TeacherStudentProfileScreenV2.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: `attendanceDayColor()` returns raw `Color` values instead of using `VColors` tokens. **Fix**: Map to `VColors.success`, `VColors.error`, `VColors.gold`, etc.
- **MINOR**: `ParentContactCard` shows parent phone/mobile without any tap-to-call or tap-to-message action. **Fix**: Add `clickable` modifier with intent to dial or open messages.
- **MINOR**: `TrendPill` uses raw `VColors.success`/`VColors.error` backgrounds with alpha — should use `VColors.mintSoft`/`VColors.errorSoft` for consistency. **Fix**: Use soft token variants.
**Status**: COMPLETE

---

### 10. Timetable — TeacherTimetableScreenV2.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: `ChangeRequestSheet` uses raw `OutlinedTextField` for time inputs instead of a time picker. **Fix**: Use a proper time picker component or at minimum validate time format.
- **MINOR**: `periodAccent()` and `periodSoft()` duplicate `vtSubjectColor()` logic from `TeacherKitV2`. **Fix**: Use the shared helper.
- **MINOR**: Day rail only shows Mon–Sat. Sunday is excluded, but some schools may have Sunday classes. **Fix**: Make day list dynamic from VM data or include Sunday conditionally.
**Status**: COMPLETE

---

### 11. Profile — TeacherProfileScreenV2.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: `PasswordForm` has no password strength indicator. **Fix**: Add a simple strength meter (length + character variety check).
- **MINOR**: `LeaveComposer` date range picker uses raw date text fields. **Fix**: Use a proper date picker component.
- **MINOR**: `VThemePicker` and `VLanguagePicker` are defined inline in the profile screen. **Fix**: Extract to shared components if reused by Parent Portal.
**Status**: COMPLETE

---

### 12. HealthAlerts — TeacherHealthAlertsScreenV2.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **MAJOR**: Uses `.padding(16.dp)` for content area (line 78) — violates the 20dp horizontal padding standard. **Fix**: Change to `.padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp)`.
- **MAJOR**: Uses `VtT.bodyStrong` and `VtT.caption` — `bodyStrong` is not a canonical token in `VTypography`. **Fix**: Use `VtT.body` with `FontWeight.SemiBold` modifier.
- **MINOR**: `parseJsonArray()` is a local helper that manually parses JSON. **Fix**: Use `kotlinx.serialization.json.Json` for robust parsing.
- **MINOR**: No `VPullRefresh` wrapper — only `VStateHost`. **Fix**: Add `VPullRefresh` for pull-to-refresh on the alerts list.
**Status**: NEEDS REWORK (padding + token issues)

---

### 13. Pews — TeacherPewsScreenV2.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **MAJOR**: Defines its own `PewsColors` and `PewsType` private objects (lines 78-96) instead of using `VtC`/`VtT` from `TeacherKitV2`. This creates a third parallel token system. **Fix**: Replace `PewsColors`/`PewsType` with `VtC`/`VtT`.
- **MAJOR**: `TeacherStudentCard` is a massive 230-line composable with deeply nested `Column`/`Row`/`Box` hierarchy. Hard to maintain and test. **Fix**: Extract sub-components: `StudentHeaderRow`, `StudentMetricsRow`, `StudentSignalsRow`, `InterventionCard`, `ParentDraftBox`.
- **MINOR**: Uses `PewsType.bodyStrong` which maps to `VTypography.body` — but `bodyStrong` is not a real token. **Fix**: Use `VTypography.body` with `FontWeight.SemiBold`.
- **MINOR**: `Spacer(Modifier.height(120.dp))` at end of list (line 172) — should use `TeacherDockClearance` constant. **Fix**: Replace with `TeacherDockClearance`.
- **MINOR**: Language dropdown uses raw `DropdownMenu` with hardcoded language list. **Fix**: Extract to a shared `LanguagePicker` component.
**Status**: NEEDS REWORK (token system + component extraction)

---

### 14. Messages — TeacherMessagesScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Thread list uses hardcoded English strings: "No messages yet", "Messages from parents and school admin will appear here.", "Send a message below to start the conversation.", "Type a message…". **Fix**: Replace with `appString(StringKeys.*)` for i18n.
- **MAJOR**: `TeacherThreadRow` uses `VtC.accent` for unread badge background and `VtC.accent` for unread time text — but `accent` is `VColors.violet`. Unread indicators should be more distinct. **Fix**: Use `VColors.violet` for badge, `VColors.ink3` for read time, `VColors.violet` for unread time.
- **MINOR**: `TeacherConversationContent` uses `VtC.accentTint` for chat background — verify this maps to a valid `VColors` token. **Fix**: Ensure `accentTint` maps to `VColors.violetSoft` or `VColors.surfaceTint`.
- **MINOR**: `reply` text state is not cleared when switching threads. **Fix**: Reset `reply` to `""` in `LaunchedEffect(state.openThreadId)`.
**Status**: NEEDS REWORK (i18n + unread state)

---

### 15. LeaveRequests — TeacherLeaveRequestsScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Uses hardcoded English strings: "Leave Requests", "No leave requests", "There are no student leave requests for your classes right now.", "Reject", "Approve". **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: `VTypography.bodySmall` used in `LeaveRequestCard` (line 148). **Fix**: Use `VTypography.body` or `VTypography.caption`.
- **MINOR**: `state.decisionError` displayed in a `VCard` at the bottom — could be missed by the user. **Fix**: Show as a snackbar or inline error near the action button.
**Status**: NEEDS REWORK (i18n)

---

### 16. AnnouncementDetail — TeacherAnnouncementDetailScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Uses hardcoded English strings: "Announcement", "Announcement unavailable", "This announcement may have been removed or is no longer available." **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: Uses `.padding(horizontal = 24.dp)` (line 102) — should be 20dp per standard. **Fix**: Change to `horizontal = 20.dp`.
- **MINOR**: No `VPullRefresh` — announcements could benefit from pull-to-refresh. **Fix**: Wrap content in `VPullRefresh`.
- **MINOR**: `htmlDecode()` is called on title and description but not on category. **Fix**: Also decode category if it could contain HTML entities.
**Status**: NEEDS REWORK (i18n + padding)

---

### 17. ReportReview — TeacherReportReviewQueueScreen.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **CRITICAL**: Uses `collectAsState()` (line 56) instead of `collectAsStateV2()` — inconsistent with the rest of the codebase which uses `collectAsStateV2()`. This may cause issues if `collectAsStateV2` has special lifecycle handling. **Fix**: Change to `collectAsStateV2()`.
- **MAJOR**: No `VBackHeader` — uses a custom header row with a `VButton` for back (line 73). Breaks the overlay pattern. **Fix**: Replace with `VBackHeader(title = ..., onBack = onBack)`.
- **MAJOR**: No `statusBarsPadding()` on the root `Column`. Content will overlap with the status bar on notch devices. **Fix**: Add `.statusBarsPadding()` to the root modifier.
- **MAJOR**: No `navigationBarsPadding()` — bottom content may be cut off. **Fix**: Add `.navigationBarsPadding()`.
- **MINOR**: `VCard` content uses `Modifier.padding(14.dp)` inside VCard (line 184) — double padding. **Fix**: Remove inner padding, let VCard handle it.
- **MINOR**: `CircularProgressIndicator` used directly (line 108) instead of `TeacherSpinner()`. **Fix**: Use `TeacherSpinner()`.
**Status**: NEEDS REWORK (critical: collectAsState, header, padding)

---

### 18. ReportDraftEditor — TeacherReportDraftEditorScreen.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **CRITICAL**: Uses `collectAsState()` (line 48) instead of `collectAsStateV2()`. **Fix**: Change to `collectAsStateV2()`.
- **MAJOR**: No `VBackHeader` — uses custom header with `VButton` for back (line 63). **Fix**: Replace with `VBackHeader`.
- **MAJOR**: No `statusBarsPadding()` or `navigationBarsPadding()`. **Fix**: Add both to root modifier.
- **MAJOR**: `VCard` content uses `Modifier.padding(14.dp)` inside VCard (line 86) — double padding. **Fix**: Remove inner padding.
- **MAJOR**: `OutlinedTextField` for draft content has a fixed `height(280.dp)` (line 99) — violates layout safety rule (no fixed heights on growing content). **Fix**: Use `weight(1f)` inside a `Column` with `fillMaxSize`.
- **MINOR**: `CircularProgressIndicator` used directly (line 70) instead of `TeacherSpinner()`. **Fix**: Use `TeacherSpinner()`.
- **MINOR**: No `imePadding()` — keyboard will cover save buttons when editing the text field. **Fix**: Add `.imePadding()` to the scrollable column.
**Status**: NEEDS REWORK (critical: collectAsState, header, padding, fixed height)

---

### 19. PTM EventRegistration — TeacherPtmEventRegistrationScreenV2.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **MAJOR**: Uses hardcoded English strings throughout: "PTM Detail", "PTM Events", "No PTM events scheduled", "No slots configured for this event", "No bookings for this slot", "Check In", "Checked In", "Student:", "attendee(s)". **Fix**: Replace with `appString(StringKeys.*)`.
- **MAJOR**: `VCard` content uses `Modifier.padding(16.dp)` inside VCard (lines 143, 187, 277) — double padding. **Fix**: Remove inner padding.
- **MINOR**: Uses emoji `📞` directly in text (line 248). **Fix**: Use `VIcons.Phone` icon instead.
- **MINOR**: `VStateHost` error parameter uses `state.errorMessage` but this is also used as the info message source — error and info states are conflated. **Fix**: Separate error and info message handling.
**Status**: NEEDS REWORK (i18n + VCard padding)

---

### 20. SalaryHistory — TeacherSalaryOverlayScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MINOR**: Uses hardcoded English strings: "Salary & Payments", "No Salary Records", "Your salary history will appear here once the school admin sets up your salary.", "Base:", "Allowances:", "Deductions:", "Net:", "Paid on:". **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: `VCard` content has no explicit `Column` arrangement — relies on VCard's default column. **Fix**: Wrap content in explicit `Column(verticalArrangement = Arrangement.spacedBy(8.dp))`.
- **MINOR**: Uses `.padding(horizontal = 16.dp)` (line 72) — should be 20dp. **Fix**: Change to `horizontal = 20.dp`.
**Status**: COMPLETE (minor i18n)

---

### 21. Gamification — TeacherGamificationScreenV2.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **MAJOR**: Uses hardcoded English strings throughout: "Gamification Tools", "Earned Badges", "Encourage", "Spotlight", "Send Shoutout", "Cancel Shoutout", "Assign Quest", "Award Badge", "Parent Alert", "Class Gamification", "Total XP", "Badges", "Quests", "Class Leaderboard", "Class Goals", "Send Pep Talk", "Confirm Pep Talk", "Create Class Goal", "Recent Shoutouts", "Mentor Assignments", "Study Buddy Pairs", "Remove", "Assign Mentor", "Pair Study Buddies", etc. **Fix**: Replace all with `appString(StringKeys.*)`.
- **MAJOR**: `TeacherClassGamificationCard` is a 330-line composable — too large. **Fix**: Extract into sub-components: `OverviewStatsRow`, `ClassLeaderboardSection`, `ClassGoalsSection`, `PepTalkSection`, `ShoutoutModerationSection`, `MentorAssignmentSection`, `StudyBuddySection`.
- **MAJOR**: Uses `VButton(modifier = Modifier.weight(1f))` in some places (lines 93, 97) — should use `full = true` when full-width is intended, or use `weight` only in `RowScope`. The current usage is correct for `Row` weight, but inconsistent with the `full` pattern for standalone buttons.
- **MINOR**: `LeaderboardRow` shows "Student #{last 6 chars of ID}" — not a real name. **Fix**: Load and display actual student names from the VM.
- **MINOR**: `ClassGoalRow` uses `Map<String, *>` for goal data — not type-safe. **Fix**: Create a `ClassGoalDto` data class.
- **MINOR**: `ShoutoutRow` uses `Map<String, *>` for shoutout data — not type-safe. **Fix**: Create a `ShoutoutDto` data class.
**Status**: NEEDS REWORK (i18n + component extraction + type safety)

---

### 22. Shared Components — TeacherKitV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MINOR**: `VtC` and `VtT` are bridge objects that map old token names to new ones. This is technical debt — the old names should eventually be removed and all references updated to use `VColors`/`VTypography` directly. **Fix**: Plan a migration to replace all `VtC`/`VtT` usages with direct `VColors`/`VTypography` references, then delete the bridge.
- **MINOR**: `VtT.bodyStrong` maps to `VTypography.body` — but `bodyStrong` is not a real token. This creates confusion. **Fix**: Remove `bodyStrong` from `VtT` and use `VTypography.body` with `FontWeight.SemiBold` at call sites.
- **MINOR**: `VtT.h3` maps to `VTypography.h3` — identity mapping, no transformation needed. **Fix**: Remove identity mappings from `VtT`.
**Status**: COMPLETE (tech debt)

---

### 23. Shared Components — TeacherHeader.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: `TeacherHeader` (legacy) is still defined but `TeacherPremiumHeader` is the canonical header. **Fix**: Remove `TeacherHeader` once all references are migrated.
- **MINOR**: `TeacherSubHeader` uses `VTypography.bodySmall` for subtitle (line 263). **Fix**: Use `VTypography.caption`.
**Status**: COMPLETE

---

### 24. Shared Components — TeacherDock.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: Hardcoded "5" dock items in the dock layout calculation. **Fix**: Use `dockItems.size` for flexibility.
- **MINOR**: Haptic feedback is called but no check for whether haptics are enabled in user preferences. **Fix**: Add a `hapticsEnabled` check from settings.
**Status**: COMPLETE

---

### 25. Shared Components — TeacherScopeSelector.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: `VTypography.bodySmall` used in `ScopeRow` for class/section text. **Fix**: Use `VTypography.caption`.
- **MINOR**: Search field uses raw `OutlinedTextField` instead of a shared search component. **Fix**: Use `VSearchField` if available, or create one.
**Status**: COMPLETE

---

### 26. Shared Components — TeacherCheckInPopup.kt
**Round 1**: PASS
**Issues Found**:
- **MINOR**: No timeout handling for biometric prompt — if the user doesn't interact, the popup stays indefinitely. **Fix**: Add a timeout (e.g., 30 seconds) that falls back to manual check-in.
- **MINOR**: Uses hardcoded English strings: "Check In", "Use Fingerprint", "Use Device PIN", "Check In Manually", "Checking in…". **Fix**: Replace with `appString(StringKeys.*)`.
**Status**: COMPLETE

---

### 27. Shared Components — TeacherDialogs.kt
**Round 1**: PASS
**Issues Found**:
- No issues found. Clean, minimal implementation.
**Status**: COMPLETE

---

## Cross-Cutting Issues (Affect Multiple Screens)

### CC-1: `VTypography.bodySmall` Usage (MAJOR)
**Affected files**: TeacherHomeScreenV2, TeacherUpdateScreenV2, TeacherAttendanceScreenV2, TeacherMarksScreenV2, TeacherClassesScreenV2, TeacherLeaveRequestsScreenV2, TeacherHeader.kt, TeacherScopeSelector.kt
**Fix**: `bodySmall` is not in the canonical token set. Audit all usages and replace with `VTypography.caption` (for small text) or `VTypography.body` (for body text). Consider adding `bodySmall` to `VTypography` if it's genuinely needed as a distinct size.

### CC-2: Hardcoded English Strings (MAJOR)
**Affected files**: TeacherMessagesScreenV2, TeacherLeaveRequestsScreenV2, TeacherAnnouncementDetailScreen, TeacherPtmEventRegistrationScreenV2, TeacherSalaryOverlayScreen, TeacherGamificationScreenV2, TeacherCheckInPopup.kt
**Fix**: All user-facing strings must use `appString(StringKeys.*)`. Create missing `StringKeys` entries for any strings that don't have keys yet. This is critical for Hindi/Marathi/Tamil/Telugu/Bengali localization.

### CC-3: `collectAsState()` vs `collectAsStateV2()` (CRITICAL)
**Affected files**: TeacherReportReviewQueueScreen.kt, TeacherReportDraftEditorScreen.kt
**Fix**: These two files use `collectAsState()` instead of `collectAsStateV2()`. If `collectAsStateV2` has lifecycle-aware behavior (which it likely does), these screens may have state management bugs. Change to `collectAsStateV2()`.

### CC-4: VCard Double Padding (MAJOR)
**Affected files**: TeacherPtmEventRegistrationScreenV2 (lines 143, 187, 277), TeacherReportReviewQueueScreen (line 184), TeacherReportDraftEditorScreen (line 86)
**Fix**: These files add `Modifier.padding()` inside `VCard` content. VCard already applies internal padding. Remove the inner padding calls.

### CC-5: Missing `VBackHeader` on Overlays (MAJOR)
**Affected files**: TeacherReportReviewQueueScreen.kt, TeacherReportDraftEditorScreen.kt
**Fix**: These overlay screens use custom header rows instead of `VBackHeader`. Replace with `VBackHeader(title = ..., onBack = onBack)` for consistent overlay navigation.

### CC-6: Missing `statusBarsPadding()` / `navigationBarsPadding()` (MAJOR)
**Affected files**: TeacherReportReviewQueueScreen.kt, TeacherReportDraftEditorScreen.kt
**Fix**: These screens don't handle system bar insets. Add `.statusBarsPadding()` and `.navigationBarsPadding()` to the root `Column` modifier.

### CC-7: Parallel Token Systems (MAJOR)
**Affected files**: TeacherPewsScreenV2 (PewsColors/PewsType), TeacherClassesScreenV2 (TCard/TEyebrow/TIconDisc/TPill/TRing)
**Fix**: Multiple files define their own private token bridges instead of using the shared `VtC`/`VtT` from `TeacherKitV2.kt`. Consolidate to use `VtC`/`VtT` or migrate directly to `VColors`/`VTypography`.

### CC-8: Duplicate Subject Color Helpers (MINOR)
**Affected files**: TeacherHomeScreenV2 (`subjectColor()`), TeacherTimetableScreenV2 (`periodAccent()`/`periodSoft()`), TeacherKitV2 (`vtSubjectColor()`)
**Fix**: Remove local duplicates and use `vtSubjectColor()` from `TeacherKitV2` everywhere.

### CC-9: `TeacherSpinner()` vs `CircularProgressIndicator` (MINOR)
**Affected files**: TeacherReportReviewQueueScreen (line 108), TeacherReportDraftEditorScreen (line 70)
**Fix**: Use `TeacherSpinner()` for loading states instead of raw `CircularProgressIndicator`.

### CC-10: Non-standard Content Padding (MINOR)
**Affected files**: TeacherHealthAlertsScreenV2 (16dp), TeacherAnnouncementDetailScreen (24dp), TeacherSalaryOverlayScreen (16dp)
**Fix**: Standardize to `.padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp)` for scrollable content areas.

---

## Summary Dashboard

| Screen | Round 1 | Round 2 | Status |
|--------|---------|---------|--------|
| Home | PASS | N/A | COMPLETE |
| Update (shell) | PASS | N/A | COMPLETE |
| Attendance | PASS (minor) | N/A | NEEDS REWORK (double-tap) |
| Marks | PASS (minor) | N/A | NEEDS REWORK (input debounce) |
| Syllabus | PASS (minor) | N/A | NEEDS REWORK (parse error state) |
| Homework | PASS | N/A | COMPLETE |
| LessonPlan | PASS (minor) | N/A | NEEDS REWORK (keyboard scroll) |
| Classes | PASS (minor) | N/A | NEEDS REWORK (local aliases) |
| StudentProfile | PASS | N/A | COMPLETE |
| Timetable | PASS | N/A | COMPLETE |
| Profile | PASS | N/A | COMPLETE |
| Notifications | NOT READ | N/A | NOT AUDITED |
| NotificationPrefs | NOT READ | N/A | NOT AUDITED |
| HealthAlerts | PASS (issues) | N/A | NEEDS REWORK (padding + tokens) |
| TransportAttendance | NOT READ | N/A | NOT AUDITED |
| Pews | PASS (issues) | N/A | NEEDS REWORK (token system) |
| ReportReview | PASS (issues) | N/A | NEEDS REWORK (critical) |
| ReportDraftEditor | PASS (issues) | N/A | NEEDS REWORK (critical) |
| Heatmap | NOT READ | N/A | NOT AUDITED |
| DigitalIdCard | NOT READ | N/A | NOT AUDITED |
| ScheduledMessages | NOT READ | N/A | NOT AUDITED |
| EventRegistration | PASS (issues) | N/A | NEEDS REWORK (i18n + padding) |
| Messages | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| Calendar | NOT READ | N/A | NOT AUDITED |
| AnnouncementDetail | PASS (minor) | N/A | NEEDS REWORK (i18n + padding) |
| LeaveRequests | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| ExamTimetableList | NOT READ | N/A | NOT AUDITED |
| ExamTimetableUpload | NOT READ | N/A | NOT AUDITED |
| ExamTimetableDetail | NOT READ | N/A | NOT AUDITED |
| ExamSyllabusMapping | NOT READ | N/A | NOT AUDITED |
| ExamMarksImport | NOT READ | N/A | NOT AUDITED |
| Export | NOT READ | N/A | NOT AUDITED |
| SalaryHistory | PASS (minor) | N/A | COMPLETE |
| Gamification | PASS (issues) | N/A | NEEDS REWORK (i18n + types) |

---

## Priority Fix Order

### P0 — Critical (Fix immediately)
1. **CC-3**: `collectAsState()` → `collectAsStateV2()` in ReportReviewQueue + ReportDraftEditor
2. **CC-5 + CC-6**: Add `VBackHeader` + `statusBarsPadding` + `navigationBarsPadding` to ReportReviewQueue + ReportDraftEditor

### P1 — Major (Fix before Parent Portal rebuild)
3. **CC-2**: Replace all hardcoded English strings with `appString(StringKeys.*)`
4. **CC-4**: Remove VCard double padding in PTM, ReportReview, ReportDraftEditor
5. **CC-7**: Consolidate parallel token systems (PewsColors, TCard/TPill/etc.) to VtC/VtT
6. **CC-1**: Replace `VTypography.bodySmall` with canonical tokens
7. **Attendance**: Add double-tap protection on StatusChip
8. **Marks**: Add debounce on MarkInput
9. **Syllabus**: Add parse error state for AI syllabus parsing
10. **LessonPlan**: Fix keyboard scroll coverage in editor mode
11. **Classes**: Replace local component aliases with shared Vt* components
12. **Messages**: Fix unread state styling + reset reply text on thread switch
13. **Gamification**: Extract large composables + replace Map<String,*> with DTOs

### P2 — Minor (Fix during rebuild)
14. **CC-8**: Consolidate duplicate subject color helpers
15. **CC-9**: Replace CircularProgressIndicator with TeacherSpinner
16. **CC-10**: Standardize content padding to 20dp horizontal
17. **StudentProfile**: Add tap-to-call on parent contact, use soft color tokens
18. **Timetable**: Use time picker, make day list dynamic
19. **Profile**: Add password strength indicator
20. **TeacherKitV2**: Plan migration to remove VtC/VtT bridge entirely

---

## Remaining Screen Audits (Phase 2 Overlays)

### 28. Notifications — NotificationsScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Custom `PremiumNotificationHeader` instead of `VBackHeader` — breaks overlay pattern consistency. **Fix**: Replace with `VBackHeader(title = ..., onBack = onBack, action = { ... })`.
- **MAJOR**: `VTypography.bodySmall` used at lines 327, 478 — not a canonical token. **Fix**: Use `VTypography.caption` or `VTypography.body`.
- **MAJOR**: Hardcoded "Clear" string (line 411). **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: Uses raw `Color` values for tile backgrounds (`BlobTeal`, `TileFgAttendance`, etc.) — one-off literals from React prototype. **Fix**: Map to `VColors` tokens where possible.
- **MINOR**: `NotificationRow` uses `.padding(14.dp)` inside a custom `Box` (not VCard) — acceptable but inconsistent.
**Status**: NEEDS REWORK (header + token + i18n)

---

### 29. NotificationPreferences — NotificationPreferencesScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings: "Notification Preferences" (line 54), "No Preferences Found" (line 98), "Notification preferences will appear here once configured." (line 99), "Preference saved" (line 112). **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: `VPullRefresh` `isRefreshing` condition is `state.isLoading && state.preferences.isNotEmpty()` — won't show refresh spinner on initial load. **Fix**: Separate initial loading from refresh loading.
**Status**: NEEDS REWORK (i18n)

---

### 30. TransportAttendance — TransportAttendanceScreenV2.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings throughout: "Transport Attendance", "No routes found", "No transport routes have been created yet.", "Select a Route", "Active"/"Inactive", "No students assigned", "No students are assigned to this route today.", "Mark Pickup", "Mark Drop", "Pickup: ", "Drop: ". **Fix**: Replace with `appString(StringKeys.*)`.
- **MAJOR**: Uses 16dp padding (lines 87, 138) instead of standard 20dp. **Fix**: Change to `horizontal = 20.dp`.
- **MAJOR**: No double-tap protection on Mark Pickup / Mark Drop buttons. **Fix**: Add `enabled = !state.isMarking` or disable button after click.
- **MINOR**: No `imePadding()` — not critical since no text inputs, but good practice.
**Status**: NEEDS REWORK (i18n + padding + double-tap)

---

### 31. Heatmap — TeacherHeatmapScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Missing `navigationBarsPadding()` — bottom content may be cut off. **Fix**: Add `.navigationBarsPadding()` to root modifier.
- **MAJOR**: Uses 16dp padding (lines 118, 170) instead of standard 20dp. **Fix**: Change to `horizontal = 20.dp`.
- **MINOR**: Uses `coloredV()` extension from TeacherKitV2 — verify this is a shared utility and not a local hack. **Fix**: Confirm `coloredV` is in `TeacherKitV2.kt` or replace with `.copy(color = ...)`.
- **MINOR**: `VtC.warmOrange` used for severity color — verify this maps to a valid `VColors` token. **Fix**: Ensure `warmOrange` maps to `VColors.coral` or similar.
**Status**: NEEDS REWORK (insets + padding)

---

### 32. DigitalIdCard — DigitalIdCardScreen.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **CRITICAL**: Missing `statusBarsPadding()` and `navigationBarsPadding()` — content overlaps status bar on notch devices. **Fix**: Add both to root modifier.
- **MAJOR**: Uses `PremiumOverlayHeader` instead of `VBackHeader` — inconsistent with overlay pattern. **Fix**: Replace with `VBackHeader`.
- **MAJOR**: Loading state is text-only (line 125-128: `Text("Loading...")`) — no spinner. **Fix**: Use `TeacherSpinner()`.
- **MAJOR**: Uses 16dp/24dp padding (lines 78, 94, 115, 147) instead of standard 20dp. **Fix**: Standardize to 20dp horizontal.
- **MINOR**: `DigitalCard` has fixed `size(width = 300.dp, height = 480.dp)` (line 148) — may not fit on small screens. **Fix**: Use `fillMaxWidth` with `aspectRatio` instead.
**Status**: NEEDS REWORK (critical: insets, header, loading)

---

### 33. ScheduledMessages — ScheduledMessagesScreenV2.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings throughout: "Scheduled Messages", "No scheduled messages", "Schedule announcements and broadcasts to send later.", "All"/"Scheduled"/"Dispatched"/"Failed"/"Cancelled", "Send now", "Cancel". **Fix**: Replace with `appString(StringKeys.*)`.
- **MAJOR**: VCard content has `Modifier.padding(14.dp)` inside VCard (line 167) — double padding. **Fix**: Remove inner padding.
- **MAJOR**: Uses 16dp padding (line 69) instead of standard 20dp. **Fix**: Change to `horizontal = 20.dp`.
- **MINOR**: `VButton` uses `modifier = Modifier.weight(1f)` (lines 216, 223) — correct for Row scope, but should verify `full` isn't needed.
**Status**: NEEDS REWORK (i18n + VCard padding)

---

### 34. Calendar — AcademicCalendarScreenV2.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Uses `PremiumOverlayHeader` instead of `VBackHeader` — inconsistent. **Fix**: Replace with `VBackHeader`.
- **MAJOR**: Uses 24dp padding (line 106) instead of standard 20dp. **Fix**: Change to `horizontal = 20.dp`.
- **MAJOR**: Hardcoded "Register for Events" string (line 228). **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: Calendar grid doesn't account for first day of month offset — weeks always start at day 1 regardless of which weekday the month begins on. **Fix**: Calculate starting weekday offset and add empty cells.
- **MINOR**: Day headers are hardcoded "S", "M", "T", "W", "T", "F", "S" — should use locale-aware abbreviations. **Fix**: Use locale-aware day names.
**Status**: NEEDS REWORK (header + padding + i18n + calendar logic)

---

### 35. ExamTimetableList — ExamTimetableListScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings: "Exam Timetables", "No exam timetables yet", "Upload a timetable image or paste text to get started", "New Exam Timetable". **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: Good pattern compliance — uses 20dp padding, `full = true` on VButton, `VBackHeader`, `collectAsStateV2`, `VPullRefresh`.
**Status**: NEEDS REWORK (i18n)

---

### 36. ExamTimetableUpload — ExamTimetableUploadScreen.kt
**Round 1**: PASS (with issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings throughout: "New Exam Timetable", "Details", "Timetable name...", "Class", "Section", "Term (optional)", "Import", "Upload Image (OCR)", "Paste Text Instead", "Parse Text", "Extracting entries...", "Extracted Entries", "Create Draft Timetable". **Fix**: Replace with `appString(StringKeys.*)`.
- **MAJOR**: OCR upload button is a no-op placeholder (lines 156-158) — `onClick` is empty with a comment. **Fix**: Wire to `rememberMediaPicker` like `ExamMarksImportScreen` does.
- **MAJOR**: No `imePadding()` — keyboard covers bottom fields. **Fix**: Add `.imePadding()` to scrollable column.
- **MINOR**: Uses raw `OutlinedTextField` instead of shared input component. **Fix**: Use `VTextField` if available.
**Status**: NEEDS REWORK (i18n + OCR wiring + imePadding)

---

### 37. ExamTimetableDetail — ExamTimetableDetailScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings: "Exam Timetable", "Timetable not found", "Map Syllabus", "Publishing..."/"Publish Timetable". **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: Good pattern compliance — 20dp padding, `full = true`, `VBackHeader`, `collectAsStateV2`, `VStateHost`.
**Status**: NEEDS REWORK (i18n)

---

### 38. ExamSyllabusMapping — ExamSyllabusMappingScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings: "Syllabus Mapping", "No curriculum units found", "Add curriculum units for this class+subject first", "Topic", "Saving..."/"Save Mapping". **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: Good pattern compliance — 20dp padding, `full = true`, `VBackHeader`, `collectAsStateV2`, `VStateHost`.
**Status**: NEEDS REWORK (i18n)

---

### 39. ExamMarksImport — ExamMarksImportScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings throughout: "Import Marks", "Import Method", "Paste marks sheet text", "Parse Text", "Use Image Instead", "Upload Marks Sheet Image (OCR)", "Paste Text Instead", "Extracting marks...", "This may take a few seconds.", "Import Failed", "Image Picker Unavailable", "Extraction Results", "Review the extracted marks below...", "Apply to Grid", "Discard", "Matched"/"Unmatched", "AB". **Fix**: Replace with `appString(StringKeys.*)`.
- **MINOR**: Good pattern compliance — 20dp padding, `full = true`, `TeacherSpinner`, `rememberMediaPicker`, `imePadding`, `VBackHeader`, `collectAsStateV2`.
**Status**: NEEDS REWORK (i18n)

---

### 40. Export — ExportScreen.kt
**Round 1**: PASS (with minor issues)
**Issues Found**:
- **MAJOR**: Hardcoded English strings throughout: "Export Reports", "No exports available", "Export types will appear here once configured.", "Generate branded PDF or CSV reports...", "All Classes", "Select Test/Exam", "No assessments found", "From Date"/"To Date", "Export Ready", "Share File"/"Share Download Link", "Generating export... Please wait.", "Dismiss". **Fix**: Replace with `appString(StringKeys.*)`.
- **MAJOR**: Uses `CircularProgressIndicator` (line 450) instead of `TeacherSpinner()`. **Fix**: Use `TeacherSpinner()`.
- **MINOR**: Good pattern compliance — 20dp padding, `full = true`, `VBackHeader`, `collectAsStateV2`, `VStateHost`, `VDatePicker`, `rememberShareHelper`.
**Status**: NEEDS REWORK (i18n + spinner)

---

## Updated Cross-Cutting Issues (Additional)

### CC-11: `PremiumOverlayHeader` vs `VBackHeader` (MAJOR)
**Affected files**: DigitalIdCardScreen.kt, AcademicCalendarScreenV2.kt
**Fix**: These screens use `PremiumOverlayHeader` instead of `VBackHeader`. Replace with `VBackHeader` for consistent overlay navigation.

### CC-12: Missing `statusBarsPadding()` on Overlays (CRITICAL)
**Affected files**: DigitalIdCardScreen.kt
**Fix**: Add `.statusBarsPadding()` to root modifier.

### CC-13: Missing `navigationBarsPadding()` on Overlays (MAJOR)
**Affected files**: TeacherHeatmapScreen.kt, DigitalIdCardScreen.kt
**Fix**: Add `.navigationBarsPadding()` to root modifier.

### CC-14: No-op OCR Button in ExamTimetableUpload (MAJOR)
**Affected files**: ExamTimetableUploadScreen.kt (line 156-158)
**Fix**: Wire to `rememberMediaPicker` like `ExamMarksImportScreen` does.

### CC-15: Text-only Loading State in DigitalIdCard (MAJOR)
**Affected files**: DigitalIdCardScreen.kt (line 125-128)
**Fix**: Use `TeacherSpinner()` instead of `Text("Loading...")`.

### CC-16: Calendar Grid First-Day Offset Bug (MAJOR)
**Affected files**: AcademicCalendarScreenV2.kt (line 149)
**Fix**: Calculate starting weekday and add empty cells before day 1.

---

## Updated Summary Dashboard

| Screen | Round 1 | Round 2 | Status |
|--------|---------|---------|--------|
| Home | PASS | N/A | COMPLETE |
| Update (shell) | PASS | N/A | COMPLETE |
| Attendance | PASS (minor) | N/A | NEEDS REWORK (double-tap) |
| Marks | PASS (minor) | N/A | NEEDS REWORK (input debounce) |
| Syllabus | PASS (minor) | N/A | NEEDS REWORK (parse error state) |
| Homework | PASS | N/A | COMPLETE |
| LessonPlan | PASS (minor) | N/A | NEEDS REWORK (keyboard scroll) |
| Classes | PASS (minor) | N/A | NEEDS REWORK (local aliases) |
| StudentProfile | PASS | N/A | COMPLETE |
| Timetable | PASS | N/A | COMPLETE |
| Profile | PASS | N/A | COMPLETE |
| Notifications | PASS (issues) | N/A | NEEDS REWORK (header + token + i18n) |
| NotificationPrefs | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| HealthAlerts | PASS (issues) | N/A | NEEDS REWORK (padding + tokens) |
| TransportAttendance | PASS (issues) | N/A | NEEDS REWORK (i18n + padding + double-tap) |
| Pews | PASS (issues) | N/A | NEEDS REWORK (token system) |
| ReportReview | PASS (issues) | N/A | NEEDS REWORK (critical) |
| ReportDraftEditor | PASS (issues) | N/A | NEEDS REWORK (critical) |
| Heatmap | PASS (minor) | N/A | NEEDS REWORK (insets + padding) |
| DigitalIdCard | PASS (issues) | N/A | NEEDS REWORK (critical: insets, header, loading) |
| ScheduledMessages | PASS (issues) | N/A | NEEDS REWORK (i18n + VCard padding) |
| EventRegistration | PASS (issues) | N/A | NEEDS REWORK (i18n + padding) |
| Messages | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| Calendar | PASS (minor) | N/A | NEEDS REWORK (header + padding + calendar logic) |
| AnnouncementDetail | PASS (minor) | N/A | NEEDS REWORK (i18n + padding) |
| LeaveRequests | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| ExamTimetableList | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| ExamTimetableUpload | PASS (issues) | N/A | NEEDS REWORK (i18n + OCR + imePadding) |
| ExamTimetableDetail | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| ExamSyllabusMapping | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| ExamMarksImport | PASS (minor) | N/A | NEEDS REWORK (i18n) |
| Export | PASS (minor) | N/A | NEEDS REWORK (i18n + spinner) |
| SalaryHistory | PASS (minor) | N/A | COMPLETE |
| Gamification | PASS (issues) | N/A | NEEDS REWORK (i18n + types) |

---

## Updated Priority Fix Order

### P0 — Critical (Fix immediately)
1. **CC-3**: `collectAsState()` → `collectAsStateV2()` in ReportReviewQueue + ReportDraftEditor
2. **CC-5 + CC-6**: Add `VBackHeader` + `statusBarsPadding` + `navigationBarsPadding` to ReportReviewQueue + ReportDraftEditor
3. **CC-12**: Add `statusBarsPadding()` to DigitalIdCardScreen

### P1 — Major (Fix before Parent Portal rebuild)
4. **CC-2**: Replace all hardcoded English strings with `appString(StringKeys.*)` (now 15+ files)
5. **CC-4**: Remove VCard double padding in ScheduledMessages, PTM, ReportReview, ReportDraftEditor
6. **CC-7**: Consolidate parallel token systems (PewsColors, TCard/TPill/etc.) to VtC/VtT
7. **CC-1**: Replace `VTypography.bodySmall` with canonical tokens
8. **CC-11**: Replace `PremiumOverlayHeader` with `VBackHeader` in DigitalIdCard, Calendar
9. **CC-13**: Add `navigationBarsPadding()` to Heatmap, DigitalIdCard
10. **CC-14**: Wire OCR upload button in ExamTimetableUpload to `rememberMediaPicker`
11. **CC-15**: Replace text-only loading in DigitalIdCard with `TeacherSpinner()`
12. **CC-16**: Fix calendar grid first-day offset in AcademicCalendar
13. **CC-9 (expanded)**: Replace `CircularProgressIndicator` with `TeacherSpinner` in ExportScreen too
14. **CC-10 (expanded)**: Standardize padding to 20dp in Transport, Heatmap, DigitalId, ScheduledMessages, Calendar
15. **Attendance**: Add double-tap protection on StatusChip
16. **TransportAttendance**: Add double-tap protection on Mark Pickup/Drop
17. **Marks**: Add debounce on MarkInput
18. **Syllabus**: Add parse error state for AI syllabus parsing
19. **LessonPlan**: Fix keyboard scroll coverage in editor mode
20. **Classes**: Replace local component aliases with shared Vt* components
21. **Messages**: Fix unread state styling + reset reply text on thread switch
22. **Gamification**: Extract large composables + replace Map<String,*> with DTOs

### P2 — Minor (Fix during rebuild)
23. **CC-8**: Consolidate duplicate subject color helpers
24. **StudentProfile**: Add tap-to-call on parent contact, use soft color tokens
25. **Timetable**: Use time picker, make day list dynamic
26. **Profile**: Add password strength indicator
27. **TeacherKitV2**: Plan migration to remove VtC/VtT bridge entirely
