package com.littlebridge.enrollplus.ui.v2.screens.notifications

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsState
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.util.AnalyticsTracker
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

/**
 * NotificationsScreenV2 — faithful Compose translation of `Notifications.tsx → NotificationsScreen`.
 *
 * Reproduces the React layout exactly (UI_FIDELITY_AUDIT §9):
 *  - navy→indigo (135°) "Inbox" hero with a top-right radial teal blob, blurred bell chip, an
 *    `INBOX` overline (12sp / 0.05em / 70% white) and a **mono** unread count (28sp / 600) + "unread";
 *  - `all / unread` filter pills — **navy fill + white** when active, **cream + ink-2** when not
 *    (NOT the teal VTag chip), 12sp / 700, "unread · N" suffix;
 *  - per-item card (category badge + time, title, body, unread teal-deep dot, chevron, category-tinted
 *    icon tile) with the two React shadow levels (raised for unread, resting for read) and a staggered
 *    fade-up entrance (delay i*0.04s);
 *  - the "You're all caught up" empty state and the "Notification preferences" footer (13/600).
 *
 * **Wired to the real [NotificationsViewModel]** (`shared/`) →
 * `ParentRepository.getNotifications` → `GET /api/v1/parent/notifications`, which aggregates the
 * parent's school announcements and outstanding fee reminders. MockV2 is no longer referenced; the
 * three UI states (loading / error / empty) are handled by [VStateHost] (report §5.3, SWEEP-A).
 */
@Composable
fun NotificationsScreenV2(
    onBack: () -> Unit,
    onDeepLink: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) {
        AnalyticsTracker.event("vp_notifications_view", mapOf(
            "unread_count" to state.notifications.count { it.unread },
        ))
    }
    NotificationsContent(
        state = state,
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::refresh,
        onBack = onBack,
        onMarkAll = {
            AnalyticsTracker.event("vp_notification_mark_all_read")
            viewModel.markAllRead()
        },
        onMarkRead = { id ->
            AnalyticsTracker.event("vp_notification_mark_read", mapOf("notification_id" to id))
            viewModel.markRead(id)
        },
        onClearAll = viewModel::clearAll,
        onDeepLink = onDeepLink,
        onRetry = viewModel::load,
        modifier = modifier.statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    )
}

