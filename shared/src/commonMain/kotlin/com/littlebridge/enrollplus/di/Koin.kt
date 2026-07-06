package com.littlebridge.enrollplus.di

import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.schools.data.remote.KtorSchoolApi
import com.littlebridge.enrollplus.feature.schools.data.repository.SchoolRepositoryImpl
import com.littlebridge.enrollplus.feature.schools.domain.repository.SchoolRepository
import com.littlebridge.enrollplus.feature.schools.domain.usecase.GetSchoolsUseCase
import com.littlebridge.enrollplus.presentation.MainViewModel
import com.littlebridge.enrollplus.util.AppConfig
import com.littlebridge.enrollplus.util.AppLogger
import com.littlebridge.enrollplus.core.network.buildRefreshClient
import com.littlebridge.enrollplus.core.network.clearBearerCache
import com.littlebridge.enrollplus.core.network.installTokenAuth
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val commonModule = module {
    // Remote
    single {
        val prefs: PreferenceRepository = get()
        // Plain client (NO Auth plugin) used solely to perform the refresh-token
        // exchange, so the bearer refresh path never recurses through itself.
        val refreshClient = buildRefreshClient(get())

        // Forward reference to the authenticated client so the refresh-failure
        // logout path can evict its in-memory bearer-token cache. Assigned right
        // after the client is built, before any request can run.
        lateinit var authedClient: HttpClient

        authedClient = HttpClient(get()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(HttpRedirect) {
                checkHttpMethod = false
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }

            // Auth: attach the stored access token to every request and, on a
            // 401, automatically exchange the persisted refresh token for a new
            // access token (audit §3.4, finding F). On refresh failure the user
            // is logged out cleanly. Implemented in TokenAuthenticator.kt — this
            // call is the only wiring needed; every API call through this client
            // gets transparent 401 refresh + retry.
            installTokenAuth(
                prefs = prefs,
                refreshClient = refreshClient,
                onRefreshFailed = {
                    // Reuse the existing logout primitives: clear the persisted
                    // session FIRST so loadTokens() reads null on the next request,
                    // then evict the Auth plugin's cached bearer token. The
                    // reactive authState (App.kt observes getUserToken()) sees the
                    // null token and navigates back to landing.
                    prefs.clearSession()
                    authedClient.clearBearerCache()
                },
            )
        }
        authedClient
    }
    // RA-S01: session-manager wraps the singleton HttpClient so logout can evict
    // the Ktor Auth plugin's in-memory bearer-token cache (clearToken()).
    single { com.littlebridge.enrollplus.core.network.SessionManager(get()) }
    single { KtorSchoolApi(get(), AppConfig.schoolBaseUrl) }
    single { 
        com.littlebridge.enrollplus.feature.content.data.remote.ContentApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single { 
        com.littlebridge.enrollplus.feature.auth.data.remote.AuthApi(
            client = get(),
            baseUrl = AppConfig.authBaseUrl
        ) 
    }
    single {
        com.littlebridge.enrollplus.feature.parent.data.remote.ParentApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.OnboardingApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.AdmissionApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.MessagesApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.AnnouncementsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.TeachersApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // RA-47: institutional-profile (schools row) read/edit
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.SchoolProfileApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // RA-45: student roster + student/teacher profile detail
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.StudentsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // RA-TAM: Teacher Assignment Management (reusable assignment module)
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.TeacherAssignmentApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // RA-S17: Non-teaching-staff vertical
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.StaffApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // RA-52: admin Records rollups (attendance / marks / fees)
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.RecordsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.PtmApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.CalendarApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // VP-CAL: Academic Calendar platform + Academic Year management
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.AcademicCalendarPlatformApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.AcademicYearApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.AttendanceApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.LeaveRequestsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // RA-48: school-admin link-request queue API.
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.LinkRequestsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.AnalyticsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // Redesigned admin home dashboard (summary / analytics / activity).
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.AdminDashboardApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.ResultsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.UserProfileApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.MediaApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // Teacher vertical (master doc G1)
    single {
        com.littlebridge.enrollplus.feature.teacher.data.remote.TeacherApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // Notification FOUNDATION (push infra) — device-token registration client.
    // The admin broadcast endpoint is server-only, so the client surface is a
    // single POST /api/device-tokens call invoked by the Android token registrar.
    single {
        com.littlebridge.enrollplus.feature.notification.data.remote.NotificationApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // Health Records (P1-12) — admin/nurse + teacher + parent health endpoints.
    single {
        com.littlebridge.enrollplus.feature.health.data.remote.HealthApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    // PEWS (Predictive Early Warning System) — cross-role (admin / teacher / parent).
    single {
        com.littlebridge.enrollplus.feature.pews.data.remote.PewsApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }

    // Repositories
    single<SchoolRepository> { SchoolRepositoryImpl(get(), get()) }
    single<com.littlebridge.enrollplus.feature.content.domain.repository.ContentRepository> { 
        com.littlebridge.enrollplus.feature.content.data.repository.ContentRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository> {
        // RA-S05: 4th arg = SelectedChildHolder (cleared on logout).
        // 5th arg = LocaleManager (syncs languagePref from login response).
        com.littlebridge.enrollplus.feature.auth.data.repository.AuthRepositoryImpl(get(), get(), get(), get(), get())
    }
    single<com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository> {
        com.littlebridge.enrollplus.feature.parent.data.repository.ParentRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.core.notification.NotificationFeedRepository> {
        get<com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository>()
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.OnboardingRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.OnboardingRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.AdmissionRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.AdmissionRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.MessagesRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.MessagesRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.AnnouncementsRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.AnnouncementsRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.TeachersRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.TeachersRepositoryImpl(get())
    }
    // RA-47
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolProfileRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.SchoolProfileRepositoryImpl(get())
    }
    // RA-45
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.StudentsRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.StudentsRepositoryImpl(get())
    }
    // RA-TAM: Teacher Assignment Management repository
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.TeacherAssignmentRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.TeacherAssignmentRepositoryImpl(get())
    }
    // RA-S17
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.StaffRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.StaffRepositoryImpl(get())
    }
    // RA-52
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.RecordsRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.RecordsRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.PtmRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.PtmRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.CalendarRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.CalendarRepositoryImpl(get())
    }
    // VP-CAL: Academic Calendar platform + Academic Year management repositories
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.AcademicCalendarPlatformRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.AcademicCalendarPlatformRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.AcademicYearRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.AcademicYearRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.AttendanceRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.AttendanceRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.LeaveRequestsRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.LeaveRequestsRepositoryImpl(get())
    }
    // RA-48: school-admin link-request queue repository.
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.LinkRequestsRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.LinkRequestsRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.AnalyticsRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.AnalyticsRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.AdminDashboardRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.AdminDashboardRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.ResultsRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.ResultsRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.UserProfileRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.UserProfileRepositoryImpl(get())
    }
    single<com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherRepository> {
        com.littlebridge.enrollplus.feature.teacher.data.repository.TeacherRepositoryImpl(get())
    }
    // Notification FOUNDATION repository — delegates to NotificationApi.
    single<com.littlebridge.enrollplus.feature.notification.domain.repository.NotificationRepository> {
        com.littlebridge.enrollplus.feature.notification.data.repository.NotificationRepositoryImpl(get())
    }
    // Health Records repository (P1-12)
    single<com.littlebridge.enrollplus.feature.health.domain.repository.HealthRepository> {
        com.littlebridge.enrollplus.feature.health.data.repository.HealthRepositoryImpl(get())
    }
    // PEWS repository
    single<com.littlebridge.enrollplus.feature.pews.domain.repository.PewsRepository> {
        com.littlebridge.enrollplus.feature.pews.data.repository.PewsRepositoryImpl(get())
    }

    // AI Report Card 2.0 — cross-role (teacher / admin / parent)
    single {
        com.littlebridge.enrollplus.feature.reportcard.data.remote.ReportCardApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.reportcard.domain.repository.ReportCardRepository> {
        com.littlebridge.enrollplus.feature.reportcard.data.repository.ReportCardRepositoryImpl(get())
    }

    // Alumni Management (ALUMNI_MANAGEMENT_SPEC.md)
    single {
        com.littlebridge.enrollplus.feature.alumni.data.remote.AlumniApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.alumni.domain.repository.AlumniRepository> {
        com.littlebridge.enrollplus.feature.alumni.data.repository.AlumniRepositoryImpl(get())
    }

    // Transport Tracking (TRANSPORT_TRACKING_SPEC.md)
    single {
        com.littlebridge.enrollplus.feature.transport.data.remote.TransportApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.transport.domain.repository.TransportRepository> {
        com.littlebridge.enrollplus.feature.transport.data.repository.TransportRepositoryImpl(get())
    }

    // Scholarship Workflow (SCHOLARSHIP_WORKFLOW_SPEC.md)
    single {
        com.littlebridge.enrollplus.feature.scholarship.data.remote.ScholarshipApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.scholarship.domain.repository.ScholarshipRepository> {
        com.littlebridge.enrollplus.feature.scholarship.data.repository.ScholarshipRepositoryImpl(get())
    }

    // School Branding Kit (SCHOOL_BRANDING_KIT_SPEC.md)
    single {
        com.littlebridge.enrollplus.feature.branding.data.remote.BrandingApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.branding.domain.repository.BrandingRepository> {
        com.littlebridge.enrollplus.feature.branding.data.repository.BrandingRepositoryImpl(get())
    }
    // ID Card Generation (ID_CARD_GENERATION_SPEC.md)
    single {
        com.littlebridge.enrollplus.feature.idcard.data.remote.IdCardApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.idcard.domain.repository.IdCardRepository> {
        com.littlebridge.enrollplus.feature.idcard.data.repository.IdCardRepositoryImpl(get())
    }

    // Message Scheduling (MESSAGE_SCHEDULING_PLAN.md §7)
    single {
        com.littlebridge.enrollplus.feature.scheduling.data.remote.ScheduledMessageApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.scheduling.domain.repository.ScheduledMessageRepository> {
        com.littlebridge.enrollplus.feature.scheduling.data.repository.ScheduledMessageRepositoryImpl(get())
    }

    // Event Registration & RSVP System (EVENT_REGISTRATION_PLAN.md §4)
    single {
        com.littlebridge.enrollplus.feature.event.data.remote.EventRegistrationApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.event.domain.repository.EventRegistrationRepository> {
        com.littlebridge.enrollplus.feature.event.data.repository.EventRegistrationRepositoryImpl(get())
    }

    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.SchoolDayConfigApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolDayConfigRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.SchoolDayConfigRepositoryImpl(get())
    }

    // Timetable AI Import (OCR + text parsing)
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.TimetableImportApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }

    // Classes & Subjects management (consolidated admin screen)
    single {
        com.littlebridge.enrollplus.feature.admin.data.remote.SchoolClassesApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolClassesRepository> {
        com.littlebridge.enrollplus.feature.admin.data.repository.SchoolClassesRepositoryImpl(get())
    }

    // UseCases
    factory { GetSchoolsUseCase(get()) }

    // ── Multi-Language i18n (MULTI_LANGUAGE_SPEC.md) ──────────────────
    single {
        com.littlebridge.enrollplus.feature.i18n.data.remote.LanguageApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.i18n.domain.repository.LanguageRepository> {
        com.littlebridge.enrollplus.feature.i18n.data.repository.LanguageRepositoryImpl(get())
    }
    factory { com.littlebridge.enrollplus.feature.i18n.domain.usecase.GetLanguagePrefUseCase(get()) }
    factory { com.littlebridge.enrollplus.feature.i18n.domain.usecase.UpdateLanguagePrefUseCase(get()) }
    single { com.littlebridge.enrollplus.core.locale.NetworkMonitor() }
    single { com.littlebridge.enrollplus.core.locale.LocaleManager(get(), get(), get()) }

    // AI Tutor 2.0 — API + repository
    single {
        com.littlebridge.enrollplus.feature.tutor.data.remote.TutorApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.tutor.domain.repository.TutorRepository> {
        com.littlebridge.enrollplus.feature.tutor.data.repository.TutorRepositoryImpl(get())
    }

    // Library Management (LIBRARY_MANAGEMENT_SPEC.md)
    single {
        com.littlebridge.enrollplus.feature.library.data.remote.LibraryApi(
            client = get(),
            baseUrl = AppConfig.schoolBaseUrl
        )
    }
    single<com.littlebridge.enrollplus.feature.library.domain.repository.LibraryRepository> {
        com.littlebridge.enrollplus.feature.library.data.repository.LibraryRepositoryImpl(get(), getOrNull())
    }
}

val viewModelModule = module {
    factory { MainViewModel(get(), get(), get(), get()) }
    factory { com.littlebridge.enrollplus.presentation.PermissionViewModel(get(), get()) }
    factory { com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel(get()) }
    single { com.littlebridge.enrollplus.core.state.SelectedChildHolder() }
}

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(commonModule, viewModelModule, platformModule())
}

// For iOS
fun initKoin() = initKoin {}
