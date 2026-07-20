# Bug Type Location Audit — v1.0.0

Comprehensive mapping of each bug type category to all screens, components, and code locations in the codebase that could exhibit that class of bug.

---

## 1. Input Validation

All screens with user-editable text fields (`VInput`) or picker selections (`VSheetPicker`, `VDropdown`) where validation logic may be missing, incomplete, or bypassed.

### 1.1 SchoolRegistrationFlow.kt — Onboarding & Registration

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/auth/SchoolRegistrationFlow.kt`

| Step | Input Field | Component | Validation Function | Potential Issues |
|------|------------|-----------|---------------------|-----------------|
| Step 1: Basic Details | Admin Full Name | `VInput` | `validateName()` (L93) | Allows only letters, spaces, dots, hyphens. No unicode support for Indian names. |
| Step 1: Basic Details | Email Address | `VInput` | `validateEmail()` (L102) | Regex-based; edge cases with subdomains, plus signs, quoted local parts. |
| Step 1: Basic Details | Phone Number | `VInput` | `validatePhone()` (L117) | Hardcoded to 10-digit Indian mobile. No international support. Input filtered to digits, max 10. |
| Step 1: Basic Details | Your Role | `VSheetPicker` | Blank check only | No custom role option. Validation is `isBlank()` only. |
| Step 2: Create Password | Password | `VInput` | `validatePassword()` (L125) | 8–128 chars, upper+lower+digit+special. No common-password check. |
| Step 2: Create Password | Confirm Password | `VInput` | `validateConfirmPassword()` (L136) | Simple equality check. |
| Step 3: School Identity | Full Legal Name | `VInput` | `validateSchoolName()` (L142) | Allows letters, spaces, dots, hyphens, apostrophes. Min 3 chars. |
| Step 3: School Identity | Short Name | `VInput` | `validateShortName()` (L150) | Only letters + spaces. Min 2 chars. No max length check. |
| Step 3: School Identity | Affiliation Number | `VInput` | `validateAffiliationNumber()` (L158) | Optional (blank = valid). 3–30 chars, alphanumeric only. |
| Step 3: School Identity | Board | `VChipGroup` | Blank check | "Other" requires `customBoard` to be non-blank. No format validation on custom board name. |
| Step 3: School Identity | School Type | `VChipGroup` | Blank check only | No custom type option. |
| Step 3: School Identity | Principal's Name | `VInput` | `validatePrincipalName()` (L167) | Optional if blank; validated only if non-blank. No min length check. |
| Step 3: School Identity | Principal's Mobile | `VInput` | `validatePrincipalPhone()` (L173) | Same as `validatePhone()`. Optional if blank. Input filtered to digits, max 10. |
| Step 3: School Identity | City | `VSheetPicker` | None | No validation — user can proceed without selecting city. |
| Step 4: Academic Year | Academic Year Label | `VChipGroup` | Blank check | Only 2 hardcoded options from `academicYearOptions()`. |
| Step 4: Academic Year | Year Start Date | `VMaterialDatePicker` | Blank check | ISO date string. No range validation against current date. |
| Step 4: Academic Year | Year End Date | `VMaterialDatePicker` | Blank + `start < end` check | String comparison of ISO dates. |
| Step 4: Academic Year | Working Days | `VChipGroup` | Blank check | Only "Mon-Fri" / "Mon-Sat". |
| Step 4: Academic Year | Start Time / End Time | `VMaterialTimePicker` | `endTime > startTime` check | Parsed from string. Edge case: midnight crossing not handled. |
| Step 4: Academic Year | Periods per Day | `VSheetPicker` | Blank check | Options 4–12. No custom value. |

### 1.2 EditSchoolProfileScreenV2.kt — Edit School Profile

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/school/EditSchoolProfileScreenV2.kt`

