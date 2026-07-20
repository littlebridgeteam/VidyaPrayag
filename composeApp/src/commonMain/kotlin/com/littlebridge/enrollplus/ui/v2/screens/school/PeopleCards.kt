package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
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
//  A ground-up, dense card language for the school People directory, built to
//  feel native to the app and keyed to the parents-portal violet accent. It is
//  intentionally NOT a copy of any HTML prototype or screenshot — the layout,
//  spacing and hierarchy below are designed for signal density over decoration.
// ─────────────────────────────────────────────────────────────────────────────

// ── Palette (mirrors :root in people-tab-premium.css) ──
private val PplHairline = Color(0x0F26234D)          // rgba(38,35,77,.06) — navy @ 6%
private val PplHairlineStrong = Color(0x1A26234D)    // navy @ 10% — metric-strip dividers
private val PplStripFill = Color(0xFFF7F5FC)         // faint violet-tinted panel behind metrics
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

/**
 * Filter chips on a single line. The row scrolls horizontally if the chips
 * overflow, so Subject / Grade / Availability (and their per-tab equivalents)
 * never wrap onto a second row and eat vertical space.
 */
@Composable
internal fun FilterChipRow(chips: List<FilterChipSpec>, modifier: Modifier = Modifier) {
    Row(
        modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chips.forEach { FilterChip(it) }
    }
}

@Composable
private fun FilterChip(spec: FilterChipSpec) {
    var expanded by remember { mutableStateOf(false) }
    val active = spec.selected.isNotEmpty()
    val shape = RoundedCornerShape(11.dp)

    // Smooth open/active transitions so the chip never "pops".
    val chipBg by animateColorAsState(if (active) PplAccent else PplCard, label = "chipBg")
    val labelColor by animateColorAsState(if (active) VColors.white else PplInk2, label = "chipLabel")
    val borderColor by animateColorAsState(
        when {
            active -> Color.Transparent
            expanded -> PplAccent.copy(alpha = 0.55f)
            else -> PplHairlineStrong
        },
        label = "chipBorder",
    )
    val caretRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chipCaret")

    Box {
        Row(
            modifier = Modifier
                .shadow(
                    if (active) 5.dp else 1.dp,
                    shape,
                    ambientColor = if (active) PplAccent.copy(alpha = 0.30f) else Color(0x0D26234D),
                    spotColor = if (active) PplAccent.copy(alpha = 0.30f) else Color(0x0D26234D),
                )
                .clip(shape)
                .background(chipBg)
                .border(1.dp, borderColor, shape)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { expanded = true }
                .padding(start = 13.dp, end = 9.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                spec.label,
                color = labelColor,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.1).sp,
                maxLines = 1,
                softWrap = false,
            )
            if (active) {
                // Count badge only when something is selected — a clean white-on-violet
                // pip. Unselected chips stay quiet (just label + caret).
                Box(
                    Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.24f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        spec.selected.size.toString(),
                        color = VColors.white,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Icon(
                VIcons.ChevronDown,
                contentDescription = null,
                tint = if (active) VColors.white.copy(alpha = 0.85f) else PplInk3,
                modifier = Modifier.size(15.dp).rotate(caretRotation),
            )
        }

        if (expanded) {
            FilterDropdown(
                spec = spec,
                onDismiss = { expanded = false },
            )
        }
    }
}

/**
 * Premium filter dropdown — a custom elevated popup card (not Material3's
 * DropdownMenu, which rendered with the wrong shape/elevation and caused the
 * "distortion" the user flagged). It anchors just below the chip, has a titled
 * header with a "Clear" action, animated check rows, and its own scroll for long
 * option lists.
 */
@Composable
private fun FilterDropdown(spec: FilterChipSpec, onDismiss: () -> Unit) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
        offset = IntOffset(0, 18),
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 196.dp, max = 260.dp)
                .shadow(18.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x2626234D), spotColor = Color(0x2626234D))
                .clip(RoundedCornerShape(16.dp))
                .background(PplCard)
                .border(1.dp, PplHairline, RoundedCornerShape(16.dp))
                .padding(6.dp),
        ) {
            // Header: filter name + Clear
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 6.dp, top = 6.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    spec.label,
                    color = PplInk,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp,
                    modifier = Modifier.weight(1f),
                )
                if (spec.selected.isNotEmpty()) {
                    Text(
                        "Clear",
                        color = VColors.coral,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                spec.selected.toList().forEach(spec.onToggle)
                            }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
            if (spec.options.isEmpty()) {
                Text(
                    "No options",
                    color = PplInk3,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                )
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = 264.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    spec.options.forEach { option ->
                        FilterDropdownRow(
                            label = option,
                            selected = option in spec.selected,
                            onClick = { spec.onToggle(option) },
                        )
                    }
                }
            }
        }
    }
}

