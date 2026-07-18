package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherAchievementDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherActivityDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherAssignmentDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherProfileDto
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherProfileUiState
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonProfile
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * RA-45 / RA-PP: TeacherProfileScreenV2 — a single teacher's detail for the
 * admin, redesigned as a modern dashboard/profile experience.
 *
 * Structure (top → bottom): Hero banner · KPI carousel · Performance overview ·
 * Teaching portfolio carousel · Insights · Recent activity timeline ·
 * Achievements carousel · Professional details · Danger zone.
 *
 * [teacherId] is passed by the caller and loaded via [TeacherProfileViewModel.load]
 * in a LaunchedEffect. Three states via [VStateHost] (LAW 3). Portal overlay —
 * back returns to People.
 */
@Composable
fun TeacherProfileScreenV2(
    teacherId: String,
    onBack: () -> Unit = {},
    // RA-S17: called after a successful soft-delete so the host can pop back to
    // People and refresh the roster.
    onRemoved: () -> Unit = onBack,
    // RA-TAM — Quick Action → reusable Teacher Assignment Management module.
    onOpenAssignments: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(teacherId) { viewModel.load(teacherId) }
    // RA-S17: when the VM confirms removal, leave the profile.
    LaunchedEffect(state.removed) { if (state.removed) onRemoved() }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = appString(StringKeys.SCH_TEACHER), onBack = onBack)
        TeacherProfileContent(
            state = state,
            onRetry = viewModel::retry,
            onRemove = { viewModel.remove(teacherId) },
            onOpenAssignments = onOpenAssignments,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun TeacherProfileContent(
    state: TeacherProfileUiState,
    onRetry: () -> Unit,
    onRemove: () -> Unit,
    onOpenAssignments: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmRemove by remember { mutableStateOf(false) }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.profile == null && !state.isLoading && state.error == null,
            emptyTitle = appString(StringKeys.SCH_NO_PROFILE),
            emptyBody = appString(StringKeys.SCH_NO_PROFILE_DESC),
            emptyIcon = VIcons.User,
            onRetry = onRetry,
            skeleton = { SkeletonProfile() },
        ) {
            val p = state.profile ?: return@VStateHost
            TeacherProfileBody(p, onOpenAssignments = onOpenAssignments)

            // ── 9. Danger zone — destructive action separated at the bottom. ──
            Spacer(Modifier.height(8.dp))
            DangerZone(
                isRemoving = state.isRemoving,
                removeError = state.removeError,
                onRequestRemove = { confirmRemove = true },
            )
        }
    }

    VConfirmDialog(
        visible = confirmRemove,
        title = appString(StringKeys.SCH_REMOVE_TEACHER),
        message = appString(StringKeys.SCH_REMOVE_TEACHER_MSG, "name" to (state.profile?.name ?: appString(StringKeys.SCH_THIS_TEACHER))),
        confirmLabel = appString(StringKeys.SCH_REMOVE),
        icon = VIcons.AlertTriangle,
        onConfirm = { confirmRemove = false; onRemove() },
        onDismiss = { confirmRemove = false },
    )
}

@Composable
private fun TeacherProfileBody(p: TeacherProfileDto, onOpenAssignments: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        HeroBanner(p)                 // 1. Hero profile banner
        QuickActions(onOpenAssignments = onOpenAssignments) // 1b. Quick actions
        KpiCarousel(p)                // 2. KPI carousel
        PerformanceOverview(p)        // 3. Performance overview
        TeachingPortfolio(p.assignments) // 4. Teaching portfolio carousel
        InsightsSection(p.insights)   // 5. AI / teacher insights
        ActivityTimeline(p.recentActivities) // 6. Recent activity timeline
        AchievementsCarousel(p.achievements) // 7. Achievements carousel
        ProfessionalDetails(p)        // 8. Professional details
    }
}

// ───────────────────────── 1. Hero profile banner ─────────────────────────