| Section | Input Field | Component | Validation | Potential Issues |
|---------|------------|-----------|------------|-----------------|
| School Profile | School Name | `VInput` | `state.fieldErrors["name"]` | Server-side validation only; client lacks `validateSchoolName()` reuse. |
| School Profile | Board | `VDropdown` | None | "Other" shows custom board input but no validation on custom value. |
| School Profile | Custom Board | `VInput` | `state.fieldErrors["customBoard"]` | Only shown when board = "Other". No client-side format check. |
| School Profile | Medium | `VDropdown` | None | No validation. |
| School Profile | School Type | `VDropdown` | None | No validation. |
| Contact Details | School Phone | `VInput` | `state.fieldErrors["contactPhone"]` | No client-side phone format validation. |
| Contact Details | School Email | `VInput` | `state.fieldErrors["contactEmail"]` | No client-side email format validation. |
| Contact Details | Principal Name | `VInput` | None | No validation at all — no `validatePrincipalName()` call. |
| Contact Details | Principal Phone | `VInput` | `state.fieldErrors["principalPhone"]` | No client-side phone format validation. |
| Contact Details | Principal Email | `VInput` | `state.fieldErrors["principalEmail"]` | No client-side email format validation. **Bug 22: duplicate field.** |
| Location | Address | `VInput` (multiLine) | None | No validation. |
| Location | City | `VDropdown` | `state.fieldErrors["city"]` | **Bug 18/21: city-pincode mismatch not validated.** |
| Location | PIN Code | `VInput` (numeric) | `state.fieldErrors["pincode"]` | **Bug 19: invalid PIN codes accepted.** No length or format check on client. |
| Location | District | `VInput` | `state.fieldErrors["district"]` | No format validation. |
| Location | State | `VDropdown` | None | No validation. **Bug 21: state hardcoded, may not match city.** |

### 1.3 SchoolPeopleScreenV2.kt — Add Teacher / Add Staff / Add Student Sheets

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/school/SchoolPeopleScreenV2.kt`

| Sheet | Input Field | Component | Validation | Potential Issues |
|-------|------------|-----------|------------|-----------------|
| AddTeacherSheet (L966) | Teacher Name | `VInput` | `isNotBlank()` only | No name format validation. Special characters accepted. |
| AddTeacherSheet | Email or Phone | `VInput` | `isNotBlank()` + email check for password | No email format validation. No phone format validation. |
| AddTeacherSheet | Initial Password | `VInput` (password) | `isNotBlank()` if email | No password strength validation. |
| AddTeacherSheet | Class | `VSheetPicker` | None (optional) | No validation — can submit without class. |
| AddTeacherSheet | Section | `VSheetPicker` | None (disabled if no class) | No validation. |
| AddStaffSheet (L1079) | Staff Name | `VInput` | `isNotBlank()` only | No name format validation. |
| AddStaffSheet | Role | `VInput` | `isNotBlank()` only | **Bug 14: should be dropdown, not text field.** |
| AddStaffSheet | Department | `VInput` | None (optional) | No validation. |
| AddStaffSheet | Phone | `VInput` | None (optional) | No phone format validation. |
| AddStaffSheet | Email | `VInput` | None (optional) | No email format validation. |
| AddStudentPeopleSheet (L1168) | Student Name | `VInput` | `isNotBlank()` only | No name format validation. |
| AddStudentPeopleSheet | Class | `VSheetPicker` | `classValid` check | Validates class is in available list. |
| AddStudentPeopleSheet | Section | `VSheetPicker` | None | No validation — can submit without section. |
| AddStudentPeopleSheet | Roll Number | `VInput` (numeric) | `isNotBlank()` only | No format/length validation. |
| AddStudentPeopleSheet | Admission Date | `VInput` | None | Free-text "YYYY-MM-DD" — **no date picker, no format validation.** |
| AddStudentPeopleSheet | Parent Phone | `VInput` (phone) | `phoneDigits >= 10` if non-blank | Lax validation — accepts 10+ digits anywhere. No Indian prefix check. |

### 1.4 TeacherProfileScreenV2.kt — Leave & Password

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/teacher/TeacherProfileScreenV2.kt`

