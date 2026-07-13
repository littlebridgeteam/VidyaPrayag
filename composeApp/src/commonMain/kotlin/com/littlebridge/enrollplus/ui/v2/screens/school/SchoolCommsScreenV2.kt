package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogItem
import com.littlebridge.enrollplus.feature.admin.domain.model.MessageThread
import com.littlebridge.enrollplus.feature.admin.presentation.Announcement
import com.littlebridge.enrollplus.feature.admin.presentation.CommsDeliveryLogViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.MessagesViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.PTMHistoryItem
import com.littlebridge.enrollplus.feature.admin.presentation.SchedulePTMViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VComingSoon
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.VMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

/**
 * SchoolCommsScreenV2 — `Admin.tsx → Comms`, wired to the real
 * [SchoolAnnouncementsViewModel] (`AnnouncementsApi` → `GET/POST /api/v1/announcements`).
 *
 * The **Announcements** tab renders real announcements from the server (title, category,
 * date) with category filtering, and a detail leaf. The **Messages** and **PTM** tabs are
 * entry cards that open their dedicated backend-backed screens. The **Notifications** tab
 * is shown as `VComingSoon` (Phase D/E) rather than fabricating data (LAW 6). No MockV2
 * in production; the three UI states come from [VStateHost].
 */
@Composable
fun SchoolCommsScreenV2(
    modifier: Modifier = Modifier,
    onOpenMessages: () -> Unit = {},
    onOpenPtm: () -> Unit = {},
    onOpenScheduledMessages: () -> Unit = {},
    onOpenDeliveryLog: () -> Unit = {},
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
        onOpenDeliveryLog = onOpenDeliveryLog,
        modifier = modifier.statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    )
}

private enum class CommsSubTab {
    Announcements, Messages, Ptm, Notifications;

    @Composable
    fun label(): String = when (this) {
        Announcements -> appString(StringKeys.SCH_ANNOUNCEMENTS)
        Messages -> appString(StringKeys.SCH_MESSAGES)
        Ptm -> appString(StringKeys.SCH_PTM)
        Notifications -> appString(StringKeys.SCH_NOTIFICATIONS)
    }
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
    onOpenDeliveryLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var openAnnouncement by remember { mutableStateOf<String?>(null) }

    openAnnouncement?.let { id ->
        AnnouncementDetailV2(
            announcement = state.announcements.find { it.id == id }
                ?: state.allAnnouncements.find { it.id == id },
            onBack = { openAnnouncement = null },
            modifier = modifier,
        )
        return
    }

    var subTab by remember { mutableStateOf(CommsSubTab.Announcements) }
    val subTabLabels = CommsSubTab.entries.map { it.label() }
    val pagerState = rememberPagerState(pageCount = { CommsSubTab.entries.size })

    // Stagger entrance
    val headerAlpha = remember { Animatable(0f) }
    val headerOffset = remember { Animatable(20f) }
    LaunchedEffect(Unit) {
        headerAlpha.snapTo(0f); headerOffset.snapTo(20f)
        launch {
            delay(100)
            headerAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
            headerOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
        }
    }

    // Sync tab taps with pager.
    LaunchedEffect(subTab) {
        val page = subTab.ordinal
        if (pagerState.currentPage != page) {
            pagerState.animateScrollToPage(page)
        }
    }
    // Sync pager swipes with tabs.
    LaunchedEffect(pagerState.currentPage) {
        subTab = CommsSubTab.entries[pagerState.currentPage]
    }

