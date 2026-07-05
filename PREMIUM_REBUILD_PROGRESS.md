# Premium V3 Screen Rebuild — Progress Tracker
> **Updated:** July 5, 2026 — Reflects actual file system state, not estimates.
> **Source of truth:** `PREMIUM_V3_REBUILD_PROMPT_v2.md` at repo root.

## Phase 0: Foundation — COMPLETE (6 tokens, 3 modifiers, 65 component files)
- [x] Copy tokens (VColors, VShapes, VMotion, VTypography) into composeApp
- [x] Copy modifiers (VShapeMorph, VGlow, VPressScale) into composeApp
- [x] Copy all 30 original component files into composeApp subpackages
- [x] Build VColorPalette + Light/Dark instances + CompositionLocal
- [x] Rewrite VColors to @Composable get() delegating to LocalVColorPalette
- [x] Build PremiumTheme provider with M3 ColorScheme bridge
- [x] Fix VPrimaryButton missing background
- [x] Fix VSecondaryButton missing background
- [x] Fix VSocialButton missing background
- [x] Fix VPulseDot/VGradientText/VIconButton default params for @Composable get()
- [x] Build VDatePickerPremium
- [x] Build VTimePickerPremium
- [x] Build VSnackbarPremium
- [x] Build VPullRefreshPremium
- [x] Build VLanguagePickerPremium
- [x] Build VThemePickerPremium
- [x] Build VChartsPremium (VDonutPremium, VSparklinePremium, VBarsPremium, VLegendDotPremium)
- [x] Build VBrandLogoPremium (VBrandLogoPremium, VBridgeMarkPremium, VLogoPremium)
- [x] EXPANDED: 27 new component files added (VTextButton, VBadgePremium, VChildLinkCard, VGradientHeroPremium, VHeroCard, VListTilePremium, VProfileHeroCard, VQuickStatCard, VSchoolCardFull, VSchoolOptionCard, VStatCardPremium, VStatTile, VSurfaceCard, VStaggeredColumn, VAvatarPremium, VComingSoonPremium, VDataTablePremium, VEmptyStatePremium, VPhoneFrame, VShimmerPremium, VStateHostPremium, VStatusBar, VFilterBarPremium, VOnboardingDots, VScreenScaffoldPremium, VSegmentedToggle, VTopTabsPremium, VConfirmDialogPremium, VDialog, VFullScreenOverlay, VShimmer)
- [x] Compile check: BUILD SUCCESSFUL (both targets green)

## Phase 1: Auth & Onboarding — COMPLETE (10/10 screens)
- [x] SplashScreen
- [x] LanguageSelectionScreen
- [x] CommonLandingScreen (redesign with latest M3 Expressive, not the HTML ref)
- [x] ParentAuthScreen (login — premium M3 design)
- [x] AdminAuthScreen (staff login — premium M3 design)
- [x] AuthScaffoldPremium (shared auth layout)
- [x] SchoolOnboardingScreen
- [x] ParentLinkChildScreen
- [x] TeacherFirstLoginScreen
- [x] LegalInfoScreen
- [x] Compile check: BUILD SUCCESSFUL

## Phase 2: Parent Portal — COMPLETE (33 files)
- [x] ParentPortalShell
- [x] ParentHomeScreen
- [x] ParentAcademicsScreen
- [x] ParentFeesScreen
- [x] ParentConversationsScreen
- [x] ParentProfileScreen
- [x] ParentOverlayScaffold
- [x] + 26 overlay/sub screens (Notifications, Calendar, Scholarships, Leave, Messages, ComposeMessage, ThreadDetail, Unlinked, Discovery, Health, Pulse, Transport, TutorChat, TutorProgress, DigitalIdCard, Library, Events, ReportCard, Timetable, Homework, Quizzes, QuizDetail, SyllabusV2, Announcements, DailySummary, Leaderboard)

## Phase 3: Teacher Portal — COMPLETE (30 files)
- [x] TeacherPortalShell
- [x] TeacherHomeScreen
- [x] TeacherClassesScreen
- [x] TeacherAttendanceScreen
- [x] TeacherMarksScreen
- [x] TeacherHomeworkScreen
- [x] TeacherSyllabusScreen
- [x] TeacherTimetableScreen
- [x] TeacherMessagesScreen
- [x] TeacherProfileScreen
- [x] TeacherOverlayScaffold
- [x] + 19 overlay screens (Announcements, Calendar, ChangePassword, HealthAlerts, Leave, LessonPlan, Library, Notifications, Pews, Ptm, PtmRegistration, ReportCard, ReportDraft, ReportReview, ScopeSelector, StudentProfile, Transport, TransportAttendance, Update)