| Section | Input Field | Component | Validation | Potential Issues |
|---------|------------|-----------|------------|-----------------|
| LeaveComposer (L583) | Start Date | `VDatePicker` | None | No validation that start date is not in the past. |
| LeaveComposer | End Date | `VDatePicker` | None | No validation that end date > start date. |
| LeaveComposer | Reason | `VInput` | None | No min length check. |
| PasswordCard (L640+) | Old Password | `VInput` (password) | None | No validation. |
| PasswordCard | New Password | `VInput` (password) | None | **No `validatePassword()` reuse.** Password strength not checked. |
| PasswordCard | Confirm Password | `VInput` (password) | None | No match validation on client. |

### 1.5 VInput.kt — Component Level

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/components/VInput.kt`

- `VInput` accepts `isError: Boolean` and `errorText: String?` but does **no internal validation** — all validation is delegated to the caller.
- No built-in max length, character filter, or format mask.
- No built-in required-field indicator.

---

## 2. API / Loading

All screens and ViewModels with loading states, API calls, or refresh mechanisms that could cause infinite loading, stuck spinners, or refresh loops.

### 2.1 Loading State Components

| Component | File | Risk |
|-----------|------|------|
| `VButton` (loading param) | `composeApp/.../ui/v2/components/VButton.kt` | If `loading` never flips to false, spinner persists. **Bug 5 risk.** |
| `VStateHost` (v2 version) | `composeApp/.../ui/v2/screens/Shared.kt` | Manages loading/error/empty/content transitions. If `loading` stays true, content never renders. |
| `VStateHost` (standalone) | `composeApp/.../ui/components/VStateHost.kt` | Takes `UiState<T>` — skeleton shown while `isLoading`. |
| `VPullRefresh` | `composeApp/.../ui/v2/components/VPullRefresh.kt` | If `isRefreshing` never resets to false, indicator stays. **Bug 23 risk.** |
| `TeacherSpinner` | `composeApp/.../ui/v2/screens/teacher/TeacherKit.kt` | Plain `CircularProgressIndicator` — no auto-dismiss. |
| `SkeletonList` / `SkeletonDashboard` / `SkeletonProfile` | Various screens in `ui/v2/screens/` | Static shimmer placeholders shown during load. |

### 2.2 Screens with Loading / API State

| Screen | File | ViewModel | Loading State | Risk |
|--------|------|-----------|---------------|------|
| SchoolRegistrationFlow | `auth/SchoolRegistrationFlow.kt` | `RegistrationOnboardingViewModel` | `state.isLoading` on Continue/Register buttons | **Bug 5: infinite loading on Continue.** If API fails silently, `isLoading` may not reset. |
| SchoolSettingsScreenV2 | `school/SchoolSettingsScreenV2.kt` | `InstitutionalProfileViewModel` | `state.isLoading` + `VPullRefresh` | **Bug 23: refresh loop.** If `load()` triggers state change that re-triggers `LaunchedEffect`. |
| EditSchoolProfileScreenV2 | `school/EditSchoolProfileScreenV2.kt` | `SchoolProfileViewModel` | `state.isLoading` + `VStateHost` | Save button loading state may not reset on error. |
| NotificationPreferencesScreenV2 | `notifications/NotificationPreferencesScreenV2.kt` | `NotificationPreferencesViewModel` | `state.isLoading` + `VPullRefresh` | Refresh could loop if `load()` is called in `LaunchedEffect`. |
| SchoolHomeScreenV2 | `school/SchoolHomeScreenV2.kt` | Multiple VMs | Various `isLoading` states | Multiple concurrent API calls; one failing could leave partial loading. |
| SchoolPeopleScreenV2 | `school/SchoolPeopleScreenV2.kt` | `SchoolTeachersViewModel`, `StudentRosterViewModel`, `StaffViewModel` | Various loading states | **Bug 13: new teacher not visible** — list may not refresh after add. |
| SchoolCommsScreenV2 | `school/SchoolCommsScreenV2.kt` | `MessagesViewModel`, `AnnouncementsViewModel` | Various loading states | Tab switch may not cancel in-flight requests. |
| MessagesScreenV2 | `school/MessagesScreenV2.kt` | `MessagesViewModel` | `state.isLoading` | Thread list loading. |
| NotificationsScreenV2 | `notifications/NotificationsScreenV2.kt` | `NotificationsViewModel` | `isRefreshing` | Pull-to-refresh state. |
| AcademicCalendarScreenV2 | `discovery/AcademicCalendarScreenV2.kt` | Calendar VM | `state.isLoading` | Event list loading. |
| TeacherHomeScreenV2 | `teacher/TeacherHomeScreenV2.kt` | Multiple VMs | Various loading states | Insights, attendance, quick actions. |
| TeacherReportDraftEditorScreen | `teacher/TeacherReportDraftEditorScreen.kt` | Report VM | `state.isLoading` | Report draft auto-save. |
| AlumniScreen | `school/AlumniScreen.kt` | `AlumniViewModel` | `state.isLoading` | Directory/campaigns loading. |
| HealthRecordsScreenV2 | `school/HealthRecordsScreenV2.kt` | `HealthRecordsViewModel` | `state.isLoading` | Student health records. |
| FeeSalaryManagementScreen | `school/FeeSalaryManagementScreen.kt` | Fee/Salary VMs | `state.isLoading` | Fee structure / salary loading. |
| IdCardScreen | `school/IdCardScreen.kt` | `IdCardViewModel` | `state.isLoading` | Template/card generation. |
| ClassesSubjectsScreenV2 | `school/ClassesSubjectsScreenV2.kt` | Classes/Subjects VMs | `state.isLoading` | Class/subject list loading. |
| SchoolRecordsScreenV2 | `school/SchoolRecordsScreenV2.kt` | `SchoolRecordsViewModel` | `state.isLoading` | Records summary loading. |
| ClassDetailScreenV2 | `school/ClassDetailScreenV2.kt` | ClassDetail VM | `state.isLoading` | Class detail loading. |
| Transport management, Library, Scholarships, Gamification, Branding | Various school overlay screens | Respective VMs | `state.isLoading` | Standard loading pattern. |

### 2.3 OnboardingGateViewModel — Post-Login Gate

**File:** `shared/.../feature/admin/presentation/OnboardingGateViewModel.kt`

- `OnboardingGate.Resolving` state shows `SkeletonDashboard`.
- If `GET /api/v1/onboarding/status` fails or hangs, user is stuck on resolving skeleton.
- **Bug 5 connection:** if onboarding status API fails, the Continue button on registration may appear to hang.

---

## 3. Navigation

All screens with navigation flows, overlay transitions, or tab-switching logic that could route to wrong screens or lose state.

### 3.1 SchoolPortalV2 — Overlay Navigation

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/school/SchoolPortalV2.kt`

