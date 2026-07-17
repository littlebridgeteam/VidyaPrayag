# Auto Fix Mode — Bug Log

## Batch 1: Discovered 2026-07-14

### BUG-001: Teachers tab in People Directory is completely blank
- **Severity:** High
- **Category:** Functional — Data not rendering
- **Expected:** Teachers tab should display list of teacher cards (dashboard shows 9 Total Teachers, API returns 200 OK)
- **Actual:** Teachers tab shows completely blank screen — no cards, no loading indicator, no empty state
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap People tab in bottom navigation
  3. Observe: Teachers sub-tab is selected but screen is blank
- **Screen:** admin/people/teachers
- **Screenshot:** /tmp/qa_screenshots/68_teachers_check.png
- **Status:** 🔍 Investigating

### BUG-002: Duplicate "Class" prefix in student cards and announcements
- **Severity:** Medium
- **Category:** UI — Text formatting
- **Expected:** Student card should show "Class 10-A" (single Class prefix)
- **Actual:** Student card displays "Class Class 10-A" — the word "Class" is duplicated. Also appears in announcement descriptions.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab > Students sub-tab
  3. Scroll through student cards — some show "Class Class X-A"
- **Screen:** admin/people/students, admin/comms/announcements
- **Screenshot:** /tmp/qa_screenshots/67_students_loaded.png
- **Status:** 🔍 Investigating

### BUG-003: Notification timestamps displayed as raw ISO 8601 strings
- **Severity:** Medium
- **Category:** UI — Data formatting
- **Expected:** Timestamps should display as relative time (e.g., "3h ago") or readable format
- **Actual:** Raw ISO 8601 string shown: "2026-07-14T13:23:46.734177Z" — includes microseconds and timezone suffix
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap notification bell icon (top right of dashboard)
  3. Observe timestamp on notification items
- **Screen:** admin/notifications
- **Screenshot:** /tmp/qa_screenshots/73_notifications.png
- **Status:** 🔍 Investigating

### BUG-004: Announce quick action button opens Notifications instead of Announcements
- **Severity:** High
- **Category:** Functional — Navigation
- **Expected:** Tapping Announce should open the Announcements screen or create-announcement composer
- **Actual:** Tapping Announce navigates to the Notifications screen. Add Event and Reports buttons work correctly.
- **Steps to Reproduce:**
  1. Login as school admin
  2. On dashboard, locate quick action row: Announce / Add Event / Reports
  3. Tap Announce button
  4. Observe: Notifications screen opens
- **Screen:** admin/dashboard → admin/notifications (wrong)
- **Screenshot:** /tmp/qa_screenshots/75_announce_bug_v2.png
- **Status:** Reported to Slack

### BUG-014: Analytics screen has overlapping UI components
- **Severity:** High
- **Category:** UI — Layout overlap
- **Expected:** All labels, stat cards, and insight items should be properly spaced with no overlapping text or components
- **Actual:** Multiple components overlap on the Analytics screen:
  1. "INSIGHTS" label (X=53-209, Y=287) overlaps with "OVERVIEW" label (X=209-236, Y=287) — same Y position, adjacent X
  2. "Top Performer" (X=222-465, Y=334) overlaps with "Attendance Peak" (X=465-503, Y=334) — same Y, touching X
  3. "Dr. Jenkins: 5.0 Engagement Rating" (X=222-700, Y=382) overlaps with "Class 10-A reached 99% attendance" (X=700-718, Y=382) — text colliding
  4. "OVERVIEW" label (X=798-959, Y=371) sits on the same row as "+36.4%" (X=214-299, Y=380) and "80%" (X=181-214, Y=380) — label mispositioned over stat cards row
  5. Two sections (INSIGHTS and OVERVIEW) appear rendered on top of each other rather than stacked vertically
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Analytics screen
  3. Observe: Labels and stat values overlap each other in the top section
- **Screen:** admin/analytics
- **Screenshot:** /tmp/qa_screenshots/manual_overlap.png
- **Status:** Reported to Slack

### BUG-005: Message thread screen is empty — no message bubbles displayed
- **Severity:** High
- **Category:** Functional — Data not rendering
- **Expected:** Message thread should display all conversation messages as chat bubbles
- **Actual:** Opening conversation shows only header (avatar, name, timestamp) but message body area is completely blank
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap Comms tab > Messages sub-tab
  3. Tap on Gaurav conversation
  4. Observe: Message thread is empty
- **Screen:** admin/comms/messages/thread
- **Screenshot:** /tmp/qa_screenshots/77_message_thread.png
- **Status:** 🔍 Investigating

### BUG-015: Unexpected logout — session cleared after server IP change
- **Severity:** Medium
- **Category:** Functional — Session/Auth
- **Expected:** App should maintain session across server restarts on the same address
- **Actual:** After rebuilding the app with a new server IP (192.168.1.58:8080 replacing 192.168.1.15:8080), the app logged out unexpectedly. Root cause: the old JWT token stored in DataStore was issued by the previous server instance. On the first API call to the new server, the token was rejected (401) → TokenAuthenticator attempted refresh via POST /api/v1/auth/refresh → refresh token also invalid → onRefreshFailed() cleared the session → navigated to landing screen.
- **Steps to Reproduce:**
  1. Login as school admin on server at IP A
  2. Change server IP and rebuild/reinstall app
  3. Open app — first API call gets 401 → refresh fails → clean logout
- **Screen:** App-wide (auth flow)
- **Screenshot:** /tmp/qa_screenshots/manual_logout.png
- **Status:** Reported to Slack — **Not a bug. Expected behavior: token refresh flow worked correctly. 401 → refresh fail → clean logout.**

### BUG-016: Student detail screen has overlapping UI components
- **Severity:** High
- **Category:** UI — Layout overlap
- **Expected:** Student profile/detail screen should have properly spaced sections: danger zone, health records, overview stats, academic info, teacher/parent connections
- **Actual:** Multiple sections overlap on the student detail screen:
  1. "DANGER ZONE" label (X=53-299, Y=287) overlaps with "Health Records" card (X=253-519, Y=329) — danger zone section colliding with health records card
  2. "Remove student" text (X=100-372, Y=400) overlaps with "Health Records" description (X=253-880, Y=383) — text collision
  3. "Remove from school" button (Y=596) and danger zone description (Y=454) are mixed with the health records section
  4. Academic Overview fields (Class=1, Section=A, Roll Number=2, Admission Date=2026-06-16) all left-aligned at X=221 with labels and values stacked too closely
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap People tab > Students sub-tab
  3. Tap on any student to open detail/profile
  4. Observe: DANGER ZONE, Health Records, and Remove student sections overlap each other
- **Screen:** admin/people/students/detail
- **Screenshot:** /tmp/qa_screenshots/manual_teacher_overlap.png
- **Status:** Reported to Slack

### BUG-017: Call and Message buttons non-functional in People Directory student cards
- **Severity:** High
- **Category:** Functional — Dead buttons
- **Expected:** Tapping "Call" should open phone dialer with student's parent phone number. Tapping "Message" should open messaging/compose screen or SMS intent.
- **Actual:** Both "Call" (X=518-563, Y=1343) and "Message" (X=783-889, Y=1343) buttons on student cards in People Directory > Students tab are completely non-functional. Tapping either button produces no UI change, no logcat activity, no intent fired. Buttons appear to have no click handler attached.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap People tab > Students sub-tab
  3. Observe student cards with Profile / Call / Message buttons
  4. Tap "Call" button — nothing happens
  5. Tap "Message" button — nothing happens
- **Screen:** admin/people/students (student card row actions)
- **Screenshot:** /tmp/qa_screenshots/manual_recapture.png
- **Status:** Reported to Slack

### BUG-018: Records > Pace tab — JsonConvertException deserialization error
- **Severity:** High
- **Category:** API — Deserialization failure
- **Expected:** Pace tab should display pace/syllabus coverage data without errors
- **Actual:** Records > Pace tab shows "Something went wrong" error screen with full exception:
  `JsonConvertException: Illegal input: Field 'id' is required for type with serial name 'com.littlebridge.enrollplus.feature.teacher.domain.model.PaceSnapshotDto', but it was missing at path: $.data.snapshots[0]`
  The API response is missing the `id` field in the first snapshot object, causing kotlinx.serialization to fail.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap Records tab in bottom navigation
  3. Tap "Pace" sub-tab
  4. Observe: "Something went wrong" error with JsonConvertException
- **Screen:** admin/records/pace
- **Screenshot:** /tmp/qa_screenshots/manual_api_issue.png
- **Status:** Reported to Slack

### BUG-019: "Cancelled" tab label clipped in Scheduled Messages screen
- **Severity:** Medium
- **Category:** UI — Layout/overflow
- **Expected:** All tab labels (All, Scheduled, Dispatched, Failed, Cancelled) should be fully visible and properly spaced within the screen width
- **Actual:** The "Cancelled" tab label in the Scheduled Messages screen is clipped at the right edge. Tab bounds extend to X=1038 which exceeds the visible area. The tab row has 5 tabs (All, Scheduled, Dispatched, Failed, Cancelled) but "Cancelled" is pushed to the far right and partially cut off.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap Comms tab
  3. Navigate to Scheduled Messages
  4. Observe: "Cancelled" tab at the right end is clipped/cut off
- **Screen:** admin/comms/scheduled-messages
- **Screenshot:** /tmp/qa_screenshots/manual_cancelled.png
- **Status:** Reported to Slack

### BUG-020: "New message" button overlaps with system navigation bar
- **Severity:** Medium
- **Category:** UI — System nav bar overlap
- **Expected:** The "New message" FAB/button should be positioned above the system navigation bar with proper bottom inset padding
- **Actual:** The "New message" button (content-desc="New message", X=922-985, Y=2302) overlaps with the Android system navigation bar at the bottom of the screen. The button is positioned at Y=2302 which is within the system nav bar area (~Y=2292+), making it difficult to tap and visually overlapping.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap Comms tab > Messages sub-tab
  3. Observe: "New message" button at bottom-right overlaps with system navigation bar
- **Screen:** admin/comms/messages
- **Screenshot:** /tmp/qa_screenshots/manual_newchat_overlap.png
- **Status:** Reported to Slack

### BUG-021: New message compose screen — missing search, bottom misalignment, white status bar
- **Severity:** High
- **Category:** UI — Multiple issues
- **Expected:** New message screen should have: (1) a search bar to filter recipients, (2) bottom action bar properly aligned above system nav bar, (3) system status bar with proper theme color (not white)
- **Actual:** Three issues on the New message compose screen:
  1. **Missing search bar** — recipient list shows all teachers (Asha Verma, Luttan, T1-T20...) with no search field to filter. For schools with many staff, this is unusable.
  2. **Bottom UI misalignment** — "Attach image" (X=61-119, Y=2221) and "Send" (X=961-1019, Y=2221) buttons with "Pick a recipient above…" text (Y=2206) are pushed to the very bottom, overlapping with system nav bar area
  3. **White system status bar** — the top status bar area is white, making system icons (time, battery, wifi) invisible/low-contrast against the white background. Should use the app's theme color.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap Comms tab > Messages > New message button
  3. Observe: No search bar for recipients, bottom buttons misaligned, top status bar is white
- **Screen:** admin/comms/messages/new
- **Screenshot:** /tmp/qa_screenshots/manual_bottom_align.png
- **Status:** Reported to Slack

### BUG-022: Text input bar overlaps with action buttons and system nav bar on New message screen
- **Severity:** High
- **Category:** UI — Layout overlap / missing insets
- **Expected:** Text input field, attach image button, and send button should be properly spaced in a bottom action bar that sits above the system navigation bar with proper inset padding
- **Actual:** On the New message compose screen, the text input field ("hello", EditText at X=206-874, Y=2156), "Attach image" button (X=61-119, Y=2221), and "Send" button (X=961-1019, Y=2221) are all crammed together at the very bottom of the screen. They overlap with each other and with the system navigation bar (~Y=2292+). No bottom inset padding is applied. The text bar appears to be overlapping or touching the attach/send buttons with no spacing.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap Comms tab > Messages > New message button
  3. Select a recipient (e.g., Gaurav)
  4. Tap the text input bar to type a message
  5. Observe: Text input, attach, and send buttons are overlapping at the bottom, crammed against system nav bar
- **Screen:** admin/comms/messages/new
- **Screenshot:** /tmp/qa_screenshots/manual_textbar3.png
- **Status:** Reported to Slack

### BUG-023: Keyboard covers text input bar and action buttons on New message screen
- **Severity:** Critical
- **Category:** UI — Keyboard inset missing
- **Expected:** When the soft keyboard opens, the text input field and send/attach buttons should lift up above the keyboard so they remain visible and accessible
- **Actual:** When the soft keyboard opens on the New message compose screen, there appears to be **double ime padding** applied. Both the content/recipient list area AND the bottom compose bar receive keyboard inset padding, causing the recipient list to be pushed up unnecessarily while the compose bar (text input at Y=2156, attach/send at Y=2221) still sits at the bottom edge behind/overlapping with the keyboard. The result is that the text input and action buttons are nearly at the top of the keyboard area rather than properly positioned just above it.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap Comms tab > Messages > New message button
  3. Select a recipient (e.g., Gaurav)
  4. Tap the text input bar — keyboard opens
  5. Observe: Text input field, attach button, and send button are completely hidden behind the keyboard
- **Screen:** admin/comms/messages/new
- **Screenshot:** /tmp/qa_screenshots/manual_keyboard_open.png
- **Status:** Reported to Slack

### BUG-024: Tapping Send on New message triggers unexpected logout
- **Severity:** Critical
- **Category:** Functional — Auth failure on action
- **Expected:** Tapping Send should send the message and remain on the conversation screen
- **Actual:** Tapping the Send button on the New message compose screen triggers an unexpected logout. The app navigates back to the landing/login screen. Root cause is likely the same as BUG-015: the message send API call receives a 401 (token expired), the TokenAuthenticator attempts a refresh, the refresh fails, and onRefreshFailed() clears the session and navigates to landing. The staging server (Render) is up and login works, but the stored token may have expired between login and the send action.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap Comms tab > Messages > New message
  3. Select a recipient (e.g., Gaurav)
  4. Type a message in the text bar
  5. Tap Send button
  6. Observe: App logs out and returns to landing screen
- **Screen:** admin/comms/messages/new → landing (logout)
- **Screenshot:** /tmp/qa_screenshots/manual_send_logout.png
- **Status:** Reported to Slack

### BUG-025: Schedule PTM screen — HISTORY label overlaps with Schedule new PTM button
- **Severity:** Medium
- **Category:** UI — Layout overlap
- **Expected:** HISTORY section label and "Schedule new PTM" button should be properly spaced, not overlapping
- **Actual:** On the Schedule PTM screen, the "HISTORY" label (X=53-199, Y=287) overlaps with the "Schedule new PTM" button (X=379-702, Y=313) at the same Y position. The label and button are rendered on the same row with insufficient vertical spacing.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Schedule PTM screen
  3. Observe: HISTORY label and Schedule new PTM button overlap at the top
- **Screen:** admin/ptm/schedule
- **Screenshot:** /tmp/qa_screenshots/manual_overlap_cards.png
- **Status:** Reported to Slack

### BUG-026: Redundant Notifications entry point — Home screen and Comms Hub
- **Severity:** Low
- **Category:** UX — Feature redundancy
- **Expected:** Notifications should have a single, clear entry point. If notifications are already accessible from the Home screen top bar, the Comms Hub should not duplicate it as a sub-tab.
- **Actual:** Notifications has two entry points:
  1. Home screen — notifications icon at top right (X=940-998, Y=148, content-desc="Notifications")
  2. Comms Hub — "Notifications" sub-tab (X=726-940, Y=336) alongside Announcements, Messages, and PTM
  This is redundant and confusing. The Comms Hub Notifications tab should be removed since notifications are already accessible from the Home screen.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Observe Home screen — notifications icon at top right
  3. Tap Comms tab — observe "Notifications" sub-tab in Comms Hub
  4. Both lead to the same notifications screen
- **Screen:** admin/home + admin/comms/hub
- **Screenshot:** /tmp/qa_screenshots/manual_home_check.png
- **Status:** Reported to Slack

### BUG-027: Institutional Profile screen — multiple overlapping field labels and sections
- **Severity:** High
- **Category:** UI — Layout overlap
- **Expected:** School profile form should have properly spaced sections (School profile, Location, Contact details) with fields stacked vertically, each with clear labels and values
- **Actual:** The Institutional Profile screen has severe overlapping throughout:
  1. "School profile" (Y=287) overlaps with "Location" (Y=329) and "Contact details" (Y=329) — section labels on same row
  2. "School address" (Y=376), "Leadership contact" (Y=376), "Public communication" (Y=376) — three labels crammed on same Y position
  3. "Address" (Y=490) overlaps with "School name" (Y=490) — field labels on same row
  4. "Phone" (Y=687) overlaps with "City" (Y=743) and "PIN" (Y=743) — misaligned fields
  5. "Email" (Y=884) overlaps with "Medium" (Y=922) and "District" (Y=940) — fields overlapping
  6. "School type" (Y=1081) overlaps with "State" (Y=1137) — fields overlapping
  7. "Save changes" button (Y=2230) at very bottom, overlapping with system nav bar
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > Institutional Profile
  3. Observe: Section labels, field labels, and values overlap throughout the form
