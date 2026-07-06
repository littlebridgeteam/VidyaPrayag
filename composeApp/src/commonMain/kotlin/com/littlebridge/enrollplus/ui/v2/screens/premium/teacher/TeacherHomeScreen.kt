package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.teacher.presentation.ResolvedPeriodUi
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayViewModel
import com.littlebridge.enrollplus.ui.v2.components.typography.VGreetingEyebrow
import com.littlebridge.enrollplus.ui.v2.components.typography.VGreetingTitle
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.tokens.rememberLiveBlink
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: TeacherTodayViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Greeting ──
        Column(Modifier.padding(horizontal = 20.dp)) {
            VGreetingEyebrow("Welcome back")
            Spacer(Modifier.height(4.dp))
            VGreetingTitle("Today's Classes")
        }
        Spacer(Modifier.height(20.dp))

        if (state.isLoading && state.day == null) {
            StatusBox("Loading your schedule...")
            return@Column
        }

        if (state.error != null && state.day == null) {
            StatusBox(state.error!!, isError = true)
            return@Column
        }

        val day = state.day
        if (day == null) {
            StatusBox("No schedule loaded")
            return@Column
        }

        if (day.isHoliday) {
            HolidayCard(day.holidayName ?: "Holiday")
            return@Column
        }

        if (day.periods.isEmpty()) {
            StatusBox("No classes scheduled today")
            return@Column
        }

        // ── Quick stats row ──
        Row(
            Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            QuickStat("${day.periods.size}", "Classes", Modifier.weight(1f))
            val marked = day.periods.count { it.attendanceMarked }
            QuickStat("$marked", "Marked", Modifier.weight(1f))
            val cancelled = day.periods.count { it.isCancelled }
            QuickStat("$cancelled", "Cancelled", Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        VSectionHeader("Today's Schedule")
        Spacer(Modifier.height(12.dp))

        day.periods.forEach { period ->
            PeriodCard(period)
            Spacer(Modifier.height(10.dp))
        }

        if (day.calendar.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            VSectionHeader("Calendar Events")
            Spacer(Modifier.height(12.dp))
            day.calendar.forEach { evt ->
                CalendarEventCard(evt.title, evt.audience)
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PeriodCard(period: ResolvedPeriodUi) {
    val isCancelled = period.isCancelled
    val isLive = !isCancelled && period.attendanceMarked.not() && false
    val interaction = remember { MutableInteractionSource() }
    val bg = if (isCancelled) VColors.ErrorContainer else VColors.SurfaceContainerLowest
    val onColor = if (isCancelled) VColors.OnErrorContainer else VColors.OnSurface
    val onColorVariant = if (isCancelled) VColors.OnErrorContainer.copy(alpha = 0.7f) else VColors.OnSurfaceVariant

    Row(
        Modifier.padding(horizontal = 20.dp).fillMaxWidth()
            .clip(VShapes.Xl)
            .background(bg)
            .then(
                if (isLive) Modifier.radialGlow(offsetX = 120.dp, offsetY = (-40).dp, radius = 140.dp, color = VColors.LiveCyan.copy(alpha = 0.15f))
                else Modifier
            )
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Time block
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                period.startTime,
                style = VTypography.NavLabel.copy(color = onColor, fontWeight = FontWeight.Bold, fontSize = 13.sp),
            )
            Text(
                period.endTime,
                style = VTypography.NavLabel.copy(color = onColorVariant, fontSize = 11.sp),
            )
        }
        // Vertical divider
        Box(Modifier.size(1.dp, 36.dp).background(VColors.Outline.copy(alpha = 0.2f)))
        Column(Modifier.weight(1f)) {
            Text(
                period.subject,
                style = VTypography.UpdateTitle.copy(color = onColor, fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${period.classLabel} · Room ${period.room}",
                style = VTypography.NavLabel.copy(color = onColorVariant),
            )
        }
        // Status badge
        if (period.attendanceMarked) {
            Box(
                Modifier.clip(VShapes.Full).background(VColors.TertiaryContainer).padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VColors.Tertiary, modifier = Modifier.size(12.dp))
                    Text("DONE", style = VTypography.NavLabel.copy(color = VColors.OnTertiaryContainer, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                }
            }
        } else if (isCancelled) {
            Box(
                Modifier.clip(VShapes.Full).background(VColors.Error).padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("CANCELLED", style = VTypography.NavLabel.copy(color = VColors.OnError, fontWeight = FontWeight.Bold, fontSize = 10.sp))
            }
        } else if (isLive) {
            val blinkAlpha = rememberLiveBlink()
            val liveColor = VColors.LiveCyan
            Row(
                Modifier.clip(VShapes.Full).background(VColors.GlassWhite20).padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(liveColor.copy(alpha = blinkAlpha)))
                Text("LIVE", style = VTypography.NavLabel.copy(color = onColor, fontWeight = FontWeight.Bold, fontSize = 10.sp))
            }
        }
    }
}

@Composable
private fun HolidayCard(name: String) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(VShapes.TwoXl).background(VColors.PrimaryContainer).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.Schedule, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text(name, style = VTypography.GreetingTitle.copy(color = VColors.OnPrimaryContainer))
        Text("No classes today", style = VTypography.UpdateText.copy(color = VColors.OnPrimaryContainer.copy(alpha = 0.7f)))
    }
}

@Composable
private fun QuickStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VTypography.HeroStatValue.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun CalendarEventCard(title: String, audience: String) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.padding(horizontal = 20.dp).fillMaxWidth()
            .clip(VShapes.Xl).background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(VColors.Primary))
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(audience, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(120.dp).clip(VShapes.Xl)
            .background(if (isError) VColors.ErrorContainer else VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.OnErrorContainer else VColors.OnSurfaceVariant))
    }
}
