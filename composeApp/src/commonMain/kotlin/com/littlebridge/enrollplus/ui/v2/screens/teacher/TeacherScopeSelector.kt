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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString

/**
 * TeacherScopeSelector — the scope gate that fronts every UPDATE sub-screen (Attendance, Marks,
 * Syllabus, Homework). Per the scope law, the teacher first picks ONE of their own allocations
 * (class · section · subject), which resolves to the pre-authorized `assignmentId` every typed
 * endpoint requires. Only the teacher's real allocations appear — there is no free-text class.
 *
 * A class-teacher row is badged; a row whose attendance is already done today shows a green tick.
 * Selecting a row hands its [TeacherClassSummaryDto] back so the host can carry both the
 * assignmentId AND the human scope label into the sub-screen's wrong-class guard header.
 */
@Composable
fun TeacherScopeSelector(
    classes: List<TeacherClassSummaryDto>,
    onPick: (TeacherClassSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
    title: String = appString(StringKeys.TC_PICK_CLASS),
    caption: String = appString(StringKeys.TC_PICK_ALLOCATION_DESC),
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(classes, query) {
        if (query.isBlank()) classes
        else classes.filter { "${it.className} ${it.section} ${it.subject}".contains(query.trim(), ignoreCase = true) }
    }

    Column(modifier.fillMaxWidth()) {
        ScopeSelectorHeading(title, caption)
        Spacer(Modifier.height(14.dp))

        if (classes.size > 6) {
            ScopeSearchField(query) { query = it }
            Spacer(Modifier.height(10.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered, key = { it.assignmentId }) { cls ->
                ScopeRow(cls, onPick)
            }
        }
    }
}

/** The eyebrow + title + caption block shared by the standalone selector and the
 *  LazyListScope variant below. */
@Composable
fun ScopeSelectorHeading(title: String, caption: String) {
    Column {
        VtEyebrow(appString(StringKeys.SCH_SELECT_SCOPE), dot = VColors.violet)
        Spacer(Modifier.height(6.dp))
        Text(title, style = VTypography.h2.copy(color = VColors.ink))
        Spacer(Modifier.height(2.dp))
        Text(caption, style = VTypography.body.copy(color = VColors.ink2))
    }
}

/**
 * scopeSelectorItems — emits the scope rows straight into a host [LazyColumn] so the
 * WHOLE screen (header, tool rail, intro, class list) scrolls as one, instead of the
 * selector owning a nested scroll. Row content and behaviour are identical to
 * [TeacherScopeSelector]; the host owns the scroll and provides the surrounding chrome.
 */
fun LazyListScope.scopeSelectorItems(
    classes: List<TeacherClassSummaryDto>,
    onPick: (TeacherClassSummaryDto) -> Unit,
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {
    items(classes, key = { it.assignmentId }) { cls ->
        Box(Modifier.fillMaxWidth().padding(horizontal = horizontalPadding)) {
            ScopeRow(cls, onPick)
        }
    }
}

@Composable
private fun ScopeSearchField(value: String, onChange: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VColors.surfaceTint)
            .border(1.dp, VColors.line, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(VIcons.Search, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(16.dp))
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = VTypography.body.copy(color = VColors.ink),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(VColors.violet),
            decorationBox = { inner ->
                Box {
                    if (value.isBlank()) Text(appString(StringKeys.TC_SEARCH_CLASSES), style = VTypography.body.copy(color = VColors.ink3))
                    inner()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ScopeRow(cls: TeacherClassSummaryDto, onPick: (TeacherClassSummaryDto) -> Unit) {
    val accent = vtSubjectColor(cls.subject.ifBlank { cls.className })
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, RoundedCornerShape(18.dp))
            .clickable { onPick(cls) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VtIconDisc(VIcons.School, tint = accent, bg = accent.copy(alpha = 0.12f), size = 44.dp, glyph = 22.dp)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (cls.section.isBlank()) cls.className else "${cls.className}-${cls.section}",
                    style = VTypography.body.copy(color = VColors.ink),
                )
                if (cls.isClassTeacher) {
                    VtPill(appString(StringKeys.TC_CLASS_TEACHER), bg = VColors.violet.copy(alpha = 0.12f), fg = VColors.violetInk)
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                appString(StringKeys.TC_STUDENTS_COUNT, "subject" to cls.subject.ifBlank { "—" }, "count" to cls.studentCount.toString()),
                style = VTypography.caption.copy(color = VColors.ink2),
            )
        }
        if (cls.todayAttendanceMarked) {
            VtIconDisc(VIcons.Check, tint = VColors.success, bg = VColors.success.copy(alpha = 0.18f), size = 28.dp, glyph = 15.dp)
        } else {
            Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(20.dp))
        }
    }
}