@Composable
private fun HeroBanner(p: TeacherProfileDto) {
    val active = p.status.equals("active", ignoreCase = true)
    VCard(padding = 20.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VAvatar(name = p.name, size = 76.dp, ring = true)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(p.name, style = VTypography.h2, color = VColors.ink)
                val sub = listOfNotNull(
                    p.designation?.takeIf { it.isNotBlank() },
                    p.role.replaceFirstChar { it.uppercase() }.takeIf { it.isNotBlank() },
                ).joinToString(" · ")
                if (sub.isNotBlank()) {
                    Text(sub, style = VTypography.caption, color = VColors.ink2)
                }
                VBadge(
                    text = if (active) appString(StringKeys.SCH_ACTIVE) else appString(StringKeys.SCH_INACTIVE),
                    tone = if (active) VBadgeTone.Success else VBadgeTone.Neutral,
                    leadingIcon = VIcons.Check,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            HeroFact(
                icon = VIcons.TrendingUp,
                label = appString(StringKeys.SCH_EXPERIENCE),
                value = p.experienceYears?.let { "$it yr${if (it == 1) "" else "s"}" } ?: "—",
            )
            HeroFact(
                icon = VIcons.Calendar,
                label = appString(StringKeys.SCH_JOINED),
                value = p.joinedOn?.takeIf { it.isNotBlank() } ?: "—",
            )
        }
    }
}

@Composable
private fun HeroFact(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(VColors.violet.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(value, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
            Text(label, style = VTypography.label, color = VColors.ink3)
        }
    }
}

// ─────────────────────────── 1b. Quick actions ────────────────────────────

/**
 * RA-TAM: Quick Actions row on the Teacher Profile dashboard. The "Assignments"
 * action is one of the three entry points into the reusable Teacher Assignment
 * Management module (single source of truth).
 */
@Composable
private fun QuickActions(onOpenAssignments: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_QUICK_ACTIONS))
        VCard(padding = 16.dp, onClick = onOpenAssignments) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(42.dp).clip(RoundedCornerShape(12.dp))
                        .background(VColors.violet.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.GraduationCap, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(20.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(appString(StringKeys.SCH_ASSIGNMENTS), style = VTypography.h3, color = VColors.ink)
                    Text(
                        appString(StringKeys.SCH_MANAGE_CLASSES_SUBJECTS),
                        style = VTypography.caption, color = VColors.ink2,
                    )
                }
                Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ──────────────────────────── 2. KPI carousel ─────────────────────────────

@Composable
private fun KpiCarousel(p: TeacherProfileDto) {
    val kpis = listOf(
        KpiCardData(appString(StringKeys.SCH_TOTAL_STUDENTS), p.studentCount.toString(), appString(StringKeys.SCH_ACROSS_CLASSES), VIcons.Users, VBadgeTone.Arctic),
        KpiCardData(appString(StringKeys.SCH_CLASSES), p.classCount.toString(), appString(StringKeys.SCH_SECTIONS_TAUGHT), VIcons.School, VBadgeTone.Success),
        KpiCardData(appString(StringKeys.SCH_SUBJECTS), p.subjectCount.toString(), appString(StringKeys.SCH_COVERED), VIcons.BookOpen, VBadgeTone.Warning),
        KpiCardData(appString(StringKeys.SCH_ATTENDANCE), "${p.attendancePercent.toInt()}%", appString(StringKeys.SCH_PERSONAL), VIcons.Check, VBadgeTone.Success),
        KpiCardData(appString(StringKeys.SCH_ASSIGNMENTS), "${p.assignmentCompletionPercent.toInt()}%", appString(StringKeys.SCH_COMPLETION), VIcons.Target, VBadgeTone.Arctic),
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_OVERVIEW))
        LazyRow(
            contentPadding = PaddingValues(end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(kpis) { kpi -> KpiCard(kpi) }
        }
    }
}

 data class KpiCardData(
    val label: String,
    val value: String,
    val support: String,
    val icon: ImageVector,
    val tone: VBadgeTone,
)