| Overlay | Trigger | Expected Target | Risk |
|---------|---------|----------------|------|
| `SchoolOverlay.AdmissionsCRM` | Quick Action "Add Student" | Should open Student Details | **Bug 6: opens Admissions CRM instead.** |
| `SchoolOverlay.StudentRoster` | People tab → Students sub-tab | Student roster list | Correct routing. |
| `SchoolOverlay.TeacherProfile` | People tab → Teacher tap | Teacher profile overlay | **Bug 11: edit mode not working** — `teacherProfileStartInEdit` flag may not be passed correctly. |
| `SchoolOverlay.ClassDetail` | Classes tab → Class tap | Class detail overlay | Back handler pops to `ClassesSubjects`. |
| `SchoolOverlay.PewsStudentDetail` | PEWS → Student tap | PEWS detail with student code | `selectedPewsStudentCode` must be set before overlay. |
| `SchoolOverlay.CreateEvent` | Quick Action "Announce" / "Add Event" | Create Event wizard | `createEventInitialType` differentiates announce vs event. |
| `SchoolOverlay.Messages` | Comms tab → Messages | Messages overlay | `deepLinkThreadId` for deep link navigation. |
| Deep link routing | `DeepLinkTarget.SchoolScreen` | 25+ screen mappings | `else -> tab = "home"` fallback for unknown screens. |
| Deep link routing | `DeepLinkTarget.Messages` | Messages with thread ID | Sets tab + overlay. |
| Deep link routing | `DeepLinkTarget.Generic` | Path-based routing | 15+ path prefix matches. |

