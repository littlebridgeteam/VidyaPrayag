package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherCardDto
import com.littlebridge.enrollplus.feature.school.domain.model.StaffDto
import com.littlebridge.enrollplus.feature.school.domain.model.StudentDto
import com.littlebridge.enrollplus.feature.school.domain.model.TodayItemDto
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VStatusDot
import kotlin.math.roundToInt
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

// ──────────────────────────── Avatar variants ────────────────────────────

private enum class AvatarVariant {
    Violet, Coral, Gold, Mint, Sky
}

private val AvatarVariant.gradient: Pair<Color, Color>
    get() = when (this) {
        AvatarVariant.Violet -> Color(0xFF7B61E5) to Color(0xFF5B41D5)
        AvatarVariant.Coral -> Color(0xFFFF5C85) to Color(0xFFF82B60)
        AvatarVariant.Gold -> Color(0xFFFFD040) to Color(0xFFFCB400)
        AvatarVariant.Mint -> Color(0xFF4EE6A0) to Color(0xFF2DCE89)
        AvatarVariant.Sky -> Color(0xFF42CCFF) to Color(0xFF18BFFF)
    }

private fun avatarVariantFor(name: String): AvatarVariant {
    val hash = name.sumOf { it.code }
    return AvatarVariant.entries[(hash % AvatarVariant.entries.size + AvatarVariant.entries.size) % AvatarVariant.entries.size]
}

private fun initialsOf(name: String): String =
    name.trim().split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }

@Composable
private fun GradientAvatar(
    name: String,
    photoUrl: String?,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val variant = remember(name) { avatarVariantFor(name) }
    val (start, end) = variant.gradient
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(start, end))),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsOf(name),
            style = VTheme.type.body.copy(
                fontSize = (size.value * 0.35).sp,
                fontWeight = FontWeight.Bold,
                color = VTheme.colors.white,
            ),
        )
        if (!photoUrl.isNullOrBlank()) {
            val painter = rememberAsyncImagePainter(model = photoUrl)
            val state by painter.state.collectAsState()
            if (state is AsyncImagePainter.State.Success) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillSize().clip(CircleShape),
                )
            }
        }
    }
}

private fun Modifier.fillSize() = fillMaxSize()

// ──────────────────────────── Link banner ──────────────────────────────

@Composable
internal fun LinkRequestsBanner(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (count <= 0) return
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(VTheme.colors.violetSoft)
            .clickable { onClick() }
            .padding(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF7B61E5), Color(0xFF5B41D5)))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Check, contentDescription = "", tint = VTheme.colors.white, modifier = Modifier.size(18.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "$count pending link requests",
                    style = VTheme.type.label.copy(fontWeight = FontWeight.Bold),
                    color = VTheme.colors.violet,
                )
                Text(
                    "Tap to review parent→child approvals",
                    style = VTheme.type.caption,
                    color = VTheme.colors.violetInk.copy(alpha = 0.7f),
                )
            }
            Icon(VIcons.ChevronRight, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(18.dp))
        }
    }
}

// ──────────────────────────── Filter chips ─────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FilterChipRow(
    chips: List<FilterChipSpec>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            FilterChip(
                label = chip.label,
                options = chip.options,
                selected = chip.selected,
                onToggle = chip.onToggle,
            )
        }
    }
}

internal data class FilterChipSpec(
    val label: String,
    val options: List<String>,
    val selected: Set<String>,
    val onToggle: (String) -> Unit,
)

