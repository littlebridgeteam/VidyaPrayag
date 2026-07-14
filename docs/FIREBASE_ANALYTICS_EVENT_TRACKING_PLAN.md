# Firebase Analytics — Comprehensive Event Tracking Plan

> Track every meaningful user-level event across all three portals for funnel analysis, retention, crash correlation, and product insights.

## Naming Convention

- **Prefix:** `vp_` | **Format:** `snake_case`
- **Standard params:** `role`, `school_id`, `user_id`, `app_version`, `session_id`, `platform`

---

## 1. App Lifecycle

| Event | Trigger | Key Params |
|-------|---------|------------|
| `vp_app_open` | App foregrounded | `session_id` |
| `vp_app_background` | App backgrounded | `session_id`, `dwell_ms` |
| `vp_app_first_open` | First launch | `install_referrer` |
| `vp_app_update` | New version | `previous_version`, `new_version` |
| `vp_session_start` | Auth session begins | `role`, `school_id` |
| `vp_session_end` | Logout/token expiry | `role`, `session_duration_ms` |

## 2. Auth & Onboarding

| Event | Trigger | Key Params |
|-------|---------|------------|
| `vp_auth_landing_view` | Landing shown | — |
| `vp_auth_login_start` | Login tapped | — |
| `vp_auth_otp_requested` | OTP sent | `phone_masked` |
| `vp_auth_otp_verified` | OTP success | `phone_masked`, `role` |
| `vp_auth_otp_failed` | OTP failed | `phone_masked`, `error_reason` |
| `vp_auth_login_success` | JWT received | `role`, `school_id` |
| `vp_auth_login_failed` | Login failed | `error_reason` |
| `vp_auth_logout` | Explicit logout | `role`, `session_duration_ms` |
| `vp_auth_change_password` | Password changed | `role` |
| `vp_onboarding_start` | Onboarding shown | `resume_step` |
| `vp_onboarding_step_complete` | Step done | `step` |
| `vp_onboarding_complete` | All steps done | `school_id` |
| `vp_teacher_firstlogin_complete` | Teacher setup done | `school_id` |
| `vp_parent_linkchild_submit` | Link request sent | `school_id`, `class`, `section` |
| `vp_parent_linkchild_success` | Link accepted | `school_id`, `status` |

## 3. Parent Portal

| Event | Trigger | Key Params |
|-------|---------|------------|
| `vp_parent_tab_switch` | Tab selected | `tab` |
| `vp_parent_overlay_open` | Overlay opened | `overlay` |
| `vp_parent_home_view` | Home rendered | `child_name` |
| `vp_parent_home_cta_tap` | CTA tapped | `cta_target` |
| `vp_parent_attendance_view` | Attendance viewed | `attendance_pct` |
| `vp_parent_marks_view` | Marks viewed | `subject` |
| `vp_parent_reportcard_view` | Report card opened | `term` |
| `vp_parent_syllabus_view` | Syllabus viewed | `subject`, `progress_pct` |
| `vp_parent_homework_view` | Homework opened | — |
| `vp_parent_fees_view` | Fees rendered | `total_pending` |
| `vp_parent_fee_detail_open` | Invoice opened | `fee_id`, `amount` |
| `vp_parent_fee_pay_initiate` | Pay tapped | `fee_id`, `amount` |
| `vp_parent_fee_pay_success` | Payment done | `fee_id`, `amount`, `payment_method` |
| `vp_parent_fee_pay_failed` | Payment failed | `fee_id`, `error_reason` |
| `vp_parent_message_thread_open` | Thread opened | `thread_id`, `recipient_role` |
| `vp_parent_message_send` | Message sent | `thread_id` |
| `vp_parent_announcement_open` | Announcement viewed | `announcement_id` |
| `vp_parent_profile_edit` | Profile edited | `fields_changed` |
| `vp_parent_theme_change` | Theme changed | `theme` |
| `vp_parent_transport_bus_track` | Bus tracking started | `route_id`, `vehicle_id` |
| `vp_parent_leave_apply_submit` | Leave submitted | `leave_type`, `start_date` |
| `vp_parent_event_register` | Event registered | `event_id`, `child_id` |
| `vp_parent_tutor_chat_open` | AI tutor opened | `subject` |
| `vp_parent_tutor_message_send` | AI message sent | `subject` |
| `vp_parent_scholarship_apply` | Scholarship applied | `scheme_id`, `child_id` |
| `vp_parent_health_view` | Health opened | `child_id` |
| `vp_parent_pews_view` | PEWS opened | `child_id` |
| `vp_parent_digital_id_view` | ID card viewed | `child_id` |

