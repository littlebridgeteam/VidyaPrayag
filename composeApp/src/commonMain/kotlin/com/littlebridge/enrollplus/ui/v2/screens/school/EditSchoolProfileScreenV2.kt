package com.littlebridge.enrollplus.ui.v2.screens.school

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolProfileState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
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
            .statusBarsPadding()
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            VBackHeader(
                title = "Institutional Profile",
                onBack = onBack
            )

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
                onRetry = viewModel::load,

                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 96.dp)
            )
        }

        // Floating Save Button — fixed above system nav bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),

            color = VColors.surfaceCard,

            shadowElevation = 12.dp
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    )
            ) {
                VButton(
                    text = "Save changes",

                    onClick = viewModel::save,

                    full = true,

                    variant =
                        VButtonVariant.Primary,

                    tone =
                        VButtonTone.Teal,

                    enabled =
                        !state.isSaving,

                    loading =
                        state.isSaving
                )
            }
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
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {

    

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .imePadding()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(20.dp)
    ) {


        VStateHost(
            loading = state.isLoading,
            error = state.loadError,
            isEmpty = false,
            onRetry = onRetry,
            skeleton = { SkeletonProfile() },
        ) {


            // HEADER ------------------------------------------------

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "School profile",

                    style =
                        VTypography.h2
                            .copy(color = VColors.ink)
                )


                Text(
                    text =
                        "Keep your school's information accurate for parents, students and documents.",

                    style =
                        VTypography.body
                            .copy(color = VColors.ink3)
                )
            }



            // SCHOOL PROFILE ------------------------------------------------

            EditSection(
                title = "School profile",
                subtitle = "Basic information",
                icon = Icons.Outlined.School
            ) {


                VInput(
                    value = state.name,
                    onValueChange = onName,
                    label = "School name",
                    placeholder =
                        "Little Bridge Public School",
                    isError = state.fieldErrors.containsKey("name"),
                    errorText = state.fieldErrors["name"]
                )


                VInput(
                    value = state.board,
                    onValueChange = onBoard,
                    label = "Board",
                    placeholder =
                        "CBSE / ICSE / State"
                )


                VInput(
                    value = state.medium,
                    onValueChange = onMedium,
                    label = "Medium",
                    placeholder =
                        "English"
                )


                VInput(
                    value = state.schoolGender,
                    onValueChange = onSchoolGender,
                    label = "School type",
                    placeholder =
                        "Co-ed / Boys / Girls"
                )
            }



            // CONTACT DETAILS ------------------------------------------------

            EditSection(
                title = "Contact details",
                subtitle = "Public communication & leadership",
                icon = Icons.Outlined.Phone
            ) {


                VInput(
                    value = state.contactPhone,
                    onValueChange = onContactPhone,
                    label = "School phone",
                    placeholder =
                        "10-digit number",
                    keyboardType =
                        KeyboardType.Phone,
                    isError = state.fieldErrors.containsKey("contactPhone"),
                    errorText = state.fieldErrors["contactPhone"]
                )


                VInput(
                    value = state.contactEmail,
                    onValueChange = onContactEmail,
                    label = "School email",
                    placeholder =
                        "office@school.edu",
                    keyboardType =
                        KeyboardType.Email,
                    isError = state.fieldErrors.containsKey("contactEmail"),
                    errorText = state.fieldErrors["contactEmail"]
                )


                VInput(
                    value = state.principalName,
                    onValueChange = onPrincipalName,
                    label = "Principal name",
                    placeholder =
                        "Full name"
                )


                VInput(
                    value = state.principalPhone,
                    onValueChange = onPrincipalPhone,
                    label = "Principal phone",
                    placeholder =
                        "10-digit number",
                    keyboardType =
                        KeyboardType.Phone,
                    isError = state.fieldErrors.containsKey("principalPhone"),
                    errorText = state.fieldErrors["principalPhone"]
                )


                VInput(
                    value = state.principalEmail,
                    onValueChange = onPrincipalEmail,
                    label = "Principal email",
                    placeholder =
                        "principal@school.edu",
                    keyboardType =
                        KeyboardType.Email,
                    isError = state.fieldErrors.containsKey("principalEmail"),
                    errorText = state.fieldErrors["principalEmail"]
                )
            }




            // ADDRESS ------------------------------------------------

            EditSection(
                title = "Location",
                subtitle = "School address",
                icon = Icons.Outlined.LocationOn
            ) {


                VInput(
                    value = state.fullAddress,
                    onValueChange = onFullAddress,
                    label = "Address",
                    placeholder =
                        "Street, area, landmark",
                    singleLine = false
                )


                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Box(
                        Modifier.weight(1f)
                    ) {
                        VInput(
                            value = state.city,
                            onValueChange = onCity,
                            label = "City",
                            isError = state.fieldErrors.containsKey("city"),
                            errorText = state.fieldErrors["city"]
                        )
                    }


                    Box(
                        Modifier.weight(1f)
                    ) {
                        VInput(
                            value = state.pincode,
                            onValueChange = onPincode,
                            label = "PIN",
                            keyboardType =
                                KeyboardType.Number,
                            isError = state.fieldErrors.containsKey("pincode"),
                            errorText = state.fieldErrors["pincode"]
                        )
                    }
                }


                VInput(
                    value = state.district,
                    onValueChange = onDistrict,
                    label = "District",
                    isError = state.fieldErrors.containsKey("district"),
                    errorText = state.fieldErrors["district"]
                )


                VInput(
                    value = state.state,
                    onValueChange = onState,
                    label = "State"
                )
            }




            // FEEDBACK -----------------------------------------------

            state.errorMessage?.let {

                Text(
                    it,
                    style =
                        VTypography.body
                            .copy(color = VColors.error)
                )
            }


            state.infoMessage?.let {

                Text(
                    it,
                    style =
                        VTypography.body
                            .copy(color = VColors.success)
                )
            }



            Spacer(
                Modifier.height(20.dp)
            )
        }
    }



    // Floating save button
}

@Composable
private fun EditSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {

    VCard {

        Column(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    icon,
                    contentDescription = null
                )


                Spacer(
                    Modifier.width(12.dp)
                )


                Column {

                    Text(
                        title,
                        style =
                            VTypography.body
                    )

                    Text(
                        subtitle,
                        style =
                            VTypography.caption
                    )
                }
            }


            HorizontalDivider()


            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp),
                content = content
            )
        }
    }
}