@Composable
private fun KpiCard(data: KpiCardData) {
    val tint = when (data.tone) {
        VBadgeTone.Accent -> VColors.violet
        VBadgeTone.Arctic -> VColors.violet
        VBadgeTone.Success -> VColors.success
        VBadgeTone.Warning -> VColors.gold
        VBadgeTone.Danger -> VColors.error
        VBadgeTone.Neutral -> VColors.ink3
    }
    VCard(modifier = Modifier.width(150.dp), padding = 16.dp) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(data.icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(data.value, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp), color = VColors.ink)
        Text(data.label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
        Text(data.support, style = VTypography.label, color = VColors.ink3)
    }
}

// ───────────────────────── 3. Performance overview ────────────────────────

@Composable
private fun PerformanceOverview(p: TeacherProfileDto) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_PERFORMANCE))
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricBar(appString(StringKeys.SCH_ATTENDANCE), p.attendancePercent, VBadgeTone.Success)
                MetricBar(appString(StringKeys.SCH_ASSIGNMENT_COMPLETION), p.assignmentCompletionPercent, VBadgeTone.Arctic)
                MetricBar(appString(StringKeys.SCH_PARENT_SATISFACTION), p.parentSatisfactionPercent, VBadgeTone.Warning)
            }
        }
    }
}

@Composable
private fun MetricBar(label: String, value: Float, tone: VBadgeTone) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
            Text("${value.toInt()}%", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
        }
        VProgressBar(value = value, tone = tone, height = 8.dp)
    }
}

// ────────────────────── 4. Teaching portfolio carousel ────────────────────

@Composable
private fun TeachingPortfolio(assignments: List<TeacherAssignmentDto>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_TEACHING_PORTFOLIO))
        if (assignments.isEmpty()) {
            EmptyCard(VIcons.BookOpen, appString(StringKeys.SCH_NO_ASSIGNMENTS_YET))
        } else {
            LazyRow(
                contentPadding = PaddingValues(end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(assignments) { a -> PortfolioCard(a) }
            }
        }
    }
}

@Composable
private fun PortfolioCard(a: TeacherAssignmentDto) {
    VCard(modifier = Modifier.width(180.dp), padding = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(VColors.violet.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.BookOpen, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
            }
            VBadge(text = appString(StringKeys.SCH_SEC, "section" to a.section), tone = VBadgeTone.Neutral)
        }
        Spacer(Modifier.height(12.dp))
        Text(a.subject, style = VTypography.h3, color = VColors.ink)
        Text(a.className, style = VTypography.caption, color = VColors.ink2)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(VIcons.Users, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
            Text(appString(StringKeys.SCH_N_STUDENTS, "count" to a.studentCount.toString()), style = VTypography.caption, color = VColors.ink3)
        }
    }
}

// ──────────────────────── 5. AI / teacher insights ────────────────────────

