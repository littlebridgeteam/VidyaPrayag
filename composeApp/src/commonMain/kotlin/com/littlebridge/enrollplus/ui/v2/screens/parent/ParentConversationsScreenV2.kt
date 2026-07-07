package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VMotion
import org.koin.compose.viewmodel.koinViewModel

/**
 * ParentConversationsScreenV2 — Phase 3 (commit 9).
 *
 * The renamed **Conversations** tab (was "Activity"). "Conversations" now means *real two-way
 * messaging first* — the parent ↔ teacher/office threads from [ParentMessageViewModel] — with the
 * one-way school **Announcements** feed kept a tap away on a second segment. This routes messaging
 * properly *through the tab* instead of hiding it behind a header chat-icon overlay.
 *
 * LAW (no floating toasts): the segment switch is an in-place [AnimatedContent] crossfade with a
 * spring-sliding pill indicator — no popups. Both surfaces read their own real ViewModel (no MockV2).
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParentConversationsScreenV2(
    modifier: Modifier = Modifier,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    messageViewModel: ParentMessageViewModel = koinViewModel(),
    initialSegment: ConversationsSegment? = null,
    onSegmentConsumed: () -> Unit = {},
) {
    var segment by remember { mutableStateOf(ConversationsSegment.Messages) }

    // Apply deep-link initial segment once.
    LaunchedEffect(initialSegment) {
        if (initialSegment != null) {
            segment = initialSegment
            onSegmentConsumed()
        }
    }

    val messageState by messageViewModel.state.collectAsStateV2()
    val unreadThreads = messageState.threads.count { it.unreadCount > 0 }

    // §11 — within the Messages segment, system/predictive back peels the drilled-in layers
    // (compose-new → open conversation) before the portal lets back exit the tab. On the
    // Announcements segment there is nothing to peel, so back falls through to the shell.
    val onMessages = segment == ConversationsSegment.Messages
    BackHandler(enabled = onMessages && (messageState.composeOpen || messageState.openThreadId != null)) {
        when {
            messageState.composeOpen -> messageViewModel.closeCompose()
            messageState.openThreadId != null -> messageViewModel.closeThread()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(bottom = 130.dp),
    ) {
        ParentPortalHeader(
            label = "Conversations",
            children = children,
            selectedChild = selectedChild,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            unreadNotificationsCount = unreadNotificationsCount,
        )

        PortalQuickActionChips(
            chips = listOf(
                QuickActionChipSpec(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    iconColor = VColors.violet,
                    iconBg = VColors.violetSoft,
                    title = "New\nMessage",
                    onClick = { messageViewModel.openCompose() },
                ),
                QuickActionChipSpec(
                    icon = Icons.Filled.Campaign,
                    iconColor = VColors.gold,
                    iconBg = VColors.goldSoft,
                    title = "School\nNotices",
                    onClick = { segment = ConversationsSegment.Announcements },
                ),
            ),
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(ConversationsSegment.entries) { seg ->
                PortalTabChip(
                    label = seg.label,
                    selected = segment == seg,
                    onClick = { segment = seg },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            targetState = segment,
            transitionSpec = { VMotion.quietFade() },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            label = "conversations-segment",
        ) { seg ->
            when (seg) {
                ConversationsSegment.Messages ->
                    // The chrome-less messaging surface — inbox → conversation → compose,
                    // all driven by the SAME shared ParentMessageViewModel.
                    ParentMessagesBody(
                        viewModel = messageViewModel,
                        modifier = Modifier.fillMaxSize(),
                    )
                ConversationsSegment.Announcements ->
                    // The existing one-way announcement feed (its own ViewModel via koinViewModel()).
                    ParentActivityScreenV2(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

private val ConversationsSegment.label: String
    get() = when (this) {
        ConversationsSegment.Messages -> "Messages"
        ConversationsSegment.Announcements -> "Announcements"
    }

/** The two faces of the Conversations hub. Messages is primary (the tab's reason to exist). */
enum class ConversationsSegment { Messages, Announcements }