### 3.2 NavGraphV2 — AuthedFlow Gate

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/navigation/NavGraphV2.kt`

| Route | Condition | Risk |
|-------|-----------|------|
| `AuthedRoute.Resolving` | Initial state for all roles | Brief skeleton frame. If gate never resolves, user stuck. |
| `AuthedRoute.TeacherFirstLogin` | Teacher + `!profileCompleted` | Change-password gate. |
| `AuthedRoute.Portal` | All other cases | Hands off to `RolePortal`. |
| `AuthedRoute.ParentLinkChild` | Not currently used | Parent lands directly on Portal (RA-S04). |

### 3.3 TeacherPortalV2 — Teacher Navigation

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/teacher/TeacherPortalV2.kt`

- 5-tab bottom nav: Home, Update, Classes, Timetable, Profile.
- Overlay state machine for teacher-specific screens.
- **Bug 11:** Teacher Profile edit navigation — `onEdit` callback may not toggle edit mode.

### 3.4 ParentPortalV2 — Parent Navigation

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/parent/ParentPortalV2.kt`

- 5-tab bottom nav: Home, Academics, Fees, Conversations, Profile.
- Overlay state machine for parent-specific screens.

---

## 4. UI Layout — Tab Overlap

All screens using `VTopTabs` or `HorizontalPager` for sub-tab navigation where tabs could overlap or content could bleed.

### 4.1 VTopTabs Component

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/components/VNavigation.kt` (L76–178)

- Horizontally scrollable pill-style tab bar.
- Active tab: colored bg + white text + scale 1.0.
- Inactive: transparent bg + `ink3` + scale 0.98.
- **Risk:** If tab labels are long and screen is narrow, pills may overflow or overlap. No `maxLines` or text truncation on tab labels.

### 4.2 Screens Using VTopTabs

| Screen | File | Tabs | Bug Risk |
|--------|------|------|----------|
| SchoolPeopleScreenV2 | `school/SchoolPeopleScreenV2.kt:330` | Teachers, Students, Staff | **Bug 15: Teacher & Non-Teaching tab overlap.** |
| SchoolCommsScreenV2 | `school/SchoolCommsScreenV2.kt:178` | Announcements, Messages, PTM, Notifications | **Bug 17: Common Hub tab overlapping.** |
| SchoolRecordsScreenV2 | `school/SchoolRecordsScreenV2.kt:230` | Coverage, Pace, Attendance, Marks, Fee, Documents | 6 tabs — high overlap risk on narrow screens. |
| AlumniScreen | `school/AlumniScreen.kt:67` | Directory, Pending, Campaigns, Donations, Mentorship, Analytics | 6 tabs — high overlap risk. |
| AlumniDetailScreen | `school/AlumniDetailScreen.kt:90` | Profile, Career, Donations | 3 tabs — low risk. |
| HealthRecordsScreenV2 | `school/HealthRecordsScreenV2.kt:104` | Profile, Immunizations, Incidents | 3 tabs — low risk. |
| ClassesSubjectsScreenV2 | `school/ClassesSubjectsScreenV2.kt:180` | Classes, Subjects | 2 tabs — low risk. |
| IdCardScreen | `school/IdCardScreen.kt:42` | Templates, Generate, Cards | 3 tabs — low risk. |
| FeeSalaryManagementScreen | `school/FeeSalaryManagementScreen.kt:54` | Fees, Salary (nested sub-tabs: Structure, Payments, Charges, Reminders, Late Fees) | **Complex nested tabs — high overlap risk.** |
| ClassDetailScreenV2 | `school/ClassDetailScreenV2.kt:78` | Students, Teachers, Timetable, Analytics | 4 tabs — medium risk. |
| TeacherProfileScreenV2 | `teacher/TeacherProfileScreenV2.kt` | Profile, Leave, Security | **Bug 12: Danger Zone UI overlapping.** |

### 4.3 HorizontalPager Usage (Alternative Tab Pattern)

