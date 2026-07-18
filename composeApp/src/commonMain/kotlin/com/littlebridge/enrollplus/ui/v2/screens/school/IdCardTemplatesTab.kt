package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.branding.domain.model.SchoolBranding
import com.littlebridge.enrollplus.feature.idcard.domain.model.IdCardTemplateDto
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardState
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardViewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.littlebridge.enrollplus.ui.v2.components.ShimmerBox
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressRing
import com.littlebridge.enrollplus.ui.v2.components.VTag
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.theme.BrandingColorMapper
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString

// ════════════════════════════════════════════════════════════════════════════
// STATS BANNER — Progress ring + breakdown + achievement badge
// ════════════════════════════════════════════════════════════════════════════

@Composable
internal fun IdCardStatsBanner(
    totalCards: Int,
    studentCards: Int,
    teacherCards: Int,
    staffCards: Int,
    onBadgeClick: () -> Unit = {},
) {
    val milestoneLabel = when {
        totalCards >= 500 -> appString(StringKeys.IDCARD_MILESTONE_MASTER)
        totalCards >= 100 -> appString(StringKeys.IDCARD_MILESTONE_CENTURY)
        totalCards >= 50 -> appString(StringKeys.IDCARD_MILESTONE_HALF)
        totalCards >= 1 -> appString(StringKeys.IDCARD_MILESTONE_FIRST)
        else -> appString(StringKeys.IDCARD_MILESTONE_START)
    }
    val milestoneIcon = when {
        totalCards >= 500 -> Icons.Filled.Star
        totalCards >= 100 -> Icons.Filled.Star
        totalCards >= 50 -> Icons.Filled.Check
        totalCards >= 1 -> Icons.Filled.Check
        else -> Icons.Filled.Add
    }
    val milestoneTone = when {
        totalCards >= 500 -> VBadgeTone.Accent
        totalCards >= 100 -> VBadgeTone.Success
        totalCards >= 50 -> VBadgeTone.Warning
        totalCards >= 1 -> VBadgeTone.Arctic
        else -> VBadgeTone.Neutral
    }

    VCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                VProgressRing(
                    value = if (totalCards > 0) 100f else 0f,
                    size = 56.dp,
                    strokeWidth = 6.dp,
                    tone = VBadgeTone.Accent,
                    label = totalCards.toString(),
                )
                Text(
                    text = appString(StringKeys.IDCARD_TOTAL_CARDS),
                    style = VTypography.caption.copy(color = VColors.ink3),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                StatRow(label = appString(StringKeys.IDCARD_STUDENTS), count = studentCards, color = VColors.violet)
                StatRow(label = appString(StringKeys.IDCARD_TEACHERS), count = teacherCards, color = VColors.sky)
                StatRow(label = appString(StringKeys.IDCARD_STAFF), count = staffCards, color = VColors.coral)
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(VColors.violet.copy(alpha = 0.12f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBadgeClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        milestoneIcon,
                        contentDescription = milestoneLabel,
                        tint = VColors.violetInk,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Text(
                    text = milestoneLabel,
                    style = VTypography.caption.copy(color = VColors.ink2).copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, count: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(
            text = "$count $label",
            style = VTypography.caption.copy(color = VColors.ink2),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TEMPLATES TAB — Visual template builder
// ════════════════════════════════════════════════════════════════════════════

private val AVAILABLE_FIELDS = listOf(
    "school", "photo", "qrOnFront",
    "name", "role", "class",
    "idNumber", "rollNumber",
    "dateOfBirth", "bloodGroup",
    "emergencyContact", "address",
    "email",
)

@Composable
private fun fieldLabel(field: String): String = when (field) {
    "name" -> appString(StringKeys.IDCARD_FIELD_NAME)
    "role" -> appString(StringKeys.IDCARD_FIELD_ROLE)
    "class" -> appString(StringKeys.IDCARD_FIELD_CLASS)
    "school" -> appString(StringKeys.IDCARD_FIELD_SCHOOL)
    "photo" -> appString(StringKeys.IDCARD_FIELD_PHOTO)
    "qrOnFront" -> appString(StringKeys.IDCARD_FIELD_QR)
    "emergencyContact" -> appString(StringKeys.IDCARD_FIELD_EMERGENCY)
    "bloodGroup" -> appString(StringKeys.IDCARD_FIELD_BLOOD)
    "idNumber" -> "ID Number"
    "rollNumber" -> "Roll Number"
    "dateOfBirth" -> "Date of Birth"
    "address" -> "Address"
    "email" -> "Email"
    else -> field
}

private fun fieldIcon(field: String): ImageVector = when (field) {
    "school" -> VIcons.School
    "photo" -> VIcons.Camera
    "qrOnFront" -> VIcons.CheckCircle
    "name" -> VIcons.User
    "role" -> VIcons.Star
    "class" -> VIcons.BookOpen
    "idNumber" -> VIcons.IdCard
    "rollNumber" -> VIcons.ListChecks
    "dateOfBirth" -> VIcons.Calendar
    "bloodGroup" -> VIcons.Heart
    "emergencyContact" -> VIcons.Phone
    "address" -> VIcons.MapPin
    "email" -> VIcons.Mail
    else -> VIcons.User
}

private val PRESET_COLORS = listOf(
    0xFF6C5CE0.toInt() to "Lavender",
    0xFF00B4D8.toInt() to "Teal",
    0xFF1A2422.toInt() to "Ink",
    0xFFB3261E.toInt() to "Red",
    0xFF1F7A4D.toInt() to "Green",
    0xFFE67E22.toInt() to "Orange",
    0xFF9B59B6.toInt() to "Rainbow",
)

private val BACKGROUND_STYLES = listOf(
    "gradient" to "Gradient",
    "waves" to "Waves",
    "minimal" to "Minimal",
    "pattern" to "Pattern",
)

@Composable
internal fun TemplatesTab(
    state: IdCardState,
    viewModel: IdCardViewModel,
    scrollState: ScrollState = rememberScrollState(),
    branding: SchoolBranding? = null,
) {
    var templateName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("student") }
    var selectedFields by remember { mutableStateOf(setOf("school", "photo", "qrOnFront", "name", "role", "class")) }
    var accentColorArgb by remember { mutableStateOf(0xFF6C5CE0.toInt()) }
    var selectedBgStyle by remember { mutableStateOf("gradient") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        state.error?.let { err ->
            VCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(err, color = VColors.error, style = VTypography.body)
            }
        }
        state.infoMessage?.let { msg ->
            VCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(msg, color = VColors.success, style = VTypography.body)
            }
        }

        if (state.templates.isEmpty() && state.isLoading) {
            repeat(2) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    height = 64.dp,
                )
            }
        } else if (state.templates.isEmpty()) {
            VEmptyState(
                title = appString(StringKeys.IDCARD_NO_TEMPLATES),
                body = appString(StringKeys.IDCARD_NO_TEMPLATES_DESC),
                icon = Icons.Filled.School,
                modifier = Modifier.padding(top = 24.dp),
            )
        } else {
            val activeTemplates = state.templates.filter { it.isActive }
            val inactiveTemplates = state.templates.filter { !it.isActive }

            activeTemplates.forEach { template ->
                TemplateCard(
                    template = template,
                    onDeactivate = { viewModel.deactivateTemplate(template.id) },
                )
            }

            if (inactiveTemplates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Inactive templates",
                    style = VTypography.label.copy(color = VColors.ink3),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                inactiveTemplates.forEach { template ->
                    TemplateCard(
                        template = template,
                        onDeactivate = { viewModel.deactivateTemplate(template.id) },
                        enabled = false,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── CREATE NEW TEMPLATE ──
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            // Template Name
            OutlinedTextField(
                value = templateName,
                onValueChange = { templateName = it },
                label = { Text(appString(StringKeys.IDCARD_TEMPLATE_NAME)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ── Card Type ──
            SectionHeader(
                title = appString(StringKeys.IDCARD_CARD_TYPE),
                subtitle = "Select who this template is for",
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CardTypeChip(
                    label = appString(StringKeys.IDCARD_STUDENT),
                    icon = VIcons.User,
                    iconColor = Color(0xFF4CAF50),
                    selected = selectedRole == "student",
                    onClick = { selectedRole = "student" },
                )
                CardTypeChip(
                    label = appString(StringKeys.IDCARD_TEACHER_ROLE),
                    icon = VIcons.User,
                    iconColor = Color(0xFFE67E22),
                    selected = selectedRole == "teacher",
                    onClick = { selectedRole = "teacher" },
                )
                CardTypeChip(
                    label = appString(StringKeys.IDCARD_STAFF_ROLE),
                    icon = VIcons.User,
                    iconColor = Color(0xFFE67E22),
                    selected = selectedRole == "staff",
                    onClick = { selectedRole = "staff" },
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // ── Fields to Display ──
            SectionHeader(
                title = appString(StringKeys.IDCARD_FIELDS_DISPLAY),
                subtitle = "Choose information to show on the ID card",
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AVAILABLE_FIELDS.forEach { field ->
                    FieldChip(
                        label = fieldLabel(field),
                        icon = fieldIcon(field),
                        selected = field in selectedFields,
                        onClick = {
                            selectedFields = if (field in selectedFields) selectedFields - field else selectedFields + field
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // ── Accent Color ──
            SectionHeader(
                title = appString(StringKeys.IDCARD_ACCENT_COLOR),
                subtitle = "Choose your primary color",
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                PRESET_COLORS.forEach { (argb, _) ->
                    AccentColorSwatch(
                        argb = argb,
                        selected = accentColorArgb == argb,
                        onClick = { accentColorArgb = argb },
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // ── Background Style ──
            SectionHeader(
                title = "Background Style",
                subtitle = "Choose card background",
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BACKGROUND_STYLES.forEach { (key, label) ->
                    BackgroundStyleOption(
                        label = label,
                        style = key,
                        accentArgb = accentColorArgb,
                        selected = selectedBgStyle == key,
                        onClick = { selectedBgStyle = key },
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // ── Live Preview ──
            SectionHeader(
                title = appString(StringKeys.IDCARD_LIVE_PREVIEW),
                subtitle = "See how your card looks",
            )
            Spacer(modifier = Modifier.height(8.dp))
            LiveCardPreview(
                templateName = templateName.ifBlank { appString(StringKeys.IDCARD_PREVIEW) },
                roleType = selectedRole,
                fields = selectedFields,
                accentArgb = accentColorArgb,
                logoUrl = branding?.logoUrl,
                schoolName = branding?.schoolName,
                brandingPrimaryHex = branding?.primaryColor,
                brandingAccentHex = branding?.accentColor,
                bgStyle = selectedBgStyle,
            )
            Spacer(modifier = Modifier.height(16.dp))

            VButton(
                text = if (state.isLoading) appString(StringKeys.IDCARD_CREATING) else appString(StringKeys.IDCARD_CREATE_BTN),
                onClick = {
                    if (templateName.isNotBlank()) {
                        viewModel.clearMessages()
                        val config = buildConfigJson(selectedFields, accentColorArgb)
                        viewModel.createTemplate(templateName, selectedRole, config, config)
                        templateName = ""
                    }
                },
                variant = VButtonVariant.Primary,
                enabled = !state.isLoading && templateName.isNotBlank(),
                full = true,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Section header — bold title + subtitle
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            color = VColors.ink,
        )
        Text(
            text = subtitle,
            style = VTypography.caption.copy(fontSize = 12.sp),
            color = VColors.ink3,
        )
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Card Type chip — icon + text, colored border when selected
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CardTypeChip(
    label: String,
    icon: ImageVector,
    iconColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Color(0xFF6C5CE0) else VColors.line
    val bgColor = if (selected) Color(0xFF6C5CE0).copy(alpha = 0.06f) else VColors.surfaceCard

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            color = VColors.ink,
        )
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Field chip — icon + text + checkmark when selected
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FieldChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Color(0xFF6C5CE0) else VColors.line
    val bgColor = if (selected) Color(0xFF6C5CE0).copy(alpha = 0.06f) else VColors.surfaceCard
    val iconTint = if (selected) Color(0xFF6C5CE0) else VColors.ink3

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            color = VColors.ink,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C5CE0)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Accent color swatch — circle with checkmark overlay when selected
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AccentColorSwatch(
    argb: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = Color(argb)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFF6C5CE0), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6C5CE0)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Background style option — thumbnail with label
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BackgroundStyleOption(
    label: String,
    style: String,
    accentArgb: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = Color(accentArgb)
    val borderColor = if (selected) Color(0xFF6C5CE0) else VColors.line

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VColors.surfaceCard)
                .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            when (style) {
                "gradient" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(accent, accent.copy(alpha = 0.5f), Color.White)
                                )
                            ),
                    )
                }
                "waves" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(VColors.cream),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .offset(y = 16.dp)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .background(accent.copy(alpha = 0.15f)),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .offset(y = 30.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(accent.copy(alpha = 0.08f)),
                        )
                    }
                }
                "minimal" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White),
                    )
                }
                "pattern" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(VColors.cream),
                    ) {
                        for (row in 0..3) {
                            for (col in 0..3) {
                                Box(
                                    modifier = Modifier
                                        .offset(
                                            x = (12 + col * 16).dp,
                                            y = (12 + row * 16).dp,
                                        )
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(accent.copy(alpha = 0.2f)),
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = VTypography.caption.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = if (selected) Color(0xFF6C5CE0) else VColors.ink3,
        )
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Live Preview — real-time card preview
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LiveCardPreview(
    templateName: String,
    roleType: String,
    fields: Set<String>,
    accentArgb: Int,
    logoUrl: String? = null,
    schoolName: String? = null,
    brandingPrimaryHex: String? = null,
    brandingAccentHex: String? = null,
    bgStyle: String = "gradient",
) {
    val brandPrimary = brandingPrimaryHex?.let { BrandingColorMapper.parseHex(it) }
    val brandAccent = brandingAccentHex?.let { BrandingColorMapper.parseHex(it) }
    val accent = brandAccent ?: brandPrimary ?: Color(accentArgb)
    val displaySchoolName = schoolName?.takeIf { it.isNotBlank() } ?: "Vidya Prayag School"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .aspectRatio(54f / 86f)
            .clip(RoundedCornerShape(16.dp))
            .background(VColors.surfaceCard)
            .border(2.dp, accent, RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Header band
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.30f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(accent, accent.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if ("school" in fields) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (!logoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = logoUrl,
                                contentDescription = displaySchoolName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(VIcons.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Text(
                            text = displaySchoolName,
                            style = VTypography.caption.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "Excellence in Education",
                            style = VTypography.caption.copy(color = Color.White.copy(alpha = 0.8f), fontSize = 7.sp),
                        )
                    }
                }
            }

            // Body — photo overlapping header, then details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.62f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Photo — overlapping the header
                    if ("photo" in fields) {
                        Box(
                            Modifier
                                .offset(y = (-20).dp)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.15f))
                                .border(2.5.dp, accent.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = accent, modifier = Modifier.size(30.dp))
                        }
                    }

                    // Name
                    if ("name" in fields) {
                        Text(
                            text = templateName,
                            style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = VColors.ink),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                    }

                    // Role badge
                    if ("role" in fields) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accent.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = roleType.replaceFirstChar { it.uppercase() },
                                style = VTypography.caption.copy(color = accent, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            )
                        }
                    }

                    // Class
                    if ("class" in fields) {
                        Text("Class 10-A", style = VTypography.caption.copy(color = VColors.ink2, fontSize = 9.sp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Details + QR row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            if ("idNumber" in fields) {
                                DetailRow(label = "ID No.", value = "VP20261001")
                            }
                            if ("rollNumber" in fields) {
                                DetailRow(label = "Roll No.", value = "23")
                            }
                            if ("bloodGroup" in fields) {
                                DetailRow(label = "Blood Group", value = "B+")
                            }
                            if ("emergencyContact" in fields) {
                                DetailRow(label = "Emergency", value = "9876543210")
                            }
                            if ("address" in fields) {
                                DetailRow(label = "Address", value = "Delhi, India")
                            }
                            if ("email" in fields) {
                                DetailRow(label = "Email", value = "aarav@school.in")
                            }
                        }
                        if ("qrOnFront" in fields) {
                            QrGridPreview(
                                size = 48.dp,
                                color = VColors.ink,
                            )
                        }
                    }
                }
            }
        }

        // Footer band — absolute overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .align(Alignment.BottomCenter)
                .background(accent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = appString(StringKeys.IDCARD_SCAN_QR),
                style = VTypography.caption.copy(color = Color.White, fontSize = 7.sp),
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "$label:",
            style = VTypography.caption.copy(color = VColors.ink3, fontSize = 7.sp),
        )
        Text(
            text = value,
            style = VTypography.caption.copy(color = VColors.ink, fontWeight = FontWeight.SemiBold, fontSize = 7.sp),
        )
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// QR grid preview
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QrGridPreview(
    size: androidx.compose.ui.unit.Dp,
    color: Color,
) {
    val cellSize = size / 7
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
    ) {
        val pattern = listOf(
            listOf(1, 1, 1, 0, 1, 0, 1),
            listOf(1, 0, 1, 1, 0, 1, 1),
            listOf(1, 1, 1, 0, 1, 1, 0),
            listOf(0, 1, 0, 1, 0, 0, 1),
            listOf(1, 0, 1, 1, 1, 1, 0),
            listOf(1, 1, 0, 0, 1, 0, 1),
            listOf(0, 1, 1, 1, 0, 1, 1),
        )
        pattern.forEachIndexed { row, cols ->
            cols.forEachIndexed { col, cell ->
                if (cell == 1) {
                    Box(
                        Modifier
                            .offset(x = cellSize * col, y = cellSize * row)
                            .size(cellSize)
                            .background(color),
                    )
                }
            }
        }
        val corners = listOf(0 to 0, 4 to 0, 0 to 4)
        corners.forEach { (col, row) ->
            Box(
                Modifier
                    .offset(x = cellSize * col, y = cellSize * row)
                    .size(cellSize * 3)
                    .background(color),
            )
            Box(
                Modifier
                    .offset(x = cellSize * (col + 1), y = cellSize * (row + 1))
                    .size(cellSize)
                    .background(Color.White),
            )
        }
    }
}

private fun buildConfigJson(fields: Set<String>, accentArgb: Int): String {
    val fieldsArray = fields.joinToString(",") { "\"$it\"" }
    val hex = "#" + (accentArgb and 0xFFFFFF).toString(16).padStart(6, '0').uppercase()
    val showQr = "qrOnFront" in fields
    return """{"fields":[$fieldsArray],"backgroundColor":"#FFFFFF","textColor":"#1A2422","accentColor":"$hex","showLogo":true,"showQrOnFront":$showQr}"""
}

@Composable
private fun TemplateCard(
    template: IdCardTemplateDto,
    onDeactivate: () -> Unit,
    enabled: Boolean = true,
) {
    VCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    template.name,
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = if (enabled) VColors.ink else VColors.ink3),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    VBadge(
                        text = template.roleType.replaceFirstChar { it.uppercase() },
                        tone = if (enabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                    )
                    if (template.isActive) {
                        VBadge(text = appString(StringKeys.IDCARD_ACTIVE), tone = VBadgeTone.Success, leadingIcon = Icons.Filled.Check)
                    } else {
                        VBadge(text = appString(StringKeys.IDCARD_INACTIVE), tone = VBadgeTone.Neutral)
                    }
                }
            }
            if (enabled && template.isActive) {
                VButton(
                    text = appString(StringKeys.IDCARD_DEACTIVATE),
                    onClick = onDeactivate,
                    variant = VButtonVariant.Secondary,
                    size = VButtonSize.Sm,
                )
            }
        }
    }
}
