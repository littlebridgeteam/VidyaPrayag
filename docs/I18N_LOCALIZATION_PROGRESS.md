# I18N Localization Progress Tracker

> **Goal:** Convert all 120 screen files from hardcoded English strings to `appString(StringKeys.XYZ)` calls, with full translations for 10 languages (`en`, `hi`, `bn`, `ta`, `te`, `mr`, `gu`, `kn`, `ml`, `pa`).
>
> **Spec ref:** `MULTI_LANGUAGE_SPEC.md` §12
>
> **How to use:** For each file, check the box when ALL hardcoded user-visible strings have been replaced with `appString()` / `appPlural()` calls and the corresponding `StringKeys` + translations have been added to `AppStrings.kt`.

---

## Supported Languages

| Code | Language | Native Name | Status |
|------|----------|-------------|--------|
| `en` | English | English | ✅ Baseline |
| `hi` | Hindi | हिन्दी | ✅ Done |
| `bn` | Bengali | বাংলা | ⬜ Placeholder (English fallback) |
| `ta` | Tamil | தமிழ் | ⬜ Placeholder (English fallback) |
| `te` | Telugu | తెలుగు | ⬜ Placeholder (English fallback) |
| `mr` | Marathi | मराठी | ⬜ Placeholder (English fallback) |
| `gu` | Gujarati | ગુજરાતી | ⬜ Placeholder (English fallback) |
| `kn` | Kannada | ಕನ್ನಡ | ⬜ Placeholder (English fallback) |
| `ml` | Malayalam | മലയാളം | ⬜ Placeholder (English fallback) |
| `pa` | Punjabi | ਪੰਜਾਬੀ | ⬜ Placeholder (English fallback) |

---

## Already Localized (48 files)

- [x] `auth/LanguageSelectionScreen.kt` — fully uses `appString()`
- [x] `school/SchoolSettingsScreenV2.kt` — partially (title, language label, logout)
- [x] `auth/SplashScreenV2.kt` — fully localized
- [x] `auth/AuthScaffoldV2.kt` — fully localized
- [x] `auth/ParentAuthScreenV2.kt` — fully localized
- [x] `auth/AdminAuthScreenV2.kt` — fully localized
- [x] `auth/TeacherFirstLoginScreenV2.kt` — fully localized
- [x] `auth/LegalInfoScreenV2.kt` — fully localized
- [x] `auth/ParentLinkChildScreenV2.kt` — fully localized
- [x] `auth/CommonLandingScreenV2.kt` — fully localized
- [x] `auth/CommonLandingScreenV3.kt` — fully localized
- [x] `auth/SchoolOnboardingScreenV2.kt` — fully localized
- [x] `parent/ParentHomeScreenV2.kt` — fully localized
- [x] `parent/ParentAcademicsScreenV2.kt` — fully localized
- [x] `parent/ParentProfileCardScreenV2.kt` — fully localized
- [x] `parent/ParentMessagesScreenV2.kt` — fully localized
- [x] `parent/ParentScheduleCard.kt` — fully localized
- [x] `parent/ParentPortalV2.kt` — fully localized
- [x] `parent/ParentAttendanceCard.kt` — fully localized
- [x] `parent/ParentEventRegistrationScreenV2.kt` — fully localized
- [x] `parent/PulseCard.kt` — fully localized
- [x] `parent/ParentFeesScreenV2.kt` — fully localized
- [x] `parent/ParentUnlinkedScreenV2.kt` — fully localized
- [x] `parent/ParentAttendanceCalendar.kt` — fully localized
- [x] `parent/ParentConversationsScreenV2.kt` — fully localized
- [x] `parent/ParentCoveredDetailOverlay.kt` — fully localized
- [x] `parent/ParentLeaveScreenV2.kt` — fully localized
- [x] `parent/ParentResultsFeesCards.kt` — fully localized
- [x] `parent/ScholarshipsScreenV2.kt` — fully localized
- [x] `parent/ParentDock.kt` — no hardcoded strings (uses VNavItem labels)
- [x] `parent/ParentPulseScreen.kt` — fully localized
- [x] `parent/BusTrackingScreenV2.kt` — fully localized
- [x] `parent/DigitalIdCardScreen.kt` — fully localized
- [x] `parent/ParentPewsScreenV2.kt` — fully localized
- [x] `parent/ParentProfileScreenV2.kt` — fully localized
- [x] `parent/ParentActivityScreenV2.kt` — fully localized
- [x] `parent/ParentCoveredCard.kt` — fully localized
- [x] `parent/ParentNudgeCard.kt` — fully localized
- [x] `parent/ParentPalette.kt` — no user-facing strings (color utility)

