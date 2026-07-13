package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * ParentActivityScreenV2 — school announcements feed, rebuilt with the same premium parent-portal
 * design language as the Academics tab. Cream background, white pill filter chips, tonal cards,
 * category-tinted iconography, and inline loading/error/empty states.
 *
 * Wired to the real [ParentAnnouncementViewModel] → `ParentRepository.getAnnouncements` →
 * `GET /api/v1/parent/announcements`. MockV2 is no longer referenced.
 */
@Composable
fun ParentActivityScreenV2(
    modifier: Modifier = Modifier,
    viewModel: ParentAnnouncementViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentActivityContent(state = state, modifier = modifier)
}

/** Stateless body — also used by @Preview with seeded state (no MockV2 in the live path). */
@Composable
private fun ParentActivityContent(
    state: ParentAnnouncementState,
    modifier: Modifier = Modifier,
) {
    var filter by remember { mutableStateOf("All") }

    // Build the filter set from the real announcement categories + the "All" pseudo-filter.
    val categories = state.announcements.map { it.category }.distinct()
    val filters = listOf("All") + categories
    val filtered = if (filter == "All") {
        state.announcements
    } else {
        state.announcements.filter { it.category.equals(filter, ignoreCase = true) }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        Text(
            "Announcements",
            style = VTheme.type.h3,
            color = VTheme.colors.ink,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )

        when {
            state.isLoading && state.announcements.isEmpty() ->
                Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VTheme.colors.violet, modifier = Modifier.size(36.dp))
                }

            state.error != null && state.announcements.isEmpty() ->
                EmptyStateCard(
                    title = "Couldn't load announcements",
                    body = state.error ?: "Something went wrong",
                )

            state.announcements.isEmpty() ->
                EmptyStateCard(
                    title = "All caught up",
                    body = "New announcements from your school will show up here.",
                )

            else -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filters.size) { idx ->
                        val f = filters[idx]
                        PortalTabChip(
                            label = f,
                            selected = filter == f,
                            onClick = { filter = f },
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    filtered.forEach { a -> AnnouncementCard(a) }
                }

                if (state.isWhatsAppSyncEnabled) {
                    Spacer(Modifier.height(16.dp))
                    WhatsAppSyncBanner()
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(a: ParentAnnouncement) {
    val (tint, icon) = when (a.category.lowercase()) {
        "holidays", "holiday" -> VTheme.colors.violet to VIcons.Calendar
        "ptm" -> VTheme.colors.gold to VIcons.UsersGroup
        "events", "event" -> Color(0xFF6C8DF5) to VIcons.Star
        "reminder" -> VTheme.colors.error to VIcons.Clock
        else -> VTheme.colors.sky to VIcons.Bell
    }

    val cardBg = if (a.isFeatured) VTheme.colors.violetSoft else VTheme.colors.surfaceCard
    val borderColor = if (a.isFeatured) VTheme.colors.violet.copy(alpha = 0.3f) else VTheme.colors.line

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = "", tint = tint, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                a.title,
                style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                color = VTheme.colors.ink,
            )
            if (a.description.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    a.description,
                    style = VTheme.type.caption,
                    color = VTheme.colors.ink2,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                a.date,
                style = VTheme.type.caption.copy(fontSize = 11.sp),
                color = VTheme.colors.ink3,
            )
        }
        if (a.isFeatured) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Featured",
                tint = VTheme.colors.violet,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    body: String,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(56.dp).clip(CircleShape).background(VTheme.colors.creamDeep),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Bell, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(title, style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink)
        Spacer(Modifier.height(4.dp))
        Text(body, style = VTheme.type.caption, color = VTheme.colors.ink2)
    }
}

@Composable
private fun WhatsAppSyncBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.successSoft)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(VTheme.colors.success.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Megaphone, contentDescription = "", tint = VTheme.colors.success, modifier = Modifier.size(18.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                "WhatsApp sync is on",
                style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                color = VTheme.colors.ink,
            )
            Text(
                "You'll also receive these updates on WhatsApp.",
                style = VTheme.type.caption,
                color = VTheme.colors.ink2,
            )
        }
    }
}
