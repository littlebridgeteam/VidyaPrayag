package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherCardDto
import com.littlebridge.enrollplus.feature.school.domain.model.StaffDto
import com.littlebridge.enrollplus.feature.school.domain.model.StudentDto
import com.littlebridge.enrollplus.feature.school.domain.model.TodayItemDto
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────────────────────
//  People Tab — premium card system.
//  1:1 translation of prototypes/people-tab-premium.{html,css}. Every dimension,
//  colour and layout decision below is copied from that prototype and its
//  matching screenshots; nothing is improvised.
// ─────────────────────────────────────────────────────────────────────────────

// ── Palette (mirrors :root in people-tab-premium.css) ──
private val PplHairline = Color(0x0F26234D)          // rgba(38,35,77,.06) — navy @ 6%
private val PplCard = VColors.white                  // --card:#FFFFFF
private val PplInk = Color(0xFF1A1614)               // --ink
private val PplInk2 = Color(0xFF5C544E)              // --ink-2
private val PplInk3 = Color(0xFF8A8078)              // --ink-3
private val PplAccent = Color(0xFF5B41D5)            // --accent
private val PplAccentDeep = Color(0xFF4A30C4)        // --accent-deep
private val PplAccentSoft = Color(0xFF7B6BE0)        // --accent-soft
private val PplSurface = Color(0xFFF4F3FA)           // --surface / --accent-tint

private val SuccessInk = Color(0xFF1F7A4D)           // --success-ink
private val SuccessSoft = Color(0x40A8E6CF)          // rgba(168,230,207,.25)
private val WarningInk = Color(0xFFB3651A)           // --warning-ink
private val WarningSoft = Color(0x40FFD4A3)          // rgba(255,212,163,.25)
private val DangerInk = Color(0xFFB3261E)            // --danger-ink
private val DangerSoft = Color(0x33FFADA8)           // rgba(255,173,168,.2)

private val TealCol = Color(0xFF3CB9A9)              // --teal
private val TealInk = Color(0xFF006A60)              // --teal-deep
private val TealSoft = Color(0x1F3CB9A9)             // rgba(60,185,169,.12)
private val SkyCol = Color(0xFF6C8DF5)               // --sky
private val SkyInk = Color(0xFF4A6BD8)
private val SkySoft = Color(0x1F6C8DF5)              // rgba(108,141,245,.12)
private val PeachCol = Color(0xFFFF8A65)             // --peach
private val PeachInk = Color(0xFFC04A20)
private val PeachSoft = Color(0x1FFF8A65)            // rgba(255,138,101,.12)
private val VioletSoftFill = Color(0x125B41D5)       // rgba(91,65,213,.07)
private val GoldCol = Color(0xFFB45309)              // .av.gold background

// ── Avatar gradients (parent palette rotation, .av-* in css) ──
private enum class AvatarVariant { Violet, Teal, Sky, Peach, Gold, Mint }

private val AvatarVariant.gradient: Pair<Color, Color>
    get() = when (this) {
        AvatarVariant.Violet -> Color(0xFF7B6BE0) to Color(0xFF5B41D5)
        AvatarVariant.Teal -> Color(0xFF5DD9C8) to Color(0xFF3CB9A9)
        AvatarVariant.Sky -> Color(0xFF8BA8F8) to Color(0xFF6C8DF5)
        AvatarVariant.Peach -> Color(0xFFFFB088) to Color(0xFFFF8A65)
        AvatarVariant.Gold -> Color(0xFFFFD040) to Color(0xFFFCB400)
        AvatarVariant.Mint -> Color(0xFF5DD9C8) to Color(0xFF006A60)
    }

private fun avatarVariantFor(key: String): AvatarVariant {
    val variants = AvatarVariant.entries
    val hash = key.sumOf { it.code }
    return variants[(hash % variants.size + variants.size) % variants.size]
}