@Composable
private fun InsightsSection(insights: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_INSIGHTS))
        if (insights.isEmpty()) {
            EmptyCard(VIcons.Sparkles, appString(StringKeys.SCH_NO_INSIGHTS_YET))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                insights.forEach { insight ->
                    VCard(padding = 14.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(VColors.violetSoft),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(17.dp))
                            }
                            Text(insight, style = VTypography.body, color = VColors.ink, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────── 6. Recent activity timeline ──────────────────────

@Composable
private fun ActivityTimeline(activities: List<TeacherActivityDto>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_RECENT_ACTIVITY))
        if (activities.isEmpty()) {
            EmptyCard(VIcons.Clock, appString(StringKeys.SCH_NO_RECENT_ACTIVITY))
        } else {
            VCard(padding = 18.dp) {
                Column {
                    activities.forEachIndexed { index, activity ->
                        TimelineRow(activity, isLast = index == activities.lastIndex)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(activity: TeacherActivityDto, isLast: Boolean) {
    val tone = activityTone(activity.type)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // marker + connector line
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(tone))
            if (!isLast) {
                Box(Modifier.width(2.dp).height(34.dp).background(VColors.line))
            }
        }
        Column(Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(activity.title, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
            Text(formatActivityMeta(activity), style = VTypography.label, color = VColors.ink3)
        }
    }
}

@Composable
private fun activityTone(type: String) = when (type.lowercase()) {
    "homework" -> VColors.violet
    "exam_result" -> VColors.success
    "assessment" -> VColors.gold
    "announcement" -> VColors.error
    else -> VColors.ink3
}

private fun formatActivityMeta(activity: TeacherActivityDto): String {
    val label = activity.type.replace('_', ' ').replaceFirstChar { it.uppercase() }
    val date = activity.createdAt.take(10)
    return if (date.isNotBlank()) "$label · $date" else label
}

// ───────────────────────── 7. Achievements carousel ───────────────────────

@Composable
private fun AchievementsCarousel(achievements: List<TeacherAchievementDto>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_ACHIEVEMENTS))
        if (achievements.isEmpty()) {
            EmptyCard(VIcons.Star, appString(StringKeys.SCH_NO_ACHIEVEMENTS))
        } else {
            LazyRow(
                contentPadding = PaddingValues(end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(achievements) { index, item -> AchievementCard(item, index) }
            }
        }
    }
}

@Composable
private fun AchievementCard(item: TeacherAchievementDto, index: Int) {
    val tones = listOf(VColors.gold, VColors.success, VColors.violet, VColors.error)
    val tint = tones[index % tones.size]
    VCard(modifier = Modifier.width(200.dp), padding = 16.dp) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Star, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(item.title, style = VTypography.h3, color = VColors.ink)
        Spacer(Modifier.height(4.dp))
        Text(item.description, style = VTypography.caption, color = VColors.ink2, maxLines = 3)
    }
}

// ──────────────────────── 8. Professional details ─────────────────────────

@Composable
private fun ProfessionalDetails(p: TeacherProfileDto) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_PROFESSIONAL_DETAILS))
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailRow(VIcons.Mail, appString(StringKeys.SCH_EMAIL), p.email?.takeIf { it.isNotBlank() } ?: "—")
                DetailRow(VIcons.Phone, appString(StringKeys.SCH_PHONE), p.phone?.takeIf { it.isNotBlank() } ?: "—")
                DetailRow(VIcons.Calendar, appString(StringKeys.SCH_JOINED_DATE), p.joinedOn?.takeIf { it.isNotBlank() } ?: "—")
                DetailRow(
                    VIcons.TrendingUp,
                    appString(StringKeys.SCH_EXPERIENCE),
                    p.experienceYears?.let { appString(StringKeys.SCH_N_YEARS, "count" to it.toString()) } ?: "—",
                )
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(VColors.cream),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(16.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = VTypography.label, color = VColors.ink3)
            Text(value, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
        }
    }
}

// ─────────────────────────────── 9. Danger zone ───────────────────────────

@Composable
private fun DangerZone(
    isRemoving: Boolean,
    removeError: String?,
    onRequestRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_DANGER_ZONE))
        VCard(padding = 18.dp, border = true) {
            Text(appString(StringKeys.SCH_REMOVE_TEACHER), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.error)
            Spacer(Modifier.height(4.dp))
            Text(
                appString(StringKeys.SCH_REMOVE_TEACHER_DANGER),
                style = VTypography.caption, color = VColors.ink2,
            )
            Spacer(Modifier.height(14.dp))
            removeError?.let { err ->
                Text(err, style = VTypography.caption, color = VColors.error)
                Spacer(Modifier.height(8.dp))
            }
            VButton(
                text = appString(StringKeys.SCH_REMOVE_FROM_SCHOOL),
                onClick = onRequestRemove,
                variant = VButtonVariant.Destructive,
                full = true,
                enabled = !isRemoving,
                loading = isRemoving,
                leading = { Icon(VIcons.Close, contentDescription = null, modifier = Modifier.size(16.dp)) },
            )
        }
    }
}

// ────────────────────────────── shared bits ───────────────────────────────

@Composable
private fun EmptyCard(icon: ImageVector, message: String) {
    VCard(padding = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(VColors.cream),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(17.dp))
            }
            Text(message, style = VTypography.body, color = VColors.ink2)
        }
    }
}