    VPullRefresh(
        isRefreshing = state.isLoading,
        onRefresh = onRetry,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Premium header
            Column(
                modifier = Modifier
                    .graphicsLayer(translationY = headerOffset.value)
                    .alpha(headerAlpha.value),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Box(Modifier.size(5.dp).clip(CircleShape).background(VTheme.colors.violet))
                    Text(
                        appString(StringKeys.SCH_COMMUNICATIONS),
                        style = VTheme.type.accentLabel,
                        color = VTheme.colors.violet,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VTheme.colors.ink)) {
                            append("Comms")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VTheme.colors.ink2)) {
                            append(" Hub")
                        }
                    },
                    style = VTheme.type.h2,
                )
            }

            VTopTabs(
                tabs = subTabLabels,
                selected = subTabLabels[subTab.ordinal],
                onSelect = { label ->
                    subTab = CommsSubTab.entries[subTabLabels.indexOf(label)]
                },
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) { page ->
                when (CommsSubTab.entries[page]) {
                    CommsSubTab.Announcements -> AnnouncementsTab(
                        state = state,
                        onRetry = onRetry,
                        onSelectCategory = onSelectCategory,
                        onOpen = { openAnnouncement = it },
                        onCreateEvent = onCreateEvent,
                        onOpenScheduledMessages = onOpenScheduledMessages,
                    )
                    CommsSubTab.Messages -> MessagesTab(onOpenMessages = onOpenMessages)
                    CommsSubTab.Ptm -> PtmTab(onOpenPtm = onOpenPtm)
                    CommsSubTab.Notifications -> NotificationsTab(onOpenDeliveryLog = onOpenDeliveryLog)
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
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(appString(StringKeys.SCH_ANNOUNCEMENTS), style = VTheme.type.label, color = VTheme.colors.ink3)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.SCH_SCHEDULED),
                    onClick = onOpenScheduledMessages,
                    variant = VButtonVariant.Ghost,
                    size = VButtonSize.Sm,
                    leading = { Icon(VIcons.Clock, contentDescription = "", modifier = Modifier.size(14.dp)) },
                )
                VButton(
                    text = appString(StringKeys.SCH_NEW),
                    onClick = onCreateEvent,
                    variant = VButtonVariant.Primary,
                    size = VButtonSize.Sm,
                    leading = { Icon(VIcons.Plus, contentDescription = "", modifier = Modifier.size(14.dp)) },
                    enabled = !state.isCreating,
                )
            }
        }

        VStateHost(
            modifier = Modifier.fillMaxWidth().weight(1f),
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.announcements.isEmpty(),
            emptyTitle = appString(StringKeys.SCH_NO_ANNOUNCEMENTS),
            emptyBody = appString(StringKeys.SCH_NO_ANNOUNCEMENTS_DESC),
            emptyIcon = VIcons.Megaphone,
            onRetry = onRetry,
            skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonAnnouncements() },
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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
                state.announcements.forEachIndexed { index, a ->
                    AnnouncementCard(
                        announcement = a,
                        onClick = { onOpen(a.id) },
                        index = index,
                    )
                }
            }
        }
    }
}

@Composable
private fun MessagesTab(
    onOpenMessages: () -> Unit,
    viewModel: MessagesViewModel = koinViewModel(),
) {
    val messagesState by viewModel.state.collectAsStateV2()
    val isLoading by viewModel.isLoading.collectAsStateV2()
    val errorMessage by viewModel.errorMessage.collectAsStateV2()

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(appString(StringKeys.SCH_MESSAGES), style = VTheme.type.label, color = VTheme.colors.ink3)
        VStateHost(
            modifier = Modifier.fillMaxWidth().weight(1f),
            loading = isLoading,
            error = errorMessage,
            isEmpty = messagesState.threads.isEmpty(),
            emptyTitle = appString(StringKeys.SCH_NO_MESSAGES),
            emptyBody = appString(StringKeys.SCH_NO_MESSAGES_DESC),
            emptyIcon = VIcons.Chat,
            skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 5) },
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                messagesState.threads.take(5).forEachIndexed { index, thread ->
                    MessagePreviewCard(
                        thread = thread,
                        onClick = onOpenMessages,
                        index = index,
                    )
                }
                CommsEntryCard(
                    icon = VIcons.Chat,
                    title = appString(StringKeys.SCH_SEE_ALL_MESSAGES),
                    description = appString(StringKeys.SCH_SEE_ALL_MESSAGES_DESC),
                    onClick = onOpenMessages,
                )
            }
        }
    }
}

