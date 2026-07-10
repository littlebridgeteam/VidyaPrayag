package com.littlebridge.enrollplus.ui.v2.screens.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.CalendarEventDto
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarState
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.PremiumOverlayHeader
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.Qualifier

/**
 * AcademicCalendarScreenV2 — premium month grid + events list for the parent portal.
 * Wired to [AcademicCalendarViewModel] → `GET /api/v1/school/calendar?date=&view_type=`.
 * Events are clickable and navigate to event registration. Uses the parent portal's
 * warm cream theme (VColors/VShapes/VTypography) consistent with system-wide UI/UX.
 */
@Composable
fun AcademicCalendarScreenV2(
    onBack: () -> Unit,
    onOpenEventRegistration: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModelQualifier: Qualifier? = null,
    viewModel: AcademicCalendarViewModel = koinViewModel(qualifier = viewModelQualifier),
) {
    val state by viewModel.state.collectAsStateV2()
    AcademicCalendarContent(
        state = state,
        onBack = onBack,
        onPrev = viewModel::goToPreviousMonth,
        onNext = viewModel::goToNextMonth,
        onRetry = { viewModel.loadCalendar() },
        onOpenEventRegistration = onOpenEventRegistration,
        modifier = modifier.statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    )
}

@Composable
private fun AcademicCalendarContent(
    state: AcademicCalendarState,
    onBack: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    onOpenEventRegistration: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(VColors.cream)) {
        PremiumOverlayHeader(title = appString(StringKeys.CAL_ACADEMIC_TITLE), onBack = onBack)

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = !state.isLoading && state.errorMessage == null && state.currentMonth.isBlank(),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            emptyTitle = appString(StringKeys.CAL_NOT_AVAILABLE),
            emptyBody = appString(StringKeys.CAL_SIGN_IN_PROMPT),
            emptyIcon = VIcons.Calendar,
            onRetry = onRetry,
            skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonCalendar() },
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Month header with navigation pills
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MonthPill(appString(StringKeys.CAL_PREV), onClick = onPrev)
                    Text(
                        state.currentMonth.ifBlank { "—" },
                        style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = VColors.ink,
                    )
                    MonthPill(appString(StringKeys.CAL_NEXT_BTN), onClick = onNext)
                }

                // Day grid
                val (year, monthIdx, _) = parseIsoDate(state.currentDate) ?: Triple(0, 0, 0)
                val daysInMonth = if (year != 0 && monthIdx != 0) daysInMonth(year, monthIdx) else 30
                val eventsByDay: Map<Int, List<CalendarEventDto>> = remember(state.calendarEvents) {
                    state.calendarEvents.groupBy { dayOfIsoDate(it.date) ?: 0 }
                        .filterKeys { it in 1..31 }
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(VShapes.xl)
                        .background(VColors.surfaceCard)
                        .border(1.dp, VColors.line, VShapes.xl)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                Text(d, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink3)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    val weeks = (1..daysInMonth).toList().chunked(7)
                    weeks.forEach { week ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            week.forEach { day ->
                                val hasEvent = day in eventsByDay
                                val tone = if (hasEvent) VColors.violet.copy(alpha = 0.12f) else Color.Transparent
                                val dotColor = if (hasEvent) VColors.violet else Color.Transparent
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(tone),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(day.toString(), style = VTypography.caption, color = VColors.ink)
                                    Box(
                                        Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp)
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(dotColor),
                                    )
                                }
                            }
                            repeat(7 - week.size) { Box(Modifier.weight(1f)) {} }
                        }
                    }
                }

                // Stats row
                if (state.workingDays > 0 || state.holidays > 0) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatPill(
                            label = appString(StringKeys.CAL_WORKING_DAYS),
                            value = state.workingDays.toString(),
                            modifier = Modifier.weight(1f),
                        )
                        StatPill(
                            label = appString(StringKeys.CAL_HOLIDAYS),
                            value = state.holidays.toString(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                // Events section
                Text(
                    appString(StringKeys.CAL_UPCOMING_EVENTS),
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = VColors.ink,
                )
                if (state.calendarEvents.isEmpty()) {
                    Text(
                        appString(StringKeys.CAL_NO_EVENTS),
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                } else {
                    state.calendarEvents.forEach { e ->
                        EventRow(e, onClick = onOpenEventRegistration)
                    }
                }

                // Register for events CTA
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(VShapes.lg)
                        .background(VColors.violet)
                        .clickable { onOpenEventRegistration() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Register for Events",
                        style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                        color = VColors.white,
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun EventRow(e: CalendarEventDto, onClick: () -> Unit) {
    val day = dayOfIsoDate(e.date)
    val monthShort = monthShortOfIsoDate(e.date)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            Modifier.size(width = 52.dp, height = 52.dp)
                .clip(VShapes.md)
                .background(VColors.violetSoft),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                (day?.toString() ?: "—"),
                style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = VColors.violet,
            )
            Text(
                (monthShort ?: ""),
                style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = VColors.violet,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(e.eventTitle, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
            Text(
                e.eventDescription.ifBlank { e.day.ifBlank { e.date } },
                style = VTypography.caption,
                color = VColors.ink3,
                maxLines = 2,
            )
        }
        Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) {
        Text(value, style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp), color = VColors.ink)
        Text(label, style = VTypography.caption, color = VColors.ink3)
    }
}

@Composable
private fun MonthPill(label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(VShapes.full)
            .background(VColors.creamDeep)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Text(label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
    }
}

// ── small date helpers (kept local to avoid pulling kotlinx-datetime just for this) ─────

/** Parses "YYYY-MM-DD" into Triple(year, month, day). Returns null on bad input. */
private fun parseIsoDate(iso: String): Triple<Int, Int, Int>? {
    if (iso.length < 10) return null
    val y = iso.substring(0, 4).toIntOrNull() ?: return null
    val m = iso.substring(5, 7).toIntOrNull() ?: return null
    val d = iso.substring(8, 10).toIntOrNull() ?: return null
    if (m !in 1..12 || d !in 1..31) return null
    return Triple(y, m, d)
}

private fun dayOfIsoDate(iso: String): Int? = parseIsoDate(iso)?.third

private fun monthShortOfIsoDate(iso: String): String? {
    val m = parseIsoDate(iso)?.second ?: return null
    return MONTH_SHORT.getOrNull(m - 1)
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
    else -> 30
}

private val MONTH_SHORT = listOf(
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
    "JUL", "AUG", "SEP", "OCT", "NOV", "DEC",
)
