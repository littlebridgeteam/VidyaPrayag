package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.Announcement
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolAnnouncementsViewModel
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
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    var tab by remember { mutableStateOf("Announcements") }
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

    VPullRefresh(
        isRefreshing = state.isLoading,
        onRefresh = onRetry,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 140.dp),
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
                    Box(Modifier.size(5.dp).clip(CircleShape).background(VColors.violet))
                    Text(
                        appString(StringKeys.SCH_COMMUNICATIONS),
                        style = VTypography.accentLabel,
                        color = VColors.violet,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VColors.ink)) {
                            append("Comms")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                            append(" Hub")
                        }
                    },
                    style = VTypography.h2,
                )
            }

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
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(appString(StringKeys.SCH_ANNOUNCEMENTS), style = VTypography.label, color = VColors.ink3)
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
                CommsStaggeredItem(index = index) {
                    CreamCard(onClick = { onOpen(a.id) }) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                a.title,
                                style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = VColors.ink,
                                modifier = Modifier.weight(1f),
                            )
                            if (a.isCalendarOnly) {
                                MiniBadge(text = appString(StringKeys.SCH_CALENDAR_ONLY), color = VColors.gold, bg = VColors.goldSoft)
                            } else if (a.category.isNotBlank()) {
                                MiniBadge(text = a.category, color = VColors.violet, bg = VColors.violetSoft)
                            }
                        }
                        if (a.date.isNotBlank()) {
                            Text(a.date, style = VTypography.caption, color = VColors.ink3, modifier = Modifier.padding(top = 2.dp))
                        }
                        if (a.description.isNotBlank()) {
                            Text(
                                a.description,
                                style = VTypography.caption,
                                color = VColors.ink2,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommsEntryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    CreamCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                Text(description, style = VTypography.caption, color = VColors.ink3)
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    val (bg, fg) = if (active) VColors.violetSoft to VColors.violet else VColors.creamDeep to VColors.ink3
    Text(
        label,
        style = VTypography.caption.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.Medium),
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
                Text(appString(StringKeys.SCH_ANNOUNCEMENT_UNAVAILABLE), style = VTypography.h3, color = VColors.ink)
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
                    Box(Modifier.size(5.dp).clip(CircleShape).background(VColors.violet))
                    Text(announcement.category, style = VTypography.accentLabel, color = VColors.violet)
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(announcement.title, style = VTypography.h2, color = VColors.ink)
            Text(
                appString(StringKeys.SCH_POSTED_BY, "date" to announcement.date),
                style = VTypography.caption,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft))
            Spacer(Modifier.height(16.dp))
            Text(
                announcement.description,
                style = VTypography.bodySmall.copy(lineHeight = 22.4.sp),
                color = VColors.ink2,
            )
        }
    }
}

// ── Premium shared primitives ─────────────────────────────────────────────────

@Composable
private fun CreamCard(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(16.dp),
    ) { content() }
}

@Composable
private fun MiniBadge(text: String, color: Color, bg: Color) {
    Text(
        text = text,
        style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun CommsStaggeredItem(index: Int, content: @Composable () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(24f) }
    LaunchedEffect(Unit) {
        delay(220 + index * 60L)
        launch { alpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease)) }
        launch { offsetY.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease)) }
    }
    Box(
        modifier = Modifier
            .graphicsLayer(translationY = offsetY.value)
            .alpha(alpha.value),
    ) { content() }
}
