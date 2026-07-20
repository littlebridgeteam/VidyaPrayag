package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherAchievementDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherActivityDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherAssignmentDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherProfileDto
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherProfileUiState
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherProfileViewModel
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
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonProfile
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherProfileScreenV2(
    teacherId: String,
    onBack: () -> Unit = {},
    onRemoved: () -> Unit = onBack,
    onOpenAssignments: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(teacherId) { viewModel.load(teacherId) }
    LaunchedEffect(state.removed) { if (state.removed) onRemoved() }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Teacher Profile", onBack = onBack)
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
            .padding(top = 4.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
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
            val profile = state.profile ?: return@VStateHost
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                TeacherProfileBody(profile, onOpenAssignments)
                DangerZone(
                    isRemoving = state.isRemoving,
                    removeError = state.removeError,
                    onRequestRemove = { confirmRemove = true },
                )
            }
        }
    }

    VConfirmDialog(
        visible = confirmRemove,
        title = appString(StringKeys.SCH_REMOVE_TEACHER),
        message = appString(
            StringKeys.SCH_REMOVE_TEACHER_MSG,
            "name" to (state.profile?.name ?: appString(StringKeys.SCH_THIS_TEACHER)),
        ),
        confirmLabel = appString(StringKeys.SCH_REMOVE),
        icon = VIcons.AlertTriangle,
        onConfirm = {
            confirmRemove = false
            onRemove()
        },
        onDismiss = { confirmRemove = false },
    )
}

@Composable
private fun TeacherProfileBody(profile: TeacherProfileDto, onOpenAssignments: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        ProfileHero(profile)
        QuickActions(onOpenAssignments)
        ProfileKpis(profile)
        PerformanceCard(profile)
        TeachingPortfolio(profile.assignments)
        InsightCards(profile.insights)
        ActivityTimeline(profile.recentActivities)
        AchievementCarousel(profile.achievements)
        ProfessionalDetails(profile)
    }
}

@Composable
private fun ProfileHero(profile: TeacherProfileDto) {
    val active = profile.status.equals("active", ignoreCase = true)
    val subject = profile.assignments.firstOrNull()?.subject?.takeIf { it.isNotBlank() }
    val subtitle = listOfNotNull(
        profile.designation?.takeIf { it.isNotBlank() },
        subject ?: profile.role.takeIf { it.isNotBlank() },
    ).joinToString(" · ")

    VCard(padding = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                VAvatar(name = profile.name, size = 72.dp, ring = true)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = profile.name,
                    style = VTypography.h2.copy(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold),
                    color = VColors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(subtitle, style = VTypography.caption, color = VColors.ink2)
                }
                Spacer(Modifier.height(2.dp))
                VBadge(
                    text = if (active) appString(StringKeys.SCH_ACTIVE) else appString(StringKeys.SCH_INACTIVE),
                    tone = if (active) VBadgeTone.Success else VBadgeTone.Neutral,
                    leadingIcon = if (active) VIcons.Check else null,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            HeroFact(
                icon = VIcons.TrendingUp,
                value = profile.experienceYears?.let { "$it yr${if (it == 1) "" else "s"}" } ?: "—",
                label = appString(StringKeys.SCH_EXPERIENCE),
                modifier = Modifier.weight(1f),
            )
            HeroFact(
                icon = VIcons.Calendar,
                value = profile.joinedOn?.takeIf { it.isNotBlank() } ?: "—",
                label = appString(StringKeys.SCH_JOINED),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HeroFact(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IconChip(icon = icon, tint = VColors.violet, size = 32.dp, iconSize = 16.dp)
        Column {
            Text(value, style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
            Text(label, style = VTypography.label, color = VColors.ink3)
        }
    }
}

@Composable
private fun QuickActions(onOpenAssignments: () -> Unit) {
    Section(title = appString(StringKeys.SCH_QUICK_ACTIONS)) {
        VCard(padding = 16.dp, onClick = onOpenAssignments) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                IconChip(VIcons.GraduationCap, VColors.violet, size = 42.dp, iconSize = 20.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        appString(StringKeys.SCH_ASSIGNMENTS),
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    Text(appString(StringKeys.SCH_MANAGE_CLASSES_SUBJECTS), style = VTypography.caption, color = VColors.ink2)
                }
                Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(20.dp))
            }
        }
    }
}

private data class ProfileKpi(
    val value: String,
    val label: String,
    val support: String,
    val icon: ImageVector,
    val tint: Color,
)

