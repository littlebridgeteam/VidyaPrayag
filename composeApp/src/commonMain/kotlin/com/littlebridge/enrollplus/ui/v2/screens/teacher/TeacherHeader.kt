package com.littlebridge.enrollplus.ui.v2.screens.teacher

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VDivider
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VStatusDot
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString

/**
 * TeacherHeader — THE single canonical header for the whole rebuilt Teacher Portal, rendered
 * identically on every tab (Home included), mirroring the Parents Portal's one-header law.
 *
 * Left  — a tappable identity chip: the teacher's avatar + name + a contextual subline (the
 *         time-sensitive greeting on Home, the school name elsewhere). Tapping it opens the
 *         Profile tab/overlay (account & settings live there, never "tap photo = logout").
 * Right — an icon cluster: a notifications bell (with a real unread dot) and the account avatar.
 *
 * Surface: a clean white bar on the lavender canvas with a hairline divider — lavender/violet is
 * the brand accent (active dot), never a wall-to-wall fill.
 */
@Composable
fun TeacherHeader(
    teacherName: String,
    subline: String,
    photoUrl: String?,
    unreadCount: Int,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenNotifications: (() -> Unit)? = null,
) {
    val c = VtC
    Column(
        modifier
            .fillMaxWidth()
            .background(c.card)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 10.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // ── Identity chip → opens Profile ─────────────────────────────────────
            val chipIx = remember { MutableInteractionSource() }
            Row(
                Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                    .clickable(interactionSource = chipIx, indication = null) { onOpenProfile() }
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VAvatar(name = teacherName.ifBlank { "Teacher" }, src = photoUrl, size = 38.dp, ring = true)
                Column {
                    Text(
                        subline,
                        style = VtT.label.coloredV(c.ink3)
                            .copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp),
                    )
                    Text(
                        teacherName.ifBlank { "Teacher" },
                        style = VtT.bodyStrong.coloredV(c.ink)
                            .copy(fontSize = 15.sp, fontWeight = FontWeight.ExtraBold),
                    )
                }
            }

            // ── Icon cluster: notifications · account ─────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onOpenNotifications != null) {
                    Box {
                        HeaderIconButton(VIcons.Bell, "Notifications", onOpenNotifications)
                        if (unreadCount > 0) {
                            VStatusDot(
                                color = c.dangerInk,
                                size = 7.dp,
                                modifier = Modifier.align(Alignment.TopEnd).padding(7.dp),
                            )
                        }
                    }
                }
                val accountIx = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .clip(CircleShape)
                        .clickable(interactionSource = accountIx, indication = null) { onOpenProfile() },
                ) {
                    VAvatar(name = teacherName.ifBlank { "Teacher" }, src = photoUrl, size = 36.dp, ring = true)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        VDivider()
    }
}

/**
 * TeacherPremiumHeader — THE single premium header rendered identically on every
 * teacher tab (Home · Update · Classes · Timetable · Profile), so the whole portal
 * shares one chrome. Directly on the cream/violet token system (§4-§7):
 *
 *   • "Enroll+" wordmark in violet (top-left).
 *   • A notification bell (top-right) with a live unread dot.
 *   • "Hi {name}" eyebrow in violet.
 *   • A big two-tone greeting line — a neutral lead word + a violet accent word —
 *     that each tab customises to its own context (e.g. "here's your day" on Home,
 *     "let's update" on Update, "your week" on Timetable).
 *
 * It sits inside the scrolling content of each tab (NOT the scaffold topBar) so the
 * greeting scrolls away like the parent portal — identical everywhere.
 */
@Composable
fun TeacherPremiumHeader(
    teacherName: String,
    lead: String,
    accent: String,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name = teacherName.trim().substringBefore(" ")
        .ifBlank { teacherName.ifBlank { appString(StringKeys.TEACHER_TITLE) } }

    // Greeting line is 10% larger than the base h2 (24sp → 26.4sp) for a bolder,
    // more premium masthead while staying comfortable. The "Hi {name}" eyebrow is
    // bumped proportionately (14sp → ~15.4sp).
    val greetingSize = 26.4.sp
    val eyebrowSize = 15.4.sp

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Enroll+",
                style = VTypography.wordmark.copy(color = VColors.violet, fontSize = 17.sp),
            )

            val ix = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .size(42.dp)
                    .clip(VShapes.full)
                    .background(VColors.surfaceCard)
                    .clickable(interactionSource = ix, indication = null) { onOpenNotifications() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = VIcons.BellStroke,
                    contentDescription = appString(StringKeys.TC_MESSAGES),
                    tint = VColors.ink,
                    modifier = Modifier.size(24.dp),
                )
                if (unreadCount > 0) {
                    VStatusDot(
                        color = VColors.coral,
                        size = 8.dp,
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 8.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Hi $name",
            style = VTypography.caption.copy(
                fontSize = eyebrowSize,
                fontWeight = FontWeight.SemiBold,
                color = VColors.violet,
            ),
        )

        Spacer(Modifier.height(3.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(
                text = lead,
                style = VTypography.h2.copy(color = VColors.ink, fontSize = greetingSize),
            )
            Text(
                text = accent,
                style = VTypography.h2.copy(color = VColors.violet, fontSize = greetingSize),
            )
        }
    }
}

/** A simpler header for in-portal sub-screens (overlays): a back chevron + title. */
@Composable
fun TeacherSubHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val c = VtC
    Column(
        modifier
            .fillMaxWidth()
            .background(c.card)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HeaderIconButton(VIcons.ArrowLeft, "Back", onBack)
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = VtT.h3.coloredV(c.ink).copy(fontWeight = FontWeight.ExtraBold),
                    maxLines = 1,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(subtitle, style = VtT.caption.coloredV(c.ink3).copy(fontSize = 11.sp), maxLines = 1)
                }
            }
            trailing?.invoke()
        }
        Spacer(Modifier.height(8.dp))
        VDivider()
    }
}

/** A circular header action button — cream surface, navy glyph, no ripple. */
@Composable
private fun HeaderIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    val c = VtC
    val ix = remember { MutableInteractionSource() }
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(c.cream)
            .clickable(interactionSource = ix, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = label, tint = c.ink, modifier = Modifier.size(16.dp))
    }
}