## Phase 4: Admin/School Portal — COMPLETE (48/48 screens built)
- [x] SchoolPortalPremium (shell)
- [x] SchoolHomePremium
- [x] SchoolPeoplePremium
- [x] SchoolRecordsPremium
- [x] SchoolSettingsPremium
- [x] SchoolCommsPremium
- [x] HealthRecordsPremium
- [x] LeaveRequestsPremium
- [x] PewsCohortPremium
- [x] PewsStudentDetailPremium
- [x] StaffProfilePremium
- [x] StudentProfilePremium
- [x] StudentRosterPremium
- [x] TeacherAssignmentPremium
- [x] TeacherProfilePremium
- [x] AcademicCalendarPlatformPremium
- [x] AcademicYearManagementPremium
- [x] AdminEventRegistrationPremium
- [x] AdminReportPublishPremium
- [x] AdminReportingEffectivenessPremium
- [x] AdmissionsCrmPremium
- [x] AlumniCampaignPremium
- [x] AlumniDetailPremium
- [x] AlumniPremium
- [x] AnalyticsDashboardPremium
- [x] BrandingSettingsPremium
- [x] ClassDetailPremium
- [x] ClassPerformancePremium
- [x] ClassesSubjectsPremium
- [x] DailyAttendancePremium
- [x] EditSchoolProfilePremium
- [x] IdCardPremium
- [x] LinkRequestsPremium
- [x] MessagesPremium
- [x] PaceAlertsPremium
- [x] PewsEffectivenessPremium
- [x] PewsPreviewPremium (absorbed into PewsCohortPremium)
- [x] ResultsPublishPremium
- [x] SchedulePtmPremium
- [x] ScheduledMessagesPremium
- [x] ScholarshipManagementPremium
- [x] SchoolDayConfigPremium
- [x] SchoolLibraryPremium
- [x] TeacherPerformancePremium
- [x] TransportManagementPremium
- [x] TutorManagementPremium
- [x] UnifiedCreateEventPremium
- [x] SchoolNotificationsScreen (Phase 6 cutover)
- [x] SchoolAcademicCalendarScreen (Phase 6 cutover)
- [x] SchoolOverlayScaffold (Phase 6 cutover)
- [x] Compile check: BUILD SUCCESSFUL

## Phase 5: Shared/Cross-Portal — COMPLETE (absorbed into Phases 2-3)
- [x] Discovery → ParentDiscoveryScreen
- [x] AcademicCalendar → ParentCalendarScreen + TeacherCalendarScreen
- [x] Notifications → ParentNotificationsScreen + TeacherNotificationsScreen
- [x] DigitalIdCard → ParentDigitalIdCardScreen
- [x] BusTracking → ParentTransportScreen + TeacherTransportScreen
- [x] Library → ParentLibraryScreen + TeacherLibraryScreen
- [x] TutorChat → ParentTutorChatScreen
- [x] ParentProgress → ParentTutorProgressScreen
- [x] ParentHealth → ParentHealthScreen + TeacherHealthAlertsScreen
- [x] ParentPulse → ParentPulseScreen
- [x] ParentLeave → ParentLeaveScreen + TeacherLeaveScreen
- [x] EventRegistration → ParentEventsScreen
- [x] ScholarshipWorkflow → ParentScholarshipScreen
- NOTE: Admin versions of shared screens (Library, Transport, Calendar, Messages, Events) are part of Phase 4 remaining work.

## Phase 6: Cutover — COMPLETE
- [x] Swap NavGraphV2 auth imports to premium auth screens
- [x] Swap NavGraphV2 portal imports to premium portal shells (ParentPortalShell, TeacherPortalShell)
- [x] Swap NavGraphV2 discovery import to ParentDiscoveryScreen
- [x] Update all call sites to match premium screen signatures (resumeStep, LegalInfoScreen String, onOpenSchool)
- [x] Upgrade ParentPortalShell with 16 overlay states + deep-link routing + back handler + unlinked gate
- [x] Upgrade TeacherPortalShell with 10 overlay states + deep-link routing + back handler
- [x] Build SchoolNotificationsScreen + SchoolAcademicCalendarScreen + SchoolOverlayScaffold
- [x] Swap SchoolPortalPremium V2 overlay imports to premium school screens
- [x] Swap App.kt SplashScreenV2 → premium SplashScreen
- [x] Delete old V2 auth directory (10 files) + ParentPortalV2 + ParentUnlinkedScreenV2 + TeacherPortalV2 + SchoolPortalV2
- [x] Move SUPPORT_EMAIL to premium auth LegalInfoScreen (public const)
- [x] Full build verification: BUILD SUCCESSFUL
- [x] Update this progress tracker