@Composable
private fun FilterChip(
    label: String,
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val active = selected.isNotEmpty()
    val interactionSource = remember { MutableInteractionSource() }

    Box {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .then(
                    if (active) {
                        Modifier.background(Brush.linearGradient(listOf(Color(0xFF7B61E5), Color(0xFF5B41D5))))
                    } else {
                        Modifier
                            .background(VTheme.colors.white)
                            .border(1.5.dp, VTheme.colors.line, RoundedCornerShape(50))
                    }
                )
                .clickable(interactionSource = interactionSource, indication = null) { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    label,
                    style = VTheme.type.label.copy(fontSize = 12.sp),
                    color = if (active) VTheme.colors.white else VTheme.colors.ink2,
                )
                Icon(
                    VIcons.ChevronDown,
                    contentDescription = "",
                    tint = if (active) VTheme.colors.white else VTheme.colors.ink3,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(VTheme.colors.white, RoundedCornerShape(14.dp)),
        ) {
            options.forEach { option ->
                val isSelected = option in selected
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            style = VTheme.type.bodySmall,
                            color = if (isSelected) VTheme.colors.violet else VTheme.colors.ink,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                    onClick = { onToggle(option); expanded = false },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isSelected) {
                                        Modifier.background(VTheme.colors.violet)
                                    } else {
                                        Modifier.border(1.5.dp, VTheme.colors.line, CircleShape)
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Icon(VIcons.Check, contentDescription = "", tint = VTheme.colors.white, modifier = Modifier.size(12.dp))
                            }
                        }
                    },
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
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        selected.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(VTheme.colors.violetSoft)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        item,
                        style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = VTheme.colors.violet,
                    )
                    Icon(
                        VIcons.Close,
                        contentDescription = "",
                        tint = VTheme.colors.violet,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { onRemove(item) },
                    )
                }
            }
        }
        Text(
            "Clear all",
            style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VTheme.colors.coral,
            modifier = Modifier.clickable { onClearAll() }
                .padding(horizontal = 4.dp, vertical = 4.dp),
        )
    }
}

// ──────────────────────────── Quick action tile ────────────────────────

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(VTheme.colors.surfaceTint)
            .clickable { onClick() }
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(17.dp))
            Text(label, style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink2)
        }
    }
}

// ──────────────────────────── Student card ─────────────────────────────

@Composable
internal fun StudentCard(
    student: StudentDto,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alerts = remember(student) { studentAlerts(student) }

    VCard(
        modifier = modifier.fillMaxWidth(),
        padding = 16.dp,
        shape = RoundedCornerShape(14.dp),
        onClick = onOpen,
    ) {
        Column {
            // Header
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GradientAvatar(
                    name = student.fullName,
                    photoUrl = student.profilePhotoUrl,
                    size = 48.dp,
                )
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            student.fullName,
                            style = VTheme.type.body.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                            color = VTheme.colors.ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(VTheme.colors.violetSoft)
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                        ) {
                            Text(
                                "Class ${student.className}-${student.section}",
                                style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = VTheme.colors.violet,
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Roll #${student.rollNumber}",
                            style = VTheme.type.caption,
                            color = VTheme.colors.ink3,
                        )
                        Text(
                            "Admission #${student.studentCode}",
                            style = VTheme.type.caption,
                            color = VTheme.colors.ink3,
                        )
                    }
                }
                Icon(
                    VIcons.More,
                    contentDescription = "",
                    tint = VTheme.colors.ink3,
                    modifier = Modifier.size(18.dp),
                )
            }

            // Parent info
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(VIcons.User, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        student.parentName?.takeIf { it.isNotBlank() } ?: "—",
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink2,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(VIcons.Phone, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        maskPhone(student.parentPhone),
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink2,
                    )
                }
            }

            DividerLine()

            // Alerts
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                alerts.forEach { (label, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        VStatusDot(color = color, size = 8.dp, ring = true)
                        Text(label, style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink2)
                    }
                }
            }

            // Micro viz
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MicroViz(
                    label = "ATTENDANCE",
                    value = student.attendancePercent.takeIf { it > 0 }?.let { "${it.roundToInt()}%" } ?: "—",
                    percent = student.attendancePercent,
                    tone = attendanceBarTone(student.attendancePercent),
                    modifier = Modifier.weight(1f),
                )
                MicroViz(
                    label = "HOMEWORK",
                    value = student.homeworkPercent.takeIf { it > 0 }?.let { "${it.roundToInt()}%" } ?: "—",
                    percent = student.homeworkPercent,
                    tone = homeworkBarTone(student.homeworkPercent),
                    modifier = Modifier.weight(1f),
                )
            }

            // Today
            if (student.todayItems.isNotEmpty()) {
                TodaySummary(student.todayItems)
            }

            // Actions
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                QuickActionButton(VIcons.User, "Profile", onOpen, Modifier.weight(1f))
                QuickActionButton(VIcons.Phone, "Call", onCall, Modifier.weight(1f))
                QuickActionButton(VIcons.Chat, "Message", onMessage, Modifier.weight(1f))
            }
        }
    }
}