---

## Phase 1 — Auth Screens (10 files, ~32 hardcoded strings)

> Priority: HIGH — first user touchpoint, language selection happens here.

- [x] `auth/CommonLandingScreenV3.kt` (1294 lines, 5 strings)
- [x] `auth/SchoolOnboardingScreenV2.kt` (1138 lines, 25 strings)
- [x] `auth/CommonLandingScreenV2.kt` (913 lines, 1 string)
- [x] `auth/ParentLinkChildScreenV2.kt` (460 lines, 4 strings)
- [x] `auth/AdminAuthScreenV2.kt` (397 lines, 1 string)
- [x] `auth/LegalInfoScreenV2.kt` (364 lines, 0 strings)
- [x] `auth/SplashScreenV2.kt` (255 lines, 0 strings)
- [x] `auth/AuthScaffoldV2.kt` (244 lines, 0 strings)
- [x] `auth/ParentAuthScreenV2.kt` (155 lines, 0 strings)
- [x] `auth/TeacherFirstLoginScreenV2.kt` (154 lines, 1 string)

---

## Phase 2 — Parent Screens (30 files, ~99 hardcoded strings)

- [x] `parent/ParentAcademicsScreenV2.kt` (1106 lines, 28 strings)
- [x] `parent/ParentProfileCardScreenV2.kt` (1098 lines, 0 strings)
- [x] `parent/ParentHomeScreenV2.kt` (839 lines, 0 strings)
- [x] `parent/ParentMessagesScreenV2.kt` (765 lines, 0 strings)
- [x] `parent/ParentScheduleCard.kt` (697 lines, 4 strings)
- [x] `parent/ParentPortalV2.kt` (533 lines, 0 strings)
- [x] `parent/ParentAttendanceCard.kt` (578 lines, 2 strings)
- [x] `parent/ScholarshipWorkflowScreenV2.kt` (471 lines, 10 strings)
- [x] `parent/ParentEventRegistrationScreenV2.kt` (444 lines, 4 strings)
- [x] `parent/ParentLibraryScreenV2.kt` (407 lines, 17 strings)
- [x] `parent/PulseCard.kt` (335 lines, 0 strings)
- [x] `parent/ParentFeesScreenV2.kt` (201 lines, 4 strings)
- [x] `parent/ParentHealthScreenV2.kt` (270 lines, 8 strings)
- [x] `parent/ParentUnlinkedScreenV2.kt` (261 lines, 0 strings)
- [x] `parent/ParentAttendanceCalendar.kt` (260 lines, 0 strings)
- [x] `parent/ParentConversationsScreenV2.kt` (225 lines, 0 strings)
- [x] `parent/ParentCoveredDetailOverlay.kt` (225 lines, 2 strings)
- [x] `parent/ParentReportScreen.kt` (219 lines, 12 strings)
- [x] `parent/ParentLeaveScreenV2.kt` (208 lines, 1 string)
- [x] `parent/ParentResultsFeesCards.kt` (284 lines, 1 string)
- [x] `parent/ScholarshipsScreenV2.kt` (238 lines, 3 strings)
- [x] `parent/ParentDock.kt` (250 lines, 0 strings)
- [x] `parent/ParentPulseScreen.kt` (194 lines, 0 strings)
- [x] `parent/BusTrackingScreenV2.kt` (270 lines, 0 strings)
- [x] `parent/DigitalIdCardScreen.kt` (195 lines, 0 strings)
- [x] `parent/ParentPewsScreenV2.kt` (189 lines, 2 strings)
- [x] `parent/ParentProfileScreenV2.kt` (262 lines, 1 string)
- [x] `parent/ParentActivityScreenV2.kt` (139 lines, 1 string)
- [x] `parent/ParentCoveredCard.kt` (143 lines, 0 strings)
- [x] `parent/ParentNudgeCard.kt` (133 lines, 0 strings)

