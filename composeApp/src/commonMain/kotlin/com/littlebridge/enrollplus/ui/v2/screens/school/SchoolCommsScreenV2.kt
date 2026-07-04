package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.Announcement
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VComingSoon
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import org.koin.compose.viewmodel.koinViewModel

/**
 * SchoolCommsScreenV2 — `Admin.tsx → Comms`, wired to the real
 * [SchoolAnnouncementsViewModel] (`AnnouncementsApi` → `GET/POST /api/v1/announcements`).
 *
 * The **Announcements** tab renders real announcements from the server (title, category,
 * date) with category filtering, and a detail leaf. The **Messages**, **PTM** and
 * **Notifications** tabs are dedicated backends/screens that don't exist yet (Phase D/E),
 * so they're shown as `VComingSoon` rather than fabricating data (LAW 6). No MockV2 in
 * production; the three UI states come from [VStateHost].
 */
@Composable
fun SchoolCommsScreenV2(
    modifier: Modifier = Modifier,
    onOpenMessages: () -> Unit = {},
    onOpenPtm: () -> Unit = {},
    onOpenScheduledMessages: () -> Unit = {},
    onCreateEvent: () -> Unit = {},
    viewModel: SchoolAnnouncementsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    SchoolCommsContent(
        state = state,
        onRetry = viewModel::loadAnnouncements,
        onSelectCategory = viewModel::setCategoryFilter,
        onCreateEvent = onCreateEvent,
        onOpenMessages = onOpenMessages,
        onOpenPtm = onOpenPtm,
        onOpenScheduledMessages = onOpenScheduledMessages,
        modifier = modifier.statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    )
}

@Composable
private fun SchoolCommsContent(
    state: SchoolAnnouncementsState,
    onRetry: () -> Unit,
    onSelectCategory: (String?) -> Unit,
    onCreateEvent: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenPtm: () -> Unit,
    onOpenScheduledMessages: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors
    var tab by remember { mutableStateOf("Announcements") }
    var openAnnouncement by remember { mutableStateOf<String?>(null) }

    // Mirror React `Comms`: tapping a card opens an AnnouncementDetail leaf.
    openAnnouncement?.let { id ->
        AnnouncementDetailV2(
            announcement = state.announcements.find { it.id == id }
                ?: state.allAnnouncements.find { it.id == id },
            onBack = { openAnnouncement = null },
            modifier = modifier,
        )
        return
    }

    // Feature 7 — pull-to-refresh on this scrollable list screen. `isRefreshing`
    // tracks the load flag; `onRefresh` re-runs the announcements fetch. On
    // completion the announcement cards re-enter via the Feature 5 staggered
    // ladder already wired below.
    VPullRefresh(
        isRefreshing = state.isLoading,
        onRefresh = onRetry,
        modifier = modifier.fillMaxSize(),
    ) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(appString(StringKeys.SCH_COMMUNICATIONS), style = VTheme.type.h1.colored(c.ink))
        val tabLabels = listOf(
            appString(StringKeys.SCH_ANNOUNCEMENTS),
            appString(StringKeys.SCH_MESSAGES),
            appString(StringKeys.SCH_PTM),
            appString(StringKeys.SCH_NOTIFICATIONS),
        )
        VTopTabs(
            tabs = tabLabels,
            selected = tab,
            onSelect = { tab = it },
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when (tab) {
                tabLabels[0] -> AnnouncementsTab(
                    state = state,
                    onRetry = onRetry,
                    onSelectCategory = onSelectCategory,
                    onOpen = { openAnnouncement = it },
                    onCreateEvent = onCreateEvent,
                    onOpenScheduledMessages = onOpenScheduledMessages,
                )
                tabLabels[1] -> CommsEntryCard(
                    icon = VIcons.Chat,
                    title = appString(StringKeys.SCH_PARENT_MESSAGES),
                    description = appString(StringKeys.SCH_PARENT_MESSAGES_DESC),
                    onClick = onOpenMessages,
                )
                tabLabels[2] -> CommsEntryCard(
                    icon = VIcons.Calendar,
                    title = appString(StringKeys.SCH_PARENT_TEACHER_MEETINGS),
                    description = appString(StringKeys.SCH_PARENT_TEACHER_MEETINGS_DESC),
                    onClick = onOpenPtm,
                )
                tabLabels[3] -> VComingSoon(
                    title = appString(StringKeys.SCH_DELIVERY_LOG),
                    description = appString(StringKeys.SCH_DELIVERY_LOG_DESC),
                )
            }
        }
    }
    }
}

