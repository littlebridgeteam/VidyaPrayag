package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherAssignmentOverviewDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherClassAssignmentDto
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherAssignmentUiState
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherAssignmentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherAssignmentManagementScreen(
    teacherId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherAssignmentViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(teacherId) { viewModel.load(teacherId) }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(
            title = "Assignment Management",
            onBack = onBack,
        )
        AssignmentContent(
            state = state,
            onRetry = viewModel::retry,
            onSelectSubject = viewModel::selectSubject,
            onToggleClass = viewModel::toggleClass,
            onToggleSection = viewModel::toggleSection,
            onResetDraft = viewModel::resetDraft,
            onSave = { viewModel.saveDraft() },
            onRemove = viewModel::removeAssignment,
            onClearMessage = viewModel::clearSaveMessage,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun AssignmentContent(
    state: TeacherAssignmentUiState,
    onRetry: () -> Unit,
    onSelectSubject: (String?, String?) -> Unit,
    onToggleClass: (String) -> Unit,
    onToggleSection: (String) -> Unit,
    onResetDraft: () -> Unit,
    onSave: () -> Unit,
    onRemove: (String) -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingRemoveId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.overview == null && !state.isLoading && state.error == null,
            emptyTitle = appString(StringKeys.SCH_NO_TEACHER),
            emptyBody = appString(StringKeys.SCH_NO_TEACHER_DESC),
            emptyIcon = VIcons.User,
            onRetry = onRetry,
            skeleton = { SkeletonDashboard() },
        ) {
            val overview = state.overview ?: return@VStateHost
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                TeacherHero(overview)
                AssignmentKpis(overview)
                CurrentAssignments(
                    assignments = overview.assignments,
                    removingId = state.removingId,
                    onRequestRemove = { pendingRemoveId = it },
                )
                state.removeError?.let { Text(it, style = VTypography.caption, color = VColors.error) }
                AddAssignmentCard(
                    state = state,
                    onSelectSubject = onSelectSubject,
                    onToggleClass = onToggleClass,
                    onToggleSection = onToggleSection,
                    onResetDraft = onResetDraft,
                    onSave = onSave,
                    onClearMessage = onClearMessage,
                )
                WorkloadInsights(overview.insights)
                SubjectDistribution(overview)
            }
        }
    }

    VConfirmDialog(
        visible = pendingRemoveId != null,
        title = appString(StringKeys.SCH_REMOVE_ASSIGNMENT),
        message = appString(StringKeys.SCH_REMOVE_ASSIGNMENT_DESC),
        confirmLabel = appString(StringKeys.SCH_REMOVE),
        icon = VIcons.AlertTriangle,
        onConfirm = {
            pendingRemoveId?.let(onRemove)
            pendingRemoveId = null
        },
        onDismiss = { pendingRemoveId = null },
    )
}

@Composable
private fun TeacherHero(overview: TeacherAssignmentOverviewDto) {
    val summary = overview.summary
    val leadingSubject = overview.distribution.firstOrNull()?.subject?.takeIf { it.isNotBlank() }

    VCard(padding = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier.size(76.dp).clip(CircleShape).background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                VAvatar(name = summary.teacherName, size = 72.dp, ring = true)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    summary.teacherName,
                    style = VTypography.h2.copy(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold),
                    color = VColors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    leadingSubject?.let { appString(StringKeys.SCH_SUBJECT_TEACHER, "subject" to it) }
                        ?: appString(StringKeys.SCH_TEACHER),
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VBadge(
                        appString(StringKeys.SCH_COUNT_CLASSES, "count" to summary.classCount.toString()),
                        tone = VBadgeTone.Accent,
                    )
                    VBadge(
                        appString(StringKeys.SCH_COUNT_SUBJECTS, "count" to summary.subjectCount.toString()),
                        tone = VBadgeTone.Success,
                    )
                }
            }
        }
    }
}

private data class AssignmentKpi(
    val value: String,
    val label: String,
    val support: String,
    val icon: ImageVector,
    val tint: Color,
)

