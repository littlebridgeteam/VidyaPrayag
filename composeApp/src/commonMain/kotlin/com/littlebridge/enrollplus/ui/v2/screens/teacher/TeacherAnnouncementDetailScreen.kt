package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.Announcement
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.util.htmlDecode
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherSpinner

/**
 * TeacherAnnouncementDetailScreen — full-screen overlay showing a single
 * announcement. Backed by [SchoolAnnouncementsViewModel] which calls
 * `GET /api/v1/school/announcements` (now accessible to teachers via
 * `requireSchoolOrTeacherContext`).
 *
 * If [announcementId] is provided, the screen searches the loaded list for
 * the matching announcement. If not found or null, a fallback message is shown.
 */
@Composable
fun TeacherAnnouncementDetailScreen(
    announcementId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SchoolAnnouncementsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) {
        if (state.allAnnouncements.isEmpty()) {
            viewModel.loadAnnouncements()
        }
    }

    val announcement = announcementId?.let { id ->
        state.announcements.find { it.id == id }
            ?: state.allAnnouncements.find { it.id == id }
    }

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(title = "Announcement", onBack = onBack)

        when {
            state.isLoading && state.allAnnouncements.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TeacherSpinner()
                }
            }
            announcement != null -> {
                AnnouncementDetailContent(announcement = announcement)
            }
            else -> {
                Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Announcement unavailable",
                            style = VTypography.h3,
                            color = VColors.ink,
                        )
                        Text(
                            "This announcement may have been removed or is no longer available.",
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementDetailContent(announcement: Announcement) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(vertical = 20.dp),
    ) {
        if (announcement.category.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(VColors.violet))
                Text(announcement.category, style = VTypography.label, color = VColors.violet)
            }
            Spacer(Modifier.height(8.dp))
        }
        Text(announcement.title.htmlDecode(), style = VTypography.h2, color = VColors.ink)
        Text(
            announcement.date,
            style = VTypography.caption,
            color = VColors.ink3,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
        Spacer(Modifier.height(16.dp))
        Text(
            announcement.description.htmlDecode(),
            style = VTypography.caption.copy(lineHeight = 22.4.sp),
            color = VColors.ink2,
        )
    }
}