private fun initialsOf(name: String): String = name.trim().split(" ")
    .filter(String::isNotBlank)
    .take(2)
    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
    .joinToString("")
    .ifEmpty { "?" }

/** 42dp circular avatar — DM-Mono initials on a solid colour, matching `.av` in the prototype. */
@Composable
private fun PplAvatar(name: String, photoUrl: String?, size: Dp = 42.dp) {
    val (start, end) = remember(name) { avatarVariantFor(name).gradient }
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(start, end))),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initialsOf(name),
            color = VColors.white,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
        )
        if (!photoUrl.isNullOrBlank()) {
            val painter = rememberAsyncImagePainter(photoUrl)
            val painterState by painter.state.collectAsStateV2()
            if (painterState is AsyncImagePainter.State.Success) {
                Image(
                    painter = painter,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Link-requests banner (Students sub-tab) — `.link-banner`
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun LinkRequestsBanner(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (count <= 0) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(PplSurface, Color(0xFFEAE6FA))))
            .border(1.dp, PplAccent.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(36.dp)
                .shadow(5.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(PplAccentSoft, PplAccent))),
            contentAlignment = Alignment.Center,
        ) { Icon(VIcons.Check, null, tint = VColors.white, modifier = Modifier.size(18.dp)) }
        Column(Modifier.weight(1f)) {
            Text("$count pending link requests", color = PplAccentDeep, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Tap to review parent\u2192child approvals", color = PplAccentSoft, fontSize = 12.sp)
        }
        Icon(VIcons.ChevronRight, null, tint = PplAccent, modifier = Modifier.size(18.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Filter chips — `.frow` / `.fchip`
// ─────────────────────────────────────────────────────────────────────────────

internal data class FilterChipSpec(
    val label: String,
    val options: List<String>,
    val selected: Set<String>,
    val onToggle: (String) -> Unit,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FilterChipRow(chips: List<FilterChipSpec>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { FilterChip(it) }
    }
}

@Composable
private fun FilterChip(spec: FilterChipSpec) {
    var expanded by remember { mutableStateOf(false) }
    val active = spec.selected.isNotEmpty()
    val shape = RoundedCornerShape(50)
    Box {
        Row(
            modifier = Modifier
                .shadow(2.dp, shape, ambientColor = Color(0x0D26234D))
                .clip(shape)
                .then(
                    if (active) {
                        Modifier.background(Brush.linearGradient(listOf(PplAccent, PplAccentSoft)))
                    } else {
                        Modifier.background(PplCard).border(1.dp, PplHairline, shape)
                    },
                )
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { expanded = true }
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(spec.label, color = if (active) VColors.white else PplInk2, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (active) Color.White.copy(alpha = 0.25f) else Color(0x1426234D))
                    .padding(horizontal = 6.dp, vertical = 1.dp),
            ) {
                Text(
                    (if (active) spec.selected.size else spec.options.size).toString(),
                    color = if (active) VColors.white else PplInk2,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        DropdownMenu(expanded, { expanded = false }, modifier = Modifier.background(PplCard, RoundedCornerShape(14.dp))) {
            if (spec.options.isEmpty()) {
                DropdownMenuItem(text = { Text("No options", color = PplInk3) }, onClick = {}, enabled = false)
            } else spec.options.forEach { option ->
                val selected = option in spec.selected
                DropdownMenuItem(
                    text = { Text(option, color = if (selected) PplAccent else PplInk, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    onClick = { spec.onToggle(option); expanded = false },
                    leadingIcon = { if (selected) Icon(VIcons.Check, null, tint = PplAccent, modifier = Modifier.size(16.dp)) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ActiveFilterChips(
    selected: Set<String>,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (selected.isEmpty()) return
    FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        selected.forEach { value ->
            Row(
                Modifier.clip(CircleShape).background(VColors.violetSoft).clickable { onRemove(value) }.padding(horizontal = 9.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(value, color = PplAccent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Icon(VIcons.Close, "Remove $value", tint = PplAccent, modifier = Modifier.size(11.dp))
            }
        }
        Text("Clear all", color = VColors.coral, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onClearAll).padding(4.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Shared card primitives
// ─────────────────────────────────────────────────────────────────────────────

private enum class DotStatus { Active, Leave, Inactive }

private fun statusDotFor(status: String): DotStatus = when {
    status.contains("leave", true) || status.equals("on_leave", true) -> DotStatus.Leave
    status.equals("active", true) || status.equals("available", true) -> DotStatus.Active
    status.isBlank() -> DotStatus.Active
    else -> DotStatus.Inactive
}

private val DotStatus.color: Color
    get() = when (this) {
        DotStatus.Active -> SuccessInk
        DotStatus.Leave -> WarningInk
        DotStatus.Inactive -> PplInk3
    }

/** Card container — `.card`: white bg, hairline border, 16dp radius, navy-tinted shadow, 16dp padding. */
@Composable
private fun PersonCard(onClick: () -> Unit, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x0D26234D), spotColor = Color(0x0D26234D))
            .clip(RoundedCornerShape(16.dp))
            .background(PplCard)
            .border(1.dp, PplHairline, RoundedCornerShape(16.dp))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(16.dp),
    ) {
        content()
    }
}

/** Header row — 42dp avatar + status dot + name + subtitle. `.tr` / `.info`. */
@Composable
private fun CardHeader(
    name: String,
    photoUrl: String?,
    status: String,
    subtitle: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PplAvatar(name, photoUrl)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(statusDotFor(status).color))
                Text(
                    name,
                    color = PplInk,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                subtitle,
                color = PplInk2,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

private data class Metric(val value: String, val label: String, val valueColor: Color = PplInk)

/** Metric strip — hairline-divided columns, DM-Mono values + 10sp uppercase labels. `.metrics`. */
@Composable
private fun MetricStrip(metrics: List<Metric>) {
    Box(Modifier.fillMaxWidth().padding(top = 14.dp).height(1.dp).background(PplHairline))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = 14.dp),
    ) {
        metrics.forEachIndexed { index, m ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (index == 0) 0.dp else 10.dp,
                        end = if (index == metrics.lastIndex) 0.dp else 10.dp,
                    ),
            ) {
                Text(
                    m.value,
                    color = m.valueColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.2).sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    m.label.uppercase(),
                    color = PplInk3,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            if (index != metrics.lastIndex) {
                Box(Modifier.fillMaxHeight().width(1.dp).background(PplHairline))
            }
        }
    }
}

private enum class TagTheme { Violet, Teal, Sky, Peach, ScoreHi, ScoreMid, ScoreLo }

private val TagTheme.ink: Color
    get() = when (this) {
        TagTheme.Violet -> PplAccentDeep
        TagTheme.Teal -> TealInk
        TagTheme.Sky -> SkyInk
        TagTheme.Peach -> PeachInk
        TagTheme.ScoreHi -> SuccessInk
        TagTheme.ScoreMid -> WarningInk
        TagTheme.ScoreLo -> DangerInk
    }

private val TagTheme.fill: Color
    get() = when (this) {
        TagTheme.Violet -> VioletSoftFill
        TagTheme.Teal -> TealSoft
        TagTheme.Sky -> SkySoft
        TagTheme.Peach -> PeachSoft
        TagTheme.ScoreHi -> SuccessSoft
        TagTheme.ScoreMid -> WarningSoft
        TagTheme.ScoreLo -> DangerSoft
    }

private val TagTheme.dot: Color
    get() = when (this) {
        TagTheme.Violet -> PplAccent
        TagTheme.Teal -> TealCol
        TagTheme.Sky -> SkyCol
        TagTheme.Peach -> PeachCol
        TagTheme.ScoreHi -> SuccessInk
        TagTheme.ScoreMid -> WarningInk
        TagTheme.ScoreLo -> DangerInk
    }

/** `.subj-tag` — 11sp semibold, 6dp radius, coloured dot + text. */
@Composable
private fun SubjectTag(text: String, theme: TagTheme) {
    Row(
        Modifier.clip(RoundedCornerShape(6.dp)).background(theme.fill).padding(horizontal = 9.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(Modifier.size(5.dp).clip(CircleShape).background(theme.dot))
        Text(text, color = theme.ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

private val subjectPalette = listOf(TagTheme.Violet, TagTheme.Teal, TagTheme.Sky, TagTheme.Peach)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectTagRow(tags: List<Pair<String, TagTheme>>) {
    if (tags.isEmpty()) return
    FlowRow(
        Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) { tags.forEach { (text, theme) -> SubjectTag(text, theme) } }
}

/** Action row — hairline-separated text-link buttons (Call / Message / Assign). `.acts` / `.act-link`. */
@Composable
private fun ActionRow(actions: List<Triple<ImageVector, String, () -> Unit>>) {
    Box(Modifier.fillMaxWidth().padding(top = 14.dp).height(1.dp).background(PplHairline))
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEach { (icon, label, onClick) ->
            Row(
                modifier = Modifier
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
                    .padding(end = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(icon, label, tint = PplInk2, modifier = Modifier.size(14.dp))
                Text(label, color = PplInk2, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Contact row (staff) — phone + email inline. `.contact-row`. */
@Composable
private fun ContactRow(items: List<Pair<ImageVector, String>>) {
    if (items.isEmpty()) return
    Row(
        Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { (icon, text) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, null, tint = PplInk3, modifier = Modifier.size(13.dp))
                Text(text, color = PplInk2, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Teacher card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun TeacherCard(
    teacher: TeacherCardDto,
    onViewProfile: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onAssignClass: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = teacher.profile
    val subtitle = listOf(
        teacher.academicAssignment.subjects.firstOrNull().orEmpty(),
        p.role,
        p.experience?.takeIf(String::isNotBlank).orEmpty(),
    ).filter(String::isNotBlank).joinToString(" \u00B7 ")

    Box(modifier) {
        PersonCard(onClick = onViewProfile) {
            CardHeader(p.name, p.avatarUrl, p.status, subtitle)

            val attendance = teacher.activity.attendancePercentage
            val onLeave = statusDotFor(p.status) == DotStatus.Leave
            MetricStrip(
                listOf(
                    Metric(teacher.workload.totalStudents.toString(), "Students"),
                    Metric(teacher.workload.totalClasses.toString(), "Classes"),
                    Metric(attendance?.let { "$it%" } ?: "\u2014", "Attendance", PplAccent),
                    Metric(
                        if (onLeave) "\u2014" else (p.rating?.toString() ?: "\u2014"),
                        "PEWS",
                        PplAccent,
                    ),
                ),
            )

            SubjectTagRow(
                teacher.academicAssignment.subjects.mapIndexed { index, subject ->
                    subject to subjectPalette[index % subjectPalette.size]
                },
            )

            ActionRow(
                buildList {
                    add(Triple(VIcons.Phone, "Call", onCall))
                    add(Triple(VIcons.Chat, "Message", onMessage))
                    if (teacher.actions.canAssignClass) add(Triple(VIcons.School, "Assign", onAssignClass))
                },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Student card
// ─────────────────────────────────────────────────────────────────────────────

private enum class FeeStatus(val label: String, val ink: Color, val fill: Color, val bar: Color, val progress: Float) {
    Paid("Paid", SuccessInk, SuccessSoft, SuccessInk, 1f),
    Pending("Pending", WarningInk, WarningSoft, WarningInk, 0.6f),
}

/** Fee section — `.fees` box from the student screenshots: rounded 10dp surface, status badge, 4dp bar. */
@Composable
private fun FeeSection(fee: FeeStatus) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(PplSurface)
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("FEES", color = PplInk3, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp)
            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(fee.fill).padding(horizontal = 8.dp, vertical = 3.dp)) {
                Text(fee.label, color = fee.ink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0x14000000)),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(fee.progress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(fee.bar),
            )
        }
    }
}

private fun tagThemeForColor(color: String): TagTheme = when (color.lowercase()) {
    "green" -> TagTheme.ScoreHi
    "red" -> TagTheme.ScoreLo
    "yellow", "amber", "orange" -> TagTheme.ScoreMid
    "sky", "blue" -> TagTheme.Sky
    "teal" -> TagTheme.Teal
    "peach" -> TagTheme.Peach
    else -> TagTheme.Violet
}

@Composable
internal fun StudentCard(
    student: StudentDto,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitle = buildList {
        add("Class ${student.className}")
        if (student.rollNumber.isNotBlank()) add("Roll ${student.rollNumber}")
        if (student.studentCode.isNotBlank()) add(student.studentCode)
        if (student.isNewAdmission) add("New")
    }.joinToString(" \u00B7 ")

    Box(modifier) {
        PersonCard(onClick = onOpen) {
            CardHeader(student.fullName, student.profilePhotoUrl, student.status, subtitle)

            val avg = student.homeworkPercent.takeIf { it > 0f }?.let { "${it.roundToInt()}%" } ?: "\u2014"
            val att = student.attendancePercent.takeIf { it > 0f }?.let { "${it.roundToInt()}%" } ?: "\u2014"
            val classLabel = listOf(student.className, student.section.takeIf(String::isNotBlank))
                .filterNotNull().joinToString("-").ifBlank { "\u2014" }
            MetricStrip(
                listOf(
                    Metric(avg, "Average"),
                    Metric(att, "Attendance", SkyInk),
                    Metric(classLabel, "Class"),
                ),
            )

            FeeSection(if (student.feesPending) FeeStatus.Pending else FeeStatus.Paid)

            SubjectTagRow(
                student.todayItems
                    .filter { it.text.isNotBlank() }
                    .map { item: TodayItemDto -> item.text to tagThemeForColor(item.color) },
            )

            ActionRow(
                listOf(
                    Triple(VIcons.Phone, "Call Parent", onCall),
                    Triple(VIcons.Chat, "Message", onMessage),
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Staff card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun StaffCard(
    staff: StaffDto,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitle = listOf(
        staff.role,
        staff.department?.takeIf(String::isNotBlank).orEmpty(),
        staff.joinedYear?.takeIf(String::isNotBlank)?.let { "$it yrs" }.orEmpty(),
    ).filter(String::isNotBlank).joinToString(" \u00B7 ")

    Box(modifier) {
        PersonCard(onClick = onOpen) {
            CardHeader(staff.fullName, staff.photoUrl, staff.status, subtitle)

            val statusLabel = if (staff.status.equals("active", true) || staff.status.isBlank()) "FT" else
                staff.status.uppercase().take(2)
            MetricStrip(
                listOf(
                    Metric(staff.joinedYear?.takeIf(String::isNotBlank) ?: "\u2014", "Years"),
                    Metric(statusLabel, "Status"),
                    Metric(staff.employeeId?.takeIf(String::isNotBlank) ?: "\u2014", "ID"),
                ),
            )

            val tags = buildList {
                staff.department?.takeIf(String::isNotBlank)?.let { add(it to TagTheme.Peach) }
                staff.shift?.takeIf(String::isNotBlank)?.let { add(it to TagTheme.Violet) }
            }
            SubjectTagRow(tags)

            ContactRow(
                buildList {
                    staff.phone?.takeIf(String::isNotBlank)?.let { add(VIcons.Phone to it) }
                    staff.email?.takeIf(String::isNotBlank)?.let { add(VIcons.Mail to it) }
                },
            )

            ActionRow(
                listOf(
                    Triple(VIcons.Phone, "Call", onCall),
                    Triple(VIcons.Chat, "Message", onMessage),
                ),
            )
        }
    }
}
