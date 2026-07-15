package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogItem
import com.littlebridge.enrollplus.feature.admin.domain.model.MessageThread
import com.littlebridge.enrollplus.feature.admin.presentation.Announcement
import com.littlebridge.enrollplus.feature.admin.presentation.PTMHistoryItem
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance

/**
 * CommsCards — reusable, premium cards for the School Comms hub.
 *
 * Modeled after [PeopleCards.kt]: each card is a self-contained [VCard] surface
 * with deterministic tinting, clear typography hierarchy, and click handling.
 */

@Composable
internal fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
) {
    val (badgeText, badgeMeta) = when {
        announcement.isCalendarOnly -> "Calendar" to Triple(VColors.gold, VColors.goldSoft, VIcons.Calendar)
        announcement.category.isNotBlank() -> announcement.category to Triple(VColors.violet, VColors.violetSoft, VIcons.Megaphone)
        else -> "Announcement" to Triple(VColors.ink3, VColors.surfaceTint, VIcons.Megaphone)
    }
    val badgeColor = badgeMeta.first
    val badgeBg = badgeMeta.second
    val icon = badgeMeta.third

    VCard(
        modifier = modifier
            .fillMaxWidth()
            .staggeredItemEntrance(index, true)
            .clickable { onClick() },
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
            }

            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        announcement.title,
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.width(8.dp))
                    CommsBadge(text = badgeText, color = badgeColor, bg = badgeBg)
                }
                if (announcement.date.isNotBlank()) {
                    Text(
                        announcement.date,
                        style = VTypography.caption,
                        color = VColors.ink3,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (announcement.description.isNotBlank()) {
                    Text(
                        announcement.description,
                        style = VTypography.caption,
                        color = VColors.ink2,
                        modifier = Modifier.padding(top = 6.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
internal fun CommsEntryCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        padding = 16.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                Text(description, style = VTypography.caption, color = VColors.ink3, maxLines = 2)
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
internal fun CommsBadge(text: String, color: Color, bg: Color) {
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
internal fun MessagePreviewCard(
    thread: MessageThread,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
) {
    VCard(
        modifier = modifier
            .fillMaxWidth()
            .staggeredItemEntrance(index, true)
            .clickable { onClick() },
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VAvatar(name = thread.senderName, src = thread.senderImageUrl, size = 44.dp)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        thread.senderName,
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        thread.time,
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
                Text(
                    thread.lastMessage,
                    style = VTypography.caption,
                    color = if (thread.isRead) VColors.ink3 else VColors.ink2,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            if (thread.unreadCount > 0) {
                VBadge(
                    text = thread.unreadCount.toString(),
                    tone = VBadgeTone.Arctic,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
internal fun PtmActivePreviewCard(
    title: String,
    date: String,
    slot: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.mintSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Calendar, contentDescription = null, tint = VColors.mint, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CommsBadge(text = "Active", color = VColors.mint, bg = VColors.mintSoft)
                    Text(
                        title,
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                        maxLines = 1,
                    )
                }
                Text(
                    if (slot.isNotBlank()) "$date · $slot" else date,
                    style = VTypography.caption,
                    color = VColors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
internal fun PtmPreviewCard(
    item: PTMHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
) {
    VCard(
        modifier = modifier
            .fillMaxWidth()
            .staggeredItemEntrance(index, true)
            .clickable { onClick() },
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.skySoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Calendar, contentDescription = null, tint = VColors.sky, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
                Text(
                    "${item.date} · ${item.turnout}/${item.totalMet} met",
                    style = VTypography.caption,
                    color = VColors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
internal fun DeliveryLogRowCard(
    item: DeliveryLogItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
) {
    val channelTint = when (item.channel) {
        "whatsapp" -> VColors.mint to VColors.mintSoft
        "push" -> VColors.sky to VColors.skySoft
        "sms" -> VColors.gold to VColors.goldSoft
        "email" -> VColors.violet to VColors.violetSoft
        else -> VColors.ink3 to VColors.surfaceTint
    }
    val icon = when (item.channel) {
        "whatsapp" -> VIcons.Phone
        "push" -> VIcons.Bell
        "sms" -> VIcons.Chat
        "email" -> VIcons.Mail
        else -> VIcons.Megaphone
    }
    val statusColor = when (item.status) {
        "delivered", "read", "sent" -> VColors.success
        "queued" -> VColors.gold
        "failed" -> VColors.error
        else -> VColors.ink3
    }

    VCard(
        modifier = modifier
            .fillMaxWidth()
            .staggeredItemEntrance(index, true)
            .clickable { onClick() },
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(channelTint.second),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = channelTint.first, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.announcementTitle.ifBlank { "Announcement" },
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    Text(
                        item.status.replaceFirstChar { it.uppercase() },
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                Text(
                    "${item.channel.replaceFirstChar { it.uppercase() }} · ${maskIdentifier(item.recipientIdentifier)}",
                    style = VTypography.caption,
                    color = VColors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

private fun maskIdentifier(identifier: String): String {
    return when {
        identifier.length <= 4 -> identifier
        identifier.contains("@") -> {
            val parts = identifier.split("@")
            val local = parts[0]
            val masked = if (local.length <= 2) local else "${local.take(2)}****"
            "$masked@${parts[1]}"
        }
        else -> {
            val start = identifier.take(3)
            val end = identifier.takeLast(2)
            "$start****$end"
        }
    }
}