@Composable
private fun PtmTab(
    onOpenPtm: () -> Unit,
    viewModel: SchedulePTMViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(appString(StringKeys.SCH_PTM), style = VTheme.type.label, color = VTheme.colors.ink3)
        VStateHost(
            modifier = Modifier.fillMaxWidth().weight(1f),
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.history.isEmpty(),
            emptyTitle = appString(StringKeys.SCH_NO_PTMS_YET),
            emptyBody = appString(StringKeys.SCH_NO_PTMS_DESC),
            emptyIcon = VIcons.Calendar,
            skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 5) },
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.history.take(5).forEachIndexed { index, item ->
                    PtmPreviewCard(
                        item = item,
                        onClick = onOpenPtm,
                        index = index,
                    )
                }
                CommsEntryCard(
                    icon = VIcons.Calendar,
                    title = appString(StringKeys.SCH_SEE_ALL_PTM),
                    description = appString(StringKeys.SCH_SEE_ALL_PTM_DESC),
                    onClick = onOpenPtm,
                )
            }
        }
    }
}

@Composable
private fun NotificationsTab(
    onOpenDeliveryLog: () -> Unit,
    viewModel: CommsDeliveryLogViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(appString(StringKeys.SCH_DELIVERY_LOG), style = VTheme.type.label, color = VTheme.colors.ink3)
        VStateHost(
            modifier = Modifier.fillMaxWidth().weight(1f),
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.items.isEmpty(),
            emptyTitle = appString(StringKeys.SCH_NO_DELIVERY_LOG),
            emptyBody = appString(StringKeys.SCH_NO_DELIVERY_LOG_DESC),
            emptyIcon = VIcons.Bell,
            skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 5) },
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.items.take(5).forEachIndexed { index, item ->
                    DeliveryLogRowCard(
                        item = item,
                        onClick = onOpenDeliveryLog,
                        index = index,
                    )
                }
                CommsEntryCard(
                    icon = VIcons.Bell,
                    title = appString(StringKeys.SCH_SEE_ALL_DELIVERY_LOG),
                    description = appString(StringKeys.SCH_SEE_ALL_DELIVERY_LOG_DESC),
                    onClick = onOpenDeliveryLog,
                )
            }
        }
    }
}


@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    val (bg, fg) = if (active) VTheme.colors.violetSoft to VTheme.colors.violet else VTheme.colors.creamDeep to VTheme.colors.ink3
    Text(
        label,
        style = VTheme.type.caption.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.Medium),
        color = fg,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
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
    Column(modifier.fillMaxSize()) {
        VBackHeader(title = appString(StringKeys.SCH_ANNOUNCEMENT), onBack = onBack)
        if (announcement == null) {
            Column(Modifier.fillMaxSize().padding(24.dp)) {
                Text(appString(StringKeys.SCH_ANNOUNCEMENT_UNAVAILABLE), style = VTheme.type.h3, color = VTheme.colors.ink)
            }
            return
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(vertical = 20.dp),
        ) {
            // Accent dot + category
            if (announcement.category.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Box(Modifier.size(5.dp).clip(CircleShape).background(VTheme.colors.violet))
                    Text(announcement.category, style = VTheme.type.accentLabel, color = VTheme.colors.violet)
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(announcement.title, style = VTheme.type.h2, color = VTheme.colors.ink)
            Text(
                appString(StringKeys.SCH_POSTED_BY, "date" to announcement.date),
                style = VTheme.type.caption,
                color = VTheme.colors.ink3,
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(VTheme.colors.lineSoft))
            Spacer(Modifier.height(16.dp))
            Text(
                announcement.description,
                style = VTheme.type.bodySmall.copy(lineHeight = 22.4.sp),
                color = VTheme.colors.ink2,
            )
        }
    }
}