---

## Phase 3 — School/Admin Screens (38 files, ~367 hardcoded strings)

> Largest effort — includes the biggest offenders.

- [ ] `school/ClassesSubjectsScreenV2.kt` (2672 lines, 71 strings)
- [ ] `school/SchoolLibraryScreen.kt` (1877 lines, 98 strings)
- [ ] `school/SchoolHomeScreenV2.kt` (1747 lines, 34 strings)
- [ ] `school/SchoolPeopleScreenV2.kt` (1299 lines, 19 strings)
- [ ] `school/ScholarshipManagementScreenV2.kt` (854 lines, 10 strings)
- [ ] `school/AlumniScreen.kt` (686 lines, 24 strings)
- [x] `school/PewsCohortScreenV2.kt` (684 lines, 2 strings)
- [x] `school/StudentProfileScreenV2.kt` (700 lines, 6 strings)
- [x] `school/MessagesScreenV2.kt` (710 lines, 0 strings)
- [ ] `school/BrandingSettingsScreen.kt` (704 lines, 8 strings)
- [x] `school/TeacherProfileScreenV2.kt` (602 lines, 4 strings)
- [ ] `school/PewsStudentDetailScreenV2.kt` (597 lines, 7 strings)
- [ ] `school/IdCardTemplatesTab.kt` (594 lines, 8 strings)
- [x] `school/SchoolPortalV2.kt` (592 lines, 0 strings)
- [ ] `school/ClassDetailScreenV2.kt` (583 lines, 10 strings)
- [ ] `school/TransportManagementScreenV2.kt` (537 lines, 9 strings)
- [ ] `school/AcademicCalendarPlatformScreenV2.kt` (543 lines, 6 strings)
- [ ] `school/HealthRecordsScreenV2.kt` (526 lines, 7 strings)
- [x] `school/EditSchoolProfileScreenV2.kt` (531 lines, 0 strings)
- [x] `school/SchoolDayConfigScreenV2.kt` (512 lines, 4 strings)
- [x] `school/TeacherAssignmentManagementScreen.kt` (546 lines, 2 strings)
- [ ] `school/SchoolRecordsScreenV2.kt` (626 lines, 24 strings)
- [x] `school/SchoolCommsScreenV2.kt` (351 lines, 3 strings)
- [x] `school/IdCardCardsTab.kt` (375 lines, 1 string)
- [x] `school/StudentRosterScreenV2.kt` (353 lines, 6 strings)
- [x] `school/ClassPerformanceScreenV2.kt` (265 lines, 3 strings)
- [x] `school/LinkRequestsScreenV2.kt` (236 lines, 0 strings)
- [x] `school/AnalyticsDashboardScreenV2.kt` (240 lines, 1 string)
- [x] `school/LeaveRequestsScreenV2.kt` (224 lines, 0 strings)
- [x] `school/AcademicYearManagementScreenV2.kt` (204 lines, 3 strings)
- [x] `school/AdminReportPublishScreen.kt` (204 lines, 6 strings)
- [x] `school/IdCardGenerateTab.kt` (199 lines, 3 strings)
- [x] `school/StaffProfileScreenV2.kt` (174 lines, 1 string)
- [x] `school/AdminReportingEffectivenessScreen.kt` (172 lines, 7 strings)
- [x] `school/DailyAttendanceScreenV2.kt` (212 lines, 1 string)
- [x] `school/TeacherPerformanceScreenV2.kt` (228 lines, 2 strings)
- [x] `school/AlumniDetailScreen.kt` (208 lines, 8 strings)
- [x] `school/AlumniCampaignScreen.kt` (139 lines, 9 strings)
- [x] `school/PewsEffectivenessScreenV2.kt` (218 lines, 1 string)
- [x] `school/ResultsPublishScreenV2.kt` (206 lines, 1 string)
- [x] `school/SchedulePtmScreenV2.kt` (287 lines, 1 string)
- [x] `school/ScheduledMessagesScreenV2.kt` (243 lines, 0 strings)
- [x] `school/IdCardScreen.kt` (99 lines, 0 strings)
- [x] `school/PewsPreview.kt` (158 lines, 0 strings)

