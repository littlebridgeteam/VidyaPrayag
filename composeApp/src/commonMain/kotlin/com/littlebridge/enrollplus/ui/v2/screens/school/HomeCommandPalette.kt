package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

/**
 * Full-screen spotlight/command palette for jumping to admin destinations.
 *
 * @param visible Whether the palette is open.
 * @param onDismiss Called when the user taps the scrim or presses back.
 * @param onSelect Called with the selected route id.
 */
@Composable
fun HomeCommandPalette(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        var query by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(visible) {
            if (visible) focusRequester.requestFocus()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VTheme.colors.ink.copy(alpha = 0.55f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                )
                .statusBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(VTheme.colors.card)
                        .padding(16.dp),
                ) {
                    VInput(
                        value = query,
                        onValueChange = { query = it },
                        hint = "Search or jump to...",
                        leadingIcon = Icons.Default.Search,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    )

                    Spacer(Modifier.height(12.dp))

                    val results = remember(query) {
                        COMMAND_DESTINATIONS.filter {
                            query.isBlank() ||
                                it.label.contains(query, ignoreCase = true) ||
                                it.id.contains(query, ignoreCase = true)
                        }
                    }

                    LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                        items(results, key = { it.id }) { dest ->
                            CommandRow(
                                dest = dest,
                                onClick = {
                                    onDismiss()
                                    onSelect(dest.id)
                                },
                            )
                        }
                    }

                    if (results.isEmpty()) {
                        Text(
                            text = "No matching screens",
                            style = VTheme.type.body,
                            color = VTheme.colors.ink3,
                            modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    VButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        variant = VButtonVariant.Ghost,
                        full = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun CommandRow(
    dest: CommandDestination,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VTheme.colors.accentTint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = resolveIcon(dest.icon),
                contentDescription = "",
                tint = VTheme.colors.accent,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dest.label,
                style = VTheme.type.body,
                color = VTheme.colors.ink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Text(
                text = dest.id.replace("_", " ").replaceFirstChar { it.uppercase() },
                style = VTheme.type.caption,
                color = VTheme.colors.ink3,
            )
        }
        Icon(
            imageVector = VIcons.ArrowRight,
            contentDescription = "",
            tint = VTheme.colors.ink3,
            modifier = Modifier.size(18.dp),
        )
    }
}

data class CommandDestination(
    val id: String,
    val label: String,
    val icon: String,
)

private fun resolveIcon(name: String): ImageVector = when (name.lowercase()) {
    "home" -> VIcons.Home
    "people" -> VIcons.User
    "records" -> VIcons.BookOpen
    "comms" -> VIcons.Chat
    "settings" -> VIcons.Settings
    "calendar" -> VIcons.Calendar
    "events" -> VIcons.Sparkles
    "reports" -> VIcons.Target
    "transport" -> VIcons.MapPin
    "analytics" -> VIcons.TrendingUp
    "notifications" -> VIcons.Bell
    "messages" -> VIcons.Chat
    "linkrequests" -> VIcons.Users
    "leaverequests" -> VIcons.Calendar
    "branding" -> VIcons.Palette
    "profile" -> VIcons.User
    "idcards" -> VIcons.IdCard
    "library" -> VIcons.BookOpen
    "classes" -> VIcons.School
    "fees" -> VIcons.Wallet
    "scholarships" -> VIcons.Sparkles
    "alumni" -> VIcons.GraduationCap
    "dailyattendance" -> VIcons.Check
    else -> VIcons.Star
}

private val COMMAND_DESTINATIONS = listOf(
    CommandDestination("tab_people", "People", "people"),
    CommandDestination("tab_records", "Records", "records"),
    CommandDestination("tab_comms", "Communications", "comms"),
    CommandDestination("tab_settings", "Settings", "settings"),
    CommandDestination("overlay_notifications", "Notifications", "notifications"),
    CommandDestination("overlay_calendar", "Academic Calendar", "calendar"),
    CommandDestination("overlay_events", "Events & PTM", "events"),
    CommandDestination("overlay_transport", "Transport", "transport"),
    CommandDestination("overlay_analytics", "Analytics", "analytics"),
    CommandDestination("overlay_messages", "Messages", "messages"),
    CommandDestination("overlay_link_requests", "Link Requests", "linkrequests"),
    CommandDestination("overlay_leave_requests", "Leave Requests", "leaverequests"),
    CommandDestination("overlay_branding", "Branding & Photos", "branding"),
    CommandDestination("overlay_profile", "Profile", "profile"),
    CommandDestination("overlay_id_cards", "ID Cards", "idcards"),
    CommandDestination("overlay_library", "Library", "library"),
    CommandDestination("overlay_classes_subjects", "Classes & Subjects", "classes"),
    CommandDestination("overlay_fees", "Fees", "fees"),
    CommandDestination("overlay_scholarships", "Scholarships", "scholarships"),
    CommandDestination("overlay_alumni", "Alumni", "alumni"),
    CommandDestination("overlay_daily_attendance", "Daily Attendance", "dailyattendance"),
)
