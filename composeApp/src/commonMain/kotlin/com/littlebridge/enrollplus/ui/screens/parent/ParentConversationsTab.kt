package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAnnouncementDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

private enum class ConvSegment(val label: String) { Messages("Messages"), Announcements("Announcements") }

private val avatarColors = listOf(
    VColors.violet to VColors.violetSoft,
    VColors.sky to VColors.skySoft,
    VColors.gold to VColors.goldSoft,
    VColors.coral to VColors.coralSoft,
    VColors.success to VColors.mintSoft,
)

@Composable
fun ParentConversationsTab(
    viewModel: ParentViewModel,
    onThreadClick: (String) -> Unit,
) {
    val threadsState by viewModel.threadsState.collectAsState()
    val announcementsState by viewModel.announcementsState.collectAsState()

    var segment by rememberSaveable { mutableStateOf(ConvSegment.Messages) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.cream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // Segment selector
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .background(VColors.surfaceTint, VShapes.md)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            ConvSegment.entries.forEach { seg ->
                val isSelected = seg == segment
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) VColors.white else Color.Transparent, VShapes.sm)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { segment = seg }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        seg.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isSelected) VColors.ink else VColors.ink3,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        when (segment) {
            ConvSegment.Messages -> {
                when (val s = threadsState) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Loading messages...", color = VColors.ink3)
                        }
                    }
                    is UiState.Error -> {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(s.message, color = VColors.coral)
                        }
                    }
                    is UiState.Success -> {
                        if (s.data.threads.isEmpty()) {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No messages yet", color = VColors.ink3)
                            }
                        } else {
                            s.data.threads.forEach { thread ->
                                ThreadCard(thread = thread, onClick = { onThreadClick(thread.id) })
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
            ConvSegment.Announcements -> {
                when (val s = announcementsState) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Loading announcements...", color = VColors.ink3)
                        }
                    }
                    is UiState.Error -> {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(s.message, color = VColors.coral)
                        }
                    }
                    is UiState.Success -> {
                        if (s.data.announcements.isEmpty()) {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No announcements", color = VColors.ink3)
                            }
                        } else {
                            s.data.announcements.forEach { ann ->
                                ConvAnnouncementCard(ann)
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun ThreadCard(thread: ParentMessageThreadDto, onClick: () -> Unit) {
    val initials = thread.senderName.take(2).uppercase()
    val colorIdx = thread.senderName.hashCode().mod(avatarColors.size)
    val (tint, bg) = avatarColors[colorIdx]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(bg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = tint)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(thread.senderName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink, letterSpacing = (-0.2).sp)
            Text(thread.lastMessage, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(thread.time, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
            if (thread.unreadCount > 0) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier.size(18.dp).background(VColors.coral, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(thread.unreadCount.coerceAtMost(9).toString(), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = VColors.white)
                }
            }
        }
    }
}

@Composable
private fun ConvAnnouncementCard(ann: ParentAnnouncementDto) {
    val (catBg, catColor) = when (ann.category.lowercase()) {
        "events" -> VColors.violetSoft to VColors.violet
        "ptm" -> VColors.skySoft to VColors.sky
        "fees" -> VColors.coralSoft to VColors.coral
        "holiday" -> VColors.mintSoft to VColors.success
        else -> VColors.goldSoft to VColors.gold
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .background(catBg, VShapes.full)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(ann.category, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = catColor, letterSpacing = 0.5.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(ann.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink, letterSpacing = (-0.2).sp)
        Text(ann.description, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, lineHeight = 17.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
        Text(ann.date, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink3, modifier = Modifier.padding(top = 6.dp))
    }
}
