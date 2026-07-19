# Test Cases — Bug Fixes v1.0.0

**Branch:** `fix/bug-fixes-v1.0.0`
**Date:** 2026-07-19
**Scope:** Bugs 12–23 (P1, P2, P3)

---

## Test Case Format

| Field | Description |
|-------|-------------|
| **TC ID** | Unique test case identifier |
| **Bug #** | Associated bug number |
| **Priority** | P1 / P2 / P3 |
| **Type** | UI / Data / Validation / API / Navigation |
| **Preconditions** | Required state before test |
| **Steps** | Sequential actions to execute |
| **Expected Result** | What should happen after steps |
| **Negative Test** | Edge case or failure scenario |

---

## Bug 12 (P2): Danger Zone UI Overlapping in TeacherProfileScreenV2

**File:** `composeApp/.../TeacherProfileScreenV2.kt`
**Fix:** Wrapped `VStateHost` content lambda in `Column` to prevent composable stacking inside `AnimatedContent`'s `Box`.

### TC-12.1: Danger Zone renders without overlap (loading → content)

| Field | Value |
|-------|-------|
| **TC ID** | TC-12.1 |
| **Bug #** | 12 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | School admin logged in. At least one teacher exists in the school. |
| **Steps** | 1. Navigate to People → Teachers tab. 2. Tap a teacher card to open `TeacherProfileScreenV2`. 3. Wait for profile to load (skeleton → content transition). 4. Scroll to the bottom of the profile. |
| **Expected Result** | Danger Zone section displays below all other sections. `VSectionHeader`, `VCard` with warning text, and `VButton` (Destructive) are stacked vertically with 20dp spacing. No UI overlap. |

### TC-12.2: Danger Zone visible during skeleton loading state

| Field | Value |
|-------|-------|
| **TC ID** | TC-12.2 |
| **Bug #** | 12 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | School admin logged in. Slow network (throttle to 2G if possible). |
| **Steps** | 1. Navigate to People → Teachers tab. 2. Tap a teacher card. 3. Observe the skeleton loading state. 4. Wait for content to replace skeleton. |
| **Expected Result** | During `AnimatedContent` transition from skeleton to content, the Danger Zone composables flow vertically inside a `Column`. No stacking/overlap during or after transition. |

### TC-12.3: Danger Zone after error state → retry

| Field | Value |
|-------|-------|
| **TC ID** | TC-12.3 |
| **Bug #** | 12 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | School admin logged in. Server returns 500 for teacher profile API. |
| **Steps** | 1. Navigate to a teacher profile (triggers error state). 2. Tap Retry. 3. Server now returns 200. 4. Scroll to Danger Zone. |
| **Expected Result** | Error state shows retry button. After retry, content loads and Danger Zone renders without overlap. |

### TC-12.4: Deactivate button functional after layout fix

| Field | Value |
|-------|-------|
| **TC ID** | TC-12.4 |
| **Bug #** | 12 |
| **Priority** | P2 |
| **Type** | UI / Functional |
| **Preconditions** | School admin logged in. Teacher exists. |
| **Steps** | 1. Open teacher profile. 2. Scroll to Danger Zone. 3. Tap "Deactivate" button. 4. Confirm in dialog. |
| **Expected Result** | Confirmation dialog appears. After confirming, teacher is deactivated and profile closes or updates. Button is fully tappable (not covered by overlapping composable). |

---

## Bug 13 (P2): Newly Added Teacher Not Visible

**File:** `shared/.../SchoolTeachersViewModel.kt`
**Fix:** `addTeacher` now calls `refresh()` (silent, no `isLoading` set) instead of `load()` (which sets `isLoading = true` and triggers skeleton).

### TC-13.1: New teacher appears immediately after add

| Field | Value |
|-------|-------|
| **TC ID** | TC-13.1 |
| **Bug #** | 13 |
| **Priority** | P2 |
| **Type** | Data / State Sync |
| **Preconditions** | School admin logged in. People → Teachers tab is open. At least 1 teacher exists. |
| **Steps** | 1. Tap "Add Teacher" button. 2. Fill name and email/phone. 3. Tap "Add" (submit). 4. Wait for dialog to dismiss. 5. Observe the teachers list. |
| **Expected Result** | The newly added teacher appears in the list immediately without manual refresh. No loading skeleton flash — the list updates silently. |

### TC-13.2: No skeleton flash after adding teacher

| Field | Value |
|-------|-------|
| **TC ID** | TC-13.2 |
| **Bug #** | 13 |
| **Priority** | P2 |
| **Type** | UI / Loading |
| **Preconditions** | School admin logged in. Teachers tab open with existing list. |
| **Steps** | 1. Add a new teacher via the Add Teacher sheet. 2. Observe the list area during and after submission. |
| **Expected Result** | `isLoading` is NOT set to `true` during the post-add refresh. The existing list remains visible while the new data loads in the background. No skeleton appears. |

### TC-13.3: Add teacher fails — list unchanged, error shown

| Field | Value |
|-------|-------|
| **TC ID** | TC-13.3 |
| **Bug #** | 13 |
| **Priority** | P2 |
| **Type** | API / Error Handling |
| **Preconditions** | School admin logged in. Simulate server error (e.g., duplicate email). |
| **Steps** | 1. Open Add Teacher sheet. 2. Enter name + an email that already exists. 3. Tap Add. |
| **Expected Result** | Error message is shown in the sheet. Sheet stays open. Existing teacher list is unchanged (no partial update, no skeleton). `isMutating` resets to `false`. |

### TC-13.4: Add teacher on slow network — list still updates