| Screen | File | Pager Usage | Risk |
|--------|------|-------------|------|
| SchoolPeopleScreenV2 | `school/SchoolPeopleScreenV2.kt` | 3-page pager for Teachers/Students/Staff | Page content may not sync with tab indicator. |
| SchoolCommsScreenV2 | `school/SchoolCommsScreenV2.kt` | 4-page pager for Announcements/Messages/PTM/Notifications | Same risk. |
| LandingScreen | `auth/LandingScreen.kt` | 2-slide pager (Parents / Schools) | Low risk — only 2 slides. |
| SuccessScreen | `auth/SchoolRegistrationFlow.kt:955` | Feature carousel pager | Low risk — display only. |

---

## 5. UI / Missing Header

All overlay screens that should display a `VBackHeader` but may be missing one.

### 5.1 VBackHeader Component

**File:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/components/VNavigation.kt:913–955`

- Back chevron + centered title + optional trailing action.
- **V2 version** uses `VTheme.colors` (theme-aware).
- **Legacy version** at `composeApp/.../ui/components/VBackHeader.kt` uses hardcoded `VColors` (not theme-aware).

### 5.2 Screens with Headers

| Screen | File | Header Type | Bug Risk |
|--------|------|-------------|----------|
| NotificationPreferencesScreenV2 | `notifications/NotificationPreferencesScreenV2.kt:54` | `VBackHeader` | OK — header present. |
| NotificationsScreenV2 | `notifications/NotificationsScreenV2.kt:160` | `PremiumNotificationHeader` | OK — custom header present. |
| EditSchoolProfileScreenV2 | `school/EditSchoolProfileScreenV2.kt:192` | Custom Row with `IconButton` | Non-standard — doesn't use `VBackHeader`. |
| MessagesScreenV2 | `school/MessagesScreenV2.kt` | `VBackHeader` | OK. |
| AcademicCalendarScreenV2 | `discovery/AcademicCalendarScreenV2.kt` | `VBackHeader` | OK. |
| TeacherReportDraftEditorScreen | `teacher/TeacherReportDraftEditorScreen.kt` | `VBackHeader` | OK. |
| SchoolCommsScreenV2 — Announcements tab | `school/SchoolCommsScreenV2.kt` | **Missing?** | **Bug 7: Announcement screen header missing.** |
| SchoolCommsScreenV2 — Reports/Report Publish | `school/SchoolCommsScreenV2.kt` | **Missing?** | **Bug 8: Report screen header missing.** |

### 5.3 Overlay Screens — Header Audit

All `SchoolOverlay` screens should have a back header since they're full-screen overlays:

| Overlay | Screen File | Has Header? |
|---------|------------|-------------|
| Notifications | `NotificationsScreenV2.kt` | Yes (`PremiumNotificationHeader`) |
| AcademicCalendarPlatform | `AcademicCalendarScreenV2.kt` | Yes |
| CreateEvent | Event wizard screen | Yes |
| Messages | `MessagesScreenV2.kt` | Yes |
| LeaveRequests | Leave requests screen | Yes |
| LinkRequests | Link requests screen | Yes |
| AdmissionsCRM | Admissions CRM screen | Yes |
| Results | Results screen | Yes |
| EditProfile | `EditSchoolProfileScreenV2.kt` | Custom (non-standard) |
| StudentRoster | Roster screen | Yes |
| PewsCohort | PEWS screen | Yes |
| TeacherProfile | `TeacherProfileScreenV2.kt` | Yes |
| HealthRecords | `HealthRecordsScreenV2.kt` | Yes |
| Alumni | `AlumniScreen.kt` | Yes |
| TransportManagement | Transport screen | Yes |
| ReportPublish | Report publish screen | **Bug 8: header missing** |
| ScholarshipManagement | Scholarship screen | Yes |
| BrandingKit | Branding screen | Yes |
| IdCards | `IdCardScreen.kt` | Yes |
| Library | Library screen | Yes |
| ClassesSubjects | `ClassesSubjectsScreenV2.kt` | Yes |
| FeeSalaryManagement | `FeeSalaryManagementScreen.kt` | Yes |
| GamificationManagement | Gamification screen | Yes |
| NotificationPreferences | `NotificationPreferencesScreenV2.kt` | Yes |

---

## 6. UI / Missing Component

Screens where a required UI component is absent or conditionally hidden.

| Bug | Screen | File | Missing Component |
|-----|--------|------|-------------------|
| Bug 3 | SchoolRegistrationFlow Step 3 | `auth/SchoolRegistrationFlow.kt:723` | "Other" board text field — should appear when "Other" chip is selected. Now fixed. |
| Bug 3 | EditSchoolProfileScreenV2 | `school/EditSchoolProfileScreenV2.kt:274` | Same "Other" board field in edit profile. Now fixed. |

---

## 7. UI / Component Type

Screens where the wrong component type is used for a field (e.g., text field instead of dropdown).

| Bug | Screen | File | Current Component | Expected Component |
|-----|--------|------|-------------------|-------------------|
| Bug 14 | AddStaffSheet — Role field | `school/SchoolPeopleScreenV2.kt:1106` | `VInput` (free text) | `VDropdown` or `VSheetPicker` (select from predefined roles) |

### Other Potential Component Type Issues

| Screen | File | Field | Current | Potential Issue |
|--------|------|-------|---------|----------------|
| AddStudentPeopleSheet | `SchoolPeopleScreenV2.kt:1217` | Admission Date | `VInput` (free text "YYYY-MM-DD") | Should use `VDatePicker` or `VMaterialDatePicker` |
| EditSchoolProfileScreenV2 | `EditSchoolProfileScreenV2.kt:386` | District | `VInput` (free text) | Could be `VDropdown` if district list is available |
| SchoolRegistrationFlow Step 3 | `SchoolRegistrationFlow.kt:747` | City | `VSheetPicker` | OK — already uses picker. |
| EditSchoolProfileScreenV2 | `EditSchoolProfileScreenV2.kt:369` | City | `VDropdown` | OK — but **Bug 20: inconsistent with `VSheetPicker` used in registration.** |

---

## 8. UI / Design System — Inconsistent Components

Locations where different component styles are used for the same purpose.

| Component Pair | Location A | Location B | Inconsistency |
|---------------|-----------|-----------|---------------|
| `VDropdown` vs `VSheetPicker` | `EditSchoolProfileScreenV2.kt:267` (Board, Medium, School Type, City, State) | `SchoolRegistrationFlow.kt:559` (Role, City, Periods) | **Bug 20: Role dropdown in onboarding uses `VSheetPicker`, but profile edit uses `VDropdown`.** Different visual styles. |
| `VBackHeader` (v2) vs `VBackHeader` (legacy) | `ui/v2/components/VNavigation.kt:922` | `ui/components/VBackHeader.kt:33` | Two different implementations. V2 is theme-aware; legacy uses hardcoded colors. |
| `VMaterialDatePicker` vs `VDatePicker` | `SchoolRegistrationFlow.kt:820` | Various screens | `VMaterialDatePicker` used in registration; `VDatePicker` is the app-wide standard per design system. |
| `VMaterialTimePicker` | `SchoolRegistrationFlow.kt:835` | Only used in registration | No standard time picker component in design system. |
| `VChipGroup` (private) vs `VChipGroup` (if exists in components) | `SchoolRegistrationFlow.kt:214` | N/A | Private to registration flow — not reusable. |

---

## 9. Data / Logic

Screens where computed data may be incorrect or stale.

| Bug | Screen | File | Logic Issue |
|-----|--------|------|-------------|
| Bug 9 | Analytics Dashboard | `school/SchoolHomeScreenV2.kt` or analytics overlay | Attendance trend shows 4.2% for new school with no data. Should show 0% or empty state. |
| Bug 16 | Dashboard Staff Count | `school/SchoolHomeScreenV2.kt` | Staff count only counts teachers, not non-teaching staff. Should count both. |
| Bug 21 | School Location | `school/EditSchoolProfileScreenV2.kt` | State field hardcoded to UP options; doesn't auto-populate from city selection. Bangalore → should show Karnataka, not UP. |

---

## 10. Data / State Sync

Screens where local state may not reflect server state after mutations.

| Bug | Screen | File | State Sync Issue |
|-----|--------|------|------------------|
| Bug 13 | Teacher List | `school/SchoolPeopleScreenV2.kt` | After adding a teacher, the list doesn't refresh. `SchoolTeachersViewModel` may not re-fetch or the pager may not notify the page. `peopleRefreshKey` is incremented but may not trigger VM reload. |
| Bug 5 | Onboarding Continue | `auth/SchoolRegistrationFlow.kt` | After `submitBasicDetails` / `createAccount` / `submitSchoolIdentity` / `submitAcademicYear`, if the API succeeds but the state transition fails, the user is stuck. `isLoading` stays true. |

### State Sync Risk Areas

| Screen | File | Mutation | Refresh Mechanism | Risk |
|--------|------|----------|-------------------|------|
| SchoolPeopleScreenV2 — Add Teacher | `SchoolPeopleScreenV2.kt` | `onSubmit(name, identifier, password)` | `peopleRefreshKey++` | Key increment may not trigger VM `load()`. |
| SchoolPeopleScreenV2 — Add Student | `SchoolPeopleScreenV2.kt` | `onSubmit(name, class, section, roll, phone, date)` | `studentRefreshKey++` | Same risk. |
| SchoolPeopleScreenV2 — Add Staff | `SchoolPeopleScreenV2.kt` | `onSubmit(name, role, dept, phone, email)` | `peopleRefreshKey++` | Same risk. |
| EditSchoolProfileScreenV2 — Save | `EditSchoolProfileScreenV2.kt` | `onSave()` | `state.infoMessage` / `state.errorMessage` | No auto-refresh of profile data after save. |
| SchoolCommsScreenV2 — Send Announcement | `SchoolCommsScreenV2.kt` | Announcement compose | Tab switch | Sent announcement may not appear in list without manual refresh. |

---

## 11. UI / Duplicate Field

Screens where the same data field appears more than once.

| Bug | Screen | File | Duplicate Field | Details |
|-----|--------|------|-----------------|---------|
| Bug 22 | Edit School Profile — Contact Details | `school/EditSchoolProfileScreenV2.kt:342` | Principal Email | `VInput` for Principal Email appears at L342. Check if another instance exists in the same card or if the server returns it twice causing dual rendering. |

---

## Summary — Bug Type to Location Count

| Bug Type | Screens Affected | Components Involved | Open Bugs |
|----------|-----------------|---------------------|-----------|
| Input Validation | 4+ screens, 30+ fields | `VInput`, `VSheetPicker`, `VDropdown`, `VChipGroup` | Bugs 1, 2, 18, 19 |
| API / Loading | 20+ screens, 20+ ViewModels | `VButton`, `VStateHost`, `VPullRefresh`, `TeacherSpinner` | Bugs 5, 23 |
| Navigation | 3 portal shells, 40+ overlays | `SchoolPortalV2`, `NavGraphV2`, `TeacherPortalV2`, `ParentPortalV2` | Bugs 6, 11 |
| UI Layout (Tab Overlap) | 11 screens with `VTopTabs` | `VTopTabs`, `HorizontalPager` | Bugs 4, 10, 12, 15, 17 |
| UI / Missing Header | 2+ overlay screens | `VBackHeader`, `PremiumNotificationHeader` | Bugs 7, 8 |
| UI / Missing Component | 2 screens | `VInput` (conditional) | Bug 3 |
| UI / Component Type | 2+ screens | `VInput` vs `VSheetPicker`/`VDropdown` | Bug 14 |
| UI / Design System | 5+ component pairs | `VDropdown` vs `VSheetPicker`, `VBackHeader` variants | Bug 20 |
| Data / Logic | 3 screens | Analytics, Dashboard, Profile | Bugs 9, 16, 21 |
| Data / State Sync | 5+ mutation flows | Various ViewModels | Bugs 5, 13 |
| UI / Duplicate Field | 1 screen | `VInput` | Bug 22 |
