package com.littlebridge.enrollplus.ui.v2.screens.discovery

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.CalendarEventDto
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarState
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.PremiumOverlayHeader
import com.littlebridge.enrollplus.ui.v2.screens.teacher.VtC
import com.littlebridge.enrollplus.ui.v2.screens.teacher.VtT
import com.littlebridge.enrollplus.ui.v2.screens.teacher.coloredV
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.Qualifier

/**
 * AcademicCalendarScreenV2 — month grid + upcoming-events list, wired to the real
 * [AcademicCalendarViewModel] → `GET /api/v1/school/calendar?date=&view_type=` endpoint.
 * (Phase C batch 4; BACKEND_GAPS.md §4.1.)
 *
 * The previous implementation was driven by `MockV2.calendarEvents` (a fixed June 2026 seed).
 * This rewrite wires the real backend:
 *  - The VM's `currentMonth` label drives the header ("June 2026").
 *  - The day grid tints a day when the real `CalendarEventDto.date` falls on it.
 *  - The "Upcoming events" list renders one card per real event with title + ISO date.
 *
 * The server today does not categorise events (no `type` field), so the per-day color coding
 * collapses to a single accent tone. When the backend adds an event-type taxonomy we'll
 * restore the four-color scheme exactly as the React design specifies.
 */
@Composable
fun AcademicCalendarScreenV2(
    onBack: () -> Unit,
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
    modifier: Modifier = Modifier,
) {
    val c = VtC
    Column(modifier.fillMaxSize().background(VColors.cream)) {
        PremiumOverlayHeader(title = appString(StringKeys.CAL_ACADEMIC_TITLE), onBack = onBack)

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            // The grid itself is always shown even when no events exist this month —
            // a school can have zero events without being "empty". Only suppress when
            // we never got back a month label (i.e. the call never resolved).
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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Month header (the chevron pills navigate via the VM).
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MonthPill(appString(StringKeys.CAL_PREV), onClick = onPrev)
                    Text(
                        state.currentMonth.ifBlank { "—" },
                        style = VtT.bodyStrong.coloredV(c.ink),
                    )
                    MonthPill(appString(StringKeys.CAL_NEXT_BTN), onClick = onNext)
                }

                // Day grid — derive the visible month from `currentDate` (ISO YYYY-MM-DD),
                // then render N days for that month, tinting any day that hosts an event.
                val (year, monthIdx, _) = parseIsoDate(state.currentDate) ?: Triple(0, 0, 0)
                val daysInMonth = if (year != 0 && monthIdx != 0) daysInMonth(year, monthIdx) else 30
                val eventsByDay: Map<Int, List<CalendarEventDto>> = remember(state.calendarEvents) {
                    state.calendarEvents.groupBy { dayOfIsoDate(it.date) ?: 0 }
                        .filterKeys { it in 1..31 }
                }

                VCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                Text(d, style = VtT.label.coloredV(c.ink3))
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    val weeks = (1..daysInMonth).toList().chunked(7)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        weeks.forEach { week ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                week.forEach { day ->
                                    val hasEvent = day in eventsByDay
                                    // Server doesn't categorise events yet — single accent tone
                                    // for every event day. When the backend grows a type/category
                                    // taxonomy we restore the React four-color palette.
                                    val tone = if (hasEvent) c.teal.copy(alpha = 0.16f) else Color.Transparent
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(CircleShape)
                                            .background(tone),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(day.toString(), style = VtT.caption.coloredV(c.ink))
                                    }
                                }
                                repeat(7 - week.size) { Box(Modifier.weight(1f)) {} }
                            }
                        }
                    }
                }

                // Optional summary row — only show when the server returned non-zero stats.
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

                VLabel(appString(StringKeys.CAL_UPCOMING_EVENTS))
                if (state.calendarEvents.isEmpty()) {
                    Text(
                        appString(StringKeys.CAL_NO_EVENTS),
                        style = VtT.caption.coloredV(c.ink2),
                    )
                } else {
                    state.calendarEvents.forEach { e ->
                        EventRow(e)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventRow(e: CalendarEventDto) {
    val c = VtC
    val day = dayOfIsoDate(e.date)
    val monthShort = monthShortOfIsoDate(e.date)
    VCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.size(width = 48.dp, height = 44.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    (day?.toString() ?: "—"),
                    style = VtT.dataLg.coloredV(c.ink).copy(fontSize = 20.sp),
                )
                Text((monthShort ?: ""), style = VtT.label.coloredV(c.ink3))
            }
            Column(Modifier.weight(1f)) {
                Text(e.eventTitle, style = VtT.bodyStrong.coloredV(c.ink))
                Text(
                    e.eventDescription.ifBlank { e.day.ifBlank { e.date } },
                    style = VtT.label.coloredV(c.ink3),
                )
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = c.ink3, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
    val c = VtC
    VCard(modifier = modifier) {
        Text(value, style = VtT.h3.coloredV(c.ink))
        Text(label, style = VtT.label.coloredV(c.ink3))
    }
}

@Composable
private fun MonthPill(label: String, onClick: () -> Unit) {
    val c = VtC
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(c.cream)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(label, style = VtT.caption.coloredV(c.ink2))
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