- **Screen:** admin/settings/institutional-profile
- **Screenshot:** /tmp/qa_screenshots/manual_overlap_cards2.png
- **Status:** Reported to Slack

### BUG-028: Institutional Profile — fields appear disabled, no validation, Save button non-clickable
- **Severity:** High
- **Category:** UI/UX — Disabled appearance, missing validation, dead button
- **Expected:** Form fields should look interactive (not greyed out), validate input (phone, email, PIN format), and Save changes button should be clickable
- **Actual:** Three issues on the Institutional Profile form:
  1. **Fields appear disabled** — EditText fields are technically enabled=true/clickable=true but visually appear greyed out/disabled in the UI. Empty fields show placeholder "Aa" with no clear styling indicating they're editable.
  2. **No field validation** — Phone ("1234567890"), PIN ("1234546"), Email ("principal1@gmail.com") have no visible validation. No error messages for invalid formats, no required field indicators.
  3. **Save changes button is non-clickable** — The "Save changes" button (X=420-660, Y=2230) has clickable=false, meaning it cannot be tapped. The form cannot be saved.
  4. **Stale success message** — "Institutional profile updated" text (Y=287) is visible on screen, suggesting a success state that shouldn't appear on initial load.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > Institutional Profile
  3. Observe: Fields look disabled/greyed out, no validation indicators, Save button is not tappable
- **Screen:** admin/settings/institutional-profile
- **Screenshot:** /tmp/qa_screenshots/manual_disabled_fields.png
- **Status:** Reported to Slack

### BUG-029: Institutional Profile — fields not grouped by section
- **Severity:** Medium
- **Category:** UX — Information architecture
- **Expected:** Form fields should be visually grouped under their respective section headers (School profile, Location, Contact details) with clear visual separation — e.g., cards, dividers, or spacing between groups
- **Actual:** The Institutional Profile form has three section headers (School profile, Location, Contact details) with sub-labels (School address, Leadership contact, Public communication), but all form fields are mixed together in a single overlapping column with no visual grouping. Fields are not organized under their respective sections:
  - School name, School type, Medium should be under "School profile"
  - Address, City, PIN, District, State should be under "Location"
  - Phone, Email should be under "Contact details"
  Instead, all fields are jumbled together with overlapping labels (see BUG-027).
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > Institutional Profile
  3. Observe: Section headers exist but fields are not grouped under them — everything is mixed in one overlapping column
- **Screen:** admin/settings/institutional-profile
- **Screenshot:** /tmp/qa_screenshots/manual_section_grouping.png
- **Status:** Reported to Slack

### BUG-030: Teacher management in Settings is redundant with People tab
- **Severity:** Low
- **Category:** UX — Feature redundancy
- **Expected:** Teacher management should have a single entry point. If teachers can be viewed and managed from the People tab > Teachers sub-tab, the Settings > Teacher management card is redundant.
- **Actual:** Teacher management has two entry points:
  1. People tab > Teachers sub-tab — view, search, and manage teachers
  2. Settings > Teacher management card ("Add, view & remove teachers", Y=1147) — duplicate functionality
  This is redundant and confusing. The Settings > Teacher management card should be removed since teacher management is already available in the People tab.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Tap People tab — observe Teachers sub-tab for managing teachers
  3. Tap Settings — observe "Teacher management" card with same description "Add, view & remove teachers"
  4. Both provide access to the same teacher management functionality
- **Screen:** admin/settings + admin/people/teachers
- **Screenshot:** /tmp/qa_screenshots/manual_teacher_mgmt.png
- **Status:** Reported to Slack