## 4. Teacher Portal

| Event | Trigger | Key Params |
|-------|---------|------------|
| `vp_teacher_tab_switch` | Tab selected | `tab` |
| `vp_teacher_overlay_open` | Overlay opened | `overlay` |
| `vp_teacher_home_view` | Home rendered | — |
| `vp_teacher_home_fingerprint_checkin` | Biometric prompt | — |
| `vp_teacher_attendance_mark` | Attendance saved | `class`, `section`, `subject`, `present`, `absent` |
| `vp_teacher_marks_enter` | Marks saved | `class`, `section`, `subject`, `student_count` |
| `vp_teacher_syllabus_update` | Syllabus updated | `class`, `subject`, `progress_pct` |
| `vp_teacher_homework_assign` | Homework assigned | `class`, `section`, `due_date` |
| `vp_teacher_class_detail_open` | Class detail | `class_id`, `section` |
| `vp_teacher_student_profile_open` | Student drilled | `student_id` |
| `vp_teacher_report_review_open` | Report review | `class`, `term` |
| `vp_teacher_report_review_approve` | Report approved | `draft_id` |
| `vp_teacher_heatmap_view` | Heatmap opened | `class`, `subject` |
| `vp_teacher_leave_request_approve` | Leave approved | `leave_id` |
| `vp_teacher_leave_request_reject` | Leave rejected | `leave_id` |
| `vp_teacher_announcement_create` | Announcement created | `target_audience` |
| `vp_teacher_message_send` | Message sent | `thread_id` |
| `vp_teacher_exam_timetable_create` | Exam timetable | `class`, `term` |
| `vp_teacher_transport_attendance_mark` | Transport attendance | `route_id` |
| `vp_teacher_export_complete` | Export done | `export_type` |
| `vp_teacher_leave_apply_submit` | Leave applied | `leave_type` |

## 5. School Admin Portal

| Event | Trigger | Key Params |
|-------|---------|------------|
| `vp_admin_tab_switch` | Tab selected | `tab` |
| `vp_admin_overlay_open` | Overlay opened | `overlay` |
| `vp_admin_student_add` | Student added | `class`, `section` |
| `vp_admin_student_profile_open` | Student opened | `student_id` |
| `vp_admin_teacher_add` | Teacher added | `class`, `subject` |
| `vp_admin_teacher_assign` | Teacher assigned | `teacher_id`, `class`, `subject` |
| `vp_admin_link_request_approve` | Link approved | `link_id` |
| `vp_admin_link_request_reject` | Link rejected | `link_id` |
| `vp_admin_student_graduate` | Students graduated | `count`, `year` |
| `vp_admin_fee_invoice_create` | Invoice created | `class`, `amount` |
| `vp_admin_report_publish` | Report published | `class`, `term`, `student_count` |
| `vp_admin_class_create` | Class created | `class_code` |
| `vp_admin_timetable_save` | Timetable saved | `class`, `section` |
| `vp_admin_announcement_create` | Announcement created | `target_audience` |
| `vp_admin_announcement_delete` | Announcement deleted | `announcement_id` |
| `vp_admin_scheduled_message_create` | Scheduled msg | `recipient_count` |
| `vp_admin_branding_save` | Branding saved | `fields_changed` |
| `vp_admin_id_cards_generate` | IDs generated | `count` |
| `vp_admin_transport_route_create` | Route created | `route_name` |
| `vp_admin_transport_route_delete` | Route deleted | `route_id` |
| `vp_admin_transport_vehicle_create` | Vehicle added | `bus_number` |
| `vp_admin_transport_assignment_create` | Assignment created | `route_id`, `student_id` |
| `vp_admin_library_book_add` | Book added | `title` |
| `vp_admin_library_book_issue` | Book issued | `book_id`, `student_id` |
| `vp_admin_scholarship_scheme_create` | Scheme created | `scheme_name`, `amount` |
| `vp_admin_scholarship_application_approve` | Application approved | `scheme_id`, `student_id` |
| `vp_admin_event_create` | Event created | `event_title` |
| `vp_admin_event_cancel` | Event cancelled | `event_id` |
| `vp_admin_admission_enquiry_create` | Enquiry created | `class` |
| `vp_admin_admission_status_change` | Status changed | `new_status` |
| `vp_admin_health_record_add` | Health record | `student_id`, `record_type` |
| `vp_admin_ptm_schedule` | PTM scheduled | `date`, `slot_count` |
| `vp_admin_pews_student_detail` | PEWS student | `student_code` |
| `vp_admin_calendar_event_create` | Calendar event | `event_type`, `date` |
| `vp_admin_leave_request_approve` | Leave approved | `leave_id` |
| `vp_admin_gamification_save` | Gamification saved | `config_type` |