@Composable
private fun AnnouncementsTab(
    state: SchoolAnnouncementsState,
    onRetry: () -> Unit,
    onSelectCategory: (String?) -> Unit,
    onOpen: (String) -> Unit,
    onCreateEvent: () -> Unit,
    onOpenScheduledMessages: () -> Unit,
) {
    val c = VTheme.colors

    // Compose button lives ABOVE the state host so an admin can post the very
    // first announcement even when the list is empty (RA-23). Frozen primitives.
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(appString(StringKeys.SCH_ANNOUNCEMENTS), style = VTheme.type.h3.colored(c.ink))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VButton(
                text = appString(StringKeys.SCH_SCHEDULED),
                onClick = onOpenScheduledMessages,
                variant = VButtonVariant.Ghost,
                size = VButtonSize.Sm,
                leading = { Icon(VIcons.Clock, contentDescription = null, modifier = Modifier.size(14.dp)) },
            )
            VButton(
                text = appString(StringKeys.SCH_NEW),
                onClick = onCreateEvent,
                variant = VButtonVariant.Primary,
                size = VButtonSize.Sm,
                leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(14.dp)) },
                enabled = !state.isCreating,
            )
        }
    }
    Spacer(Modifier.height(12.dp))

    VStateHost(
        loading = state.isLoading,
        error = state.errorMessage,
        isEmpty = state.announcements.isEmpty(),
        emptyTitle = appString(StringKeys.SCH_NO_ANNOUNCEMENTS),
        emptyBody = appString(StringKeys.SCH_NO_ANNOUNCEMENTS_DESC),
        emptyIcon = VIcons.Megaphone,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonAnnouncements() },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Category filter chips derived from the loaded data.
            val categories = remember(state.allAnnouncements) {
                state.allAnnouncements.map { it.category }.filter { it.isNotBlank() }.distinct()
            }
            if (categories.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(appString(StringKeys.SCH_ALL), state.selectedCategory == null) { onSelectCategory(null) }
                    categories.forEach { cat ->
                        FilterChip(cat, state.selectedCategory.equals(cat, ignoreCase = true)) { onSelectCategory(cat) }
                    }
                }
            }
            // Feature 5 — staggered list entrance for announcement cards once
            // VStateHost flips from skeleton → content. `ready` only flips on
            // the *initial* data-load; subsequent refreshes keep it true so
            // items never re-animate (RULE-2: no jank).
            val ready = state.announcements.isNotEmpty() && !state.isLoading
            state.announcements.forEachIndexed { index, a ->
                Box(modifier = Modifier.staggeredItemEntrance(index = index, trigger = ready)) {
                    VCard(onClick = { onOpen(a.id) }) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(a.title, style = VTheme.type.bodyStrong.colored(c.ink), modifier = Modifier.weight(1f))
                            if (a.isCalendarOnly) {
                                VBadge(text = appString(StringKeys.SCH_CALENDAR_ONLY), tone = VBadgeTone.Warning)
                            } else if (a.category.isNotBlank()) {
                                VBadge(text = a.category, tone = VBadgeTone.Arctic)
                            }
                        }
                        if (a.date.isNotBlank()) {
                            Text(a.date, style = VTheme.type.caption.colored(c.ink2), modifier = Modifier.padding(top = 2.dp))
                        }
                        if (a.description.isNotBlank()) {
                            Text(
                                a.description,
                                style = VTheme.type.caption.colored(c.ink2),
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * RA-24: tappable entry card opening an existing, backend-backed Comms screen
 * (Messages / PTM). Frozen V* primitives only.
 */
@Composable
private fun CommsEntryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    val c = VTheme.colors
    VCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(c.teal.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(18.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = VTheme.type.bodyStrong.colored(c.ink))
                Text(description, style = VTheme.type.caption.colored(c.ink2))
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = c.ink3, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    val c = VTheme.colors
    val (bg, fg) = if (active) c.teal.copy(alpha = 0.16f) to c.tealDeep else c.cream to c.ink2
    Text(
        label,
        style = VTheme.type.label.colored(fg),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

/**
 * AnnouncementDetailV2 — title, category + date, body copy. Renders from the real
 * [Announcement] model (no recipients/opens fields exist on the server model, so the
 * old mock-only "Delivery" stats are intentionally dropped — LAW 6).
 */
@Composable
private fun AnnouncementDetailV2(
    announcement: Announcement?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors
    Column(modifier.fillMaxSize()) {
        VBackHeader(title = appString(StringKeys.SCH_ANNOUNCEMENT), onBack = onBack)
        if (announcement == null) {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Text(appString(StringKeys.SCH_ANNOUNCEMENT_UNAVAILABLE), style = VTheme.type.h3.colored(c.ink))
            }
            return
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(vertical = 20.dp),
        ) {
            Text(announcement.title, style = VTheme.type.h2.colored(c.ink))
            Text(
                appString(StringKeys.SCH_POSTED_BY, "date" to announcement.date),
                style = VTheme.type.caption.colored(c.ink2),
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(16.dp))
            if (announcement.category.isNotBlank()) {
                VBadge(text = announcement.category, tone = VBadgeTone.Arctic)
            }
            Text(
                announcement.description,
                style = VTheme.type.body.colored(c.ink2).copy(lineHeight = 22.4.sp),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}
