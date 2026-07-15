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

## Findings Template (Per Screen)

```
### [Screen Name]
**File**: path/to/file.kt
**Round 1**: PASS/FAIL
| # | Check | Result | Notes |
|---|-------|--------|-------|
| 1 | Backend route | PASS/FAIL | notes |
| ... | ... | ... | ... |
**Round 2**: PASS/FAIL
| # | Check | Result | Notes |
| ... | ... | ... | ... |
**Issues Found**:
- Issue 1: description
**Status**: COMPLETE / NEEDS REWORK / BLOCKED
```

---

## Summary Dashboard (To be filled)

| Screen | Round 1 | Round 2 | Status |
|--------|---------|---------|--------|
| Home | | | |
| Update (shell) | | | |
| Attendance | | | |
| Marks | | | |
| Syllabus | | | |
| Homework | | | |
| LessonPlan | | | |
| Classes | | | |
| StudentProfile | | | |
| Timetable | | | |
| Profile | | | |
| Notifications | | | |
| NotificationPrefs | | | |
| HealthAlerts | | | |
| TransportAttendance | | | |
| Pews | | | |
| ReportReview | | | |
| ReportDraftEditor | | | |
| Heatmap | | | |
| DigitalIdCard | | | |
| ScheduledMessages | | | |
| EventRegistration | | | |
| Messages | | | |
| Calendar | | | |
| AnnouncementDetail | | | |
| LeaveRequests | | | |
| ExamTimetableList | | | |
| ExamTimetableUpload | | | |
| ExamTimetableDetail | | | |
| ExamSyllabusMapping | | | |
| ExamMarksImport | | | |
| Export | | | |
| SalaryHistory | | | |

---

## All Issues Found (Running List)

*(Populated during audit execution)*