| Field | Value |
|-------|-------|
| **TC ID** | TC-13.4 |
| **Bug #** | 13 |
| **Priority** | P2 |
| **Type** | Data / State Sync |
| **Preconditions** | School admin logged in. Network throttled to 2G/3G. |
| **Steps** | 1. Add a teacher. 2. Wait for the sheet to dismiss (success callback). 3. Wait for the silent refresh to complete. |
| **Expected Result** | New teacher appears in the list once the background `refresh()` completes. No timeout or stale list. |

### TC-13.5: Add multiple teachers sequentially

| Field | Value |
|-------|-------|
| **TC ID** | TC-13.5 |
| **Bug #** | 13 |
| **Priority** | P2 |
| **Type** | Data / State Sync |
| **Preconditions** | School admin logged in. Teachers tab open. |
| **Steps** | 1. Add Teacher A. 2. Immediately add Teacher B. 3. Observe the list after both additions. |
| **Expected Result** | Both Teacher A and Teacher B appear in the list. No race condition causes one to be missing. `refresh()` calls may overlap but the final state reflects both additions. |

### TC-13.6: Remove teacher — list updates (regression check)

| Field | Value |
|-------|-------|
| **TC ID** | TC-13.6 |
| **Bug #** | 13 |
| **Priority** | P2 |
| **Type** | Data / State Sync |
| **Preconditions** | School admin logged in. At least 2 teachers exist. |
| **Steps** | 1. Open a teacher profile. 2. Deactivate the teacher. 3. Return to the Teachers list. |
| **Expected Result** | Deactivated teacher is removed from the list. `load()` is still called for remove (full reload with skeleton is acceptable for removal). |

---

## Bug 14 (P2): Non-Teaching Staff Role Field — dropdown

**File:** `composeApp/.../SchoolPeopleScreenV2.kt`
**Fix:** Replaced role `VInput` (text field) with `VDropdown` using `STAFF_ROLE_OPTIONS` constant.

### TC-14.1: Role field is a dropdown, not text input

| Field | Value |
|-------|-------|
| **TC ID** | TC-14.1 |
| **Bug #** | 14 |
| **Priority** | P2 |
| **Type** | UI / Component Type |
| **Preconditions** | School admin logged in. People → Staff tab is open. |
| **Steps** | 1. Tap "Add Staff" button. 2. Observe the Add Staff sheet. 3. Tap the Role field. |
| **Expected Result** | A dropdown menu appears with predefined role options (Accountant, Librarian, Office Assistant, Receptionist, Lab Assistant, Peon, Security Guard, Driver, Sweeper, Gardener, Electrician, Plumber, Carpenter, Other). The field does NOT accept free-text input. |

### TC-14.2: Select a role from dropdown

| Field | Value |
|-------|-------|
| **TC ID** | TC-14.2 |
| **Bug #** | 14 |
| **Priority** | P2 |
| **Type** | UI / Functional |
| **Preconditions** | Add Staff sheet is open. |
| **Steps** | 1. Tap the Role dropdown. 2. Select "Accountant" from the list. 3. Observe the field value. |
| **Expected Result** | The Role field displays "Accountant". The dropdown closes. The value is set in state. |

### TC-14.3: Submit staff without selecting a role

| Field | Value |
|-------|-------|
| **TC ID** | TC-14.3 |
| **Bug #** | 14 |
| **Priority** | P2 |
| **Type** | Validation |
| **Preconditions** | Add Staff sheet is open. |
| **Steps** | 1. Enter a name. 2. Leave Role dropdown empty (placeholder "Select role" visible). 3. Tap "Add Staff". |
| **Expected Result** | Submit button is disabled (`canSubmit = name.isNotBlank() && role.isNotBlank()`). Button appears greyed out / non-interactive. |

### TC-14.4: Select "Other" role

| Field | Value |
|-------|-------|
| **TC ID** | TC-14.4 |
| **Bug #** | 14 |
| **Priority** | P2 |
| **Type** | UI / Functional |
| **Preconditions** | Add Staff sheet is open. |
| **Steps** | 1. Tap Role dropdown. 2. Select "Other". 3. Tap Add Staff (with name filled). |
| **Expected Result** | "Other" is accepted as a valid role. Staff member is created with role "Other". |

### TC-14.5: All 14 role options are present

| Field | Value |
|-------|-------|
| **TC ID** | TC-14.5 |
| **Bug #** | 14 |
| **Priority** | P2 |
| **Type** | Data / Completeness |
| **Preconditions** | Add Staff sheet is open. |
| **Steps** | 1. Tap Role dropdown. 2. Scroll through all options. |
| **Expected Result** | All 14 options are visible: Accountant, Librarian, Office Assistant, Receptionist, Lab Assistant, Peon, Security Guard, Driver, Sweeper, Gardener, Electrician, Plumber, Carpenter, Other. |

### TC-14.6: Role dropdown matches VDropdown visual style

| Field | Value |
|-------|-------|
| **TC ID** | TC-14.6 |
| **Bug #** | 14 |
| **Priority** | P2 |
| **Type** | UI / Design System |
| **Preconditions** | Add Staff sheet is open. |
| **Steps** | 1. Compare the Role dropdown visual style with other `VDropdown` instances (e.g., City dropdown in EditSchoolProfileScreenV2). |
| **Expected Result** | Both dropdowns have identical styling: same border, focus treatment, label style, placeholder style, and option list appearance. |

---

## Bug 15 (P2): Teacher & Non-Teaching Tab Overlap in SchoolPeopleScreenV2

**File:** `composeApp/.../VNavigation.kt` (`VTopTabs` component)
**Fix:** Added `maxLines = 1` and `TextOverflow.Ellipsis` to tab label `Text`.

### TC-15.1: Three sub-tabs render without overlap

