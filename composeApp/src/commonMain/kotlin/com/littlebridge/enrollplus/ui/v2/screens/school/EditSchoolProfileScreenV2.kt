package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolProfileState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VDropdown
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonProfile
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * RA-47: EditSchoolProfileScreenV2 — the admin edits the live `schools` row
 * (the institutional record) via [SchoolProfileViewModel]
 * (`GET /api/v1/school/profile`, `PUT /api/v1/school/profile`). The server
 * resolves school_id from the JWT and enforces school-admin (never trusts the
 * body), so an admin can only ever edit their own school.
 *
 * Three states via [VStateHost] (LAW 3): loading while the row loads,
 * error+retry on a fatal load failure, and the editable form once loaded.
 * Save errors / confirmations are surfaced inline (dangerInk / successInk)
 * rather than failing silently. Portal overlay — back returns to Settings.
 */
@Composable
fun EditSchoolProfileScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SchoolProfileViewModel = koinViewModel(),
)
{

    val state by viewModel.state.collectAsStateV2()


    LaunchedEffect(state.infoMessage) {
        if (state.infoMessage != null) {
            delay(2500)
            viewModel.clearMessages()
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            EditSchoolProfileContent(
                state = state,

                onName = viewModel::onName,
                onBoard = viewModel::onBoard,
                onMedium = viewModel::onMedium,
                onSchoolGender = viewModel::onSchoolGender,

                onContactPhone = viewModel::onContactPhone,
                onContactEmail = viewModel::onContactEmail,

                onPrincipalName = viewModel::onPrincipalName,
                onPrincipalPhone = viewModel::onPrincipalPhone,
                onPrincipalEmail = viewModel::onPrincipalEmail,

                onFullAddress = viewModel::onFullAddress,
                onCity = viewModel::onCity,
                onDistrict = viewModel::onDistrict,
                onState = viewModel::onState,
                onPincode = viewModel::onPincode,

                onSave = viewModel::save,
                onBack = onBack,
                onRetry = viewModel::load,

                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 88.dp)
            )
        }

        // Floating Save Button — fixed above system nav bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            VButton(
                text = "Save changes",
                onClick = viewModel::save,
                full = true,
                variant = VButtonVariant.Primary,
                tone = VButtonTone.Teal,
                enabled = !state.isSaving,
                loading = state.isSaving,
                leading = {
                    Icon(
                        VIcons.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                },
                trailing = {
                    Icon(
                        VIcons.ArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun EditSchoolProfileContent(
    state: SchoolProfileState,
    onName: (String) -> Unit,
    onBoard: (String) -> Unit,
    onMedium: (String) -> Unit,
    onSchoolGender: (String) -> Unit,
    onContactPhone: (String) -> Unit,
    onContactEmail: (String) -> Unit,
    onPrincipalName: (String) -> Unit,
    onPrincipalPhone: (String) -> Unit,
    onPrincipalEmail: (String) -> Unit,
    onFullAddress: (String) -> Unit,
    onCity: (String) -> Unit,
    onDistrict: (String) -> Unit,
    onState: (String) -> Unit,
    onPincode: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        // ── Header: back arrow ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    VIcons.ArrowLeft,
                    contentDescription = "Back",
                    tint = VColors.ink,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // ── Title section ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = "School profile",
                style = VTypography.h1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                ),
                color = VColors.ink,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Provide accurate information so parents,\nstudents and documents stay trusted.",
                style = VTypography.body.copy(fontSize = 14.sp),
                color = VColors.ink3,
                lineHeight = 20.sp,
            )
        }

        // ── Content (loading / error / form) ──
        VStateHost(
            loading = state.isLoading,
            error = state.loadError,
            isEmpty = false,
            onRetry = onRetry,
            skeleton = { SkeletonProfile() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                // ── SCHOOL PROFILE card ──
                EditSection(
                    title = "School profile",
                    subtitle = "Basic information about your school",
                    icon = VIcons.GraduationCap,
                    accentColor = Color(0xFF0D9488),       // teal
                    iconBgColor = Color(0xFFCCFBF1),       // light teal
                ) {
                    VInput(
                        value = state.name,
                        onValueChange = onName,
                        placeholder = "School Name",
                        leadingIcon = VIcons.School,
                        isError = state.fieldErrors.containsKey("name"),
                        errorText = state.fieldErrors["name"],
                    )
                    VDropdown(
                        value = state.board,
                        options = BOARD_OPTIONS,
                        onSelect = onBoard,
                        placeholder = "Board",
                        leadingIcon = VIcons.School,
                    )
                    VDropdown(
                        value = state.medium,
                        options = MEDIUM_OPTIONS,
                        onSelect = onMedium,
                        placeholder = "Medium",
                        leadingIcon = VIcons.Globe,
                    )
                    VDropdown(
                        value = state.schoolGender,
                        options = SCHOOL_TYPE_OPTIONS,
                        onSelect = onSchoolGender,
                        placeholder = "School Type",
                        leadingIcon = VIcons.Users,
                    )
                }


                // ── CONTACT DETAILS card ──
                EditSection(
                    title = "Contact Details",
                    subtitle = "Public communication & leadership",
                    icon = VIcons.Phone,
                    accentColor = Color(0xFF2563EB),       // blue
                    iconBgColor = Color(0xFFDBEAFE),       // light blue
                ) {
                    VInput(
                        value = state.contactPhone,
                        onValueChange = onContactPhone,
                        placeholder = "School Phone",
                        leadingIcon = VIcons.Phone,
                        keyboardType = KeyboardType.Phone,
                        isError = state.fieldErrors.containsKey("contactPhone"),
                        errorText = state.fieldErrors["contactPhone"],
                    )
                    VInput(
                        value = state.contactEmail,
                        onValueChange = onContactEmail,
                        placeholder = "School Email",
                        leadingIcon = VIcons.Mail,
                        keyboardType = KeyboardType.Email,
                        isError = state.fieldErrors.containsKey("contactEmail"),
                        errorText = state.fieldErrors["contactEmail"],
                    )
                    VInput(
                        value = state.principalName,
                        onValueChange = onPrincipalName,
                        placeholder = "Principal Name",
                        leadingIcon = VIcons.User,
                    )
                    VInput(
                        value = state.principalPhone,
                        onValueChange = onPrincipalPhone,
                        placeholder = "Principal Phone",
                        leadingIcon = VIcons.Phone,
                        keyboardType = KeyboardType.Phone,
                        isError = state.fieldErrors.containsKey("principalPhone"),
                        errorText = state.fieldErrors["principalPhone"],
                    )
                    VInput(
                        value = state.principalEmail,
                        onValueChange = onPrincipalEmail,
                        placeholder = "Principal Email",
                        leadingIcon = VIcons.Mail,
                        keyboardType = KeyboardType.Email,
                        isError = state.fieldErrors.containsKey("principalEmail"),
                        errorText = state.fieldErrors["principalEmail"],
                    )
                }


                // ── LOCATION card ──
                EditSection(
                    title = "Location",
                    subtitle = "School address",
                    icon = VIcons.MapPin,
                    accentColor = Color(0xFF7C3AED),       // purple
                    iconBgColor = Color(0xFFEDE9FE),       // light purple
                ) {
                    VInput(
                        value = state.fullAddress,
                        onValueChange = onFullAddress,
                        placeholder = "Address",
                        leadingIcon = VIcons.Home,
                        singleLine = false,
                    )
                    VDropdown(
                        value = state.city,
                        options = CITY_OPTIONS,
                        onSelect = onCity,
                        placeholder = "City",
                        leadingIcon = VIcons.School,
                        isError = state.fieldErrors.containsKey("city"),
                    )
                    VInput(
                        value = state.pincode,
                        onValueChange = onPincode,
                        placeholder = "PIN",
                        leadingIcon = VIcons.Settings,
                        keyboardType = KeyboardType.Number,
                        isError = state.fieldErrors.containsKey("pincode"),
                        errorText = state.fieldErrors["pincode"],
                    )
                    VInput(
                        value = state.district,
                        onValueChange = onDistrict,
                        placeholder = "District",
                        leadingIcon = VIcons.MapPin,
                        isError = state.fieldErrors.containsKey("district"),
                        errorText = state.fieldErrors["district"],
                    )
                    VDropdown(
                        value = state.state,
                        options = STATE_OPTIONS,
                        onSelect = onState,
                        placeholder = "State",
                        leadingIcon = VIcons.MapPin,
                    )
                }


                // ── Feedback ──
                state.errorMessage?.let {
                    Text(
                        it,
                        style = VTypography.body.copy(color = VColors.error),
                    )
                }

                state.infoMessage?.let {
                    Text(
                        it,
                        style = VTypography.body.copy(color = VColors.success),
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Section card — colored icon circle + accent title + stacked VInput fields
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    iconBgColor: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Colored circular icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column {
                Text(
                    text = title,
                    style = VTypography.body.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    ),
                    color = accentColor,
                )
                Text(
                    text = subtitle,
                    style = VTypography.caption.copy(fontSize = 12.sp),
                    color = VColors.ink3,
                )
            }
        }

        // Input fields
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Dropdown option lists (matching onboarding flow)
// ─────────────────────────────────────────────────────────────────────────────

private val BOARD_OPTIONS = listOf("CBSE", "ICSE", "UP State", "Other")

private val MEDIUM_OPTIONS = listOf("English", "Hindi", "Bilingual (English + Hindi)", "Other")

private val SCHOOL_TYPE_OPTIONS = listOf("Government", "Private Aided", "Private Unaided", "Central")

private val CITY_OPTIONS = listOf(
    "New Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata",
    "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow",
    "Kanpur", "Varanasi", "Meerut", "Noida", "Ghaziabad", "Gurugram",
)

private val STATE_OPTIONS = listOf(
    "Uttar Pradesh", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi",
    "Gujarat", "Rajasthan", "West Bengal", "Telangana", "Andhra Pradesh",
    "Kerala", "Madhya Pradesh", "Bihar", "Punjab", "Haryana",
    "Odisha", "Jharkhand", "Chhattisgarh", "Assam", "Uttarakhand",
    "Himachal Pradesh", "Goa", "Manipur", "Meghalaya", "Nagaland",
    "Tripura", "Mizoram", "Arunachal Pradesh", "Sikkim",
    "Jammu & Kashmir", "Ladakh", "Chandigarh", "Puducherry",
)
