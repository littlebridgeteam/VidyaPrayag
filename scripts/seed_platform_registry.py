#!/usr/bin/env python3
"""
Seed the Platform Feature & Screen Registry from feature_audit.csv + codebase scan.
Pass 1: Insert all features from CSV (163 rows) + all screens from codebase scan (131 files).
"""

import csv
import json
import re
import os
import sys
import urllib.request
import urllib.error

API_BASE = os.environ.get("API_BASE", "http://localhost:8080")
CSV_PATH = os.path.join(os.path.dirname(__file__), "..", "feature_audit.csv")
SCREENS_DIR = os.path.join(os.path.dirname(__file__), "..", "composeApp", "src", "commonMain", "kotlin", "com", "littlebridge", "enrollplus", "ui", "v2", "screens")

# ─── Helpers ──────────────────────────────────────────────────────────────

def api_call(path, method="GET", body=None, token=None):
    url = f"{API_BASE}{path}"
    data = json.dumps(body).encode() if body else None
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", f"Bearer {token}")
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        err_body = e.read().decode()
        print(f"  ERROR {e.code} on {method} {path}: {err_body[:200]}")
        return None
    except Exception as e:
        print(f"  ERROR on {method} {path}: {e}")
        return None

def slugify(text):
    return re.sub(r'[^a-z0-9]+', '_', text.lower()).strip('_')[:80]

def map_status(csv_status):
    s = csv_status.strip()
    if "Complete" in s or "✅" in s:
        return "completed"
    elif "Partially" in s or "🟡" in s:
        return "in_progress"
    elif "Stub" in s or "🟠" in s:
        return "planned"
    elif "TODO" in s or "Missing" in s or "🔴" in s:
        return "planned"
    elif "Dead" in s or "⚫" in s:
        return "deprecated"
    elif "Needs Review" in s:
        return "in_progress"
    return "in_progress"

def parse_pct(s):
    s = s.strip().replace("%", "")
    try:
        return int(s)
    except:
        return 0

# ─── Authenticate ─────────────────────────────────────────────────────────

def authenticate():
    print("Authenticating as super@vidyaprayag.demo...")
    resp = api_call("/api/v1/auth/login", "POST", {
        "identifier": "super@vidyaprayag.demo",
        "password": "Demo@1234"
    })
    if not resp or not resp.get("success"):
        print(f"Login failed: {resp}")
        sys.exit(1)
    token = resp["data"]["token"]
    print(f"  Token acquired for {resp['data']['name']} ({resp['data']['role']})")
    return token

# ─── Fetch existing features ──────────────────────────────────────────────

def fetch_existing_features(token):
    existing = {}
    page = 1
    while True:
        resp = api_call(f"/api/admin/platform/features?page={page}&page_size=100", token=token)
        if not resp or not resp.get("success"):
            break
        items = resp["data"]["items"]
        for item in items:
            existing[item["feature_id"]] = item
        if page >= resp["data"]["total_pages"]:
            break
        page += 1
    print(f"  Existing features in DB: {len(existing)}")
    return existing

def fetch_existing_screens(token):
    existing = {}
    page = 1
    while True:
        resp = api_call(f"/api/admin/platform/screens?page={page}&page_size=100", token=token)
        if not resp or not resp.get("success"):
            break
        items = resp["data"]["items"]
        for item in items:
            existing[item["screen_id"]] = item
        if page >= resp["data"]["total_pages"]:
            break
        page += 1
    print(f"  Existing screens in DB: {len(existing)}")
    return existing

# ─── Feature creation from CSV ────────────────────────────────────────────