| Field | Value |
|-------|-------|
| **TC ID** | TC-15.1 |
| **Bug #** | 15 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | School admin logged in. |
| **Steps** | 1. Navigate to People tab. 2. Observe the sub-tab bar (Teachers, Students, Staff). |
| **Expected Result** | All three tab labels are visible without overlapping. Each tab is a pill with its label on a single line. Active tab has colored background; inactive tabs are transparent. |

### TC-15.2: Switch between tabs

| Field | Value |
|-------|-------|
| **TC ID** | TC-15.2 |
| **Bug #** | 15 |
| **Priority** | P2 |
| **Type** | UI / Navigation |
| **Preconditions** | People tab is open. |
| **Steps** | 1. Tap "Teachers" tab. 2. Tap "Students" tab. 3. Tap "Staff" tab. 4. Tap "Teachers" tab again. |
| **Expected Result** | Each tap switches the active tab and the `HorizontalPager` animates to the corresponding page. No tab label overlap during or after switching. |

### TC-15.3: Long tab labels truncate with ellipsis

| Field | Value |
|-------|-------|
| **TC ID** | TC-15.3 |
| **Bug #** | 15 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | Test on a narrow screen (320dp width) or with a long tab label. |
| **Steps** | 1. Navigate to a screen with many tabs (e.g., SchoolRecordsScreenV2 with 6 tabs: Coverage, Pace, Attendance, Marks, Fee, Documents). 2. Observe tab labels. |
| **Expected Result** | Tab labels that don't fit are truncated with ellipsis (`...`) on a single line. No wrapping to multiple lines. No overlap between adjacent tabs. |

### TC-15.4: Swipe between tabs (HorizontalPager)

| Field | Value |
|-------|-------|
| **TC ID** | TC-15.4 |
| **Bug #** | 15 |
| **Priority** | P2 |
| **Type** | UI / Navigation |
| **Preconditions** | People tab is open. |
| **Steps** | 1. Swipe left from Teachers to Students. 2. Swipe left from Students to Staff. 3. Swipe right from Staff back to Students. |
| **Expected Result** | Pager animates between pages. The active tab pill indicator updates to match the current page. No overlap during swipe animation. |

### TC-15.5: Tab labels on narrow screen (320dp)

| Field | Value |
|-------|-------|
| **TC ID** | TC-15.5 |
| **Bug #** | 15 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | App running on a device with 320dp screen width. |
| **Steps** | 1. Open People tab. 2. Observe the three sub-tabs. |
| **Expected Result** | Tabs are horizontally scrollable. Each label is on a single line with `maxLines = 1`. If a label is too long, it truncates with ellipsis. No overlap. |

---

## Bug 16 (P1): Dashboard Staff Count Incorrect

**Files:** `server/.../AdminDashboardRouting.kt`, `shared/.../AdminDashboardModels.kt`, `composeApp/.../SchoolHomeScreenV2.kt`
**Fix:** Added `NonTeachingStaffTable` count to dashboard statistics. `HeroCard` and `KeyMetrics` now show teachers + staff combined.

### TC-16.1: Dashboard shows combined teacher + staff count

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.1 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | School has 2 teachers and 1 non-teaching staff member. School admin logged in. |
| **Steps** | 1. Navigate to Home (Dashboard). 2. Observe the "Staff" KPI in the HeroCard. 3. Observe the "Staff & Teachers" KPI in KeyMetrics. |
| **Expected Result** | HeroCard "Staff" count shows 3 (2 teachers + 1 non-teaching staff). KeyMetrics "Staff & Teachers" total shows 3 and active shows 3 (if all are active). |

### TC-16.2: Dashboard with only teachers, no non-teaching staff

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.2 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | School has 3 teachers and 0 non-teaching staff. |
| **Steps** | 1. Navigate to Dashboard. 2. Observe staff KPI. |
| **Expected Result** | Staff count shows 3 (3 teachers + 0 staff). No `null` or `0` displayed incorrectly. |

### TC-16.3: Dashboard with only non-teaching staff, no teachers

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.3 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | School has 0 teachers and 2 non-teaching staff. |
| **Steps** | 1. Navigate to Dashboard. 2. Observe staff KPI. |
| **Expected Result** | Staff count shows 2 (0 teachers + 2 staff). `teacherTotal` is 0, `staffTotal` is 2, combined is 2. |

### TC-16.4: Dashboard with zero teachers and zero staff

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.4 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | Newly created school with no teachers or staff. |
| **Steps** | 1. Navigate to Dashboard. 2. Observe staff KPI. |
| **Expected Result** | Staff count shows 0. No crash, no null pointer, no "NaN" or "null" displayed. |

### TC-16.5: Staff count updates after adding non-teaching staff

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.5 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | Data / State Sync |
| **Preconditions** | Dashboard shows 2 staff. School admin logged in. |
| **Steps** | 1. Navigate to People → Staff. 2. Add a new non-teaching staff member. 3. Navigate back to Dashboard. 4. Pull to refresh. |
| **Expected Result** | Staff KPI now shows 3 (previous 2 + 1 new). The dashboard API re-fetches `NonTeachingStaffTable` count. |

### TC-16.6: Staff count updates after adding a teacher

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.6 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | Data / State Sync |
| **Preconditions** | Dashboard shows 2 staff. |
| **Steps** | 1. Navigate to People → Teachers. 2. Add a new teacher. 3. Navigate back to Dashboard. 4. Pull to refresh. |
| **Expected Result** | Staff KPI now shows 3 (previous 2 + 1 new teacher). |