@Composable
private fun ProfileKpis(profile: TeacherProfileDto) {
    val data = listOf(
        ProfileKpi(profile.studentCount.toString(), appString(StringKeys.SCH_TOTAL_STUDENTS), appString(StringKeys.SCH_ACROSS_CLASSES), VIcons.Users, VColors.violet),
        ProfileKpi(profile.classCount.toString(), appString(StringKeys.SCH_CLASSES), appString(StringKeys.SCH_SECTIONS_TAUGHT), VIcons.School, VColors.success),
        ProfileKpi(profile.subjectCount.toString(), appString(StringKeys.SCH_SUBJECTS), appString(StringKeys.SCH_COVERED), VIcons.BookOpen, VColors.gold),
        ProfileKpi("${profile.attendancePercent.toInt()}%", appString(StringKeys.SCH_ATTENDANCE), appString(StringKeys.SCH_PERSONAL), VIcons.Check, VColors.success),
        ProfileKpi("${profile.assignmentCompletionPercent.toInt()}%", appString(StringKeys.SCH_ASSIGNMENTS), appString(StringKeys.SCH_COMPLETION), VIcons.Target, VColors.violet),
    )
    Section(title = appString(StringKeys.SCH_OVERVIEW)) {
        LazyRow(
            contentPadding = PaddingValues(end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(data) { item -> ProfileKpiCard(item) }
        }
    }
}

@Composable
private fun ProfileKpiCard(item: ProfileKpi) {
    VCard(modifier = Modifier.width(150.dp), padding = 16.dp) {
        IconChip(item.icon, item.tint, size = 36.dp, iconSize = 18.dp)
        Spacer(Modifier.height(12.dp))
        Text(item.value, style = VTypography.body.copy(fontSize = 22.sp, fontWeight = FontWeight.SemiBold), color = VColors.ink)
        Text(item.label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
        Text(item.support, style = VTypography.label, color = VColors.ink3)
    }
}

@Composable
private fun PerformanceCard(profile: TeacherProfileDto) {
    Section(title = appString(StringKeys.SCH_PERFORMANCE)) {
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PerformanceRow(appString(StringKeys.SCH_ATTENDANCE), profile.attendancePercent, Color(0xFFA8E6CF))
                PerformanceRow(appString(StringKeys.SCH_ASSIGNMENT_COMPLETION), profile.assignmentCompletionPercent, AdminHomeTokens.Violet)
                PerformanceRow(appString(StringKeys.SCH_PARENT_SATISFACTION), profile.parentSatisfactionPercent, Color(0xFFFFD4A3))
            }
        }
    }
}

@Composable
private fun PerformanceRow(label: String, value: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink2)
            Text("${value.toInt()}%", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFF4F3FA)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value.coerceIn(0f, 100f)) / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun TeachingPortfolio(assignments: List<TeacherAssignmentDto>) {
    Section(title = appString(StringKeys.SCH_TEACHING_PORTFOLIO)) {
        if (assignments.isEmpty()) {
            EmptyCard(VIcons.BookOpen, appString(StringKeys.SCH_NO_ASSIGNMENTS_YET))
        } else {
            LazyRow(
                contentPadding = PaddingValues(end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(assignments) { assignment -> PortfolioCard(assignment) }
            }
        }
    }
}

@Composable
private fun PortfolioCard(assignment: TeacherAssignmentDto) {
    VCard(modifier = Modifier.width(180.dp), padding = 16.dp) {
        IconChip(VIcons.BookOpen, VColors.sky, size = 36.dp, iconSize = 18.dp, background = VColors.skySoft)
        Spacer(Modifier.height(12.dp))
        Text(
            assignment.subject,
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            appString(StringKeys.SCH_CLASS_SECTION_LABEL, "className" to assignment.className, "section" to assignment.section),
            style = VTypography.caption,
            color = VColors.ink2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(VIcons.Users, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(13.dp))
            Text(appString(StringKeys.SCH_N_STUDENTS, "count" to assignment.studentCount.toString()), style = VTypography.label, color = VColors.ink3)
        }
    }
}

@Composable
private fun InsightCards(insights: List<String>) {
    if (insights.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        insights.forEach { insight -> InsightCard(appString(StringKeys.SCH_INSIGHTS), insight) }
    }
}

@Composable
private fun InsightCard(label: String, text: String) {
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
            Text(label, style = VTypography.label.copy(fontWeight = FontWeight.Bold), color = VColors.violet)
        }
        Text(text, style = VTypography.body, color = VColors.ink)
    }
}