// ──────────────────────────── Staff card ─────────────────────────────────

@Composable
internal fun StaffCard(
    staff: StaffDto,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alerts = remember(staff) { staffAlerts(staff) }

    VCard(
        modifier = modifier.fillMaxWidth(),
        padding = 16.dp,
        shape = RoundedCornerShape(14.dp),
        onClick = onOpen,
    ) {
        Column {
            // Header
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GradientAvatar(
                    name = staff.fullName,
                    photoUrl = staff.photoUrl,
                    size = 48.dp,
                )
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            staff.fullName,
                            style = VTheme.type.body.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                            color = VTheme.colors.ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(VTheme.colors.mintSoft)
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                        ) {
                            Text(
                                staff.department ?: "Staff",
                                style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = VTheme.colors.success,
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            staff.role,
                            style = VTheme.type.caption,
                            color = VTheme.colors.ink3,
                        )
                        Text(
                            staff.employeeId?.takeIf { it.isNotBlank() }?.let { "EMP-$it" } ?: "",
                            style = VTheme.type.caption,
                            color = VTheme.colors.ink3,
                        )
                    }
                }
                Icon(
                    VIcons.More,
                    contentDescription = "",
                    tint = VTheme.colors.ink3,
                    modifier = Modifier.size(18.dp),
                )
            }

            // Contact info
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(VIcons.Phone, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        staff.phone?.takeIf { it.isNotBlank() } ?: "—",
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink2,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(VIcons.Mail, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        staff.email?.takeIf { it.isNotBlank() } ?: "—",
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink2,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(VIcons.Clock, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        staff.shift?.takeIf { it.isNotBlank() } ?: "—",
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink2,
                    )
                }
            }

            DividerLine()

            // Alerts
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                alerts.forEach { (label, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        VStatusDot(color = color, size = 8.dp, ring = true)
                        Text(label, style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink2)
                    }
                }
            }

            // Today
            if (staff.todayItems.isNotEmpty()) {
                TodaySummary(staff.todayItems)
            }

            // Actions
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                QuickActionButton(VIcons.User, "Profile", onOpen, Modifier.weight(1f))
                QuickActionButton(VIcons.Phone, "Call", onCall, Modifier.weight(1f))
                QuickActionButton(VIcons.Chat, "Message", onMessage, Modifier.weight(1f))
            }
        }
    }
}

// ──────────────────────────── Teacher card (bento) ───────────────────────