### TC-16.7: Server API returns staff field in response

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.7 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | API |
| **Preconditions** | API testing tool (Postman/curl). Valid JWT token. |
| **Steps** | 1. `GET /api/v1/school/admin/dashboard/summary` with valid Authorization header. 2. Inspect the JSON response. |
| **Expected Result** | Response `statistics` object contains a `staff` field with `total` and `active` integers. `staff.total` equals the count of `NonTeachingStaffTable` rows for the school. |

### TC-16.8: Inactive staff not counted in active count

| Field | Value |
|-------|-------|
| **TC ID** | TC-16.8 |
| **Bug #** | 16 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | School has 3 teachers (2 active, 1 inactive) and 2 staff (1 active, 1 inactive). |
| **Steps** | 1. Navigate to Dashboard. 2. Observe staff KPI total and active counts. |
| **Expected Result** | Total = 5 (3 + 2). Active = 3 (2 + 1). Inactive staff/teachers are counted in total but not in active. |

---

## Bug 17 (P2): Common Hub Tab Overlapping in SchoolCommsScreenV2

**File:** `composeApp/.../VNavigation.kt` (`VTopTabs` component — same fix as Bug 15)
**Fix:** Added `maxLines = 1` and `TextOverflow.Ellipsis` to tab labels.

### TC-17.1: Comms sub-tabs render without overlap

| Field | Value |
|-------|-------|
| **TC ID** | TC-17.1 |
| **Bug #** | 17 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | School admin logged in. |
| **Steps** | 1. Navigate to Comms tab. 2. Observe the sub-tab bar (Announcements, Messages, PTM, Notifications). |
| **Expected Result** | All four tab labels are visible without overlapping. Each label is on a single line. Active tab has colored background. |

### TC-17.2: Switch between Comms tabs

| Field | Value |
|-------|-------|
| **TC ID** | TC-17.2 |
| **Bug #** | 17 |
| **Priority** | P2 |
| **Type** | UI / Navigation |
| **Preconditions** | Comms tab is open. |
| **Steps** | 1. Tap "Announcements". 2. Tap "Messages". 3. Tap "PTM". 4. Tap "Notifications". 5. Tap "Announcements" again. |
| **Expected Result** | Each tap switches the active tab. Content area updates to show the selected tab's content. No overlap during switching. |

### TC-17.3: Long tab labels on narrow screen

| Field | Value |
|-------|-------|
| **TC ID** | TC-17.3 |
| **Bug #** | 17 |
| **Priority** | P2 |
| **Type** | UI Layout |
| **Preconditions** | App on 320dp width screen. |
| **Steps** | 1. Open Comms tab. 2. Observe "Announcements" and "Notifications" labels. |
| **Expected Result** | Long labels truncate with ellipsis if needed. All tabs remain on a single line. No overlap. Tabs are horizontally scrollable. |

### TC-17.4: Swipe between Comms tabs

| Field | Value |
|-------|-------|
| **TC ID** | TC-17.4 |
| **Bug #** | 17 |
| **Priority** | P2 |
| **Type** | UI / Navigation |
| **Preconditions** | Comms tab is open. |
| **Steps** | 1. Swipe left through all 4 tabs. 2. Swipe right back to the first tab. |
| **Expected Result** | Pager animates smoothly. Active tab indicator follows the current page. No label overlap during swipe. |

---

## Bug 18 (P2): City & Pincode Mismatch Validation

**File:** `shared/.../SchoolProfileViewModel.kt`
**Fix:** Added `CITY_PINCODE_PREFIX` mapping and cross-validation in `save()`.

### TC-18.1: Correct city-pincode combination accepted

| Field | Value |
|-------|-------|
| **TC ID** | TC-18.1 |
| **Bug #** | 18 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | School admin logged in. Edit School Profile screen open. |
| **Steps** | 1. Set City = "New Delhi". 2. Set PIN code = "110001". 3. Fill all other required fields. 4. Tap Save. |
| **Expected Result** | Profile saves successfully. No validation error for pincode. "110" prefix matches New Delhi. |

### TC-18.2: Mismatched city-pincode rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-18.2 |
| **Bug #** | 18 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Set City = "New Delhi". 2. Set PIN code = "221001" (Varanasi prefix). 3. Fill all required fields. 4. Tap Save. |
| **Expected Result** | Validation error: "PIN code doesn't match New Delhi. Expected starting with 110." Profile is NOT saved. `fieldErrors["pincode"]` is set. |

### TC-18.3: City not in prefix map — no mismatch error

| Field | Value |
|-------|-------|
| **TC ID** | TC-18.3 |
| **Bug #** | 18 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. City field allows custom entry or a city not in `CITY_PINCODE_PREFIX`. |
| **Steps** | 1. Set City to a value not in the mapping (e.g., "Surat" if not mapped). 2. Set any valid 6-digit PIN code. 3. Tap Save. |
| **Expected Result** | No mismatch error. Profile saves if all other validations pass. The prefix check only fires for cities in `CITY_PINCODE_PREFIX`. |

### TC-18.4: All city-prefix mappings verified

| Field | Value |
|-------|-------|
| **TC ID** | TC-18.4 |
| **Bug #** | 18 |
| **Priority** | P2 |
| **Type** | Data / Logic |
| **Preconditions** | Unit test environment. |
| **Steps** | For each entry in `CITY_PINCODE_PREFIX`, verify: New Delhi→110, Mumbai→400, Pune→411, Bangalore→560, Chennai→600, Kolkata→700, Hyderabad→500, Ahmedabad→380, Jaipur→302, Lucknow→226, Kanpur→208, Varanasi→221, Meerut→250, Noida→201, Ghaziabad→201, Gurugram→122. |
| **Expected Result** | All 16 city-to-prefix mappings are correct and match Indian postal PIN code regions. |

### TC-18.5: Pincode mismatch error only when pincode is valid 6 digits

