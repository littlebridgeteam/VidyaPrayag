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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import kotlin.math.roundToInt

private enum class AvatarVariant {
    Violet, Coral, Gold, Mint, Sky,
}

private val AvatarVariant.gradient: Pair<Color, Color>
    get() = when (this) {
        AvatarVariant.Violet -> Color(0xFF8D7AE8) to VColors.violet
        AvatarVariant.Coral -> Color(0xFFFF7898) to VColors.coral
        AvatarVariant.Gold -> Color(0xFFFFDA69) to Color(0xFFE9A700)
        AvatarVariant.Mint -> Color(0xFF66E9AE) to Color(0xFF20B978)
        AvatarVariant.Sky -> Color(0xFF6BD7FF) to Color(0xFF169ED9)
    }

private fun avatarVariantFor(name: String): AvatarVariant {
    val hash = name.sumOf { it.code }
    val variants = AvatarVariant.entries
    return variants[(hash % variants.size + variants.size) % variants.size]
}

private fun initialsOf(name: String): String = name.trim()
    .split(" ")
    .filter { it.isNotBlank() }
    .take(2)
    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
    .joinToString("")
    .ifEmpty { "?" }

@Composable
private fun GradientAvatar(
    name: String,
    photoUrl: String?,
    size: Dp = 52.dp,
    modifier: Modifier = Modifier,
) {
    val (start, end) = remember(name) { avatarVariantFor(name).gradient }
    Box(
        modifier = modifier
            .shadow(8.dp, CircleShape, ambientColor = VColors.violet.copy(alpha = 0.2f))
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(start, end)))
            .border(2.dp, VColors.white, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsOf(name),
            color = VColors.white,
            fontSize = (size.value * 0.32f).sp,
            fontWeight = FontWeight.ExtraBold,
        )
        if (!photoUrl.isNullOrBlank()) {
            val painter = rememberAsyncImagePainter(model = photoUrl)
            val state by painter.state.collectAsStateV2()
            if (state is AsyncImagePainter.State.Success) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                )
            }
        }
    }
}

@Composable
internal fun LinkRequestsBanner(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (count <= 0) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFF0ECFF), Color(0xFFFFF7FA))))
            .border(1.dp, VColors.violet.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(VColors.violet),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Check, contentDescription = null, tint = VColors.white, modifier = Modifier.size(16.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                "$count pending link requests",
                color = VColors.violet,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
            )
            Text("Review parent and student links", color = VColors.ink3, fontSize = 10.5.sp)
        }
        Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
    }
}

internal data class FilterChipSpec(
    val label: String,
    val options: List<String>,
    val selected: Set<String>,
    val onToggle: (String) -> Unit,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FilterChipRow(
    chips: List<FilterChipSpec>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
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

@Composable
private fun FilterChip(
    label: String,
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val active = selected.isNotEmpty()
    val shape = RoundedCornerShape(50)
    Box {
        Row(
            modifier = Modifier
                .clip(shape)
                .then(
                    if (active) Modifier.background(VColors.violet)
                    else Modifier.background(VColors.white).border(1.dp, VColors.line, shape),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                if (active) "$label ${selected.size}" else label,
                color = if (active) VColors.white else VColors.ink2,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                VIcons.ChevronDown,
                contentDescription = null,
                tint = if (active) VColors.white else VColors.ink3,
                modifier = Modifier.size(14.dp),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(VColors.white, RoundedCornerShape(14.dp)),
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No options", color = VColors.ink3, fontSize = 12.sp) },
                    onClick = { expanded = false },
                    enabled = false,
                )
            } else {
                options.forEach { option ->
                    val chosen = option in selected
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                color = if (chosen) VColors.violet else VColors.ink,
                                fontWeight = if (chosen) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        onClick = { onToggle(option); expanded = false },
                        leadingIcon = {
                            Box(
                                Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .then(
                                        if (chosen) Modifier.background(VColors.violet)
                                        else Modifier.border(1.dp, VColors.line, CircleShape),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (chosen) {
                                    Icon(VIcons.Check, contentDescription = null, tint = VColors.white, modifier = Modifier.size(12.dp))
                                }
                            }
                        },
                    )
                }
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
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(VColors.violetSoft)
                    .clickable { onRemove(item) }
                    .padding(horizontal = 9.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(item, color = VColors.violet, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                Icon(VIcons.Close, contentDescription = "Remove $item", tint = VColors.violet, modifier = Modifier.size(12.dp))
            }
        }
        Text(
            "Clear all",
            color = VColors.coral,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onClearAll() }.padding(4.dp),
        )
    }
}

@Composable
private fun CircularAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    emphasized: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (emphasized) VColors.violet else VColors.creamDeep)
            .border(1.dp, if (emphasized) VColors.violet else VColors.line, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = if (emphasized) VColors.white else VColors.violet,
            modifier = Modifier.size(17.dp),
        )
    }
}

