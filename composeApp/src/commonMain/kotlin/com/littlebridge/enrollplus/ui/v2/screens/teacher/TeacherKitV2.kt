package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons

/**
 * VtC / VtT — the shared legacy-token bridge. Screens mid-migration bind
 * `val c = VtC` in place of the old `val c = VTheme.colors`, mapping every
 * legacy lavender token name onto the cream/violet [VColors] system. This lets a
 * screen retire its [com.littlebridge.enrollplus.ui.v2.theme.VTheme] dependency
 * without touching any layout/branch logic. Prefer using [VColors]/[VTypography]
 * directly in brand-new code; this bridge exists purely for in-place migration.
 */
object VtC {
    val background: Color get() = VColors.cream
    val card: Color get() = VColors.white
    val cream: Color get() = VColors.surfaceTint      // subtle chip / disc fill
    val hairline: Color get() = VColors.line
    val ink: Color get() = VColors.ink
    val ink2: Color get() = VColors.ink2
    val ink3: Color get() = VColors.ink3
    val navyDeep: Color get() = VColors.ink           // primary heading ink
    val accent: Color get() = VColors.violet
    val accentDeep: Color get() = VColors.violetInk
    val accentTint: Color get() = VColors.violetSoft
    val teal: Color get() = VColors.mint              // positive / coverage
    val tealDeep: Color get() = VColors.success
    val success: Color get() = VColors.success
    val successInk: Color get() = VColors.success
    val border2: Color get() = VColors.lineSoft
    val placeholder: Color get() = VColors.ink3
    val warning: Color get() = VColors.gold
    val warningInk: Color get() = VColors.gold
    val danger: Color get() = VColors.error
    val dangerInk: Color get() = VColors.error
    val warmOrange: Color get() = VColors.coral       // legacy warm accent
    val sky: Color get() = VColors.sky
    val navy: Color get() = VColors.ink               // legacy deep navy -> ink
    val accentSoft: Color get() = VColors.violetSoft
    val lavenderLight: Color get() = VColors.surfaceTint
    /** Legacy dark-mode flag; the cream system is a single light theme. */
    val isNight: Boolean get() = false
}

/** Typography twin for the [VtC] bridge — maps legacy VTheme.type.* names. */
object VtT {
    val h2: TextStyle get() = VTypography.h2
    val h3: TextStyle get() = VTypography.h3
    val body: TextStyle get() = VTypography.body
    val bodyStrong: TextStyle get() = VTypography.body
    val bodySmall: TextStyle get() = VTypography.caption
    val caption: TextStyle get() = VTypography.caption
    val label: TextStyle get() = VTypography.label
    val dataLg: TextStyle get() = VTypography.h2       // large numeric figure
    val dataSm: TextStyle get() = VTypography.label    // small numeric figure
}

/** Local `colored` extension so migrated screens keep `style.colored(c.x)` calls. */
fun TextStyle.coloredV(color: Color): TextStyle = copy(color = color)

/**
 * TeacherKitV2 — the token-based atom set for the rebuilt Teacher Portal.
 *
 * Per TEACHER_PORTAL_REDESIGN.md §3.3 / §4-§7, rebuilt screens must use the
 * [VColors] / [VTypography] / [VShapes] tokens DIRECTLY (the warm cream base and
 * deep violet accent) instead of the legacy lavender [VTheme] helpers (TCard,
 * TEyebrow, …). These atoms are the clean bridge: same vocabulary, new tokens.
 *
 * Naming uses the `Vt*` prefix (V-teacher) to avoid clashing with the legacy
 * `T*` helpers in TeacherKit.kt while the migration is in progress.
 */

/** Dock clearance: scrollable content must clear the floating dock (§8.4). */
val TeacherDockClearance: Dp = 120.dp

/** Standard white card surface with a hairline outline (§7.1). */
@Composable
fun VtCard(
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var base = modifier
        .fillMaxWidth()
        .clip(VShapes.lg)
        .background(VColors.surfaceCard)
        .border(1.dp, VColors.line, VShapes.lg)
    if (onClick != null) {
        base = base.clickable(onClick = onClick)
    }
    Box(base.padding(padding)) { content() }
}

