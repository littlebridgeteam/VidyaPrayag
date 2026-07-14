# Vidya Prayag — Analytics Event Inventory

**Last updated:** 2026-07-14  
**Tracking SDKs:** Firebase Analytics + Microsoft Clarity (Android), no-op stubs (iOS/JVM)  
**Entry point:** `AnalyticsTracker` (expect/actual) in `shared/src/commonMain/.../util/AnalyticsTracker.kt`

---

## 1. Session & User Tagging

| Method | Where | Trigger |
|--------|-------|---------|
| `setCustomTag("role", role)` | `MainViewModel` | Auth state changes |
| `setCustomTag("auth_status", ...)` | `MainViewModel` | Auth state changes |
| `setCustomTag("user_id", userId)` | `MainViewModel` | User ID flow |
| `setCustomTag("user_name", name)` | `MainViewModel` | User name flow |
| `setCustomUserId(userId)` | `MainViewModel` | User ID flow |
| `setUserId(null)` | `MainViewModel.logout()` | Logout |
| `setCustomTag("role","guest")` | `MainViewModel.logout()` | Logout |

---

## 2. Auth Events

| Event Name | Properties | File | Trigger |
|------------|-----------|------|---------|
| `vp_auth_login_started` | `auth_method`, `role` | `AuthViewModel` | OTP login / password login start |
| `vp_auth_otp_requested` | `phone_masked`, `purpose` | `AuthViewModel` | OTP sent successfully |
| `vp_auth_otp_request_failed` | `purpose`, `error_reason` | `AuthViewModel` | OTP send failed |
| `vp_auth_otp_verified` | `phone_masked` | `AuthViewModel` | OTP verified successfully |
| `vp_auth_otp_failed` | `phone_masked`, `error_reason` | `AuthViewModel` | OTP verification failed |
| `vp_auth_login_success` | `role`, `auth_method` | `AuthViewModel` | Login completed |
| `vp_auth_login_failed` | `error_reason`, `auth_method` | `AuthViewModel` | Login failed |
| `vp_auth_signup_started` | `auth_method`, `role` | `AuthViewModel` | Signup start |
| `vp_auth_signup_success` | `role` | `AuthViewModel` | Signup completed |
| `vp_auth_signup_failed` | `error_reason` | `AuthViewModel` | Signup failed |
| `vp_auth_logout` | `role` | `MainViewModel` | User logout |
| `vp_auth_change_password` | `role` | `TeacherFirstLoginScreenV2` | Teacher first-login password change |
| `vp_teacher_firstlogin_complete` | — | `TeacherFirstLoginScreenV2` | Teacher first-login flow done |

---

## 3. Screen View Events

| Event Name | Properties | File | Trigger |
|------------|-----------|------|---------|
| `vp_screen_viewed` | `screen`, `role` | `NavGraphV2` | Every navigation destination |
| `vp_screen_viewed` | `screen` | `AuthNavGraph` | Auth flow screen views |
| `vp_screen_viewed` | `screen`, `portal` | `SchoolPortalV2` | Admin tab + overlay views |
| `vp_screen_viewed` | `screen`, `portal` | `TeacherPortalV2` | Teacher tab + overlay views |
| `vp_screen_viewed` | `screen`, `portal` | `ParentPortalV2` | Parent tab + overlay views |
| `vp_screen_viewed` | `screen` | `DiscoveryScreenV2` | Discovery flow views |
| `vp_admin_tab_switch` | `tab` | `SchoolPortalV2` | Admin bottom nav tap |
| `vp_teacher_tab_switch` | `tab` | `TeacherPortalV2` | Teacher bottom nav tap |
| `vp_parent_tab_switch` | `tab` | `ParentPortalV2` | Parent bottom nav tap |

---

## 4. Deep Link Events