def create_features_from_csv(token, existing_features):
    created = 0
    updated = 0
    skipped = 0

    with open(CSV_PATH, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row_num, row in enumerate(reader, 2):
            module = row.get("Module", "").strip()
            category = row.get("Feature Category", "").strip()
            feature_name = row.get("Feature Name", "").strip()
            sub_feature = row.get("Sub Feature", "").strip()
            description = row.get("Description", "").strip()
            primary_role = row.get("Primary Role", "").strip()
            secondary_roles = row.get("Secondary Roles", "").strip()
            nav_path = row.get("Navigation Path", "").strip()
            status_raw = row.get("Current Status", "").strip()
            completion = parse_pct(row.get("Completion Percentage", "0"))
            priority = row.get("Priority", "Medium").strip().lower()
            deps = row.get("Dependencies", "").strip()
            notes = row.get("Notes", "").strip()
            stub_details = row.get("Stub Details", "").strip()
            recommended = row.get("Recommended Action", "").strip()
            business_crit = row.get("Business Criticality", "").strip()
            platform = row.get("Platform", "").strip()
            ui_avail = row.get("UI Available", "").strip()
            backend_avail = row.get("Backend Available", "").strip()
            api_connected = row.get("API Connected", "").strip()
            db_ready = row.get("Database Ready", "").strip()
            offline = row.get("Offline Support", "").strip()
            push = row.get("Push Notifications", "").strip()

            if not feature_name:
                continue

            # Build feature_id
            feature_id = slugify(f"{module}_{category}_{feature_name}")
            if sub_feature and sub_feature != feature_name:
                full_name = f"{feature_name} — {sub_feature}"
            else:
                full_name = feature_name

            # Build tags
            tags = []
            for flag, tag in [(ui_avail, "ui"), (backend_avail, "backend"), (api_connected, "api"), (db_ready, "db"), (offline, "offline"), (push, "push")]:
                if flag.lower() == "yes":
                    tags.append(tag)
            if "Dead" in status_raw or "⚫" in status_raw:
                tags.append("dead-code")
            if "Stub" in status_raw or "🟠" in status_raw:
                tags.append("stub")
            if "TODO" in status_raw or "🔴" in status_raw:
                tags.append("todo")
            if "Missing" in status_raw:
                tags.append("missing")

            # Build metadata
            metadata = {
                "primary_role": primary_role,
                "secondary_roles": secondary_roles,
                "nav_path": nav_path,
                "platform": platform,
                "business_criticality": business_crit,
                "recommended_action": recommended,
                "stub_details": stub_details,
                "csv_row": row_num,
            }

            status = map_status(status_raw)

            body = {
                "feature_id": feature_id,
                "name": full_name,
                "description": description or None,
                "module": module or None,
                "category": category or None,
                "status": status,
                "completion_pct": completion,
                "priority": priority if priority in ("high", "medium", "low") else "medium",
                "tags": json.dumps(tags),
                "metadata": json.dumps(metadata),
            }

            if feature_id in existing_features:
                # Update
                update_body = {k: v for k, v in body.items() if k != "feature_id"}
                resp = api_call(f"/api/admin/platform/features/{existing_features[feature_id]['id']}", "PUT", update_body, token)
                if resp and resp.get("success"):
                    updated += 1
                else:
                    skipped += 1
            else:
                # Create
                resp = api_call("/api/admin/platform/features", "POST", body, token)
                if resp and resp.get("success"):
                    created += 1
                    existing_features[feature_id] = resp["data"]
                else:
                    skipped += 1

    print(f"  Features: {created} created, {updated} updated, {skipped} skipped")
    return created, updated, skipped

# ─── Screen creation from codebase scan ───────────────────────────────────

# Map screen file directories to modules and features
SCREEN_MODULE_MAP = {
    "auth": "Authentication",
    "discovery": "Discovery",
    "parent": "Parent Portal",
    "school": "School Admin",
    "teacher": "Teacher Portal",
    "tutor": "AI Tutor",
    "student": "Student",
    "notifications": "Notifications",
    "library": "Library",
}

# Map screen files to feature_ids (best-effort matching)
SCREEN_FEATURE_MAP = {
    # Auth
    "AdminAuthScreenV2": "authentication_login_password",
    "ParentAuthScreenV2": "authentication_login_otp",
    "ParentLinkChildScreenV2": "parent_portal_profile_link_child",
    "SchoolOnboardingScreenV2": "onboarding_school_setup_school_onboarding_wizard",
    "TeacherFirstLoginScreenV2": "authentication_auth_teacher_first_login",
    "LanguageSelectionScreen": "authentication_auth_language_selection",
    "LegalInfoScreenV2": "common_legal_legal_info_screen",
    "CommonLandingScreenV3": "common_landing_common_landing_screen_v3",
    "CommonLandingScreenV2": "common_landing_common_landing_screen_v2",
    "SplashScreenV2": "authentication_auth_splash_screen",
    "AuthScaffoldV2": "authentication_auth_auth_scaffold",
    # Discovery
    "DiscoveryScreenV2": "parent_portal_discovery_school_discovery",
    "SriPreview": "parent_portal_discovery_sri_score_preview",
    "AcademicCalendarScreenV2": "parent_portal_discovery_academic_calendar_parent",
    # Parent
    "ParentPortalV2": "parent_portal_navigation_parent_portal_shell",
    "ParentHomeScreenV2": "parent_portal_home_parent_home_dashboard",
    "ParentUnlinkedScreenV2": "parent_portal_home_parent_unlinked_screen",
    "ParentAcademicsScreenV2": "parent_portal_academics_attendance_view",
    "ParentFeesScreenV2": "parent_portal_fees_fee_records",
    "ParentConversationsScreenV2": "parent_portal_conversations_parent_messaging",
    "ParentMessagesScreenV2": "parent_portal_conversations_parent_messaging",
    "ParentProfileScreenV2": "parent_portal_profile_parent_profile",
    "ParentProfileCardScreenV2": "parent_portal_profile_parent_profile_card",
    "ParentLeaveScreenV2": "parent_portal_profile_parent_leave_request",
    "ParentHealthScreenV2": "parent_portal_discovery_health",
    "ParentPewsScreenV2": "missing_recommended_analytics_pews_predictive_early_warning_system",
    "ParentPulseScreen": "parent_portal_home_parent_pulse",
    "ParentReportScreen": "parent_portal_academics_ai_report_card",
    "ParentEventRegistrationScreenV2": "missing_recommended_engagement_event_registration",
    "ParentLibraryScreenV2": "parent_portal_discovery_library",
    "ScholarshipsScreenV2": "parent_portal_discovery_scholarships",
    "ScholarshipWorkflowScreenV2": "parent_portal_discovery_scholarships",
    "BusTrackingScreenV2": "missing_recommended_transport_live_bus_tracking",
    "DigitalIdCardScreen": "parent_portal_discovery_digital_id_card",
    "AiReportCardPreview": "parent_portal_academics_ai_report_card",
    "ParentActivityScreenV2": "parent_portal_home_parent_activity",
    "ParentAttendanceCalendar": "parent_portal_academics_attendance_view",
    "ParentAttendanceCard": "parent_portal_academics_attendance_view",
    "ParentCoveredCard": "parent_portal_academics_syllabus_view",
    "ParentCoveredDetailOverlay": "parent_portal_academics_syllabus_view",
    "ParentDock": "parent_portal_navigation_parent_portal_shell",
    "ParentNudgeCard": "parent_portal_home_parent_home_dashboard",
    "ParentPalette": "parent_portal_navigation_parent_portal_shell",
    "ParentResultsFeesCards": "parent_portal_fees_fee_records",
    "ParentScheduleCard": "parent_portal_academics_timetable_view",
    "PulseCard": "parent_portal_home_parent_pulse",
    # School Admin
    "SchoolPortalV2": "school_admin_navigation_school_portal_shell",
    "SchoolHomeScreenV2": "school_admin_home_admin_dashboard",
    "SchoolPeopleScreenV2": "school_admin_people_student_roster",
    "SchoolRecordsScreenV2": "school_admin_records_attendance_summary",
    "SchoolCommsScreenV2": "school_admin_comms_announcements",
    "SchoolSettingsScreenV2": "school_admin_settings_school_settings",
    "StudentRosterScreenV2": "school_admin_people_student_roster",
    "StudentProfileScreenV2": "school_admin_people_student_profile_admin",
    "TeacherAssignmentManagementScreen": "school_admin_people_teacher_assignment_management",
    "TeacherProfileScreenV2": "school_admin_people_teacher_management",
    "TeacherPerformanceScreenV2": "school_admin_analytics_teacher_performance",
    "ClassesSubjectsScreenV2": "school_admin_people_teacher_assignment_management",
    "ClassDetailScreenV2": "school_admin_people_student_roster",
    "ClassPerformanceScreenV2": "school_admin_analytics_class_performance",
    "DailyAttendanceScreenV2": "school_admin_records_daily_attendance_screen",
    "LeaveRequestsScreenV2": "school_admin_comms_leave_requests_admin",
    "LinkRequestsScreenV2": "school_admin_people_link_requests",
    "MessagesScreenV2": "school_admin_comms_messages_admin",
    "AnalyticsDashboardScreenV2": "school_admin_analytics_analytics_dashboard",
    "EditSchoolProfileScreenV2": "school_admin_settings_edit_school_profile",
    "AcademicYearManagementScreenV2": "school_admin_settings_academic_year_management",
    "AcademicCalendarPlatformScreenV2": "school_admin_settings_academic_calendar_platform",
    "AdmissionsCrmScreenV2": "school_admin_settings_admissions_crm",
    "ResultsPublishScreenV2": "school_admin_settings_results_publishing",
    "AdminReportPublishScreen": "school_admin_settings_results_publishing",
    "AdminReportingEffectivenessScreen": "school_admin_settings_results_publishing",
    "SchedulePtmScreenV2": "school_admin_comms_ptm_scheduling",
    "ScheduledMessagesScreenV2": "missing_recommended_communication_scheduled_announcements",
    "BrandingSettingsScreen": "school_admin_settings_institutional_profile",
    "HealthRecordsScreenV2": "missing_recommended_health_health_records",
    "IdCardScreen": "school_admin_settings_id_cards",
    "IdCardCardsTab": "school_admin_settings_id_cards",
    "IdCardGenerateTab": "school_admin_settings_id_cards",
    "IdCardTemplatesTab": "school_admin_settings_id_cards",
    "PewsCohortScreenV2": "missing_recommended_analytics_pews_predictive_early_warning_system",
    "PewsEffectivenessScreenV2": "missing_recommended_analytics_pews_predictive_early_warning_system",
    "PewsPreview": "missing_recommended_analytics_pews_predictive_early_warning_system",
    "PewsStudentDetailScreenV2": "missing_recommended_analytics_pews_predictive_early_warning_system",
    "SchoolLibraryScreen": "missing_recommended_library_library_management",
    "ScholarshipManagementScreenV2": "school_admin_settings_scholarship_management",
    "SchoolDayConfigScreenV2": "school_admin_settings_school_day_config",
    "StaffProfileScreenV2": "school_admin_people_staff_profile",
    "TransportManagementScreenV2": "missing_recommended_transport_transport_management",
    "AlumniScreen": "missing_recommended_admin_alumni_management",
    "AlumniDetailScreen": "missing_recommended_admin_alumni_management",
    "AlumniCampaignScreen": "missing_recommended_admin_alumni_management",
    "AdminEventRegistrationScreenV2": "missing_recommended_engagement_event_registration",
    "UnifiedCreateEventScreenV2": "missing_recommended_engagement_event_registration",
    # Teacher
    "TeacherPortalV2": "teacher_portal_navigation_teacher_portal_shell",
    "TeacherHomeScreenV2": "teacher_portal_today_teacher_today_home",
    "TeacherUpdateScreenV2": "teacher_portal_update_attendance_marking",
    "TeacherClassesScreenV2": "teacher_portal_classes_class_list",
    "TeacherProfileScreenV2": "teacher_portal_profile_teacher_profile",
    "TeacherAttendanceScreenV2": "teacher_portal_update_attendance_marking",
    "TeacherMarksScreenV2": "teacher_portal_update_gradebook_assessments",
    "TeacherSyllabusScreenV2": "teacher_portal_update_syllabus_tracking",
    "TeacherHomeworkScreenV2": "teacher_portal_update_homework_management",
    "TeacherMessagesScreenV2": "teacher_portal_messaging_teacher_messaging",
    "TeacherStudentProfileScreenV2": "teacher_portal_classes_student_profile_scoped",
    "TeacherCheckInPopup": "teacher_portal_today_teacher_check_in",
    "TeacherDialogs": "teacher_portal_update_attendance_marking",
    "TeacherDock": "teacher_portal_navigation_teacher_portal_shell",
    "TeacherHeader": "teacher_portal_navigation_teacher_portal_shell",
    "TeacherHealthAlertsScreenV2": "missing_recommended_health_health_records",
    "TeacherKit": "teacher_portal_navigation_teacher_portal_shell",
    "TeacherLessonPlanScreenV2": "missing_recommended_academics_lesson_planning",
    "TeacherPewsScreenV2": "missing_recommended_analytics_pews_predictive_early_warning_system",
    "TeacherPtmEventRegistrationScreenV2": "missing_recommended_engagement_event_registration",
    "TeacherReportDraftEditorScreen": "teacher_portal_update_gradebook_assessments",
    "TeacherReportReviewQueueScreen": "teacher_portal_update_gradebook_assessments",
    "TeacherScopeSelector": "teacher_portal_update_attendance_marking",
    "TeacherTimetableScreenV2": "teacher_portal_today_teacher_today_home",
    "TransportAttendanceScreenV2": "missing_recommended_transport_transport_management",
    # Tutor
    "TutorChatScreen": "missing_recommended_academics_ai_tutoring",
    "TutorPlanScreen": "missing_recommended_academics_ai_tutoring",
    "TutorPracticeScreen": "missing_recommended_academics_ai_tutoring",
    "ParentProgressScreen": "missing_recommended_academics_ai_tutoring",
    "TeacherHeatmapScreen": "missing_recommended_academics_ai_tutoring",
    # Student
    "StudentLibraryScreen": "missing_recommended_library_library_management",
    # Notifications
    "NotificationsScreenV2": "parent_portal_notifications_in_app_notifications",
    # Library components
    "LibraryUixComponents": "missing_recommended_library_library_management",
    "LibraryUixComponents2": "missing_recommended_library_library_management",
    "LibraryUixComponents3": "missing_recommended_library_library_management",
    # Shared
    "Shared": "common_design_system_v_design_system",
    "Skeletons": "common_design_system_v_design_system",
}

# Map screen type based on file name patterns
def get_screen_type(filename):
    name = filename.replace(".kt", "")
    if "Popup" in name or "Dialog" in name:
        return "dialog"
    if "Overlay" in name:
        return "overlay"
    if "Card" in name and "Screen" not in name:
        return "component"
    if "Dock" in name or "Header" in name or "Kit" in name:
        return "component"
    if "Palette" in name or "Scaffold" in name:
        return "component"
    if "Preview" in name:
        return "preview"
    if "Tab" in name and "Screen" not in name:
        return "tab"
    if "Portal" in name:
        return "portal_shell"
    if "Screen" in name:
        return "screen"
    return "component"

def get_screen_route(filename, directory):
    name = filename.replace(".kt", "").replace("V2", "").replace("Screen", "")
    route_parts = []
    dir_map = {"auth": "auth", "discovery": "discovery", "parent": "parent", "school": "school", "teacher": "teacher", "tutor": "tutor", "student": "student", "notifications": "notifications", "library": "library"}
    base = dir_map.get(directory, "shared")
    slug = slugify(name)
    return f"/{base}/{slug}"

def create_screens_from_codebase(token, existing_screens, existing_features):
    created = 0
    updated = 0
    skipped = 0

    for root, dirs, files in os.walk(SCREENS_DIR):
        rel_dir = os.path.relpath(root, SCREENS_DIR)
        if rel_dir == ".":
            directory = "shared"
        else:
            directory = rel_dir.split("/")[0]

        for filename in sorted(files):
            if not filename.endswith(".kt"):
                continue

            screen_name = filename.replace(".kt", "")
            screen_id = slugify(f"{directory}_{screen_name}")

            # Skip non-screen components but still register them
            screen_type = get_screen_type(filename)
            module = SCREEN_MODULE_MAP.get(directory, "Shared")
            feature_id = SCREEN_FEATURE_MAP.get(screen_name)

            # Verify feature exists
            if feature_id and feature_id not in existing_features:
                feature_id = None  # Don't link to non-existent feature

            route = get_screen_route(filename, directory)

            # Build purpose description
            purpose = f"{screen_type.replace('_', ' ').title()} in {module}"
            if screen_type == "dialog":
                purpose = f"Dialog/popup in {module}"
            elif screen_type == "overlay":
                purpose = f"Overlay screen in {module}"
            elif screen_type == "component":
                purpose = f"UI component in {module}"
            elif screen_type == "portal_shell":
                purpose = f"Portal shell for {module}"

            body = {
                "screen_id": screen_id,
                "name": screen_name,
                "route": route,
                "module": module,
                "purpose": purpose,
                "feature_id": feature_id,
                "metadata": json.dumps({"type": screen_type, "file": filename, "directory": directory}),
            }

            if screen_id in existing_screens:
                # Update
                update_body = {k: v for k, v in body.items() if k != "screen_id"}
                resp = api_call(f"/api/admin/platform/screens/{existing_screens[screen_id]['id']}", "PUT", update_body, token)
                if resp and resp.get("success"):
                    updated += 1
                else:
                    skipped += 1
            else:
                # Create
                resp = api_call("/api/admin/platform/screens", "POST", body, token)
                if resp and resp.get("success"):
                    created += 1
                    existing_screens[screen_id] = resp["data"]
                else:
                    skipped += 1

    print(f"  Screens: {created} created, {updated} updated, {skipped} skipped")
    return created, updated, skipped

# ─── Main ─────────────────────────────────────────────────────────────────

def main():
    print("=" * 70)
    print("Platform Registry Seed Script — Pass 1")
    print("=" * 70)

    token = authenticate()

    print("\nFetching existing registry data...")
    existing_features = fetch_existing_features(token)
    existing_screens = fetch_existing_screens(token)

    print("\n--- Pass 1A: Features from CSV (163 rows) ---")
    f_created, f_updated, f_skipped = create_features_from_csv(token, existing_features)

    print("\n--- Pass 1B: Screens from codebase scan ---")
    s_created, s_updated, s_skipped = create_screens_from_codebase(token, existing_screens, existing_features)

    print("\n" + "=" * 70)
    print("PASS 1 SUMMARY")
    print(f"  Features: {f_created} created, {f_updated} updated, {f_skipped} skipped")
    print(f"  Screens:  {s_created} created, {s_updated} updated, {s_skipped} skipped")
    print(f"  Total features in DB: {len(existing_features)}")
    print(f"  Total screens in DB:  {len(existing_screens)}")
    print("=" * 70)

if __name__ == "__main__":
    main()
