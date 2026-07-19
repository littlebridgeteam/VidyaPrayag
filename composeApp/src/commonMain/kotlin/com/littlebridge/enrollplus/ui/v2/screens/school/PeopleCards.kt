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
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import kotlin.math.roundToInt

private val PeopleHairline = Color(0x0F26234D)
private val SuccessInk = Color(0xFF1F7A4D)
private val SuccessSoft = Color(0x3FA8E6CF)
private val WarningInk = Color(0xFFB3651A)
private val WarningSoft = Color(0x3FFFD4A3)
private val SkyInk = Color(0xFF4A6BD8)
private val SkySoft = Color(0x1F6C8DF5)
private val TealInk = Color(0xFF006A60)
private val TealSoft = Color(0x1F3CB9A9)
private val PeachInk = Color(0xFFC04A20)
private val PeachSoft = Color(0x1FFF8A65)

private enum class AvatarVariant { Violet, Peach, Sky, Mint, Gold }

private val AvatarVariant.gradient: Pair<Color, Color>
    get() = when (this) {
        AvatarVariant.Violet -> Color(0xFF7B6BE0) to Color(0xFF5B41D5)
        AvatarVariant.Peach -> Color(0xFFFFB088) to Color(0xFFFF8A65)
        AvatarVariant.Sky -> Color(0xFF8BA8F8) to Color(0xFF6C8DF5)
        AvatarVariant.Mint -> Color(0xFF5DD9C8) to Color(0xFF3CB9A9)
        AvatarVariant.Gold -> Color(0xFFFFD040) to Color(0xFFFCB400)
    }

private fun avatarVariantFor(key: String): AvatarVariant {
    val variants = AvatarVariant.entries
    val hash = key.sumOf { it.code }
    return variants[(hash % variants.size + variants.size) % variants.size]
}

private fun initialsOf(name: String): String = name.trim().split(" ")
    .filter(String::isNotBlank)
    .take(2)
    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
    .joinToString("")
    .ifEmpty { "?" }

@Composable
private fun GradientAvatar(name: String, photoUrl: String?, size: Dp = 48.dp) {
    val (start, end) = remember(name) { avatarVariantFor(name).gradient }
    Box(
        modifier = Modifier
            .shadow(7.dp, CircleShape, ambientColor = Color(0x1A26234D))
            .size(size)
            .border(2.dp, VColors.white, CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(start, end))),
        contentAlignment = Alignment.Center,
    ) {
        Text(initialsOf(name), color = VColors.white, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        if (!photoUrl.isNullOrBlank()) {
            val painter = rememberAsyncImagePainter(photoUrl)
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
internal fun LinkRequestsBanner(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (count <= 0) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFF4F3FA), Color(0xFFEAE6FA))))
            .border(1.dp, VColors.violet.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(36.dp).shadow(5.dp, RoundedCornerShape(10.dp)).clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF7B6BE0), VColors.violet))),
            contentAlignment = Alignment.Center,
        ) { Icon(VIcons.Check, null, tint = VColors.white, modifier = Modifier.size(18.dp)) }
        Column(Modifier.weight(1f)) {
            Text("$count pending link requests", color = Color(0xFF4A30C4), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Tap to review parent–child approvals", color = Color(0xFF7B6BE0), fontSize = 11.sp)
        }
        Icon(VIcons.ChevronRight, null, tint = VColors.violet, modifier = Modifier.size(18.dp))
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
internal fun FilterChipRow(chips: List<FilterChipSpec>, modifier: Modifier = Modifier) {
    FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chips.forEach { FilterChip(it) }
    }
}