| Field | Value |
|-------|-------|
| **TC ID** | TC-18.5 |
| **Bug #** | 18 |
| **Priority** | P2 |
| **Type** | Validation / Edge Case |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Set City = "Mumbai". 2. Set PIN code = "400" (only 3 digits). 3. Tap Save. |
| **Expected Result** | Error is "PIN must be exactly 6 digits" (from the format check), NOT the mismatch error. The mismatch check only runs when pincode passes the 6-digit format check. |

### TC-18.6: Empty city — no mismatch check

| Field | Value |
|-------|-------|
| **TC ID** | TC-18.6 |
| **Bug #** | 18 |
| **Priority** | P2 |
| **Type** | Validation / Edge Case |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Leave City blank. 2. Set PIN code = "110001". 3. Tap Save. |
| **Expected Result** | "City is required" error fires. No pincode mismatch error (the mismatch check requires `city.isNotBlank()`). |

---

## Bug 19 (P2): Invalid PIN Code Accepted

**File:** `shared/.../SchoolProfileViewModel.kt`
**Fix:** Made pincode required (`if (s.pincode.isBlank()) errors["pincode"] = "PIN code is required"`).

### TC-19.1: Empty PIN code rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-19.1 |
| **Bug #** | 19 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Leave PIN code field empty. 2. Fill all other required fields. 3. Tap Save. |
| **Expected Result** | Validation error: "PIN code is required". Profile is NOT saved. |

### TC-19.2: 5-digit PIN code rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-19.2 |
| **Bug #** | 19 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Enter PIN code = "11001" (5 digits). 2. Tap Save. |
| **Expected Result** | Validation error: "PIN must be exactly 6 digits". |

### TC-19.3: 7-digit PIN code rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-19.3 |
| **Bug #** | 19 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Enter PIN code = "1100011" (7 digits). 2. Tap Save. |
| **Expected Result** | Validation error: "PIN must be exactly 6 digits". |

### TC-19.4: PIN code with letters rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-19.4 |
| **Bug #** | 19 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Enter PIN code = "11000A". 2. Tap Save. |
| **Expected Result** | Validation error: "PIN must be exactly 6 digits". Regex `^\d{6}$` rejects non-numeric characters. |

### TC-19.5: Valid 6-digit PIN code accepted

| Field | Value |
|-------|-------|
| **TC ID** | TC-19.5 |
| **Bug #** | 19 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. City = "New Delhi". |
| **Steps** | 1. Enter PIN code = "110001". 2. Fill all required fields. 3. Tap Save. |
| **Expected Result** | Profile saves successfully. No pincode validation error. |

### TC-19.6: PIN code with spaces rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-19.6 |
| **Bug #** | 19 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Enter PIN code = "110 001" (with space). 2. Tap Save. |
| **Expected Result** | Validation error: "PIN must be exactly 6 digits". Space fails the `^\d{6}$` regex. |

### TC-19.7: PIN code "000000" accepted (edge case)

| Field | Value |
|-------|-------|
| **TC ID** | TC-19.7 |
| **Bug #** | 19 |
| **Priority** | P2 |
| **Type** | Validation / Edge Case |
| **Preconditions** | Edit School Profile screen open. City not in prefix map or city = "" (to avoid mismatch error). |
| **Steps** | 1. Enter PIN code = "000000". 2. Tap Save. |
| **Expected Result** | "000000" passes the `^\d{6}$` regex. If no city mismatch, profile saves. (Note: "000000" is not a real PIN code but passes format validation — this is a known limitation of format-only validation.) |

---

## Bug 20 (P3): Inconsistent Dropdown Design

**File:** `composeApp/.../SchoolRegistrationFlow.kt`
**Fix:** Replaced `VSheetPicker` with `VDropdown` for City field in registration flow.

### TC-20.1: Registration City field uses VDropdown

| Field | Value |
|-------|-------|
| **TC ID** | TC-20.1 |
| **Bug #** | 20 |
| **Priority** | P3 |
| **Type** | UI / Design System |
| **Preconditions** | App on registration flow (not logged in). |
| **Steps** | 1. Start school registration. 2. Navigate to the step with City field. 3. Tap the City field. |
| **Expected Result** | A `VDropdown` inline menu opens (not a bottom sheet). The visual style matches other `VDropdown` instances in the app. |

### TC-20.2: Registration City dropdown matches Edit Profile City dropdown

| Field | Value |
|-------|-------|
| **TC ID** | TC-20.2 |
| **Bug #** | 20 |
| **Priority** | P3 |
| **Type** | UI / Design System |
| **Preconditions** | Access to both registration flow and Edit School Profile screen. |
| **Steps** | 1. Open registration flow City dropdown. 2. Open Edit School Profile City dropdown. 3. Compare visual appearance. |
| **Expected Result** | Both dropdowns have identical styling: same border, focus treatment, label, placeholder, option list, and animation. |

### TC-20.3: City selection works in registration flow

| Field | Value |
|-------|-------|
| **TC ID** | TC-20.3 |
| **Bug #** | 20 |
| **Priority** | P3 |
| **Type** | UI / Functional |
| **Preconditions** | Registration flow is open. |
| **Steps** | 1. Tap City dropdown. 2. Select "Mumbai". 3. Observe the field value. |
| **Expected Result** | "Mumbai" is displayed in the City field. Dropdown closes. State is updated with `city = "Mumbai"`. |

### TC-20.4: All 16 city options present in registration dropdown