@Composable
internal fun TeacherCard(
    teacher: TeacherCardDto,
    onViewProfile: () -> Unit,
    onAssignClass: () -> Unit,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val isActive = teacher.profile.status.equals("ACTIVE", ignoreCase = true)
    val subject = teacher.academicAssignment.subjects.firstOrNull() ?: "Mathematics"
    val theme = subjectTheme(subject)

    Box(modifier = modifier.fillMaxWidth()) {
        VCard(
            modifier = Modifier.fillMaxWidth(),
            padding = 14.dp,
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    GradientAvatar(
                        name = teacher.profile.name,
                        photoUrl = teacher.profile.avatarUrl,
                        size = 38.dp,
                    )
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                teacher.profile.name.ifBlank { "Unnamed" },
                                style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = VTheme.colors.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            if (teacher.profile.isClassTeacher) {
                                Spacer(Modifier.width(4.dp))
                                Icon(VIcons.Star, contentDescription = "", tint = VTheme.colors.gold, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(
                            listOfNotNull(
                                teacher.profile.role.takeIf { it.isNotBlank() },
                                teacher.profile.experience?.takeIf { it.isNotBlank() },
                            ).joinToString(" · "),
                            style = VTheme.type.caption,
                            color = VTheme.colors.ink3,
                        )
                    }
                    AvailabilityPill(teacher.availability)
                }

                DividerLine()

                // Bento grid
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        BentoCell(
                            label = "CLASSES",
                            value = teacher.workload.totalClasses.toString(),
                            sub = "active",
                            icon = VIcons.BookOpen,
                            iconBg = VTheme.colors.violetSoft,
                            iconTint = VTheme.colors.violet,
                            modifier = Modifier.weight(1f),
                        )
                        Box(Modifier.width(1.dp).height(60.dp).background(VTheme.colors.lineSoft))
                        BentoCell(
                            label = "STUDENTS",
                            value = teacher.workload.totalStudents.toString(),
                            sub = "enrolled",
                            icon = VIcons.Users,
                            iconBg = VTheme.colors.skySoft,
                            iconTint = VTheme.colors.sky,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(VTheme.colors.lineSoft))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val att = teacher.activity.attendancePercentage
                        val attTone = when {
                            att == null -> VBadgeTone.Neutral
                            att >= 95 -> VBadgeTone.Success
                            att >= 85 -> VBadgeTone.Warning
                            else -> VBadgeTone.Danger
                        }
                        BentoCell(
                            label = "ATTENDANCE",
                            value = att?.let { "$it%" } ?: "—",
                            sub = "avg rate",
                            icon = VIcons.Calendar,
                            iconBg = when (attTone) {
                                VBadgeTone.Success -> VTheme.colors.mintSoft
                                VBadgeTone.Warning -> VTheme.colors.goldSoft
                                VBadgeTone.Danger -> VTheme.colors.coralSoft
                                else -> VTheme.colors.surfaceTint
                            },
                            iconTint = when (attTone) {
                                VBadgeTone.Success -> VTheme.colors.success
                                VBadgeTone.Warning -> VTheme.colors.gold
                                VBadgeTone.Danger -> VTheme.colors.coral
                                else -> VTheme.colors.ink3
                            },
                            modifier = Modifier.weight(1f),
                        )
                        Box(Modifier.width(1.dp).height(60.dp).background(VTheme.colors.lineSoft))
                        BentoCell(
                            label = "WORKLOAD",
                            value = "${teacher.workload.workloadPercent}%",
                            sub = "Weekly capacity",
                            icon = null,
                            isWorkload = true,
                            workloadPercent = teacher.workload.workloadPercent,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(VTheme.colors.lineSoft))
                    // Subjects & grades
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                    ) {
                        Text(
                            "SUBJECTS & GRADES",
                            style = VTheme.type.caption.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = VTheme.colors.ink3,
                        )
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            teacher.academicAssignment.subjects.forEach { subj ->
                                val t = subjectTheme(subj)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(t.soft)
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                ) {
                                    Text(
                                        subj,
                                        style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                                        color = t.ink,
                                    )
                                }
                            }
                            teacher.academicAssignment.grades.forEach { grade ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(VTheme.colors.surfaceTint)
                                        .border(1.dp, VTheme.colors.lineSoft, RoundedCornerShape(50))
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                ) {
                                    Text(
                                        "Gr $grade",
                                        style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = VTheme.colors.ink2,
                                    )
                                }
                            }
                        }
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(VTheme.colors.lineSoft))
                    // Schedule + actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(VIcons.Calendar, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(13.dp))
                            Text(
                                teacher.workload.schedule.takeIf { it.isNotBlank() } ?: "No schedule",
                                style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold),
                                color = VTheme.colors.ink2,
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            VButton(
                                text = "Profile",
                                onClick = onViewProfile,
                                variant = VButtonVariant.Primary,
                                size = VButtonSize.Sm,
                                leading = { Icon(VIcons.User, contentDescription = "", modifier = Modifier.size(14.dp)) },
                            )
                            Box {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(VTheme.colors.surfaceTint)
                                        .clickable { menuOpen = true },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(VIcons.More, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(18.dp))
                                }
                                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                                    if (teacher.actions.canAssignClass) {
                                        DropdownMenuItem(
                                            text = { Text("Assign Classes") },
                                            onClick = { menuOpen = false; onAssignClass() },
                                            leadingIcon = { Icon(VIcons.Menu, contentDescription = "", modifier = Modifier.size(16.dp)) },
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text("Edit Details") },
                                        onClick = { menuOpen = false; onViewProfile() },
                                        leadingIcon = { Icon(VIcons.User, contentDescription = "", modifier = Modifier.size(16.dp)) },
                                    )
                                    if (teacher.actions.canDeactivate) {
                                        DropdownMenuItem(
                                            text = { Text("Deactivate", color = VTheme.colors.coral) },
                                            onClick = { menuOpen = false; onDeactivate() },
                                            leadingIcon = { Icon(VIcons.Close, contentDescription = "", tint = VTheme.colors.coral, modifier = Modifier.size(16.dp)) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Accent strip — drawn on top so it sits flush against the card top edge.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(Brush.linearGradient(listOf(theme.start, theme.end))),
        )
    }
}

