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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogItem
import com.littlebridge.enrollplus.feature.admin.domain.model.MessageThread
import com.littlebridge.enrollplus.feature.admin.presentation.Announcement
import com.littlebridge.enrollplus.feature.admin.presentation.PTMHistoryItem
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

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
        announcement.isCalendarOnly -> "Calendar" to Triple(VTheme.colors.gold, VTheme.colors.goldSoft, VIcons.Calendar)
        announcement.category.isNotBlank() -> announcement.category to Triple(VTheme.colors.violet, VTheme.colors.violetSoft, VIcons.Megaphone)
        else -> "Announcement" to Triple(VTheme.colors.ink3, VTheme.colors.surfaceTint, VIcons.Megaphone)
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
                Icon(icon, contentDescription = "", tint = badgeColor, modifier = Modifier.size(20.dp))
            }

            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        announcement.title,
                        style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VTheme.colors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    CommsBadge(text = badgeText, color = badgeColor, bg = badgeBg)
                }
                if (announcement.date.isNotBlank()) {
                    Text(
                        announcement.date,
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink3,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (announcement.description.isNotBlank()) {
                    Text(
                        announcement.description,
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink2,
                        modifier = Modifier.padding(top = 6.dp),
                        maxLines = 2,
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
                    .background(VTheme.colors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.Bold), color = VTheme.colors.ink)
                Text(description, style = VTheme.type.caption, color = VTheme.colors.ink3, maxLines = 2)
            }
            Icon(VIcons.ChevronRight, contentDescription = "", tint = VTheme.colors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
internal fun CommsBadge(text: String, color: Color, bg: Color) {
    Text(
        text = text,
        style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
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
                        style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VTheme.colors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        thread.time,
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink3,
                    )
                }
                Text(
                    thread.lastMessage,
                    style = VTheme.type.caption,
                    color = if (thread.isRead) VTheme.colors.ink3 else VTheme.colors.ink2,
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
                    .background(VTheme.colors.skySoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Calendar, contentDescription = "", tint = VTheme.colors.sky, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = VTheme.colors.ink,
                )
                Text(
                    "${item.date} · ${item.turnout}/${item.totalMet} met",
                    style = VTheme.type.caption,
                    color = VTheme.colors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Icon(VIcons.ChevronRight, contentDescription = "", tint = VTheme.colors.ink3.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
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
        "whatsapp" -> VTheme.colors.mint to VTheme.colors.mintSoft
        "push" -> VTheme.colors.sky to VTheme.colors.skySoft
        "sms" -> VTheme.colors.gold to VTheme.colors.goldSoft
        "email" -> VTheme.colors.violet to VTheme.colors.violetSoft
        else -> VTheme.colors.ink3 to VTheme.colors.surfaceTint
    }
    val icon = when (item.channel) {
        "whatsapp" -> VIcons.Phone
        "push" -> VIcons.Bell
        "sms" -> VIcons.Chat
        "email" -> VIcons.Mail
        else -> VIcons.Megaphone
    }
    val statusColor = when (item.status) {
        "delivered", "read", "sent" -> VTheme.colors.success
        "queued" -> VTheme.colors.gold
        "failed" -> VTheme.colors.error
        else -> VTheme.colors.ink3
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
                Icon(icon, contentDescription = "", tint = channelTint.first, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.announcementTitle.ifBlank { "Announcement" },
                        style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VTheme.colors.ink,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    Text(
                        item.status.replaceFirstChar { it.uppercase() },
                        style = VTheme.type.caption.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                Text(
                    "${item.channel.replaceFirstChar { it.uppercase() }} · ${maskIdentifier(item.recipientIdentifier)}",
                    style = VTheme.type.caption,
                    color = VTheme.colors.ink3,
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
