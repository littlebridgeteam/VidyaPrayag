package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.presentation.InstitutionalProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VLanguagePickerPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VThemePickerPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolSettingsPremium(
    onLogout: () -> Unit = {},
    onOpenTeachers: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenAcademicYear: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenScholarships: () -> Unit = {},
    onOpenBranding: () -> Unit = {},
    onOpenIdCards: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenClassesSubjects: () -> Unit = {},
    onOpenFees: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    viewModel: InstitutionalProfileViewModel = koinViewModel(),
    preferenceRepository: PreferenceRepository = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val themeMode by preferenceRepository.getThemeMode().collectAsState(initial = "system")
    val localeManager = koinInject<LocaleManager>()
    val currentLocale by localeManager.currentLocale.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Settings", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface), modifier = Modifier.padding(horizontal = 20.dp))

        if (state.profileCompletion > 0) {
            VStatCardPremium(
                value = "${state.profileCompletion}%",
                label = "Profile Completion",
                onClick = onOpenProfile,
                icon = VIcons.Settings,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        Text("Management", style = VTypography.SectionHeader.copy(color = VColors.OnSurface), modifier = Modifier.padding(horizontal = 20.dp))
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            VListTilePremium(title = "Teacher Management", subtitle = "View and manage teaching staff", onClick = onOpenTeachers, leadingIcon = VIcons.GraduationCap)
            VListTilePremium(title = "Edit School Profile", subtitle = "Update institutional information", onClick = onOpenProfile, leadingIcon = VIcons.Settings)
            VListTilePremium(title = "Academic Year", subtitle = "Manage academic years and terms", onClick = onOpenAcademicYear, leadingIcon = VIcons.Calendar)
            VListTilePremium(title = "Transport Management", subtitle = "Routes, vehicles & assignments", onClick = onOpenTransport, leadingIcon = VIcons.MapPin)
            VListTilePremium(title = "Scholarship Management", subtitle = "Schemes, applications & renewals", onClick = onOpenScholarships, leadingIcon = VIcons.Wallet)
            VListTilePremium(title = "Branding Kit", subtitle = "Colors, logo, subdomain", onClick = onOpenBranding, leadingIcon = VIcons.Sparkles)
            VListTilePremium(title = "ID Cards", subtitle = "Templates, generation, PDF export", onClick = onOpenIdCards, leadingIcon = VIcons.FileText)
            VListTilePremium(title = "Library Management", subtitle = "Catalog, issues, returns, fines", onClick = onOpenLibrary, leadingIcon = VIcons.BookOpen)
            VListTilePremium(title = "Classes & Subjects", subtitle = "Classes, subjects, bell schedule, timetable", onClick = onOpenClassesSubjects, leadingIcon = VIcons.Bookmark)
            VListTilePremium(title = "Fee Structure", subtitle = "Fee ledger and collection", onClick = onOpenFees, leadingIcon = VIcons.Wallet)
            VListTilePremium(title = "Notifications", subtitle = "Notification center", onClick = onOpenNotifications, leadingIcon = VIcons.Bell)
        }

        Text("Preferences", style = VTypography.SectionHeader.copy(color = VColors.OnSurface), modifier = Modifier.padding(horizontal = 20.dp))
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            VThemePickerPremium(
                currentMode = themeMode,
                onSelect = { mode ->
                    scope.launch {
                        preferenceRepository.setThemeMode(mode)
                    }
                },
            )
            VLanguagePickerPremium(
                currentLang = currentLocale,
                onSelect = { lang -> localeManager.setLocale(lang) },
            )
        }

        Text("Account", style = VTypography.SectionHeader.copy(color = VColors.OnSurface), modifier = Modifier.padding(horizontal = 20.dp))
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            VListTilePremium(
                title = "Logout",
                subtitle = "Sign out of admin portal",
                onClick = onLogout,
                leadingIcon = VIcons.Close,
            )
        }
    }
}