| Field | Value |
|-------|-------|
| **TC ID** | TC-20.4 |
| **Bug #** | 20 |
| **Priority** | P3 |
| **Type** | Data / Completeness |
| **Preconditions** | Registration flow is open. |
| **Steps** | 1. Tap City dropdown. 2. Count and verify all options. |
| **Expected Result** | All 16 cities are present: New Delhi, Mumbai, Bangalore, Chennai, Kolkata, Hyderabad, Pune, Ahmedabad, Jaipur, Lucknow, Kanpur, Varanasi, Meerut, Noida, Ghaziabad, Gurugram. |

### TC-20.5: Other VSheetPicker fields still work (regression check)

| Field | Value |
|-------|-------|
| **TC ID** | TC-20.5 |
| **Bug #** | 20 |
| **Priority** | P3 |
| **Type** | Regression |
| **Preconditions** | Registration flow is open. |
| **Steps** | 1. Tap "Your Role" field (still uses VSheetPicker). 2. Select a role. 3. Tap "Periods per day" field (still uses VSheetPicker). 4. Select a value. |
| **Expected Result** | Both `VSheetPicker` fields still function correctly. Only the City field was changed to `VDropdown`. No regression in other sheet picker fields. |

---

## Bug 21 (P1): School Location Mismatch — State Auto-populate

**File:** `composeApp/.../EditSchoolProfileScreenV2.kt`
**Fix:** Added `CITY_TO_STATE` mapping. City dropdown's `onSelect` now also calls `onState()`.

### TC-21.1: State auto-populates when city is selected

| Field | Value |
|-------|-------|
| **TC ID** | TC-21.1 |
| **Bug #** | 21 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | School admin logged in. Edit School Profile screen open. |
| **Steps** | 1. Tap City dropdown. 2. Select "Bangalore". 3. Observe the State field. |
| **Expected Result** | State field automatically updates to "Karnataka" (from `CITY_TO_STATE` mapping). No manual state selection needed. |

### TC-21.2: State updates when city changes

| Field | Value |
|-------|-------|
| **TC ID** | TC-21.2 |
| **Bug #** | 21 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | Edit School Profile screen open. City = "Bangalore", State = "Karnataka". |
| **Steps** | 1. Change City to "Mumbai". 2. Observe the State field. |
| **Expected Result** | State automatically changes to "Maharashtra". The state always reflects the currently selected city. |

### TC-21.3: All city-to-state mappings verified

| Field | Value |
|-------|-------|
| **TC ID** | TC-21.3 |
| **Bug #** | 21 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | Unit test or manual verification. |
| **Steps** | For each city in `CITY_TO_STATE`, verify: New Delhi→Delhi, Mumbai→Maharashtra, Pune→Maharashtra, Bangalore→Karnataka, Chennai→Tamil Nadu, Kolkata→West Bengal, Hyderabad→Telangana, Ahmedabad→Gujarat, Jaipur→Rajasthan, Lucknow→Uttar Pradesh, Kanpur→Uttar Pradesh, Varanasi→Uttar Pradesh, Meerut→Uttar Pradesh, Noida→Uttar Pradesh, Ghaziabad→Uttar Pradesh, Gurugram→Haryana. |
| **Expected Result** | All 16 city-to-state mappings are correct per Indian geography. |

### TC-21.4: State can still be manually overridden

| Field | Value |
|-------|-------|
| **TC ID** | TC-21.4 |
| **Bug #** | 21 |
| **Priority** | P1 |
| **Type** | UI / Functional |
| **Preconditions** | Edit School Profile screen open. City = "New Delhi", State = "Delhi". |
| **Steps** | 1. Tap State dropdown. 2. Select a different state (e.g., "Uttar Pradesh"). 3. Observe the State field. |
| **Expected Result** | State field shows the manually selected value. The auto-populate only fires on city selection, not on state dropdown interaction. |

### TC-21.5: City selection when state was previously empty

| Field | Value |
|-------|-------|
| **TC ID** | TC-21.5 |
| **Bug #** | 21 |
| **Priority** | P1 |
| **Type** | Data / Logic |
| **Preconditions** | Edit School Profile screen open. State field is empty. |
| **Steps** | 1. Tap City dropdown. 2. Select "Chennai". |
| **Expected Result** | State field populates with "Tamil Nadu" immediately. |

### TC-21.6: Save profile with auto-populated state

| Field | Value |
|-------|-------|
| **TC ID** | TC-21.6 |
| **Bug #** | 21 |
| **Priority** | P1 |
| **Type** | API / Integration |
| **Preconditions** | Edit School Profile screen open. City = "Kolkata", State = "West Bengal" (auto-populated). |
| **Steps** | 1. Fill all required fields. 2. Tap Save. 3. Reopen Edit School Profile. |
| **Expected Result** | Profile saves with state = "West Bengal". On reload, the state field shows "West Bengal" persisted from server. |

---

## Bug 22 (P2): Duplicate Principal Email

**File:** `shared/.../SchoolProfileViewModel.kt`
**Fix:** Added validation that `principalEmail != contactEmail` (case-insensitive, trimmed).

### TC-22.1: Same email in both fields rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-22.1 |
| **Bug #** | 22 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Set Contact Email = "principal@school.com". 2. Set Principal Email = "principal@school.com". 3. Fill all required fields. 4. Tap Save. |
| **Expected Result** | Validation error: "Principal email cannot be the same as contact email". `fieldErrors["principalEmail"]` is set. Profile is NOT saved. |

### TC-22.2: Different emails accepted

| Field | Value |
|-------|-------|
| **TC ID** | TC-22.2 |
| **Bug #** | 22 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Set Contact Email = "admin@school.com". 2. Set Principal Email = "principal@school.com". 3. Fill all required fields. 4. Tap Save. |
| **Expected Result** | Profile saves successfully. No duplicate email error. |