@Composable
private fun AssignmentKpis(overview: TeacherAssignmentOverviewDto) {
    val summary = overview.summary
    val data = listOf(
        AssignmentKpi(summary.classCount.toString(), appString(StringKeys.SCH_CLASSES_ASSIGNED), appString(StringKeys.SCH_ACTIVE_KPI), VIcons.School, VColors.violet),
        AssignmentKpi(summary.subjectCount.toString(), appString(StringKeys.SCH_SUBJECTS_ASSIGNED), appString(StringKeys.SCH_COVERED), VIcons.BookOpen, VColors.gold),
        AssignmentKpi(summary.studentCount.toString(), appString(StringKeys.SCH_TOTAL_STUDENTS), appString(StringKeys.SCH_TAUGHT), VIcons.Users, VColors.success),
        AssignmentKpi(summary.sectionCount.toString(), appString(StringKeys.SCH_SECTIONS_COVERED), appString(StringKeys.SCH_ACROSS_CLASSES), VIcons.Target, VColors.violet),
    )

    Section(appString(StringKeys.SCH_ASSIGNMENT_SUMMARY)) {
        LazyRow(
            contentPadding = PaddingValues(end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(data) { item -> AssignmentKpiCard(item) }
        }
    }
}

@Composable
private fun AssignmentKpiCard(item: AssignmentKpi) {
    VCard(modifier = Modifier.width(150.dp), padding = 16.dp) {
        IconChip(item.icon, item.tint, size = 36.dp, iconSize = 18.dp)
        Spacer(Modifier.height(12.dp))
        Text(item.value, style = VTypography.body.copy(fontSize = 22.sp, fontWeight = FontWeight.SemiBold), color = VColors.ink)
        Text(item.label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
        Text(item.support, style = VTypography.label, color = VColors.ink3)
    }
}

@Composable
private fun CurrentAssignments(
    assignments: List<TeacherClassAssignmentDto>,
    removingId: String?,
    onRequestRemove: (String) -> Unit,
) {
    Section(appString(StringKeys.SCH_CURRENT_ASSIGNMENTS)) {
        if (assignments.isEmpty()) {
            EmptyCard(VIcons.BookOpen, appString(StringKeys.SCH_NO_CLASSES_ASSIGNED))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                assignments.forEach { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        isRemoving = removingId == assignment.id,
                        onRemove = { onRequestRemove(assignment.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignmentCard(
    assignment: TeacherClassAssignmentDto,
    isRemoving: Boolean,
    onRemove: () -> Unit,
) {
    VCard(padding = 16.dp) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconChip(
                icon = VIcons.BookOpen,
                tint = VColors.sky,
                size = 40.dp,
                iconSize = 20.dp,
                background = VColors.skySoft,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    assignment.subject,
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    appString(
                        StringKeys.SCH_CLASS_SECTION_LABEL,
                        "className" to assignment.className,
                        "section" to assignment.section,
                    ),
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(VIcons.Users, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        appString(StringKeys.SCH_COUNT_STUDENTS, "count" to assignment.studentCount.toString()),
                        style = VTypography.label,
                        color = VColors.ink3,
                    )
                }
            }
            RemoveButton(isRemoving = isRemoving, onClick = onRemove)
        }
    }
}

@Composable
private fun RemoveButton(isRemoving: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, VColors.error, RoundedCornerShape(10.dp))
            .clickable(enabled = !isRemoving, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isRemoving) "…" else appString(StringKeys.SCH_REMOVE),
            style = VTypography.label.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.error,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddAssignmentCard(
    state: TeacherAssignmentUiState,
    onSelectSubject: (String?, String?) -> Unit,
    onToggleClass: (String) -> Unit,
    onToggleSection: (String) -> Unit,
    onResetDraft: () -> Unit,
    onSave: () -> Unit,
    onClearMessage: () -> Unit,
) {
    val options = state.options
    val draft = state.draft

    VCard(padding = 18.dp) {
        Text("ADD NEW ASSIGNMENT", style = VTypography.label.copy(fontWeight = FontWeight.Bold), color = VColors.ink2)
        Spacer(Modifier.height(14.dp))

        when {
            options == null -> Text(appString(StringKeys.SCH_LOADING_OPTIONS), style = VTypography.body, color = VColors.ink2)
            options.classes.isEmpty() && options.subjects.isEmpty() ->
                Text(appString(StringKeys.SCH_NO_CLASSES_SUBJECTS), style = VTypography.body, color = VColors.ink2)
            else -> {
                FieldLabel("Subject")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.subjects.forEach { subject ->
                        AssignmentChoiceChip(
                            text = subject.name,
                            active = draft.subjectId == subject.subjectId ||
                                (draft.subjectId == null && draft.subjectName == subject.name),
                            onClick = { onSelectSubject(subject.subjectId, subject.name) },
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                FieldLabel("Classes")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.classes.forEach { classOption ->
                        AssignmentChoiceChip(
                            text = classOption.code.ifBlank { classOption.name },
                            active = classOption.classId in draft.selectedClassIds,
                            onClick = { onToggleClass(classOption.classId) },
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                FieldLabel("Sections")
                val availableSections = options.classes
                    .filter { it.classId in draft.selectedClassIds }
                    .flatMap { it.sections }
                    .distinct()
                    .sorted()
                if (availableSections.isEmpty()) {
                    Text(appString(StringKeys.SCH_PICK_CLASSES_FIRST), style = VTypography.caption, color = VColors.ink3)
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableSections.forEach { section ->
                            AssignmentChoiceChip(
                                text = section,
                                active = section in draft.selectedSections,
                                onClick = { onToggleSection(section) },
                            )
                        }
                    }
                }

                val selectedClasses = options.classes.filter { it.classId in draft.selectedClassIds }
                val previewTargets = selectedClasses.flatMap { classOption ->
                    val sections = if (draft.selectedSections.isEmpty()) {
                        classOption.sections
                    } else {
                        classOption.sections.filter { it in draft.selectedSections }
                    }
                    sections.ifEmpty { draft.selectedSections.toList() }.map { section ->
                        "${classOption.code.ifBlank { classOption.name }}-$section"
                    }
                }

                if (!draft.subjectName.isNullOrBlank() && previewTargets.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    FieldLabel(appString(StringKeys.SCH_STEP_4_PREVIEW))
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(VColors.cream).padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(draft.subjectName.orEmpty(), style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            previewTargets.forEach { target -> VBadge(target, tone = VBadgeTone.Accent) }
                        }
                    }
                }

                state.saveError?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, style = VTypography.caption, color = VColors.error)
                }
                state.lastSaveMessage?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, style = VTypography.caption, color = VColors.success)
                }

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssignmentActionButton(
                        text = "Reset",
                        onClick = {
                            onResetDraft()
                            onClearMessage()
                        },
                        modifier = Modifier.weight(1f),
                        primary = false,
                        enabled = !state.isSaving,
                    )
                    AssignmentActionButton(
                        text = if (state.isSaving) "Saving…" else "Save\nAssignment",
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        primary = true,
                        enabled = !state.isSaving,
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignmentChoiceChip(text: String, active: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(999.dp)
    Text(
        text = text,
        modifier = Modifier
            .clip(shape)
            .background(if (active) AdminHomeTokens.Violet else Color(0xFFF4F3FA))
            .border(
                width = 1.dp,
                color = if (active) AdminHomeTokens.Violet else AdminHomeTokens.Line.copy(alpha = 0.55f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
        color = if (active) Color.White else VColors.ink2,
        maxLines = 1,
    )
}

@Composable
private fun AssignmentActionButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(if (primary) AdminHomeTokens.Violet else Color.White)
            .border(1.dp, if (primary) AdminHomeTokens.Violet else AdminHomeTokens.Line, shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (primary) Color.White else VColors.ink,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
        color = VColors.ink2,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun WorkloadInsights(insights: List<String>) {
    if (insights.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        insights.forEach { insight ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(VColors.violet.copy(alpha = 0.10f), VColors.violetSoft.copy(alpha = 0.72f)),
                        ),
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Box(Modifier.size(14.dp).clip(CircleShape).background(VColors.violet))
                    Text("WORKLOAD INSIGHT", style = VTypography.label.copy(fontWeight = FontWeight.Bold), color = VColors.violet)
                }
                Text(insight, style = VTypography.body, color = VColors.ink)
            }
        }
    }
}

@Composable
private fun SubjectDistribution(overview: TeacherAssignmentOverviewDto) {
    val distribution = overview.distribution
    if (distribution.isEmpty()) return

    val totalStudents = distribution.sumOf { it.studentCount }
    val totalClasses = distribution.sumOf { it.classCount }.coerceAtLeast(1)
    val colors = listOf(VColors.violet, VColors.sky, VColors.success, VColors.gold, VColors.coral)

    Section("SUBJECT DISTRIBUTION") {
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                distribution.forEachIndexed { index, item ->
                    val rawPercent = if (totalStudents > 0) {
                        item.studentCount * 100f / totalStudents
                    } else {
                        item.classCount * 100f / totalClasses
                    }
                    DistributionRow(
                        label = item.subject,
                        percent = rawPercent,
                        color = colors[index % colors.size],
                    )
                }
            }
        }
    }
}

@Composable
private fun DistributionRow(label: String, percent: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink2,
            modifier = Modifier.width(80.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier.weight(1f).height(28.dp).clip(RoundedCornerShape(10.dp)).background(VColors.violetSoft.copy(alpha = 0.45f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percent / 100f).coerceIn(0f, 1f))
                    .height(28.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    "${percent.toInt()}%",
                    style = VTypography.label.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun IconChip(
    icon: ImageVector,
    tint: Color,
    size: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    background: Color = tint.copy(alpha = 0.12f),
) {
    Box(
        modifier = Modifier.size(size).clip(RoundedCornerShape(10.dp)).background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        VSectionHeader(title = title)
        content()
    }
}

@Composable
private fun EmptyCard(icon: ImageVector, message: String) {
    VCard(padding = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconChip(icon, VColors.ink3, size = 34.dp, iconSize = 17.dp, background = VColors.cream)
            Text(message, style = VTypography.body, color = VColors.ink2, modifier = Modifier.weight(1f))
        }
    }
}