@Composable
private fun FilterChip(spec: FilterChipSpec) {
    var expanded by remember { mutableStateOf(false) }
    val active = spec.selected.isNotEmpty()
    val shape = RoundedCornerShape(50)
    Box {
        Row(
            modifier = Modifier
                .shadow(2.dp, shape, ambientColor = Color(0x0D26234D))
                .clip(shape)
                .then(if (active) Modifier.background(VColors.violet) else Modifier.background(VColors.white).border(1.dp, PeopleHairline, shape))
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { expanded = true }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(spec.label, color = if (active) VColors.white else VColors.ink2, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Box(
                Modifier.clip(CircleShape).background(if (active) Color.White.copy(alpha = 0.22f) else Color(0x1426234D))
                    .padding(horizontal = 6.dp, vertical = 1.dp),
            ) {
                Text(
                    (if (active) spec.selected.size else spec.options.size).toString(),
                    color = if (active) VColors.white else VColors.ink2,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        DropdownMenu(expanded, { expanded = false }, modifier = Modifier.background(VColors.white, RoundedCornerShape(14.dp))) {
            if (spec.options.isEmpty()) {
                DropdownMenuItem(text = { Text("No options", color = VColors.ink3) }, onClick = {}, enabled = false)
            } else spec.options.forEach { option ->
                val selected = option in spec.selected
                DropdownMenuItem(
                    text = { Text(option, color = if (selected) VColors.violet else VColors.ink, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    onClick = { spec.onToggle(option); expanded = false },
                    leadingIcon = { if (selected) Icon(VIcons.Check, null, tint = VColors.violet, modifier = Modifier.size(16.dp)) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ActiveFilterChips(selected: Set<String>, onRemove: (String) -> Unit, onClearAll: () -> Unit, modifier: Modifier = Modifier) {
    if (selected.isEmpty()) return
    FlowRow(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        selected.forEach { value ->
            Row(
                Modifier.clip(CircleShape).background(VColors.violetSoft).clickable { onRemove(value) }.padding(horizontal = 9.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(value, color = VColors.violet, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Icon(VIcons.Close, "Remove $value", tint = VColors.violet, modifier = Modifier.size(11.dp))
            }
        }
        Text("Clear all", color = VColors.coral, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onClearAll).padding(4.dp))
    }
}

@Composable
private fun CircularAction(icon: ImageVector, description: String, onClick: () -> Unit, enabled: Boolean = true) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(VColors.white)
            .border(1.dp, PeopleHairline, CircleShape)
            .then(if (enabled) Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = VColors.ink2.copy(alpha = if (enabled) 1f else 0.3f), modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun StatusBadge(status: String) {
    val active = status.equals("active", true) || status.equals("available", true)
    val ink = if (active) SuccessInk else VColors.ink3
    val fill = if (active) SuccessSoft else Color(0xFFF4F3FA)
    Row(
        Modifier.clip(CircleShape).background(fill).padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        if (active) Icon(VIcons.Check, null, tint = ink, modifier = Modifier.size(9.dp))
        Text(status.ifBlank { "Active" }.lowercase().replaceFirstChar(Char::uppercase), color = ink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Badge(text: String, ink: Color = Color(0xFF4A30C4), fill: Color = Color(0xFFF4F3FA)) {
    Box(Modifier.clip(CircleShape).background(fill).padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(text, color = ink, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun SubjectTag(text: String, index: Int) {
    val themes = listOf(
        Color(0xFF4A30C4) to Color(0xFFF4F3FA),
        TealInk to TealSoft,
        SkyInk to SkySoft,
        PeachInk to PeachSoft,
    )
    val (ink, fill) = themes[index % themes.size]
    Badge(text, ink, fill)
}

@Composable
private fun MiniMetric(icon: ImageVector, value: String, suffix: String = "", tint: Color = VColors.ink3) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(13.dp))
        Text(value, color = if (tint == VColors.ink3) VColors.ink else tint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        if (suffix.isNotBlank()) Text(suffix, color = tint, fontSize = 10.sp)
    }
}

@Composable
private fun PersonCard(content: @Composable () -> Unit) {
    VCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 16.dp,
        shape = RoundedCornerShape(16.dp),
        background = VColors.white,
    ) { content() }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TeacherCard(
    teacher: TeacherCardDto,
    onViewProfile: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onAssignClass: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = teacher.profile
    Box(modifier.clickable(onClick = onViewProfile)) {
        PersonCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientAvatar(p.name, p.avatarUrl)
                Column(Modifier.weight(1f)) {
                    Text(p.name, color = VColors.ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    val subtitle = (teacher.academicAssignment.subjects.take(1) + teacher.academicAssignment.grades.takeIf { it.isNotEmpty() }?.let { "Grades ${it.joinToString("–")}" }.orEmpty() + p.role)
                        .filter(String::isNotBlank).joinToString(" · ")
                    Text(subtitle, color = VColors.ink2, fontSize = 10.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(Modifier.padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusBadge(p.status)
                        p.experience?.takeIf(String::isNotBlank)?.let { Badge(it) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.Top) {
                    CircularAction(VIcons.Phone, "Call teacher", onCall, !p.phone.isNullOrBlank())
                    CircularAction(VIcons.Chat, "Message teacher", onMessage)
                    CircularAction(VIcons.School, "Assign classes", onAssignClass, teacher.actions.canAssignClass)
                }
            }
            if (teacher.academicAssignment.subjects.isNotEmpty()) {
                FlowRow(
                    Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) { teacher.academicAssignment.subjects.forEachIndexed { index, subject -> SubjectTag(subject, index) } }
            }
            Box(Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp).background(PeopleHairline))
            Row(
                Modifier.fillMaxWidth().padding(top = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MiniMetric(VIcons.Users, teacher.workload.totalStudents.toString(), "students")
                MiniMetric(VIcons.School, teacher.workload.totalClasses.toString(), "classes")
                MiniMetric(VIcons.Target, teacher.activity.attendancePercentage?.let { "$it%" } ?: "—", "att.")
                p.rating?.let { MiniMetric(VIcons.Star, it.toString(), tint = WarningInk) }
            }
        }
    }
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
    Box(modifier.clickable(onClick = onOpen)) {
        PersonCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientAvatar(student.fullName, student.profilePhotoUrl)
                Column(Modifier.weight(1f)) {
                    Text(student.fullName, color = VColors.ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        listOf("Class ${student.className}", "Roll ${student.rollNumber}", student.section.takeIf(String::isNotBlank)?.let { "Sec $it" }).filterNotNull().joinToString(" · "),
                        color = VColors.ink2,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(Modifier.padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusBadge(student.status)
                        if (student.isNewAdmission) Badge("New")
                        student.homeworkPercent.takeIf { it > 0f }?.let { Badge("${it.roundToInt()}% avg", SkyInk, SkySoft) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    CircularAction(VIcons.Phone, "Call parent", onCall, !student.parentPhone.isNullOrBlank())
                    CircularAction(VIcons.Chat, "Message parent", onMessage, !student.parentUserId.isNullOrBlank())
                }
            }
            val tags = student.todayItems.map { it.text }.filter(String::isNotBlank)
            if (tags.isNotEmpty()) {
                FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    tags.forEachIndexed { index, text -> SubjectTag(text, index) }
                }
            }
            Box(Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp).background(PeopleHairline))
            Row(
                Modifier.fillMaxWidth().padding(top = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MiniMetric(VIcons.School, student.studentCode.ifBlank { "—" })
                MiniMetric(VIcons.Target, student.attendancePercent.takeIf { it > 0f }?.let { "${it.roundToInt()}%" } ?: "—", "att.")
                when {
                    student.feesPending -> MiniMetric(VIcons.AlertCircle, "Fees pending", tint = WarningInk)
                    else -> MiniMetric(VIcons.Check, "Fees paid", tint = SuccessInk)
                }
            }
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
    Box(modifier.clickable(onClick = onOpen)) {
        PersonCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientAvatar(staff.fullName, staff.photoUrl)
                Column(Modifier.weight(1f)) {
                    Text(staff.fullName, color = VColors.ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(listOfNotNull(staff.role.takeIf(String::isNotBlank), staff.department?.takeIf(String::isNotBlank)).joinToString(" · "), color = VColors.ink2, fontSize = 10.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(Modifier.padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusBadge(staff.status)
                        staff.joinedYear?.takeIf(String::isNotBlank)?.let { Badge("Since $it", PeachInk, PeachSoft) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    CircularAction(VIcons.Phone, "Call staff member", onCall, !staff.phone.isNullOrBlank())
                    CircularAction(VIcons.Chat, "Message staff member", onMessage, !staff.phone.isNullOrBlank())
                }
            }
            val tags = listOfNotNull(staff.department?.takeIf(String::isNotBlank), staff.shift?.takeIf(String::isNotBlank))
            if (tags.isNotEmpty()) {
                FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tags.forEachIndexed { index, value -> SubjectTag(value, index) }
                }
            }
            Box(Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp).background(PeopleHairline))
            Row(
                Modifier.fillMaxWidth().padding(top = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                staff.phone?.takeIf(String::isNotBlank)?.let { MiniMetric(VIcons.Phone, it) }
                staff.email?.takeIf(String::isNotBlank)?.let { MiniMetric(VIcons.Mail, it) }
            }
        }
    }
}
