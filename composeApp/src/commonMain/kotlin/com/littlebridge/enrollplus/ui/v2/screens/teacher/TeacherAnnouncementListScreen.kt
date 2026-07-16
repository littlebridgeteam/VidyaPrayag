package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.Announcement
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.util.htmlDecode
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherAnnouncementListScreen(
    onBack: () -> Unit,
    onOpenAnnouncement: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SchoolAnnouncementsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VtC

    LaunchedEffect(Unit) {
        if (state.allAnnouncements.isEmpty()) {
            viewModel.loadAnnouncements()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Announcements", onBack = onBack)

        VStateHost(
            loading = state.isLoading && state.allAnnouncements.isEmpty(),
            error = state.errorMessage,
            isEmpty = state.allAnnouncements.isEmpty(),
            emptyTitle = "No announcements yet",
            emptyBody = "School announcements will appear here when published.",
            emptyIcon = VIcons.Megaphone,
            onRetry = { viewModel.loadAnnouncements() },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = TeacherDockClearance),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.allAnnouncements, key = { it.id }) { ann ->
                    AnnouncementRow(
                        announcement = ann,
                        onClick = { onOpenAnnouncement(ann.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnouncementRow(
    announcement: Announcement,
    onClick: () -> Unit,
) {
    VtCard(onClick = onClick) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (announcement.category.isNotBlank()) {
                Text(
                    announcement.category,
                    style = VTypography.label,
                    color = VColors.violet,
                )
            }
            Text(
                announcement.title.htmlDecode(),
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                announcement.description.htmlDecode(),
                style = VTypography.caption,
                color = VColors.ink2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                announcement.date,
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
    }
}