/** Stateless body — also used by the @Preview with seeded state (no MockV2 in the live path). */
@Composable
private fun NotificationsContent(
    state: NotificationsState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onMarkAll: () -> Unit,
    onMarkRead: (String) -> Unit,
    onClearAll: () -> Unit,
    onDeepLink: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterUnread by remember { mutableStateOf(false) }

    val items = state.notifications.map {
        VNotification(
            id = it.id,
            category = it.category,
            title = it.title,
            body = it.body,
            time = it.time,
            unread = it.unread,
            deepLink = it.deepLink,
        )
    }
    val unread = items.count { it.unread }
    val visible = if (filterUnread) items.filter { it.unread } else items

    Column(modifier.fillMaxSize().background(VColors.cream).statusBarsPadding()
        .imePadding()
        .navigationBarsPadding()) {
        PremiumNotificationHeader(
            title = appString(StringKeys.NOTIF_TITLE),
            onBack = onBack,
            onMarkAll = onMarkAll,
            onClearAll = onClearAll,
            canClear = items.isNotEmpty(),
        )

        VPullRefresh(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Inbox hero ──────────────────────────────────────────────────────────
            // React: `px-5 pt-2 pb-5` = 20 / 8 / 20px wrapper around an 18px-radius gradient card.
            Box(Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            // React: linear-gradient(135deg, --navy 0%, #3b3870 100%)
                            Brush.linearGradient(
                                colors = listOf(VColors.violet, VColors.violetHover),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                            ),
                        )
                        // §9#2: decorative top-right radial teal blob (rgba(60,185,169,0.45)→transparent).
                        .drawBehind {
                            val blobR = 88.dp.toPx() // React w-44/h-44 = 176px → radius 88, offset -40
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(BlobTeal, Color.Transparent),
                                    center = Offset(size.width + 40.dp.toPx(), -40.dp.toPx()),
                                    radius = blobR * 2f,
                                ),
                                radius = blobR * 2f,
                                center = Offset(size.width + 40.dp.toPx(), -40.dp.toPx()),
                            )
                        }
                        .padding(24.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Blurred white bell chip (React rgba(255,255,255,0.14) + backdrop-blur).
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(VIcons.Bell, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            // React: 12sp / 0.05em / uppercase / opacity .7 — NOT the 11sp label token.
                            Text(
                                appString(StringKeys.NOTIF_INBOX),
                                style = VTypography.body.copy(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    letterSpacing = 0.05.em,
                                ),
                            )
                            Spacer(Modifier.height(2.dp))
                            // React: mono 28 / 600 / line-height 1.1, with "unread" at 14 / opacity .7.
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    unread.toString(),
                                    style = VTypography.body.copy(
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        lineHeight = 30.8.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    appString(StringKeys.NOTIF_UNREAD_LABEL),
                                    style = VTypography.body.copy(color = Color.White.copy(alpha = 0.7f)),
                                    modifier = Modifier.padding(bottom = 3.dp),
                                )
                            }
                        }
                    }
                }
            }

            // ── Filter pills ──────────────────────────────────────────────────────
            // React: `px-5 mb-3 flex gap-2`. Pills: navy active / cream inactive, 12/700, capitalize.
            Row(
                Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterPill(label = appString(StringKeys.COMMON_ALL), active = !filterUnread) { filterUnread = false }
                FilterPill(
                    label = if (unread > 0) "${appString(StringKeys.NOTIF_FILTER_UNREAD)} · $unread" else appString(StringKeys.NOTIF_FILTER_UNREAD),
                    active = filterUnread,
                ) { filterUnread = true }
            }

            // ── List · loading · error · empty (LAW 3 via VStateHost) ──────────────
            // React: `px-5 pb-8 space-y-2`.
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VStateHost(
                    loading = state.isLoading,
                    error = state.error,
                    isEmpty = visible.isEmpty(),
                    emptyIcon = VIcons.Check,
                    emptyTitle = appString(StringKeys.NOTIF_ALL_CAUGHT_UP),
                    emptyBody = if (filterUnread) appString(StringKeys.NOTIF_NO_UNREAD) else appString(StringKeys.NOTIF_NONE_YET),
                    onRetry = onRetry,
                ) {
                    visible.forEachIndexed { i, n ->
                        // React: staggered fade-up entrance (delay i*0.04s).
                        var shown by remember(n.id) { mutableStateOf(false) }
                        LaunchedEffect(n.id) {
                            delay(i * 40L)
                            shown = true
                        }
                        AnimatedVisibility(
                            visible = shown,
                            enter = fadeIn(tween(VMotion.durSlow, easing = VMotion.ease)) +
                                slideInVertically(
                                    tween(VMotion.durSlow, easing = VMotion.ease),
                                    initialOffsetY = { 8 },
                                ),
                        ) {
                            NotificationRow(
                                n,
                                onClick = {
                                    onMarkRead(n.id)
                                    n.deepLink?.let { onDeepLink(it) }
                                },
                            )
                        }
                    }
                }
            }

            // ── Preferences footer ──────────────────────────────────────────────────
            // React: `px-5 pb-10` wrapper; pb-8 on the list above → extra gap before footer.
            Spacer(Modifier.height(16.dp))
            Box(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 40.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VColors.cream)
                        .clickable {}
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(VIcons.Close, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        appString(StringKeys.NOTIF_PREFERENCES),
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                        color = VColors.ink2,
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun PremiumNotificationHeader(
    title: String,
    onBack: () -> Unit,
    onMarkAll: () -> Unit,
    onClearAll: () -> Unit,
    canClear: Boolean,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(VColors.surfaceCard)
                        .border(1.dp, VColors.line, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = VColors.ink,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    title,
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable { onMarkAll() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(VIcons.Check, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(14.dp))
                    Text(
                        appString(StringKeys.NOTIF_MARK_ALL),
                        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = VColors.violet,
                        maxLines = 1,
                    )
                }
                if (canClear) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onClearAll() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(VIcons.Close, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
                        Text(
                            "Clear",
                            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                            color = VColors.ink3,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line).padding(horizontal = 24.dp))
    }
}

@Composable
private fun FilterPill(label: String, active: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) VColors.violet else VColors.cream)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = VTypography.caption.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (active) Color.White else VColors.ink2,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun NotificationRow(n: VNotification, onClick: () -> Unit) {
    val (tileBg, tileFg) = categoryTile(n.category)
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tileBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(categoryIcon(n.category), contentDescription = null, tint = tileFg, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VBadge(text = n.category, tone = categoryBadgeTone(n.category))
                    Spacer(Modifier.width(8.dp))
                    Text(n.time, style = VTypography.label.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = VColors.ink3)
                }
                Spacer(Modifier.height(6.dp))
                Text(n.title, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
                Spacer(Modifier.height(2.dp))
                Text(n.body, style = VTypography.caption, color = VColors.ink2)
            }
            if (n.unread) {
                Box(
                    Modifier
                        .padding(start = 8.dp, top = 0.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VColors.violet),
                )
            } else {
                Icon(
                    VIcons.ChevronRight,
                    contentDescription = null,
                    tint = VColors.ink3,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp).size(16.dp),
                )
            }
        }
    }
}

/** UI-only notification model. Mirrors the server feed shape so a real feed can map to it. */
data class VNotification(
    val id: String,
    val category: String, // "attendance" | "academic" | "fees" | "announcement"
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean,
    val deepLink: String? = null,
)

// §9 one-off literals (lifted verbatim from Notifications.tsx; not part of the global palette):
//  - hero blob = rgba(60,185,169,0.45)
//  - icon-tile foregrounds: attendance #7a3f00, fees #7a1c18, academic --teal-deep, default #155e3a.
private val BlobTeal = Color(0x733CB9A9)        // rgba(60,185,169,0.45)
private val TileFgAttendance = Color(0xFF7A3F00)
private val TileFgFees = Color(0xFF7A1C18)
private val TileFgDefault = Color(0xFF155E3A)

private fun categoryIcon(cat: String): ImageVector = when (cat.lowercase()) {
    "attendance" -> VIcons.Calendar
    "academic" -> VIcons.BookOpen // React iconFor: academic → <BookOpen/>
    "fees" -> VIcons.Wallet
    else -> VIcons.Megaphone
}

private fun categoryBadgeTone(cat: String): VBadgeTone = when (cat.lowercase()) {
    "fees" -> VBadgeTone.Danger
    "attendance" -> VBadgeTone.Warning
    "academic" -> VBadgeTone.Arctic
    else -> VBadgeTone.Success
}

/** Category-tinted icon-tile colors, matching the React `toneFor()` map (§9#4) verbatim. */
private fun categoryTile(cat: String): Pair<Color, Color> {
    return when (cat.lowercase()) {
        "attendance" -> VColors.goldSoft.copy(alpha = 0.55f) to TileFgAttendance
        "fees" -> VColors.coralSoft.copy(alpha = 0.55f) to TileFgFees
        "academic" -> VColors.violetSoft.copy(alpha = 0.18f) to VColors.violet
        else -> VColors.mintSoft.copy(alpha = 0.42f) to TileFgDefault
    }
}