@Composable
private fun StatusBadge(status: String, highlight: Boolean = false) {
    val active = status.equals("active", true) || status.equals("available", true) || highlight
    val color = if (active) Color(0xFF1C9C64) else VColors.ink3
    val background = if (active) Color(0xFFE5F7EE) else VColors.creamDeep
    Row(
        Modifier.clip(RoundedCornerShape(50)).background(background).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(status.ifBlank { "Active" }.replaceFirstChar { it.uppercase() }, color = color, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Tag(text: String, color: Color = VColors.violet, background: Color = VColors.violetSoft) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(background).padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(text, color = color, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(value, color = VColors.ink, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(label.uppercase(), color = VColors.ink3, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp, maxLines = 1)
    }
}

@Composable
private fun CardDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line.copy(alpha = 0.72f)))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StudentCard(
    student: StudentDto,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(
        modifier = modifier.fillMaxWidth(),
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
        background = VColors.white,
        onClick = onOpen,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            GradientAvatar(student.fullName, student.profilePhotoUrl)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        student.fullName,
                        color = VColors.ink,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    StatusBadge(student.status, highlight = student.status.isBlank())
                }
                val classLabel = listOf(student.className, student.section.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" · ")
                Text(classLabel, color = VColors.ink2, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                Text("Roll ${student.rollNumber}", color = VColors.ink3, fontSize = 10.5.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (student.isNewAdmission) Tag("New admission", VColors.violet, VColors.violetSoft)
            student.parentName?.takeIf { it.isNotBlank() }?.let { Tag(it, VColors.ink2, VColors.creamDeep) }
            if (student.feesPending) Tag("Fees pending", Color(0xFFC26A00), Color(0xFFFFF1D6))
            if (student.parentMeetingScheduled) Tag("Parent meeting", Color(0xFF1674A3), Color(0xFFE4F5FC))
        }
        Spacer(Modifier.height(11.dp))
        CardDivider()
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Metric("Student ID", student.studentCode.ifBlank { "—" }, Modifier.weight(1f))
            Metric("Attendance", student.attendancePercent.takeIf { it > 0f }?.let { "${it.roundToInt()}%" } ?: "—", Modifier.weight(0.8f))
            Metric("Homework", student.homeworkPercent.takeIf { it > 0f }?.let { "${it.roundToInt()}%" } ?: "—", Modifier.weight(0.8f))
            CircularAction(VIcons.Phone, "Call parent", onCall)
            CircularAction(VIcons.Chat, "Message parent", onMessage, emphasized = true)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StaffCard(
    staff: StaffDto,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(
        modifier = modifier.fillMaxWidth(),
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
        background = VColors.white,
        onClick = onOpen,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            GradientAvatar(staff.fullName, staff.photoUrl)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        staff.fullName,
                        color = VColors.ink,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    StatusBadge(staff.status)
                }
                Text(staff.role.ifBlank { "Staff" }, color = VColors.ink2, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                staff.department?.takeIf { it.isNotBlank() }?.let { Text(it, color = VColors.ink3, fontSize = 10.5.sp) }
            }
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            staff.department?.takeIf { it.isNotBlank() }?.let { Tag(it) }
            staff.shift?.takeIf { it.isNotBlank() }?.let { Tag(it, VColors.ink2, VColors.creamDeep) }
            staff.joinedYear?.takeIf { it.isNotBlank() }?.let { Tag("Since $it", Color(0xFF1C7A56), Color(0xFFE5F7EE)) }
        }
        Spacer(Modifier.height(11.dp))
        CardDivider()
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Metric("Phone", staff.phone?.takeIf { it.isNotBlank() } ?: "—", Modifier.weight(1f))
            Metric("Email", staff.email?.takeIf { it.isNotBlank() } ?: "—", Modifier.weight(1.3f))
            CircularAction(VIcons.Phone, "Call staff member", onCall)
            CircularAction(VIcons.Chat, "Message staff member", onMessage, emphasized = true)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TeacherCard(
    teacher: TeacherCardDto,
    onViewProfile: () -> Unit,
    onAssignClass: () -> Unit,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val profile = teacher.profile
    val attendance = teacher.activity.attendancePercentage
    VCard(
        modifier = modifier.fillMaxWidth(),
        padding = 14.dp,
        shape = RoundedCornerShape(16.dp),
        background = VColors.white,
        onClick = onViewProfile,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            GradientAvatar(profile.name, profile.avatarUrl)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        profile.name.ifBlank { "Unnamed teacher" },
                        color = VColors.ink,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (profile.isClassTeacher) Icon(VIcons.Star, contentDescription = "Class teacher", tint = Color(0xFFE9A700), modifier = Modifier.size(14.dp))
                    StatusBadge(profile.status)
                }
                Text(profile.role.ifBlank { "Teacher" }, color = VColors.ink2, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                profile.experience?.takeIf { it.isNotBlank() }?.let { Text(it, color = VColors.ink3, fontSize = 10.5.sp) }
            }
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            teacher.academicAssignment.subjects.forEach { subject ->
                val theme = subjectTheme(subject)
                Tag(subject, theme.ink, theme.soft)
            }
            teacher.academicAssignment.grades.forEach { grade -> Tag("Grade $grade", VColors.ink2, VColors.creamDeep) }
        }
        Spacer(Modifier.height(11.dp))
        CardDivider()
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Metric("Students", teacher.workload.totalStudents.toString(), Modifier.weight(0.7f))
            Metric("Classes", teacher.workload.totalClasses.toString(), Modifier.weight(0.7f))
            Metric("Attendance", attendance?.let { "$it%" } ?: "—", Modifier.weight(0.9f))
            profile.rating?.let { Metric("Rating", it.toString(), Modifier.weight(0.7f)) }
            if (teacher.actions.canAssignClass) {
                CircularAction(VIcons.School, "Assign classes", onAssignClass, emphasized = true)
            }
            Box {
                CircularAction(VIcons.More, "More teacher actions", { menuOpen = true })
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier.background(VColors.white, RoundedCornerShape(14.dp)),
                ) {
                    DropdownMenuItem(
                        text = { Text("View profile") },
                        onClick = { menuOpen = false; onViewProfile() },
                        leadingIcon = { Icon(VIcons.User, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                    if (teacher.actions.canAssignClass) {
                        DropdownMenuItem(
                            text = { Text("Assign classes") },
                            onClick = { menuOpen = false; onAssignClass() },
                            leadingIcon = { Icon(VIcons.School, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        )
                    }
                    if (teacher.actions.canDeactivate) {
                        DropdownMenuItem(
                            text = { Text("Deactivate", color = VColors.coral) },
                            onClick = { menuOpen = false; onDeactivate() },
                            leadingIcon = { Icon(VIcons.Close, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(16.dp)) },
                        )
                    }
                }
            }
        }
    }
}

private data class SubjectTheme(val soft: Color, val ink: Color)

private fun subjectTheme(subject: String): SubjectTheme = when (subject.lowercase()) {
    "science" -> SubjectTheme(Color(0xFFE5F7EE), Color(0xFF1C7A56))
    "english" -> SubjectTheme(Color(0xFFE4F5FC), Color(0xFF1674A3))
    "social studies", "social" -> SubjectTheme(Color(0xFFFFE9EF), Color(0xFFC42752))
    "hindi" -> SubjectTheme(Color(0xFFFFF1D6), Color(0xFFA56300))
    "computer science", "cs", "computers" -> SubjectTheme(Color(0xFFEDE9FE), Color(0xFF5B21B6))
    else -> SubjectTheme(VColors.violetSoft, VColors.violet)
}