### TC-22.3: Same email with different case rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-22.3 |
| **Bug #** | 22 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Set Contact Email = "Principal@School.com". 2. Set Principal Email = "principal@school.com". 3. Tap Save. |
| **Expected Result** | Validation error: "Principal email cannot be the same as contact email". Comparison is case-insensitive (`equals(..., ignoreCase = true)`). |

### TC-22.4: Same email with trailing spaces rejected

| Field | Value |
|-------|-------|
| **TC ID** | TC-22.4 |
| **Bug #** | 22 |
| **Priority** | P2 |
| **Type** | Input Validation |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Set Contact Email = "principal@school.com ". 2. Set Principal Email = " principal@school.com". 3. Tap Save. |
| **Expected Result** | Validation error: emails are compared after `trim()`, so "principal@school.com " and " principal@school.com" are treated as equal. Duplicate email error fires. |

### TC-22.5: One email empty — no duplicate check

| Field | Value |
|-------|-------|
| **TC ID** | TC-22.5 |
| **Bug #** | 22 |
| **Priority** | P2 |
| **Type** | Validation / Edge Case |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Set Contact Email = "admin@school.com". 2. Leave Principal Email empty. 3. Tap Save. |
| **Expected Result** | No duplicate email error (the check requires both fields to be non-blank). Profile saves if other validations pass. |

### TC-22.6: Both emails empty — no duplicate check

| Field | Value |
|-------|-------|
| **TC ID** | TC-22.6 |
| **Bug #** | 22 |
| **Priority** | P2 |
| **Type** | Validation / Edge Case |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Leave both Contact Email and Principal Email empty. 2. Fill all required fields. 3. Tap Save. |
| **Expected Result** | No duplicate email error. Both emails are optional. Profile saves. |

---

## Bug 23 (P1): Settings Screen Refresh Loop

**File:** `composeApp/.../SchoolSettingsScreenV2.kt`
**Fix:** Removed separate `isRefreshing` state and `LaunchedEffect(state.isLoading)`. `VPullRefresh` now binds directly to `state.isLoading`.

### TC-23.1: Settings screen loads once on open

| Field | Value |
|-------|-------|
| **TC ID** | TC-23.1 |
| **Bug #** | 23 |
| **Priority** | P1 |
| **Type** | API / Loading |
| **Preconditions** | School admin logged in. |
| **Steps** | 1. Navigate to Settings tab. 2. Observe the loading behavior. |
| **Expected Result** | Settings screen loads data once via `InstitutionalProfileViewModel.load()` (called in `init`). Loading indicator appears briefly, then content displays. No repeated loading cycles. |

### TC-23.2: Pull-to-refresh works exactly once

| Field | Value |
|-------|-------|
| **TC ID** | TC-23.2 |
| **Bug #** | 23 |
| **Priority** | P1 |
| **Type** | API / Loading |
| **Preconditions** | Settings screen is loaded and displaying content. |
| **Steps** | 1. Pull down to refresh. 2. Release. 3. Observe the refresh indicator. |
| **Expected Result** | Refresh indicator appears while `state.isLoading` is `true`. Once the load completes, `isLoading` becomes `false` and the indicator disappears. No second refresh is triggered. |

### TC-23.3: No refresh loop after pull-to-refresh

| Field | Value |
|-------|-------|
| **TC ID** | TC-23.3 |
| **Bug #** | 23 |
| **Priority** | P1 |
| **Type** | API / Loading |
| **Preconditions** | Settings screen is loaded. |
| **Steps** | 1. Pull down to refresh. 2. Wait for refresh to complete. 3. Observe the screen for 10 seconds. |
| **Expected Result** | No additional refresh cycles. The screen remains stable. The previous `LaunchedEffect(state.isLoading)` that caused a loop is removed. |

### TC-23.4: Navigate away and back — loads once

| Field | Value |
|-------|-------|
| **TC ID** | TC-23.4 |
| **Bug #** | 23 |
| **Priority** | P1 |
| **Type** | Navigation / Loading |
| **Preconditions** | Settings screen is loaded. |
| **Steps** | 1. Navigate to a different tab (e.g., Home). 2. Navigate back to Settings. 3. Observe loading behavior. |
| **Expected Result** | Settings screen loads once when revisited. No infinite refresh loop. The ViewModel may or may not reload depending on lifecycle (Koin `factory` scope), but no loop occurs. |

### TC-23.5: Refresh on network error

| Field | Value |
|-------|-------|
| **TC ID** | TC-23.5 |
| **Bug #** | 23 |
| **Priority** | P1 |
| **Type** | API / Error Handling |
| **Preconditions** | Settings screen is loaded. Network is disconnected. |
| **Steps** | 1. Pull to refresh. 2. Observe error state. 3. Reconnect network. 4. Pull to refresh again. |
| **Expected Result** | First refresh shows error. Second refresh loads successfully. No loop in either case. `isLoading` correctly transitions: `true` → `false` (error) → `true` → `false` (success). |

### TC-23.6: Rapid pull-to-refresh