## 6. Cross-Portal Events

| Event | Trigger | Key Params |
|-------|---------|------------|
| `vp_notification_received` | FCM push (foreground) | `type` |
| `vp_notification_tapped` | Notification tapped | `type`, `deep_link` |
| `vp_notifications_view` | List opened | `role`, `unread_count` |
| `vp_notification_mark_all_read` | All marked read | `role` |
| `vp_deeplink_received` | Deep link received | `path`, `role` |
| `vp_deeplink_resolved` | Deep link navigated | `target_screen`, `role` |
| `vp_deeplink_failed` | Deep link unresolved | `path`, `reason` |
| `vp_error_displayed` | Error state shown to user | `error_type`, `screen`, `recoverable` |
| `vp_empty_state_view` | Empty state shown | `screen`, `empty_reason` |
| `vp_loading_timeout` | Loading exceeded 10s | `screen` |
| `vp_pull_refresh` | Pull-to-refresh | `screen` |
| `vp_offline_detected` | Network lost | — |
| `vp_online_restored` | Network restored | `offline_duration_ms` |

## 7. Crashlytics Custom Keys

Set these once per session for crash triage:

| Key | Value | Set When |
|-----|-------|----------|
| `app_version` | Build version | App start |
| `role` | Current user role | Login |
| `school_id` | School ID | Login |
| `session_id` | UUID per launch | App start |
| `backend_url` | Resolved base URL | App start |
| `build_flavor` | `dev`/`staging`/`prod` | App start |
| `current_screen` | Last visible screen | Tab switch / overlay open |
| `theme_mode` | Active theme | Theme change |

## 8. Funnels to Measure

1. **Signup → First Session:** `vp_auth_otp_requested` → `vp_auth_otp_verified` → `vp_session_start`
2. **Parent Link Child:** `vp_parent_linkchild_start` → `vp_parent_linkchild_school_selected` → `vp_parent_linkchild_submit` → `vp_parent_linkchild_success`
3. **Fee Payment:** `vp_parent_fee_detail_open` → `vp_parent_fee_pay_initiate` → `vp_parent_fee_pay_success`
4. **Teacher Attendance:** `vp_teacher_update_view` → `vp_teacher_attendance_mark`
5. **Admin Onboarding:** `vp_onboarding_start` → `vp_onboarding_step_complete` (×N) → `vp_onboarding_complete`
6. **Event Registration:** `vp_parent_events_view` → `vp_parent_event_detail_open` → `vp_parent_event_register` → `vp_parent_event_register_success`
7. **Announcement Reach:** `vp_notification_received` → `vp_notification_tapped` → `vp_parent_announcement_open` → `vp_parent_announcement_ack`
8. **Scholarship Apply:** `vp_parent_scholarships_view` → `vp_parent_scholarship_detail_open` → `vp_parent_scholarship_apply` → `vp_parent_scholarship_apply_success`