---

## Phase 4 — Teacher Screens (22 files, ~163 hardcoded strings)

- [x] `teacher/TeacherSyllabusScreenV2.kt` (1640 lines, 54 strings)
- [x] `teacher/TeacherLessonPlanScreenV2.kt` (860 lines, 24 strings)
- [x] `teacher/TeacherHomeScreenV2.kt` (739 lines, 8 strings)
- [x] `teacher/TeacherMessagesScreenV2.kt` (550 lines, 0 strings)
- [x] `teacher/TeacherClassesScreenV2.kt` (622 lines, 12 strings)
- [x] `teacher/TeacherProfileScreenV2.kt` (569 lines, 10 strings)
- [x] `teacher/TeacherMarksScreenV2.kt` (415 lines, 13 strings)
- [x] `teacher/TeacherTimetableScreenV2.kt` (423 lines, 11 strings)
- [x] `teacher/TeacherPewsScreenV2.kt` (407 lines, 5 strings)
- [x] `teacher/TeacherKit.kt` (405 lines, 0 strings)
- [x] `teacher/TeacherHomeworkScreenV2.kt` (329 lines, 7 strings)
- [x] `teacher/TeacherPortalV2.kt` (301 lines, 0 strings)
- [x] `teacher/TeacherStudentProfileScreenV2.kt` (317 lines, 3 strings)
- [x] `teacher/TeacherReportReviewQueueScreen.kt` (228 lines, 6 strings)
- [x] `teacher/TeacherUpdateScreenV2.kt` (215 lines, 2 strings)
- [x] `teacher/TeacherCheckInPopup.kt` (205 lines, 0 strings)
- [x] `teacher/TeacherReportDraftEditorScreen.kt` (127 lines, 6 strings)
- [x] `teacher/TeacherDock.kt` (239 lines, 0 strings)
- [x] `teacher/TeacherAttendanceScreenV2.kt` (247 lines, 1 string)
- [x] `teacher/TeacherHeader.kt` (181 lines, 0 strings)
- [x] `teacher/TeacherHealthAlertsScreenV2.kt` (128 lines, 3 strings)
- [x] `teacher/TeacherScopeSelector.kt` (153 lines, 1 string)
- [x] `teacher/TeacherDialogs.kt` (90 lines, 0 strings)
- [x] `teacher/TransportAttendanceScreenV2.kt` (222 lines, 0 strings)
- [x] `teacher/TeacherPtmEventRegistrationScreenV2.kt` (301 lines, 0 strings)

---

## Phase 5 — Library Screens (4 files, ~70 hardcoded strings)

- [ ] `student/StudentLibraryScreen.kt` (1018 lines, 26 strings)
- [ ] `library/LibraryUixComponents.kt` (485 lines, 11 strings)
- [ ] `library/LibraryUixComponents2.kt` (418 lines, 19 strings)
- [ ] `library/LibraryUixComponents3.kt` (286 lines, 14 strings)

---