| Field | Value |
|-------|-------|
| **TC ID** | TC-23.6 |
| **Bug #** | 23 |
| **Priority** | P1 |
| **Type** | API / Loading |
| **Preconditions** | Settings screen is loaded. |
| **Steps** | 1. Pull to refresh. 2. Before it completes, pull to refresh again. 3. Wait. |
| **Expected Result** | No crash or loop. The `VPullRefresh` component handles overlapping refresh calls. `state.isLoading` prevents duplicate loads (ViewModel's `load()` sets `isLoading = true` at start). |

### TC-23.7: VPullRefresh indicator matches isLoading state

| Field | Value |
|-------|-------|
| **TC ID** | TC-23.7 |
| **Bug #** | 23 |
| **Priority** | P1 |
| **Type** | UI / Loading |
| **Preconditions** | Settings screen is loaded. |
| **Steps** | 1. Pull to refresh. 2. Observe the refresh indicator visibility. 3. Wait for load to complete. 4. Observe indicator disappearance. |
| **Expected Result** | Indicator is visible exactly when `state.isLoading == true` and hidden when `state.isLoading == false`. No desync between indicator and actual loading state (since `isRefreshing` was removed and `isLoading` is used directly). |

---

## Cross-Bug Regression Tests

### TC-REG.1: Full school profile save with all validations

| Field | Value |
|-------|-------|
| **TC ID** | TC-REG.1 |
| **Bug #** | 18, 19, 21, 22 |
| **Priority** | P1 |
| **Type** | Integration |
| **Preconditions** | School admin logged in. Edit School Profile screen open. |
| **Steps** | 1. Set School Name = "Test School". 2. Set City = "Mumbai" (state auto-fills to "Maharashtra"). 3. Set District = "Mumbai City". 4. Set PIN code = "400001" (matches Mumbai prefix "400"). 5. Set Contact Email = "contact@testschool.edu". 6. Set Principal Email = "principal@testschool.edu" (different from contact). 7. Tap Save. |
| **Expected Result** | Profile saves successfully. All validations pass: city-state match, pincode format + city prefix match, no duplicate emails. |

### TC-REG.2: Full school profile save with all validation errors

| Field | Value |
|-------|-------|
| **TC ID** | TC-REG.2 |
| **Bug #** | 18, 19, 22 |
| **Priority** | P1 |
| **Type** | Integration |
| **Preconditions** | Edit School Profile screen open. |
| **Steps** | 1. Leave School Name blank. 2. Set City = "New Delhi". 3. Set PIN code = "221001" (Varanasi prefix, mismatches Delhi). 4. Set Contact Email = "same@email.com". 5. Set Principal Email = "same@email.com". 6. Tap Save. |
| **Expected Result** | Multiple validation errors: "School name is required", "PIN code doesn't match New Delhi. Expected starting with 110.", "Principal email cannot be the same as contact email". Profile is NOT saved. All errors displayed simultaneously in `fieldErrors`. |

### TC-REG.3: VTopTabs across all screens (Bugs 15, 17 regression)

| Field | Value |
|-------|-------|
| **TC ID** | TC-REG.3 |
| **Bug #** | 15, 17 |
| **Priority** | P2 |
| **Type** | UI Layout / Regression |
| **Preconditions** | School admin logged in. |
| **Steps** | 1. Open People tab → verify 3 sub-tabs. 2. Open Comms tab → verify 4 sub-tabs. 3. Open Records tab → verify 6 sub-tabs. 4. Open Alumni screen → verify 6 sub-tabs. |
| **Expected Result** | All screens with `VTopTabs` render tab labels on a single line with no overlap. Long labels truncate with ellipsis. The `maxLines = 1` + `TextOverflow.Ellipsis` fix applies globally. |

### TC-REG.4: Add teacher then check dashboard (Bugs 13, 16 regression)

| Field | Value |
|-------|-------|
| **TC ID** | TC-REG.4 |
| **Bug #** | 13, 16 |
| **Priority** | P1 |
| **Type** | Integration |
| **Preconditions** | School admin logged in. Dashboard shows 2 staff. |
| **Steps** | 1. Go to People → Teachers. 2. Add a new teacher. 3. Verify teacher appears in list immediately (no skeleton). 4. Navigate to Dashboard. 5. Pull to refresh. |
| **Expected Result** | New teacher is visible in list (Bug 13 fix). Dashboard staff count increases by 1 (Bug 16 fix). |

### TC-REG.5: Add staff then check dashboard (Bugs 14, 16 regression)

| Field | Value |
|-------|-------|
| **TC ID** | TC-REG.5 |
| **Bug #** | 14, 16 |
| **Priority** | P1 |
| **Type** | Integration |
| **Preconditions** | School admin logged in. Dashboard shows 2 staff. |
| **Steps** | 1. Go to People → Staff. 2. Tap "Add Staff". 3. Select role "Librarian" from dropdown. 4. Fill name and submit. 5. Navigate to Dashboard. 6. Pull to refresh. |
| **Expected Result** | Role dropdown works (Bug 14 fix). New staff appears in staff list. Dashboard staff count increases by 1 (Bug 16 fix). |

---

## Test Execution Summary

| Bug # | # of Test Cases | Priority | Key Test Focus |
|-------|-----------------|----------|----------------|
| 12 | 4 | P2 | UI layout, AnimatedContent transition, functional button |
| 13 | 6 | P2 | Data sync, no skeleton flash, error handling, sequential adds |
| 14 | 6 | P2 | Dropdown component, all options, validation, visual consistency |
| 15 | 5 | P2 | Tab overlap, switching, narrow screen, swipe, ellipsis |
| 16 | 8 | P1 | Combined count, edge cases, API response, active vs total |
| 17 | 4 | P2 | Tab overlap, switching, narrow screen, swipe |
| 18 | 6 | P2 | City-pincode match, mismatch, unmapped city, edge cases |
| 19 | 7 | P2 | Required field, format validation, edge cases |
| 20 | 5 | P3 | Component type, visual match, functional, regression |
| 21 | 6 | P1 | Auto-populate, mapping accuracy, manual override, persistence |
| 22 | 6 | P2 | Duplicate detection, case-insensitive, trim, optional fields |
| 23 | 7 | P1 | No loop, single refresh, error recovery, rapid refresh |
| **Total** | **70** | | |
| **Cross-bug** | **5** | | Integration regression |
| **Grand Total** | **75** | | |