/** One option row inside the filter dropdown — a soft-highlight pill with an
 *  animated violet check box on the trailing edge. Multi-select: tapping toggles
 *  without closing the popup. */
@Composable
private fun FilterDropdownRow(label: String, selected: Boolean, onClick: () -> Unit) {
    val rowBg by animateColorAsState(if (selected) VioletSoftFill else Color.Transparent, label = "rowBg")
    val boxBg by animateColorAsState(if (selected) PplAccent else Color.Transparent, label = "rowBoxBg")
    val boxBorder by animateColorAsState(if (selected) PplAccent else PplHairlineStrong, label = "rowBoxBorder")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9.dp))
            .background(rowBg)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            color = if (selected) PplAccentDeep else PplInk,
            fontSize = 12.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Box(
            Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(boxBg)
                .border(1.5.dp, boxBorder, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(VIcons.Check, null, tint = VColors.white, modifier = Modifier.size(12.dp))
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

/** Small tinted status pill for the header trailing slot (Active / On leave / Inactive). */
@Composable
private fun StatusPill(status: String) {
    val dot = statusDotFor(status)
    val (label, ink, fill) = when (dot) {
        DotStatus.Active -> Triple("Active", SuccessInk, SuccessSoft)
        DotStatus.Leave -> Triple("On leave", WarningInk, WarningSoft)
        DotStatus.Inactive -> Triple("Inactive", PplInk3, Color(0x14262340))
    }
    Box(
        Modifier.clip(RoundedCornerShape(50)).background(fill).padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
        Text(label, color = ink, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
    }
}

/** "New" admission badge — violet accent pill for the header trailing slot. */
@Composable
private fun NewBadge() {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.linearGradient(listOf(PplAccent, PplAccentSoft)))
            .padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
        Text("New", color = VColors.white, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
    }
}

/** Card container — white bg, hairline border, 18dp radius, navy-tinted shadow, 14dp padding. */
@Composable
private fun PersonCard(onClick: () -> Unit, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x1426234D), spotColor = Color(0x1426234D))
            .clip(RoundedCornerShape(18.dp))
            .background(PplCard)
            .border(1.dp, PplHairline, RoundedCornerShape(18.dp))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(14.dp),
    ) {
        content()
    }
}

/**
 * Header row — 44dp avatar + name (with an inline status dot) + subtitle, and a
 * quiet trailing chevron that signals the whole card opens the profile. A
 * trailing slot lets each card drop in a status pill (e.g. "On leave").
 */