/** All-caps eyebrow label with an optional coloured leading dot (§7.5). */
@Composable
fun VtEyebrow(text: String, dot: Color? = null, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (dot != null) Box(Modifier.size(6.dp).clip(CircleShape).background(dot))
        Text(
            text.uppercase(),
            style = VTypography.label.copy(letterSpacing = 0.6.sp, color = VColors.ink3),
        )
    }
}

/** Section header: title (h3) + optional trailing violet text action (§7.5). */
@Composable
fun VtSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = VTypography.h3.copy(color = VColors.ink))
        if (actionLabel != null && onAction != null) {
            Text(
                actionLabel,
                style = VTypography.label.copy(color = VColors.violet),
                modifier = Modifier.clickable { onAction() },
            )
        }
    }
}

/** Rounded status pill (§7.3). */
@Composable
fun VtPill(
    label: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier.clip(VShapes.full).background(bg).padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (leading != null) leading()
        Text(
            label,
            style = VTypography.caption.copy(color = fg),
        )
    }
}

/** Circular icon chip on a soft tinted disc (§7.4 leading). */
@Composable
fun VtIconDisc(icon: ImageVector, tint: Color, bg: Color, size: Dp = 44.dp, glyph: Dp = 22.dp) {
    Box(Modifier.size(size).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(glyph))
    }
}

/** Compact metric tile: number over caption on a soft tinted plate. */
@Composable
fun VtMetricTile(value: String, label: String, tint: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(VShapes.md)
            .background(tint.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VTypography.h3.copy(color = VColors.ink))
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTypography.caption.copy(color = VColors.ink3), textAlign = TextAlign.Center)
    }
}

/** Compact metric pill: smaller, horizontally-friendly variant for cramped tool headers. */
@Composable
fun VtCompactMetric(value: String, label: String, tint: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(VShapes.md)
            .background(tint.copy(alpha = 0.10f))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VTypography.caption.copy(color = VColors.ink))
        Text(label, style = VTypography.caption, color = VColors.ink3)
    }
}

/** Centre-screen host for spinners / errors on the cream base. */
@Composable
fun VtCenterState(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            content()
        }
    }
}

/** Empty-state card: soft tinted icon disc + title + optional subtext (§7.6). */
@Composable
fun VtEmptyCard(
    title: String,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    icon: ImageVector? = null,
    tint: Color = VColors.violet,
) {
    VtCard(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (icon != null) {
                VtIconDisc(icon, tint = tint, bg = tint.copy(alpha = 0.12f), size = 48.dp, glyph = 24.dp)
            }
            Text(
                title,
                style = VTypography.caption.copy(color = VColors.ink),
                textAlign = TextAlign.Center,
            )
            if (subtext != null) {
                Text(
                    subtext,
                    style = VTypography.caption.copy(color = VColors.ink3),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Inline error card with a retry affordance. */
@Composable
fun VtErrorState(
    title: String,
    detail: String?,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        VtIconDisc(VIcons.AlertTriangle, tint = VColors.coral, bg = VColors.coralSoft, size = 48.dp, glyph = 24.dp)
        Text(title, style = VTypography.h3.copy(color = VColors.ink), textAlign = TextAlign.Center)
        if (!detail.isNullOrBlank()) {
            Text(detail, style = VTypography.caption.copy(color = VColors.ink2), textAlign = TextAlign.Center, maxLines = 3)
        }
        Spacer(Modifier.height(4.dp))
        Row(
            Modifier
                .clip(VShapes.full)
                .background(VColors.violetSoft)
                .clickable { onRetry() }
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(retryLabel, style = VTypography.label.copy(color = VColors.violet))
        }
    }
}

/** Deterministic subject-accent colour from the token accent palette. */
fun vtSubjectColor(key: String): Color {
    val palette = listOf(VColors.violet, VColors.mint, VColors.sky, VColors.coral, VColors.gold)
    if (key.isBlank()) return palette.first()
    val idx = ((key.hashCode() % palette.size) + palette.size) % palette.size
    return palette[idx]
}