@Composable
private fun BentoCell(
    label: String,
    value: String,
    sub: String,
    icon: ImageVector?,
    modifier: Modifier = Modifier,
    iconBg: Color = VTheme.colors.surfaceTint,
    iconTint: Color = VTheme.colors.ink3,
    isWorkload: Boolean = false,
    workloadPercent: Int = 0,
) {
    Column(
        modifier = modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label, style = VTheme.type.caption.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp), color = VTheme.colors.ink3)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (icon != null) {
                Box(
                    modifier = Modifier.size(24.dp).clip(RoundedCornerShape(7.dp)).background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = "", tint = iconTint, modifier = Modifier.size(14.dp))
                }
            }
            Column {
                Text(value, style = VTheme.type.h3.copy(fontSize = 22.sp, fontWeight = FontWeight.ExtraBold), color = VTheme.colors.ink)
                Text(sub, style = VTheme.type.caption, color = VTheme.colors.ink3)
            }
        }
        if (isWorkload) {
            Spacer(Modifier.height(4.dp))
            VProgressBar(
                value = workloadPercent.toFloat(),
                tone = workloadBarTone(workloadPercent.toFloat()),
                height = 6.dp,
            )
        }
    }
}

@Composable
private fun AvailabilityPill(availability: String) {
    val (label, dotColor, bgGradient) = when (availability.lowercase()) {
        "teaching" -> Triple("Teaching", VTheme.colors.success, listOf(VTheme.colors.mintSoft, Color(0xFFB8EBCC)))
        "break" -> Triple("Break", VTheme.colors.gold, listOf(VTheme.colors.goldSoft, Color(0xFFFFEAB8)))
        "meeting" -> Triple("In Meeting", VTheme.colors.sky, listOf(VTheme.colors.skySoft, Color(0xFFC8E4FF)))
        "leave" -> Triple("On Leave", VTheme.colors.coral, listOf(VTheme.colors.coralSoft, Color(0xFFFFD0DA)))
        else -> Triple("Break", VTheme.colors.gold, listOf(VTheme.colors.goldSoft, Color(0xFFFFEAB8)))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.linearGradient(bgGradient))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            VStatusDot(color = dotColor, size = 7.dp)
            Text(label, style = VTheme.type.caption.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Bold), color = dotColor)
        }
    }
}

// ──────────────────────────── Shared helpers ─────────────────────────────

@Composable
private fun DividerLine() {
    Box(Modifier.padding(vertical = 12.dp).fillMaxWidth().height(1.dp).background(VTheme.colors.lineSoft))
}

@Composable
private fun MicroViz(
    label: String,
    value: String,
    percent: Float,
    tone: VBadgeTone,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp),
                color = VTheme.colors.ink3,
            )
            Text(value, style = VTheme.type.label.copy(fontWeight = FontWeight.Bold), color = VTheme.colors.ink)
        }
        Spacer(Modifier.height(4.dp))
        VProgressBar(value = percent, tone = tone, height = 6.dp)
    }
}

@Composable
private fun TodaySummary(items: List<TodayItemDto>) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(VTheme.colors.surfaceTint)
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "TODAY",
                style = VTheme.type.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp),
                color = VTheme.colors.ink2,
            )
            Icon(VIcons.ChevronRight, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.height(4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VStatusDot(color = todayColor(item.color), size = 5.dp)
                    Text(item.text, style = VTheme.type.caption, color = VTheme.colors.ink2)
                }
            }
        }
    }
}

private fun maskPhone(phone: String?): String {
    if (phone.isNullOrBlank()) return "—"
    val digits = phone.filter { it.isDigit() }
    val prefix = phone.takeWhile { it == '+' || it == ' ' }
    if (digits.length <= 4) return phone
    val suffix = digits.takeLast(4)
    val masked = "X".repeat(digits.length - 4)
    return "${prefix}${masked}${suffix}"
}