## Phase 6 — Discovery Screens (3 files, ~8 hardcoded strings)

- [ ] `discovery/DiscoveryScreenV2.kt` (810 lines, 6 strings)
- [ ] `discovery/AcademicCalendarScreenV2.kt` (287 lines, 0 strings)
- [ ] `discovery/SriPreview.kt` (90 lines, 2 strings)

---

## Phase 7 — Notifications (1 file, 0 hardcoded strings)

- [ ] `notifications/NotificationsScreenV2.kt` (440 lines, 0 strings)

---

## Phase 8 — Tutor Screens (4 files, ~4 hardcoded strings)

- [ ] `tutor/TutorChatScreen.kt` (317 lines, 1 string)
- [ ] `tutor/TutorPracticeScreen.kt` (257 lines, 2 strings)
- [ ] `tutor/ParentProgressScreen.kt` (221 lines, 1 string)
- [ ] `tutor/TeacherHeatmapScreen.kt` (256 lines, 0 strings)

---

## Phase 9 — Shared/Other (3 files, 0 hardcoded strings)

- [ ] `Shared.kt` (198 lines, 0 strings)
- [ ] `Skeletons.kt` (221 lines, 0 strings)

---

## Per-Language Translation Tracking

For each language, track translation completion of `AppStrings.kt` string maps.

### String Keys Added

| Phase | Keys Added | Running Total |
|-------|-----------|---------------|
| Baseline (LanguageSelectionScreen) | 6 | 6 |
| SchoolSettingsScreenV2 (partial) | 3 | 9 |
| Phase 1 — Auth | 120 | 129 |
| Phase 2 — Parent | — | — |
| Phase 3 — School/Admin | — | — |
| Phase 4 — Teacher | — | — |
| Phase 5 — Library | — | — |
| Phase 6 — Discovery | — | — |
| Phase 7 — Notifications | — | — |
| Phase 8 — Tutor | — | — |
| Phase 9 — Shared | — | — |

### Translation Progress Per Language

| Language | Strings Translated | Total Strings | % Complete |
|----------|-------------------|---------------|------------|
| `hi` (Hindi) | 129 | 129 | 100% |
| `bn` (Bengali) | — | — | — |
| `ta` (Tamil) | — | — | — |
| `te` (Telugu) | — | — | — |
| `mr` (Marathi) | — | — | — |
| `gu` (Gujarati) | — | — | — |
| `kn` (Kannada) | — | — | — |
| `ml` (Malayalam) | — | — | — |
| `pa` (Punjabi) | — | — | — |

---

## How to Localize a Screen

1. **Identify all hardcoded strings** — search for `Text("`, `label = "`, `title = "`, `message = "`, `hint = "`, `placeholder = "`, `confirmLabel = "`, `cancelLabel = "` in the file
2. **Add StringKeys** — add `const val` entries to `AppStrings.kt` → `StringKeys` object
3. **Add English values** — add entries to the `en` map in `AppStrings.kt`
4. **Add Hindi values** — add entries to the `hi` map in `AppStrings.kt`
5. **Add other 8 language values** — add entries to `bn`, `ta`, `te`, `mr`, `gu`, `kn`, `ml`, `pa` maps (or leave as English fallback initially)
6. **Replace hardcoded strings in the screen** — change `Text("Settings")` to `Text(appString(StringKeys.SETTINGS_TITLE))`
7. **Add imports** — `import com.littlebridge.enrollplus.core.locale.StringKeys` and `import com.littlebridge.enrollplus.ui.v2.locale.appString`
8. **Build & test** — `./gradlew :composeApp:compileDevDebugKotlinAndroid`
9. **Check the box** in this tracker

---

## Summary Stats

| Metric | Value |
|--------|-------|
| Total screen files | 120 |
| Already localized | 12 |
| Remaining | 108 |
| Total hardcoded strings (approx) | ~743 |
| Total lines of screen code | ~48,000 |