| Event Name | Properties | File | Trigger |
|------------|-----------|------|---------|
| `vp_deeplink_received` | `path`, `role` | `NavGraphV2` | App opened via deep link |
| `vp_deeplink_resolved` | `path`, `role` | `NavGraphV2` | Deep link parsed to navigation |

---

## 5. Admin Business Events

| Event Name | Properties | File | Trigger |
|------------|-----------|------|---------|
| `vp_staff_search` | `query_length` | `StaffViewModel` | Staff search query |
| `vp_staff_created` | `role` | `StaffViewModel` | Staff member created |
| `vp_staff_create_failed` | `error_reason` | `StaffViewModel` | Staff create failed |
| `vp_staff_updated` | `staff_id` | `StaffViewModel` | Staff member updated |
| `vp_staff_update_failed` | `error_reason` | `StaffViewModel` | Staff update failed |
| `vp_staff_deleted` | `staff_id` | `StaffViewModel` | Staff member deleted |
| `vp_staff_delete_failed` | `error_reason` | `StaffViewModel` | Staff delete failed |
| `vp_student_created` | `class` | `StudentRosterViewModel` | Student created |
| `vp_student_create_failed` | `error_reason` | `StudentRosterViewModel` | Student create failed |
| `vp_student_bulk_import` | `inserted`, `failed`, `total` | `StudentRosterViewModel` | Bulk import result |
| `vp_student_bulk_import_failed` | `error_reason` | `StudentRosterViewModel` | Bulk import failed |
| `vp_student_deleted` | `student_id` | `StudentRosterViewModel` | Student deleted |
| `vp_student_delete_failed` | `error_reason` | `StudentRosterViewModel` | Student delete failed |
| `vp_link_request_approved` | `link_id` | `LinkRequestsViewModel` | Link request approved |
| `vp_link_request_rejected` | `link_id` | `LinkRequestsViewModel` | Link request rejected |
| `vp_link_request_approve_failed` | `error_reason` | `LinkRequestsViewModel` | Approve failed |
| `vp_link_request_reject_failed` | `error_reason` | `LinkRequestsViewModel` | Reject failed |
| `vp_message_sent` | `thread_id`, `is_new_thread` | `MessagesViewModel` | Admin message sent (new + reply) |
| `vp_message_send_failed` | `error_reason` | `MessagesViewModel` | Admin message send failed |
| `vp_announcement_created` | `type`, `audience` | `SchoolAnnouncementsViewModel` | Announcement created |
| `vp_announcement_create_failed` | `error_reason` | `SchoolAnnouncementsViewModel` | Announcement create failed |
| `vp_admin_theme_change` | `theme` | `SchoolSettingsScreenV2` | Admin theme switched |

---

## 6. Teacher Business Events

| Event Name | Properties | File | Trigger |
|------------|-----------|------|---------|
| `vp_attendance_saved` | `assignment_id`, `student_count`, `present`, `absent`, `late`, `leave` | `TeacherAttendanceViewModel` | Attendance saved |
| `vp_attendance_save_failed` | `error_reason` | `TeacherAttendanceViewModel` | Attendance save failed |
| `vp_homework_assigned` | `assignment_id` | `TeacherHomeworkViewModel` | Homework assigned |
| `vp_homework_assign_failed` | `error_reason` | `TeacherHomeworkViewModel` | Homework assign failed |
| `vp_homework_reviewed` | `student_id`, `status` | `TeacherHomeworkViewModel` | Submission reviewed |
| `vp_homework_review_failed` | `error_reason` | `TeacherHomeworkViewModel` | Review failed |
| `vp_marks_saved` | `assessment_id`, `entered_count`, `roster_count` | `TeacherGradebookViewModel` | Marks saved |
| `vp_marks_save_failed` | `error_reason` | `TeacherGradebookViewModel` | Marks save failed |
| `vp_marks_published` | `assessment_id`, `parents_notified` | `TeacherGradebookViewModel` | Marks published |
| `vp_marks_publish_failed` | `error_reason` | `TeacherGradebookViewModel` | Publish failed |
| `vp_teacher_theme_change` | `theme` | `TeacherProfileScreenV2` | Teacher theme switched |