private fun studentAlerts(student: StudentDto): List<Pair<String, Color>> {
    val list = mutableListOf<Pair<String, Color>>()
    when {
        !student.status.equals("active", true) -> list.add("Inactive" to VTheme.colors.ink3)
        student.isNewAdmission -> list.add("New Admission" to VTheme.colors.violet)
        student.attendancePercent in 0.1f..74.9f -> list.add("Low Attendance" to VTheme.colors.coral)
        student.attendancePercent >= 75f -> list.add("Healthy" to VTheme.colors.mint)
    }
    if (student.homeworkPercent < 80f && student.homeworkPercent > 0) list.add("Homework Due" to VTheme.colors.gold)
    if (student.feesPending) list.add("Fees Pending" to Color(0xFFFF8800))
    if (student.parentMeetingScheduled) list.add("Parent Meeting" to VTheme.colors.sky)
    return list
}

private fun staffAlerts(staff: StaffDto): List<Pair<String, Color>> {
    val list = mutableListOf<Pair<String, Color>>()
    val active = staff.status.equals("active", true)
    val checkedIn = staff.todayItems.any { it.text.contains("Checked in", ignoreCase = true) }
    when {
        active && checkedIn -> list.add("On Duty" to VTheme.colors.mint)
        active -> list.add("On Duty" to VTheme.colors.mint)
        else -> list.add("Off Duty" to VTheme.colors.coral)
    }
    if (staff.joinedYear?.isNotBlank() == true) {
        list.add("Since ${staff.joinedYear}" to VTheme.colors.violet)
    }
    return list
}

private fun todayColor(name: String): Color = when (name.lowercase()) {
    "green" -> VTheme.colors.mint
    "yellow" -> VTheme.colors.gold
    "red" -> VTheme.colors.coral
    "sky" -> VTheme.colors.sky
    else -> VTheme.colors.ink3
}

private fun attendanceBarTone(value: Float): VBadgeTone = when {
    value >= 85f -> VBadgeTone.Success
    value >= 75f -> VBadgeTone.Warning
    value > 0f -> VBadgeTone.Danger
    else -> VBadgeTone.Arctic
}

private fun homeworkBarTone(value: Float): VBadgeTone = when {
    value >= 80f -> VBadgeTone.Success
    value >= 50f -> VBadgeTone.Warning
    else -> VBadgeTone.Danger
}

private fun workloadBarTone(value: Float): VBadgeTone = when {
    value >= 80f -> VBadgeTone.Danger
    value >= 60f -> VBadgeTone.Warning
    value > 0f -> VBadgeTone.Success
    else -> VBadgeTone.Neutral
}

private data class SubjectTheme(val start: Color, val end: Color, val soft: Color, val ink: Color)

private fun subjectTheme(subject: String): SubjectTheme = when (subject.lowercase()) {
    "mathematics", "maths", "math" -> SubjectTheme(Color(0xFF7B61E5), Color(0xFF5B41D5), VTheme.colors.violetSoft, VTheme.colors.violet)
    "science" -> SubjectTheme(Color(0xFF4EE6A0), Color(0xFF2DCE89), VTheme.colors.mintSoft, VTheme.colors.success)
    "english" -> SubjectTheme(Color(0xFF42CCFF), Color(0xFF18BFFF), VTheme.colors.skySoft, Color(0xFF0B7AB8))
    "social studies", "social" -> SubjectTheme(Color(0xFFFF5C85), Color(0xFFF82B60), VTheme.colors.coralSoft, Color(0xFFD11A4A))
    "hindi" -> SubjectTheme(Color(0xFFFFD040), Color(0xFFFCB400), VTheme.colors.goldSoft, Color(0xFFB07500))
    "computer science", "cs", "computers" -> SubjectTheme(Color(0xFFA78BFA), Color(0xFF7C3AED), Color(0xFFEDE9FE), Color(0xFF5B21B6))
    else -> SubjectTheme(Color(0xFF7B61E5), Color(0xFF5B41D5), VTheme.colors.violetSoft, VTheme.colors.violet)
}