@Composable
private fun ActivityTimeline(activities: List<TeacherActivityDto>) {
    Section(title = appString(StringKeys.SCH_RECENT_ACTIVITY)) {
        if (activities.isEmpty()) {
            EmptyCard(VIcons.Clock, appString(StringKeys.SCH_NO_RECENT_ACTIVITY))
        } else {
            VCard(padding = 18.dp) {
                activities.forEachIndexed { index, activity ->
                    ActivityRow(activity, index == activities.lastIndex)
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: TeacherActivityDto, isLast: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(activityColor(activity.type)))
            if (!isLast) Box(Modifier.width(1.dp).height(42.dp).background(VColors.line))
        }
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 10.dp)) {
            Text(activity.title, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
            Text(activity.createdAt, style = VTypography.label, color = VColors.ink3)
        }
    }
}

private fun activityColor(type: String): Color = when (type.lowercase()) {
    "homework" -> VColors.violet
    "exam_result" -> VColors.success
    "assessment" -> VColors.gold
    "announcement" -> VColors.coral
    else -> VColors.sky
}

@Composable
private fun AchievementCarousel(achievements: List<TeacherAchievementDto>) {
    Section(title = appString(StringKeys.SCH_ACHIEVEMENTS)) {
        if (achievements.isEmpty()) {
            EmptyCard(VIcons.Star, appString(StringKeys.SCH_NO_ACHIEVEMENTS))
        } else {
            LazyRow(
                contentPadding = PaddingValues(end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(achievements) { index, achievement -> AchievementCard(achievement, index) }
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: TeacherAchievementDto, index: Int) {
    val tint = listOf(VColors.gold, VColors.success, VColors.violet, VColors.coral)[index % 4]
    VCard(modifier = Modifier.width(200.dp), padding = 16.dp) {
        IconChip(VIcons.Star, tint, size = 40.dp, iconSize = 20.dp)
        Spacer(Modifier.height(10.dp))
        Text(
            achievement.title,
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(achievement.description, style = VTypography.caption, color = VColors.ink3, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ProfessionalDetails(profile: TeacherProfileDto) {
    Section(title = appString(StringKeys.SCH_PROFESSIONAL_DETAILS)) {
        VCard(padding = 14.dp) {
            DetailRow(VIcons.BookOpen, "Designation", profile.designation?.takeIf { it.isNotBlank() } ?: "—")
            DetailDivider()
            DetailRow(VIcons.GraduationCap, "Role", profile.role.takeIf { it.isNotBlank() } ?: "—")
            DetailDivider()
            DetailRow(VIcons.Mail, appString(StringKeys.SCH_EMAIL), profile.email?.takeIf { it.isNotBlank() } ?: "—")
            DetailDivider()
            DetailRow(VIcons.Phone, appString(StringKeys.SCH_PHONE), profile.phone?.takeIf { it.isNotBlank() } ?: "—")
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconChip(icon, VColors.ink2, size = 34.dp, iconSize = 16.dp, background = VColors.cream)
        Text(label, style = VTypography.caption, color = VColors.ink3, modifier = Modifier.weight(1f))
        Text(value, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DetailDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line.copy(alpha = 0.55f)))
}

@Composable
private fun DangerZone(isRemoving: Boolean, removeError: String?, onRequestRemove: () -> Unit) {
    VCard(
        modifier = Modifier.border(1.dp, VColors.errorSoft, RoundedCornerShape(16.dp)),
        padding = 16.dp,
        background = VColors.white,
    ) {
        Text("DANGER ZONE", style = VTypography.label.copy(fontWeight = FontWeight.Bold), color = VColors.error)
        Spacer(Modifier.height(8.dp))
        Text(
            "Removing this teacher will unassign all classes and revoke access. This cannot be undone.",
            style = VTypography.caption,
            color = VColors.ink2,
        )
        removeError?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = VTypography.caption, color = VColors.error)
        }
        Spacer(Modifier.height(12.dp))
        val shape = RoundedCornerShape(12.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(shape)
                .border(1.dp, VColors.error, shape)
                .clickable(enabled = !isRemoving, onClick = onRequestRemove),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = VColors.error, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                if (isRemoving) "Removing…" else "Remove Teacher",
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.error,
            )
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