---

## 7. Parent Business Events

| Event Name | Properties | File | Trigger |
|------------|-----------|------|---------|
| `vp_school_search` | `query_length`, `results` | `LinkChildViewModel` | School search in link wizard |
| `vp_link_child_submitted` | `school_id`, `status` | `LinkChildViewModel` | Link child request submitted |
| `vp_link_child_failed` | `error_reason` | `LinkChildViewModel` | Link child failed |
| `vp_leave_applied` | `child_id` | `ParentLeaveViewModel` | Leave application submitted |
| `vp_leave_apply_failed` | `error_reason` | `ParentLeaveViewModel` | Leave application failed |
| `vp_parent_message_sent` | `thread_id`, `is_new_thread` | `ParentMessageViewModel` | Parent message sent (reply + compose) |
| `vp_parent_message_send_failed` | `error_reason` | `ParentMessageViewModel` | Parent message send failed |
| `vp_parent_theme_change` | `theme` | `ParentProfileScreenV2` | Parent theme switched |

---

## 8. Cross-Cutting UI Events

| Event Name | Properties | File | Trigger |
|------------|-----------|------|---------|
| `vp_error_shown` | `error_message` (truncated 200) | `Shared.kt` → `VErrorState` | Any error state shown via VStateHost |
| `vp_retry_tapped` | — | `Shared.kt` → `VErrorState` | Retry button tapped |
| `vp_empty_state_shown` | `empty_title` | `Shared.kt` → `VStateHost` | Empty state shown |
| `vp_notifications_view` | `unread_count` | `NotificationsScreenV2` | Notifications screen opened |
| `vp_notification_mark_all_read` | — | `NotificationsScreenV2` | Mark all notifications read |
| `vp_notification_mark_read` | `notification_id` | `NotificationsScreenV2` | Single notification marked read |

---

## 9. Coverage Summary

| Layer | Status | Events |
|-------|--------|--------|
| Session tagging | ✅ Complete | 7 setCustomTag/setUserId calls |
| Auth flow | ✅ Complete | 13 event types |
| Screen views | ✅ Complete | All 3 portals + auth + discovery |
| Deep links | ✅ Complete | 2 event types |
| Admin business | ✅ Complete | 22 event types (staff, students, links, messages, announcements, theme) |
| Teacher business | ✅ Complete | 10 event types (attendance, homework, marks, theme) |
| Parent business | ✅ Complete | 8 event types (link child, leave, messages, theme) |
| Cross-cutting UI | ✅ Complete | 6 event types (error, retry, empty, notifications) |
| **Total** | **✅ All phases** | **~68 distinct event names** |

---

## 10. Naming Convention

- **Prefix:** `vp_` (Vidya Prayag)
- **Pattern:** `vp_<feature>_<action>` for success, `vp_<feature>_<action>_failed` for failure
- **Properties:** snake_case keys, string/number/boolean values
- **Error properties:** always include `error_reason` on failure events
- **Screen names:** `<portal>_<tab>` (e.g., `admin_home`, `teacher_marks`)

---

## 11. Architecture Notes

- `AnalyticsTracker` is an `expect/actual` object — Android actual wraps Firebase Analytics + Firebase Crashlytics + Microsoft Clarity; iOS/JVM are no-op stubs.
- All calls are wrapped in `runCatching` in the actual implementations — analytics failures never crash the app.
- `setCurrentScreenName` is called separately from `vp_screen_viewed` events to sync Clarity's session recording UI with Firebase screen tracking.
- Business events are emitted from ViewModels (not composables) to ensure they fire exactly once per operation regardless of recomposition.
- Cross-cutting UI events (`vp_error_shown`, `vp_retry_tapped`, `vp_empty_state_shown`) are emitted from `LaunchedEffect` blocks to avoid duplicate emissions on recomposition.