@Composable
private fun CardHeader(
    name: String,
    photoUrl: String?,
    status: String,
    subtitle: String,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        PplAvatar(name, photoUrl, size = 44.dp)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(statusDotFor(status).color))
                Text(
                    name,
                    color = PplInk,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            Text(
                subtitle,
                color = PplInk2,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(VIcons.ChevronRight, null, tint = PplInk3.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
    }
}

/**
 * Header variant that takes an explicit status-dot colour (rather than deriving
 * it from a status string), so callers with a richer status model — e.g. the
 * teacher card's "Unassigned" state — can drive the dot precisely.
 */
@Composable
private fun CardHeaderStatus(
    name: String,
    photoUrl: String?,
    dotColor: Color,
    subtitle: String,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        PplAvatar(name, photoUrl, size = 44.dp)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(dotColor))
                Text(
                    name,
                    color = PplInk,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            Text(
                subtitle,
                color = PplInk2,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(VIcons.ChevronRight, null, tint = PplInk3.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
    }
}

private data class Metric(val value: String, val label: String, val valueColor: Color = PplInk)

/**
 * Metric strip — a compact tinted panel with hairline-divided columns. Values
 * are big DM-Mono numerals; labels are single-line (never wrap to "STUDENT\nS")
 * with tightened tracking so all four teacher columns breathe evenly. The soft
 * surface + inset dividers give the strip weight so the card no longer reads as
 * an empty white block.
 */
@Composable
private fun MetricStrip(metrics: List<Metric>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(PplStripFill)
            .padding(vertical = 11.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        metrics.forEachIndexed { index, m ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    m.value,
                    color = m.valueColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.4).sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                )
                Text(
                    m.label.uppercase(),
                    color = PplInk3,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (index != metrics.lastIndex) {
                Box(Modifier.fillMaxHeight().width(1.dp).background(PplHairlineStrong))
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
        Modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) { tags.forEach { (text, theme) -> SubjectTag(text, theme) } }
}

/**
 * Action row — equal-weight pill buttons separated by a hairline. Each is a soft
 * tinted chip with an icon + label, so the footer reads as a deliberate control
 * bar instead of three lonely text links floating in white space. The primary
 * action (last, e.g. Assign / Message) is emphasised in the violet accent.
 */
@Composable
private fun ActionRow(actions: List<Triple<ImageVector, String, () -> Unit>>) {
    if (actions.isEmpty()) return
    Box(Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp).background(PplHairline))
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        actions.forEachIndexed { index, (icon, label, onClick) ->
            val primary = index == actions.lastIndex && actions.size > 1
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (primary) VioletSoftFill else PplStripFill)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
                    .padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(icon, label, tint = if (primary) PplAccent else PplInk2, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    label,
                    color = if (primary) PplAccentDeep else PplInk2,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Teacher card
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Teacher card presentation status. Distinct from the account active/inactive
 * flag: an active teacher with NO class allotted is surfaced as "Unassigned"
 * (never "Active"), per the requirement that a teacher with no classes must not
 * read as active.
 */
private enum class TeacherStatus(val label: String, val ink: Color, val fill: Color, val dot: Color) {
    Active("Active", SuccessInk, SuccessSoft, SuccessInk),
    Leave("On leave", WarningInk, WarningSoft, WarningInk),
    Unassigned("Unassigned", SkyInk, SkySoft, SkyCol),
    Inactive("Inactive", PplInk3, Color(0x14262340), PplInk3);

    companion object {
        fun resolve(rawStatus: String, totalClasses: Int): TeacherStatus = when {
            statusDotFor(rawStatus) == DotStatus.Leave -> Leave
            statusDotFor(rawStatus) == DotStatus.Inactive -> Inactive
            totalClasses <= 0 -> Unassigned
            else -> Active
        }
    }
}

/**
 * Premium status pill — a soft-tinted capsule with a small solid status dot.
 * Replaces the flat text pill so the active/leave/unassigned signal reads as a
 * deliberate, high-craft indicator rather than a plain label.
 */
@Composable
private fun PremiumTeacherStatusPill(status: TeacherStatus) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(status.fill)
            .padding(start = 8.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(status.dot))
        Text(status.label, color = status.ink, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
    }
}

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
    val totalClasses = teacher.workload.totalClasses
    val status = TeacherStatus.resolve(p.status, totalClasses)
    // Role + class-teacher flag — the subject chips below already carry subjects,
    // so we never render the redundant "Biology · Biology Teacher" from the old UI.
    val subtitle = buildList {
        add(p.role.ifBlank { "Teacher" })
        if (p.isClassTeacher) add("Class teacher")
        p.experience?.takeIf(String::isNotBlank)?.let { add("$it exp") }
    }.joinToString(" \u00B7 ")

    Box(modifier) {
        PersonCard(onClick = onViewProfile) {
            CardHeaderStatus(
                p.name, p.avatarUrl, status.dot, subtitle,
                trailing = { PremiumTeacherStatusPill(status) },
            )

            val attendance = teacher.activity.attendancePercentage
            val workloadPct = teacher.workload.workloadPercent
            // Metric strip — every column now carries real, always-available data.
            // The old "PEWS" column was empty most of the time (rating is null
            // until a teacher is rated); "Workload" is derived from live class
            // load, so the fourth column no longer reads as a dead "—".
            MetricStrip(
                listOf(
                    Metric(teacher.workload.totalStudents.toString(), "Students"),
                    Metric(totalClasses.toString(), "Classes"),
                    Metric(attendance?.let { "$it%" } ?: "\u2014", "Attend.", PplAccent),
                    Metric("$workloadPct%", "Workload", PplAccent),
                ),
            )

            if (teacher.academicAssignment.subjects.isNotEmpty()) {
                SubjectTagRow(
                    teacher.academicAssignment.subjects.mapIndexed { index, subject ->
                        subject to subjectPalette[index % subjectPalette.size]
                    },
                )
            } else {
                // No subjects assigned — a quiet inline hint instead of a blank gap,
                // reinforcing the "Unassigned" state and inviting the Assign action.
                Row(
                    Modifier.fillMaxWidth().padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(VIcons.School, null, tint = PplInk3, modifier = Modifier.size(13.dp))
                    Text(
                        "No classes assigned yet",
                        color = PplInk3,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            CompactActionBar(
                onCall = onCall,
                onMessage = onMessage,
                extra = if (teacher.actions.canAssignClass)
                    Triple(VIcons.School, "Assign", onAssignClass) else null,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Student card
// ─────────────────────────────────────────────────────────────────────────────

private fun tagThemeForColor(color: String): TagTheme = when (color.lowercase()) {
    "green" -> TagTheme.ScoreHi
    "red" -> TagTheme.ScoreLo
    "yellow", "amber", "orange" -> TagTheme.ScoreMid
    "sky", "blue" -> TagTheme.Sky
    "teal" -> TagTheme.Teal
    "peach" -> TagTheme.Peach
    else -> TagTheme.Violet
}

// ─────────────────────────────────────────────────────────────────────────────
//  Compact dense card primitives (v2 — kills the tall stacked panels)
//
//  The old Student/Staff cards stacked a full MetricStrip panel + a tall fee box
//  + a tag row + a full-width action bar, so each card was a column of near-empty
//  blocks. The primitives below pack the same signal into far less height:
//   • InlineStat   — one "value / label" cell, no heavy panel behind it.
//   • InlineStatRail — a hairline-divided single row of those cells.
//   • MetaChip     — a tiny icon+text status/fee chip that lives on the stat row.
//   • CompactActionBar — a slim footer: a primary violet button + quiet icon
//     buttons, right-weighted, instead of two lonely full-width pills.
// ─────────────────────────────────────────────────────────────────────────────

/** One stat cell — big DM-Mono value over a tight caption. */
@Composable
private fun InlineStat(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.Start) {
        Text(
            value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.4).sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
        )
        Text(
            label.uppercase(),
            color = PplInk3,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/**
 * A single compact stat rail — equal-weight [InlineStat] cells separated by thin
 * hairlines. No filled panel, no big padding: it sits directly on the card so the
 * card reads dense instead of as a stack of empty boxes.
 */
@Composable
private fun InlineStatRail(stats: List<Triple<String, String, Color>>) {
    if (stats.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        stats.forEachIndexed { index, (value, label, color) ->
            InlineStat(
                value = value,
                label = label,
                valueColor = color,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
            )
            if (index != stats.lastIndex) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(vertical = 1.dp)
                        .background(PplHairlineStrong),
                )
                Spacer(Modifier.width(12.dp))
            }
        }
    }
}

/** Tiny icon+label status chip that sits inline (used for fees / meeting flags). */
@Composable
private fun MetaChip(icon: ImageVector, label: String, ink: Color, fill: Color) {
    Row(
        Modifier.clip(RoundedCornerShape(7.dp)).background(fill).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, null, tint = ink, modifier = Modifier.size(11.dp))
        Text(label, color = ink, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
    }
}

/**
 * A guardian / contact strip — a soft-tinted panel that holds the parent name
 * and phone number so the student card footer no longer reads as empty white
 * space. The leading pip echoes the avatar palette; the number is DM-Mono so it
 * scans like a real dialable contact. Falls back gracefully when only one of
 * name / phone is on record.
 */
@Composable
private fun GuardianStrip(name: String?, phone: String?, relation: String = "Guardian") {
    val cleanName = name?.takeIf { it.isNotBlank() }
    val cleanPhone = phone?.takeIf { it.isNotBlank() }
    if (cleanName == null && cleanPhone == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PplStripFill)
            .padding(horizontal = 11.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(VioletSoftFill),
            contentAlignment = Alignment.Center,
        ) { Icon(VIcons.UsersGroup, null, tint = PplAccent, modifier = Modifier.size(15.dp)) }
        Column(Modifier.weight(1f)) {
            Text(
                cleanName ?: relation,
                color = PplInk,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                if (cleanName != null) relation.uppercase() else "CONTACT",
                color = PplInk3,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
        if (cleanPhone != null) {
            Text(
                cleanPhone,
                color = PplInk2,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                letterSpacing = (-0.3).sp,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

/**
 * Premium footer control bar — two equal-weight, icon-forward buttons that read
 * as one deliberate control cluster (the user asked for Call and Message to look
 * consistent). "Call" is a quiet tinted button; "Message" is the violet accent
 * button. Both share the same height, radius and icon size so neither looks like
 * an afterthought floating in white space.
 */
@Composable
private fun CompactActionBar(
    onCall: () -> Unit,
    onMessage: () -> Unit,
    messageLabel: String = "Message",
    callLabel: String = "Call",
    extra: Triple<ImageVector, String, () -> Unit>? = null,
) {
    Box(Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp).background(PplHairline))
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Quiet secondary — Call
        PplActionButton(
            icon = VIcons.Phone,
            label = callLabel,
            primary = false,
            modifier = Modifier.weight(1f),
            onClick = onCall,
        )
        // Optional third action (e.g. Assign) — quiet, equal weight
        if (extra != null) {
            PplActionButton(
                icon = extra.first,
                label = extra.second,
                primary = false,
                modifier = Modifier.weight(1f),
                onClick = extra.third,
            )
        }
        // Primary — Message (violet accent)
        PplActionButton(
            icon = VIcons.ChatPremium,
            label = messageLabel,
            primary = true,
            modifier = Modifier.weight(1f),
            onClick = onMessage,
        )
    }
}

/** One premium action button — consistent 40dp height, icon + label. */
@Composable
private fun PplActionButton(
    icon: ImageVector,
    label: String,
    primary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(11.dp)
    val base = if (primary) {
        Modifier
            .shadow(6.dp, shape, ambientColor = PplAccent.copy(alpha = 0.28f), spotColor = PplAccent.copy(alpha = 0.28f))
            .clip(shape)
            .background(Brush.linearGradient(listOf(PplAccent, PplAccentSoft)))
    } else {
        Modifier
            .clip(shape)
            .background(PplStripFill)
            .border(1.dp, PplHairlineStrong, shape)
    }
    Row(
        modifier = modifier
            .height(40.dp)
            .then(base)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            icon,
            label,
            tint = if (primary) VColors.white else PplInk2,
            modifier = Modifier.size(15.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            color = if (primary) VColors.white else PplInk2,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
internal fun StudentCard(
    student: StudentDto,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Roll + code + class in one tight subtitle — the class no longer needs its
    // own stat column, freeing the rail for the numbers that actually matter.
    val classLabel = listOf(student.className, student.section.takeIf(String::isNotBlank))
        .filterNotNull().joinToString("-")
    val subtitle = buildList {
        if (classLabel.isNotBlank()) add(classLabel)
        if (student.rollNumber.isNotBlank()) add("Roll ${student.rollNumber}")
        if (student.studentCode.isNotBlank()) add(student.studentCode)
    }.joinToString(" \u00B7 ").ifBlank { "Student" }

    // Tri-state fee signal (server: fee_status). We only render the fee pill when
    // we actually have data — a student with no fee records no longer reads as a
    // false-positive "Fees paid".
    val feeState = FeeState.from(student.feeStatus, student.feesPending)
    val avg = student.homeworkPercent.takeIf { it > 0f }?.let { "${it.roundToInt()}%" } ?: "\u2014"
    val att = student.attendancePercent.takeIf { it > 0f }?.let { "${it.roundToInt()}%" } ?: "\u2014"

    Box(modifier) {
        PersonCard(onClick = onOpen) {
            CardHeader(
                student.fullName, student.profilePhotoUrl, student.status, subtitle,
                trailing = { if (student.isNewAdmission) NewBadge() else StatusPill(student.status) },
            )

            // Dense rail: numeric signal (Homework / Attendance), plus the fee
            // chip inline — but only when there's real fee data to show.
            Row(
                Modifier.fillMaxWidth().padding(top = 12.dp).height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InlineStat(avg, "Homework", PplAccent, Modifier.weight(1f))
                Box(Modifier.fillMaxHeight().width(1.dp).padding(vertical = 1.dp).background(PplHairlineStrong))
                Spacer(Modifier.width(12.dp))
                InlineStat(att, "Attend.", SkyInk, Modifier.weight(1f))
                if (feeState != null) {
                    Spacer(Modifier.width(8.dp))
                    MetaChip(
                        icon = VIcons.WalletPremium,
                        label = feeState.label,
                        ink = feeState.ink,
                        fill = feeState.fill,
                    )
                }
            }

            // Parent / guardian contact — fills the previously-empty footer area
            // with the info an admin actually needs to reach the family.
            GuardianStrip(
                name = student.parentName,
                phone = student.parentPhone,
                relation = "Guardian",
            )

            SubjectTagRow(
                student.todayItems
                    .filter { it.text.isNotBlank() }
                    .take(3)
                    .map { item: TodayItemDto -> item.text to tagThemeForColor(item.color) },
            )

            CompactActionBar(onCall = onCall, onMessage = onMessage)
        }
    }
}

/** Fee pill presentation resolved from the server's tri-state fee_status. */
private enum class FeeState(val label: String, val ink: Color, val fill: Color) {
    Paid("Fees paid", SuccessInk, SuccessSoft),
    Pending("Fees due", WarningInk, WarningSoft);

    companion object {
        /** Returns null for "no fee data" so the card shows no misleading pill. */
        fun from(status: String, legacyPending: Boolean): FeeState? = when (status.uppercase()) {
            "PAID" -> Paid
            "PENDING" -> Pending
            "NONE" -> null
            // Legacy payloads without fee_status fall back to the boolean, but we
            // still only surface a pill when it explicitly signals a due balance.
            else -> if (legacyPending) Pending else null
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
    val role = staff.role.takeIf(String::isNotBlank)?.replaceFirstChar { it.uppercase() } ?: "Staff"
    val dept = staff.department?.takeIf(String::isNotBlank)
    val shift = staff.shift?.takeIf(String::isNotBlank)
    // Role + department + shift, deduped, in one tight subtitle line.
    val subtitle = listOfNotNull(
        role,
        dept?.takeIf { !it.equals(role, true) },
        shift?.let { "$it shift" },
    ).joinToString(" \u00B7 ")

    // Employment type — full-time for active staff, else the real status word.
    val employment = if (staff.status.equals("active", true) || staff.status.isBlank()) "Full-time" else
        staff.status.replaceFirstChar { it.uppercase() }
    val phone = staff.phone?.takeIf(String::isNotBlank)
    val email = staff.email?.takeIf(String::isNotBlank)

    Box(modifier) {
        PersonCard(onClick = onOpen) {
            CardHeader(
                staff.fullName, staff.photoUrl, staff.status, subtitle,
                trailing = { StatusPill(staff.status) },
            )

            InlineStatRail(
                listOf(
                    Triple(staff.joinedYear?.takeIf(String::isNotBlank) ?: "\u2014", "Joined", PplAccent),
                    Triple(employment, "Type", PplInk),
                    Triple(staff.employeeId?.takeIf(String::isNotBlank) ?: "\u2014", "Emp ID", PplInk),
                ),
            )

            // Contact strip — a dense tinted panel so the card body no longer
            // has a dead white gap between the stat rail and the actions. Shows
            // the phone (dialable) with the email as a quiet second line.
            if (phone != null || email != null) {
                ContactStrip(phone = phone, email = email)
            }

            CompactActionBar(onCall = onCall, onMessage = onMessage)
        }
    }
}

/**
 * Staff contact strip — mirrors [GuardianStrip] so student & staff cards share
 * one visual language. Leading phone plate, dialable number in DM-Mono, and a
 * quiet email underneath when present.
 */
@Composable
private fun ContactStrip(phone: String?, email: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PplStripFill)
            .padding(horizontal = 11.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(VioletSoftFill),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (phone != null) VIcons.Phone else VIcons.Mail,
                null,
                tint = PplAccent,
                modifier = Modifier.size(15.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                phone ?: email ?: "\u2014",
                color = PplInk,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = if (phone != null) FontFamily.Monospace else FontFamily.Default,
                letterSpacing = if (phone != null) (-0.3).sp else (-0.2).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (phone != null && email != null) {
                Text(
                    email,
                    color = PplInk3,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 1.dp),
                )
            } else {
                Text(
                    if (phone != null) "PHONE" else "EMAIL",
                    color = PplInk3,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }
    }
}
