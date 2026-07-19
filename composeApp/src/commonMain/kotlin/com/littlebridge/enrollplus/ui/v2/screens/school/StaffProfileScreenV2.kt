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
import androidx.compose.foundation.rememberScrollState
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
import com.littlebridge.enrollplus.feature.admin.domain.model.StaffDto
import com.littlebridge.enrollplus.feature.admin.presentation.StaffViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonProfile
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * RA-S17: StaffProfileScreenV2 — a single non-teaching-staff member's record for
 * the admin, redesigned 1:1 with #view-staff-profile in people-tab-premium.html.
 *
 * Structure (top → bottom): Hero banner · Contact Information · Professional
 * Details · Danger zone. All data comes from the roster [StaffDto]; the screen
 * resolves the row from the VM's list so it does not need its own fetch.
 * Deletion lives here behind a confirm dialog. Three states via [VStateHost].
 */
@Composable
fun StaffProfileScreenV2(
    staffId: String,
    onBack: () -> Unit = {},
    onRemoved: () -> Unit = onBack,
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val member = state.staff.firstOrNull { it.id == staffId }
    // When the VM removes this member from the list, leave the profile.
    LaunchedEffect(state.staff) {
        if (state.staff.none { it.id == staffId } && !state.isLoading) onRemoved()
    }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = appString(StringKeys.SCH_STAFF_PROFILE), onBack = onBack)
        StaffProfileContent(
            member = member,
            isLoading = state.isLoading,
            error = state.error,
            isRemoving = state.removingIds.contains(staffId),
            onRetry = viewModel::load,
            onRemove = { viewModel.removeStaff(staffId) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun StaffProfileContent(
    member: StaffDto?,
    isLoading: Boolean,
    error: String?,
    isRemoving: Boolean,
    onRetry: () -> Unit,
    onRemove: () -> Unit,
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
            loading = isLoading,
            error = error,
            isEmpty = member == null && !isLoading && error == null,
            emptyTitle = appString(StringKeys.SCH_NO_PROFILE),
            emptyBody = appString(StringKeys.SCH_NO_PROFILE_DESC),
            emptyIcon = VIcons.User,
            onRetry = onRetry,
            skeleton = { SkeletonProfile() },
        ) {
            val m = member ?: return@VStateHost
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                StaffHeroBanner(m)          // Hero profile banner
                StaffContactInformation(m)  // Contact Information
                StaffProfessionalDetails(m) // Professional Details

                Spacer(Modifier.height(8.dp))
                StaffDangerZone(
                    isRemoving = isRemoving,
                    onRequestRemove = { confirmRemove = true },
                )
            }
        }
    }

    VConfirmDialog(
        visible = confirmRemove,
        title = appString(StringKeys.SCH_REMOVE_STAFF_MEMBER),
        message = appString(StringKeys.SCH_REMOVE_STAFF_CONFIRM, "name" to (member?.fullName ?: appString(StringKeys.SCH_STAFF))),
        confirmLabel = appString(StringKeys.SCH_REMOVE),
        icon = VIcons.AlertTriangle,
        onConfirm = { confirmRemove = false; onRemove() },
        onDismiss = { confirmRemove = false },
    )
}

// ───────────────────────── 1. Hero profile banner ─────────────────────────
// HTML .hero: 72dp avatar ring, name, "Role · Department" subtitle, Active
// badge, and a facts row with the Joined date.

@Composable
private fun StaffHeroBanner(m: StaffDto) {
    val active = m.status.equals("active", ignoreCase = true)
    val department = m.department?.takeIf { it.isNotBlank() }
    val subtitle = if (department != null) "${m.role} · $department" else m.role
    val joined = m.joinedDate?.takeIf { it.isNotBlank() }
        ?: m.joinedYear?.takeIf { it.isNotBlank() }
    VCard(padding = 20.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VAvatar(name = m.fullName, src = m.photoUrl, size = 72.dp, ring = true)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(m.fullName, style = VTypography.h2, color = VColors.ink)
                Text(subtitle, style = VTypography.caption, color = VColors.ink2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VBadge(
                        text = if (active) appString(StringKeys.SCH_ACTIVE) else appString(StringKeys.SCH_INACTIVE),
                        tone = if (active) VBadgeTone.Success else VBadgeTone.Neutral,
                        leadingIcon = VIcons.Check,
                    )
                }
            }
        }
        if (joined != null) {
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                StaffHeroFact(VIcons.Calendar, appString(StringKeys.SCH_JOINED), joined)
            }
        }
    }
}

@Composable
private fun StaffHeroFact(icon: ImageVector, label: String, value: String) {
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

// ─────────────────────────── 2. Contact Information ────────────────────────
// HTML: Phone · Email · Address detail rows with 34dp cream icon chips.

@Composable
private fun StaffContactInformation(m: StaffDto) {
    val phone = m.phone?.takeIf { it.isNotBlank() }
    val email = m.email?.takeIf { it.isNotBlank() }
    val address = m.address?.takeIf { it.isNotBlank() }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_CONTACT_INFORMATION))
        VCard(padding = 18.dp) {
            if (phone == null && email == null && address == null) {
                Text(appString(StringKeys.SCH_NO_CONTACT_DETAILS), style = VTypography.caption, color = VColors.ink2)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    DetailRow(VIcons.Phone, appString(StringKeys.SCH_PHONE), phone ?: "—")
                    DetailRow(VIcons.Mail, appString(StringKeys.SCH_EMAIL), email ?: "—")
                    if (address != null) {
                        DetailRow(VIcons.MapPin, appString(StringKeys.SCH_ADDRESS), address)
                    }
                }
            }
        }
    }
}

// ─────────────────────────── 3. Professional Details ───────────────────────
// HTML: Role · Department · Joined detail rows.

@Composable
private fun StaffProfessionalDetails(m: StaffDto) {
    val department = m.department?.takeIf { it.isNotBlank() }
    val joined = m.joinedDate?.takeIf { it.isNotBlank() }
        ?: m.joinedYear?.takeIf { it.isNotBlank() }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_PROFESSIONAL_DETAILS))
        VCard(padding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailRow(VIcons.IdCard, appString(StringKeys.SCH_ROLE), m.role)
                DetailRow(VIcons.HomeStroke, appString(StringKeys.SCH_DEPARTMENT), department ?: "—")
                DetailRow(VIcons.Calendar, appString(StringKeys.SCH_JOINED), joined ?: "—")
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

// ─────────────────────────────── 4. Danger zone ───────────────────────────

@Composable
private fun StaffDangerZone(
    isRemoving: Boolean,
    onRequestRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        VSectionHeader(title = appString(StringKeys.SCH_DANGER_ZONE))
        VCard(padding = 18.dp, border = true) {
            Text(appString(StringKeys.SCH_REMOVE_STAFF), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.error)
            Spacer(Modifier.height(4.dp))
            Text(
                appString(StringKeys.SCH_REMOVE_STAFF_DANGER),
                style = VTypography.caption, color = VColors.ink2,
            )
            Spacer(Modifier.height(14.dp))
            VButton(
                text = appString(StringKeys.SCH_REMOVE_STAFF),
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