### BUG-031: Branding & Photos — no proper color picker, only hex text fields
- **Severity:** Medium
- **Category:** UX — Missing color picker
- **Expected:** Color customization should include a visual color picker (color wheel, swatches, or palette) that allows users to pick colors visually, not just type hex codes
- **Actual:** The Branding & Photos screen has three color fields (Primary Color #DC2626, Secondary Color #059669, Accent Color #FF1240) but only provides plain hex code text inputs. Users must manually type hex codes to customize branding colors. There is no visual color picker, no color wheel, no swatch palette, and no preview of the selected color next to the input. This is not user-friendly for non-technical users who don't know hex codes.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > Branding & Photos
  3. Observe: Color customization only has hex code text fields — no visual color picker
- **Screen:** admin/settings/branding
- **Screenshot:** /tmp/qa_screenshots/manual_color_picker.png
- **Status:** Reported to Slack

### BUG-032: Branding elements (logo, cover, profile pic, gallery) not displayed in Settings/Home
- **Severity:** Medium
- **Category:** UX — Branding not surfaced
- **Expected:** Branding elements (school logo, campus cover photo, admin profile picture, campus gallery) configured in Branding & Photos should be visible in relevant places — Settings landing page, Home screen header, or a public profile preview
- **Actual:** The Branding & Photos screen allows uploading:
  1. Admin profile picture ("Your photo across the console")
  2. School logo ("Logo seen by parents & staff")
  3. Campus cover ("Hero photo for your public profile")
  4. Campus gallery ("Showcase your school")
  However, none of these are displayed on the Settings landing page, Home screen, or any other visible location in the app. The branding is configured but never shown back to the user. There should be a preview or display of the logo, cover, and profile pic on the Settings page and/or Home screen header.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > Branding & Photos
  3. Upload logo, cover photo, profile picture
  4. Go back to Settings landing page — no logo/cover shown
  5. Go to Home screen — no logo/profile pic in header
- **Screen:** admin/settings/branding + admin/settings + admin/home
- **Screenshot:** /tmp/qa_screenshots/manual_branding_visibility.png
- **Status:** Reported to Slack

### BUG-033: ID Cards — inactive cards mixed with active, top stat cards overlapping
- **Severity:** Medium
- **Category:** UX + UI — Grouping and layout overlap
- **Expected:** (1) Inactive ID cards should be grouped in a separate section or tab to declutter the active cards list. (2) Top stat cards (Students, Teachers, Staff counts) should be properly spaced with no overlapping text.
- **Actual:** Two issues on the ID Cards screen:
  1. **Inactive cards mixed with active** — Active card (t3, Y=745, "Active") and inactive cards (t3 Y=964, T2 Y=1182, t1 Y=1400, all "Inactive") are all displayed in the same list with no separation. Inactive cards should be grouped separately to declutter the page.
  2. **Top stat cards overlapping** — Summary cards "10 Students" (Y=334), "0 Teachers" (Y=382), "0 Staff" (Y=430) overlap with "Total Cards" label (Y=455) and "10" value (Y=359). "First Steps" text appears at both Y=351 and Y=445, overlapping with the stat cards. Text and values are colliding in the top section.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > ID Cards
  3. Observe: Inactive cards mixed in same list as active cards; top stat cards have overlapping text
- **Screen:** admin/settings/id-cards
- **Screenshot:** /tmp/qa_screenshots/manual_inactive_group.png
- **Status:** Reported to Slack

### BUG-034: ID Cards Live Preview is barebones and poorly designed
- **Severity:** Medium
- **Category:** UI — Poor preview quality
- **Expected:** Live Preview should show a realistic ID card with proper layout — school logo, photo placeholder, name, role, class, QR code, and branding colors matching the template being created
- **Actual:** The Live Preview on the ID Cards screen is extremely basic — just plain text labels ("Vidya Prayag School", "Preview", "Student", "Class 10-A", "Scan QR to verify") with no visual card design. Missing:
  1. No card background/border styling
  2. No photo placeholder
  3. No school logo
  4. No actual QR code rendered (only text "Scan QR to verify")
  5. No branding colors applied
  6. No proper card layout/structure
  The preview does not represent what the actual ID card will look like.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > ID Cards
  3. Scroll to Live Preview section
  4. Observe: Preview is just plain text, no realistic card design
- **Screen:** admin/settings/id-cards/preview
- **Screenshot:** /tmp/qa_screenshots/manual_preview.png
- **Status:** Reported to Slack

### BUG-035: Generated ID cards are barebones due to bad template design
- **Severity:** High
- **Category:** UI — Poor card output quality
- **Expected:** Generated ID cards should display a proper visual card with photo, school logo, name, role, class, QR code, and branding colors — matching the template configuration
- **Actual:** The actual generated ID cards on the Cards tab are just as barebones as the Live Preview (BUG-034). Each card shows only:
  1. Name (e.g., "AravB", "Gaurav", "Arav", "C3")
  2. "Student" role label
  3. "Valid" badge
  4. "PDF" and "Verify" buttons
  5. Delete icon
  Missing from generated cards:
  - No photo placeholder or actual photo
  - No school logo
  - No QR code rendered
  - No class/section info
  - No card background/border styling
  - No branding colors applied
  - No proper card layout/structure
  The bad template design (BUG-034) directly results in poor actual card output. The cards are not usable as real ID cards.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > ID Cards > Cards tab
  3. Observe: Generated cards are barebones — just name, role, and buttons, no visual card design
- **Screen:** admin/settings/id-cards/cards
- **Screenshot:** /tmp/qa_screenshots/manual_bad_cards.png
- **Status:** Reported to Slack

### BUG-036: PDF and Verify buttons on ID cards have incorrect redirection
- **Severity:** High
- **Category:** Functional — Incorrect navigation/redirect
- **Expected:** PDF button should generate/download a PDF of the ID card within the app. Verify button should open an in-app verification view showing the card details and QR verification — not launch an external browser.
- **Actual:**
  1. **Verify button** — Tapping Verify opens an external Chrome browser to `app.vidyaprayag.in/verify?id=...&type=student`. This is an external web redirect, not an in-app feature. The browser also shows "Your connection to this site is not secure" warning (HTTP, not HTTPS). User is taken out of the app experience.
  2. **PDF button** — Tapping PDF does not produce any visible result (no PDF generated, no download, no viewer, no toast). The button appears non-functional or silently fails.
  Both buttons should have proper in-app feature behaviour — PDF should generate and display/share a PDF, Verify should show an in-app verification screen.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > ID Cards > Cards tab
  3. Tap "Verify" on any card — opens external Chrome browser to verify URL (not secure)
  4. Go back, tap "PDF" on any card — no visible action occurs
- **Screen:** admin/settings/id-cards/cards
- **Screenshot:** /tmp/qa_screenshots/manual_verify_tap.png
- **Status:** Reported to Slack

### BUG-037: Gamification Management Console — all buttons non-functional, API endpoints missing
- **Severity:** High
- **Category:** Functional — Dead UI + missing backend
- **Expected:** Gamification Management Console should allow toggling feature flags (Enable Gamification, Leaderboards, Rewards Shop, etc.) and viewing badge catalog, backed by working API endpoints
- **Actual:** Two issues:
  1. **All UI buttons/toggles non-clickable** — Every element on the Gamification Management Console has `clickable=false`. The "Enable Gamification" master toggle, all granular toggles (Leaderboards, Rewards Shop, House System, Quests, Mentor System, Shout-Outs, Seasonal Events, Class Goals, Combos, XP Boosts), and badge catalog items are all non-interactive. None of the buttons work.
  2. **API endpoints missing** — Server returns "Endpoint not found" for both:
     - `GET /api/v1/gamification/config` → 404: "Endpoint not found"
     - `GET /api/v1/gamification/badges` → 404: "Endpoint not found"
  The UI is rendering a management console for a feature that has no backend support. The badge catalog shows 14 badges but these are likely hardcoded, not from the server.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Navigate to Settings > Gamification Management Console
  3. Observe: All toggles and buttons are non-clickable
  4. API check: GET /api/v1/gamification/config returns 404
- **Screen:** admin/settings/gamification
- **Screenshot:** /tmp/qa_screenshots/manual_ui_button_fail.png
- **Status:** Reported to Slack

### BUG-038: Jump To screen — some screens only accessible via overlay search, not from normal navigation
- **Severity:** Medium
- **Category:** UX — Hidden navigation paths
- **Expected:** All screens should be accessible from normal tab navigation or settings menus, not only from the Jump To search overlay
- **Actual:** The Jump To screen overlay shows screens like "Notifications" (Overlay notifications) and "Academic Calendar" (Overlay calendar) that are not accessible from any normal tab or settings menu. These screens are only reachable via the Jump To search, making them effectively hidden from users who don't know about the search feature.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap "Jump to screen..." search bar on Home
  3. Observe: Notifications and Academic Calendar listed as overlay screens
  4. Try to find these from normal navigation — they are not accessible from any tab or settings
- **Screen:** admin/home (Jump To overlay)
- **Screenshot:** /tmp/qa_screenshots/manual_auto_jump_full.png
- **Status:** Reported to Slack

### BUG-039: Daily Attendance — all P/A/L buttons and Save button are non-clickable
- **Severity:** Critical
- **Category:** Functional — Dead buttons
- **Expected:** Present/Absent/Late buttons should be tappable to mark attendance, and Save Attendance should submit
- **Actual:** On the Daily Attendance screen, all P/A/L buttons for every faculty member (T3-T8, T20, Asha Verma, Luttan) and the "Save Attendance" button have `clickable=false`. No attendance can be marked. The entire attendance marking feature is non-functional.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Home > Mark attendance
  3. Observe: P/A/L buttons do not respond to taps, Save button does not work
- **Screen:** admin/attendance/daily
- **Screenshot:** /tmp/qa_screenshots/manual_auto_mark_att.png
- **Status:** Reported to Slack

### BUG-040: Daily Attendance — all class tabs show "No roster" despite Home showing 11 students
- **Severity:** High
- **Category:** Data — Roster missing
- **Expected:** Students should appear in their respective class tabs for attendance marking
- **Actual:** All 4 class tabs (Grade 10-A, 10-B, 11-A, 12-C) show "No roster" — "There are no students in Grade X-X yet." But the Home screen shows "11 Total Students" and the People Directory > Students tab shows 2 students. Data inconsistency between screens.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Home > Mark attendance > Students tab
  3. Check all class tabs — all show "No roster"
  4. Go to Home — shows 11 Total Students
- **Screen:** admin/attendance/daily (Students tab)
- **Screenshot:** /tmp/qa_screenshots/manual_auto_att_students.png
- **Status:** Reported to Slack

### BUG-041: People Directory > Teachers tab is empty despite Home showing 9 Total Teachers
- **Severity:** High
- **Category:** Data — Teacher list missing
- **Expected:** Teachers tab should show all teachers (9 according to Home screen)
- **Actual:** The People Directory > Teachers tab is completely empty — no teacher cards shown. The Home screen shows "9 Total Teachers" and "9 active faculty". The Faculty tab in Daily Attendance shows 9 teachers (T3-T8, T20, Asha Verma, Luttan). Data inconsistency.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab > Teachers
  3. Observe: Empty list, no teachers shown
  4. Go to Home — shows 9 Total Teachers
- **Screen:** admin/people/teachers
- **Screenshot:** /tmp/qa_screenshots/manual_auto_people_teachers.png
- **Status:** Reported to Slack

### BUG-042: People Directory > Students tab shows only 2 students despite Home showing 11 Total Students
- **Severity:** High
- **Category:** Data — Student count mismatch
- **Expected:** Students tab should show all 11 students as indicated on Home screen
- **Actual:** The People Directory > Students tab shows only 2 students (C/C2 in Class 1-A, D/Dd in Class 10-A). The Home screen shows "11 Total Students". Major data inconsistency — 9 students are missing from the directory.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab > Students
  3. Observe: Only 2 students shown
  4. Go to Home — shows 11 Total Students
- **Screen:** admin/people/students
- **Screenshot:** /tmp/qa_screenshots/manual_auto_people_teachers.png
- **Status:** Reported to Slack

### BUG-043: Settings screen — "ID Cards" text element has zero-size bounds
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** All settings menu items should be properly rendered with correct bounds
- **Actual:** On the Settings screen, the "ID Cards" text element has bounds `[0,0][0,0]` — a zero-size element that is not visible. The ID Cards menu item may not be rendering correctly in the settings list, though it is still accessible by scrolling down to where it should appear.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap Settings tab
  3. Observe UI dump: "ID Cards" text has bounds [0,0][0,0]
- **Screen:** admin/settings
- **Screenshot:** /tmp/qa_screenshots/manual_auto_settings.png
- **Status:** Reported to Slack

### BUG-044: Comms Hub — announcement text is truncated
- **Severity:** Medium
- **Category:** UI — Text truncation
- **Expected:** Announcement card text should be fully visible or properly truncated with ellipsis
- **Actual:** On the Comms Hub > Announcements tab, announcement text is truncated mid-word: "New Quiz: Social Studies Quiz — The Rise of Nationalism in Europe (History), French Revoluti" — cut off at "Revoluti" instead of properly truncating with "..." or wrapping to multiple lines.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap Comms tab > Announcements
  3. Observe: Quiz announcement text is cut off mid-word
- **Screen:** admin/comms/announcements
- **Screenshot:** /tmp/qa_screenshots/manual_auto_comms.png
- **Status:** Reported to Slack

### BUG-045: Comms Hub — "Notifications" sub-tab label is clipped
- **Severity:** Low
- **Category:** UI — Label clipping
- **Expected:** All sub-tab labels should be fully visible
- **Actual:** On the Comms Hub, the "Notifications" sub-tab label is clipped to only 37px wide (bounds=[980,336][1017,379]). Only the first letter or two of "Notifications" is visible. The tab is barely tappable.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap Comms tab
  3. Observe: "Notifications" sub-tab label is clipped at the right edge
- **Screen:** admin/comms/hub
- **Screenshot:** /tmp/qa_screenshots/manual_auto_comms.png
- **Status:** Reported to Slack

### BUG-046: API returns 10 teachers but People Directory shows 0 — UI rendering bug
- **Severity:** High
- **Category:** Data — API/UI mismatch
- **Expected:** People Directory > Teachers tab should display all 10 teachers returned by the API
- **Actual:** `GET /api/v1/school/teachers` returns `success: true` with 10 teachers in `data.teachers`. But the People Directory > Teachers tab renders 0 teachers. The UI is not displaying the API response data. This is a UI rendering bug, not an API bug.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab > Teachers — empty list
  3. API check: GET /api/v1/school/teachers returns 10 teachers successfully
- **Screen:** admin/people/teachers
- **Screenshot:** /tmp/qa_screenshots/manual_auto_people_teachers.png
- **Status:** Reported to Slack

### BUG-047: API returns 10 students but People Directory shows only 2 — UI rendering bug
- **Severity:** High
- **Category:** Data — API/UI mismatch
- **Expected:** People Directory > Students tab should display all 10 students returned by the API
- **Actual:** `GET /api/v1/school/students` returns `success: true` with 10 students in `data.students`. But the People Directory > Students tab renders only 2 students. The UI is not displaying all API response data. This is a UI rendering bug, not an API bug.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab > Students — only 2 students shown
  3. API check: GET /api/v1/school/students returns 10 students successfully
- **Screen:** admin/people/students
- **Screenshot:** /tmp/qa_screenshots/manual_auto_people_teachers.png
- **Status:** Reported to Slack

### BUG-048: Attendance roster API endpoint missing — 404
- **Severity:** High
- **Category:** API — Missing endpoint
- **Expected:** `GET /api/v1/school/attendance/roster?class_name=10&section=A` should return the student roster for the specified class and section
- **Actual:** Server returns 404: "Endpoint not found: /api/v1/school/attendance/roster". This is likely why all class tabs show "No roster" in the Daily Attendance screen (BUG-040). The endpoint either doesn't exist or has a different path than what the client expects.
- **Steps to Reproduce:**
  1. Login as school admin
  2. API check: GET /api/v1/school/attendance/roster?class_name=10&section=A → 404
- **Screen:** admin/attendance/daily
- **Screenshot:** N/A (API test)
- **Status:** Reported to Slack

### BUG-049: Coverage API endpoint missing — 404
- **Severity:** Medium
- **Category:** API — Missing endpoint
- **Expected:** `GET /api/v1/school/coverage` should return syllabus coverage data
- **Actual:** Server returns 404: "Endpoint not found: /api/v1/school/coverage". The Records > Coverage tab shows "No coverage data yet" — this may be because the endpoint doesn't exist rather than because there's genuinely no data.
- **Steps to Reproduce:**
  1. Login as school admin
  2. API check: GET /api/v1/school/coverage → 404
- **Screen:** admin/records/coverage
- **Screenshot:** N/A (API test)
- **Status:** Reported to Slack

### BUG-050: App crashes/logout when tapping class filter in Subjects tab
- **Severity:** Critical
- **Category:** Functional — App crash/force logout
- **Expected:** Tapping a class filter chip (e.g., Class 10) in the Subjects tab should filter and display subjects for that class
- **Actual:** Tapping "Class 10" in the Classes & Subjects > Subjects tab caused the app to crash or force-logout. The app returned to the onboarding/login screen. This is a critical stability issue — a simple UI tap should never cause a session loss or crash.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Settings > Classes & subjects > Subjects tab
  3. Tap "Class 10" filter chip
  4. App crashes/returns to login screen
- **Screen:** admin/settings/classes/subjects
- **Screenshot:** /tmp/qa_screenshots/manual_auto_subjects.png
- **Status:** Reported to Slack

### BUG-051: Classes & Subjects — UKG class card missing "Edit" button
- **Severity:** Low
- **Category:** UI — Missing element
- **Expected:** All class cards should have both "Edit" and "×" (delete) action buttons
- **Actual:** In the Classes & Subjects > Classes tab, the UKG class card only has a "×" (delete) button but is missing the "Edit" button that all other class cards have (Class 10, 11, 9, LKG all have Edit). UKG cannot be edited from the UI.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Settings > Classes & subjects > Classes tab
  3. Scroll to UKG class card
  4. Observe: No "Edit" button, only "×" delete button
- **Screen:** admin/settings/classes
- **Screenshot:** /tmp/qa_screenshots/manual_auto_classes_subtabs_ui.xml
- **Status:** Reported to Slack

### BUG-052: Home screen — "Teacher Spotlight" element has zero-size bounds
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** All UI elements should have proper visible bounds
- **Actual:** On the Home screen, the "Teacher Spotlight" text element has bounds `[0,0][0,0]` — a zero-size invisible element. Similar to BUG-043 (ID Cards zero-size on Settings). This appears to be a pattern of elements not rendering correctly.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Observe UI dump on Home screen: "Teacher Spotlight" has bounds [0,0][0,0]
- **Screen:** admin/home
- **Screenshot:** /tmp/qa_screenshots/manual_auto_002.png
- **Status:** Reported to Slack

### BUG-053: Home "Announce" quick action opens Notifications instead of announcement creation
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** Tapping "Announce" on Home should open an announcement creation screen or the Announcements tab in Comms Hub
- **Actual:** Tapping "Announce" opens the Notifications overlay screen (showing "You're all caught up" / "No notifications yet"). This is the wrong screen — the user expects to create an announcement, not view notifications. The "Announce" button should navigate to announcement creation or the Comms Hub > Announcements tab.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap "Announce" quick action on Home
  3. Observe: Opens Notifications screen instead of announcement creation
- **Screen:** admin/home
- **Screenshot:** /tmp/qa_screenshots/manual_auto_announce.png
- **Status:** Reported to Slack

### BUG-054: Analytics screen — insight text severely clipped/narrow
- **Severity:** Medium
- **Category:** UI — Text clipping
- **Expected:** Insight card text should be fully visible with proper width
- **Actual:** On the Analytics screen, insight text elements are severely clipped:
  - "Attendance Peak" at bounds [465,334][503,377] — only 38px wide
  - "Class 10-A reached 99% attendance" at bounds [700,382][718,419] — only 18px wide
  - "Top Performer" at bounds [222,334][465,377] — reasonable but may be clipped
  - "Dr. Jenkins: 5.0 Engagement Rating" at bounds [222,382][700,419] — reasonable
  The insight cards have text squeezed into very narrow bounds, making them unreadable.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap "Pending Approvals" on Home (opens Analytics)
  3. Observe: Insight text is clipped and unreadable
- **Screen:** admin/analytics
- **Screenshot:** /tmp/qa_screenshots/manual_auto_pending.png
- **Status:** Reported to Slack

### BUG-055: Home "Pending Approvals" card opens Analytics instead of pending approvals list
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** Tapping "Pending Approvals" should open the pending link requests / approvals queue
- **Actual:** Tapping "Pending Approvals" on the Home screen opens the Analytics screen instead of the pending approvals list. The user expects to see pending parent-child link requests that need approval, not analytics charts.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap "Pending Approvals" card on Home
  3. Observe: Opens Analytics screen instead of approvals queue
- **Screen:** admin/home
- **Screenshot:** /tmp/qa_screenshots/manual_auto_pending.png
- **Status:** Reported to Slack

### BUG-056: Settings "Public profile" link opens Chrome browser to facebook.com
- **Severity:** High
- **Category:** Functional — Incorrect external navigation
- **Expected:** Tapping "Public profile" in Settings should open the school's public profile editing screen within the app
- **Actual:** Tapping "Public profile" (at the top of the Institutional profile card in Settings) opened the Chrome browser to m.facebook.com. This is completely wrong — no part of the settings should navigate to Facebook. The "Public profile" text appears to be a link that incorrectly triggers an external browser intent to Facebook instead of navigating to the in-app profile screen. The Institutional Profile screen IS accessible by tapping the "Institutional profile" card itself, but the "Public profile" sub-link is broken.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap Settings tab
  3. Tap "Public profile" text (below the Institutional profile heading)
  4. Observe: Chrome opens to m.facebook.com
- **Screen:** admin/settings
- **Screenshot:** N/A (Chrome browser opened)
- **Status:** Reported to Slack

### BUG-057: Institutional Profile — "Medium" label has near-zero height (2px)
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** All form field labels should have proper visible height
- **Actual:** On the Institutional Profile screen, the "Medium" label has bounds `[176,922][216,924]` — only 2px tall. The label is essentially invisible. This is similar to the zero-size element pattern seen in BUG-043 and BUG-052.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Settings > Institutional Profile
  3. Observe UI dump: "Medium" label has height of only 2px
- **Screen:** admin/settings/profile
- **Screenshot:** /tmp/qa_screenshots/manual_auto_public_profile.png
- **Status:** Reported to Slack

---

## Teacher Role Testing (t21@gmail.com — Asha Verma)

### BUG-058: Teacher Home — "PREFERENCES" section header has zero-size bounds
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** Section headers should have proper visible bounds
- **Actual:** On the Teacher Home screen, the "PREFERENCES" text element has bounds `[0,0][0,0]` — zero-size invisible element. This is the 4th instance of the zero-size element pattern (also seen in BUG-043, BUG-052, BUG-057).
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Observe UI dump on Home screen: "PREFERENCES" has bounds [0,0][0,0]
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_home.png
- **Status:** Reported to Slack

### BUG-059: Teacher Home — greeting text "your account" split across two lines awkwardly
- **Severity:** Low
- **Category:** UI — Text layout
- **Expected:** Greeting text should flow naturally on one line or break at a sensible point
- **Actual:** The greeting text on Teacher Home reads "Hi Asha" then "your" on one line and "account" on the next line (bounds: "your"=[53,316][191,397], "account"=[209,316][450,397]). This appears to be a welcome message like "Welcome to your account" but the text is awkwardly split, making it look like a broken sentence. The word "your" ends at x=191 and "account" starts at x=209 — they're on the same Y line but with a gap, suggesting they're separate text elements rather than one flowing sentence.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Observe Home screen greeting area below "Hi Asha"
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_home.png
- **Status:** Reported to Slack

### BUG-060: Teacher Home — "Password" label in Security section severely clipped (5px wide)
- **Severity:** Medium
- **Category:** UI — Text clipping
- **Expected:** The "Password" label should be fully visible
- **Actual:** In the Security section of Teacher Home, the "Password" label has bounds `[235,2232][240,2279]` — only 5px wide. The text is completely unreadable. Only the "P" of "Password" might be visible. The "Change your sign-in password" subtext below it is visible.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Scroll to Security section on Home
  3. Observe: "Password" label is clipped to 5px width
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_home.png
- **Status:** Reported to Slack

### BUG-061: Teacher login — Google security warning dialog "Change your password" appears on first login
- **Severity:** Medium
- **Category:** UX — Unwanted security dialog
- **Expected:** Teacher should be able to log in without a security warning interrupting the flow, unless the password is genuinely compromised
- **Actual:** On first login as teacher (t21@gmail.com / 2345678), a dialog appears: "Change your password — The password that you just used was found in a data breach. Google recommends changing your password now." This is a Google/Android system dialog, not an app dialog. While this is a device-level security feature, it interrupts the login flow and may confuse users. The password "2345678" is indeed a weak password, but this should be handled by the app's own password policy, not by a system dialog.
- **Steps to Reproduce:**
  1. Force stop app and restart
  2. Login as teacher (t21@gmail.com / 2345678)
  3. Observe: Google security dialog appears after login
- **Screen:** teacher/login
- **Screenshot:** N/A (system dialog)
- **Status:** Reported to Slack

### BUG-062: Teacher Attendance — P/A/Late/Leave buttons non-functional (same as admin BUG-039)
- **Severity:** Critical
- **Category:** Functional — Non-functional buttons
- **Expected:** Tapping P/A/Late/Leave buttons should mark attendance and update the summary counts
- **Actual:** On the teacher Attendance screen (Class 10-A · Social Studies), tapping P/A/Late/Leave buttons for any student does nothing. The summary counts (5 Present, 0 Absent, 0 Late, 0 Leave) remain unchanged. "Mark all present" button also does nothing. This is the same issue as admin BUG-039 — attendance marking is completely non-functional across both roles.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Update tab > Attendance > Class 10-A · Social Studies
  3. Tap any P/A/Late/Leave button
  4. Observe: Nothing happens, counts don't change
- **Screen:** teacher/update/attendance
- **Screenshot:** /tmp/qa_screenshots/teacher_attendance.png
- **Status:** Reported to Slack

### BUG-063: Teacher Marks — "{max}" template variable not interpolated in test list
- **Severity:** Medium
- **Category:** UI — Uninterpolated template variable
- **Expected:** Test list should show "Max 100 · 14 Jul · Entered 0 of 5" (with actual max value)
- **Actual:** In the Marks tab, the test item shows "Max {max} · 14 Jul · Entered 0 of 5" — the `{max}` template variable is displayed literally instead of being replaced with the actual maximum marks value. When you tap into the test, the detail view correctly shows "Max 100", so the data is available but not interpolated in the list view.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Update tab > Marks
  3. Observe: Test list shows "Max {max}" instead of "Max 100"
- **Screen:** teacher/update/marks
- **Screenshot:** /tmp/qa_screenshots/teacher_marks.png
- **Status:** Reported to Slack

### BUG-064: Teacher Timetable — "Room {room}" template variable not interpolated
- **Severity:** Medium
- **Category:** UI — Uninterpolated template variable
- **Expected:** Timetable entries should show the actual room number (e.g., "Room 12" or "Room TBD")
- **Actual:** In the Timetable tab, all class entries show "Room {room}" — the `{room}` template variable is displayed literally instead of being replaced with the actual room value. This appears on all schedule entries.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Timetable tab
  3. Observe: All class entries show "Room {room}" literally
- **Screen:** teacher/timetable
- **Screenshot:** /tmp/qa_screenshots/teacher_timetable.png
- **Status:** Reported to Slack

### BUG-065: Teacher Attendance/Marks — student names show placeholder/system IDs instead of real names
- **Severity:** High
- **Category:** Data — Invalid student data
- **Expected:** Student names should display actual student names
- **Actual:** In the teacher Attendance and Marks screens, student names are displayed as:
  - "Dd" (appears twice, both with Roll 1 — duplicate)
  - "Gaurav" (correct)
  - "S10A-1" (looks like a system-generated ID, not a name)
  - "S10A-2" (same — system ID)
  Two students have the same name "Dd" and same roll number "Roll 1". "S10A-1" and "S10A-2" appear to be auto-generated student IDs being displayed as names.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Update tab > Attendance > Class 10-A · Social Studies
  3. Observe: Student list shows "Dd", "Dd", "Gaurav", "S10A-1", "S10A-2"
- **Screen:** teacher/update/attendance
- **Screenshot:** /tmp/qa_screenshots/teacher_attendance.png
- **Status:** Reported to Slack

### BUG-066: Teacher Update tab — "Class 11-C" text clipped to 2px wide, "Class 9-A" zero-size
- **Severity:** Medium
- **Category:** UI — Text clipping / invisible element
- **Expected:** All class names should be fully visible in the class list
- **Actual:** In the Update tab class list:
  - "Class 11-C" has bounds [238,2188][240,2235] — only 2px wide, completely unreadable
  - "Class 9-A" has bounds [0,0][0,0] — zero-size, invisible
  These are at the bottom of the scrollable list, suggesting a rendering issue with off-screen items.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Update tab
  3. Scroll to bottom of class list
  4. Observe: Class 11-C is clipped, Class 9-A is invisible
- **Screen:** teacher/update
- **Screenshot:** /tmp/qa_screenshots/teacher_update.png
- **Status:** Reported to Slack

### BUG-067: Teacher Classes tab — third class card has zero-size elements at bottom of scroll
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** All class cards should render fully with visible avatar and subject name
- **Actual:** On the Classes tab, the third class card (Class 10-A Mathematics) has:
  - "C" avatar with bounds [0,0][0,0] — zero-size
  - "Mathematics" text with bounds [0,0][0,0] — zero-size
  This is at the bottom of the visible scroll area, same pattern as BUG-066.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Classes tab
  3. Observe bottom of visible list: third class card has invisible avatar and subject
- **Screen:** teacher/classes
- **Screenshot:** /tmp/qa_screenshots/teacher_classes.png
- **Status:** Reported to Slack

### BUG-068: Teacher Profile tab is identical to Home screen — redundant navigation
- **Severity:** Low
- **Category:** UX — Redundant screen
- **Expected:** Profile tab should show a profile-specific screen (e.g., edit profile, settings, preferences)
- **Actual:** The Profile tab displays the exact same content as the Home screen — same greeting, same avatar, same subjects/classes list, same Time Off section, same Security section. The UI dump is identical. This is redundant navigation — the Profile tab serves no unique purpose.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Home tab — note content
  3. Tap Profile tab — observe: identical content to Home
- **Screen:** teacher/profile
- **Screenshot:** /tmp/qa_screenshots/teacher_profile.png
- **Status:** Reported to Slack

### BUG-069: Teacher Update tab — multiple classes show "0 students" for Mathematics
- **Severity:** Medium
- **Category:** Data — Missing student data
- **Expected:** Classes should show actual student counts if students are enrolled
- **Actual:** In the Update tab, several Mathematics classes show "0 students":
  - Class 10-B · Mathematics · 0 students
  - Class 10-D · Mathematics · 0 students
  - Class 11-A · Mathematics · 0 students
  - Class 11-C · Mathematics · 0 students
  But Class 10-A · Mathematics shows 5 students. This may indicate missing enrollment data for these classes, or a data sync issue.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Update tab
  3. Observe: Multiple Mathematics classes show 0 students
- **Screen:** teacher/update
- **Screenshot:** /tmp/qa_screenshots/teacher_update.png
- **Status:** Reported to Slack

### BUG-070: Teacher Home — "Class Class 10-A" double prefix in NOW TEACHING section
- **Severity:** Medium
- **Category:** UI — Text formatting
- **Expected:** Class name should display as "Class 10-A" (single prefix)
- **Actual:** In the NOW TEACHING section of the teacher Home screen, the class name displays as "Class Class 10-A" — the word "Class" appears twice. This is a string formatting bug where the class label prefix is being added twice.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Observe Home screen NOW TEACHING section
  3. "Class Class 10-A" is displayed with double "Class" prefix
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_leave_apply.png
- **Status:** Reported to Slack

### BUG-071: Teacher Home — "Needs Attention" section has zero-size bounds for all content
- **Severity:** Medium
- **Category:** UI — Invisible element
- **Expected:** The Needs Attention section should display warning text and a View Insights button
- **Actual:** On the teacher Home screen, the "Needs Attention" section has:
  - "2 at-risk students in Class 10-A" — bounds [0,0][0,0]
  - "Attendance below 75%. Review and notify parents." — bounds [0,0][0,0]
  - "View Insights" button — bounds [0,0][0,0]
  All content in this section is invisible. This is the 5th+ instance of the zero-size element pattern.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Scroll to Needs Attention section on Home
  3. Observe: All text and buttons in this section have zero-size bounds
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_leave_apply.png
- **Status:** Reported to Slack

### BUG-072: Teacher Notifications — timestamp shown in raw ISO format instead of human-readable
- **Severity:** Low
- **Category:** UI — Data formatting
- **Expected:** Notification timestamps should be displayed in a human-readable format (e.g., "Jul 14, 6:50 PM")
- **Actual:** In the teacher Notifications/Messages screen, the notification timestamp is displayed as raw ISO 8601: "2026-07-14T18:50:24.964017Z". This is not user-friendly and includes microseconds and timezone suffix that are meaningless to users.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Messages icon (top right)
  3. Observe: Notification timestamp shows "2026-07-14T18:50:24.964017Z"
- **Screen:** teacher/notifications
- **Screenshot:** /tmp/qa_screenshots/teacher_messages.png
- **Status:** Reported to Slack

### BUG-073: Teacher Home — "Apply" button for Time Off/leave doesn't open leave application form
- **Severity:** Medium
- **Category:** Functional — Non-functional button
- **Expected:** Tapping "Apply" next to "My leave" should open a leave application form
- **Actual:** Tapping the "Apply" button in the Time Off section does not open a leave application form. Instead, the Home screen changed to a different view (NOW TEACHING section appeared). The Apply button either has no action or triggers an unexpected navigation. There is already an approved leave (3 Jul – 15 Jul, "ja rhi hun") shown, so the section is partially functional, but the Apply button itself doesn't work as expected.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Scroll to Time Off section on Home
  3. Tap "Apply" button
  4. Observe: No leave application form appears
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_leave_apply.png
- **Status:** Reported to Slack

### BUG-074: Teacher Home — "Lesson Plan" button opens check-in dialog instead of lesson plan
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** Tapping "Lesson Plan" should open a lesson planning screen for the current class
- **Actual:** Tapping "Lesson Plan" in the NOW TEACHING section opens a "Check in for today" dialog ("Confirm your arrival to mark yourself present at school") instead of a lesson plan screen. This is completely unrelated functionality.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. On Home screen NOW TEACHING section, tap "Lesson Plan"
  3. Observe: Check-in dialog appears instead of lesson plan
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_lesson_plan.png
- **Status:** Reported to Slack

### BUG-075: Teacher Home — "Results" / "1 to publish" navigates to Update tab instead of results/publishing screen
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** Tapping "Results" or "1 to publish" in Pending Actions should open a results publishing screen
- **Actual:** Tapping "Results" navigates to the Update tab (Attendance/Marks/Homework class list) instead of a results publishing screen. The user expects to see pending results to publish, not a class selection list.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. On Home screen, scroll to Pending Actions > Results
  3. Tap "Results" or "1 to publish"
  4. Observe: Opens Update tab class list, not results publishing
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_results.png
- **Status:** Reported to Slack

### BUG-076: Teacher Attendance — "Insights" button opens marks entry instead of attendance insights
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** Tapping "Insights" on the Attendance screen should show attendance analytics/insights for the class
- **Actual:** Tapping "Insights" on the Attendance screen opens the Marks entry detail for "unit test 2" instead of attendance insights. This is completely wrong navigation — it crosses from attendance to marks.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Update tab > Attendance > Class 10-A
  3. Tap "Insights" button
  4. Observe: Opens marks entry for unit test 2 instead of attendance insights
- **Screen:** teacher/update/attendance
- **Screenshot:** /tmp/qa_screenshots/teacher_insights.png
- **Status:** Reported to Slack

### BUG-077: Teacher — "Check in for today" dialog appears repeatedly and unexpectedly
- **Severity:** Low
- **Category:** UX — Annoying repeated dialog
- **Expected:** The check-in dialog should appear once per day or be dismissible without reappearing
- **Actual:** The "Check in for today" dialog keeps appearing during navigation — it appeared when tapping "Lesson Plan", when pressing back from marks entry, and at other unexpected times. Even after tapping "Later" to dismiss it, it reappears on subsequent navigation actions. This is disruptive to the user experience.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Navigate between screens (Home, Update, back)
  3. Observe: Check-in dialog appears multiple times unexpectedly
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_lesson_plan.png
- **Status:** Reported to Slack

### BUG-078: Teacher Class Detail — "{count}" template variable not interpolated in homework
- **Severity:** Medium
- **Category:** UI — Uninterpolated template variable
- **Expected:** Homework card should show "0 turned in · Due 14 Jul" (with actual count)
- **Actual:** In the Class Detail screen, the active homework card shows "{count} turned in · Due 14 Jul" — the `{count}` template variable is displayed literally. This is the 3rd template variable bug (after {max} in BUG-063 and {room} in BUG-064).
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap a class card
  3. Observe: Homework section shows "{count} turned in"
- **Screen:** teacher/classes/detail
- **Screenshot:** /tmp/qa_screenshots/teacher_class_detail2.png
- **Status:** Reported to Slack

### BUG-079: Teacher Class Detail — "MARKS_PENDING" label has underscore instead of space
- **Severity:** Low
- **Category:** UI — Text formatting
- **Expected:** Label should display as "MARKS PENDING" (with space)
- **Actual:** In the Class Detail screen, the test status badge shows "MARKS_PENDING" with an underscore instead of a space. Likely an enum or constant name being displayed directly without proper formatting.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap a class card
  3. Observe: Test status shows "MARKS_PENDING" with underscore
- **Screen:** teacher/classes/detail
- **Screenshot:** /tmp/qa_screenshots/teacher_class_mark_att.png
- **Status:** Reported to Slack

### BUG-080: Teacher Gamification — Leaderboard shows "Student #hexid" instead of student names
- **Severity:** High
- **Category:** Data — Invalid display data
- **Expected:** Leaderboard should show actual student names
- **Actual:** The Class Gamification leaderboard displays student names as "Student #ecaead", "Student #0b00e8", "Student #39d3f1", "Student #5b4664", "Student #a4daa8" — all showing hex IDs instead of real names.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap a class card
  3. Scroll to CLASS GAMIFICATION section
  4. Observe: Leaderboard shows "Student #hexid" instead of names
- **Screen:** teacher/classes/detail/gamification
- **Screenshot:** /tmp/qa_screenshots/teacher_pep_talk.png
- **Status:** Reported to Slack

### BUG-081: Teacher Gamification — "Send Pep Talk" and "Assign Mentor" both open Leaderboard
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** "Send Pep Talk" should open a pep talk composition screen; "Assign Mentor" should open a mentor assignment screen
- **Actual:** Both "Send Pep Talk" and "Assign Mentor" buttons open the same Class Leaderboard view. Neither button performs its intended action.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap a class card > scroll to CLASS GAMIFICATION
  3. Tap "Send Pep Talk" — opens Leaderboard
  4. Tap "Assign Mentor" — also opens Leaderboard
- **Screen:** teacher/classes/detail/gamification
- **Screenshot:** /tmp/qa_screenshots/teacher_pep_talk.png
- **Status:** Reported to Slack

### BUG-082: Teacher Gamification — "Create Class Goal" button does nothing
- **Severity:** Medium
- **Category:** Functional — Non-functional button
- **Expected:** Tapping "Create Class Goal" should open a goal creation form
- **Actual:** Tapping "Create Class Goal" has no visible effect — no screen opens, no dialog appears, no error shown.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap class card > scroll to CLASS GAMIFICATION
  3. Tap "Create Class Goal"
  4. Observe: Nothing happens
- **Screen:** teacher/classes/detail/gamification
- **Screenshot:** /tmp/qa_screenshots/teacher_class_goal.png
- **Status:** Reported to Slack

### BUG-083: Teacher Gamification — "Pair Study Buddies" dialog has zero-size "Student 1 ID" field
- **Severity:** Medium
- **Category:** UI — Invisible form field
- **Expected:** The pair study buddies dialog should show student selection fields with proper visible bounds
- **Actual:** The "Pair Study Buddies" dialog opens with a "Cancel" button and "Student 1 ID" text, but "Student 1 ID" has bounds [0,0][0,0] — zero-size, invisible. The dialog appears to be an incomplete/placeholder form.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap class card > scroll to CLASS GAMIFICATION
  3. Tap "Pair Study Buddies"
  4. Observe: Dialog has invisible "Student 1 ID" field
- **Screen:** teacher/classes/detail/gamification
- **Screenshot:** /tmp/qa_screenshots/teacher_study_buddies.png
- **Status:** Reported to Slack

### BUG-084: Teacher Marks — "Publish & notify parents" button non-functional
- **Severity:** High
- **Category:** Functional — Non-functional button
- **Expected:** Tapping "Publish & notify parents" should publish marks and send notifications to parents
- **Actual:** After saving marks (button changed to "Saved (not published)"), tapping "Publish & notify parents" does nothing — no confirmation dialog, no error, no state change. Marks cannot be published.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Update tab > Marks > Class 10-A > unit test 2
  3. Tap "Save marks" (works — shows "Saved (not published)")
  4. Tap "Publish & notify parents"
  5. Observe: Nothing happens
- **Screen:** teacher/update/marks/entry
- **Screenshot:** /tmp/qa_screenshots/teacher_publish.png
- **Status:** Reported to Slack

### BUG-085: Teacher Home — "Reports" quick action navigates to Home instead of reports screen
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** Tapping "Reports" quick action should open a reports screen or export options
- **Actual:** Tapping "Reports" in the Quick actions section on Home navigates back to the Home screen (top) instead of opening a reports screen.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Scroll down to Quick actions section
  3. Tap "Reports"
  4. Observe: Goes to top of Home screen, no reports screen
- **Screen:** teacher/home
- **Screenshot:** /tmp/qa_screenshots/teacher_reports_qa.png
- **Status:** Reported to Slack

### BUG-086: Teacher Notifications — "Notification preferences" link non-functional
- **Severity:** Medium
- **Category:** Functional — Non-functional button
- **Expected:** Tapping "Notification preferences" should open a notification settings/preferences screen
- **Actual:** Tapping "Notification preferences" on the Notifications screen does nothing — stays on the same notifications screen.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Tap Messages icon (top right)
  3. Tap "Notification preferences" link
  4. Observe: Nothing happens, stays on notifications screen
- **Screen:** teacher/notifications
- **Screenshot:** /tmp/qa_screenshots/teacher_notif_prefs2.png
- **Status:** Reported to Slack

### BUG-087: Teacher Profile — "Cream & lavender" theme description clipped to 5px wide
- **Severity:** Low
- **Category:** UI — Text clipping
- **Expected:** Theme description text should be fully visible
- **Actual:** In the Profile > Appearance section, the "Cream & lavender" theme description has bounds [416,2165][421,2205] — only 5px wide. The text is completely unreadable.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Profile tab > scroll to Appearance section
  3. Observe: "Cream & lavender" text is clipped to 5px
- **Screen:** teacher/profile/appearance
- **Screenshot:** /tmp/qa_screenshots/teacher_change_pwd2.png
- **Status:** Reported to Slack

### BUG-088: Teacher Profile — "WCAG AAA accessibility" text has zero-size bounds
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** Theme description should be fully visible
- **Actual:** In the Profile > Appearance section, the "WCAG AAA accessibility" description for the High Contrast theme has bounds [0,0][0,0] — zero-size, invisible.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Profile tab > scroll to Appearance section
  3. Observe: "WCAG AAA accessibility" text is invisible
- **Screen:** teacher/profile/appearance
- **Screenshot:** /tmp/qa_screenshots/teacher_change_pwd2.png
- **Status:** Reported to Slack

### BUG-089: Teacher Profile — Tamil language option has zero-size bounds
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** All language options should be visible and selectable
- **Actual:** In the Profile > Language section, the Tamil language option (TA, தமிழ், Tamil) has all three text elements with bounds [0,0][0,0] — zero-size, invisible.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Profile tab > scroll to Language section
  3. Observe: Tamil option is invisible
- **Screen:** teacher/profile/language
- **Screenshot:** /tmp/qa_screenshots/teacher_profile_bottom_ui.xml
- **Status:** Reported to Slack

### BUG-090: Teacher Timetable — All 6 days (Mon–Sat) show identical schedule with same single class
- **Severity:** Medium
- **Category:** Data — Invalid schedule data
- **Expected:** Different days should show different class schedules with varied subjects/times
- **Actual:** All 6 day tabs (Mon, Tue, Wed, Thu, Fri, Sat) show the exact same schedule: Class 10-A, Mathematics, 08:00–08:40, Room {room}. This is either a data issue (same schedule duplicated for all days) or a UI rendering issue (not switching data when changing tabs). Combined with the {room} bug (BUG-064), every day shows "Room {room}".
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Timetable tab > Schedule sub-tab
  3. Tap each day tab (Mon through Sat)
  4. Observe: All days show identical Class 10-A Mathematics 08:00–08:40
- **Screen:** teacher/timetable/schedule
- **Screenshot:** /tmp/qa_screenshots/teacher_tt_wed.png
- **Status:** Reported to Slack

### BUG-091: Teacher Student Detail — "Assign Quest" opens "Cancel Badge" form instead
- **Severity:** Medium
- **Category:** Functional — Misnavigation
- **Expected:** Tapping "Assign Quest" should open a quest assignment form
- **Actual:** Tapping "Assign Quest" in the student detail gamification tools opens a "Cancel Badge" form — the Award Badge dialog instead of a quest assignment form. The button triggers the wrong action.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap class card > scroll to Students > tap a student
  3. Scroll to GAMIFICATION TOOLS
  4. Tap "Assign Quest"
  5. Observe: "Cancel Badge" form appears instead of quest assignment
- **Screen:** teacher/student/detail
- **Screenshot:** /tmp/qa_screenshots/teacher_assign_quest.png
- **Status:** Reported to Slack

### BUG-092: Teacher Student Detail — "Parent Alert" form has zero-size "Send Alert" button
- **Severity:** High
- **Category:** UI — Invisible element
- **Expected:** The "Send Alert" button should be visible and tappable
- **Actual:** In the Parent Alert form, the "Send Alert" button has bounds [0,0][0,0] — zero-size, invisible. The form has a text input ("Type a positive message to the parent...") and a "Cancel Alert" button, but the submit button is invisible. Users cannot send the parent alert.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap class > tap student > scroll to GAMIFICATION TOOLS
  3. Tap "Parent Alert"
  4. Observe: "Send Alert" button is invisible (zero-size bounds)
- **Screen:** teacher/student/detail
- **Screenshot:** /tmp/qa_screenshots/teacher_parent_alert.png
- **Status:** Reported to Slack

### BUG-093: Teacher Student Detail — "Encourage" button fails with API error
- **Severity:** Medium
- **Category:** Functional — API failure
- **Expected:** Tapping "Encourage" should send an encouragement to the student
- **Actual:** Tapping "Encourage" shows an error message "Failed to encourage student" — the API call fails. This indicates a backend endpoint issue or missing API implementation.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap class > tap student > scroll to GAMIFICATION TOOLS
  3. Tap "Encourage"
  4. Observe: Error message "Failed to encourage student"
- **Screen:** teacher/student/detail
- **Screenshot:** /tmp/qa_screenshots/teacher_encourage.png
- **Status:** Reported to Slack

### BUG-094: Teacher Student Detail — "Spotlight" button does nothing
- **Severity:** Medium
- **Category:** Functional — Non-functional button
- **Expected:** Tapping "Spotlight" should spotlight/highlight the student in some way
- **Actual:** Tapping "Spotlight" in the student detail gamification tools has no visible effect — no dialog, no navigation, no error, no confirmation. The button is completely non-functional.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Classes tab > tap class > tap student > scroll to GAMIFICATION TOOLS
  3. Tap "Spotlight"
  4. Observe: Nothing happens
- **Screen:** teacher/student/detail
- **Screenshot:** /tmp/qa_screenshots/teacher_spotlight.png
- **Status:** Reported to Slack

### BUG-095: Teacher Homework Detail — Last student (S10A-2) has zero-size bounds for all fields
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** All students in the homework submission list should be visible
- **Actual:** In the homework detail screen, the last student (S10A-2) has zero-size bounds for "Roll No" text [0,0][0,0] and "NOT SUBMITTED" status [0,0][0,0]. The student name "S10A-2" is visible but the roll number and submission status are invisible. Another instance of the zero-size element pattern.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Update tab > Homework > Class 10-A > tap homework item
  3. Scroll to bottom of student list
  4. Observe: Last student's Roll No and NOT SUBMITTED status are invisible
- **Screen:** teacher/update/homework/detail
- **Screenshot:** /tmp/qa_screenshots/teacher_hw_detail2.png
- **Status:** Reported to Slack

### BUG-096: Teacher Attendance — "Mark all present" button non-functional
- **Severity:** High
- **Category:** Functional — Non-functional button
- **Expected:** Tapping "Mark all present" should mark all students as present and update the attendance counts
- **Actual:** Tapping "Mark all present" does nothing — attendance counts remain unchanged (5/0/0/0), no toast, no error, no confirmation. The button is completely non-functional, same as the individual P/A/Late/Leave buttons (BUG-062).
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Update tab > Attendance > Class 10-A
  3. Tap "Mark all present"
  4. Observe: Nothing happens, counts stay 5/0/0/0
- **Screen:** teacher/update/attendance
- **Screenshot:** /tmp/qa_screenshots/teacher_att_after_map.png
- **Status:** Reported to Slack

### BUG-097: Teacher Profile — "Update password" button no validation on empty fields
- **Severity:** Medium
- **Category:** Functional — Missing validation
- **Expected:** Tapping "Update password" with empty fields should show validation errors (e.g., "Current password required", "New password required")
- **Actual:** Tapping "Update password" with all fields empty does nothing — no validation error, no toast, no feedback. The form just stays as-is or collapses. Users get no indication that their input is required.
- **Steps to Reproduce:**
  1. Login as teacher (t21@gmail.com)
  2. Profile tab > scroll to SECURITY > tap "Change your sign-in password"
  3. Tap "Update password" without entering any text
  4. Observe: No validation error, no feedback
- **Screen:** teacher/profile/security
- **Screenshot:** /tmp/qa_screenshots/teacher_pwd_update.png

## Batch 10: Fee & Salary Management — Discovered 2026-07-15 (Admin role, latest pull)

### BUG-098: "Add Fee Structure" button hidden by empty state — users can never create first fee structure
- **Severity:** Critical
- **Category:** Functional — Button unreachable
- **Expected:** When no fee structures exist, the "Add Fee Structure" button should be visible so users can create one
- **Actual:** The "Add Fee Structure" button is inside `VStateHost` content block (line 141 of FeeSalaryManagementScreen.kt). When `structures.isEmpty()`, `VStateHost` shows the empty state ("No Fee Structures" / "Add a fee structure to start collecting fees from students.") which REPLACES the content containing the button. Users can never add the first fee structure — chicken-and-egg problem.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Settings > Fee & Salary > Fees tab > Structure sub-tab
  3. Observe: "No Fee Structures" empty state with no add button
- **Screen:** admin/settings/fee-salary/structure
- **Screenshot:** /tmp/qa_screenshots/god_fee_salary2.png
- **Source:** `FeeSalaryManagementScreen.kt:120-155` — `VButton` at line 141 is inside `VStateHost` content lambda

### BUG-099: "Add Salary Record" button hidden by empty state — users can never create first salary record
- **Severity:** Critical
- **Category:** Functional — Button unreachable
- **Expected:** When no salary records exist, the "Add Salary Record" button should be visible so users can create one
- **Actual:** Same issue as BUG-098. The "Add Salary Record" button (line 542) is inside `VStateHost` content, which is replaced by empty state when `salaryRecords.isEmpty()`. Users can never add the first salary record.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Settings > Fee & Salary > Salary tab
  3. Observe: "No Salary Records" empty state with no add button
- **Screen:** admin/settings/fee-salary/salary
- **Screenshot:** /tmp/qa_screenshots/god_salary.png
- **Source:** `FeeSalaryManagementScreen.kt:521-556` — `VButton` at line 542 is inside `VStateHost` content lambda

### BUG-100: Add/Generate sheets are plain Columns, not Dialogs — content renders with zero size
- **Severity:** Critical
- **Category:** UI — Broken dialog/sheet rendering
- **Expected:** `AddFeeStructureSheet`, `GenerateFeesSheet`, and `AddSalarySheet` should render as modal dialogs or bottom sheets overlaying the screen
- **Actual:** All three are plain `Column(Modifier.fillMaxWidth().padding(16.dp))` composables — NOT wrapped in `Dialog` or `ModalBottomSheet`. When `showAddDialog`/`showGenerateDialog` is true, the sheet content renders inline with zero-size bounds. E.g., tapping "Generate Monthly Fees" produces a "2026-07" text element with bounds `[0,0][0,0]` — the dialog content is not visible.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Settings > Fee & Salary > Fees > Payments
  3. Tap "Generate Monthly Fees"
  4. Observe: No dialog appears, "2026-07" text appears with zero-size bounds in UI dump
- **Screen:** admin/settings/fee-salary/payments
- **Screenshot:** /tmp/qa_screenshots/god_generate_dialog.png
- **Source:** `FeeSalaryManagementScreen.kt:224-272` (AddFeeStructureSheet), `412-446` (GenerateFeesSheet), `640-706` (AddSalarySheet) — all plain Column, no Dialog wrapper

### BUG-101: "NO_FEES" status displayed raw with underscores in Payments
- **Severity:** Medium
- **Category:** UI — Unformatted status text
- **Expected:** Fee status should display as human-readable text (e.g., "No Fees")
- **Actual:** Student payment card shows "NO_FEES" as badge text with underscore format. The server returns `status = "NO_FEES"` (FeeSalaryRouting.kt:564) and the client renders it directly via `VBadge(text = student.status, ...)` without any formatting or mapping.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Settings > Fee & Salary > Fees > Payments
  3. Observe: Student "Gaurav" shows "NO_FEES" badge
- **Screen:** admin/settings/fee-salary/payments
- **Screenshot:** /tmp/qa_screenshots/god_fee_payments.png
- **Source:** `FeeSalaryManagementScreen.kt:380` — `VBadge(text = student.status, ...)` passes raw enum value

### BUG-102: Reminder Day Save button disabled silently — no inline validation feedback
- **Severity:** Medium
- **Category:** UX — Missing validation feedback
- **Expected:** When user enters an invalid day (e.g., 35, 0, or empty), the UI should show why Save is disabled (e.g., "Day must be between 1 and 28")
- **Actual:** The Save button has `enabled = reminderDay.toIntOrNull()?.let { it in 1..28 } ?: false` (line 503), so it becomes disabled for invalid values. However, there is NO inline error message or hint explaining why. Additionally, the previous "Reminder config updated" success message persists, making it appear as though the invalid value was saved successfully.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Settings > Fee & Salary > Fees > Reminders
  3. Change day to 10, tap Save → "Reminder config updated" appears
  4. Change day to 35 → Save button becomes disabled but "Reminder config updated" message persists
  5. User sees "35" in field + "Reminder config updated" → thinks 35 was saved
- **Screen:** admin/settings/fee-salary/reminders
- **Screenshot:** /tmp/qa_screenshots/god_reminder_invalid_ui.xml
- **Source:** `FeeSalaryManagementScreen.kt:496-511` — no error message for disabled state, `actionMessage` not cleared on failed validation

### BUG-103: Add Salary Sheet asks for raw Teacher UUID — no teacher picker
- **Severity:** High
- **Category:** UX — Unusable input
- **Expected:** Salary record creation should present a dropdown/picker of teachers from the school
- **Actual:** The `AddSalarySheet` has a text input with label "Teacher ID" and placeholder "Paste teacher UUID". No normal user knows a teacher's UUID. The school has 11 teachers but there's no way to select one from a list.
- **Steps to Reproduce:**
  1. (Hypothetical — button is hidden by BUG-099, but if visible)
  2. Tap "Add Salary Record"
  3. See "Teacher ID" field with "Paste teacher UUID" placeholder
  4. No teacher dropdown or picker available
- **Screen:** admin/settings/fee-salary/salary
- **Source:** `FeeSalaryManagementScreen.kt:657-662` — `VInput(label = "Teacher ID", placeholder = "Paste teacher UUID")`

### BUG-104: Student payment card not tappable — no detail view
- **Severity:** Low
- **Category:** UX — Missing interaction
- **Expected:** Tapping a student payment card should open a detail view showing individual fee items
- **Actual:** `FeeStudentCard` (line 360-408) is a plain `VCard` without `onClick`. Tapping the card does nothing. The only interactive element is the "Mark Paid" button which only appears when `dueAmount > 0`. For students with ₹0 due (like Gaurav with NO_FEES), there's no way to view fee details.
- **Steps to Reproduce:**
  1. Login as school admin (a1@gmail.com)
  2. Settings > Fee & Salary > Fees > Payments
  3. Tap on student "Gaurav" card
  4. Observe: Nothing happens
- **Screen:** admin/settings/fee-salary/payments
- **Source:** `FeeSalaryManagementScreen.kt:360-408` — `VCard` has no `onClick` parameter

### BUG-105: "Generate Monthly Fees" with no fee structures silently does nothing
- **Severity:** Medium
- **Category:** Functional — No feedback on no-op
- **Expected:** If no fee structures exist, "Generate Monthly Fees" should inform the user (e.g., "No fee structures configured. Add a fee structure first.")
- **Actual:** Tapping "Generate Monthly Fees" opens the generate sheet (if it rendered properly — see BUG-100). Even if the generate call is made, the server creates 0 fee records because there are no structures. No feedback is given to the user that nothing was generated.
- **Source:** `FeeSalaryRouting.kt:680-725` — iterates fee structures; if none exist, `generated = 0, skipped = 0` with no error
- **Status:** Reported to Slack

### BUG-106: Delete icon in FeeStructureCard is non-functional — onDelete callback never invoked
- **Severity:** High
- **Category:** Functional — Button not wired
- **Expected:** Tapping the delete (X) icon on a fee structure card should trigger the delete confirmation dialog
- **Actual:** `FeeStructureCard` accepts an `onDelete` callback parameter (line 183) but never calls it. The close icon (line 213-218) is a plain `Icon` with no `Modifier.clickable` — it's decorative only. Users can never delete a fee structure.
- **Steps to Reproduce:**
  1. (Hypothetical — requires a fee structure to exist)
  2. Navigate to Fee & Salary > Fees > Structure
  3. Tap the X icon on any fee structure card
  4. Observe: Nothing happens — no confirmation dialog
- **Screen:** admin/settings/fee-salary/structure
- **Source:** `FeeSalaryManagementScreen.kt:180-221` — `onDelete` declared but never used; Icon has no clickable modifier

### BUG-107: No edit functionality for fee structures — only create and delete
- **Severity:** Medium
- **Category:** Functional — Missing feature
- **Expected:** Users should be able to edit existing fee structures (title, amount, description, frequency, active status)
- **Actual:** The ViewModel has `updateFeeStructure()` (line 157) and the server has a PUT endpoint, but the UI has no edit button or edit flow. `FeeStructureCard` only shows a delete icon (which is also broken — BUG-106). There's no way to toggle a structure between active/inactive or change its amount.
- **Source:** `FeeSalaryManagementScreen.kt:180-221` — no edit UI; `FeeSalaryViewModel.kt:157-174` — `updateFeeStructure` exists but is never called from UI

### BUG-108: classId filter in fee students and generate fees compares child UUID against class UUID
- **Severity:** High
- **Category:** Backend — Logic error
- **Expected:** When a classId filter is provided, the query should filter students by their class/grade enrollment
- **Actual:** Both the fee students endpoint (line 536) and generate fees endpoint (line 679) filter `ChildrenTable.id eq classIdFilter` — comparing a child's UUID against a class UUID. This will never match, meaning class-filtered queries always return empty results. The `classIdFilter` is parsed from the `classId` query parameter which represents a class, not a child.
- **Source:** `FeeSalaryRouting.kt:536` — `ChildrenTable.id eq classIdFilter`; `FeeSalaryRouting.kt:679` — same pattern in generate fees

### BUG-109: Section filter parameter read but never applied in fee students query
- **Severity:** Medium
- **Category:** Backend — Dead code / missing filter
- **Expected:** The `section` query parameter should filter students by section
- **Actual:** `sectionFilter` is read from query parameters (line 528) but never used in the `ChildrenTable` query (lines 533-538). The parameter is accepted by the API client (`FeeSalaryApi.kt:115`) and sent to the server, but silently ignored.
- **Source:** `FeeSalaryRouting.kt:528` — `val sectionFilter = ...` declared but never referenced in query

### BUG-110: Salary POST endpoint doesn't verify teacher belongs to admin's school
- **Severity:** High
- **Category:** Security — Cross-tenant data access
- **Expected:** When creating a salary record, the server should verify that the specified teacher belongs to the same school as the admin
- **Actual:** The POST `/api/v1/school/salary` endpoint (line 864) checks `teacherId` is a valid UUID and looks up the teacher name from `AppUsersTable`, but never checks that the teacher's `school_id` matches `ctx.schoolId`. An admin from School A could create salary records for a teacher from School B by providing their UUID.
- **Source:** `FeeSalaryRouting.kt:864-874` — no school_id verification on teacher lookup

### BUG-111: Fee structure frequency displayed raw as "MONTHLY" in badge
- **Severity:** Low
- **Category:** UI — Unformatted text
- **Expected:** Frequency should display in human-readable format (e.g., "Monthly")
- **Actual:** `FeeStructureCard` shows `struct.frequency` directly as badge text (line 205), which displays "MONTHLY" in all-caps. Same pattern as BUG-101 with "NO_FEES".
- **Source:** `FeeSalaryManagementScreen.kt:205` — `VBadge(text = struct.frequency, ...)`

### BUG-112: Generated fee due dates hardcoded to 5th — ignores reminder day config
- **Severity:** Medium
- **Category:** Backend — Config not respected
- **Expected:** Generated fee records should use the school's configured reminder day as the due date
- **Actual:** The generate fees endpoint hardcodes `dueDate = "${req.month}-05"` (line 664), always setting the 5th of the month. The reminder config has a configurable day (we set it to 10), but this is completely ignored during fee generation.
- **Source:** `FeeSalaryRouting.kt:664` — `val dueDate = "${req.month}-05"` hardcoded

### BUG-113: No class selector in Add Fee Structure form — all structures are school-wide only
- **Severity:** Medium
- **Category:** UI — Missing feature
- **Expected:** The add fee structure form should allow selecting a specific class or "All Classes"
- **Actual:** `AddFeeStructureSheet.onCreate` always passes `classId = null` (line 161), meaning all fee structures are school-wide. The server supports class-specific structures (`FeeStructuresTable.classId`), but the UI provides no way to set it.
- **Source:** `FeeSalaryManagementScreen.kt:161` — `viewModel.createFeeStructure(title, amount, desc, null)`

### BUG-114: Fee students endpoint queries ChildrenTable instead of StudentsTable — only 1 of 11 students visible
- **Severity:** High
- **Category:** Backend — Wrong data source
- **Expected:** The fee payments tab should show all students from the school roster (StudentsTable — 11 students per dashboard)
- **Actual:** The fee students endpoint queries `ChildrenTable` (line 533), which only contains parent-linked child records. Only 1 student (Gaurav) appears because only 1 parent-child link has been approved. The other 10 students in the school roster are invisible in fee management.
- **Source:** `FeeSalaryRouting.kt:533` — `ChildrenTable.selectAll()` instead of `StudentsTable.selectAll()`

### BUG-115: No search bar or month picker on Payments tab
- **Severity:** Medium
- **Category:** UI — Missing controls
- **Expected:** The Payments tab should have a month picker (to view different months) and a search bar (to find students by name)
- **Actual:** The ViewModel has `selectedMonth`, `searchQuery`, `setMonth()`, and `setSearchQuery()` (lines 48-49, 231-238), and the API supports `month` and `search` parameters. But the UI has no month picker or search input — only the current month is shown with no way to change it.
- **Source:** `FeeSalaryManagementScreen.kt:276-358` — `PaymentTrackingSubTab` has no search field or month selector

### BUG-116: Salary record card shows raw "UNPAID" status in badge
- **Severity:** Low
- **Category:** UI — Unformatted text
- **Expected:** Salary status should display as "Unpaid" / "Paid" in human-readable format
- **Actual:** `SalaryRecordCard` shows `record.status` directly as badge text (line 606), displaying "UNPAID" or "PAID" in all-caps with underscore format. Same pattern as BUG-101 and BUG-111.
- **Source:** `FeeSalaryManagementScreen.kt:606` — `VBadge(text = record.status, ...)`

### BUG-117: Salary record card shows "Teacher" fallback when teacherName is blank
- **Severity:** Low
- **Category:** UI — Placeholder text shown to users
- **Expected:** If teacher name is unknown, show "Unknown Teacher" or resolve the name
- **Actual:** `SalaryRecordCard` uses `record.teacherName.ifBlank { "Teacher" }` (line 602), which displays the literal word "Teacher" as the name. This is a developer placeholder, not user-facing text.
- **Source:** `FeeSalaryManagementScreen.kt:602` — `record.teacherName.ifBlank { "Teacher" }`


---

## New Feature Testing — Academic Year, Classes & Subjects, Transport, Scholarship, ID Cards, Library, Gamification, Branding & Photos

### BUG-118: Academic Year — empty form submission causes full-screen error instead of inline validation
- **Severity:** High
- **Category:** UI — Poor error handling
- **Expected:** Tapping "Create & Activate" or "Save Draft" with an empty name field should show inline validation error on the name field
- **Actual:** Submitting an empty form causes a full-screen error message (server error), requiring the user to retry and lose context. No client-side validation before API call.
- **Steps to Reproduce:**
  1. Navigate to Settings > Academic Year
  2. Tap "New"
  3. Leave name blank, tap "Save Draft" or "Create & Activate"
  4. Observe full-screen error
- **Screen:** admin/settings/academic-year/new
- **Status:** Reported

### BUG-119: Classes & Subjects — duplicate "Mathematics" subject in Class 10 with different codes
- **Severity:** Low
- **Category:** Data — Duplicate entry
- **Expected:** Each subject should appear once per class
- **Actual:** Class 10 shows two "Mathematics" entries: one with code "MAT001" and another with code "MATH". This is confusing and may cause issues with grade calculations or timetable assignment.
- **Steps to Reproduce:**
  1. Navigate to Settings > Classes & Subjects
  2. Tap "Subjects" tab
  3. Tap "Class 10" filter chip
  4. Observe two "Mathematics" entries
- **Screen:** admin/settings/classes-subjects (Subjects tab, Class 10)
- **Status:** Reported

### BUG-120: Transport Management — all forms render inline instead of as modal dialogs/sheets
- **Severity:** Medium
- **Category:** UI — Missing modal wrapper
- **Expected:** Tapping "+ Add Route", "+ Add Vehicle", or "Assign" should open a modal bottom sheet or dialog
- **Actual:** All three forms (CreateRouteForm, CreateVehicleForm, CreateAssignmentForm) render as inline items in the LazyColumn, pushing existing content down. No scrim, no modal behavior. Same pattern as Fee & Salary sheets (BUG-100).
- **Source:** `TransportManagementScreenV2.kt:117-119` — `if (showRouteForm) { item { CreateRouteForm(...) } }` inside LazyColumn; same for vehicle (line 143-145) and assignment (line 169-171)
- **Screen:** admin/settings/transport
- **Status:** Reported

### BUG-121: Transport Management — Create Route form silently fails on empty name
- **Severity:** Medium
- **Category:** UI — Missing validation feedback
- **Expected:** Tapping "Create Route" with empty name field should show inline error or disable the button
- **Actual:** The "Create Route" button is always enabled. When tapped with empty name, the `if (name.isNotBlank())` check (line 213) silently skips the API call — no error message, no visual feedback. User has no idea why nothing happens.
- **Source:** `TransportManagementScreenV2.kt:208-216` — `onClick = { if (name.isNotBlank()) { viewModel.createRoute(...) } }` with no else branch
- **Screen:** admin/settings/transport (Add Route form)
- **Status:** Reported

### BUG-122: Transport Management — assignment form asks for raw "Student UUID" with no picker
- **Severity:** Medium
- **Category:** UX — Poor input design
- **Expected:** Admin should pick from a student list or search by name/code
- **Actual:** The assignment form uses a `VInput` with label "Student ID" and placeholder "Enter student UUID or code" (lines 320-326). Admins won't know student UUIDs. Same UX issue as Fee & Salary's teacher UUID field (BUG-111).
- **Source:** `TransportManagementScreenV2.kt:320-326` — `VInput(value = studentId, ..., label = "Student ID", placeholder = "Enter student UUID or code")`
- **Screen:** admin/settings/transport (Assignment form)
- **Status:** Reported

### BUG-123: Transport Management — fee due date uses plain text input instead of VDatePicker
- **Severity:** Low
- **Category:** UI — Inconsistent date input
- **Expected:** Date fields should use `VDatePicker` (the app-wide standard for all date inputs)
- **Actual:** The fee due date field (lines 407-413) uses a plain `VInput` with placeholder "YYYY-MM-DD", requiring manual text entry. This is inconsistent with the rest of the app which uses `VDatePicker` (e.g., Scholarship scheme form uses `VDatePicker` for start/end dates).
- **Source:** `TransportManagementScreenV2.kt:407-413` — `VInput(value = feeDueDate, ..., placeholder = "YYYY-MM-DD")` instead of `VDatePicker`
- **Screen:** admin/settings/transport (Assignment form)
- **Status:** Reported

### BUG-124: Scholarship Management — "Deactivate" button actually deletes the scheme
- **Severity:** Medium
- **Category:** UI — Misleading button label
- **Expected:** "Deactivate" should set `isActive = false`, not delete the scheme
- **Actual:** The scheme card's "Deactivate" button (line 349-355) triggers `onDelete = { deleteScheme = scheme }`, which opens a delete confirmation dialog titled "Delete Scheme". The button label says "Deactivate" but the action is destructive deletion. This is misleading — admins may expect to reactivate later.
- **Source:** `ScholarshipManagementScreenV2.kt:349-355` — `VButton(text = "Deactivate", onClick = onDelete)` where `onDelete = { deleteScheme = scheme }`; line 254-256 calls `viewModel.deleteScheme(it.id)`
- **Screen:** admin/settings/scholarship (Schemes tab, scheme card)
- **Status:** Reported

### BUG-125: Scholarship Management — scheme form only validates title, ignores amount
- **Severity:** Medium
- **Category:** UI — Missing validation
- **Expected:** The form should validate required fields (title, amount) before submission
- **Actual:** The "Create" button (line 765) only checks `if (title.isNotBlank())` before calling `onCreate`. The amount field is not validated — an empty amount is sent to the server. No inline error messages are shown for any field.
- **Source:** `ScholarshipManagementScreenV2.kt:765` — `if (title.isNotBlank()) { onCreate(CreateSchemeRequest(title = title, amount = amount, ...)) }`
- **Screen:** admin/settings/scholarship (Create Scheme form)
- **Status:** Reported

### BUG-126: Scholarship Management — renewal card shows "Renewal for" text twice
- **Severity:** Low
- **Category:** UI — Duplicate text
- **Expected:** The renewal card should show the scholarship title and student name, not duplicate labels
- **Actual:** The renewal card (lines 522-529) shows `renewal.scholarshipTitle ?: "Renewal for"` as the title, then immediately below shows `"Renewal for"` again as the subtitle. When `scholarshipTitle` is null, both lines show "Renewal for". When it's not null, the subtitle still shows the generic "Renewal for" with no student info.
- **Source:** `ScholarshipManagementScreenV2.kt:523-528` — title: `renewal.scholarshipTitle ?: appString(StringKeys.SCH_RENEWAL_FOR)`, subtitle: `appString(StringKeys.SCH_RENEWAL_FOR)`
- **Screen:** admin/settings/scholarship (Renewals tab)
- **Status:** Reported

### BUG-127: ID Cards — duplicate template names "t3" (one active, one inactive)
- **Severity:** Low
- **Category:** Data — Duplicate/confusing naming
- **Expected:** Template names should be unique or clearly distinguishable
- **Actual:** The Templates tab shows two templates both named "t3" — one Active and one Inactive. This is confusing for admins when selecting templates for card generation.
- **Steps to Reproduce:**
  1. Navigate to Settings > ID Cards
  2. Observe Templates tab — two "t3" entries
- **Screen:** admin/settings/id-cards (Templates tab)
- **Status:** Reported

### BUG-128: Library Management — "Bulk Return" tab truncated at screen edge, no scroll indicator
- **Severity:** Medium
- **Category:** UI — Truncated content
- **Expected:** All 14 library tabs should be accessible, with a visual indicator that more tabs exist beyond the visible area
- **Actual:** The tab bar uses `horizontalScroll` but "Bulk Return" at bounds `[1024,232][1080,345]` is only 56px wide — severely truncated. There are 14 tabs total (Dashboard, Books, Copies, Issues, Quick Issue, Bulk Return, Categories, Audit, Announcements, Acquisition, Reservations, History, More, Settings) but only 5-6 are visible. No scroll indicator or fade edge hints that more tabs exist.
- **Source:** `SchoolLibraryScreen.kt:144-156` — `Row` with `horizontalScroll` but no fade edge or scroll indicator
- **Screen:** admin/settings/library
- **Status:** Reported

### BUG-129: Gamification — leaderboard shows "Student #<hex>" instead of student names
- **Severity:** High
- **Category:** UI — Missing data / privacy issue
- **Expected:** Leaderboard should show student names, not truncated UUID hex codes
- **Actual:** The leaderboard displays `"Student #${entry.studentId.takeLast(6)}"` (e.g., "Student #ecaead"). The server returns only `studentId` (a UUID) without resolving the student's name. The `LeaderboardEntry` model has no `studentName` field. Admins cannot identify students on the leaderboard.
- **Source:** `AdminGamificationScreenV2.kt:474` — `Text("Student #${entry.studentId.takeLast(6)}", ...)`; `GamificationSubsystems.kt:419` — `studentId = row[GameStudentStatsTable.studentId].toString()` (no name join); `GamificationModels.kt:132-139` — `LeaderboardEntry` has no `studentName` field
- **Screen:** admin/settings/gamification (Leaderboard card)
- **Status:** Reported

### BUG-130: Gamification — leaderboard "Lv 300" and "Lv 100" don't match level definitions (0-10)
- **Severity:** High
- **Category:** Data — Inconsistent level system
- **Expected:** Student levels on the leaderboard should match the defined level system (levels 0-10 with titles like Novice, Beginner, Explorer, etc.)
- **Actual:** The leaderboard shows "Lv 300" for rank 1 and "Lv 100" for ranks 2-4. The level definitions show levels 0-10. The `currentLevel` field in `GameStudentStatsTable` appears to store XP threshold values (100, 300) instead of actual level numbers (1, 2, 3...). This creates a confusing disconnect between the level definitions shown and the actual student levels.
- **Source:** `GamificationSubsystems.kt:421` — `currentLevel = row[GameStudentStatsTable.currentLevel]` (raw value from DB); Level definitions in `LevelDefinitionsCard` show levels 0-10
- **Screen:** admin/settings/gamification (Leaderboard card vs Level Definitions card)
- **Status:** Reported

### BUG-131: Gamification — class leaderboard doesn't filter by class
- **Severity:** Medium
- **Category:** Backend — Missing filter implementation
- **Expected:** `getClassLeaderboard` should filter students by class
- **Actual:** The `getClassLeaderboard` function (line 428) has a comment "Class leaderboard would need a join with students table for class info" and "For now, return school-level leaderboard filtered by class if available" but it doesn't actually filter by class at all. It returns the same school-level leaderboard regardless of the `className` parameter.
- **Source:** `GamificationSubsystems.kt:428-440` — `getClassLeaderboard` ignores `className` parameter
- **Status:** Reported

### BUG-132: Gamification — screen uses old design tokens (VColors/VTypography) instead of VTheme
- **Severity:** Low
- **Category:** UI — Design system inconsistency
- **Expected:** New screens should use the `VTheme` design system (`VTheme.colors`, `VTheme.type`)
- **Actual:** `AdminGamificationScreenV2.kt` uses `VColors.cream`, `VColors.violet`, `VTypography.body`, etc. (old design tokens) throughout, instead of `VTheme.colors.cream`, `VTheme.type.body`, etc. This causes visual inconsistency with newer screens that use `VTheme`. Same issue in `ScholarshipManagementScreenV2.kt` and `TransportManagementScreenV2.kt`.
- **Source:** `AdminGamificationScreenV2.kt`, `ScholarshipManagementScreenV2.kt`, `TransportManagementScreenV2.kt` — all use `VColors.*` and `VTypography.*` instead of `VTheme.*`
- **Status:** Reported

### BUG-133: Gamification — XP Boost form has no input validation
- **Severity:** Medium
- **Category:** UI — Missing validation
- **Expected:** The boost creation form should validate inputs (multiplier > 0, duration > 0, valid scope)
- **Actual:** The "Create Boost" button (line 621-626) calls `multiplier.toFloatOrNull() ?: 1.0f` and `durationHours.toIntOrNull() ?: 24` with fallback defaults. If the user enters invalid values like "abc" for multiplier, it silently uses 1.0x. No inline error messages are shown. The `targetScope` field accepts free text instead of providing a dropdown with valid options (ALL/CLASS/STUDENT).
- **Source:** `AdminGamificationScreenV2.kt:622-624` — `toFloatOrNull() ?: 1.0f`, `toIntOrNull() ?: 24`; no validation feedback
- **Screen:** admin/settings/gamification (XP Boosts card)
- **Status:** Reported

### BUG-134: Gamification — Boost form targetId always passed as null
- **Severity:** Medium
- **Category:** Functional — Incomplete feature
- **Expected:** When target scope is "CLASS" or "STUDENT", a target ID should be provided
- **Actual:** The `onCreateBoost` call (line 624) always passes `null` for `targetId`: `onCreateBoost(boostType, mult, targetScope, null, hrs)`. There's no UI field to enter a class ID or student ID. If an admin selects "CLASS" or "STUDENT" scope, the boost will be created with no target, making it ineffective.
- **Source:** `AdminGamificationScreenV2.kt:624` — `onCreateBoost(boostType, mult, targetScope, null, hrs)`
- **Screen:** admin/settings/gamification (XP Boosts > Create New Boost form)
- **Status:** Reported

### BUG-135: Branding & Photos — "Campus gallery" text had zero-size bounds during scroll
- **Severity:** Low
- **Category:** UI — Rendering glitch
- **Expected:** All text elements should have non-zero bounds
- **Actual:** During scroll, the "Campus gallery" title and "Showcase your school (1)" subtitle had bounds `[0,0][0,0]` (zero-size). On a second scroll, they appeared with proper bounds. This suggests a rendering/layout issue during scroll animation.
- **Screen:** admin/settings/branding (Campus gallery section)
- **Status:** Reported


---

## Teacher Portal Testing — Logged in as t2@gmail.com (Asha Verma)

### BUG-136: Teacher Profile — username displayed with "@" prefix, looks like Twitter handle
- **Severity:** Low
- **Category:** UI — Formatting
- **Expected:** Username/email should be displayed cleanly, e.g. "t21@gmail.com"
- **Actual:** The identity hero card shows "@t21@gmail.com" — the code prepends "@" to the username field (`Text("@${p.username}", ...)`), but the username IS the full email address, creating a confusing "@t21@gmail.com" display that looks like a social media handle.
- **Source:** `TeacherProfileScreenV2.kt:370` — `Text("@${p.username}", ...)`
- **Screen:** Teacher > Profile (Identity Hero card)
- **Status:** Reported

### BUG-137: Teacher Attendance — student names show raw codes ("Dd", "S10A-1", "S10A-2") instead of real names
- **Severity:** High
- **Category:** Data — Missing student names
- **Expected:** Attendance list should show student full names
- **Actual:** Out of 5 students in Class 10-A, only "Gaurav" has a real name. The other 4 show: "Dd", "Dd", "S10A-1", "S10A-2" — these are student codes or initials, not display names. All students show "Roll No" as the subtitle instead of actual roll numbers.
- **Screen:** Teacher > Update > Attendance (Class 10-A, Social Studies)
- **Status:** Reported

### BUG-138: Teacher Attendance — duplicate student name "Dd" appears twice
- **Severity:** Medium
- **Category:** Data — Duplicate entry
- **Expected:** Each student should have a unique display name
- **Actual:** Two students in the attendance list are both named "Dd" with "Roll No" subtitle. Cannot distinguish between them.
- **Screen:** Teacher > Update > Attendance (Class 10-A, Social Studies)
- **Status:** Reported

### BUG-139: Teacher Marks — all roll numbers show "Roll 1" except last student
- **Severity:** Medium
- **Category:** Data — Incorrect roll numbers
- **Expected:** Each student should have their actual roll number
- **Actual:** In the marks entry screen for "unit test 2", 4 out of 5 students show "Roll 1" and only the last student shows "Roll 2". This suggests roll numbers are not being correctly assigned or displayed.
- **Screen:** Teacher > Update > Marks > unit test 2
- **Status:** Reported

### BUG-140: Teacher Create Test — "Exam" type tab severely truncated (1px width)
- **Severity:** Medium
- **Category:** UI — Truncated content
- **Expected:** All 5 test type options (Scheduled, Surprise, Assignment, Project, Exam) should be fully visible
- **Actual:** The "Exam" type tab has bounds [952,1045][953,1208] — only 1 pixel wide. The 5 type options don't fit on screen and "Exam" is effectively invisible/untappable.
- **Screen:** Teacher > Update > Marks > Create a test
- **Status:** Reported

### BUG-141: Teacher Create Test form renders inline instead of as modal dialog
- **Severity:** Medium
- **Category:** UI — Missing modal wrapper
- **Expected:** "Create a test" should open a modal bottom sheet or dialog
- **Actual:** The form renders inline within the screen content, pushing existing content down. No scrim or modal behavior. Same pattern as Transport Management forms (BUG-120) and Fee & Salary sheets (BUG-100).
- **Screen:** Teacher > Update > Marks > Create a test
- **Status:** Reported

### BUG-142: Teacher Timetable — all days (Mon-Sat) show identical schedule, day selector may not work
- **Severity:** Medium
- **Category:** Functional — Day selector not working
- **Expected:** Tapping different day tabs (Mon-Sat) should show that day's specific timetable
- **Actual:** Every day (Mon through Sat) shows the same schedule: Class 10-A, Mathematics, Room 1 at 08:00-08:40. Either the day selector is not switching content, or all days genuinely have the same single period (which is unlikely for a real timetable).
- **Screen:** Teacher > Timetable
- **Status:** Reported

### BUG-143: Teacher Classes — "Mark attendance" button shown for 0-student classes
- **Severity:** Low
- **Category:** UI — Misleading action
- **Expected:** "Mark attendance" should not appear for classes with 0 students
- **Actual:** Classes like "Class 10-B · Mathematics · 0 students", "Class 10-D · 0 students", "Class 11-A · 0 students", etc. all show a "Mark attendance" button. Tapping it would open an empty attendance screen.
- **Screen:** Teacher > Classes
- **Status:** Reported

### BUG-144: Teacher — multiple elements have zero-size bounds (rendering glitches)
- **Severity:** Low
- **Category:** UI — Rendering glitch
- **Expected:** All visible elements should have non-zero bounds
- **Actual:** Multiple elements across different tabs have bounds [0,0][0,0]:
  - Home: "त्वरित क्रियाएँ" (Quick Actions) section header
  - Home: "इस महीने के लिए कोई कार्यक्रम निर्धारित नहीं।" (Upcoming Events empty text)
  - Classes: 3rd class card (Class 10-A, Mathematics) — completely invisible
  - Profile: "SALARY & PAYMENTS" section label
  - Profile: Telugu language option (తెలుగు) on first render
  - Curriculum: "The Making of a Global World (History)" unit title
  - Update: "Class 9-A" class card truncated at bottom of list
- **Screen:** Multiple teacher tabs
- **Status:** Reported

### BUG-145: Teacher — Messages icon opens Notifications screen, not messaging
- **Severity:** Medium
- **Category:** UI — Mislabeled navigation
- **Expected:** The "Messages" icon (content-desc="Messages") should open a messaging/chat screen
- **Actual:** Tapping the Messages icon opens the Notifications screen (सूचनाएँ) showing "INBOX", "0 unread", and "अभी कोई सूचना नहीं" (no notifications). The content-desc says "Messages" but the screen title is "सूचनाएँ" (Notifications). Either the icon is mislabeled or the wrong screen is opened.
- **Screen:** Teacher > All tabs (Messages icon in header)
- **Status:** Reported

### BUG-146: Teacher Curriculum — "क्विक़्" (Quizzes) is misspelled Hindi
- **Severity:** Low
- **Category:** UI — Localization error
- **Expected:** Hindi for "Quizzes" should be "क्विज़" (quiz) or "क्विज़ेज़" (quizzes)
- **Actual:** The button text shows "क्विक़्" which is not a valid Hindi word — it appears to be a corrupted transliteration. The correct Hindi word for quiz is "क्विज़".
- **Screen:** Teacher > Update > Curriculum
- **Status:** Reported

### BUG-147: Teacher screens use old design tokens (VColors/VTypography) instead of VTheme
- **Severity:** Low
- **Category:** UI — Design system inconsistency
- **Expected:** All screens should use the VTheme design system (`VTheme.colors`, `VTheme.type`)
- **Actual:** `TeacherHomeScreenV2.kt` uses `VColors.cream`, `VColors.violet`, `VTypography.body`, etc. (old design tokens) throughout, instead of `VTheme.colors.cream`, `VTheme.type.body`, etc. Same issue as gamification, scholarship, and transport screens (BUG-132).
- **Source:** `TeacherHomeScreenV2.kt:61-63` — imports `VColors`, `VShapes`, `VTypography` instead of `VTheme`
- **Status:** Reported

### BUG-148: Teacher Homework — no minimum length validation on homework title
- **Severity:** Low
- **Category:** UI — Missing validation
- **Expected:** Homework title should have a minimum length requirement (e.g., 3 characters)
- **Actual:** A homework titled "y" (single character) exists in the system. While this may be test data, the form allows single-character titles, indicating no minimum length validation on the homework title field.
- **Screen:** Teacher > Update > Homework (Class 10-A, Social Studies)
- **Status:** Reported


### BUG-149: Teacher Homework — list doesn't auto-refresh after assigning new homework
- **Severity:** Medium
- **Category:** Functional — Missing auto-refresh
- **Expected:** After assigning homework, the new item should appear immediately in the list
- **Actual:** After submitting "ReadChapter5" homework, the form closed but the list still showed only the old "y" homework. User must pull-to-refresh to see the new homework.
- **Screen:** Teacher > Update > Homework (Class 10-A, Social Studies)
- **Status:** Reported

### BUG-150: Teacher Create Test — form doesn't close after successful submission
- **Severity:** Medium
- **Category:** Functional — Form not closing
- **Expected:** After successfully creating a test, the form should close and return to the marks list
- **Actual:** After tapping "Create test" with title "Quiz1", the test was created (appeared in list) but the form stayed open with cleared fields. User must manually close the form via "बंद करें" button.
- **Screen:** Teacher > Update > Marks > Create a test
- **Status:** Reported

### BUG-151: Teacher Create Test — allows submission without picking a date
- **Severity:** Medium
- **Category:** Functional — Missing validation
- **Expected:** Date field should be required — a test cannot be scheduled without a date
- **Actual:** Tapping "Create test" with title "Quiz1" and no date picked successfully created the test. The date field shows "Pick the test date" placeholder but is not validated.
- **Screen:** Teacher > Update > Marks > Create a test
- **Status:** Reported

### BUG-152: Teacher Marks — test created without date shows "READY TO MARK" and no date in list
- **Severity:** Low
- **Category:** Data — Inconsistent display
- **Expected:** Tests without a date should either not be created (validation) or show a clear indicator
- **Actual:** The "Quiz1" test appears in the list as "Max 100 · Entered 0 of 5" (no date shown, unlike "unit test 2" which shows "Max 100 · 14 Jul · Entered 0 of 5"). It shows "READY TO MARK" badge instead of "MARKS PENDING" — inconsistent status for a test with no date.
- **Screen:** Teacher > Update > Marks (Class 10-A, Social Studies)
- **Status:** Reported

### BUG-153: Teacher Leave form — "To" date selector truncated to 5px width
- **Severity:** High
- **Category:** UI — Truncated content
- **Expected:** "To" date selector should be fully visible and tappable
- **Actual:** The "To" (को) date selector has bounds [235,2213][240,2259] — only 5px wide. Users cannot tap it to select an end date for leave.
- **Screen:** Teacher > Profile > My leave > Apply
- **Status:** Reported

### BUG-154: Teacher Leave form — Reason EditText has zero-size bounds (invisible)
- **Severity:** High
- **Category:** UI — Invisible element
- **Expected:** Reason text field should be visible and editable
- **Actual:** The Reason (कारण) EditText has bounds [0,0][0,0] — completely invisible and untappable. Users cannot enter a reason for their leave request.
- **Screen:** Teacher > Profile > My leave > Apply
- **Status:** Reported

### BUG-155: Teacher Password form — fields initially have zero-size bounds (LazyColumn rendering)
- **Severity:** Medium
- **Category:** UI — Rendering glitch
- **Expected:** Password form fields should be visible when the form expands
- **Actual:** When the password section is expanded, "Current password" label and EditText have bounds [0,0][0,0]. They only become visible after scrolling down and back up. This is a LazyColumn rendering issue where off-screen items get zero bounds.
- **Screen:** Teacher > Profile > Security > Password
- **Status:** Reported

### BUG-156: Teacher Report Card Review — leading space in class label " A • Term 1"
- **Severity:** Low
- **Category:** UI — Formatting
- **Expected:** Class label should display as "A • Term 1" without leading space
- **Actual:** The class/term label shows " A • Term 1" with a leading space before "A", indicating a formatting issue in the label construction.
- **Screen:** Teacher > Home > Quick Actions > Reports
- **Status:** Reported

### BUG-157: Teacher Needs Attention — no action buttons on at-risk student cards
- **Severity:** Medium
- **Category:** Functional — Missing actions
- **Expected:** At-risk student cards should have action buttons like "Notify Parent", "Mark Intervention", or "View Details"
- **Actual:** The Needs Attention screen shows at-risk students (Gaurav, Dd) with attendance/marks data and recommended action ("parent_call: Standard first response for attendance cases.") but no actionable buttons. Teachers can see the recommendation but cannot act on it from this screen.
- **Screen:** Teacher > Home > Needs Attention > View Insights
- **Status:** Reported


### BUG-158: Teacher Marks — "AB" badge persists after entering marks for a student
- **Severity:** Low
- **Category:** UI — Stale state
- **Expected:** "AB" (absent) badge should disappear once marks are entered for a student
- **Actual:** After entering marks (75 for Dd, 85 for Gaurav), the "AB" badge still shows next to all students, including those with marks entered. The badge should either not appear for students with marks, or should be replaced with a "Present" indicator.
- **Screen:** Teacher > Update > Marks > unit test 2
- **Status:** Reported

### BUG-159: Teacher Profile — "High Contrast" theme option has zero-size bounds
- **Severity:** Low
- **Category:** UI — Invisible element
- **Expected:** "High Contrast" theme option should be visible and selectable
- **Actual:** The "High Contrast" (WCAG AAA accessibility) theme option has bounds [0,0][0,0] — completely invisible and untappable. This is a recurring LazyColumn rendering issue.
- **Screen:** Teacher > Profile > Preferences > Appearance
- **Status:** Reported

### BUG-160: Teacher Marks — "Import Marks (OCR / Text)" button visible but functionality untested
- **Severity:** Low
- **Category:** UI — Feature visibility
- **Expected:** "Import Marks" should open a file picker or text input dialog
- **Actual:** The button is visible on the marks entry screen. Not tested for functionality but noted as present.
- **Screen:** Teacher > Update > Marks > unit test 2
- **Status:** Reported (info only)


### BUG-161: Teacher Notification Preferences — app crashes when opening
- **Severity:** Critical
- **Category:** Functional — Crash
- **Expected:** Tapping "सूचना प्राथमिकताएँ" (Notification Preferences) should open the preferences screen
- **Actual:** Tapping the Notification Preferences link from the Notifications screen causes the app to crash with "EnRoll+ keeps stopping" dialog. The app must be restarted.
- **Screen:** Teacher > Notifications > Notification Preferences
- **Status:** Reported

### BUG-162: Teacher Home — 5 overlay screens unreachable (dead code callbacks)
- **Severity:** High
- **Category:** Functional — Unreachable features
- **Expected:** HealthAlerts, TransportAttendance, Heatmap, DigitalIdCard, and ScheduledMessages overlays should be accessible from the UI
- **Actual:** The callbacks `onOpenHealthAlerts`, `onOpenTransportAttendance`, `onOpenHeatmap`, `onOpenIdCard`, and `onOpenScheduledMessages` are declared as parameters in TeacherHomeScreenV2 and wired in TeacherPortalV2, but are never invoked anywhere in the Home screen UI. These 5 overlay screens are completely unreachable from the teacher portal.
- **Screen:** Teacher > Home (all overlays unreachable)
- **Status:** Reported

### BUG-163: Teacher Class Detail — homework shows literal `{count} turned in` placeholder
- **Severity:** Medium
- **Category:** UI — Untranslated/i18n placeholder
- **Expected:** Homework cards should show the actual count of submissions, e.g. "3 turned in"
- **Actual:** Both homework items in the Class Detail screen show "{count} turned in · Due 14 Jul" and "{count} turned in · Due 15 Jul" — the `{count}` placeholder is displayed literally instead of being interpolated with the actual number.
- **Screen:** Teacher > Classes > Class 10-A (detail)
- **Status:** Reported


### BUG-164: Teacher Exam Timetables — "New Exam Timetable" button hidden by empty state
- **Severity:** High
- **Category:** UI — Button hidden by VStateHost
- **Expected:** "New Exam Timetable" button should be visible when no timetables exist, so teachers can upload one
- **Actual:** The "New Exam Timetable" button is inside VStateHost content, which is replaced by the empty state when the list is empty. Teachers see "No exam timetables yet / Upload a timetable image or paste text to get started" but have no button to actually do so.
- **Screen:** Teacher > Home > Quick Actions > Exams
- **Status:** Reported

### BUG-165: Teacher Timetable — Request New Period doesn't appear in Requests tab after submission
- **Severity:** Medium
- **Category:** Functional — Submission not persisted or not refreshing
- **Expected:** After submitting a new period request, it should appear in the Requests tab
- **Actual:** Submitted a Request New Period form (Class 10-A Social Studies, Mon, 09:00-10:00, reason "NeedExtraClass"). The form closed successfully but the Requests tab still shows "No change requests" even after pull-to-refresh.
- **Screen:** Teacher > Timetable > Requests
- **Status:** Reported

### BUG-166: Teacher Class Gamification — leaderboard shows hex IDs instead of student names
- **Severity:** Medium
- **Category:** UI — Raw IDs displayed
- **Expected:** Class leaderboard should show student names, not hex IDs
- **Actual:** The Class Gamification leaderboard shows "Student #ecaead", "Student #0b00e8", "Student #39d3f1", "Student #5b4664", "Student #a4daa8" instead of actual student names (Dd, Gaurav, S10A-1, S10A-2).
- **Screen:** Teacher > Classes > Class 10-A > Class Gamification
- **Status:** Reported


### BUG-167: Teacher Gamification — "Encourage" button gives no visible feedback after tap
- **Severity:** Medium
- **Category:** UX — Missing feedback
- **Expected:** After tapping "Encourage", teacher should see a success toast, snackbar, or button state change
- **Actual:** Tapping "Encourage" on a student's gamification panel calls the API silently with no visible feedback. The button has a `loading` state (`state.isActionLoading`) but no success/error toast. Teacher has no way to know if the action succeeded or failed.
- **Screen:** Teacher > Classes > Student Profile > Gamification Tools
- **Status:** Reported

### BUG-168: Teacher Lesson Plan — "Create plan" button clipped by bottom nav bar (only 9px visible)
- **Severity:** Medium
- **Category:** UI — Button clipped
- **Expected:** "Create plan" button should be fully visible and easily tappable
- **Actual:** The "Create plan" button at the bottom of the New Lesson Plan form has bounds [439,2325][641,2334] — only 9px high. The bottom nav bar overlaps the button, making it nearly impossible to tap. The button is still functional but severely truncated.
- **Screen:** Teacher > Update > Lesson Plan > New plan
- **Status:** Reported

### BUG-169: Teacher Gamification — "Send Shoutout" gives no success confirmation after sending
- **Severity:** Low
- **Category:** UX — Missing confirmation
- **Expected:** After sending a shoutout, teacher should see a success message or toast
- **Actual:** Typed "GreatJob" in the shoutout field and tapped "Send Shoutout". The text field collapsed and button reverted to "Send Shoutout", but no success toast or confirmation was shown. Teacher has no way to know if the shoutout was actually sent.
- **Screen:** Teacher > Classes > Student Profile > Gamification Tools > Send Shoutout
- **Status:** Reported


### BUG-170: Teacher Report Draft Editor — unreachable (no drafts, no create button)
- **Severity:** Medium
- **Category:** Functional — Unreachable feature
- **Expected:** Teachers should be able to create or edit report card drafts
- **Actual:** The Report Card Review screen shows "No drafts found" with no button to create a new draft. The ReportDraftEditor overlay is unreachable. The only way to access it would be via an existing draft, but there's no way to create one from the teacher portal.
- **Screen:** Teacher > Home > Quick Actions > Reports
- **Status:** Reported

### BUG-171: Teacher Exam Timetable sub-screens — 4 overlays unreachable due to empty state hiding button
- **Severity:** High
- **Category:** Functional — Unreachable features
- **Expected:** ExamTimetableUpload, ExamTimetableDetail, ExamSyllabusMapping, and ExamMarksImport should be accessible
- **Actual:** All 4 Exam Timetable sub-screens are unreachable. The "New Exam Timetable" button (which opens ExamTimetableUpload) is hidden by VStateHost empty state (BUG-164). Without uploading a timetable, ExamTimetableDetail, ExamSyllabusMapping, and ExamMarksImport are also unreachable since they require an existing timetable.
- **Screen:** Teacher > Home > Quick Actions > Exams (all sub-screens)
- **Status:** Reported

### BUG-172: Teacher Announcement Detail — deep-link only, no UI trigger
- **Severity:** Low
- **Category:** Functional — Unreachable from UI
- **Expected:** Teachers should be able to view announcements from the UI (e.g., from Notifications or a dedicated Announcements section)
- **Actual:** The AnnouncementDetail overlay is only accessible via deep links (from push notifications). There is no UI element in the teacher portal that triggers this overlay. Teachers who don't receive push notifications cannot view announcements.
- **Screen:** Teacher > AnnouncementDetail (unreachable from UI)
- **Status:** Reported


### BUG-173: Admin Fee Structure — "Add Fee Structure" button hidden by VStateHost empty state
- **Severity:** High
- **Category:** Functional — Unreachable feature
- **Expected:** "Add Fee Structure" button should be visible even when no fee structures exist
- **Actual:** The "Add Fee Structure" button is inside VStateHost content lambda. When the structures list is empty, VStateHost shows the empty state ("No Fee Structures") instead of the content, hiding the button. Admin cannot create new fee structures.
- **Screen:** Admin > Settings > Fee & Salary > Fees > Structure
- **Status:** Reported

### BUG-174: Admin Fee Management — "Generate Monthly Fees" and "Add Fee Structure" dialogs render invisible (zero-size)
- **Severity:** High
- **Category:** UI — Dialog not visible
- **Expected:** Tapping "Generate Monthly Fees" should open a visible dialog with month input and Generate/Cancel buttons
- **Actual:** `GenerateFeesSheet` and `AddFeeStructureSheet` are plain `Column` composables with no dialog/sheet/bottom-sheet wrapper. When shown, they render with all elements at bounds [0,0][0,0] — completely invisible. The EditText for month "2026-07" is in the view hierarchy but not visible on screen. Admin cannot generate fees or add structures even if the buttons were visible.
- **Screen:** Admin > Settings > Fee & Salary > Fees > Payments > Generate Monthly Fees
- **Status:** Reported

### BUG-175: Admin Fee Payments — Student status shows raw "NO_FEES" enum value instead of user-friendly label
- **Severity:** Low
- **Category:** UI — Raw enum displayed
- **Expected:** Status badge should show a user-friendly label like "No Fees" or "Pending"
- **Actual:** The student fee card shows "NO_FEES" as the status badge text — a raw backend enum value. The VBadge tone mapping handles "PAID", "DUE", "OVERDUE", "PARTIAL" but falls through to Neutral for unknown values, displaying the raw string.
- **Screen:** Admin > Settings > Fee & Salary > Fees > Payments
- **Status:** Reported


### BUG-176: Admin Salary — "Add Salary Record" button hidden by VStateHost empty state (same pattern as BUG-173)
- **Severity:** High
- **Category:** Functional — Unreachable feature
- **Expected:** "Add Salary Record" button should be visible even when no salary records exist
- **Actual:** Same VStateHost pattern as BUG-173. The "Add Salary Record" button is inside VStateHost content. When salary records list is empty, the empty state ("No Salary Records") is shown instead, hiding the button. Admin cannot create salary records.
- **Screen:** Admin > Settings > Fee & Salary > Salary
- **Status:** Reported

### BUG-177: Admin Salary — AddSalarySheet renders invisible and requires raw Teacher UUID
- **Severity:** High
- **Category:** UI — Dialog not visible + Poor UX
- **Expected:** Add Salary form should appear as a visible dialog with a teacher picker dropdown
- **Actual:** Two issues: (1) AddSalarySheet is a plain Column with no dialog/sheet wrapper — renders invisible (same as BUG-174). (2) The form asks admin to "Paste teacher UUID" in a text field — extremely poor UX requiring admin to know and paste a raw UUID. Should be a teacher picker/dropdown.
- **Screen:** Admin > Settings > Fee & Salary > Salary > Add Salary Record
- **Status:** Reported


### BUG-179: Admin People — Teachers tab shows no teacher cards despite 11 teachers reported on Home
- **Severity:** High
- **Category:** Functional — Data not loading
- **Expected:** Teachers tab should list all 11 teachers as cards with names, subjects, classes
- **Actual:** The Teachers tab in People Directory shows only the tab pills and a "More" button. No teacher cards are visible between the tabs and the More button. The Home screen reports "11 Total Teachers" but none appear in the People > Teachers tab. The area where cards should be is completely blank.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab
  3. Tap Teachers sub-tab (already selected by default)
  4. Observe blank area — no teacher cards visible
  5. Home screen shows 11 Total Teachers
- **Screen:** Admin > People > Teachers
- **Status:** Reported

### BUG-180: Admin People — Non-teaching staff tab shows error state mixed with partial data
- **Severity:** Medium
- **Category:** UI — Error state overlapping content
- **Expected:** Non-teaching staff tab should show either staff cards or a clean error state, not both
- **Actual:** The Non-teaching staff tab shows partial staff cards (Class 1-A, Message buttons) alongside an error message "Something went wrong. Please try again later." with a Retry button. The error state appears to be overlapping or mixed with the staff data, creating a confusing UI.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab
  3. Tap Non-teaching staff sub-tab
  4. Observe staff cards and error message both visible
- **Screen:** Admin > People > Non-teaching staff
- **Status:** Reported

### BUG-181: Admin Records — Attendance "By class" row shows "—" instead of class name
- **Severity:** Low
- **Category:** UI — Missing data display
- **Expected:** The "By class" breakdown should show the class name (e.g., "Class 10-A") not a dash
- **Actual:** In the Attendance sub-tab of Records, the "By class" row shows "—" (em dash) instead of the actual class name. The attendance numbers (5/5 • 100%) are correct but the class label is missing.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap Records tab
  3. Tap Attendance sub-tab
  4. Observe "By class" row shows "—" instead of class name
- **Screen:** Admin > Records > Attendance
- **Status:** Reported


### BUG-182: Admin Comms — Announcement title "Tommorow is off" has spelling mistake
- **Severity:** Low
- **Category:** UI — Spelling error in user-generated content
- **Expected:** Announcement title should spell "Tomorrow" correctly
- **Actual:** The announcement titled "Tommorow is off" (dated 2026-07-02) has a spelling mistake — "Tommorow" should be "Tomorrow". While this is user-generated content, it suggests the app doesn't provide any text validation or spell-check for announcement titles.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap Comms tab
  3. Scroll through announcements
  4. Find "Tommorow is off" announcement
- **Screen:** Admin > Comms > Announcements
- **Status:** Reported

### BUG-183: Admin Analytics — Insight text shows "Class Class 10-A" with duplicate "Class" prefix
- **Severity:** Low
- **Category:** UI — String formatting
- **Expected:** Insight should read "Class 10-A reached 40% attendance" not "Class Class 10-A reached 40% attendance"
- **Actual:** The Analytics Dashboard insight for "Attendance Peak" shows "Class Class 10-A reached 40% attendance" — the word "Class" is duplicated. This is a string formatting issue where the class name already includes "Class" prefix and the template adds another.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Search "analytics" and open Analytics Dashboard
  3. Scroll to INSIGHTS section
  4. Read "Attendance Peak" insight — shows "Class Class 10-A"
- **Screen:** Admin > Analytics Dashboard > Insights
- **Status:** Reported

### BUG-184: Admin Home — Profile avatar tap intercepted by notification bell, profile unreachable
- **Severity:** Medium
- **Category:** UI — Overlapping touch targets
- **Expected:** Tapping the admin profile avatar should open the profile/settings
- **Actual:** The admin profile avatar (bounds [911,123][1027,239]) and the notification bell (bounds [940,152][998,210]) overlap. Tapping anywhere on the profile avatar area always triggers the notification bell, opening the Notifications screen instead. The admin profile is unreachable by tap. The only way to access settings is through the bottom nav Settings tab.
- **Steps to Reproduce:**
  1. Login as school admin
  2. On Home screen, tap the profile avatar (top-right area)
  3. Notifications screen opens instead of profile/settings
  4. Try tapping different points on the avatar — all trigger notifications
- **Screen:** Admin > Home (profile avatar area)
- **Status:** Reported


### BUG-185: Admin Settings — Classes & Subjects shows duplicate Mathematics subject with different codes
- **Severity:** Low
- **Category:** Data — Duplicate entries
- **Expected:** Each subject should appear only once per class with a single code
- **Actual:** In Classes & Subjects > Subjects tab > Class 10, Mathematics appears twice: once with code MAT001 and again with code MATH. This is a duplicate subject entry that could cause confusion in marks entry, report cards, and assignments.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Search "classes" and open Classes & Subjects
  3. Tap Subjects tab
  4. Select Class 10
  5. Observe Mathematics listed twice with different codes
- **Screen:** Admin > Classes & Subjects > Subjects > Class 10
- **Status:** Reported

### BUG-186: Admin Home — Search palette missing several overlays (PEWS, Health Records, Delivery Log, Admissions, Report Publishing)
- **Severity:** Medium
- **Category:** Functional — Missing search entries
- **Expected:** All reachable overlays should be searchable from the "Jump to screen" search palette
- **Actual:** Searching for "pews", "health", "delivery", "admissions", and "report" all return "No matching screens" despite these overlays existing in the SchoolOverlay enum and being reachable via other navigation paths. The search palette only includes a subset of available overlays.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap "Jump to screen..." search bar
  3. Type "pews" — No matching screens
  4. Type "health" — No matching screens
  5. Type "delivery" — No matching screens
  6. Type "admissions" — No matching screens
  7. Type "report" — No matching screens
- **Screen:** Admin > Home > Search palette
- **Status:** Reported

### BUG-187: Admin Analytics — Multiple metrics show em dash instead of values
- **Severity:** Low
- **Category:** UI — Missing data display
- **Expected:** Analytics metrics should show actual values or a proper "No data" label
- **Actual:** In the Analytics Dashboard, several metrics show "—" (em dash) instead of actual values: Syllabus Coverage, Teacher Accountability (Avg Rating), and Class Performance (Proficiency). While these may genuinely have no data, the em dash is not a user-friendly way to communicate this — it should show "0%", "N/A", or "No data yet".
- **Steps to Reproduce:**
  1. Login as school admin
  2. Search "analytics" and open Analytics Dashboard
  3. Scroll to OVERVIEW section
  4. Observe Syllabus Coverage, Teacher Accountability, Class Performance all show "—"
- **Screen:** Admin > Analytics Dashboard > Overview
- **Status:** Reported


### BUG-188: Admin Settings — Institutional Profile City and District fields show "Aa" placeholder text
- **Severity:** Low
- **Category:** Data — Placeholder/test data in production
- **Expected:** City and District fields should show actual city and district names
- **Actual:** In the Institutional Profile screen, the City field shows "Aa" and the District field also shows "Aa". These appear to be placeholder or test data that was never replaced with real values. The address field correctly shows "Education Lane, Knowledge Hub, Sector 42, New Delhi - 110001" making the "Aa" values clearly wrong.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Search "profile" and open Institutional Profile
  3. Scroll to Location section
  4. Observe City field shows "Aa" and District field shows "Aa"
- **Screen:** Admin > Institutional Profile > Location
- **Status:** Reported

### BUG-189: Admin Settings — Institutional Profile State shows "Uttar Pradesh" but address says "New Delhi"
- **Severity:** Low
- **Category:** Data — Inconsistent location data
- **Expected:** State should be consistent with the address (New Delhi → Delhi, not Uttar Pradesh)
- **Actual:** The Institutional Profile shows address as "Education Lane, Knowledge Hub, Sector 42, New Delhi - 110001" but the State field shows "Uttar Pradesh". New Delhi is in the Delhi union territory, not Uttar Pradesh. This inconsistency could cause issues in reports, UDISE filings, and parent-facing pages.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Search "profile" and open Institutional Profile
  3. Scroll to Location section
  4. Compare address (New Delhi) with State (Uttar Pradesh)
- **Screen:** Admin > Institutional Profile > Location
- **Status:** Reported

### BUG-190: Admin People — Teachers tab consistently shows no teacher cards across multiple visits
- **Severity:** High
- **Category:** Functional — Data not rendering
- **Expected:** Teachers tab should render teacher cards with avatars, names, subjects, and action buttons
- **Actual:** Across multiple visits to the People > Teachers tab, no teacher cards are ever rendered. The screen shows only the tab pills (Teachers/Students/Non-teaching staff) and a floating "More" button with a large blank area where teacher cards should be. The Home screen reports "11 Total Teachers" and the Daily Attendance > Faculty tab shows 11 faculty members, confirming teachers exist in the system. This is a persistent rendering failure, not a transient loading issue.
- **Steps to Reproduce:**
  1. Login as school admin
  2. Tap People tab
  3. Tap Teachers sub-tab
  4. Observe blank area — no teacher cards
  5. Switch to Students and back to Teachers — still blank
  6. Verify Home shows 11 Total Teachers
  7. Verify Daily Attendance > Faculty shows 0/11
- **Screen:** Admin > People > Teachers
- **Status:** Reported


### BUG-191: Parent Home — AVG GRADE shows em dash instead of value
- **Severity:** Low
- **Category:** UI — Missing data display
- **Expected:** AVG GRADE should show actual grade or "N/A"
- **Actual:** Home screen child card shows "—" for AVG GRADE. Same em dash issue as admin analytics.
- **Steps:** Login as parent > Home > observe AVG GRADE shows "—"
- **Screen:** Parent > Home > Child card
- **Status:** Reported

### BUG-192: Parent Profile — Shows "Student" label instead of "Parent"
- **Severity:** Medium
- **Category:** UI — Incorrect label
- **Expected:** Profile tab should show "Parent" as the user role
- **Actual:** Profile tab shows "Gaurav" with label "Student" below name. Account Settings correctly shows "Parent". Inconsistent labeling.
- **Steps:** Login as parent > Profile tab > observe "Student" label under name
- **Screen:** Parent > Profile
- **Status:** Reported

### BUG-193: Parent Academics — Quizzes tab cut off at screen edge
- **Severity:** Medium
- **Category:** UI — Layout overflow
- **Expected:** All sub-tabs (Overview/Attendance/Marks/Syllabus/Quizzes) should be fully visible and tappable
- **Actual:** Quizzes tab has bounds [1052,445][1080,571] — only 28px wide, cut off at right edge. Nearly impossible to tap.
- **Steps:** Login as parent > Academics tab > observe Quizzes tab is cut off
- **Screen:** Parent > Academics
- **Status:** Reported


### BUG-194: Parent Account Settings — Notification preferences row not clickable
- **Severity:** High | **Category:** Functional — Dead UI element | **Status:** Reported

### BUG-195: Parent Account Settings — Change password row not clickable
- **Severity:** High | **Category:** Functional — Dead UI element | **Status:** Reported

### BUG-196: Parent Academics — Skill Test answer submission fails
- **Severity:** High | **Category:** Functional — API error | **Status:** Reported
